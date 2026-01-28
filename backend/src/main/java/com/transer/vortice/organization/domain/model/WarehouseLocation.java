package com.transer.vortice.organization.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad de dominio que representa una ubicación física dentro de un almacén.
 * <p>
 * Una ubicación es un espacio específico dentro de un almacén donde se almacenan físicamente las llantas.
 * Ejemplos: estantes, zonas, bahías, etc.
 * <p>
 * Reglas de negocio:
 * - El código debe ser único dentro del almacén (no globalmente)
 * - Una ubicación pertenece a UN SOLO almacén
 * - Una vez creada, no se puede cambiar el almacén (warehouse_id es inmutable)
 * - No se puede eliminar si tiene llantas asignadas
 * - Los usuarios solo ven ubicaciones de almacenes de su oficina (RLS)
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(
    name = "warehouse_locations",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_location_warehouse_code",
        columnNames = {"warehouse_id", "code"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class WarehouseLocation extends OrganizationalEntity {

    @NotBlank(message = "El código de la ubicación es obligatorio")
    @Size(min = 1, max = 10, message = "El código debe tener entre 1 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9\\-]{1,10}$", message = "El código debe contener solo letras mayúsculas, números y guiones")
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "El almacén es obligatorio")
    @Column(name = "warehouse_id", nullable = false, updatable = false)
    private UUID warehouseId;

    // Relación opcional para carga lazy (no se usa en queries, solo para navegación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    // =====================================================
    // Constructores de conveniencia
    // =====================================================

    /**
     * Constructor para crear una nueva ubicación
     *
     * @param code        Código único dentro del almacén (ej: A1, ZONA-01)
     * @param name        Nombre descriptivo de la ubicación (opcional)
     * @param warehouseId ID del almacén al que pertenece
     * @param description Descripción opcional
     */
    public WarehouseLocation(String code, String name, UUID warehouseId, String description) {
        this.code = normalizeCode(code);
        this.name = name;
        this.warehouseId = warehouseId;
        this.description = description;
        this.setIsActive(true);
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Actualiza la información de la ubicación.
     * El código y el almacén NO se pueden cambiar.
     *
     * @param name        Nuevo nombre
     * @param description Nueva descripción
     * @param updatedBy   ID del usuario que realiza la actualización
     */
    public void updateInfo(String name, String description, Long updatedBy) {
        if (isDeleted()) {
            throw new IllegalStateException("No se puede actualizar una ubicación eliminada");
        }

        this.name = name;
        this.description = description;
        this.setUpdatedBy(updatedBy);
    }

    /**
     * Verifica si la ubicación pertenece a un almacén específico
     *
     * @param warehouseId ID del almacén a verificar
     * @return true si la ubicación pertenece al almacén
     */
    public boolean belongsToWarehouse(UUID warehouseId) {
        return this.warehouseId != null && this.warehouseId.equals(warehouseId);
    }

    /**
     * Marca la ubicación como eliminada (soft delete)
     * Solo se puede eliminar si no tiene llantas asignadas.
     *
     * @param deletedBy ID del usuario que elimina
     */
    @Override
    public void markAsDeleted(Long deletedBy) {
        // TODO: Validar que no tenga llantas asignadas
        // Esta validación se debe hacer en el servicio antes de llamar a este método
        super.markAsDeleted(deletedBy);
    }

    /**
     * Normaliza el código a mayúsculas
     *
     * @param code Código a normalizar
     * @return Código en mayúsculas
     */
    private String normalizeCode(String code) {
        return code != null ? code.trim().toUpperCase() : null;
    }

    // =====================================================
    // Métodos de validación
    // =====================================================

    /**
     * Valida que la ubicación esté en un estado válido para operaciones
     */
    public void validateActiveState() {
        if (isDeleted()) {
            throw new IllegalStateException("La ubicación está eliminada");
        }
        if (!getIsActive()) {
            throw new IllegalStateException("La ubicación está inactiva");
        }
    }

    /**
     * Valida que la ubicación pertenece al almacén especificado
     *
     * @param expectedWarehouseId ID del almacén esperado
     * @throws IllegalStateException si no pertenece al almacén
     */
    public void validateWarehouseOwnership(UUID expectedWarehouseId) {
        if (!belongsToWarehouse(expectedWarehouseId)) {
            throw new IllegalStateException(
                String.format("La ubicación no pertenece al almacén %s", expectedWarehouseId)
            );
        }
    }

    /**
     * Obtiene el nombre de visualización de la ubicación.
     * Si no tiene nombre, usa el código.
     *
     * @return Nombre o código de la ubicación
     */
    public String getDisplayName() {
        return name != null && !name.trim().isEmpty() ? name : code;
    }

    // =====================================================
    // toString()
    // =====================================================

    @Override
    public String toString() {
        return "WarehouseLocation{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", warehouseId=" + warehouseId +
                ", isActive=" + getIsActive() +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}
