package com.example.policyservice.model;

import com.example.policyservice.dto.PolicyResponse;
import com.example.policyservice.dto.QuoteDto;
import com.example.policyservice.dto.StatusUpdateRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PolicyModelAndDtoTest {

    @Test
    void testPolicy() {
        Policy p = new Policy();
        assertNotNull(p);
        assertEquals(250.0, p.getDeductible());
        assertEquals("PENDING_PAYMENT", p.getStatus());

        LocalDateTime now = LocalDateTime.now();
        p.setId(10L);
        p.setPolicyId(10L);
        p.setPolicyNumber("POL-10");
        p.setQuoteId(5L);
        p.setCustomerId(20L);
        p.setPetId(30L);
        p.setCoverageAmount(15000.0);
        p.setPremiumAmount(50.0);
        p.setMonthlyPremium(50.0);
        p.setDeductible(200.0);
        p.setStartDate("2026-01-01");
        p.setEndDate("2027-01-01");
        p.setStatus("ACTIVE");
        p.setIssuedBy("AGENT");
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        assertEquals(10L, p.getId());
        assertEquals(10L, p.getPolicyId());
        assertEquals("POL-10", p.getPolicyNumber());
        assertEquals(5L, p.getQuoteId());
        assertEquals(20L, p.getCustomerId());
        assertEquals(30L, p.getPetId());
        assertEquals(15000.0, p.getCoverageAmount());
        assertEquals(50.0, p.getPremiumAmount());
        assertEquals(50.0, p.getMonthlyPremium());
        assertEquals(200.0, p.getDeductible());
        assertEquals("2026-01-01", p.getStartDate());
        assertEquals("2027-01-01", p.getEndDate());
        assertEquals("ACTIVE", p.getStatus());
        assertEquals("AGENT", p.getIssuedBy());
        assertEquals(now, p.getCreatedAt());
        assertEquals(now, p.getUpdatedAt());

        Policy full = new Policy(1L, "NUM", 2L, 3L, 4L, 10000.0, 40.0, 250.0,
                "2026-01-01", "2027-01-01", "ACTIVE", "SYS", now, now);
        assertEquals(1L, full.getId());

        Policy fromQuote = Policy.createFromQuote(2L, 3L, 4L, 10000.0, 40.0);
        assertNotNull(fromQuote);
        assertEquals(2L, fromQuote.getQuoteId());
    }

    @Test
    void testCoverage() {
        Coverage c = new Coverage();
        assertNotNull(c);
        assertEquals("ACTIVE", c.getStatus());
        assertEquals(0.0, c.getDeductible());

        c.setId(1L);
        c.setCoverageId(1L);
        c.setPolicyId(10L);
        c.setCoverageType("PET_CONTINUITY");
        c.setCoverageAmount(20000.0);
        c.setLimitAmount(20000.0);
        c.setDeductible(100.0);
        c.setStatus("INACTIVE");

        assertEquals(1L, c.getId());
        assertEquals(1L, c.getCoverageId());
        assertEquals(10L, c.getPolicyId());
        assertEquals("PET_CONTINUITY", c.getCoverageType());
        assertEquals(20000.0, c.getCoverageAmount());
        assertEquals(20000.0, c.getLimitAmount());
        assertEquals(100.0, c.getDeductible());
        assertEquals("INACTIVE", c.getStatus());

        Coverage full = new Coverage(2L, 10L, "TYPE", 1000.0, 1000.0, 50.0, "ACTIVE");
        assertEquals(2L, full.getId());
    }

    @Test
    void testPolicyStatusHistory() {
        PolicyStatusHistory h = new PolicyStatusHistory();
        assertNotNull(h);

        LocalDateTime now = LocalDateTime.now();
        h.setId(1L);
        h.setHistoryId(1L);
        h.setPolicyId(10L);
        h.setOldStatus("PENDING");
        h.setPreviousStatus("PENDING");
        h.setNewStatus("ACTIVE");
        h.setChangedBy("ADMIN");
        h.setReason("Paid");
        h.setChangedAt(now);

        assertEquals(1L, h.getId());
        assertEquals(1L, h.getHistoryId());
        assertEquals(10L, h.getPolicyId());
        assertEquals("PENDING", h.getOldStatus());
        assertEquals("PENDING", h.getPreviousStatus());
        assertEquals("ACTIVE", h.getNewStatus());
        assertEquals("ADMIN", h.getChangedBy());
        assertEquals("Paid", h.getReason());
        assertEquals(now, h.getChangedAt());

        PolicyStatusHistory created = PolicyStatusHistory.create(10L, "NONE", "ACTIVE", "Init");
        assertEquals(10L, created.getPolicyId());

        PolicyStatusHistory full = new PolicyStatusHistory(2L, 10L, "OLD", "NEW", "BY", "REASON", now);
        assertEquals(2L, full.getId());
    }

    @Test
    void testDtos() {
        StatusUpdateRequest sur = new StatusUpdateRequest("ACTIVE", "Payment confirmed");
        assertEquals("ACTIVE", sur.status());
        assertEquals("Payment confirmed", sur.reason());

        LocalDateTime now = LocalDateTime.now();
        PolicyResponse pr = new PolicyResponse(1L, "POL-1", 2L, 3L, 4L, 25000.0, 720.0, "ACTIVE", "2026-01-01", "2027-01-01", now);
        assertEquals(1L, pr.id());
        assertEquals("POL-1", pr.policyNumber());
        assertEquals(2L, pr.quoteId());
        assertEquals(3L, pr.customerId());
        assertEquals(4L, pr.petId());
        assertEquals(25000.0, pr.coverageAmount());
        assertEquals(720.0, pr.monthlyPremium());
        assertEquals("ACTIVE", pr.status());
        assertEquals("2026-01-01", pr.startDate());
        assertEquals("2027-01-01", pr.endDate());
        assertEquals(now, pr.createdAt());

        QuoteDto quote = new QuoteDto(1L, 2L, 3L, 25000.0, 720.0, 5, "APPROVED", now);
        assertEquals(1L, quote.id());
        assertEquals(2L, quote.customerId());
        assertEquals(3L, quote.petId());
        assertEquals(25000.0, quote.requestedCoverage());
        assertEquals(720.0, quote.monthlyPremium());
        assertEquals(5, quote.riskScore());
        assertEquals("APPROVED", quote.decision());
        assertEquals(now, quote.validUntil());
    }
}
