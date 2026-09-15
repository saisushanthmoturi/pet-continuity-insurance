package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("care_plans")
public class CarePlan {

    @Id
    private Long id;

    @Column("pet_id")
    private Long petId;

    @Column("primary_caretaker_id")
    private Long primaryCaretakerId;

    @Column("backup_caretaker_id")
    private Long backupCaretakerId;

    @Column("vet_contact")
    private String vetContact;

    @Column("feeding_instructions")
    private String feedingInstructions;

    @Column("special_needs")
    private String specialNeeds;

    @Column("created_at")
    private LocalDateTime createdAt;

    public CarePlan() {
    }

    public CarePlan(Long id, Long petId, Long primaryCaretakerId, Long backupCaretakerId, String vetContact, String feedingInstructions, String specialNeeds, LocalDateTime createdAt) {
        this.id = id;
        this.petId = petId;
        this.primaryCaretakerId = primaryCaretakerId;
        this.backupCaretakerId = backupCaretakerId;
        this.vetContact = vetContact;
        this.feedingInstructions = feedingInstructions;
        this.specialNeeds = specialNeeds;
        this.createdAt = createdAt;
    }

    public static CarePlan create(Long petId, Long primaryCaretakerId, Long backupCaretakerId, String vetContact, String feedingInstructions, String specialNeeds) {
        return new CarePlan(null, petId, primaryCaretakerId, backupCaretakerId, vetContact, feedingInstructions, specialNeeds, LocalDateTime.now());
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

    public Long getPrimaryCaretakerId() {
        return primaryCaretakerId;
    }

    public void setPrimaryCaretakerId(Long primaryCaretakerId) {
        this.primaryCaretakerId = primaryCaretakerId;
    }

    public Long getBackupCaretakerId() {
        return backupCaretakerId;
    }

    public void setBackupCaretakerId(Long backupCaretakerId) {
        this.backupCaretakerId = backupCaretakerId;
    }

    public String getVetContact() {
        return vetContact;
    }

    public void setVetContact(String vetContact) {
        this.vetContact = vetContact;
    }

    public String getFeedingInstructions() {
        return feedingInstructions;
    }

    public void setFeedingInstructions(String feedingInstructions) {
        this.feedingInstructions = feedingInstructions;
    }

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
