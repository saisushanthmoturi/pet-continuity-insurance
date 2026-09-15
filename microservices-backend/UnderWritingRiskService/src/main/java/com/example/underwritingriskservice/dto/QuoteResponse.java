package com.example.underwritingriskservice.dto;

import java.time.LocalDateTime;

public record QuoteResponse(
        Long id,
        Long customerId,
        Long petId,
        Double requestedCoverage,
        Double monthlyPremium,
        Integer riskScore,
        String decision,
        String status,
        Double projectedCareLiability,
        Double coverageGap,
        String riskLevel,
        LocalDateTime validUntil
) {}
