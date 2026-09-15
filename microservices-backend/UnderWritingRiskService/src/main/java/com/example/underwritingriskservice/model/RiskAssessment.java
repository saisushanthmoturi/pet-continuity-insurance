package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("risk_assessments")
public class RiskAssessment {

    @Id
    private Long id;

    @Column("quote_id")
    private Long quoteId;

    @Column("pet_id")
    private Long petId;

    @Column("age_factor")
    private Double ageFactor;

    @Column("health_factor")
    private Double healthFactor;

    @Column("projected_care_liability")
    private Double projectedCareLiability;

    @Column("coverage_gap")
    private Double coverageGap;

    @Column("risk_level")
    private String riskLevel;

    @Column("assessment_date")
    private LocalDateTime assessmentDate;

    public RiskAssessment() {
    }

    public RiskAssessment(Long id, Long quoteId, Long petId, Double ageFactor, Double healthFactor, Double projectedCareLiability, Double coverageGap, String riskLevel, LocalDateTime assessmentDate) {
        this.id = id;
        this.quoteId = quoteId;
        this.petId = petId;
        this.ageFactor = ageFactor;
        this.healthFactor = healthFactor;
        this.projectedCareLiability = projectedCareLiability;
        this.coverageGap = coverageGap;
        this.riskLevel = riskLevel;
        this.assessmentDate = assessmentDate;
    }

    public static RiskAssessment createNew(Long quoteId, Long petId, Double ageFactor, Double healthFactor, Double projectedCareLiability, Double coverageGap, String riskLevel) {
        return new RiskAssessment(null, quoteId, petId, ageFactor, healthFactor, projectedCareLiability, coverageGap, riskLevel, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Double getAgeFactor() {
        return ageFactor;
    }

    public void setAgeFactor(Double ageFactor) {
        this.ageFactor = ageFactor;
    }

    public Double getHealthFactor() {
        return healthFactor;
    }

    public void setHealthFactor(Double healthFactor) {
        this.healthFactor = healthFactor;
    }

    public Double getProjectedCareLiability() {
        return projectedCareLiability;
    }

    public void setProjectedCareLiability(Double projectedCareLiability) {
        this.projectedCareLiability = projectedCareLiability;
    }

    public Double getCoverageGap() {
        return coverageGap;
    }

    public void setCoverageGap(Double coverageGap) {
        this.coverageGap = coverageGap;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }
}
