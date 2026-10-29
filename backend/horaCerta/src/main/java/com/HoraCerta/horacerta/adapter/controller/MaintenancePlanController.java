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

/**
 * Controlador REST responsável pelo gerenciamento dos planos
 * de manutenção preventiva e de seus itens.
 *
 * @author Maria Luiza
 */
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

    /**
     * Cadastra um novo plano de manutenção.
     *
     * @param dto dados necessários para cadastrar o plano
     * @return plano cadastrado com status HTTP 201 (Created)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<MaintenancePlanResponseDTO> register(@RequestBody @Valid MaintenancePlanRequestDTO dto) {
        MaintenancePlanResponseDTO response = maintenancePlanService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os planos de manutenção cadastrados.
     *
     * @return lista de planos de manutenção
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<List<MaintenancePlanResponseDTO>> findAll() {
        List<MaintenancePlanResponseDTO> response = maintenancePlanService.findAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca um plano de manutenção pelo identificador.
     *
     * @param id identificador do plano
     * @return plano de manutenção encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<MaintenancePlanResponseDTO> findById(@PathVariable Long id) {
        MaintenancePlanResponseDTO response = maintenancePlanService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza os dados de um plano de manutenção existente.
     *
     * @param id identificador do plano que será atualizado
     * @param dto novos dados do plano
     * @return plano atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenancePlanResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid MaintenancePlanUpdateRequestDTO dto) {
        MaintenancePlanResponseDTO response = maintenancePlanService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Exclui um plano de manutenção.
     *
     * @param id identificador do plano que será excluído
     * @return resposta sem conteúdo, com status HTTP 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenancePlanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Alterna o estado de ativação de um plano de manutenção.
     *
     * @param id identificador do plano
     * @return plano com o estado atualizado
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenancePlanResponseDTO> toggleStatus(@PathVariable Long id) {
        MaintenancePlanResponseDTO response = maintenancePlanService.toggleStatus(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona um item a um plano de manutenção existente.
     *
     * @param planId identificador do plano que receberá o item
     * @param dto dados do item de manutenção
     * @return item cadastrado com status HTTP 201 (Created)
     */
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
