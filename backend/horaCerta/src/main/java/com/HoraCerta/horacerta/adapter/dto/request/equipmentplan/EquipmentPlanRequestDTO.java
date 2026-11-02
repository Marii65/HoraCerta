package com.HoraCerta.horacerta.adapter.dto.request.equipmentplan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record EquipmentPlanRequestDTO(

        @Positive(message = "Equipment ID must be positive")
        @NotNull(message = "Equipment ID is mandatory")
        Long equipmentId,

        @Positive(message = "Maintenance Plan ID must be positive")
        @NotNull(message = "Maintenance ID plan is mandatory")
        Long maintenancePlanId,

        @PositiveOrZero(message = "Start hour meter must be positive or zero")
        BigDecimal startHourMeter
) {
}
