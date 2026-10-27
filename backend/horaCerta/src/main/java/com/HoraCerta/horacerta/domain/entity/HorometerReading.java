package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "horometer_reading")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HorometerReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Equipment is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @NotNull(message = "Reading value is mandatory")
    @PositiveOrZero(message = "Reading value must be positive or zero")
    @Column(name = "reading_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal readingValue;

    @NotNull(message = "Read at timestamp is mandatory")
    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @PrePersist
    protected void onCreate() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
}
