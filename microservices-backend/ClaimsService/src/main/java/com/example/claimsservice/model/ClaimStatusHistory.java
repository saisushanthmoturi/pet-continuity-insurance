package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("claim_status_history")
public class ClaimStatusHistory {

    @Id
    @Column("history_id")
    private Long historyId;

    @Column("claim_id")
    private Long claimId;

    @Column("old_status")
    private String oldStatus;

    @Column("new_status")
    private String newStatus;

    @Column("changed_by")
    private String changedBy = "SYSTEM";

    @Column("reason")
    private String reason;

    @Column("changed_at")
    private LocalDateTime changedAt = LocalDateTime.now();

    public ClaimStatusHistory() {
    }

    public ClaimStatusHistory(Long historyId, Long claimId, String oldStatus, String newStatus, String changedBy, String reason, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.claimId = claimId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy != null ? changedBy : "SYSTEM";
        this.reason = reason;
        this.changedAt = changedAt != null ? changedAt : LocalDateTime.now();
    }

    public static ClaimStatusHistory create(Long claimId, String oldStatus, String newStatus, String reason) {
        return new ClaimStatusHistory(null, claimId, oldStatus, newStatus, "SYSTEM", reason, LocalDateTime.now());
    }

    public Long getId() {
        return historyId;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
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

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
