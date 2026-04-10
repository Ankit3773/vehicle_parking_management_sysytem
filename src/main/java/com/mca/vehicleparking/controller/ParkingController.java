package com.mca.vehicleparking.controller;

import com.mca.vehicleparking.dto.ParkingActionResponse;
import com.mca.vehicleparking.dto.SearchResponse;
import com.mca.vehicleparking.dto.VehicleEntryRequest;
import com.mca.vehicleparking.dto.VehicleExitRequest;
import com.mca.vehicleparking.dto.ParkingRecordResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.mca.vehicleparking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/entry")
    public ParkingActionResponse createEntry(@Valid @RequestBody VehicleEntryRequest request) {
        return parkingService.createVehicleEntry(request);
    }

    @PostMapping("/exit")
    public ParkingActionResponse processExit(@Valid @RequestBody VehicleExitRequest request) {
        return parkingService.processVehicleExit(request);
    }

    @GetMapping("/active")
    public List<ParkingRecordResponse> getActiveRecords() {
        return parkingService.findActiveRecords();
    }

    @GetMapping("/search")
    public SearchResponse searchVehicle(
            @RequestParam
            @NotBlank(message = "Vehicle number is required.")
            @Size(max = 20, message = "Vehicle number must be within 20 characters.")
            @Pattern(
                    regexp = "^[A-Za-z0-9 -]{4,20}$",
                    message = "Vehicle number may contain only letters, numbers, spaces, and hyphens."
            )
            String vehicleNumber) {
        return parkingService.searchByVehicleNumber(vehicleNumber);
    }
}
