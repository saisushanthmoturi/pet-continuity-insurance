package com.example.paymentfundservice.handler;

import com.example.paymentfundservice.dto.CreateFundRequest;
import com.example.paymentfundservice.dto.ExpenseRequest;
import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.model.*;
import com.example.paymentfundservice.service.PaymentFundService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentFundHandlerTest {

    @Mock
    private PaymentFundService paymentFundService;

    private PaymentFundHandler handler;
    private PremiumPayment samplePayment;
    private PetContinuityFund sampleFund;

    @BeforeEach
    void setUp() {
        handler = new PaymentFundHandler(paymentFundService);
        samplePayment = PremiumPayment.create(10L, 60.0, "CARD", "SUCCESS");
        samplePayment.setId(1L);
        samplePayment.setCustomerId(5L);

        sampleFund = PetContinuityFund.create(10L, 20L, 25000.0, 300.0, 4000.0, 2000.0);
        sampleFund.setId(1L);
    }

    @Test
    void payPremium_success() {
        PaymentRequest req = new PaymentRequest(10L, 5L, 60.0, "CARD", false);
        when(paymentFundService.processPremiumPayment(any(PaymentRequest.class))).thenReturn(Mono.just(samplePayment));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        Mono<ServerResponse> response = handler.payPremium(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void payPremium_failedStatus_returnsPaymentRequired() {
        PremiumPayment failedPayment = PremiumPayment.create(10L, 60.0, "CARD", "FAILED");
        when(paymentFundService.processPremiumPayment(any(PaymentRequest.class))).thenReturn(Mono.just(failedPayment));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new PaymentRequest(10L, 60.0, "CARD", true)));
        Mono<ServerResponse> response = handler.payPremium(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.PAYMENT_REQUIRED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void payPremium_error_returnsBadRequest() {
        when(paymentFundService.processPremiumPayment(any(PaymentRequest.class))).thenReturn(Mono.error(new RuntimeException("Error")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new PaymentRequest(10L, 60.0, "CARD", false)));
        Mono<ServerResponse> response = handler.payPremium(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getPaymentsByCustomerId_success() {
        when(paymentFundService.getPaymentsByCustomerId(5L)).thenReturn(Flux.just(samplePayment));

        MockServerRequest request = MockServerRequest.builder().pathVariable("customerId", "5").build();
        Mono<ServerResponse> response = handler.getPaymentsByCustomerId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createFund_success() {
        CreateFundRequest req = new CreateFundRequest(10L, 20L, 25000.0, 300.0, 4000.0, 2000.0);
        when(paymentFundService.createFund(any(CreateFundRequest.class))).thenReturn(Mono.just(sampleFund));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));
        Mono<ServerResponse> response = handler.createFund(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.CREATED, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void createFund_error_returnsBadRequest() {
        when(paymentFundService.createFund(any(CreateFundRequest.class))).thenReturn(Mono.error(new RuntimeException("Error")));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new CreateFundRequest(10L, 20L, 25000.0, 300.0, 4000.0, 2000.0)));
        Mono<ServerResponse> response = handler.createFund(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void disburseMonthly_withQueryParams_success() {
        FundTransaction txn = FundTransaction.create(1L, "DISBURSEMENT", 300.0, 24700.0, "desc", "SUCCESS");
        when(paymentFundService.disburseMonthly(1L, 20L, 30L)).thenReturn(Mono.just(txn));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .queryParam("petId", "20")
                .queryParam("caretakerId", "30")
                .build();
        Mono<ServerResponse> response = handler.disburseMonthly(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void disburseMonthly_withoutQueryParams_usesDefaults() {
        FundTransaction txn = FundTransaction.create(1L, "DISBURSEMENT", 300.0, 24700.0, "desc", "SUCCESS");
        when(paymentFundService.disburseMonthly(1L, 1L, 1L)).thenReturn(Mono.just(txn));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .build();
        Mono<ServerResponse> response = handler.disburseMonthly(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void disburseMonthly_error_returnsBadRequest() {
        when(paymentFundService.disburseMonthly(1L, 1L, 1L)).thenReturn(Mono.error(new RuntimeException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.disburseMonthly(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void recordExpense_success() {
        FundTransaction txn = FundTransaction.create(1L, "VET_EXPENSE", 200.0, 24800.0, "desc", "SUCCESS");
        ExpenseRequest req = new ExpenseRequest("VET_EXPENSE", 200.0, "desc");
        when(paymentFundService.recordExpense(eq(1L), any(ExpenseRequest.class))).thenReturn(Mono.just(txn));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(req));
        Mono<ServerResponse> response = handler.recordExpense(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void recordExpense_error_returnsBadRequest() {
        when(paymentFundService.recordExpense(eq(1L), any(ExpenseRequest.class))).thenReturn(Mono.error(new RuntimeException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(new ExpenseRequest("VET", 200.0, "desc")));
        Mono<ServerResponse> response = handler.recordExpense(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getFundById_found() {
        when(paymentFundService.getFundById(1L)).thenReturn(Mono.just(sampleFund));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getFundById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getFundById_notFound() {
        when(paymentFundService.getFundById(1L)).thenReturn(Mono.error(new RuntimeException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getFundById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getFundByPolicyId_found() {
        when(paymentFundService.getFundByPolicyId(10L)).thenReturn(Mono.just(sampleFund));

        MockServerRequest request = MockServerRequest.builder().pathVariable("policyId", "10").build();
        Mono<ServerResponse> response = handler.getFundByPolicyId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getFundByPolicyId_notFound() {
        when(paymentFundService.getFundByPolicyId(10L)).thenReturn(Mono.error(new RuntimeException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("policyId", "10").build();
        Mono<ServerResponse> response = handler.getFundByPolicyId(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllFunds_success() {
        when(paymentFundService.getAllFunds()).thenReturn(Flux.just(sampleFund));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllFunds(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateFund_success() {
        CreateFundRequest req = new CreateFundRequest(10L, 20L, 25000.0, 300.0, 4000.0, 2000.0);
        when(paymentFundService.updateFund(eq(1L), any(CreateFundRequest.class))).thenReturn(Mono.just(sampleFund));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(req));
        Mono<ServerResponse> response = handler.updateFund(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateFund_error_returnsBadRequest() {
        when(paymentFundService.updateFund(eq(1L), any(CreateFundRequest.class))).thenReturn(Mono.error(new RuntimeException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(new CreateFundRequest(10L, 20L, 25000.0, 300.0, 4000.0, 2000.0)));
        Mono<ServerResponse> response = handler.updateFund(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateFundStatus_success() {
        when(paymentFundService.updateFundStatus(1L, "ACTIVE")).thenReturn(Mono.just(sampleFund));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(Map.of("status", "ACTIVE")));
        Mono<ServerResponse> response = handler.updateFundStatus(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateFundStatus_error_returnsBadRequest() {
        when(paymentFundService.updateFundStatus(1L, "ACTIVE")).thenReturn(Mono.error(new RuntimeException("Error")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").body(Mono.just(Map.of("status", "ACTIVE")));
        Mono<ServerResponse> response = handler.updateFundStatus(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteFund_success() {
        when(paymentFundService.deleteFund(1L)).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteFund(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NO_CONTENT, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteFund_notFound() {
        when(paymentFundService.deleteFund(1L)).thenReturn(Mono.error(new RuntimeException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.deleteFund(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getAllPayments_success() {
        when(paymentFundService.getAllPayments()).thenReturn(Flux.just(samplePayment));

        MockServerRequest request = MockServerRequest.builder().build();
        Mono<ServerResponse> response = handler.getAllPayments(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getPaymentById_found() {
        when(paymentFundService.getPaymentById(1L)).thenReturn(Mono.just(samplePayment));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getPaymentById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getPaymentById_notFound() {
        when(paymentFundService.getPaymentById(1L)).thenReturn(Mono.error(new RuntimeException("Not found")));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getPaymentById(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.NOT_FOUND, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getTransactions_success() {
        FundTransaction txn = FundTransaction.create(1L, "DEPOSIT", 1000.0, 1000.0, "desc", "SUCCESS");
        when(paymentFundService.getTransactions(1L)).thenReturn(Flux.just(txn));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getTransactions(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getDisbursements_success() {
        Disbursement d = new Disbursement();
        when(paymentFundService.getDisbursementsByFundId(1L)).thenReturn(Flux.just(d));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getDisbursements(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getExpenses_success() {
        Expense e = new Expense();
        when(paymentFundService.getExpensesByFundId(1L)).thenReturn(Flux.just(e));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getExpenses(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void getFundHistory_success() {
        FundStatusHistory fsh = new FundStatusHistory();
        when(paymentFundService.getFundStatusHistory(1L)).thenReturn(Flux.just(fsh));

        MockServerRequest request = MockServerRequest.builder().pathVariable("id", "1").build();
        Mono<ServerResponse> response = handler.getFundHistory(request);

        StepVerifier.create(response)
                .assertNext(resp -> assertEquals(HttpStatus.OK, resp.statusCode()))
                .verifyComplete();
    }

    @Test
    void testAccessDeniedLambdasDirectly() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        for (Method m : PaymentFundHandler.class.getDeclaredMethods()) {
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
