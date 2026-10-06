import { createAction, props } from '@ngrx/store';
import { UserDTO } from '../models/adminDTO';

export const loadAdminData = createAction('[ADMIN] LOAD DATA');
export const loadUsers = loadAdminData;
export const loadGlobalPolicies = loadAdminData;
export const loadGlobalFunds = loadAdminData;
export const checkSystemHealth = loadAdminData;

export const loadUsersSuccess = createAction('[ADMIN] LOAD USERS SUCCESS', props<{ users: UserDTO[] }>());
export const loadPoliciesSuccess = createAction('[ADMIN] LOAD POLICIES SUCCESS', props<{ policies: any[] }>());
export const loadFundsSuccess = createAction('[ADMIN] LOAD FUNDS SUCCESS', props<{ funds: any[] }>());
export const setHealthStatus = createAction('[ADMIN] SET HEALTH', props<{ healthy: boolean }>());
