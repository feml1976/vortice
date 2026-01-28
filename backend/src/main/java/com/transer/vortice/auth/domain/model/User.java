package com.transer.vortice.auth.domain.model;

import com.transer.vortice.shared.domain.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad de dominio que representa un usuario en el sistema.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditableEntity {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "El password es obligatorio")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "office_id", nullable = false)
    private UUID officeId;

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Obtiene el nombre completo del usuario.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Verifica si el usuario puede iniciar sesión.
     */
    public boolean canLogin() {
        return isActive && !isLocked;
    }

    /**
     * Incrementa el contador de intentos fallidos de login.
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.isLocked = true;
        }
    }

    /**
     * Resetea el contador de intentos fallidos de login.
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    /**
     * Registra un login exitoso.
     */
    public void registerSuccessfulLogin() {
        this.lastLoginAt = Instant.now();
        resetFailedLoginAttempts();
    }

    /**
     * Agrega un rol al usuario.
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Remueve un rol del usuario.
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }
}
