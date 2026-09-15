# Pet Continuity Assurance - Reactive Microservices Platform

A complete insurance-domain reactive platform built with **Spring Boot WebFlux (Functional Router & Handler pattern)**, **Spring Cloud Gateway**, **Netflix Eureka**, and **MySQL R2DBC** with independent databases per service.

The platform guarantees continuous financial protection, veterinary care, and living support for a pet in the event of an owner's death.

---

## 🏛 Architecture & Technology Stack

- **Architecture**: Microservices Architecture with Eureka Service Discovery and Spring Cloud API Gateway.
- **Reactive Engine**: Spring WebFlux using functional `RouterFunction<ServerResponse>` and `HandlerFunction`.
- **Database Access**: Reactive Relational Database Connectivity (R2DBC) with MySQL (`io.asyncer:r2dbc-mysql`).
- **Database per Service**: 8 isolated schemas with automated DDL initialization (`schema.sql`).
- **Inter-Service Communication**: Non-blocking REST using `@LoadBalanced WebClient` resolved via Eureka.
- **Security**: Reactive Spring Security (`SecurityWebFilterChain`), BCrypt password hashing, and JJWT token generation/validation.

---

## 🚀 Services & Ports Matrix

| Service | Port | Database | Primary Responsibility |
| :--- | :--- | :--- | :--- |
| **EurekaServer** | `8761` | — | Service discovery registry |
| **ApiGateway** | `8080` | — | Unified backend entry point, CORS for Angular, route proxying |
| **AuthService** | `8081` | `auth_db` | User registration, login, JWT issuance, roles (`CUSTOMER`, `UNDERWRITER`, `CLAIMS_OFFICER`, `CARETAKER`, `ADMIN`) |
| **CustomerService** | `8082` | `customer_db` | Policyholder identity, profile, contact info |
| **PetService** | `8083` | `pet_db` | Pet profile, care cost baseline, medical records |
| **UnderWritingRiskService** | `8084` | `underwriting_db` | Actuarial risk rating, future care liability, quote & coverage gap calculation |
| **PolicyService** | `8085` | `policy_db` | Quote acceptance, `PENDING_PAYMENT` lifecycle, activation, status history |
| **ClaimsService** | `8086` | `claims_db` | Owner death registration, death certificate verification, fraud checks, fund trigger |
| **PaymentFundService** | `8087` | `payment_fund_db` | Simulated premium payment, Pet Continuity Fund allocation, conditional monthly disbursement |
| **CareVerificationService** | `8088` | `care_verification_db` | Caretaker management (Primary & Backup), care plans, pet check-ins, monthly eligibility check |

---

## 🔄 End-to-End User Journey

1. **Register/Login**: Authenticate via `AuthService` (`/api/auth/register`, `/api/auth/login`) to receive a signed JWT with user role.
2. **Customer Profile**: Create policyholder record via `CustomerService` (`POST /api/customers`).
3. **Pet Registration**: Register pet (e.g. "Max", 7-year-old Golden Retriever) and medical records via `PetService` (`POST /api/pets`, `POST /api/pets/{id}/medical-records`).
4. **Caretaker & Care Plan**: Nominate primary and backup caretakers, vet contact, and feeding instructions via `CareVerificationService` (`POST /api/care/caretakers`, `POST /api/care/care-plans`).
5. **Quote & Underwriting**: Request quote from `UnderWritingRiskService` (`POST /api/underwriting/quotes`). The service dynamically retrieves pet and medical history from `PetService`, calculates actuarial risk score, future care liability, coverage gap, and premium.
6. **Policy Creation**: Accept quote via `PolicyService` (`POST /api/policies/from-quote/{quoteId}`), creating policy in `PENDING_PAYMENT` status.
7. **Simulated Payment & Activation**: Pay premium via `PaymentFundService` (`POST /api/payments/premium`). On `SUCCESS`, it calls `PolicyService` to transition policy status to `ACTIVE`.
8. **Owner-Death Claim**: Authorized claimant files death claim via `ClaimsService` (`POST /api/claims`).
9. **Death Verification & Investigation**: Mock death verification runs against registry (`POST /api/claims/{id}/verify-death`). Investigation verifies active policy, waiting period, and fraud indicators (`POST /api/claims/{id}/investigate`).
10. **Pet Continuity Fund Setup**: Upon approval, `ClaimsService` triggers `PaymentFundService` (`POST /api/payments/funds/create`) allocating:
    - **Total Continuity Fund**: $23,000
    - **Monthly Care Allowance**: $300
    - **Veterinary Reserve**: $4,000
    - **Emergency Reserve**: $2,000
11. **Pet Care Check & Conditional Disbursement**: Caretaker submits pet check-in (`POST /api/care/verifications`). `PaymentFundService` verifies monthly eligibility with `CareVerificationService` (`POST /api/payments/funds/{id}/disburse-monthly`). If verified, monthly $300 care benefit is released; if verification fails, disbursement is `SUSPENDED`.
12. **Caretaker Transfer / Failover**: If primary caretaker becomes `UNAVAILABLE`, care engine redirects eligibility and benefit disbursements to verified backup caretaker.

---

## 🛠 Local Setup & Running

### Prerequisites
- Java 17+ (or Java 21 / 25)
- Maven 3.9+
- MySQL Server (running on `localhost:3306` with user `root` and empty password, or configure `application.yaml`)

### Databases
```sql
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS customer_db;
CREATE DATABASE IF NOT EXISTS pet_db;
CREATE DATABASE IF NOT EXISTS underwriting_db;
CREATE DATABASE IF NOT EXISTS policy_db;
CREATE DATABASE IF NOT EXISTS claims_db;
CREATE DATABASE IF NOT EXISTS payment_fund_db;
CREATE DATABASE IF NOT EXISTS care_verification_db;
```

### Start Services (Order)
1. **Eureka Server**: `cd microservices-backend/EurekaServer && mvn spring-boot:run` (:8761)
2. **API Gateway**: `cd microservices-backend/ApiGateway && mvn spring-boot:run` (:8080)
3. **Business Services**: Start `AuthService` (:8081), `CustomerService` (:8082), `PetService` (:8083), `UnderWritingRiskService` (:8084), `PolicyService` (:8085), `ClaimsService` (:8086), `PaymentFundService` (:8087), `CareVerificationService` (:8088).
