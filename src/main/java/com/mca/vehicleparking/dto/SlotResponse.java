package com.mca.vehicleparking.dto;

import com.mca.vehicleparking.model.SlotType;

public record SlotResponse(
        Long id,
        String slotNumber,
        SlotType slotType,
        boolean occupied
) {
}
