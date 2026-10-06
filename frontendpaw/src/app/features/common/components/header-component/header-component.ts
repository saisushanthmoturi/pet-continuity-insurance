import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { isLoggedInSelector, roleSelector, emailSelector } from '../../../auth/state/login.selector';
import { logout } from '../../../auth/state/login.action';

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

  ngOnInit(): void {
    // Session state is managed by NgRx and localStorage
  }

  onLogout(): void {
    this.store.dispatch(logout());
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
