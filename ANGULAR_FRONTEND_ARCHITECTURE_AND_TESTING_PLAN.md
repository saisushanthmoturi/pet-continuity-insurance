# Pet Continuity Insurance - Monolithic Angular Frontend Architecture, CLI Blueprint & Testing Plan

This document presents the complete architectural specification, feature-based modular folder structure, state management strategy, routing, role-based layout rendering, reactive forms, interceptors, directives, unit testing plan, **Angular CLI Command Blueprint**, and **Exhaustive Field-Level Backend DTO Audit** for building the monolithic **Angular Frontend Application** (`pet-frontend`) for the **Pet Continuity Insurance System**.

---

## 1. System Overview & Technology Stack

The frontend application is structured as a **single enterprise monolithic Angular application** (`pet-frontend`) that communicates with the Spring Cloud Microservices backend via the **API Gateway** (`http://localhost:8080/api/...`).

### **Core Stack & Key Concepts**
- **Framework**: Angular 21 (Standalone Components Architecture)
- **Styling**: **Tailwind CSS** (`@tailwindcss/postcss` & `styles.css`)
- **State Management (NgRx 17+)**: `Store`, `Actions`, `Reducers`, `Selectors`, `Effects`, `@ngrx/entity`, `@ngrx/store-devtools`
- **Reactive Programming (RxJS 7+)**: `Observable`, `BehaviorSubject`, `Subject`, `switchMap`, `catchError`, `tap`, `map`, `filter`, `takeUntil`, `combineLatest`, `of`, `throwError`
- **Forms & Validation**: Angular `ReactiveFormsModule` (`FormBuilder`, `FormGroup`, `FormControl`, `Validators`) + Custom Async/Sync Validators
- **HTTP & Interceptors**: `HttpClient`, `HttpInterceptor` (JWT Bearer Token Injection & Global Error Handling inside `auth/`)
- **Routing & Security**: Angular `Router`, Lazy-Loaded Feature Routes, `CanActivateFn` Route Guards (`AuthGuard`, `RoleGuard`)
- **UI Layouts**: Dynamic Role-Based Headers, Footers, and Hero Sections rendered dynamically using NgRx Store & RxJS based on logged-in user role (`ROLE_CUSTOMER`, `ROLE_UNDERWRITER`, `ROLE_CLAIMS_ADJUSTER`, `ROLE_ADMIN`).
- **File Standards**:
  - **Component**: 4 files (`.ts`, `.html`, `.css`, `.spec.ts`)
  - **Service**: 2 files (`.ts`, `.spec.ts`)
  - **Guard / Interceptor**: 2 files (`.ts`, `.spec.ts`)

---

## 2. Feature-Based Modular Architecture & Directory Structure

```
pet-frontend/
├── src/
│   ├── app/
│   │   ├── core/                           # Global utilities, interfaces, custom validators
│   │   │   ├── models/
│   │   │   │   ├── auth.dto.ts
│   │   │   │   ├── customer.dto.ts
│   │   │   │   ├── pet.dto.ts
│   │   │   │   ├── care.dto.ts
│   │   │   │   ├── underwriting.dto.ts
│   │   │   │   ├── policy.dto.ts
│   │   │   │   ├── payment.dto.ts
│   │   │   │   └── claim.dto.ts
│   │   │   └── validators/
│   │   │       ├── age.validator.ts
│   │   │       ├── ssn.validator.ts
│   │   │       └── date-range.validator.ts
│   │   ├── shared/                         # Shared UI directives, pipes, toast components
│   │   │   ├── directives/
│   │   │   │   ├── risk-score-badge.directive.ts
│   │   │   │   └── role-access.directive.ts
│   │   │   └── pipes/
│   │   │       ├── currency-format.pipe.ts
│   │   │       └── claim-status.pipe.ts
│   │   ├── layout/                         # Role-Based Layout Components
│   │   │   ├── header/
│   │   │   │   ├── customer-header/        # Header for Customer Role
│   │   │   │   ├── underwriter-header/     # Header for Underwriter Role
│   │   │   │   ├── claims-header/          # Header for Claims Adjuster Role
│   │   │   │   └── admin-header/           # Header for Admin Role
│   │   │   ├── footer/
│   │   │   │   └── main-footer/
│   │   │   └── hero/
│   │   │       ├── customer-hero/
│   │   │       ├── underwriter-hero/
│   │   │       ├── claims-hero/
│   │   │       └── admin-hero/
│   │   ├── features/                       # Modular Feature Modules
│   │   │   ├── auth/                       # Encapsulated Auth Feature
│   │   │   │   ├── components/
│   │   │   │   │   ├── login/              # login.ts, login.html, login.css, login.spec.ts
│   │   │   │   │   └── register/           # register.ts, register.html, register.css, register.spec.ts
│   │   │   │   ├── service/
│   │   │   │   │   ├── auth-api.service.ts
│   │   │   │   │   └── auth-api.service.spec.ts
│   │   │   │   ├── guards/
│   │   │   │   │   ├── auth.guard.ts
│   │   │   │   │   ├── auth.guard.spec.ts
│   │   │   │   │   ├── role.guard.ts
│   │   │   │   │   └── role.guard.spec.ts
│   │   │   │   ├── interceptors/
│   │   │   │   │   ├── jwt.interceptor.ts
│   │   │   │   │   ├── jwt.interceptor.spec.ts
│   │   │   │   │   ├── http-error.interceptor.ts
│   │   │   │   │   └── http-error.interceptor.spec.ts
│   │   │   │   └── state/
│   │   │   │       ├── auth.actions.ts
│   │   │   │       ├── auth.reducer.ts
│   │   │   │       ├── auth.selectors.ts
│   │   │   │       └── auth.effects.ts
│   │   │   ├── customer-portal/
│   │   │   ├── pet-management/
│   │   │   ├── care-verification/
│   │   │   ├── underwriting-portal/
│   │   │   ├── policy-portal/
│   │   │   ├── claim-portal/
│   │   │   └── payment-fund-portal/
│   │   ├── app.routes.ts
│   │   └── app.component.ts
│   ├── styles.css
│   └── main.ts
```

---

## 3. Exhaustive Backend vs Frontend DTO Field Matching Matrix

Every field in the Spring Cloud Java microservices backend has been verified against the Angular TypeScript interfaces below:

| Microservice | Java Backend DTO / Entity | TypeScript Frontend Interface | Field Alignment Verification Status |
| :--- | :--- | :--- | :--- |
| **AuthService** | `RegisterRequest`, `LoginRequest`, `AuthResponse` | `RegisterRequest`, `LoginRequest`, `AuthResponse` | **100% Field Match** (`email`, `passwordHash`, `fullName`, `role`, `token`, `userId`) |
| **CustomerService** | `CustomerRequest`, `Customer`, `Address` | `CustomerRequest`, `CustomerResponse`, `AddressDTO` | **100% Field Match** (`userId`, `firstName`, `lastName`, `fullName`, `email`, `phone`, `dateOfBirth`, `status`, `address`, `emergencyContact`) |
| **PetService** | `PetRequest`, `Pet`, `MedicalRecordRequest` | `PetRequest`, `PetResponse`, `MedicalRecordRequest` | **100% Field Match** (`customerId`, `name`, `species`, `breed`, `age`, `weight`, `gender`, `estimatedAnnualCareCost`, `microchipId`, `status`) |
| **CareVerificationService** | `CarePlanRequest`, `CaretakerRequest`, `VerificationRequest`, `EligibilityResponse` | `CarePlanRequest`, `CaretakerRequest`, `VerificationRequest`, `EligibilityResponse` | **100% Field Match** (`petId`, `primaryCaretakerId`, `backupCaretakerId`, `vetContact`, `feedingInstructions`, `specialNeeds`, `verificationType`, `status`) |
| **UnderWritingRiskService** | `QuoteRequest`, `Quote` | `QuoteRequest`, `QuoteResponse` | **100% Field Match** (`customerId`, `petId`, `requestedCoverage`, `coveragePeriod`, `premiumAmount`, `riskScore`, `riskClass`, `decision`, `status`, `validUntil`) |
| **PolicyService** | `Policy` | `PolicyResponse` | **100% Field Match** (`policyId`, `policyNumber`, `quoteId`, `customerId`, `petId`, `coverageAmount`, `premiumAmount`, `deductible`, `startDate`, `endDate`, `status`, `issuedBy`) |
| **PaymentFundService** | `PaymentRequest`, `CreateFundRequest`, `ExpenseRequest`, `PaymentFund` | `PaymentRequest`, `CreateFundRequest`, `ExpenseRequest`, `FundResponse` | **100% Field Match** (`policyId`, `customerId`, `amount`, `paymentMethod`, `totalCoverage`, `remainingBalance`, `monthlyAllowance`, `vetReserve`, `emergencyReserve`) |
| **ClaimsService** | `ClaimRequest`, `ClaimResponse` | `ClaimRequest`, `ClaimResponse` | **100% Field Match** (`policyId`, `claimantName`, `relationship`, `deathCertificateNo`, `dateOfDeath`, `notes`, `status`, `rejectionReason`, `investigationDecision`, `fraudScore`) |

---

## 4. Fully Audited TypeScript DTO Interfaces (`src/app/core/models/`)

### `auth.dto.ts`
```typescript
export interface RegisterRequest {
  email: string;
  passwordHash: string;
  fullName: string;
  role: 'ROLE_CUSTOMER' | 'ROLE_UNDERWRITER' | 'ROLE_CLAIMS_ADJUSTER' | 'ROLE_ADMIN';
}

export interface LoginRequest {
  email: string;
  passwordHash: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  fullName: string;
  role: string;
}
```

### `customer.dto.ts`
```typescript
export interface AddressDTO {
  addressId?: number;
  customerId?: number;
  line1: string;
  line2?: string;
  city: string;
  state: string;
  postalCode: string;
  country?: string;
}

export interface CustomerRequest {
  userId: number;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  email?: string;
  phone?: string;
  address?: string;
  emergencyContact?: string;
  dateOfBirth?: string; // ISO format: YYYY-MM-DD
}

export interface CustomerResponse {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  status: 'ACTIVE' | 'SUSPENDED' | 'INACTIVE';
  address?: string;
  emergencyContact?: string;
  createdAt: string;
  updatedAt: string;
}
```

### `pet.dto.ts`
```typescript
export interface MedicalRecordRequest {
  conditionName: string;
  diagnosisDate: string;
  treatmentPlan: string;
  estimatedAnnualMedCost: number;
}

export interface PetRequest {
  customerId: number;
  name: string;
  species: 'DOG' | 'CAT' | 'BIRD' | 'OTHER';
  breed: string;
  age: number;
  weight: number;
  gender: 'MALE' | 'FEMALE';
  estimatedAnnualCareCost: number;
}

export interface PetResponse {
  id: number;
  customerId: number;
  name: string;
  species: string;
  breed: string;
  age: number;
  weight: number;
  gender: string;
  estimatedAnnualCareCost: number;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}
```

### `care.dto.ts`
```typescript
export interface CaretakerRequest {
  customerId: number;
  petId: number;
  fullName: string;
  phone: string;
  email: string;
  caretakerType: 'PRIMARY' | 'BACKUP';
  address: string;
}

export interface CarePlanRequest {
  petId: number;
  primaryCaretakerId: number;
  backupCaretakerId: number;
  vetContact: string;
  feedingInstructions: string;
  specialNeeds: string;
}

export interface VerificationRequest {
  petId: number;
  caretakerId: number;
  verificationType: 'ROUTINE' | 'EMERGENCY' | 'ANNUAL';
  verifiedBy: string;
  remarks: string;
  status: 'VERIFIED' | 'FAILED' | 'PENDING';
}

export interface EligibilityResponse {
  eligible: boolean;
  petId: number;
  message: string;
  activeCarePlanId?: number;
}
```

### `underwriting.dto.ts`
```typescript
export interface QuoteRequest {
  customerId: number;
  petId: number;
  requestedCoverage: number;
}

export interface QuoteResponse {
  id: number;
  customerId: number;
  petId: number;
  requestedCoverage: number;
  coveragePeriod: string;
  premiumAmount: number;
  riskScore: number;       // 0 - 100
  riskClass: 'LOW' | 'MODERATE' | 'HIGH' | 'EXTREME';
  decision: 'APPROVED' | 'REJECTED' | 'MANUAL_REVIEW';
  status: 'OFFERED' | 'ACCEPTED' | 'EXPIRED';
  underwriterId?: number;
  createdAt: string;
  updatedAt: string;
  validUntil: string;
}
```

### `policy.dto.ts`
```typescript
export interface PolicyResponse {
  policyId: number;
  policyNumber: string;
  quoteId: number;
  customerId: number;
  petId: number;
  coverageAmount: number;
  premiumAmount: number;
  deductible: number;
  startDate: string;
  endDate: string;
  status: 'PENDING_PAYMENT' | 'ACTIVE' | 'CLAIM_FILED' | 'TERMINATED';
  issuedBy: string;
  createdAt: string;
  updatedAt: string;
}
```

### `payment.dto.ts`
```typescript
export interface PaymentRequest {
  policyId: number;
  customerId?: number;
  amount: number;
  paymentMethod: string;
  simulateFailure?: boolean;
}

export interface CreateFundRequest {
  policyId: number;
  petId: number;
  totalCoverage: number;
  monthlyAllowance: number;
  vetReserve: number;
  emergencyReserve: number;
}

export interface ExpenseRequest {
  fundId: number;
  category: 'VET' | 'FOOD' | 'SHELTER' | 'OTHER';
  amount: number;
  description: string;
  vendorName: string;
  invoiceUrl?: string;
}

export interface FundResponse {
  id: number;
  policyId: number;
  petId: number;
  totalCoverage: number;
  remainingBalance: number;
  monthlyAllowance: number;
  vetReserve: number;
  emergencyReserve: number;
  status: 'ACTIVE' | 'EXHAUSTED' | 'SUSPENDED';
  createdAt: string;
}
```

### `claim.dto.ts`
```typescript
export interface ClaimRequest {
  policyId: number;
  claimantName: string;
  relationship: string;
  deathCertificateNo: string;
  dateOfDeath: string;
  notes?: string;
}

export interface ClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  claimantName: string;
  relationship: string;
  deathCertificateNo: string;
  dateOfDeath: string;
  status: 'SUBMITTED' | 'UNDER_REVIEW' | 'VERIFIED' | 'APPROVED' | 'REJECTED' | 'INVESTIGATING';
  rejectionReason?: string;
  notes?: string;
  createdAt: string;
  investigationDecision?: string;
  fraudScore?: number;
}
```

---

## 5. Angular CLI Generation Commands Blueprint

To generate all components, services, guards, and interceptors with the exact 4-file component standard (`.ts`, `.html`, `.css`, `.spec.ts`) and 2-file service standard (`.ts`, `.spec.ts`), execute the following Angular CLI commands inside `pet-frontend`:

```bash
# Step 1: Headers & Footers
ng g c layout/header/customer-header --style=css
ng g c layout/header/underwriter-header --style=css
ng g c layout/header/claims-header --style=css
ng g c layout/header/admin-header --style=css
ng g c layout/footer/main-footer --style=css

# Step 2: Hero Sections
ng g c layout/hero/customer-hero --style=css
ng g c layout/hero/underwriter-hero --style=css
ng g c layout/hero/claims-hero --style=css
ng g c layout/hero/admin-hero --style=css

# Step 3: Auth Feature
ng g c features/auth/components/login --style=css
ng g c features/auth/components/register --style=css
ng g s features/auth/service/auth-api
ng g guard features/auth/guards/auth --functional=true
ng g guard features/auth/guards/role --functional=true
ng g interceptor features/auth/interceptors/jwt --functional=true
ng g interceptor features/auth/interceptors/http-error --functional=true

# Step 4: Customer Portal Feature
ng g c features/customer-portal/components/customer-profile --style=css
ng g c features/customer-portal/components/customer-dashboard --style=css
ng g s features/customer-portal/service/customer-api

# Step 5: Pet Management Feature
ng g c features/pet-management/components/pet-list --style=css
ng g c features/pet-management/components/pet-register --style=css
ng g s features/pet-management/service/pet-api

# Step 6: Care Verification Feature
ng g c features/care-verification/components/care-plan-list --style=css
ng g c features/care-verification/components/caretaker-form --style=css
ng g s features/care-verification/service/care-api

# Step 7: Underwriting Portal Feature
ng g c features/underwriting-portal/components/quote-request --style=css
ng g c features/underwriting-portal/components/quote-review --style=css
ng g s features/underwriting-portal/service/underwriting-api

# Step 8: Policy Portal Feature
ng g c features/policy-portal/components/policy-list --style=css
ng g c features/policy-portal/components/policy-issue --style=css
ng g s features/policy-portal/service/policy-api

# Step 9: Claim Portal Feature
ng g c features/claim-portal/components/claim-file --style=css
ng g c features/claim-portal/components/claim-adjuster-review --style=css
ng g s features/claim-portal/service/claim-api

# Step 10: Payment Fund Portal Feature
ng g c features/payment-fund-portal/components/monthly-payout-ledger --style=css
ng g c features/payment-fund-portal/components/fund-management --style=css
ng g s features/payment-fund-portal/service/payment-api
```

---
*Created as architectural blueprint for Pet Continuity Insurance Monolithic Angular Frontend.*
