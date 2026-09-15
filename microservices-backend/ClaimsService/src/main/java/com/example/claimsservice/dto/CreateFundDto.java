package com.example.claimsservice.dto;

public record CreateFundDto(
        Long policyId,
        Long petId,
        Double totalCoverage,
        Double monthlyAllowance,
        Double vetReserve,
        Double emergencyReserve
) {}
