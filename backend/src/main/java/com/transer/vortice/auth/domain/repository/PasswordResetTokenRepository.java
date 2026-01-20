package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.PasswordResetToken;
import com.transer.vortice.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * Repositorio para la entidad PasswordResetToken.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token de reset por su valor.
     *
     * @param token valor del token
     * @return token de reset si existe
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Busca un token válido de reset por su valor.
     *
     * @param token valor del token
     * @return token de reset si existe y es válido
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token AND t.isValid = true")
    Optional<PasswordResetToken> findByTokenAndIsValid(@Param("token") String token);

    /**
     * Invalida todos los tokens de reset de un usuario.
     *
     * @param user usuario
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.isValid = false WHERE t.user = :user AND t.isValid = true")
    void invalidateAllTokensByUser(@Param("user") User user);

    /**
     * Elimina tokens expirados.
     *
     * @param now fecha y hora actual
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
}
