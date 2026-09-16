package com.example.customerservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("customers")
public class Customer {

    @Id
    @Column("customer_id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("date_of_birth")
    private LocalDate dateOfBirth;

    @Column("address_id")
    private Long addressId;

    @Column("status")
    private String status = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // In-memory support for legacy fields
    private transient String address;
    private transient String emergencyContact;

    public Customer() {
    }

    public Customer(Long id, Long userId, String firstName, String lastName, String email, String phone, LocalDate dateOfBirth, Long addressId, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.addressId = addressId;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static Customer createNew(Long userId, String fullName, String email, String phone, String address, String emergencyContact) {
        String fName = fullName;
        String lName = "";
        if (fullName != null && fullName.trim().contains(" ")) {
            int idx = fullName.trim().lastIndexOf(" ");
            fName = fullName.trim().substring(0, idx);
            lName = fullName.trim().substring(idx + 1);
        } else if (fullName != null) {
            fName = fullName.trim();
        }
        LocalDateTime now = LocalDateTime.now();
        Customer c = new Customer(null, userId, fName, lName, email, phone, LocalDate.of(1990, 1, 1), 1L, "ACTIVE", now, now);
        c.setAddress(address);
        c.setEmergencyContact(emergencyContact);
        return c;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return id;
    }

    public void setCustomerId(Long customerId) {
        this.id = customerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        if (firstName == null) return lastName != null ? lastName : "";
        if (lastName == null || lastName.isBlank()) return firstName;
        return firstName + " " + lastName;
    }

    public void setFullName(String fullName) {
        if (fullName != null && fullName.trim().contains(" ")) {
            int idx = fullName.trim().lastIndexOf(" ");
            this.firstName = fullName.trim().substring(0, idx);
            this.lastName = fullName.trim().substring(idx + 1);
        } else {
            this.firstName = fullName;
            this.lastName = "";
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
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

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
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
