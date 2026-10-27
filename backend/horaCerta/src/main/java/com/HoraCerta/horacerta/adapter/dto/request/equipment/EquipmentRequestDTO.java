package com.HoraCerta.horacerta.adapter.dto.request.equipment;

import jakarta.validation.constraints.*;


public record EquipmentRequestDTO (

        @NotBlank(message = "Code is mandatory")
        @Size(max = 50, message = "Code must be up to 50 characters")
        String code,

        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name must be up to 100 characters")
        String name,

        @Size(max = 50, message = "Model must be up to 50 characters")
        String model,

        @Size(max = 50, message = "Category must be up to 50 characters")
        String category,

        @Size(max = 50, message = "Serial number must be up to 50 characters")
        String serialNumber
){
}
