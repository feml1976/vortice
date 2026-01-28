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
 * Entidad de dominio que representa un almacén dentro de una oficina.
 * <p>
 * Un almacén es una unidad de almacenamiento física que pertenece a una oficina específica y contiene:
 * - Ubicaciones (warehouse_locations)
 * - Inventario de llantas
 * <p>
 * Reglas de negocio:
 * - El código debe ser único dentro de la oficina (no globalmente)
 * - Un almacén pertenece a UNA SOLA oficina
 * - Una vez creado, no se puede cambiar la oficina (office_id es inmutable)
 * - No se puede eliminar si tiene ubicaciones activas o inventario
 * - Los usuarios solo ven almacenes de su oficina (RLS)
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(
    name = "warehouses",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_warehouse_office_code",
        columnNames = {"office_id", "code"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class Warehouse extends OrganizationalEntity {

    @NotBlank(message = "El código del almacén es obligatorio")
    @Size(min = 2, max = 10, message = "El código debe tener entre 2 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "El código debe contener solo letras mayúsculas y números")
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @NotBlank(message = "El nombre del almacén es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La oficina es obligatoria")
    @Column(name = "office_id", nullable = false, updatable = false)
    private UUID officeId;

    // Relación opcional para carga lazy (no se usa en queries, solo para navegación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", insertable = false, updatable = false)
    private Office office;

    // =====================================================
    // Constructores de conveniencia
    // =====================================================

    /**
     * Constructor para crear un nuevo almacén
     *
     * @param code        Código único dentro de la oficina (ej: PRIN, TALL)
     * @param name        Nombre del almacén
     * @param officeId    ID de la oficina a la que pertenece
     * @param description Descripción opcional
     */
    public Warehouse(String code, String name, UUID officeId, String description) {
        this.code = normalizeCode(code);
        this.name = name;
        this.officeId = officeId;
        this.description = description;
        this.setIsActive(true);
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Actualiza la información del almacén.
     * El código y la oficina NO se pueden cambiar.
     *
     * @param name        Nuevo nombre
     * @param description Nueva descripción
     * @param updatedBy   ID del usuario que realiza la actualización
     */
    public void updateInfo(String name, String description, Long updatedBy) {
        if (isDeleted()) {
            throw new IllegalStateException("No se puede actualizar un almacén eliminado");
        }

        this.name = name;
        this.description = description;
        this.setUpdatedBy(updatedBy);
    }

    /**
     * Verifica si el almacén pertenece a una oficina específica
     *
     * @param officeId ID de la oficina a verificar
     * @return true si el almacén pertenece a la oficina
     */
    public boolean belongsToOffice(UUID officeId) {
        return this.officeId != null && this.officeId.equals(officeId);
    }

    /**
     * Marca el almacén como eliminado (soft delete)
     * Solo se puede eliminar si no tiene ubicaciones activas o inventario.
     *
     * @param deletedBy ID del usuario que elimina
     */
    @Override
    public void markAsDeleted(Long deletedBy) {
        // TODO: Validar que no tenga ubicaciones activas o inventario
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
     * Valida que el almacén esté en un estado válido para operaciones
     */
    public void validateActiveState() {
        if (isDeleted()) {
            throw new IllegalStateException("El almacén está eliminado");
        }
        if (!getIsActive()) {
            throw new IllegalStateException("El almacén está inactivo");
        }
    }

    /**
     * Valida que el almacén pertenece a la oficina especificada
     *
     * @param expectedOfficeId ID de la oficina esperada
     * @throws IllegalStateException si no pertenece a la oficina
     */
    public void validateOfficeOwnership(UUID expectedOfficeId) {
        if (!belongsToOffice(expectedOfficeId)) {
            throw new IllegalStateException(
                String.format("El almacén no pertenece a la oficina %s", expectedOfficeId)
            );
        }
    }

    // =====================================================
    // toString()
    // =====================================================

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", officeId=" + officeId +
                ", isActive=" + getIsActive() +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}
