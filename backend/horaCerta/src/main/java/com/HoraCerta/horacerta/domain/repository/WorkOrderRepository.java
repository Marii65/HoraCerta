package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.WorkOrder;
import com.HoraCerta.horacerta.domain.enums.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findByEquipmentPlan_Id(Long equipmentPlanId);

    // Filtros combinados para relatórios e listagens
    @Query("SELECT wo FROM WorkOrder wo WHERE " +
            "(:equipmentPlanId IS NULL OR wo.equipmentPlan.id = :equipmentPlanId) AND " +
            "(:status IS NULL OR wo.status = :status) AND " +
            "(:maintenanceType IS NULL OR wo.maintenanceType = :maintenanceType) AND " +
            "(:startDate IS NULL OR wo.scheduledDate >= :startDate) AND " +
            "(:endDate IS NULL OR wo.scheduledDate <= :endDate)")
    List<WorkOrder> findWithFilters(
            @Param("equipmentPlanId") Long equipmentPlanId,
            @Param("status") WorkOrderStatus status,
            @Param("maintenanceType") MaintenanceType maintenanceType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByEquipmentPlan_Equipment_Id(Long equipmentId);

    boolean existsByEquipmentPlan_MaintenancePlan_Id(Long maintenancePlanId);

    boolean existsByMaintenancePlanItem_Id(Long itemId);
}
