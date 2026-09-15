package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("claims")
public class Claim {

    @Id
    private Long id;

    @Column("claim_number")
    private String claimNumber;

    @Column("policy_id")
    private Long policyId;

    @Column("claimant_name")
    private String claimantName;

    @Column("relationship")
    private String relationship;

    @Column("death_certificate_no")
    private String deathCertificateNo;

    @Column("date_of_death")
    private String dateOfDeath;

    @Column("status")
    private String status; // PENDING, VERIFIED, INVESTIGATING, APPROVED, REJECTED, MANUAL_REVIEW

    @Column("rejection_reason")
    private String rejectionReason;

    @Column("notes")
    private String notes;

    @Column("created_at")
    private LocalDateTime createdAt;

    public Claim() {
    }

    public Claim(Long id, String claimNumber, Long policyId, String claimantName, String relationship, String deathCertificateNo, String dateOfDeath, String status, String rejectionReason, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.policyId = policyId;
        this.claimantName = claimantName;
        this.relationship = relationship;
        this.deathCertificateNo = deathCertificateNo;
        this.dateOfDeath = dateOfDeath;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static Claim create(Long policyId, String claimantName, String relationship, String deathCertificateNo, String dateOfDeath, String notes) {
        String num = "CLM-" + System.currentTimeMillis();
        return new Claim(null, num, policyId, claimantName, relationship, deathCertificateNo, dateOfDeath, "PENDING", null, notes, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
