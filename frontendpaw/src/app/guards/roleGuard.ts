import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const rawUser = localStorage.getItem('currentUser');
  if (!rawUser) {
    router.navigate(['auth/login']);
    return false;
  }

  const user = JSON.parse(rawUser);
  const expectedRoles: string[] = route.data['roles'] || [];

  if (expectedRoles.length > 0 && !expectedRoles.includes(user.role)) {
    router.navigate(['auth/login']);
    return false;
  }

  return true;
};
