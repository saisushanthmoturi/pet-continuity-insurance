package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("premium_payments")
public class PremiumPayment {

    @Id
    @Column("payment_id")
    private Long paymentId;

    @Column("policy_id")
    private Long policyId;

    @Column("customer_id")
    private Long customerId;

    @Column("amount")
    private Double amount;

    @Column("payment_reference")
    private String paymentReference;

    @Column("payment_method")
    private String paymentMethod;

    @Column("status")
    private String status; // SUCCESS, FAILED

    @Column("payment_date")
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column("failure_reason")
    private String failureReason;

    public PremiumPayment() {
        this.paymentDate = LocalDateTime.now();
    }

    public PremiumPayment(Long paymentId, Long policyId, Long customerId, Double amount,
                          String paymentReference, String paymentMethod, String status,
                          LocalDateTime paymentDate, String failureReason) {
        this.paymentId = paymentId;
        this.policyId = policyId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentReference = paymentReference;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = paymentDate != null ? paymentDate : LocalDateTime.now();
        this.failureReason = failureReason;
    }

    public static PremiumPayment create(Long policyId, Double amount, String paymentMethod, String status) {
        String ref = "PAY-" + System.currentTimeMillis();
        PremiumPayment p = new PremiumPayment();
        p.setPolicyId(policyId);
        p.setAmount(amount);
        p.setPaymentMethod(paymentMethod);
        p.setStatus(status);
        p.setPaymentReference(ref);
        p.setPaymentDate(LocalDateTime.now());
        return p;
    }

    public Long getId() {
        return paymentId;
    }

    public void setId(Long id) {
        this.paymentId = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getTransactionReference() {
        return paymentReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.paymentReference = transactionReference;
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

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getCreatedAt() {
        return paymentDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.paymentDate = createdAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
