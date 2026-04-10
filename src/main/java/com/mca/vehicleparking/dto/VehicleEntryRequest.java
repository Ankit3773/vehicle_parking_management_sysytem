package com.mca.vehicleparking.dto;

import com.mca.vehicleparking.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleEntryRequest(
        @NotBlank(message = "Vehicle number is required.")
        @Size(max = 20, message = "Vehicle number must be within 20 characters.")
        @Pattern(
                regexp = "^[A-Za-z0-9 -]{4,20}$",
                message = "Vehicle number may contain only letters, numbers, spaces, and hyphens."
        )
        String vehicleNumber,

        @NotBlank(message = "Owner name is required.")
        @Size(max = 100, message = "Owner name must be within 100 characters.")
        String ownerName,

        @NotNull(message = "Vehicle type is required.")
        VehicleType vehicleType,

        @NotBlank(message = "Vehicle color is required.")
        @Size(max = 30, message = "Vehicle color must be within 30 characters.")
        String color,

        @Size(max = 255, message = "Notes must be within 255 characters.")
        String notes
) {
}
