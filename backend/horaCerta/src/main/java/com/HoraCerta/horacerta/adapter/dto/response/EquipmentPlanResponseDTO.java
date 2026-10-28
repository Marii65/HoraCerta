package com.HoraCerta.horacerta.adapter.dto.response;

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
