CREATE TABLE IF NOT EXISTS caretakers (
    caretaker_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    user_id BIGINT,
    customer_id BIGINT,
    pet_id BIGINT,
    name VARCHAR(150),
    full_name VARCHAR(150),
    phone VARCHAR(50),
    email VARCHAR(150),
    identity_status VARCHAR(50) DEFAULT 'VERIFIED',
    relationship VARCHAR(50) DEFAULT 'PRIMARY',
    caretaker_type VARCHAR(50) DEFAULT 'PRIMARY',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    verification_status VARCHAR(50) DEFAULT 'VERIFIED',
    availability_status VARCHAR(50) DEFAULT 'ACTIVE',
    priority INT DEFAULT 1,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS pet_caretaker_assignments (
    assignment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    caretaker_id BIGINT NOT NULL,
    priority INT DEFAULT 1,
    caretaker_type VARCHAR(50) DEFAULT 'PRIMARY',
    relationship VARCHAR(50),
    availability_status VARCHAR(50) DEFAULT 'AVAILABLE',
    assignment_status VARCHAR(50) DEFAULT 'ACTIVE',
    nominated_by BIGINT,
    effective_from DATE,
    effective_to DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS caretaker_verification (
    verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caretaker_id BIGINT NOT NULL,
    verification_type VARCHAR(50) DEFAULT 'IDENTITY',
    verification_method VARCHAR(50) DEFAULT 'DOCUMENT',
    verification_status VARCHAR(50) DEFAULT 'VERIFIED',
    evidence_reference VARCHAR(255),
    verification_by VARCHAR(100),
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS care_plans (
    care_plan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    pet_id BIGINT NOT NULL,
    primary_caretaker_id BIGINT,
    backup_caretaker_id BIGINT,
    feeding_instructions TEXT,
    medication_instructions TEXT,
    vet_details TEXT,
    vet_contact TEXT,
    routine_details TEXT,
    special_requirements TEXT,
    special_needs TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    plan_version INT DEFAULT 1,
    effective_from DATE,
    effective_to DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_verifications (
    pet_verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BIGINT,
    pet_id BIGINT NOT NULL,
    caretaker_id BIGINT NOT NULL,
    verification_method VARCHAR(50) DEFAULT 'MOBILE_CHECKIN',
    verification_status VARCHAR(50) DEFAULT 'PASSED',
    status VARCHAR(50) DEFAULT 'PASSED',
    evidence_reference VARCHAR(255),
    verification_date VARCHAR(50),
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_verification_date DATE,
    remarks TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS verification_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    caretaker_id BIGINT NOT NULL,
    verification_type VARCHAR(50),
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    verification_method VARCHAR(50),
    verified_by VARCHAR(100),
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS monthly_eligible_checks (
    eligibility_check_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    assignment_id BIGINT NOT NULL,
    fund_id BIGINT NOT NULL,
    eligibility_month VARCHAR(20) NOT NULL,
    caretaker_verified BOOLEAN DEFAULT TRUE,
    pet_verified BOOLEAN DEFAULT TRUE,
    policy_active BOOLEAN DEFAULT TRUE,
    fund_active BOOLEAN DEFAULT TRUE,
    eligible BOOLEAN DEFAULT TRUE,
    failure_reason VARCHAR(255),
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    correlation_id VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS care_transfers (
    transfer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    from_assignment_id BIGINT,
    to_assignment_id BIGINT,
    reason VARCHAR(255),
    effective_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    initiated_by VARCHAR(100),
    status VARCHAR(50) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
