package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("verification_history")
public class VerificationHistory {

    @Id
    @Column("history_id")
    private Long historyId;

    @Column("pet_id")
    private Long petId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("verification_type")
    private String verificationType;

    @Column("old_status")
    private String oldStatus;

    @Column("new_status")
    private String newStatus;

    @Column("verification_method")
    private String verificationMethod;

    @Column("verified_by")
    private String verifiedBy;

    @Column("verified_at")
    private LocalDateTime verifiedAt = LocalDateTime.now();

    @Column("remarks")
    private String remarks;

    public VerificationHistory() {
        this.verifiedAt = LocalDateTime.now();
    }

    public VerificationHistory(Long historyId, Long petId, Long caretakerId, String verificationType,
                               String oldStatus, String newStatus, String verificationMethod,
                               String verifiedBy, LocalDateTime verifiedAt, String remarks) {
        this.historyId = historyId;
        this.petId = petId;
        this.caretakerId = caretakerId;
        this.verificationType = verificationType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.verificationMethod = verificationMethod;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt != null ? verifiedAt : LocalDateTime.now();
        this.remarks = remarks;
    }

    public Long getId() {
        return historyId;
    }

    public void setId(Long id) {
        this.historyId = id;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
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

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
