package com.example.underwritingriskservice.dto;

public record MedicalRecordDto(
        Long id,
        Long petId,
        String conditionName,
        String diagnosisDate,
        String treatmentPlan,
        Double estimatedAnnualMedCost
) {}
