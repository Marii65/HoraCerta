package com.HoraCerta.horacerta.adapter.dto.response;

import com.HoraCerta.horacerta.domain.enums.Role;

import java.util.UUID;

public record LoginResponseDTO (
        String token,
        String tokenType,
        UUID userId,
        String username,
        String email,
        Role role
) {
}
