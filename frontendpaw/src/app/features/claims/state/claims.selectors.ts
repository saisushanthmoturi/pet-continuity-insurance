import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ClaimsState } from './claims.state';

export const claimsState = createFeatureSelector<ClaimsState>('claims');
export const allClaimsSelector = createSelector(claimsState, (state) => state.claims);
export const claimsSelector = allClaimsSelector;
export const selectedClaimSelector = createSelector(claimsState, (state) => state.selectedClaim);
export const claimDocumentsSelector = createSelector(claimsState, (state) => state.documents);
export const claimsQueueStatusSelector = createSelector(claimsState, (state) => state.claimsQueueStatus);
export const claimsStatusSelector = claimsQueueStatusSelector;
