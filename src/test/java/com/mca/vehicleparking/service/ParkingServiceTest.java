package com.mca.vehicleparking.service;

import com.mca.vehicleparking.dto.ParkingActionResponse;
import com.mca.vehicleparking.dto.VehicleEntryRequest;
import com.mca.vehicleparking.dto.VehicleExitRequest;
import com.mca.vehicleparking.exception.BadRequestException;
import com.mca.vehicleparking.model.ParkingRecord;
import com.mca.vehicleparking.model.ParkingRecordStatus;
import com.mca.vehicleparking.model.ParkingSlot;
import com.mca.vehicleparking.model.Payment;
import com.mca.vehicleparking.model.SlotType;
import com.mca.vehicleparking.model.Vehicle;
import com.mca.vehicleparking.model.VehicleType;
import com.mca.vehicleparking.repository.ParkingRecordRepository;
import com.mca.vehicleparking.repository.ParkingSlotRepository;
import com.mca.vehicleparking.repository.PaymentRepository;
import com.mca.vehicleparking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private ParkingRecordRepository parkingRecordRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ParkingService parkingService;

    private ParkingSlot availableCarSlot;

    @BeforeEach
    void setUp() {
        availableCarSlot = new ParkingSlot();
        availableCarSlot.setId(1L);
        availableCarSlot.setSlotNumber("C-01");
        availableCarSlot.setSlotType(SlotType.CAR);
        availableCarSlot.setOccupied(false);
        availableCarSlot.setActive(true);
    }

    @Test
    void createVehicleEntry_assignsFirstVacantMatchingSlot() {
        VehicleEntryRequest request = new VehicleEntryRequest(
                "mh12 de 3434",
                "Rahul Deshmukh",
                VehicleType.CAR,
                "Silver",
                "Academic demo vehicle"
        );

        when(parkingRecordRepository.findByVehicleVehicleNumberIgnoreCaseAndStatus("MH12DE3434", ParkingRecordStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(vehicleRepository.findByVehicleNumberIgnoreCase("MH12DE3434")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle vehicle = invocation.getArgument(0);
            vehicle.setId(10L);
            return vehicle;
        });
        when(parkingSlotRepository.findFirstBySlotTypeAndOccupiedFalseAndActiveTrueOrderBySlotNumberAsc(SlotType.CAR))
                .thenReturn(Optional.of(availableCarSlot));
        when(parkingSlotRepository.saveAndFlush(any(ParkingSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenAnswer(invocation -> {
            ParkingRecord record = invocation.getArgument(0);
            record.setId(99L);
            return record;
        });

        ParkingActionResponse response = parkingService.createVehicleEntry(request);

        assertTrue(availableCarSlot.getOccupied());
        assertEquals("C-01", response.record().slotNumber());
        assertEquals("MH12DE3434", response.record().vehicleNumber());

        ArgumentCaptor<ParkingRecord> recordCaptor = ArgumentCaptor.forClass(ParkingRecord.class);
        verify(parkingRecordRepository).save(recordCaptor.capture());
        ParkingRecord savedRecord = recordCaptor.getValue();
        assertEquals(ParkingRecordStatus.ACTIVE, savedRecord.getStatus());
        assertEquals("Academic demo vehicle", savedRecord.getNotes());
        assertEquals("C-01", savedRecord.getSlot().getSlotNumber());
    }

    @Test
    void processVehicleExit_releasesSlotAndCreatesPayment() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(7L);
        vehicle.setVehicleNumber("MH12DE3434");
        vehicle.setOwnerName("Rahul Deshmukh");
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setColor("Silver");

        ParkingRecord activeRecord = new ParkingRecord();
        activeRecord.setId(12L);
        activeRecord.setVehicle(vehicle);
        activeRecord.setSlot(availableCarSlot);
        activeRecord.setEntryTime(LocalDateTime.now().minusMinutes(65));
        activeRecord.setStatus(ParkingRecordStatus.ACTIVE);
        availableCarSlot.setOccupied(true);

        when(parkingRecordRepository.findByVehicleVehicleNumberIgnoreCaseAndStatus("MH12DE3434", ParkingRecordStatus.ACTIVE))
                .thenReturn(Optional.of(activeRecord));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.findByParkingRecordId(12L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParkingActionResponse response = parkingService.processVehicleExit(new VehicleExitRequest("MH12DE3434"));

        assertEquals("COMPLETED", response.record().status());
        assertEquals(new BigDecimal("30"), response.record().amount());
        assertFalse(availableCarSlot.getOccupied());
        assertNotNull(activeRecord.getExitTime());
        assertTrue(activeRecord.getDurationMinutes() >= 65);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(new BigDecimal("30"), savedPayment.getAmount());
        assertEquals(activeRecord.getId(), savedPayment.getParkingRecord().getId());
    }

    @Test
    void createVehicleEntry_shouldReturnBadRequestWhenVehicleInsertHitsUniqueConstraint() {
        VehicleEntryRequest request = new VehicleEntryRequest(
                "MH12DE5555",
                "Final Demo",
                VehicleType.CAR,
                "Blue",
                "Duplicate click"
        );

        when(parkingRecordRepository.findByVehicleVehicleNumberIgnoreCaseAndStatus("MH12DE5555", ParkingRecordStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(vehicleRepository.findByVehicleNumberIgnoreCase("MH12DE5555")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> parkingService.createVehicleEntry(request));
        assertEquals("Vehicle entry is already being processed for this vehicle. Please refresh and try again.", exception.getMessage());
    }
}
