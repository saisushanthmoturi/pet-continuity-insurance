package com.example.claimsservice.dto;

public record DeathVerificationRequest(
        Boolean verified,
        String registryNotes
) {}
