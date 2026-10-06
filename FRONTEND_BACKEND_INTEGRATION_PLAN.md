# Pet Continuity Insurance System - Frontend & Backend End-to-End Integration Plan

## 1. Architectural Overview & Integration Objectives

The **Pet Continuity Insurance System** provides guaranteed financial and care continuity for pets. The frontend is built with **Angular 21 (Standalone Components, NgRx State Management, RxJS, Reactive Forms, and Tailwind CSS)** and connects to a **Spring Cloud Microservices Backend** routed through an **API Gateway running on `http://localhost:8080`**.

This document outlines the master blueprint for connecting the Angular frontend to live backend microservice REST endpoints, enforcing role-based user interfaces, managing JWT authentication, and providing reactive loading states (`loading$`) across every CRUD transaction.

---

## 2. Security, Authentication & JWT Lifecycle

### 2.1 API Gateway & Auth Endpoint Specification
- **Base Gateway URL**: `http://localhost:8080`
- **Registration Endpoint**: `POST /api/auth/register`
- **Login Endpoint**: `POST /api/auth/login`
- **Token Validation**: `GET /api/auth/validate`

### 2.2 JWT Interceptor & Error Handling
1. **`jwtInterceptor`**:
   - Intercepts all outgoing HTTP requests targeting `/api/**`.
   - Reads `jwt_token` from `localStorage`.
   - Attaches `Authorization: Bearer <token>` header automatically.
2. **`httpErrorInterceptor`**:
   - Captures `401 Unauthorized` responses -> Clears stored token & dispatches NgRx `logout()` action.
   - Captures `403 Forbidden` responses -> Redirects user to their role-authorized dashboard.
   - Captures `500 Internal Server Error` -> Formats user-friendly error banners in UI.

### 2.3 Route Security & Role Guards
- **`authGuard`**: Verifies JWT presence and validity before routing to protected paths.
- **`roleGuard`**: Verifies user's decoded role claim matches the required route permission:
  - `ROLE_CUSTOMER`
  - `ROLE_UNDERWRITER`
  - `ROLE_CLAIMS_ADJUSTER`
  - `ROLE_ADMIN`

---

## 3. Role-Based Dynamic UI & Workspaces

The application header, hero banner, quick actions, and route accessibility dynamically adapt based on the logged-in user's role (`userRole$ | async`).

### 3.1 Role 1: Pet Owner (`ROLE_CUSTOMER`)
- **Header Navigation**: Dashboard | My Pets | Care Plans | Request Quote | My Policies | Claims | Payouts
- **Hero Banner**: "Pet Continuity Owner Portal - Protection & Care Agreements"
- **Workspace Features**:
  - View registered pets & register new pets with medical history & microchip IDs.
  - Formulate care routines & assign primary/backup caretakers.
  - Request actuarial insurance quotes.
  - View active policy contracts & remaining coverage limits.
  - File continuity claims with vet evidence & incident details.
  - Make monthly premium payments online.

### 3.2 Role 2: Underwriting Specialist (`ROLE_UNDERWRITER`)
- **Header Navigation**: Risk Assessment Workbench | Request Quote | Policy Registry | Issue Policy
- **Hero Banner**: "Underwriting Specialist Workbench - Actuarial Risk Engine & Quote Review"
- **Workspace Features**:
  - Actuarial Risk Assessment Engine calculating risk score (0-100) and category (`LOW`, `MEDIUM`, `HIGH`).
  - Manual Review Queue for flagged quotes exceeding automated threshold.
  - 1-click Quote Approval & Rejection with underwriter notes.
  - Issue & bind policy contracts directly from approved quotes.

### 3.3 Role 3: Claims Adjuster (`ROLE_CLAIMS_ADJUSTER`)
- **Header Navigation**: Claims Workbench | File Claim | Financial Payout Ledger
- **Hero Banner**: "Claims Adjuster Portal - Verification, Adjudication & Payouts"
- **Workspace Features**:
  - Review submitted continuity claims & vet receipts.
  - Fraud score inspection & investigation decision recording.
  - Approve claim payout amount or reject with formal reason modal.
  - Audit financial ledger of disbursements.

### 3.4 Role 4: System Administrator (`ROLE_ADMIN`)
- **Header Navigation**: Continuity Reserves Pool | Financial Ledger | Pet Registry | Policies Registry
- **Hero Banner**: "System Administrator Workspace - Solvency Health & Reserve Pools"
- **Workspace Features**:
  - Monitor Pet Continuity Reserve Fund Pool balance & emergency allocations.
  - Trigger monthly automated disbursements to caretakers.
  - Complete financial audit trail across premium inflows & claim payouts.

---

## 4. Universal Loading & Error States (`onloading` / `loading$`)

Every component and NgRx store slice implements explicit loading indicators to ensure smooth user feedback during async backend HTTP calls:

1. **NgRx Auth Store**:
   - `selectAuthLoading`: Drives spinner on `Login` & `Register` submit buttons.
   - `selectAuthError`: Displays red alert banners for authentication failures.
2. **Component Async Streams (`loading$` / `*ngIf="data$ | async as data; else loading"`)**:
   - **`CustomerDashboard`**: Centered pulsing spinner while fetching pets, care plans, policies, and claims in parallel via `combineLatest`.
   - **`PetList`**: Animated loading state during search and species filtering.
   - **`CarePlanList`**: Loading indicator during care protocol verification updates.
   - **`QuoteRequest`**: "Computing Risk Algorithms..." progress indicator on submission.
   - **`QuoteReview`**: Loading state during underwriter approval/rejection API calls.
   - **`PolicyList`**: Spinner during policy activation.
   - **`ClaimAdjusterReview`**: Loading indicator while submitting claim decisions.
   - **`MonthlyPayoutLedger`**: Spinner on premium online payment modal.
   - **`FundManagement`**: Loading indicator during monthly fund disbursement triggers.

---

## 5. Microservices API Endpoint & DTO Mapping Matrix

| Microservice | Gateway Path | Frontend API Service | DTO Schemas | Key Endpoints |
| :--- | :--- | :--- | :--- | :--- |
| **AuthService** | `/api/auth` | `AuthApiService` | `RegisterRequest`, `LoginRequest`, `AuthResponse` | `POST /register`<br>`POST /login` |
| **CustomerService** | `/api/customers` | `CustomerApiService` | `CustomerRequest`, `CustomerResponse` | `POST /`<br>`GET /{id}`<br>`GET /user/{userId}`<br>`PUT /{id}` |
| **PetService** | `/api/pets` | `PetApiService` | `PetRequest`, `PetResponse`, `MedicalRecordRequest` | `POST /`<br>`GET /customer/{customerId}`<br>`GET /{id}`<br>`POST /{petId}/medical-records` |
| **CareVerificationService** | `/api/care` | `CareApiService` | `CarePlanRequest`, `CaretakerRequest`, `VerificationRequest`, `EligibilityResponse` | `POST /caretakers`<br>`POST /care-plans`<br>`GET /care-plans/pet/{petId}`<br>`POST /verifications`<br>`GET /eligibility/check?petId={id}` |
| **UnderWritingRiskService** | `/api/underwriting` | `UnderwritingApiService` | `QuoteRequest`, `QuoteResponse` | `POST /quotes`<br>`GET /quotes`<br>`GET /quotes/{id}` |
| **PolicyService** | `/api/policies` | `PolicyApiService` | `PolicyResponse` | `POST /from-quote/{quoteId}`<br>`POST /{id}/activate`<br>`GET /customer/{customerId}` |
| **ClaimsService** | `/api/claims` | `ClaimApiService` | `ClaimRequest`, `ClaimResponse` | `POST /`<br>`GET /policy/{policyId}`<br>`GET /`<br>`POST /{id}/approve`<br>`POST /{id}/reject` |
| **PaymentFundService** | `/api/payments` | `PaymentApiService` | `PaymentRequest`, `CreateFundRequest`, `ExpenseRequest`, `FundResponse` | `POST /premium`<br>`POST /funds/create`<br>`POST /funds/{id}/disburse-monthly`<br>`GET /funds/by-policy/{id}` |

---

## 6. Step-by-Step Implementation Roadmap

When authorized by the user to proceed with coding, execution will follow these precise steps:

### Phase 1: Environment & API Base URL Configuration
- Configure `src/environments/environment.ts` with `apiUrl: 'http://localhost:8080'`.
- Verify `app.config.ts` HttpClient interceptors (`jwtInterceptor`, `httpErrorInterceptor`).

### Phase 2: Live Auth & Token Persistence Wire-Up
- Connect `AuthApiService` to live `/api/auth/login` and `/api/auth/register`.
- Update `AuthEffects` to store JWT token & decode role claims on boot.
- Verify quick demo account buttons trigger live authentication.

### Phase 3: DTO Alignment & Microservices Service Wire-Up
- Switch `CustomerApiService`, `PetApiService`, `CareApiService`, `UnderwritingApiService`, `PolicyApiService`, `ClaimApiService`, and `PaymentApiService` to make live HTTP calls to `http://localhost:8080/api/...`.
- Retain fallback error handling to ensure seamless UI operation if a specific backend microservice is starting up.

### Phase 4: Role-Based Component & Navigation Verification
- Verify header, hero, and route activation for `ROLE_CUSTOMER`, `ROLE_UNDERWRITER`, `ROLE_CLAIMS_ADJUSTER`, and `ROLE_ADMIN`.

### Phase 5: Loading State & Error Alert Audit
- Ensure every form, list view, and modal displays loading spinners (`loading$`) during active backend requests and red alert banners on errors.

---

> [!NOTE]
> **Status**: Comprehensive Frontend-to-Backend Integration Plan generated and saved to `FRONTEND_BACKEND_INTEGRATION_PLAN.md`. Ready for user review and signal to commence code execution.
