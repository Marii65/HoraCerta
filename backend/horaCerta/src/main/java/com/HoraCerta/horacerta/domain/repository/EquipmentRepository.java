package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.enums.equipment.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment,Long> {

    Optional<Equipment> findByCategoryAndSerialNumber(String category, String serialNumber);

    Optional<Equipment> findByCode(String code);

    boolean existsByCode(String code);

    List<Equipment> findByNameContainingIgnoreCase(String name);

    List<Equipment> findByStatus(EquipmentStatus status);

    List<Equipment> findByCategory(String category);

    List<Equipment> findByStatusAndCategory(EquipmentStatus status, String category);
}
