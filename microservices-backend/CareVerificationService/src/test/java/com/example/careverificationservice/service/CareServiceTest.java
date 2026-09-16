package com.example.careverificationservice.service;

import com.example.careverificationservice.dto.CarePlanRequest;
import com.example.careverificationservice.dto.CaretakerRequest;
import com.example.careverificationservice.dto.EligibilityResponse;
import com.example.careverificationservice.dto.VerificationRequest;
import com.example.careverificationservice.model.CarePlan;
import com.example.careverificationservice.model.Caretaker;
import com.example.careverificationservice.model.PetVerification;
import com.example.careverificationservice.repository.CarePlanRepository;
import com.example.careverificationservice.repository.CaretakerRepository;
import com.example.careverificationservice.repository.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

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

    @InjectMocks
    private CareService careService;

    private Caretaker primaryCaretaker;
    private Caretaker backupCaretaker;
    private CarePlan sampleCarePlan;
    private PetVerification sampleVerification;

    @BeforeEach
    void setUp() {
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
        CaretakerRequest reqNoCustomer = new CaretakerRequest(null, 20L, "Name", "123", "email", "PRIMARY", null);
        StepVerifier.create(careService.addCaretaker(reqNoCustomer))
                .expectError(IllegalArgumentException.class)
                .verify();

        CaretakerRequest reqNoPet = new CaretakerRequest(10L, null, "Name", "123", "email", "PRIMARY", null);
        StepVerifier.create(careService.addCaretaker(reqNoPet))
                .expectError(IllegalArgumentException.class)
                .verify();

        CaretakerRequest reqNoName = new CaretakerRequest(10L, 20L, null, "123", "email", "PRIMARY", null);
        StepVerifier.create(careService.addCaretaker(reqNoName))
                .expectError(IllegalArgumentException.class)
                .verify();
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
    void transferToBackup_successful() {
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.just(sampleCarePlan));
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.findById(2L)).thenReturn(Mono.just(backupCaretaker));
        when(caretakerRepository.save(any(Caretaker.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(careService.transferToBackup(20L))
                .assertNext(newPrimary -> {
                    assertEquals(2L, newPrimary.getId());
                    assertEquals("PRIMARY", newPrimary.getCaretakerType());
                    assertEquals("ACTIVE", newPrimary.getStatus());
                })
                .verifyComplete();

        assertEquals("UNAVAILABLE", primaryCaretaker.getStatus());
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
    void saveCarePlan_createsNewPlan() {
        CarePlanRequest req = new CarePlanRequest(20L, 1L, 2L, "Dr. Smith", "Twice daily", "Special diet");
        when(carePlanRepository.findByPetId(20L)).thenReturn(Mono.empty());
        when(carePlanRepository.save(any(CarePlan.class))).thenReturn(Mono.just(sampleCarePlan));

        StepVerifier.create(careService.saveCarePlan(req))
                .assertNext(plan -> assertEquals(100L, plan.getId()))
                .verifyComplete();

        verify(carePlanRepository).save(any(CarePlan.class));
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
        PetVerification failedVerification = PetVerification.create(20L, 1L, "2024-03-01", "FAILED", "Pet was not found at residence");
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
    void deleteCaretaker_successful() {
        when(caretakerRepository.findById(1L)).thenReturn(Mono.just(primaryCaretaker));
        when(caretakerRepository.delete(primaryCaretaker)).thenReturn(Mono.empty());

        StepVerifier.create(careService.deleteCaretaker(1L))
                .verifyComplete();

        verify(caretakerRepository).delete(primaryCaretaker);
    }
}
