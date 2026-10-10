import { Routes } from '@angular/router';
import { authGuard } from './guards/authGuard';
import { roleGuard } from './guards/roleGuard';

import { LoginComponent } from './features/auth/components/login-component/login-component';
import { RegisterComponent } from './features/auth/components/register-component/register-component';
import { CustomerDashboardComponent } from './features/customer/components/customer-dashboard/customer-dashboard';
import { PaymentPageComponent } from './features/customer/components/payment-page/payment-page';
import { UnderwriterDashboardComponent } from './features/underwriter/components/underwriter-dashboard/underwriter-dashboard';
import { ClaimsDashboardComponent } from './features/claims/components/claims-dashboard/claims-dashboard';
import { CaretakerDashboardComponent } from './features/caretaker/components/caretaker-dashboard/caretaker-dashboard';
import { AdminDashboardComponent } from './features/admin/components/admin-dashboard/admin-dashboard';

export const routes: Routes = [
  { path: '', redirectTo: 'customer', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  {
    path: 'customer',
    component: CustomerDashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'payment',
    component: PaymentPageComponent,
    canActivate: [authGuard]
  },
  {
    path: 'underwriter',
    component: UnderwriterDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_UNDERWRITER', 'UNDERWRITER'] }
  },
  {
    path: 'claims',
    component: ClaimsDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_CLAIMS_ADJUSTER', 'CLAIMS_ADJUSTER'] }
  },
  {
    path: 'caretaker',
    component: CaretakerDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_CARETAKER', 'CARETAKER'] }
  },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ADMIN'] }
  },
  { path: '**', redirectTo: 'customer' }
];
