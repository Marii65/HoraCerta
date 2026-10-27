package com.HoraCerta.horacerta.domain.entity;

import com.HoraCerta.horacerta.domain.enums.workorder.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.workorder.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "work_order")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_plan_id", nullable = false)
    private EquipmentPlan equipmentPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_plan_item_id")
    private MaintenancePlanItem maintenancePlanItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 20)
    private MaintenanceType maintenanceType = MaintenanceType.PREVENTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderStatus status = WorkOrderStatus.SCHEDULED;

    @Column(name = "scheduled_horometer", precision = 10, scale = 2)
    private BigDecimal scheduledHorometer;

    @Column(name = "executed_horometer", precision = 10, scale = 2)
    private BigDecimal executedHorometer;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "responsible_technician", length = 100)
    private String responsibleTechnician;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (this.maintenanceType == null) {
            this.maintenanceType = MaintenanceType.PREVENTIVE;
        }
        if (this.status == null) {
            this.status = WorkOrderStatus.SCHEDULED;
        }
    }

}
