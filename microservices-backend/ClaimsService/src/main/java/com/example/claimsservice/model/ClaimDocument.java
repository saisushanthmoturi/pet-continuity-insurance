package com.example.claimsservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("claim_documents")
public class ClaimDocument {

    @Id
    @Column("document_id")
    private Long documentId;

    @Column("claim_id")
    private Long claimId;

    @Column("document_type")
    private String documentType;

    @Column("file_name")
    private String fileName;

    @Column("file_reference")
    private String fileReference;

    @Column("verification_status")
    private String verificationStatus = "VERIFIED";

    @Column("uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public ClaimDocument() {
        this.uploadedAt = LocalDateTime.now();
        this.verificationStatus = "VERIFIED";
    }

    public ClaimDocument(Long documentId, Long claimId, String documentType, String fileName,
                         String fileReference, String verificationStatus, LocalDateTime uploadedAt) {
        this.documentId = documentId;
        this.claimId = claimId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.fileReference = fileReference;
        this.verificationStatus = verificationStatus != null ? verificationStatus : "VERIFIED";
        this.uploadedAt = uploadedAt != null ? uploadedAt : LocalDateTime.now();
    }

    public Long getId() {
        return documentId;
    }

    public void setId(Long id) {
        this.documentId = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
