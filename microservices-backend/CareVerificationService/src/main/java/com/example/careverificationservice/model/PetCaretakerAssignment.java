package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("pet_caretaker_assignments")
public class PetCaretakerAssignment {

    @Id
    @Column("assignment_id")
    private Long assignmentId;

    @Column("pet_id")
    private Long petId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("priority")
    private Integer priority = 1;

    @Column("caretaker_type")
    private String caretakerType = "PRIMARY";

    @Column("relationship")
    private String relationship;

    @Column("availability_status")
    private String availabilityStatus = "AVAILABLE";

    @Column("assignment_status")
    private String assignmentStatus = "ACTIVE";

    @Column("nominated_by")
    private Long nominatedBy;

    @Column("effective_from")
    private LocalDate effectiveFrom = LocalDate.now();

    @Column("effective_to")
    private LocalDate effectiveTo;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column("version")
    private Integer version = 1;

    public PetCaretakerAssignment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.effectiveFrom = LocalDate.now();
        this.version = 1;
    }

    public PetCaretakerAssignment(Long assignmentId, Long petId, Long caretakerId, Integer priority,
                                  String caretakerType, String relationship, String availabilityStatus,
                                  String assignmentStatus, Long nominatedBy, LocalDate effectiveFrom,
                                  LocalDate effectiveTo, LocalDateTime createdAt, LocalDateTime updatedAt,
                                  Integer version) {
        this.assignmentId = assignmentId;
        this.petId = petId;
        this.caretakerId = caretakerId;
        this.priority = priority != null ? priority : 1;
        this.caretakerType = caretakerType != null ? caretakerType : "PRIMARY";
        this.relationship = relationship;
        this.availabilityStatus = availabilityStatus != null ? availabilityStatus : "AVAILABLE";
        this.assignmentStatus = assignmentStatus != null ? assignmentStatus : "ACTIVE";
        this.nominatedBy = nominatedBy;
        this.effectiveFrom = effectiveFrom != null ? effectiveFrom : LocalDate.now();
        this.effectiveTo = effectiveTo;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.version = version != null ? version : 1;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCaretakerType() {
        return caretakerType;
    }

    public void setCaretakerType(String caretakerType) {
        this.caretakerType = caretakerType;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public Long getNominatedBy() {
        return nominatedBy;
    }

    public void setNominatedBy(Long nominatedBy) {
        this.nominatedBy = nominatedBy;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
