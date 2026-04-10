package com.mca.vehicleparking.dto;

public record AdminProfileResponse(
        Long id,
        String username,
        String fullName
) {
}
