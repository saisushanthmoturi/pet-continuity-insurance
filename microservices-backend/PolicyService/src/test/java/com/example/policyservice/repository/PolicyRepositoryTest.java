package com.example.policyservice.repository;

import com.example.policyservice.model.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyRepositoryTest {

    @Mock
    private PolicyRepository policyRepository;

    private Policy samplePolicy;

    @BeforeEach
    void setUp() {
        samplePolicy = new Policy(1L, "POL-1790093637581-1", 3L, 1L, 1L, 25000.0, 187.50, 250.0,
                "2026-09-22", "2027-09-22", "ACTIVE", "SYSTEM",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void findByCustomerId_Found() {
        when(policyRepository.findByCustomerId(1L)).thenReturn(Flux.just(samplePolicy));

        StepVerifier.create(policyRepository.findByCustomerId(1L))
                .expectNextMatches(p -> p.getCustomerId().equals(1L) && p.getPolicyNumber().equals("POL-1790093637581-1"))
                .verifyComplete();
    }

    @Test
    void findByQuoteId_Found() {
        when(policyRepository.findByQuoteId(3L)).thenReturn(Mono.just(samplePolicy));

        StepVerifier.create(policyRepository.findByQuoteId(3L))
                .expectNextMatches(p -> p.getQuoteId().equals(3L))
                .verifyComplete();
    }
}
