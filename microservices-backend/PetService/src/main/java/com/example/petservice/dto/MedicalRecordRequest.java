package com.example.petservice.dto;

public record MedicalRecordRequest(
        String conditionName,
        String diagnosisDate,
        String treatmentPlan,
        Double estimatedAnnualMedCost
) {}
