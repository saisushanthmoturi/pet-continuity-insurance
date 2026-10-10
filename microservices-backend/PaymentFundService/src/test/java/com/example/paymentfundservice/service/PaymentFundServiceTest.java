package com.example.paymentfundservice.service;

import com.example.paymentfundservice.dto.CreateFundRequest;
import com.example.paymentfundservice.dto.EligibilityResponse;
import com.example.paymentfundservice.dto.ExpenseRequest;
import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.dto.PolicyDto;
import com.example.paymentfundservice.model.*;
import com.example.paymentfundservice.repository.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private DisbursementRepository disbursementRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private FundStatusHistoryRepository fundStatusHistoryRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec getUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec postUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Mock
    private ReactiveCircuitBreaker circuitBreaker;

    private PaymentFundService paymentFundService;
    private PaymentFundService secondaryPaymentFundService;
    private PetContinuityFund sampleFund;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);

        // Typed matchers for circuitBreaker.run to avoid ambiguity
        when(circuitBreaker.run(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> inv.getArgument(0));

        when(webClient.get()).thenReturn(getUriSpec);
        when(getUriSpec.uri(anyString(), any(Object[].class))).thenReturn(getUriSpec);
        when(getUriSpec.uri(any(Function.class))).thenReturn(getUriSpec);
        when(getUriSpec.header(anyString(), anyString())).thenReturn(getUriSpec);
        when(getUriSpec.retrieve()).thenReturn(responseSpec);

        when(webClient.post()).thenReturn(postUriSpec);
        when(postUriSpec.uri(anyString(), any(Object[].class))).thenReturn(postUriSpec);
        when(postUriSpec.header(anyString(), anyString())).thenReturn(postUriSpec);
        when(postUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.empty());

        paymentFundService = new PaymentFundService(
                paymentRepository,
                fundRepository,
                transactionRepository,
                disbursementRepository,
                expenseRepository,
                fundStatusHistoryRepository,
                webClientBuilder,
                circuitBreakerFactory
        );

        secondaryPaymentFundService = new PaymentFundService(
                paymentRepository,
                fundRepository,
                transactionRepository,
                webClientBuilder,
                circuitBreakerFactory
        );

        sampleFund = PetContinuityFund.create(100L, 20L, 10000.0, 500.0, 2000.0, 1000.0);
        sampleFund.setId(1L);
    }

    @Test
    void processPremiumPayment_withCustomerId_successful_activatesPolicy() {
        PaymentRequest req = new PaymentRequest(100L, 5L, 45.0, "CARD", false);
        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "CARD", "SUCCESS");
        payment.setId(10L);
        payment.setCustomerId(5L);

        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> {
                    assertEquals(10L, p.getId());
                    assertEquals("SUCCESS", p.getStatus());
                })
                .verifyComplete();

        verify(paymentRepository).save(any(PremiumPayment.class));
    }

    @Test
    void processPremiumPayment_nullCustomerId_fetchesFromPolicyService_activatesPolicy() {
        PaymentRequest req = new PaymentRequest(100L, 45.0, "CARD", false);
        PolicyDto policyDto = new PolicyDto(100L, "POL-1", 10L, 7L, 20L, 10000.0, 45.0, "ACTIVE");
        when(responseSpec.bodyToMono(PolicyDto.class)).thenReturn(Mono.just(policyDto));

        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "CARD", "SUCCESS");
        payment.setId(10L);
        payment.setCustomerId(7L);
        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> {
                    assertEquals(10L, p.getId());
                    assertEquals("SUCCESS", p.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void processPremiumPayment_policyFetchFallback_usesDefaultCustomerId() {
        PaymentRequest req = new PaymentRequest(100L, 45.0, "CARD", false);
        // Force fallback by making circuit breaker invoke error handler
        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("PolicyService down"));
        });

        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "CARD", "SUCCESS");
        payment.setId(10L);
        payment.setCustomerId(1L);
        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> assertEquals("SUCCESS", p.getStatus()))
                .verifyComplete();
    }

    @Test
    void processPremiumPayment_activatePolicyFallback() {
        PaymentRequest req = new PaymentRequest(100L, 5L, 45.0, null, false);
        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "SIMULATED_CARD", "SUCCESS");
        payment.setId(10L);
        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));

        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("Activate error"));
        });

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> assertEquals("SUCCESS", p.getStatus()))
                .verifyComplete();
    }

    @Test
    void processPremiumPayment_simulatedFailure_doesNotActivatePolicy() {
        PaymentRequest req = new PaymentRequest(100L, 10L, 45.0, "SIMULATED_CARD", true);
        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "SIMULATED_CARD", "FAILED");
        payment.setId(11L);

        when(paymentRepository.save(any(PremiumPayment.class))).thenReturn(Mono.just(payment));

        StepVerifier.create(paymentFundService.processPremiumPayment(req))
                .assertNext(p -> assertEquals("FAILED", p.getStatus()))
                .verifyComplete();

        verify(webClient, never()).post();
    }

    @Test
    void processPremiumPayment_invalidInputs_throwsError() {
        PaymentRequest nullPolicy = new PaymentRequest(null, 10L, 45.0, "CARD", false);
        StepVerifier.create(paymentFundService.processPremiumPayment(nullPolicy))
                .expectError(IllegalArgumentException.class)
                .verify();

        PaymentRequest nullAmount = new PaymentRequest(100L, 10L, null, "CARD", false);
        StepVerifier.create(paymentFundService.processPremiumPayment(nullAmount))
                .expectError(IllegalArgumentException.class)
                .verify();

        PaymentRequest negativeAmount = new PaymentRequest(100L, 10L, -5.0, "CARD", false);
        StepVerifier.create(paymentFundService.processPremiumPayment(negativeAmount))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getPaymentsByCustomerId_returnsFlux() {
        PremiumPayment payment = PremiumPayment.create(100L, 45.0, "CARD", "SUCCESS");
        when(paymentRepository.findByCustomerId(5L)).thenReturn(Flux.just(payment));

        StepVerifier.create(paymentFundService.getPaymentsByCustomerId(5L))
                .expectNext(payment)
                .verifyComplete();
    }

    @Test
    void createFund_whenFundAlreadyExists_returnsExisting() {
        CreateFundRequest req = new CreateFundRequest(100L, 20L, 10000.0, 500.0, 2000.0, 1000.0);
        when(fundRepository.findByPolicyId(100L)).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(paymentFundService.createFund(req))
                .assertNext(f -> assertEquals(1L, f.getId()))
                .verifyComplete();

        verify(fundRepository, never()).save(any(PetContinuityFund.class));
    }

    @Test
    void createFund_whenFundDoesNotExist_createsNew() {
        CreateFundRequest req = new CreateFundRequest(100L, 20L, 10000.0, 500.0, 2000.0, 1000.0);
        when(fundRepository.findByPolicyId(100L)).thenReturn(Mono.empty());
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        FundTransaction initialTxn = FundTransaction.create(1L, "DEPOSIT", 10000.0, 10000.0, "Initial allocation", "SUCCESS");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(initialTxn));

        StepVerifier.create(paymentFundService.createFund(req))
                .assertNext(f -> assertEquals(1L, f.getId()))
                .verifyComplete();

        verify(fundRepository).save(any(PetContinuityFund.class));
        verify(transactionRepository).save(any(FundTransaction.class));
    }

    @Test
    void disburseMonthly_fundNotFound_throwsError() {
        when(fundRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.disburseMonthly(99L, 20L, 10L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void disburseMonthly_fundNotActive_throwsError() {
        sampleFund.setStatus("SUSPENDED");
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(paymentFundService.disburseMonthly(1L, 20L, 10L))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void disburseMonthly_eligible_deductsAllowanceAndSucceeds() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        EligibilityResponse eligibility = new EligibilityResponse(true, "Eligible", 10L);
        when(responseSpec.bodyToMono(EligibilityResponse.class)).thenReturn(Mono.just(eligibility));

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
    void disburseMonthly_eligible_insufficientBalance_createsFailedTxn() {
        sampleFund.setCurrentBalance(200.0);
        sampleFund.setMonthlyAllowance(500.0);
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));

        EligibilityResponse eligibility = new EligibilityResponse(true, "Eligible", 10L);
        when(responseSpec.bodyToMono(EligibilityResponse.class)).thenReturn(Mono.just(eligibility));

        FundTransaction failTxn = FundTransaction.create(1L, "DISBURSEMENT", 0.0, 200.0, "Insufficient fund balance", "FAILED");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(failTxn));

        StepVerifier.create(paymentFundService.disburseMonthly(1L, 20L, 10L))
                .assertNext(t -> assertEquals("FAILED", t.getStatus()))
                .verifyComplete();
    }

    @Test
    void disburseMonthly_ineligible_suspendsFund() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));

        EligibilityResponse eligibility = new EligibilityResponse(false, "Verification failed", null);
        when(responseSpec.bodyToMono(EligibilityResponse.class)).thenReturn(Mono.just(eligibility));

        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));
        FundTransaction txn = FundTransaction.create(1L, "DISBURSEMENT", 0.0, 10000.0, "Suspended", "SUSPENDED");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.disburseMonthly(1L, 20L, 10L))
                .assertNext(t -> assertEquals("SUSPENDED", t.getStatus()))
                .verifyComplete();

        assertEquals("SUSPENDED", sampleFund.getStatus());
    }

    @Test
    void disburseMonthly_careServiceFallback_suspendsFund() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));

        when(circuitBreaker.run(any(Mono.class), any(Function.class))).thenAnswer(inv -> {
            Function<Throwable, Mono<?>> fallback = inv.getArgument(1);
            return fallback.apply(new RuntimeException("Care Service Unavailable"));
        });

        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));
        FundTransaction txn = FundTransaction.create(1L, "DISBURSEMENT", 0.0, 10000.0, "Suspended", "SUSPENDED");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.disburseMonthly(1L, 20L, 10L))
                .assertNext(t -> assertEquals("SUSPENDED", t.getStatus()))
                .verifyComplete();
    }

    @Test
    void recordExpense_invalidAmount_throwsError() {
        ExpenseRequest reqNull = new ExpenseRequest("VET", null, "desc");
        StepVerifier.create(paymentFundService.recordExpense(1L, reqNull))
                .expectError(IllegalArgumentException.class)
                .verify();

        ExpenseRequest reqNeg = new ExpenseRequest("VET", -10.0, "desc");
        StepVerifier.create(paymentFundService.recordExpense(1L, reqNeg))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void recordExpense_fundNotFound_throwsError() {
        ExpenseRequest req = new ExpenseRequest("VET", 100.0, "desc");
        when(fundRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.recordExpense(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
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
    void recordExpense_vetExpense_successful() {
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
    void recordExpense_emergencyExpense_successful() {
        ExpenseRequest req = new ExpenseRequest("EMERGENCY", 400.0, null);
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        FundTransaction txn = FundTransaction.create(1L, "EMERGENCY", 400.0, 9600.0, "EMERGENCY payout", "SUCCESS");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.recordExpense(1L, req))
                .assertNext(t -> assertEquals("EMERGENCY", t.getTransactionType()))
                .verifyComplete();
    }

    @Test
    void recordExpense_nullType_defaultsToVet() {
        ExpenseRequest req = new ExpenseRequest(null, 200.0, "Routine check");
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        FundTransaction txn = FundTransaction.create(1L, "VET_EXPENSE", 200.0, 9800.0, "Routine check", "SUCCESS");
        when(transactionRepository.save(any(FundTransaction.class))).thenReturn(Mono.just(txn));

        StepVerifier.create(paymentFundService.recordExpense(1L, req))
                .assertNext(t -> assertEquals("VET_EXPENSE", t.getTransactionType()))
                .verifyComplete();
    }

    @Test
    void getAllFunds_returnsFlux() {
        when(fundRepository.findAll()).thenReturn(Flux.just(sampleFund));

        StepVerifier.create(paymentFundService.getAllFunds())
                .expectNext(sampleFund)
                .verifyComplete();
    }

    @Test
    void updateFund_found_updatesFields() {
        CreateFundRequest req = new CreateFundRequest(100L, 20L, 30000.0, 400.0, 5000.0, 3000.0);
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(paymentFundService.updateFund(1L, req))
                .assertNext(f -> {
                    assertEquals(400.0, f.getMonthlyAllowance());
                    assertEquals(5000.0, f.getVetReserve());
                    assertEquals(3000.0, f.getEmergencyReserve());
                    assertEquals(30000.0, f.getTotalFund());
                })
                .verifyComplete();
    }

    @Test
    void updateFund_notFound_throwsError() {
        CreateFundRequest req = new CreateFundRequest(100L, 20L, 30000.0, 400.0, 5000.0, 3000.0);
        when(fundRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.updateFund(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateFundStatus_withHistoryRepo_savesHistory() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));
        when(fundStatusHistoryRepository.save(any(FundStatusHistory.class))).thenReturn(Mono.just(new FundStatusHistory()));

        StepVerifier.create(paymentFundService.updateFundStatus(1L, "SUSPENDED"))
                .assertNext(f -> assertEquals("SUSPENDED", f.getStatus()))
                .verifyComplete();

        verify(fundStatusHistoryRepository).save(any(FundStatusHistory.class));
    }

    @Test
    void updateFundStatus_withoutHistoryRepo_returnsFund() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.save(any(PetContinuityFund.class))).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(secondaryPaymentFundService.updateFundStatus(1L, "CLOSED"))
                .assertNext(f -> assertEquals("CLOSED", f.getStatus()))
                .verifyComplete();
    }

    @Test
    void updateFundStatus_notFound_throwsError() {
        when(fundRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.updateFundStatus(99L, "ACTIVE"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteFund_found_deletes() {
        when(fundRepository.findById(1L)).thenReturn(Mono.just(sampleFund));
        when(fundRepository.delete(sampleFund)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.deleteFund(1L))
                .verifyComplete();

        verify(fundRepository).delete(sampleFund);
    }

    @Test
    void deleteFund_notFound_throwsError() {
        when(fundRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.deleteFund(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAllPayments_returnsFlux() {
        PremiumPayment p = PremiumPayment.create(100L, 45.0, "CARD", "SUCCESS");
        when(paymentRepository.findAll()).thenReturn(Flux.just(p));

        StepVerifier.create(paymentFundService.getAllPayments())
                .expectNext(p)
                .verifyComplete();
    }

    @Test
    void getPaymentById_found() {
        PremiumPayment p = PremiumPayment.create(100L, 45.0, "CARD", "SUCCESS");
        p.setId(10L);
        when(paymentRepository.findById(10L)).thenReturn(Mono.just(p));

        StepVerifier.create(paymentFundService.getPaymentById(10L))
                .assertNext(res -> assertEquals(10L, res.getId()))
                .verifyComplete();
    }

    @Test
    void getPaymentById_notFound_throwsError() {
        when(paymentRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.getPaymentById(99L))
                .expectError(IllegalArgumentException.class)
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
    void getFundById_notFound_throwsError() {
        when(fundRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.getFundById(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getFundByPolicyId_found() {
        when(fundRepository.findByPolicyId(100L)).thenReturn(Mono.just(sampleFund));

        StepVerifier.create(paymentFundService.getFundByPolicyId(100L))
                .assertNext(f -> assertEquals(1L, f.getId()))
                .verifyComplete();
    }

    @Test
    void getFundByPolicyId_notFound_throwsError() {
        when(fundRepository.findByPolicyId(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentFundService.getFundByPolicyId(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
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
    void getDisbursementsByFundId_withRepo_returnsFlux() {
        Disbursement d = new Disbursement();
        when(disbursementRepository.findByFundId(1L)).thenReturn(Flux.just(d));

        StepVerifier.create(paymentFundService.getDisbursementsByFundId(1L))
                .expectNext(d)
                .verifyComplete();
    }

    @Test
    void getDisbursementsByFundId_nullRepo_returnsEmpty() {
        StepVerifier.create(secondaryPaymentFundService.getDisbursementsByFundId(1L))
                .verifyComplete();
    }

    @Test
    void getExpensesByFundId_withRepo_returnsFlux() {
        Expense e = new Expense();
        when(expenseRepository.findByFundId(1L)).thenReturn(Flux.just(e));

        StepVerifier.create(paymentFundService.getExpensesByFundId(1L))
                .expectNext(e)
                .verifyComplete();
    }

    @Test
    void getExpensesByFundId_nullRepo_returnsEmpty() {
        StepVerifier.create(secondaryPaymentFundService.getExpensesByFundId(1L))
                .verifyComplete();
    }

    @Test
    void getFundStatusHistory_withRepo_returnsFlux() {
        FundStatusHistory hist = new FundStatusHistory();
        when(fundStatusHistoryRepository.findByFundIdOrderByChangedAtDesc(1L)).thenReturn(Flux.just(hist));

        StepVerifier.create(paymentFundService.getFundStatusHistory(1L))
                .expectNext(hist)
                .verifyComplete();
    }

    @Test
    void getFundStatusHistory_nullRepo_returnsEmpty() {
        StepVerifier.create(secondaryPaymentFundService.getFundStatusHistory(1L))
                .verifyComplete();
    }
}
