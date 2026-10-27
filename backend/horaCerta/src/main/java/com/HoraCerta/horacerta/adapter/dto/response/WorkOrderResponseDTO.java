package com.HoraCerta.horacerta.adapter.dto.response;

import com.HoraCerta.horacerta.domain.enums.workorder.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.workorder.WorkOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkOrderResponseDTO(
        Long id,
        Long equipmentPlanId,
        Long maintenancePlanItemId,
        MaintenanceType maintenanceType,
        WorkOrderStatus status,
        BigDecimal scheduledHorometer,
        BigDecimal executedHorometer,
        LocalDate scheduledDate,
        LocalDateTime completedAt,
        String responsibleTechnician,
        String notes
) {
}
