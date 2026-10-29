package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.MaintenancePlanResponseDTO;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.exception.InactiveMaintenancePlanException;
import com.HoraCerta.horacerta.domain.exception.MaintenancePlanHasHistoryException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import com.HoraCerta.horacerta.domain.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gerencia os planos de manutenção, incluindo cadastro, consulta,
 * atualização, ativação, desativação e exclusão.
 *
 * @author Maria Luiza
 */
@Service
public class MaintenancePlanService {
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final EquipmentPlanRepository equipmentPlanRepository;
    private final WorkOrderRepository workOrderRepository;

    public MaintenancePlanService(MaintenancePlanRepository maintenancePlanRepository,
                                  EquipmentPlanRepository equipmentPlanRepository,
                                  WorkOrderRepository workOrderRepository) {
        this.maintenancePlanRepository = maintenancePlanRepository;
        this.equipmentPlanRepository = equipmentPlanRepository;
        this.workOrderRepository = workOrderRepository;
    }

    /**
     * Cadastra um novo plano de manutenção com status ativo.
     *
     * @param dto nome e descrição do plano
     * @return dados do plano cadastrado
     */
    @Transactional
    public MaintenancePlanResponseDTO register(MaintenancePlanRequestDTO dto) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName(dto.name().trim());
        plan.setDescription(normalizeDescription(dto.description()));
        plan.setActive(true);

        MaintenancePlan savedPlan = maintenancePlanRepository.save(plan);

        return toResponseDTO(savedPlan);
    }

    /**
     * Lista todos os planos de manutenção cadastrados.
     *
     * @return lista de planos de manutenção
     */
    @Transactional(readOnly = true)
    public List<MaintenancePlanResponseDTO> findAll(){
        return maintenancePlanRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca um plano de manutenção pelo identificador.
     *
     * @param id identificador do plano
     * @return dados do plano encontrado
     * @throws ResourceNotFoundException se o plano não existir
     */
    @Transactional(readOnly = true)
    public MaintenancePlanResponseDTO findById(Long id){
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);
        return toResponseDTO(plan);
    }

    /**
     * Atualiza o nome e a descrição de um plano existente.
     *
     * @param id identificador do plano
     * @param dto novos dados do plano
     * @return dados atualizados do plano
     * @throws ResourceNotFoundException se o plano não existir
     */
    @Transactional
    public MaintenancePlanResponseDTO update(Long id, MaintenancePlanUpdateRequestDTO dto) {
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);

        plan.setName(dto.name().trim());
        plan.setDescription(normalizeDescription(dto.description()));

        MaintenancePlan updatedPlan = maintenancePlanRepository.save(plan);

        return toResponseDTO(updatedPlan);
    }

    /**
     * Exclui um plano quando não existem vínculos com equipamentos
     * nem ordens de serviço associadas.
     *
     * @param id identificador do plano
     * @throws ResourceNotFoundException se o plano não existir
     * @throws MaintenancePlanHasHistoryException se houver vínculos
     * ou histórico de ordens de serviço
     */
    @Transactional
    public void delete (Long id){
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);

        boolean hasLinkedEquipments = equipmentPlanRepository.existsByMaintenancePlan_Id(id);
        boolean hasWorkOrders = workOrderRepository.existsByEquipmentPlan_MaintenancePlan_Id(id);

        if (hasLinkedEquipments || hasWorkOrders) {
            throw new MaintenancePlanHasHistoryException(
                    "Cannot delete a maintenance plan with equipment links "
                            + "or work order history. Deactivate it instead."
            );
        }

        maintenancePlanRepository.delete(plan);
    }

    /**
     * Alterna o status de ativação do plano de manutenção.
     *
     * @param id identificador do plano
     * @return dados atualizados do plano
     * @throws ResourceNotFoundException se o plano não existir
     */
    @Transactional
    public MaintenancePlanResponseDTO toggleStatus(Long id) {
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);

        // Alterna entre ativo e inativo
        plan.setActive(!plan.getActive());

        MaintenancePlan updatedPlan = maintenancePlanRepository.save(plan);
        return toResponseDTO(updatedPlan);
    }

    /**
     * Valida o status do plano antes de permitir sua associação a equipamentos.
     *
     * @param id identificador do plano
     * @throws ResourceNotFoundException se o plano não existir
     * @throws MaintenancePlanHasHistoryException quando a condição de bloqueio
     * definida pelo método for satisfeita
     */
    public void validatePlanIsActive(Long id) {
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);

        if (Boolean.FALSE.equals(plan.getActive())) {
            throw new InactiveMaintenancePlanException(
                    "Cannot link equipments to an inactive maintenance plan."
            );
        }
    }

    private MaintenancePlanResponseDTO toResponseDTO(MaintenancePlan plan) {
        return new MaintenancePlanResponseDTO(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getActive()
        );
    }

    private MaintenancePlan getMaintenancePlanOrThrow(Long id) {
        return maintenancePlanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Maintenance plan not found with id: " + id
                        )
                );
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}

