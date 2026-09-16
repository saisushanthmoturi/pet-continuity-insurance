package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("pet_care_funds")
public class PetContinuityFund {

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
    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, DEPLETED

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PetContinuityFund() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public PetContinuityFund(Long fundId, Long policyId, Long petId, Double totalAmount, Double availableAmount,
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

    public static PetContinuityFund create(Long policyId, Long petId, Double totalFund, Double monthlyAllowance, Double vetReserve, Double emergencyReserve) {
        double monthly = monthlyAllowance != null ? monthlyAllowance : 300.0;
        double vet = vetReserve != null ? vetReserve : 4000.0;
        double emg = emergencyReserve != null ? emergencyReserve : 2000.0;
        double total = totalFund != null ? totalFund : 25000.0;
        PetContinuityFund fund = new PetContinuityFund();
        fund.setPolicyId(policyId);
        fund.setPetId(petId);
        fund.setTotalAmount(total);
        fund.setAvailableAmount(total);
        fund.setMonthlyAllowance(monthly);
        fund.setVeterinaryReserve(vet);
        fund.setEmergencyReserve(emg);
        fund.setStatus("ACTIVE");
        fund.setCreatedAt(LocalDateTime.now());
        fund.setUpdatedAt(LocalDateTime.now());
        return fund;
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

    public Double getTotalFund() {
        return totalAmount;
    }

    public void setTotalFund(Double totalFund) {
        this.totalAmount = totalFund;
    }

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Double getCurrentBalance() {
        return availableAmount;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.availableAmount = currentBalance;
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

    public Double getVetReserve() {
        return veterinaryReserve;
    }

    public void setVetReserve(Double vetReserve) {
        this.veterinaryReserve = vetReserve;
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
