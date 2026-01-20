package com.transer.vortice.auth.domain.model;

import com.transer.vortice.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio que representa un token de reseteo de contraseña.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken extends BaseEntity {

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = true;

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Crea un nuevo token de reseteo para un usuario.
     *
     * @param user usuario
     * @param expirationMinutes minutos de validez del token
     * @return token de reseteo
     */
    public static PasswordResetToken create(User user, int expirationMinutes) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(Instant.now().plusSeconds(expirationMinutes * 60L));
        token.setIsValid(true);
        return token;
    }

    /**
     * Verifica si el token está expirado.
     *
     * @return true si está expirado
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Verifica si el token ha sido usado.
     *
     * @return true si ha sido usado
     */
    public boolean isUsed() {
        return usedAt != null;
    }

    /**
     * Marca el token como usado.
     */
    public void markAsUsed() {
        this.usedAt = Instant.now();
        this.isValid = false;
    }

    /**
     * Invalida el token.
     */
    public void invalidate() {
        this.isValid = false;
    }

    /**
     * Verifica si el token puede ser usado.
     *
     * @return true si el token es válido, no ha expirado y no ha sido usado
     */
    public boolean canBeUsed() {
        return isValid && !isExpired() && !isUsed();
    }
}
