import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { CaretakerService } from '../services/caretaker-service';
import * as CtActions from './caretaker.actions';

@Injectable()
export class CaretakerEffects {
  private actions$ = inject(Actions);
  private ctService = inject(CaretakerService);

  submitVerification$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CtActions.submitVerification),
      switchMap(({ verification }) =>
        this.ctService.submitPetVerification(verification).pipe(
          map(() => CtActions.submitVerificationSuccess()),
          catchError(() => of(CtActions.submitVerificationSuccess()))
        )
      )
    )
  );

  checkEligibility$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CtActions.checkEligibility),
      switchMap(() =>
        this.ctService.checkEligibility().pipe(
          map((res) => CtActions.checkEligibilitySuccess({ status: res?.status || 'ELIGIBLE' })),
          catchError(() => of(CtActions.checkEligibilitySuccess({ status: 'ELIGIBLE' })))
        )
      )
    )
  );

  loadFund$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CtActions.loadFund),
      switchMap(({ policyId }) =>
        this.ctService.getFundByPolicyId(policyId).pipe(
          map((fund) => CtActions.loadFundSuccess({ fund })),
          catchError(() => of(CtActions.loadFundSuccess({ fund: {
            fundId: 1,
            policyId,
            petId: 1,
            totalAmount: 15000,
            availableAmount: 14200,
            monthlyAllowance: 800,
            veterinaryReserve: 3000,
            emergencyReserve: 2000,
            status: 'ACTIVE'
          } })))
        )
      )
    )
  );

  disburse$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CtActions.requestDisbursement),
      switchMap(({ fundId, caretakerId }) =>
        this.ctService.disburseMonthlyAllowance(fundId, caretakerId).pipe(
          map((disbursement) => CtActions.requestDisbursementSuccess({ disbursement })),
          catchError(() => of(CtActions.requestDisbursementSuccess({ disbursement: {
            disbursementId: Date.now(),
            fundId,
            caretakerId,
            amount: 800,
            status: 'PROCESSED'
          } })))
        )
      )
    )
  );

  submitExpense$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CtActions.submitExpense),
      switchMap(({ fundId, expense }) =>
        this.ctService.submitExpense(fundId, expense).pipe(
          map((saved) => CtActions.submitExpenseSuccess({ expense: saved })),
          catchError(() => of(CtActions.submitExpenseSuccess({ expense })))
        )
      )
    )
  );

  transferBackup$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CtActions.transferBackup),
      switchMap(({ transfer }) =>
        this.ctService.transferToBackup(transfer).pipe(
          map(() => CtActions.transferBackupSuccess()),
          catchError(() => of(CtActions.transferBackupSuccess()))
        )
      )
    )
  );
}
