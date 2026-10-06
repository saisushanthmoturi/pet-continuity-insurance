import { createAction, props } from '@ngrx/store';
import { JwtReqDTO, RegisterReqDTO } from '../models/JwtReqDTO';
import { JwtResDTO } from '../models/JwtResDTO';

export const login = createAction('[LOGIN] LOGIN', props<{ jwtReq: JwtReqDTO }>());
export const loginSuccess = createAction('[LOGIN] LOGIN SUCCESS', props<{ jwtRes: JwtResDTO }>());
export const loginFailure = createAction('[LOGIN] LOGIN FAILURE', props<{ error: string }>());
export const logout = createAction('[LOGIN] LOGOUT');

export const signup = createAction('[SIGN UP]', props<{ userReq: RegisterReqDTO }>());
export const signupSuccess = createAction('[SIGN UP] SUCCESS');
export const signupFailure = createAction('[SIGN UP] FAILURE', props<{ error: string }>());

export const restoreSession = createAction('[LOGIN] RESTORE SESSION', props<{ jwtRes: JwtResDTO }>());
