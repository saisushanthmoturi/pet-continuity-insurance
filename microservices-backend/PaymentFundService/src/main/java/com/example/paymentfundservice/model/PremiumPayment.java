package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("premium_payments")
public class PremiumPayment {

    @Id
    private Long id;

    @Column("policy_id")
    private Long policyId;

    @Column("amount")
    private Double amount;

    @Column("payment_method")
    private String paymentMethod;

    @Column("status")
    private String status; // SUCCESS, FAILED

    @Column("transaction_reference")
    private String transactionReference;

    @Column("created_at")
    private LocalDateTime createdAt;

    public PremiumPayment() {
    }

    public PremiumPayment(Long id, Long policyId, Double amount, String paymentMethod, String status, String transactionReference, LocalDateTime createdAt) {
        this.id = id;
        this.policyId = policyId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionReference = transactionReference;
        this.createdAt = createdAt;
    }

    public static PremiumPayment create(Long policyId, Double amount, String paymentMethod, String status) {
        String ref = "TXN-" + System.currentTimeMillis();
        return new PremiumPayment(null, policyId, amount, paymentMethod, status, ref, LocalDateTime.now());
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
