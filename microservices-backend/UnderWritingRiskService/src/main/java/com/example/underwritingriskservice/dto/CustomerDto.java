package com.example.underwritingriskservice.dto;

public record CustomerDto(
        Long id,
        Long userId,
        String fullName,
        String email,
        String phone,
        String address
) {
}
