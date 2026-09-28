package com.example.underwritingriskservice.router;

import com.example.underwritingriskservice.dto.QuoteRequest;
import com.example.underwritingriskservice.dto.QuoteResponse;
import com.example.underwritingriskservice.handler.UnderwritingHandler;
import com.example.underwritingriskservice.service.UnderwritingService;
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
class UnderwritingRouterTest {

    @Mock
    private UnderwritingService underwritingService;

    private WebTestClient webTestClient;
    private QuoteResponse sampleQuoteResponse;

    @BeforeEach
    void setUp() {
        UnderwritingHandler handler = new UnderwritingHandler(underwritingService);
        UnderwritingRouter router = new UnderwritingRouter();
        RouterFunction<ServerResponse> routes = router.underwritingRoutes(handler);
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        sampleQuoteResponse = new QuoteResponse(
                1L, 1L, 1L, 25000.0, 187.50, 34, "APPROVED", "ISSUED", 20000.0, 5000.0, "MEDIUM", LocalDateTime.now().plusDays(30)
        );
    }

    @Test
    void generateQuote_Success() {
        when(underwritingService.generateQuote(any(QuoteRequest.class))).thenReturn(Mono.just(sampleQuoteResponse));

        QuoteRequest request = new QuoteRequest(1L, 1L, 25000.0);

        webTestClient.post()
                .uri("/api/underwriting/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.customerId").isEqualTo(1)
                .jsonPath("$.monthlyPremium").isEqualTo(187.50);
    }

    @Test
    void getQuoteById_Found() {
        when(underwritingService.getQuoteById(1L)).thenReturn(Mono.just(sampleQuoteResponse));

        webTestClient.get()
                .uri("/api/underwriting/quotes/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("ISSUED");
    }

    @Test
    void getQuotesByCustomerId_ReturnsList() {
        when(underwritingService.getQuotesByCustomerId(1L)).thenReturn(Flux.just(sampleQuoteResponse));

        webTestClient.get()
                .uri("/api/underwriting/quotes/customer/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1);
    }
}
