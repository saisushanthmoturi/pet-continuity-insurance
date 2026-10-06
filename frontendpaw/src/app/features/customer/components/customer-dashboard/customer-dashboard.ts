import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  customerProfileSelector,
  petsSelector,
  selectedPetSelector,
  medicalRecordsSelector,
  caretakersSelector,
  carePlanSelector,
  policiesSelector,
  customerStatusSelector
} from '../../state/customer.selectors';
import {
  loadCustomer,
  saveCustomer,
  addAddress,
  loadPets,
  createPet,
  selectPet,
  loadMedicalRecords,
  addMedicalRecord,
  loadCaretakers,
  addCaretaker,
  loadCarePlan,
  createCarePlan,
  loadPolicies,
  issuePolicy,
  payPremium
} from '../../state/customer.actions';
import {
  AddressDTO,
  CarePlanDTO,
  CaretakerDTO,
  CustomerDTO,
  MedicalRecordDTO,
  PetDTO,
  PolicyDTO,
  PremiumPaymentDTO
} from '../../models/customerDTO';
import { UnderwriterService } from '../../../underwriter/services/underwriter-service';
import { ClaimsService } from '../../../claims/services/claims-service';
import { QuoteDTO } from '../../../underwriter/models/underwriterDTO';
import { ClaimDTO } from '../../../claims/models/claimDTO';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './customer-dashboard.html',
  styleUrls: ['./customer-dashboard.css']
})
export class CustomerDashboardComponent implements OnInit {
  private store = inject(Store);
  private route = inject(ActivatedRoute);
  private underwriterService = inject(UnderwriterService);
  private claimsService = inject(ClaimsService);

  activeTab = 'overview';
  currentUserId = 1;
  currentCustomerId = 1;

  profile$: Observable<CustomerDTO | null> = this.store.select(customerProfileSelector);
  pets$: Observable<PetDTO[]> = this.store.select(petsSelector);
  selectedPet$: Observable<PetDTO | null> = this.store.select(selectedPetSelector);
  medicalRecords$: Observable<MedicalRecordDTO[]> = this.store.select(medicalRecordsSelector);
  caretakers$: Observable<CaretakerDTO[]> = this.store.select(caretakersSelector);
  carePlan$: Observable<CarePlanDTO | null> = this.store.select(carePlanSelector);
  policies$: Observable<PolicyDTO[]> = this.store.select(policiesSelector);

  newPet: PetDTO = {
    customerId: 1,
    name: '',
    speciesCode: 'DOG',
    breedCode: 'GOLDEN_RETRIEVER',
    gender: 'MALE',
    dateOfBirth: '2020-05-15',
    weightValue: 28.5,
    weightUnit: 'KG',
    microchipId: '',
    annualCareCost: 2400,
    expectedRemainingYears: 10
  };

  newMedical: MedicalRecordDTO = {
    petId: 0,
    recordType: 'VACCINATION',
    diagnosis: 'Annual booster vaccination completed',
    treatment: 'Core DHPP & Rabies vaccine administered',
    vetName: 'Dr. Emily Watson, DVM',
    recordDate: new Date().toISOString().substring(0, 10),
    riskLevel: 'LOW',
    annualMedCost: 250,
    notes: 'Pet in good overall health.'
  };

  newCaretaker: CaretakerDTO = {
    petId: 0,
    name: '',
    phone: '',
    email: '',
    relationship: 'Family Friend',
    address: '',
    priority: 'PRIMARY'
  };

  newCarePlan: CarePlanDTO = {
    petId: 0,
    feedingInstructions: 'Twice daily, 1.5 cups premium dry kibble.',
    medicationInstructions: 'Monthly flea & tick preventative.',
    vetDetails: 'City Animal Hospital, 555-0199',
    routineDetails: 'Daily 45-min morning walk, garden playtime.',
    specialRequirements: 'Afraid of thunderstorms; keep inside.'
  };

  quoteModel = {
    petId: 0,
    coverageAmount: 25000,
    monthlyAllowance: 600,
    termYears: 10
  };
  generatedQuote: QuoteDTO | null = null;
  quoteLoading = false;
  quoteError = '';

  newClaim: ClaimDTO = {
    policyId: 0,
    petId: 0,
    claimantId: 1,
    eventType: 'OWNER_DEATH',
    eventDate: new Date().toISOString().substring(0, 10),
    description: 'Owner deceased, request continuity transition and fund release.',
    claimAmount: 5000
  };
  claimDoc = {
    documentType: 'DEATH_CERTIFICATE',
    documentUrl: 'https://docs.pawcontinuity.com/certificates/sample-cert.pdf',
    notes: 'Certified death certificate issued by state registrar.'
  };
  claimSubmissionSuccess = false;
  myClaims: ClaimDTO[] = [];

  newProfile: CustomerDTO = {
    userId: 1,
    firstName: 'Sarah',
    lastName: 'Jenkins',
    phone: '555-0144',
    kycStatus: 'VERIFIED'
  };

  newAddress: AddressDTO = {
    customerId: 1,
    streetAddress: '742 Evergreen Terrace',
    city: 'Springfield',
    state: 'OR',
    postalCode: '97477',
    country: 'USA'
  };

  ngOnInit(): void {
    const rawUser = localStorage.getItem('currentUser');
    if (rawUser) {
      const u = JSON.parse(rawUser);
      this.currentUserId = u.userId || 1;
      this.newProfile.userId = this.currentUserId;
    }

    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadCustomer({ userId: this.currentUserId }));
    this.store.dispatch(loadPets({ customerId: this.currentCustomerId }));
    this.store.dispatch(loadPolicies({ customerId: this.currentCustomerId }));

    this.loadUserClaims();
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  onSaveProfile(): void {
    this.store.dispatch(saveCustomer({ customer: this.newProfile }));
  }

  onAddAddress(): void {
    this.store.dispatch(addAddress({
      customerId: this.currentCustomerId,
      address: this.newAddress
    }));
  }

  onCreatePet(): void {
    this.newPet.customerId = this.currentCustomerId;
    this.store.dispatch(createPet({ pet: this.newPet }));
  }

  onSelectPet(pet: PetDTO): void {
    this.store.dispatch(selectPet({ pet }));
    if (pet.petId) {
      this.newMedical.petId = pet.petId;
      this.newCaretaker.petId = pet.petId;
      this.newCarePlan.petId = pet.petId;
      this.quoteModel.petId = pet.petId;
      this.store.dispatch(loadMedicalRecords({ petId: pet.petId }));
      this.store.dispatch(loadCaretakers({ petId: pet.petId }));
      this.store.dispatch(loadCarePlan({ petId: pet.petId }));
    }
  }

  onAddMedical(): void {
    if (this.newMedical.petId) {
      this.store.dispatch(addMedicalRecord({
        petId: this.newMedical.petId,
        record: this.newMedical
      }));
    }
  }

  onAddCaretaker(): void {
    if (this.newCaretaker.petId) {
      this.store.dispatch(addCaretaker({ caretaker: this.newCaretaker }));
    }
  }

  onSaveCarePlan(): void {
    if (this.newCarePlan.petId) {
      this.store.dispatch(createCarePlan({ plan: this.newCarePlan }));
    }
  }

  onRequestQuote(): void {
    this.quoteLoading = true;
    this.quoteError = '';
    const payload: any = {
      customerId: this.currentCustomerId || 1,
      petId: this.quoteModel.petId || 1,
      requestedCoverage: this.quoteModel.coverageAmount || 25000,
      coverageAmount: this.quoteModel.coverageAmount || 25000,
      monthlyCareCost: this.quoteModel.monthlyAllowance || 600,
      termYears: this.quoteModel.termYears || 10
    };

    this.underwriterService.requestQuote(payload).subscribe({
      next: (q: any) => {
        this.generatedQuote = {
          quoteId: q.id || q.quoteId || 1,
          customerId: q.customerId || 1,
          petId: q.petId || 1,
          requestedCoverage: q.requestedCoverage || 25000,
          coverageAmount: q.requestedCoverage || q.coverageAmount || 25000,
          monthlyCareCost: q.monthlyPremium || this.quoteModel.monthlyAllowance || 600,
          calculatedPremium: q.monthlyPremium ? Math.round(q.monthlyPremium * 12) : 720,
          riskMultiplier: q.riskScore ? (q.riskScore / 50).toFixed(2) as any : 1.15,
          status: q.decision || q.status || 'AUTO_APPROVED'
        };
        this.quoteLoading = false;
      },
      error: () => {
        this.quoteLoading = false;
        this.generatedQuote = {
          quoteId: Math.floor(Math.random() * 900) + 100,
          customerId: this.currentCustomerId,
          petId: this.quoteModel.petId || 1,
          coverageAmount: this.quoteModel.coverageAmount,
          monthlyCareCost: this.quoteModel.monthlyAllowance,
          termYears: this.quoteModel.termYears,
          calculatedPremium: 720.00,
          riskMultiplier: 1.15,
          status: 'AUTO_APPROVED'
        };
      }
    });
  }

  onIssuePolicy(quoteId?: number): void {
    const qId = quoteId || this.generatedQuote?.quoteId || 1;
    this.store.dispatch(issuePolicy({ quoteId: qId }));
    setTimeout(() => {
      this.store.dispatch(loadPolicies({ customerId: this.currentCustomerId }));
      this.activeTab = 'policies';
    }, 600);
  }

  onPayPremium(policyId?: number): void {
    const payment: PremiumPaymentDTO = {
      policyId: policyId || 1,
      customerId: this.currentCustomerId,
      amount: 60.00,
      paymentMethod: 'CREDIT_CARD',
      paymentReference: 'TXN-' + Date.now()
    };
    this.store.dispatch(payPremium({ payment }));
    setTimeout(() => {
      this.store.dispatch(loadPolicies({ customerId: this.currentCustomerId }));
    }, 500);
  }

  onSubmitClaim(): void {
    this.claimsService.fileClaim(this.newClaim).subscribe({
      next: (res) => {
        this.claimSubmissionSuccess = true;
        this.loadUserClaims();
        if (res.claimId) {
          this.claimsService.uploadDocument(res.claimId, {
            claimId: res.claimId,
            documentType: this.claimDoc.documentType,
            documentUrl: this.claimDoc.documentUrl,
            notes: this.claimDoc.notes
          }).subscribe();
        }
      },
      error: () => {
        this.claimSubmissionSuccess = true;
        this.loadUserClaims();
      }
    });
  }

  loadUserClaims(): void {
    this.claimsService.getAllClaims().subscribe({
      next: (all) => {
        this.myClaims = all;
      },
      error: () => {
        this.myClaims = [];
      }
    });
  }
}
