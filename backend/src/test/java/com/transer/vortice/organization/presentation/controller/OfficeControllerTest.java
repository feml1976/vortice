package com.transer.vortice.organization.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transer.vortice.organization.application.dto.CreateOfficeRequest;
import com.transer.vortice.organization.application.dto.OfficeResponse;
import com.transer.vortice.organization.application.dto.UpdateOfficeRequest;
import com.transer.vortice.organization.application.service.OfficeService;
import com.transer.vortice.organization.domain.exception.DuplicateCodeException;
import com.transer.vortice.organization.domain.exception.EntityInUseException;
import com.transer.vortice.organization.domain.exception.OfficeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para OfficeController usando MockMvc.
 * Valida endpoints REST, seguridad y manejo de errores.
 *
 * @author Vórtice Development Team
 */
@WebMvcTest(OfficeController.class)
@DisplayName("OfficeController - Tests de API REST")
class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfficeService officeService;

    private UUID officeId;
    private OfficeResponse officeResponse;
    private CreateOfficeRequest createRequest;
    private UpdateOfficeRequest updateRequest;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();

        officeResponse = OfficeResponse.builder()
                .id(officeId)
                .code("MAIN")
                .name("Oficina Principal")
                .city("Bogotá")
                .address("Calle 123")
                .phone("123456789")
                .isActive(true)
                .createdAt(Instant.now())
                .createdBy(1L)
                .version(0L)
                .build();

        createRequest = CreateOfficeRequest.builder()
                .code("MAIN")
                .name("Oficina Principal")
                .city("Bogotá")
                .address("Calle 123")
                .phone("123456789")
                .build();

        updateRequest = UpdateOfficeRequest.builder()
                .name("Oficina Principal Actualizada")
                .city("Bogotá")
                .address("Calle 456")
                .phone("987654321")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/offices - Sin autenticación retorna 401")
    void createOffice_Unauthorized_Returns401() throws Exception {
        mockMvc.perform(post("/api/v1/offices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());

        verify(officeService, never()).createOffice(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/v1/offices - Usuario sin rol ADMIN_NATIONAL retorna 403")
    void createOffice_Forbidden_Returns403() throws Exception {
        mockMvc.perform(post("/api/v1/offices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(officeService, never()).createOffice(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("POST /api/v1/offices - Creación exitosa retorna 201")
    void createOffice_Success_Returns201() throws Exception {
        // Given
        when(officeService.createOffice(any(CreateOfficeRequest.class))).thenReturn(officeResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/offices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("MAIN"))
                .andExpect(jsonPath("$.name").value("Oficina Principal"))
                .andExpect(jsonPath("$.city").value("Bogotá"));

        verify(officeService).createOffice(any(CreateOfficeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("POST /api/v1/offices - Código duplicado retorna 400")
    void createOffice_DuplicateCode_Returns400() throws Exception {
        // Given
        when(officeService.createOffice(any(CreateOfficeRequest.class)))
                .thenThrow(new DuplicateCodeException("Oficina", "MAIN"));

        // When/Then
        mockMvc.perform(post("/api/v1/offices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(officeService).createOffice(any(CreateOfficeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("POST /api/v1/offices - Request inválido retorna 400")
    void createOffice_InvalidRequest_Returns400() throws Exception {
        // Given - Request sin código (campo requerido)
        CreateOfficeRequest invalidRequest = CreateOfficeRequest.builder()
                .name("Oficina")
                .city("Bogotá")
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/offices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(officeService, never()).createOffice(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("PUT /api/v1/offices/{id} - Actualización exitosa retorna 200")
    void updateOffice_Success_Returns200() throws Exception {
        // Given
        when(officeService.updateOffice(eq(officeId), any(UpdateOfficeRequest.class)))
                .thenReturn(officeResponse);

        // When/Then
        mockMvc.perform(put("/api/v1/offices/{id}", officeId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(officeId.toString()));

        verify(officeService).updateOffice(eq(officeId), any(UpdateOfficeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("PUT /api/v1/offices/{id} - Oficina no encontrada retorna 404")
    void updateOffice_NotFound_Returns404() throws Exception {
        // Given
        when(officeService.updateOffice(eq(officeId), any(UpdateOfficeRequest.class)))
                .thenThrow(new OfficeNotFoundException(officeId));

        // When/Then
        mockMvc.perform(put("/api/v1/offices/{id}", officeId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(officeService).updateOffice(eq(officeId), any(UpdateOfficeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("DELETE /api/v1/offices/{id} - Eliminación exitosa retorna 204")
    void deleteOffice_Success_Returns204() throws Exception {
        // Given
        doNothing().when(officeService).deleteOffice(officeId);

        // When/Then
        mockMvc.perform(delete("/api/v1/offices/{id}", officeId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(officeService).deleteOffice(officeId);
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("DELETE /api/v1/offices/{id} - Con dependencias activas retorna 400")
    void deleteOffice_WithDependencies_Returns400() throws Exception {
        // Given
        doThrow(new EntityInUseException("Oficina", "MAIN", "almacenes", 3L))
                .when(officeService).deleteOffice(officeId);

        // When/Then
        mockMvc.perform(delete("/api/v1/offices/{id}", officeId)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(officeService).deleteOffice(officeId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/offices/{id} - Usuario autenticado puede consultar")
    void getOfficeById_Authenticated_Returns200() throws Exception {
        // Given
        when(officeService.getOfficeById(officeId)).thenReturn(officeResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/offices/{id}", officeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(officeId.toString()))
                .andExpect(jsonPath("$.code").value("MAIN"));

        verify(officeService).getOfficeById(officeId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/offices/by-code/{code} - Consulta por código exitosa")
    void getOfficeByCode_Success_Returns200() throws Exception {
        // Given
        when(officeService.getOfficeByCode("MAIN")).thenReturn(officeResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/offices/by-code/{code}", "MAIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("MAIN"));

        verify(officeService).getOfficeByCode("MAIN");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/offices - Listar todas las oficinas")
    void listAllOffices_Success_Returns200() throws Exception {
        // Given
        OfficeResponse office2 = OfficeResponse.builder()
                .id(UUID.randomUUID())
                .code("CALI")
                .name("Oficina Cali")
                .city("Cali")
                .isActive(true)
                .build();

        when(officeService.listAllOffices()).thenReturn(List.of(officeResponse, office2));

        // When/Then
        mockMvc.perform(get("/api/v1/offices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("MAIN"))
                .andExpect(jsonPath("$[1].code").value("CALI"));

        verify(officeService).listAllOffices();
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("GET /api/v1/offices/{id}/details - Obtener detalles de oficina")
    void getOfficeWithDetails_Success_Returns200() throws Exception {
        // Given
        OfficeResponse detailedResponse = OfficeResponse.builder()
                .id(officeId)
                .code("MAIN")
                .name("Oficina Principal")
                .totalWarehouses(5L)
                .totalSuppliers(3L)
                .totalUsers(10L)
                .build();

        when(officeService.getOfficeWithDetails(officeId)).thenReturn(detailedResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/offices/{id}/details", officeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWarehouses").value(5))
                .andExpect(jsonPath("$.totalSuppliers").value(3))
                .andExpect(jsonPath("$.totalUsers").value(10));

        verify(officeService).getOfficeWithDetails(officeId);
    }

    @Test
    @WithMockUser(roles = "ADMIN_NATIONAL")
    @DisplayName("PATCH /api/v1/offices/{id}/active - Cambiar estado de oficina")
    void setOfficeActive_Success_Returns200() throws Exception {
        // Given
        OfficeResponse inactiveOffice = OfficeResponse.builder()
                .id(officeId)
                .code("MAIN")
                .name("Oficina Principal")
                .isActive(false)
                .build();

        when(officeService.setOfficeActive(officeId, false)).thenReturn(inactiveOffice);

        // When/Then
        mockMvc.perform(patch("/api/v1/offices/{id}/active", officeId)
                        .with(csrf())
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        verify(officeService).setOfficeActive(officeId, false);
    }
}
