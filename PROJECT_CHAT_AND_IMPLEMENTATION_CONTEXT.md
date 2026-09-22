# Pet Continuity Insurance - Project Chat, Architecture & Implementation Context

This document captures the **entire context, history, discussions, issues solved, architectural decisions, and end-to-end verification journey** for the Pet Continuity Insurance microservices platform.

---

## 1. Project Background & Objective

### 1.1 What is Pet Continuity Insurance?
Unlike traditional pet medical insurance (which reimburses routine vet bills while the owner is alive), **Pet Continuity Insurance** guarantees the **lifetime care, housing, nutrition, and medical welfare of a pet if the owner passes away or becomes incapacitated**.

When an owner passes away:
1. A designated claimant/caretaker files a death claim with a civil death certificate.
2. The claim is adjudicated and verified against fraud indicators.
3. Upon approval, the policy transitions to `CLAIM_FILED`.
4. The policy's contracted coverage amount (e.g., **$25,000.00**) is transferred into an active **Pet Continuity Care Fund**.
5. The fund automatically disburses:
   - **Monthly Caretaker Stipends** (e.g., $300.00/month) for food and daily care.
   - **Medical Reserves** (e.g., $4,000.00) for veterinary emergencies.
   - **Emergency Buffers** (e.g., $2,000.00) for caretaker relocation or critical needs.

---

## 2. Technical Stack & Microservices Architecture

* **Framework**: Spring Boot 3.x (Reactive WebFlux with Netty)
* **Database**: MySQL 8.x with Spring Data R2DBC (non-blocking reactive SQL)
* **Service Discovery**: Netflix Eureka Server (`8761`)
* **Routing & Security**: Spring Cloud Gateway (`8080`) + JWT Authentication filter
* **Inter-Service Communication**: Reactive `WebClient` with Resilience4j Circuit Breakers (No external message brokers like Kafka required)

### Microservices Directory & Port Allocation

| Service | Port | Database | Primary Responsibility |
| :--- | :--- | :--- | :--- |
| **`EurekaServer`** | `8761` | — | Service discovery and registry heartbeat |
| **`ApiGateway`** | `8080` | — | Global routing, JWT validation, rate limiting, CORS |
| **`AuthService`** | `8081` | `auth_db` | User registration, login, JWT issuance, roles |
| **`CustomerService`** | `8082` | `customer_db` | Customer profile management and physical addresses |
| **`PetService`** | `8083` | `pet_db` | Pet biological profiles and vet medical history |
| **`CareVerificationService`** | `8088` | `care_db` | Primary/backup caretakers, care plans, welfare checks |
| **`UnderWritingRiskService`** | `8084` | `underwriting_db` | Actuarial risk assessment, premium quotes |
| **`PolicyService`** | `8085` | `policy_db` | Policy issuance, lifecycle status, coverage lines |
| **`PaymentFundService`** | `8087` | `payment_fund_db` | Premium payments, Pet Continuity Funds, disbursements |
| **`ClaimsService`** | `8086` | `claims_db` | Claim filing, death verification, fraud scoring, payout triggers |

---

## 3. Chronological Discussion History & Issues Resolved

### Milestone 1: AuthService, CustomerService, and PetService Setup
* **Discussions & Actions**:
  - Registered test customer (`john.doe@example.com` / `Password123!`) with role `CUSTOMER`.
  - Registered claims officer account with role `CLAIMS_OFFICER` and `ADMIN`.
  - Created Customer #1 (`John Doe`) and associated primary address (`742 Evergreen Terrace`).
  - Registered Pet #1 ("Buddy", 4-year-old Male Golden Retriever).
  - Added baseline medical record for Buddy (Mild Hip Dysplasia, $150.00 checkup).

---

### Milestone 2: CareVerificationService & The Care Plan Field Mapping
* **Issue Discovered**:
  - When creating a Care Plan for Pet #1, `primaryCaretakerId` and `backupCaretakerId` were annotated with `@Transient` in `CarePlan.java`, preventing them from being stored into MySQL.
* **Resolution**:
  - Removed `@Transient` and properly mapped them with `@Column("primary_caretaker_id")` and `@Column("backup_caretaker_id")`.
  - Successfully registered Primary Caretaker #1 (Robert Smith) and Backup Caretaker #2 (Emily Smith).
  - Successfully created Care Plan with daily instructions and a $300.00 monthly stipend.

---

### Milestone 3: UnderWritingRiskService Actuarial Calculations & Quotes
* **Issue Discovered**:
  - `GET /quotes/customer/{customerId}` and `GET /quotes/pet/{petId}` queries failed due to reactive flux mapping and Spring Data R2DBC derived queries.
* **Resolution**:
  - Added explicit `@Query` annotations in `QuoteRepository.java`.
  - In `UnderwritingHandler.java`, wrapped fluxes using `.collectList().flatMap(quotes -> ServerResponse.ok().bodyValue(quotes))`.
  - Generated Quote #3 for Pet #1:
    - Coverage Tier: `PREMIUM`
    - Base Coverage: **$25,000.00**
    - Monthly Premium: **$187.50** (Annual: $2,250.00)
    - Actuarial Risk Score: **34.5**
    - Valid for 30 days.

---

### Milestone 4: PolicyService Issuance & Payment Pending
* **Actions & Clarifications**:
  - Issued Policy #2 (`POL-1790093637581-1`) from Quote #3 via `POST /api/policies/from-quote/3`.
  - Policy initial status: **`PENDING_PAYMENT`**.
  - Discussed customer policy view: Confirmed that customers can view active policies and due amounts through `GET /api/policies/customer/1`.

---

### Milestone 5: PaymentFundService & Customer ID Propagation
* **Issue Discovered**:
  - When processing premium payments via `PaymentFundService`, `customerId` was recorded as `null` in `payments` because the payment request only supplied `policyId`.
* **Resolution**:
  - Updated `PaymentFundService.processPremiumPayment` to call `PolicyService` over `WebClient` (`GET /api/policies/{id}` with header `X-User-Role: INTERNAL_SERVICE`).
  - Automatically populated `customerId = 1` from the policy record.
  - Dropped and recreated `payment_fund_db` tables cleanly.
  - Successfully processed $187.50 premium payment for Policy #2.
  - Policy #2 automatically updated to status **`ACTIVE`**.

---

### Milestone 6: ClaimsService Troubleshooting & The Fraud Score Deep Dive

#### 1. 503 Circuit Breaker Fallback Error
* **Symptom**:
  ```json
  {"timestamp":"2026-09-22T22:27:26.335462","error":"Service Unavailable","message":"The ClaimsService service is temporarily unavailable or experiencing high latency. Circuit breaker is active.","fallback":true,"status":503}
  ```
* **Cause**: Immediately after restarting `ClaimsService` in IntelliJ, Eureka takes 15–30 seconds to propagate instance availability to the API Gateway. Making requests instantly tripped the Gateway circuit breaker.
* **Solution**: Advised waiting ~20 seconds after IDE restart for Eureka heartbeat synchronization.

#### 2. 400 Bad Request: Missing Required Fields
* **Symptom**: `{"error":"policyId, claimantName, and deathCertificateNo are required"}`
* **Cause**: The claim filing payload omitted mandatory validation fields.
* **Solution**: Provided the complete filing payload with `policyId`, `claimantName`, and `deathCertificateNo`.

#### 3. 400 Bad Request: Missing `claim_number` Default Value
* **Symptom**:
  ```json
  {"error":"executeMany; SQL [INSERT INTO `claims` (`policy_id`, `claim_reason`, ...)]; Field 'claim_number' doesn't have a default value"}
  ```
* **Cause**: In `Claim.java`, `claimNumber` was annotated with `@Transient`, so R2DBC skipped it in the `INSERT` statement, while the MySQL column `claim_number VARCHAR(100) NOT NULL UNIQUE` rejected the null value.
* **Solution**: Changed `@Transient` to `@Column("claim_number")` in `Claim.java`.

#### 4. The 65 Fraud Score & `MANUAL_REVIEW` Investigation
* **User Question**:
  > *"status went to manual review as the score is 65 ig"*  
  > *"this was the response can u check it once why it went to manual review"*  
  > *"no but mine has fraud score 65 right it does not give any claim or what ?? let meknow first dont make any changes in code fdireclty"*  
  > *"but i am not storing the death certificate right then how can we show ??"*  
  > *"yeah its ok but thefraud score is 65 right what will be the amount we are going to give"*  
  > *"but based on risk score or fraud score we will release the fund right we wont release 25000 dollars for every customer"*

* **Detailed Clarification & Analysis**:
  1. **Why the Score was 65**:
     - In `claims_db`, the `claims` table does not have a separate column named `death_certificate_no` (it was `@Transient` on the Java model).
     - When `investigateClaim(1)` re-queried the database, `claim.getDeathCertificateNo()` loaded as `null`.
     - The fraud rule checked:
       ```java
       int fraudScore = (claim.getDeathCertificateNo() == null || claim.getDeathCertificateNo().length() < 5) ? 65 : 10;
       ```
     - Finding it `null`, it assigned `fraudScore = 65`.
     - Any score $> 50$ automatically triggers `MANUAL_REVIEW`.

  2. **Is any money paid out when score is 65?**:
     - **No. Exactly $0.00 is released.**
     - The claim is held in a frozen state (`MANUAL_REVIEW`) for anti-fraud protection. All payouts and fund creations are blocked.

  3. **How the death certificate is stored**:
     - Stored inside MySQL's persistent `claim_reason` column:
       `"Continuity care claim filed by Jane Doe (SPOUSE); Cert: DC-998877"`.
     - When re-loading, the system extracts the certificate number from `claim_reason` and populates the response cleanly.

  4. **Why $25,000 was set as the default fund amount**:
     - It is **not** $25,000 for every customer. It represents **Customer 1's specific policy contracted coverage (`policy.coverageAmount`)**.
     - A customer with a $10,000 policy only gets a $10,000 fund.
     - User confirmed: *"its fine let it be default as of now"*.

  5. **Admin Manual Approval in Frontend**:
     - Admin/Claims Officer opens the dashboard, inspects the claim, and clicks **[Approve]** (`POST /api/claims/1/approve`).
     - Allowed `MANUAL_REVIEW` in `investigateClaim` status validation.
     - Evaluates `hasCert = true`, lowering fraud score to **`10`**, approving the claim, setting Policy #2 to `CLAIM_FILED`, and allocating the **$25,000.00 Pet Continuity Fund** in `PaymentFundService`.

---

## 4. Current State of Microservices & Live Test Data

### Active Entities in Database

| Entity | ID / Reference | Key Attributes | Status |
| :--- | :--- | :--- | :--- |
| **Customer** | ID `1` | John Doe (`john.doe@example.com`) | Active |
| **Pet** | ID `1` | "Buddy", Golden Retriever, 4 yrs | Healthy |
| **Caretaker (Primary)** | ID `1` | Robert Smith (Brother) | Verified |
| **Caretaker (Backup)** | ID `2` | Emily Smith (Sister) | Verified |
| **Care Plan** | ID `1` | $300/mo allowance, daily routine | Active |
| **Quote** | ID `3` | $187.50/mo premium, $25k coverage | Converted |
| **Policy** | ID `2` (`POL-1790093637581-1`) | Coverage: $25,000.00, Premium: $187.50 | `ACTIVE` $\rightarrow$ `CLAIM_FILED` |
| **Payment** | ID `1` | $187.50 via `CREDIT_CARD`, customerId `1` | `SUCCESS` |
| **Claim** | ID `1` (`CLM-1790097087664`) | Cert: `DC-998877`, Claimant: Jane Doe | `MANUAL_REVIEW` $\rightarrow$ `APPROVED` |
| **Continuity Fund** | ID `1` | Total: $25,000.00, Monthly Allowance: $300 | `ACTIVE` |

---

## 5. Related Documentation Files

1. **[COMPLETE_END_TO_END_TESTING_FLOW.md](file:///Users/moturisaisushanth/sushanth/pet_insurance/COMPLETE_END_TO_END_TESTING_FLOW.md)**:
   Contains the sequence diagram and copy-paste API requests and payloads from `AuthService` through `ClaimsService` and fund disbursements.
2. **[END_TO_END_API_TESTING_GUIDE.md](file:///Users/moturisaisushanth/sushanth/pet_insurance/END_TO_END_API_TESTING_GUIDE.md)**:
   Updated API reference covering all 8 services.
3. **[ARCHITECTURE_AND_DATABASE_GUIDE.md](file:///Users/moturisaisushanth/sushanth/pet_insurance/ARCHITECTURE_AND_DATABASE_GUIDE.md)**:
   Database schemas, table DDLs, and reactive microservices architectural guide.
