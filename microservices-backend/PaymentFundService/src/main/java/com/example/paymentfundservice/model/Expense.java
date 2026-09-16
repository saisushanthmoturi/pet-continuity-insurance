package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("expenses")
public class Expense {

    @Id
    @Column("expense_id")
    private Long expenseId;

    @Column("fund_id")
    private Long fundId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("expense_type")
    private String expenseType;

    @Column("amount")
    private Double amount;

    @Column("vendor_name")
    private String vendorName;

    @Column("document_reference")
    private String documentReference;

    @Column("approval_status")
    private String approvalStatus = "APPROVED";

    @Column("submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column("approved_at")
    private LocalDateTime approvedAt = LocalDateTime.now();

    public Expense() {
        this.approvalStatus = "APPROVED";
        this.submittedAt = LocalDateTime.now();
        this.approvedAt = LocalDateTime.now();
    }

    public Expense(Long expenseId, Long fundId, Long caretakerId, String expenseType, Double amount,
                   String vendorName, String documentReference, String approvalStatus,
                   LocalDateTime submittedAt, LocalDateTime approvedAt) {
        this.expenseId = expenseId;
        this.fundId = fundId;
        this.caretakerId = caretakerId;
        this.expenseType = expenseType;
        this.amount = amount;
        this.vendorName = vendorName;
        this.documentReference = documentReference;
        this.approvalStatus = approvalStatus != null ? approvalStatus : "APPROVED";
        this.submittedAt = submittedAt != null ? submittedAt : LocalDateTime.now();
        this.approvedAt = approvedAt != null ? approvedAt : LocalDateTime.now();
    }

    public Long getId() {
        return expenseId;
    }

    public void setId(Long id) {
        this.expenseId = id;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
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

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
}
