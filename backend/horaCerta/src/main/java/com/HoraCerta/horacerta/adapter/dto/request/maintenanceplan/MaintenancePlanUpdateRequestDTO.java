package com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MaintenancePlanUpdateRequestDTO(
        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name must be up to 100 characters")
        String name,

        @Size(max = 500, message = "Description must be up to 500 characters")
        String description
) {
}
