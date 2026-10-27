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