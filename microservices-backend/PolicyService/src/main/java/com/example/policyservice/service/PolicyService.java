package com.example.policyservice.service;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.dto.QuoteDto;
import com.example.policyservice.model.Policy;
import com.example.policyservice.model.PolicyStatusHistory;
import com.example.policyservice.repository.PolicyRepository;
import com.example.policyservice.repository.PolicyStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.policyservice.model.Coverage;
import com.example.policyservice.repository.CoverageRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private final PolicyRepository policyRepository;
    private final PolicyStatusHistoryRepository historyRepository;
    private final CoverageRepository coverageRepository;
    private final WebClient webClient;
    private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Autowired
    public PolicyService(PolicyRepository policyRepository,
                         PolicyStatusHistoryRepository historyRepository,
                         CoverageRepository coverageRepository,
                         WebClient.Builder webClientBuilder,
                         ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.policyRepository = policyRepository;
        this.historyRepository = historyRepository;
        this.coverageRepository = coverageRepository;
        this.webClient = webClientBuilder.build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public PolicyService(PolicyRepository policyRepository,
                         PolicyStatusHistoryRepository historyRepository,
                         WebClient.Builder webClientBuilder,
                         ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this(policyRepository, historyRepository, null, webClientBuilder, circuitBreakerFactory);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<PolicyResponse> createPolicyFromQuote(Long quoteId) {
        ReactiveCircuitBreaker cb = circuitBreakerFactory.create("policyCB");

        return policyRepository.findByQuoteId(quoteId)
                .map(this::toResponse)
                .switchIfEmpty(Mono.defer(() ->
                        cb.run(
                                webClient.get()
                                        .uri("http://UnderWritingRiskService/api/underwriting/quotes/{id}", quoteId)
                                        .header("X-User-Role", "INTERNAL_SERVICE")
                                        .retrieve()
                                        .bodyToMono(QuoteDto.class),
                                e -> {
                                    log.error("CircuitBreaker fallback for UnderWritingRiskService: {}", e.getMessage());
                                    return Mono.error(new IllegalStateException("Underwriting service unavailable to fetch quote: " + quoteId));
                                }
                        )
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Quote not found with id: " + quoteId)))
                                .flatMap(quote -> {
                                    if ("REJECTED".equalsIgnoreCase(quote.decision())) {
                                        return Mono.error(new IllegalStateException("Cannot create policy for rejected quote: " + quoteId));
                                    }
                                    if (quote.validUntil() != null && quote.validUntil().isBefore(java.time.LocalDateTime.now())) {
                                        return Mono.error(new IllegalStateException("Quote " + quoteId + " has expired on " + quote.validUntil() + ". Please generate a new quote."));
                                    }
                                    Policy policy = Policy.createFromQuote(
                                             quote.id(),
                                            quote.customerId(),
                                            quote.petId(),
                                            quote.requestedCoverage(),
                                            quote.monthlyPremium()
                                    );
                                    return policyRepository.save(policy)
                                            .flatMap(saved -> {
                                                log.info("Created policy id={}, number={}, status={}", saved.getId(), saved.getPolicyNumber(), saved.getStatus());
                                                PolicyStatusHistory history = PolicyStatusHistory.create(
                                                        saved.getId(),
                                                        "NONE",
                                                        "PENDING_PAYMENT",
                                                        "Quote accepted by customer"
                                                );
                                                Mono<Coverage> covMono = coverageRepository != null
                                                        ? coverageRepository.save(new Coverage(null, saved.getId(), "PET_CONTINUITY_LIFE", saved.getCoverageAmount(), saved.getCoverageAmount(), 0.0, "ACTIVE"))
                                                        : Mono.empty();
                                                return historyRepository.save(history)
                                                        .then(covMono.defaultIfEmpty(new Coverage()))
                                                        .thenReturn(toResponse(saved));
                                            });
                                })
                ));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<PolicyResponse> activatePolicy(Long id) {
        return policyRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with id: " + id)))
                .flatMap(policy -> {
                    String previous = policy.getStatus();
                    policy.setStatus("ACTIVE");
                    return policyRepository.save(policy)
                            .flatMap(saved -> {
                                log.info("Activated policy id={}, number={}, status moves {} -> ACTIVE", saved.getId(), saved.getPolicyNumber(), previous);
                                PolicyStatusHistory history = PolicyStatusHistory.create(
                                        saved.getId(),
                                        previous,
                                        "ACTIVE",
                                        "Premium payment successful"
                                );
                                return historyRepository.save(history).thenReturn(toResponse(saved));
                            });
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE', 'ROLE_CLAIMS_OFFICER')")
    public Mono<PolicyResponse> updateStatus(Long id, String newStatus, String reason) {
        return policyRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with id: " + id)))
                .flatMap(policy -> {
                    String previous = policy.getStatus();
                    policy.setStatus(newStatus.toUpperCase());
                    return policyRepository.save(policy)
                            .flatMap(saved -> {
                                PolicyStatusHistory history = PolicyStatusHistory.create(saved.getId(), previous, newStatus, reason);
                                return historyRepository.save(history).thenReturn(toResponse(saved));
                            });
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<PolicyResponse> getById(Long id) {
        return policyRepository.findById(id)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with id: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Mono<PolicyResponse> getByPolicyNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with number: " + policyNumber)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Flux<PolicyResponse> getByCustomerId(Long customerId) {
        return policyRepository.findByCustomerId(customerId).map(this::toResponse);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<PolicyResponse> getAll() {
        return policyRepository.findAll().map(this::toResponse);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<PolicyResponse> updatePolicy(Long id, Policy updated) {
        return policyRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with id: " + id)))
                .flatMap(existing -> {
                    if (updated.getCoverageAmount() != null) existing.setCoverageAmount(updated.getCoverageAmount());
                    if (updated.getMonthlyPremium() != null) existing.setMonthlyPremium(updated.getMonthlyPremium());
                    if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
                    return policyRepository.save(existing).map(this::toResponse);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deletePolicy(Long id) {
        return policyRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with id: " + id)))
                .flatMap(policyRepository::delete)
                .doOnSuccess(v -> log.info("Deleted policy id={}", id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<Coverage> getCoveragesByPolicyId(Long policyId) {
        return coverageRepository != null ? coverageRepository.findByPolicyId(policyId) : Flux.empty();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_UNDERWRITER')")
    public Mono<Coverage> addCoverage(Long policyId, Coverage coverage) {
        if (coverageRepository == null) return Mono.empty();
        coverage.setPolicyId(policyId);
        return coverageRepository.save(coverage)
                .doOnSuccess(c -> log.info("Added coverage id={} to policyId={}", c.getId(), policyId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_UNDERWRITER', 'ROLE_CLAIMS_OFFICER', 'ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE')")
    public Flux<PolicyStatusHistory> getPolicyStatusHistory(Long policyId) {
        return historyRepository.findByPolicyIdOrderByChangedAtDesc(policyId);
    }

    private PolicyResponse toResponse(Policy p) {
        return new PolicyResponse(
                p.getId(),
                p.getPolicyNumber(),
                p.getQuoteId(),
                p.getCustomerId(),
                p.getPetId(),
                p.getCoverageAmount(),
                p.getMonthlyPremium(),
                p.getStatus(),
                p.getStartDate(),
                p.getEndDate(),
                p.getCreatedAt()
        );
    }
}
