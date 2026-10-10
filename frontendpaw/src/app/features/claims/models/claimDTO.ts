export interface ClaimDTO {
  claimId?: number;
  claimNumber?: string;
  policyId: number;
  policyNumber?: string;
  customerId?: number;
  customerEmail?: string;
  customerName?: string;
  claimantId?: number;
  claimantName?: string;
  relationship?: string;
  deathCertificateNo?: string;
  dateOfDeath?: string;
  petId?: number;
  eventType?: string;
  claimType?: string;
  requestedAmount?: number;
  claimAmount?: number;
  approvedAmount?: number;
  status?: string;
  decisionReason?: string;
  rejectionReason?: string;
  eventDate?: string;
  description?: string;
  notes?: string;
  filedAt?: string;
  createdAt?: string;
  investigationDecision?: string;
  fraudScore?: number;
}

export interface ClaimDocumentDTO {
  documentId?: number;
  claimId: number;
  documentType: string;
  documentName?: string;
  fileName?: string;
  filePath?: string;
  fileReference?: string;
  documentUrl?: string;
  notes?: string;
  uploadedAt?: string;
}

export interface EventVerificationDTO {
  verificationId?: number;
  claimId: number;
  eventType: string;
  verificationMethod?: string;
  verificationStatus: string;
  verifiedBy?: string;
  remarks?: string;
}
