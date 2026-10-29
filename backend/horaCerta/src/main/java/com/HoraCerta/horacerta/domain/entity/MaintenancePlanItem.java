package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa um item de manutenção preventiva de um plano,
 * definindo o serviço a executar, o intervalo entre manutenções
 * e a margem de horas utilizada para alertas de vencimento.
 *
 * @author Maria Luiza
 */
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MaintenancePlan maintenancePlan;

    @Column(name = "service_description", nullable = false, length = 200)
    private String serviceDescription;

    @Column(name = "interval_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal intervalHours;

    @Column(name = "alert_margin_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal alertMarginHours = new BigDecimal("20.00");
}
