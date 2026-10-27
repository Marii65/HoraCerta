package com.HoraCerta.horacerta.adapter.dto.response;

import com.HoraCerta.horacerta.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO (
        UUID id,
        String username,
        String email,
        Role role,
        Boolean active,
        LocalDateTime createdAt
){
}
