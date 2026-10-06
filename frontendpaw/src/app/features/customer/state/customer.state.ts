import { Status } from '../../auth/state/login.state';
import {
  AddressDTO,
  CarePlanDTO,
  CaretakerDTO,
  CustomerDTO,
  MedicalRecordDTO,
  PetDTO,
  PolicyDTO
} from '../models/customerDTO';

export interface CustomerState {
  profile: CustomerDTO | null;
  addresses: AddressDTO[];
  pets: PetDTO[];
  selectedPet: PetDTO | null;
  medicalRecords: MedicalRecordDTO[];
  caretakers: CaretakerDTO[];
  carePlan: CarePlanDTO | null;
  policies: PolicyDTO[];
  profileStatus: Status;
  petStatus: Status;
  medicalStatus: Status;
  caretakerStatus: Status;
  carePlanStatus: Status;
  policyStatus: Status;
  paymentStatus: Status;
}

export const InitialCustomerState: CustomerState = {
  profile: null,
  addresses: [],
  pets: [],
  selectedPet: null,
  medicalRecords: [],
  caretakers: [],
  carePlan: null,
  policies: [],
  profileStatus: { loading: false, success: false, error: '' },
  petStatus: { loading: false, success: false, error: '' },
  medicalStatus: { loading: false, success: false, error: '' },
  caretakerStatus: { loading: false, success: false, error: '' },
  carePlanStatus: { loading: false, success: false, error: '' },
  policyStatus: { loading: false, success: false, error: '' },
  paymentStatus: { loading: false, success: false, error: '' }
};
