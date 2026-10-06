import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AdminState } from './admin.state';

export const adminState = createFeatureSelector<AdminState>('admin');
export const adminUsersSelector = createSelector(adminState, (state) => state.users);
export const usersSelector = adminUsersSelector;
export const adminPoliciesSelector = createSelector(adminState, (state) => state.policies);
export const globalPoliciesSelector = adminPoliciesSelector;
export const adminFundsSelector = createSelector(adminState, (state) => state.funds);
export const globalFundsSelector = adminFundsSelector;
export const systemHealthySelector = createSelector(adminState, (state) => state.systemHealthy);
export const systemHealthSelector = createSelector(systemHealthySelector, (healthy) => ({ status: healthy ? 'ONLINE' : 'DEGRADED' }));
export const adminStatusSelector = systemHealthySelector;
