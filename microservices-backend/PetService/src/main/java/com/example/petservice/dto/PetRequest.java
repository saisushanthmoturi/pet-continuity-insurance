package com.example.petservice.dto;

public record PetRequest(
        Long customerId,
        String name,
        String species,
        String breed,
        Integer age,
        Double weight,
        String gender,
        Double estimatedAnnualCareCost
) {}
