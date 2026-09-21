package com.example.petservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("pet_medical_records")
public class PetMedicalRecord {

    @Id
    @Column("medical_record_id")
    private Long id;

    @Column("pet_id")
    private Long petId;

    @Column("record_type")
    private String recordType = "GENERAL";

    @Column("diagnosis")
    private String diagnosis;

    @Column("treatment")
    private String treatment;

    @Column("vet_name")
    private String vetName;

    @Column("record_date")
    private LocalDate recordDate = LocalDate.now();

    @Column("risk_level")
    private String riskLevel = "LOW";

    @Column("notes")
    private String notes;

    @Column("annual_med_cost")
    private Double estimatedAnnualMedCost = 0.0;

    @Column("status")
    private String status = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PetMedicalRecord() {
    }

    public PetMedicalRecord(Long id, Long petId, String recordType, String diagnosis, String treatment, String vetName, LocalDate recordDate, String riskLevel, String notes, String status) {
        this.id = id;
        this.petId = petId;
        this.recordType = recordType != null ? recordType : "GENERAL";
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.vetName = vetName;
        this.recordDate = recordDate != null ? recordDate : LocalDate.now();
        this.riskLevel = riskLevel != null ? riskLevel : "LOW";
        this.notes = notes;
        this.status = status != null ? status : "ACTIVE";
    }

    public static PetMedicalRecord createNew(Long petId, String conditionName, String diagnosisDate, String treatmentPlan, Double estimatedAnnualMedCost) {
        LocalDate rDate = LocalDate.now();
        try {
            if (diagnosisDate != null && !diagnosisDate.isBlank()) {
                rDate = LocalDate.parse(diagnosisDate);
            }
        } catch (Exception ignored) {
        }
        PetMedicalRecord record = new PetMedicalRecord(null, petId, "CONDITION", conditionName, treatmentPlan, "Primary Vet", rDate, "LOW", treatmentPlan, "ACTIVE");
        record.setEstimatedAnnualMedCost(estimatedAnnualMedCost != null ? estimatedAnnualMedCost : 0.0);
        return record;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicalRecordId() {
        return id;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.id = medicalRecordId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getConditionName() {
        return diagnosis;
    }

    public void setConditionName(String conditionName) {
        this.diagnosis = conditionName;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getTreatmentPlan() {
        return treatment;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatment = treatmentPlan;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getDiagnosisDate() {
        return recordDate != null ? recordDate.toString() : "";
    }

    public void setDiagnosisDate(String diagnosisDate) {
        try {
            this.recordDate = LocalDate.parse(diagnosisDate);
        } catch (Exception ignored) {
        }
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Double getEstimatedAnnualMedCost() {
        return estimatedAnnualMedCost;
    }

    public void setEstimatedAnnualMedCost(Double estimatedAnnualMedCost) {
        this.estimatedAnnualMedCost = estimatedAnnualMedCost;
    }
}
