package com.mca.vehicleparking.dto;

import com.mca.vehicleparking.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingRecordResponse(
        Long recordId,
        String vehicleNumber,
        String ownerName,
        VehicleType vehicleType,
        String color,
        String slotNumber,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        String status,
        Long durationMinutes,
        BigDecimal amount
) {
}
