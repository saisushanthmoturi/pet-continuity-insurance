package com.example.policyservice.dto;

public record QuoteDto(
        Long id,
        Long customerId,
        Long petId,
        Double requestedCoverage,
        Double monthlyPremium,
        Integer riskScore,
        String decision
) {}
