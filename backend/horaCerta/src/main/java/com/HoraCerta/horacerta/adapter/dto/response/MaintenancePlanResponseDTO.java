package com.HoraCerta.horacerta.adapter.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MaintenancePlanResponseDTO(
        Long id,
        Long maintenancePlanId,
        String serviceDescription,
        BigDecimal intervalHours,
        BigDecimal alertMarginHours
) {
}
