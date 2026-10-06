import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  usersSelector,
  globalPoliciesSelector,
  globalFundsSelector,
  systemHealthSelector,
  adminStatusSelector
} from '../../state/admin.selectors';
import {
  loadUsers,
  loadGlobalPolicies,
  loadGlobalFunds,
  checkSystemHealth
} from '../../state/admin.actions';
import { UserDTO } from '../../models/adminDTO';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboardComponent implements OnInit {
  private store = inject(Store);
  private route = inject(ActivatedRoute);

  activeTab = 'overview';
  users$: Observable<UserDTO[]> = this.store.select(usersSelector);
  policies$: Observable<any[]> = this.store.select(globalPoliciesSelector);
  funds$: Observable<any[]> = this.store.select(globalFundsSelector);
  health$: Observable<any> = this.store.select(systemHealthSelector);

  userRoleFilter = 'ALL';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadUsers());
    this.store.dispatch(loadGlobalPolicies());
    this.store.dispatch(loadGlobalFunds());
    this.store.dispatch(checkSystemHealth());
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  formatRole(role: string): string {
    if (!role) return '';
    return role.replace('ROLE_', '').replace('_', ' ');
  }
}
