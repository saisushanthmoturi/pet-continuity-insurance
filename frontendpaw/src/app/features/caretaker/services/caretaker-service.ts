import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  BackupTransferDTO,
  DisbursementDTO,
  ExpenseDTO,
  PetCareFundDTO,
  PetVerificationDTO
} from '../models/caretakerDTO';

@Injectable({
  providedIn: 'root'
})
export class CaretakerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  submitPetVerification(verification: PetVerificationDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/care/verify-pet`, verification);
  }

  checkEligibility(): Observable<any> {
    return this.http.get(`${this.apiUrl}/care/eligibility/check`);
  }

  getFundByPolicyId(policyId: number): Observable<PetCareFundDTO> {
    return this.http.get<PetCareFundDTO>(`${this.apiUrl}/payments/funds/by-policy/${policyId}`);
  }

  getFundById(fundId: number): Observable<PetCareFundDTO> {
    return this.http.get<PetCareFundDTO>(`${this.apiUrl}/payments/funds/${fundId}`);
  }

  disburseMonthlyAllowance(fundId: number, caretakerId: number): Observable<DisbursementDTO> {
    return this.http.post<DisbursementDTO>(`${this.apiUrl}/payments/funds/${fundId}/disburse`, {
      caretakerId
    });
  }

  submitExpense(fundId: number, expense: ExpenseDTO): Observable<ExpenseDTO> {
    return this.http.post<ExpenseDTO>(`${this.apiUrl}/payments/funds/${fundId}/expense`, expense);
  }

  transferToBackup(transfer: BackupTransferDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/care/backup-transfer/${transfer.petId}`, transfer);
  }
}
