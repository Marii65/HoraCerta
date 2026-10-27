package com.HoraCerta.horacerta.adapter.dto.response;

import com.HoraCerta.horacerta.domain.enums.equipment.EquipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EquipmentResponseDTO(
        Long id,
        String code,
        String name,
        String model,
        String category,
        String serialNumber,
        BigDecimal currentHorometer,
        EquipmentStatus status,
        LocalDateTime createdAt
) {
}
