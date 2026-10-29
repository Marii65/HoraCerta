package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.workorder.WorkOrderCompletionRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.workorder.WorkOrderRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.WorkOrderResponseDTO;
import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.EquipmentPlan;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlanItem;
import com.HoraCerta.horacerta.domain.entity.WorkOrder;
import com.HoraCerta.horacerta.domain.enums.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.WorkOrderStatus;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanItemRepository;
import com.HoraCerta.horacerta.domain.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pelo gerenciamento das ordens de serviço,
 * incluindo criação, conclusão, cancelamento, consulta e verificação
 * de proximidade do vencimento das manutenções preventivas.
 */
@Service
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final EquipmentPlanRepository equipmentPlanRepository;
    private final MaintenancePlanItemRepository maintenancePlanItemRepository;
    private final EquipmentService equipmentService;


    public WorkOrderService(WorkOrderRepository workOrderRepository,
                            EquipmentPlanRepository equipmentPlanRepository,
                            MaintenancePlanItemRepository maintenancePlanItemRepository,
                            EquipmentService equipmentService) {
        this.workOrderRepository = workOrderRepository;
        this.equipmentPlanRepository = equipmentPlanRepository;
        this.maintenancePlanItemRepository = maintenancePlanItemRepository;
        this.equipmentService = equipmentService;
    }

    /**
     * Cria uma ordem de serviço vinculada a um equipamento e a um plano
     * de manutenção ativo.
     *
     * @param dto dados necessários para criar a ordem de serviço
     * @return dados da ordem de serviço criada
     * @throws ResourceNotFoundException se o vínculo ou o item de manutenção
     *         informado não existir
     * @throws IllegalStateException se o vínculo do equipamento com o plano
     *         estiver inativo
     * @throws IllegalArgumentException se o item não pertencer ao plano
     *         associado ao equipamento
     */
    @Transactional
    public WorkOrderResponseDTO create(WorkOrderRequestDTO dto) {
        EquipmentPlan equipmentPlan = equipmentPlanRepository.findById(dto.equipmentPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment plan link not found with id: " + dto.equipmentPlanId()
                ));

        if (!Boolean.TRUE.equals(equipmentPlan.getActive())) {
            throw new IllegalStateException(
                    "Cannot create work orders for an inactive equipment plan link."
            );
        }

        MaintenancePlanItem planItem = null;
        if (dto.maintenancePlanItemId() != null) {
            planItem = maintenancePlanItemRepository.findById(dto.maintenancePlanItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Maintenance plan item not found with id: " + dto.maintenancePlanItemId()
                    ));

            if (!planItem.getMaintenancePlan().getId().equals(equipmentPlan.getMaintenancePlan().getId())) {
                throw new IllegalArgumentException(
                        "The maintenance item does not belong to the maintenance plan associated with this equipment."
                );
            }
        }

        WorkOrder workOrder = new WorkOrder();
        workOrder.setEquipmentPlan(equipmentPlan);
        workOrder.setMaintenancePlanItem(planItem);
        workOrder.setMaintenanceType(dto.maintenanceType());
        workOrder.setScheduledHorometer(dto.scheduledHorometer());
        workOrder.setScheduledDate(dto.scheduledDate());
        workOrder.setResponsibleTechnician(dto.responsibleTechnician());
        workOrder.setNotes(dto.notes());


        workOrder.setStatus(WorkOrderStatus.SCHEDULED);

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
        return toResponseDTO(savedWorkOrder);
    }

    /**
     * Registra a conclusão de uma ordem de serviço e atualiza o horímetro
     * atual do equipamento com o valor informado.
     *
     * @param id identificador da ordem de serviço
     * @param dto dados da conclusão, incluindo o horímetro executado
     * @return dados atualizados da ordem de serviço
     * @throws ResourceNotFoundException se a ordem não existir
     * @throws IllegalStateException se a ordem já estiver concluída ou cancelada
     * @throws IllegalArgumentException se o horímetro executado for inválido
     *         ou inferior ao horímetro atual do equipamento
     */
    @Transactional
    public WorkOrderResponseDTO complete(Long id, WorkOrderCompletionRequestDTO dto) {
        WorkOrder workOrder = getWorkOrderOrThrow(id);

        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("This work order is already completed.");
        }
        if (workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete a cancelled work order.");
        }

        if (dto.executedHorometer() == null
                || dto.executedHorometer().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Executed horometer cannot be null or negative."
            );
        }

        Equipment equipment = workOrder.getEquipmentPlan().getEquipment();

        if (dto.executedHorometer().compareTo(equipment.getCurrentHorometer()) < 0) {
            throw new IllegalArgumentException(
                    "Executed horometer (" + dto.executedHorometer() +
                            ") cannot be less than the current equipment horometer (" +
                            equipment.getCurrentHorometer() + ")."
            );
        }

        workOrder.setExecutedHorometer(dto.executedHorometer());
        workOrder.setCompletedAt(LocalDateTime.now());
        workOrder.setStatus(WorkOrderStatus.COMPLETED);

        equipmentService.updateHorometer(
                equipment,
                dto.executedHorometer()
        );

        WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);
        return toResponseDTO(updatedWorkOrder);
    }

    /**
     * Cancela uma ordem de serviço que ainda não foi concluída.
     *
     * @param id identificador da ordem de serviço
     * @return dados atualizados da ordem de serviço
     * @throws ResourceNotFoundException se a ordem não existir
     * @throws IllegalStateException se a ordem já estiver concluída
     */
    @Transactional
    public WorkOrderResponseDTO cancel(Long id) {
        WorkOrder workOrder = getWorkOrderOrThrow(id);

        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed work order. History must be preserved.");
        }

        workOrder.setStatus(WorkOrderStatus.CANCELLED);
        WorkOrder updated = workOrderRepository.save(workOrder);
        return toResponseDTO(updated);
    }

    /**
     * Consulta ordens de serviço aplicando os filtros informados.
     *
     * @param equipmentPlanId identificador opcional do vínculo equipamento-plano
     * @param status status opcional da ordem de serviço
     * @param maintenanceType tipo opcional de manutenção
     * @param startDate data inicial opcional para o período de consulta
     * @param endDate data final opcional para o período de consulta
     * @return lista de ordens de serviço correspondentes aos filtros
     */
    @Transactional(readOnly = true)
    public List<WorkOrderResponseDTO> findWithFilters(Long equipmentPlanId,
                                                      WorkOrderStatus status,
                                                      MaintenanceType maintenanceType,
                                                      LocalDate startDate,
                                                      LocalDate endDate) {
        return workOrderRepository.findWithFilters(equipmentPlanId, status, maintenanceType, startDate, endDate)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca uma ordem de serviço pelo identificador.
     *
     * @param id identificador da ordem de serviço
     * @return dados da ordem encontrada
     * @throws ResourceNotFoundException se a ordem não existir
     */
    @Transactional(readOnly = true)
    public WorkOrderResponseDTO findById(Long id) {
        return toResponseDTO(getWorkOrderOrThrow(id));
    }

    /**
     * Verifica se o horímetro atual atingiu o limite de alerta para a
     * próxima manutenção prevista no item informado.
     *
     * @param equipmentPlan vínculo entre o equipamento e o plano de manutenção
     * @param item item de manutenção com intervalo e margem de alerta
     * @return {@code true} se o horímetro atual for igual ou superior
     *         ao limite de alerta; caso contrário, {@code false}
     */
    @Transactional(readOnly = true)
    public boolean isMaintenanceNearDue(EquipmentPlan equipmentPlan, MaintenancePlanItem item) {
        BigDecimal currentHorometer = equipmentPlan.getEquipment().getCurrentHorometer();
        BigDecimal startHourMeter = equipmentPlan.getStartHourMeter();
        BigDecimal interval = item.getIntervalHours();
        BigDecimal alertMargin = item.getAlertMarginHours() != null ? item.getAlertMarginHours() : BigDecimal.ZERO;

        BigDecimal nextDueHorometer = startHourMeter.add(interval);
        BigDecimal alertThreshold = nextDueHorometer.subtract(alertMargin);

        return currentHorometer.compareTo(alertThreshold) >= 0;
    }

    /**
     * Busca uma ordem de serviço ou lança uma exceção caso não exista.
     *
     * @param id identificador da ordem de serviço
     * @return entidade da ordem encontrada
     * @throws ResourceNotFoundException se a ordem não existir
     */
    private WorkOrder getWorkOrderOrThrow(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with id: " + id));
    }

    /**
     * Converte uma entidade de ordem de serviço para seu DTO de resposta.
     *
     * @param wo entidade da ordem de serviço
     * @return DTO com os dados da ordem
     */
    private WorkOrderResponseDTO toResponseDTO(WorkOrder wo) {
        return new WorkOrderResponseDTO(
                wo.getId(),
                wo.getEquipmentPlan().getId(),
                wo.getMaintenancePlanItem() != null ? wo.getMaintenancePlanItem().getId() : null,
                wo.getMaintenanceType(),
                wo.getStatus(),
                wo.getScheduledHorometer(),
                wo.getExecutedHorometer(),
                wo.getScheduledDate(),
                wo.getCompletedAt(),
                wo.getResponsibleTechnician(),
                wo.getNotes()
        );
    }
}
