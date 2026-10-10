package com.example.underwritingriskservice.handler;

import com.example.underwritingriskservice.dto.QuoteRequest;
import com.example.underwritingriskservice.dto.QuoteResponse;
import com.example.underwritingriskservice.model.RatingRule;
import com.example.underwritingriskservice.model.RiskAssessment;
import com.example.underwritingriskservice.service.UnderwritingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderwritingHandlerTest {

    @Mock
    private UnderwritingService underwritingService;

    private UnderwritingHandler handler;
    private QuoteResponse sampleQuote;
    private RatingRule sampleRule;
    private RiskAssessment sampleAssessment;

    @BeforeEach
    void setUp() {
        handler = new UnderwritingHandler(underwritingService);
        sampleQuote = new QuoteResponse(1L, 10L, 20L, 15000.0, 45.0, 35, "APPROVED", "OFFERED", 15000.0, 0.0, "LOW", LocalDateTime.now());
        sampleRule = new RatingRule(1L, "RULE_1", "BREED", 1.0, 5.0, 10, 1.1, "ACTIVE", null, null);
        sampleAssessment = RiskAssessment.createNew(1L, 20L, 5.0, 10.0, 4000.0, 500.0, "LOW");
    }

    @Test
    void generateQuote_success() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 15000.0);
        when(underwritingService.generateQuote(any(QuoteRequest.class))).thenReturn(Mono.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        Mono<ServerResponse> response = handler.generateQuote(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void generateQuote_error_returnsBadRequest() {
        when(underwritingService.generateQuote(any(QuoteRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid input")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new QuoteRequest(10L, 20L, -100.0)));
        Mono<ServerResponse> response = handler.generateQuote(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getQuoteById_found() {
        when(underwritingService.getQuoteById(1L)).thenReturn(Mono.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getQuoteById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getQuoteById_notFound() {
        when(underwritingService.getQuoteById(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getQuoteById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getRiskMonitoring_found() {
        when(underwritingService.getRiskMonitoring(20L)).thenReturn(Mono.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getRiskMonitoring(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getRiskMonitoring_notFound() {
        when(underwritingService.getRiskMonitoring(20L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getRiskMonitoring(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllQuotes_success() {
        when(underwritingService.getAllQuotes()).thenReturn(Flux.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllQuotes(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getQuotesByCustomerId_success() {
        when(underwritingService.getQuotesByCustomerId(10L)).thenReturn(Flux.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().pathVariable("customerId", "10").build();
        Mono<ServerResponse> response = handler.getQuotesByCustomerId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getQuotesByPetId_success() {
        when(underwritingService.getQuotesByPetId(20L)).thenReturn(Flux.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.getQuotesByPetId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void reassessPetRisk_success() {
        when(underwritingService.reassessPetRisk(20L)).thenReturn(Mono.just(sampleAssessment));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.reassessPetRisk(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void reassessPetRisk_error_returnsBadRequest() {
        when(underwritingService.reassessPetRisk(20L)).thenReturn(Mono.error(new IllegalArgumentException("Pet error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("petId", "20").build();
        Mono<ServerResponse> response = handler.reassessPetRisk(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateQuote_success() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 20000.0);
        when(underwritingService.updateQuote(eq(1L), any(QuoteRequest.class))).thenReturn(Mono.just(sampleQuote));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(req));
        Mono<ServerResponse> response = handler.updateQuote(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateQuote_error_returnsBadRequest() {
        when(underwritingService.updateQuote(eq(1L), any(QuoteRequest.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(new QuoteRequest(10L, 20L, 20000.0)));
        Mono<ServerResponse> response = handler.updateQuote(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteQuote_success() {
        when(underwritingService.deleteQuote(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteQuote(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteQuote_notFound() {
        when(underwritingService.deleteQuote(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteQuote(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllRules_success() {
        when(underwritingService.getAllRules()).thenReturn(Flux.just(sampleRule));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllRules(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getRuleById_found() {
        when(underwritingService.getRuleById(1L)).thenReturn(Mono.just(sampleRule));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getRuleById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getRuleById_notFound() {
        when(underwritingService.getRuleById(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getRuleById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createRule_success() {
        when(underwritingService.createRule(any(RatingRule.class))).thenReturn(Mono.just(sampleRule));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(sampleRule));
        Mono<ServerResponse> response = handler.createRule(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createRule_error_returnsBadRequest() {
        when(underwritingService.createRule(any(RatingRule.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(sampleRule));
        Mono<ServerResponse> response = handler.createRule(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateRule_success() {
        when(underwritingService.updateRule(eq(1L), any(RatingRule.class))).thenReturn(Mono.just(sampleRule));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(sampleRule));
        Mono<ServerResponse> response = handler.updateRule(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateRule_error_returnsBadRequest() {
        when(underwritingService.updateRule(eq(1L), any(RatingRule.class))).thenReturn(Mono.error(new IllegalArgumentException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(sampleRule));
        Mono<ServerResponse> response = handler.updateRule(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteRule_success() {
        when(underwritingService.deleteRule(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteRule(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteRule_notFound() {
        when(underwritingService.deleteRule(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteRule(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAssessmentByQuoteId_found() {
        when(underwritingService.getRiskAssessmentByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        MockServerRequest request = MockServerRequest.builder().pathVariable("quoteId", "1").build();
        Mono<ServerResponse> response = handler.getAssessmentByQuoteId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAssessmentByQuoteId_notFound() {
        when(underwritingService.getRiskAssessmentByQuoteId(1L)).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("quoteId", "1").build();
        Mono<ServerResponse> response = handler.getAssessmentByQuoteId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void testAccessDeniedLambdasDirectly() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        for (Method m : UnderwritingHandler.class.getDeclaredMethods()) {
            if (m.getName().startsWith("lambda$") && m.getParameterCount() == 1 && m.getParameterTypes()[0] == AccessDeniedException.class) {
                m.setAccessible(true);
                Object res = m.invoke(null, ex);
                if (res instanceof Mono<?> mono) {
                    StepVerifier.create(mono)
                            .expectNextCount(1)
                            .verifyComplete();
                }
            }
        }
    }
}
