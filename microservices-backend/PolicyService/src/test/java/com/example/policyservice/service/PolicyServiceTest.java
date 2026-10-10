package com.example.policyservice.service;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.dto.QuoteDto;
import com.example.policyservice.model.Coverage;
import com.example.policyservice.model.Policy;
import com.example.policyservice.model.PolicyStatusHistory;
import com.example.policyservice.repository.CoverageRepository;
import com.example.policyservice.repository.PolicyRepository;
import com.example.policyservice.repository.PolicyStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyStatusHistoryRepository historyRepository;

    @Mock
    private CoverageRepository coverageRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Mock
    private ReactiveCircuitBreaker circuitBreaker;

    private PolicyService policyService;
    private PolicyService policyServiceWithCoverage;

    private Policy samplePolicy;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        policyService = new PolicyService(policyRepository, historyRepository, webClientBuilder, circuitBreakerFactory);
        policyServiceWithCoverage = new PolicyService(policyRepository, historyRepository, coverageRepository, webClientBuilder, circuitBreakerFactory);

        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> inv.getArgument(0));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.empty());

        samplePolicy = new Policy(100L, "POL-2024-001", 5L, 10L, 20L, 5000.0, 45.0, 250.0,
                "2024-01-01", "2025-01-01", "PENDING_PAYMENT", "SYSTEM",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createPolicyFromQuote_returnsExistingPolicyIfPresent() {
        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.just(samplePolicy));

        StepVerifier.create(policyService.createPolicyFromQuote(5L))
                .assertNext(res -> {
                    assertEquals(100L, res.id());
                    assertEquals("POL-2024-001", res.policyNumber());
                })
                .verifyComplete();

        verify(webClient, never()).get();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPolicyFromQuote_successForApprovedQuote() {
        QuoteDto approvedQuote = new QuoteDto(5L, 10L, 20L, 5000.0, 45.0, 30, "APPROVED", LocalDateTime.now().plusDays(30));

        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());
        when(responseSpec.bodyToMono(QuoteDto.class)).thenReturn(Mono.just(approvedQuote));
        when(policyRepository.save(any(Policy.class))).thenReturn(Mono.just(samplePolicy));
        when(historyRepository.save(any(PolicyStatusHistory.class))).thenReturn(Mono.just(new PolicyStatusHistory()));

        StepVerifier.create(policyService.createPolicyFromQuote(5L))
                .assertNext(res -> {
                    assertEquals(100L, res.id());
                    assertEquals("POL-2024-001", res.policyNumber());
                })
                .verifyComplete();

        verify(policyRepository).save(any(Policy.class));
        verify(historyRepository).save(any(PolicyStatusHistory.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPolicyFromQuote_successWithCoverageRepository() {
        QuoteDto approvedQuote = new QuoteDto(5L, 10L, 20L, 5000.0, 45.0, 30, "ACCEPTED", LocalDateTime.now().plusDays(30));

        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());
        when(responseSpec.bodyToMono(QuoteDto.class)).thenReturn(Mono.just(approvedQuote));
        when(policyRepository.save(any(Policy.class))).thenReturn(Mono.just(samplePolicy));
        when(historyRepository.save(any(PolicyStatusHistory.class))).thenReturn(Mono.just(new PolicyStatusHistory()));
        when(coverageRepository.save(any(Coverage.class))).thenReturn(Mono.just(new Coverage()));

        StepVerifier.create(policyServiceWithCoverage.createPolicyFromQuote(5L))
                .assertNext(res -> assertEquals(100L, res.id()))
                .verifyComplete();

        verify(coverageRepository).save(any(Coverage.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPolicyFromQuote_rejectedQuote_throwsError() {
        QuoteDto rejectedQuote = new QuoteDto(5L, 10L, 20L, 5000.0, 45.0, 90, "REJECTED", LocalDateTime.now().plusDays(30));

        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());
        when(responseSpec.bodyToMono(QuoteDto.class)).thenReturn(Mono.just(rejectedQuote));

        StepVerifier.create(policyService.createPolicyFromQuote(5L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("Cannot create policy for rejected quote"))
                .verify();

        verify(policyRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPolicyFromQuote_circuitBreakerFallback_throwsIllegalStateException() {
        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("CB error"));
        });

        StepVerifier.create(policyService.createPolicyFromQuote(5L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("Underwriting service unavailable"))
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createPolicyFromQuote_quoteNotFound_throwsIllegalArgumentException() {
        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenReturn(Mono.empty());

        StepVerifier.create(policyService.createPolicyFromQuote(5L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Quote not found"))
                .verify();
    }

    @Test
    void activatePolicy_successful() {
        when(policyRepository.findById(100L)).thenReturn(Mono.just(samplePolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(Mono.just(samplePolicy));
        when(historyRepository.save(any(PolicyStatusHistory.class))).thenReturn(Mono.just(new PolicyStatusHistory()));

        StepVerifier.create(policyService.activatePolicy(100L))
                .assertNext(res -> assertEquals("ACTIVE", samplePolicy.getStatus()))
                .verifyComplete();

        verify(historyRepository).save(any(PolicyStatusHistory.class));
    }

    @Test
    void activatePolicy_notFound_throwsError() {
        when(policyRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.activatePolicy(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateStatus_successful() {
        when(policyRepository.findById(100L)).thenReturn(Mono.just(samplePolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(Mono.just(samplePolicy));
        when(historyRepository.save(any(PolicyStatusHistory.class))).thenReturn(Mono.just(new PolicyStatusHistory()));

        StepVerifier.create(policyService.updateStatus(100L, "TERMINATED", "Customer requested cancellation"))
                .assertNext(res -> assertEquals("TERMINATED", samplePolicy.getStatus()))
                .verifyComplete();
    }

    @Test
    void updateStatus_notFound_throwsError() {
        when(policyRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.updateStatus(999L, "ACTIVE", "reason"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getById_found() {
        when(policyRepository.findById(100L)).thenReturn(Mono.just(samplePolicy));

        StepVerifier.create(policyService.getById(100L))
                .assertNext(res -> assertEquals(100L, res.id()))
                .verifyComplete();
    }

    @Test
    void getById_notFound_throwsError() {
        when(policyRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.getById(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getByPolicyNumber_found() {
        when(policyRepository.findByPolicyNumber("POL-2024-001")).thenReturn(Mono.just(samplePolicy));

        StepVerifier.create(policyService.getByPolicyNumber("POL-2024-001"))
                .assertNext(res -> assertEquals("POL-2024-001", res.policyNumber()))
                .verifyComplete();
    }

    @Test
    void getByPolicyNumber_notFound_throwsError() {
        when(policyRepository.findByPolicyNumber("NONEXISTENT")).thenReturn(Mono.empty());

        StepVerifier.create(policyService.getByPolicyNumber("NONEXISTENT"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getByCustomerId_returnsFlux() {
        when(policyRepository.findByCustomerId(10L)).thenReturn(Flux.just(samplePolicy));

        StepVerifier.create(policyService.getByCustomerId(10L))
                .expectNextMatches(res -> res.id().equals(100L))
                .verifyComplete();
    }

    @Test
    void getAll_returnsFlux() {
        when(policyRepository.findAll()).thenReturn(Flux.just(samplePolicy));

        StepVerifier.create(policyService.getAll())
                .expectNextMatches(res -> res.id().equals(100L))
                .verifyComplete();
    }

    @Test
    void updatePolicy_success() {
        Policy update = new Policy();
        update.setCoverageAmount(8000.0);
        update.setMonthlyPremium(65.0);
        update.setStatus("ACTIVE");

        when(policyRepository.findById(100L)).thenReturn(Mono.just(samplePolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(Mono.just(samplePolicy));

        StepVerifier.create(policyService.updatePolicy(100L, update))
                .assertNext(res -> {
                    assertEquals(8000.0, samplePolicy.getCoverageAmount());
                    assertEquals(65.0, samplePolicy.getMonthlyPremium());
                    assertEquals("ACTIVE", samplePolicy.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void updatePolicy_notFound() {
        when(policyRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.updatePolicy(999L, new Policy()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deletePolicy_successful() {
        when(policyRepository.findById(100L)).thenReturn(Mono.just(samplePolicy));
        when(policyRepository.delete(samplePolicy)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.deletePolicy(100L))
                .verifyComplete();

        verify(policyRepository).delete(samplePolicy);
    }

    @Test
    void deletePolicy_notFound() {
        when(policyRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.deletePolicy(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getCoveragesByPolicyId_tests() {
        // null repo
        StepVerifier.create(policyService.getCoveragesByPolicyId(100L))
                .verifyComplete();

        // with repo
        Coverage cov = new Coverage(1L, 100L, "PET_CONTINUITY", 5000.0, 5000.0, 0.0, "ACTIVE");
        when(coverageRepository.findByPolicyId(100L)).thenReturn(Flux.just(cov));

        StepVerifier.create(policyServiceWithCoverage.getCoveragesByPolicyId(100L))
                .assertNext(c -> assertEquals(1L, c.getId()))
                .verifyComplete();
    }

    @Test
    void addCoverage_tests() {
        Coverage cov = new Coverage(null, 100L, "PET_CONTINUITY", 5000.0, 5000.0, 0.0, "ACTIVE");

        // null repo
        StepVerifier.create(policyService.addCoverage(100L, cov))
                .verifyComplete();

        // with repo
        Coverage saved = new Coverage(1L, 100L, "PET_CONTINUITY", 5000.0, 5000.0, 0.0, "ACTIVE");
        when(coverageRepository.save(any(Coverage.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(policyServiceWithCoverage.addCoverage(100L, cov))
                .assertNext(c -> assertEquals(1L, c.getId()))
                .verifyComplete();
    }

    @Test
    void getPolicyStatusHistory_returnsFlux() {
        PolicyStatusHistory hist = PolicyStatusHistory.create(100L, "PENDING", "ACTIVE", "Paid");
        when(historyRepository.findByPolicyIdOrderByChangedAtDesc(100L)).thenReturn(Flux.just(hist));

        StepVerifier.create(policyService.getPolicyStatusHistory(100L))
                .assertNext(h -> assertEquals(100L, h.getPolicyId()))
                .verifyComplete();
    }
}
