CREATE TABLE equipments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(50),
    category VARCHAR(50),
    serial_number VARCHAR(50),
    current_horometer DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_equipments PRIMARY KEY (id),
    CONSTRAINT uk_equipments_code UNIQUE (code)
) ENGINE=InnoDB;