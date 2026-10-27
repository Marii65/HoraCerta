package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "equipment_plans",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_equipment_plan",
                        columnNames = {"equipment_id", "maintenance_plan_id"}
                )
        }
)
public class EquipmentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_plan_id", nullable = false)
    private MaintenancePlan maintenancePlan;

    @Column(name = "start_hour_meter", nullable = false, precision = 10, scale = 2)
    private BigDecimal startHourMeter = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (startHourMeter == null) {
            startHourMeter = BigDecimal.ZERO;
        }

        if (active == null) {
            active = true;
        }
    }
}


