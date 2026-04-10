CREATE DATABASE IF NOT EXISTS vehicle_parking_db;
USE vehicle_parking_db;

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    owner_name VARCHAR(100) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    color VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS parking_slots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    slot_number VARCHAR(20) NOT NULL UNIQUE,
    slot_type VARCHAR(20) NOT NULL,
    occupied BIT NOT NULL DEFAULT 0,
    active BIT NOT NULL DEFAULT 1,
    INDEX idx_parking_slots_type_status (slot_type, occupied, active, slot_number)
);

CREATE TABLE IF NOT EXISTS parking_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME NULL,
    status VARCHAR(20) NOT NULL,
    duration_minutes BIGINT NULL,
    notes VARCHAR(255) NULL,
    CONSTRAINT fk_parking_records_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT fk_parking_records_slot FOREIGN KEY (slot_id) REFERENCES parking_slots(id),
    INDEX idx_parking_records_vehicle_status (vehicle_id, status),
    INDEX idx_parking_records_slot_status (slot_id, status),
    INDEX idx_parking_records_status_entry_time (status, entry_time),
    INDEX idx_parking_records_entry_time (entry_time)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parking_record_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    duration_minutes BIGINT NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    paid_at DATETIME NOT NULL,
    CONSTRAINT fk_payments_parking_record FOREIGN KEY (parking_record_id) REFERENCES parking_records(id),
    INDEX idx_payments_paid_at (paid_at)
);
