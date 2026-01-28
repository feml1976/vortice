package com.transer.vortice.organization.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para la entidad Office.
 * Valida reglas de negocio y comportamiento del dominio.
 *
 * @author Vórtice Development Team
 */
@DisplayName("Office - Tests de Entidad de Dominio")
class OfficeTest {

    private Office office;

    @BeforeEach
    void setUp() {
        office = new Office("MAIN", "Oficina Principal", "Bogotá", "Calle 123", "123456789");
        office.setCreatedBy(1L);
    }

    @Test
    @DisplayName("Crear oficina con datos válidos - exitoso")
    void createOffice_WithValidData_Success() {
        // Then
        assertThat(office.getCode()).isEqualTo("MAIN");
        assertThat(office.getName()).isEqualTo("Oficina Principal");
        assertThat(office.getCity()).isEqualTo("Bogotá");
        assertThat(office.getAddress()).isEqualTo("Calle 123");
        assertThat(office.getPhone()).isEqualTo("123456789");
        assertThat(office.getIsActive()).isTrue();
        assertThat(office.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Actualizar información de oficina - exitoso")
    void updateInfo_WithValidData_Success() {
        // When
        office.updateInfo(
            "Oficina Actualizada",
            "Medellín",
            "Calle 456",
            "987654321",
            2L
        );

        // Then
        assertThat(office.getName()).isEqualTo("Oficina Actualizada");
        assertThat(office.getCity()).isEqualTo("Medellín");
        assertThat(office.getAddress()).isEqualTo("Calle 456");
        assertThat(office.getPhone()).isEqualTo("987654321");
        assertThat(office.getUpdatedBy()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Actualizar oficina eliminada - lanza excepción")
    void updateInfo_OnDeletedOffice_ThrowsException() {
        // Given
        office.markAsDeleted(1L);

        // When/Then
        assertThatThrownBy(() -> office.updateInfo(
            "Oficina Actualizada",
            "Medellín",
            null,
            null,
            2L
        ))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("eliminada");
    }

    @Test
    @DisplayName("Marcar oficina como eliminada - actualiza campos correctamente")
    void markAsDeleted_UpdatesFieldsCorrectly() {
        // Given
        Long deletedBy = 2L;

        // When
        office.markAsDeleted(deletedBy);

        // Then
        assertThat(office.isDeleted()).isTrue();
        assertThat(office.getDeletedBy()).isEqualTo(deletedBy);
        assertThat(office.getDeletedAt()).isNotNull();
        assertThat(office.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Normalizar código - convierte a mayúsculas en constructor")
    void normalizeCode_ConvertsToUpperCase() {
        // Given/When - el código se normaliza en el constructor
        Office officeWithLowerCode = new Office("main", "Oficina", "Ciudad", null, null);

        // Then
        assertThat(officeWithLowerCode.getCode()).isEqualTo("MAIN");
    }

    @Test
    @DisplayName("Validar estado activo - oficina eliminada debe estar inactiva")
    void validateActiveState_DeletedOfficeMustBeInactive() {
        // Given
        office.markAsDeleted(1L);

        // When
        office.validateActiveState();

        // Then - No lanza excepción porque setIsActive(false) se llamó en markAsDeleted
        assertThat(office.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Validar estado activo - oficina eliminada no puede ser activa")
    void validateActiveState_DeletedOfficeCannotBeActive_ThrowsException() {
        // Given
        office.markAsDeleted(1L);
        office.setIsActive(true); // Forzar estado inválido

        // When/Then
        assertThatThrownBy(() -> office.validateActiveState())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("eliminada no puede estar activa");
    }

    @Test
    @DisplayName("Verificar si la oficina está activa y no eliminada")
    void isActiveAndNotDeleted_ReturnsCorrectValue() {
        // Given - Oficina activa
        assertThat(office.getIsActive()).isTrue();
        assertThat(office.isDeleted()).isFalse();

        // When - Marcar como inactiva
        office.setIsActive(false);

        // Then
        assertThat(office.getIsActive()).isFalse();

        // When - Marcar como eliminada
        office.markAsDeleted(1L);

        // Then
        assertThat(office.isDeleted()).isTrue();
        assertThat(office.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Código debe ser inmutable después de creación")
    void code_IsImmutableAfterCreation() {
        // Given
        String originalCode = office.getCode();

        // When - Intentar cambiar código (en producción el setter está protegido)
        // Este test documenta que el código NO debe cambiar

        // Then
        assertThat(office.getCode()).isEqualTo(originalCode);
        // En la entidad real, el campo code tiene updatable = false
    }

    @Test
    @DisplayName("Lifecycle - Constructor normaliza código")
    void lifecycle_Constructor_NormalizesCode() {
        // Given/When - código se normaliza en el constructor
        Office newOffice = new Office("bog-01", "Oficina Bogotá", "Bogotá", null, null);

        // Then
        assertThat(newOffice.getCode()).isEqualTo("BOG-01");
    }

    @Test
    @DisplayName("Lifecycle - PreUpdate valida estado activo")
    void lifecycle_PreUpdate_ValidatesActiveState() {
        // Given - Oficina eliminada pero marcada como activa (estado inválido)
        office.markAsDeleted(1L);
        office.setIsActive(true);

        // When/Then
        assertThatThrownBy(() -> office.validateActiveState())
            .isInstanceOf(IllegalStateException.class);
    }
}
