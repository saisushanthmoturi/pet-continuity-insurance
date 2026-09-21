package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("fund_status_history")
public class FundStatusHistory {

    @Id
    @Column("history_id")
    private Long historyId;

    @Column("fund_id")
    private Long fundId;

    @Column("old_status")
    private String oldStatus;

    @Column("new_status")
    private String newStatus;

    @Column("changed_by")
    private String changedBy;

    @Column("reason")
    private String reason;

    @Column("changed_at")
    private LocalDateTime changedAt = LocalDateTime.now();

    public FundStatusHistory() {
        this.changedAt = LocalDateTime.now();
    }

    public FundStatusHistory(Long historyId, Long fundId, String oldStatus, String newStatus,
                             String changedBy, String reason, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.fundId = fundId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt != null ? changedAt : LocalDateTime.now();
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
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
