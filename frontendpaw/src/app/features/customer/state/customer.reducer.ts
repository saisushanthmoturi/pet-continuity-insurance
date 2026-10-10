import { createReducer, on } from '@ngrx/store';
import { InitialCustomerState } from './customer.state';
import * as CustActions from './customer.actions';

export const customerReducer = createReducer(
  InitialCustomerState,
  on(CustActions.resetCustomerState, () => InitialCustomerState),
  on(CustActions.loadCustomer, (state) => ({
    ...state,
    profileStatus: { loading: true, success: false, error: '' }
  })),
  on(CustActions.loadCustomerSuccess, (state, { customer }) => ({
    ...state,
    profile: customer,
    profileStatus: { loading: false, success: true, error: '' }
  })),
  on(CustActions.loadCustomerFailure, (state, { error }) => ({
    ...state,
    profileStatus: { loading: false, success: false, error }
  })),
  on(CustActions.saveCustomerSuccess, (state, { customer }) => ({
    ...state,
    profile: customer,
    profileStatus: { loading: false, success: true, error: '' }
  })),
  on(CustActions.loadAddressesSuccess, (state, { addresses }) => ({
    ...state,
    addresses
  })),
  on(CustActions.addAddressSuccess, (state, { address }) => ({
    ...state,
    addresses: [...state.addresses, address]
  })),
  on(CustActions.loadPets, (state) => ({
    ...state,
    petStatus: { loading: true, success: false, error: '' }
  })),
  on(CustActions.loadPetsSuccess, (state, { pets }) => ({
    ...state,
    pets,
    petStatus: { loading: false, success: true, error: '' }
  })),
  on(CustActions.createPetSuccess, (state, { pet }) => ({
    ...state,
    pets: [...state.pets, pet],
    selectedPet: pet,
    petStatus: { loading: false, success: true, error: '' }
  })),
  on(CustActions.selectPet, (state, { pet }) => ({
    ...state,
    selectedPet: pet
  })),
  on(CustActions.loadMedicalRecordsSuccess, (state, { records }) => ({
    ...state,
    medicalRecords: records
  })),
  on(CustActions.addMedicalRecordSuccess, (state, { record }) => ({
    ...state,
    medicalRecords: [...state.medicalRecords, record]
  })),
  on(CustActions.loadCaretakersSuccess, (state, { caretakers }) => ({
    ...state,
    caretakers
  })),
  on(CustActions.addCaretakerSuccess, (state, { caretaker }) => ({
    ...state,
    caretakers: [...state.caretakers, caretaker]
  })),
  on(CustActions.loadCarePlanSuccess, (state, { plan }) => ({
    ...state,
    carePlan: plan
  })),
  on(CustActions.createCarePlanSuccess, (state, { plan }) => ({
    ...state,
    carePlan: plan
  })),
  on(CustActions.loadPoliciesSuccess, (state, { policies }) => ({
    ...state,
    policies
  })),
  on(CustActions.issuePolicySuccess, (state, { policy }) => ({
    ...state,
    policies: [...state.policies, policy]
  })),
  on(CustActions.payPremiumSuccess, (state) => ({
    ...state,
    paymentStatus: { loading: false, success: true, error: '' }
  }))
);
