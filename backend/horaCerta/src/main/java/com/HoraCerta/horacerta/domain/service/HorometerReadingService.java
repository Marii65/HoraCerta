package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.horometerreading.HorometerReadingRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.HorometerReadingResponseDTO;
import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.HorometerReading;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
import com.HoraCerta.horacerta.domain.repository.HorometerReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HorometerReadingService {
    private final HorometerReadingRepository readingRepository;
    private final EquipmentRepository equipmentRepository;
    private final SecurityContextService securityContextService;
    private final EquipmentService equipmentService;

    public HorometerReadingService(HorometerReadingRepository readingRepository,
                                   EquipmentRepository equipmentRepository,
                                   SecurityContextService securityContextService,
                                   EquipmentService equipmentService) {
        this.readingRepository = readingRepository;
        this.equipmentRepository = equipmentRepository;
        this.securityContextService = securityContextService;
        this.equipmentService = equipmentService;
    }

    @Transactional
    public HorometerReadingResponseDTO register(HorometerReadingRequestDTO dto) {
        Equipment equipment = equipmentRepository.findById(dto.equipmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + dto.equipmentId()
                ));

        if (dto.readingValue() == null
                || dto.readingValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Reading value cannot be null or negative."
            );
        }

        if (dto.readingValue().compareTo(equipment.getCurrentHorometer()) < 0) {
            throw new IllegalArgumentException(
                    "New reading value (" + dto.readingValue() +
                            ") cannot be less than the current equipment horometer (" +
                            equipment.getCurrentHorometer() + ")."
            );
        }

        User currentUser = securityContextService.getAuthenticatedUser();

        equipmentService.updateHorometer(
                equipment,
                dto.readingValue()
        );

        HorometerReading reading = new HorometerReading();
        reading.setEquipment(equipment);
        reading.setUser(currentUser);
        reading.setReadingValue(dto.readingValue());
        reading.setObservation(dto.observation());
        reading.setReadAt(LocalDateTime.now());

        HorometerReading savedReading = readingRepository.save(reading);

        return toResponseDTO(savedReading);
    }

    @Transactional(readOnly = true)
    public List<HorometerReadingResponseDTO> findByEquipmentId(Long equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + equipmentId);
        }

        return readingRepository.findByEquipment_IdOrderByReadAtDesc(equipmentId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HorometerReadingResponseDTO> findByEquipmentAndPeriod(Long equipmentId,
                                                                      LocalDateTime startDate,
                                                                      LocalDateTime endDate) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + equipmentId);
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Both start date and end date are required."
            );
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "The start date cannot be after the end date."
            );
        }

        return readingRepository.findByEquipment_IdAndReadAtBetweenOrderByReadAtDesc(
                        equipmentId, startDate, endDate
                )
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public HorometerReadingResponseDTO findById(Long id) {
        HorometerReading reading = readingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Horometer reading not found with id: " + id
                ));
        return toResponseDTO(reading);
    }

    private HorometerReadingResponseDTO toResponseDTO(HorometerReading reading) {
        return new HorometerReadingResponseDTO(
                reading.getId(),
                reading.getUser().getId(),
                reading.getUser().getUsername(),
                reading.getEquipment().getId(),
                reading.getEquipment().getCode(),
                reading.getEquipment().getName(),
                reading.getReadingValue(),
                reading.getReadAt(),
                reading.getObservation()
        );
    }
}
