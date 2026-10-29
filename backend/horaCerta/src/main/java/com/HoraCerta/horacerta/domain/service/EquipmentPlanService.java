package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.equipmentplan.EquipmentPlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.EquipmentPlanResponseDTO;
import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.EquipmentPlan;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.exception.EquipmentPlanAlreadyActiveException;
import com.HoraCerta.horacerta.domain.exception.InactiveMaintenancePlanException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Gerencia os vínculos entre equipamentos e planos de manutenção,
 * incluindo cadastro, consulta, reativação e alteração de status.
 *
 * @author Maria Luiza
 */
@Service
public class EquipmentPlanService {
    private final EquipmentPlanRepository equipmentPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final MaintenancePlanRepository maintenancePlanRepository;

    public EquipmentPlanService(EquipmentPlanRepository equipmentPlanRepository,
                                EquipmentRepository equipmentRepository,
                                MaintenancePlanRepository maintenancePlanRepository) {
        this.equipmentPlanRepository = equipmentPlanRepository;
        this.equipmentRepository = equipmentRepository;
        this.maintenancePlanRepository = maintenancePlanRepository;
    }

    /**
     * Cria um vínculo entre um equipamento e um plano de manutenção
     * ou reativa um vínculo existente.
     *
     * @param dto dados do equipamento, plano e horímetro inicial
     * @return dados do vínculo registrado ou reativado
     * @throws ResourceNotFoundException se o equipamento ou plano não existir
     * @throws InactiveMaintenancePlanException se o plano estiver inativo
     * @throws EquipmentPlanAlreadyActiveException se o vínculo já estiver ativo
     * @throws IllegalArgumentException se o horímetro inicial for inválido
     */
    @Transactional
    public EquipmentPlanResponseDTO register(EquipmentPlanRequestDTO dto) {
        Equipment equipment = equipmentRepository.findById(dto.equipmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + dto.equipmentId()
                ));

        MaintenancePlan maintenancePlan = maintenancePlanRepository.findById(dto.maintenancePlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance plan not found with id: " + dto.maintenancePlanId()
                ));

        if (Boolean.FALSE.equals(maintenancePlan.getActive())) {
            throw new InactiveMaintenancePlanException(
                    "Cannot link an inactive maintenance plan to an equipment."
            );
        }
        BigDecimal startHourMeter = dto.startHourMeter() != null
                ? dto.startHourMeter()
                : equipment.getCurrentHorometer();

        if (startHourMeter.compareTo(BigDecimal.ZERO) < 0
                || startHourMeter.compareTo(equipment.getCurrentHorometer()) > 0) {
            throw new IllegalArgumentException(
                    "Start hour meter must be between zero and the equipment's current hour meter."
            );
        }

        List<EquipmentPlan> existingPlans = equipmentPlanRepository.findByEquipment_Id(dto.equipmentId());

        Optional<EquipmentPlan> existingLink = existingPlans.stream()
                .filter(ep -> ep.getMaintenancePlan().getId().equals(dto.maintenancePlanId()))
                .findFirst();

        EquipmentPlan equipmentPlan;

        if (existingLink.isPresent()) {
            equipmentPlan = existingLink.get();

            if (Boolean.TRUE.equals(equipmentPlan.getActive())) {
                throw new EquipmentPlanAlreadyActiveException(
                        "This equipment is already actively linked to this maintenance plan."
                );
            }
            equipmentPlan.setActive(true);

            equipmentPlan.setStartHourMeter(equipment.getCurrentHorometer());

        } else {
            equipmentPlan = new EquipmentPlan();
            equipmentPlan.setEquipment(equipment);
            equipmentPlan.setMaintenancePlan(maintenancePlan);
            equipmentPlan.setActive(true);
            equipmentPlan.setStartHourMeter(startHourMeter);

        }

        EquipmentPlan savedEquipmentPlan = equipmentPlanRepository.save(equipmentPlan);
        return toResponseDTO(savedEquipmentPlan);
    }

    /**
     * Lista os vínculos associados a um equipamento.
     *
     * @param equipmentId identificador do equipamento
     * @return lista de vínculos encontrados
     * @throws ResourceNotFoundException se o equipamento não existir
     */
    @Transactional(readOnly = true)
    public List<EquipmentPlanResponseDTO> findByEquipmentId(Long equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + equipmentId);
        }

        return equipmentPlanRepository.findByEquipment_Id(equipmentId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca um vínculo pelo identificador.
     *
     * @param id identificador do vínculo
     * @return dados do vínculo encontrado
     * @throws ResourceNotFoundException se o vínculo não existir
     */
    @Transactional(readOnly = true)
    public EquipmentPlanResponseDTO findById(Long id) {
        EquipmentPlan equipmentPlan = getEquipmentPlanOrThrow(id);
        return toResponseDTO(equipmentPlan);
    }

    /**
     * Alterna o estado de ativação de um vínculo existente.
     *
     * @param id identificador do vínculo
     * @return dados atualizados do vínculo
     * @throws ResourceNotFoundException se o vínculo não existir
     */
    @Transactional
    public EquipmentPlanResponseDTO toggleStatus(Long id) {
        EquipmentPlan equipmentPlan = getEquipmentPlanOrThrow(id);

        equipmentPlan.setActive(!Boolean.TRUE.equals(equipmentPlan.getActive()));

        EquipmentPlan updatedPlan = equipmentPlanRepository.save(equipmentPlan);
        return toResponseDTO(updatedPlan);
    }

    /**
     * Verifica se existe um vínculo ativo entre o equipamento e o plano.
     *
     * @param equipmentId identificador do equipamento
     * @param maintenancePlanId identificador do plano de manutenção
     * @throws IllegalStateException se não existir um vínculo ativo
     */
    @Transactional(readOnly = true)
    public void validateLinkIsActive(Long equipmentId, Long maintenancePlanId) {
        boolean isActive = equipmentPlanRepository.existsByEquipment_IdAndMaintenancePlan_IdAndActiveTrue(
                equipmentId, maintenancePlanId
        );

        if (!isActive) {
            throw new IllegalStateException("Cannot create preventive work orders for an inactive equipment plan link.");
        }
    }

    private EquipmentPlanResponseDTO toResponseDTO(EquipmentPlan ep) {
        return new EquipmentPlanResponseDTO(
                ep.getId(),
                ep.getEquipment().getId(),
                ep.getEquipment().getCode(),
                ep.getEquipment().getName(),
                ep.getMaintenancePlan().getId(),
                ep.getMaintenancePlan().getName(),
                ep.getStartHourMeter(),
                ep.getActive(),
                ep.getCreatedAt()
        );
    }

    private EquipmentPlan getEquipmentPlanOrThrow(Long id) {
        return equipmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment plan link not found with id: " + id
                ));
    }
}
