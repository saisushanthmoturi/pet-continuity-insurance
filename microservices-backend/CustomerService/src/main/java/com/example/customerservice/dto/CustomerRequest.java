package com.example.customerservice.dto;

public record CustomerRequest(
        Long userId,
        String fullName,
        String email,
        String phone,
        String address,
        String emergencyContact
) {}
