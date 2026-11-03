package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = createAuthenticatedUser(
                "admin.teste@exemplo.com",
                Role.ADMIN
        );
    }

    private String createAuthenticatedUser(
            String email,
            Role role
    ) throws Exception {

        String registerBody = """
                {
                    "username": "Usuário Teste",
                    "email": "%s",
                    "password": "Teste@123456"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        user.setRole(role);
        userRepository.saveAndFlush(user);

        String loginBody = """
                {
                    "email": "%s",
                    "password": "Teste@123456"
                }
                """.formatted(email);

        MvcResult result = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        return json.get("token").asText();
    }

    private MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request
    ) {
        return request.header(
                "Authorization",
                "Bearer " + token
        );
    }

    private User createOperator(String email) throws Exception {

        String registerBody = """
                {
                    "username": "Operador Teste",
                    "email": "%s",
                    "password": "Teste@123456"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        return userRepository
                .findByEmail(email)
                .orElseThrow();
    }

    @Test
    @DisplayName("GET /api/users - ADMIN deve listar usuários")
    void shouldAllowAdminToListUsers() throws Exception {

        createOperator("operador.lista@exemplo.com");

        mockMvc.perform(authenticated(
                        get("/api/users")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /api/users/{id}/role - ADMIN deve alterar perfil")
    void shouldAllowAdminToUpdateRole() throws Exception {

        User operator = createOperator(
                "operador.role@exemplo.com"
        );

        String requestBody = """
                {
                    "role": "TECHNICIAN"
                }
                """;

        mockMvc.perform(authenticated(
                        patch(
                                "/api/users/{id}/role",
                                operator.getId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.role",
                        is("TECHNICIAN")
                ));
    }

    @Test
    @DisplayName("PATCH /api/users/{id}/status - ADMIN deve alterar status")
    void shouldAllowAdminToToggleStatus() throws Exception {

        User operator = createOperator(
                "operador.status@exemplo.com"
        );

        mockMvc.perform(authenticated(
                        patch(
                                "/api/users/{id}/status",
                                operator.getId()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.active",
                        is(false)
                ));
    }

    @Test
    @DisplayName("GET /api/users - OPERATOR não deve acessar gerenciamento")
    void shouldForbidOperatorFromListingUsers() throws Exception {

        token = createAuthenticatedUser(
                "operador.permissao@exemplo.com",
                Role.OPERATOR
        );

        mockMvc.perform(authenticated(
                        get("/api/users")))
                .andExpect(status().isForbidden());
    }
}