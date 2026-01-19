package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.RefreshToken;
import com.transer.vortice.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad RefreshToken.
 * Proporciona métodos de acceso a datos para tokens de actualización.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Busca un refresh token por su valor.
     *
     * @param token valor del token
     * @return Optional con el token si existe
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Busca un refresh token válido (no revocado y no expirado) por su valor.
     *
     * @param token valor del token
     * @param now momento actual
     * @return Optional con el token si existe y es válido
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token AND rt.revoked = false AND rt.expiresAt > :now")
    Optional<RefreshToken> findValidToken(@Param("token") String token, @Param("now") Instant now);

    /**
     * Busca todos los refresh tokens de un usuario.
     *
     * @param user usuario propietario de los tokens
     * @return lista de refresh tokens del usuario
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Busca todos los refresh tokens válidos de un usuario.
     *
     * @param user usuario propietario de los tokens
     * @param now momento actual
     * @return lista de refresh tokens válidos del usuario
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByUser(@Param("user") User user, @Param("now") Instant now);

    /**
     * Revoca todos los tokens de un usuario.
     *
     * @param user usuario propietario de los tokens
     * @param now momento actual
     * @return número de tokens revocados
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.user = :user AND rt.revoked = false")
    int revokeAllUserTokens(@Param("user") User user, @Param("now") Instant now);

    /**
     * Elimina todos los tokens expirados.
     *
     * @param now momento actual
     * @return número de tokens eliminados
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Elimina todos los tokens de un usuario.
     *
     * @param user usuario propietario de los tokens
     * @return número de tokens eliminados
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    int deleteByUser(@Param("user") User user);

    /**
     * Verifica si existe un token con el valor dado.
     *
     * @param token valor del token
     * @return true si existe, false en caso contrario
     */
    boolean existsByToken(String token);
}
