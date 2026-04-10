package com.mca.vehicleparking.util;

import java.math.BigDecimal;

public final class FeeCalculatorUtil {

    private static final BigDecimal FIRST_HOUR_RATE = BigDecimal.valueOf(20);
    private static final BigDecimal ADDITIONAL_HOUR_RATE = BigDecimal.valueOf(10);

    private FeeCalculatorUtil() {
    }

    // The first hour costs Rs. 20. Any extra started hour costs Rs. 10.
    public static BigDecimal calculate(long durationMinutes) {
        if (durationMinutes <= 60) {
            return FIRST_HOUR_RATE;
        }

        long remainingMinutes = durationMinutes - 60;
        long additionalHours = (long) Math.ceil(remainingMinutes / 60.0);
        return FIRST_HOUR_RATE.add(ADDITIONAL_HOUR_RATE.multiply(BigDecimal.valueOf(additionalHours)));
    }
}
