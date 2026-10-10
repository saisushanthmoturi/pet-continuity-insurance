package com.example.claimsservice.model;

import com.example.claimsservice.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClaimsModelAndDtoTest {

    @Test
    void testClaim() {
        Claim c = new Claim();
        assertNotNull(c);
        assertEquals("PENDING", c.getStatus());

        c.setId(10L);
        c.setClaimId(10L);
        c.setPolicyId(20L);
        c.setCustomerId(30L);
        c.setPetId(40L);
        c.setClaimReason("Reason");
        c.setTriggeringEvent("ACCIDENT");
        c.setClaimAmount(15000.0);
        c.setStatus("APPROVED");
        c.setPriority("HIGH");
        c.setClaimsOfficerId(50L);
        c.setFraudStatus("FLAGGED");
        c.setClaimNumber("CLM-999");
        c.setClaimantName("John");
        c.setRelationship("FRIEND");
        c.setDeathCertificateNo("DC-111");
        c.setDateOfDeath("2026-10-10");
        c.setRejectionReason("None");
        c.setNotes("Note");
        LocalDateTime now = LocalDateTime.now();
        c.setSubmittedAt(now);
        c.setUpdatedAt(now);

        assertEquals(10L, c.getId());
        assertEquals(10L, c.getClaimId());
        assertEquals(20L, c.getPolicyId());
        assertEquals(30L, c.getCustomerId());
        assertEquals(40L, c.getPetId());
        assertEquals("Reason", c.getClaimReason());
        assertEquals("ACCIDENT", c.getTriggeringEvent());
        assertEquals(15000.0, c.getClaimAmount());
        assertEquals("APPROVED", c.getStatus());
        assertEquals("HIGH", c.getPriority());
        assertEquals(50L, c.getClaimsOfficerId());
        assertEquals("FLAGGED", c.getFraudStatus());
        assertEquals("CLM-999", c.getClaimNumber());
        assertEquals("John", c.getClaimantName());
        assertEquals("FRIEND", c.getRelationship());
        assertEquals("DC-111", c.getDeathCertificateNo());
        assertEquals("2026-10-10", c.getDateOfDeath());
        assertEquals("None", c.getRejectionReason());
        assertEquals("Note", c.getNotes());
        assertEquals(now, c.getSubmittedAt());
        assertEquals(now, c.getUpdatedAt());

        Claim created = Claim.create(1L, "Alice", "SPOUSE", "DC-222", "2026-10-10", "Notes");
        assertNotNull(created);
        assertEquals("Alice", created.getClaimantName());

        Claim full = new Claim(1L, 2L, 3L, 4L, "Reason", "EVENT", 5000.0, "PENDING", "NORMAL", 5L, "CLEARED", now, now);
        assertEquals(1L, full.getId());
    }

    @Test
    void testClaimDocument() {
        ClaimDocument doc = new ClaimDocument();
        assertNotNull(doc);

        LocalDateTime now = LocalDateTime.now();
        doc.setId(1L);
        doc.setClaimId(10L);
        doc.setDocumentType("CERT");
        doc.setFileName("file.pdf");
        doc.setFileReference("ref");
        doc.setVerificationStatus("VERIFIED");
        doc.setUploadedAt(now);

        assertEquals(1L, doc.getId());
        assertEquals(10L, doc.getClaimId());
        assertEquals("CERT", doc.getDocumentType());
        assertEquals("file.pdf", doc.getFileName());
        assertEquals("ref", doc.getFileReference());
        assertEquals("VERIFIED", doc.getVerificationStatus());
        assertEquals(now, doc.getUploadedAt());

        ClaimDocument docFull = new ClaimDocument(2L, 20L, "TYPE", "name", "ref", "STATUS", now);
        assertEquals(2L, docFull.getId());
    }

    @Test
    void testClaimInvestigation() {
        ClaimInvestigation inv = new ClaimInvestigation();
        assertNotNull(inv);

        LocalDateTime now = LocalDateTime.now();
        inv.setId(1L);
        inv.setInvestigationId(1L);
        inv.setClaimId(10L);
        inv.setInvestigatorId(2L);
        inv.setPolicyActive(true);
        inv.setWaitingPeriodPassed(true);
        inv.setFraudScore(5);
        inv.setDecision("APPROVED");
        inv.setRecommendation("APPROVED");
        inv.setFindings("Findings");
        inv.setNotes("Notes");
        inv.setStatus("COMPLETED");
        inv.setEligibilityStatus("ELIGIBLE");
        inv.setFraudIndicatorCount(0);
        inv.setCompletedAt(now);
        inv.setInvestigatedAt(now);

        assertEquals(1L, inv.getId());
        assertEquals(1L, inv.getInvestigationId());
        assertEquals(10L, inv.getClaimId());
        assertEquals(2L, inv.getInvestigatorId());
        assertTrue(inv.getPolicyActive());
        assertTrue(inv.getWaitingPeriodPassed());
        assertEquals(5, inv.getFraudScore());
        assertEquals("APPROVED", inv.getDecision());
        assertEquals("APPROVED", inv.getRecommendation());
        assertEquals("Notes", inv.getFindings());
        assertEquals("Notes", inv.getNotes());
        assertEquals("COMPLETED", inv.getStatus());
        assertEquals("ELIGIBLE", inv.getEligibilityStatus());
        assertEquals(0, inv.getFraudIndicatorCount());
        assertEquals(now, inv.getCompletedAt());
        assertEquals(now, inv.getInvestigatedAt());

        ClaimInvestigation created = ClaimInvestigation.create(10L, true, true, 5, "APPROVED", "Notes");
        assertNotNull(created);
        assertEquals("ELIGIBLE", created.getEligibilityStatus());

        ClaimInvestigation createdHighFraud = ClaimInvestigation.create(10L, false, false, 60, "REJECTED", null);
        assertEquals("INELIGIBLE", createdHighFraud.getEligibilityStatus());
        assertEquals(2, createdHighFraud.getFraudIndicatorCount());

        ClaimInvestigation full = new ClaimInvestigation(1L, 10L, 2L, "FINDINGS", "ELIGIBLE", 0, "REC", "STATUS", now);
        assertEquals(1L, full.getId());
    }

    @Test
    void testClaimStatusHistory() {
        ClaimStatusHistory h = new ClaimStatusHistory();
        assertNotNull(h);

        LocalDateTime now = LocalDateTime.now();
        h.setHistoryId(1L);
        h.setClaimId(10L);
        h.setOldStatus("PENDING");
        h.setNewStatus("APPROVED");
        h.setChangedBy("ADMIN");
        h.setReason("Reason");
        h.setChangedAt(now);

        assertEquals(1L, h.getId());
        assertEquals(1L, h.getHistoryId());
        assertEquals(10L, h.getClaimId());
        assertEquals("PENDING", h.getOldStatus());
        assertEquals("APPROVED", h.getNewStatus());
        assertEquals("ADMIN", h.getChangedBy());
        assertEquals("Reason", h.getReason());
        assertEquals(now, h.getChangedAt());

        ClaimStatusHistory created = ClaimStatusHistory.create(10L, "PENDING", "APPROVED", "Reason");
        assertEquals(10L, created.getClaimId());
        assertEquals("SYSTEM", created.getChangedBy());

        ClaimStatusHistory full = new ClaimStatusHistory(1L, 10L, "OLD", "NEW", "USER", "REASON", now);
        assertEquals(1L, full.getId());
    }

    @Test
    void testFraudAssessment() {
        FraudAssessment fa = new FraudAssessment();
        assertNotNull(fa);

        LocalDateTime now = LocalDateTime.now();
        fa.setId(1L);
        fa.setClaimId(10L);
        fa.setScore(15);
        fa.setIndicators("IND");
        fa.setDecision("CLEARED");
        fa.setAssessedBy("ENGINE");
        fa.setAssessedAt(now);
        fa.setStatus("COMPLETED");

        assertEquals(1L, fa.getId());
        assertEquals(10L, fa.getClaimId());
        assertEquals(15, fa.getScore());
        assertEquals("IND", fa.getIndicators());
        assertEquals("CLEARED", fa.getDecision());
        assertEquals("ENGINE", fa.getAssessedBy());
        assertEquals(now, fa.getAssessedAt());
        assertEquals("COMPLETED", fa.getStatus());

        FraudAssessment full = new FraudAssessment(1L, 10L, 15, "IND", "DEC", "BY", now, "STATUS");
        assertEquals(1L, full.getId());
    }

    @Test
    void testEventVerification() {
        EventVerification ev = new EventVerification();
        assertNotNull(ev);

        java.time.LocalDate today = java.time.LocalDate.now();
        ev.setId(1L);
        ev.setVerificationId(1L);
        ev.setClaimId(10L);
        ev.setEventType("DEATH_VERIFICATION");
        ev.setVerificationMethod("REGISTRY");
        ev.setVerificationStatus("CONFIRMED");
        ev.setVerifiedBy("OFFICER");
        ev.setVerificationDate(today);
        ev.setRemarks("Verified ok");

        assertEquals(1L, ev.getId());
        assertEquals(1L, ev.getVerificationId());
        assertEquals(10L, ev.getClaimId());
        assertEquals("DEATH_VERIFICATION", ev.getEventType());
        assertEquals("REGISTRY", ev.getVerificationMethod());
        assertEquals("CONFIRMED", ev.getVerificationStatus());
        assertEquals("OFFICER", ev.getVerifiedBy());
        assertEquals(today, ev.getVerificationDate());
        assertEquals("Verified ok", ev.getRemarks());

        EventVerification full = new EventVerification(1L, 10L, "TYPE", "SRC", "STATUS", "BY", today, "NOTES");
        assertEquals(1L, full.getId());
        assertEquals("TYPE", full.getEventType());
    }

    @Test
    void testClaimInformationRequest() {
        ClaimInformationRequest cir = new ClaimInformationRequest();
        assertNotNull(cir);

        LocalDateTime now = LocalDateTime.now();
        cir.setRequestId(1L);
        cir.setClaimId(10L);
        cir.setRequestedBy("OFFICER");
        cir.setRequestText("Need document");
        cir.setStatus("PENDING");
        cir.setDueAt(now.plusDays(7));
        cir.setCreatedAt(now);
        cir.setRespondedAt(now.plusDays(1));

        assertEquals(1L, cir.getId());
        assertEquals(1L, cir.getRequestId());
        assertEquals(10L, cir.getClaimId());
        assertEquals("OFFICER", cir.getRequestedBy());
        assertEquals("Need document", cir.getRequestText());
        assertEquals("PENDING", cir.getStatus());
        assertEquals(now.plusDays(7), cir.getDueAt());
        assertEquals(now, cir.getCreatedAt());
        assertEquals(now.plusDays(1), cir.getRespondedAt());

        ClaimInformationRequest full = new ClaimInformationRequest(1L, 10L, "BY", "TEXT", now, "OPEN", now, now);
        assertEquals(1L, full.getId());
        assertEquals("TEXT", full.getRequestText());
    }

    @Test
    void testDtos() {
        DocumentRequest dr = new DocumentRequest("CERT", "file.pdf", "/ref", "VERIFIED");
        assertEquals("CERT", dr.documentType());
        assertEquals("file.pdf", dr.fileName());
        assertEquals("/ref", dr.fileReference());
        assertEquals("VERIFIED", dr.verificationStatus());

        PolicyDto pol = new PolicyDto(1L, "POL-1", 2L, 3L, 4L, 25000.0, 720.0, "ACTIVE");
        assertEquals(1L, pol.id());
        assertEquals("POL-1", pol.policyNumber());
        assertEquals(2L, pol.quoteId());
        assertEquals(3L, pol.customerId());
        assertEquals(4L, pol.petId());
        assertEquals(25000.0, pol.coverageAmount());
        assertEquals(720.0, pol.monthlyPremium());
        assertEquals("ACTIVE", pol.status());

        PolicyDto polShort = new PolicyDto(1L, 4L, 3L, "ACTIVE", 25000.0, 720.0);
        assertEquals(1L, polShort.id());

        CreateFundDto fund = new CreateFundDto(1L, 2L, 25000.0, 300.0, 4000.0, 2000.0);
        assertEquals(1L, fund.policyId());
        assertEquals(2L, fund.petId());
        assertEquals(25000.0, fund.totalCoverage());
        assertEquals(300.0, fund.monthlyAllowance());
        assertEquals(4000.0, fund.vetReserve());
        assertEquals(2000.0, fund.emergencyReserve());

        DeathVerificationRequest dvr = new DeathVerificationRequest(true, "Registry verified");
        assertTrue(dvr.verified());
        assertEquals("Registry verified", dvr.registryNotes());

        ClaimRequest cr = new ClaimRequest(10L, "John", "FAMILY", "DC-123", "2026-10-10", "Notes");
        assertEquals(10L, cr.policyId());
        assertEquals("John", cr.claimantName());
        assertEquals("FAMILY", cr.relationship());
        assertEquals("DC-123", cr.deathCertificateNo());
        assertEquals("2026-10-10", cr.dateOfDeath());
        assertEquals("Notes", cr.notes());

        LocalDateTime now = LocalDateTime.now();
        ClaimResponse crp = new ClaimResponse(1L, "CLM-1", 10L, "John", "FAMILY", "DC-123", "2026-10-10", "APPROVED", null, "Notes", now, "APPROVED", 5);
        assertEquals(1L, crp.id());
        assertEquals("CLM-1", crp.claimNumber());
        assertEquals(10L, crp.policyId());
        assertEquals("John", crp.claimantName());
        assertEquals("FAMILY", crp.relationship());
        assertEquals("DC-123", crp.deathCertificateNo());
        assertEquals("2026-10-10", crp.dateOfDeath());
        assertEquals("APPROVED", crp.status());
        assertNull(crp.rejectionReason());
        assertEquals("Notes", crp.notes());
        assertEquals(now, crp.createdAt());
        assertEquals("APPROVED", crp.investigationDecision());
        assertEquals(5, crp.fraudScore());
    }
}
