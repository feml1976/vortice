package com.transer.vortice.auth.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * Entidad de dominio que representa un token de actualización (refresh token).
 * Se usa para obtener nuevos access tokens sin requerir credenciales.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "refresh_tokens")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El token es obligatorio")
    @Column(name = "token", unique = true, nullable = false, length = 500)
    private String token;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "La fecha de expiración es obligatoria")
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_token", length = 500)
    private String replacedByToken;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // =====================================================
    // Constructores personalizados
    // =====================================================

    public RefreshToken(String token, User user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Verifica si el token ha expirado.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Verifica si el token es válido (no revocado y no expirado).
     */
    public boolean isValid() {
        return !revoked && !isExpired();
    }

    /**
     * Revoca el token.
     */
    public void revoke() {
        this.revoked = true;
        this.revokedAt = Instant.now();
    }

    /**
     * Revoca el token y lo reemplaza por uno nuevo.
     */
    public void revokeAndReplace(String newToken) {
        revoke();
        this.replacedByToken = newToken;
    }

    // =====================================================
    // equals, hashCode y toString
    // =====================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshToken that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", token='" + token.substring(0, Math.min(20, token.length())) + "...'" +
                ", userId=" + (user != null ? user.getId() : null) +
                ", expiresAt=" + expiresAt +
                ", revoked=" + revoked +
                '}';
    }
}
