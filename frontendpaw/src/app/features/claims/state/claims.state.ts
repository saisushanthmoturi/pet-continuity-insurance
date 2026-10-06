import { Status } from '../../auth/state/login.state';
import { ClaimDTO, ClaimDocumentDTO } from '../models/claimDTO';

export interface ClaimsState {
  claims: ClaimDTO[];
  selectedClaim: ClaimDTO | null;
  documents: ClaimDocumentDTO[];
  claimsQueueStatus: Status;
  claimFilingStatus: Status;
  decisionStatus: Status;
}

export const InitialClaimsState: ClaimsState = {
  claims: [],
  selectedClaim: null,
  documents: [],
  claimsQueueStatus: { loading: false, success: false, error: '' },
  claimFilingStatus: { loading: false, success: false, error: '' },
  decisionStatus: { loading: false, success: false, error: '' }
};
