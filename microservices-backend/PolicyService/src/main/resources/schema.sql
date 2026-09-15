CREATE TABLE IF NOT EXISTS policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(100) NOT NULL UNIQUE,
    quote_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    coverage_amount DOUBLE NOT NULL,
    monthly_premium DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_date VARCHAR(50),
    end_date VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS policy_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
