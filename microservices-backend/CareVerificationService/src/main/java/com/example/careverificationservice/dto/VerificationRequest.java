package com.example.careverificationservice.dto;

public record VerificationRequest(
        Long petId,
        Long caretakerId,
        String verificationDate,
        String status, // VERIFIED, FAILED
        String notes
) {}
