package com.example.paymentfundservice.handler;

import com.example.paymentfundservice.dto.CreateFundRequest;
import com.example.paymentfundservice.dto.ExpenseRequest;
import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.service.PaymentFundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PaymentFundHandler {

    private final PaymentFundService paymentFundService;

    public PaymentFundHandler(PaymentFundService paymentFundService) {
        this.paymentFundService = paymentFundService;
    }

    public Mono<ServerResponse> payPremium(ServerRequest request) {
        return request.bodyToMono(PaymentRequest.class)
                .flatMap(paymentFundService::processPremiumPayment)
                .flatMap(payment -> {
                    if ("SUCCESS".equals(payment.getStatus())) {
                        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(payment);
                    } else {
                        return ServerResponse.status(HttpStatus.PAYMENT_REQUIRED).contentType(MediaType.APPLICATION_JSON).bodyValue(payment);
                    }
                })
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getPaymentsByCustomerId(ServerRequest request) {
        Long customerId = Long.valueOf(request.pathVariable("customerId"));
        return paymentFundService.getPaymentsByCustomerId(customerId)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> createFund(ServerRequest request) {
        return request.bodyToMono(CreateFundRequest.class)
                .flatMap(paymentFundService::createFund)
                .flatMap(fund -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> disburseMonthly(ServerRequest request) {
        Long fundId = Long.valueOf(request.pathVariable("id"));
        Long petId = Long.valueOf(request.queryParam("petId").orElse("1"));
        Long caretakerId = Long.valueOf(request.queryParam("caretakerId").orElse("1"));

        return paymentFundService.disburseMonthly(fundId, petId, caretakerId)
                .flatMap(txn -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(txn))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> recordExpense(ServerRequest request) {
        Long fundId = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(ExpenseRequest.class)
                .flatMap(req -> paymentFundService.recordExpense(fundId, req))
                .flatMap(txn -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(txn))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getFundById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return paymentFundService.getFundById(id)
                .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getFundByPolicyId(ServerRequest request) {
        Long policyId = Long.valueOf(request.pathVariable("policyId"));
        return paymentFundService.getFundByPolicyId(policyId)
                .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getAllFunds(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentFundService.getAllFunds(), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> updateFund(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CreateFundRequest.class)
                .flatMap(req -> paymentFundService.updateFund(id, req))
                .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> updateFundStatus(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Map.class)
                .map(m -> (String) m.getOrDefault("status", "ACTIVE"))
                .flatMap(status -> paymentFundService.updateFundStatus(id, status))
                .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> deleteFund(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return paymentFundService.deleteFund(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getAllPayments(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentFundService.getAllPayments(), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getPaymentById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return paymentFundService.getPaymentById(id)
                .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> getTransactions(ServerRequest request) {
        Long fundId = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentFundService.getTransactions(fundId), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getDisbursements(ServerRequest request) {
        Long fundId = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentFundService.getDisbursementsByFundId(fundId), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getExpenses(ServerRequest request) {
        Long fundId = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentFundService.getExpensesByFundId(fundId), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }

    public Mono<ServerResponse> getFundHistory(ServerRequest request) {
        Long fundId = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentFundService.getFundStatusHistory(fundId), Object.class)
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage(), "status", 403)));
    }
}
