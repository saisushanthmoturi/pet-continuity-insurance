import { createReducer, on } from '@ngrx/store';
import { InitialUnderwriterState } from './underwriter.state';
import * as UwActions from './underwriter.actions';

export const underwriterReducer = createReducer(
  InitialUnderwriterState,
  on(UwActions.loadQuotes, (state) => ({
    ...state,
    queueStatus: { loading: true, success: false, error: '' }
  })),
  on(UwActions.loadQuotesSuccess, (state, { quotes }) => ({
    ...state,
    quotes,
    queueStatus: { loading: false, success: true, error: '' }
  })),
  on(UwActions.requestQuoteSuccess, (state, { quote }) => ({
    ...state,
    quotes: [quote, ...state.quotes],
    selectedQuote: quote
  })),
  on(UwActions.selectQuote, (state, { quote }) => ({
    ...state,
    selectedQuote: quote
  })),
  on(UwActions.loadAssessmentSuccess, (state, { assessment }) => ({
    ...state,
    riskAssessment: assessment
  })),
  on(UwActions.updateQuoteDecisionSuccess, (state, { quote }) => ({
    ...state,
    quotes: state.quotes.map((q) => (q.quoteId === quote.quoteId ? quote : q)),
    selectedQuote: quote
  })),
  on(UwActions.loadRatingRulesSuccess, (state, { rules }) => ({
    ...state,
    rules
  })),
  on(UwActions.addRatingRuleSuccess, (state, { rule }) => ({
    ...state,
    rules: [...state.rules, rule]
  }))
);
