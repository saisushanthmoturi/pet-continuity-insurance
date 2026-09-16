package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("investigations")
public class ClaimInvestigation {

    @Id
    @Column("investigation_id")
    private Long investigationId;

    @Column("claim_id")
    private Long claimId;

    @Column("investigator_id")
    private Long investigatorId;

    @Column("findings")
    private String findings;

    @Column("eligibility_status")
    private String eligibilityStatus = "ELIGIBLE";

    @Column("fraud_indicator_count")
    private Integer fraudIndicatorCount = 0;

    @Column("recommendation")
    private String recommendation; // APPROVED, REJECTED, MANUAL_REVIEW

    @Column("status")
    private String status = "COMPLETED";

    @Column("completed_at")
    private LocalDateTime completedAt = LocalDateTime.now();

    // Transient fields for compatibility
    @Transient
    private Boolean policyActive;

    @Transient
    private Boolean waitingPeriodPassed;

    @Transient
    private Integer fraudScore;

    public ClaimInvestigation() {
        this.completedAt = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public ClaimInvestigation(Long investigationId, Long claimId, Long investigatorId, String findings,
                              String eligibilityStatus, Integer fraudIndicatorCount, String recommendation,
                              String status, LocalDateTime completedAt) {
        this.investigationId = investigationId;
        this.claimId = claimId;
        this.investigatorId = investigatorId;
        this.findings = findings;
        this.eligibilityStatus = eligibilityStatus != null ? eligibilityStatus : "ELIGIBLE";
        this.fraudIndicatorCount = fraudIndicatorCount != null ? fraudIndicatorCount : 0;
        this.recommendation = recommendation;
        this.status = status != null ? status : "COMPLETED";
        this.completedAt = completedAt != null ? completedAt : LocalDateTime.now();
    }

    public static ClaimInvestigation create(Long claimId, Boolean policyActive, Boolean waitingPeriodPassed, Integer fraudScore, String decision, String notes) {
        ClaimInvestigation inv = new ClaimInvestigation();
        inv.setClaimId(claimId);
        inv.setPolicyActive(policyActive);
        inv.setWaitingPeriodPassed(waitingPeriodPassed);
        inv.setFraudScore(fraudScore);
        inv.setRecommendation(decision);
        inv.setFindings(notes != null ? notes : "Policy active: " + policyActive + ", Waiting period passed: " + waitingPeriodPassed + ", Fraud score: " + fraudScore);
        inv.setEligibilityStatus(Boolean.TRUE.equals(policyActive) && Boolean.TRUE.equals(waitingPeriodPassed) ? "ELIGIBLE" : "INELIGIBLE");
        inv.setFraudIndicatorCount(fraudScore != null && fraudScore > 50 ? 2 : 0);
        inv.setStatus("COMPLETED");
        inv.setCompletedAt(LocalDateTime.now());
        return inv;
    }

    public Long getId() {
        return investigationId;
    }

    public void setId(Long id) {
        this.investigationId = id;
    }

    public Long getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(Long investigationId) {
        this.investigationId = investigationId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Long getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(Long investigatorId) {
        this.investigatorId = investigatorId;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public Integer getFraudIndicatorCount() {
        return fraudIndicatorCount;
    }

    public void setFraudIndicatorCount(Integer fraudIndicatorCount) {
        this.fraudIndicatorCount = fraudIndicatorCount;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getDecision() {
        return recommendation;
    }

    public void setDecision(String decision) {
        this.recommendation = decision;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getInvestigatedAt() {
        return completedAt;
    }

    public void setInvestigatedAt(LocalDateTime investigatedAt) {
        this.completedAt = investigatedAt;
    }

    public String getNotes() {
        return findings;
    }

    public void setNotes(String notes) {
        this.findings = notes;
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
}
