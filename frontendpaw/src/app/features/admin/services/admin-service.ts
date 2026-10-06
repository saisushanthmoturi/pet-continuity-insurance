import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDTO } from '../models/adminDTO';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/auth/users`);
  }

  getAllPolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/policies`);
  }

  getAllFunds(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/payments/funds`);
  }

  checkHealth(): Observable<any> {
    return this.http.get('http://localhost:8080/actuator/health');
  }
}
