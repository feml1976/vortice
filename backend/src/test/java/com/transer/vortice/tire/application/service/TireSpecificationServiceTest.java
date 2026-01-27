package com.transer.vortice.tire.application.service;

import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import com.transer.vortice.shared.infrastructure.exception.ValidationException;
import com.transer.vortice.tire.application.dto.request.CreateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.request.UpdateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.response.TireSpecificationResponse;
import com.transer.vortice.tire.application.dto.response.TireSpecificationSummaryResponse;
import com.transer.vortice.tire.application.mapper.TireSpecificationMapper;
import com.transer.vortice.tire.domain.model.TireSpecification;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import com.transer.vortice.tire.domain.repository.*;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TireSpecificationService.
 * Usa Mockito para simular dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TireSpecificationService Tests")
class TireSpecificationServiceTest {

    @Mock
    private TireSpecificationRepository tireSpecificationRepository;

    @Mock
    private TireBrandRepository tireBrandRepository;

    @Mock
    private TireTypeRepository tireTypeRepository;

    @Mock
    private TireReferenceRepository tireReferenceRepository;

    @Mock
    private TireSupplierRepository tireSupplierRepository;

    @Mock
    private TireSpecificationMapper mapper;

    @Mock
    private CodeGeneratorService codeGeneratorService;

    @InjectMocks
    private TireSpecificationService tireSpecificationService;

    private UUID testId;
    private TireSpecification testSpecification;
    private TireBrand testBrand;
    private TireType testType;
    private TireReference testReference;
    private TireSupplier testSupplier;
    private CreateTireSpecificationRequest createRequest;
    private UpdateTireSpecificationRequest updateRequest;
    private TireSpecificationResponse testResponse;
    private TireSpecificationSummaryResponse testSummaryResponse;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        // Crear catálogos de prueba
        testBrand = new TireBrand();
        testBrand.setId(UUID.randomUUID());
        testBrand.setName("Michelin");
        testBrand.setIsActive(true);

        testType = new TireType();
        testType.setId(UUID.randomUUID());
        testType.setName("Radial");
        testType.setIsActive(true);

        testReference = new TireReference();
        testReference.setId(UUID.randomUUID());
        testReference.setCode("295/80R22.5");
        testReference.setIsActive(true);

        testSupplier = new TireSupplier();
        testSupplier.setId(UUID.randomUUID());
        testSupplier.setName("Proveedor Test");
        testSupplier.setIsActive(true);

        // Crear especificación de prueba
        testSpecification = new TireSpecification();
        testSpecification.setId(testId);
        testSpecification.setCode("FT-000001");
        testSpecification.setBrand(testBrand);
        testSpecification.setType(testType);
        testSpecification.setReference(testReference);
        testSpecification.setMainProvider(testSupplier);
        testSpecification.setExpectedMileage(150000);
        testSpecification.setMileageRangeMin(100000);
        testSpecification.setMileageRangeAvg(150000);
        testSpecification.setMileageRangeMax(200000);
        testSpecification.setExpectedRetreads((short) 2);
        testSpecification.setInitialDepthInternalMm(new BigDecimal("18.0"));
        testSpecification.setInitialDepthCentralMm(new BigDecimal("18.0"));
        testSpecification.setInitialDepthExternalMm(new BigDecimal("18.0"));

        // Crear request de creación
        createRequest = new CreateTireSpecificationRequest();
        createRequest.setBrandId(testBrand.getId());
        createRequest.setTypeId(testType.getId());
        createRequest.setReferenceId(testReference.getId());
        createRequest.setMainProviderId(testSupplier.getId());
        createRequest.setExpectedMileage(150000);
        createRequest.setMileageRangeMin(100000);
        createRequest.setMileageRangeAvg(150000);
        createRequest.setMileageRangeMax(200000);
        createRequest.setExpectedRetreads((short) 2);
        createRequest.setInitialDepthInternalMm(new BigDecimal("18.0"));
        createRequest.setInitialDepthCentralMm(new BigDecimal("18.0"));
        createRequest.setInitialDepthExternalMm(new BigDecimal("18.0"));

        // Crear request de actualización
        updateRequest = new UpdateTireSpecificationRequest();
        updateRequest.setBrandId(testBrand.getId());
        updateRequest.setTypeId(testType.getId());
        updateRequest.setReferenceId(testReference.getId());
        updateRequest.setMainProviderId(testSupplier.getId());
        updateRequest.setExpectedMileage(160000);
        updateRequest.setMileageRangeMin(120000);
        updateRequest.setMileageRangeAvg(160000);
        updateRequest.setMileageRangeMax(220000);
        updateRequest.setExpectedRetreads((short) 3);
        updateRequest.setInitialDepthInternalMm(new BigDecimal("19.0"));
        updateRequest.setInitialDepthCentralMm(new BigDecimal("19.0"));
        updateRequest.setInitialDepthExternalMm(new BigDecimal("19.0"));

        // Crear respuestas de prueba
        testResponse = new TireSpecificationResponse();
        testResponse.setId(testId);
        testResponse.setCode("FT-000001");

        testSummaryResponse = new TireSpecificationSummaryResponse();
        testSummaryResponse.setId(testId);
        testSummaryResponse.setCode("FT-000001");
    }

    // =====================================================
    // TESTS: Crear Especificación Técnica
    // =====================================================

    @Test
    @DisplayName("Debe crear especificación técnica exitosamente")
    void shouldCreateTireSpecificationSuccessfully() {
        // Given
        when(tireBrandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(tireTypeRepository.findById(testType.getId())).thenReturn(Optional.of(testType));
        when(tireReferenceRepository.findById(testReference.getId())).thenReturn(Optional.of(testReference));
        when(tireSupplierRepository.findById(testSupplier.getId())).thenReturn(Optional.of(testSupplier));
        when(mapper.toEntity(createRequest)).thenReturn(testSpecification);
        when(codeGeneratorService.generateTireSpecificationCode()).thenReturn("FT-000001");
        when(tireSpecificationRepository.save(any(TireSpecification.class))).thenReturn(testSpecification);
        when(mapper.toResponse(testSpecification)).thenReturn(testResponse);

        // When
        TireSpecificationResponse response = tireSpecificationService.createTireSpecification(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testId);
        assertThat(response.getCode()).isEqualTo("FT-000001");

        verify(tireBrandRepository, times(1)).findById(testBrand.getId());
        verify(tireTypeRepository, times(1)).findById(testType.getId());
        verify(tireReferenceRepository, times(1)).findById(testReference.getId());
        verify(codeGeneratorService, times(1)).generateTireSpecificationCode();
        verify(tireSpecificationRepository, times(1)).save(any(TireSpecification.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando marca no existe")
    void shouldThrowExceptionWhenBrandNotFound() {
        // Given
        when(tireBrandRepository.findById(testBrand.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.createTireSpecification(createRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Marca de llanta no encontrada");

        verify(tireBrandRepository, times(1)).findById(testBrand.getId());
        verify(tireSpecificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo no existe")
    void shouldThrowExceptionWhenTypeNotFound() {
        // Given
        when(tireBrandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(tireTypeRepository.findById(testType.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.createTireSpecification(createRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Tipo de llanta no encontrado");

        verify(tireSpecificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando referencia no existe")
    void shouldThrowExceptionWhenReferenceNotFound() {
        // Given
        when(tireBrandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(tireTypeRepository.findById(testType.getId())).thenReturn(Optional.of(testType));
        when(tireReferenceRepository.findById(testReference.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.createTireSpecification(createRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Referencia de llanta no encontrada");

        verify(tireSpecificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe crear especificación sin proveedores opcionales")
    void shouldCreateSpecificationWithoutOptionalSuppliers() {
        // Given
        createRequest.setMainProviderId(null);
        when(tireBrandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(tireTypeRepository.findById(testType.getId())).thenReturn(Optional.of(testType));
        when(tireReferenceRepository.findById(testReference.getId())).thenReturn(Optional.of(testReference));
        when(mapper.toEntity(createRequest)).thenReturn(testSpecification);
        when(codeGeneratorService.generateTireSpecificationCode()).thenReturn("FT-000001");
        when(tireSpecificationRepository.save(any(TireSpecification.class))).thenReturn(testSpecification);
        when(mapper.toResponse(testSpecification)).thenReturn(testResponse);

        // When
        TireSpecificationResponse response = tireSpecificationService.createTireSpecification(createRequest);

        // Then
        assertThat(response).isNotNull();
        verify(tireSupplierRepository, never()).findById(any());
    }

    // =====================================================
    // TESTS: Listar Especificaciones Técnicas
    // =====================================================

    @Test
    @DisplayName("Debe listar especificaciones con paginación")
    void shouldListTireSpecificationsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> page = new PageImpl<>(List.of(testSpecification), pageable, 1);
        when(tireSpecificationRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toSummaryResponse(testSpecification)).thenReturn(testSummaryResponse);

        // When
        Page<TireSpecificationSummaryResponse> result = tireSpecificationService.listTireSpecifications(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(testId);

        verify(tireSpecificationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Debe listar todas las especificaciones activas sin paginación")
    void shouldListAllActiveSpecificationsWithoutPagination() {
        // Given
        List<TireSpecification> specifications = List.of(testSpecification);
        when(tireSpecificationRepository.findAllActive()).thenReturn(specifications);
        when(mapper.toSummaryResponse(testSpecification)).thenReturn(testSummaryResponse);

        // When
        List<TireSpecificationSummaryResponse> result = tireSpecificationService.listActiveTireSpecifications();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testId);

        verify(tireSpecificationRepository, times(1)).findAllActive();
    }

    // =====================================================
    // TESTS: Obtener Especificación por ID
    // =====================================================

    @Test
    @DisplayName("Debe obtener especificación por ID exitosamente")
    void shouldGetTireSpecificationByIdSuccessfully() {
        // Given
        when(tireSpecificationRepository.findByIdWithRelations(testId)).thenReturn(Optional.of(testSpecification));
        when(mapper.toResponse(testSpecification)).thenReturn(testResponse);

        // When
        TireSpecificationResponse response = tireSpecificationService.getTireSpecificationById(testId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testId);

        verify(tireSpecificationRepository, times(1)).findByIdWithRelations(testId);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando especificación no existe por ID")
    void shouldThrowExceptionWhenSpecificationNotFoundById() {
        // Given
        when(tireSpecificationRepository.findByIdWithRelations(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.getTireSpecificationById(testId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Especificación técnica no encontrada con ID");

        verify(tireSpecificationRepository, times(1)).findByIdWithRelations(testId);
    }

    // =====================================================
    // TESTS: Obtener Especificación por Código
    // =====================================================

    @Test
    @DisplayName("Debe obtener especificación por código exitosamente")
    void shouldGetTireSpecificationByCodeSuccessfully() {
        // Given
        String code = "FT-000001";
        when(tireSpecificationRepository.findActiveByCode(code)).thenReturn(Optional.of(testSpecification));
        when(mapper.toResponse(testSpecification)).thenReturn(testResponse);

        // When
        TireSpecificationResponse response = tireSpecificationService.getTireSpecificationByCode(code);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(code);

        verify(tireSpecificationRepository, times(1)).findActiveByCode(code);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando especificación no existe por código")
    void shouldThrowExceptionWhenSpecificationNotFoundByCode() {
        // Given
        String code = "FT-999999";
        when(tireSpecificationRepository.findActiveByCode(code)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.getTireSpecificationByCode(code))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Especificación técnica no encontrada con código");

        verify(tireSpecificationRepository, times(1)).findActiveByCode(code);
    }

    // =====================================================
    // TESTS: Actualizar Especificación Técnica
    // =====================================================

    @Test
    @DisplayName("Debe actualizar especificación exitosamente")
    void shouldUpdateTireSpecificationSuccessfully() {
        // Given
        when(tireSpecificationRepository.findById(testId)).thenReturn(Optional.of(testSpecification));
        when(tireBrandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(tireTypeRepository.findById(testType.getId())).thenReturn(Optional.of(testType));
        when(tireReferenceRepository.findById(testReference.getId())).thenReturn(Optional.of(testReference));
        when(tireSupplierRepository.findById(testSupplier.getId())).thenReturn(Optional.of(testSupplier));
        doNothing().when(mapper).updateEntity(testSpecification, updateRequest);
        when(tireSpecificationRepository.save(any(TireSpecification.class))).thenReturn(testSpecification);
        when(mapper.toResponse(testSpecification)).thenReturn(testResponse);

        // When
        TireSpecificationResponse response = tireSpecificationService.updateTireSpecification(testId, updateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testId);

        verify(tireSpecificationRepository, times(1)).findById(testId);
        verify(mapper, times(1)).updateEntity(testSpecification, updateRequest);
        verify(tireSpecificationRepository, times(1)).save(testSpecification);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar especificación eliminada")
    void shouldThrowExceptionWhenUpdatingDeletedSpecification() {
        // Given
        testSpecification.markAsDeleted(1L);
        when(tireSpecificationRepository.findById(testId)).thenReturn(Optional.of(testSpecification));

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.updateTireSpecification(testId, updateRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No se puede actualizar una especificación técnica eliminada");

        verify(tireSpecificationRepository, times(1)).findById(testId);
        verify(tireSpecificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando especificación no existe para actualizar")
    void shouldThrowExceptionWhenSpecificationNotFoundForUpdate() {
        // Given
        when(tireSpecificationRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.updateTireSpecification(testId, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Especificación técnica no encontrada");

        verify(tireSpecificationRepository, never()).save(any());
    }

    // =====================================================
    // TESTS: Eliminar Especificación Técnica (Soft Delete)
    // =====================================================

    @Test
    @DisplayName("Debe eliminar especificación exitosamente (soft delete)")
    void shouldDeleteTireSpecificationSuccessfully() {
        // Given
        when(tireSpecificationRepository.findById(testId)).thenReturn(Optional.of(testSpecification));
        when(tireSpecificationRepository.save(any(TireSpecification.class))).thenReturn(testSpecification);

        // When
        tireSpecificationService.deleteTireSpecification(testId);

        // Then
        verify(tireSpecificationRepository, times(1)).findById(testId);
        verify(tireSpecificationRepository, times(1)).save(testSpecification);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar especificación ya eliminada")
    void shouldThrowExceptionWhenDeletingAlreadyDeletedSpecification() {
        // Given
        testSpecification.markAsDeleted(1L);
        when(tireSpecificationRepository.findById(testId)).thenReturn(Optional.of(testSpecification));

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.deleteTireSpecification(testId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("La especificación técnica ya está eliminada");

        verify(tireSpecificationRepository, times(1)).findById(testId);
        verify(tireSpecificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando especificación no existe para eliminar")
    void shouldThrowExceptionWhenSpecificationNotFoundForDelete() {
        // Given
        when(tireSpecificationRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.deleteTireSpecification(testId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Especificación técnica no encontrada");

        verify(tireSpecificationRepository, never()).save(any());
    }

    // =====================================================
    // TESTS: Buscar Especificaciones Técnicas
    // =====================================================

    @Test
    @DisplayName("Debe buscar especificaciones por texto")
    void shouldSearchTireSpecificationsByText() {
        // Given
        String searchText = "Michelin";
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> page = new PageImpl<>(List.of(testSpecification), pageable, 1);
        when(tireSpecificationRepository.searchByText(searchText, pageable)).thenReturn(page);
        when(mapper.toSummaryResponse(testSpecification)).thenReturn(testSummaryResponse);

        // When
        Page<TireSpecificationSummaryResponse> result = tireSpecificationService.searchTireSpecifications(searchText, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(tireSpecificationRepository, times(1)).searchByText(searchText, pageable);
    }

    @Test
    @DisplayName("Debe buscar especificaciones con filtros múltiples")
    void shouldFindSpecificationsByMultipleFilters() {
        // Given
        UUID brandId = testBrand.getId();
        UUID typeId = testType.getId();
        UUID referenceId = testReference.getId();
        Boolean isActive = true;
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> page = new PageImpl<>(List.of(testSpecification), pageable, 1);

        when(tireSpecificationRepository.findByFilters(brandId, typeId, referenceId, isActive, pageable))
                .thenReturn(page);
        when(mapper.toSummaryResponse(testSpecification)).thenReturn(testSummaryResponse);

        // When
        Page<TireSpecificationSummaryResponse> result = tireSpecificationService.findByFilters(
                brandId, typeId, referenceId, isActive, pageable
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(tireSpecificationRepository, times(1)).findByFilters(brandId, typeId, referenceId, isActive, pageable);
    }

    @Test
    @DisplayName("Debe buscar especificaciones por marca")
    void shouldFindSpecificationsByBrand() {
        // Given
        UUID brandId = testBrand.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> page = new PageImpl<>(List.of(testSpecification), pageable, 1);

        when(tireBrandRepository.findById(brandId)).thenReturn(Optional.of(testBrand));
        when(tireSpecificationRepository.findByBrandId(brandId, pageable)).thenReturn(page);
        when(mapper.toSummaryResponse(testSpecification)).thenReturn(testSummaryResponse);

        // When
        Page<TireSpecificationSummaryResponse> result = tireSpecificationService.findByBrand(brandId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(tireBrandRepository, times(1)).findById(brandId);
        verify(tireSpecificationRepository, times(1)).findByBrandId(brandId, pageable);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar por marca no existente")
    void shouldThrowExceptionWhenSearchingByNonExistentBrand() {
        // Given
        UUID brandId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(tireBrandRepository.findById(brandId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tireSpecificationService.findByBrand(brandId, pageable))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Marca de llanta no encontrada");

        verify(tireBrandRepository, times(1)).findById(brandId);
        verify(tireSpecificationRepository, never()).findByBrandId(any(), any());
    }
}
