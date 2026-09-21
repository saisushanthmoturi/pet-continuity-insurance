package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("monthly_eligible_checks")
public class MonthlyEligibleCheck {

    @Id
    @Column("eligibility_check_id")
    private Long eligibilityCheckId;

    @Column("pet_id")
    private Long petId;

    @Column("assignment_id")
    private Long assignmentId;

    @Column("fund_id")
    private Long fundId;

    @Column("eligibility_month")
    private String eligibilityMonth;

    @Column("caretaker_verified")
    private Boolean caretakerVerified = true;

    @Column("pet_verified")
    private Boolean petVerified = true;

    @Column("policy_active")
    private Boolean policyActive = true;

    @Column("fund_active")
    private Boolean fundActive = true;

    @Column("eligible")
    private Boolean eligible = true;

    @Column("failure_reason")
    private String failureReason;

    @Column("evaluated_at")
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    @Column("correlation_id")
    private String correlationId;

    public MonthlyEligibleCheck() {
        this.evaluatedAt = LocalDateTime.now();
        this.eligible = true;
    }

    public MonthlyEligibleCheck(Long eligibilityCheckId, Long petId, Long assignmentId, Long fundId,
                                String eligibilityMonth, Boolean caretakerVerified, Boolean petVerified,
                                Boolean policyActive, Boolean fundActive, Boolean eligible,
                                String failureReason, LocalDateTime evaluatedAt, String correlationId) {
        this.eligibilityCheckId = eligibilityCheckId;
        this.petId = petId;
        this.assignmentId = assignmentId;
        this.fundId = fundId;
        this.eligibilityMonth = eligibilityMonth;
        this.caretakerVerified = caretakerVerified != null ? caretakerVerified : true;
        this.petVerified = petVerified != null ? petVerified : true;
        this.policyActive = policyActive != null ? policyActive : true;
        this.fundActive = fundActive != null ? fundActive : true;
        this.eligible = eligible != null ? eligible : true;
        this.failureReason = failureReason;
        this.evaluatedAt = evaluatedAt != null ? evaluatedAt : LocalDateTime.now();
        this.correlationId = correlationId;
    }

    public Long getEligibilityCheckId() {
        return eligibilityCheckId;
    }

    public void setEligibilityCheckId(Long eligibilityCheckId) {
        this.eligibilityCheckId = eligibilityCheckId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getEligibilityMonth() {
        return eligibilityMonth;
    }

    public void setEligibilityMonth(String eligibilityMonth) {
        this.eligibilityMonth = eligibilityMonth;
    }

    public Boolean getCaretakerVerified() {
        return caretakerVerified;
    }

    public void setCaretakerVerified(Boolean caretakerVerified) {
        this.caretakerVerified = caretakerVerified;
    }

    public Boolean getPetVerified() {
        return petVerified;
    }

    public void setPetVerified(Boolean petVerified) {
        this.petVerified = petVerified;
    }

    public Boolean getPolicyActive() {
        return policyActive;
    }

    public void setPolicyActive(Boolean policyActive) {
        this.policyActive = policyActive;
    }

    public Boolean getFundActive() {
        return fundActive;
    }

    public void setFundActive(Boolean fundActive) {
        this.fundActive = fundActive;
    }

    public Boolean getEligible() {
        return eligible;
    }

    public void setEligible(Boolean eligible) {
        this.eligible = eligible;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
