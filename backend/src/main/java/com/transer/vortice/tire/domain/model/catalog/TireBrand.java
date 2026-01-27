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
 * Entidad de catálogo: Marcas de llantas
 *
 * Representa las marcas comerciales de llantas disponibles en el sistema.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "brands", schema = "tire_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TireBrand extends AuditableUUIDEntity {

    @NotBlank(message = "El código de la marca es obligatorio")
    @Size(max = 10, message = "El código no puede superar los 10 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Activa la marca
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Desactiva la marca
     */
    public void deactivate() {
        this.isActive = false;
    }
}
