package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.shared.infrastructure.BaseRepositoryTest;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para repositorios de catálogos del módulo de llantas.
 * Usa Testcontainers con PostgreSQL para ejecutar tests contra una BD real.
 *
 * @author Vórtice Development Team
 */
@DisplayName("Tire Catalog Repositories Integration Tests")
class TireCatalogRepositoriesTest extends BaseRepositoryTest {

    @Autowired
    private TireBrandRepository tireBrandRepository;

    @Autowired
    private TireTypeRepository tireTypeRepository;

    @Autowired
    private TireReferenceRepository tireReferenceRepository;

    @Autowired
    private TireSupplierRepository tireSupplierRepository;

    @Autowired
    private TestEntityManager entityManager;

    // =====================================================
    // TESTS: TireBrandRepository
    // =====================================================

    @Test
    @DisplayName("Debe guardar y recuperar marca de llanta")
    void shouldSaveAndRetrieveTireBrand() {
        // Given
        TireBrand brand = new TireBrand();
        brand.setCode("MCH");
        brand.setName("Michelin");
        brand.setIsActive(true);

        // When
        TireBrand saved = tireBrandRepository.save(brand);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<TireBrand> retrieved = tireBrandRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Michelin");
        assertThat(retrieved.get().getCode()).isEqualTo("MCH");
    }

    @Test
    @DisplayName("Debe encontrar marca por código")
    void shouldFindBrandByCode() {
        // Given
        TireBrand brand = new TireBrand();
        brand.setCode("GDY");
        brand.setName("Goodyear");
        brand.setIsActive(true);
        tireBrandRepository.save(brand);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<TireBrand> found = tireBrandRepository.findByCode("GDY");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Goodyear");
    }

    @Test
    @DisplayName("Debe listar todas las marcas activas")
    void shouldListAllActiveBrands() {
        // Given
        TireBrand activeBrand = new TireBrand();
        activeBrand.setCode("MCH");
        activeBrand.setName("Michelin");
        activeBrand.setIsActive(true);
        tireBrandRepository.save(activeBrand);

        TireBrand inactiveBrand = new TireBrand();
        inactiveBrand.setCode("GDY");
        inactiveBrand.setName("Goodyear");
        inactiveBrand.setIsActive(false);
        tireBrandRepository.save(inactiveBrand);

        entityManager.flush();
        entityManager.clear();

        // When
        List<TireBrand> actives = tireBrandRepository.findAllActive();

        // Then
        assertThat(actives).hasSize(1);
        assertThat(actives.get(0).getName()).isEqualTo("Michelin");
    }

    @Test
    @DisplayName("Debe activar y desactivar marca")
    void shouldActivateAndDeactivateBrand() {
        // Given
        TireBrand brand = new TireBrand();
        brand.setCode("MCH");
        brand.setName("Michelin");
        brand.setIsActive(true);
        brand = tireBrandRepository.save(brand);
        entityManager.flush();
        entityManager.clear();

        // When - desactivar
        TireBrand reloaded = tireBrandRepository.findById(brand.getId()).orElseThrow();
        reloaded.deactivate();
        tireBrandRepository.save(reloaded);
        entityManager.flush();
        entityManager.clear();

        // Then
        TireBrand deactivated = tireBrandRepository.findById(brand.getId()).orElseThrow();
        assertThat(deactivated.getIsActive()).isFalse();

        // When - activar
        deactivated.activate();
        tireBrandRepository.save(deactivated);
        entityManager.flush();
        entityManager.clear();

        // Then
        TireBrand activated = tireBrandRepository.findById(brand.getId()).orElseThrow();
        assertThat(activated.getIsActive()).isTrue();
    }

    // =====================================================
    // TESTS: TireTypeRepository
    // =====================================================

    @Test
    @DisplayName("Debe guardar y recuperar tipo de llanta")
    void shouldSaveAndRetrieveTireType() {
        // Given
        TireType type = new TireType();
        type.setCode("RAD");
        type.setName("Radial");
        type.setName("Llanta tipo radial");
        type.setIsActive(true);

        // When
        TireType saved = tireTypeRepository.save(type);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<TireType> retrieved = tireTypeRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Radial");
        assertThat(retrieved.get().getCode()).isEqualTo("RAD");
        assertThat(retrieved.get().getName()).isEqualTo("Llanta tipo radial");
    }

    @Test
    @DisplayName("Debe encontrar tipo por código")
    void shouldFindTypeByCode() {
        // Given
        TireType type = new TireType();
        type.setCode("CON");
        type.setName("Convencional");
        type.setName("Llanta convencional");
        type.setIsActive(true);
        tireTypeRepository.save(type);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<TireType> found = tireTypeRepository.findByCode("CON");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Convencional");
    }

    @Test
    @DisplayName("Debe listar todos los tipos activos")
    void shouldListAllActiveTypes() {
        // Given
        TireType activeType = new TireType();
        activeType.setCode("RAD");
        activeType.setName("Radial");
        activeType.setName("Tipo radial");
        activeType.setIsActive(true);
        tireTypeRepository.save(activeType);

        TireType inactiveType = new TireType();
        inactiveType.setCode("CON");
        inactiveType.setName("Convencional");
        inactiveType.setName("Tipo convencional");
        inactiveType.setIsActive(false);
        tireTypeRepository.save(inactiveType);

        entityManager.flush();
        entityManager.clear();

        // When
        List<TireType> actives = tireTypeRepository.findAllActive();

        // Then
        assertThat(actives).hasSize(1);
        assertThat(actives.get(0).getName()).isEqualTo("Radial");
    }

    // =====================================================
    // TESTS: TireReferenceRepository
    // =====================================================

    @Test
    @DisplayName("Debe guardar y recuperar referencia de llanta")
    void shouldSaveAndRetrieveTireReference() {
        // Given
        TireReference reference = new TireReference();
        reference.setCode("295/80R22.5");
        reference.setName("Medida estándar para camiones");
        reference.setIsActive(true);

        // When
        TireReference saved = tireReferenceRepository.save(reference);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<TireReference> retrieved = tireReferenceRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getCode()).isEqualTo("295/80R22.5");
        assertThat(retrieved.get().getName()).isEqualTo("Medida estándar para camiones");
    }

    @Test
    @DisplayName("Debe encontrar referencia por código")
    void shouldFindReferenceByCode() {
        // Given
        TireReference reference = new TireReference();
        reference.setCode("275/70R22.5");
        reference.setName("Medida alternativa");
        reference.setIsActive(true);
        tireReferenceRepository.save(reference);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<TireReference> found = tireReferenceRepository.findByCode("275/70R22.5");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Medida alternativa");
    }

    @Test
    @DisplayName("Debe listar todas las referencias activas")
    void shouldListAllActiveReferences() {
        // Given
        TireReference activeRef = new TireReference();
        activeRef.setCode("295/80R22.5");
        activeRef.setName("Referencia activa");
        activeRef.setIsActive(true);
        tireReferenceRepository.save(activeRef);

        TireReference inactiveRef = new TireReference();
        inactiveRef.setCode("275/70R22.5");
        inactiveRef.setName("Referencia inactiva");
        inactiveRef.setIsActive(false);
        tireReferenceRepository.save(inactiveRef);

        entityManager.flush();
        entityManager.clear();

        // When
        List<TireReference> actives = tireReferenceRepository.findAllActive();

        // Then
        assertThat(actives).hasSize(1);
        assertThat(actives.get(0).getCode()).isEqualTo("295/80R22.5");
    }

    // =====================================================
    // TESTS: TireSupplierRepository
    // =====================================================

    @Test
    @DisplayName("Debe guardar y recuperar proveedor de llantas")
    void shouldSaveAndRetrieveTireSupplier() {
        // Given
        TireSupplier supplier = new TireSupplier();
        supplier.setCode("PROV01");
        supplier.setName("Proveedor Principal");
        supplier.setTaxId("1234567890");
        supplier.setContactEmail("contacto@proveedor.com");
        supplier.setIsActive(true);

        // When
        TireSupplier saved = tireSupplierRepository.save(supplier);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<TireSupplier> retrieved = tireSupplierRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Proveedor Principal");
        assertThat(retrieved.get().getCode()).isEqualTo("PROV01");
        assertThat(retrieved.get().getTaxId()).isEqualTo("1234567890");
        assertThat(retrieved.get().getContactEmail()).isEqualTo("contacto@proveedor.com");
    }

    @Test
    @DisplayName("Debe encontrar proveedor por código")
    void shouldFindSupplierByCode() {
        // Given
        TireSupplier supplier = new TireSupplier();
        supplier.setCode("PROV02");
        supplier.setName("Proveedor Secundario");
        supplier.setTaxId("0987654321");
        supplier.setIsActive(true);
        tireSupplierRepository.save(supplier);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<TireSupplier> found = tireSupplierRepository.findByCode("PROV02");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Proveedor Secundario");
    }

    @Test
    @DisplayName("Debe listar todos los proveedores activos")
    void shouldListAllActiveSuppliers() {
        // Given
        TireSupplier activeSupplier = new TireSupplier();
        activeSupplier.setCode("PROV01");
        activeSupplier.setName("Proveedor Activo");
        activeSupplier.setTaxId("1111111111");
        activeSupplier.setIsActive(true);
        tireSupplierRepository.save(activeSupplier);

        TireSupplier inactiveSupplier = new TireSupplier();
        inactiveSupplier.setCode("PROV02");
        inactiveSupplier.setName("Proveedor Inactivo");
        inactiveSupplier.setTaxId("2222222222");
        inactiveSupplier.setIsActive(false);
        tireSupplierRepository.save(inactiveSupplier);

        entityManager.flush();
        entityManager.clear();

        // When
        List<TireSupplier> actives = tireSupplierRepository.findAllActive();

        // Then
        assertThat(actives).hasSize(1);
        assertThat(actives.get(0).getName()).isEqualTo("Proveedor Activo");
    }

    // =====================================================
    // TESTS: Integridad Referencial
    // =====================================================

    @Test
    @DisplayName("Debe mantener integridad referencial entre catálogos")
    void shouldMaintainReferentialIntegrityBetweenCatalogs() {
        // Given - crear todos los catálogos
        TireBrand brand = new TireBrand();
        brand.setCode("MCH");
        brand.setName("Michelin");
        brand.setIsActive(true);
        brand = tireBrandRepository.save(brand);

        TireType type = new TireType();
        type.setCode("RAD");
        type.setName("Radial");
        type.setName("Tipo radial");
        type.setIsActive(true);
        type = tireTypeRepository.save(type);

        TireReference reference = new TireReference();
        reference.setCode("295/80R22.5");
        reference.setName("Medida estándar");
        reference.setIsActive(true);
        reference = tireReferenceRepository.save(reference);

        TireSupplier supplier = new TireSupplier();
        supplier.setCode("PROV01");
        supplier.setName("Proveedor Test");
        supplier.setTaxId("1234567890");
        supplier.setIsActive(true);
        supplier = tireSupplierRepository.save(supplier);

        entityManager.flush();
        entityManager.clear();

        // When - recuperar todos
        TireBrand retrievedBrand = tireBrandRepository.findById(brand.getId()).orElseThrow();
        TireType retrievedType = tireTypeRepository.findById(type.getId()).orElseThrow();
        TireReference retrievedRef = tireReferenceRepository.findById(reference.getId()).orElseThrow();
        TireSupplier retrievedSupplier = tireSupplierRepository.findById(supplier.getId()).orElseThrow();

        // Then
        assertThat(retrievedBrand.getId()).isNotNull();
        assertThat(retrievedType.getId()).isNotNull();
        assertThat(retrievedRef.getId()).isNotNull();
        assertThat(retrievedSupplier.getId()).isNotNull();
    }

    // =====================================================
    // TESTS: Auditoría
    // =====================================================

    @Test
    @DisplayName("Debe registrar campos de auditoría al crear marca")
    void shouldRecordAuditFieldsWhenCreatingBrand() {
        // Given
        TireBrand brand = new TireBrand();
        brand.setCode("MCH");
        brand.setName("Michelin");
        brand.setIsActive(true);

        // When
        TireBrand saved = tireBrandRepository.save(brand);
        entityManager.flush();
        entityManager.clear();

        // Then
        TireBrand retrieved = tireBrandRepository.findById(saved.getId()).orElseThrow();
        assertThat(retrieved.getCreatedAt()).isNotNull();
        assertThat(retrieved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe actualizar campos de auditoría al modificar tipo")
    void shouldUpdateAuditFieldsWhenModifyingType() {
        // Given
        TireType type = new TireType();
        type.setCode("RAD");
        type.setName("Radial");
        type.setName("Tipo radial");
        type.setIsActive(true);
        type = tireTypeRepository.save(type);
        entityManager.flush();
        entityManager.clear();

        // When - modificar
        TireType reloaded = tireTypeRepository.findById(type.getId()).orElseThrow();
        reloaded.setName("Tipo radial actualizado");
        tireTypeRepository.save(reloaded);
        entityManager.flush();
        entityManager.clear();

        // Then
        TireType updated = tireTypeRepository.findById(type.getId()).orElseThrow();
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getName()).isEqualTo("Tipo radial actualizado");
    }
}
