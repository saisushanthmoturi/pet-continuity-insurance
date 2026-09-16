package com.example.policyservice.service;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.dto.QuoteDto;
import com.example.policyservice.model.Policy;
import com.example.policyservice.model.PolicyStatusHistory;
import com.example.policyservice.repository.PolicyRepository;
import com.example.policyservice.repository.PolicyStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyStatusHistoryRepository historyRepository;

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

    private Policy samplePolicy;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        policyService = new PolicyService(policyRepository, historyRepository, webClientBuilder, circuitBreakerFactory);

        samplePolicy = new Policy(100L, "POL-2024-001", 5L, 10L, 20L, 5000.0, 45.0, 250.0,
                "2024-01-01", "2025-01-01", "PENDING_PAYMENT", "SYSTEM",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createPolicyFromQuote_returnsExistingPolicyIfPresent() {
        when(circuitBreakerFactory.create("policyCB")).thenReturn(circuitBreaker);
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
        QuoteDto approvedQuote = new QuoteDto(5L, 10L, 20L, 5000.0, 45.0, 30, "APPROVED");

        when(circuitBreakerFactory.create("policyCB")).thenReturn(circuitBreaker);
        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(QuoteDto.class)).thenReturn(Mono.just(approvedQuote));

        // Circuit breaker executes the mono directly
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

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
    void createPolicyFromQuote_rejectedQuote_throwsError() {
        QuoteDto rejectedQuote = new QuoteDto(5L, 10L, 20L, 5000.0, 45.0, 90, "REJECTED");

        when(circuitBreakerFactory.create("policyCB")).thenReturn(circuitBreaker);
        when(policyRepository.findByQuoteId(5L)).thenReturn(Mono.empty());

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(QuoteDto.class)).thenReturn(Mono.just(rejectedQuote));

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(policyService.createPolicyFromQuote(5L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("rejected"))
                .verify();

        verify(policyRepository, never()).save(any());
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
    void getByCustomerId_returnsFlux() {
        when(policyRepository.findByCustomerId(10L)).thenReturn(Flux.just(samplePolicy));

        StepVerifier.create(policyService.getByCustomerId(10L))
                .expectNextMatches(res -> res.id().equals(100L))
                .verifyComplete();
    }

    @Test
    void deletePolicy_successful() {
        when(policyRepository.findById(100L)).thenReturn(Mono.just(samplePolicy));
        when(policyRepository.delete(samplePolicy)).thenReturn(Mono.empty());

        StepVerifier.create(policyService.deletePolicy(100L))
                .verifyComplete();

        verify(policyRepository).delete(samplePolicy);
    }
}
