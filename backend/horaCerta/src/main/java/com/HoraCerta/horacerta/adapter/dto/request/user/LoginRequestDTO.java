package com.HoraCerta.horacerta.adapter.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must be up to 150 characters")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(max = 72, message = "Password must be up to 72 characters")
        String password
) {
}
