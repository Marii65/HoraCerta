package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.EquipmentResponseDTO;
import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.enums.equipment.EquipmentStatus;
import com.HoraCerta.horacerta.domain.exception.DuplicateEquipmentCodeException;
import com.HoraCerta.horacerta.domain.exception.EquipmentHasHistoryException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final HorometerReadingRepository horometerReadingRepository;
    private final EquipmentPlanRepository equipmentPlanRepository;
    private final WorkOrderRepository workOrderRepository;

    public EquipmentService(EquipmentRepository equipmentRepository,
                            HorometerReadingRepository horometerReadingRepository,
                            EquipmentPlanRepository equipmentPlanRepository,
                            WorkOrderRepository workOrderRepository) {
        this.equipmentRepository = equipmentRepository;
        this.horometerReadingRepository = horometerReadingRepository;
        this.equipmentPlanRepository = equipmentPlanRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public EquipmentResponseDTO register(EquipmentRequestDTO dto) {
        String code = normalizeCode(dto.code());

        if (equipmentRepository.existsByCode(code)) {
            throw new DuplicateEquipmentCodeException(
                    "Equipment with code '" + code + "' already exists."
            );
        }

        Equipment equipment = new Equipment();
        equipment.setCode(code);
        equipment.setName(dto.name().trim());
        equipment.setCategory(normalizeOptionalText(dto.category()));
        equipment.setModel(normalizeOptionalText(dto.model()));
        equipment.setSerialNumber(
                normalizeOptionalText(dto.serialNumber())
        );
        equipment.setCurrentHorometer(BigDecimal.ZERO);
        equipment.setStatus(EquipmentStatus.ACTIVE);

        Equipment savedEquipment = equipmentRepository.save(equipment);

        return toResponseDTO(savedEquipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> findAll() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponseDTO findById(Long id) {
        Equipment equipment = getEquipmentOrThrow(id);
        return toResponseDTO(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> filter(EquipmentStatus status, String category) {
        List<Equipment> equipments;

        if (status != null && category != null && !category.isBlank()) {
            equipments = equipmentRepository.findByStatusAndCategory(status, category.trim());
        } else if (status != null) {
            equipments = equipmentRepository.findByStatus(status);
        } else if (category != null && !category.isBlank()) {
            equipments = equipmentRepository.findByCategory(category.trim());
        } else {
            equipments = equipmentRepository.findAll();
        }

        return equipments.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public EquipmentResponseDTO update(Long id, EquipmentUpdateRequestDTO dto) {
        Equipment equipment = getEquipmentOrThrow(id);
        String code = normalizeCode(dto.code());

        if (!equipment.getCode().equals(code) && equipmentRepository.existsByCode(code)) {
            throw new DuplicateEquipmentCodeException(
                    "Equipment code '" + code + "' is already in use."
            );
        }

        equipment.setCode(code);
        equipment.setName(dto.name().trim());
        equipment.setCategory(normalizeOptionalText(dto.category()));
        equipment.setModel(normalizeOptionalText(dto.model()));
        equipment.setSerialNumber(
                normalizeOptionalText(dto.serialNumber())
        );

        if (dto.status() != null) {
            equipment.setStatus(dto.status());
        }


        Equipment updatedEquipment = equipmentRepository.save(equipment);

        return toResponseDTO(updatedEquipment);
    }

    @Transactional
    public void delete(Long id) {
        Equipment equipment = getEquipmentOrThrow(id);

        boolean hasReadings = horometerReadingRepository.existsByEquipmentId(id);
        boolean hasPlans = equipmentPlanRepository.existsByEquipmentId(id);
        boolean hasWorkOrders = workOrderRepository.existsByEquipmentPlan_Equipment_Id(id);

        if (hasReadings || hasPlans || hasWorkOrders) {
            throw new EquipmentHasHistoryException(
                    "Cannot delete equipment because it has associated history or active links."
            );
        }

        equipmentRepository.delete(equipment);
    }

    private EquipmentResponseDTO toResponseDTO(Equipment equipment) {
        return new EquipmentResponseDTO(
                equipment.getId(),
                equipment.getCode(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getModel(),
                equipment.getSerialNumber(),
                equipment.getCurrentHorometer(),
                equipment.getStatus(),
                equipment.getCreatedAt()
        );
    }

    private Equipment getEquipmentOrThrow(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Equipment not found with id: " + id
                        )
                );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
