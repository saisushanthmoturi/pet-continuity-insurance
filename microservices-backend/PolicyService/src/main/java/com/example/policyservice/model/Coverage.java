package com.example.policyservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("coverages")
public class Coverage {

    @Id
    @Column("coverage_id")
    private Long coverageId;

    @Column("policy_id")
    private Long policyId;

    @Column("coverage_type")
    private String coverageType;

    @Column("coverage_amount")
    private Double coverageAmount;

    @Column("limit_amount")
    private Double limitAmount;

    @Column("deductible")
    private Double deductible = 0.0;

    @Column("status")
    private String status = "ACTIVE";

    public Coverage() {
        this.status = "ACTIVE";
        this.deductible = 0.0;
    }

    public Coverage(Long coverageId, Long policyId, String coverageType, Double coverageAmount,
                    Double limitAmount, Double deductible, String status) {
        this.coverageId = coverageId;
        this.policyId = policyId;
        this.coverageType = coverageType;
        this.coverageAmount = coverageAmount;
        this.limitAmount = limitAmount;
        this.deductible = deductible != null ? deductible : 0.0;
        this.status = status != null ? status : "ACTIVE";
    }

    public Long getId() {
        return coverageId;
    }

    public void setId(Long id) {
        this.coverageId = id;
    }

    public Long getCoverageId() {
        return coverageId;
    }

    public void setCoverageId(Long coverageId) {
        this.coverageId = coverageId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    public Double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(Double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public Double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(Double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Double getDeductible() {
        return deductible;
    }

    public void setDeductible(Double deductible) {
        this.deductible = deductible;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
