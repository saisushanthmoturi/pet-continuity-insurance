# Pet Continuity Assurance System - Policy & Underwriting Workflow Diagram

This document contains the detailed end-to-end Mermaid sequence and flowchart diagrams for the **Policy Creation and Underwriting Workflow**, including explicit actions performed by the **Customer**, **Underwriting System**, **Underwriter Persona**, **Policy Service**, and **Payment Service**.

---

## 1. Policy & Underwriting Flowchart


```mermaid
flowchart TD
    subgraph CUSTOMER_JOURNEY ["Customer Journey"]
        A1[Customer Log In] --> A2[Register Pet & Medical Records]
        A2 --> A3[Assign Primary & Backup Caretakers]
        A3 --> A4[Submit Pet Care Plan Instructions]
        A4 --> A5[Submit Quote Request /api/underwriting/quotes]
    end

    subgraph UNDERWRITING_ENGINE ["Underwriting & Risk Service Engine"]
        A5 --> B1[Calculate Age Factor & Breed Risk]
        B1 --> B2[Analyze Medical History Costs]
        B2 --> B3[Evaluate Care Cost & Continuity Factors]
        B3 --> B4[Calculate Projected Liability & Coverage Gap]
        B4 --> B5[Compute Total Risk Score & Base Premium]
        B5 --> B6{Risk Score Assessment}
        B6 -- Auto Approve (Low Risk) --> B7[Status: OFFERED]
        B6 -- High Risk / Special Case --> B8[Status: PENDING_UW_REVIEW]
        B6 -- Extremely High Risk --> B9[Status: DECLINED]
    end

    subgraph UNDERWRITER_ACTIONS ["Underwriter Persona Actions"]
        B8 --> C1[View Pending Quotes Queue /api/underwriting/quotes]
        C1 --> C2[Inspect Actuarial Assessment & Rating Rules]
        C2 --> C3[Trigger Pet Risk Re-assessment /assessments/reassess/petId]
        C3 --> C4{Underwriter Decision}
        C4 -- Override & Approve --> B7
        C4 -- Adjust Rating Factors --> B5
        C4 -- Reject Quote --> B9
    end

    subgraph POLICY_ISSUANCE ["Policy Issuance & Activation"]
        B7 --> D1[Customer Reviews & Accepts Quote OFFERED]
        D1 --> D2[Convert Quote to Policy /api/policies/from-quote/quoteId]
        D2 --> D3[Policy Created: Status PENDING_PAYMENT]
        D3 --> D4[Customer Pays Premium /api/payments/premium]
        D4 --> D5[Payment Verified: Status COMPLETED]
        D5 --> D6[Activate Policy /api/policies/id/activate]
        D6 --> D7[Policy Transitioned: Status ACTIVE]
    end

    classDef customer fill:#e0f2fe,stroke:#0284c7,stroke-width:2px;
    classDef uw fill:#fef3c7,stroke:#d97706,stroke-width:2px;
    classDef uw_persona fill:#fce7f3,stroke:#db2777,stroke-width:2px;
    classDef policy fill:#dcfce7,stroke:#16a34a,stroke-width:2px;

    class A1,A2,A3,A4,A5,D1 customer;
    class B1,B2,B3,B4,B5,B6,B7,B8,B9 uw;
    class C1,C2,C3,C4 uw_persona;
    class D2,D3,D4,D5,D6,D7 policy;
```

---

## 2. Policy & Underwriting Detailed Sequence Diagram


```mermaid
sequenceDiagram
    autonumber
    actor Customer as Customer
    actor Underwriter as Underwriter Persona
    participant Gateway as API Gateway (8080)
    participant Auth as Auth Service (8081)
    participant Pet as Pet Service (8083)
    participant Care as Care Verification (8088)
    participant UW as Underwriting Service (8084)
    participant Policy as Policy Service (8085)
    participant Payment as Payment Service (8087)

    Customer->>Gateway: POST /api/auth/login
    Gateway->>Auth: Authenticate Credentials
    Auth-->>Customer: Return JWT Token (role: CUSTOMER)

    Customer->>Gateway: POST /api/pets
    Gateway->>Pet: Create Pet Profile
    Pet-->>Customer: Return Pet DTO (petId)

    Customer->>Gateway: POST /api/care/caretakers
    Gateway->>Care: Nominate Primary & Backup Caretakers
    Care-->>Customer: Caretakers Saved

    Customer->>Gateway: POST /api/underwriting/quotes
    Gateway->>UW: Generate Quote Request (customerId, petId, requestedCoverage)

    note over UW: Actuarial Engine evaluates Age, Breed,<br/>Medical History & Care Cost Factors
    UW->>UW: Calculate Risk Score & Base Premium

    alt Auto-Approved Quote
        UW-->>Customer: Return Quote DTO (status: OFFERED, premiumAmount)
    else Underwriter Review Required
        UW->>UW: Set Quote status: PENDING_UW_REVIEW
        Underwriter->>Gateway: GET /api/underwriting/quotes
        Gateway->>UW: Fetch Pending Quotes Queue
        UW-->>Underwriter: Return Pending Quotes List

        Underwriter->>Gateway: GET /api/underwriting/assessments/quote/{quoteId}
        Gateway->>UW: Fetch Risk Assessment Details
        UW-->>Underwriter: Return Assessment Breakdown & Rating Rules

        Underwriter->>Gateway: POST /api/underwriting/assessments/reassess/{petId}
        Gateway->>UW: Re-evaluate Pet Risk Factors
        UW-->>Underwriter: Updated Risk Calculation

        Underwriter->>Gateway: PUT /api/underwriting/quotes/{id} (decision: APPROVED)
        Gateway->>UW: Update Quote Status to OFFERED
        UW-->>Underwriter: Quote Approved
    end

    Customer->>Gateway: PUT /api/underwriting/quotes/{id} (status: ACCEPTED)
    Gateway->>UW: Accept Quote Offer
    UW-->>Customer: Quote Status ACCEPTED

    Customer->>Gateway: POST /api/policies/from-quote/{quoteId}
    Gateway->>Policy: Issue Policy from Quote
    Policy-->>Customer: Policy Created (status: PENDING_PAYMENT, policyNumber)

    Customer->>Gateway: POST /api/payments/premium
    Gateway->>Payment: Process Premium Payment
    Payment-->>Customer: Payment Reference (status: COMPLETED)

    Customer->>Gateway: POST /api/policies/{id}/activate
    Gateway->>Policy: Activate Policy
    Policy-->>Customer: Policy Active (status: ACTIVE)
```
