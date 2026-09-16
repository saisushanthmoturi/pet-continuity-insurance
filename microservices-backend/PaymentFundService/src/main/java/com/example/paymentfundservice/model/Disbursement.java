package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("disbursements")
public class Disbursement {

    @Id
    @Column("disbursement_id")
    private Long disbursementId;

    @Column("fund_id")
    private Long fundId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("amount")
    private Double amount;

    @Column("disbursement_type")
    private String disbursementType = "MONTHLY_ALLOWANCE";

    @Column("eligibility_status")
    private String eligibilityStatus = "ELIGIBLE";

    @Column("status")
    private String status = "PROCESSED";

    @Column("scheduled_date")
    private LocalDate scheduledDate = LocalDate.now();

    @Column("processed_at")
    private LocalDateTime processedAt = LocalDateTime.now();

    public Disbursement() {
        this.disbursementType = "MONTHLY_ALLOWANCE";
        this.eligibilityStatus = "ELIGIBLE";
        this.status = "PROCESSED";
        this.scheduledDate = LocalDate.now();
        this.processedAt = LocalDateTime.now();
    }

    public Disbursement(Long disbursementId, Long fundId, Long caretakerId, Double amount,
                        String disbursementType, String eligibilityStatus, String status,
                        LocalDate scheduledDate, LocalDateTime processedAt) {
        this.disbursementId = disbursementId;
        this.fundId = fundId;
        this.caretakerId = caretakerId;
        this.amount = amount;
        this.disbursementType = disbursementType != null ? disbursementType : "MONTHLY_ALLOWANCE";
        this.eligibilityStatus = eligibilityStatus != null ? eligibilityStatus : "ELIGIBLE";
        this.status = status != null ? status : "PROCESSED";
        this.scheduledDate = scheduledDate != null ? scheduledDate : LocalDate.now();
        this.processedAt = processedAt != null ? processedAt : LocalDateTime.now();
    }

    public Long getId() {
        return disbursementId;
    }

    public void setId(Long id) {
        this.disbursementId = id;
    }

    public Long getDisbursementId() {
        return disbursementId;
    }

    public void setDisbursementId(Long disbursementId) {
        this.disbursementId = disbursementId;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public Long getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(String disbursementType) {
        this.disbursementType = disbursementType;
    }

    public String getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
