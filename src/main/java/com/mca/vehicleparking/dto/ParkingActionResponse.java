package com.mca.vehicleparking.dto;

public record ParkingActionResponse(
        String message,
        ParkingRecordResponse record
) {
}
