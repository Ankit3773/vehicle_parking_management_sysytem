package com.mca.vehicleparking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleExitRequest(
        @NotBlank(message = "Vehicle number is required.")
        @Size(max = 20, message = "Vehicle number must be within 20 characters.")
        @Pattern(
                regexp = "^[A-Za-z0-9 -]{4,20}$",
                message = "Vehicle number may contain only letters, numbers, spaces, and hyphens."
        )
        String vehicleNumber
) {
}
