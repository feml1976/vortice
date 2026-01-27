package com.transer.vortice.tire.domain.model.catalog;

import com.transer.vortice.shared.domain.entity.AuditableUUIDEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de catálogo: Referencias de llantas
 *
 * Representa las referencias o modelos específicos de llantas (ej: XZA2, XDA5, etc.)
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "tire_references", schema = "tire_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TireReference extends AuditableUUIDEntity {

    @NotBlank(message = "El código de la referencia es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @NotBlank(message = "El nombre de la referencia es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Activa la referencia
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Desactiva la referencia
     */
    public void deactivate() {
        this.isActive = false;
    }
}
