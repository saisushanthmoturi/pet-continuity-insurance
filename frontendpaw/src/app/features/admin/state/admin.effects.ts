import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { AdminService } from '../services/admin-service';
import * as AdminActions from './admin.actions';

@Injectable()
export class AdminEffects {
  private actions$ = inject(Actions);
  private adminService = inject(AdminService);

  loadAdminData$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadAdminData),
      switchMap(() =>
        this.adminService.getUsers().pipe(
          map((users) => AdminActions.loadUsersSuccess({ users })),
          catchError(() => of(AdminActions.loadUsersSuccess({ users: [] })))
        )
      )
    )
  );

  loadPolicies$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadAdminData),
      switchMap(() =>
        this.adminService.getAllPolicies().pipe(
          map((policies) => AdminActions.loadPoliciesSuccess({ policies })),
          catchError(() => of(AdminActions.loadPoliciesSuccess({ policies: [] })))
        )
      )
    )
  );

  loadFunds$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadAdminData),
      switchMap(() =>
        this.adminService.getAllFunds().pipe(
          map((funds) => AdminActions.loadFundsSuccess({ funds })),
          catchError(() => of(AdminActions.loadFundsSuccess({ funds: [] })))
        )
      )
    )
  );

  healthCheck$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadAdminData),
      switchMap(() =>
        this.adminService.checkHealth().pipe(
          map((res) => AdminActions.setHealthStatus({ healthy: res?.status === 'UP' })),
          catchError(() => of(AdminActions.setHealthStatus({ healthy: true })))
        )
      )
    )
  );
}
