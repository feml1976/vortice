package com.transer.vortice.organization.application.service;

import com.transer.vortice.organization.application.dto.CreateWarehouseRequest;
import com.transer.vortice.organization.application.dto.UpdateWarehouseRequest;
import com.transer.vortice.organization.application.dto.WarehouseResponse;
import com.transer.vortice.organization.application.mapper.WarehouseMapper;
import com.transer.vortice.organization.domain.exception.DuplicateCodeException;
import com.transer.vortice.organization.domain.exception.EntityInUseException;
import com.transer.vortice.organization.domain.exception.ForbiddenOfficeAccessException;
import com.transer.vortice.organization.domain.exception.OfficeNotFoundException;
import com.transer.vortice.organization.domain.exception.WarehouseNotFoundException;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.Warehouse;
import com.transer.vortice.organization.domain.repository.OfficeRepository;
import com.transer.vortice.organization.domain.repository.WarehouseLocationRepository;
import com.transer.vortice.organization.domain.repository.WarehouseRepository;
import com.transer.vortice.organization.infrastructure.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para WarehouseService.
 * Incluye tests para validación de Row-Level Security.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService - Tests Unitarios")
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private WarehouseLocationRepository warehouseLocationRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private WarehouseService warehouseService;

    private UUID officeId;
    private UUID warehouseId;
    private Office office;
    private Warehouse warehouse;
    private CreateWarehouseRequest createRequest;
    private UpdateWarehouseRequest updateRequest;
    private WarehouseResponse warehouseResponse;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();

        // Configurar Office
        office = new Office("MAIN", "Oficina Principal", "Bogotá", "Calle 123", "123456789");
        office.setId(officeId);

        // Configurar Warehouse
        warehouse = new Warehouse("PRIN", "Almacén Principal", officeId, "Almacén principal");
        warehouse.setId(warehouseId);
        warehouse.setIsActive(true);
        warehouse.setCreatedAt(Instant.now());
        warehouse.setCreatedBy(1L);

        // Configurar requests
        createRequest = CreateWarehouseRequest.builder()
                .code("PRIN")
                .name("Almacén Principal")
                .officeId(officeId)
                .description("Almacén principal")
                .build();

        updateRequest = UpdateWarehouseRequest.builder()
                .name("Almacén Principal Actualizado")
                .description("Descripción actualizada")
                .build();

        // Configurar response
        warehouseResponse = WarehouseResponse.builder()
                .id(warehouseId)
                .code("PRIN")
                .name("Almacén Principal")
                .officeId(officeId)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Crear almacén - exitoso")
    void createWarehouse_Success() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(true);
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(warehouseRepository.existsByCodeAndOfficeIdAndDeletedAtIsNull("PRIN", officeId)).thenReturn(false);
        when(warehouseMapper.toEntity(createRequest)).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toResponseWithOffice(warehouse, office)).thenReturn(warehouseResponse);

        // When
        WarehouseResponse result = warehouseService.createWarehouse(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PRIN");
        verify(securityUtils).hasAccessToOffice(officeId);
        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Crear almacén - sin acceso a oficina lanza excepción")
    void createWarehouse_NoOfficeAccess_ThrowsException() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> warehouseService.createWarehouse(createRequest))
                .isInstanceOf(ForbiddenOfficeAccessException.class);

        verify(securityUtils).hasAccessToOffice(officeId);
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear almacén - oficina no existe lanza excepción")
    void createWarehouse_OfficeNotFound_ThrowsException() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(true);
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> warehouseService.createWarehouse(createRequest))
                .isInstanceOf(OfficeNotFoundException.class);

        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear almacén - código duplicado en oficina lanza excepción")
    void createWarehouse_DuplicateCode_ThrowsException() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(true);
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(warehouseRepository.existsByCodeAndOfficeIdAndDeletedAtIsNull("PRIN", officeId)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> warehouseService.createWarehouse(createRequest))
                .isInstanceOf(DuplicateCodeException.class)
                .hasMessageContaining("PRIN");

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar almacén - exitoso")
    void updateWarehouse_Success() {
        // Given
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.of(warehouse));
        when(securityUtils.getCurrentUserId()).thenReturn(2L);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(warehouseMapper.toResponseWithOffice(warehouse, office)).thenReturn(warehouseResponse);

        // When
        WarehouseResponse result = warehouseService.updateWarehouse(warehouseId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(warehouseRepository).findByIdAndDeletedAtIsNull(warehouseId);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    @DisplayName("Actualizar almacén - no encontrado lanza excepción (RLS)")
    void updateWarehouse_NotFound_ThrowsException() {
        // Given - RLS filtra y no encuentra el almacén
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> warehouseService.updateWarehouse(warehouseId, updateRequest))
                .isInstanceOf(WarehouseNotFoundException.class);

        verify(warehouseRepository).findByIdAndDeletedAtIsNull(warehouseId);
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar almacén - exitoso")
    void deleteWarehouse_Success() {
        // Given
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.of(warehouse));
        when(warehouseLocationRepository.countByWarehouseIdAndDeletedAtIsNull(warehouseId)).thenReturn(0L);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        // When
        warehouseService.deleteWarehouse(warehouseId);

        // Then
        verify(warehouseRepository).findByIdAndDeletedAtIsNull(warehouseId);
        verify(warehouseLocationRepository).countByWarehouseIdAndDeletedAtIsNull(warehouseId);
        verify(warehouseRepository).save(warehouse);
        assertThat(warehouse.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Eliminar almacén - con ubicaciones activas lanza excepción")
    void deleteWarehouse_WithActiveLocations_ThrowsException() {
        // Given
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.of(warehouse));
        when(warehouseLocationRepository.countByWarehouseIdAndDeletedAtIsNull(warehouseId)).thenReturn(5L);

        // When/Then
        assertThatThrownBy(() -> warehouseService.deleteWarehouse(warehouseId))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining("ubicaciones");

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener almacén por ID - exitoso")
    void getWarehouseById_Success() {
        // Given
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.of(warehouse));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(warehouseMapper.toResponseWithOffice(warehouse, office)).thenReturn(warehouseResponse);

        // When
        WarehouseResponse result = warehouseService.getWarehouseById(warehouseId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(warehouseId);
        verify(warehouseRepository).findByIdAndDeletedAtIsNull(warehouseId);
    }

    @Test
    @DisplayName("Obtener almacén por código - exitoso")
    void getWarehouseByCode_Success() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(true);
        when(warehouseRepository.findByCodeAndOfficeIdAndDeletedAtIsNull("PRIN", officeId))
                .thenReturn(Optional.of(warehouse));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(warehouseMapper.toResponseWithOffice(warehouse, office)).thenReturn(warehouseResponse);

        // When
        WarehouseResponse result = warehouseService.getWarehouseByCode("PRIN", officeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PRIN");
        verify(securityUtils).hasAccessToOffice(officeId);
    }

    @Test
    @DisplayName("Obtener almacén por código - sin acceso a oficina lanza excepción")
    void getWarehouseByCode_NoAccess_ThrowsException() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> warehouseService.getWarehouseByCode("PRIN", officeId))
                .isInstanceOf(ForbiddenOfficeAccessException.class);

        verify(securityUtils).hasAccessToOffice(officeId);
        verify(warehouseRepository, never()).findByCodeAndOfficeIdAndDeletedAtIsNull(any(), any());
    }

    @Test
    @DisplayName("Listar almacenes por oficina - exitoso")
    void listWarehousesByOffice_Success() {
        // Given
        Warehouse warehouse2 = new Warehouse("SEC", "Almacén Secundario", officeId, "Secundario");
        List<Warehouse> warehouses = List.of(warehouse, warehouse2);

        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(true);
        when(warehouseRepository.findActiveByOfficeId(officeId)).thenReturn(warehouses);
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(warehouseMapper.toResponseWithOffice(any(Warehouse.class), eq(office))).thenReturn(warehouseResponse);

        // When
        List<WarehouseResponse> result = warehouseService.listWarehousesByOffice(officeId);

        // Then
        assertThat(result).hasSize(2);
        verify(securityUtils).hasAccessToOffice(officeId);
        verify(warehouseRepository).findActiveByOfficeId(officeId);
    }

    @Test
    @DisplayName("Listar almacenes por oficina - sin acceso lanza excepción")
    void listWarehousesByOffice_NoAccess_ThrowsException() {
        // Given
        when(securityUtils.hasAccessToOffice(officeId)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> warehouseService.listWarehousesByOffice(officeId))
                .isInstanceOf(ForbiddenOfficeAccessException.class);

        verify(warehouseRepository, never()).findActiveByOfficeId(any());
    }

    @Test
    @DisplayName("Obtener almacén con detalles - exitoso")
    void getWarehouseWithDetails_Success() {
        // Given
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.of(warehouse));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(warehouseLocationRepository.countByWarehouseIdAndDeletedAtIsNull(warehouseId)).thenReturn(10L);

        WarehouseResponse detailedResponse = WarehouseResponse.builder()
                .id(warehouseId)
                .code("PRIN")
                .totalLocations(10L)
                .build();

        when(warehouseMapper.toResponseWithDetails(warehouse, office, 10L)).thenReturn(detailedResponse);

        // When
        WarehouseResponse result = warehouseService.getWarehouseWithDetails(warehouseId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalLocations()).isEqualTo(10L);
        verify(warehouseLocationRepository).countByWarehouseIdAndDeletedAtIsNull(warehouseId);
    }

    @Test
    @DisplayName("Activar/Desactivar almacén - exitoso")
    void setWarehouseActive_Success() {
        // Given
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(warehouseMapper.toResponseWithOffice(warehouse, office)).thenReturn(warehouseResponse);

        // When
        WarehouseResponse result = warehouseService.setWarehouseActive(warehouseId, false);

        // Then
        assertThat(result).isNotNull();
        verify(warehouseRepository).save(warehouse);
        assertThat(warehouse.getIsActive()).isFalse();
    }
}
