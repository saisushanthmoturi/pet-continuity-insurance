package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("event_verifications")
public class EventVerification {

    @Id
    @Column("verification_id")
    private Long verificationId;

    @Column("claim_id")
    private Long claimId;

    @Column("event_type")
    private String eventType;

    @Column("verification_method")
    private String verificationMethod;

    @Column("verification_status")
    private String verificationStatus;

    @Column("verified_by")
    private String verifiedBy;

    @Column("verification_date")
    private LocalDate verificationDate = LocalDate.now();

    @Column("remarks")
    private String remarks;

    public EventVerification() {
        this.verificationDate = LocalDate.now();
    }

    public EventVerification(Long verificationId, Long claimId, String eventType, String verificationMethod,
                             String verificationStatus, String verifiedBy, LocalDate verificationDate, String remarks) {
        this.verificationId = verificationId;
        this.claimId = claimId;
        this.eventType = eventType;
        this.verificationMethod = verificationMethod;
        this.verificationStatus = verificationStatus;
        this.verifiedBy = verifiedBy;
        this.verificationDate = verificationDate != null ? verificationDate : LocalDate.now();
        this.remarks = remarks;
    }

    public Long getId() {
        return verificationId;
    }

    public void setId(Long id) {
        this.verificationId = id;
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDate getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDate verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
