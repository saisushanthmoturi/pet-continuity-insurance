package com.example.underwritingriskservice.service;

import com.example.underwritingriskservice.dto.MedicalRecordDto;
import com.example.underwritingriskservice.dto.PetDto;
import com.example.underwritingriskservice.dto.QuoteRequest;
import com.example.underwritingriskservice.dto.QuoteResponse;
import com.example.underwritingriskservice.model.Quote;
import com.example.underwritingriskservice.model.RiskAssessment;
import com.example.underwritingriskservice.repository.QuoteRepository;
import com.example.underwritingriskservice.repository.RiskAssessmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UnderwritingService {

    private static final Logger log = LoggerFactory.getLogger(UnderwritingService.class);

    private final QuoteRepository quoteRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final WebClient webClient;

    public UnderwritingService(QuoteRepository quoteRepository,
                               RiskAssessmentRepository riskAssessmentRepository,
                               WebClient.Builder webClientBuilder) {
        this.quoteRepository = quoteRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.webClient = webClientBuilder.build();
    }

    public Mono<QuoteResponse> generateQuote(QuoteRequest req) {
        if (req.customerId() == null || req.petId() == null || req.requestedCoverage() == null || req.requestedCoverage() <= 0) {
            return Mono.error(new IllegalArgumentException("customerId, petId, and valid requestedCoverage are required"));
        }

        // Fetch pet profile and medical records from PetService
        Mono<PetDto> petMono = webClient.get()
                .uri("http://PetService/api/pets/{id}", req.petId())
                .retrieve()
                .bodyToMono(PetDto.class)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch pet from PetService, using fallback: {}", e.getMessage());
                    return Mono.just(new PetDto(req.petId(), req.customerId(), "Pet", "Dog", "Mixed", 5, 20.0, "Unknown", 1200.0));
                });

        Mono<List<MedicalRecordDto>> medRecordsMono = webClient.get()
                .uri("http://PetService/api/pets/{id}/medical-records", req.petId())
                .retrieve()
                .bodyToFlux(MedicalRecordDto.class)
                .collectList()
                .onErrorReturn(List.of());

        return Mono.zip(petMono, medRecordsMono)
                .flatMap(tuple -> {
                    PetDto pet = tuple.getT1();
                    List<MedicalRecordDto> medRecords = tuple.getT2();

                    // Actuarial calculation
                    double ageFactor = Math.max(0, (pet.age() - 3) * 5.0);
                    double healthFactor = medRecords.size() * 15.0;
                    int riskScore = (int) Math.min(100, Math.max(10, 20 + ageFactor + healthFactor));

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
                    double annualMed = medRecords.stream().mapToDouble(r -> r.estimatedAnnualMedCost() != null ? r.estimatedAnnualMedCost() : 0.0).sum();
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
                                                    savedAss.getProjectedCareLiability(),
                                                    savedAss.getCoverageGap(),
                                                    savedAss.getRiskLevel(),
                                                    savedQuote.getValidUntil()
                                            );
                                        });
                            });
                });
    }

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
}
