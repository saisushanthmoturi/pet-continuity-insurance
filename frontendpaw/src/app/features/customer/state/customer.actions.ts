import { createAction, props } from '@ngrx/store';
import {
  AddressDTO,
  CarePlanDTO,
  CaretakerDTO,
  CustomerDTO,
  MedicalRecordDTO,
  PetDTO,
  PolicyDTO,
  PremiumPaymentDTO
} from '../models/customerDTO';

// Customer Profile
export const loadCustomer = createAction('[CUSTOMER] LOAD PROFILE', props<{ userId: number }>());
export const loadCustomerSuccess = createAction('[CUSTOMER] LOAD PROFILE SUCCESS', props<{ customer: CustomerDTO }>());
export const loadCustomerFailure = createAction('[CUSTOMER] LOAD PROFILE FAILURE', props<{ error: string }>());

export const saveCustomer = createAction('[CUSTOMER] SAVE PROFILE', props<{ customer: CustomerDTO }>());
export const saveCustomerSuccess = createAction('[CUSTOMER] SAVE PROFILE SUCCESS', props<{ customer: CustomerDTO }>());
export const saveCustomerFailure = createAction('[CUSTOMER] SAVE PROFILE FAILURE', props<{ error: string }>());

// Addresses
export const loadAddresses = createAction('[CUSTOMER] LOAD ADDRESSES', props<{ customerId: number }>());
export const loadAddressesSuccess = createAction('[CUSTOMER] LOAD ADDRESSES SUCCESS', props<{ addresses: AddressDTO[] }>());
export const addAddress = createAction('[CUSTOMER] ADD ADDRESS', props<{ customerId: number; address: AddressDTO }>());
export const addAddressSuccess = createAction('[CUSTOMER] ADD ADDRESS SUCCESS', props<{ address: AddressDTO }>());

// Pets
export const loadPets = createAction('[CUSTOMER] LOAD PETS', props<{ customerId: number }>());
export const loadPetsSuccess = createAction('[CUSTOMER] LOAD PETS SUCCESS', props<{ pets: PetDTO[] }>());
export const loadPetsFailure = createAction('[CUSTOMER] LOAD PETS FAILURE', props<{ error: string }>());

export const createPet = createAction('[CUSTOMER] CREATE PET', props<{ pet: PetDTO }>());
export const createPetSuccess = createAction('[CUSTOMER] CREATE PET SUCCESS', props<{ pet: PetDTO }>());
export const createPetFailure = createAction('[CUSTOMER] CREATE PET FAILURE', props<{ error: string }>());

export const selectPet = createAction('[CUSTOMER] SELECT PET', props<{ pet: PetDTO }>());

// Medical Records
export const loadMedicalRecords = createAction('[CUSTOMER] LOAD MEDICAL', props<{ petId: number }>());
export const loadMedicalRecordsSuccess = createAction('[CUSTOMER] LOAD MEDICAL SUCCESS', props<{ records: MedicalRecordDTO[] }>());
export const addMedicalRecord = createAction('[CUSTOMER] ADD MEDICAL', props<{ petId: number; record: MedicalRecordDTO }>());
export const addMedicalRecordSuccess = createAction('[CUSTOMER] ADD MEDICAL SUCCESS', props<{ record: MedicalRecordDTO }>());

// Caretakers & Care Plan
export const loadCaretakers = createAction('[CUSTOMER] LOAD CARETAKERS', props<{ petId: number }>());
export const loadCaretakersSuccess = createAction('[CUSTOMER] LOAD CARETAKERS SUCCESS', props<{ caretakers: CaretakerDTO[] }>());
export const addCaretaker = createAction('[CUSTOMER] ADD CARETAKER', props<{ caretaker: CaretakerDTO }>());
export const addCaretakerSuccess = createAction('[CUSTOMER] ADD CARETAKER SUCCESS', props<{ caretaker: CaretakerDTO }>());

export const loadCarePlan = createAction('[CUSTOMER] LOAD CARE PLAN', props<{ petId: number }>());
export const loadCarePlanSuccess = createAction('[CUSTOMER] LOAD CARE PLAN SUCCESS', props<{ plan: CarePlanDTO }>());
export const createCarePlan = createAction('[CUSTOMER] CREATE CARE PLAN', props<{ plan: CarePlanDTO }>());
export const createCarePlanSuccess = createAction('[CUSTOMER] CREATE CARE PLAN SUCCESS', props<{ plan: CarePlanDTO }>());

// Policies & Payments
export const loadPolicies = createAction('[CUSTOMER] LOAD POLICIES', props<{ customerId: number }>());
export const loadPoliciesSuccess = createAction('[CUSTOMER] LOAD POLICIES SUCCESS', props<{ policies: PolicyDTO[] }>());
export const issuePolicy = createAction('[CUSTOMER] ISSUE POLICY', props<{ quoteId: number }>());
export const issuePolicySuccess = createAction('[CUSTOMER] ISSUE POLICY SUCCESS', props<{ policy: PolicyDTO }>());
export const payPremium = createAction('[CUSTOMER] PAY PREMIUM', props<{ payment: PremiumPaymentDTO }>());
export const payPremiumSuccess = createAction('[CUSTOMER] PAY PREMIUM SUCCESS');
