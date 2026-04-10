package com.mca.vehicleparking.repository;

import com.mca.vehicleparking.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsernameIgnoreCaseAndActiveTrue(String username);
}
