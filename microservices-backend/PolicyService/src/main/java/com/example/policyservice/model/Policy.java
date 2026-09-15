package com.example.policyservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("policies")
public class Policy {

    @Id
    private Long id;

    @Column("policy_number")
    private String policyNumber;

    @Column("quote_id")
    private Long quoteId;

    @Column("customer_id")
    private Long customerId;

    @Column("pet_id")
    private Long petId;

    @Column("coverage_amount")
    private Double coverageAmount;

    @Column("monthly_premium")
    private Double monthlyPremium;

    @Column("status")
    private String status; // PENDING_PAYMENT, ACTIVE, CLAIM_FILED, TERMINATED

    @Column("start_date")
    private String startDate;

    @Column("end_date")
    private String endDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    public Policy() {
    }

    public Policy(Long id, String policyNumber, Long quoteId, Long customerId, Long petId, Double coverageAmount, Double monthlyPremium, String status, String startDate, String endDate, LocalDateTime createdAt) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.quoteId = quoteId;
        this.customerId = customerId;
        this.petId = petId;
        this.coverageAmount = coverageAmount;
        this.monthlyPremium = monthlyPremium;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public static Policy createFromQuote(Long quoteId, Long customerId, Long petId, Double coverageAmount, Double monthlyPremium) {
        String polNum = "POL-" + System.currentTimeMillis() + "-" + petId;
        LocalDate now = LocalDate.now();
        return new Policy(
                null,
                polNum,
                quoteId,
                customerId,
                petId,
                coverageAmount,
                monthlyPremium,
                "PENDING_PAYMENT",
                now.toString(),
                now.plusYears(1).toString(),
                LocalDateTime.now()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(Double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public Double getMonthlyPremium() {
        return monthlyPremium;
    }

    public void setMonthlyPremium(Double monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
