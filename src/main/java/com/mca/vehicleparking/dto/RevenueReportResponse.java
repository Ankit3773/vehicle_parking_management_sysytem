package com.mca.vehicleparking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueReportResponse(
        LocalDate reportDate,
        long totalPayments,
        BigDecimal totalRevenue
) {
}
