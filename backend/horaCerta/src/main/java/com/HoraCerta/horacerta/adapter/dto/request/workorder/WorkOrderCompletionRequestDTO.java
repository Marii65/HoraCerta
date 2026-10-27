package com.HoraCerta.horacerta.adapter.dto.request.workorder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record WorkOrderCompletionRequestDTO(
        @NotNull(message = "Executed horometer is mandatory")
        @PositiveOrZero(message = "Executed horometer must be positive or zero")
        BigDecimal executedHorometer
) {
}
