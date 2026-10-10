import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map, switchMap } from "rxjs";
import {
  AddressDTO,
  CarePlanDTO,
  CaretakerDTO,
  CustomerDTO,
  MedicalRecordDTO,
  PetDTO,
  PolicyDTO,
  PremiumPaymentDTO
} from "../models/customerDTO";

@Injectable({
  providedIn: "root"
})
export class CustomerService {
  private http = inject(HttpClient);
  private apiUrl = "http://localhost:8080/api";

  // Customer Profile
  createCustomer(customer: CustomerDTO): Observable<CustomerDTO> {
    const fn = customer.firstName || "Customer";
    const ln = customer.lastName || "";
    const payload = {
      userId: customer.userId || 1,
      email: (customer as any).email || (customer as any).contactEmail || ("user" + (customer.userId || 1) + "@example.com"),
      fullName: (fn + " " + ln).trim() || "Customer",
      firstName: fn,
      lastName: ln,
      phone: customer.phone || "555-0100",
      kycStatus: customer.kycStatus || "VERIFIED"
    };
    return this.http.post<any>(`${this.apiUrl}/customers`, payload).pipe(
      map((res: any) => ({
        customerId: res.id || res.customerId,
        userId: res.userId,
        firstName: res.firstName,
        lastName: res.lastName,
        phone: res.phone,
        kycStatus: res.kycStatus,
        status: res.status || "ACTIVE"
      }))
    );
  }

  updateCustomer(customerId: number, customer: CustomerDTO): Observable<CustomerDTO> {
    const fn = customer.firstName || "Customer";
    const ln = customer.lastName || "";
    const payload = {
      userId: customer.userId,
      email: (customer as any).email || (customer as any).contactEmail || ("user" + (customer.userId || 1) + "@example.com"),
      fullName: (fn + " " + ln).trim() || "Customer",
      firstName: fn,
      lastName: ln,
      phone: customer.phone || "555-0100",
      kycStatus: customer.kycStatus || "VERIFIED"
    };
    return this.http.put<any>(`${this.apiUrl}/customers/${customerId}`, payload).pipe(
      map((res: any) => ({
        customerId: res.id || res.customerId || customerId,
        userId: res.userId || customer.userId,
        firstName: res.firstName || fn,
        lastName: res.lastName || ln,
        email: res.email || (customer as any).email,
        phone: res.phone || customer.phone,
        kycStatus: res.kycStatus || "VERIFIED",
        status: res.status || "ACTIVE"
      }))
    );
  }

  getCustomerByUserId(userId: number): Observable<CustomerDTO> {
    return this.http.get<any>(`${this.apiUrl}/customers/user/${userId}`).pipe(
      map((res: any) => ({
        customerId: res.id || res.customerId,
        userId: res.userId,
        firstName: res.firstName,
        lastName: res.lastName,
        phone: res.phone,
        kycStatus: res.kycStatus,
        status: res.status || "ACTIVE"
      }))
    );
  }

  addAddress(customerId: number, address: AddressDTO): Observable<AddressDTO> {
    const payload = {
      streetAddress: address.streetAddress || "",
      city: address.city || "",
      state: address.state || "",
      postalCode: address.postalCode || "",
      country: address.country || "USA"
    };
    return this.http.post<any>(`${this.apiUrl}/customers/${customerId}/addresses`, payload).pipe(
      map((res: any) => ({
        addressId: res.id || res.addressId,
        customerId: res.customerId || customerId,
        streetAddress: res.streetAddress,
        city: res.city,
        state: res.state,
        postalCode: res.postalCode,
        country: res.country
      }))
    );
  }

  getAddresses(customerId: number): Observable<AddressDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/customers/${customerId}/addresses`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          addressId: res.id || res.addressId,
          customerId: res.customerId || customerId,
          streetAddress: res.streetAddress,
          city: res.city,
          state: res.state,
          postalCode: res.postalCode,
          country: res.country
        }))
      )
    );
  }

  // Pet Management
  createPet(pet: PetDTO): Observable<PetDTO> {
    const calculatedAge = pet.dateOfBirth
      ? Math.max(1, new Date().getFullYear() - new Date(pet.dateOfBirth).getFullYear())
      : (pet as any).age || 3;

    const payload = {
      customerId: Number(pet.customerId) || 1,
      name: pet.name,
      species: pet.speciesCode || (pet as any).species || "DOG",
      breed: pet.breedCode || (pet as any).breed || "MIXED",
      age: calculatedAge,
      weight: Number(pet.weightValue) || Number((pet as any).weight) || 15.0,
      gender: pet.gender || "MALE",
      estimatedAnnualCareCost: Number(pet.annualCareCost) || Number((pet as any).estimatedAnnualCareCost) || 1200.0
    };

    return this.http.post<any>(`${this.apiUrl}/pets`, payload).pipe(
      map((res: any) => ({
        petId: res.id || res.petId,
        customerId: res.customerId,
        name: res.name,
        speciesCode: res.species || res.speciesCode || "DOG",
        breedCode: res.breed || res.breedCode || "MIXED",
        gender: res.gender || "MALE",
        dateOfBirth: res.dateOfBirth || pet.dateOfBirth,
        weightValue: res.weight !== undefined ? res.weight : (res.weightValue !== undefined ? res.weightValue : 15),
        weightUnit: res.weightUnit || "KG",
        microchipId: res.microchipId || pet.microchipId || "",
        annualCareCost: res.annualCareCost !== undefined ? res.annualCareCost : (res.estimatedAnnualCareCost || 1200),
        expectedRemainingYears: res.expectedRemainingYears || 10,
        status: res.status || "ACTIVE"
      }))
    );
  }

  getPetsByCustomerId(customerId: number): Observable<PetDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pets/customer/${customerId}`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          petId: res.id || res.petId,
          customerId: res.customerId,
          name: res.name,
          speciesCode: res.species || res.speciesCode || "DOG",
          breedCode: res.breed || res.breedCode || "MIXED",
          gender: res.gender || "MALE",
          dateOfBirth: res.dateOfBirth || "",
          weightValue: res.weight !== undefined ? res.weight : (res.weightValue !== undefined ? res.weightValue : 15),
          weightUnit: res.weightUnit || "KG",
          microchipId: res.microchipId || "",
          annualCareCost: res.annualCareCost !== undefined ? res.annualCareCost : (res.estimatedAnnualCareCost || 1200),
          expectedRemainingYears: res.expectedRemainingYears || 10,
          status: res.status || "ACTIVE"
        }))
      )
    );
  }

  addMedicalRecord(petId: number, record: MedicalRecordDTO): Observable<MedicalRecordDTO> {
    const payload = {
      conditionName: record.diagnosis || record.recordType || "General Health",
      diagnosisDate: record.recordDate || new Date().toISOString().substring(0, 10),
      treatmentPlan: record.treatment || "Standard medical management",
      estimatedAnnualMedCost: Number(record.annualMedCost) || 250.0
    };
    return this.http.post<any>(`${this.apiUrl}/pets/${petId}/medical-records`, payload).pipe(
      map((res: any) => ({
        medicalRecordId: res.id || res.medicalRecordId,
        petId: res.petId || petId,
        recordType: record.recordType || "VACCINATION",
        diagnosis: res.conditionName || record.diagnosis,
        treatment: res.treatmentPlan || record.treatment,
        vetName: record.vetName || "Attending Veterinarian",
        recordDate: res.diagnosisDate || record.recordDate,
        riskLevel: record.riskLevel || "LOW",
        annualMedCost: res.estimatedAnnualMedCost || record.annualMedCost || 250,
        notes: record.notes || ""
      }))
    );
  }

  getMedicalRecords(petId: number): Observable<MedicalRecordDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pets/${petId}/medical-records`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          medicalRecordId: res.id || res.medicalRecordId,
          petId: res.petId || petId,
          recordType: "CLINICAL_NOTE",
          diagnosis: res.conditionName,
          treatment: res.treatmentPlan || "N/A",
          vetName: "Attending Vet",
          recordDate: res.diagnosisDate || "",
          riskLevel: "LOW",
          annualMedCost: res.estimatedAnnualMedCost || 200,
          notes: ""
        }))
      )
    );
  }

  // Caretaker & Care Plan
  addCaretaker(caretaker: CaretakerDTO): Observable<CaretakerDTO> {
    const payload = {
      customerId: (caretaker as any).customerId || 1,
      petId: caretaker.petId || 1,
      fullName: caretaker.name || (caretaker as any).fullName || "Assigned Caretaker",
      phone: caretaker.phone || "555-0199",
      email: caretaker.email || "caretaker@example.com",
      caretakerType: caretaker.priority || "PRIMARY",
      address: caretaker.address || "Address on file"
    };
    return this.http.post<any>(`${this.apiUrl}/care/caretakers`, payload).pipe(
      map((res: any) => ({
        caretakerId: res.id || res.caretakerId,
        petId: res.petId || caretaker.petId,
        name: res.fullName || caretaker.name,
        phone: res.phone || caretaker.phone,
        email: res.email || caretaker.email,
        relationship: caretaker.relationship || "Family Friend",
        address: res.address || caretaker.address,
        priority: res.caretakerType || caretaker.priority || "PRIMARY",
        status: res.status || "ACTIVE"
      }))
    );
  }

  getCaretakersByPetId(petId: number): Observable<CaretakerDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/care/caretakers/pet/${petId}`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          caretakerId: res.id || res.caretakerId,
          petId: res.petId || petId,
          name: res.fullName || res.name || "Caretaker",
          phone: res.phone || "",
          email: res.email || "",
          relationship: res.relationship || "Family Friend",
          address: res.address || "",
          priority: res.caretakerType || "PRIMARY",
          status: res.status || "ACTIVE"
        }))
      )
    );
  }

  createCarePlan(plan: CarePlanDTO): Observable<CarePlanDTO> {
    const payload = {
      petId: plan.petId || 1,
      primaryCaretakerId: (plan as any).primaryCaretakerId || 1,
      backupCaretakerId: (plan as any).backupCaretakerId || null,
      vetContact: plan.vetDetails || "City Animal Hospital",
      feedingInstructions: plan.feedingInstructions || "Standard feeding schedule",
      specialNeeds: plan.specialRequirements || plan.routineDetails || plan.medicationInstructions || "None"
    };
    return this.http.post<any>(`${this.apiUrl}/care/care-plans`, payload).pipe(
      map((res: any) => ({
        planId: res.id || res.planId,
        petId: res.petId,
        feedingInstructions: res.feedingInstructions,
        medicationInstructions: plan.medicationInstructions || "",
        vetDetails: res.vetContact,
        routineDetails: plan.routineDetails || "",
        specialRequirements: res.specialNeeds
      }))
    );
  }

  getCarePlanByPetId(petId: number): Observable<CarePlanDTO> {
    return this.http.get<any>(`${this.apiUrl}/care/care-plans/pet/${petId}`).pipe(
      map((res: any) => ({
        planId: res.id || res.planId,
        petId: res.petId,
        feedingInstructions: res.feedingInstructions || "",
        medicationInstructions: "Monthly preventative",
        vetDetails: res.vetContact || "",
        routineDetails: "Daily activity schedule",
        specialRequirements: res.specialNeeds || ""
      }))
    );
  }

  // Policies & Payments
  issuePolicyFromQuote(quoteId: number): Observable<PolicyDTO> {
    return this.http.post<any>(`${this.apiUrl}/policies/from-quote/${quoteId}`, {}).pipe(
      map((res: any) => ({
        policyId: res.id || res.policyId,
        policyNumber: res.policyNumber || "POL-00" + (res.id || res.policyId),
        customerId: res.customerId,
        petId: res.petId,
        coverageAmount: res.coverageAmount,
        premiumAmount: res.premiumAmount,
        status: res.status || "ACTIVE"
      }))
    );
  }

  getPoliciesByCustomerId(customerId: number): Observable<PolicyDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/policies/customer/${customerId}`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          policyId: res.id || res.policyId,
          policyNumber: res.policyNumber || "POL-00" + (res.id || res.policyId),
          customerId: res.customerId,
          petId: res.petId,
          coverageAmount: res.coverageAmount,
          premiumAmount: res.premiumAmount,
          status: res.status || "ACTIVE",
          startDate: res.startDate || res.effectiveDate,
          endDate: res.endDate || res.expiryDate
        }))
      )
    );
  }

  payPremium(payment: PremiumPaymentDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/payments/premium`, payment);
  }

  activatePolicy(policyId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/policies/${policyId}/activate`, {});
  }

  payAndActivatePolicy(policyId: number, customerId: number, amount: number): Observable<any> {
    const payment = {
      policyId,
      customerId: customerId || 1,
      amount: amount || 75.00,
      paymentMethod: "CREDIT_CARD",
      simulateFailure: false
    };
    return this.http.post(`${this.apiUrl}/payments/premium`, payment).pipe(
      switchMap(() => this.http.post(`${this.apiUrl}/policies/${policyId}/activate`, {}))
    );
  }
}
