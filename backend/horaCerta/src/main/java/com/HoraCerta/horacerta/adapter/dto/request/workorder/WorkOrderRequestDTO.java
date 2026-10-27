package com.HoraCerta.horacerta.adapter.dto.request.workorder;

import com.HoraCerta.horacerta.domain.enums.workorder.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WorkOrderRequestDTO(
        @NotNull(message = "Equipment Plan ID is mandatory")
        Long equipmentPlanId,

        Long maintenancePlanItemId,

        @NotNull(message = "Maintenance type is mandatory")
        MaintenanceType maintenanceType,

        @PositiveOrZero(message = "Scheduled horometer must be positive or zero")
        BigDecimal scheduledHorometer,


        LocalDate scheduledDate,

        @Size(max = 100, message = "Responsible technician name must be up to 100 characters")
        String responsibleTechnician,

        @Size(max = 2000, message = "Notes must be up to 2000 characters")
        String notes
) {
}
