package com.HoraCerta.horacerta.adapter.dto.response;

import java.math.BigDecimal;

public record MaintenancePlanItemResponseDTO(
        Long id,
        Long maintenancePlanId,
        String serviceDescription,
        BigDecimal intervalHours,
        BigDecimal alertMarginHours
) {
}
