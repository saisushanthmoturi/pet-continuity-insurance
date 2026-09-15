package com.example.paymentfundservice.dto;

public record CreateFundRequest(
        Long policyId,
        Long petId,
        Double totalCoverage,
        Double monthlyAllowance,
        Double vetReserve,
        Double emergencyReserve
) {}
