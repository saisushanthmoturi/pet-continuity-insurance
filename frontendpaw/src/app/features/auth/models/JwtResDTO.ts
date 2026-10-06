export interface JwtResDTO {
  token: string;
  userId: number;
  email: string;
  fullName: string;
  role: string;
}

export interface CurrentUser {
  userId: number;
  email: string;
  fullName: string;
  role: string;
  token: string;
}
