package com.HoraCerta.horacerta.domain.entity;

import com.HoraCerta.horacerta.domain.enums.equipment.EquipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @NotBlank(message = "Code is mandatory")
    @Size(max = 50, message = "Code must be up to 50 characters")
    @Column(name = "code",nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name must be up to 100 characters")
    @Column(name = "name",nullable = false, length = 100)
    private String name;

    @Size(max = 50, message = "Model must be up to 50 characters")
    @Column(name = "model",length = 50)
    private String model;

    @Size(max = 50, message = "Category must be up to 50 characters")
    @Column(name = "category",length = 50)
    private String category;

    @Size(max = 50, message = "Serial number must be up to 50 characters")
    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    @NotNull(message = "Current horometer is mandatory")
    @PositiveOrZero(message = "Current horometer must be positive or zero")
    @Column(name = "current_horometer", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentHorometer = BigDecimal.ZERO;

    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentStatus status = EquipmentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
