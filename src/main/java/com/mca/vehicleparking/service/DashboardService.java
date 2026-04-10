package com.mca.vehicleparking.service;

import com.mca.vehicleparking.dto.DashboardResponse;
import com.mca.vehicleparking.repository.ParkingRecordRepository;
import com.mca.vehicleparking.repository.ParkingSlotRepository;
import com.mca.vehicleparking.repository.PaymentRepository;
import com.mca.vehicleparking.util.DateRangeUtil;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(ParkingSlotRepository parkingSlotRepository,
                            ParkingRecordRepository parkingRecordRepository,
                            PaymentRepository paymentRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingRecordRepository = parkingRecordRepository;
        this.paymentRepository = paymentRepository;
    }

    // Aggregates the key numbers shown on the admin dashboard.
    public DashboardResponse getSummary() {
        DateRangeUtil.DateRange today = DateRangeUtil.forDate(null);

        return new DashboardResponse(
                parkingSlotRepository.countByActiveTrue(),
                parkingSlotRepository.countByActiveTrueAndOccupiedTrue(),
                parkingSlotRepository.countByActiveTrueAndOccupiedFalse(),
                parkingRecordRepository.countByEntryTimeBetween(today.start(), today.end()),
                paymentRepository.sumAmountByPaidAtBetween(today.start(), today.end())
        );
    }
}
