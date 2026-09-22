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
    public static CarePlanDto empty() {
        return new CarePlanDto(null, null, null, null, null, null, null);
    }
}
