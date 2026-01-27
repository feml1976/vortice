package com.transer.vortice.tire.application.service;

import com.transer.vortice.tire.application.dto.response.TireBrandResponse;
import com.transer.vortice.tire.application.dto.response.TireReferenceResponse;
import com.transer.vortice.tire.application.dto.response.TireSupplierResponse;
import com.transer.vortice.tire.application.dto.response.TireTypeResponse;
import com.transer.vortice.tire.application.mapper.TireSpecificationMapper;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import com.transer.vortice.tire.domain.repository.TireBrandRepository;
import com.transer.vortice.tire.domain.repository.TireReferenceRepository;
import com.transer.vortice.tire.domain.repository.TireSupplierRepository;
import com.transer.vortice.tire.domain.repository.TireTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TireCatalogService.
 * Usa Mockito para simular dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TireCatalogService Tests")
class TireCatalogServiceTest {

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

    @InjectMocks
    private TireCatalogService tireCatalogService;

    private TireBrand activeBrand;
    private TireBrand inactiveBrand;
    private TireType activeType;
    private TireType inactiveType;
    private TireReference activeReference;
    private TireReference inactiveReference;
    private TireSupplier activeSupplier;
    private TireSupplier inactiveSupplier;

    private TireBrandResponse brandResponse;
    private TireTypeResponse typeResponse;
    private TireReferenceResponse referenceResponse;
    private TireSupplierResponse supplierResponse;

    @BeforeEach
    void setUp() {
        // Crear marcas de prueba
        activeBrand = new TireBrand();
        activeBrand.setId(UUID.randomUUID());
        activeBrand.setName("Michelin");
        activeBrand.setIsActive(true);

        inactiveBrand = new TireBrand();
        inactiveBrand.setId(UUID.randomUUID());
        inactiveBrand.setName("Goodyear");
        inactiveBrand.setIsActive(false);

        // Crear tipos de prueba
        activeType = new TireType();
        activeType.setId(UUID.randomUUID());
        activeType.setName("Radial");
        activeType.setIsActive(true);

        inactiveType = new TireType();
        inactiveType.setId(UUID.randomUUID());
        inactiveType.setName("Convencional");
        inactiveType.setIsActive(false);

        // Crear referencias de prueba
        activeReference = new TireReference();
        activeReference.setId(UUID.randomUUID());
        activeReference.setCode("295/80R22.5");
        activeReference.setIsActive(true);

        inactiveReference = new TireReference();
        inactiveReference.setId(UUID.randomUUID());
        inactiveReference.setCode("275/70R22.5");
        inactiveReference.setIsActive(false);

        // Crear proveedores de prueba
        activeSupplier = new TireSupplier();
        activeSupplier.setId(UUID.randomUUID());
        activeSupplier.setName("Proveedor Activo");
        activeSupplier.setIsActive(true);

        inactiveSupplier = new TireSupplier();
        inactiveSupplier.setId(UUID.randomUUID());
        inactiveSupplier.setName("Proveedor Inactivo");
        inactiveSupplier.setIsActive(false);

        // Crear respuestas de prueba
        brandResponse = new TireBrandResponse();
        brandResponse.setId(activeBrand.getId());
        brandResponse.setName("Michelin");

        typeResponse = new TireTypeResponse();
        typeResponse.setId(activeType.getId());
        typeResponse.setName("Radial");

        referenceResponse = new TireReferenceResponse();
        referenceResponse.setId(activeReference.getId());
        referenceResponse.setCode("295/80R22.5");

        supplierResponse = new TireSupplierResponse();
        supplierResponse.setId(activeSupplier.getId());
        supplierResponse.setName("Proveedor Activo");
    }

    // =====================================================
    // TESTS: Marcas de Llantas
    // =====================================================

    @Test
    @DisplayName("Debe obtener todas las marcas activas")
    void shouldGetAllActiveBrands() {
        // Given
        List<TireBrand> activeBrands = List.of(activeBrand);
        when(tireBrandRepository.findAllActive()).thenReturn(activeBrands);
        when(mapper.toBrandResponse(activeBrand)).thenReturn(brandResponse);

        // When
        List<TireBrandResponse> result = tireCatalogService.getAllActiveBrands();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Michelin");

        verify(tireBrandRepository, times(1)).findAllActive();
        verify(mapper, times(1)).toBrandResponse(activeBrand);
    }

    @Test
    @DisplayName("Debe obtener todas las marcas (activas e inactivas)")
    void shouldGetAllBrands() {
        // Given
        List<TireBrand> allBrands = List.of(activeBrand, inactiveBrand);
        TireBrandResponse inactiveBrandResponse = new TireBrandResponse();
        inactiveBrandResponse.setId(inactiveBrand.getId());
        inactiveBrandResponse.setName("Goodyear");

        when(tireBrandRepository.findAll()).thenReturn(allBrands);
        when(mapper.toBrandResponse(activeBrand)).thenReturn(brandResponse);
        when(mapper.toBrandResponse(inactiveBrand)).thenReturn(inactiveBrandResponse);

        // When
        List<TireBrandResponse> result = tireCatalogService.getAllBrands();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(tireBrandRepository, times(1)).findAll();
        verify(mapper, times(2)).toBrandResponse(any(TireBrand.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay marcas activas")
    void shouldReturnEmptyListWhenNoActiveBrands() {
        // Given
        when(tireBrandRepository.findAllActive()).thenReturn(List.of());

        // When
        List<TireBrandResponse> result = tireCatalogService.getAllActiveBrands();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(tireBrandRepository, times(1)).findAllActive();
        verify(mapper, never()).toBrandResponse(any());
    }

    // =====================================================
    // TESTS: Tipos de Llantas
    // =====================================================

    @Test
    @DisplayName("Debe obtener todos los tipos activos")
    void shouldGetAllActiveTypes() {
        // Given
        List<TireType> activeTypes = List.of(activeType);
        when(tireTypeRepository.findAllActive()).thenReturn(activeTypes);
        when(mapper.toTypeResponse(activeType)).thenReturn(typeResponse);

        // When
        List<TireTypeResponse> result = tireCatalogService.getAllActiveTypes();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Radial");

        verify(tireTypeRepository, times(1)).findAllActive();
        verify(mapper, times(1)).toTypeResponse(activeType);
    }

    @Test
    @DisplayName("Debe obtener todos los tipos (activos e inactivos)")
    void shouldGetAllTypes() {
        // Given
        List<TireType> allTypes = List.of(activeType, inactiveType);
        TireTypeResponse inactiveTypeResponse = new TireTypeResponse();
        inactiveTypeResponse.setId(inactiveType.getId());
        inactiveTypeResponse.setName("Convencional");

        when(tireTypeRepository.findAll()).thenReturn(allTypes);
        when(mapper.toTypeResponse(activeType)).thenReturn(typeResponse);
        when(mapper.toTypeResponse(inactiveType)).thenReturn(inactiveTypeResponse);

        // When
        List<TireTypeResponse> result = tireCatalogService.getAllTypes();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(tireTypeRepository, times(1)).findAll();
        verify(mapper, times(2)).toTypeResponse(any(TireType.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tipos activos")
    void shouldReturnEmptyListWhenNoActiveTypes() {
        // Given
        when(tireTypeRepository.findAllActive()).thenReturn(List.of());

        // When
        List<TireTypeResponse> result = tireCatalogService.getAllActiveTypes();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(tireTypeRepository, times(1)).findAllActive();
        verify(mapper, never()).toTypeResponse(any());
    }

    // =====================================================
    // TESTS: Referencias de Llantas
    // =====================================================

    @Test
    @DisplayName("Debe obtener todas las referencias activas")
    void shouldGetAllActiveReferences() {
        // Given
        List<TireReference> activeReferences = List.of(activeReference);
        when(tireReferenceRepository.findAllActive()).thenReturn(activeReferences);
        when(mapper.toReferenceResponse(activeReference)).thenReturn(referenceResponse);

        // When
        List<TireReferenceResponse> result = tireCatalogService.getAllActiveReferences();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("295/80R22.5");

        verify(tireReferenceRepository, times(1)).findAllActive();
        verify(mapper, times(1)).toReferenceResponse(activeReference);
    }

    @Test
    @DisplayName("Debe obtener todas las referencias (activas e inactivas)")
    void shouldGetAllReferences() {
        // Given
        List<TireReference> allReferences = List.of(activeReference, inactiveReference);
        TireReferenceResponse inactiveReferenceResponse = new TireReferenceResponse();
        inactiveReferenceResponse.setId(inactiveReference.getId());
        inactiveReferenceResponse.setCode("275/70R22.5");

        when(tireReferenceRepository.findAll()).thenReturn(allReferences);
        when(mapper.toReferenceResponse(activeReference)).thenReturn(referenceResponse);
        when(mapper.toReferenceResponse(inactiveReference)).thenReturn(inactiveReferenceResponse);

        // When
        List<TireReferenceResponse> result = tireCatalogService.getAllReferences();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(tireReferenceRepository, times(1)).findAll();
        verify(mapper, times(2)).toReferenceResponse(any(TireReference.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay referencias activas")
    void shouldReturnEmptyListWhenNoActiveReferences() {
        // Given
        when(tireReferenceRepository.findAllActive()).thenReturn(List.of());

        // When
        List<TireReferenceResponse> result = tireCatalogService.getAllActiveReferences();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(tireReferenceRepository, times(1)).findAllActive();
        verify(mapper, never()).toReferenceResponse(any());
    }

    // =====================================================
    // TESTS: Proveedores de Llantas
    // =====================================================

    @Test
    @DisplayName("Debe obtener todos los proveedores activos")
    void shouldGetAllActiveSuppliers() {
        // Given
        List<TireSupplier> activeSuppliers = List.of(activeSupplier);
        when(tireSupplierRepository.findAllActive()).thenReturn(activeSuppliers);
        when(mapper.toSupplierResponse(activeSupplier)).thenReturn(supplierResponse);

        // When
        List<TireSupplierResponse> result = tireCatalogService.getAllActiveSuppliers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Proveedor Activo");

        verify(tireSupplierRepository, times(1)).findAllActive();
        verify(mapper, times(1)).toSupplierResponse(activeSupplier);
    }

    @Test
    @DisplayName("Debe obtener todos los proveedores (activos e inactivos)")
    void shouldGetAllSuppliers() {
        // Given
        List<TireSupplier> allSuppliers = List.of(activeSupplier, inactiveSupplier);
        TireSupplierResponse inactiveSupplierResponse = new TireSupplierResponse();
        inactiveSupplierResponse.setId(inactiveSupplier.getId());
        inactiveSupplierResponse.setName("Proveedor Inactivo");

        when(tireSupplierRepository.findAll()).thenReturn(allSuppliers);
        when(mapper.toSupplierResponse(activeSupplier)).thenReturn(supplierResponse);
        when(mapper.toSupplierResponse(inactiveSupplier)).thenReturn(inactiveSupplierResponse);

        // When
        List<TireSupplierResponse> result = tireCatalogService.getAllSuppliers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(tireSupplierRepository, times(1)).findAll();
        verify(mapper, times(2)).toSupplierResponse(any(TireSupplier.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay proveedores activos")
    void shouldReturnEmptyListWhenNoActiveSuppliers() {
        // Given
        when(tireSupplierRepository.findAllActive()).thenReturn(List.of());

        // When
        List<TireSupplierResponse> result = tireCatalogService.getAllActiveSuppliers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(tireSupplierRepository, times(1)).findAllActive();
        verify(mapper, never()).toSupplierResponse(any());
    }

    // =====================================================
    // TESTS: Integración entre Catálogos
    // =====================================================

    @Test
    @DisplayName("Debe invocar servicios de catálogos independientemente")
    void shouldInvokeCatalogServicesIndependently() {
        // Given
        when(tireBrandRepository.findAllActive()).thenReturn(List.of(activeBrand));
        when(tireTypeRepository.findAllActive()).thenReturn(List.of(activeType));
        when(tireReferenceRepository.findAllActive()).thenReturn(List.of(activeReference));
        when(tireSupplierRepository.findAllActive()).thenReturn(List.of(activeSupplier));
        when(mapper.toBrandResponse(any())).thenReturn(brandResponse);
        when(mapper.toTypeResponse(any())).thenReturn(typeResponse);
        when(mapper.toReferenceResponse(any())).thenReturn(referenceResponse);
        when(mapper.toSupplierResponse(any())).thenReturn(supplierResponse);

        // When
        List<TireBrandResponse> brands = tireCatalogService.getAllActiveBrands();
        List<TireTypeResponse> types = tireCatalogService.getAllActiveTypes();
        List<TireReferenceResponse> references = tireCatalogService.getAllActiveReferences();
        List<TireSupplierResponse> suppliers = tireCatalogService.getAllActiveSuppliers();

        // Then
        assertThat(brands).hasSize(1);
        assertThat(types).hasSize(1);
        assertThat(references).hasSize(1);
        assertThat(suppliers).hasSize(1);

        verify(tireBrandRepository, times(1)).findAllActive();
        verify(tireTypeRepository, times(1)).findAllActive();
        verify(tireReferenceRepository, times(1)).findAllActive();
        verify(tireSupplierRepository, times(1)).findAllActive();
    }
}
