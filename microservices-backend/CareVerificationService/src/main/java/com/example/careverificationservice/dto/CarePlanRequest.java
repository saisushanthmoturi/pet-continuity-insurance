package com.example.careverificationservice.dto;

public record CarePlanRequest(
        Long petId,
        Long primaryCaretakerId,
        Long backupCaretakerId,
        String vetContact,
        String feedingInstructions,
        String specialNeeds
) {}
