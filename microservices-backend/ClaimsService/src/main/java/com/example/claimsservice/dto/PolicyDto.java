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
) {
    public PolicyDto(Long id, Long petId, Long customerId, String status, Double coverageAmount, Double monthlyPremium) {
        this(id, id != null ? "POL-" + id : "POL-001", 1L, customerId, petId, coverageAmount, monthlyPremium, status);
    }
}
