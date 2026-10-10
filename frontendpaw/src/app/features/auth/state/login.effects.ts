import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { AuthService } from '../service/auth-service';
import * as LoginActions from './login.action';

@Injectable()
export class LoginEffects {
  private actions$ = inject(Actions);
  private authService = inject(AuthService);
  private router = inject(Router);

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(LoginActions.login),
      switchMap(({ jwtReq }) =>
        this.authService.login(jwtReq).pipe(
          tap((jwtRes) => {
            localStorage.setItem('token', jwtRes.token);
            localStorage.setItem('role', jwtRes.role);
            localStorage.setItem('currentUser', JSON.stringify({
              userId: jwtRes.userId,
              email: jwtRes.email,
              role: jwtRes.role,
              token: jwtRes.token,
              fullName: jwtRes.fullName || (jwtRes as any).username || ''
            }));
          }),
          tap((jwtRes) => {
            if (jwtRes.role === 'ROLE_UNDERWRITER' || jwtRes.role === 'UNDERWRITER') {
              this.router.navigate(['underwriter']);
            } else if (jwtRes.role === 'ROLE_CLAIMS_ADJUSTER' || jwtRes.role === 'CLAIMS_ADJUSTER') {
              this.router.navigate(['claims']);
            } else if (jwtRes.role === 'ROLE_CARETAKER' || jwtRes.role === 'CARETAKER') {
              this.router.navigate(['caretaker']);
            } else if (jwtRes.role === 'ROLE_ADMIN' || jwtRes.role === 'ADMIN') {
              this.router.navigate(['admin']);
            } else {
              this.router.navigate(['customer']);
            }
          }),
          map((jwtRes) => LoginActions.loginSuccess({ jwtRes })),
          catchError((error) =>
            of(LoginActions.loginFailure({ error: error?.error?.message || error?.message || 'Login failed' }))
          )
        )
      )
    )
  );

  signup$ = createEffect(() =>
    this.actions$.pipe(
      ofType(LoginActions.signup),
      switchMap(({ userReq }) =>
        this.authService.register(userReq).pipe(
          tap(() => this.router.navigate(['auth/login'])),
          map(() => LoginActions.signupSuccess()),
          catchError((error) =>
            of(LoginActions.signupFailure({ error: error?.error?.message || error?.message || 'Registration failed' }))
          )
        )
      )
    )
  );

  logout$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(LoginActions.logout),
        tap(() => {
          localStorage.removeItem('token');
          localStorage.removeItem('role');
          localStorage.removeItem('currentUser');
          const path = window.location.pathname;
          if (!path.includes('/auth/login') && !path.includes('/auth/register')) {
            this.router.navigate(['auth/login']);
          }
        })
      ),
    { dispatch: false }
  );
}
