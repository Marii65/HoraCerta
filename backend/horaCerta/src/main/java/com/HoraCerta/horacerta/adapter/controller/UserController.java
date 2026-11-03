package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.user.UserRoleUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.UserResponseDTO;
import com.HoraCerta.horacerta.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelas operações relacionadas aos usuários.
 *
 * <p>Disponibiliza endpoints para consulta dos dados do usuário
 * autenticado no sistema.</p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Cria uma nova instância do controller de usuários.
     *
     * @param userService serviço responsável pelas operações de usuários
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retorna os dados do usuário atualmente autenticado.
     *
     * <p>O e-mail do usuário é obtido a partir da autenticação
     * presente no contexto de segurança.</p>
     *
     * @param principal usuário autenticado na requisição
     * @return resposta contendo os dados do usuário autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            Principal principal
    ) {

        return ResponseEntity.ok(
                userService.findByEmail(
                        principal.getName()
                )
        );
    }

    /**
     * Retorna todos os usuários cadastrados.
     *
     * @return lista de usuários cadastrados
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> findAll() {

        return ResponseEntity.ok(
                userService.findAll()
        );
    }

    /**
     * Busca um usuário pelo identificador.
     *
     * @param id identificador UUID do usuário
     * @return dados do usuário encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                userService.findById(id)
        );
    }

    /**
     * Altera o perfil de acesso de um usuário.
     *
     * @param id identificador UUID do usuário
     * @param dto novo perfil do usuário
     * @return usuário atualizado
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateRole(
            @PathVariable UUID id,
            @RequestBody @Valid UserRoleUpdateRequestDTO dto
    ) {

        return ResponseEntity.ok(
                userService.updateRole(id, dto)
        );
    }

    /**
     * Ativa ou desativa um usuário.
     *
     * @param id identificador UUID do usuário
     * @return usuário atualizado
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> toggleStatus(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                userService.toggleStatus(id)
        );
    }
}
