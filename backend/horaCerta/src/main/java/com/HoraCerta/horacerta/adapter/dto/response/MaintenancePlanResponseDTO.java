package com.HoraCerta.horacerta.adapter.dto.response;

import java.time.LocalDateTime;

public record MaintenancePlanResponseDTO(
        Long id,
        String name,
        String description,
        Boolean active
) {
}
