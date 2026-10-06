import { createReducer, on } from '@ngrx/store';
import { InitialAdminState } from './admin.state';
import * as AdminActions from './admin.actions';

export const adminReducer = createReducer(
  InitialAdminState,
  on(AdminActions.loadUsersSuccess, (state, { users }) => ({
    ...state,
    users
  })),
  on(AdminActions.loadPoliciesSuccess, (state, { policies }) => ({
    ...state,
    policies
  })),
  on(AdminActions.loadFundsSuccess, (state, { funds }) => ({
    ...state,
    funds
  })),
  on(AdminActions.setHealthStatus, (state, { healthy }) => ({
    ...state,
    systemHealthy: healthy
  }))
);
