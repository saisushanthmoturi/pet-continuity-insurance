import { createFeatureSelector, createSelector } from '@ngrx/store';
import { CaretakerState } from './caretaker.state';

export const caretakerState = createFeatureSelector<CaretakerState>('caretaker');
export const activeFundSelector = createSelector(caretakerState, (state) => state.activeFund);
export const disbursementsSelector = createSelector(caretakerState, (state) => state.disbursements);
export const expensesSelector = createSelector(caretakerState, (state) => state.expenses);
export const eligibilityStatusSelector = createSelector(caretakerState, (state) => state.eligibilityStatus);
export const caretakerStatusSelector = eligibilityStatusSelector;
