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

/**
 * Gerencia o registro e a consulta das leituras de horímetro dos equipamentos.
 * Cada leitura é associada ao usuário autenticado e atualiza o horímetro atual
 * do equipamento.
 *
 * @author Maria Luiza
 */
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

    /**
     * Registra uma leitura de horímetro para um equipamento existente.
     * O valor informado não pode ser negativo nem inferior ao horímetro atual.
     *
     * @param dto dados da leitura, incluindo o equipamento e o valor registrado
     * @return dados da leitura cadastrada
     * @throws ResourceNotFoundException se o equipamento não existir
     * @throws IllegalArgumentException se o valor for nulo, negativo ou inferior
     * ao horímetro atual
     */
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

    /**
     * Consulta o histórico de leituras de um equipamento,
     * ordenado da mais recente para a mais antiga.
     *
     * @param equipmentId identificador do equipamento
     * @return lista de leituras encontradas
     * @throws ResourceNotFoundException se o equipamento não existir
     */
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

    /**
     * Consulta as leituras de um equipamento dentro de um período.
     * Os limites inicial e final são incluídos na consulta.
     *
     * @param equipmentId identificador do equipamento
     * @param startDate início do período
     * @param endDate fim do período
     * @return lista de leituras do período, da mais recente para a mais antiga
     * @throws ResourceNotFoundException se o equipamento não existir
     * @throws IllegalArgumentException se alguma data for nula ou o início
     * ocorrer depois do fim
     */
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

    /**
     * Consulta todas as leituras de horímetro cadastradas,
     * ordenadas da mais recente para a mais antiga.
     *
     * @return lista de leituras cadastradas
     */
    @Transactional(readOnly = true)
    public List<HorometerReadingResponseDTO> findAll() {

        return readingRepository
                .findAllByOrderByReadAtDesc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca uma leitura pelo identificador.
     *
     * @param id identificador da leitura
     * @return dados da leitura encontrada
     * @throws ResourceNotFoundException se a leitura não existir
     */
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
