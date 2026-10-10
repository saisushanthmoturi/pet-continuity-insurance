package com.example.underwritingriskservice.service;

import com.example.underwritingriskservice.dto.*;
import com.example.underwritingriskservice.model.Quote;
import com.example.underwritingriskservice.model.RatingRule;
import com.example.underwritingriskservice.model.RiskAssessment;
import com.example.underwritingriskservice.repository.QuoteRepository;
import com.example.underwritingriskservice.repository.RatingRuleRepository;
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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private RatingRuleRepository ratingRuleRepository;

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
    private UnderwritingService secondaryUnderwritingService;

    private Quote sampleQuote;
    private RiskAssessment sampleAssessment;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);

        when(circuitBreaker.run(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> inv.getArgument(0));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        when(uriSpec.header(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.empty());
        when(responseSpec.bodyToFlux(any(Class.class))).thenReturn(Flux.empty());

        underwritingService = new UnderwritingService(
                quoteRepository,
                riskAssessmentRepository,
                ratingRuleRepository,
                webClientBuilder,
                circuitBreakerFactory
        );

        secondaryUnderwritingService = new UnderwritingService(
                quoteRepository,
                riskAssessmentRepository,
                webClientBuilder,
                circuitBreakerFactory
        );

        sampleQuote = Quote.createNew(10L, 20L, 10000.0, 30.0, 20, "APPROVED");
        sampleQuote.setId(1L);

        sampleAssessment = RiskAssessment.createNew(1L, 20L, 0.0, 0.0, 15120.0, 5120.0, "LOW");
        sampleAssessment.setId(100L);
    }

    @Test
    void generateQuote_youngHealthyPet_approved() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 10000.0);
        PetDto pet = new PetDto(20L, 10L, "Buddy", "Dog", "Labrador", 3, 25.0, "MALE", 1200.0);
        CustomerDto customer = new CustomerDto(10L, 100L, "John Doe", "john@example.com", "1234567890", "Address");

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.empty());
        when(responseSpec.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customer));
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.empty());

        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.generateQuote(req))
                .assertNext(res -> {
                    assertEquals(1L, res.id());
                    assertEquals(10L, res.customerId());
                    assertEquals(20L, res.petId());
                    assertEquals("APPROVED", res.decision());
                })
                .verifyComplete();
    }

    @Test
    void generateQuote_breedAndHealthAndCarePlanCombinations() {
        // High risk breed (Bulldog), older pet (age 10), medical records with cost, carePlan with backup caretaker
        QuoteRequest req = new QuoteRequest(10L, 20L, 20000.0);
        PetDto pet = new PetDto(20L, 10L, "Rocky", "Dog", "English Bulldog", 10, 28.0, "MALE", 1500.0);
        CustomerDto customer = new CustomerDto(10L, 100L, "Jane Doe", "jane@example.com", "1234567890", "Address");
        MedicalRecordDto med1 = new MedicalRecordDto(1L, 20L, "Hip Dysplasia", "2023-01-01", "Surgery", 2000.0);
        MedicalRecordDto med2 = new MedicalRecordDto(2L, 20L, "Allergy", "2023-05-01", "Meds", null);
        CarePlanDto cp = new CarePlanDto(1L, 20L, 5L, 6L, "Vet", "Feed", "None");

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.just(med1, med2));
        when(responseSpec.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customer));
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.just(cp));

        Quote qHigh = Quote.createNew(10L, 20L, 20000.0, 70.0, 78, "APPROVED");
        qHigh.setId(2L);
        RiskAssessment raHigh = RiskAssessment.createNew(2L, 20L, 36.0, 56.0, 18000.0, 0.0, "HIGH");
        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(qHigh));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(Mono.just(raHigh));

        StepVerifier.create(underwritingService.generateQuote(req))
                .assertNext(res -> assertEquals(2L, res.id()))
                .verifyComplete();
    }

    @Test
    void generateQuote_poodleBreed_carePlanWithoutBackup() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 15000.0);
        PetDto pet = new PetDto(20L, 10L, "Coco", "Dog", "Standard Poodle", 4, 18.0, "FEMALE", 1000.0);
        CustomerDto customer = new CustomerDto(10L, 100L, "Alice", "alice@example.com", "1234567890", "Address");
        CarePlanDto cp = new CarePlanDto(1L, 20L, 5L, null, "Vet", "Feed", "None");

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.empty());
        when(responseSpec.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customer));
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.just(cp));

        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.generateQuote(req))
                .assertNext(res -> assertEquals(1L, res.id()))
                .verifyComplete();
    }

    @Test
    void generateQuote_ownershipMismatch_throwsError() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 10000.0);
        // Pet belongs to customer 99, not 10
        PetDto pet = new PetDto(20L, 99L, "Buddy", "Dog", "Labrador", 3, 25.0, "MALE", 1200.0);
        CustomerDto customer = new CustomerDto(10L, 100L, "John Doe", "john@example.com", "1234567890", "Address");

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.empty());
        when(responseSpec.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customer));
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.generateQuote(req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void generateQuote_petServiceFallback_throwsIllegalState() {
        QuoteRequest req = new QuoteRequest(10L, 20L, 10000.0);
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("PetService down"));
        });

        StepVerifier.create(underwritingService.generateQuote(req))
                .expectError(IllegalStateException.class)
                .verify();
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
    void getQuotesByCustomerId_withAndWithoutAssessment() {
        when(quoteRepository.findByCustomerId(10L)).thenReturn(Flux.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.getQuotesByCustomerId(10L))
                .expectNextMatches(res -> res.id().equals(1L) && res.riskLevel().equals("LOW"))
                .verifyComplete();

        // Without assessment
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.empty());
        StepVerifier.create(underwritingService.getQuotesByCustomerId(10L))
                .expectNextMatches(res -> res.id().equals(1L))
                .verifyComplete();
    }

    @Test
    void getQuotesByPetId_withAndWithoutAssessment() {
        when(quoteRepository.findByPetId(20L)).thenReturn(Flux.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.getQuotesByPetId(20L))
                .expectNextMatches(res -> res.id().equals(1L))
                .verifyComplete();

        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.empty());
        StepVerifier.create(underwritingService.getQuotesByPetId(20L))
                .expectNextMatches(res -> res.id().equals(1L))
                .verifyComplete();
    }

    @Test
    void reassessPetRisk_withExistingQuotes_updatesLatest() {
        PetDto pet = new PetDto(20L, 10L, "Buddy", "Dog", "German_Shepherd", 6, 30.0, "MALE", 1400.0);
        MedicalRecordDto med = new MedicalRecordDto(1L, 20L, "Ear Infection", "2023-01-01", "Drops", 150.0);
        CarePlanDto cp = new CarePlanDto(1L, 20L, 5L, 6L, "Vet", "Feed", "None");

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.just(med));
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.just(cp));

        when(quoteRepository.findByPetId(20L)).thenReturn(Flux.just(sampleQuote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.reassessPetRisk(20L))
                .assertNext(ass -> assertEquals(100L, ass.getId()))
                .verifyComplete();
    }

    @Test
    void reassessPetRisk_withoutExistingQuotes_createsDraft() {
        PetDto pet = new PetDto(20L, 10L, "Buddy", "Dog", null, 2, 10.0, "FEMALE", null);

        when(responseSpec.bodyToMono(PetDto.class)).thenReturn(Mono.just(pet));
        when(responseSpec.bodyToFlux(MedicalRecordDto.class)).thenReturn(Flux.empty());
        when(responseSpec.bodyToMono(CarePlanDto.class)).thenReturn(Mono.empty());

        when(quoteRepository.findByPetId(20L)).thenReturn(Flux.empty());
        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.reassessPetRisk(20L))
                .assertNext(ass -> assertEquals(100L, ass.getId()))
                .verifyComplete();
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
    void getRiskMonitoring_foundWithQuoteId() {
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
    void getRiskMonitoring_foundWithoutQuoteId() {
        RiskAssessment noQuoteAss = RiskAssessment.createNew(null, 20L, 5.0, 5.0, 5000.0, 0.0, "MODERATE");
        when(riskAssessmentRepository.findFirstByPetIdOrderByAssessmentDateDesc(20L)).thenReturn(Mono.just(noQuoteAss));

        StepVerifier.create(underwritingService.getRiskMonitoring(20L))
                .assertNext(res -> {
                    assertEquals(20L, res.petId());
                    assertEquals("MONITORING", res.decision());
                })
                .verifyComplete();
    }

    @Test
    void getRiskMonitoring_notFound_throwsError() {
        when(riskAssessmentRepository.findFirstByPetIdOrderByAssessmentDateDesc(20L)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.getRiskMonitoring(20L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAllQuotes_withAndWithoutAssessment() {
        when(quoteRepository.findAll()).thenReturn(Flux.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.getAllQuotes())
                .expectNextMatches(res -> res.id().equals(1L))
                .verifyComplete();

        // Without assessment
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.empty());
        StepVerifier.create(underwritingService.getAllQuotes())
                .expectNextMatches(res -> res.id().equals(1L) && res.riskLevel().equals("MODERATE"))
                .verifyComplete();
    }

    @Test
    void updateQuote_withDifferentRiskScores() {
        // Score <= 40
        sampleQuote.setRiskScore(30);
        QuoteRequest req = new QuoteRequest(10L, 20L, 15000.0);
        when(quoteRepository.findById(1L)).thenReturn(Mono.just(sampleQuote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(Mono.just(sampleQuote));
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.updateQuote(1L, req))
                .assertNext(res -> assertEquals(15000.0, sampleQuote.getRequestedCoverage()))
                .verifyComplete();

        // Score <= 70
        sampleQuote.setRiskScore(60);
        StepVerifier.create(underwritingService.updateQuote(1L, req))
                .assertNext(res -> assertNotNull(res))
                .verifyComplete();

        // Score > 70
        sampleQuote.setRiskScore(80);
        StepVerifier.create(underwritingService.updateQuote(1L, req))
                .assertNext(res -> assertNotNull(res))
                .verifyComplete();

        // Null coverage in request
        QuoteRequest reqNullCov = new QuoteRequest(10L, 20L, null);
        StepVerifier.create(underwritingService.updateQuote(1L, reqNullCov))
                .assertNext(res -> assertNotNull(res))
                .verifyComplete();
    }

    @Test
    void updateQuote_notFound_throwsError() {
        when(quoteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.updateQuote(99L, new QuoteRequest(10L, 20L, 10000.0)))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteQuote_successful() {
        when(quoteRepository.findById(1L)).thenReturn(Mono.just(sampleQuote));
        when(quoteRepository.delete(sampleQuote)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.deleteQuote(1L))
                .verifyComplete();

        verify(quoteRepository).delete(sampleQuote);
    }

    @Test
    void deleteQuote_notFound_throwsError() {
        when(quoteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.deleteQuote(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void ratingRules_withRepository() {
        RatingRule rule = new RatingRule(1L, "RULE", "BREED", 1.0, 5.0, 10, 1.2, "ACTIVE", null, null);
        when(ratingRuleRepository.findAll()).thenReturn(Flux.just(rule));
        when(ratingRuleRepository.findById(1L)).thenReturn(Mono.just(rule));
        when(ratingRuleRepository.findById(99L)).thenReturn(Mono.empty());
        when(ratingRuleRepository.save(any(RatingRule.class))).thenReturn(Mono.just(rule));
        when(ratingRuleRepository.delete(rule)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.getAllRules())
                .expectNext(rule)
                .verifyComplete();

        StepVerifier.create(underwritingService.getRuleById(1L))
                .expectNext(rule)
                .verifyComplete();

        StepVerifier.create(underwritingService.getRuleById(99L))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(underwritingService.createRule(rule))
                .expectNext(rule)
                .verifyComplete();

        RatingRule updateDto = new RatingRule(null, "UPDATED", "AGE", 2.0, 6.0, 15, 1.3, "INACTIVE", null, null);
        StepVerifier.create(underwritingService.updateRule(1L, updateDto))
                .expectNext(rule)
                .verifyComplete();

        StepVerifier.create(underwritingService.updateRule(99L, updateDto))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(underwritingService.deleteRule(1L))
                .verifyComplete();

        StepVerifier.create(underwritingService.deleteRule(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void ratingRules_withoutRepository() {
        RatingRule rule = new RatingRule();
        StepVerifier.create(secondaryUnderwritingService.getAllRules())
                .verifyComplete();

        StepVerifier.create(secondaryUnderwritingService.getRuleById(1L))
                .verifyComplete();

        StepVerifier.create(secondaryUnderwritingService.createRule(rule))
                .verifyComplete();

        StepVerifier.create(secondaryUnderwritingService.updateRule(1L, rule))
                .verifyComplete();

        StepVerifier.create(secondaryUnderwritingService.deleteRule(1L))
                .verifyComplete();
    }

    @Test
    void getRiskAssessmentByQuoteId_found() {
        when(riskAssessmentRepository.findByQuoteId(1L)).thenReturn(Mono.just(sampleAssessment));

        StepVerifier.create(underwritingService.getRiskAssessmentByQuoteId(1L))
                .assertNext(ass -> assertEquals(100L, ass.getId()))
                .verifyComplete();
    }

    @Test
    void getRiskAssessmentByQuoteId_notFound_throwsError() {
        when(riskAssessmentRepository.findByQuoteId(99L)).thenReturn(Mono.empty());

        StepVerifier.create(underwritingService.getRiskAssessmentByQuoteId(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
