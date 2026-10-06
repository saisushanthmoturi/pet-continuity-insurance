import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JwtReqDTO, RegisterReqDTO } from '../models/JwtReqDTO';
import { JwtResDTO } from '../models/JwtResDTO';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/auth';

  login(credentials: JwtReqDTO): Observable<JwtResDTO> {
    return this.http.post<JwtResDTO>(`${this.apiUrl}/login`, credentials);
  }

  register(payload: RegisterReqDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, payload);
  }

  validate(): Observable<any> {
    return this.http.get(`${this.apiUrl}/validate`);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {});
  }
}
