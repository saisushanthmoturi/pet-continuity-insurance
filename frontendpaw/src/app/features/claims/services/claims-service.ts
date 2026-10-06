import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClaimDTO, ClaimDocumentDTO } from '../models/claimDTO';

@Injectable({
  providedIn: 'root'
})
export class ClaimsService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/claims';

  fileClaim(claim: ClaimDTO): Observable<ClaimDTO> {
    return this.http.post<ClaimDTO>(`${this.apiUrl}`, claim);
  }

  getAllClaims(): Observable<ClaimDTO[]> {
    return this.http.get<ClaimDTO[]>(`${this.apiUrl}`);
  }

  getClaimById(id: number): Observable<ClaimDTO> {
    return this.http.get<ClaimDTO>(`${this.apiUrl}/${id}`);
  }

  getClaimsByPolicyId(policyId: number): Observable<ClaimDTO[]> {
    return this.http.get<ClaimDTO[]>(`${this.apiUrl}/policy/${policyId}`);
  }

  uploadDocument(claimId: number, document: ClaimDocumentDTO): Observable<ClaimDocumentDTO> {
    return this.http.post<ClaimDocumentDTO>(`${this.apiUrl}/${claimId}/documents`, document);
  }

  getDocuments(claimId: number): Observable<ClaimDocumentDTO[]> {
    return this.http.get<ClaimDocumentDTO[]>(`${this.apiUrl}/${claimId}/documents`);
  }

  verifyEvent(claimId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${claimId}/verify-death`, {});
  }

  investigateClaim(claimId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${claimId}/investigate`, {});
  }

  approveClaim(claimId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${claimId}/approve`, {});
  }

  rejectClaim(claimId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${claimId}/reject`, {});
  }
}
