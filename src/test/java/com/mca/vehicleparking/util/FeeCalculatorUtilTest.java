package com.mca.vehicleparking.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeCalculatorUtilTest {

    @Test
    void shouldChargeTwentyForFirstHourOrLess() {
        assertEquals(BigDecimal.valueOf(20), FeeCalculatorUtil.calculate(1));
        assertEquals(BigDecimal.valueOf(20), FeeCalculatorUtil.calculate(60));
    }

    @Test
    void shouldChargeThirtyForOneAdditionalStartedHour() {
        assertEquals(BigDecimal.valueOf(30), FeeCalculatorUtil.calculate(61));
        assertEquals(BigDecimal.valueOf(30), FeeCalculatorUtil.calculate(120));
    }

    @Test
    void shouldChargeFortyForTwoAdditionalStartedHours() {
        assertEquals(BigDecimal.valueOf(40), FeeCalculatorUtil.calculate(121));
    }
}
