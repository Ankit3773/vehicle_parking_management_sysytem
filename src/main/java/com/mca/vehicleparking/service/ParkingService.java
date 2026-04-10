package com.mca.vehicleparking.service;

import com.mca.vehicleparking.dto.ParkingActionResponse;
import com.mca.vehicleparking.dto.ParkingRecordResponse;
import com.mca.vehicleparking.dto.SearchResponse;
import com.mca.vehicleparking.dto.VehicleEntryRequest;
import com.mca.vehicleparking.dto.VehicleExitRequest;
import com.mca.vehicleparking.exception.BadRequestException;
import com.mca.vehicleparking.exception.NotFoundException;
import com.mca.vehicleparking.model.ParkingRecord;
import com.mca.vehicleparking.model.ParkingRecordStatus;
import com.mca.vehicleparking.model.ParkingSlot;
import com.mca.vehicleparking.model.Payment;
import com.mca.vehicleparking.model.PaymentStatus;
import com.mca.vehicleparking.model.SlotType;
import com.mca.vehicleparking.model.Vehicle;
import com.mca.vehicleparking.model.VehicleType;
import com.mca.vehicleparking.repository.ParkingRecordRepository;
import com.mca.vehicleparking.repository.ParkingSlotRepository;
import com.mca.vehicleparking.repository.PaymentRepository;
import com.mca.vehicleparking.repository.VehicleRepository;
import com.mca.vehicleparking.util.FeeCalculatorUtil;
import com.mca.vehicleparking.util.VehicleNumberUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ParkingService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final PaymentRepository paymentRepository;

    public ParkingService(VehicleRepository vehicleRepository,
                          ParkingSlotRepository parkingSlotRepository,
                          ParkingRecordRepository parkingRecordRepository,
                          PaymentRepository paymentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingRecordRepository = parkingRecordRepository;
        this.paymentRepository = paymentRepository;
    }

    // Creates a new active parking record after validating duplicate parking and slot availability.
    @Transactional
    public ParkingActionResponse createVehicleEntry(VehicleEntryRequest request) {
        String vehicleNumber = validateAndNormalizeVehicleNumber(request.vehicleNumber());

        validateVehicleIsNotAlreadyParked(vehicleNumber);

        Vehicle vehicle = saveOrUpdateVehicle(request, vehicleNumber);
        ParkingSlot slot = findAvailableSlot(request.vehicleType());

        slot.setOccupied(true);
        parkingSlotRepository.saveAndFlush(slot);

        ParkingRecord record = new ParkingRecord();
        record.setVehicle(vehicle);
        record.setSlot(slot);
        record.setEntryTime(LocalDateTime.now());
        record.setStatus(ParkingRecordStatus.ACTIVE);
        record.setNotes(normalizeOptionalText(request.notes()));
        record = parkingRecordRepository.save(record);

        return new ParkingActionResponse(
                "Vehicle entry recorded and slot " + slot.getSlotNumber() + " assigned successfully.",
                toRecordResponse(record, null)
        );
    }

    // Completes an active parking record, calculates the fee, releases the slot, and stores payment details.
    @Transactional
    public ParkingActionResponse processVehicleExit(VehicleExitRequest request) {
        String vehicleNumber = validateAndNormalizeVehicleNumber(request.vehicleNumber());

        ParkingRecord record = findActiveParkingRecord(vehicleNumber);

        LocalDateTime exitTime = LocalDateTime.now();
        long durationMinutes = Math.max(1, ChronoUnit.MINUTES.between(record.getEntryTime(), exitTime));
        BigDecimal amount = FeeCalculatorUtil.calculate(durationMinutes);

        record.setExitTime(exitTime);
        record.setDurationMinutes(durationMinutes);
        record.setStatus(ParkingRecordStatus.COMPLETED);
        parkingRecordRepository.save(record);

        ParkingSlot slot = record.getSlot();
        slot.setOccupied(false);
        parkingSlotRepository.save(slot);

        Payment payment = paymentRepository.findByParkingRecordId(record.getId()).orElseGet(Payment::new);
        payment.setParkingRecord(record);
        payment.setDurationMinutes(durationMinutes);
        payment.setAmount(amount);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(exitTime);
        paymentRepository.save(payment);

        return new ParkingActionResponse(
                "Vehicle exit completed. Parking fee calculated successfully.",
                toRecordResponse(record, amount)
        );
    }

    // Returns the current and historical parking records for one vehicle number.
    @Transactional(readOnly = true)
    public SearchResponse searchByVehicleNumber(String rawVehicleNumber) {
        String vehicleNumber = validateAndNormalizeVehicleNumber(rawVehicleNumber);
        Vehicle vehicle = vehicleRepository.findByVehicleNumberIgnoreCase(vehicleNumber)
                .orElseThrow(() -> new NotFoundException("No vehicle record found for vehicle number " + vehicleNumber + "."));

        List<ParkingRecordResponse> records = parkingRecordRepository
                .findAllByVehicleVehicleNumberIgnoreCaseOrderByEntryTimeDesc(vehicleNumber)
                .stream()
                .map(record -> toRecordResponse(record, getAmount(record.getId())))
                .toList();

        return new SearchResponse(
                vehicle.getVehicleNumber(),
                vehicle.getOwnerName(),
                vehicle.getVehicleType(),
                vehicle.getColor(),
                records
        );
    }

    // Used by the reporting module to fetch all records for a given day.
    @Transactional(readOnly = true)
    public List<ParkingRecordResponse> findDailyRecords(LocalDateTime start, LocalDateTime end) {
        return parkingRecordRepository.findAllByEntryTimeBetweenOrderByEntryTimeDesc(start, end)
                .stream()
                .map(record -> toRecordResponse(record, getAmount(record.getId())))
                .toList();
    }

    // Returns all currently active parking records to support slot monitoring and exit processing.
    @Transactional(readOnly = true)
    public List<ParkingRecordResponse> findActiveRecords() {
        return parkingRecordRepository.findAllByStatusOrderByEntryTimeDesc(ParkingRecordStatus.ACTIVE)
                .stream()
                .map(record -> toRecordResponse(record, null))
                .toList();
    }

    private void validateVehicleIsNotAlreadyParked(String vehicleNumber) {
        parkingRecordRepository.findByVehicleVehicleNumberIgnoreCaseAndStatus(vehicleNumber, ParkingRecordStatus.ACTIVE)
                .ifPresent(existing -> {
                    throw new BadRequestException("Vehicle is already parked in slot " + existing.getSlot().getSlotNumber() + ".");
                });
    }

    private String validateAndNormalizeVehicleNumber(String rawVehicleNumber) {
        if (!VehicleNumberUtil.isValidRawInput(rawVehicleNumber)) {
            throw new BadRequestException("Vehicle number may contain only letters, numbers, spaces, and hyphens.");
        }
        return VehicleNumberUtil.normalize(rawVehicleNumber);
    }

    private Vehicle saveOrUpdateVehicle(VehicleEntryRequest request, String vehicleNumber) {
        Vehicle vehicle = vehicleRepository.findByVehicleNumberIgnoreCase(vehicleNumber)
                .orElseGet(Vehicle::new);

        vehicle.setVehicleNumber(vehicleNumber);
        vehicle.setOwnerName(request.ownerName().trim());
        vehicle.setVehicleType(request.vehicleType());
        vehicle.setColor(request.color().trim());

        try {
            return vehicleRepository.save(vehicle);
        } catch (DataIntegrityViolationException exception) {
            throw new BadRequestException("Vehicle entry is already being processed for this vehicle. Please refresh and try again.");
        }
    }

    private ParkingSlot findAvailableSlot(VehicleType vehicleType) {
        SlotType slotType = SlotType.valueOf(vehicleType.name());
        return parkingSlotRepository.findFirstBySlotTypeAndOccupiedFalseAndActiveTrueOrderBySlotNumberAsc(slotType)
                .orElseThrow(() -> new BadRequestException("No vacant slot is available for " + vehicleType.name().toLowerCase() + "."));
    }

    private ParkingRecord findActiveParkingRecord(String vehicleNumber) {
        return parkingRecordRepository.findByVehicleVehicleNumberIgnoreCaseAndStatus(vehicleNumber, ParkingRecordStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("No active parking record found for vehicle number " + vehicleNumber + "."));
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private BigDecimal getAmount(Long parkingRecordId) {
        return paymentRepository.findByParkingRecordId(parkingRecordId)
                .map(Payment::getAmount)
                .orElse(null);
    }

    private ParkingRecordResponse toRecordResponse(ParkingRecord record, BigDecimal amount) {
        return new ParkingRecordResponse(
                record.getId(),
                record.getVehicle().getVehicleNumber(),
                record.getVehicle().getOwnerName(),
                record.getVehicle().getVehicleType(),
                record.getVehicle().getColor(),
                record.getSlot().getSlotNumber(),
                record.getEntryTime(),
                record.getExitTime(),
                record.getStatus().name(),
                record.getDurationMinutes(),
                amount
        );
    }
}
