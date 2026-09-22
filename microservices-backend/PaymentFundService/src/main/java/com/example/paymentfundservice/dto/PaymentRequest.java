package com.example.paymentfundservice.dto;

public record PaymentRequest(
        Long policyId,
        Long customerId,
        Double amount,
        String paymentMethod,
        Boolean simulateFailure
) {
    public PaymentRequest(Long policyId, Double amount, String paymentMethod, Boolean simulateFailure) {
        this(policyId, null, amount, paymentMethod, simulateFailure);
    }
}
