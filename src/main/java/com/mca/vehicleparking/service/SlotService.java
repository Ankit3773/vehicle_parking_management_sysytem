package com.mca.vehicleparking.service;

import com.mca.vehicleparking.dto.SlotResponse;
import com.mca.vehicleparking.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotService {

    private final ParkingSlotRepository parkingSlotRepository;

    public SlotService(ParkingSlotRepository parkingSlotRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
    }

    // Returns all active parking slots in display order for the slot status page.
    public List<SlotResponse> getAllSlots() {
        return parkingSlotRepository.findAllByActiveTrueOrderBySlotNumberAsc()
                .stream()
                .map(slot -> new SlotResponse(slot.getId(), slot.getSlotNumber(), slot.getSlotType(), Boolean.TRUE.equals(slot.getOccupied())))
                .toList();
    }
}
