package com.mca.vehicleparking.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleNumberUtilTest {

    @Test
    void shouldNormalizeVehicleNumberToUpperCaseWithoutSpaces() {
        assertEquals("MP09AB1234", VehicleNumberUtil.normalize(" mp09 ab1234 "));
        assertEquals("DL01-XY7788", VehicleNumberUtil.normalize("dl01-xy7788"));
    }

    @Test
    void shouldReturnNullWhenVehicleNumberIsNull() {
        assertNull(VehicleNumberUtil.normalize(null));
    }

    @Test
    void shouldValidateAllowedVehicleNumberInput() {
        assertTrue(VehicleNumberUtil.isValidRawInput("MH12 DE 3434"));
        assertFalse(VehicleNumberUtil.isValidRawInput("INVALID@@@"));
    }
}
