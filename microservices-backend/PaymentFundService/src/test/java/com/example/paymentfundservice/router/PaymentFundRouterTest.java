package com.example.paymentfundservice.router;

import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.handler.PaymentFundHandler;
import com.example.paymentfundservice.model.PetContinuityFund;
import com.example.paymentfundservice.model.PremiumPayment;
import com.example.paymentfundservice.service.PaymentFundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentFundRouterTest {

    @Mock
    private PaymentFundService paymentFundService;

    private WebTestClient webTestClient;
    private PremiumPayment samplePayment;
    private PetContinuityFund sampleFund;

    @BeforeEach
    void setUp() {
        PaymentFundHandler handler = new PaymentFundHandler(paymentFundService);
        PaymentFundRouter router = new PaymentFundRouter();
        RouterFunction<ServerResponse> routes = router.paymentFundRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        samplePayment = PremiumPayment.create(2L, 187.50, "CREDIT_CARD", "SUCCESS");
        samplePayment.setCustomerId(1L);
        samplePayment.setId(1L);

        sampleFund = PetContinuityFund.create(2L, 1L, 25000.0, 300.0, 4000.0, 2000.0);
        sampleFund.setId(1L);
    }

    @Test
    void payPremium_Success() {
        when(paymentFundService.processPremiumPayment(any(PaymentRequest.class))).thenReturn(Mono.just(samplePayment));

        PaymentRequest request = new PaymentRequest(2L, 1L, 187.50, "CREDIT_CARD", false);

        webTestClient.post()
                .uri("/api/payments/premium")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.policyId").isEqualTo(2)
                .jsonPath("$.amount").isEqualTo(187.50);
    }

    @Test
    void getFundByPolicyId_Found() {
        when(paymentFundService.getFundByPolicyId(2L)).thenReturn(Mono.just(sampleFund));

        webTestClient.get()
                .uri("/api/payments/funds/by-policy/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.policyId").isEqualTo(2)
                .jsonPath("$.totalAmount").isEqualTo(25000.0);
    }
}
