package com.HoraCerta.horacerta.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name must be up to 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Active status is mandatory")
    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "maintenancePlan", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<MaintenancePlanItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.active == null) {
            this.active = true;
        }
    }
}
