package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "maintenance_plan_items")
public class MaintenancePlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Maintenance plan is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MaintenancePlan maintenancePlan;

    @NotBlank(message = "Service description is mandatory")
    @Size(max = 200, message = "Service description must be up to 200 characters")
    @Column(name = "service_description", nullable = false, length = 200)
    private String serviceDescription;

    @NotNull(message = "Interval hours is mandatory")
    @Positive(message = "Interval hours must be greater than zero")
    @Column(name = "interval_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal intervalHours;

    @NotNull(message = "Alert margin hours is mandatory")
    @PositiveOrZero(message = "Alert margin hours must be positive or zero")
    @Column(name = "alert_margin_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal alertMarginHours = new BigDecimal("20.00");
}
