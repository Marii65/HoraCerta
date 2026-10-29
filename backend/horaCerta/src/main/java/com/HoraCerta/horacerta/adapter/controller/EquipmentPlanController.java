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

/**
 * Controlador REST responsável pelo gerenciamento dos vínculos
 * entre equipamentos e planos de manutenção.
 *
 * <p>Permite associar planos a equipamentos, consultar os planos
 * vinculados a um equipamento e ativar ou desativar essas associações.</p>
 *
 * @author Maria Luiza
 */
@RestController
@RequestMapping("/api/equipment-plans")
public class EquipmentPlanController {
    private final EquipmentPlanService equipmentPlanService;

    public EquipmentPlanController(EquipmentPlanService equipmentPlanService) {
        this.equipmentPlanService = equipmentPlanService;
    }

    /**
     * Associa um plano de manutenção a um equipamento.
     *
     * @param dto dados necessários para criar o vínculo
     * @return vínculo criado com status HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<EquipmentPlanResponseDTO> register(@RequestBody @Valid EquipmentPlanRequestDTO dto) {
        EquipmentPlanResponseDTO response =  equipmentPlanService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista os vínculos de planos de manutenção associados a um equipamento.
     *
     * @param equipmentId identificador do equipamento
     * @return lista de vínculos encontrados
     */
    @GetMapping("/equipment/{equipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR')")
    public ResponseEntity<List<EquipmentPlanResponseDTO>> findByEquipmentId(
            @PathVariable Long equipmentId
    ){
        List<EquipmentPlanResponseDTO> response = equipmentPlanService.findByEquipmentId(equipmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Alterna o estado de ativação de um vínculo entre equipamento e plano.
     *
     * @param id identificador do vínculo
     * @return vínculo com o estado atualizado
     */
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public  ResponseEntity<EquipmentPlanResponseDTO> toggleStatus(
            @PathVariable Long id
    ){
        EquipmentPlanResponseDTO response = equipmentPlanService.toggleStatus(id);
        return ResponseEntity.ok(response);
    }

}
