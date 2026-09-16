package com.example.careverificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("pet_verifications")
public class PetVerification {

    @Id
    @Column("pet_verification_id")
    private Long petVerificationId;

    @Column("pet_id")
    private Long petId;

    @Column("caretaker_id")
    private Long caretakerId;

    @Column("verification_method")
    private String verificationMethod = "MOBILE_CHECKIN";

    @Column("verification_status")
    private String verificationStatus = "PASSED";

    @Column("evidence_reference")
    private String evidenceReference;

    @Column("verified_at")
    private LocalDateTime verifiedAt = LocalDateTime.now();

    @Column("next_verification_date")
    private LocalDate nextVerificationDate;

    @Column("remarks")
    private String remarks;

    public PetVerification() {
        this.verificationMethod = "MOBILE_CHECKIN";
        this.verificationStatus = "PASSED";
        this.verifiedAt = LocalDateTime.now();
    }

    public PetVerification(Long petVerificationId, Long petId, Long caretakerId,
                           String verificationMethod, String verificationStatus,
                           String evidenceReference, LocalDateTime verifiedAt,
                           LocalDate nextVerificationDate, String remarks) {
        this.petVerificationId = petVerificationId;
        this.petId = petId;
        this.caretakerId = caretakerId;
        this.verificationMethod = verificationMethod != null ? verificationMethod : "MOBILE_CHECKIN";
        this.verificationStatus = verificationStatus != null ? verificationStatus : "PASSED";
        this.evidenceReference = evidenceReference;
        this.verifiedAt = verifiedAt != null ? verifiedAt : LocalDateTime.now();
        this.nextVerificationDate = nextVerificationDate;
        this.remarks = remarks;
    }

    public static PetVerification create(Long petId, Long caretakerId, String verificationDate, String status, String notes) {
        PetVerification pv = new PetVerification();
        pv.setPetId(petId);
        pv.setCaretakerId(caretakerId);
        pv.setVerificationMethod("MOBILE_CHECKIN");
        pv.setVerificationStatus(status != null && !status.isBlank() ? status.toUpperCase() : "PASSED");
        pv.setRemarks(notes);
        pv.setVerifiedAt(LocalDateTime.now());
        pv.setNextVerificationDate(LocalDate.now().plusMonths(1));
        return pv;
    }

    public Long getId() {
        return petVerificationId;
    }

    public void setId(Long id) {
        this.petVerificationId = id;
    }

    public Long getPetVerificationId() {
        return petVerificationId;
    }

    public void setPetVerificationId(Long petVerificationId) {
        this.petVerificationId = petVerificationId;
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

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getStatus() {
        return verificationStatus;
    }

    public void setStatus(String status) {
        this.verificationStatus = status;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return verifiedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.verifiedAt = createdAt;
    }

    public String getVerificationDate() {
        return verifiedAt != null ? verifiedAt.toLocalDate().toString() : null;
    }

    public void setVerificationDate(String verificationDate) {
        // no-op or parse if needed
    }

    public LocalDate getNextVerificationDate() {
        return nextVerificationDate;
    }

    public void setNextVerificationDate(LocalDate nextVerificationDate) {
        this.nextVerificationDate = nextVerificationDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNotes() {
        return remarks;
    }

    public void setNotes(String notes) {
        this.remarks = notes;
    }
}
