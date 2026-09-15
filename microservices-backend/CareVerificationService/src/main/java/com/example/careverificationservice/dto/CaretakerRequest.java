package com.example.careverificationservice.dto;

public record CaretakerRequest(
        Long customerId,
        Long petId,
        String fullName,
        String phone,
        String email,
        String caretakerType, // PRIMARY, BACKUP
        String address
) {}
