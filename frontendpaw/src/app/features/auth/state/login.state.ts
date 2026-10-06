export interface Status {
  loading: boolean;
  success: boolean;
  error: string;
}

export interface LoginState {
  isLoggedIn: boolean;
  role: string;
  userId: number;
  email: string;
  loginStatus: Status;
  signupStatus: Status;
}

export const InitialLoginState: LoginState = {
  isLoggedIn: false,
  role: '',
  userId: 0,
  email: '',
  loginStatus: { loading: false, success: false, error: '' },
  signupStatus: { loading: false, success: false, error: '' }
};
