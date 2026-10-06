import { Status } from '../../auth/state/login.state';
import { QuoteDTO, RatingRuleDTO, RiskAssessmentDTO } from '../models/underwriterDTO';

export interface UnderwriterState {
  quotes: QuoteDTO[];
  selectedQuote: QuoteDTO | null;
  riskAssessment: RiskAssessmentDTO | null;
  rules: RatingRuleDTO[];
  queueStatus: Status;
  assessmentStatus: Status;
  ruleStatus: Status;
}

export const InitialUnderwriterState: UnderwriterState = {
  quotes: [],
  selectedQuote: null,
  riskAssessment: null,
  rules: [],
  queueStatus: { loading: false, success: false, error: '' },
  assessmentStatus: { loading: false, success: false, error: '' },
  ruleStatus: { loading: false, success: false, error: '' }
};
