package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("claim_investigations")
public class ClaimInvestigation {

    @Id
    private Long id;

    @Column("claim_id")
    private Long claimId;

    @Column("policy_active")
    private Boolean policyActive;

    @Column("waiting_period_passed")
    private Boolean waitingPeriodPassed;

    @Column("fraud_score")
    private Integer fraudScore;

    @Column("decision")
    private String decision; // APPROVED, REJECTED, MANUAL_REVIEW

    @Column("notes")
    private String notes;

    @Column("investigated_at")
    private LocalDateTime investigatedAt;

    public ClaimInvestigation() {
    }

    public ClaimInvestigation(Long id, Long claimId, Boolean policyActive, Boolean waitingPeriodPassed, Integer fraudScore, String decision, String notes, LocalDateTime investigatedAt) {
        this.id = id;
        this.claimId = claimId;
        this.policyActive = policyActive;
        this.waitingPeriodPassed = waitingPeriodPassed;
        this.fraudScore = fraudScore;
        this.decision = decision;
        this.notes = notes;
        this.investigatedAt = investigatedAt;
    }

    public static ClaimInvestigation create(Long claimId, Boolean policyActive, Boolean waitingPeriodPassed, Integer fraudScore, String decision, String notes) {
        return new ClaimInvestigation(null, claimId, policyActive, waitingPeriodPassed, fraudScore, decision, notes, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Boolean getPolicyActive() {
        return policyActive;
    }

    public void setPolicyActive(Boolean policyActive) {
        this.policyActive = policyActive;
    }

    public Boolean getWaitingPeriodPassed() {
        return waitingPeriodPassed;
    }

    public void setWaitingPeriodPassed(Boolean waitingPeriodPassed) {
        this.waitingPeriodPassed = waitingPeriodPassed;
    }

    public Integer getFraudScore() {
        return fraudScore;
    }

    public void setFraudScore(Integer fraudScore) {
        this.fraudScore = fraudScore;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getInvestigatedAt() {
        return investigatedAt;
    }

    public void setInvestigatedAt(LocalDateTime investigatedAt) {
        this.investigatedAt = investigatedAt;
    }
}
