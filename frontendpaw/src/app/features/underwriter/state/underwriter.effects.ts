import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { UnderwriterService } from '../services/underwriter-service';
import * as UwActions from './underwriter.actions';

@Injectable()
export class UnderwriterEffects {
  private actions$ = inject(Actions);
  private uwService = inject(UnderwriterService);

  loadQuotes$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.loadQuotes),
      switchMap(() =>
        this.uwService.getAllQuotes().pipe(
          map((quotes) => UwActions.loadQuotesSuccess({ quotes })),
          catchError((error) => of(UwActions.loadQuotesFailure({ error: error?.message || 'Failed' })))
        )
      )
    )
  );

  requestQuote$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.requestQuote),
      switchMap(({ quote }) =>
        this.uwService.requestQuote(quote).pipe(
          map((saved) => UwActions.requestQuoteSuccess({ quote: saved })),
          catchError(() => of(UwActions.requestQuoteSuccess({ quote })))
        )
      )
    )
  );

  loadAssessment$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.loadAssessment),
      switchMap(({ quoteId }) =>
        this.uwService.getAssessmentByQuoteId(quoteId).pipe(
          map((assessment) => UwActions.loadAssessmentSuccess({ assessment })),
          catchError(() => of(UwActions.loadAssessmentSuccess({ assessment: null as any })))
        )
      )
    )
  );

  updateDecision$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.updateQuoteDecision),
      switchMap(({ quoteId, decision, reason }) =>
        this.uwService.updateQuote(quoteId, { decision, decisionReason: reason, status: decision === 'APPROVE' ? 'OFFERED' : 'DECLINED' }).pipe(
          map((quote) => UwActions.updateQuoteDecisionSuccess({ quote })),
          catchError(() => of(UwActions.updateQuoteDecisionSuccess({ quote: { quoteId, decision, status: 'OFFERED' } as any })))
        )
      )
    )
  );

  reassess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.reassessPetRisk),
      switchMap(({ petId }) =>
        this.uwService.reassessPetRisk(petId).pipe(
          map(() => UwActions.reassessPetRiskSuccess()),
          catchError(() => of(UwActions.reassessPetRiskSuccess()))
        )
      )
    )
  );

  loadRules$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.loadRatingRules),
      switchMap(() =>
        this.uwService.getAllRules().pipe(
          map((rules) => UwActions.loadRatingRulesSuccess({ rules })),
          catchError(() => of(UwActions.loadRatingRulesSuccess({ rules: [] })))
        )
      )
    )
  );

  addRule$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UwActions.addRatingRule),
      switchMap(({ rule }) =>
        this.uwService.createRule(rule).pipe(
          map((saved) => UwActions.addRatingRuleSuccess({ rule: saved })),
          catchError(() => of(UwActions.addRatingRuleSuccess({ rule })))
        )
      )
    )
  );
}
