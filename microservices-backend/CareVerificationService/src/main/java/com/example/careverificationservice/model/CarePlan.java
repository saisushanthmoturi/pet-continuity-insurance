package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("care_plans")
public class CarePlan {

    @Id
    @Column("care_plan_id")
    private Long carePlanId;

    @Column("pet_id")
    private Long petId;

    @Column("feeding_instructions")
    private String feedingInstructions;

    @Column("medication_instructions")
    private String medicationInstructions;

    @Column("vet_details")
    private String vetDetails;

    @Column("routine_details")
    private String routineDetails;

    @Column("special_requirements")
    private String specialRequirements;

    @Column("status")
    private String status = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column("primary_caretaker_id")
    private Long primaryCaretakerId;

    @Column("backup_caretaker_id")
    private Long backupCaretakerId;

    public CarePlan() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public CarePlan(Long carePlanId, Long petId, String feedingInstructions, String medicationInstructions,
                    String vetDetails, String routineDetails, String specialRequirements,
                    String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.carePlanId = carePlanId;
        this.petId = petId;
        this.feedingInstructions = feedingInstructions;
        this.medicationInstructions = medicationInstructions;
        this.vetDetails = vetDetails;
        this.routineDetails = routineDetails;
        this.specialRequirements = specialRequirements;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static CarePlan create(Long petId, Long primaryCaretakerId, Long backupCaretakerId, String vetContact, String feedingInstructions, String specialNeeds) {
        CarePlan cp = new CarePlan();
        cp.setPetId(petId);
        cp.setPrimaryCaretakerId(primaryCaretakerId);
        cp.setBackupCaretakerId(backupCaretakerId);
        cp.setVetDetails(vetContact);
        cp.setFeedingInstructions(feedingInstructions);
        cp.setSpecialRequirements(specialNeeds);
        cp.setRoutineDetails("Standard daily routine; primary caretaker: " + primaryCaretakerId);
        cp.setStatus("ACTIVE");
        cp.setCreatedAt(LocalDateTime.now());
        cp.setUpdatedAt(LocalDateTime.now());
        return cp;
    }

    public Long getId() {
        return carePlanId;
    }

    public void setId(Long id) {
        this.carePlanId = id;
    }

    public Long getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(Long carePlanId) {
        this.carePlanId = carePlanId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getFeedingInstructions() {
        return feedingInstructions;
    }

    public void setFeedingInstructions(String feedingInstructions) {
        this.feedingInstructions = feedingInstructions;
    }

    public String getMedicationInstructions() {
        return medicationInstructions;
    }

    public void setMedicationInstructions(String medicationInstructions) {
        this.medicationInstructions = medicationInstructions;
    }

    public String getVetDetails() {
        return vetDetails;
    }

    public void setVetDetails(String vetDetails) {
        this.vetDetails = vetDetails;
    }

    public String getVetContact() {
        return vetDetails;
    }

    public void setVetContact(String vetContact) {
        this.vetDetails = vetContact;
    }

    public String getRoutineDetails() {
        return routineDetails;
    }

    public void setRoutineDetails(String routineDetails) {
        this.routineDetails = routineDetails;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getSpecialNeeds() {
        return specialRequirements;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialRequirements = specialNeeds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
}
