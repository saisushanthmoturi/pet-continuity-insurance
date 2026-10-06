import { Status } from '../../auth/state/login.state';
import { UserDTO } from '../models/adminDTO';

export interface AdminState {
  users: UserDTO[];
  policies: any[];
  funds: any[];
  systemHealthy: boolean;
  adminStatus: Status;
}

export const InitialAdminState: AdminState = {
  users: [],
  policies: [],
  funds: [],
  systemHealthy: true,
  adminStatus: { loading: false, success: false, error: '' }
};
