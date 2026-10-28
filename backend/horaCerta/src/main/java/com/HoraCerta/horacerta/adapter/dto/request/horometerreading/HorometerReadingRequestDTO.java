package com.HoraCerta.horacerta.adapter.dto.request.horometerreading;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record HorometerReadingRequestDTO(

        @Positive(message = "Equipment Plan ID must be positive")
        @NotNull(message = "Equipment ID is mandatory")
        Long equipmentId,

        @NotNull
        @PositiveOrZero
        BigDecimal readingValue,

        @Size(max = 1000, message = "Observation must be up to 1000 characters")
        String observation

) {
}
