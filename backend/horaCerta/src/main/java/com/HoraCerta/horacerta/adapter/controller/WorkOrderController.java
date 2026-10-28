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

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> create(@RequestBody @Valid WorkOrderRequestDTO dto) {
        WorkOrderResponseDTO response = workOrderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> complete(
            @PathVariable Long id,
            @RequestBody @Valid WorkOrderCompletionRequestDTO dto
    ){
        WorkOrderResponseDTO response = workOrderService.complete(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO> cancel(
            @PathVariable Long id
    ) {
        WorkOrderResponseDTO response = workOrderService.cancel(id);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<WorkOrderResponseDTO> findById(
            @PathVariable Long id
    ) {
        WorkOrderResponseDTO response = workOrderService.findById(id);
        return ResponseEntity.ok(response);
    }
}
