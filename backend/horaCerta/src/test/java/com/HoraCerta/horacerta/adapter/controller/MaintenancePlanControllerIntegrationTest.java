package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplanitem.MaintenancePlanItemRequestDTO;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanItemRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import org.springframework.security.test.context.support.WithMockUser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MaintenancePlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private MaintenancePlanItemRepository maintenancePlanItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {

        String registerBody = """
                {
                    "username": "Teste Integracao",
                    "email": "teste.integracao@exemplo.com",
                    "password": "Teste@123456"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().is2xxSuccessful());

        User user = userRepository
                .findByEmail("teste.integracao@exemplo.com")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuário de teste não encontrado"
                        )
                );

        user.setRole(Role.TECHNICIAN);
        userRepository.saveAndFlush(user);

        String loginBody = """
                {
                    "email": "teste.integracao@exemplo.com",
                    "password": "Teste@123456"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        token = response.get("token").asText();
    }

    private MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request
    ) {
        return request.header("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("POST /api/maintenance-plans - Deve criar um plano de manutenção")
    void shouldCreateMaintenancePlanSuccessfully() throws Exception {
        MaintenancePlanRequestDTO requestDTO =
                new MaintenancePlanRequestDTO(
                        "Plano Preventivo 250h",
                        "Manutenção preventiva padrão para motores diesel"
                );

        mockMvc.perform(authenticated(post("/api/maintenance-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Plano Preventivo 250h")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("PATCH /api/maintenance-plans/{id}/status - Deve alternar o status do plano")
    void shouldToggleMaintenancePlanStatus() throws Exception {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano Temporário");
        plan.setDescription("Teste de status");
        plan.setActive(true);

        MaintenancePlan savedPlan =
                maintenancePlanRepository.saveAndFlush(plan);

        mockMvc.perform(authenticated(
                        patch("/api/maintenance-plans/{id}/status",
                                savedPlan.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));

        mockMvc.perform(authenticated(
                        patch("/api/maintenance-plans/{id}/status",
                                savedPlan.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("POST /api/maintenance-plans/{planId}/items - Deve adicionar item a plano ativo")
    void shouldAddMaintenancePlanItemSuccessfully() throws Exception {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano 500h");
        plan.setDescription("Revisão de 500 horas");
        plan.setActive(true);

        MaintenancePlan savedPlan =
                maintenancePlanRepository.saveAndFlush(plan);

        MaintenancePlanItemRequestDTO requestDTO =
                new MaintenancePlanItemRequestDTO(
                        savedPlan.getId(),
                        "Troca de óleo do motor",
                        new BigDecimal("500.0"),
                        new BigDecimal("50.0")
                );

        mockMvc.perform(authenticated(
                        post("/api/maintenance-plans/{planId}/items",
                                savedPlan.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.serviceDescription",
                        is("Troca de óleo do motor")))
                .andExpect(jsonPath("$.intervalHours", is(500.0)));
    }

    @Test
    @DisplayName("POST /api/maintenance-plans/{planId}/items - Deve rejeitar item em plano inativo")
    void shouldRejectAddingItemToInactivePlan() throws Exception {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano Inativo");
        plan.setDescription("Plano desativado");
        plan.setActive(false);

        MaintenancePlan savedPlan =
                maintenancePlanRepository.saveAndFlush(plan);

        MaintenancePlanItemRequestDTO requestDTO =
                new MaintenancePlanItemRequestDTO(
                        savedPlan.getId(),
                        "Troca de filtros",
                        new BigDecimal("250.0"),
                        new BigDecimal("25.0")
                );

        mockMvc.perform(authenticated(
                        post("/api/maintenance-plans/{planId}/items",
                                savedPlan.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))))
                .andExpect(status().isConflict());
    }
}