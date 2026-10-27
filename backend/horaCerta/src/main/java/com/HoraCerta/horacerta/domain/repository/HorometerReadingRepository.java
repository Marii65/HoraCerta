package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.HorometerReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HorometerReadingRepository extends JpaRepository<HorometerReading, Long> {

    List<HorometerReading> findByEquipment_IdOrderByReadAtDesc(Long equipmentId);

    List<HorometerReading> findByEquipment_IdAndReadAtBetweenOrderByReadAtDesc(
            Long equipmentId, LocalDateTime startDate, LocalDateTime endDate
    );

    boolean existsByEquipment_Id(Long equipmentId);
}
