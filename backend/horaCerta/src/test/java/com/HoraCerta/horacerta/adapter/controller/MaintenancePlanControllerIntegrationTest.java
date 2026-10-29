package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplan.MaintenancePlanRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.maintenanceplanitem.MaintenancePlanItemRequestDTO;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanItemRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MaintenancePlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private MaintenancePlanItemRepository maintenancePlanItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        maintenancePlanItemRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/maintenance-plans - Deve criar um novo plano de manutenção com sucesso (201 Created)")
    void shouldCreateMaintenancePlanSuccessfully() throws Exception {
        MaintenancePlanRequestDTO requestDTO = new MaintenancePlanRequestDTO(
                "Plano Preventivo 250h",
                "Manutenção preventiva padrão para motores diesel"
        );

        mockMvc.perform(post("/api/maintenance-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Plano Preventivo 250h")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("PATCH /api/maintenance-plans/{id}/status - Deve alternar o status do plano entre ativo e inativo (200 OK)")
    void shouldToggleMaintenancePlanStatus() throws Exception {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano Temporário");
        plan.setDescription("Teste de status");
        plan.setActive(true);
        MaintenancePlan savedPlan = maintenancePlanRepository.save(plan);

        // Alterna para inativo (false)
        mockMvc.perform(patch("/api/maintenance-plans/{id}/status", savedPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));

        // Alterna novamente para ativo (true)
        mockMvc.perform(patch("/api/maintenance-plans/{id}/status", savedPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("POST /api/maintenance-plans/{planId}/items - Deve adicionar um item válido a um plano ativo (201 Created)")
    void shouldAddMaintenancePlanItemSuccessfully() throws Exception {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano 500h");
        plan.setDescription("Revisão de 500 horas");
        plan.setActive(true);
        MaintenancePlan savedPlan = maintenancePlanRepository.save(plan);

        MaintenancePlanItemRequestDTO itemRequestDTO = new MaintenancePlanItemRequestDTO(
                "Troca de óleo do motor",
                new BigDecimal("500.0"),
                new BigDecimal("50.0")
        );

        mockMvc.perform(post("/api/maintenance-plans/{planId}/items", savedPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.serviceDescription", is("Troca de óleo do motor")))
                .andExpect(jsonPath("$.intervalHours", is(500.0)));
    }

    @Test
    @DisplayName("POST /api/maintenance-plans/{planId}/items - Deve retornar erro ao tentar adicionar item em plano inativo")
    void shouldReturnErrorWhenAddingItemToInactivePlan() throws Exception {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano Inativo");
        plan.setDescription("Plano desativado");
        plan.setActive(false); // Plano inativo
        MaintenancePlan savedPlan = maintenancePlanRepository.save(plan);

        MaintenancePlanItemRequestDTO itemRequestDTO = new MaintenancePlanItemRequestDTO(
                "Troca de filtros",
                new BigDecimal("250.0"),
                new BigDecimal("25.0")
        );

        mockMvc.perform(post("/api/maintenance-plans/{planId}/items", savedPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDTO)))
                .andExpect(status().isBadRequest()); // Ou Conflict dependendo do GlobalExceptionHandler para InactiveMaintenancePlanException
    }
}