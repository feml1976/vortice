package com.transer.vortice.organization.application.mapper;

import com.transer.vortice.organization.application.dto.CreateWarehouseRequest;
import com.transer.vortice.organization.application.dto.WarehouseResponse;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para WarehouseMapper.
 * Valida las conversiones entre entidades y DTOs.
 *
 * @author Vórtice Development Team
 */
@DisplayName("WarehouseMapper - Tests de Mapeo")
class WarehouseMapperTest {

    private WarehouseMapper warehouseMapper;
    private UUID officeId;
    private UUID warehouseId;

    @BeforeEach
    void setUp() {
        warehouseMapper = new WarehouseMapper();
        officeId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
    }

    @Test
    @DisplayName("toEntity - convierte CreateWarehouseRequest a Warehouse")
    void toEntity_FromCreateRequest_ReturnsWarehouse() {
        // Given
        CreateWarehouseRequest request = CreateWarehouseRequest.builder()
                .code("PRIN")
                .name("Almacén Principal")
                .officeId(officeId)
                .description("Almacén principal de la oficina")
                .build();

        // When
        Warehouse warehouse = warehouseMapper.toEntity(request);

        // Then
        assertThat(warehouse).isNotNull();
        assertThat(warehouse.getCode()).isEqualTo("PRIN");
        assertThat(warehouse.getName()).isEqualTo("Almacén Principal");
        assertThat(warehouse.getOfficeId()).isEqualTo(officeId);
        assertThat(warehouse.getDescription()).isEqualTo("Almacén principal de la oficina");
    }

    @Test
    @DisplayName("toEntity - maneja request null")
    void toEntity_NullRequest_ReturnsNull() {
        // When
        Warehouse warehouse = warehouseMapper.toEntity(null);

        // Then
        assertThat(warehouse).isNull();
    }

    @Test
    @DisplayName("toResponse - convierte Warehouse a WarehouseResponse")
    void toResponse_FromWarehouse_ReturnsWarehouseResponse() {
        // Given
        Warehouse warehouse = new Warehouse("PRIN", "Almacén Principal", officeId, "Descripción");
        warehouse.setId(warehouseId);
        warehouse.setIsActive(true);
        warehouse.setCreatedAt(Instant.now());
        warehouse.setCreatedBy(1L);
        warehouse.setVersion(0L);

        // When
        WarehouseResponse response = warehouseMapper.toResponse(warehouse);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(warehouseId);
        assertThat(response.getCode()).isEqualTo("PRIN");
        assertThat(response.getName()).isEqualTo("Almacén Principal");
        assertThat(response.getOfficeId()).isEqualTo(officeId);
        assertThat(response.getDescription()).isEqualTo("Descripción");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedBy()).isEqualTo(1L);
        assertThat(response.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("toResponse - maneja warehouse null")
    void toResponse_NullWarehouse_ReturnsNull() {
        // When
        WarehouseResponse response = warehouseMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("toResponseWithOffice - incluye información de oficina")
    void toResponseWithOffice_WithOffice_IncludesOfficeInfo() {
        // Given
        Warehouse warehouse = new Warehouse("PRIN", "Almacén Principal", officeId, "Descripción");
        warehouse.setId(warehouseId);
        warehouse.setIsActive(true);
        warehouse.setCreatedAt(Instant.now());
        warehouse.setCreatedBy(1L);

        Office office = new Office("MAIN", "Oficina Principal", "Bogotá", "Calle 123", "123456789");
        office.setId(officeId);

        // When
        WarehouseResponse response = warehouseMapper.toResponseWithOffice(warehouse, office);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(warehouseId);
        assertThat(response.getCode()).isEqualTo("PRIN");
        assertThat(response.getOfficeId()).isEqualTo(officeId);
        assertThat(response.getOfficeCode()).isEqualTo("MAIN");
        assertThat(response.getOfficeName()).isEqualTo("Oficina Principal");
    }

    @Test
    @DisplayName("toResponseWithOffice - maneja office null")
    void toResponseWithOffice_NullOffice_OnlyWarehouseInfo() {
        // Given
        Warehouse warehouse = new Warehouse("PRIN", "Almacén Principal", officeId, null);
        warehouse.setId(warehouseId);

        // When
        WarehouseResponse response = warehouseMapper.toResponseWithOffice(warehouse, null);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(warehouseId);
        assertThat(response.getOfficeCode()).isNull();
        assertThat(response.getOfficeName()).isNull();
    }

    @Test
    @DisplayName("toResponseWithDetails - incluye información completa con totales")
    void toResponseWithDetails_WithOfficeAndCount_IncludesAllInfo() {
        // Given
        Warehouse warehouse = new Warehouse("PRIN", "Almacén Principal", officeId, "Descripción");
        warehouse.setId(warehouseId);
        warehouse.setIsActive(true);

        Office office = new Office("MAIN", "Oficina Principal", "Bogotá", null, null);
        office.setId(officeId);

        Long totalLocations = 15L;

        // When
        WarehouseResponse response = warehouseMapper.toResponseWithDetails(warehouse, office, totalLocations);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(warehouseId);
        assertThat(response.getCode()).isEqualTo("PRIN");
        assertThat(response.getOfficeCode()).isEqualTo("MAIN");
        assertThat(response.getOfficeName()).isEqualTo("Oficina Principal");
        assertThat(response.getTotalLocations()).isEqualTo(15L);
    }

    @Test
    @DisplayName("toResponseWithDetails - maneja valores null")
    void toResponseWithDetails_NullValues_HandlesGracefully() {
        // Given
        Warehouse warehouse = new Warehouse("PRIN", "Almacén", officeId, null);
        warehouse.setId(warehouseId);

        // When
        WarehouseResponse response = warehouseMapper.toResponseWithDetails(null, null, 0L);

        // Then
        assertThat(response).isNull();

        // When - warehouse válido, office null
        response = warehouseMapper.toResponseWithDetails(warehouse, null, 0L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTotalLocations()).isEqualTo(0L);
        assertThat(response.getOfficeCode()).isNull();
    }

    @Test
    @DisplayName("Conversión completa - CreateRequest -> Entity -> Response")
    void fullConversion_RequestToEntityToResponse_PreservesData() {
        // Given
        CreateWarehouseRequest request = CreateWarehouseRequest.builder()
                .code("TEST")
                .name("Almacén Test")
                .officeId(officeId)
                .description("Test description")
                .build();

        // When - Convertir a entidad
        Warehouse warehouse = warehouseMapper.toEntity(request);
        warehouse.setId(warehouseId);
        warehouse.setIsActive(true);
        warehouse.setCreatedAt(Instant.now());
        warehouse.setCreatedBy(1L);
        warehouse.setVersion(0L);

        // When - Convertir a response
        WarehouseResponse response = warehouseMapper.toResponse(warehouse);

        // Then - Verificar que los datos se preservaron
        assertThat(response.getCode()).isEqualTo(request.getCode());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getOfficeId()).isEqualTo(request.getOfficeId());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getId()).isEqualTo(warehouseId);
    }

    @Test
    @DisplayName("Builder pattern - crea objetos válidos")
    void builderPattern_CreatesValidObjects() {
        // Given
        WarehouseResponse response = WarehouseResponse.builder()
                .id(warehouseId)
                .code("PRIN")
                .name("Almacén")
                .officeId(officeId)
                .officeCode("MAIN")
                .officeName("Oficina")
                .totalLocations(10L)
                .isActive(true)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(warehouseId);
        assertThat(response.getCode()).isEqualTo("PRIN");
        assertThat(response.getOfficeCode()).isEqualTo("MAIN");
        assertThat(response.getTotalLocations()).isEqualTo(10L);
    }
}
