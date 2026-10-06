import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { signup } from '../../state/login.action';
import { signupStatusSelector } from '../../state/login.selector';
import { Status } from '../../state/login.state';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register-component.html',
  styleUrls: ['./register-component.css']
})
export class RegisterComponent {
  private store = inject(Store);

  fullName = '';
  email = '';
  password = '';
  role = 'CUSTOMER';

  signupStatus$: Observable<Status> = this.store.select(signupStatusSelector);

  onSubmit(): void {
    if (this.fullName && this.email && this.password) {
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
