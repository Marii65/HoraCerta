package com.HoraCerta.horacerta.adapter.dto.response;

public record MaintenancePlanResponseDTO(
        Long id,
        String name,
        String description,
        Boolean active
) {
}
