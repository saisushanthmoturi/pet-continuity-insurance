import { createReducer, on } from '@ngrx/store';
import { InitialClaimsState } from './claims.state';
import * as ClaimsActions from './claims.actions';

export const claimsReducer = createReducer(
  InitialClaimsState,
  on(ClaimsActions.loadClaims, (state) => ({
    ...state,
    claimsQueueStatus: { loading: true, success: false, error: '' }
  })),
  on(ClaimsActions.loadClaimsSuccess, (state, { claims }) => ({
    ...state,
    claims,
    claimsQueueStatus: { loading: false, success: true, error: '' }
  })),
  on(ClaimsActions.fileClaim, (state) => ({
    ...state,
    claimFilingStatus: { loading: true, success: false, error: '' }
  })),
  on(ClaimsActions.fileClaimSuccess, (state, { claim }) => ({
    ...state,
    claims: [claim, ...state.claims],
    selectedClaim: claim,
    claimFilingStatus: { loading: false, success: true, error: '' }
  })),
  on(ClaimsActions.selectClaim, (state, { claim }) => ({
    ...state,
    selectedClaim: claim
  })),
  on(ClaimsActions.loadDocumentsSuccess, (state, { documents }) => ({
    ...state,
    documents
  })),
  on(ClaimsActions.uploadDocumentSuccess, (state, { document }) => ({
    ...state,
    documents: [...state.documents, document]
  })),
  on(ClaimsActions.claimDecisionSuccess, (state, { claimId, status }) => ({
    ...state,
    claims: state.claims.map((c) => (c.claimId === claimId ? { ...c, status } : c)),
    selectedClaim: state.selectedClaim?.claimId === claimId ? { ...state.selectedClaim, status } : state.selectedClaim
  }))
);
