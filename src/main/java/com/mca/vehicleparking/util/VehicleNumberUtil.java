package com.mca.vehicleparking.util;

import java.util.regex.Pattern;

public final class VehicleNumberUtil {

    private static final Pattern RAW_INPUT_PATTERN = Pattern.compile("^[A-Za-z0-9 -]{4,20}$");

    private VehicleNumberUtil() {
    }

    public static String normalize(String vehicleNumber) {
        return vehicleNumber == null ? null : vehicleNumber.trim().toUpperCase().replaceAll("\\s+", "");
    }

    public static boolean isValidRawInput(String vehicleNumber) {
        if (vehicleNumber == null) {
            return false;
        }
        return RAW_INPUT_PATTERN.matcher(vehicleNumber.trim()).matches();
    }
}
