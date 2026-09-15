package com.example.paymentfundservice.dto;

public record ExpenseRequest(
        String transactionType, // VET_EXPENSE, EMERGENCY
        Double amount,
        String description
) {}
