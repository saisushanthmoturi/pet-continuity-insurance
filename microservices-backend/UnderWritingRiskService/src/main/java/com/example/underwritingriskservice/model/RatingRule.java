package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("rating_rules")
public class RatingRule {

    @Id
    @Column("rule_id")
    private Long ruleId;

    @Column("rule_name")
    private String ruleName;

    @Column("factor")
    private String factor;

    @Column("min_value")
    private Double minValue;

    @Column("max_value")
    private Double maxValue;

    @Column("score")
    private Integer score;

    @Column("premium_factor")
    private Double premiumFactor;

    @Column("status")
    private String status = "ACTIVE";

    @Column("effective_from")
    private LocalDate effectiveFrom;

    @Column("effective_to")
    private LocalDate effectiveTo;

    public RatingRule() {
        this.status = "ACTIVE";
    }

    public RatingRule(Long ruleId, String ruleName, String factor, Double minValue, Double maxValue,
                      Integer score, Double premiumFactor, String status, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.factor = factor;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.score = score;
        this.premiumFactor = premiumFactor;
        this.status = status != null ? status : "ACTIVE";
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public Long getId() {
        return ruleId;
    }

    public void setId(Long id) {
        this.ruleId = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Double getPremiumFactor() {
        return premiumFactor;
    }

    public void setPremiumFactor(Double premiumFactor) {
        this.premiumFactor = premiumFactor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}
