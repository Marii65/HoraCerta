CREATE TABLE maintenance_plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_maintenance_plans PRIMARY KEY (id)
) ENGINE=InnoDB;