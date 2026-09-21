package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("caretaker_verification")
public class CaretakerVerification {

    @Id
    @Column("verification_id")
    private Long verificationId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("verification_type")
    private String verificationType = "IDENTITY";

    @Column("verification_method")
    private String verificationMethod = "DOCUMENT";

    @Column("verification_status")
    private String verificationStatus = "VERIFIED";

    @Column("evidence_reference")
    private String evidenceReference;

    @Column("verification_by")
    private String verificationBy;

    @Column("verified_at")
    private LocalDateTime verifiedAt = LocalDateTime.now();

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("failure_reason")
    private String failureReason;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public CaretakerVerification() {
        this.createdAt = LocalDateTime.now();
        this.verifiedAt = LocalDateTime.now();
    }

    public CaretakerVerification(Long verificationId, Long caretakerId, String verificationType,
                                 String verificationMethod, String verificationStatus,
                                 String evidenceReference, String verificationBy,
                                 LocalDateTime verifiedAt, LocalDateTime expiresAt,
                                 String failureReason, LocalDateTime createdAt) {
        this.verificationId = verificationId;
        this.caretakerId = caretakerId;
        this.verificationType = verificationType != null ? verificationType : "IDENTITY";
        this.verificationMethod = verificationMethod != null ? verificationMethod : "DOCUMENT";
        this.verificationStatus = verificationStatus != null ? verificationStatus : "VERIFIED";
        this.evidenceReference = evidenceReference;
        this.verificationBy = verificationBy;
        this.verifiedAt = verifiedAt != null ? verifiedAt : LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.failureReason = failureReason;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public Long getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }

    public String getVerificationBy() {
        return verificationBy;
    }

    public void setVerificationBy(String verificationBy) {
        this.verificationBy = verificationBy;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
