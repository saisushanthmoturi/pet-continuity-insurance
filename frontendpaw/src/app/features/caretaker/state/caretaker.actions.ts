import { createAction, props } from '@ngrx/store';
import { BackupTransferDTO, DisbursementDTO, ExpenseDTO, PetCareFundDTO, PetVerificationDTO } from '../models/caretakerDTO';

export const submitVerification = createAction('[CARETAKER] SUBMIT VERIFICATION', props<{ verification: PetVerificationDTO }>());
export const submitVerificationSuccess = createAction('[CARETAKER] SUBMIT VERIFICATION SUCCESS');
export const verifyCustody = submitVerification;

export const checkEligibility = createAction('[CARETAKER] CHECK ELIGIBILITY');
export const checkEligibilitySuccess = createAction('[CARETAKER] CHECK ELIGIBILITY SUCCESS', props<{ status: string }>());

export const loadFund = createAction('[CARETAKER] LOAD FUND', props<{ policyId: number }>());
export const loadFundSuccess = createAction('[CARETAKER] LOAD FUND SUCCESS', props<{ fund: PetCareFundDTO }>());
export const loadFundByPolicy = loadFund;

export const requestDisbursement = createAction('[CARETAKER] DISBURSE MONTHLY', props<{ fundId: number; caretakerId: number }>());
export const requestDisbursementSuccess = createAction('[CARETAKER] DISBURSE MONTHLY SUCCESS', props<{ disbursement: DisbursementDTO }>());
export const disburseAllowance = requestDisbursement;

export const loadExpenses = createAction('[CARETAKER] LOAD EXPENSES', props<{ fundId: number }>());
export const loadExpensesSuccess = createAction('[CARETAKER] LOAD EXPENSES SUCCESS', props<{ expenses: ExpenseDTO[] }>());

export const submitExpense = createAction('[CARETAKER] SUBMIT EXPENSE', props<{ fundId: number; expense: ExpenseDTO }>());
export const submitExpenseSuccess = createAction('[CARETAKER] SUBMIT EXPENSE SUCCESS', props<{ expense: ExpenseDTO }>());

export const transferBackup = createAction('[CARETAKER] TRANSFER BACKUP', props<{ transfer: BackupTransferDTO }>());
export const transferBackupSuccess = createAction('[CARETAKER] TRANSFER BACKUP SUCCESS');
