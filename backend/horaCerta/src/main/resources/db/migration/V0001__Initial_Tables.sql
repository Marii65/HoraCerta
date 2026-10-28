-- Users --
CREATE TABLE users (
    id BINARY(16) NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'TECHNICIAN', 'OPERATOR') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

-- Equipments --
CREATE TABLE equipments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(50),
    category VARCHAR(50),
    serial_number VARCHAR(50),
    current_horometer DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'INACTIVE', 'UNDER_MAINTENANCE') NOT NULL,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_equipments PRIMARY KEY (id),
    CONSTRAINT uk_equipments_code UNIQUE (code)
) ENGINE=InnoDB;

-- Maintenance Plans --
CREATE TABLE maintenance_plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_maintenance_plans PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Maintenance Plan Items --
CREATE TABLE maintenance_plan_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    service_description VARCHAR(200) NOT NULL,
    interval_hours DECIMAL(10,2) NOT NULL,
    alert_margin_hours DECIMAL(10,2) NOT NULL DEFAULT 20.00,

    CONSTRAINT pk_maintenance_plan_items PRIMARY KEY (id),

    CONSTRAINT fk_plan_items_plan
        FOREIGN KEY (plan_id)
        REFERENCES maintenance_plans (id),

    CONSTRAINT chk_plan_item_interval
        CHECK (interval_hours > 0),

    CONSTRAINT chk_plan_item_alert_margin
        CHECK (alert_margin_hours >= 0),

    INDEX idx_plan_items_plan (plan_id)
) ENGINE=InnoDB;

-- Equipment Plans --
CREATE TABLE equipment_plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL,
    maintenance_plan_id BIGINT NOT NULL,
    start_hour_meter DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_equipment_plans PRIMARY KEY (id),

    CONSTRAINT uk_equipment_plan
        UNIQUE (equipment_id, maintenance_plan_id),

    CONSTRAINT fk_equipment_plans_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipments (id),

    CONSTRAINT fk_equipment_plans_plan
        FOREIGN KEY (maintenance_plan_id)
        REFERENCES maintenance_plans (id),

    CONSTRAINT chk_equipment_plan_start_hour
        CHECK (start_hour_meter >= 0),

    INDEX idx_equipment_plans_equipment (equipment_id),
    INDEX idx_equipment_plans_plan (maintenance_plan_id)
) ENGINE=InnoDB;

-- Horometer Reading --
CREATE TABLE horometer_reading (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BINARY(16) NOT NULL,
    equipment_id BIGINT NOT NULL,
    reading_value DECIMAL(10,2) NOT NULL,
    read_at DATETIME(6) NOT NULL,
    observation TEXT,

    CONSTRAINT pk_horometer_reading PRIMARY KEY (id),

    CONSTRAINT fk_horometer_reading_user
        FOREIGN KEY (user_id)
        REFERENCES users (id),

    CONSTRAINT fk_horometer_reading_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipments (id),

    CONSTRAINT chk_horometer_reading_value
        CHECK (reading_value >= 0),

    INDEX idx_horometer_reading_equipment_date
        (equipment_id, read_at),

    INDEX idx_horometer_reading_user (user_id)
) ENGINE=InnoDB;

-- Work Order --
CREATE TABLE work_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    equipment_plan_id BIGINT NOT NULL,
    maintenance_plan_item_id BIGINT,
    maintenance_type ENUM('PREVENTIVE', 'CORRECTIVE') NOT NULL,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL,
    scheduled_horometer DECIMAL(10,2),
    executed_horometer DECIMAL(10,2),
    scheduled_date DATE,
    completed_at DATETIME(6),
    responsible_technician VARCHAR(100),
    notes TEXT,

    CONSTRAINT pk_work_order PRIMARY KEY (id),

    CONSTRAINT fk_work_order_equipment_plan
        FOREIGN KEY (equipment_plan_id)
        REFERENCES equipment_plans (id),

    CONSTRAINT fk_work_order_plan_item
        FOREIGN KEY (maintenance_plan_item_id)
        REFERENCES maintenance_plan_items (id),

    CONSTRAINT chk_work_order_scheduled_hour
        CHECK (scheduled_horometer IS NULL OR scheduled_horometer >= 0),

    CONSTRAINT chk_work_order_executed_hour
        CHECK (executed_horometer IS NULL OR executed_horometer >= 0),

    INDEX idx_work_order_equipment_plan (equipment_plan_id),
    INDEX idx_work_order_status_date (status, scheduled_date),
    INDEX idx_work_order_plan_item (maintenance_plan_item_id)
) ENGINE=InnoDB;