package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("risk_assessments")
public class RiskAssessment {

    @Id
    @Column("risk_assessment_id")
    private Long riskAssessmentId;

    @Column("quote_id")
    private Long quoteId;

    @Column("age_factor")
    private Double ageFactor;

    @Column("breed_factor")
    private Double breedFactor = 0.0;

    @Column("medical_factor")
    private Double medicalFactor;

    @Column("care_cost_factor")
    private Double careCostFactor = 0.0;

    @Column("continuity_factor")
    private Double continuityFactor = 0.0;

    @Column("total_score")
    private Double totalScore;

    @Column("risk_class")
    private String riskClass;

    @Column("explanation")
    private String explanation;

    @Column("assessed_at")
    private LocalDateTime assessedAt = LocalDateTime.now();

    @Column("status")
    private String status = "COMPLETED";

    public RiskAssessment() {
        this.assessedAt = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public RiskAssessment(Long riskAssessmentId, Long quoteId, Double ageFactor, Double breedFactor,
                          Double medicalFactor, Double careCostFactor, Double continuityFactor,
                          Double totalScore, String riskClass, String explanation,
                          LocalDateTime assessedAt, String status) {
        this.riskAssessmentId = riskAssessmentId;
        this.quoteId = quoteId;
        this.ageFactor = ageFactor;
        this.breedFactor = breedFactor != null ? breedFactor : 0.0;
        this.medicalFactor = medicalFactor;
        this.careCostFactor = careCostFactor != null ? careCostFactor : 0.0;
        this.continuityFactor = continuityFactor != null ? continuityFactor : 0.0;
        this.totalScore = totalScore;
        this.riskClass = riskClass;
        this.explanation = explanation;
        this.assessedAt = assessedAt != null ? assessedAt : LocalDateTime.now();
        this.status = status != null ? status : "COMPLETED";
    }

    // Factory method for existing service calls
    public static RiskAssessment createNew(Long quoteId, Long petId, Double ageFactor, Double healthFactor,
                                           Double projectedCareLiability, Double coverageGap, String riskLevel) {
        RiskAssessment ra = new RiskAssessment();
        ra.setQuoteId(quoteId);
        ra.setAgeFactor(ageFactor);
        ra.setBreedFactor(0.0);
        ra.setMedicalFactor(healthFactor);
        ra.setCareCostFactor(projectedCareLiability != null ? projectedCareLiability : 0.0);
        ra.setContinuityFactor(coverageGap != null ? coverageGap : 0.0);
        ra.setTotalScore(healthFactor != null ? healthFactor * 10 : 25.0);
        ra.setRiskClass(riskLevel);
        ra.setExplanation("Automated assessment. Liability: " + projectedCareLiability + ", Gap: " + coverageGap);
        ra.setAssessedAt(LocalDateTime.now());
        ra.setStatus("COMPLETED");
        return ra;
    }

    // Backward compatible getters/setters
    public Long getId() {
        return riskAssessmentId;
    }

    public void setId(Long id) {
        this.riskAssessmentId = id;
    }

    public Long getRiskAssessmentId() {
        return riskAssessmentId;
    }

    public void setRiskAssessmentId(Long riskAssessmentId) {
        this.riskAssessmentId = riskAssessmentId;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Double getAgeFactor() {
        return ageFactor;
    }

    public void setAgeFactor(Double ageFactor) {
        this.ageFactor = ageFactor;
    }

    public Double getBreedFactor() {
        return breedFactor;
    }

    public void setBreedFactor(Double breedFactor) {
        this.breedFactor = breedFactor;
    }

    public Double getMedicalFactor() {
        return medicalFactor;
    }

    public void setMedicalFactor(Double medicalFactor) {
        this.medicalFactor = medicalFactor;
    }

    public Double getHealthFactor() {
        return medicalFactor;
    }

    public void setHealthFactor(Double healthFactor) {
        this.medicalFactor = healthFactor;
    }

    public Double getCareCostFactor() {
        return careCostFactor;
    }

    public void setCareCostFactor(Double careCostFactor) {
        this.careCostFactor = careCostFactor;
    }

    public Double getContinuityFactor() {
        return continuityFactor;
    }

    public void setContinuityFactor(Double continuityFactor) {
        this.continuityFactor = continuityFactor;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public String getRiskClass() {
        return riskClass;
    }

    public void setRiskClass(String riskClass) {
        this.riskClass = riskClass;
    }

    public String getRiskLevel() {
        return riskClass;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskClass = riskLevel;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }

    public LocalDateTime getAssessmentDate() {
        return assessedAt;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessedAt = assessmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getProjectedCareLiability() {
        return careCostFactor;
    }

    public Double getCoverageGap() {
        return continuityFactor;
    }
}
