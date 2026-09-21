CREATE TABLE IF NOT EXISTS policies (
    policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(100) NOT NULL UNIQUE,
    quote_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    coverage_amount DOUBLE NOT NULL,
    premium_amount DOUBLE NOT NULL,
    deductible DOUBLE DEFAULT 250.0,
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
