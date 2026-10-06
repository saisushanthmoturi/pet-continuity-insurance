# Pet Continuity Insurance System - User Flow Diagrams & Role Workflows

This document details the end-to-end frontend operational workflows and state transitions for each user role within the **Pet Continuity Insurance System**. All diagrams are authored using **Mermaid.js** syntax for easy rendering in GitHub, Antigravity IDE, and Markdown previewers.

---

## 1. System Overview & Authentication Flow

Before accessing any role-specific workspace, users register or log in via the JWT Authentication module.

```mermaid
flowchart TD
    Start(["User Accesses App"]) --> Choice{"Has Account?"}
    Choice -- No --> Register["Navigate to /auth/register"]
    Register --> RegForm["Fill Registration Form\n(Email, Password, Name, Role)"]
    RegForm --> RegSubmit["Submit Registration API"]
    RegSubmit --> StoreJWT["Store JWT Token, Role, & User Details\nin LocalStorage"]

    Choice -- Yes --> Login["Navigate to /auth/login"]
    Login --> QuickDemo{"Use Quick Demo Login?"}
    QuickDemo -- Yes --> ClickDemo["Click Role Button\n(Customer, Underwriter, Adjuster, Admin)"]
    QuickDemo -- No --> ManualLogin["Enter Email & Password"]
    ClickDemo --> AuthAPI["POST /api/auth/login"]
    ManualLogin --> AuthAPI

    AuthAPI --> AuthSuccess{"Auth Successful?"}
    AuthSuccess -- No --> ErrMsg["Display Error Alert Banner"]
    ErrMsg --> Login

    AuthSuccess -- Yes --> DispatchStore["Dispatch NgRx loginSuccess(auth)"]
    DispatchStore --> StoreJWT
    StoreJWT --> RoleSwitch{"User Role Claim"}

    RoleSwitch -- "ROLE_CUSTOMER" --> CustomerDash["Redirect to /dashboard\n(Customer Portal)"]
    RoleSwitch -- "ROLE_UNDERWRITER" --> UnderwriterWB["Redirect to /quotes/review\n(Underwriter Workbench)"]
    RoleSwitch -- "ROLE_CLAIMS_ADJUSTER" --> ClaimsWB["Redirect to /claims/review\n(Claims Adjuster Portal)"]
    RoleSwitch -- "ROLE_ADMIN" --> AdminPool["Redirect to /funds\n(Admin Reserves Pool)"]
```

---

## 2. Role 1: Pet Owner Workflow (`ROLE_CUSTOMER`)

The Pet Owner portal guarantees pet continuity protection by orchestrating pet registration, care protocol formulation, actuarial quote generation, policy activation, claim filing, and monthly premium payments.

### 2.1 Complete Pet Owner End-to-End User Journey

```mermaid
flowchart TD
    subgraph OwnerPortal["Pet Owner Workspace (/dashboard)"]
        Dashboard["Customer Dashboard\n(Overview Metrics)"]
        MyPets["Pet Registry (/pets)"]
        PetReg["Register Pet (/pets/register)"]
        CarePlan["Care Protocols (/care)"]
        AssignCare["Assign Caretaker (/care/add-caretaker)"]
        ReqQuote["Request Quote (/quotes)"]
        Policies["Policy List (/policies)"]
        FileClaim["File Claim (/claims/file)"]
        PayPremium["Pay Premium (/payments)"]
    end

    Dashboard --> MyPets
    MyPets --> PetReg
    PetReg -- "Save Pet Details" --> MyPets
    MyPets --> CarePlan
    CarePlan --> AssignCare
    AssignCare -- "Save Care Plan" --> CarePlan
    CarePlan -- "Check Eligibility" --> ReqQuote
    ReqQuote -- "Generate Quote" --> Policies
    Policies -- "Activate Policy" --> FileClaim
    Policies --> PayPremium
```

### 2.2 Pet Registration & Medical History Flow

```mermaid
flowchart TD
    StartPet["Click '+ Register New Pet'"] --> Form["Open Pet Registration Form"]
    Form --> InputDetails["Enter Pet Name, Species, Breed, Age, Gender"]
    InputDetails --> Microchip["Enter Microchip ID (CHIP-XXXXXX)"]
    Microchip --> Flags["Set Vaccination & Sterilization Toggles"]
    Flags --> MedHistory["Enter Medical History Notes"]
    MedHistory --> Submit["Click 'Save & Register Pet'"]
    Submit --> API["POST /api/pets"]
    API --> UpdateStream["Update RxJS pets$ BehaviorSubject"]
    UpdateStream --> Redirect["Redirect to /pets Grid View"]
```

### 2.3 Caretaker Assignment & Care Verification Flow

```mermaid
flowchart TD
    StartCare["Click '+ Assign Caretaker Protocol'"] --> SelectPet["Select Covered Pet from Dropdown"]
    SelectPet --> CheckElig["Trigger Care Compliance Check"]
    CheckElig --> CareAPI["GET /api/care/eligibility/check"]
    CareAPI --> DisplayScore["Display Compliance Score % Badge"]
    DisplayScore --> FillCare["Enter Caretaker Name, Phone, Daily Routine, Diet, Medications"]
    FillCare --> SavePlan["Click 'Save Care Plan Protocol'"]
    SavePlan --> PlanAPI["POST /api/care/care-plans"]
    PlanAPI --> ListPlans["Update Care Plan List (/care)"]
    ListPlans --> VerifyBtn["Click 'Verify Routine' Button"]
    VerifyBtn --> RecVerify["POST /api/care/verifications"]
    RecVerify --> VerifiedBadge["Badge Updates to 'VERIFIED'"]
```

### 2.4 Quote Generation & Policy Binding Flow

```mermaid
flowchart TD
    StartQuote["Navigate to /quotes"] --> SelectCoverage["Select Pet, Annual Limit ($5k-$25k), and Deductible"]
    SelectCoverage --> ClickCalc["Click 'Calculate Actuarial Risk & Generate Quote'"]
    ClickCalc --> ActuarialEngine["POST /api/underwriting/quotes"]
    ActuarialEngine --> CalcRisk["Calculate Risk Score (0-100) & Category"]
    CalcRisk --> ShowQuoteCard["Display Quote Card with Monthly Premium & Risk Badge"]
    ShowQuoteCard --> DecisionCheck{"Status?"}

    DecisionCheck -- "APPROVED" --> IssueBtn["Click 'Issue & Bind Policy Now'"]
    DecisionCheck -- "PENDING_REVIEW" --> UnderwriterFlag["Sent to Underwriter Workbench"]

    IssueBtn --> PolicyAPI["POST /api/policies/from-quote/{id}"]
    PolicyAPI --> ShowPolicy["Redirect to /policies (Status: PENDING)"]
    ShowPolicy --> ActivateBtn["Click 'Activate Policy'"]
    ActivateBtn --> ActiveAPI["POST /api/policies/{id}/activate"]
    ActiveAPI --> ActiveBadge["Status Updates to 'ACTIVE / In Force'"]
```

### 2.5 Claim Submission & Premium Online Payment Flow

```mermaid
flowchart TD
    subgraph ClaimFlow["Claim Filing"]
        FileClick["Navigate to /claims/file"] --> ClaimInputs["Enter Policy ID, Pet ID, Claim Amount, Incident Date"]
        ClaimInputs --> Evidence["Enter Veterinary Description & Evidence Summary"]
        Evidence --> SubmitClaim["Click 'Submit Continuity Claim'"]
        SubmitClaim --> PostClaim["POST /api/claims"]
        PostClaim --> RedirectAdjuster["Forwarded to Claims Adjuster Queue"]
    end

    subgraph PaymentFlow["Premium Payment"]
        PayClick["Click 'Pay Premium Online' on /payments"] --> OpenModal["Open Payment Modal"]
        OpenModal --> SelectMethod["Select Amount & Payment Method (Credit Card / ACH / PayPal)"]
        SelectMethod --> ClickPay["Click 'Pay $XX.XX'"]
        ClickPay --> PostPay["POST /api/payments/premium"]
        PostPay --> UpdateLedger["Inflow Transaction added to Financial Ledger"]
    end
```

---

## 3. Role 2: Underwriting Specialist Workflow (`ROLE_UNDERWRITER`)

The Underwriter evaluates actuarial risk parameters, reviews manually flagged quotes, approves or rejects coverage requests with notes, and binds insurance contracts.

```mermaid
flowchart TD
    LoginUW["Sign In as Underwriter"] --> RedirectUW["Redirect to /quotes/review"]
    RedirectUW --> ViewWB["Underwriting Quote Workbench"]
    ViewWB --> FilterQuotes["Filter Quotes (All, Pending Review, Approved, Rejected)"]
    FilterQuotes --> SelectQuote["Inspect Risk Score (0-100), Category (LOW/MEDIUM/HIGH), Premium"]

    SelectQuote --> ActionChoice{"Underwriter Decision"}
    
    ActionChoice -- "Approve Quote" --> ClickApprove["Click '✓ Approve Quote'"]
    ClickApprove --> ApproveAPI["PUT /api/underwriting/quotes/{id}/status"]
    ApproveAPI --> StatusApproved["Status updates to 'APPROVED'"]
    StatusApproved --> BindPolicy["Click 'Issue Policy from Quote' (/policies/issue)"]
    BindPolicy --> CreatePolAPI["POST /api/policies/from-quote/{quoteId}"]
    CreatePolAPI --> PolicyIssued["Policy Contract Created"]

    ActionChoice -- "Decline Quote" --> ClickDecline["Click '✕ Decline'"]
    ClickDecline --> DeclineAPI["PUT /api/underwriting/quotes/{id}/status"]
    DeclineAPI --> StatusRejected["Status updates to 'REJECTED'"]
```

---

## 4. Role 3: Claims Adjuster Workflow (`ROLE_CLAIMS_ADJUSTER`)

The Claims Adjuster verifies incident reports, inspects fraud scores, adjudicates claim amounts, approves payout disbursements, or issues formal rejection notices.

```mermaid
flowchart TD
    LoginAdjuster["Sign In as Claims Adjuster"] --> RedirectAdjuster["Redirect to /claims/review"]
    RedirectAdjuster --> ViewClaimsWB["Claims Adjuster Portal"]
    ViewClaimsWB --> FilterClaims["Filter Claims (All, Submitted, Approved, Rejected)"]
    FilterClaims --> InspectClaim["Inspect Claimed Amount, Vet Description, Incident Date, Fraud Score"]

    InspectClaim --> AdjDecision{"Adjuster Decision"}

    AdjDecision -- "Approve Payout" --> ClickApproveClaim["Click '✓ Approve Claim'"]
    ClickApproveClaim --> PostApprove["POST /api/claims/{id}/approve"]
    PostApprove --> ApprovedState["Status updates to 'APPROVED & Processing Payout'"]
    ApprovedState --> LedgerEntry["Disbursement added to Financial Ledger (/funds/ledger)"]

    AdjDecision -- "Reject Claim" --> ClickRejectClaim["Click '✕ Reject'"]
    ClickRejectClaim --> OpenRejectModal["Open Rejection Modal"]
    OpenRejectModal --> InputReason["Enter Formal Rejection Reason"]
    InputReason --> ConfirmReject["Click 'Confirm Rejection'"]
    ConfirmReject --> PostReject["POST /api/claims/{id}/reject"]
    PostReject --> RejectedState["Status updates to 'REJECTED' with Reason Banner"]
```

---

## 5. Role 4: System Administrator Workflow (`ROLE_ADMIN`)

The System Administrator oversees the Pet Continuity Reserve Fund Pool, monitors pool solvency health, triggers monthly caretaker payouts, and audits all financial transactions.

```mermaid
flowchart TD
    LoginAdmin["Sign In as Administrator"] --> RedirectAdmin["Redirect to /funds"]
    RedirectAdmin --> ViewReserves["Pet Continuity Reserve Fund Pool Overview"]
    ViewReserves --> CheckSolvency["Monitor Reserve Balances, Target Accumulation & Solvency Health %"]

    CheckSolvency --> AdminAction{"Admin Action"}

    AdminAction -- "Monthly Disbursement" --> TriggerDisburse["Click '⚡ Process Monthly Disbursement'"]
    TriggerDisburse --> DisburseAPI["POST /api/payments/funds/{id}/disburse-monthly"]
    DisburseAPI --> UpdatePool["Pool Balance Updated & Disbursement Recorded"]

    AdminAction -- "Audit Financial Ledger" --> NavLedger["Navigate to /payments"]
    NavLedger --> ViewAudit["Inspect Premium Inflows (+) & Claim Outflows (-)"]
    ViewAudit --> FilterTxns["Filter by Transaction Type & Search Txn ID"]
```

---

## 6. Route & Component Navigation Matrix

| Route Path | Permission Guard | Role Access | Primary Action / View |
| :--- | :--- | :--- | :--- |
| `/auth/login` | Public | Visitor / All Roles | Sign In & Quick Demo Login |
| `/auth/register` | Public | Visitor / All Roles | Register New Account |
| `/dashboard` | `authGuard` | `ROLE_CUSTOMER` | Customer Overview & Metrics |
| `/profile` | `authGuard` | `ROLE_CUSTOMER` | Edit Personal Profile |
| `/pets` | `authGuard` | `ROLE_CUSTOMER`, `ROLE_ADMIN` | Pet Search & Species Filter |
| `/pets/register` | `authGuard` | `ROLE_CUSTOMER` | Register New Pet Profile |
| `/care` | `authGuard` | `ROLE_CUSTOMER` | Care Protocols & Verification |
| `/care/add-caretaker` | `authGuard` | `ROLE_CUSTOMER` | Formulate Care Routine |
| `/quotes` | `authGuard` | `ROLE_CUSTOMER`, `ROLE_UNDERWRITER` | Request Actuarial Quote |
| `/quotes/review` | `authGuard` | `ROLE_UNDERWRITER` | Underwriting Risk Workbench |
| `/policies` | `authGuard` | All Roles | View Policy Contracts |
| `/policies/issue` | `authGuard` | `ROLE_UNDERWRITER` | Bind Policy from Quote |
| `/claims` / `/claims/file` | `authGuard` | `ROLE_CUSTOMER`, `ROLE_CLAIMS_ADJUSTER` | File Continuity Claim |
| `/claims/review` | `authGuard` | `ROLE_CLAIMS_ADJUSTER` | Claims Adjuster Workbench |
| `/payments` / `/funds/ledger` | `authGuard` | All Roles | Financial Ledger & Pay Premium |
| `/funds` / `/admin/funds` | `authGuard` | `ROLE_ADMIN` | Reserve Pool Solvency |

---

> [!NOTE]
> All flow diagrams are fully rendered using Mermaid syntax. Users can preview these flowcharts in GitHub, IDE Markdown previewers, or Mermaid Live Editor.
