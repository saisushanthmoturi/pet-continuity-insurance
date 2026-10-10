import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { isLoggedInSelector, roleSelector, emailSelector } from '../../../auth/state/login.selector';
import { logout } from '../../../auth/state/login.action';
import { resetCustomerState } from '../../../customer/state/customer.actions';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header-component.html',
  styleUrls: ['./header-component.css']
})
export class HeaderComponent implements OnInit {
  private store = inject(Store);
  private router = inject(Router);

  isLoggedIn$: Observable<boolean> = this.store.select(isLoggedInSelector);
  role$: Observable<string> = this.store.select(roleSelector);
  email$: Observable<string> = this.store.select(emailSelector);

  currentUrl = '';

  ngOnInit(): void {
    this.currentUrl = this.router.url || '';
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.currentUrl = event.urlAfterRedirects || event.url || '';
    });
  }

  onLogout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('currentUser');
    this.store.dispatch(logout());
    this.store.dispatch(resetCustomerState());
    this.router.navigate(['/auth/login']);
  }

  isAuthPage(): boolean {
    const url = this.currentUrl || this.router.url || '';
    return url.includes('/auth/login') || url.includes('/auth/register');
  }

  isRoleTabActive(basePath: string, tab: string, defaultTab: string): boolean {
    const url = this.currentUrl || this.router.url || '';
    if (!url.startsWith(basePath)) return false;
    if (tab === defaultTab) {
      return !url.includes('tab=') || url.includes('tab=' + defaultTab);
    }
    return url.includes('tab=' + tab);
  }

  isTabActive(tab: string): boolean {
    return this.isRoleTabActive('/customer', tab, 'overview');
  }

  getDashboardLink(role: string): string {
    if (role === 'ROLE_UNDERWRITER' || role === 'UNDERWRITER') return '/underwriter';
    if (role === 'ROLE_CLAIMS_ADJUSTER' || role === 'CLAIMS_ADJUSTER') return '/claims';
    if (role === 'ROLE_CARETAKER' || role === 'CARETAKER') return '/caretaker';
    if (role === 'ROLE_ADMIN' || role === 'ADMIN') return '/admin';
    return '/customer';
  }

  formatRole(role: string): string {
    if (!role) return '';
    return role.replace('ROLE_', '').replace('_', ' ');
  }
}
