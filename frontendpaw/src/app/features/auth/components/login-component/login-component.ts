import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { login } from '../../state/login.action';
import { loginStatusSelector } from '../../state/login.selector';
import { Status } from '../../state/login.state';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login-component.html',
  styleUrls: ['./login-component.css']
})
export class LoginComponent {
  private store = inject(Store);

  email = '';
  password = '';

  loginStatus$: Observable<Status> = this.store.select(loginStatusSelector);

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
        this.email = 'sarah.jenkins@example.com';
        this.password = 'password123';
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
