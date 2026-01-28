package com.transer.vortice.organization.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de dominio que representa una oficina o sede de la empresa.
 * <p>
 * Una oficina es la unidad organizacional de más alto nivel y contiene:
 * - Almacenes (warehouses)
 * - Proveedores (tire_suppliers)
 * - Usuarios asignados
 * <p>
 * Reglas de negocio:
 * - El código debe ser único globalmente
 * - Una vez creada, no se puede cambiar el código
 * - No se puede eliminar físicamente (solo soft delete)
 * - Los usuarios solo ven datos de su oficina (RLS)
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Office extends OrganizationalEntity {

    @NotBlank(message = "El código de la oficina es obligatorio")
    @Size(min = 2, max = 10, message = "El código debe tener entre 2 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "El código debe contener solo letras mayúsculas y números")
    @Column(name = "code", unique = true, nullable = false, length = 10, updatable = false)
    private String code;

    @NotBlank(message = "El nombre de la oficina es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede superar los 50 caracteres")
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Size(max = 500, message = "La dirección no puede superar los 500 caracteres")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Column(name = "phone", length = 20)
    private String phone;

    // =====================================================
    // Constructores de conveniencia
    // =====================================================

    /**
     * Constructor para crear una nueva oficina
     *
     * @param code    Código único de la oficina (ej: BOG, MED)
     * @param name    Nombre de la oficina
     * @param city    Ciudad donde se ubica
     * @param address Dirección física (opcional)
     * @param phone   Teléfono de contacto (opcional)
     */
    public Office(String code, String name, String city, String address, String phone) {
        this.code = normalizeCode(code);
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.setIsActive(true);
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Actualiza la información de la oficina.
     * El código NO se puede cambiar.
     *
     * @param name       Nuevo nombre
     * @param city       Nueva ciudad
     * @param address    Nueva dirección
     * @param phone      Nuevo teléfono
     * @param updatedBy  ID del usuario que realiza la actualización
     */
    public void updateInfo(String name, String city, String address, String phone, Long updatedBy) {
        if (isDeleted()) {
            throw new IllegalStateException("No se puede actualizar una oficina eliminada");
        }

        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.setUpdatedBy(updatedBy);
    }

    /**
     * Marca la oficina como eliminada (soft delete)
     * Solo se puede eliminar si no tiene almacenes, proveedores o usuarios activos.
     *
     * @param deletedBy ID del usuario que elimina
     */
    @Override
    public void markAsDeleted(Long deletedBy) {
        // TODO: Validar que no tenga almacenes activos, proveedores o usuarios
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
     * Valida que la oficina esté en un estado válido para operaciones
     */
    public void validateActiveState() {
        if (isDeleted()) {
            throw new IllegalStateException("La oficina está eliminada");
        }
        if (!getIsActive()) {
            throw new IllegalStateException("La oficina está inactiva");
        }
    }

    // =====================================================
    // toString()
    // =====================================================

    @Override
    public String toString() {
        return "Office{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", isActive=" + getIsActive() +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}
