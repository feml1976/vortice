package com.transer.vortice.organization.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad de dominio que representa un proveedor de llantas de una oficina específica.
 * <p>
 * Un proveedor es una empresa que suministra llantas a una oficina. Los proveedores son específicos
 * por oficina, lo que significa que si el mismo proveedor opera en múltiples oficinas, debe
 * registrarse por separado en cada una.
 * <p>
 * Reglas de negocio:
 * - El código debe ser único dentro de la oficina (no globalmente)
 * - Un proveedor pertenece a UNA SOLA oficina
 * - Una vez creado, no se puede cambiar la oficina (office_id es inmutable)
 * - No se puede eliminar si tiene compras asociadas o inventario
 * - Los usuarios solo ven proveedores de su oficina (RLS)
 * - El tax_id (NIT) debe ser válido pero puede repetirse entre oficinas
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(
    name = "tire_suppliers",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_supplier_office_code",
        columnNames = {"office_id", "code"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class TireSupplier extends OrganizationalEntity {

    @NotBlank(message = "El código del proveedor es obligatorio")
    @Size(min = 2, max = 10, message = "El código debe tener entre 2 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9\\-]{2,10}$", message = "El código debe contener solo letras mayúsculas, números y guiones")
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "El NIT o identificación tributaria es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    @Column(name = "tax_id", nullable = false, length = 20)
    private String taxId;

    @NotNull(message = "La oficina es obligatoria")
    @Column(name = "office_id", nullable = false, updatable = false)
    private UUID officeId;

    @Size(max = 100, message = "El nombre de contacto no puede superar los 100 caracteres")
    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 500, message = "La dirección no puede superar los 500 caracteres")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    // Relación opcional para carga lazy (no se usa en queries, solo para navegación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", insertable = false, updatable = false)
    private Office office;

    // =====================================================
    // Constructores de conveniencia
    // =====================================================

    /**
     * Constructor para crear un nuevo proveedor
     *
     * @param code    Código único dentro de la oficina (ej: PROV-01)
     * @param name    Nombre o razón social del proveedor
     * @param taxId   NIT o identificación tributaria
     * @param officeId ID de la oficina a la que pertenece
     */
    public TireSupplier(String code, String name, String taxId, UUID officeId) {
        this.code = normalizeCode(code);
        this.name = name;
        this.taxId = taxId;
        this.officeId = officeId;
        this.setIsActive(true);
    }

    /**
     * Constructor completo para crear un nuevo proveedor con información de contacto
     *
     * @param code        Código único dentro de la oficina
     * @param name        Nombre o razón social del proveedor
     * @param taxId       NIT o identificación tributaria
     * @param officeId    ID de la oficina a la que pertenece
     * @param contactName Nombre del contacto
     * @param email       Email del proveedor
     * @param phone       Teléfono de contacto
     * @param address     Dirección física
     */
    public TireSupplier(String code, String name, String taxId, UUID officeId,
                        String contactName, String email, String phone, String address) {
        this(code, name, taxId, officeId);
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Actualiza la información del proveedor.
     * El código y la oficina NO se pueden cambiar.
     *
     * @param name        Nuevo nombre
     * @param taxId       Nuevo NIT
     * @param contactName Nuevo nombre de contacto
     * @param email       Nuevo email
     * @param phone       Nuevo teléfono
     * @param address     Nueva dirección
     * @param updatedBy   ID del usuario que realiza la actualización
     */
    public void updateInfo(String name, String taxId, String contactName, String email,
                          String phone, String address, Long updatedBy) {
        if (isDeleted()) {
            throw new IllegalStateException("No se puede actualizar un proveedor eliminado");
        }

        this.name = name;
        this.taxId = taxId;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.setUpdatedBy(updatedBy);
    }

    /**
     * Verifica si el proveedor pertenece a una oficina específica
     *
     * @param officeId ID de la oficina a verificar
     * @return true si el proveedor pertenece a la oficina
     */
    public boolean belongsToOffice(UUID officeId) {
        return this.officeId != null && this.officeId.equals(officeId);
    }

    /**
     * Marca el proveedor como eliminado (soft delete)
     * Solo se puede eliminar si no tiene compras asociadas o inventario.
     *
     * @param deletedBy ID del usuario que elimina
     */
    @Override
    public void markAsDeleted(Long deletedBy) {
        // TODO: Validar que no tenga compras asociadas o inventario
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
     * Valida que el proveedor esté en un estado válido para operaciones
     */
    public void validateActiveState() {
        if (isDeleted()) {
            throw new IllegalStateException("El proveedor está eliminado");
        }
        if (!getIsActive()) {
            throw new IllegalStateException("El proveedor está inactivo");
        }
    }

    /**
     * Valida que el proveedor pertenece a la oficina especificada
     *
     * @param expectedOfficeId ID de la oficina esperada
     * @throws IllegalStateException si no pertenece a la oficina
     */
    public void validateOfficeOwnership(UUID expectedOfficeId) {
        if (!belongsToOffice(expectedOfficeId)) {
            throw new IllegalStateException(
                String.format("El proveedor no pertenece a la oficina %s", expectedOfficeId)
            );
        }
    }

    /**
     * Verifica si el proveedor tiene información de contacto completa
     *
     * @return true si tiene al menos email o teléfono
     */
    public boolean hasContactInfo() {
        return (email != null && !email.trim().isEmpty()) ||
               (phone != null && !phone.trim().isEmpty());
    }

    // =====================================================
    // toString()
    // =====================================================

    @Override
    public String toString() {
        return "TireSupplier{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", taxId='" + taxId + '\'' +
                ", officeId=" + officeId +
                ", isActive=" + getIsActive() +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}
