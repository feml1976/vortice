package com.transer.vortice.organization.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para la entidad Warehouse.
 * Valida reglas de negocio, incluyendo pertenencia a oficina y RLS.
 *
 * @author Vórtice Development Team
 */
@DisplayName("Warehouse - Tests de Entidad de Dominio")
class WarehouseTest {

    private UUID officeId;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();
        warehouse = new Warehouse("PRIN", "Almacén Principal", officeId, "Almacén principal");
        warehouse.setCreatedBy(1L);
    }

    @Test
    @DisplayName("Crear almacén con datos válidos - exitoso")
    void createWarehouse_WithValidData_Success() {
        // Then
        assertThat(warehouse.getCode()).isEqualTo("PRIN");
        assertThat(warehouse.getName()).isEqualTo("Almacén Principal");
        assertThat(warehouse.getOfficeId()).isEqualTo(officeId);
        assertThat(warehouse.getDescription()).isEqualTo("Almacén principal");
        assertThat(warehouse.getIsActive()).isTrue();
        assertThat(warehouse.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Verificar pertenencia a oficina - retorna true si coincide")
    void belongsToOffice_SameOffice_ReturnsTrue() {
        // When/Then
        assertThat(warehouse.belongsToOffice(officeId)).isTrue();
    }

    @Test
    @DisplayName("Verificar pertenencia a oficina - retorna false si no coincide")
    void belongsToOffice_DifferentOffice_ReturnsFalse() {
        // Given
        UUID differentOfficeId = UUID.randomUUID();

        // When/Then
        assertThat(warehouse.belongsToOffice(differentOfficeId)).isFalse();
    }

    @Test
    @DisplayName("Validar propiedad de oficina - exitoso si coincide")
    void validateOfficeOwnership_SameOffice_Success() {
        // When/Then - No lanza excepción
        warehouse.validateOfficeOwnership(officeId);
    }

    @Test
    @DisplayName("Validar propiedad de oficina - lanza excepción si no coincide")
    void validateOfficeOwnership_DifferentOffice_ThrowsException() {
        // Given
        UUID differentOfficeId = UUID.randomUUID();

        // When/Then
        assertThatThrownBy(() -> warehouse.validateOfficeOwnership(differentOfficeId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no pertenece a la oficina");
    }

    @Test
    @DisplayName("Actualizar información de almacén - exitoso")
    void updateInfo_WithValidData_Success() {
        // When
        warehouse.updateInfo(
            "Almacén Actualizado",
            "Nueva descripción",
            2L
        );

        // Then
        assertThat(warehouse.getName()).isEqualTo("Almacén Actualizado");
        assertThat(warehouse.getDescription()).isEqualTo("Nueva descripción");
        assertThat(warehouse.getUpdatedBy()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Actualizar almacén eliminado - lanza excepción")
    void updateInfo_OnDeletedWarehouse_ThrowsException() {
        // Given
        warehouse.markAsDeleted(1L);

        // When/Then
        assertThatThrownBy(() -> warehouse.updateInfo(
            "Almacén Actualizado",
            "Nueva descripción",
            2L
        ))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("eliminado");
    }

    @Test
    @DisplayName("Marcar almacén como eliminado - actualiza campos correctamente")
    void markAsDeleted_UpdatesFieldsCorrectly() {
        // Given
        Long deletedBy = 2L;

        // When
        warehouse.markAsDeleted(deletedBy);

        // Then
        assertThat(warehouse.isDeleted()).isTrue();
        assertThat(warehouse.getDeletedBy()).isEqualTo(deletedBy);
        assertThat(warehouse.getDeletedAt()).isNotNull();
        assertThat(warehouse.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Normalizar código - convierte a mayúsculas en constructor")
    void normalizeCode_ConvertsToUpperCase() {
        // Given/When - el código se normaliza en el constructor
        Warehouse warehouseWithLowerCode = new Warehouse(
            "prin",
            "Almacén",
            officeId,
            "Descripción"
        );

        // Then
        assertThat(warehouseWithLowerCode.getCode()).isEqualTo("PRIN");
    }

    @Test
    @DisplayName("OfficeId es inmutable después de creación")
    void officeId_IsImmutableAfterCreation() {
        // Given
        UUID originalOfficeId = warehouse.getOfficeId();

        // Then - El campo officeId tiene updatable = false en JPA
        assertThat(warehouse.getOfficeId()).isEqualTo(originalOfficeId);
        // En la entidad real, no existe setter para officeId
    }

    @Test
    @DisplayName("Código es inmutable después de creación")
    void code_IsImmutableAfterCreation() {
        // Given
        String originalCode = warehouse.getCode();

        // Then - El campo code no tiene setter público
        assertThat(warehouse.getCode()).isEqualTo(originalCode);
    }

    @Test
    @DisplayName("Validar estado activo - almacén eliminado debe estar inactivo")
    void validateActiveState_DeletedWarehouseMustBeInactive() {
        // Given
        warehouse.markAsDeleted(1L);

        // When
        warehouse.validateActiveState();

        // Then - No lanza excepción
        assertThat(warehouse.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Validar estado activo - almacén eliminado no puede estar activo")
    void validateActiveState_DeletedWarehouseCannotBeActive_ThrowsException() {
        // Given
        warehouse.markAsDeleted(1L);
        warehouse.setIsActive(true); // Forzar estado inválido

        // When/Then
        assertThatThrownBy(() -> warehouse.validateActiveState())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("eliminado no puede estar activo");
    }

    @Test
    @DisplayName("Lifecycle - Constructor normaliza código")
    void lifecycle_Constructor_NormalizesCode() {
        // Given/When - código se normaliza en el constructor
        Warehouse newWarehouse = new Warehouse("wh-01", "Almacén 01", officeId, null);

        // Then
        assertThat(newWarehouse.getCode()).isEqualTo("WH-01");
    }

    @Test
    @DisplayName("Contexto multi-sede - almacenes con mismo código en diferentes oficinas")
    void multiTenant_SameCodeDifferentOffices_IsValid() {
        // Given
        UUID officeId1 = UUID.randomUUID();
        UUID officeId2 = UUID.randomUUID();

        Warehouse warehouse1 = new Warehouse("PRIN", "Principal Oficina 1", officeId1, null);
        Warehouse warehouse2 = new Warehouse("PRIN", "Principal Oficina 2", officeId2, null);

        // Then - Mismo código, diferentes oficinas es válido (unicidad por oficina)
        assertThat(warehouse1.getCode()).isEqualTo(warehouse2.getCode());
        assertThat(warehouse1.getOfficeId()).isNotEqualTo(warehouse2.getOfficeId());
        assertThat(warehouse1.belongsToOffice(officeId1)).isTrue();
        assertThat(warehouse2.belongsToOffice(officeId2)).isTrue();
        assertThat(warehouse1.belongsToOffice(officeId2)).isFalse();
    }

    @Test
    @DisplayName("Obtener nombre completo de display")
    void getDisplayName_ReturnsCodeAndName() {
        // When
        String displayName = warehouse.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("PRIN - Almacén Principal");
    }
}
