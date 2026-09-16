package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("fraud_assessments")
public class FraudAssessment {

    @Id
    @Column("fraud_assessment_id")
    private Long fraudAssessmentId;

    @Column("claim_id")
    private Long claimId;

    @Column("score")
    private Integer score;

    @Column("indicators")
    private String indicators;

    @Column("decision")
    private String decision;

    @Column("assessed_by")
    private String assessedBy = "FRAUD_DETECTION_ENGINE";

    @Column("assessed_at")
    private LocalDateTime assessedAt = LocalDateTime.now();

    @Column("status")
    private String status = "COMPLETED";

    public FraudAssessment() {
        this.assessedAt = LocalDateTime.now();
        this.status = "COMPLETED";
        this.assessedBy = "FRAUD_DETECTION_ENGINE";
    }

    public FraudAssessment(Long fraudAssessmentId, Long claimId, Integer score, String indicators,
                           String decision, String assessedBy, LocalDateTime assessedAt, String status) {
        this.fraudAssessmentId = fraudAssessmentId;
        this.claimId = claimId;
        this.score = score;
        this.indicators = indicators;
        this.decision = decision;
        this.assessedBy = assessedBy != null ? assessedBy : "FRAUD_DETECTION_ENGINE";
        this.assessedAt = assessedAt != null ? assessedAt : LocalDateTime.now();
        this.status = status != null ? status : "COMPLETED";
    }

    public Long getId() {
        return fraudAssessmentId;
    }

    public void setId(Long id) {
        this.fraudAssessmentId = id;
    }

    public Long getFraudAssessmentId() {
        return fraudAssessmentId;
    }

    public void setFraudAssessmentId(Long fraudAssessmentId) {
        this.fraudAssessmentId = fraudAssessmentId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getIndicators() {
        return indicators;
    }

    public void setIndicators(String indicators) {
        this.indicators = indicators;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(String assessedBy) {
        this.assessedBy = assessedBy;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
