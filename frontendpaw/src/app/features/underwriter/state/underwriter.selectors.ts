import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UnderwriterState } from './underwriter.state';

export const underwriterState = createFeatureSelector<UnderwriterState>('underwriter');
export const allQuotesSelector = createSelector(underwriterState, (state) => state.quotes);
export const quotesSelector = allQuotesSelector;
export const selectedQuoteSelector = createSelector(underwriterState, (state) => state.selectedQuote);
export const riskAssessmentSelector = createSelector(underwriterState, (state) => state.riskAssessment);
export const ratingRulesSelector = createSelector(underwriterState, (state) => state.rules);
export const underwriterQueueStatusSelector = createSelector(underwriterState, (state) => state.queueStatus);
export const underwriterStatusSelector = underwriterQueueStatusSelector;
