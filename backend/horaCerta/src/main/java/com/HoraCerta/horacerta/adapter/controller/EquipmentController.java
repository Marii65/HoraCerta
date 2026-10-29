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

/**
 * Controlador REST responsável pelo gerenciamento dos equipamentos
 * monitorados pelo HoraCerta.
 *
 * <p>Disponibiliza operações para cadastrar, consultar, atualizar e
 * excluir equipamentos, respeitando as permissões de cada perfil.</p>
 *
 * @author Maria Luiza
 */
@RestController
@RequestMapping("/api/equipments")
public class EquipmentController    {
    private EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * Cadastra um novo equipamento.
     *
     * @param dto dados necessários para o cadastro do equipamento
     * @return equipamento cadastrado com status HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponseDTO> register(@RequestBody @Valid EquipmentRequestDTO dto) {
        EquipmentResponseDTO response =  equipmentService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista os equipamentos cadastrados, permitindo filtragem por status
     * e categoria.
     *
     * @param status status utilizado para filtrar os equipamentos, se informado
     * @param category categoria utilizada para filtrar os equipamentos, se informada
     * @return lista de equipamentos que correspondem aos filtros
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR')")
    public ResponseEntity<List<EquipmentResponseDTO>> findAll(
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) String category
            ){
        List<EquipmentResponseDTO> response = equipmentService.filter(status,category);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca um equipamento pelo seu identificador.
     *
     * @param id identificador do equipamento
     * @return dados do equipamento encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR')")
    public ResponseEntity<EquipmentResponseDTO> findById(@PathVariable Long id){
        EquipmentResponseDTO response = equipmentService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza os dados de um equipamento existente.
     *
     * @param id identificador do equipamento que será atualizado
     * @param dto novos dados do equipamento
     * @return equipamento atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponseDTO> update(@PathVariable Long id, @RequestBody @Valid EquipmentUpdateRequestDTO dto){
        EquipmentResponseDTO response = equipmentService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Exclui um equipamento pelo seu identificador.
     *
     * @param id identificador do equipamento que será excluído
     * @return resposta sem conteúdo, com status HTTP 204
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
