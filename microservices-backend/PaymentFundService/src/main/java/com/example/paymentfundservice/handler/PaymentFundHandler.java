package com.example.paymentfundservice.handler;

import com.example.paymentfundservice.dto.CreateFundRequest;
import com.example.paymentfundservice.dto.ExpenseRequest;
import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.service.PaymentFundService;
import com.example.paymentfundservice.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () ->
                request.bodyToMono(PaymentRequest.class)
                        .flatMap(paymentFundService::processPremiumPayment)
                        .flatMap(payment -> {
                            if ("SUCCESS".equals(payment.getStatus())) {
                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(payment);
                            } else {
                                return ServerResponse.status(HttpStatus.PAYMENT_REQUIRED).contentType(MediaType.APPLICATION_JSON).bodyValue(payment);
                            }
                        })
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
        );
    }

    public Mono<ServerResponse> createFund(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CLAIMS_OFFICER", "ADMIN"}, () ->
                request.bodyToMono(CreateFundRequest.class)
                        .flatMap(paymentFundService::createFund)
                        .flatMap(fund -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
        );
    }

    public Mono<ServerResponse> disburseMonthly(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "ADMIN"}, () -> {
            Long fundId = Long.valueOf(request.pathVariable("id"));
            Long petId = Long.valueOf(request.queryParam("petId").orElse("1"));
            Long caretakerId = Long.valueOf(request.queryParam("caretakerId").orElse("1"));

            return paymentFundService.disburseMonthly(fundId, petId, caretakerId)
                    .flatMap(txn -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(txn))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> recordExpense(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "ADMIN"}, () -> {
            Long fundId = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(ExpenseRequest.class)
                    .flatMap(req -> paymentFundService.recordExpense(fundId, req))
                    .flatMap(txn -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(txn))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getFundById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "CUSTOMER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return paymentFundService.getFundById(id)
                    .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getFundByPolicyId(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "CUSTOMER", "ADMIN"}, () -> {
            Long policyId = Long.valueOf(request.pathVariable("policyId"));
            return paymentFundService.getFundByPolicyId(policyId)
                    .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getAllFunds(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(paymentFundService.getAllFunds(), Object.class)
        );
    }

    public Mono<ServerResponse> updateFund(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(CreateFundRequest.class)
                    .flatMap(req -> paymentFundService.updateFund(id, req))
                    .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> updateFundStatus(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return request.bodyToMono(Map.class)
                    .map(m -> (String) m.getOrDefault("status", "ACTIVE"))
                    .flatMap(status -> paymentFundService.updateFundStatus(id, status))
                    .flatMap(fund -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fund))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> deleteFund(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return paymentFundService.deleteFund(id)
                    .then(ServerResponse.noContent().build())
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getAllPayments(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"ADMIN"}, () ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(paymentFundService.getAllPayments(), Object.class)
        );
    }

    public Mono<ServerResponse> getPaymentById(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CUSTOMER", "ADMIN"}, () -> {
            Long id = Long.valueOf(request.pathVariable("id"));
            return paymentFundService.getPaymentById(id)
                    .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                    .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", e.getMessage())));
        });
    }

    public Mono<ServerResponse> getTransactions(ServerRequest request) {
        return SecurityUtil.checkRole(request, new String[]{"CARETAKER", "CUSTOMER", "ADMIN"}, () -> {
            Long fundId = Long.valueOf(request.pathVariable("id"));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentFundService.getTransactions(fundId), Object.class);
        });
    }
}
