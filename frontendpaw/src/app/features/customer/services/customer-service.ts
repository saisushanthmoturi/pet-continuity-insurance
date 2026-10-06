import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AddressDTO,
  CarePlanDTO,
  CaretakerDTO,
  CustomerDTO,
  MedicalRecordDTO,
  PetDTO,
  PolicyDTO,
  PremiumPaymentDTO
} from '../models/customerDTO';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  // Customer Profile
  createCustomer(customer: CustomerDTO): Observable<CustomerDTO> {
    return this.http.post<CustomerDTO>(`${this.apiUrl}/customers`, customer);
  }

  getCustomerByUserId(userId: number): Observable<CustomerDTO> {
    return this.http.get<CustomerDTO>(`${this.apiUrl}/customers/user/${userId}`);
  }

  addAddress(customerId: number, address: AddressDTO): Observable<AddressDTO> {
    return this.http.post<AddressDTO>(`${this.apiUrl}/customers/${customerId}/addresses`, address);
  }

  getAddresses(customerId: number): Observable<AddressDTO[]> {
    return this.http.get<AddressDTO[]>(`${this.apiUrl}/customers/${customerId}/addresses`);
  }

  // Pet Management
  createPet(pet: PetDTO): Observable<PetDTO> {
    return this.http.post<PetDTO>(`${this.apiUrl}/pets`, pet);
  }

  getPetsByCustomerId(customerId: number): Observable<PetDTO[]> {
    return this.http.get<PetDTO[]>(`${this.apiUrl}/pets/customer/${customerId}`);
  }

  addMedicalRecord(petId: number, record: MedicalRecordDTO): Observable<MedicalRecordDTO> {
    return this.http.post<MedicalRecordDTO>(`${this.apiUrl}/pets/${petId}/medical-records`, record);
  }

  getMedicalRecords(petId: number): Observable<MedicalRecordDTO[]> {
    return this.http.get<MedicalRecordDTO[]>(`${this.apiUrl}/pets/${petId}/medical-records`);
  }

  // Caretaker & Care Plan
  addCaretaker(caretaker: CaretakerDTO): Observable<CaretakerDTO> {
    return this.http.post<CaretakerDTO>(`${this.apiUrl}/care/caretakers`, caretaker);
  }

  getCaretakersByPetId(petId: number): Observable<CaretakerDTO[]> {
    return this.http.get<CaretakerDTO[]>(`${this.apiUrl}/care/caretakers/pet/${petId}`);
  }

  createCarePlan(plan: CarePlanDTO): Observable<CarePlanDTO> {
    return this.http.post<CarePlanDTO>(`${this.apiUrl}/care/care-plans`, plan);
  }

  getCarePlanByPetId(petId: number): Observable<CarePlanDTO> {
    return this.http.get<CarePlanDTO>(`${this.apiUrl}/care/care-plans/pet/${petId}`);
  }

  // Policies & Payments
  issuePolicyFromQuote(quoteId: number): Observable<PolicyDTO> {
    return this.http.post<PolicyDTO>(`${this.apiUrl}/policies/from-quote/${quoteId}`, {});
  }

  getPoliciesByCustomerId(customerId: number): Observable<PolicyDTO[]> {
    return this.http.get<PolicyDTO[]>(`${this.apiUrl}/policies/customer/${customerId}`);
  }

  payPremium(payment: PremiumPaymentDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/payments/premium`, payment);
  }

  activatePolicy(policyId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/policies/${policyId}/activate`, {});
  }
}
