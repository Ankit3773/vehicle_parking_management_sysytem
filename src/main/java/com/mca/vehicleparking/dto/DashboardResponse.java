package com.mca.vehicleparking.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        long totalSlots,
        long occupiedSlots,
        long vacantSlots,
        long todayVehicleCount,
        BigDecimal todayRevenue
) {
}
