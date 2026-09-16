package com.example.paymentfundservice.service;

import com.example.paymentfundservice.dto.CreateFundRequest;
import com.example.paymentfundservice.dto.EligibilityResponse;
import com.example.paymentfundservice.dto.ExpenseRequest;
import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.model.FundTransaction;
import com.example.paymentfundservice.model.PetContinuityFund;
import com.example.paymentfundservice.model.PremiumPayment;
import com.example.paymentfundservice.repository.FundRepository;
import com.example.paymentfundservice.repository.PaymentRepository;
import com.example.paymentfundservice.repository.TransactionRepository;
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

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentFundServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FundRepository fundRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec postUriSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec getUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Mock
    private ReactiveCircuitBreaker circuitBreaker;

    private PaymentFundService paymentFundService;

    private PetContinuityFund sampleFund;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        paymentFundService = new PaymentFundService(paymentRepository, fundRepository, transactionRepository, webClientBuilder, circuitBreakerFactory);

        sampleFund = PetContinuityFund.create(100L, 20L, 10000.0, 500.0, 2000.0, 1000.0);
        sampleFund.setId(1L);
        sampleFund.setStatus("ACTIVE");
    }

    @Test
    @SuppressWarnings("unchecked")
    void processPremiumPayment_successPath_activatesPolicy() {
        PaymentRequest req = new PaymentRequest(100L, 45.0, "SIMULATED_CARD", false);
        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "SIMULATED_CARD", "SUCCESS");
        payment.setId(10L);

        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));
        when(circuitBreakerFactory.create("paymentCB")).thenReturn(circuitBreaker);

        when(webClient.post()).thenReturn(postUriSpec);
        when(postUriSpec.uri(anyString(), any(Object[].class))).thenReturn(postUriSpec);
        when(postUriSpec.header(anyString(), anyString())).thenReturn(postUriSpec);
        when(postUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> {
                    assertEquals(10L, p.getId());
                    assertEquals("SUCCESS", p.getStatus());
                })
                .verifyComplete();

        verify(paymentRepository).save(any(PremiumPayment.class));
    }

    @Test
    void processPremiumPayment_simulatedFailure_doesNotActivatePolicy() {
        PaymentRequest req = new PaymentRequest(100L, 45.0, "SIMULATED_CARD", true);
        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "SIMULATED_CARD", "FAILED");
        payment.setId(11L);

        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));
        when(circuitBreakerFactory.create("paymentCB")).thenReturn(circuitBreaker);

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> assertEquals("FAILED", p.getStatus()))
                .verifyComplete();

        verify(webClient, never()).post();
    }

    @Test
    void processPremiumPayment_invalidAmount_throwsError() {
        PaymentRequest req = new PaymentRequest(100L, -10.0, "CARD", false);
        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createFund_successful() {
        CreateFundRequest req = new CreateFundRequest(100L, 20L, 10000.0, 500.0, 2000.0, 1000.0);
        when(fundRepository.findByPolicyId(100L)).thenReturn(Mono.empty());
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        FundTransaction initialTxn = FundTransaction.create(1L, "DEPOSIT", 10000.0, 10000.0, "Initial allocation", "SUCCESS");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(initialTxn));

        StepVerifier.create(paymentFundService.createFund(req))
                .assertNext(f -> {
                    assertEquals(1L, f.getId());
                    assertEquals(100L, f.getPolicyId());
                })
                .verifyComplete();

        verify(fundRepository).save(any(PetContinuityFund.class));
        verify(transactionRepository).save(any(FundTransaction.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void disburseMonthly_eligible_deductsAllowanceAndSucceeds() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(circuitBreakerFactory.create("paymentCB")).thenReturn(circuitBreaker);

        when(webClient.get()).thenReturn(getUriSpec);
        when(getUriSpec.uri(any(Function.class))).thenReturn(getUriSpec);
        when(getUriSpec.header(anyString(), anyString())).thenReturn(getUriSpec);
        when(getUriSpec.retrieve()).thenReturn(responseSpec);

        EligibilityResponse eligibility = new EligibilityResponse(true, "Eligible", 10L);
        when(responseSpec.bodyToMono(EligibilityResponse.class)).thenReturn(Mono.just(eligibility));
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));
        FundTransaction txn = FundTransaction.create(1L, "DISBURSEMENT", 500.0, 9500.0, "Monthly allowance", "SUCCESS");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.disburseMonthly(1L, 20L, 10L))
                .assertNext(t -> {
                    assertEquals("DISBURSEMENT", t.getTransactionType());
                    assertEquals("SUCCESS", t.getStatus());
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void disburseMonthly_ineligible_suspendsFund() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(circuitBreakerFactory.create("paymentCB")).thenReturn(circuitBreaker);

        when(webClient.get()).thenReturn(getUriSpec);
        when(getUriSpec.uri(any(Function.class))).thenReturn(getUriSpec);
        when(getUriSpec.header(anyString(), anyString())).thenReturn(getUriSpec);
        when(getUriSpec.retrieve()).thenReturn(responseSpec);

        EligibilityResponse eligibility = new EligibilityResponse(false, "Verification failed", null);
        when(responseSpec.bodyToMono(EligibilityResponse.class)).thenReturn(Mono.just(eligibility));
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));
        FundTransaction txn = FundTransaction.create(1L, "DISBURSEMENT", 0.0, 10000.0, "Suspended", "SUSPENDED");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.disburseMonthly(1L, 20L, 10L))
                .assertNext(t -> assertEquals("SUSPENDED", t.getStatus()))
                .verifyComplete();

        assertEquals("SUSPENDED", sampleFund.getStatus());
    }

    @Test
    void recordExpense_successful() {
        ExpenseRequest req = new ExpenseRequest("VET_EXPENSE", 300.0, "Annual checkup vaccination");
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        FundTransaction txn = FundTransaction.create(1L, "VET_EXPENSE", 300.0, 9700.0, "Annual checkup", "SUCCESS");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.recordExpense(1L, req))
                .assertNext(t -> assertEquals("VET_EXPENSE", t.getTransactionType()))
                .verifyComplete();
    }

    @Test
    void recordExpense_insufficientBalance_throwsError() {
        sampleFund.setCurrentBalance(100.0);
        ExpenseRequest req = new ExpenseRequest("VET_EXPENSE", 500.0, "Major surgery");
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(paymentFundService.recordExpense(1L, req))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("Insufficient fund balance"))
                .verify();
    }

    @Test
    void getFundById_found() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(paymentFundService.getFundById(1L))
                .assertNext(f -> assertEquals(1L, f.getId()))
                .verifyComplete();
    }

    @Test
    void getTransactions_returnsFlux() {
        FundTransaction txn1 = FundTransaction.create(1L, "DEPOSIT", 10000.0, 10000.0, "Initial", "SUCCESS");
        when(transactionRepository.findByFundIdOrderByCreatedAtDesc(1L)).thenReturn(Flux.just(txn1));

        StepVerifier.create(paymentFundService.getTransactions(1L))
                .expectNextMatches(t -> t.getTransactionType().equals("DEPOSIT"))
                .verifyComplete();
    }

    @Test
    void deleteFund_successful() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.delete(sampleFund)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.deleteFund(1L))
                .verifyComplete();

        verify(fundRepository).delete(sampleFund);
    }
}
