CREATE TABLE IF NOT EXISTS claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_number VARCHAR(100) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL,
    claimant_name VARCHAR(150) NOT NULL,
    relationship VARCHAR(100) NOT NULL,
    death_certificate_no VARCHAR(100) NOT NULL,
    date_of_death VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    rejection_reason VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS claim_investigations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    policy_active BOOLEAN NOT NULL,
    waiting_period_passed BOOLEAN NOT NULL,
    fraud_score INT NOT NULL,
    decision VARCHAR(50) NOT NULL,
    notes VARCHAR(255),
    investigated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
