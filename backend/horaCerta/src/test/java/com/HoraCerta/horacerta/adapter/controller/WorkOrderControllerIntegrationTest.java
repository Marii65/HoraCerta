
package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.EquipmentPlan;
import com.HoraCerta.horacerta.domain.entity.MaintenancePlan;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.entity.WorkOrder;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import com.HoraCerta.horacerta.domain.enums.MaintenanceType;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.enums.WorkOrderStatus;
import com.HoraCerta.horacerta.domain.repository.EquipmentPlanRepository;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
import com.HoraCerta.horacerta.domain.repository.MaintenancePlanRepository;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import com.HoraCerta.horacerta.domain.repository.WorkOrderRepository;

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
import java.time.LocalDate;

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
class WorkOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private EquipmentPlanRepository equipmentPlanRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        workOrderRepository.deleteAll();
        equipmentPlanRepository.deleteAll();
        equipmentRepository.deleteAll();

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
        equipment.setName("Equipamento " + code);
        equipment.setStatus(EquipmentStatus.ACTIVE);
        equipment.setCurrentHorometer(horometer);

        return equipmentRepository.saveAndFlush(equipment);
    }

    private EquipmentPlan createEquipmentPlan(
            String code,
            BigDecimal currentHorometer,
            BigDecimal startHourMeter) {

        Equipment equipment = createEquipment(code, currentHorometer);

        MaintenancePlan plan = new MaintenancePlan();
        plan.setName("Plano " + code);
        plan.setActive(true);
        plan = maintenancePlanRepository.saveAndFlush(plan);

        EquipmentPlan link = new EquipmentPlan();
        link.setEquipment(equipment);
        link.setMaintenancePlan(plan);
        link.setStartHourMeter(startHourMeter);
        link.setActive(true);

        return equipmentPlanRepository.saveAndFlush(link);
    }

    private String createWorkOrderRequest(
            Long equipmentPlanId,
            MaintenanceType type,
            String scheduledHorometer,
            String scheduledDate) {

        return """
            {
                "equipmentPlanId": %d,
                "maintenanceType": "%s",
                "scheduledHorometer": %s,
                "scheduledDate": "%s",
                "responsibleTechnician": "Técnico de teste",
                "notes": "Ordem de serviço de teste"
            }
            """.formatted(
                equipmentPlanId,
                type.name(),
                scheduledHorometer,
                scheduledDate
        );
    }

    private WorkOrder createWorkOrder(
            EquipmentPlan equipmentPlan,
            MaintenanceType type,
            WorkOrderStatus orderStatus) {

        WorkOrder order = new WorkOrder();
        order.setEquipmentPlan(equipmentPlan);
        order.setMaintenanceType(type);
        order.setStatus(orderStatus);
        order.setScheduledDate(LocalDate.of(2026, 10, 20));
        order.setScheduledHorometer(new BigDecimal("150.00"));

        return workOrderRepository.saveAndFlush(order);
    }

    @Test
    @DisplayName("POST /api/work-orders - Deve criar uma ordem de serviço")
    void shouldCreateWorkOrder() throws Exception {
        EquipmentPlan link = createEquipmentPlan(
                "EQ-WO-001",
                new BigDecimal("100.00"),
                BigDecimal.ZERO);

        String requestBody = createWorkOrderRequest(
                link.getId(),
                MaintenanceType.PREVENTIVE,
                "150.00",
                "2026-11-15");

        mockMvc.perform(authenticated(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.equipmentPlanId",
                        is(link.getId().intValue())))
                .andExpect(jsonPath("$.maintenanceType",
                        is("PREVENTIVE")))
                .andExpect(jsonPath("$.status",
                        is("SCHEDULED")));
    }

    @Test
    @DisplayName("PATCH /api/work-orders/{id}/complete - Deve concluir OS e atualizar horímetro")
    void shouldCompleteWorkOrderAndUpdateHorometer() throws Exception {
        EquipmentPlan link = createEquipmentPlan(
                "EQ-WO-002",
                new BigDecimal("100.00"),
                BigDecimal.ZERO);

        WorkOrder order = createWorkOrder(
                link,
                MaintenanceType.PREVENTIVE,
                WorkOrderStatus.SCHEDULED);

        String requestBody = """
            {
                "executedHorometer": 175.50
            }
            """;

        mockMvc.perform(authenticated(patch(
                        "/api/work-orders/{id}/complete",
                        order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.executedHorometer", is(175.50)))
                .andExpect(jsonPath("$.completedAt", notNullValue()));

        Equipment updatedEquipment = equipmentRepository.findById(
                        link.getEquipment().getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                updatedEquipment.getCurrentHorometer()
                        .compareTo(new BigDecimal("175.50"))
        );
    }

    @Test
    @DisplayName("PATCH /api/work-orders/{id}/complete - Deve rejeitar horímetro inferior ao atual")
    void shouldRejectCompletionWithLowerHorometer() throws Exception {
        EquipmentPlan link = createEquipmentPlan(
                "EQ-WO-003",
                new BigDecimal("200.00"),
                BigDecimal.ZERO);

        WorkOrder order = createWorkOrder(
                link,
                MaintenanceType.PREVENTIVE,
                WorkOrderStatus.SCHEDULED);

        String requestBody = """
            {
                "executedHorometer": 150.00
            }
            """;

        mockMvc.perform(authenticated(patch(
                        "/api/work-orders/{id}/complete",
                        order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)))
                .andExpect(status().isBadRequest());

        WorkOrder unchangedOrder = workOrderRepository.findById(order.getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                WorkOrderStatus.SCHEDULED,
                unchangedOrder.getStatus()
        );

        Equipment unchangedEquipment = equipmentRepository.findById(
                        link.getEquipment().getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                unchangedEquipment.getCurrentHorometer()
                        .compareTo(new BigDecimal("200.00"))
        );
    }

    @Test
    @DisplayName("PATCH /api/work-orders/{id}/cancel - Deve cancelar OS agendada")
    void shouldCancelScheduledWorkOrder() throws Exception {
        EquipmentPlan link = createEquipmentPlan(
                "EQ-WO-004",
                new BigDecimal("100.00"),
                BigDecimal.ZERO);

        WorkOrder order = createWorkOrder(
                link,
                MaintenanceType.CORRECTIVE,
                WorkOrderStatus.SCHEDULED);

        mockMvc.perform(authenticated(patch(
                        "/api/work-orders/{id}/cancel",
                        order.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(order.getId().intValue())))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @DisplayName("GET /api/work-orders - Deve filtrar por status, tipo e vínculo do plano")
    void shouldFilterWorkOrders() throws Exception {
        EquipmentPlan firstLink = createEquipmentPlan(
                "EQ-WO-005",
                new BigDecimal("100.00"),
                BigDecimal.ZERO);

        EquipmentPlan secondLink = createEquipmentPlan(
                "EQ-WO-006",
                new BigDecimal("200.00"),
                BigDecimal.ZERO);

        createWorkOrder(
                firstLink,
                MaintenanceType.PREVENTIVE,
                WorkOrderStatus.SCHEDULED);

        createWorkOrder(
                firstLink,
                MaintenanceType.CORRECTIVE,
                WorkOrderStatus.CANCELLED);

        createWorkOrder(
                secondLink,
                MaintenanceType.PREVENTIVE,
                WorkOrderStatus.SCHEDULED);

        mockMvc.perform(authenticated(get("/api/work-orders")
                        .param("equipmentPlanId", firstLink.getId().toString())
                        .param("status", "SCHEDULED")
                        .param("maintenanceType", "PREVENTIVE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].equipmentPlanId",
                        is(firstLink.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is("SCHEDULED")))
                .andExpect(jsonPath("$[0].maintenanceType", is("PREVENTIVE")));
    }
}
