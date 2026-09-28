package com.example.paymentfundservice.repository;

import com.example.paymentfundservice.model.PremiumPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PremiumPayment samplePayment;

    @BeforeEach
    void setUp() {
        samplePayment = PremiumPayment.create(2L, 187.50, "CREDIT_CARD", "SUCCESS");
        samplePayment.setCustomerId(1L);
        samplePayment.setId(1L);
    }

    @Test
    void findByPolicyId_Found() {
        when(paymentRepository.findByPolicyId(2L)).thenReturn(Flux.just(samplePayment));

        StepVerifier.create(paymentRepository.findByPolicyId(2L))
                .expectNextMatches(p -> p.getPolicyId().equals(2L) && p.getAmount().equals(187.50))
                .verifyComplete();
    }

    @Test
    void findByCustomerId_Found() {
        when(paymentRepository.findByCustomerId(1L)).thenReturn(Flux.just(samplePayment));

        StepVerifier.create(paymentRepository.findByCustomerId(1L))
                .expectNextMatches(p -> p.getCustomerId().equals(1L))
                .verifyComplete();
    }
}
