package com.mca.vehicleparking.controller;

import com.mca.vehicleparking.dto.DailyReportResponse;
import com.mca.vehicleparking.dto.RevenueReportResponse;
import com.mca.vehicleparking.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily")
    public DailyReportResponse getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return reportService.getDailyReport(date);
    }

    @GetMapping("/revenue")
    public RevenueReportResponse getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return reportService.getRevenueReport(date);
    }
}
