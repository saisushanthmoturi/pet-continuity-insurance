CREATE TABLE IF NOT EXISTS premium_payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    policy_id BIGINT NOT NULL,
    customer_id BIGINT,
    currency VARCHAR(10) DEFAULT 'USD',
    idempotency_key VARCHAR(100),
    amount DOUBLE NOT NULL,
    payment_reference VARCHAR(100),
    payment_method VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(255),
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    failed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_care_funds (
    fund_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    policy_id BIGINT NOT NULL UNIQUE,
    pet_id BIGINT NOT NULL,
    approved_claim_id BIGINT,
    currency VARCHAR(10) DEFAULT 'USD',
    total_amount DOUBLE NOT NULL,
    available_amount DOUBLE NOT NULL,
    monthly_allowance DOUBLE NOT NULL,
    veterinary_reserve DOUBLE NOT NULL,
    emergency_reserve DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS fund_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    fund_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    amount DOUBLE NOT NULL,
    balance_after DOUBLE NOT NULL,
    external_reference VARCHAR(100),
    idempotency_key VARCHAR(100),
    reference_id VARCHAR(100),
    reversal_of_transaction_id BIGINT,
    description VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS',
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS disbursements (
    disbursement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    fund_id BIGINT NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    amount DOUBLE NOT NULL,
    assignment_id BIGINT,
    caretaker_id BIGINT,
    eligibility_check_id BIGINT,
    idempotency_key VARCHAR(100),
    disbursement_type VARCHAR(50) DEFAULT 'MONTHLY_ALLOWANCE',
    eligibility_status VARCHAR(50) DEFAULT 'ELIGIBLE',
    benefit_month VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'PROCESSED',
    scheduled_date DATE,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS expenses (
    expense_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    fund_id BIGINT NOT NULL,
    assignment_id BIGINT,
    caretaker_id BIGINT,
    expense_type VARCHAR(50) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    amount DOUBLE NOT NULL,
    vendor_name VARCHAR(150),
    document_reference VARCHAR(255),
    approval_status VARCHAR(50) DEFAULT 'APPROVED',
    idempotency_key VARCHAR(100),
    decided_by VARCHAR(100),
    decided_at TIMESTAMP NULL,
    decision_reason VARCHAR(255),
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
