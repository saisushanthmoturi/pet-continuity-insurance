export interface CustomerDTO {
  customerId?: number;
  userId: number;
  firstName: string;
  lastName: string;
  email?: string;
  phone: string;
  kycStatus: string;
  status?: string;
  createdAt?: string;
}

export interface AddressDTO {
  addressId?: number;
  customerId: number;
  streetAddress: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isPrimary?: boolean;
}

export interface PetDTO {
  petId?: number;
  customerId: number;
  name: string;
  speciesCode: string;
  breedCode: string;
  gender: string;
  dateOfBirth: string;
  dateOfBirthEstimated?: boolean;
  weightValue: number;
  weightUnit: string;
  microchipId?: string;
  neuteredStatus?: boolean;
  annualCareCost: number;
  expectedRemainingYears: number;
  currency?: string;
  status?: string;
}

export interface MedicalRecordDTO {
  medicalRecordId?: number;
  petId: number;
  recordType: string;
  diagnosis: string;
  treatment: string;
  vetName: string;
  recordDate: string;
  riskLevel: string;
  annualMedCost: number;
  notes: string;
}

export interface CaretakerDTO {
  caretakerId?: number;
  petId: number;
  customerId?: number;
  userId?: number;
  name: string;
  phone: string;
  email: string;
  relationship: string;
  address: string;
  priority: string;
  verificationStatus?: string;
  availabilityStatus?: string;
}

export interface CarePlanDTO {
  carePlanId?: number;
  petId: number;
  primaryCaretakerId?: number;
  backupCaretakerId?: number;
  feedingInstructions: string;
  medicationInstructions: string;
  vetDetails: string;
  routineDetails: string;
  specialRequirements: string;
  status?: string;
}

export interface PolicyDTO {
  policyId?: number;
  policyNumber?: string;
  quoteId?: number;
  customerId: number;
  petId: number;
  coverageAmount: number;
  premiumAmount: number;
  deductible?: number;
  currency?: string;
  startDate?: string;
  endDate?: string;
  status?: string;
}

export interface PremiumPaymentDTO {
  paymentId?: number;
  policyId: number;
  customerId: number;
  amount: number;
  paymentMethod: string;
  paymentReference?: string;
  status?: string;
}
