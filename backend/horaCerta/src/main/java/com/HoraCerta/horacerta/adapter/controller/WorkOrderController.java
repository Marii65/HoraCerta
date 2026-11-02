package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.workorder.WorkOrderCompletionRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.workorder.WorkOrderRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.WorkOrderResponseDTO;
import com.HoraCerta.horacerta.domain.enums.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.WorkOrderStatus;
import com.HoraCerta.horacerta.domain.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST responsável pelo gerenciamento das ordens de serviço,
 * incluindo criação, conclusão, cancelamento e consulta com filtros.
 *
 * @author Maria Luiza
 */
@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * Cria uma nova ordem de serviço.
     *
     * @param dto dados necessários para criar a ordem de serviço
     * @return ordem criada com status HTTP 201 (Created)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> create(@RequestBody @Valid WorkOrderRequestDTO dto) {
        WorkOrderResponseDTO response = workOrderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Registra a conclusão de uma ordem de serviço.
     *
     * @param id identificador da ordem de serviço
     * @param dto dados necessários para registrar a conclusão
     * @return ordem de serviço atualizada
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> complete(
            @PathVariable Long id,
            @RequestBody @Valid WorkOrderCompletionRequestDTO dto
    ){
        WorkOrderResponseDTO response = workOrderService.complete(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela uma ordem de serviço.
     *
     * @param id identificador da ordem de serviço que será cancelada
     * @return ordem de serviço atualizada
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> cancel(
            @PathVariable Long id
    ) {
        WorkOrderResponseDTO response = workOrderService.cancel(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Inicia a execução de uma ordem de serviço agendada.
     *
     * @param id identificador da ordem de serviço
     * @return ordem de serviço atualizada
     */
    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> start(
            @PathVariable Long id
    ) {
        WorkOrderResponseDTO response =
                workOrderService.start(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Consulta ordens de serviço aplicando os filtros informados.
     *
     * <p>Todos os filtros são opcionais e podem ser combinados.</p>
     *
     * @param equipmentPlanId identificador do vínculo entre equipamento
     *                        e plano de manutenção
     * @param status status da ordem de serviço
     * @param maintenanceType tipo de manutenção
     * @param startDate data inicial do período de consulta
     * @param endDate data final do período de consulta
     * @return lista de ordens de serviço que correspondem aos filtros
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<List<WorkOrderResponseDTO>> findWithFilters(
            @RequestParam(required = false) Long equipmentPlanId,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false) MaintenanceType maintenanceType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        List<WorkOrderResponseDTO> response = workOrderService.findWithFilters(
                equipmentPlanId, status, maintenanceType, startDate, endDate
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Busca uma ordem de serviço pelo identificador.
     *
     * @param id identificador da ordem de serviço
     * @return ordem de serviço encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<WorkOrderResponseDTO> findById(
            @PathVariable Long id
    ) {
        WorkOrderResponseDTO response = workOrderService.findById(id);
        return ResponseEntity.ok(response);
    }
}
