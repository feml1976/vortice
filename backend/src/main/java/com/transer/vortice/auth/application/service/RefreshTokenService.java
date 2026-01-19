package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.RefreshToken;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.RefreshTokenRepository;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Servicio para gestionar refresh tokens.
 * Maneja la creación, validación, renovación y revocación de tokens de actualización.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${vortice.jwt.refresh-expiration}")
    private Long refreshTokenExpirationMs;

    /**
     * Crea un nuevo refresh token para un usuario.
     *
     * @param user usuario propietario del token
     * @return refresh token creado
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.debug("Creando refresh token para usuario: {}", user.getUsername());

        String tokenValue = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshToken refreshToken = new RefreshToken(tokenValue, user, expiresAt);
        refreshToken = refreshTokenRepository.save(refreshToken);

        log.info("Refresh token creado exitosamente para usuario: {} (ID: {})",
                user.getUsername(), user.getId());

        return refreshToken;
    }

    /**
     * Busca un refresh token por su valor.
     *
     * @param token valor del token
     * @return refresh token encontrado
     * @throws NotFoundException si el token no existe
     */
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        log.debug("Buscando refresh token");

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token no encontrado");
                    return new NotFoundException("Refresh token no encontrado");
                });
    }

    /**
     * Verifica que un refresh token sea válido (no revocado y no expirado).
     *
     * @param refreshToken token a verificar
     * @return el mismo token si es válido
     * @throws BusinessException si el token no es válido
     */
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getRevoked()) {
            log.warn("Intento de uso de refresh token revocado (ID: {})", refreshToken.getId());
            throw new BusinessException("El refresh token ha sido revocado");
        }

        if (refreshToken.isExpired()) {
            log.warn("Intento de uso de refresh token expirado (ID: {})", refreshToken.getId());
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException("El refresh token ha expirado. Por favor, inicie sesión nuevamente");
        }

        return refreshToken;
    }

    /**
     * Revoca un refresh token.
     *
     * @param token valor del token a revocar
     */
    @Transactional
    public void revokeToken(String token) {
        log.debug("Revocando refresh token");

        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.revoke();
                    refreshTokenRepository.save(refreshToken);
                    log.info("Refresh token revocado exitosamente (ID: {})", refreshToken.getId());
                });
    }

    /**
     * Revoca todos los tokens de un usuario.
     *
     * @param user usuario propietario de los tokens
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        log.debug("Revocando todos los tokens del usuario: {}", user.getUsername());

        int revokedCount = refreshTokenRepository.revokeAllUserTokens(user, Instant.now());

        log.info("Revocados {} tokens del usuario: {} (ID: {})",
                revokedCount, user.getUsername(), user.getId());
    }

    /**
     * Rota un refresh token (revoca el anterior y crea uno nuevo).
     *
     * @param oldToken token anterior a revocar
     * @param user usuario propietario del token
     * @return nuevo refresh token
     */
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken, User user) {
        log.debug("Rotando refresh token para usuario: {}", user.getUsername());

        // Crear nuevo token
        RefreshToken newToken = createRefreshToken(user);

        // Revocar el token anterior y marcarlo como reemplazado
        oldToken.revokeAndReplace(newToken.getToken());
        refreshTokenRepository.save(oldToken);

        log.info("Refresh token rotado exitosamente para usuario: {} (ID: {})",
                user.getUsername(), user.getId());

        return newToken;
    }

    /**
     * Limpia tokens expirados de la base de datos.
     * Se ejecuta automáticamente cada día a las 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Cada día a las 2:00 AM
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Iniciando limpieza de refresh tokens expirados");

        int deletedCount = refreshTokenRepository.deleteExpiredTokens(Instant.now());

        log.info("Limpieza completada: {} refresh tokens expirados eliminados", deletedCount);
    }
}
