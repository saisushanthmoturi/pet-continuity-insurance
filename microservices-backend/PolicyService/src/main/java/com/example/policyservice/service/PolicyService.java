package com.example.policyservice.service;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.dto.QuoteDto;
import com.example.policyservice.model.Policy;
import com.example.policyservice.model.PolicyStatusHistory;
import com.example.policyservice.repository.PolicyRepository;
import com.example.policyservice.repository.PolicyStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private final PolicyRepository policyRepository;
    private final PolicyStatusHistoryRepository historyRepository;
    private final WebClient webClient;

    public PolicyService(PolicyRepository policyRepository,
                         PolicyStatusHistoryRepository historyRepository,
                         WebClient.Builder webClientBuilder) {
        this.policyRepository = policyRepository;
        this.historyRepository = historyRepository;
        this.webClient = webClientBuilder.build();
    }

    public Mono<PolicyResponse> createPolicyFromQuote(Long quoteId) {
        return policyRepository.findByQuoteId(quoteId)
                .map(this::toResponse)
                .switchIfEmpty(Mono.defer(() ->
                        webClient.get()
                                .uri("http://UnderWritingRiskService/api/underwriting/quotes/{id}", quoteId)
                                .retrieve()
                                .bodyToMono(QuoteDto.class)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Quote not found with id: " + quoteId)))
                                .flatMap(quote -> {
                                    if ("REJECTED".equalsIgnoreCase(quote.decision())) {
                                        return Mono.error(new IllegalStateException("Cannot create policy for rejected quote: " + quoteId));
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
                                                return historyRepository.save(history).thenReturn(toResponse(saved));
                                            });
                                })
                ));
    }

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

    public Mono<PolicyResponse> getById(Long id) {
        return policyRepository.findById(id)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with id: " + id)));
    }

    public Mono<PolicyResponse> getByPolicyNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Policy not found with number: " + policyNumber)));
    }

    public Flux<PolicyResponse> getByCustomerId(Long customerId) {
        return policyRepository.findByCustomerId(customerId).map(this::toResponse);
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
