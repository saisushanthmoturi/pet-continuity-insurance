package com.example.petservice.model;

import com.example.petservice.config.DatabaseConfig;
import com.example.petservice.dto.MedicalRecordRequest;
import com.example.petservice.dto.PetRequest;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PetModelAndDtoTest {

    @Test
    void testPetModel() {
        LocalDate dob = LocalDate.of(2020, 1, 1);
        LocalDateTime now = LocalDateTime.now();

        Pet p = new Pet(1L, 10L, "Buddy", "DOG", "Golden", "MALE", dob, 25.0, "CHIP123", "LOW", 1200.0, 12, "ACTIVE", now, now);
        assertEquals(1L, p.getId());
        assertEquals(1L, p.getPetId());
        assertEquals(10L, p.getCustomerId());
        assertEquals("Buddy", p.getName());
        assertEquals("DOG", p.getSpecies());
        assertEquals("DOG", p.getSpeciesCode());
        assertEquals("Golden", p.getBreed());
        assertEquals("Golden", p.getBreedCode());
        assertEquals("MALE", p.getGender());
        assertEquals(dob, p.getDateOfBirth());
        assertEquals(25.0, p.getWeight());
        assertEquals(25.0, p.getWeightValue());
        assertEquals("CHIP123", p.getMicrochipId());
        assertEquals("LOW", p.getMedicalRisk());
        assertEquals(1200.0, p.getAnnualCareCost());
        assertEquals(1200.0, p.getEstimatedAnnualCareCost());
        assertEquals(12, p.getExpectedRemainingYears());
        assertEquals("ACTIVE", p.getStatus());
        assertEquals(now, p.getCreatedAt());
        assertEquals(now, p.getUpdatedAt());
        assertNotNull(p.getAge());
        assertEquals("USD", p.getCurrency());
        assertEquals("KG", p.getWeightUnit());
        assertFalse(p.getNeuteredStatus());
        assertFalse(p.getDateOfBirthEstimated());

        p.setId(2L);
        assertEquals(2L, p.getId());
        p.setPetId(3L);
        assertEquals(3L, p.getPetId());
        p.setCustomerId(20L);
        assertEquals(20L, p.getCustomerId());
        p.setName("Charlie");
        assertEquals("Charlie", p.getName());
        p.setSpecies("CAT");
        assertEquals("CAT", p.getSpecies());
        p.setSpeciesCode("FELINE");
        assertEquals("FELINE", p.getSpeciesCode());
        p.setBreed("Persian");
        assertEquals("Persian", p.getBreed());
        p.setBreedCode("PER");
        assertEquals("PER", p.getBreedCode());
        p.setGender("FEMALE");
        assertEquals("FEMALE", p.getGender());
        LocalDate newDob = LocalDate.of(2019, 5, 5);
        p.setDateOfBirth(newDob);
        assertEquals(newDob, p.getDateOfBirth());
        p.setWeight(15.0);
        assertEquals(15.0, p.getWeight());
        p.setWeightValue(16.0);
        assertEquals(16.0, p.getWeightValue());
        p.setMicrochipId("CHIP999");
        assertEquals("CHIP999", p.getMicrochipId());
        p.setMedicalRisk("HIGH");
        assertEquals("HIGH", p.getMedicalRisk());
        p.setAnnualCareCost(1500.0);
        assertEquals(1500.0, p.getAnnualCareCost());
        p.setEstimatedAnnualCareCost(1600.0);
        assertEquals(1600.0, p.getEstimatedAnnualCareCost());
        p.setExpectedRemainingYears(8);
        assertEquals(8, p.getExpectedRemainingYears());
        p.setStatus("INACTIVE");
        assertEquals("INACTIVE", p.getStatus());
        p.setAge(5);
        assertEquals(5, p.getAge());
        p.setDateOfBirthEstimated(true);
        assertTrue(p.getDateOfBirthEstimated());
        p.setCurrency("EUR");
        assertEquals("EUR", p.getCurrency());
        p.setWeightUnit("LBS");
        assertEquals("LBS", p.getWeightUnit());
        p.setNeuteredStatus(true);
        assertTrue(p.getNeuteredStatus());
        LocalDateTime later = now.plusDays(1);
        p.setCreatedAt(later);
        assertEquals(later, p.getCreatedAt());
        p.setUpdatedAt(later);
        assertEquals(later, p.getUpdatedAt());

        // Default constructor
        Pet def = new Pet();
        assertNull(def.getId());
        assertEquals("ACTIVE", def.getStatus());

        // Null fields in constructor
        Pet nullArgs = new Pet(null, 10L, "N", "S", "B", "M", null, null, null, null, null, null, null, null, null);
        assertEquals("LOW", nullArgs.getMedicalRisk());
        assertEquals(1200.0, nullArgs.getAnnualCareCost());
        assertEquals(10, nullArgs.getExpectedRemainingYears());
        assertEquals("ACTIVE", nullArgs.getStatus());
        assertNotNull(nullArgs.getCreatedAt());
        assertNotNull(nullArgs.getUpdatedAt());

        // getAge with null dateOfBirth
        nullArgs.setDateOfBirth(null);
        assertEquals(3, nullArgs.getAge());
        nullArgs.setAge(null);
        assertEquals(3, nullArgs.getAge());

        // Factory createNew
        Pet created = Pet.createNew(10L, "Bella", "DOG", "Lab", 2, 20.0, "FEMALE", 1000.0);
        assertEquals("Bella", created.getName());
        assertEquals(2, created.getAge());
        assertEquals(1000.0, created.getEstimatedAnnualCareCost());
        assertEquals("ACTIVE", created.getStatus());

        // Factory with nulls
        Pet createdNulls = Pet.createNew(10L, "Bella", "DOG", "Lab", null, null, null, null);
        assertEquals(3, createdNulls.getAge());
        assertEquals(1200.0, createdNulls.getEstimatedAnnualCareCost());
    }

    @Test
    void testPetMedicalRecordModel() {
        LocalDate rDate = LocalDate.of(2023, 6, 1);
        LocalDateTime now = LocalDateTime.now();

        PetMedicalRecord rec = new PetMedicalRecord(1L, 10L, "VACCINE", "Rabies", "Shot given", "Dr. John", rDate, "LOW", "Annual checkup", "ACTIVE");
        assertEquals(1L, rec.getId());
        assertEquals(1L, rec.getMedicalRecordId());
        assertEquals(10L, rec.getPetId());
        assertEquals("VACCINE", rec.getRecordType());
        assertEquals("Rabies", rec.getDiagnosis());
        assertEquals("Rabies", rec.getConditionName());
        assertEquals("Shot given", rec.getTreatment());
        assertEquals("Shot given", rec.getTreatmentPlan());
        assertEquals("Dr. John", rec.getVetName());
        assertEquals(rDate, rec.getRecordDate());
        assertEquals("2023-06-01", rec.getDiagnosisDate());
        assertEquals("LOW", rec.getRiskLevel());
        assertEquals("Annual checkup", rec.getNotes());
        assertEquals("ACTIVE", rec.getStatus());

        rec.setId(2L);
        assertEquals(2L, rec.getId());
        rec.setMedicalRecordId(3L);
        assertEquals(3L, rec.getMedicalRecordId());
        rec.setPetId(20L);
        assertEquals(20L, rec.getPetId());
        rec.setRecordType("SURGERY");
        assertEquals("SURGERY", rec.getRecordType());
        rec.setDiagnosis("Broken leg");
        assertEquals("Broken leg", rec.getDiagnosis());
        rec.setConditionName("Fracture");
        assertEquals("Fracture", rec.getConditionName());
        rec.setTreatment("Cast");
        assertEquals("Cast", rec.getTreatment());
        rec.setTreatmentPlan("Splint");
        assertEquals("Splint", rec.getTreatmentPlan());
        rec.setVetName("Dr. Smith");
        assertEquals("Dr. Smith", rec.getVetName());
        LocalDate newDate = LocalDate.of(2024, 1, 1);
        rec.setRecordDate(newDate);
        assertEquals(newDate, rec.getRecordDate());
        rec.setDiagnosisDate("2024-02-02");
        assertEquals("2024-02-02", rec.getDiagnosisDate());
        rec.setDiagnosisDate("invalid-date"); // catch block
        assertEquals("2024-02-02", rec.getDiagnosisDate());
        rec.setRiskLevel("HIGH");
        assertEquals("HIGH", rec.getRiskLevel());
        rec.setNotes("Requires rest");
        assertEquals("Requires rest", rec.getNotes());
        rec.setStatus("RESOLVED");
        assertEquals("RESOLVED", rec.getStatus());
        rec.setEstimatedAnnualMedCost(500.0);
        assertEquals(500.0, rec.getEstimatedAnnualMedCost());
        LocalDateTime later = now.plusDays(1);
        rec.setCreatedAt(later);
        assertEquals(later, rec.getCreatedAt());
        rec.setUpdatedAt(later);
        assertEquals(later, rec.getUpdatedAt());

        // Default constructor
        PetMedicalRecord def = new PetMedicalRecord();
        assertNull(def.getId());
        rec.setRecordDate(null);
        assertEquals("", rec.getDiagnosisDate());

        // Constructor with nulls
        PetMedicalRecord nullArgs = new PetMedicalRecord(null, null, null, null, null, null, null, null, null, null);
        assertEquals("GENERAL", nullArgs.getRecordType());
        assertEquals("LOW", nullArgs.getRiskLevel());
        assertEquals("ACTIVE", nullArgs.getStatus());

        // Factory createNew
        PetMedicalRecord created = PetMedicalRecord.createNew(10L, "Asthma", "2024-03-01", "Inhaler", 300.0);
        assertEquals(10L, created.getPetId());
        assertEquals("Asthma", created.getConditionName());
        assertEquals(300.0, created.getEstimatedAnnualMedCost());

        // Factory with nulls and invalid date
        PetMedicalRecord createdInvalid = PetMedicalRecord.createNew(10L, "Asthma", "bad-date", "Inhaler", null);
        assertEquals(0.0, createdInvalid.getEstimatedAnnualMedCost());

        PetMedicalRecord createdNullDate = PetMedicalRecord.createNew(10L, "Asthma", null, "Inhaler", 100.0);
        assertNotNull(createdNullDate.getRecordDate());
    }

    @Test
    void testDtos() {
        PetRequest pr = new PetRequest(10L, "Buddy", "DOG", "Golden", 3, 25.0, "MALE", 1200.0);
        assertEquals(10L, pr.customerId());
        assertEquals("Buddy", pr.name());
        assertEquals("DOG", pr.species());
        assertEquals("Golden", pr.breed());
        assertEquals(3, pr.age());
        assertEquals(25.0, pr.weight());
        assertEquals("MALE", pr.gender());
        assertEquals(1200.0, pr.estimatedAnnualCareCost());

        MedicalRecordRequest mr = new MedicalRecordRequest("Allergy", "2024-01-01", "Antihistamine", 200.0);
        assertEquals("Allergy", mr.conditionName());
        assertEquals("2024-01-01", mr.diagnosisDate());
        assertEquals("Antihistamine", mr.treatmentPlan());
        assertEquals(200.0, mr.estimatedAnnualMedCost());
    }

    @Test
    void testDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        ConnectionFactoryInitializer initializer = config.initializer(connectionFactory);
        assertNotNull(initializer);
    }
}
