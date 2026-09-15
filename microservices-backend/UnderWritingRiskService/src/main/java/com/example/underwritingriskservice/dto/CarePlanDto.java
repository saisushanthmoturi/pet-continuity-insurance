package com.example.underwritingriskservice.dto;

public record CarePlanDto(
        Long id,
        Long petId,
        Long primaryCaretakerId,
        Long backupCaretakerId,
        String vetContact,
        String feedingInstructions,
        String specialNeeds
) {
}
