package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.response.UserResponseDTO;
import com.HoraCerta.horacerta.domain.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
}
