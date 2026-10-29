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

/**
 * Gerencia os itens dos planos de manutenção, incluindo cadastro,
 * consulta e exclusão com preservação do histórico de ordens de serviço.
 *
 * @author Maria Luiza
 */
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

    /**
     * Cadastra um item em um plano de manutenção ativo.
     * A margem de alerta deve ser inferior ao intervalo de manutenção.
     *
     * @param planId identificador do plano de manutenção
     * @param dto descrição do serviço, intervalo e margem de alerta
     * @return dados do item cadastrado
     * @throws ResourceNotFoundException se o plano não existir
     * @throws InactiveMaintenancePlanException se o plano estiver inativo
     * @throws IllegalArgumentException se a margem de alerta for maior ou igual
     * ao intervalo de manutenção
     */
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

    /**
     * Lista os itens associados a um plano de manutenção.
     *
     * @param planId identificador do plano de manutenção
     * @return lista de itens do plano
     * @throws ResourceNotFoundException se o plano não existir
     */
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

    /**
     * Busca um item de plano de manutenção pelo identificador.
     *
     * @param id identificador do item
     * @return dados do item encontrado
     * @throws ResourceNotFoundException se o item não existir
     */
    @Transactional(readOnly = true)
    public MaintenancePlanItemResponseDTO findById(Long id) {
        MaintenancePlanItem item = getItemOrThrow(id);
        return toResponseDTO(item);
    }

    /**
     * Exclui um item somente quando ele não estiver referenciado
     * por ordens de serviço.
     *
     * @param id identificador do item
     * @throws ResourceNotFoundException se o item não existir
     * @throws MaintenancePlanItemHasHistoryException se houver ordens de serviço
     * associadas ao item
     */
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
