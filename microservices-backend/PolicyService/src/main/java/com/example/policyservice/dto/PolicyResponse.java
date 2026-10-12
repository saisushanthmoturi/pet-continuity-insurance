package com.example.policyservice.dto;

import java.time.LocalDateTime;

public record PolicyResponse(
        Long id,
        String policyNumber,
        Long quoteId,
        Long customerId,
        Long petId,
        Double coverageAmount,
        Double monthlyPremium,
        String status,
        String startDate,
        String endDate,
        LocalDateTime createdAt
) {
    public Long getPolicyId() {
        return id;
    }

    public Double getPremiumAmount() {
        return monthlyPremium != null ? Math.round(monthlyPremium * 12.0 * 100.0) / 100.0 : null;
    }
}
