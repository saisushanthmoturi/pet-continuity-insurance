package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("care_transfers")
public class CareTransfer {

    @Id
    @Column("transfer_id")
    private Long transferId;

    @Column("pet_id")
    private Long petId;

    @Column("from_assignment_id")
    private Long fromAssignmentId;

    @Column("to_assignment_id")
    private Long toAssignmentId;

    @Column("reason")
    private String reason;

    @Column("effective_at")
    private LocalDateTime effectiveAt = LocalDateTime.now();

    @Column("initiated_by")
    private String initiatedBy;

    @Column("status")
    private String status = "COMPLETED";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public CareTransfer() {
        this.createdAt = LocalDateTime.now();
        this.effectiveAt = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public CareTransfer(Long transferId, Long petId, Long fromAssignmentId, Long toAssignmentId,
                        String reason, LocalDateTime effectiveAt, String initiatedBy,
                        String status, LocalDateTime createdAt) {
        this.transferId = transferId;
        this.petId = petId;
        this.fromAssignmentId = fromAssignmentId;
        this.toAssignmentId = toAssignmentId;
        this.reason = reason;
        this.effectiveAt = effectiveAt != null ? effectiveAt : LocalDateTime.now();
        this.initiatedBy = initiatedBy;
        this.status = status != null ? status : "COMPLETED";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getFromAssignmentId() {
        return fromAssignmentId;
    }

    public void setFromAssignmentId(Long fromAssignmentId) {
        this.fromAssignmentId = fromAssignmentId;
    }

    public Long getToAssignmentId() {
        return toAssignmentId;
    }

    public void setToAssignmentId(Long toAssignmentId) {
        this.toAssignmentId = toAssignmentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(LocalDateTime effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
