import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Store } from '@ngrx/store';
import { HeaderComponent } from './features/common/components/header-component/header-component';
import { FooterComponent } from './features/common/components/footer-component/footer-component';
import { restoreSession } from './features/auth/state/login.action';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  private store = inject(Store);

  ngOnInit(): void {
    const rawUser = localStorage.getItem('currentUser');
    if (rawUser) {
      try {
        const u = JSON.parse(rawUser);
        this.store.dispatch(restoreSession({
          jwtRes: {
            token: u.token || localStorage.getItem('token') || '',
            role: u.role || localStorage.getItem('role') || 'CUSTOMER',
            userId: u.userId || 1,
            email: u.email || '',
            fullName: u.fullName || ''
          }
        }));
      } catch (e) {
        // Ignore JSON parse errors on invalid stored data
      }
    }
  }
}
