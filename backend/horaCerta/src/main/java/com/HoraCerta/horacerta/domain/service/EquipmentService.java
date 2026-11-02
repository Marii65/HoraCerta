package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.EquipmentResponseDTO;
import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import com.HoraCerta.horacerta.domain.exception.DuplicateEquipmentCodeException;
import com.HoraCerta.horacerta.domain.exception.EquipmentHasHistoryException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.HorometerReadingRepository;
import com.HoraCerta.horacerta.domain.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Gerencia o cadastro, a consulta, a atualização e a exclusão de equipamentos,
 * incluindo a atualização do horímetro.
 *
 * @author Maria Luiza
 */
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

    /**
     * Cadastra um equipamento com código normalizado, horímetro inicial zero
     * e status ativo.
     *
     * @param dto dados do equipamento
     * @return dados do equipamento cadastrado
     * @throws DuplicateEquipmentCodeException se o código já estiver em uso
     */
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

    /**
     * Lista todos os equipamentos cadastrados.
     *
     * @return lista de equipamentos
     */
    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> findAll() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca um equipamento pelo identificador.
     *
     * @param id identificador do equipamento
     * @return dados do equipamento encontrado
     * @throws ResourceNotFoundException se o equipamento não existir
     */
    @Transactional(readOnly = true)
    public EquipmentResponseDTO findById(Long id) {
        Equipment equipment = getEquipmentOrThrow(id);
        return toResponseDTO(equipment);
    }

    /**
     * Filtra equipamentos pelo status, pela categoria ou por ambos.
     * Quando nenhum filtro válido é informado, retorna todos os equipamentos.
     *
     * @param status status pelo qual filtrar, podendo ser nulo
     * @param category categoria pela qual filtrar, podendo ser nula ou vazia
     * @return lista de equipamentos correspondentes aos filtros
     */
    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> filter(EquipmentStatus status, String category) {
        List<Equipment> equipments;

        if (status != null && category != null && !category.isBlank()) {
            equipments = equipmentRepository.findByStatusAndCategoryIgnoreCase(status, category.trim());
        } else if (status != null) {
            equipments = equipmentRepository.findByStatus(status);
        } else if (category != null && !category.isBlank()) {
            equipments = equipmentRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            equipments = equipmentRepository.findAll();
        }

        return equipments.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Atualiza os dados cadastrais de um equipamento.
     * O horímetro não é alterado por este método.
     *
     * @param id identificador do equipamento
     * @param dto novos dados cadastrais
     * @return dados atualizados do equipamento
     * @throws ResourceNotFoundException se o equipamento não existir
     * @throws DuplicateEquipmentCodeException se o novo código pertencer a outro equipamento
     */
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

    /**
     * Exclui um equipamento somente quando não existem leituras,
     * vínculos com planos ou ordens de serviço associados.
     *
     * @param id identificador do equipamento
     * @throws ResourceNotFoundException se o equipamento não existir
     * @throws EquipmentHasHistoryException se houver histórico ou vínculos associados
     */
    @Transactional
    public void delete(Long id) {
        Equipment equipment = getEquipmentOrThrow(id);

        boolean hasReadings = horometerReadingRepository.existsByEquipment_Id(id);
        boolean hasPlans = equipmentPlanRepository.existsByEquipment_Id(id);
        boolean hasWorkOrders = workOrderRepository.existsByEquipmentPlan_Equipment_Id(id);

        if (hasReadings || hasPlans || hasWorkOrders) {
            throw new EquipmentHasHistoryException(
                    "Cannot delete equipment because it has associated history or active links."
            );
        }

        equipmentRepository.delete(equipment);
    }

    /**
     * Atualiza o horímetro do equipamento por meio de uma operação condicional
     * no banco de dados, evitando sobrescrever uma atualização concorrente.
     *
     * @param equipment equipamento cujo horímetro será atualizado
     * @param newHorometer novo valor do horímetro
     * @throws IllegalArgumentException se o novo valor for nulo ou negativo
     * @throws IllegalStateException se o horímetro atual não estiver inicializado
     * ou se a atualização condicional não modificar nenhuma linha
     */
    @Transactional
    public void updateHorometer(Equipment equipment, BigDecimal newHorometer) {
        if (newHorometer == null || newHorometer.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Horometer cannot be null or negative."
            );
        }

        BigDecimal currentHorometer = equipment.getCurrentHorometer();

        if (currentHorometer == null) {
            throw new IllegalStateException(
                    "Equipment current horometer is not initialized."
            );
        }

        int rowsUpdated = equipmentRepository.updateHorometerIfGreater(
                equipment.getId(),
                newHorometer
        );

        if (rowsUpdated == 0) {
            throw new IllegalStateException(
                    "The equipment horometer was updated concurrently. Please retry."
            );
        }

        equipment.setCurrentHorometer(newHorometer);
    }

    private EquipmentResponseDTO toResponseDTO(Equipment equipment) {
        return new EquipmentResponseDTO(
                equipment.getId(),
                equipment.getCode(),
                equipment.getName(),
                equipment.getModel(),
                equipment.getCategory(),
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
