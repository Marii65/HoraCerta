package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.MaintenancePlanResponseDTO;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.exception.MaintenancePlanHasHistoryException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import com.HoraCerta.horacerta.domain.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional
    public MaintenancePlanResponseDTO register(MaintenancePlanRequestDTO dto) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName(dto.name().trim());
        plan.setDescription(normalizeDescription(dto.description()));
        plan.setActive(true);

        MaintenancePlan savedPlan = maintenancePlanRepository.save(plan);

        return toResponseDTO(savedPlan);
    }

    @Transactional(readOnly = true)
    public List<MaintenancePlanResponseDTO> findAll(){
        return maintenancePlanRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaintenancePlanResponseDTO findById(Long id){
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);
        return toResponseDTO(plan);
    }

    @Transactional
    public MaintenancePlanResponseDTO update(Long id, MaintenancePlanUpdateRequestDTO dto) {
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);

        plan.setName(dto.name().trim());
        plan.setDescription(normalizeDescription(dto.description()));

        MaintenancePlan updatedPlan = maintenancePlanRepository.save(plan);

        return toResponseDTO(updatedPlan);
    }

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

    @Transactional
    public MaintenancePlanResponseDTO toggleStatus(Long id) {
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);

        // Alterna entre ativo e inativo
        plan.setActive(!plan.getActive());

        MaintenancePlan updatedPlan = maintenancePlanRepository.save(plan);
        return toResponseDTO(updatedPlan);
    }

    public void validatePlanIsActive(Long id) {
        MaintenancePlan plan = getMaintenancePlanOrThrow(id);
        if (Boolean.FALSE.equals(plan.getActive())) {
            throw new MaintenancePlanHasHistoryException
                    ("Cannot link equipments to an inactive maintenance plan.");
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

