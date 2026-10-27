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