package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("pet_care_funds")
public class PetCareFund {

    @Id
    @Column("fund_id")
    private Long fundId;

    @Column("policy_id")
    private Long policyId;

    @Column("pet_id")
    private Long petId;

    @Column("total_amount")
    private Double totalAmount;

    @Column("available_amount")
    private Double availableAmount;

    @Column("monthly_allowance")
    private Double monthlyAllowance;

    @Column("veterinary_reserve")
    private Double veterinaryReserve;

    @Column("emergency_reserve")
    private Double emergencyReserve;

    @Column("status")
    private String status = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PetCareFund() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public PetCareFund(Long fundId, Long policyId, Long petId, Double totalAmount, Double availableAmount,
                       Double monthlyAllowance, Double veterinaryReserve, Double emergencyReserve,
                       String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fundId = fundId;
        this.policyId = policyId;
        this.petId = petId;
        this.totalAmount = totalAmount;
        this.availableAmount = availableAmount;
        this.monthlyAllowance = monthlyAllowance;
        this.veterinaryReserve = veterinaryReserve;
        this.emergencyReserve = emergencyReserve;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public Long getId() {
        return fundId;
    }

    public void setId(Long id) {
        this.fundId = id;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Double getMonthlyAllowance() {
        return monthlyAllowance;
    }

    public void setMonthlyAllowance(Double monthlyAllowance) {
        this.monthlyAllowance = monthlyAllowance;
    }

    public Double getVeterinaryReserve() {
        return veterinaryReserve;
    }

    public void setVeterinaryReserve(Double veterinaryReserve) {
        this.veterinaryReserve = veterinaryReserve;
    }

    public Double getEmergencyReserve() {
        return emergencyReserve;
    }

    public void setEmergencyReserve(Double emergencyReserve) {
        this.emergencyReserve = emergencyReserve;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
