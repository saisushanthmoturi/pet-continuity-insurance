export interface UserDTO {
  userId?: number;
  id?: number;
  username?: string;
  fullName?: string;
  email: string;
  role: string;
  status?: string;
  createdAt?: string;
}

export interface AdminMetricsDTO {
  activePolicies: number;
  totalFunds: number;
  pendingClaims: number;
  registeredPets: number;
}
