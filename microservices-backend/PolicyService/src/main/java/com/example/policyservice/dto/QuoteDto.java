package com.example.policyservice.dto;

import java.time.LocalDateTime;

public record QuoteDto(
        Long id,
        Long customerId,
        Long petId,
        Double requestedCoverage,
        Double monthlyPremium,
        Integer riskScore,
        String decision,
        LocalDateTime validUntil
) {}
