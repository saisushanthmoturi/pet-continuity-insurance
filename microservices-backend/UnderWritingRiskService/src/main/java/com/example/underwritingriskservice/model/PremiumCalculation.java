package com.example.underwritingriskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("premium_calculations")
public class PremiumCalculation {

    @Id
    @Column("premium_calculation_id")
    private Long premiumCalculationId;

    @Column("quote_id")
    private Long quoteId;

    @Column("risk_assessment_id")
    private Long riskAssessmentId;

    @Column("base_premium")
    private Double basePremium;

    @Column("risk_multiplier")
    private Double riskMultiplier;

    @Column("final_premium")
    private Double finalPremium;

    @Column("currency")
    private String currency = "USD";

    @Column("rule_set_version")
    private String ruleSetVersion = "V1";

    @Column("calculated_at")
    private LocalDateTime calculatedAt = LocalDateTime.now();

    public PremiumCalculation() {
    }

    public PremiumCalculation(Long premiumCalculationId, Long quoteId, Long riskAssessmentId, Double basePremium, Double riskMultiplier, Double finalPremium, String currency, String ruleSetVersion, LocalDateTime calculatedAt) {
        this.premiumCalculationId = premiumCalculationId;
        this.quoteId = quoteId;
        this.riskAssessmentId = riskAssessmentId;
        this.basePremium = basePremium;
        this.riskMultiplier = riskMultiplier;
        this.finalPremium = finalPremium;
        this.currency = currency != null ? currency : "USD";
        this.ruleSetVersion = ruleSetVersion != null ? ruleSetVersion : "V1";
        this.calculatedAt = calculatedAt != null ? calculatedAt : LocalDateTime.now();
    }

    public Long getPremiumCalculationId() {
        return premiumCalculationId;
    }

    public void setPremiumCalculationId(Long premiumCalculationId) {
        this.premiumCalculationId = premiumCalculationId;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Long getRiskAssessmentId() {
        return riskAssessmentId;
    }

    public void setRiskAssessmentId(Long riskAssessmentId) {
        this.riskAssessmentId = riskAssessmentId;
    }

    public Double getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(Double basePremium) {
        this.basePremium = basePremium;
    }

    public Double getRiskMultiplier() {
        return riskMultiplier;
    }

    public void setRiskMultiplier(Double riskMultiplier) {
        this.riskMultiplier = riskMultiplier;
    }

    public Double getFinalPremium() {
        return finalPremium;
    }

    public void setFinalPremium(Double finalPremium) {
        this.finalPremium = finalPremium;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRuleSetVersion() {
        return ruleSetVersion;
    }

    public void setRuleSetVersion(String ruleSetVersion) {
        this.ruleSetVersion = ruleSetVersion;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
