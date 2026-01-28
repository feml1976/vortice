package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.shared.infrastructure.BaseRepositoryTest;
import com.transer.vortice.tire.domain.model.TireSpecification;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para TireSpecificationRepository.
 * Usa Testcontainers con PostgreSQL para ejecutar tests contra una BD real.
 *
 * @author Vórtice Development Team
 */
@DisplayName("TireSpecificationRepository Integration Tests")
class TireSpecificationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TireSpecificationRepository tireSpecificationRepository;

    @Autowired
    private TireBrandRepository tireBrandRepository;

    @Autowired
    private TireTypeRepository tireTypeRepository;

    @Autowired
    private TireReferenceRepository tireReferenceRepository;

    @Autowired
    private TireCatalogSupplierRepository tireCatalogSupplierRepository;

    @Autowired
    private TestEntityManager entityManager;

    private TireBrand testBrand;
    private TireType testType;
    private TireReference testReference;
    private TireSupplier testSupplier;
    private TireSpecification testSpecification;

    @BeforeEach
    void setUp() {
        // Crear catálogos de prueba
        testBrand = new TireBrand();
        testBrand.setCode("MCH");
        testBrand.setName("Michelin");
        testBrand.setIsActive(true);
        testBrand = tireBrandRepository.save(testBrand);

        testType = new TireType();
        testType.setCode("RAD");
        testType.setName("Radial");
        testType.setDescription("Llanta tipo radial");
        testType.setIsActive(true);
        testType = tireTypeRepository.save(testType);

        testReference = new TireReference();
        testReference.setCode("295/80R22.5");
        testReference.setName("Medida estándar para camiones");
        testReference.setIsActive(true);
        testReference = tireReferenceRepository.save(testReference);

        testSupplier = new TireSupplier();
        testSupplier.setCode("PROV01");
        testSupplier.setName("Proveedor Principal");
        testSupplier.setTaxId("1234567890");
        testSupplier.setIsActive(true);
        testSupplier = tireCatalogSupplierRepository.save(testSupplier);

        // Crear especificación de prueba
        testSpecification = new TireSpecification();
        testSpecification.setCode("FT-000001");
        testSpecification.setBrand(testBrand);
        testSpecification.setType(testType);
        testSpecification.setReference(testReference);
        testSpecification.setMainProvider(testSupplier);
        testSpecification.setDimension("295/80R22.5");
        testSpecification.setExpectedMileage(150000);
        testSpecification.setMileageRangeMin(100000);
        testSpecification.setMileageRangeAvg(150000);
        testSpecification.setMileageRangeMax(200000);
        testSpecification.setExpectedRetreads((short) 2);
        testSpecification.setInitialDepthInternalMm(new BigDecimal("18.0"));
        testSpecification.setInitialDepthCentralMm(new BigDecimal("18.0"));
        testSpecification.setInitialDepthExternalMm(new BigDecimal("18.0"));
        testSpecification = tireSpecificationRepository.save(testSpecification);

        entityManager.flush();
        entityManager.clear();
    }

    // =====================================================
    // TESTS: Consultas Básicas
    // =====================================================

    @Test
    @DisplayName("Debe encontrar especificación por código")
    void shouldFindSpecificationByCode() {
        // When
        Optional<TireSpecification> found = tireSpecificationRepository.findByCode("FT-000001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("FT-000001");
        assertThat(found.get().getDimension()).isEqualTo("295/80R22.5");
    }

    @Test
    @DisplayName("Debe verificar si existe código")
    void shouldCheckIfCodeExists() {
        // When
        boolean exists = tireSpecificationRepository.existsByCode("FT-000001");
        boolean notExists = tireSpecificationRepository.existsByCode("FT-999999");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debe encontrar especificación activa por código")
    void shouldFindActiveSpecificationByCode() {
        // When
        Optional<TireSpecification> found = tireSpecificationRepository.findActiveByCode("FT-000001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("FT-000001");
        assertThat(found.get().isDeleted()).isFalse();
    }

    @Test
    @DisplayName("No debe encontrar especificación eliminada por código activo")
    void shouldNotFindDeletedSpecificationByActiveCode() {
        // Given - marcar como eliminada
        testSpecification.markAsDeleted(1L);
        tireSpecificationRepository.save(testSpecification);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<TireSpecification> found = tireSpecificationRepository.findActiveByCode("FT-000001");

        // Then
        assertThat(found).isEmpty();
    }

    // =====================================================
    // TESTS: Consultas con Relaciones
    // =====================================================

    @Test
    @DisplayName("Debe encontrar especificación con todas las relaciones cargadas")
    void shouldFindSpecificationWithAllRelations() {
        // When
        Optional<TireSpecification> found = tireSpecificationRepository.findByIdWithRelations(testSpecification.getId());

        // Then
        assertThat(found).isPresent();
        TireSpecification spec = found.get();
        assertThat(spec.getBrand()).isNotNull();
        assertThat(spec.getBrand().getName()).isEqualTo("Michelin");
        assertThat(spec.getType()).isNotNull();
        assertThat(spec.getType().getName()).isEqualTo("Radial");
        assertThat(spec.getReference()).isNotNull();
        assertThat(spec.getReference().getCode()).isEqualTo("295/80R22.5");
        assertThat(spec.getMainProvider()).isNotNull();
        assertThat(spec.getMainProvider().getName()).isEqualTo("Proveedor Principal");
    }

    // =====================================================
    // TESTS: Consultas de Búsqueda
    // =====================================================

    @Test
    @DisplayName("Debe buscar especificaciones por texto en código")
    void shouldSearchSpecificationsByCodeText() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.searchByText("FT-000001", pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getCode()).isEqualTo("FT-000001");
    }

    @Test
    @DisplayName("Debe buscar especificaciones por texto en marca")
    void shouldSearchSpecificationsByBrandText() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.searchByText("Michelin", pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getBrand().getName()).isEqualTo("Michelin");
    }

    @Test
    @DisplayName("Debe buscar especificaciones por texto en referencia")
    void shouldSearchSpecificationsByReferenceText() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.searchByText("295/80", pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getReference().getCode()).contains("295/80");
    }

    @Test
    @DisplayName("Debe retornar página vacía cuando no hay coincidencias de búsqueda")
    void shouldReturnEmptyPageWhenNoSearchMatches() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.searchByText("NoExiste", pageable);

        // Then
        assertThat(results.getContent()).isEmpty();
    }

    // =====================================================
    // TESTS: Filtros Múltiples
    // =====================================================

    @Test
    @DisplayName("Debe filtrar especificaciones por marca")
    void shouldFilterSpecificationsByBrand() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByFilters(
                testBrand.getId(), null, null, null, pageable
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getBrand().getId()).isEqualTo(testBrand.getId());
    }

    @Test
    @DisplayName("Debe filtrar especificaciones por tipo")
    void shouldFilterSpecificationsByType() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByFilters(
                null, testType.getId(), null, null, pageable
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getType().getId()).isEqualTo(testType.getId());
    }

    @Test
    @DisplayName("Debe filtrar especificaciones por referencia")
    void shouldFilterSpecificationsByReference() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByFilters(
                null, null, testReference.getId(), null, pageable
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getReference().getId()).isEqualTo(testReference.getId());
    }

    @Test
    @DisplayName("Debe filtrar especificaciones activas")
    void shouldFilterActiveSpecifications() {
        // Given - crear especificación inactiva
        TireSpecification inactiveSpec = new TireSpecification();
        inactiveSpec.setCode("FT-000002");
        inactiveSpec.setBrand(testBrand);
        inactiveSpec.setType(testType);
        inactiveSpec.setReference(testReference);
        inactiveSpec.setExpectedMileage(150000);
        inactiveSpec.setExpectedRetreads((short) 2);
        inactiveSpec.setInitialDepthInternalMm(new BigDecimal("18.0"));
        inactiveSpec.setInitialDepthCentralMm(new BigDecimal("18.0"));
        inactiveSpec.setInitialDepthExternalMm(new BigDecimal("18.0"));
        inactiveSpec.markAsDeleted(1L);
        tireSpecificationRepository.save(inactiveSpec);
        entityManager.flush();
        entityManager.clear();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByFilters(
                null, null, null, true, pageable
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getCode()).isEqualTo("FT-000001");
    }

    @Test
    @DisplayName("Debe filtrar con múltiples criterios combinados")
    void shouldFilterWithMultipleCriteria() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByFilters(
                testBrand.getId(), testType.getId(), testReference.getId(), true, pageable
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        TireSpecification spec = results.getContent().get(0);
        assertThat(spec.getBrand().getId()).isEqualTo(testBrand.getId());
        assertThat(spec.getType().getId()).isEqualTo(testType.getId());
        assertThat(spec.getReference().getId()).isEqualTo(testReference.getId());
    }

    // =====================================================
    // TESTS: Consultas por Marca
    // =====================================================

    @Test
    @DisplayName("Debe encontrar especificaciones por ID de marca")
    void shouldFindSpecificationsByBrandId() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByBrandId(testBrand.getId(), pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getBrand().getId()).isEqualTo(testBrand.getId());
    }

    @Test
    @DisplayName("Debe retornar página vacía cuando marca no tiene especificaciones")
    void shouldReturnEmptyPageWhenBrandHasNoSpecifications() {
        // Given - crear marca sin especificaciones
        TireBrand emptyBrand = new TireBrand();
        emptyBrand.setCode("GDY");
        emptyBrand.setName("Goodyear");
        emptyBrand.setIsActive(true);
        emptyBrand = tireBrandRepository.save(emptyBrand);
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<TireSpecification> results = tireSpecificationRepository.findByBrandId(emptyBrand.getId(), pageable);

        // Then
        assertThat(results.getContent()).isEmpty();
    }

    // =====================================================
    // TESTS: Listar Todas Activas
    // =====================================================

    @Test
    @DisplayName("Debe listar todas las especificaciones activas")
    void shouldListAllActiveSpecifications() {
        // Given - crear especificación adicional activa
        TireSpecification activeSpec = new TireSpecification();
        activeSpec.setCode("FT-000002");
        activeSpec.setBrand(testBrand);
        activeSpec.setType(testType);
        activeSpec.setReference(testReference);
        activeSpec.setExpectedMileage(180000);
        activeSpec.setExpectedRetreads((short) 3);
        activeSpec.setInitialDepthInternalMm(new BigDecimal("19.0"));
        activeSpec.setInitialDepthCentralMm(new BigDecimal("19.0"));
        activeSpec.setInitialDepthExternalMm(new BigDecimal("19.0"));
        tireSpecificationRepository.save(activeSpec);

        // Y crear especificación eliminada
        TireSpecification deletedSpec = new TireSpecification();
        deletedSpec.setCode("FT-000003");
        deletedSpec.setBrand(testBrand);
        deletedSpec.setType(testType);
        deletedSpec.setReference(testReference);
        deletedSpec.setExpectedMileage(160000);
        deletedSpec.setExpectedRetreads((short) 2);
        deletedSpec.setInitialDepthInternalMm(new BigDecimal("18.5"));
        deletedSpec.setInitialDepthCentralMm(new BigDecimal("18.5"));
        deletedSpec.setInitialDepthExternalMm(new BigDecimal("18.5"));
        deletedSpec.markAsDeleted(1L);
        tireSpecificationRepository.save(deletedSpec);
        entityManager.flush();
        entityManager.clear();

        // When
        List<TireSpecification> actives = tireSpecificationRepository.findAllActive();

        // Then
        assertThat(actives).hasSize(2);
        assertThat(actives).extracting(TireSpecification::getCode)
                .containsExactlyInAnyOrder("FT-000001", "FT-000002");
    }

    // =====================================================
    // TESTS: Soft Delete
    // =====================================================

    @Test
    @DisplayName("Debe marcar especificación como eliminada (soft delete)")
    void shouldMarkSpecificationAsDeleted() {
        // When
        testSpecification.markAsDeleted(1L);
        tireSpecificationRepository.save(testSpecification);
        entityManager.flush();
        entityManager.clear();

        // Then
        TireSpecification updated = tireSpecificationRepository.findById(testSpecification.getId()).orElseThrow();
        assertThat(updated.isDeleted()).isTrue();
        assertThat(updated.getDeletedAt()).isNotNull();
        assertThat(updated.getDeletedBy()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Especificación eliminada no debe aparecer en consultas activas")
    void deletedSpecificationShouldNotAppearInActiveQueries() {
        // Given
        testSpecification.markAsDeleted(1L);
        tireSpecificationRepository.save(testSpecification);
        entityManager.flush();
        entityManager.clear();

        // When
        List<TireSpecification> actives = tireSpecificationRepository.findAllActive();
        Optional<TireSpecification> activeByCode = tireSpecificationRepository.findActiveByCode("FT-000001");

        // Then
        assertThat(actives).isEmpty();
        assertThat(activeByCode).isEmpty();
    }

    // =====================================================
    // TESTS: Validación de Rangos de Kilometraje
    // =====================================================

    @Test
    @DisplayName("Debe validar rangos de kilometraje coherentes")
    void shouldValidateCoherentMileageRanges() {
        // Given
        TireSpecification spec = new TireSpecification();
        spec.setMileageRangeMin(100000);
        spec.setMileageRangeAvg(150000);
        spec.setMileageRangeMax(200000);

        // When & Then
        assertThat(spec.validateMileageRanges()).isTrue();
    }

    @Test
    @DisplayName("Debe invalidar rangos de kilometraje incoherentes")
    void shouldInvalidateIncoherentMileageRanges() {
        // Given - promedio menor que mínimo
        TireSpecification spec = new TireSpecification();
        spec.setMileageRangeMin(150000);
        spec.setMileageRangeAvg(100000);
        spec.setMileageRangeMax(200000);

        // When & Then
        assertThat(spec.validateMileageRanges()).isFalse();
    }

    // =====================================================
    // TESTS: Contador para Generación de Códigos
    // =====================================================

    @Test
    @DisplayName("Debe contar correctamente las especificaciones existentes")
    void shouldCountSpecificationsCorrectly() {
        // Given - hay una especificación creada en setUp

        // When
        long count = tireSpecificationRepository.count();

        // Then
        assertThat(count).isEqualTo(1L);

        // When - agregar otra especificación
        TireSpecification newSpec = new TireSpecification();
        newSpec.setCode("FT-000002");
        newSpec.setBrand(testBrand);
        newSpec.setType(testType);
        newSpec.setReference(testReference);
        newSpec.setExpectedMileage(160000);
        newSpec.setExpectedRetreads((short) 2);
        newSpec.setInitialDepthInternalMm(new BigDecimal("18.0"));
        newSpec.setInitialDepthCentralMm(new BigDecimal("18.0"));
        newSpec.setInitialDepthExternalMm(new BigDecimal("18.0"));
        tireSpecificationRepository.save(newSpec);
        entityManager.flush();

        // Then
        assertThat(tireSpecificationRepository.count()).isEqualTo(2L);
    }
}
