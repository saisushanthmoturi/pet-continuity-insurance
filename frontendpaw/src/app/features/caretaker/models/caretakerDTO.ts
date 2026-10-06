export interface PetVerificationDTO {
  petVerificationId?: number;
  petId: number;
  caretakerId: number;
  verificationMethod?: string;
  verificationType?: string;
  verificationStatus?: string;
  status?: string;
  evidenceReference?: string;
  verifiedAt?: string;
  remarks?: string;
  notes?: string;
}

export interface PetCareFundDTO {
  fundId?: number;
  policyId: number;
  petId?: number;
  customerId?: number;
  caretakerId?: number;
  totalAmount?: number;
  totalAllocated?: number;
  availableAmount?: number;
  currentBalance?: number;
  monthlyAllowance?: number;
  veterinaryReserve?: number;
  emergencyReserve?: number;
  status?: string;
}

export interface DisbursementDTO {
  disbursementId?: number;
  fundId: number;
  caretakerId: number;
  amount: number;
  disbursementType?: string;
  status?: string;
  scheduledDate?: string;
  processedAt?: string;
}

export interface ExpenseDTO {
  expenseId?: number;
  fundId: number;
  caretakerId?: number;
  expenseType?: string;
  category?: string;
  amount: number;
  vendorName?: string;
  documentReference?: string;
  receiptUrl?: string;
  description?: string;
  approvalStatus?: string;
  submittedAt?: string;
}

export interface BackupTransferDTO {
  transferId?: number;
  petId: number;
  primaryCaretakerId?: number;
  currentCaretakerId?: number;
  backupCaretakerId: number;
  reason: string;
  status?: string;
}
