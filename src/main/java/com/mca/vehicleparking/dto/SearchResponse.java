package com.mca.vehicleparking.dto;

import com.mca.vehicleparking.model.VehicleType;

import java.util.List;

public record SearchResponse(
        String vehicleNumber,
        String ownerName,
        VehicleType vehicleType,
        String color,
        List<ParkingRecordResponse> records
) {
}
