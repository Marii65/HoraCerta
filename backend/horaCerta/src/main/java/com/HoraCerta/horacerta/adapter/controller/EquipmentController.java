package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.EquipmentResponseDTO;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import com.HoraCerta.horacerta.domain.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController    {
    private EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponseDTO> register(@RequestBody @Valid EquipmentRequestDTO dto) {
        EquipmentResponseDTO response =  equipmentService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR')")
    public ResponseEntity<List<EquipmentResponseDTO>> findAll(
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) String category
            ){
        List<EquipmentResponseDTO> response = equipmentService.filter(status,category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR')")
    public ResponseEntity<EquipmentResponseDTO> findById(@PathVariable Long id){
        EquipmentResponseDTO response = equipmentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponseDTO> update(@PathVariable Long id, @RequestBody @Valid EquipmentUpdateRequestDTO dto){
        EquipmentResponseDTO response = equipmentService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
