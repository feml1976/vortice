package com.transer.vortice.tire.domain.model.catalog;

import com.transer.vortice.shared.domain.entity.AuditableUUIDEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Entidad de catálogo: Proveedores de llantas
 *
 * Representa los proveedores de llantas y servicios de reencauche.
 *
 * @author Vórtice Development Team
 */
@Entity(name = "TireCatalogSupplier")
@Table(name = "suppliers", schema = "tire_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TireSupplier extends AuditableUUIDEntity {

    @NotBlank(message = "El código del proveedor es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotBlank(message = "El NIT/RUC es obligatorio")
    @Size(max = 20, message = "El NIT/RUC no puede superar los 20 caracteres")
    @Column(name = "tax_id", nullable = false, length = 20)
    private String taxId;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 200, message = "El nombre de contacto no puede superar los 200 caracteres")
    @Column(name = "contact_name", length = 200)
    private String contactName;

    /**
     * Información de contacto adicional en formato JSON
     * Ejemplo: {"email": "contacto@proveedor.com", "phone": "123456", "position": "Gerente", "notes": "Notas adicionales"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    private Map<String, Object> contactInfo;

    @Column(name = "payment_terms_days", nullable = false)
    private Integer paymentTermsDays = 30;

    @Column(name = "legacy_milenio_code")
    private Integer legacyMilenioCode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Activa el proveedor
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Desactiva el proveedor
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Obtiene el email de contacto desde el JSON
     */
    public String getContactEmail() {
        if (contactInfo != null && contactInfo.containsKey("email")) {
            return (String) contactInfo.get("email");
        }
        return null;
    }

    /**
     * Establece el email de contacto en el JSON
     */
    public void setContactEmail(String email) {
        if (contactInfo == null) {
            contactInfo = new java.util.HashMap<>();
        }
        contactInfo.put("email", email);
    }
}
