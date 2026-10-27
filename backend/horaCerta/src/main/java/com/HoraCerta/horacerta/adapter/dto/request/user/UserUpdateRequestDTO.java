package com.HoraCerta.horacerta.adapter.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name must be up to 100 characters")
        String username,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must be up to 150 characters")
        String email
) {
}
