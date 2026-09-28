package com.example.policyservice.router;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.handler.PolicyHandler;
import com.example.policyservice.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyRouterTest {

    @Mock
    private PolicyService policyService;

    private WebTestClient webTestClient;
    private PolicyResponse samplePolicyResponse;

    @BeforeEach
    void setUp() {
        PolicyHandler handler = new PolicyHandler(policyService);
        PolicyRouter router = new PolicyRouter();
        RouterFunction<ServerResponse> routes = router.policyRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        samplePolicyResponse = new PolicyResponse(
                1L, "POL-1790093637581-1", 3L, 1L, 1L, 25000.0, 187.50, "ACTIVE", "2026-09-22", "2027-09-22", LocalDateTime.now()
        );
    }

    @Test
    void createFromQuote_Success() {
        when(policyService.createPolicyFromQuote(3L)).thenReturn(Mono.just(samplePolicyResponse));

        webTestClient.post()
                .uri("/api/policies/from-quote/3")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.policyNumber").isEqualTo("POL-1790093637581-1")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void getById_Found() {
        when(policyService.getById(1L)).thenReturn(Mono.just(samplePolicyResponse));

        webTestClient.get()
                .uri("/api/policies/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.policyNumber").isEqualTo("POL-1790093637581-1");
    }

    @Test
    void getByCustomerId_ReturnsList() {
        when(policyService.getByCustomerId(1L)).thenReturn(Flux.just(samplePolicyResponse));

        webTestClient.get()
                .uri("/api/policies/customer/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1);
    }
}
