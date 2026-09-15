package com.example.policyservice.dto;

public record StatusUpdateRequest(
        String status,
        String reason
) {}
