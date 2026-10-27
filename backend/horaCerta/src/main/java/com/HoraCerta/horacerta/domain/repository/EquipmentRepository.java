package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.enums.equipment.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    //fará o update somente se o valor atual armazenado no banco for menor do que a nova leitura.
    @Modifying
    @Query("UPDATE Equipment e SET e.currentHorometer = :newHorometer WHERE e.id = :id AND e.currentHorometer <= :newHorometer")
    int updateHorometerIfGreater(@Param("id") Long id, @Param("newHorometer") BigDecimal newHorometer);
}
