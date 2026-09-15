package com.example.policyservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("policy_status_history")
public class PolicyStatusHistory {

    @Id
    private Long id;

    @Column("policy_id")
    private Long policyId;

    @Column("previous_status")
    private String previousStatus;

    @Column("new_status")
    private String newStatus;

    @Column("reason")
    private String reason;

    @Column("changed_at")
    private LocalDateTime changedAt;

    public PolicyStatusHistory() {
    }

    public PolicyStatusHistory(Long id, Long policyId, String previousStatus, String newStatus, String reason, LocalDateTime changedAt) {
        this.id = id;
        this.policyId = policyId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    public static PolicyStatusHistory create(Long policyId, String previousStatus, String newStatus, String reason) {
        return new PolicyStatusHistory(null, policyId, previousStatus, newStatus, reason, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
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
