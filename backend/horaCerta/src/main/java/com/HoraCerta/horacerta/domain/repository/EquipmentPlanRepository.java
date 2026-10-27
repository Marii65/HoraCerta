package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.EquipmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentPlanRepository extends JpaRepository<EquipmentPlan, Long> {
    List<EquipmentPlan> findByEquipmentId(Long equipmentId);

    List<EquipmentPlan> findByMaintenancePlanId(Long maintenancePlanId);
}
