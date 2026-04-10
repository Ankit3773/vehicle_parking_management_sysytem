package com.mca.vehicleparking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailyReportResponse(
        LocalDate reportDate,
        long totalEntries,
        long completedExits,
        BigDecimal totalRevenue,
        List<ParkingRecordResponse> records
) {
}
