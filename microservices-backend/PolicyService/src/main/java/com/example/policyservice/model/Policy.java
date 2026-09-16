package com.example.policyservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("policies")
public class Policy {

    @Id
    @Column("policy_id")
    private Long policyId;

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

    @Column("premium_amount")
    private Double premiumAmount;

    @Column("deductible")
    private Double deductible = 250.0;

    @Column("start_date")
    private String startDate;

    @Column("end_date")
    private String endDate;

    @Column("status")
    private String status = "PENDING_PAYMENT"; // PENDING_PAYMENT, ACTIVE, CLAIM_FILED, TERMINATED

    @Column("issued_by")
    private String issuedBy = "SYSTEM";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Policy() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deductible = 250.0;
        this.issuedBy = "SYSTEM";
        this.status = "PENDING_PAYMENT";
    }

    public Policy(Long policyId, String policyNumber, Long quoteId, Long customerId, Long petId,
                  Double coverageAmount, Double premiumAmount, Double deductible,
                  String startDate, String endDate, String status, String issuedBy,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.quoteId = quoteId;
        this.customerId = customerId;
        this.petId = petId;
        this.coverageAmount = coverageAmount;
        this.premiumAmount = premiumAmount;
        this.deductible = deductible != null ? deductible : 250.0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : "PENDING_PAYMENT";
        this.issuedBy = issuedBy != null ? issuedBy : "SYSTEM";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static Policy createFromQuote(Long quoteId, Long customerId, Long petId, Double coverageAmount, Double monthlyPremium) {
        String polNum = "POL-" + System.currentTimeMillis() + "-" + petId;
        LocalDate now = LocalDate.now();
        Policy policy = new Policy();
        policy.setPolicyNumber(polNum);
        policy.setQuoteId(quoteId);
        policy.setCustomerId(customerId);
        policy.setPetId(petId);
        policy.setCoverageAmount(coverageAmount);
        policy.setPremiumAmount(monthlyPremium);
        policy.setDeductible(250.0);
        policy.setStartDate(now.toString());
        policy.setEndDate(now.plusYears(1).toString());
        policy.setStatus("PENDING_PAYMENT");
        policy.setIssuedBy("SYSTEM");
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());
        return policy;
    }

    // Backward-compatible ID getter/setter
    public Long getId() {
        return policyId;
    }

    public void setId(Long id) {
        this.policyId = id;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public Double getMonthlyPremium() {
        return premiumAmount;
    }

    public void setMonthlyPremium(Double monthlyPremium) {
        this.premiumAmount = monthlyPremium;
    }

    public Double getDeductible() {
        return deductible;
    }

    public void setDeductible(Double deductible) {
        this.deductible = deductible;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
