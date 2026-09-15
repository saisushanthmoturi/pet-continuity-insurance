package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("caretakers")
public class Caretaker {

    @Id
    private Long id;

    @Column("customer_id")
    private Long customerId;

    @Column("pet_id")
    private Long petId;

    @Column("full_name")
    private String fullName;

    @Column("phone")
    private String phone;

    @Column("email")
    private String email;

    @Column("caretaker_type")
    private String caretakerType; // PRIMARY, BACKUP

    @Column("status")
    private String status; // ACTIVE, UNAVAILABLE

    @Column("address")
    private String address;

    @Column("created_at")
    private LocalDateTime createdAt;

    public Caretaker() {
    }

    public Caretaker(Long id, Long customerId, Long petId, String fullName, String phone, String email, String caretakerType, String status, String address, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.petId = petId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.caretakerType = caretakerType;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
    }

    public static Caretaker create(Long customerId, Long petId, String fullName, String phone, String email, String caretakerType, String address) {
        String type = (caretakerType == null || caretakerType.isBlank()) ? "PRIMARY" : caretakerType.toUpperCase();
        return new Caretaker(null, customerId, petId, fullName, phone, email, type, "ACTIVE", address, LocalDateTime.now());
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaretakerType() {
        return caretakerType;
    }

    public void setCaretakerType(String caretakerType) {
        this.caretakerType = caretakerType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
