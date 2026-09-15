package com.example.claimsservice.dto;

import java.time.LocalDateTime;

public record ClaimResponse(
        Long id,
        String claimNumber,
        Long policyId,
        String claimantName,
        String relationship,
        String deathCertificateNo,
        String dateOfDeath,
        String status,
        String rejectionReason,
        String notes,
        LocalDateTime createdAt,
        String investigationDecision,
        Integer fraudScore
) {}
