CREATE TABLE work_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    equipment_plan_id BIGINT NOT NULL,
    maintenance_plan_item_id BIGINT,
    maintenance_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
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