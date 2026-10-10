import { createReducer, on } from '@ngrx/store';
import { InitialLoginState } from './login.state';
import * as LoginActions from './login.action';

export const loginReducer = createReducer(
  InitialLoginState,
  on(LoginActions.login, (state) => ({
    ...state,
    loginStatus: { loading: true, success: false, error: '' }
  })),
  on(LoginActions.loginSuccess, (state, { jwtRes }) => {
    localStorage.setItem('token', jwtRes.token);
    localStorage.setItem('role', jwtRes.role);
    localStorage.setItem('currentUser', JSON.stringify({
      userId: jwtRes.userId,
      email: jwtRes.email,
      role: jwtRes.role,
      token: jwtRes.token,
      fullName: jwtRes.fullName || (jwtRes as any).username || ''
    }));
    return {
      ...state,
      isLoggedIn: true,
      role: jwtRes.role,
      userId: jwtRes.userId,
      email: jwtRes.email,
      loginStatus: { loading: false, success: true, error: '' }
    };
  }),
  on(LoginActions.restoreSession, (state, { jwtRes }) => ({
    ...state,
    isLoggedIn: true,
    role: jwtRes.role,
    userId: jwtRes.userId,
    email: jwtRes.email
  })),
  on(LoginActions.loginFailure, (state, { error }) => ({
    ...state,
    loginStatus: { loading: false, success: false, error }
  })),
  on(LoginActions.logout, (state) => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('currentUser');
    return {
      ...state,
      isLoggedIn: false,
      role: '',
      userId: 0,
      email: '',
      loginStatus: { loading: false, success: false, error: '' }
    };
  }),
  on(LoginActions.signup, (state) => ({
    ...state,
    signupStatus: { loading: true, success: false, error: '' }
  })),
  on(LoginActions.signupSuccess, (state) => ({
    ...state,
    signupStatus: { loading: false, success: true, error: '' }
  })),
  on(LoginActions.signupFailure, (state, { error }) => ({
    ...state,
    signupStatus: { loading: false, success: false, error }
  }))
);
