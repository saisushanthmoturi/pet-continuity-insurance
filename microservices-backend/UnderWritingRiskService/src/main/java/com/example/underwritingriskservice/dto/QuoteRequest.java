package com.example.underwritingriskservice.dto;

public record QuoteRequest(
        Long customerId,
        Long petId,
        Double requestedCoverage
) {}
