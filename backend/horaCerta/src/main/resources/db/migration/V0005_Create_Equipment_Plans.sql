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