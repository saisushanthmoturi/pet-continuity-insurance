package com.example.paymentfundservice.dto;

public record PaymentRequest(
        Long policyId,
        Double amount,
        String paymentMethod,
        Boolean simulateFailure
) {}
