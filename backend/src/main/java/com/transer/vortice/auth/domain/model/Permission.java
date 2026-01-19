package com.transer.vortice.auth.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * Entidad de dominio que representa un permiso en el sistema.
 * Los permisos definen acciones sobre recursos específicos.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 100, message = "El nombre del permiso no puede superar los 100 caracteres")
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @NotBlank(message = "El recurso es obligatorio")
    @Size(max = 50, message = "El recurso no puede superar los 50 caracteres")
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 50, message = "La acción no puede superar los 50 caracteres")
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // =====================================================
    // Constructores personalizados
    // =====================================================

    public Permission(String name, String resource, String action, String description) {
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    public Permission(String name, String resource, String action) {
        this.name = name;
        this.resource = resource;
        this.action = action;
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Construye el nombre del permiso basado en el recurso y la acción.
     * Formato: RESOURCE:ACTION (ej: WORK_ORDER:CREATE)
     */
    public static String buildPermissionName(String resource, String action) {
        return resource.toUpperCase() + ":" + action.toUpperCase();
    }

    /**
     * Verifica si este permiso coincide con un recurso y acción específicos.
     */
    public boolean matches(String resource, String action) {
        return this.resource.equalsIgnoreCase(resource) &&
               this.action.equalsIgnoreCase(action);
    }

    // =====================================================
    // equals, hashCode y toString
    // =====================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission permission)) return false;
        return id != null && id.equals(permission.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
