package com.mca.vehicleparking.repository;

import com.mca.vehicleparking.model.ParkingSlot;
import com.mca.vehicleparking.model.SlotType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ParkingSlot> findFirstBySlotTypeAndOccupiedFalseAndActiveTrueOrderBySlotNumberAsc(SlotType slotType);

    long countByActiveTrue();

    long countByActiveTrueAndOccupiedTrue();

    long countByActiveTrueAndOccupiedFalse();

    List<ParkingSlot> findAllByActiveTrueOrderBySlotNumberAsc();
}
