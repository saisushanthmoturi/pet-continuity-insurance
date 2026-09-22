package com.example.paymentfundservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
