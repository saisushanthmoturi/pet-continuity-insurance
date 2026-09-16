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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private PetService petService;

    private Pet samplePet;

    @BeforeEach
    void setUp() {
        samplePet = new Pet();
        samplePet.setId(1L);
        samplePet.setCustomerId(10L);
        samplePet.setName("Buddy");
        samplePet.setSpecies("Dog");
        samplePet.setBreed("Golden Retriever");
        samplePet.setGender("MALE");
        samplePet.setDateOfBirth(LocalDate.now().minusYears(3));
        samplePet.setWeight(28.5);
        samplePet.setAnnualCareCost(1500.0);
        samplePet.setStatus("ACTIVE");
        samplePet.setCreatedAt(LocalDateTime.now());
        samplePet.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createPet_successful() {
        PetRequest req = new PetRequest(10L, "Buddy", "Dog", "Golden Retriever", 3, 28.5, "MALE", 1500.0);
        when(petRepository.save(any(Pet.class))).thenReturn(Mono.just(samplePet));

        StepVerifier.create(petService.createPet(req))
                .assertNext(pet -> {
                    assertEquals(1L, pet.getId());
                    assertEquals("Buddy", pet.getName());
                    assertEquals("Dog", pet.getSpecies());
                    assertEquals(10L, pet.getCustomerId());
                })
                .verifyComplete();

        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void createPet_missingRequiredFields_throwsError() {
        PetRequest reqMissingName = new PetRequest(10L, null, "Dog", "Golden Retriever", 3, 28.5, "MALE", 1500.0);
        StepVerifier.create(petService.createPet(reqMissingName))
                .expectError(IllegalArgumentException.class)
                .verify();

        PetRequest reqMissingSpecies = new PetRequest(10L, "Buddy", null, "Golden Retriever", 3, 28.5, "MALE", 1500.0);
        StepVerifier.create(petService.createPet(reqMissingSpecies))
                .expectError(IllegalArgumentException.class)
                .verify();

        PetRequest reqMissingBreed = new PetRequest(10L, "Buddy", "Dog", null, 3, 28.5, "MALE", 1500.0);
        StepVerifier.create(petService.createPet(reqMissingBreed))
                .expectError(IllegalArgumentException.class)
                .verify();

        PetRequest reqMissingAge = new PetRequest(10L, "Buddy", "Dog", "Golden Retriever", null, 28.5, "MALE", 1500.0);
        StepVerifier.create(petService.createPet(reqMissingAge))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getPetById_found() {
        when(petRepository.findById(1L)).thenReturn(Mono.just(samplePet));

        StepVerifier.create(petService.getPetById(1L))
                .assertNext(pet -> {
                    assertEquals(1L, pet.getId());
                    assertEquals("Buddy", pet.getName());
                })
                .verifyComplete();
    }

    @Test
    void getPetById_notFound_throwsError() {
        when(petRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.getPetById(99L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Pet not found"))
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
        MedicalRecordRequest req = new MedicalRecordRequest("", "2024-01-15", "Plan", 100.0);

        StepVerifier.create(petService.addMedicalRecord(1L, req))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(petRepository, never()).findById(anyLong());
    }

    @Test
    void addMedicalRecord_petNotFound_throwsError() {
        MedicalRecordRequest req = new MedicalRecordRequest("Allergy", "2024-01-15", "Meds", 100.0);
        when(petRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(petService.addMedicalRecord(99L, req))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Pet not found"))
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
    void deleteMedicalRecord_successful() {
        PetMedicalRecord record = new PetMedicalRecord();
        record.setId(50L);

        when(medicalRecordRepository.findById(50L)).thenReturn(Mono.just(record));
        when(medicalRecordRepository.delete(record)).thenReturn(Mono.empty());

        StepVerifier.create(petService.deleteMedicalRecord(50L))
                .verifyComplete();

        verify(medicalRecordRepository).delete(record);
    }
}
