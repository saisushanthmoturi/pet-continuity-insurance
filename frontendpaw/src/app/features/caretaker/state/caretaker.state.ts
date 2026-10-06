import { Status } from '../../auth/state/login.state';
import { DisbursementDTO, ExpenseDTO, PetCareFundDTO } from '../models/caretakerDTO';

export interface CaretakerState {
  activeFund: PetCareFundDTO | null;
  disbursements: DisbursementDTO[];
  expenses: ExpenseDTO[];
  eligibilityStatus: string;
  verificationStatus: Status;
  disbursementStatus: Status;
  expenseStatus: Status;
}

export const InitialCaretakerState: CaretakerState = {
  activeFund: null,
  disbursements: [],
  expenses: [],
  eligibilityStatus: 'UNKNOWN',
  verificationStatus: { loading: false, success: false, error: '' },
  disbursementStatus: { loading: false, success: false, error: '' },
  expenseStatus: { loading: false, success: false, error: '' }
};
