package com.mca.vehicleparking.dto;

public record LoginResponse(
        String message,
        AdminProfileResponse admin
) {
}
