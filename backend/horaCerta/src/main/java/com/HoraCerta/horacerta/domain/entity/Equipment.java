package com.HoraCerta.horacerta.domain.entity;

import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa um equipamento monitorado pelo sistema HoraCerta,
 * incluindo sua identificação, horímetro atual e estado de ativação.
 *
 * @author Maria Luiza
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "equipments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "code",nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name",nullable = false, length = 100)
    private String name;

    @Column(name = "model",length = 50)
    private String model;

    @Column(name = "category",length = 50)
    private String category;

    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    @Column(name = "current_horometer", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentHorometer = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentStatus status = EquipmentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Inicializa a data de criação e garante os valores padrão
     * do horímetro e do estado antes de persistir a entidade.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currentHorometer == null) {
            this.currentHorometer = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = EquipmentStatus.ACTIVE;
        }
    }

}
