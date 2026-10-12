import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { signup } from '../../state/login.action';
import { resetCustomerState } from '../../../customer/state/customer.actions';
import { signupStatusSelector } from '../../state/login.selector';
import { Status } from '../../state/login.state';

const SEED_REGISTERED_EMAILS = [
  'john.doe@example.com',
  'sarah.uw@example.com',
  'mike.claims@example.com',
  'emma.caretaker@example.com',
  'admin@petcontinuity.com',
  'underwriter@pawcontinuity.com',
  'adjuster@pawcontinuity.com',
  'caretaker@pawcontinuity.com',
  'admin@pawcontinuity.com',
  'jadhav@gmail.com',
  'robert.downey@example.com',
  'sarah.smith@example.com',
  'sarah.jenkins@example.com'
];

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
  confirmPassword = '';
  role = 'CUSTOMER';

  fullNameTouched = false;
  emailTouched = false;
  passwordTouched = false;
  confirmPasswordTouched = false;

  clientErrorMessage = '';

  signupStatus$: Observable<Status> = this.store.select(signupStatusSelector);

  ngOnInit(): void {
    // Clear any previous session upon entering registration page
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('currentUser');
    this.store.dispatch(resetCustomerState());
  }

  getExistingEmails(): string[] {
    const list = [...SEED_REGISTERED_EMAILS];
    try {
      const raw = localStorage.getItem('registered_emails_registry');
      if (raw) {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed)) {
          parsed.forEach((e: string) => {
            if (typeof e === 'string' && !list.includes(e.toLowerCase())) {
              list.push(e.toLowerCase());
            }
          });
        }
      }
    } catch {}
    return list;
  }

  isEmailDuplicate(emailValue: string): boolean {
    const normalized = (emailValue || '').trim().toLowerCase();
    if (!normalized) return false;
    return this.getExistingEmails().includes(normalized);
  }

  get fullNameError(): string {
    if (!this.fullNameTouched && !this.fullName) return '';
    const trimmed = (this.fullName || '').trim();
    if (!trimmed) {
      return 'Username / Full Name is required.';
    }
    if (trimmed.length < 3) {
      return 'Username / Full Name must be at least 3 characters long.';
    }
    if (/^\d+$/.test(trimmed)) {
      return 'Username cannot consist entirely of numbers. Please include letters.';
    }
    if (!/[a-zA-Z]/.test(trimmed)) {
      return 'Username must contain alphabetic letters.';
    }
    return '';
  }

  get emailError(): string {
    if (!this.emailTouched && !this.email) return '';
    const trimmed = (this.email || '').trim().toLowerCase();
    if (!trimmed) {
      return 'Email address is required.';
    }
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(trimmed)) {
      return 'Please enter a valid email address (e.g. name@example.com).';
    }
    if (this.isEmailDuplicate(trimmed)) {
      return 'An account with this email already exists. Only 1 account per email is allowed.';
    }
    return '';
  }

  get passwordError(): string {
    if (!this.passwordTouched && !this.password) return '';
    if (!this.password) {
      return 'Password is required.';
    }
    if (this.password.length < 6) {
      return 'Password must be at least 6 characters long.';
    }
    return '';
  }

  get confirmPasswordError(): string {
    if (!this.confirmPasswordTouched && !this.confirmPassword) return '';
    if (this.confirmPassword && this.password !== this.confirmPassword) {
      return 'Passwords do not match.';
    }
    return '';
  }

  isFormValid(): boolean {
    const trimmedName = (this.fullName || '').trim();
    if (!trimmedName || trimmedName.length < 3 || /^\d+$/.test(trimmedName) || !/[a-zA-Z]/.test(trimmedName)) {
      return false;
    }

    const trimmedEmail = (this.email || '').trim().toLowerCase();
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!trimmedEmail || !emailRegex.test(trimmedEmail) || this.isEmailDuplicate(trimmedEmail)) {
      return false;
    }

    if (!this.password || this.password.length < 6) {
      return false;
    }

    if (this.confirmPassword && this.password !== this.confirmPassword) {
      return false;
    }

    return true;
  }

  onSubmit(): void {
    this.fullNameTouched = true;
    this.emailTouched = true;
    this.passwordTouched = true;
    this.confirmPasswordTouched = true;

    if (!this.isFormValid()) {
      if (this.fullNameError) {
        this.clientErrorMessage = this.fullNameError;
      } else if (this.emailError) {
        this.clientErrorMessage = this.emailError;
      } else if (this.passwordError) {
        this.clientErrorMessage = this.passwordError;
      } else if (this.confirmPasswordError) {
        this.clientErrorMessage = this.confirmPasswordError;
      } else {
        this.clientErrorMessage = 'Please correct all form errors before submitting.';
      }
      return;
    }

    this.clientErrorMessage = '';
    const cleanEmail = this.email.trim().toLowerCase();
    const cleanName = this.fullName.trim();

    localStorage.setItem('user_registered_name_' + cleanEmail, cleanName);
    this.store.dispatch(signup({
      userReq: {
        fullName: cleanName,
        email: cleanEmail,
        password: this.password,
        role: this.role
      }
    }));
  }
}
