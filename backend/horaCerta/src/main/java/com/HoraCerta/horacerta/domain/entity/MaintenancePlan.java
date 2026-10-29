package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um plano de manutenção preventiva, contendo sua identificação,
 * descrição, estado de ativação e os itens de manutenção associados.
 *
 * @author Maria Luiza
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "maintenance_plans")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaintenancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "maintenancePlan", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MaintenancePlanItem> items = new ArrayList<>();

    /**
     * Define o plano como ativo antes da persistência quando
     * nenhum estado de ativação tiver sido informado.
     */
    @PrePersist
    protected void onCreate() {
        if (this.active == null) {
            this.active = true;
        }
    }
}
