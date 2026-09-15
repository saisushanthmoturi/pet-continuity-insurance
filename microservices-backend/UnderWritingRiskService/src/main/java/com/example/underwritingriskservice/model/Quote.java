package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("quotes")
public class Quote {

    @Id
    private Long id;

    @Column("customer_id")
    private Long customerId;

    @Column("pet_id")
    private Long petId;

    @Column("requested_coverage")
    private Double requestedCoverage;

    @Column("monthly_premium")
    private Double monthlyPremium;

    @Column("risk_score")
    private Integer riskScore;

    @Column("decision")
    private String decision;

    @Column("status")
    private String status;

    @Column("valid_until")
    private LocalDateTime validUntil;

    @Column("created_at")
    private LocalDateTime createdAt;

    public Quote() {
    }

    public Quote(Long id, Long customerId, Long petId, Double requestedCoverage, Double monthlyPremium, Integer riskScore, String decision, String status, LocalDateTime validUntil, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.petId = petId;
        this.requestedCoverage = requestedCoverage;
        this.monthlyPremium = monthlyPremium;
        this.riskScore = riskScore;
        this.decision = decision;
        this.status = status;
        this.validUntil = validUntil;
        this.createdAt = createdAt;
    }

    public static Quote createNew(Long customerId, Long petId, Double requestedCoverage, Double monthlyPremium, Integer riskScore, String decision) {
        LocalDateTime now = LocalDateTime.now();
        return new Quote(null, customerId, petId, requestedCoverage, monthlyPremium, riskScore, decision, "OFFERED", now.plusDays(30), now);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getMonthlyPremium() {
        return monthlyPremium;
    }

    public void setMonthlyPremium(Double monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
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

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
