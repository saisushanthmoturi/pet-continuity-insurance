package com.example.careverificationservice.service;

import com.example.careverificationservice.dto.*;
import com.example.careverificationservice.model.*;
import com.example.careverificationservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CareServiceTest {

    @Mock
    private CaretakerRepository caretakerRepository;

    @Mock
    private CarePlanRepository carePlanRepository;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private CareTransferRepository careTransferRepository;

    @Mock
    private CaretakerVerificationRepository caretakerVerificationRepository;

    private CareService careService;
    private CareService secondaryCareService;

    private Caretaker primaryCaretaker;
    private Caretaker backupCaretaker;
    private CarePlan sampleCarePlan;
    private PetVerification sampleVerification;

    @BeforeEach
    void setUp() {
        careService = new CareService(
                caretakerRepository,
                carePlanRepository,
                verificationRepository,
                careTransferRepository,
                caretakerVerificationRepository
        );

        secondaryCareService = new CareService(
                caretakerRepository,
                carePlanRepository,
                verificationRepository
        );

        primaryCaretaker = Caretaker.create(10L, 20L, "John Caretaker", "1234567890", "john@caretaker.com", "PRIMARY", "123 Main St");
        primaryCaretaker.setId(1L);

        backupCaretaker = Caretaker.create(10L, 20L, "Jane Backup", "0987654321", "jane@backup.com", "BACKUP", "456 Oak St");
        backupCaretaker.setId(2L);

        sampleCarePlan = CarePlan.create(20L, 1L, 2L, "Dr. Smith (555-1111)", "Twice a day", "None");
        sampleCarePlan.setId(100L);

        sampleVerification = PetVerification.create(20L, 1L, "2024-03-01", "VERIFIED", "Pet is healthy and happy");
        sampleVerification.setId(200L);
    }

    @Test
    void addCaretaker_successful() {
        CaretakerRequest req = new CaretakerRequest(10L, 20L, "John Caretaker", "1234567890", "john@caretaker.com", "PRIMARY", "123 Main St");
        when(caretakerRepository.save(any(Caretaker.class))).thenReturn(Mono.just(primaryCaretaker));

        StepVerifier.create(careService.addCaretaker(req))
                .assertNext(c -> {
                    assertEquals(1L, c.getId());
                    assertEquals("John Caretaker", c.getFullName());
                })
                .verifyComplete();

        verify(caretakerRepository).save(any(Caretaker.class));
    }

    @Test
    void addCaretaker_missingFields_throwsError() {
        CaretakerRequest reqNoCust = new CaretakerRequest(null, 20L, "Name", "123", "email", "PRIMARY", "Address");
        StepVerifier.create(careService.addCaretaker(reqNoCust))
                .expectError(IllegalArgumentException.class)
                .verify();

        CaretakerRequest reqNoPet = new CaretakerRequest(10L, null, "Name", "123", "email", "PRIMARY", "Address");
        StepVerifier.create(careService.addCaretaker(reqNoPet))
                .expectError(IllegalArgumentException.class)
                .verify();

        CaretakerRequest reqNoName = new CaretakerRequest(10L, 20L, null, "123", "email", "PRIMARY", "Address");
        StepVerifier.create(careService.addCaretaker(reqNoName))
                .expectError(IllegalArgumentException.class)
                .verify();

        CaretakerRequest reqNoPhone = new CaretakerRequest(10L, 20L, "Name", null, "email", "PRIMARY", "Address");
        StepVerifier.create(careService.addCaretaker(reqNoPhone))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAllCaretakers_returnsFlux() {
        when(caretakerRepository.findAll()).thenReturn(Flux.just(primaryCaretaker));

        StepVerifier.create(careService.getAllCaretakers())
                .expectNext(primaryCaretaker)
                .verifyComplete();
    }

    @Test
    void getCaretakerById_found() {
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));

        StepVerifier.create(careService.getCaretakerById(1L))
                .assertNext(c -> assertEquals(1L, c.getId()))
                .verifyComplete();
    }

    @Test
    void getCaretakerById_notFound_throwsError() {
        when(caretakerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.getCaretakerById(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateCaretaker_found() {
        CaretakerRequest req = new CaretakerRequest(10L, 20L, "John Updated", "999", "updated@example.com", "PRIMARY", "New St");
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.save(any(Caretaker.class))).thenReturn(Mono.just(primaryCaretaker));

        StepVerifier.create(careService.updateCaretaker(1L, req))
                .assertNext(c -> assertEquals(1L, c.getId()))
                .verifyComplete();
    }

    @Test
    void updateCaretaker_notFound_throwsError() {
        CaretakerRequest req = new CaretakerRequest(10L, 20L, "John", "999", "email", "PRIMARY", "New St");
        when(caretakerRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.updateCaretaker(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteCaretaker_successful() {
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.delete(primaryCaretaker)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteCaretaker(1L))
                .verifyComplete();

        verify(caretakerRepository).delete(primaryCaretaker);
    }

    @Test
    void deleteCaretaker_notFound_throwsError() {
        when(caretakerRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteCaretaker(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getCaretakersByPetId_returnsFlux() {
        when(caretakerRepository.findByPetId(20L)).thenReturn(Flux.just(primaryCaretaker));

        StepVerifier.create(careService.getCaretakersByPetId(20L))
                .expectNext(primaryCaretaker)
                .verifyComplete();
    }

    @Test
    void updateCaretakerStatus_found() {
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.save(any(Caretaker.class))).thenReturn(Mono.just(primaryCaretaker));

        StepVerifier.create(careService.updateCaretakerStatus(1L, "INACTIVE"))
                .assertNext(c -> assertEquals("INACTIVE", c.getStatus()))
                .verifyComplete();
    }

    @Test
    void updateCaretakerStatus_notFound_throwsError() {
        when(caretakerRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.updateCaretakerStatus(99L, "INACTIVE"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void transferToBackup_successful_withTransferRepo() {
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.findById(2L)).thenReturn(Mono.just(backupCaretaker));
        when(caretakerRepository.save(any(Caretaker.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(careTransferRepository.save(any(CareTransfer.class))).thenReturn(Mono.just(new CareTransfer()));

        StepVerifier.create(careService.transferToBackup(20L))
                .assertNext(newPrimary -> {
                    assertEquals(2L, newPrimary.getId());
                    assertEquals("PRIMARY", newPrimary.getCaretakerType());
                    assertEquals("ACTIVE", newPrimary.getStatus());
                })
                .verifyComplete();

        assertEquals("UNAVAILABLE", primaryCaretaker.getStatus());
        verify(careTransferRepository).save(any(CareTransfer.class));
    }

    @Test
    void transferToBackup_successful_withoutTransferRepo() {
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.findById(2L)).thenReturn(Mono.just(backupCaretaker));
        when(caretakerRepository.save(any(Caretaker.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(secondaryCareService.transferToBackup(20L))
                .assertNext(newPrimary -> assertEquals(2L, newPrimary.getId()))
                .verifyComplete();
    }

    @Test
    void transferToBackup_carePlanNotFound_throwsError() {
        when(carePlanRepository.findByPetId(99L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.transferToBackup(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void transferToBackup_noBackupInPlan_throwsError() {
        CarePlan planNoBackup = CarePlan.create(20L, 1L, null, "Vet", "Food", "None");
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(planNoBackup));

        StepVerifier.create(careService.transferToBackup(20L))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().contains("No backup caretaker"))
                .verify();
    }

    @Test
    void getAllCarePlans_returnsFlux() {
        when(carePlanRepository.findAll()).thenReturn(Flux.just(sampleCarePlan));

        StepVerifier.create(careService.getAllCarePlans())
                .expectNext(sampleCarePlan)
                .verifyComplete();
    }

    @Test
    void getCarePlanById_found() {
        when(carePlanRepository.findById(100L)).thenReturn(Mono.just(sampleCarePlan));

        StepVerifier.create(careService.getCarePlanById(100L))
                .assertNext(p -> assertEquals(100L, p.getId()))
                .verifyComplete();
    }

    @Test
    void getCarePlanById_notFound_throwsError() {
        when(carePlanRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.getCarePlanById(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteCarePlan_found() {
        when(carePlanRepository.findById(100L)).thenReturn(Mono.just(sampleCarePlan));
        when(carePlanRepository.delete(sampleCarePlan)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteCarePlan(100L))
                .verifyComplete();

        verify(carePlanRepository).delete(sampleCarePlan);
    }

    @Test
    void deleteCarePlan_notFound_throwsError() {
        when(carePlanRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteCarePlan(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveCarePlan_missingFields_throwsError() {
        CarePlanRequest noPet = new CarePlanRequest(null, 1L, 2L, "Vet", "Feed", "None");
        StepVerifier.create(careService.saveCarePlan(noPet))
                .expectError(IllegalArgumentException.class)
                .verify();

        CarePlanRequest noPrimary = new CarePlanRequest(20L, null, 2L, "Vet", "Feed", "None");
        StepVerifier.create(careService.saveCarePlan(noPrimary))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveCarePlan_updatesExisting() {
        CarePlanRequest req = new CarePlanRequest(20L, 1L, 2L, "Dr. Smith", "Twice daily", "Special diet");
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));
        when(carePlanRepository.save(any(CarePlan.class))).thenReturn(Mono.just(sampleCarePlan));

        StepVerifier.create(careService.saveCarePlan(req))
                .assertNext(plan -> assertEquals(100L, plan.getId()))
                .verifyComplete();
    }

    @Test
    void saveCarePlan_createsNew() {
        CarePlanRequest req = new CarePlanRequest(20L, 1L, 2L, "Dr. Smith", "Twice daily", "Special diet");
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.empty());
        when(carePlanRepository.save(any(CarePlan.class))).thenReturn(Mono.just(sampleCarePlan));

        StepVerifier.create(careService.saveCarePlan(req))
                .assertNext(plan -> assertEquals(100L, plan.getId()))
                .verifyComplete();
    }

    @Test
    void getCarePlanByPetId_found() {
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));

        StepVerifier.create(careService.getCarePlanByPetId(20L))
                .assertNext(plan -> assertEquals(100L, plan.getId()))
                .verifyComplete();
    }

    @Test
    void getCarePlanByPetId_notFound_throwsError() {
        when(carePlanRepository.findByPetId(999L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.getCarePlanByPetId(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getAllVerifications_returnsFlux() {
        when(verificationRepository.findAll()).thenReturn(Flux.just(sampleVerification));

        StepVerifier.create(careService.getAllVerifications())
                .expectNext(sampleVerification)
                .verifyComplete();
    }

    @Test
    void getVerificationById_found() {
        when(verificationRepository.findById(200L)).thenReturn(Mono.just(sampleVerification));

        StepVerifier.create(careService.getVerificationById(200L))
                .assertNext(v -> assertEquals(200L, v.getId()))
                .verifyComplete();
    }

    @Test
    void getVerificationById_notFound_throwsError() {
        when(verificationRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.getVerificationById(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteVerification_found() {
        when(verificationRepository.findById(200L)).thenReturn(Mono.just(sampleVerification));
        when(verificationRepository.delete(sampleVerification)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteVerification(200L))
                .verifyComplete();

        verify(verificationRepository).delete(sampleVerification);
    }

    @Test
    void deleteVerification_notFound_throwsError() {
        when(verificationRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteVerification(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void recordVerification_missingFields_throwsError() {
        VerificationRequest noPet = new VerificationRequest(null, 1L, "2024-03-01", "PASSED", "Notes");
        StepVerifier.create(careService.recordVerification(noPet))
                .expectError(IllegalArgumentException.class)
                .verify();

        VerificationRequest noCaretaker = new VerificationRequest(20L, null, "2024-03-01", "PASSED", "Notes");
        StepVerifier.create(careService.recordVerification(noCaretaker))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void recordVerification_successful() {
        VerificationRequest req = new VerificationRequest(20L, 1L, "2024-03-01", "VERIFIED", "All good");
        when(verificationRepository.save(any(PetVerification.class))).thenReturn(Mono.just(sampleVerification));

        StepVerifier.create(careService.recordVerification(req))
                .assertNext(v -> {
                    assertEquals(200L, v.getId());
                    assertEquals("VERIFIED", v.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getVerificationsByPetId_returnsFlux() {
        when(verificationRepository.findByPetIdOrderByCreatedAtDesc(20L)).thenReturn(Flux.just(sampleVerification));

        StepVerifier.create(careService.getVerificationsByPetId(20L))
                .expectNext(sampleVerification)
                .verifyComplete();
    }

    @Test
    void checkMonthlyEligibility_eligibleWhenActiveAndVerified() {
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(verificationRepository.findFirstByPetIdOrderByCreatedAtDesc(20L)).thenReturn(Mono.just(sampleVerification));

        StepVerifier.create(careService.checkMonthlyEligibility(20L, 1L))
                .assertNext(res -> {
                    assertTrue(res.eligible());
                    assertEquals(1L, res.activeCaretakerId());
                })
                .verifyComplete();
    }

    @Test
    void checkMonthlyEligibility_ineligibleWhenVerificationFailed() {
        PetVerification failedVerification = PetVerification.create(20L, 1L, "2024-03-01", "FAILED", "Pet not found");
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(verificationRepository.findFirstByPetIdOrderByCreatedAtDesc(20L)).thenReturn(Mono.just(failedVerification));

        StepVerifier.create(careService.checkMonthlyEligibility(20L, 1L))
                .assertNext(res -> {
                    assertFalse(res.eligible());
                    assertTrue(res.reason().contains("FAILED"));
                })
                .verifyComplete();
    }

    @Test
    void checkMonthlyEligibility_caretakerUnavailable_redirectsToBackup() {
        primaryCaretaker.setStatus("UNAVAILABLE");
        backupCaretaker.setStatus("ACTIVE");

        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));
        when(caretakerRepository.findById(2L)).thenReturn(Mono.just(backupCaretaker));
        when(verificationRepository.findFirstByPetIdOrderByCreatedAtDesc(20L)).thenReturn(Mono.just(sampleVerification));

        StepVerifier.create(careService.checkMonthlyEligibility(20L, 1L))
                .assertNext(res -> {
                    assertTrue(res.eligible());
                    assertEquals(2L, res.activeCaretakerId());
                })
                .verifyComplete();
    }

    @Test
    void checkMonthlyEligibility_caretakerUnavailable_noBackupAssigned() {
        primaryCaretaker.setStatus("UNAVAILABLE");
        CarePlan planNoBackup = CarePlan.create(20L, 1L, null, "Vet", "Feed", "None");

        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(planNoBackup));

        StepVerifier.create(careService.checkMonthlyEligibility(20L, 1L))
                .assertNext(res -> assertFalse(res.eligible()))
                .verifyComplete();
    }

    @Test
    void checkMonthlyEligibility_caretakerUnavailable_noCarePlan() {
        primaryCaretaker.setStatus("UNAVAILABLE");

        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.empty());

        StepVerifier.create(careService.checkMonthlyEligibility(20L, 1L))
                .assertNext(res -> assertFalse(res.eligible()))
                .verifyComplete();
    }

    @Test
    void checkMonthlyEligibility_noCaretakerRegistered() {
        when(caretakerRepository.findById(1L)).thenReturn(Mono.empty());
        when(caretakerRepository.findByPetId(20L)).thenReturn(Flux.empty());

        StepVerifier.create(careService.checkMonthlyEligibility(20L, 1L))
                .assertNext(res -> assertFalse(res.eligible()))
                .verifyComplete();
    }

    @Test
    void getTransfersByPetId_withRepo() {
        CareTransfer ct = new CareTransfer();
        when(careTransferRepository.findByPetIdOrderByCreatedAtDesc(20L)).thenReturn(Flux.just(ct));

        StepVerifier.create(careService.getTransfersByPetId(20L))
                .expectNext(ct)
                .verifyComplete();
    }

    @Test
    void getTransfersByPetId_withoutRepo() {
        StepVerifier.create(secondaryCareService.getTransfersByPetId(20L))
                .verifyComplete();
    }

    @Test
    void recordCaretakerVerification_withRepo() {
        CaretakerVerification cv = new CaretakerVerification();
        when(caretakerVerificationRepository.save(any(CaretakerVerification.class))).thenReturn(Mono.just(cv));

        StepVerifier.create(careService.recordCaretakerVerification(1L, cv))
                .expectNext(cv)
                .verifyComplete();
    }

    @Test
    void recordCaretakerVerification_withoutRepo() {
        StepVerifier.create(secondaryCareService.recordCaretakerVerification(1L, new CaretakerVerification()))
                .verifyComplete();
    }

    @Test
    void getCaretakerVerifications_withRepo() {
        CaretakerVerification cv = new CaretakerVerification();
        when(caretakerVerificationRepository.findByCaretakerIdOrderByVerifiedAtDesc(1L)).thenReturn(Flux.just(cv));

        StepVerifier.create(careService.getCaretakerVerifications(1L))
                .expectNext(cv)
                .verifyComplete();
    }

    @Test
    void getCaretakerVerifications_withoutRepo() {
        StepVerifier.create(secondaryCareService.getCaretakerVerifications(1L))
                .verifyComplete();
    }
}
