CREATE TABLE IF NOT EXISTS quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    requested_coverage DOUBLE NOT NULL,
    monthly_premium DOUBLE NOT NULL,
    risk_score INT NOT NULL,
    decision VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS risk_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_id BIGINT,
    pet_id BIGINT NOT NULL,
    age_factor DOUBLE NOT NULL,
    health_factor DOUBLE NOT NULL,
    projected_care_liability DOUBLE NOT NULL,
    coverage_gap DOUBLE NOT NULL,
    risk_level VARCHAR(50) NOT NULL,
    assessment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
