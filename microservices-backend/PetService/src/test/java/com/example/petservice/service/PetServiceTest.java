package com.example.petservice.service;

import com.example.petservice.dto.MedicalRecordRequest;
import com.example.petservice.dto.PetRequest;
import com.example.petservice.model.Pet;
import com.example.petservice.model.PetMedicalRecord;
import com.example.petservice.repository.MedicalRecordRepository;
import com.example.petservice.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    private PetService petService;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        petService = new PetService(petRepository, medicalRecordRepository);

        samplePet = Pet.createNew(10L, "Buddy", "DOG", "Golden Retriever", 3, 28.5, "MALE", 1500.0);
        samplePet.setId(1L);
    }

    @Test
    void createPet_successful() {
        PetRequest req = new PetRequest(10L, "Buddy", "DOG", "Golden Retriever", 3, 28.5, "MALE", 1500.0);
        when(petRepository.save(any(Pet.class))).thenReturn(Mono.just(samplePet));

        StepVerifier.create(petService.createPet(req))
                .assertNext(p -> {
                    assertEquals(1L, p.getId());
                    assertEquals("Buddy", p.getName());
                })
                .verifyComplete();

        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void createPet_missingRequiredFields_throwsError() {
        // missing customerId
        PetRequest req1 = new PetRequest(null, "Name", "Species", "Breed", 3, 20.0, "M", 100.0);
        StepVerifier.create(petService.createPet(req1)).expectError(IllegalArgumentException.class).verify();

        // missing name
        PetRequest req2 = new PetRequest(10L, null, "Species", "Breed", 3, 20.0, "M", 100.0);
        StepVerifier.create(petService.createPet(req2)).expectError(IllegalArgumentException.class).verify();

        // missing species
        PetRequest req3 = new PetRequest(10L, "Name", null, "Breed", 3, 20.0, "M", 100.0);
        StepVerifier.create(petService.createPet(req3)).expectError(IllegalArgumentException.class).verify();

        // missing breed
        PetRequest req4 = new PetRequest(10L, "Name", "Species", null, 3, 20.0, "M", 100.0);
        StepVerifier.create(petService.createPet(req4)).expectError(IllegalArgumentException.class).verify();

        // missing age
        PetRequest req5 = new PetRequest(10L, "Name", "Species", "Breed", null, 20.0, "M", 100.0);
        StepVerifier.create(petService.createPet(req5)).expectError(IllegalArgumentException.class).verify();
    }

    @Test
    void getPetById_found() {
        when(petRepository.findById(1L)).thenReturn(Mono.just(samplePet));

        StepVerifier.create(petService.getPetById(1L))
                .assertNext(p -> assertEquals(1L, p.getId()))
                .verifyComplete();
    }

    @Test
    void getPetById_notFound_throwsError() {
        when(petRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.getPetById(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getPetsByCustomerId_returnsFlux() {
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setCustomerId(10L);
        pet2.setName("Milo");

        when(petRepository.findByCustomerId(10L)).thenReturn(Flux.just(samplePet, pet2));

        StepVerifier.create(petService.getPetsByCustomerId(10L))
                .expectNextMatches(p -> p.getName().equals("Buddy"))
                .expectNextMatches(p -> p.getName().equals("Milo"))
                .verifyComplete();
    }

    @Test
    void getAllPets_returnsFlux() {
        when(petRepository.findAll()).thenReturn(Flux.just(samplePet));

        StepVerifier.create(petService.getAllPets())
                .expectNext(samplePet)
                .verifyComplete();
    }

    @Test
    void addMedicalRecord_successful() {
        MedicalRecordRequest req = new MedicalRecordRequest("Hip Dysplasia", "2024-01-15", "Physical Therapy", 500.0);
        PetMedicalRecord record = PetMedicalRecord.createNew(1L, "Hip Dysplasia", "2024-01-15", "Physical Therapy", 500.0);
        record.setId(50L);

        when(petRepository.findById(1L)).thenReturn(Mono.just(samplePet));
        when(medicalRecordRepository.save(any(PetMedicalRecord.class))).thenReturn(Mono.just(record));

        StepVerifier.create(petService.addMedicalRecord(1L, req))
                .assertNext(r -> {
                    assertEquals(50L, r.getId());
                    assertEquals(1L, r.getPetId());
                    assertEquals("Hip Dysplasia", r.getConditionName());
                })
                .verifyComplete();
    }

    @Test
    void addMedicalRecord_blankConditionName_throwsError() {
        MedicalRecordRequest req1 = new MedicalRecordRequest("", "2024-01-15", "Plan", 100.0);
        StepVerifier.create(petService.addMedicalRecord(1L, req1))
                .expectError(IllegalArgumentException.class)
                .verify();

        MedicalRecordRequest req2 = new MedicalRecordRequest(null, "2024-01-15", "Plan", 100.0);
        StepVerifier.create(petService.addMedicalRecord(1L, req2))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(petRepository, never()).findById(anyLong());
    }

    @Test
    void addMedicalRecord_petNotFound_throwsError() {
        MedicalRecordRequest req = new MedicalRecordRequest("Allergy", "2024-01-15", "Meds", 100.0);
        when(petRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.addMedicalRecord(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getMedicalRecordsByPetId_returnsFlux() {
        PetMedicalRecord r1 = new PetMedicalRecord();
        r1.setId(101L);
        r1.setPetId(1L);

        when(medicalRecordRepository.findByPetId(1L)).thenReturn(Flux.just(r1));

        StepVerifier.create(petService.getMedicalRecordsByPetId(1L))
                .expectNextMatches(r -> r.getId().equals(101L))
                .verifyComplete();
    }

    @Test
    void getAllMedicalRecords_returnsFlux() {
        PetMedicalRecord r1 = new PetMedicalRecord();
        r1.setId(101L);
        when(medicalRecordRepository.findAll()).thenReturn(Flux.just(r1));

        StepVerifier.create(petService.getAllMedicalRecords())
                .expectNext(r1)
                .verifyComplete();
    }

    @Test
    void getMedicalRecordById_found() {
        PetMedicalRecord r1 = new PetMedicalRecord();
        r1.setId(101L);
        when(medicalRecordRepository.findById(101L)).thenReturn(Mono.just(r1));

        StepVerifier.create(petService.getMedicalRecordById(101L))
                .expectNext(r1)
                .verifyComplete();
    }

    @Test
    void getMedicalRecordById_notFound() {
        when(medicalRecordRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.getMedicalRecordById(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updatePet_successful() {
        PetRequest req = new PetRequest(10L, "Buddy Updated", "Dog", "Golden Retriever", 4, 30.0, "MALE", 1600.0);
        when(petRepository.findById(1L)).thenReturn(Mono.just(samplePet));
        when(petRepository.save(any(Pet.class))).thenReturn(Mono.just(samplePet));

        StepVerifier.create(petService.updatePet(1L, req))
                .assertNext(p -> {
                    assertEquals("Buddy Updated", samplePet.getName());
                    assertEquals(30.0, samplePet.getWeight());
                })
                .verifyComplete();
    }

    @Test
    void updatePet_partialNulls() {
        PetRequest req = new PetRequest(null, "  ", "", " ", null, null, null, null);
        when(petRepository.findById(1L)).thenReturn(Mono.just(samplePet));
        when(petRepository.save(any(Pet.class))).thenReturn(Mono.just(samplePet));

        StepVerifier.create(petService.updatePet(1L, req))
                .assertNext(p -> assertEquals("Buddy", p.getName()))
                .verifyComplete();
    }

    @Test
    void updatePet_notFound_throwsError() {
        PetRequest req = new PetRequest(10L, "Buddy", "Dog", "Breed", 3, 20.0, "MALE", 1000.0);
        when(petRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.updatePet(99L, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deletePet_successful() {
        when(petRepository.findById(1L)).thenReturn(Mono.just(samplePet));
        when(petRepository.delete(samplePet)).thenReturn(Mono.empty());

        StepVerifier.create(petService.deletePet(1L))
                .verifyComplete();

        verify(petRepository).delete(samplePet);
    }

    @Test
    void deletePet_notFound() {
        when(petRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.deletePet(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateMedicalRecord_successful() {
        PetMedicalRecord r1 = new PetMedicalRecord();
        r1.setId(50L);
        MedicalRecordRequest req = new MedicalRecordRequest("Asthma Updated", "2024-05-01", "Inhaler daily", 400.0);

        when(medicalRecordRepository.findById(50L)).thenReturn(Mono.just(r1));
        when(medicalRecordRepository.save(any(PetMedicalRecord.class))).thenReturn(Mono.just(r1));

        StepVerifier.create(petService.updateMedicalRecord(50L, req))
                .assertNext(r -> {
                    assertEquals("Asthma Updated", r1.getConditionName());
                    assertEquals(400.0, r1.getEstimatedAnnualMedCost());
                })
                .verifyComplete();
    }

    @Test
    void updateMedicalRecord_partialNulls() {
        PetMedicalRecord r1 = new PetMedicalRecord();
        r1.setId(50L);
        r1.setDiagnosis("Old");
        MedicalRecordRequest req = new MedicalRecordRequest(null, null, null, null);

        when(medicalRecordRepository.findById(50L)).thenReturn(Mono.just(r1));
        when(medicalRecordRepository.save(any(PetMedicalRecord.class))).thenReturn(Mono.just(r1));

        StepVerifier.create(petService.updateMedicalRecord(50L, req))
                .assertNext(r -> assertEquals("Old", r.getDiagnosis()))
                .verifyComplete();
    }

    @Test
    void updateMedicalRecord_notFound() {
        when(medicalRecordRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.updateMedicalRecord(99L, new MedicalRecordRequest("A", "B", "C", 100.0)))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteMedicalRecord_successful() {
        PetMedicalRecord record = new PetMedicalRecord();
        record.setId(50L);

        when(medicalRecordRepository.findById(50L)).thenReturn(Mono.just(record));
        when(medicalRecordRepository.delete(record)).thenReturn(Mono.empty());

        StepVerifier.create(petService.deleteMedicalRecord(50L))
                .verifyComplete();

        verify(medicalRecordRepository).delete(record);
    }

    @Test
    void deleteMedicalRecord_notFound() {
        when(medicalRecordRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.deleteMedicalRecord(99L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
