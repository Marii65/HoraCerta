package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.WorkOrder;
import com.HoraCerta.horacerta.domain.enums.workorder.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findByEquipmentPlanId(Long equipmentPlanId);

    boolean existsByEquipmentId(Long equipmentId);
}
