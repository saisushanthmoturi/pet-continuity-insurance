import { createAction, props } from '@ngrx/store';
import { QuoteDTO, RatingRuleDTO, RiskAssessmentDTO } from '../models/underwriterDTO';

export const loadQuotes = createAction('[UNDERWRITER] LOAD QUOTES');
export const loadQuotesSuccess = createAction('[UNDERWRITER] LOAD QUOTES SUCCESS', props<{ quotes: QuoteDTO[] }>());
export const loadQuotesFailure = createAction('[UNDERWRITER] LOAD QUOTES FAILURE', props<{ error: string }>());

export const requestQuote = createAction('[UNDERWRITER] REQUEST QUOTE', props<{ quote: QuoteDTO }>());
export const requestQuoteSuccess = createAction('[UNDERWRITER] REQUEST QUOTE SUCCESS', props<{ quote: QuoteDTO }>());

export const selectQuote = createAction('[UNDERWRITER] SELECT QUOTE', props<{ quote: QuoteDTO }>());

export const loadAssessment = createAction('[UNDERWRITER] LOAD ASSESSMENT', props<{ quoteId: number }>());
export const loadAssessmentSuccess = createAction('[UNDERWRITER] LOAD ASSESSMENT SUCCESS', props<{ assessment: RiskAssessmentDTO }>());

export const updateQuoteDecision = createAction('[UNDERWRITER] UPDATE DECISION', props<{ quoteId: number; decision: string; reason: string }>());
export const updateQuoteDecisionSuccess = createAction('[UNDERWRITER] UPDATE DECISION SUCCESS', props<{ quote: QuoteDTO }>());

export const approveQuote = createAction('[UNDERWRITER] APPROVE QUOTE', props<{ quoteId: number }>());
export const rejectQuote = createAction('[UNDERWRITER] REJECT QUOTE', props<{ quoteId: number; reason: string }>());

export const reassessPetRisk = createAction('[UNDERWRITER] REASSESS PET', props<{ petId: number }>());
export const reassessPetRiskSuccess = createAction('[UNDERWRITER] REASSESS PET SUCCESS');

export const loadRatingRules = createAction('[UNDERWRITER] LOAD RULES');
export const loadRatingRulesSuccess = createAction('[UNDERWRITER] LOAD RULES SUCCESS', props<{ rules: RatingRuleDTO[] }>());
export const addRatingRule = createAction('[UNDERWRITER] ADD RULE', props<{ rule: RatingRuleDTO }>());
export const addRatingRuleSuccess = createAction('[UNDERWRITER] ADD RULE SUCCESS', props<{ rule: RatingRuleDTO }>());
export const createRatingRule = addRatingRule;
