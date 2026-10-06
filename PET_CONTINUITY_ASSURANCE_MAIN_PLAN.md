# Pet Continuity Assurance System - Master System Architecture & Main Plan

> **Document Status**: Production Master Plan  
> **Source Reference**: `Pet_Continuity_Assurance_Main_Plan (2).xlsx`  
> **Backend Architecture**: Spring Boot 3 / Spring WebFlux (Reactive Non-Blocking I/O), R2DBC, Spring Cloud Gateway, Eureka Discovery, JWT Security  
> **Frontend Architecture**: Angular 21 Standalone Components, NgRx State Management, RxJS Reactive Streams, Tailwind CSS  

---

## 1. Executive System Overview

The **Pet Continuity Assurance System** is an enterprise-grade pet insurance and continuity care platform. Beyond standard pet health insurance, it provides **continuity care protection**: if a pet owner experiences a qualifying life event (hospitalization, severe incapacitation, or death), the system automatically provisions **Pet Care Funds**, verifies pet health and custody with assigned primary/backup caretakers, and disburses monthly care allowances and emergency veterinary expense reimbursements.

---

## 2. Microservice Architecture & DB Mappings

All incoming client traffic enters through the **Spring Cloud API Gateway (Port 8080)**, which evaluates JWT tokens, enforces CORS policies, handles circuit-breaker fallbacks (`/fallback/{service}`), and forwards requests to target reactive microservices registered with the **Netflix Eureka Service Discovery Server (Port 8761)**.

| Service Name | Port | Database | Service Type | Primary Tables / Schemas | Core System Responsibilities |
| :--- | :---: | :--- | :--- | :--- | :--- |
| **Eureka Server** | `8761` | *N/A* | Infrastructure | Service Registry | Microservice discovery & health registration |
| **API Gateway** | `8080` | *N/A* | Gateway | Dynamic Routes / Fallbacks | Central API routing, CORS management, JWT header propagation, circuit breaker fallbacks |
| **Auth Service** | `8081` | `auth_db` | Reactive WebFlux | `users`, `refresh_tokens` | JWT issuance, password hashing, user registration, authentication & token validation |
| **Customer Service** | `8082` | `customer_db` | Reactive R2DBC | `customers`, `addresses` | Customer profile management, KYC verification, address registration |
| **Pet Service** | `8083` | `pet_db` | Reactive R2DBC | `pets`, `pet_medical_records` | Pet profile registration, medical record tracking, species/breed categorization |
| **Underwriting & Risk Service** | `8084` | `underwriting_db` | Reactive R2DBC | `quotes`, `risk_assessments`, `coverage_assessments`, `rating_rules`, `premium_calculations` | Actuarial risk scoring, coverage gap analysis, dynamic rating rules engine, quote generation |
| **Policy Service** | `8085` | `policy_db` | Reactive R2DBC | `policies`, `coverages`, `policy_status_history` | Policy issuance, coverage limits, policy state lifecycle transitions |
| **Claims Service** | `8086` | `claims_db` | Reactive R2DBC | `claims`, `claim_documents`, `event_verifications`, `investigations`, `fraud_assessments`, `claim_status_history` | Qualifying event filing, evidence document management, fraud scoring, investigation workflows, approvals |
| **Payment & Fund Service** | `8087` | `payment_fund_db` | Reactive R2DBC | `premium_payments`, `pet_care_funds`, `fund_transactions`, `disbursements`, `expenses`, `fund_status_history` | Premium payment handling, Pet Care Fund creation, monthly caretaker disbursements, vet expense claims |
| **Care & Verification Service** | `8088` | `care_verification_db` | Reactive R2DBC | `caretakers`, `care_plans`, `pet_verifications`, `verification_history`, `backup_transfers` | Primary & backup caretaker assignments, care plan instructions, periodic pet custody verification checks |

---

## 3. JWT Security & Role Specification

Security is enforced at the API Gateway and inside microservice security filters (`SecurityUtil`).

### 3.1 Standard JWT Payload Structure
```json
{
  "role": "CUSTOMER",
  "userId": 6,
  "email": "testuser@example.com",
  "sub": "testuser@example.com",
  "iat": 1791293370,
  "exp": 1791379770
}
```

### 3.2 Supported Roles & User Rights
1. `ROLE_CUSTOMER`: Access to personal profile, registered pets, quote requests, policy view, premium payment, claim filing, caretaker nomination, and care plan creation.
2. `ROLE_UNDERWRITER`: Access to pending quotes, actuarial risk assessment calculations, rating rule configurations, and manual underwriting overrides.
3. `ROLE_CLAIMS_ADJUSTER`: Access to claims queue, document evidence review, qualifying event verification, fraud investigation, and approval/rejection decisioning.
4. `ROLE_CARETAKER`: Access to pet verification proof submission, care plan viewing, monthly benefit disbursement claims, emergency/vet expense reimbursements, and backup caretaker transfer requests.
5. `ROLE_ADMIN`: Full administrative control across all services, system monitoring, audit logs, user management, and operational reporting.

---

## 4. Master Database Schemas (DDL Specifications)

### 4.1 Auth Database (`auth_db`)
```sql
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

### 4.2 Customer Database (`customer_db`)
```sql
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    kyc_status VARCHAR(50) DEFAULT 'PENDING',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS addresses (
    address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'USA',
    is_primary BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);
```

### 4.3 Pet Database (`pet_db`)
```sql
CREATE TABLE IF NOT EXISTS pets (
    pet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    species_code VARCHAR(50) NOT NULL,
    breed_code VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    date_of_birth DATE,
    date_of_birth_estimated BOOLEAN DEFAULT FALSE,
    weight_value DOUBLE,
    weight_unit VARCHAR(10) DEFAULT 'KG',
    microchip_id VARCHAR(100),
    neutered_status BOOLEAN DEFAULT FALSE,
    annual_care_cost DOUBLE DEFAULT 1200.0,
    expected_remaining_years INT DEFAULT 10,
    currency VARCHAR(10) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_medical_records (
    medical_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    record_type VARCHAR(50) DEFAULT 'GENERAL',
    diagnosis VARCHAR(255),
    treatment TEXT,
    vet_name VARCHAR(150),
    record_date DATE,
    risk_level VARCHAR(50) DEFAULT 'LOW',
    annual_med_cost DOUBLE DEFAULT 0.0,
    notes TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pet_medical_records_pet FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
);
```

### 4.4 Underwriting Database (`underwriting_db`)
```sql
CREATE TABLE IF NOT EXISTS quotes (
    quote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_number VARCHAR(100) UNIQUE,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    requested_coverage DOUBLE NOT NULL,
    coverage_period VARCHAR(50) DEFAULT 'ANNUAL',
    premium_amount DOUBLE NOT NULL,
    risk_score INT NOT NULL,
    risk_class VARCHAR(50) DEFAULT 'MODERATE',
    decision VARCHAR(50) NOT NULL,
    decision_reason TEXT,
    currency VARCHAR(10) DEFAULT 'USD',
    input_snapshot JSON,
    status VARCHAR(50) DEFAULT 'OFFERED',
    underwriter_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    accepted_at TIMESTAMP NULL,
    decided_at TIMESTAMP NULL,
    valid_until TIMESTAMP NULL,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS risk_assessments (
    risk_assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    age_factor DOUBLE NOT NULL,
    breed_factor DOUBLE DEFAULT 0.0,
    medical_factor DOUBLE NOT NULL,
    care_cost_factor DOUBLE DEFAULT 0.0,
    continuity_factor DOUBLE DEFAULT 0.0,
    total_score DOUBLE NOT NULL,
    risk_class VARCHAR(50) NOT NULL,
    explanation TEXT,
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assessment_version VARCHAR(20) DEFAULT '1.0',
    model_version VARCHAR(50) DEFAULT 'STANDARD_ACTUARIAL_V1',
    rating_rule_set_version VARCHAR(50) DEFAULT 'V1',
    input_snapshot JSON,
    assessed_by VARCHAR(100) DEFAULT 'SYSTEM',
    status VARCHAR(50) DEFAULT 'COMPLETED',
    CONSTRAINT fk_risk_assessments_quote FOREIGN KEY (quote_id) REFERENCES quotes(quote_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS coverage_assessments (
    coverage_assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    annual_care_cost DOUBLE NOT NULL,
    remaining_years INT NOT NULL,
    medical_reserve DOUBLE DEFAULT 0.0,
    inflation_adjustment DOUBLE DEFAULT 1.05,
    projected_liability DOUBLE NOT NULL,
    requested_coverage DOUBLE NOT NULL,
    coverage_gap DOUBLE DEFAULT 0.0,
    recommendation TEXT,
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'CALCULATED',
    CONSTRAINT fk_coverage_assessments_quote FOREIGN KEY (quote_id) REFERENCES quotes(quote_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rating_rules (
    rule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_set_version VARCHAR(50) DEFAULT 'V1',
    rule_name VARCHAR(100) NOT NULL,
    factor VARCHAR(50) NOT NULL,
    min_value DOUBLE,
    max_value DOUBLE,
    score INT NOT NULL,
    premium_factor DOUBLE DEFAULT 1.0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    effective_from DATE,
    effective_to DATE
);

CREATE TABLE IF NOT EXISTS premium_calculations (
    premium_calculation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    risk_assessment_id BIGINT,
    base_premium DOUBLE NOT NULL,
    risk_multiplier DOUBLE DEFAULT 1.0,
    final_premium DOUBLE NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    rule_set_version VARCHAR(50) DEFAULT 'V1',
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_premium_calc_quote FOREIGN KEY (quote_id) REFERENCES quotes(quote_id) ON DELETE CASCADE
);
```

### 4.5 Policy Database (`policy_db`)
```sql
CREATE TABLE IF NOT EXISTS policies (
    policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(100) NOT NULL UNIQUE,
    quote_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    coverage_amount DOUBLE NOT NULL,
    premium_amount DOUBLE NOT NULL,
    deductible DOUBLE DEFAULT 250.0,
    currency VARCHAR(10) DEFAULT 'USD',
    start_date VARCHAR(50),
    end_date VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING_PAYMENT',
    issued_by VARCHAR(100) DEFAULT 'SYSTEM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS coverages (
    coverage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    coverage_type VARCHAR(50) NOT NULL,
    coverage_amount DOUBLE NOT NULL,
    limit_amount DOUBLE NOT NULL,
    deductible DOUBLE DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    CONSTRAINT fk_coverages_policy FOREIGN KEY (policy_id) REFERENCES policies(policy_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS policy_status_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(100) DEFAULT 'SYSTEM',
    reason VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_policy_status_history_policy FOREIGN KEY (policy_id) REFERENCES policies(policy_id) ON DELETE CASCADE
);
```

### 4.6 Claims Database (`claims_db`)
```sql
CREATE TABLE IF NOT EXISTS claims (
    claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_number VARCHAR(100) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    claim_type VARCHAR(50) DEFAULT 'QUALIFYING_EVENT',
    currency VARCHAR(10) DEFAULT 'USD',
    requested_amount DOUBLE NOT NULL,
    approved_amount DOUBLE DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'FILED',
    decision_reason TEXT,
    event_date DATE,
    description TEXT,
    filed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS claim_documents (
    document_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_name VARCHAR(150),
    file_path VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_documents_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_verifications (
    verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    verification_method VARCHAR(100),
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    verified_by VARCHAR(100),
    verification_date TIMESTAMP NULL,
    remarks TEXT,
    CONSTRAINT fk_event_verifications_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS investigations (
    investigation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    investigator_id BIGINT,
    findings TEXT,
    eligibility_status VARCHAR(50) DEFAULT 'UNDER_REVIEW',
    fraud_indicator_count INT DEFAULT 0,
    recommendation VARCHAR(50),
    status VARCHAR(50) DEFAULT 'IN_PROGRESS',
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_investigations_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fraud_assessments (
    fraud_assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    score INT NOT NULL,
    indicators TEXT,
    decision VARCHAR(50) DEFAULT 'CLEARED',
    assessed_by VARCHAR(100) DEFAULT 'SYSTEM',
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    CONSTRAINT fk_fraud_assessments_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS claim_status_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(100),
    reason VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_status_history_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);
```

### 4.7 Payment & Fund Database (`payment_fund_db`)
```sql
CREATE TABLE IF NOT EXISTS premium_payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    payment_reference VARCHAR(100) UNIQUE,
    payment_method VARCHAR(50) DEFAULT 'CREDIT_CARD',
    status VARCHAR(50) DEFAULT 'COMPLETED',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    failure_reason VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS pet_care_funds (
    fund_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL UNIQUE,
    pet_id BIGINT NOT NULL,
    customer_id BIGINT,
    caretaker_id BIGINT,
    total_amount DOUBLE NOT NULL,
    available_amount DOUBLE NOT NULL,
    monthly_allowance DOUBLE DEFAULT 0.0,
    veterinary_reserve DOUBLE DEFAULT 0.0,
    emergency_reserve DOUBLE DEFAULT 0.0,
    currency VARCHAR(10) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fund_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    balance_after DOUBLE NOT NULL,
    reference_id VARCHAR(100),
    description VARCHAR(255),
    status VARCHAR(50) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS disbursements (
    disbursement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_id BIGINT NOT NULL,
    caretaker_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    disbursement_type VARCHAR(50) DEFAULT 'MONTHLY_ALLOWANCE',
    eligibility_status VARCHAR(50) DEFAULT 'ELIGIBLE',
    benefit_month VARCHAR(20),
    status VARCHAR(50) DEFAULT 'PROCESSED',
    scheduled_date DATE,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS expenses (
    expense_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_id BIGINT NOT NULL,
    caretaker_id BIGINT,
    expense_type VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    vendor_name VARCHAR(150),
    document_reference VARCHAR(255),
    approval_status VARCHAR(50) DEFAULT 'APPROVED',
    decided_by VARCHAR(100),
    decided_at TIMESTAMP NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP NULL,
    expense_date DATE
);

CREATE TABLE IF NOT EXISTS fund_status_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(100),
    reason VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.8 Care & Verification Database (`care_verification_db`)
```sql
CREATE TABLE IF NOT EXISTS caretakers (
    caretaker_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    user_id BIGINT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(100),
    relationship VARCHAR(50),
    address VARCHAR(255),
    priority VARCHAR(20) DEFAULT 'PRIMARY',
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    availability_status VARCHAR(50) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS care_plans (
    care_plan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL UNIQUE,
    feeding_instructions TEXT,
    medication_instructions TEXT,
    vet_details TEXT,
    routine_details TEXT,
    special_requirements TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_verifications (
    pet_verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    caretaker_id BIGINT NOT NULL,
    verification_method VARCHAR(50) DEFAULT 'PHOTO_PROOFS',
    verification_status VARCHAR(50) DEFAULT 'VERIFIED',
    evidence_reference VARCHAR(255),
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_verification_date DATE,
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS verification_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    caretaker_id BIGINT,
    verification_type VARCHAR(50),
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    verification_method VARCHAR(50),
    verified_by VARCHAR(100),
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS backup_transfers (
    transfer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    primary_caretaker_id BIGINT NOT NULL,
    backup_caretaker_id BIGINT NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    transferred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 5. End-to-End Persona Workflow Journeys

### 5.1 Overall System Lifecycle Journey

| Stage | Persona | Action | Goal | Angular Frontend Page | API / Endpoint | Target Microservice | Database Affected |
| :---: | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | Customer | User Registration | Create customer account | Register Component | `POST /api/auth/register` | Auth Service | `auth_db.users` |
| **2** | Customer | User Login | Authenticate & acquire JWT | Login Component | `POST /api/auth/login` | Auth Service | `auth_db.users` |
| **3** | Customer | Create Customer Profile | Save personal details | Profile Component | `POST /api/customers` | Customer Service | `customer_db.customers` |
| **4** | Customer | Add Address | Register physical address | Address Component | `POST /api/customers/{id}/addresses` | Customer Service | `customer_db.addresses` |
| **5** | Customer | Register Pet | Add pet profile | Pet Register Component | `POST /api/pets` | Pet Service | `pet_db.pets` |
| **6** | Customer | Add Medical Record | Document medical history | Medical History | `POST /api/pets/{id}/medical-records` | Pet Service | `pet_db.pet_medical_records` |
| **7** | Customer | Assign Primary Caretaker | Nominate primary caretaker | Caretaker Component | `POST /api/care/caretakers` | Care Verification Service | `care_verification_db.caretakers` |
| **8** | Customer | Assign Backup Caretaker | Nominate backup caretaker | Caretaker Component | `POST /api/care/caretakers` | Care Verification Service | `care_verification_db.caretakers` |
| **9** | Customer | Create Pet Care Plan | Set diet, vet & routine instructions | Care Plan Component | `POST /api/care/care-plans` | Care Verification Service | `care_verification_db.care_plans` |
| **10** | Customer | Request Quote | Generate continuity quote | Quote Request Component | `POST /api/underwriting/quotes` | Underwriting Risk Service | `underwriting_db.quotes` |
| **11** | System / UW | Evaluate Risk & Premium | Calculate actuarial risk score | Quote Review Component | `GET /api/underwriting/assessments/quote/{quoteId}` | Underwriting Risk Service | `underwriting_db.risk_assessments` |
| **12** | Customer | Accept Quote | Accept offered premium | Quote Review Component | `PUT /api/underwriting/quotes/{id}` | Underwriting Risk Service | `underwriting_db.quotes` |
| **13** | Customer | Issue Policy | Convert quote to policy | Policy Issue Component | `POST /api/policies/from-quote/{quoteId}` | Policy Service | `policy_db.policies` |
| **14** | Customer | Pay Premium | Complete dummy payment | Payment Component | `POST /api/payments/premium` | Payment Fund Service | `payment_fund_db.premium_payments` |
| **15** | System | Activate Policy | Transition policy to ACTIVE | Policy View Component | `POST /api/policies/{id}/activate` | Policy Service | `policy_db.policies` |
| **16** | Customer | File Qualifying Event Claim | Report owner incapacitation | Claim File Component | `POST /api/claims` | Claims Service | `claims_db.claims` |
| **17** | Customer | Upload Claim Evidence | Attach death/medical proof | Claim File Component | `POST /api/claims/{id}/documents` | Claims Service | `claims_db.claim_documents` |
| **18** | Claims Officer | Verify Qualifying Event | Validate proof of event | Event Verification Component | `POST /api/claims/{id}/verify-death` | Claims Service | `claims_db.event_verifications` |
| **19** | Claims Officer | Investigate Claim | Conduct eligibility review | Investigation Component | `POST /api/claims/{id}/investigate` | Claims Service | `claims_db.investigations` |
| **20** | Claims Officer | Approve Claim | Grant continuity benefit | Claim Decision Component | `POST /api/claims/{id}/approve` | Claims Service | `claims_db.claims` |
| **21** | System | Create Continuity Fund | Setup pet care fund reserve | Fund Dashboard | `POST /api/payments/funds` | Payment Fund Service | `payment_fund_db.pet_care_funds` |
| **22** | Caretaker | Verify Caretaker & Pet | Confirm pet is in caretaker custody | Verification Component | `POST /api/care/verify-pet` | Care Verification Service | `care_verification_db.pet_verifications` |
| **23** | Caretaker | Receive Monthly Benefit | Disburse monthly care allowance | Disbursement View | `POST /api/payments/funds/{id}/disburse` | Payment Fund Service | `payment_fund_db.disbursements` |
| **24** | Caretaker | Submit Vet Expense | Reimbursed vet expense claim | Expense Component | `POST /api/payments/funds/{id}/expense` | Payment Fund Service | `payment_fund_db.expenses` |
| **25** | Caretaker | Transfer to Backup Caretaker | Trigger backup care transfer | Caretaker Component | `POST /api/care/backup-transfer/{petId}` | Care Verification Service | `care_verification_db.backup_transfers` |

---

### 5.2 Customer Journey

| Action | Goal | Angular Page | API Endpoint | Microservice | Database Affected |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Customer Register** | Create account | `/auth/register` | `POST /api/auth/register` | Auth Service | `auth_db.users` |
| **Customer Login** | Acquire JWT | `/auth/login` | `POST /api/auth/login` | Auth Service | `auth_db.users` |
| **View Profile** | Review customer info | `/customer/profile` | `GET /api/customers/user/{userId}` | Customer Service | `customer_db.customers` |
| **Update Address** | Save primary address | `/customer/profile` | `POST /api/customers/{id}/addresses` | Customer Service | `customer_db.addresses` |
| **Register Pet** | Add new pet | `/pets/register` | `POST /api/pets` | Pet Service | `pet_db.pets` |
| **View Pet List** | Manage owned pets | `/pets/list` | `GET /api/pets/customer/{customerId}` | Pet Service | `pet_db.pets` |
| **Add Medical History** | Record vaccinations/treatments | `/pets/:id/medical` | `POST /api/pets/{id}/medical-records` | Pet Service | `pet_db.pet_medical_records` |
| **Add Primary Caretaker** | Designate main caretaker | `/care/caretakers` | `POST /api/care/caretakers` | Care Verification Service | `care_verification_db.caretakers` |
| **Create Care Plan** | Specify care routine | `/care/plans` | `POST /api/care/care-plans` | Care Verification Service | `care_verification_db.care_plans` |
| **Request Quote** | Calculate premium | `/quotes/request` | `POST /api/underwriting/quotes` | Underwriting & Risk Service | `underwriting_db.quotes` |
| **Issue Policy** | Convert quote | `/policies/issue` | `POST /api/policies/from-quote/{quoteId}` | Policy Service | `policy_db.policies` |
| **Make Payment** | Pay premium | `/payments/checkout` | `POST /api/payments/premium` | Payment & Fund Service | `payment_fund_db.premium_payments` |
| **File Claim** | Report qualifying event | `/claims/file` | `POST /api/claims` | Claims Service | `claims_db.claims` |

---

### 5.3 Underwriter Journey

| Action | Goal | Angular Page | API Endpoint | Microservice | Database Affected |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **View Pending Quotes** | Review requested quotes | `/underwriting/quotes` | `GET /api/underwriting/quotes` | Underwriting & Risk Service | `underwriting_db.quotes` |
| **Inspect Risk Assessment** | Actuarial risk analysis | `/underwriting/quotes/:id` | `GET /api/underwriting/assessments/quote/{quoteId}` | Underwriting & Risk Service | `underwriting_db.risk_assessments` |
| **Review Rating Rules** | Inspect base rating rules | `/underwriting/rules` | `GET /api/underwriting/rules` | Underwriting & Risk Service | `underwriting_db.rating_rules` |
| **Add/Modify Rating Rule** | Update actuarial weights | `/underwriting/rules` | `POST /api/underwriting/rules` | Underwriting & Risk Service | `underwriting_db.rating_rules` |
| **Reassess Pet Risk** | Re-run risk calculations | `/underwriting/reassess` | `POST /api/underwriting/assessments/reassess/{petId}` | Underwriting & Risk Service | `underwriting_db.risk_assessments` |
| **Risk Monitoring** | Track pet coverage gaps | `/underwriting/monitoring/:petId` | `GET /api/underwriting/risk-monitoring/{petId}` | Underwriting & Risk Service | `underwriting_db.coverage_assessments` |

---

### 5.4 Claims Officer Journey

| Action | Goal | Angular Page | API Endpoint | Microservice | Database Affected |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **View Claims Queue** | List filed claims | `/claims/adjuster` | `GET /api/claims` | Claims Service | `claims_db.claims` |
| **View Claim Details** | Review event evidence | `/claims/adjuster/:id` | `GET /api/claims/{id}` | Claims Service | `claims_db.claims` |
| **Verify Qualifying Event** | Confirm owner event | `/claims/adjuster/:id` | `POST /api/claims/{id}/verify-death` | Claims Service | `claims_db.event_verifications` |
| **Conduct Investigation** | Record investigation findings | `/claims/adjuster/:id` | `POST /api/claims/{id}/investigate` | Claims Service | `claims_db.investigations` |
| **Approve Claim** | Authorize continuity fund | `/claims/adjuster/:id` | `POST /api/claims/{id}/approve` | Claims Service | `claims_db.claims` |
| **Reject Claim** | Deny invalid claim | `/claims/adjuster/:id` | `POST /api/claims/{id}/reject` | Claims Service | `claims_db.claims` |

---

### 5.5 Caretaker Journey

| Action | Goal | Angular Page | API Endpoint | Microservice | Database Affected |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **View Caretaker Assignment** | Inspect pet assignment | `/caretaker/dashboard` | `GET /api/care/caretakers/pet/{petId}` | Care Verification Service | `care_verification_db.caretakers` |
| **Submit Pet Verification** | Verify pet health & custody | `/caretaker/verify` | `POST /api/care/verify-pet` | Care Verification Service | `care_verification_db.pet_verifications` |
| **Check Eligibility** | Confirm disbursement readiness | `/caretaker/eligibility` | `GET /api/care/eligibility/check` | Care Verification Service | `care_verification_db.pet_verifications` |
| **View Pet Care Fund** | View fund balance | `/caretaker/fund` | `GET /api/payments/funds/by-policy/{policyId}` | Payment & Fund Service | `payment_fund_db.pet_care_funds` |
| **Disburse Monthly Allowance** | Claim monthly care benefit | `/caretaker/fund` | `POST /api/payments/funds/{id}/disburse` | Payment & Fund Service | `payment_fund_db.disbursements` |
| **Submit Vet Expense** | File medical reimbursement | `/caretaker/expense` | `POST /api/payments/funds/{id}/expense` | Payment & Fund Service | `payment_fund_db.expenses` |
| **Transfer to Backup Caretaker** | Delegate care to backup | `/caretaker/transfer` | `POST /api/care/backup-transfer/{petId}` | Care Verification Service | `care_verification_db.backup_transfers` |

---

### 5.6 Payment & Continuity Fund Management Journey

| Action | Goal | Angular Page | API Endpoint | Microservice | Database Affected |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Process Premium Payment** | Charge premium fee | `/payments/checkout` | `POST /api/payments/premium` | Payment & Fund Service | `payment_fund_db.premium_payments` |
| **Initialize Continuity Fund** | Setup pet fund upon claim approval | Automatic / Admin | `POST /api/payments/funds` | Payment & Fund Service | `payment_fund_db.pet_care_funds` |
| **Fetch Fund Details** | Monitor fund balance & reserves | `/funds/:id` | `GET /api/payments/funds/{id}` | Payment & Fund Service | `payment_fund_db.pet_care_funds` |
| **Record Monthly Disbursement** | Execute monthly payment | `/funds/:id/disburse` | `POST /api/payments/funds/{id}/disburse` | Payment & Fund Service | `payment_fund_db.disbursements` |
| **Record Emergency Expense** | Deduct emergency vet expenses | `/funds/:id/expense` | `POST /api/payments/funds/{id}/expense` | Payment & Fund Service | `payment_fund_db.expenses` |
| **Update Fund Status** | Suspend or reactivate fund | `/funds/:id/status` | `PUT /api/payments/funds/{id}/status` | Payment & Fund Service | `payment_fund_db.fund_status_history` |

---

### 5.7 System Administrator Journey

| Action | Goal | Angular Page | API Endpoint | Microservice | Database Affected |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **System Health & Dashboard** | Monitor operational metrics | `/admin/dashboard` | `GET /actuator/health` | API Gateway | *All Microservices* |
| **Manage Users & Roles** | Audit platform access | `/admin/users` | `GET /api/auth/users` | Auth Service | `auth_db.users` |
| **Audit All Policies** | Monitor active continuity policies | `/admin/policies` | `GET /api/policies` | Policy Service | `policy_db.policies` |
| **Audit All Continuity Funds** | Review platform fund reserves | `/admin/funds` | `GET /api/payments/funds` | Payment & Fund Service | `payment_fund_db.pet_care_funds` |

---

## 6. Requirements Traceability Matrix (RTM)

| Req ID | Requirement Description | Target Microservice | Target Frontend Component | Target Database Table | API Route / Implementation | Status |
| :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **FR-01** | Customer registration & BCrypt password hashing | Auth Service | Register Component | `auth_db.users` | `POST /api/auth/register` | `COMPLETED` |
| **FR-02** | JWT login authentication & token issuance | Auth Service + Gateway | Login Component | `auth_db.users` | `POST /api/auth/login` | `COMPLETED` |
| **FR-03** | Role-based authorization & route guards | Gateway + Microservices | Angular AuthGuard / RoleGuard | *N/A* | `SecurityUtil` + Reactive Filters | `COMPLETED` |
| **FR-04** | Customer profile & physical address management | Customer Service | Profile Component | `customer_db.customers` & `addresses` | `POST /api/customers`, `POST /{id}/addresses` | `COMPLETED` |
| **FR-05** | Pet registration and management | Pet Service | Pet Register Component | `pet_db.pets` | `POST /api/pets`, `GET /api/pets` | `COMPLETED` |
| **FR-06** | Pet medical records history tracking | Pet Service | Medical History Component | `pet_db.pet_medical_records` | `POST /api/pets/{id}/medical-records` | `COMPLETED` |
| **FR-07** | Primary caretaker assignment | Care & Verification Service | Caretaker Component | `care_verification_db.caretakers` | `POST /api/care/caretakers` | `COMPLETED` |
| **FR-08** | Backup caretaker assignment | Care & Verification Service | Caretaker Component | `care_verification_db.caretakers` | `POST /api/care/caretakers` | `COMPLETED` |
| **FR-09** | Pet care plan (diet, routine, vet details) | Care & Verification Service | Care Plan Component | `care_verification_db.care_plans` | `POST /api/care/care-plans` | `COMPLETED` |
| **FR-10** | Actuarial quote generation | Underwriting & Risk Service | Quote Request Component | `underwriting_db.quotes` | `POST /api/underwriting/quotes` | `COMPLETED` |
| **FR-11** | Actuarial risk scoring engine | Underwriting & Risk Service | Quote Review Component | `underwriting_db.risk_assessments` | `GET /api/underwriting/assessments/quote/{id}` | `COMPLETED` |
| **FR-12** | Premium calculation via rating rules | Underwriting & Risk Service | Underwriting Admin | `underwriting_db.premium_calculations` | Internal Rating Engine | `COMPLETED` |
| **FR-13** | Coverage assessment & liability projection | Underwriting & Risk Service | Underwriting Admin | `underwriting_db.coverage_assessments` | Actuarial Engine | `COMPLETED` |
| **FR-14** | Coverage-gap monitoring per pet | Underwriting & Risk Service | Customer Dashboard | `underwriting_db.coverage_assessments` | `GET /api/underwriting/risk-monitoring/{petId}` | `COMPLETED` |
| **FR-15** | Quote approval / rejection management | Underwriting & Risk Service | Underwriting Admin | `underwriting_db.quotes` | `PUT /api/underwriting/quotes/{id}` | `COMPLETED` |
| **FR-16** | Policy generation from accepted quotes | Policy Service | Policy Issue Component | `policy_db.policies` | `POST /api/policies/from-quote/{quoteId}` | `COMPLETED` |
| **FR-17** | Dummy premium payment processing | Payment & Fund Service | Payment Component | `payment_fund_db.premium_payments` | `POST /api/payments/premium` | `COMPLETED` |
| **FR-18** | Policy activation upon payment validation | Policy Service | Policy Detail View | `policy_db.policies` | `POST /api/policies/{id}/activate` | `COMPLETED` |
| **FR-19** | Policy lifecycle & status history tracking | Policy Service | Customer / Admin View | `policy_db.policy_status_history` | `GET /api/policies/{id}/history` | `COMPLETED` |
| **FR-20** | Qualifying event claim filing | Claims Service | Claim File Component | `claims_db.claims` | `POST /api/claims` | `COMPLETED` |
| **FR-21** | Claim evidence upload & document storage | Claims Service | Claim Document View | `claims_db.claim_documents` | `POST /api/claims/{id}/documents` | `COMPLETED` |
| **FR-22** | Policy/event verification REST cross-call | Claims + Policy Service | Claims Adjuster View | `claims_db.event_verifications` | `POST /api/claims/{id}/verify-death` | `COMPLETED` |
| **FR-23** | Claim investigation workflow | Claims Service | Claims Adjuster View | `claims_db.investigations` | `POST /api/claims/{id}/investigate` | `COMPLETED` |
| **FR-24** | Fraud risk assessment scoring | Claims Service | Claims Adjuster View | `claims_db.fraud_assessments` | Internal Fraud Analyzer | `COMPLETED` |
| **FR-25** | Claim approval / rejection decisioning | Claims Service | Claims Adjuster View | `claims_db.claims` & `status_history` | `POST /api/claims/{id}/approve` | `COMPLETED` |
| **FR-26** | Pet Care Continuity Fund creation | Payment & Fund Service | Fund Management View | `payment_fund_db.pet_care_funds` | `POST /api/payments/funds` | `COMPLETED` |
| **FR-27** | Caretaker verification status update | Care & Verification Service | Caretaker Dashboard | `care_verification_db.caretakers` | `POST /api/care/caretakers/{id}/verifications` | `COMPLETED` |
| **FR-28** | Pet verification & custody proof submission | Care & Verification Service | Caretaker Dashboard | `care_verification_db.pet_verifications` | `POST /api/care/verify-pet` | `COMPLETED` |
| **FR-29** | Monthly benefit eligibility verification | Care & Verification Service | Caretaker Dashboard | `care_verification_db.pet_verifications` | `GET /api/care/eligibility/check` | `COMPLETED` |
| **FR-30** | Monthly benefit disbursement processing | Payment & Fund Service | Caretaker Dashboard | `payment_fund_db.disbursements` | `POST /api/payments/funds/{id}/disburse` | `COMPLETED` |
| **FR-31** | Veterinary & emergency expense claims | Payment & Fund Service | Caretaker Dashboard | `payment_fund_db.expenses` | `POST /api/payments/funds/{id}/expense` | `COMPLETED` |
| **FR-32** | Continuity fund status update / suspension | Payment & Fund Service | Admin / Caretaker | `payment_fund_db.fund_status_history` | `PUT /api/payments/funds/{id}/status` | `COMPLETED` |
| **FR-33** | Backup caretaker transfer workflow | Care & Verification Service | Caretaker Dashboard | `care_verification_db.backup_transfers` | `POST /api/care/backup-transfer/{petId}` | `COMPLETED` |
| **FR-34** | Admin operational cross-service dashboard | API Gateway + Microservices | Admin Dashboard | Multiple DBs | Aggregated WebFlux APIs | `COMPLETED` |
| **NFR-01** | Netflix Eureka Service Discovery | Eureka Server | Infrastructure | Service Registry | Port `8761` Registration | `COMPLETED` |
| **NFR-02** | Spring Cloud API Gateway Security Boundary | API Gateway | Infrastructure | *N/A* | Port `8080` Routing & Circuit Breaking | `COMPLETED` |
| **NFR-03** | End-to-end Reactive Non-blocking I/O | All Microservices | Backend Engine | R2DBC Reactive DB | Spring WebFlux + Mono/Flux | `COMPLETED` |
| **NFR-04** | Lightweight REST/WebClient Inter-service IPC | All Microservices | Inter-service IPC | *N/A* | Non-blocking WebClient Calls | `COMPLETED` |
| **NFR-05** | Production Spring Boot Actuator Monitoring | All Microservices | Admin Panel | *N/A* | `/actuator/health` | `COMPLETED` |
| **NFR-06** | Interactive OpenAPI / Swagger Documentation | All Microservices | Developer Portal | *N/A* | `springdoc-openapi-starter-webflux-ui` | `COMPLETED` |
| **NFR-07** | Angular 21 Component Architecture | `pet-frontend` | Frontend SPA | *N/A* | Standalone Components & Directives | `COMPLETED` |
| **NFR-08** | NgRx Centralized State Management | `pet-frontend` | Frontend SPA | Local Storage / Memory | Store, Actions, Reducers, Effects | `COMPLETED` |
| **NFR-09** | Reactive RxJS Asynchronous Streams | `pet-frontend` | Frontend SPA | Memory | Async Pipe, Observables, Subjects | `COMPLETED` |

---

## 7. Operational Summary & Verification

1. **Service Registration**: All 10 microservices register with Eureka (`8761`) and route seamlessly through `ApiGateway` (`8080`).
2. **Database Isolation**: All 8 domain microservices manage dedicated R2DBC schemas with foreign key integrity.
3. **End-to-End Journey Coverage**: Complete workflow mapping for Customer, Underwriter, Claims Adjuster, Caretaker, and Admin roles.
