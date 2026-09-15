package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("fund_transactions")
public class FundTransaction {

    @Id
    private Long id;

    @Column("fund_id")
    private Long fundId;

    @Column("transaction_type")
    private String transactionType; // DISBURSEMENT, VET_EXPENSE, EMERGENCY, DEPOSIT

    @Column("amount")
    private Double amount;

    @Column("balance_after")
    private Double balanceAfter;

    @Column("description")
    private String description;

    @Column("status")
    private String status; // SUCCESS, SUSPENDED, FAILED

    @Column("created_at")
    private LocalDateTime createdAt;

    public FundTransaction() {
    }

    public FundTransaction(Long id, Long fundId, String transactionType, Double amount, Double balanceAfter, String description, String status, LocalDateTime createdAt) {
        this.id = id;
        this.fundId = fundId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static FundTransaction create(Long fundId, String transactionType, Double amount, Double balanceAfter, String description, String status) {
        return new FundTransaction(null, fundId, transactionType, amount, balanceAfter, description, status, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
