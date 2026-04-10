package com.mca.vehicleparking.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateRangeUtil {

    private DateRangeUtil() {
    }

    // Builds a simple inclusive day window so dashboard and reports use the same date boundaries.
    public static DateRange forDate(LocalDate requestedDate) {
        LocalDate date = requestedDate == null ? LocalDate.now() : requestedDate;
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusSeconds(1);
        return new DateRange(date, start, end);
    }

    public record DateRange(LocalDate date, LocalDateTime start, LocalDateTime end) {
    }
}
