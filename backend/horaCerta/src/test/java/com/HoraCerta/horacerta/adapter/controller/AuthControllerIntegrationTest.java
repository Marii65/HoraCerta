package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository
                .findByEmail("auth.teste@exemplo.com")
                .ifPresent(userRepository::delete);
    }

    private String registerBody() {
        return """
                {
                    "username": "Usuário Auth",
                    "email": "auth.teste@exemplo.com",
                    "password": "Teste@123456"
                }
                """;
    }

    private String loginBody(String password) {
        return """
                {
                    "email": "auth.teste@exemplo.com",
                    "password": "%s"
                }
                """.formatted(password);
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve registrar usuário como operador")
    void shouldRegisterUser() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isCreated());

        User user = userRepository
                .findByEmail("auth.teste@exemplo.com")
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                "OPERATOR",
                user.getRole().name()
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                user.getActive()
        );
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve autenticar com credenciais válidas")
    void shouldLoginSuccessfully() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("Teste@123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve rejeitar senha incorreta")
    void shouldRejectInvalidPassword() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("SenhaErrada@123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve rejeitar usuário inativo")
    void shouldRejectInactiveUser() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody()))
                .andExpect(status().isCreated());

        User user = userRepository
                .findByEmail("auth.teste@exemplo.com")
                .orElseThrow();

        user.setActive(false);
        userRepository.saveAndFlush(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("Teste@123456")))
                .andExpect(status().isUnauthorized());
    }
}