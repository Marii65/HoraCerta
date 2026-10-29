package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma leitura do horímetro de um equipamento,
 * registrando o valor informado, a data da leitura,
 * o usuário responsável e uma observação opcional.
 *
 * @author Maria Luiza
 */
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "reading_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal readingValue;

    @NotNull
    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @Column(columnDefinition = "TEXT")
    private String observation;

    /**
     * Define a data e hora atuais como momento da leitura
     * quando nenhuma data tiver sido informada.
     */
    @PrePersist
    protected void onCreate() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
}
