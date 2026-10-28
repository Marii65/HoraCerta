package com.HoraCerta.horacerta.adapter.dto.request.user;

import com.HoraCerta.horacerta.domain.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequestDTO(
        @NotNull(message = "Role is mandatory.")
        Role role
) {

}
