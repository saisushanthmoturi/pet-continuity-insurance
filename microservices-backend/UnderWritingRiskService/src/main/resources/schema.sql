CREATE TABLE IF NOT EXISTS quotes (
    quote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_number VARCHAR(100) UNIQUE,
    currency VARCHAR(10) DEFAULT 'USD',
    input_snapshot JSON,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    requested_coverage DOUBLE NOT NULL,
    coverage_period VARCHAR(50) DEFAULT 'ANNUAL',
    premium_amount DOUBLE NOT NULL,
    risk_score INT NOT NULL,
    risk_class VARCHAR(50) DEFAULT 'MODERATE',
    decision VARCHAR(50) NOT NULL,
    decision_reason TEXT,
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
