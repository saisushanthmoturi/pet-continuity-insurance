package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("caretakers")
public class Caretaker {

    @Id
    @Column("caretaker_id")
    private Long caretakerId;

    @Column("pet_id")
    private Long petId;

    @Column("name")
    private String name;

    @Column("phone")
    private String phone;

    @Column("email")
    private String email;

    @Column("relationship")
    private String relationship = "PRIMARY";

    @Column("address")
    private String address;

    @Column("priority")
    private Integer priority = 1;

    @Column("verification_status")
    private String verificationStatus = "VERIFIED";

    @Column("availability_status")
    private String availabilityStatus = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Transient
    private Long customerId;

    public Caretaker() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.priority = 1;
        this.verificationStatus = "VERIFIED";
        this.availabilityStatus = "ACTIVE";
    }

    public Caretaker(Long caretakerId, Long petId, String name, String phone, String email,
                     String relationship, String address, Integer priority,
                     String verificationStatus, String availabilityStatus,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.caretakerId = caretakerId;
        this.petId = petId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.relationship = relationship != null ? relationship : "PRIMARY";
        this.address = address;
        this.priority = priority != null ? priority : 1;
        this.verificationStatus = verificationStatus != null ? verificationStatus : "VERIFIED";
        this.availabilityStatus = availabilityStatus != null ? availabilityStatus : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static Caretaker create(Long customerId, Long petId, String fullName, String phone, String email, String caretakerType, String address) {
        Caretaker c = new Caretaker();
        c.setCustomerId(customerId);
        c.setPetId(petId);
        c.setName(fullName);
        c.setPhone(phone);
        c.setEmail(email);
        c.setRelationship(caretakerType != null && !caretakerType.isBlank() ? caretakerType.toUpperCase() : "PRIMARY");
        c.setAddress(address);
        c.setPriority("PRIMARY".equalsIgnoreCase(caretakerType) ? 1 : 2);
        c.setVerificationStatus("VERIFIED");
        c.setAvailabilityStatus("ACTIVE");
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }

    public Long getId() {
        return caretakerId;
    }

    public void setId(Long id) {
        this.caretakerId = id;
    }

    public Long getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
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

    public String getFullName() {
        return name;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getCaretakerType() {
        return relationship;
    }

    public void setCaretakerType(String caretakerType) {
        this.relationship = caretakerType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getStatus() {
        return availabilityStatus;
    }

    public void setStatus(String status) {
        this.availabilityStatus = status;
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
