package com.HoraCerta.horacerta.adapter.dto.response;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record HorometerReadingResponseDTO(
        Long id,
        UUID userId,
        String username,
        Long equipmentId,
        String equipmentCode,
        String equipmentName,
        BigDecimal readingValue,
        LocalDateTime readAt,
        String observation
) {
}
