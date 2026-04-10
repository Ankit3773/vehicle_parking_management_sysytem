package com.mca.vehicleparking.repository;

import com.mca.vehicleparking.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNumberIgnoreCase(String vehicleNumber);
}
