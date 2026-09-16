package com.example.policyservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("policy_status_history")
public class PolicyStatusHistory {

    @Id
    @Column("history_id")
    private Long historyId;

    @Column("policy_id")
    private Long policyId;

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

    public PolicyStatusHistory() {
        this.changedAt = LocalDateTime.now();
        this.changedBy = "SYSTEM";
    }

    public PolicyStatusHistory(Long historyId, Long policyId, String oldStatus, String newStatus,
                               String changedBy, String reason, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.policyId = policyId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy != null ? changedBy : "SYSTEM";
        this.reason = reason;
        this.changedAt = changedAt != null ? changedAt : LocalDateTime.now();
    }

    public static PolicyStatusHistory create(Long policyId, String previousStatus, String newStatus, String reason) {
        return new PolicyStatusHistory(null, policyId, previousStatus, newStatus, "SYSTEM", reason, LocalDateTime.now());
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

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getPreviousStatus() {
        return oldStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.oldStatus = previousStatus;
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
