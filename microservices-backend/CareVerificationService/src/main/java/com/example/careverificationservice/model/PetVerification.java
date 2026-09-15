package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("pet_verifications")
public class PetVerification {

    @Id
    private Long id;

    @Column("pet_id")
    private Long petId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("verification_date")
    private String verificationDate;

    @Column("status")
    private String status; // VERIFIED, FAILED

    @Column("notes")
    private String notes;

    @Column("created_at")
    private LocalDateTime createdAt;

    public PetVerification() {
    }

    public PetVerification(Long id, Long petId, Long caretakerId, String verificationDate, String status, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.petId = petId;
        this.caretakerId = caretakerId;
        this.verificationDate = verificationDate;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static PetVerification create(Long petId, Long caretakerId, String verificationDate, String status, String notes) {
        String date = (verificationDate != null && !verificationDate.isBlank()) ? verificationDate : LocalDateTime.now().toLocalDate().toString();
        String st = (status != null && !status.isBlank()) ? status.toUpperCase() : "VERIFIED";
        return new PetVerification(null, petId, caretakerId, date, st, notes, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
