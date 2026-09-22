package com.example.underwritingriskservice.service;

import com.example.underwritingriskservice.dto.*;
import com.example.underwritingriskservice.model.Quote;
import com.example.underwritingriskservice.model.RiskAssessment;
import com.example.underwritingriskservice.repository.QuoteRepository;
import com.example.underwritingriskservice.repository.RiskAssessmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import com.example.underwritingriskservice.model.RatingRule;
import com.example.underwritingriskservice.repository.RatingRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UnderwritingService {

    private static final Logger log = LoggerFactory.getLogger(UnderwritingService.class);

    private final QuoteRepository quoteRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final RatingRuleRepository ratingRuleRepository;
    private final WebClient webClient;
    private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Autowired
    public UnderwritingService(QuoteRepository quoteRepository,
                               RiskAssessmentRepository riskAssessmentRepository,
                               RatingRuleRepository ratingRuleRepository,
                               WebClient.Builder webClientBuilder,
                               ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.quoteRepository = quoteRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.ratingRuleRepository = ratingRuleRepository;
        this.webClient = webClientBuilder.build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public UnderwritingService(QuoteRepository quoteRepository,
                               RiskAssessmentRepository riskAssessmentRepository,
                               WebClient.Builder webClientBuilder,
                               ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this(quoteRepository, riskAssessmentRepository, null, webClientBuilder, circuitBreakerFactory);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_ADMIN')")
    public Mono<QuoteResponse> generateQuote(QuoteRequest req) {
        if (req.customerId() == null || req.petId() == null || req.requestedCoverage() == null || req.requestedCoverage() <= 0) {
            return Mono.error(new IllegalArgumentException("customerId, petId, and valid requestedCoverage are required"));
        }

        ReactiveCircuitBreaker cb = circuitBreakerFactory.create("underwritingCB");

        // 1. Fetch pet profile from PetService
        Mono<PetDto> petMono = cb.run(
                webClient.get()
                        .uri("http://PetService/api/pets/{id}", req.petId())
                        .header("X-User-Role", "INTERNAL_SERVICE")
                        .retrieve()
                        .bodyToMono(PetDto.class),
                e -> {
                    log.error("CircuitBreaker fallback for PetService: {}", e.getMessage());
                    return Mono.error(new IllegalStateException("Pet service unavailable to fetch profile for pet " + req.petId()));
                }
        );

        // 2. Fetch medical records from PetService
        Mono<List<MedicalRecordDto>> medRecordsMono = cb.run(
                webClient.get()
                        .uri("http://PetService/api/pets/{id}/medical-records", req.petId())
                        .header("X-User-Role", "INTERNAL_SERVICE")
                        .retrieve()
                        .bodyToFlux(MedicalRecordDto.class)
                        .collectList(),
                e -> {
                    log.warn("CircuitBreaker fallback for Pet medical records: {}", e.getMessage());
                    return Mono.just(List.of());
                }
        );

        // 3. Fetch customer profile from CustomerService
        Mono<CustomerDto> customerMono = cb.run(
                webClient.get()
                        .uri("http://CustomerService/api/customers/{id}", req.customerId())
                        .header("X-User-Role", "INTERNAL_SERVICE")
                        .retrieve()
                        .bodyToMono(CustomerDto.class),
                e -> {
                    log.error("CircuitBreaker fallback for CustomerService: {}", e.getMessage());
                    return Mono.error(new IllegalStateException("Customer service unavailable to fetch profile for customer " + req.customerId()));
                }
        );

        // 4. Fetch care plan from CareVerificationService (with safe fallback if no care plan created yet)
        Mono<CarePlanDto> carePlanMono = cb.run(
                webClient.get()
                        .uri("http://CareVerificationService/api/care/care-plans/pet/{petId}", req.petId())
                        .header("X-User-Role", "INTERNAL_SERVICE")
                        .retrieve()
                        .bodyToMono(CarePlanDto.class),
                e -> {
                    log.warn("CircuitBreaker fallback for CareVerificationService: {}", e.getMessage());
                    return Mono.empty();
                }
        ).defaultIfEmpty(CarePlanDto.empty());

        return Mono.zip(petMono, medRecordsMono, customerMono, carePlanMono)
                .flatMap(tuple -> {
                    PetDto pet = tuple.getT1();
                    List<MedicalRecordDto> medRecords = tuple.getT2();
                    CustomerDto customer = tuple.getT3();
                    CarePlanDto carePlan = tuple.getT4();

                    // Security check: Validate Customer-Pet Ownership
                    if (pet.customerId() != null && !pet.customerId().equals(req.customerId())) {
                        return Mono.error(new IllegalArgumentException("Pet ID " + req.petId() + " does not belong to Customer ID " + req.customerId()));
                    }

                    log.info("Evaluating underwriting for customer '{}' and pet '{}' (breed: {}, age: {})",
                            customer.fullName(), pet.name(), pet.breed(), pet.age());

                    // 1. Age Factor (scaled life stage curve)
                    double ageFactor = Math.max(0, (pet.age() - 2) * 4.5);

                    // 2. Breed Factor (known hereditary/actuarial risk)
                    double breedFactor = calculateBreedFactor(pet.breed());

                    // 3. Medical Severity Factor (considers count AND total cost)
                    double annualMed = medRecords.stream().mapToDouble(r -> r.estimatedAnnualMedCost() != null ? r.estimatedAnnualMedCost() : 0.0).sum();
                    double healthFactor = (medRecords.size() * 8.0) + Math.min(30.0, annualMed / 50.0);

                    // 4. Care Plan Continuity Compliance Factor
                    double carePlanFactor = (carePlan != null && carePlan.primaryCaretakerId() != null)
                            ? (carePlan.backupCaretakerId() != null ? -10.0 : 0.0)
                            : 15.0;

                    // Total multi-factor risk score
                    int riskScore = (int) Math.min(100, Math.max(10, 20 + ageFactor + breedFactor + healthFactor + carePlanFactor));

                    String riskLevel;
                    double riskMultiplier;
                    if (riskScore <= 40) {
                        riskLevel = "LOW";
                        riskMultiplier = 1.0;
                    } else if (riskScore <= 70) {
                        riskLevel = "MODERATE";
                        riskMultiplier = 1.35;
                    } else if (riskScore <= 85) {
                        riskLevel = "HIGH";
                        riskMultiplier = 1.75;
                    } else {
                        riskLevel = "EXTREME";
                        riskMultiplier = 2.5;
                    }

                    String decision;
                    if (riskScore <= 80) {
                        decision = "APPROVED";
                    } else if (riskScore <= 90) {
                        decision = "REFERRED";
                    } else {
                        decision = "REJECTED";
                    }

                    int remainingYears = Math.max(1, 15 - pet.age());
                    double annualCare = pet.estimatedAnnualCareCost() != null ? pet.estimatedAnnualCareCost() : 1200.0;
                    double projectedLiability = Math.round((annualCare + annualMed) * remainingYears * 1.05 * 100.0) / 100.0;

                    double monthlyPremium = Math.round((req.requestedCoverage() * 0.003 * riskMultiplier) * 100.0) / 100.0;
                    double coverageGap = Math.max(0.0, Math.round((projectedLiability - req.requestedCoverage()) * 100.0) / 100.0);

                    Quote quote = Quote.createNew(req.customerId(), req.petId(), req.requestedCoverage(), monthlyPremium, riskScore, decision);

                    return quoteRepository.save(quote)
                            .flatMap(savedQuote -> {
                                RiskAssessment assessment = RiskAssessment.createNew(
                                        savedQuote.getId(),
                                        req.petId(),
                                        ageFactor,
                                        healthFactor,
                                        projectedLiability,
                                        coverageGap,
                                        riskLevel
                                );
                                assessment.setBreedFactor(breedFactor);
                                assessment.setExplanation("Assessment complete. BreedFactor: " + breedFactor + ", CarePlanDiscount: " + carePlanFactor + ", MedCost: $" + annualMed);
                                return riskAssessmentRepository.save(assessment)
                                        .map(savedAss -> {
                                            log.info("Generated quote id={}, petId={}, score={}, decision={}, premium=${}",
                                                    savedQuote.getId(), req.petId(), riskScore, decision, monthlyPremium);
                                            return new QuoteResponse(
                                                    savedQuote.getId(),
                                                    savedQuote.getCustomerId(),
                                                    savedQuote.getPetId(),
                                                    savedQuote.getRequestedCoverage(),
                                                    savedQuote.getMonthlyPremium(),
                                                    savedQuote.getRiskScore(),
                                                    savedQuote.getDecision(),
                                                    savedQuote.getStatus(),
                                                    projectedLiability,
                                                    coverageGap,
                                                    riskLevel,
                                                    savedQuote.getValidUntil()
                                            );
                                        });
                            });
                });
    }

    private double calculateBreedFactor(String breed) {
        if (breed == null) return 0.0;
        String b = breed.toUpperCase();
        if (b.contains("BULLDOG") || b.contains("PUG") || b.contains("BOXER") || b.contains("ROTTWEILER") || b.contains("MASTIFF") || b.contains("GERMAN_SHEPHERD")) {
            return 15.0;
        } else if (b.contains("POODLE") || b.contains("BEAGLE") || b.contains("RETRIEVER")) {
            return 5.0;
        }
        return 0.0;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<QuoteResponse> getQuotesByCustomerId(Long customerId) {
        return quoteRepository.findByCustomerId(customerId)
                .flatMap(quote -> riskAssessmentRepository.findByQuoteId(quote.getId())
                        .map(ass -> toQuoteResponse(quote, ass))
                        .defaultIfEmpty(toQuoteResponse(quote, null)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<QuoteResponse> getQuotesByPetId(Long petId) {
        return quoteRepository.findByPetId(petId)
                .flatMap(quote -> riskAssessmentRepository.findByQuoteId(quote.getId())
                        .map(ass -> toQuoteResponse(quote, ass))
                        .defaultIfEmpty(toQuoteResponse(quote, null)));
    }

    private QuoteResponse toQuoteResponse(Quote quote, RiskAssessment ass) {
        return new QuoteResponse(
                quote.getId(),
                quote.getCustomerId(),
                quote.getPetId(),
                quote.getRequestedCoverage(),
                quote.getMonthlyPremium(),
                quote.getRiskScore(),
                quote.getDecision(),
                quote.getStatus(),
                ass != null ? ass.getProjectedCareLiability() : 0.0,
                ass != null ? ass.getCoverageGap() : 0.0,
                ass != null ? ass.getRiskLevel() : quote.getRiskClass(),
                quote.getValidUntil()
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<RiskAssessment> reassessPetRisk(Long petId) {
        ReactiveCircuitBreaker cb = circuitBreakerFactory.create("underwritingCB");
        Mono<PetDto> petMono = cb.run(
                webClient.get().uri("http://PetService/api/pets/{id}", petId).header("X-User-Role", "INTERNAL_SERVICE").retrieve().bodyToMono(PetDto.class),
                e -> Mono.error(new IllegalStateException("Failed to reach PetService: " + e.getMessage()))
        );
        Mono<List<MedicalRecordDto>> medRecordsMono = cb.run(
                webClient.get().uri("http://PetService/api/pets/{id}/medical-records", petId).header("X-User-Role", "INTERNAL_SERVICE").retrieve().bodyToFlux(MedicalRecordDto.class).collectList(),
                e -> Mono.just(List.of())
        );
        Mono<CarePlanDto> carePlanMono = cb.run(
                webClient.get().uri("http://CareVerificationService/api/care/care-plans/pet/{petId}", petId).header("X-User-Role", "INTERNAL_SERVICE").retrieve().bodyToMono(CarePlanDto.class),
                e -> Mono.empty()
        ).defaultIfEmpty(CarePlanDto.empty());

        return Mono.zip(petMono, medRecordsMono, carePlanMono)
                .flatMap(t -> {
                    PetDto pet = t.getT1();
                    List<MedicalRecordDto> medRecords = t.getT2();
                    CarePlanDto carePlan = t.getT3();

                    double ageFactor = Math.max(0, (pet.age() - 2) * 4.5);
                    double breedFactor = calculateBreedFactor(pet.breed());
                    double annualMed = medRecords.stream().mapToDouble(r -> r.estimatedAnnualMedCost() != null ? r.estimatedAnnualMedCost() : 0.0).sum();
                    double healthFactor = (medRecords.size() * 8.0) + Math.min(30.0, annualMed / 50.0);
                    double carePlanFactor = (carePlan != null && carePlan.primaryCaretakerId() != null)
                            ? (carePlan.backupCaretakerId() != null ? -10.0 : 0.0) : 15.0;

                    int riskScore = (int) Math.min(100, Math.max(10, 20 + ageFactor + breedFactor + healthFactor + carePlanFactor));
                    String riskLevel = riskScore <= 40 ? "LOW" : (riskScore <= 70 ? "MODERATE" : (riskScore <= 85 ? "HIGH" : "EXTREME"));

                    int remainingYears = Math.max(1, 15 - pet.age());
                    double annualCare = pet.estimatedAnnualCareCost() != null ? pet.estimatedAnnualCareCost() : 1200.0;
                    double projectedLiability = Math.round((annualCare + annualMed) * remainingYears * 1.05 * 100.0) / 100.0;

                    return quoteRepository.findByPetId(petId)
                            .collectList()
                            .flatMap(quotes -> {
                                Mono<Quote> quoteMono;
                                if (!quotes.isEmpty()) {
                                    Quote latest = quotes.get(quotes.size() - 1);
                                    latest.setRiskScore(riskScore);
                                    latest.setDecision(riskScore <= 80 ? "APPROVED" : (riskScore <= 90 ? "REFERRED" : "REJECTED"));
                                    latest.setRiskClass(riskLevel);
                                    quoteMono = quoteRepository.save(latest);
                                } else {
                                    double multiplier = riskScore <= 40 ? 1.0 : (riskScore <= 70 ? 1.35 : (riskScore <= 85 ? 1.75 : 2.5));
                                    Quote draft = Quote.createNew(
                                            pet.customerId() != null ? pet.customerId() : 1L,
                                            petId,
                                            15000.0,
                                            Math.round((15000.0 * 0.003 * multiplier) * 100.0) / 100.0,
                                            riskScore,
                                            riskScore <= 80 ? "APPROVED" : "REFERRED"
                                    );
                                    draft.setStatus("REASSESSED");
                                    quoteMono = quoteRepository.save(draft);
                                }
                                return quoteMono.flatMap(savedQuote -> {
                                    RiskAssessment assessment = RiskAssessment.createNew(
                                            savedQuote.getId(), petId, ageFactor, healthFactor, projectedLiability, 0.0, riskLevel
                                    );
                                    assessment.setBreedFactor(breedFactor);
                                    assessment.setExplanation("On-demand reassessment. Med records: " + medRecords.size() + ", totalMedCost: $" + annualMed + ", carePlanCompliance: " + (carePlanFactor <= 0 ? "SATISFACTORY" : "PENDING"));
                                    return riskAssessmentRepository.save(assessment);
                                });
                            });
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<QuoteResponse> getQuoteById(Long id) {
        return quoteRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Quote not found with id: " + id)))
                .flatMap(quote -> riskAssessmentRepository.findByQuoteId(quote.getId())
                        .map(ass -> new QuoteResponse(
                                quote.getId(),
                                quote.getCustomerId(),
                                quote.getPetId(),
                                quote.getRequestedCoverage(),
                                quote.getMonthlyPremium(),
                                quote.getRiskScore(),
                                quote.getDecision(),
                                quote.getStatus(),
                                ass.getProjectedCareLiability(),
                                ass.getCoverageGap(),
                                ass.getRiskLevel(),
                                quote.getValidUntil()
                        )));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<QuoteResponse> getRiskMonitoring(Long petId) {
        return riskAssessmentRepository.findFirstByPetIdOrderByAssessmentDateDesc(petId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No risk assessment found for pet: " + petId)))
                .flatMap(ass -> {
                    if (ass.getQuoteId() != null) {
                        return quoteRepository.findById(ass.getQuoteId())
                                .map(q -> new QuoteResponse(
                                        q.getId(),
                                        q.getCustomerId(),
                                        q.getPetId(),
                                        q.getRequestedCoverage(),
                                        q.getMonthlyPremium(),
                                        q.getRiskScore(),
                                        q.getDecision(),
                                        q.getStatus(),
                                        ass.getProjectedCareLiability(),
                                        ass.getCoverageGap(),
                                        ass.getRiskLevel(),
                                        q.getValidUntil()
                                ));
                    }
                    return Mono.just(new QuoteResponse(
                            null, null, petId, 0.0, 0.0, 50, "MONITORING", "ACTIVE",
                            ass.getProjectedCareLiability(), ass.getCoverageGap(), ass.getRiskLevel(), null
                    ));
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_UNDERWRITER', 'ROLE_ADMIN')")
    public Flux<QuoteResponse> getAllQuotes() {
        return quoteRepository.findAll()
                .flatMap(quote -> riskAssessmentRepository.findByQuoteId(quote.getId())
                        .map(ass -> new QuoteResponse(
                                quote.getId(),
                                quote.getCustomerId(),
                                quote.getPetId(),
                                quote.getRequestedCoverage(),
                                quote.getMonthlyPremium(),
                                quote.getRiskScore(),
                                quote.getDecision(),
                                quote.getStatus(),
                                ass.getProjectedCareLiability(),
                                ass.getCoverageGap(),
                                ass.getRiskLevel(),
                                quote.getValidUntil()
                        ))
                        .defaultIfEmpty(new QuoteResponse(
                                quote.getId(),
                                quote.getCustomerId(),
                                quote.getPetId(),
                                quote.getRequestedCoverage(),
                                quote.getMonthlyPremium(),
                                quote.getRiskScore(),
                                quote.getDecision(),
                                quote.getStatus(),
                                0.0, 0.0, "MODERATE",
                                quote.getValidUntil()
                        )));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_UNDERWRITER', 'ROLE_ADMIN')")
    public Mono<QuoteResponse> updateQuote(Long id, QuoteRequest req) {
        return quoteRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Quote not found with id: " + id)))
                .flatMap(quote -> {
                    if (req.requestedCoverage() != null && req.requestedCoverage() > 0) {
                        quote.setRequestedCoverage(req.requestedCoverage());
                        // Recalculate premium based on existing score
                        double multiplier = quote.getRiskScore() <= 40 ? 1.0 : (quote.getRiskScore() <= 70 ? 1.35 : 1.75);
                        quote.setMonthlyPremium(Math.round((req.requestedCoverage() * 0.003 * multiplier) * 100.0) / 100.0);
                    }
                    return quoteRepository.save(quote)
                            .flatMap(saved -> getQuoteById(saved.getId()));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteQuote(Long id) {
        return quoteRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Quote not found with id: " + id)))
                .flatMap(quoteRepository::delete)
                .doOnSuccess(v -> log.info("Deleted quote id={}", id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_UNDERWRITER', 'ROLE_ADMIN')")
    public Flux<RatingRule> getAllRules() {
        return ratingRuleRepository != null ? ratingRuleRepository.findAll() : Flux.empty();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_UNDERWRITER', 'ROLE_ADMIN')")
    public Mono<RatingRule> getRuleById(Long id) {
        if (ratingRuleRepository == null) return Mono.empty();
        return ratingRuleRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rating rule not found with id: " + id)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<RatingRule> createRule(RatingRule rule) {
        if (ratingRuleRepository == null) return Mono.empty();
        return ratingRuleRepository.save(rule)
                .doOnSuccess(r -> log.info("Created rating rule id={}, name={}", r.getId(), r.getRuleName()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<RatingRule> updateRule(Long id, RatingRule rule) {
        if (ratingRuleRepository == null) return Mono.empty();
        return ratingRuleRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rating rule not found with id: " + id)))
                .flatMap(existing -> {
                    if (rule.getRuleName() != null) existing.setRuleName(rule.getRuleName());
                    if (rule.getFactor() != null) existing.setFactor(rule.getFactor());
                    if (rule.getMinValue() != null) existing.setMinValue(rule.getMinValue());
                    if (rule.getMaxValue() != null) existing.setMaxValue(rule.getMaxValue());
                    if (rule.getScore() != null) existing.setScore(rule.getScore());
                    if (rule.getPremiumFactor() != null) existing.setPremiumFactor(rule.getPremiumFactor());
                    if (rule.getStatus() != null) existing.setStatus(rule.getStatus());
                    return ratingRuleRepository.save(existing);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteRule(Long id) {
        if (ratingRuleRepository == null) return Mono.empty();
        return ratingRuleRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rating rule not found with id: " + id)))
                .flatMap(ratingRuleRepository::delete)
                .doOnSuccess(v -> log.info("Deleted rating rule id={}", id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_UNDERWRITER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<RiskAssessment> getRiskAssessmentByQuoteId(Long quoteId) {
        return riskAssessmentRepository.findByQuoteId(quoteId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Risk assessment not found for quoteId: " + quoteId)));
    }
}
