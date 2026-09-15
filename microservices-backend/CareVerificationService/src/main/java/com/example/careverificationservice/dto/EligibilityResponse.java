package com.example.careverificationservice.dto;

public record EligibilityResponse(
        boolean eligible,
        String reason,
        Long activeCaretakerId
) {}
