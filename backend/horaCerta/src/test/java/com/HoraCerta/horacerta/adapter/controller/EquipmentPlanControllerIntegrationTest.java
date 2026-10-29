
package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.EquipmentPlan;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EquipmentPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentPlanRepository equipmentPlanRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        equipmentPlanRepository.deleteAll();
        token = createAuthenticatedUser();
    }

    private String createAuthenticatedUser() throws Exception {
        String email = "teste.integracao@exemplo.com";

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
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Usuário de teste não encontrado"));

        user.setRole(Role.ADMIN);
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

        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString());

        return json.get("token").asText();
    }

    private MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + token);
    }

    private Equipment createEquipment(String code, BigDecimal horometer) {
        Equipment equipment = new Equipment();
        equipment.setCode(code);
        equipment.setName("Escavadeira " + code);
        equipment.setStatus(EquipmentStatus.ACTIVE);
        equipment.setCurrentHorometer(horometer);

        return equipmentRepository.saveAndFlush(equipment);
    }

    private MaintenancePlan createMaintenancePlan(String name) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName(name);
        plan.setActive(true);

        return maintenancePlanRepository.saveAndFlush(plan);
    }

    private EquipmentPlan createEquipmentPlan(
            Equipment equipment,
            MaintenancePlan plan,
            BigDecimal startHourMeter,
            boolean active) {

        EquipmentPlan link = new EquipmentPlan();
        link.setEquipment(equipment);
        link.setMaintenancePlan(plan);
        link.setStartHourMeter(startHourMeter);
        link.setActive(active);

        return equipmentPlanRepository.saveAndFlush(link);
    }

    @Test
    @DisplayName("POST /api/equipment-plans - Deve vincular plano ao equipamento")
    void shouldCreateEquipmentPlan() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-PLAN-001", new BigDecimal("150.00"));

        MaintenancePlan plan = createMaintenancePlan("Revisão preventiva");

        String requestBody = """
            {
                "equipmentId": %d,
                "maintenancePlanId": %d,
                "startHourMeter": 100.00
            }
            """.formatted(equipment.getId(), plan.getId());

        mockMvc.perform(authenticated(post("/api/equipment-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.equipmentId", is(equipment.getId().intValue())))
                .andExpect(jsonPath("$.maintenancePlanId", is(plan.getId().intValue())))
                .andExpect(jsonPath("$.startHourMeter", is(100.00)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("GET /api/equipment-plans/equipment/{equipmentId} - Deve listar vínculos do equipamento")
    void shouldListPlansByEquipment() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-PLAN-002", new BigDecimal("250.00"));

        MaintenancePlan plan1 = createMaintenancePlan("Troca de óleo");
        MaintenancePlan plan2 = createMaintenancePlan("Inspeção geral");

        createEquipmentPlan(
                equipment, plan1, new BigDecimal("200.00"), true);

        createEquipmentPlan(
                equipment, plan2, new BigDecimal("220.00"), true);

        mockMvc.perform(authenticated(get(
                        "/api/equipment-plans/equipment/{equipmentId}",
                        equipment.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].equipmentId",
                        is(equipment.getId().intValue())))
                .andExpect(jsonPath("$[1].equipmentId",
                        is(equipment.getId().intValue())));
    }

    @Test
    @DisplayName("PATCH /api/equipment-plans/{id}/toggle - Deve inativar vínculo ativo")
    void shouldToggleEquipmentPlanToInactive() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-PLAN-003", new BigDecimal("300.00"));

        MaintenancePlan plan = createMaintenancePlan("Revisão do motor");

        EquipmentPlan link = createEquipmentPlan(
                equipment, plan, new BigDecimal("250.00"), true);

        mockMvc.perform(authenticated(patch(
                        "/api/equipment-plans/{id}/toggle",
                        link.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(link.getId().intValue())))
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    @DisplayName("PATCH /api/equipment-plans/{id}/toggle - Deve reativar vínculo inativo")
    void shouldToggleEquipmentPlanToActive() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-PLAN-004", new BigDecimal("375.00"));

        MaintenancePlan plan = createMaintenancePlan("Revisão hidráulica");

        EquipmentPlan link = createEquipmentPlan(
                equipment, plan, new BigDecimal("300.00"), false);

        mockMvc.perform(authenticated(patch(
                        "/api/equipment-plans/{id}/toggle",
                        link.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(link.getId().intValue())))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("POST /api/equipment-plans - Deve reativar vínculo existente e atualizar horímetro inicial")
    void shouldReactivateExistingLinkWithCurrentHorometer() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-PLAN-005", new BigDecimal("500.00"));

        MaintenancePlan plan = createMaintenancePlan("Manutenção do sistema");

        createEquipmentPlan(
                equipment, plan, new BigDecimal("300.00"), false);

        String requestBody = """
            {
                "equipmentId": %d,
                "maintenancePlanId": %d,
                "startHourMeter": 300.00
            }
            """.formatted(equipment.getId(), plan.getId());

        mockMvc.perform(authenticated(post("/api/equipment-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.equipmentId",
                        is(equipment.getId().intValue())))
                .andExpect(jsonPath("$.maintenancePlanId",
                        is(plan.getId().intValue())))
                .andExpect(jsonPath("$.startHourMeter", is(500.00)))
                .andExpect(jsonPath("$.active", is(true)));
    }
}
