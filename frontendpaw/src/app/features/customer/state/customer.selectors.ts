import { createFeatureSelector, createSelector } from '@ngrx/store';
import { CustomerState } from './customer.state';

export const customerState = createFeatureSelector<CustomerState>('customer');
export const customerProfileSelector = createSelector(customerState, (state) => state.profile);
export const petsSelector = createSelector(customerState, (state) => state.pets);
export const selectedPetSelector = createSelector(customerState, (state) => state.selectedPet);
export const medicalRecordsSelector = createSelector(customerState, (state) => state.medicalRecords);
export const caretakersSelector = createSelector(customerState, (state) => state.caretakers);
export const carePlanSelector = createSelector(customerState, (state) => state.carePlan);
export const policiesSelector = createSelector(customerState, (state) => state.policies);
export const customerStatusSelector = createSelector(customerState, (state) => state.profileStatus);
