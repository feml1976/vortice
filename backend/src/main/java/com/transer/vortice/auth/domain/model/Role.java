package com.transer.vortice.auth.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio que representa un rol en el sistema.
 * Los roles agrupan permisos y se asignan a usuarios.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre del rol no puede superar los 50 caracteres")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system_role", nullable = false)
    private Boolean isSystemRole = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // =====================================================
    // Constructores personalizados
    // =====================================================

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.isSystemRole = false;
    }

    public Role(String name, String description, Boolean isSystemRole) {
        this.name = name;
        this.description = description;
        this.isSystemRole = isSystemRole;
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Agrega un permiso al rol.
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * Remueve un permiso del rol.
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * Verifica si el rol tiene un permiso específico.
     */
    public boolean hasPermission(String permissionName) {
        return permissions.stream()
            .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    /**
     * Verifica si el rol es del sistema (no puede ser eliminado).
     */
    public boolean isSystemRole() {
        return isSystemRole != null && isSystemRole;
    }

    // =====================================================
    // equals, hashCode y toString
    // =====================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return id != null && id.equals(role.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isSystemRole=" + isSystemRole +
                '}';
    }
}
