package com.mca.vehicleparking.controller;

import com.mca.vehicleparking.dto.SlotResponse;
import com.mca.vehicleparking.service.SlotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<SlotResponse> getAllSlots() {
        return slotService.getAllSlots();
    }
}
