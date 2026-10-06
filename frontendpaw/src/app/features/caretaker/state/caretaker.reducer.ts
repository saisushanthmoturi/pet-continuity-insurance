import { createReducer, on } from '@ngrx/store';
import { InitialCaretakerState } from './caretaker.state';
import * as CtActions from './caretaker.actions';

export const caretakerReducer = createReducer(
  InitialCaretakerState,
  on(CtActions.submitVerificationSuccess, (state) => ({
    ...state,
    verificationStatus: { loading: false, success: true, error: '' }
  })),
  on(CtActions.checkEligibilitySuccess, (state, { status }) => ({
    ...state,
    eligibilityStatus: status
  })),
  on(CtActions.loadFundSuccess, (state, { fund }) => ({
    ...state,
    activeFund: fund
  })),
  on(CtActions.requestDisbursementSuccess, (state, { disbursement }) => ({
    ...state,
    disbursements: [disbursement, ...state.disbursements],
    disbursementStatus: { loading: false, success: true, error: '' }
  })),
  on(CtActions.submitExpenseSuccess, (state, { expense }) => ({
    ...state,
    expenses: [expense, ...state.expenses],
    expenseStatus: { loading: false, success: true, error: '' }
  }))
);
