import { createAction, props } from '@ngrx/store';
import { ClaimDTO, ClaimDocumentDTO } from '../models/claimDTO';

export const loadClaims = createAction('[CLAIMS] LOAD ALL');
export const loadClaimsSuccess = createAction('[CLAIMS] LOAD ALL SUCCESS', props<{ claims: ClaimDTO[] }>());
export const loadClaimsFailure = createAction('[CLAIMS] LOAD ALL FAILURE', props<{ error: string }>());

export const fileClaim = createAction('[CLAIMS] FILE CLAIM', props<{ claim: ClaimDTO }>());
export const fileClaimSuccess = createAction('[CLAIMS] FILE CLAIM SUCCESS', props<{ claim: ClaimDTO }>());
export const fileClaimFailure = createAction('[CLAIMS] FILE CLAIM FAILURE', props<{ error: string }>());

export const selectClaim = createAction('[CLAIMS] SELECT CLAIM', props<{ claim: ClaimDTO }>());

export const loadDocuments = createAction('[CLAIMS] LOAD DOCUMENTS', props<{ claimId: number }>());
export const loadDocumentsSuccess = createAction('[CLAIMS] LOAD DOCUMENTS SUCCESS', props<{ documents: ClaimDocumentDTO[] }>());

export const uploadDocument = createAction('[CLAIMS] UPLOAD DOCUMENT', props<{ claimId: number; document: ClaimDocumentDTO }>());
export const uploadDocumentSuccess = createAction('[CLAIMS] UPLOAD DOCUMENT SUCCESS', props<{ document: ClaimDocumentDTO }>());

export const verifyDeath = createAction('[CLAIMS] VERIFY EVENT', props<{ claimId: number }>());
export const verifyEvent = verifyDeath;
export const investigateClaim = createAction('[CLAIMS] INVESTIGATE', props<{ claimId: number }>());
export const approveClaim = createAction('[CLAIMS] APPROVE', props<{ claimId: number }>());
export const rejectClaim = createAction('[CLAIMS] REJECT', props<{ claimId: number; reason?: string }>());
export const claimDecisionSuccess = createAction('[CLAIMS] DECISION SUCCESS', props<{ claimId: number; status: string }>());
