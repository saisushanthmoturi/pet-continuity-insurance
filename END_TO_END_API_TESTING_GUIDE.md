# Pet Continuity Insurance - End-to-End API Testing Guide
### Complete POST and GET Lifecycle (Auth -> Customer -> Pet -> Care Verification -> Underwriting -> Policy)

This guide provides step-by-step requests, payloads, headers, and expected responses to test the full business lifecycle through the **API Gateway** (`http://localhost:8080`).

---

## Quick Navigation
1. [Authentication Service (`AuthService`)](#1-authenticationservice-authservice)
2. [Customer Profile & Address Service (`CustomerService`)](#2-customer-profile--address-service-customerservice)
3. [Pet & Medical Records Service (`PetService`)](#3-pet--medical-records-service-petservice)
4. [Care Plan & Verification Service (`CareVerificationService`)](#4-care-plan--verification-service-careverificationservice)
5. [Underwriting & Actuarial Risk Service (`UnderWritingRiskService`)](#5-underwriting--actuarial-risk-service-underwritingriskservice)
6. [Policy Issuance & Lifecycle Service (`PolicyService`)](#6-policy-issuance--lifecycle-service-policyservice)

---

## Global Headers & Authentication

All microservices (except auth registration/login) require authentication via the API Gateway.
Include this header in every request after obtaining your token:

```http
Authorization: Bearer <YOUR_JWT_TOKEN>
Content-Type: application/json
```

---

## 1. Authentication Service (`AuthService`)
> **Direct Port**: `8081` | **Gateway Route**: `/api/auth/**`

### 1.1 Register New Customer Account (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/auth/register`
* **Request Body**:
```json
{
  "email": "testuser@example.com",
  "password": "Password123!",
  "role": "CUSTOMER"
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "testuser@example.com",
  "role": "CUSTOMER",
  "expiresIn": 86400000
}
```

### 1.2 User Login (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/auth/login`
* **Request Body**:
```json
{
  "email": "testuser@example.com",
  "password": "Password123!"
}
```
* **Expected Response (`200 OK`)**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "testuser@example.com",
  "role": "CUSTOMER",
  "expiresIn": 86400000
}
```

### 1.3 Validate Token (`GET`)
* **Endpoint**: `GET http://localhost:8080/api/auth/validate`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Expected Response (`200 OK`)**:
```json
{
  "valid": true,
  "userId": 1,
  "role": "CUSTOMER",
  "email": "testuser@example.com"
}
```

---

## 2. Customer Profile & Address Service (`CustomerService`)
> **Direct Port**: `8082` | **Gateway Route**: `/api/customers/**`

### 2.1 Create Customer Profile (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/customers`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1-555-0123",
  "contactEmail": "testuser@example.com",
  "dateOfBirth": "1988-06-15"
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "id": 1,
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "contactEmail": "testuser@example.com",
  "phone": "+1-555-0123",
  "dateOfBirth": "1988-06-15",
  "status": "ACTIVE"
}
```

### 2.2 Add Customer Address (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/customers/addresses`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "customerId": 1,
  "addressType": "PRIMARY",
  "line1": "742 Evergreen Terrace",
  "line2": "Apt 4B",
  "city": "Springfield",
  "state": "OR",
  "postalCode": "97477",
  "country": "USA"
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "addressId": 1,
  "customerId": 1,
  "addressType": "PRIMARY",
  "line1": "742 Evergreen Terrace",
  "city": "Springfield",
  "state": "OR",
  "postalCode": "97477",
  "country": "USA"
}
```

### 2.3 Get Customer Profile by ID (`GET`)
* **Endpoint**: `GET http://localhost:8080/api/customers/1`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Expected Response (`200 OK`)**:
Returns John Doe's full profile.

### 2.4 Get Addresses for Customer (`GET`)
* **Endpoint**: `GET http://localhost:8080/api/customers/1/addresses`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Expected Response (`200 OK`)**:
Returns list of registered addresses.

---

## 3. Pet & Medical Records Service (`PetService`)
> **Direct Port**: `8083` | **Gateway Route**: `/api/pets/**`

### 3.1 Register Pet (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/pets`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "customerId": 1,
  "name": "Buddy",
  "speciesCode": "DOG",
  "breedCode": "GOLDEN_RETRIEVER",
  "gender": "MALE",
  "dateOfBirth": "2021-04-10",
  "dateOfBirthEstimated": false,
  "weightValue": 31.5,
  "weightUnit": "KG",
  "microchipId": "985141002348911",
  "neuteredStatus": true,
  "status": "ACTIVE",
  "annualCareCost": 1200.0,
  "expectedRemainingYears": 11,
  "currency": "USD"
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "id": 1,
  "customerId": 1,
  "name": "Buddy",
  "speciesCode": "DOG",
  "breedCode": "GOLDEN_RETRIEVER",
  "gender": "MALE",
  "dateOfBirth": "2021-04-10",
  "weightValue": 31.5,
  "microchipId": "985141002348911",
  "neuteredStatus": true,
  "status": "ACTIVE",
  "annualCareCost": 1200.0,
  "expectedRemainingYears": 11,
  "currency": "USD"
}
```

### 3.2 Add Pet Medical Record (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/pets/1/medical-records`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "conditionName": "Annual Vaccine & Wellness Exam",
  "diagnosisDate": "2026-03-15",
  "treatmentPlan": "DHPP booster, Rabies vaccine, flea preventative",
  "estimatedAnnualMedCost": 250.0
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "id": 1,
  "petId": 1,
  "conditionName": "Annual Vaccine & Wellness Exam",
  "diagnosisDate": "2026-03-15",
  "treatmentPlan": "DHPP booster, Rabies vaccine, flea preventative",
  "estimatedAnnualMedCost": 250.0
}
```

### 3.3 Get Pet by ID (`GET`)
* **Endpoint**: `GET http://localhost:8080/api/pets/1`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Expected Response (`200 OK`)**:
Returns Buddy's full profile.

### 3.4 Get All Pets for Customer (`GET`)
* **Endpoint**: `GET http://localhost:8080/api/pets/customer/1`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Expected Response (`200 OK`)**:
Returns array containing `[ Buddy ]`.

### 3.5 Get Medical Records for Pet (`GET`)
* **Endpoint**: `GET http://localhost:8080/api/pets/1/medical-records`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Expected Response (`200 OK`)**:
Returns list of medical records for Pet 1.

---

## 4. Care Plan & Verification Service (`CareVerificationService`)
> **Direct Port**: `8088` | **Gateway Route**: `/api/care/**`

### 4.1 Register Primary Caretaker (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/care/caretakers`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "customerId": 1,
  "petId": 1,
  "fullName": "Sarah Jenkins",
  "phone": "+1-555-0199",
  "email": "sarah.jenkins@example.com",
  "caretakerType": "PRIMARY",
  "address": "742 Evergreen Terrace, Springfield, OR"
}
```
* **Expected Response (`201 CREATED`)**:
Returns Caretaker #1 details (`Sarah Jenkins`).

### 4.2 Register Backup Caretaker (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/care/caretakers`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "customerId": 1,
  "petId": 1,
  "fullName": "Michael Chang",
  "phone": "+1-555-0288",
  "email": "michael.chang@example.com",
  "caretakerType": "BACKUP",
  "address": "124 Conch Street, Springfield, OR"
}
```
* **Expected Response (`201 CREATED`)**:
Returns Caretaker #2 details (`Michael Chang`).

### 4.3 Create Continuity Care Plan for Pet 1 (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/care/care-plans`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "petId": 1,
  "primaryCaretakerId": 1,
  "backupCaretakerId": 2,
  "vetContact": "Dr. Miller, Valley Veterinary Clinic (+1-555-0144)",
  "feedingInstructions": "2 cups dry kibble twice daily at 8 AM and 6 PM",
  "specialNeeds": "Daily joint supplement with breakfast, allergic to beef"
}
```
* **Expected Response (`200 OK`)**:
Returns Care Plan details for Pet 1.

### 4.4 Record Pet Welfare Verification Check-in (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/care/verifications`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "petId": 1,
  "caretakerId": 1,
  "verificationDate": "2026-09-20",
  "status": "PASSED",
  "notes": "Monthly check-in: Buddy is healthy, active, and current on vaccinations."
}
```
* **Expected Response (`201 CREATED`)**:
Returns recorded Pet verification check-in.

### 4.5 Update Caretaker Availability Status (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/care/caretakers/1/status`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "status": "ACTIVE"
}
```

### 4.6 Record Caretaker KYC Verification (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/care/caretakers/1/verifications`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "verificationType": "IDENTITY",
  "verificationMethod": "GOVERNMENT_ID",
  "verificationStatus": "VERIFIED",
  "evidenceReference": "PASSPORT-VERIF-2026-0091",
  "verificationBy": "ADMIN_OFFICER",
  "failureReason": null
}
```

### 4.7 Care Verification Query Endpoints (`GET`)
* **Get Caretaker by ID**:
  `GET http://localhost:8080/api/care/caretakers/1`
* **Get Caretakers for Pet 1**:
  `GET http://localhost:8080/api/care/caretakers/pet/1`
* **Get Care Plan for Pet 1**:
  `GET http://localhost:8080/api/care/care-plans/pet/1`
* **Get Pet Welfare Verifications**:
  `GET http://localhost:8080/api/care/verifications/pet/1`
* **Get Caretaker KYC History**:
  `GET http://localhost:8080/api/care/caretakers/1/verifications`
* **Check Monthly Care Continuity Eligibility**:
  `GET http://localhost:8080/api/care/eligibility/check?petId=1&caretakerId=1`

---

## 5. Underwriting & Actuarial Risk Service (`UnderWritingRiskService`)
> **Direct Port**: `8084` | **Gateway Route**: `/api/underwriting/**`

### 5.1 Generate Actuarial Quote (`POST`)
This coordinates with `PetService`, `CustomerService`, and `CareVerificationService` to compute risk scores, projected continuity liabilities, and pricing.

* **Endpoint**: `POST http://localhost:8080/api/underwriting/quotes`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "customerId": 1,
  "petId": 1,
  "requestedCoverage": 25000.0
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "id": 1,
  "customerId": 1,
  "petId": 1,
  "requestedCoverage": 25000.0,
  "monthlyPremium": 75.0,
  "riskScore": 30,
  "decision": "APPROVED",
  "status": "OFFERED",
  "projectedCareLiability": 12600.0,
  "coverageGap": 0.0,
  "riskLevel": "LOW",
  "validUntil": "2026-10-21T..."
}
```

### 5.2 Add Actuarial Rating Rule (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/underwriting/rules`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "ruleName": "Senior Pet Risk Multiplier",
  "factor": "AGE",
  "minValue": 8.0,
  "maxValue": 15.0,
  "score": 25,
  "premiumFactor": 1.25,
  "status": "ACTIVE",
  "effectiveFrom": "2026-01-01",
  "effectiveTo": "2027-01-01"
}
```

### 5.3 Update Quote Coverage Amount (`PUT`)
* **Endpoint**: `PUT http://localhost:8080/api/underwriting/quotes/1`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "customerId": 1,
  "petId": 1,
  "requestedCoverage": 30000.0
}
```

### 5.4 Underwriting Query Endpoints (`GET`)
* **Get Quote by ID**:
  `GET http://localhost:8080/api/underwriting/quotes/1`
* **Get All Quotes**:
  `GET http://localhost:8080/api/underwriting/quotes`
* **Get Detailed Risk Assessment Breakdown for Quote 1**:
  `GET http://localhost:8080/api/underwriting/assessments/quote/1`
* **Get All Rating Rules**:
  `GET http://localhost:8080/api/underwriting/rules`

---

## 6. Policy Issuance & Lifecycle Service (`PolicyService`)
> **Direct Port**: `8085` | **Gateway Route**: `/api/policies/**`

### 6.1 Issue Policy from Approved Quote (`POST`)
Fetches Quote 1 terms from `UnderWritingRiskService`, binds the policy, issues policy number, assigns `PET_CONTINUITY_LIFE` coverage, and logs `NONE -> PENDING_PAYMENT`.

* **Endpoint**: `POST http://localhost:8080/api/policies/from-quote/1`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**: `{}` *(Empty)*
* **Expected Response (`201 CREATED`)**:
```json
{
  "id": 1,
  "policyNumber": "POL-...",
  "quoteId": 1,
  "customerId": 1,
  "petId": 1,
  "coverageAmount": 25000.0,
  "premiumAmount": 75.0,
  "deductible": 250.0,
  "startDate": "2026-09-21",
  "endDate": "2027-09-21",
  "status": "PENDING_PAYMENT",
  "issuedBy": "SYSTEM"
}
```

### 6.2 Activate Insurance Policy (`POST`)
Transitions policy status from `PENDING_PAYMENT` to `ACTIVE` upon payment confirmation.

* **Endpoint**: `POST http://localhost:8080/api/policies/1/activate`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**: `{}` *(Empty)*
* **Expected Response (`200 OK`)**:
```json
{
  "id": 1,
  "policyNumber": "POL-...",
  "status": "ACTIVE",
  "coverageAmount": 25000.0,
  "premiumAmount": 75.0
}
```

### 6.3 Add Supplemental Coverage Line (`POST`)
* **Endpoint**: `POST http://localhost:8080/api/policies/1/coverages`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "coverageType": "EMERGENCY_MEDICAL",
  "coverageAmount": 5000.0,
  "limitAmount": 5000.0,
  "deductible": 100.0,
  "status": "ACTIVE"
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "coverageId": 2,
  "policyId": 1,
  "coverageType": "EMERGENCY_MEDICAL",
  "coverageAmount": 5000.0,
  "limitAmount": 5000.0,
  "deductible": 100.0,
  "status": "ACTIVE"
}
```

### 6.4 Policy Query Endpoints (`GET`)
* **Get Policy by ID**:
  `GET http://localhost:8080/api/policies/1`
* **Get Coverages for Policy 1**:
  `GET http://localhost:8080/api/policies/1/coverages`
* **Get Policy Status Audit History**:
  `GET http://localhost:8080/api/policies/1/history`
* **Get All Policies for Customer 1**:
  `GET http://localhost:8080/api/policies/customer/1`
* **Get All Policies in System (Admin)**:
  `GET http://localhost:8080/api/policies`

---

## 7. Payment & Fund Service (`PaymentFundService`)
> **Direct Port**: `8087` | **Gateway Route**: `/api/payments/**`

### 7.1 Process Policy Premium Payment
Paying the first monthly premium confirms coverage and automatically transitions the policy from `PENDING_PAYMENT` to `ACTIVE`.

* **Endpoint**: `POST http://localhost:8080/api/payments/premiums/pay`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "policyId": 2,
  "amount": 187.50,
  "paymentMethod": "CREDIT_CARD",
  "transactionReference": "TXN-PET-2026-001"
}
```
* **Expected Response (`200 OK`)**:
```json
{
  "paymentId": 1,
  "policyId": 2,
  "customerId": 1,
  "amount": 187.50,
  "status": "SUCCESS",
  "message": "Premium payment processed successfully and policy activated"
}
```

### 7.2 Query Payments & Policy Fund
* **Get Payments for Policy**: `GET http://localhost:8080/api/payments/premiums/policy/2`
* **Get Pet Continuity Fund for Policy**: `GET http://localhost:8080/api/payments/funds/by-policy/2`
* **Disburse Monthly Caretaker Allowance**: `POST http://localhost:8080/api/payments/funds/1/disburse-monthly?petId=1`

---

## 8. Claims & Adjudication Service (`ClaimsService`)
> **Direct Port**: `8086` | **Gateway Route**: `/api/claims/**`

### 8.1 File Continuity Claim
Claimant files a claim following the owner's death with civil death certificate details.

* **Endpoint**: `POST http://localhost:8080/api/claims`
* **Headers**: `Authorization: Bearer <TOKEN>`
* **Request Body**:
```json
{
  "policyId": 2,
  "claimantName": "Jane Doe",
  "relationship": "SPOUSE",
  "deathCertificateNo": "DC-998877",
  "dateOfDeath": "2026-09-20",
  "notes": "Filing pet continuity care claim following policyholder passing"
}
```
* **Expected Response (`201 CREATED`)**:
```json
{
  "id": 1,
  "claimNumber": "CLM-1790097087664",
  "policyId": 2,
  "claimantName": "Jane Doe",
  "relationship": "SPOUSE",
  "deathCertificateNo": "DC-998877",
  "status": "PENDING"
}
```

### 8.2 Investigate & Approve Claim (Claims Officer / Admin)
Evaluates death certificate authenticity, validates policy status, sets fraud score, and triggers the Pet Continuity Fund creation in `PaymentFundService`.

* **Endpoint**: `POST http://localhost:8080/api/claims/1/approve`
*(Or `POST http://localhost:8080/api/claims/1/investigate`)*
* **Headers**: `Authorization: Bearer <CLAIMS_OFFICER_OR_ADMIN_TOKEN>`
* **Expected Response (`200 OK`)**:
```json
{
  "id": 1,
  "claimNumber": "CLM-1790097087664",
  "policyId": 2,
  "claimantName": "Jane Doe",
  "relationship": "SPOUSE",
  "deathCertificateNo": "DC-998877",
  "status": "APPROVED",
  "investigationDecision": "APPROVED",
  "fraudScore": 10
}
```

---

## 9. Recommended End-to-End Test Execution Sequence

1. **Auth**: `POST /api/auth/register` (Customer) -> Copy token.
2. **Auth (Officer)**: `POST /api/auth/register` (Role `CLAIMS_OFFICER`) -> Copy officer token.
3. **Customer**: `POST /api/customers` -> Creates Customer #1.
4. **Address**: `POST /api/customers/addresses` -> Assigns address to Customer #1.
5. **Pet**: `POST /api/pets` -> Registers "Buddy" (Pet #1).
6. **Medical Record**: `POST /api/pets/1/medical-records` -> Adds baseline medical record.
7. **Caretakers**: `POST /api/care/caretakers` (twice) -> Registers Primary (#1) & Backup (#2) caretakers.
8. **Care Plan**: `POST /api/care/care-plans` -> Sets care plan ($300/mo allowance).
9. **Quote**: `POST /api/underwriting/quotes` -> Computes quote ($25,000 coverage, $187.50 premium).
10. **Policy**: `POST /api/policies/from-quote/3` -> Issues Policy #2 (`PENDING_PAYMENT`).
11. **Premium Payment**: `POST /api/payments/premiums/pay` -> Pays $187.50, activates Policy #2 (`ACTIVE`).
12. **File Claim**: `POST /api/claims` -> Files Claim #1 with death certificate (`PENDING`).
13. **Approve Claim**: `POST /api/claims/1/approve` -> Claim `APPROVED`, Policy `CLAIM_FILED`, $25,000 Fund allocated.
14. **Disbursement**: `POST /api/payments/funds/1/disburse-monthly?petId=1` -> Disburses $300 to caretaker.

