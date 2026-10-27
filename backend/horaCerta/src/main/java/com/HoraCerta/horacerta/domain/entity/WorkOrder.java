package com.HoraCerta.horacerta.domain.entity;

import com.HoraCerta.horacerta.domain.enums.workorder.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.workorder.WorkOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Equipment is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_plan_id", nullable = false)
    private EquipmentPlan equipmentPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_plan_item_id")
    private MaintenancePlanItem maintenancePlanItem;

    @NotNull(message = "Maintenance type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 20)
    private MaintenanceType maintenanceType = MaintenanceType.PREVENTIVE;

    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderStatus status = WorkOrderStatus.SCHEDULED;

    @PositiveOrZero(message = "Scheduled horometer must be positive or zero")
    @Column(name = "scheduled_horometer", precision = 10, scale = 2)
    private BigDecimal scheduledHorometer;

    @PositiveOrZero(message = "Executed horometer must be positive or zero")
    @Column(name = "executed_horometer", precision = 10, scale = 2)
    private BigDecimal executedHorometer;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Size(max = 100, message = "Responsible technician name must be up to 100 characters")
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
