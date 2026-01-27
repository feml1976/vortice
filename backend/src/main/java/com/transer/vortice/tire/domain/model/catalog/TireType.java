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
 * Entidad de catálogo: Tipos de llantas
 *
 * Representa los tipos de llantas (ej: radial, convencional, tracción, direccional, etc.)
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "types", schema = "tire_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TireType extends AuditableUUIDEntity {

    @NotBlank(message = "El código del tipo es obligatorio")
    @Size(max = 10, message = "El código no puede superar los 10 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    @NotBlank(message = "El nombre del tipo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Activa el tipo
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Desactiva el tipo
     */
    public void deactivate() {
        this.isActive = false;
    }
}
