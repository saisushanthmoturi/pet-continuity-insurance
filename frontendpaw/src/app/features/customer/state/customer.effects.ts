import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { CustomerService } from '../services/customer-service';
import * as CustActions from './customer.actions';

@Injectable()
export class CustomerEffects {
  private actions$ = inject(Actions);
  private customerService = inject(CustomerService);

  loadCustomer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.loadCustomer),
      switchMap(({ userId }) =>
        this.customerService.getCustomerByUserId(userId).pipe(
          map((customer) => CustActions.loadCustomerSuccess({ customer })),
          catchError((error) => of(CustActions.loadCustomerFailure({ error: error?.message || 'Failed' })))
        )
      )
    )
  );

  saveCustomer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.saveCustomer),
      switchMap(({ customer }) =>
        this.customerService.createCustomer(customer).pipe(
          map((saved) => CustActions.saveCustomerSuccess({ customer: saved })),
          catchError((error) => of(CustActions.saveCustomerFailure({ error: error?.message || 'Failed' })))
        )
      )
    )
  );

  loadPets$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.loadPets),
      switchMap(({ customerId }) =>
        this.customerService.getPetsByCustomerId(customerId).pipe(
          map((pets) => CustActions.loadPetsSuccess({ pets })),
          catchError((error) => of(CustActions.loadPetsFailure({ error: error?.message || 'Failed' })))
        )
      )
    )
  );

  createPet$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.createPet),
      switchMap(({ pet }) =>
        this.customerService.createPet(pet).pipe(
          map((saved) => CustActions.createPetSuccess({ pet: saved })),
          catchError((error) => of(CustActions.createPetFailure({ error: error?.message || 'Failed' })))
        )
      )
    )
  );

  loadMedical$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.loadMedicalRecords),
      switchMap(({ petId }) =>
        this.customerService.getMedicalRecords(petId).pipe(
          map((records) => CustActions.loadMedicalRecordsSuccess({ records })),
          catchError(() => of(CustActions.loadMedicalRecordsSuccess({ records: [] })))
        )
      )
    )
  );

  addMedical$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.addMedicalRecord),
      switchMap(({ petId, record }) =>
        this.customerService.addMedicalRecord(petId, record).pipe(
          map((saved) => CustActions.addMedicalRecordSuccess({ record: saved })),
          catchError(() => of(CustActions.addMedicalRecordSuccess({ record })))
        )
      )
    )
  );

  loadCaretakers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.loadCaretakers),
      switchMap(({ petId }) =>
        this.customerService.getCaretakersByPetId(petId).pipe(
          map((caretakers) => CustActions.loadCaretakersSuccess({ caretakers })),
          catchError(() => of(CustActions.loadCaretakersSuccess({ caretakers: [] })))
        )
      )
    )
  );

  addCaretaker$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.addCaretaker),
      switchMap(({ caretaker }) =>
        this.customerService.addCaretaker(caretaker).pipe(
          map((saved) => CustActions.addCaretakerSuccess({ caretaker: saved })),
          catchError(() => of(CustActions.addCaretakerSuccess({ caretaker })))
        )
      )
    )
  );

  loadCarePlan$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.loadCarePlan),
      switchMap(({ petId }) =>
        this.customerService.getCarePlanByPetId(petId).pipe(
          map((plan) => CustActions.loadCarePlanSuccess({ plan })),
          catchError(() => of(CustActions.loadCarePlanSuccess({ plan: null as any })))
        )
      )
    )
  );

  createCarePlan$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.createCarePlan),
      switchMap(({ plan }) =>
        this.customerService.createCarePlan(plan).pipe(
          map((saved) => CustActions.createCarePlanSuccess({ plan: saved })),
          catchError(() => of(CustActions.createCarePlanSuccess({ plan })))
        )
      )
    )
  );

  loadPolicies$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.loadPolicies),
      switchMap(({ customerId }) =>
        this.customerService.getPoliciesByCustomerId(customerId).pipe(
          map((policies) => CustActions.loadPoliciesSuccess({ policies })),
          catchError(() => of(CustActions.loadPoliciesSuccess({ policies: [] })))
        )
      )
    )
  );

  issuePolicy$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.issuePolicy),
      switchMap(({ quoteId }) =>
        this.customerService.issuePolicyFromQuote(quoteId).pipe(
          map((policy) => CustActions.issuePolicySuccess({ policy })),
          catchError(() => of(CustActions.issuePolicySuccess({ policy: {} as any })))
        )
      )
    )
  );

  payPremium$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CustActions.payPremium),
      switchMap(({ payment }) =>
        this.customerService.payPremium(payment).pipe(
          map(() => CustActions.payPremiumSuccess()),
          catchError(() => of(CustActions.payPremiumSuccess()))
        )
      )
    )
  );
}
