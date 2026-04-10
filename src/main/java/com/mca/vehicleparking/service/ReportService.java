package com.mca.vehicleparking.service;

import com.mca.vehicleparking.dto.DailyReportResponse;
import com.mca.vehicleparking.dto.RevenueReportResponse;
import com.mca.vehicleparking.repository.PaymentRepository;
import com.mca.vehicleparking.util.DateRangeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReportService {

    private final ParkingService parkingService;
    private final PaymentRepository paymentRepository;

    public ReportService(ParkingService parkingService, PaymentRepository paymentRepository) {
        this.parkingService = parkingService;
        this.paymentRepository = paymentRepository;
    }

    // Builds the daily parking report with records, completed exits, and revenue for one date.
    public DailyReportResponse getDailyReport(LocalDate date) {
        DateRangeUtil.DateRange reportRange = DateRangeUtil.forDate(date);

        var records = parkingService.findDailyRecords(reportRange.start(), reportRange.end());
        long completedExits = records.stream()
                .filter(record -> "COMPLETED".equals(record.status()))
                .count();

        return new DailyReportResponse(
                reportRange.date(),
                records.size(),
                completedExits,
                paymentRepository.sumAmountByPaidAtBetween(reportRange.start(), reportRange.end()),
                records
        );
    }

    // Builds a compact revenue summary for one date.
    public RevenueReportResponse getRevenueReport(LocalDate date) {
        DateRangeUtil.DateRange reportRange = DateRangeUtil.forDate(date);

        return new RevenueReportResponse(
                reportRange.date(),
                paymentRepository.countByPaidAtBetween(reportRange.start(), reportRange.end()),
                paymentRepository.sumAmountByPaidAtBetween(reportRange.start(), reportRange.end())
        );
    }
}
