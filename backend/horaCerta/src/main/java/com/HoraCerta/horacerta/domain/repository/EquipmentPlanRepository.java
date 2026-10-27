package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.EquipmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentPlanRepository extends JpaRepository<EquipmentPlan, Long> {
    List<EquipmentPlan> findByEquipment_Id(Long equipmentId);

    List<EquipmentPlan> findByMaintenancePlan_Id(Long maintenancePlanId);

    boolean existsByEquipment_IdAndMaintenancePlan_Id(
            Long equipmentId,
            Long maintenancePlanId
    );

    boolean existsByEquipment_Id(Long equipmentId);

    boolean existsByMaintenancePlan_Id(Long maintenancePlanId);

    boolean existsByEquipment_IdAndMaintenancePlan_IdAndActiveTrue(Long equipmentId, Long maintenancePlanId);
}
