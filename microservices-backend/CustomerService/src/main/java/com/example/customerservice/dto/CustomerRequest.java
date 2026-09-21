package com.example.customerservice.dto;

import java.time.LocalDate;

public record CustomerRequest(
                Long userId,
                String firstName,
                String lastName,
                String fullName,
                String email,
                String phone,
                String address,
                String emergencyContact,
                LocalDate dateOfBirth) {
        public CustomerRequest(Long userId, String fullName, String email, String phone, String address,
                        String emergencyContact) {
                this(userId, null, null, fullName, email, phone, address, emergencyContact, null);
        }
}
