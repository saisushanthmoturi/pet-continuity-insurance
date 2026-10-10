import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { signup, logout } from '../../state/login.action';
import { resetCustomerState } from '../../../customer/state/customer.actions';
import { signupStatusSelector } from '../../state/login.selector';
import { Status } from '../../state/login.state';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register-component.html',
  styleUrls: ['./register-component.css']
})
export class RegisterComponent implements OnInit {
  private store = inject(Store);

  fullName = '';
  email = '';
  password = '';
  role = 'CUSTOMER';

  signupStatus$: Observable<Status> = this.store.select(signupStatusSelector);

  ngOnInit(): void {
    // Clear any previous session upon entering registration page
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('currentUser');
    this.store.dispatch(resetCustomerState());
  }

  onSubmit(): void {
    if (this.fullName && this.email && this.password) {
      localStorage.setItem("user_registered_name_" + this.email.trim().toLowerCase(), this.fullName.trim());
      this.store.dispatch(signup({
        userReq: {
          fullName: this.fullName,
          email: this.email,
          password: this.password,
          role: this.role
        }
      }));
    }
  }
}
