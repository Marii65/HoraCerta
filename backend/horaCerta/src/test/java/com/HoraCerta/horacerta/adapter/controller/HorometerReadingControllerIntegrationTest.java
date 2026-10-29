package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.entity.HorometerReading;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
import com.HoraCerta.horacerta.domain.repository.HorometerReadingRepository;
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
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HorometerReadingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private HorometerReadingRepository readingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        readingRepository.deleteAll();
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

    private String requestBody(Long equipmentId, String readingValue) {
        return """
            {
                "equipmentId": %d,
                "readingValue": %s,
                "observation": "Leitura de teste"
            }
            """.formatted(equipmentId, readingValue);
    }

    @Test
    @DisplayName("POST /api/horometer-readings - Deve registrar leitura e atualizar horímetro")
    void shouldRegisterReadingAndUpdateEquipmentHorometer() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-HOR-001", new BigDecimal("100.00"));

        mockMvc.perform(authenticated(post("/api/horometer-readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(equipment.getId(), "125.50"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.equipmentId",
                        is(equipment.getId().intValue())))
                .andExpect(jsonPath("$.readingValue", is(125.50)))
                .andExpect(jsonPath("$.username", is("Teste Integracao")))
                .andExpect(jsonPath("$.readAt", notNullValue()));

        Equipment updated = equipmentRepository.findById(equipment.getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                updated.getCurrentHorometer().compareTo(new BigDecimal("125.50"))
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                1,
                readingRepository.findByEquipment_IdOrderByReadAtDesc(
                        equipment.getId()).size()
        );
    }

    @Test
    @DisplayName("POST /api/horometer-readings - Deve rejeitar leitura inferior ao horímetro atual")
    void shouldRejectReadingBelowCurrentHorometer() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-HOR-002", new BigDecimal("200.00"));

        mockMvc.perform(authenticated(post("/api/horometer-readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(equipment.getId(), "150.00"))))
                .andExpect(status().isBadRequest());

        Equipment unchanged = equipmentRepository.findById(equipment.getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                unchanged.getCurrentHorometer().compareTo(new BigDecimal("200.00"))
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                readingRepository.findByEquipment_IdOrderByReadAtDesc(
                        equipment.getId()).size()
        );
    }

    @Test
    @DisplayName("GET /api/horometer-readings/equipment/{equipmentId}/period - Deve filtrar leituras por período")
    void shouldFilterReadingsByPeriod() throws Exception {
        Equipment equipment = createEquipment(
                "EQ-HOR-003", new BigDecimal("300.00"));

        User user = userRepository.findByEmail("teste.integracao@exemplo.com")
                .orElseThrow();

        LocalDateTime startDate = LocalDateTime.of(2026, 10, 10, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 10, 20, 23, 59, 59);

        saveReading(equipment, user, "310.00",
                LocalDateTime.of(2026, 10, 12, 10, 0));

        saveReading(equipment, user, "320.00",
                LocalDateTime.of(2026, 10, 18, 15, 30));

        saveReading(equipment, user, "330.00",
                LocalDateTime.of(2026, 10, 25, 9, 0));

        mockMvc.perform(authenticated(get(
                        "/api/horometer-readings/equipment/{equipmentId}/period",
                        equipment.getId())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].readingValue", is(320.00)))
                .andExpect(jsonPath("$[1].readingValue", is(310.00)));
    }

    private void saveReading(
            Equipment equipment,
            User user,
            String value,
            LocalDateTime readAt) {

        HorometerReading reading = new HorometerReading();
        reading.setEquipment(equipment);
        reading.setUser(user);
        reading.setReadingValue(new BigDecimal(value));
        reading.setReadAt(readAt);
        reading.setObservation("Leitura de teste");

        readingRepository.saveAndFlush(reading);
    }
}
