package com.example.authservice.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String fullName,
        String role,
        LocalDateTime createdAt
) {
}
