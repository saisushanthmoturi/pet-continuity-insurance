package com.example.careverificationservice.model;

import com.example.careverificationservice.config.DatabaseConfig;
import com.example.careverificationservice.dto.*;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CareModelAndDtoTest {

    @Test
    void testCarePlan() {
        LocalDateTime now = LocalDateTime.now();
        CarePlan cp = new CarePlan(1L, 20L, "Feed", "Med", "Vet", "Routine", "Special", "ACTIVE", now, now);
        assertEquals(1L, cp.getId());
        assertEquals(1L, cp.getCarePlanId());
        assertEquals(20L, cp.getPetId());
        assertEquals("Feed", cp.getFeedingInstructions());
        assertEquals("Med", cp.getMedicationInstructions());
        assertEquals("Vet", cp.getVetDetails());
        assertEquals("Vet", cp.getVetContact());
        assertEquals("Routine", cp.getRoutineDetails());
        assertEquals("Special", cp.getSpecialRequirements());
        assertEquals("Special", cp.getSpecialNeeds());
        assertEquals("ACTIVE", cp.getStatus());
        assertEquals(now, cp.getCreatedAt());
        assertEquals(now, cp.getUpdatedAt());

        cp.setId(2L);
        assertEquals(2L, cp.getId());
        cp.setCarePlanId(3L);
        assertEquals(3L, cp.getCarePlanId());
        cp.setPetId(25L);
        assertEquals(25L, cp.getPetId());
        cp.setPrimaryCaretakerId(7L);
        assertEquals(7L, cp.getPrimaryCaretakerId());
        cp.setBackupCaretakerId(8L);
        assertEquals(8L, cp.getBackupCaretakerId());
        cp.setVetDetails("Vet2");
        assertEquals("Vet2", cp.getVetDetails());
        cp.setVetContact("Vet3");
        assertEquals("Vet3", cp.getVetContact());
        cp.setFeedingInstructions("Feed2");
        assertEquals("Feed2", cp.getFeedingInstructions());
        cp.setMedicationInstructions("Med2");
        assertEquals("Med2", cp.getMedicationInstructions());
        cp.setRoutineDetails("Routine2");
        assertEquals("Routine2", cp.getRoutineDetails());
        cp.setSpecialRequirements("Special2");
        assertEquals("Special2", cp.getSpecialRequirements());
        cp.setSpecialNeeds("Special3");
        assertEquals("Special3", cp.getSpecialNeeds());
        cp.setStatus("INACTIVE");
        assertEquals("INACTIVE", cp.getStatus());
        LocalDateTime next = now.plusDays(1);
        cp.setCreatedAt(next);
        assertEquals(next, cp.getCreatedAt());
        cp.setUpdatedAt(next);
        assertEquals(next, cp.getUpdatedAt());

        CarePlan def = new CarePlan();
        assertNull(def.getId());
        assertEquals("ACTIVE", def.getStatus());

        CarePlan created = CarePlan.create(20L, 5L, 6L, "Vet", "Feed", "None");
        assertEquals(20L, created.getPetId());
        assertEquals(5L, created.getPrimaryCaretakerId());
        assertEquals(6L, created.getBackupCaretakerId());

        CarePlan nullArgs = new CarePlan(null, null, null, null, null, null, null, null, null, null);
        assertNotNull(nullArgs.getCreatedAt());
        assertEquals("ACTIVE", nullArgs.getStatus());
    }

    @Test
    void testCaretaker() {
        LocalDateTime now = LocalDateTime.now();
        Caretaker c = new Caretaker(1L, 20L, "John Doe", "1234567890", "john@example.com", "PRIMARY", "Address", 1, "VERIFIED", "ACTIVE", now, now);
        assertEquals(1L, c.getId());
        assertEquals(1L, c.getCaretakerId());
        assertEquals(20L, c.getPetId());
        assertEquals("John Doe", c.getFullName());
        assertEquals("John Doe", c.getName());
        assertEquals("john@example.com", c.getEmail());
        assertEquals("1234567890", c.getPhone());
        assertEquals("Address", c.getAddress());
        assertEquals("PRIMARY", c.getRelationship());
        assertEquals("PRIMARY", c.getCaretakerType());
        assertEquals(1, c.getPriority());
        assertEquals("VERIFIED", c.getVerificationStatus());
        assertEquals("ACTIVE", c.getAvailabilityStatus());
        assertEquals("ACTIVE", c.getStatus());
        assertEquals(now, c.getCreatedAt());
        assertEquals(now, c.getUpdatedAt());

        c.setId(2L);
        assertEquals(2L, c.getId());
        c.setCaretakerId(3L);
        assertEquals(3L, c.getCaretakerId());
        c.setCustomerId(100L);
        assertEquals(100L, c.getCustomerId());
        c.setPetId(25L);
        assertEquals(25L, c.getPetId());
        c.setFullName("Jane Doe");
        assertEquals("Jane Doe", c.getFullName());
        c.setName("Jane Doe 2");
        assertEquals("Jane Doe 2", c.getName());
        c.setEmail("jane@example.com");
        assertEquals("jane@example.com", c.getEmail());
        c.setPhone("0987654321");
        assertEquals("0987654321", c.getPhone());
        c.setAddress("Address2");
        assertEquals("Address2", c.getAddress());
        c.setRelationship("BACKUP");
        assertEquals("BACKUP", c.getRelationship());
        c.setCaretakerType("SECONDARY");
        assertEquals("SECONDARY", c.getCaretakerType());
        c.setPriority(2);
        assertEquals(2, c.getPriority());
        c.setVerificationStatus("PENDING");
        assertEquals("PENDING", c.getVerificationStatus());
        c.setAvailabilityStatus("UNAVAILABLE");
        assertEquals("UNAVAILABLE", c.getAvailabilityStatus());
        c.setStatus("INACTIVE");
        assertEquals("INACTIVE", c.getStatus());
        LocalDateTime next = now.plusDays(1);
        c.setCreatedAt(next);
        assertEquals(next, c.getCreatedAt());
        c.setUpdatedAt(next);
        assertEquals(next, c.getUpdatedAt());

        Caretaker def = new Caretaker();
        assertEquals("ACTIVE", def.getStatus());

        Caretaker created = Caretaker.create(10L, 20L, "Alice", "111", "alice@example.com", "PRIMARY", "Addr");
        assertEquals("Alice", created.getFullName());

        Caretaker nullArgs = new Caretaker(null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("PRIMARY", nullArgs.getCaretakerType());
        assertEquals("ACTIVE", nullArgs.getStatus());
    }

    @Test
    void testPetVerification() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        PetVerification pv = new PetVerification(1L, 20L, 5L, "MOBILE", "PASSED", "REF-1", now, today.plusMonths(1), "Good");
        assertEquals(1L, pv.getId());
        assertEquals(1L, pv.getPetVerificationId());
        assertEquals(20L, pv.getPetId());
        assertEquals(5L, pv.getCaretakerId());
        assertEquals("MOBILE", pv.getVerificationMethod());
        assertEquals("PASSED", pv.getVerificationStatus());
        assertEquals("PASSED", pv.getStatus());
        assertEquals("REF-1", pv.getEvidenceReference());
        assertEquals(now, pv.getVerifiedAt());
        assertEquals(now, pv.getCreatedAt());
        assertEquals(now.toLocalDate().toString(), pv.getVerificationDate());
        assertEquals(today.plusMonths(1), pv.getNextVerificationDate());
        assertEquals("Good", pv.getRemarks());
        assertEquals("Good", pv.getNotes());

        pv.setId(2L);
        assertEquals(2L, pv.getId());
        pv.setPetVerificationId(3L);
        assertEquals(3L, pv.getPetVerificationId());
        pv.setPetId(25L);
        assertEquals(25L, pv.getPetId());
        pv.setCaretakerId(6L);
        assertEquals(6L, pv.getCaretakerId());
        pv.setVerificationMethod("IN_PERSON");
        assertEquals("IN_PERSON", pv.getVerificationMethod());
        pv.setVerificationStatus("FAILED");
        assertEquals("FAILED", pv.getVerificationStatus());
        pv.setStatus("PENDING");
        assertEquals("PENDING", pv.getStatus());
        pv.setEvidenceReference("REF-2");
        assertEquals("REF-2", pv.getEvidenceReference());
        LocalDateTime next = now.plusDays(1);
        pv.setVerifiedAt(next);
        assertEquals(next, pv.getVerifiedAt());
        pv.setCreatedAt(next);
        assertEquals(next, pv.getCreatedAt());
        pv.setVerificationDate("2026-10-10");
        pv.setNextVerificationDate(today.plusMonths(2));
        assertEquals(today.plusMonths(2), pv.getNextVerificationDate());
        pv.setRemarks("Bad");
        assertEquals("Bad", pv.getRemarks());
        pv.setNotes("Updated notes");
        assertEquals("Updated notes", pv.getNotes());

        PetVerification def = new PetVerification();
        assertEquals("PASSED", def.getVerificationStatus());

        PetVerification created = PetVerification.create(20L, 5L, "2026-10-10", "PASSED", "Healthy");
        assertEquals(20L, created.getPetId());
        assertEquals("PASSED", created.getVerificationStatus());

        PetVerification nullArgs = new PetVerification(null, null, null, null, null, null, null, null, null);
        assertEquals("MOBILE_CHECKIN", nullArgs.getVerificationMethod());
        assertEquals("PASSED", nullArgs.getVerificationStatus());
    }

    @Test
    void testCaretakerVerification() {
        LocalDateTime now = LocalDateTime.now();
        CaretakerVerification cv = new CaretakerVerification(1L, 5L, "IDENTITY", "DOCUMENT", "VERIFIED", "REF123", "Admin", now, now.plusYears(1), null, now);
        assertEquals(1L, cv.getVerificationId());
        assertEquals(5L, cv.getCaretakerId());
        assertEquals("IDENTITY", cv.getVerificationType());
        assertEquals("DOCUMENT", cv.getVerificationMethod());
        assertEquals("VERIFIED", cv.getVerificationStatus());
        assertEquals("REF123", cv.getEvidenceReference());
        assertEquals("Admin", cv.getVerificationBy());
        assertEquals(now, cv.getVerifiedAt());
        assertEquals(now.plusYears(1), cv.getExpiresAt());
        assertNull(cv.getFailureReason());
        assertEquals(now, cv.getCreatedAt());

        cv.setVerificationId(2L);
        assertEquals(2L, cv.getVerificationId());
        cv.setCaretakerId(6L);
        assertEquals(6L, cv.getCaretakerId());
        cv.setVerificationType("CRIMINAL");
        assertEquals("CRIMINAL", cv.getVerificationType());
        cv.setVerificationMethod("MANUAL");
        assertEquals("MANUAL", cv.getVerificationMethod());
        cv.setVerificationStatus("REJECTED");
        assertEquals("REJECTED", cv.getVerificationStatus());
        cv.setEvidenceReference("REF456");
        assertEquals("REF456", cv.getEvidenceReference());
        cv.setVerificationBy("Supervisor");
        assertEquals("Supervisor", cv.getVerificationBy());
        LocalDateTime next = now.plusDays(1);
        cv.setVerifiedAt(next);
        assertEquals(next, cv.getVerifiedAt());
        cv.setExpiresAt(next.plusYears(1));
        assertEquals(next.plusYears(1), cv.getExpiresAt());
        cv.setFailureReason("Failed check");
        assertEquals("Failed check", cv.getFailureReason());
        cv.setCreatedAt(next);
        assertEquals(next, cv.getCreatedAt());

        CaretakerVerification def = new CaretakerVerification();
        assertNull(def.getVerificationId());
        assertNotNull(def.getCreatedAt());
        assertNotNull(def.getVerifiedAt());

        CaretakerVerification nullArgs = new CaretakerVerification(null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("IDENTITY", nullArgs.getVerificationType());
        assertEquals("DOCUMENT", nullArgs.getVerificationMethod());
        assertEquals("VERIFIED", nullArgs.getVerificationStatus());
    }

    @Test
    void testCareTransfer() {
        LocalDateTime now = LocalDateTime.now();
        CareTransfer ct = new CareTransfer(1L, 20L, 5L, 6L, "Illness", now, "Admin", "COMPLETED", now.plusHours(1));
        assertEquals(1L, ct.getTransferId());
        assertEquals(20L, ct.getPetId());
        assertEquals(5L, ct.getFromAssignmentId());
        assertEquals(6L, ct.getToAssignmentId());
        assertEquals("Illness", ct.getReason());
        assertEquals(now, ct.getEffectiveAt());
        assertEquals("Admin", ct.getInitiatedBy());
        assertEquals("COMPLETED", ct.getStatus());
        assertEquals(now.plusHours(1), ct.getCreatedAt());

        ct.setTransferId(2L);
        assertEquals(2L, ct.getTransferId());
        ct.setPetId(25L);
        assertEquals(25L, ct.getPetId());
        ct.setFromAssignmentId(7L);
        assertEquals(7L, ct.getFromAssignmentId());
        ct.setToAssignmentId(8L);
        assertEquals(8L, ct.getToAssignmentId());
        ct.setReason("Relocation");
        assertEquals("Relocation", ct.getReason());
        LocalDateTime next = now.plusDays(1);
        ct.setEffectiveAt(next);
        assertEquals(next, ct.getEffectiveAt());
        ct.setInitiatedBy("User");
        assertEquals("User", ct.getInitiatedBy());
        ct.setStatus("PENDING");
        assertEquals("PENDING", ct.getStatus());
        ct.setCreatedAt(next.plusHours(2));
        assertEquals(next.plusHours(2), ct.getCreatedAt());

        CareTransfer def = new CareTransfer();
        assertEquals("COMPLETED", def.getStatus());

        CareTransfer nullArgs = new CareTransfer(null, null, null, null, null, null, null, null, null);
        assertEquals("COMPLETED", nullArgs.getStatus());
    }

    @Test
    void testMonthlyEligibleCheck() {
        LocalDateTime now = LocalDateTime.now();
        MonthlyEligibleCheck mec = new MonthlyEligibleCheck(1L, 20L, 2L, 100L, "2026-10", true, true, true, true, true, null, now, "CORR-1");
        assertEquals(1L, mec.getEligibilityCheckId());
        assertEquals(20L, mec.getPetId());
        assertEquals(2L, mec.getAssignmentId());
        assertEquals(100L, mec.getFundId());
        assertEquals("2026-10", mec.getEligibilityMonth());
        assertTrue(mec.getCaretakerVerified());
        assertTrue(mec.getPetVerified());
        assertTrue(mec.getPolicyActive());
        assertTrue(mec.getFundActive());
        assertTrue(mec.getEligible());
        assertNull(mec.getFailureReason());
        assertEquals(now, mec.getEvaluatedAt());
        assertEquals("CORR-1", mec.getCorrelationId());

        mec.setEligibilityCheckId(2L);
        assertEquals(2L, mec.getEligibilityCheckId());
        mec.setPetId(25L);
        assertEquals(25L, mec.getPetId());
        mec.setAssignmentId(3L);
        assertEquals(3L, mec.getAssignmentId());
        mec.setFundId(101L);
        assertEquals(101L, mec.getFundId());
        mec.setEligibilityMonth("2026-11");
        assertEquals("2026-11", mec.getEligibilityMonth());
        mec.setCaretakerVerified(false);
        assertFalse(mec.getCaretakerVerified());
        mec.setPetVerified(false);
        assertFalse(mec.getPetVerified());
        mec.setPolicyActive(false);
        assertFalse(mec.getPolicyActive());
        mec.setFundActive(false);
        assertFalse(mec.getFundActive());
        mec.setEligible(false);
        assertFalse(mec.getEligible());
        mec.setFailureReason("Inactive");
        assertEquals("Inactive", mec.getFailureReason());
        LocalDateTime next = now.plusDays(1);
        mec.setEvaluatedAt(next);
        assertEquals(next, mec.getEvaluatedAt());
        mec.setCorrelationId("CORR-2");
        assertEquals("CORR-2", mec.getCorrelationId());

        MonthlyEligibleCheck def = new MonthlyEligibleCheck();
        assertNull(def.getEligibilityCheckId());

        MonthlyEligibleCheck nullArgs = new MonthlyEligibleCheck(null, null, null, null, null, null, null, null, null, null, "fail", null, null);
        assertEquals("fail", nullArgs.getFailureReason());
    }

    @Test
    void testPetCaretakerAssignment() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        PetCaretakerAssignment pca = new PetCaretakerAssignment(1L, 20L, 5L, 1, "PRIMARY", "Family", "AVAILABLE", "ACTIVE", 10L, today, today.plusYears(1), now, now, 1);
        assertEquals(1L, pca.getAssignmentId());
        assertEquals(20L, pca.getPetId());
        assertEquals(5L, pca.getCaretakerId());
        assertEquals(1, pca.getPriority());
        assertEquals("PRIMARY", pca.getCaretakerType());
        assertEquals("Family", pca.getRelationship());
        assertEquals("AVAILABLE", pca.getAvailabilityStatus());
        assertEquals("ACTIVE", pca.getAssignmentStatus());
        assertEquals(10L, pca.getNominatedBy());
        assertEquals(today, pca.getEffectiveFrom());
        assertEquals(today.plusYears(1), pca.getEffectiveTo());
        assertEquals(now, pca.getCreatedAt());
        assertEquals(now, pca.getUpdatedAt());
        assertEquals(1, pca.getVersion());

        pca.setAssignmentId(2L);
        assertEquals(2L, pca.getAssignmentId());
        pca.setPetId(25L);
        assertEquals(25L, pca.getPetId());
        pca.setCaretakerId(6L);
        assertEquals(6L, pca.getCaretakerId());
        pca.setPriority(2);
        assertEquals(2, pca.getPriority());
        pca.setCaretakerType("BACKUP");
        assertEquals("BACKUP", pca.getCaretakerType());
        pca.setRelationship("Friend");
        assertEquals("Friend", pca.getRelationship());
        pca.setAvailabilityStatus("BUSY");
        assertEquals("BUSY", pca.getAvailabilityStatus());
        pca.setAssignmentStatus("INACTIVE");
        assertEquals("INACTIVE", pca.getAssignmentStatus());
        pca.setNominatedBy(11L);
        assertEquals(11L, pca.getNominatedBy());
        pca.setEffectiveFrom(today.plusDays(1));
        assertEquals(today.plusDays(1), pca.getEffectiveFrom());
        pca.setEffectiveTo(today.plusDays(30));
        assertEquals(today.plusDays(30), pca.getEffectiveTo());
        LocalDateTime next = now.plusDays(1);
        pca.setCreatedAt(next);
        assertEquals(next, pca.getCreatedAt());
        pca.setUpdatedAt(next);
        assertEquals(next, pca.getUpdatedAt());
        pca.setVersion(2);
        assertEquals(2, pca.getVersion());

        PetCaretakerAssignment def = new PetCaretakerAssignment();
        assertEquals(1, def.getVersion());

        PetCaretakerAssignment nullArgs = new PetCaretakerAssignment(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("PRIMARY", nullArgs.getCaretakerType());
        assertEquals("ACTIVE", nullArgs.getAssignmentStatus());
    }

    @Test
    void testVerificationHistory() {
        LocalDateTime now = LocalDateTime.now();
        VerificationHistory vh = new VerificationHistory(1L, 20L, 5L, "ROUTINE", "PENDING", "PASSED", "MOBILE", "Admin", now, "All good");
        assertEquals(1L, vh.getId());
        assertEquals(1L, vh.getHistoryId());
        assertEquals(20L, vh.getPetId());
        assertEquals(5L, vh.getCaretakerId());
        assertEquals("ROUTINE", vh.getVerificationType());
        assertEquals("PENDING", vh.getOldStatus());
        assertEquals("PASSED", vh.getNewStatus());
        assertEquals("MOBILE", vh.getVerificationMethod());
        assertEquals("Admin", vh.getVerifiedBy());
        assertEquals(now, vh.getVerifiedAt());
        assertEquals("All good", vh.getRemarks());

        vh.setId(2L);
        assertEquals(2L, vh.getId());
        vh.setHistoryId(3L);
        assertEquals(3L, vh.getHistoryId());
        vh.setPetId(25L);
        assertEquals(25L, vh.getPetId());
        vh.setCaretakerId(6L);
        assertEquals(6L, vh.getCaretakerId());
        vh.setVerificationType("SPECIAL");
        assertEquals("SPECIAL", vh.getVerificationType());
        vh.setOldStatus("PASSED");
        assertEquals("PASSED", vh.getOldStatus());
        vh.setNewStatus("FAILED");
        assertEquals("FAILED", vh.getNewStatus());
        vh.setVerificationMethod("IN_PERSON");
        assertEquals("IN_PERSON", vh.getVerificationMethod());
        vh.setVerifiedBy("Supervisor");
        assertEquals("Supervisor", vh.getVerifiedBy());
        LocalDateTime next = now.plusDays(1);
        vh.setVerifiedAt(next);
        assertEquals(next, vh.getVerifiedAt());
        vh.setRemarks("Updated");
        assertEquals("Updated", vh.getRemarks());

        VerificationHistory def = new VerificationHistory();
        assertNotNull(def.getVerifiedAt());

        VerificationHistory nullArgs = new VerificationHistory(null, null, null, null, null, null, null, null, null, "remark");
        assertEquals("remark", nullArgs.getRemarks());
    }

    @Test
    void testDtos() {
        CarePlanRequest cpr = new CarePlanRequest(20L, 5L, 6L, "VetDoc", "Feed twice", "Special diet");
        assertEquals(20L, cpr.petId());
        assertEquals(5L, cpr.primaryCaretakerId());
        assertEquals(6L, cpr.backupCaretakerId());
        assertEquals("VetDoc", cpr.vetContact());
        assertEquals("Feed twice", cpr.feedingInstructions());
        assertEquals("Special diet", cpr.specialNeeds());

        CaretakerRequest ctr = new CaretakerRequest(10L, 20L, "John", "1234567890", "john@example.com", "PRIMARY", "Address");
        assertEquals(10L, ctr.customerId());
        assertEquals(20L, ctr.petId());
        assertEquals("John", ctr.fullName());
        assertEquals("john@example.com", ctr.email());
        assertEquals("1234567890", ctr.phone());
        assertEquals("PRIMARY", ctr.caretakerType());
        assertEquals("Address", ctr.address());

        EligibilityResponse elr = new EligibilityResponse(true, "Eligible", 5L);
        assertTrue(elr.eligible());
        assertEquals("Eligible", elr.reason());
        assertEquals(5L, elr.activeCaretakerId());

        VerificationRequest vr = new VerificationRequest(20L, 5L, "2026-10-10", "PASSED", "Healthy");
        assertEquals(20L, vr.petId());
        assertEquals(5L, vr.caretakerId());
        assertEquals("2026-10-10", vr.verificationDate());
        assertEquals("PASSED", vr.status());
        assertEquals("Healthy", vr.notes());
    }

    @Test
    void testConfigs() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        ConnectionFactory cf = mock(ConnectionFactory.class);
        ConnectionFactoryInitializer initializer = dbConfig.initializer(cf);
        assertNotNull(initializer);
    }
}
