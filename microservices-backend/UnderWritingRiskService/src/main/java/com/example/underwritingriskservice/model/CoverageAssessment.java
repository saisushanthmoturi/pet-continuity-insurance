package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("coverage_assessments")
public class CoverageAssessment {

    @Id
    @Column("coverage_assessment_id")
    private Long coverageAssessmentId;

    @Column("quote_id")
    private Long quoteId;

    @Column("annual_care_cost")
    private Double annualCareCost;

    @Column("remaining_years")
    private Integer remainingYears;

    @Column("medical_reserve")
    private Double medicalReserve;

    @Column("inflation_adjustment")
    private Double inflationAdjustment = 1.05;

    @Column("projected_liability")
    private Double projectedLiability;

    @Column("requested_coverage")
    private Double requestedCoverage;

    @Column("coverage_gap")
    private Double coverageGap;

    @Column("recommendation")
    private String recommendation;

    @Column("status")
    private String status = "CALCULATED";

    public CoverageAssessment() {
        this.status = "CALCULATED";
        this.inflationAdjustment = 1.05;
    }

    public CoverageAssessment(Long coverageAssessmentId, Long quoteId, Double annualCareCost,
                              Integer remainingYears, Double medicalReserve, Double inflationAdjustment,
                              Double projectedLiability, Double requestedCoverage, Double coverageGap,
                              String recommendation, String status) {
        this.coverageAssessmentId = coverageAssessmentId;
        this.quoteId = quoteId;
        this.annualCareCost = annualCareCost;
        this.remainingYears = remainingYears;
        this.medicalReserve = medicalReserve;
        this.inflationAdjustment = inflationAdjustment != null ? inflationAdjustment : 1.05;
        this.projectedLiability = projectedLiability;
        this.requestedCoverage = requestedCoverage;
        this.coverageGap = coverageGap;
        this.recommendation = recommendation;
        this.status = status != null ? status : "CALCULATED";
    }

    public Long getId() {
        return coverageAssessmentId;
    }

    public void setId(Long id) {
        this.coverageAssessmentId = id;
    }

    public Long getCoverageAssessmentId() {
        return coverageAssessmentId;
    }

    public void setCoverageAssessmentId(Long coverageAssessmentId) {
        this.coverageAssessmentId = coverageAssessmentId;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Double getAnnualCareCost() {
        return annualCareCost;
    }

    public void setAnnualCareCost(Double annualCareCost) {
        this.annualCareCost = annualCareCost;
    }

    public Integer getRemainingYears() {
        return remainingYears;
    }

    public void setRemainingYears(Integer remainingYears) {
        this.remainingYears = remainingYears;
    }

    public Double getMedicalReserve() {
        return medicalReserve;
    }

    public void setMedicalReserve(Double medicalReserve) {
        this.medicalReserve = medicalReserve;
    }

    public Double getInflationAdjustment() {
        return inflationAdjustment;
    }

    public void setInflationAdjustment(Double inflationAdjustment) {
        this.inflationAdjustment = inflationAdjustment;
    }

    public Double getProjectedLiability() {
        return projectedLiability;
    }

    public void setProjectedLiability(Double projectedLiability) {
        this.projectedLiability = projectedLiability;
    }

    public Double getRequestedCoverage() {
        return requestedCoverage;
    }

    public void setRequestedCoverage(Double requestedCoverage) {
        this.requestedCoverage = requestedCoverage;
    }

    public Double getCoverageGap() {
        return coverageGap;
    }

    public void setCoverageGap(Double coverageGap) {
        this.coverageGap = coverageGap;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
