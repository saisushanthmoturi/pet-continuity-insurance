import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

export function getRoleDashboard(role: string): string {
  if (!role) return 'auth/login';
  const normalized = role.toUpperCase();
  if (normalized === 'ROLE_UNDERWRITER' || normalized === 'UNDERWRITER') return 'underwriter';
  if (
    normalized === 'ROLE_CLAIMS_ADJUSTER' ||
    normalized === 'CLAIMS_ADJUSTER' ||
    normalized === 'ROLE_CLAIMS_OFFICER' ||
    normalized === 'CLAIMS_OFFICER'
  ) {
    return 'claims';
  }
  if (normalized === 'ROLE_CARETAKER' || normalized === 'CARETAKER') return 'caretaker';
  if (normalized === 'ROLE_ADMIN' || normalized === 'ADMIN') return 'admin';
  if (normalized === 'ROLE_CUSTOMER' || normalized === 'CUSTOMER') return 'customer';
  return 'auth/login';
}

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const rawUser = localStorage.getItem('currentUser');
  if (!rawUser) {
    router.navigate(['auth/login']);
    return false;
  }

  let user: any;
  try {
    user = JSON.parse(rawUser);
  } catch {
    localStorage.removeItem('currentUser');
    router.navigate(['auth/login']);
    return false;
  }

  const expectedRoles: string[] = route.data['roles'] || [];

  if (expectedRoles.length > 0 && (!user?.role || !expectedRoles.includes(user.role))) {
    const targetDashboard = getRoleDashboard(user?.role);
    router.navigate([targetDashboard]);
    return false;
  }

  return true;
};
