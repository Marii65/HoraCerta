package com.HoraCerta.horacerta.adapter.dto.request.maintenanceplanitem;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MaintenancePlanItemRequestDTO(

        @Positive(message = "Maintenance Plan ID must be positive")
        @NotNull(message = "Maintenance ID plan is mandatory")
        Long maintenancePlanId,

        @NotBlank(message = "Service description is mandatory")
        @Size(max = 200, message = "Service description must be up to 200 characters")
        String serviceDescription,

        @NotNull(message = "Interval hours is mandatory")
        @Positive(message = "Interval hours must be greater than zero")
        BigDecimal intervalHours,

        @NotNull(message = "Alert margin hours is mandatory")
        @PositiveOrZero(message = "Alert margin hours must be positive or zero")
        BigDecimal alertMarginHours

) {
}
