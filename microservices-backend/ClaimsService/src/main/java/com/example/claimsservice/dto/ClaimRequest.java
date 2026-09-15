package com.example.claimsservice.dto;

public record ClaimRequest(
        Long policyId,
        String claimantName,
        String relationship,
        String deathCertificateNo,
        String dateOfDeath,
        String notes
) {}
