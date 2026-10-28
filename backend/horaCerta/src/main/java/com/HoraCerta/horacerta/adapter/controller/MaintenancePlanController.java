package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplanitem.MaintenancePlanItemRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.MaintenancePlanItemResponseDTO;
import com.HoraCerta.horacerta.adapter.dto.response.MaintenancePlanResponseDTO;
import com.HoraCerta.horacerta.domain.service.MaintenancePlanItemService;
import com.HoraCerta.horacerta.domain.service.MaintenancePlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-plans")
public class MaintenancePlanController {
    private final MaintenancePlanService maintenancePlanService;
    private final MaintenancePlanItemService maintenancePlanItemService;

    public MaintenancePlanController(MaintenancePlanService maintenancePlanService,
                                     MaintenancePlanItemService maintenancePlanItemService) {
        this.maintenancePlanService = maintenancePlanService;
        this.maintenancePlanItemService = maintenancePlanItemService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<MaintenancePlanResponseDTO> register(@RequestBody @Valid MaintenancePlanRequestDTO dto) {
        MaintenancePlanResponseDTO response = maintenancePlanService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<List<MaintenancePlanResponseDTO>> findAll() {
        List<MaintenancePlanResponseDTO> response = maintenancePlanService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<MaintenancePlanResponseDTO> findById(@PathVariable Long id) {
        MaintenancePlanResponseDTO response = maintenancePlanService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenancePlanResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid MaintenancePlanUpdateRequestDTO dto) {
        MaintenancePlanResponseDTO response = maintenancePlanService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenancePlanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenancePlanResponseDTO> toggleStatus(@PathVariable Long id) {
        MaintenancePlanResponseDTO response = maintenancePlanService.toggleStatus(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{planId}/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenancePlanItemResponseDTO> addItem(
            @PathVariable Long planId,
            @RequestBody @Valid MaintenancePlanItemRequestDTO dto
    ) {
        MaintenancePlanItemResponseDTO response =
                maintenancePlanItemService.register(planId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
