import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { login, logout } from '../../state/login.action';
import { resetCustomerState } from '../../../customer/state/customer.actions';
import { loginStatusSelector } from '../../state/login.selector';
import { Status } from '../../state/login.state';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login-component.html',
  styleUrls: ['./login-component.css']
})
export class LoginComponent implements OnInit {
  private store = inject(Store);
  private router = inject(Router);

  email = '';
  password = '';

  loginStatus$: Observable<Status> = this.store.select(loginStatusSelector);

  ngOnInit(): void {
    // When visiting the login page, completely clear any previous user session
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('currentUser');
    this.store.dispatch(resetCustomerState());
  }

  onSubmit(): void {
    if (this.email && this.password) {
      this.store.dispatch(login({
        jwtReq: {
          email: this.email,
          password: this.password
        }
      }));
    }
  }

  fillDemo(role: string): void {
    switch (role) {
      case 'customer':
        this.email = 'john.doe@example.com';
        this.password = 'Password123!';
        break;
      case 'underwriter':
        this.email = 'underwriter@pawcontinuity.com';
        this.password = 'password123';
        break;
      case 'claims':
        this.email = 'adjuster@pawcontinuity.com';
        this.password = 'password123';
        break;
      case 'caretaker':
        this.email = 'caretaker@pawcontinuity.com';
        this.password = 'password123';
        break;
      case 'admin':
        this.email = 'admin@pawcontinuity.com';
        this.password = 'password123';
        break;
    }
  }
}
