package com.example.claimsservice.router;

import com.example.claimsservice.dto.ClaimRequest;
import com.example.claimsservice.dto.ClaimResponse;
import com.example.claimsservice.handler.ClaimsHandler;
import com.example.claimsservice.service.ClaimsService;
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
class ClaimsRouterTest {

    @Mock
    private ClaimsService claimsService;

    private WebTestClient webTestClient;
    private ClaimResponse sampleClaimResponse;

    @BeforeEach
    void setUp() {
        ClaimsHandler handler = new ClaimsHandler(claimsService);
        ClaimsRouter router = new ClaimsRouter();
        RouterFunction<ServerResponse> routes = router.claimsRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        sampleClaimResponse = new ClaimResponse(
                1L, "CLM-1790097087664", 2L, "Jane Doe", "SPOUSE", "DC-998877", "2026-09-20", "APPROVED", null, "Notes", LocalDateTime.now(), "APPROVED", 10
        );
    }

    @Test
    void fileClaim_Success() {
        when(claimsService.fileClaim(any(ClaimRequest.class))).thenReturn(Mono.just(sampleClaimResponse));

        ClaimRequest request = new ClaimRequest(2L, "Jane Doe", "SPOUSE", "DC-998877", "2026-09-20", "Notes");

        webTestClient.post()
                .uri("/api/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.claimNumber").isEqualTo("CLM-1790097087664")
                .jsonPath("$.status").isEqualTo("APPROVED");
    }

    @Test
    void approveClaim_Success() {
        when(claimsService.approveClaim(1L)).thenReturn(Mono.just(sampleClaimResponse));

        webTestClient.post()
                .uri("/api/claims/1/approve")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("APPROVED");
    }

    @Test
    void getById_Found() {
        when(claimsService.getById(1L)).thenReturn(Mono.just(sampleClaimResponse));

        webTestClient.get()
                .uri("/api/claims/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.claimantName").isEqualTo("Jane Doe");
    }

    @Test
    void getByPolicyId_ReturnsList() {
        when(claimsService.getByPolicyId(2L)).thenReturn(Flux.just(sampleClaimResponse));

        webTestClient.get()
                .uri("/api/claims/policy/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1);
    }
}
