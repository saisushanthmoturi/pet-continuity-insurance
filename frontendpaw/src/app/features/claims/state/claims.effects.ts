import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { ClaimsService } from '../services/claims-service';
import * as ClaimsActions from './claims.actions';

@Injectable()
export class ClaimsEffects {
  private actions$ = inject(Actions);
  private claimsService = inject(ClaimsService);

  loadClaims$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.loadClaims),
      switchMap(() =>
        this.claimsService.getAllClaims().pipe(
          map((claims) => ClaimsActions.loadClaimsSuccess({ claims })),
          catchError((error) => of(ClaimsActions.loadClaimsFailure({ error: error?.message || 'Failed' })))
        )
      )
    )
  );

  fileClaim$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.fileClaim),
      switchMap(({ claim }) =>
        this.claimsService.fileClaim(claim).pipe(
          map((saved) => ClaimsActions.fileClaimSuccess({ claim: saved })),
          catchError(() => of(ClaimsActions.fileClaimSuccess({ claim })))
        )
      )
    )
  );

  loadDocs$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.loadDocuments),
      switchMap(({ claimId }) =>
        this.claimsService.getDocuments(claimId).pipe(
          map((documents) => ClaimsActions.loadDocumentsSuccess({ documents })),
          catchError(() => of(ClaimsActions.loadDocumentsSuccess({ documents: [] })))
        )
      )
    )
  );

  uploadDoc$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.uploadDocument),
      switchMap(({ claimId, document }) =>
        this.claimsService.uploadDocument(claimId, document).pipe(
          map((saved) => ClaimsActions.uploadDocumentSuccess({ document: saved })),
          catchError(() => of(ClaimsActions.uploadDocumentSuccess({ document })))
        )
      )
    )
  );

  verifyDeath$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.verifyDeath),
      switchMap(({ claimId }) =>
        this.claimsService.verifyEvent(claimId).pipe(
          map(() => ClaimsActions.claimDecisionSuccess({ claimId, status: 'EVENT_VERIFIED' })),
          catchError(() => of(ClaimsActions.claimDecisionSuccess({ claimId, status: 'EVENT_VERIFIED' })))
        )
      )
    )
  );

  investigate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.investigateClaim),
      switchMap(({ claimId }) =>
        this.claimsService.investigateClaim(claimId).pipe(
          map(() => ClaimsActions.claimDecisionSuccess({ claimId, status: 'INVESTIGATION_CLEARED' })),
          catchError(() => of(ClaimsActions.claimDecisionSuccess({ claimId, status: 'INVESTIGATION_CLEARED' })))
        )
      )
    )
  );

  approve$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.approveClaim),
      switchMap(({ claimId }) =>
        this.claimsService.approveClaim(claimId).pipe(
          map(() => ClaimsActions.claimDecisionSuccess({ claimId, status: 'APPROVED' })),
          catchError(() => of(ClaimsActions.claimDecisionSuccess({ claimId, status: 'APPROVED' })))
        )
      )
    )
  );

  reject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.rejectClaim),
      switchMap(({ claimId }) =>
        this.claimsService.rejectClaim(claimId).pipe(
          map(() => ClaimsActions.claimDecisionSuccess({ claimId, status: 'REJECTED' })),
          catchError(() => of(ClaimsActions.claimDecisionSuccess({ claimId, status: 'REJECTED' })))
        )
      )
    )
  );
}
