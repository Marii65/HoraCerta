package com.HoraCerta.horacerta.adapter.dto.response;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EquipmentPlanResponseDTO(
        Long id,
        Long equipmentId,
        String equipmentCode,
        String equipmentName,
        Long maintenancePlanId,
        String maintenancePlanName,
        BigDecimal startHourMeter,
        Boolean active,
        LocalDateTime createdAt
) {
}
