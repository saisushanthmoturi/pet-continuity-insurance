package com.example.paymentfundservice.model;

import com.example.paymentfundservice.config.DatabaseConfig;
import com.example.paymentfundservice.config.WebClientConfig;
import com.example.paymentfundservice.dto.*;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PaymentFundModelAndDtoTest {

    @Test
    void testPetContinuityFund() {
        LocalDateTime now = LocalDateTime.now();
        PetContinuityFund fund = new PetContinuityFund(1L, 10L, 20L, 10000.0, 9500.0, 500.0, 2000.0, 1000.0, "ACTIVE", now, now);
        assertEquals(1L, fund.getId());
        assertEquals(1L, fund.getFundId());
        assertEquals(10L, fund.getPolicyId());
        assertEquals(20L, fund.getPetId());
        assertEquals(10000.0, fund.getTotalAmount());
        assertEquals(10000.0, fund.getTotalFund());
        assertEquals(9500.0, fund.getAvailableAmount());
        assertEquals(9500.0, fund.getCurrentBalance());
        assertEquals(500.0, fund.getMonthlyAllowance());
        assertEquals(2000.0, fund.getVeterinaryReserve());
        assertEquals(2000.0, fund.getVetReserve());
        assertEquals(1000.0, fund.getEmergencyReserve());
        assertEquals("ACTIVE", fund.getStatus());
        assertEquals(now, fund.getCreatedAt());
        assertEquals(now, fund.getUpdatedAt());

        fund.setId(2L);
        assertEquals(2L, fund.getId());
        fund.setFundId(3L);
        assertEquals(3L, fund.getFundId());
        fund.setPolicyId(30L);
        assertEquals(30L, fund.getPolicyId());
        fund.setPetId(40L);
        assertEquals(40L, fund.getPetId());
        fund.setTotalAmount(12000.0);
        assertEquals(12000.0, fund.getTotalAmount());
        fund.setTotalFund(13000.0);
        assertEquals(13000.0, fund.getTotalFund());
        fund.setAvailableAmount(11000.0);
        assertEquals(11000.0, fund.getAvailableAmount());
        fund.setCurrentBalance(10500.0);
        assertEquals(10500.0, fund.getCurrentBalance());
        fund.setMonthlyAllowance(600.0);
        assertEquals(600.0, fund.getMonthlyAllowance());
        fund.setVeterinaryReserve(2500.0);
        assertEquals(2500.0, fund.getVeterinaryReserve());
        fund.setVetReserve(2400.0);
        assertEquals(2400.0, fund.getVetReserve());
        fund.setEmergencyReserve(1200.0);
        assertEquals(1200.0, fund.getEmergencyReserve());
        fund.setStatus("SUSPENDED");
        assertEquals("SUSPENDED", fund.getStatus());
        LocalDateTime next = now.plusDays(1);
        fund.setCreatedAt(next);
        assertEquals(next, fund.getCreatedAt());
        fund.setUpdatedAt(next);
        assertEquals(next, fund.getUpdatedAt());

        // Test create with values
        PetContinuityFund created = PetContinuityFund.create(10L, 20L, 50000.0, 400.0, 5000.0, 3000.0);
        assertEquals(10L, created.getPolicyId());
        assertEquals(20L, created.getPetId());
        assertEquals(50000.0, created.getTotalFund());
        assertEquals(400.0, created.getMonthlyAllowance());
        assertEquals(5000.0, created.getVetReserve());
        assertEquals(3000.0, created.getEmergencyReserve());

        // Test create with defaults
        PetContinuityFund defFund = PetContinuityFund.create(10L, 20L, null, null, null, null);
        assertEquals(25000.0, defFund.getTotalFund());
        assertEquals(300.0, defFund.getMonthlyAllowance());
        assertEquals(4000.0, defFund.getVetReserve());
        assertEquals(2000.0, defFund.getEmergencyReserve());

        // Test constructor with nulls
        PetContinuityFund nullArgFund = new PetContinuityFund(null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("ACTIVE", nullArgFund.getStatus());
        assertNotNull(nullArgFund.getCreatedAt());
        assertNotNull(nullArgFund.getUpdatedAt());
    }

    @Test
    void testPetCareFund() {
        LocalDateTime now = LocalDateTime.now();
        PetCareFund fund = new PetCareFund(1L, 10L, 20L, 10000.0, 9500.0, 500.0, 2000.0, 1000.0, "ACTIVE", now, now);
        assertEquals(1L, fund.getId());
        assertEquals(1L, fund.getFundId());
        assertEquals(10L, fund.getPolicyId());
        assertEquals(20L, fund.getPetId());
        assertEquals(10000.0, fund.getTotalAmount());
        assertEquals(9500.0, fund.getAvailableAmount());
        assertEquals(500.0, fund.getMonthlyAllowance());
        assertEquals(2000.0, fund.getVeterinaryReserve());
        assertEquals(1000.0, fund.getEmergencyReserve());
        assertEquals("ACTIVE", fund.getStatus());
        assertEquals(now, fund.getCreatedAt());
        assertEquals(now, fund.getUpdatedAt());

        fund.setId(2L);
        assertEquals(2L, fund.getId());
        fund.setFundId(3L);
        assertEquals(3L, fund.getFundId());
        fund.setPolicyId(30L);
        assertEquals(30L, fund.getPolicyId());
        fund.setPetId(40L);
        assertEquals(40L, fund.getPetId());
        fund.setTotalAmount(12000.0);
        assertEquals(12000.0, fund.getTotalAmount());
        fund.setAvailableAmount(11000.0);
        assertEquals(11000.0, fund.getAvailableAmount());
        fund.setMonthlyAllowance(600.0);
        assertEquals(600.0, fund.getMonthlyAllowance());
        fund.setVeterinaryReserve(2500.0);
        assertEquals(2500.0, fund.getVeterinaryReserve());
        fund.setEmergencyReserve(1200.0);
        assertEquals(1200.0, fund.getEmergencyReserve());
        fund.setStatus("CLOSED");
        assertEquals("CLOSED", fund.getStatus());
        LocalDateTime next = now.plusDays(1);
        fund.setCreatedAt(next);
        assertEquals(next, fund.getCreatedAt());
        fund.setUpdatedAt(next);
        assertEquals(next, fund.getUpdatedAt());

        PetCareFund def = new PetCareFund();
        assertEquals("ACTIVE", def.getStatus());
        assertNotNull(def.getCreatedAt());
        assertNotNull(def.getUpdatedAt());

        PetCareFund nullArgs = new PetCareFund(null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("ACTIVE", nullArgs.getStatus());
    }

    @Test
    void testDisbursement() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        Disbursement d = new Disbursement(1L, 10L, 20L, 500.0, "MONTHLY_ALLOWANCE", "ELIGIBLE", "PROCESSED", today, now);
        assertEquals(1L, d.getId());
        assertEquals(1L, d.getDisbursementId());
        assertEquals(10L, d.getFundId());
        assertEquals(20L, d.getCaretakerId());
        assertEquals(500.0, d.getAmount());
        assertEquals("MONTHLY_ALLOWANCE", d.getDisbursementType());
        assertEquals("ELIGIBLE", d.getEligibilityStatus());
        assertEquals("PROCESSED", d.getStatus());
        assertEquals(today, d.getScheduledDate());
        assertEquals(now, d.getProcessedAt());

        d.setId(2L);
        assertEquals(2L, d.getId());
        d.setDisbursementId(3L);
        assertEquals(3L, d.getDisbursementId());
        d.setFundId(15L);
        assertEquals(15L, d.getFundId());
        d.setCaretakerId(25L);
        assertEquals(25L, d.getCaretakerId());
        d.setAmount(600.0);
        assertEquals(600.0, d.getAmount());
        d.setDisbursementType("EMERGENCY");
        assertEquals("EMERGENCY", d.getDisbursementType());
        d.setEligibilityStatus("INELIGIBLE");
        assertEquals("INELIGIBLE", d.getEligibilityStatus());
        d.setStatus("FAILED");
        assertEquals("FAILED", d.getStatus());
        d.setScheduledDate(today.plusDays(1));
        assertEquals(today.plusDays(1), d.getScheduledDate());
        d.setProcessedAt(now.plusDays(1));
        assertEquals(now.plusDays(1), d.getProcessedAt());

        Disbursement def = new Disbursement();
        assertEquals("MONTHLY_ALLOWANCE", def.getDisbursementType());
        assertEquals("ELIGIBLE", def.getEligibilityStatus());
        assertEquals("PROCESSED", def.getStatus());

        Disbursement nullArgs = new Disbursement(null, null, null, null, null, null, null, null, null);
        assertEquals("MONTHLY_ALLOWANCE", nullArgs.getDisbursementType());
        assertEquals("ELIGIBLE", nullArgs.getEligibilityStatus());
        assertEquals("PROCESSED", nullArgs.getStatus());
    }

    @Test
    void testPremiumPayment() {
        LocalDateTime now = LocalDateTime.now();
        PremiumPayment p = new PremiumPayment(1L, 10L, 20L, 45.0, "PAY-123", "CARD", "SUCCESS", now, null);
        assertEquals(1L, p.getId());
        assertEquals(1L, p.getPaymentId());
        assertEquals(10L, p.getPolicyId());
        assertEquals(20L, p.getCustomerId());
        assertEquals(45.0, p.getAmount());
        assertEquals("PAY-123", p.getPaymentReference());
        assertEquals("PAY-123", p.getTransactionReference());
        assertEquals("CARD", p.getPaymentMethod());
        assertEquals("SUCCESS", p.getStatus());
        assertEquals(now, p.getPaymentDate());
        assertEquals(now, p.getCreatedAt());
        assertNull(p.getFailureReason());

        p.setId(2L);
        assertEquals(2L, p.getId());
        p.setPaymentId(3L);
        assertEquals(3L, p.getPaymentId());
        p.setPolicyId(15L);
        assertEquals(15L, p.getPolicyId());
        p.setCustomerId(25L);
        assertEquals(25L, p.getCustomerId());
        p.setAmount(60.0);
        assertEquals(60.0, p.getAmount());
        p.setPaymentReference("PAY-456");
        assertEquals("PAY-456", p.getPaymentReference());
        p.setTransactionReference("PAY-789");
        assertEquals("PAY-789", p.getTransactionReference());
        p.setPaymentMethod("NETBANKING");
        assertEquals("NETBANKING", p.getPaymentMethod());
        p.setStatus("FAILED");
        assertEquals("FAILED", p.getStatus());
        p.setPaymentDate(now.plusDays(1));
        assertEquals(now.plusDays(1), p.getPaymentDate());
        p.setCreatedAt(now.plusDays(2));
        assertEquals(now.plusDays(2), p.getCreatedAt());
        p.setFailureReason("Insufficient funds");
        assertEquals("Insufficient funds", p.getFailureReason());

        PremiumPayment created = PremiumPayment.create(10L, 50.0, "CARD", "SUCCESS");
        assertEquals(10L, created.getPolicyId());
        assertEquals(50.0, created.getAmount());
        assertTrue(created.getPaymentReference().startsWith("PAY-"));

        PremiumPayment def = new PremiumPayment();
        assertNotNull(def.getPaymentDate());

        PremiumPayment nullArgs = new PremiumPayment(null, null, null, null, null, null, null, null, "reason");
        assertNotNull(nullArgs.getPaymentDate());
        assertEquals("reason", nullArgs.getFailureReason());
    }

    @Test
    void testExpense() {
        LocalDateTime now = LocalDateTime.now();
        Expense exp = new Expense(1L, 10L, 20L, "VET", 300.0, "PetClinic", "DOC-1", "APPROVED", now, now);
        assertEquals(1L, exp.getId());
        assertEquals(1L, exp.getExpenseId());
        assertEquals(10L, exp.getFundId());
        assertEquals(20L, exp.getCaretakerId());
        assertEquals("VET", exp.getExpenseType());
        assertEquals(300.0, exp.getAmount());
        assertEquals("PetClinic", exp.getVendorName());
        assertEquals("DOC-1", exp.getDocumentReference());
        assertEquals("APPROVED", exp.getApprovalStatus());
        assertEquals(now, exp.getSubmittedAt());
        assertEquals(now, exp.getApprovedAt());

        exp.setId(2L);
        assertEquals(2L, exp.getId());
        exp.setExpenseId(3L);
        assertEquals(3L, exp.getExpenseId());
        exp.setFundId(15L);
        assertEquals(15L, exp.getFundId());
        exp.setCaretakerId(25L);
        assertEquals(25L, exp.getCaretakerId());
        exp.setExpenseType("MEDICINE");
        assertEquals("MEDICINE", exp.getExpenseType());
        exp.setAmount(400.0);
        assertEquals(400.0, exp.getAmount());
        exp.setVendorName("Pharmacy");
        assertEquals("Pharmacy", exp.getVendorName());
        exp.setDocumentReference("DOC-2");
        assertEquals("DOC-2", exp.getDocumentReference());
        exp.setApprovalStatus("PENDING");
        assertEquals("PENDING", exp.getApprovalStatus());
        exp.setSubmittedAt(now.plusDays(1));
        assertEquals(now.plusDays(1), exp.getSubmittedAt());
        exp.setApprovedAt(now.plusDays(2));
        assertEquals(now.plusDays(2), exp.getApprovedAt());

        Expense def = new Expense();
        assertEquals("APPROVED", def.getApprovalStatus());
        assertNotNull(def.getSubmittedAt());

        Expense nullArgs = new Expense(null, null, null, null, null, null, null, null, null, null);
        assertEquals("APPROVED", nullArgs.getApprovalStatus());
        assertNotNull(nullArgs.getSubmittedAt());
    }

    @Test
    void testFundStatusHistory() {
        LocalDateTime now = LocalDateTime.now();
        FundStatusHistory fsh = new FundStatusHistory(1L, 10L, "ACTIVE", "SUSPENDED", "ADMIN", "Audit", now);
        assertEquals(1L, fsh.getHistoryId());
        assertEquals(10L, fsh.getFundId());
        assertEquals("ACTIVE", fsh.getOldStatus());
        assertEquals("SUSPENDED", fsh.getNewStatus());
        assertEquals("ADMIN", fsh.getChangedBy());
        assertEquals("Audit", fsh.getReason());
        assertEquals(now, fsh.getChangedAt());

        fsh.setHistoryId(2L);
        assertEquals(2L, fsh.getHistoryId());
        fsh.setFundId(15L);
        assertEquals(15L, fsh.getFundId());
        fsh.setOldStatus("SUSPENDED");
        assertEquals("SUSPENDED", fsh.getOldStatus());
        fsh.setNewStatus("ACTIVE");
        assertEquals("ACTIVE", fsh.getNewStatus());
        fsh.setChangedBy("USER");
        assertEquals("USER", fsh.getChangedBy());
        fsh.setReason("Reinstated");
        assertEquals("Reinstated", fsh.getReason());
        fsh.setChangedAt(now.plusDays(1));
        assertEquals(now.plusDays(1), fsh.getChangedAt());

        FundStatusHistory def = new FundStatusHistory();
        assertNotNull(def.getChangedAt());

        FundStatusHistory nullArgs = new FundStatusHistory(null, null, null, null, null, null, null);
        assertNotNull(nullArgs.getChangedAt());
    }

    @Test
    void testFundTransaction() {
        LocalDateTime now = LocalDateTime.now();
        FundTransaction txn = new FundTransaction(1L, 10L, "DISBURSEMENT", 500.0, 9500.0, "TXN-1", "Desc", "SUCCESS", now);
        assertEquals(1L, txn.getId());
        assertEquals(1L, txn.getTransactionId());
        assertEquals(10L, txn.getFundId());
        assertEquals("DISBURSEMENT", txn.getTransactionType());
        assertEquals(500.0, txn.getAmount());
        assertEquals(9500.0, txn.getBalanceAfter());
        assertEquals("TXN-1", txn.getReferenceId());
        assertEquals("Desc", txn.getDescription());
        assertEquals("SUCCESS", txn.getStatus());
        assertEquals(now, txn.getCreatedAt());

        txn.setId(2L);
        assertEquals(2L, txn.getId());
        txn.setTransactionId(3L);
        assertEquals(3L, txn.getTransactionId());
        txn.setFundId(15L);
        assertEquals(15L, txn.getFundId());
        txn.setTransactionType("DEPOSIT");
        assertEquals("DEPOSIT", txn.getTransactionType());
        txn.setAmount(1000.0);
        assertEquals(1000.0, txn.getAmount());
        txn.setBalanceAfter(10500.0);
        assertEquals(10500.0, txn.getBalanceAfter());
        txn.setReferenceId("TXN-2");
        assertEquals("TXN-2", txn.getReferenceId());
        txn.setDescription("New deposit");
        assertEquals("New deposit", txn.getDescription());
        txn.setStatus("FAILED");
        assertEquals("FAILED", txn.getStatus());
        txn.setCreatedAt(now.plusDays(1));
        assertEquals(now.plusDays(1), txn.getCreatedAt());

        FundTransaction created = FundTransaction.create(10L, "DEPOSIT", 5000.0, 5000.0, "Init", "SUCCESS");
        assertEquals(10L, created.getFundId());
        assertEquals("DEPOSIT", created.getTransactionType());
        assertTrue(created.getReferenceId().startsWith("TXN-"));

        FundTransaction def = new FundTransaction();
        assertEquals("SUCCESS", def.getStatus());
        assertNotNull(def.getCreatedAt());

        FundTransaction nullArgs = new FundTransaction(null, null, null, null, null, null, null, null, null);
        assertEquals("SUCCESS", nullArgs.getStatus());
        assertNotNull(nullArgs.getCreatedAt());
    }

    @Test
    void testDtos() {
        CreateFundRequest cfr = new CreateFundRequest(10L, 20L, 25000.0, 300.0, 4000.0, 2000.0);
        assertEquals(10L, cfr.policyId());
        assertEquals(20L, cfr.petId());
        assertEquals(25000.0, cfr.totalCoverage());
        assertEquals(300.0, cfr.monthlyAllowance());
        assertEquals(4000.0, cfr.vetReserve());
        assertEquals(2000.0, cfr.emergencyReserve());

        ExpenseRequest er = new ExpenseRequest("VET", 200.0, "Medicine");
        assertEquals("VET", er.transactionType());
        assertEquals(200.0, er.amount());
        assertEquals("Medicine", er.description());

        EligibilityResponse elr = new EligibilityResponse(true, "Passed", 5L);
        assertTrue(elr.eligible());
        assertEquals("Passed", elr.reason());
        assertEquals(5L, elr.activeCaretakerId());

        PolicyDto pd = new PolicyDto(1L, "POL-1", 100L, 10L, 20L, 25000.0, 60.0, "ACTIVE");
        assertEquals("POL-1", pd.policyNumber());
        assertEquals(100L, pd.quoteId());
        assertEquals(1L, pd.id());
        assertEquals(10L, pd.customerId());
        assertEquals(20L, pd.petId());
        assertEquals("ACTIVE", pd.status());
        assertEquals(25000.0, pd.coverageAmount());
        assertEquals(60.0, pd.monthlyPremium());

        PaymentRequest pr = new PaymentRequest(10L, 5L, 60.0, "CARD", false);
        assertEquals(10L, pr.policyId());
        assertEquals(5L, pr.customerId());
        assertEquals(60.0, pr.amount());
        assertEquals("CARD", pr.paymentMethod());
        assertFalse(pr.simulateFailure());

        PaymentRequest pr2 = new PaymentRequest(10L, 60.0, "CARD", false);
        assertEquals(10L, pr2.policyId());
        assertNull(pr2.customerId());
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
