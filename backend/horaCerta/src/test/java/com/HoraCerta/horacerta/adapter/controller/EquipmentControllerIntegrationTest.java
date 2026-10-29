package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.equipment.EquipmentRequestDTO;
import com.HoraCerta.horacerta.domain.entity.Equipment;
import com.HoraCerta.horacerta.domain.enums.EquipmentStatus;
import com.HoraCerta.horacerta.domain.repository.EquipmentRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EquipmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        equipmentRepository.deleteAll();

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

        String responseBody = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(responseBody);

        token = json.get("token").asText();
    }

    private MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request
    ) {
        return request.header("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("POST /api/equipments - Deve criar equipamento")
    void shouldCreateEquipment() throws Exception {
        EquipmentRequestDTO requestDTO = new EquipmentRequestDTO(
                "EQ-001",
                "Escavadeira Hidráulica",
                "Pesados",
                "CAT 320",
                "SN-12345"
        );

        mockMvc.perform(authenticated(post("/api/equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.code", is("EQ-001")))
                .andExpect(jsonPath("$.name", is("Escavadeira Hidráulica")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("POST /api/equipments - Deve retornar 400 para dados inválidos")
    void shouldReturnBadRequestWhenDataIsInvalid() throws Exception {
        EquipmentRequestDTO requestDTO = new EquipmentRequestDTO(
                "",
                "Escavadeira",
                "Pesados",
                "CAT 320",
                "SN-12345"
        );

        mockMvc.perform(authenticated(post("/api/equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErros", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/equipments - Deve retornar 409 para código duplicado")
    void shouldReturnConflictWhenCodeIsDuplicate() throws Exception {
        Equipment existing = new Equipment();
        existing.setCode("EQ-001");
        existing.setName("Trator Existente");
        existing.setStatus(EquipmentStatus.ACTIVE);
        existing.setCurrentHorometer(BigDecimal.ZERO);
        equipmentRepository.saveAndFlush(existing);

        EquipmentRequestDTO requestDTO = new EquipmentRequestDTO(
                "EQ-001",
                "Outro Trator",
                "Pesados",
                "CAT D6",
                "SN-99999"
        );

        mockMvc.perform(authenticated(post("/api/equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("GET /api/equipments - Deve listar os equipamentos")
    void shouldListAllEquipments() throws Exception {
        Equipment equipment = new Equipment();
        equipment.setCode("EQ-001");
        equipment.setName("Trator A");
        equipment.setStatus(EquipmentStatus.ACTIVE);
        equipment.setCurrentHorometer(BigDecimal.ZERO);
        equipmentRepository.saveAndFlush(equipment);

        mockMvc.perform(authenticated(get("/api/equipments")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("EQ-001")));
    }

    @Test
    @DisplayName("GET /api/equipments/{id} - Deve encontrar equipamento ou retornar 404")
    void shouldFindEquipmentByIdOrReturnNotFound() throws Exception {
        Equipment equipment = new Equipment();
        equipment.setCode("EQ-002");
        equipment.setName("Guindaste");
        equipment.setStatus(EquipmentStatus.ACTIVE);
        equipment.setCurrentHorometer(BigDecimal.ZERO);

        Equipment saved = equipmentRepository.saveAndFlush(equipment);

        mockMvc.perform(authenticated(get("/api/equipments/{id}", saved.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("EQ-002")));

        mockMvc.perform(authenticated(get("/api/equipments/{id}", 999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/equipments/{id} - Deve bloquear operador sem permissão")
    void shouldForbidEquipmentDeletionForOperator() throws Exception {
        Equipment equipment = new Equipment();
        equipment.setCode("EQ-003");
        equipment.setName("Retroescavadeira");
        equipment.setStatus(EquipmentStatus.ACTIVE);
        equipment.setCurrentHorometer(BigDecimal.ZERO);

        Equipment saved = equipmentRepository.saveAndFlush(equipment);

        mockMvc.perform(authenticated(delete(
                        "/api/equipments/{id}", saved.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }
}