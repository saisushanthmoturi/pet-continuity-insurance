CREATE TABLE IF NOT EXISTS claims (
    claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_number VARCHAR(100) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    claim_reason VARCHAR(255),
    triggering_event VARCHAR(100) DEFAULT 'OWNER_DEATH',
    event_date DATE,
    currency VARCHAR(10) DEFAULT 'USD',
    claim_amount DOUBLE NOT NULL,
    decided_at TIMESTAMP NULL,
    decision_reason VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    priority VARCHAR(50) DEFAULT 'NORMAL',
    claims_officer_id BIGINT,
    fraud_status VARCHAR(50) DEFAULT 'CLEARED',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS claim_documents (
    document_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    storage_reference VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    file_size_bytes BIGINT,
    check_sum VARCHAR(100),
    uploaded_by VARCHAR(100) DEFAULT 'USER',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_documents_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_verifications (
    verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    event_type VARCHAR(100) DEFAULT 'DEATH_VERIFICATION',
    verification_method VARCHAR(100),
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    verified_by VARCHAR(100),
    verified_at TIMESTAMP NULL,
    remarks TEXT,
    CONSTRAINT fk_event_verifications_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS investigations (
    investigation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    investigator_id BIGINT,
    findings TEXT,
    eligibility_status VARCHAR(50) DEFAULT 'PENDING',
    fraud_indicator_count INT DEFAULT 0,
    recommendation VARCHAR(100),
    status VARCHAR(50) DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_investigations_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fraud_assessments (
    fraud_assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    score INT NOT NULL,
    model_version VARCHAR(50) DEFAULT 'FRAUD_RULE_V1',
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
    changed_by VARCHAR(100) DEFAULT 'SYSTEM',
    reason VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_status_history_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS claim_information_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    requested_by VARCHAR(100) NOT NULL,
    request_text TEXT NOT NULL,
    due_at TIMESTAMP NULL,
    status VARCHAR(50) DEFAULT 'OPEN',
    responded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_info_req_claim FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE
);
