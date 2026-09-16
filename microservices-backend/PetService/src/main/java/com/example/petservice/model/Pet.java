package com.example.petservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("pets")
public class Pet {

    @Id
    @Column("pet_id")
    private Long id;

    @Column("customer_id")
    private Long customerId;

    @Column("name")
    private String name;

    @Column("species")
    private String species;

    @Column("breed")
    private String breed;

    @Column("gender")
    private String gender;

    @Column("date_of_birth")
    private LocalDate dateOfBirth;

    @Column("weight")
    private Double weight;

    @Column("microchip_id")
    private String microchipId;

    @Column("medical_risk")
    private String medicalRisk = "LOW";

    @Column("annual_care_cost")
    private Double annualCareCost = 1200.0;

    @Column("expected_remaining_years")
    private Integer expectedRemainingYears = 10;

    @Column("status")
    private String status = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private transient Integer age;

    public Pet() {
    }

    public Pet(Long id, Long customerId, String name, String species, String breed, String gender, LocalDate dateOfBirth, Double weight, String microchipId, String medicalRisk, Double annualCareCost, Integer expectedRemainingYears, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.microchipId = microchipId;
        this.medicalRisk = medicalRisk != null ? medicalRisk : "LOW";
        this.annualCareCost = annualCareCost != null ? annualCareCost : 1200.0;
        this.expectedRemainingYears = expectedRemainingYears != null ? expectedRemainingYears : 10;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static Pet createNew(Long customerId, String name, String species, String breed, Integer age, Double weight, String gender, Double estimatedAnnualCareCost) {
        LocalDateTime now = LocalDateTime.now();
        int petAge = age != null ? age : 3;
        LocalDate dob = LocalDate.now().minusYears(petAge);
        Pet p = new Pet(null, customerId, name, species, breed, gender, dob, weight, null, "LOW", estimatedAnnualCareCost != null ? estimatedAnnualCareCost : 1200.0, Math.max(1, 15 - petAge), "ACTIVE", now, now);
        p.setAge(petAge);
        return p;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return id;
    }

    public void setPetId(Long petId) {
        this.id = petId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getMicrochipId() {
        return microchipId;
    }

    public void setMicrochipId(String microchipId) {
        this.microchipId = microchipId;
    }

    public String getMedicalRisk() {
        return medicalRisk;
    }

    public void setMedicalRisk(String medicalRisk) {
        this.medicalRisk = medicalRisk;
    }

    public Double getAnnualCareCost() {
        return annualCareCost;
    }

    public void setAnnualCareCost(Double annualCareCost) {
        this.annualCareCost = annualCareCost;
    }

    public Double getEstimatedAnnualCareCost() {
        return annualCareCost;
    }

    public void setEstimatedAnnualCareCost(Double estimatedAnnualCareCost) {
        this.annualCareCost = estimatedAnnualCareCost;
    }

    public Integer getExpectedRemainingYears() {
        return expectedRemainingYears;
    }

    public void setExpectedRemainingYears(Integer expectedRemainingYears) {
        this.expectedRemainingYears = expectedRemainingYears;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAge() {
        if (dateOfBirth != null) {
            return Math.max(0, LocalDate.now().getYear() - dateOfBirth.getYear());
        }
        return age != null ? age : 3;
    }

    public void setAge(Integer age) {
        this.age = age;
        if (age != null) {
            this.dateOfBirth = LocalDate.now().minusYears(age);
            this.expectedRemainingYears = Math.max(1, 15 - age);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
