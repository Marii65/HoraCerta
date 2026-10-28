package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.equipmentplan.EquipmentPlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.EquipmentPlanResponseDTO;
import com.HoraCerta.horacerta.domain.service.EquipmentPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-plans")
public class EquipmentPlanController {
    private final EquipmentPlanService equipmentPlanService;

    public EquipmentPlanController(EquipmentPlanService equipmentPlanService) {
        this.equipmentPlanService = equipmentPlanService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<EquipmentPlanResponseDTO> register(@RequestBody @Valid EquipmentPlanRequestDTO dto) {
        EquipmentPlanResponseDTO response =  equipmentPlanService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/equipment/{equipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR')")
    public ResponseEntity<List<EquipmentPlanResponseDTO>> findByEquipmentId(
            @PathVariable Long equipmentId
    ){
        List<EquipmentPlanResponseDTO> response = equipmentPlanService.findByEquipmentId(equipmentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public  ResponseEntity<EquipmentPlanResponseDTO> toggleStatus(
            @PathVariable Long id
    ){
        EquipmentPlanResponseDTO response = equipmentPlanService.toggleStatus(id);
        return ResponseEntity.ok(response);
    }

}
