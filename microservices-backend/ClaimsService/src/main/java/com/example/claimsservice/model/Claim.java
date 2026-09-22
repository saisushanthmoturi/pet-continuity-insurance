package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("claims")
public class Claim {

    @Id
    @Column("claim_id")
    private Long claimId;

    @Column("policy_id")
    private Long policyId;

    @Column("customer_id")
    private Long customerId;

    @Column("pet_id")
    private Long petId;

    @Column("claim_reason")
    private String claimReason;

    @Column("triggering_event")
    private String triggeringEvent = "OWNER_DEATH";

    @Column("claim_amount")
    private Double claimAmount = 25000.0;

    @Column("status")
    private String status = "PENDING"; // PENDING, VERIFIED, INVESTIGATING, APPROVED, REJECTED, MANUAL_REVIEW

    @Column("priority")
    private String priority = "NORMAL";

    @Column("claims_officer_id")
    private Long claimsOfficerId;

    @Column("fraud_status")
    private String fraudStatus = "CLEARED";

    @Column("submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column("claim_number")
    private String claimNumber;

    // Transient fields for compatibility with existing service DTOs

    @Transient
    private String claimantName;

    @Transient
    private String relationship;

    @Transient
    private String deathCertificateNo;

    @Transient
    private String dateOfDeath;

    @Transient
    private String rejectionReason;

    @Transient
    private String notes;

    public Claim() {
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.triggeringEvent = "OWNER_DEATH";
        this.claimAmount = 25000.0;
        this.status = "PENDING";
        this.priority = "NORMAL";
        this.fraudStatus = "CLEARED";
    }

    public Claim(Long claimId, Long policyId, Long customerId, Long petId, String claimReason,
                 String triggeringEvent, Double claimAmount, String status, String priority,
                 Long claimsOfficerId, String fraudStatus, LocalDateTime submittedAt, LocalDateTime updatedAt) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.customerId = customerId;
        this.petId = petId;
        this.claimReason = claimReason;
        this.triggeringEvent = triggeringEvent != null ? triggeringEvent : "OWNER_DEATH";
        this.claimAmount = claimAmount != null ? claimAmount : 25000.0;
        this.status = status != null ? status : "PENDING";
        this.priority = priority != null ? priority : "NORMAL";
        this.claimsOfficerId = claimsOfficerId;
        this.fraudStatus = fraudStatus != null ? fraudStatus : "CLEARED";
        this.submittedAt = submittedAt != null ? submittedAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static Claim create(Long policyId, String claimantName, String relationship, String deathCertificateNo, String dateOfDeath, String notes) {
        Claim claim = new Claim();
        claim.setPolicyId(policyId);
        claim.setClaimReason("Continuity care claim filed by " + (claimantName != null ? claimantName : "claimant") + " (" + relationship + "); Cert: " + (deathCertificateNo != null ? deathCertificateNo : "N/A"));
        claim.setClaimantName(claimantName);
        claim.setRelationship(relationship);
        claim.setDeathCertificateNo(deathCertificateNo);
        claim.setDateOfDeath(dateOfDeath);
        claim.setNotes(notes);
        claim.setClaimNumber("CLM-" + System.currentTimeMillis());
        claim.setStatus("PENDING");
        claim.setPriority("NORMAL");
        claim.setTriggeringEvent("OWNER_DEATH");
        claim.setSubmittedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        return claim;
    }

    public Long getId() {
        return claimId;
    }

    public void setId(Long id) {
        this.claimId = id;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public String getClaimReason() {
        return claimReason;
    }

    public void setClaimReason(String claimReason) {
        this.claimReason = claimReason;
    }

    public String getTriggeringEvent() {
        return triggeringEvent;
    }

    public void setTriggeringEvent(String triggeringEvent) {
        this.triggeringEvent = triggeringEvent;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getClaimsOfficerId() {
        return claimsOfficerId;
    }

    public void setClaimsOfficerId(Long claimsOfficerId) {
        this.claimsOfficerId = claimsOfficerId;
    }

    public String getFraudStatus() {
        return fraudStatus;
    }

    public void setFraudStatus(String fraudStatus) {
        this.fraudStatus = fraudStatus;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCreatedAt() {
        return submittedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.submittedAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Compatibility getters and setters
    public String getClaimNumber() {
        return claimNumber != null ? claimNumber : ("CLM-" + (claimId != null ? claimId : ""));
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getDeathCertificateNo() {
        return deathCertificateNo;
    }

    public void setDeathCertificateNo(String deathCertificateNo) {
        this.deathCertificateNo = deathCertificateNo;
    }

    public String getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
