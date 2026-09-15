package com.example.petservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("pet_medical_records")
public class PetMedicalRecord {

    @Id
    private Long id;

    @Column("pet_id")
    private Long petId;

    @Column("condition_name")
    private String conditionName;

    @Column("diagnosis_date")
    private String diagnosisDate;

    @Column("treatment_plan")
    private String treatmentPlan;

    @Column("estimated_annual_med_cost")
    private Double estimatedAnnualMedCost;

    @Column("created_at")
    private LocalDateTime createdAt;

    public PetMedicalRecord() {
    }

    public PetMedicalRecord(Long id, Long petId, String conditionName, String diagnosisDate, String treatmentPlan, Double estimatedAnnualMedCost, LocalDateTime createdAt) {
        this.id = id;
        this.petId = petId;
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
        this.treatmentPlan = treatmentPlan;
        this.estimatedAnnualMedCost = estimatedAnnualMedCost;
        this.createdAt = createdAt;
    }

    public static PetMedicalRecord createNew(Long petId, String conditionName, String diagnosisDate, String treatmentPlan, Double estimatedAnnualMedCost) {
        return new PetMedicalRecord(null, petId, conditionName, diagnosisDate, treatmentPlan, estimatedAnnualMedCost != null ? estimatedAnnualMedCost : 0.0, LocalDateTime.now());
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

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(String diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public Double getEstimatedAnnualMedCost() {
        return estimatedAnnualMedCost;
    }

    public void setEstimatedAnnualMedCost(Double estimatedAnnualMedCost) {
        this.estimatedAnnualMedCost = estimatedAnnualMedCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
