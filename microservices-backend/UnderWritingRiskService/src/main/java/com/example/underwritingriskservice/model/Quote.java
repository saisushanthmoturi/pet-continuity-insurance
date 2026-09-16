package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("quotes")
public class Quote {

    @Id
    @Column("quote_id")
    private Long id;

    @Column("customer_id")
    private Long customerId;

    @Column("pet_id")
    private Long petId;

    @Column("requested_coverage")
    private Double requestedCoverage;

    @Column("coverage_period")
    private String coveragePeriod = "ANNUAL";

    @Column("premium_amount")
    private Double premiumAmount;

    @Column("risk_score")
    private Integer riskScore;

    @Column("risk_class")
    private String riskClass = "MODERATE";

    @Column("decision")
    private String decision;

    @Column("status")
    private String status = "OFFERED";

    @Column("underwriter_id")
    private Long underwriterId;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column("valid_until")
    private LocalDateTime validUntil;

    public Quote() {
    }

    public Quote(Long id, Long customerId, Long petId, Double requestedCoverage, String coveragePeriod, Double premiumAmount, Integer riskScore, String riskClass, String decision, String status, Long underwriterId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime validUntil) {
        this.id = id;
        this.customerId = customerId;
        this.petId = petId;
        this.requestedCoverage = requestedCoverage;
        this.coveragePeriod = coveragePeriod != null ? coveragePeriod : "ANNUAL";
        this.premiumAmount = premiumAmount;
        this.riskScore = riskScore;
        this.riskClass = riskClass != null ? riskClass : "MODERATE";
        this.decision = decision;
        this.status = status != null ? status : "OFFERED";
        this.underwriterId = underwriterId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.validUntil = validUntil != null ? validUntil : LocalDateTime.now().plusDays(30);
    }

    public static Quote createNew(Long customerId, Long petId, Double requestedCoverage, Double monthlyPremium, Integer riskScore, String decision) {
        LocalDateTime now = LocalDateTime.now();
        String rClass = riskScore <= 40 ? "LOW" : (riskScore <= 70 ? "MODERATE" : (riskScore <= 85 ? "HIGH" : "EXTREME"));
        return new Quote(null, customerId, petId, requestedCoverage, "ANNUAL", monthlyPremium, riskScore, rClass, decision, "OFFERED", 2L, now, now, now.plusDays(30));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuoteId() {
        return id;
    }

    public void setQuoteId(Long quoteId) {
        this.id = quoteId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Double getRequestedCoverage() {
        return requestedCoverage;
    }

    public void setRequestedCoverage(Double requestedCoverage) {
        this.requestedCoverage = requestedCoverage;
    }

    public String getCoveragePeriod() {
        return coveragePeriod;
    }

    public void setCoveragePeriod(String coveragePeriod) {
        this.coveragePeriod = coveragePeriod;
    }

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public Double getMonthlyPremium() {
        return premiumAmount;
    }

    public void setMonthlyPremium(Double monthlyPremium) {
        this.premiumAmount = monthlyPremium;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskClass() {
        return riskClass;
    }

    public void setRiskClass(String riskClass) {
        this.riskClass = riskClass;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUnderwriterId() {
        return underwriterId;
    }

    public void setUnderwriterId(Long underwriterId) {
        this.underwriterId = underwriterId;
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

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
}
