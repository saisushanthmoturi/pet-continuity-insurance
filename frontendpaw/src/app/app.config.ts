import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';

import { routes } from './app.routes';
import { httpReqInterceptor } from './interceptors/httpReqInterceptor';

import { loginReducer } from './features/auth/state/login.reducer';
import { customerReducer } from './features/customer/state/customer.reducer';
import { underwriterReducer } from './features/underwriter/state/underwriter.reducer';
import { claimsReducer } from './features/claims/state/claims.reducer';
import { caretakerReducer } from './features/caretaker/state/caretaker.reducer';
import { adminReducer } from './features/admin/state/admin.reducer';

import { LoginEffects } from './features/auth/state/login.effects';
import { CustomerEffects } from './features/customer/state/customer.effects';
import { UnderwriterEffects } from './features/underwriter/state/underwriter.effects';
import { ClaimsEffects } from './features/claims/state/claims.effects';
import { CaretakerEffects } from './features/caretaker/state/caretaker.effects';
import { AdminEffects } from './features/admin/state/admin.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpReqInterceptor])),
    provideStore({
      login: loginReducer,
      customer: customerReducer,
      underwriter: underwriterReducer,
      claims: claimsReducer,
      caretaker: caretakerReducer,
      admin: adminReducer
    }),
    provideEffects([
      LoginEffects,
      CustomerEffects,
      UnderwriterEffects,
      ClaimsEffects,
      CaretakerEffects,
      AdminEffects
    ])
  ]
};
