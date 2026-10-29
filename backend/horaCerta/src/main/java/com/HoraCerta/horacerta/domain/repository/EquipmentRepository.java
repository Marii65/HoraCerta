package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Fornece operações de persistência e consultas para equipamentos,
 * incluindo filtros e atualização condicional do horímetro.
 *
 * @author Maria Luiza
 */
public interface EquipmentRepository extends JpaRepository<Equipment,Long> {

    Optional<Equipment> findByCategoryAndSerialNumber(String category, String serialNumber);

    Optional<Equipment> findByCode(String code);

    boolean existsByCode(String code);

    List<Equipment> findByNameContainingIgnoreCase(String name);

    List<Equipment> findByStatus(EquipmentStatus status);

    List<Equipment> findByCategoryIgnoreCase(String category);

    List<Equipment> findByStatusAndCategoryIgnoreCase(EquipmentStatus status, String category);

    /**
     * Atualiza o horímetro somente quando o valor armazenado
     * for menor ou igual ao novo valor informado.
     *
     * @param id identificador do equipamento
     * @param newHorometer novo valor do horímetro
     * @return quantidade de registros atualizados
     */
    @Modifying
    @Query("UPDATE Equipment e SET e.currentHorometer = :newHorometer WHERE e.id = :id AND e.currentHorometer <= :newHorometer")
    int updateHorometerIfGreater(@Param("id") Long id, @Param("newHorometer") BigDecimal newHorometer);
}
