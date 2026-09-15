package com.example.petservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("pets")
public class Pet {

    @Id
    private Long id;

    @Column("customer_id")
    private Long customerId;

    @Column("name")
    private String name;

    @Column("species")
    private String species;

    @Column("breed")
    private String breed;

    @Column("age")
    private Integer age;

    @Column("weight")
    private Double weight;

    @Column("gender")
    private String gender;

    @Column("estimated_annual_care_cost")
    private Double estimatedAnnualCareCost;

    @Column("created_at")
    private LocalDateTime createdAt;

    public Pet() {
    }

    public Pet(Long id, Long customerId, String name, String species, String breed, Integer age, Double weight, String gender, Double estimatedAnnualCareCost, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.estimatedAnnualCareCost = estimatedAnnualCareCost;
        this.createdAt = createdAt;
    }

    public static Pet createNew(Long customerId, String name, String species, String breed, Integer age, Double weight, String gender, Double estimatedAnnualCareCost) {
        return new Pet(null, customerId, name, species, breed, age, weight, gender, estimatedAnnualCareCost != null ? estimatedAnnualCareCost : 1200.0, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getEstimatedAnnualCareCost() {
        return estimatedAnnualCareCost;
    }

    public void setEstimatedAnnualCareCost(Double estimatedAnnualCareCost) {
        this.estimatedAnnualCareCost = estimatedAnnualCareCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
