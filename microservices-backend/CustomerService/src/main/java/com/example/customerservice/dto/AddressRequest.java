package com.example.customerservice.dto;

public record AddressRequest(
        String addressType,
        String line1,
        String line2,
        String city,
        String state,
        String postalCode,
        String country
) {}
