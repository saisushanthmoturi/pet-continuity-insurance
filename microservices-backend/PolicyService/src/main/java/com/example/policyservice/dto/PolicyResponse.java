package com.example.policyservice.dto;

import java.time.LocalDateTime;

public record PolicyResponse(
        Long id,
        String policyNumber,
        Long quoteId,
        Long customerId,
        Long petId,
        Double coverageAmount,
        Double monthlyPremium,
        String status,
        String startDate,
        String endDate,
        LocalDateTime createdAt
) {}
