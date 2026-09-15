package com.example.authservice.dto;

public record UserUpdateRequest(
        String fullName,
        String role
) {
}
