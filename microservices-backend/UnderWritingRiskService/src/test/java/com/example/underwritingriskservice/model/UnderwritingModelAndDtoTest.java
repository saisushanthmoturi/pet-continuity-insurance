package com.example.underwritingriskservice.model;

import com.example.underwritingriskservice.config.DatabaseConfig;
import com.example.underwritingriskservice.config.WebClientConfig;
import com.example.underwritingriskservice.dto.*;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UnderwritingModelAndDtoTest {

    @Test
    void testQuote() {
        LocalDateTime now = LocalDateTime.now();
        Quote q = new Quote(1L, 10L, 20L, 15000.0, "ANNUAL", 45.0, 35, "LOW", "APPROVED", "OFFERED", 2L, now, now, now.plusDays(30));
        assertEquals(1L, q.getId());
        assertEquals(1L, q.getQuoteId());
        assertEquals(10L, q.getCustomerId());
        assertEquals(20L, q.getPetId());
        assertEquals(15000.0, q.getRequestedCoverage());
        assertEquals("ANNUAL", q.getCoveragePeriod());
        assertEquals(45.0, q.getPremiumAmount());
        assertEquals(45.0, q.getMonthlyPremium());
        assertEquals(35, q.getRiskScore());
        assertEquals("LOW", q.getRiskClass());
        assertEquals("APPROVED", q.getDecision());
        assertEquals("OFFERED", q.getStatus());
        assertEquals(2L, q.getUnderwriterId());
        assertEquals(now, q.getCreatedAt());
        assertEquals(now, q.getUpdatedAt());
        assertNotNull(q.getValidUntil());

        q.setId(2L);
        assertEquals(2L, q.getId());
        q.setQuoteId(3L);
        assertEquals(3L, q.getQuoteId());
        q.setCustomerId(15L);
        assertEquals(15L, q.getCustomerId());
        q.setPetId(25L);
        assertEquals(25L, q.getPetId());
        q.setRequestedCoverage(20000.0);
        assertEquals(20000.0, q.getRequestedCoverage());
        q.setCoveragePeriod("MONTHLY");
        assertEquals("MONTHLY", q.getCoveragePeriod());
        q.setPremiumAmount(55.0);
        assertEquals(55.0, q.getPremiumAmount());
        q.setMonthlyPremium(60.0);
        assertEquals(60.0, q.getMonthlyPremium());
        q.setRiskScore(75);
        assertEquals(75, q.getRiskScore());
        q.setRiskClass("HIGH");
        assertEquals("HIGH", q.getRiskClass());
        q.setDecision("REFERRED");
        assertEquals("REFERRED", q.getDecision());
        q.setStatus("ACCEPTED");
        assertEquals("ACCEPTED", q.getStatus());
        q.setUnderwriterId(5L);
        assertEquals(5L, q.getUnderwriterId());
        LocalDateTime next = now.plusDays(1);
        q.setCreatedAt(next);
        assertEquals(next, q.getCreatedAt());
        q.setUpdatedAt(next);
        assertEquals(next, q.getUpdatedAt());
        q.setValidUntil(next.plusDays(10));
        assertEquals(next.plusDays(10), q.getValidUntil());

        Quote def = new Quote();
        assertNull(def.getId());

        Quote nullArgs = new Quote(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("ANNUAL", nullArgs.getCoveragePeriod());
        assertEquals("MODERATE", nullArgs.getRiskClass());
        assertEquals("OFFERED", nullArgs.getStatus());

        Quote qLow = Quote.createNew(10L, 20L, 10000.0, 30.0, 30, "APPROVED");
        assertEquals("LOW", qLow.getRiskClass());
        Quote qMod = Quote.createNew(10L, 20L, 10000.0, 40.0, 60, "APPROVED");
        assertEquals("MODERATE", qMod.getRiskClass());
        Quote qHigh = Quote.createNew(10L, 20L, 10000.0, 50.0, 80, "APPROVED");
        assertEquals("HIGH", qHigh.getRiskClass());
        Quote qExt = Quote.createNew(10L, 20L, 10000.0, 75.0, 95, "REJECTED");
        assertEquals("EXTREME", qExt.getRiskClass());
    }

    @Test
    void testRiskAssessment() {
        LocalDateTime now = LocalDateTime.now();
        RiskAssessment ra = new RiskAssessment(1L, 10L, 10.0, 5.0, 15.0, 5000.0, 1000.0, 45.0, "MODERATE", "Explanation", now, "COMPLETED");
        assertEquals(1L, ra.getId());
        assertEquals(1L, ra.getRiskAssessmentId());
        assertEquals(10L, ra.getQuoteId());
        assertEquals(10.0, ra.getAgeFactor());
        assertEquals(5.0, ra.getBreedFactor());
        assertEquals(15.0, ra.getMedicalFactor());
        assertEquals(15.0, ra.getHealthFactor());
        assertEquals(5000.0, ra.getCareCostFactor());
        assertEquals(5000.0, ra.getProjectedCareLiability());
        assertEquals(1000.0, ra.getContinuityFactor());
        assertEquals(1000.0, ra.getCoverageGap());
        assertEquals(45.0, ra.getTotalScore());
        assertEquals("MODERATE", ra.getRiskClass());
        assertEquals("MODERATE", ra.getRiskLevel());
        assertEquals("Explanation", ra.getExplanation());
        assertEquals(now, ra.getAssessedAt());
        assertEquals(now, ra.getAssessmentDate());
        assertEquals("COMPLETED", ra.getStatus());

        ra.setId(2L);
        assertEquals(2L, ra.getId());
        ra.setRiskAssessmentId(3L);
        assertEquals(3L, ra.getRiskAssessmentId());
        ra.setQuoteId(15L);
        assertEquals(15L, ra.getQuoteId());
        ra.setAgeFactor(12.0);
        assertEquals(12.0, ra.getAgeFactor());
        ra.setBreedFactor(8.0);
        assertEquals(8.0, ra.getBreedFactor());
        ra.setMedicalFactor(20.0);
        assertEquals(20.0, ra.getMedicalFactor());
        ra.setHealthFactor(22.0);
        assertEquals(22.0, ra.getHealthFactor());
        ra.setCareCostFactor(6000.0);
        assertEquals(6000.0, ra.getCareCostFactor());
        ra.setContinuityFactor(1200.0);
        assertEquals(1200.0, ra.getContinuityFactor());
        ra.setTotalScore(55.0);
        assertEquals(55.0, ra.getTotalScore());
        ra.setRiskClass("HIGH");
        assertEquals("HIGH", ra.getRiskClass());
        ra.setRiskLevel("HIGH");
        assertEquals("HIGH", ra.getRiskLevel());
        ra.setExplanation("Updated explanation");
        assertEquals("Updated explanation", ra.getExplanation());
        LocalDateTime next = now.plusDays(1);
        ra.setAssessedAt(next);
        assertEquals(next, ra.getAssessedAt());
        ra.setAssessmentDate(next);
        assertEquals(next, ra.getAssessmentDate());
        ra.setStatus("REVIEW");
        assertEquals("REVIEW", ra.getStatus());

        RiskAssessment def = new RiskAssessment();
        assertEquals("COMPLETED", def.getStatus());

        RiskAssessment nullArgs = new RiskAssessment(null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals(0.0, nullArgs.getBreedFactor());
        assertEquals("COMPLETED", nullArgs.getStatus());

        RiskAssessment created = RiskAssessment.createNew(10L, 20L, 5.0, 10.0, 4000.0, 500.0, "LOW");
        assertEquals(10L, created.getQuoteId());
        assertEquals("LOW", created.getRiskLevel());
    }

    @Test
    void testRatingRule() {
        LocalDate today = LocalDate.now();
        RatingRule rule = new RatingRule(1L, "BREED_RISK", "BREED", 1.0, 10.0, 20, 1.25, "ACTIVE", today, today.plusYears(1));
        assertEquals(1L, rule.getId());
        assertEquals(1L, rule.getRuleId());
        assertEquals("BREED_RISK", rule.getRuleName());
        assertEquals("BREED", rule.getFactor());
        assertEquals(1.0, rule.getMinValue());
        assertEquals(10.0, rule.getMaxValue());
        assertEquals(20, rule.getScore());
        assertEquals(1.25, rule.getPremiumFactor());
        assertEquals("ACTIVE", rule.getStatus());
        assertEquals(today, rule.getEffectiveFrom());
        assertEquals(today.plusYears(1), rule.getEffectiveTo());

        rule.setId(2L);
        assertEquals(2L, rule.getId());
        rule.setRuleId(3L);
        assertEquals(3L, rule.getRuleId());
        rule.setRuleName("AGE_RISK");
        assertEquals("AGE_RISK", rule.getRuleName());
        rule.setFactor("AGE");
        assertEquals("AGE", rule.getFactor());
        rule.setMinValue(2.0);
        assertEquals(2.0, rule.getMinValue());
        rule.setMaxValue(15.0);
        assertEquals(15.0, rule.getMaxValue());
        rule.setScore(30);
        assertEquals(30, rule.getScore());
        rule.setPremiumFactor(1.5);
        assertEquals(1.5, rule.getPremiumFactor());
        rule.setStatus("INACTIVE");
        assertEquals("INACTIVE", rule.getStatus());
        rule.setEffectiveFrom(today.plusDays(1));
        assertEquals(today.plusDays(1), rule.getEffectiveFrom());
        rule.setEffectiveTo(today.plusDays(30));
        assertEquals(today.plusDays(30), rule.getEffectiveTo());

        RatingRule def = new RatingRule();
        assertNull(def.getId());
    }

    @Test
    void testCoverageAssessment() {
        CoverageAssessment ca = new CoverageAssessment(1L, 10L, 1200.0, 10, 3000.0, 1.05, 15000.0, 12000.0, 3000.0, "Adequate", "CALCULATED");
        assertEquals(1L, ca.getId());
        assertEquals(1L, ca.getCoverageAssessmentId());
        assertEquals(10L, ca.getQuoteId());
        assertEquals(1200.0, ca.getAnnualCareCost());
        assertEquals(10, ca.getRemainingYears());
        assertEquals(3000.0, ca.getMedicalReserve());
        assertEquals(1.05, ca.getInflationAdjustment());
        assertEquals(15000.0, ca.getProjectedLiability());
        assertEquals(12000.0, ca.getRequestedCoverage());
        assertEquals(3000.0, ca.getCoverageGap());
        assertEquals("Adequate", ca.getRecommendation());
        assertEquals("CALCULATED", ca.getStatus());

        ca.setId(2L);
        assertEquals(2L, ca.getId());
        ca.setCoverageAssessmentId(3L);
        assertEquals(3L, ca.getCoverageAssessmentId());
        ca.setQuoteId(15L);
        assertEquals(15L, ca.getQuoteId());
        ca.setAnnualCareCost(1500.0);
        assertEquals(1500.0, ca.getAnnualCareCost());
        ca.setRemainingYears(8);
        assertEquals(8, ca.getRemainingYears());
        ca.setMedicalReserve(4000.0);
        assertEquals(4000.0, ca.getMedicalReserve());
        ca.setInflationAdjustment(1.08);
        assertEquals(1.08, ca.getInflationAdjustment());
        ca.setProjectedLiability(18000.0);
        assertEquals(18000.0, ca.getProjectedLiability());
        ca.setRequestedCoverage(15000.0);
        assertEquals(15000.0, ca.getRequestedCoverage());
        ca.setCoverageGap(3000.0);
        assertEquals(3000.0, ca.getCoverageGap());
        ca.setRecommendation("Increase");
        assertEquals("Increase", ca.getRecommendation());
        ca.setStatus("FINAL");
        assertEquals("FINAL", ca.getStatus());

        CoverageAssessment def = new CoverageAssessment();
        assertEquals("CALCULATED", def.getStatus());

        CoverageAssessment nullArgs = new CoverageAssessment(null, null, null, null, null, null, null, null, null, null, null);
        assertEquals(1.05, nullArgs.getInflationAdjustment());
        assertEquals("CALCULATED", nullArgs.getStatus());
    }

    @Test
    void testPremiumCalculation() {
        LocalDateTime now = LocalDateTime.now();
        PremiumCalculation pc = new PremiumCalculation(1L, 10L, 20L, 30.0, 1.35, 40.5, "USD", "V1", now);
        assertEquals(1L, pc.getPremiumCalculationId());
        assertEquals(10L, pc.getQuoteId());
        assertEquals(20L, pc.getRiskAssessmentId());
        assertEquals(30.0, pc.getBasePremium());
        assertEquals(1.35, pc.getRiskMultiplier());
        assertEquals(40.5, pc.getFinalPremium());
        assertEquals("USD", pc.getCurrency());
        assertEquals("V1", pc.getRuleSetVersion());
        assertEquals(now, pc.getCalculatedAt());

        pc.setPremiumCalculationId(2L);
        assertEquals(2L, pc.getPremiumCalculationId());
        pc.setQuoteId(15L);
        assertEquals(15L, pc.getQuoteId());
        pc.setRiskAssessmentId(25L);
        assertEquals(25L, pc.getRiskAssessmentId());
        pc.setBasePremium(35.0);
        assertEquals(35.0, pc.getBasePremium());
        pc.setRiskMultiplier(1.5);
        assertEquals(1.5, pc.getRiskMultiplier());
        pc.setFinalPremium(52.5);
        assertEquals(52.5, pc.getFinalPremium());
        pc.setCurrency("EUR");
        assertEquals("EUR", pc.getCurrency());
        pc.setRuleSetVersion("V2");
        assertEquals("V2", pc.getRuleSetVersion());
        LocalDateTime next = now.plusDays(1);
        pc.setCalculatedAt(next);
        assertEquals(next, pc.getCalculatedAt());

        PremiumCalculation def = new PremiumCalculation();
        assertNull(def.getPremiumCalculationId());

        PremiumCalculation nullArgs = new PremiumCalculation(null, null, null, null, null, null, null, null, null);
        assertEquals("USD", nullArgs.getCurrency());
        assertEquals("V1", nullArgs.getRuleSetVersion());
        assertNotNull(nullArgs.getCalculatedAt());
    }

    @Test
    void testDtos() {
        MedicalRecordDto mrd = new MedicalRecordDto(1L, 20L, "Asthma", "2024-01-01", "Inhaler", 300.0);
        assertEquals(1L, mrd.id());
        assertEquals(20L, mrd.petId());
        assertEquals("Asthma", mrd.conditionName());
        assertEquals("2024-01-01", mrd.diagnosisDate());
        assertEquals("Inhaler", mrd.treatmentPlan());
        assertEquals(300.0, mrd.estimatedAnnualMedCost());

        CarePlanDto cpd = new CarePlanDto(1L, 20L, 5L, 6L, "VetDoc", "Feeding", "None");
        assertEquals(1L, cpd.id());
        assertEquals(20L, cpd.petId());
        assertEquals(5L, cpd.primaryCaretakerId());
        assertEquals(6L, cpd.backupCaretakerId());
        assertEquals("VetDoc", cpd.vetContact());
        assertEquals("Feeding", cpd.feedingInstructions());
        assertEquals("None", cpd.specialNeeds());

        CarePlanDto emptyCp = CarePlanDto.empty();
        assertNull(emptyCp.primaryCaretakerId());

        CustomerDto cd = new CustomerDto(10L, 100L, "John Doe", "john@example.com", "1234567890", "123 Main St");
        assertEquals(10L, cd.id());
        assertEquals(10L, cd.id());
        assertEquals(100L, cd.userId());
        assertEquals("John Doe", cd.fullName());
        assertEquals("john@example.com", cd.email());
        assertEquals("1234567890", cd.phone());
        assertEquals("123 Main St", cd.address());

        PetDto pd = new PetDto(20L, 10L, "Buddy", "DOG", "Labrador", 3, 25.0, "MALE", 1200.0);
        assertEquals(20L, pd.id());
        assertEquals(10L, pd.customerId());
        assertEquals("Buddy", pd.name());
        assertEquals("DOG", pd.species());
        assertEquals("Labrador", pd.breed());
        assertEquals(3, pd.age());
        assertEquals(25.0, pd.weight());
        assertEquals("MALE", pd.gender());
        assertEquals(1200.0, pd.estimatedAnnualCareCost());

        QuoteRequest qr = new QuoteRequest(10L, 20L, 15000.0);
        assertEquals(10L, qr.customerId());
        assertEquals(20L, qr.petId());
        assertEquals(15000.0, qr.requestedCoverage());

        LocalDateTime now = LocalDateTime.now();
        QuoteResponse resp = new QuoteResponse(1L, 10L, 20L, 15000.0, 45.0, 35, "APPROVED", "OFFERED", 15000.0, 0.0, "LOW", now);
        assertEquals(1L, resp.id());
        assertEquals(10L, resp.customerId());
        assertEquals(20L, resp.petId());
        assertEquals(15000.0, resp.requestedCoverage());
        assertEquals(45.0, resp.monthlyPremium());
        assertEquals(35, resp.riskScore());
        assertEquals("APPROVED", resp.decision());
        assertEquals("OFFERED", resp.status());
        assertEquals(15000.0, resp.projectedCareLiability());
        assertEquals(0.0, resp.coverageGap());
        assertEquals("LOW", resp.riskLevel());
        assertEquals(now, resp.validUntil());
    }

    @Test
    void testConfigs() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        ConnectionFactory cf = mock(ConnectionFactory.class);
        ConnectionFactoryInitializer initializer = dbConfig.initializer(cf);
        assertNotNull(initializer);

        WebClientConfig wcConfig = new WebClientConfig();
        WebClient.Builder builder = wcConfig.loadBalancedWebClientBuilder();
        assertNotNull(builder);
    }
}
