package com.example.underwritingriskservice.service;

import com.example.underwritingriskservice.dto.*;
import com.example.underwritingriskservice.model.Quote;
import com.example.underwritingriskservice.model.RiskAssessment;
import com.example.underwritingriskservice.repository.QuoteRepository;
import com.example.underwritingriskservice.repository.RiskAssessmentRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UnderwritingServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

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

    private UnderwritingService underwritingService;

    private Quote sampleQuote;
    private RiskAssessment sampleAssessment;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        underwritingService = new UnderwritingService(quoteRepository, riskAssessmentRepository, webClientBuilder, circuitBreakerFactory);

        sampleQuote = Quote.createNew(10L, 20L, 10000.0, 30.0, 20, "APPROVED");
        sampleQuote.setId(1L);

        sampleAssessment = RiskAssessment.createNew(1L, 20L, 0.0, 0.0, 15120.0, 5120.0, "LOW");
        sampleAssessment.setId(100L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void generateQuote_youngHealthyPet_approved() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 10000.0);
        PetDto pet = new PetDto(20L, 10L, "Buddy", "Dog", "Labrador", 3, 25.0, "MALE", 1200.0);
        CustomerDto customer = new CustomerDto(10L, 100L, "John Doe", "john@example.com", "1234567890", "Address");

        when(circuitBreakerFactory.create("underwritingCB")).thenReturn(circuitBreaker);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.empty());
        when(responseSpec.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customer));
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.empty());

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.generateQuote(req))
                .assertNext(res -> {
                    assertEquals(1L, res.id());
                    assertEquals(10L, res.customerId());
                    assertEquals(20L, res.petId());
                    assertEquals("APPROVED", res.decision());
                    assertEquals("LOW", res.riskLevel());
                })
                .verifyComplete();

        verify(quoteRepository).save(any(Quote.class));
        verify(riskAssessmentRepository).save(any(RiskAssessment.class));
    }

    @Test
    void generateQuote_missingParameters_throwsError() {
        QuoteRequest reqNoCustomer = new QuoteRequest(null, 20L, 10000.0);
        StepVerifier.create(underwritingService.generateQuote(reqNoCustomer))
                .expectError(IllegalArgumentException.class)
                .verify();

        QuoteRequest reqNoPet = new QuoteRequest(10L, null, 10000.0);
        StepVerifier.create(underwritingService.generateQuote(reqNoPet))
                .expectError(IllegalArgumentException.class)
                .verify();

        QuoteRequest reqInvalidCoverage = new QuoteRequest(10L, 20L, 0.0);
        StepVerifier.create(underwritingService.generateQuote(reqInvalidCoverage))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getQuoteById_found() {
        when(quoteRepository.findById(1L)).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.getQuoteById(1L))
                .assertNext(res -> {
                    assertEquals(1L, res.id());
                    assertEquals("APPROVED", res.decision());
                    assertEquals("LOW", res.riskLevel());
                })
                .verifyComplete();
    }

    @Test
    void getQuoteById_notFound_throwsError() {
        when(quoteRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.getQuoteById(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAllQuotes_returnsFlux() {
        when(quoteRepository.findAll()).thenReturn(Flux.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.getAllQuotes())
                .expectNextMatches(res -> res.id().equals(1L))
                .verifyComplete();
    }

    @Test
    void getRiskMonitoring_found() {
        when(riskAssessmentRepository.findFirstByPetIdOrderByAssessmentDateDesc(20L)).thenReturn(Mono.just(sampleAssessment));
        when(quoteRepository.findById(1L)).thenReturn(Mono.just(sampleQuote));

        StepVerifier.create(underwritingService.getRiskMonitoring(20L))
                .assertNext(res -> {
                    assertEquals(20L, res.petId());
                    assertEquals("LOW", res.riskLevel());
                })
                .verifyComplete();
    }

    @Test
    void updateQuote_successful() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 15000.0);
        when(quoteRepository.findById(1L)).thenReturn(Mono.just(sampleQuote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.updateQuote(1L, req))
                .assertNext(res -> assertEquals(15000.0, sampleQuote.getRequestedCoverage()))
                .verifyComplete();
    }

    @Test
    void deleteQuote_successful() {
        when(quoteRepository.findById(1L)).thenReturn(Mono.just(sampleQuote));
        when(quoteRepository.delete(sampleQuote)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.deleteQuote(1L))
                .verifyComplete();

        verify(quoteRepository).delete(sampleQuote);
    }
}
