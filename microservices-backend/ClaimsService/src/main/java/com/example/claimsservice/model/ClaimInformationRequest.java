package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("claim_information_requests")
public class ClaimInformationRequest {

    @Id
    @Column("request_id")
    private Long requestId;

    @Column("claim_id")
    private Long claimId;

    @Column("requested_by")
    private String requestedBy;

    @Column("request_text")
    private String requestText;

    @Column("due_at")
    private LocalDateTime dueAt;

    @Column("status")
    private String status = "OPEN";

    @Column("responded_at")
    private LocalDateTime respondedAt;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ClaimInformationRequest() {
    }

    public ClaimInformationRequest(Long requestId, Long claimId, String requestedBy, String requestText, LocalDateTime dueAt, String status, LocalDateTime respondedAt, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.claimId = claimId;
        this.requestedBy = requestedBy;
        this.requestText = requestText;
        this.dueAt = dueAt;
        this.status = status != null ? status : "OPEN";
        this.respondedAt = respondedAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() {
        return requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
