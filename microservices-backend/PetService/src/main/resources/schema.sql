CREATE TABLE IF NOT EXISTS pets (
    pet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    date_of_birth_estimated BOOLEAN DEFAULT FALSE,
    name VARCHAR(100) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    species_code VARCHAR(50) NOT NULL,
    breed_code VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    date_of_birth DATE,
    weight_value DOUBLE,
    microchip_id VARCHAR(100),
    weight_unit VARCHAR(10) DEFAULT 'KG',
    annual_care_cost DOUBLE DEFAULT 1200.0,
    expected_remaining_years INT DEFAULT 10,
    neutered_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_medical_records (
    medical_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    record_type VARCHAR(50) DEFAULT 'GENERAL',
    diagnosis VARCHAR(255),
    treatment TEXT,
    vet_name VARCHAR(150),
    record_date DATE,
    risk_level VARCHAR(50) DEFAULT 'LOW',
    notes TEXT,
    annual_med_cost DOUBLE DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pet_medical_records_pet FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
);
