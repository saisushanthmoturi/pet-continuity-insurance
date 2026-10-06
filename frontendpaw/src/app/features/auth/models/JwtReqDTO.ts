export interface JwtReqDTO {
  email: string;
  password: string;
}

export interface RegisterReqDTO {
  email: string;
  password: string;
  fullName: string;
  role: string;
}
