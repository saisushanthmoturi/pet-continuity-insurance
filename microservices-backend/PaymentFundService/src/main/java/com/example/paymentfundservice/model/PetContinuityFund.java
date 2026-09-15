package com.example.paymentfundservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("pet_continuity_funds")
public class PetContinuityFund {

    @Id
    private Long id;

    @Column("policy_id")
    private Long policyId;

    @Column("pet_id")
    private Long petId;

    @Column("total_fund")
    private Double totalFund;

    @Column("current_balance")
    private Double currentBalance;

    @Column("monthly_allowance")
    private Double monthlyAllowance;

    @Column("vet_reserve")
    private Double vetReserve;

    @Column("emergency_reserve")
    private Double emergencyReserve;

    @Column("status")
    private String status; // ACTIVE, SUSPENDED, DEPLETED

    @Column("created_at")
    private LocalDateTime createdAt;

    public PetContinuityFund() {
    }

    public PetContinuityFund(Long id, Long policyId, Long petId, Double totalFund, Double currentBalance, Double monthlyAllowance, Double vetReserve, Double emergencyReserve, String status, LocalDateTime createdAt) {
        this.id = id;
        this.policyId = policyId;
        this.petId = petId;
        this.totalFund = totalFund;
        this.currentBalance = currentBalance;
        this.monthlyAllowance = monthlyAllowance;
        this.vetReserve = vetReserve;
        this.emergencyReserve = emergencyReserve;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static PetContinuityFund create(Long policyId, Long petId, Double totalFund, Double monthlyAllowance, Double vetReserve, Double emergencyReserve) {
        double monthly = monthlyAllowance != null ? monthlyAllowance : 300.0;
        double vet = vetReserve != null ? vetReserve : 4000.0;
        double emergency = emergencyReserve != null ? emergencyReserve : 2000.0;
        return new PetContinuityFund(
                null,
                policyId,
                petId,
                totalFund,
                totalFund,
                monthly,
                vet,
                emergency,
                "ACTIVE",
                LocalDateTime.now()
        );
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Double getTotalFund() {
        return totalFund;
    }

    public void setTotalFund(Double totalFund) {
        this.totalFund = totalFund;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getMonthlyAllowance() {
        return monthlyAllowance;
    }

    public void setMonthlyAllowance(Double monthlyAllowance) {
        this.monthlyAllowance = monthlyAllowance;
    }

    public Double getVetReserve() {
        return vetReserve;
    }

    public void setVetReserve(Double vetReserve) {
        this.vetReserve = vetReserve;
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
}
