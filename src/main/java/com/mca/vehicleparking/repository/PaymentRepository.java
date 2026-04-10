package com.mca.vehicleparking.repository;

import com.mca.vehicleparking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByParkingRecordId(Long parkingRecordId);

    long countByPaidAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paidAt between :start and :end")
    BigDecimal sumAmountByPaidAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
