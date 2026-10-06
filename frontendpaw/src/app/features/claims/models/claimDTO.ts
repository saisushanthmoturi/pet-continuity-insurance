export interface ClaimDTO {
  claimId?: number;
  claimNumber?: string;
  policyId: number;
  customerId?: number;
  claimantId?: number;
  petId: number;
  eventType: string;
  claimType?: string;
  requestedAmount?: number;
  claimAmount?: number;
  approvedAmount?: number;
  status?: string;
  decisionReason?: string;
  eventDate?: string;
  description?: string;
  filedAt?: string;
}

export interface ClaimDocumentDTO {
  documentId?: number;
  claimId: number;
  documentType: string;
  documentName?: string;
  filePath?: string;
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
