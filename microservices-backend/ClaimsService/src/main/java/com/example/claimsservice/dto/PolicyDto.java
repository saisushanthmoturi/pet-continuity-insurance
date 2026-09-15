package com.example.claimsservice.dto;

public record PolicyDto(
        Long id,
        String policyNumber,
        Long quoteId,
        Long customerId,
        Long petId,
        Double coverageAmount,
        Double monthlyPremium,
        String status
) {}
