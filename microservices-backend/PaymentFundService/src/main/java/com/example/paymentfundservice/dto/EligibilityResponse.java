package com.example.paymentfundservice.dto;

public record EligibilityResponse(
        boolean eligible,
        String reason,
        Long activeCaretakerId
) {}
