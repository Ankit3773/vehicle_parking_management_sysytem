package com.mca.vehicleparking.repository;

import com.mca.vehicleparking.model.ParkingRecord;
import com.mca.vehicleparking.model.ParkingRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {

    Optional<ParkingRecord> findByVehicleVehicleNumberIgnoreCaseAndStatus(String vehicleNumber, ParkingRecordStatus status);

    List<ParkingRecord> findAllByVehicleVehicleNumberIgnoreCaseOrderByEntryTimeDesc(String vehicleNumber);

    List<ParkingRecord> findAllByStatusOrderByEntryTimeDesc(ParkingRecordStatus status);

    long countByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    List<ParkingRecord> findAllByEntryTimeBetweenOrderByEntryTimeDesc(LocalDateTime start, LocalDateTime end);
}
