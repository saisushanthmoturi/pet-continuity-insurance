package com.example.underwritingriskservice.dto;

public record PetDto(
        Long id,
        Long customerId,
        String name,
        String species,
        String breed,
        Integer age,
        Double weight,
        String gender,
        Double estimatedAnnualCareCost
) {}
