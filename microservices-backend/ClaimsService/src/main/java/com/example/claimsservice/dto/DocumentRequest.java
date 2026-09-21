package com.example.claimsservice.dto;

public record DocumentRequest(
        String documentType,
        String fileName,
        String fileReference,
        String verificationStatus
) {}
