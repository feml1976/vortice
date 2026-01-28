package com.transer.vortice.organization.application.service;

import com.transer.vortice.organization.application.dto.CreateOfficeRequest;
import com.transer.vortice.organization.application.dto.OfficeResponse;
import com.transer.vortice.organization.application.dto.UpdateOfficeRequest;
import com.transer.vortice.organization.application.mapper.OfficeMapper;
import com.transer.vortice.organization.domain.exception.DuplicateCodeException;
import com.transer.vortice.organization.domain.exception.EntityInUseException;
import com.transer.vortice.organization.domain.exception.OfficeNotFoundException;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.repository.OfficeRepository;
import com.transer.vortice.organization.domain.repository.TireSupplierRepository;
import com.transer.vortice.organization.domain.repository.WarehouseRepository;
import com.transer.vortice.organization.infrastructure.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para OfficeService.
 * Utiliza Mockito para simular las dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OfficeService - Tests Unitarios")
class OfficeServiceTest {

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private TireSupplierRepository tireSupplierRepository;

    @Mock
    private OfficeMapper officeMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private OfficeService officeService;

    private UUID officeId;
    private Office office;
    private CreateOfficeRequest createRequest;
    private UpdateOfficeRequest updateRequest;
    private OfficeResponse officeResponse;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();

        // Configurar Office mock
        office = new Office("MAIN", "Oficina Principal", "Bogotá", "Calle 123", "123456789");
        office.setId(officeId);
        office.setIsActive(true);
        office.setCreatedAt(Instant.now());
        office.setCreatedBy(1L);

        // Configurar request mocks
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

        // Configurar response mock
        officeResponse = OfficeResponse.builder()
                .id(officeId)
                .code("MAIN")
                .name("Oficina Principal")
                .city("Bogotá")
                .address("Calle 123")
                .phone("123456789")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Crear oficina - exitoso")
    void createOffice_Success() {
        // Given
        when(officeRepository.existsByCodeAndDeletedAtIsNull("MAIN")).thenReturn(false);
        when(officeMapper.toEntity(createRequest)).thenReturn(office);
        when(officeRepository.save(any(Office.class))).thenReturn(office);
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        OfficeResponse result = officeService.createOffice(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("MAIN");
        assertThat(result.getName()).isEqualTo("Oficina Principal");

        verify(officeRepository).existsByCodeAndDeletedAtIsNull("MAIN");
        verify(officeRepository).save(any(Office.class));
        verify(officeMapper).toEntity(createRequest);
        verify(officeMapper).toResponse(office);
    }

    @Test
    @DisplayName("Crear oficina - código duplicado lanza excepción")
    void createOffice_DuplicateCode_ThrowsException() {
        // Given
        when(officeRepository.existsByCodeAndDeletedAtIsNull("MAIN")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> officeService.createOffice(createRequest))
                .isInstanceOf(DuplicateCodeException.class)
                .hasMessageContaining("MAIN");

        verify(officeRepository).existsByCodeAndDeletedAtIsNull("MAIN");
        verify(officeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar oficina - exitoso")
    void updateOffice_Success() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(securityUtils.getCurrentUserId()).thenReturn(2L);
        when(officeRepository.save(office)).thenReturn(office);
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        OfficeResponse result = officeService.updateOffice(officeId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(officeRepository).save(office);
        verify(securityUtils).getCurrentUserId();
    }

    @Test
    @DisplayName("Actualizar oficina - no encontrada lanza excepción")
    void updateOffice_NotFound_ThrowsException() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> officeService.updateOffice(officeId, updateRequest))
                .isInstanceOf(OfficeNotFoundException.class);

        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(officeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar oficina - exitoso")
    void deleteOffice_Success() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(warehouseRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(0L);
        when(tireSupplierRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(0L);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(officeRepository.save(office)).thenReturn(office);

        // When
        officeService.deleteOffice(officeId);

        // Then
        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(warehouseRepository).countByOfficeIdAndDeletedAtIsNull(officeId);
        verify(tireSupplierRepository).countByOfficeIdAndDeletedAtIsNull(officeId);
        verify(officeRepository).save(office);
        assertThat(office.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Eliminar oficina - con almacenes activos lanza excepción")
    void deleteOffice_WithActiveWarehouses_ThrowsException() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(warehouseRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(3L);

        // When/Then
        assertThatThrownBy(() -> officeService.deleteOffice(officeId))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining("almacenes");

        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(warehouseRepository).countByOfficeIdAndDeletedAtIsNull(officeId);
        verify(officeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar oficina - con proveedores activos lanza excepción")
    void deleteOffice_WithActiveSuppliers_ThrowsException() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(warehouseRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(0L);
        when(tireSupplierRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(2L);

        // When/Then
        assertThatThrownBy(() -> officeService.deleteOffice(officeId))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining("proveedores");

        verify(tireSupplierRepository).countByOfficeIdAndDeletedAtIsNull(officeId);
        verify(officeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener oficina por ID - exitoso")
    void getOfficeById_Success() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        OfficeResponse result = officeService.getOfficeById(officeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(officeId);
        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
    }

    @Test
    @DisplayName("Obtener oficina por código - exitoso")
    void getOfficeByCode_Success() {
        // Given
        when(officeRepository.findByCodeAndDeletedAtIsNull("MAIN")).thenReturn(Optional.of(office));
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        OfficeResponse result = officeService.getOfficeByCode("MAIN");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("MAIN");
        verify(officeRepository).findByCodeAndDeletedAtIsNull("MAIN");
    }

    @Test
    @DisplayName("Listar oficinas - admin nacional ve todas")
    void listAllOffices_NationalAdmin_ReturnsAll() {
        // Given
        Office office2 = new Office("CALI", "Oficina Cali", "Cali", "Av 123", "111222");
        office2.setId(UUID.randomUUID());
        List<Office> offices = List.of(office, office2);

        when(securityUtils.isNationalAdmin()).thenReturn(true);
        when(officeRepository.findAllActive()).thenReturn(offices);
        when(officeMapper.toResponse(any(Office.class))).thenReturn(officeResponse);

        // When
        List<OfficeResponse> result = officeService.listAllOffices();

        // Then
        assertThat(result).hasSize(2);
        verify(officeRepository).findAllActive();
        verify(officeMapper, times(2)).toResponse(any(Office.class));
    }

    @Test
    @DisplayName("Listar oficinas - usuario normal ve solo su oficina")
    void listAllOffices_RegularUser_ReturnsUserOffice() {
        // Given
        when(securityUtils.isNationalAdmin()).thenReturn(false);
        when(securityUtils.getCurrentUserOfficeId()).thenReturn(officeId);
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        List<OfficeResponse> result = officeService.listAllOffices();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(officeId);
        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
    }

    @Test
    @DisplayName("Buscar oficinas con paginación - exitoso")
    void searchOffices_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Office> officePage = new PageImpl<>(List.of(office));

        when(officeRepository.searchByNameOrCity("Bogotá", pageable)).thenReturn(officePage);
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        Page<OfficeResponse> result = officeService.searchOffices("Bogotá", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(officeRepository).searchByNameOrCity("Bogotá", pageable);
    }

    @Test
    @DisplayName("Obtener oficina con detalles - exitoso")
    void getOfficeWithDetails_Success() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(warehouseRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(5L);
        when(tireSupplierRepository.countByOfficeIdAndDeletedAtIsNull(officeId)).thenReturn(3L);

        OfficeResponse detailedResponse = OfficeResponse.builder()
                .id(officeId)
                .code("MAIN")
                .name("Oficina Principal")
                .totalWarehouses(5L)
                .totalSuppliers(3L)
                .totalUsers(0L)
                .build();

        when(officeMapper.toResponseWithDetails(office, 5L, 3L, 0L)).thenReturn(detailedResponse);

        // When
        OfficeResponse result = officeService.getOfficeWithDetails(officeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalWarehouses()).isEqualTo(5L);
        assertThat(result.getTotalSuppliers()).isEqualTo(3L);
        verify(warehouseRepository).countByOfficeIdAndDeletedAtIsNull(officeId);
        verify(tireSupplierRepository).countByOfficeIdAndDeletedAtIsNull(officeId);
    }

    @Test
    @DisplayName("Activar/Desactivar oficina - exitoso")
    void setOfficeActive_Success() {
        // Given
        when(officeRepository.findByIdAndDeletedAtIsNull(officeId)).thenReturn(Optional.of(office));
        when(officeRepository.save(office)).thenReturn(office);
        when(officeMapper.toResponse(office)).thenReturn(officeResponse);

        // When
        OfficeResponse result = officeService.setOfficeActive(officeId, false);

        // Then
        assertThat(result).isNotNull();
        verify(officeRepository).findByIdAndDeletedAtIsNull(officeId);
        verify(officeRepository).save(office);
        assertThat(office.getIsActive()).isFalse();
    }
}
