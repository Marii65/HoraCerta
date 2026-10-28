package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplanitem.MaintenancePlanItemRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.MaintenancePlanItemResponseDTO;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlanItem;
import com.HoraCerta.horacerta.domain.exception.InactiveMaintenancePlanException;
import com.HoraCerta.horacerta.domain.exception.MaintenancePlanItemHasHistoryException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanItemRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import com.HoraCerta.horacerta.domain.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaintenancePlanItemService {
    private final MaintenancePlanItemRepository itemRepository;
    private final MaintenancePlanRepository planRepository;
    private final WorkOrderRepository workOrderRepository;

    public MaintenancePlanItemService(MaintenancePlanItemRepository itemRepository,
                                      MaintenancePlanRepository planRepository,
                                      WorkOrderRepository workOrderRepository) {
        this.itemRepository = itemRepository;
        this.planRepository = planRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public MaintenancePlanItemResponseDTO register(
            Long planId,
            MaintenancePlanItemRequestDTO dto) {

        MaintenancePlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance plan not found with id: " + dto.maintenancePlanId()
                ));

        if (Boolean.FALSE.equals(plan.getActive())) {
            throw new InactiveMaintenancePlanException("Cannot add items to an inactive maintenance plan.");
        }

        if (dto.alertMarginHours().compareTo(dto.intervalHours()) >= 0) {
            throw new IllegalArgumentException(
                    "Alert margin hours must be less than the interval hours."
            );
        }

        MaintenancePlanItem item = new MaintenancePlanItem();
        item.setMaintenancePlan(plan);
        item.setServiceDescription(dto.serviceDescription().trim());
        item.setIntervalHours(dto.intervalHours());
        item.setAlertMarginHours(dto.alertMarginHours());

        MaintenancePlanItem savedItem = itemRepository.save(item);

        return toResponseDTO(savedItem);
    }
    @Transactional(readOnly = true)
    public List<MaintenancePlanItemResponseDTO> findByPlanId(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Maintenance plan not found with id: " + planId);
        }

        return itemRepository.findByMaintenancePlanId(planId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaintenancePlanItemResponseDTO findById(Long id) {
        MaintenancePlanItem item = getItemOrThrow(id);
        return toResponseDTO(item);
    }

    @Transactional
    public void delete(Long id) {
        MaintenancePlanItem item = getItemOrThrow(id);

        boolean hasWorkOrders = workOrderRepository.existsByMaintenancePlanItem_Id(id);
        if (hasWorkOrders) {
            throw new MaintenancePlanItemHasHistoryException(
                    "Cannot delete maintenance plan item because it is referenced by work order history."
            );
        }

        itemRepository.delete(item);
    }
    private MaintenancePlanItemResponseDTO toResponseDTO(MaintenancePlanItem item) {
        return new MaintenancePlanItemResponseDTO(
                item.getId(),
                item.getMaintenancePlan().getId(),
                item.getServiceDescription(),
                item.getIntervalHours(),
                item.getAlertMarginHours()
        );
    }

    private MaintenancePlanItem getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Maintenance plan item not found with id: " + id
                        )
                );
    }
}
