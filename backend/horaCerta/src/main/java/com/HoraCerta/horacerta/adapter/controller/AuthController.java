package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.user.LoginRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.user.UserRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.AuthResponseDTO;
import com.HoraCerta.horacerta.adapter.dto.response.UserResponseDTO;
import com.HoraCerta.horacerta.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelas operações de autenticação do sistema.
 *
 * <p>Disponibiliza endpoints públicos para cadastro de novos usuários
 * e autenticação por e-mail e senha.</p>
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Cria uma nova instância do controller de autenticação.
     *
     * @param authService serviço responsável pelas operações de autenticação
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Cadastra um novo usuário no sistema.
     *
     * <p>Os dados recebidos são validados antes do cadastro.
     * Novos usuários são registrados com o perfil padrão definido
     * pelo serviço de autenticação.</p>
     *
     * @param dto dados necessários para o cadastro do usuário
     * @return resposta contendo os dados do usuário cadastrado
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto){
        UserResponseDTO responseDTO = authService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }

    /**
     * Autentica um usuário utilizando e-mail e senha.
     *
     * <p>Quando as credenciais são válidas, retorna o token JWT
     * utilizado para autenticar as próximas requisições.</p>
     *
     * @param dto credenciais utilizadas para autenticação
     * @return resposta contendo o token de autenticação
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(authService.login(dto));
    }
}
