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

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto){
        UserResponseDTO responseDTO = authService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(authService.login(dto));
    }
}
