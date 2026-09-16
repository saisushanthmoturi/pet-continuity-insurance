package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("fund_transactions")
public class FundTransaction {

    @Id
    @Column("transaction_id")
    private Long transactionId;

    @Column("fund_id")
    private Long fundId;

    @Column("transaction_type")
    private String transactionType; // DISBURSEMENT, VET_EXPENSE, EMERGENCY, DEPOSIT

    @Column("amount")
    private Double amount;

    @Column("balance_after")
    private Double balanceAfter;

    @Column("reference_id")
    private String referenceId;

    @Column("description")
    private String description;

    @Column("status")
    private String status = "SUCCESS"; // SUCCESS, SUSPENDED, FAILED

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public FundTransaction() {
        this.createdAt = LocalDateTime.now();
        this.status = "SUCCESS";
    }

    public FundTransaction(Long transactionId, Long fundId, String transactionType, Double amount,
                           Double balanceAfter, String referenceId, String description,
                           String status, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.fundId = fundId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
        this.description = description;
        this.status = status != null ? status : "SUCCESS";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static FundTransaction create(Long fundId, String transactionType, Double amount, Double balanceAfter, String description, String status) {
        FundTransaction txn = new FundTransaction();
        txn.setFundId(fundId);
        txn.setTransactionType(transactionType);
        txn.setAmount(amount);
        txn.setBalanceAfter(balanceAfter);
        txn.setDescription(description);
        txn.setStatus(status);
        txn.setReferenceId("TXN-" + System.currentTimeMillis());
        txn.setCreatedAt(LocalDateTime.now());
        return txn;
    }

    public Long getId() {
        return transactionId;
    }

    public void setId(Long id) {
        this.transactionId = id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
