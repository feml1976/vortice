package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.PasswordResetToken;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.PasswordResetTokenRepository;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Servicio para gestión de tokens de recuperación de contraseña.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${vortice.password-reset.expiration:60}")
    private int resetTokenExpirationMinutes;

    /**
     * Crea un nuevo token de reset de contraseña para un usuario.
     * Invalida todos los tokens previos del usuario.
     *
     * @param user usuario
     * @return token de reset creado
     */
    @Transactional
    public PasswordResetToken createPasswordResetToken(User user) {
        log.info("Creando token de reset de contraseña para usuario: {}", user.getUsername());

        // Invalidar todos los tokens previos del usuario
        passwordResetTokenRepository.invalidateAllTokensByUser(user);

        // Crear nuevo token
        PasswordResetToken resetToken = PasswordResetToken.create(user, resetTokenExpirationMinutes);

        resetToken = passwordResetTokenRepository.save(resetToken);

        log.info("Token de reset creado exitosamente para usuario: {} (expira en {} minutos)",
                user.getUsername(), resetTokenExpirationMinutes);

        return resetToken;
    }

    /**
     * Verifica y obtiene un token de reset válido.
     *
     * @param token valor del token
     * @return token de reset si es válido
     * @throws BusinessException si el token es inválido o expirado
     */
    @Transactional(readOnly = true)
    public PasswordResetToken verifyToken(String token) {
        log.info("Verificando token de reset de contraseña");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndIsValid(token)
                .orElseThrow(() -> {
                    log.warn("Token de reset inválido o no encontrado");
                    return new BusinessException("Token de reset inválido");
                });

        if (!resetToken.canBeUsed()) {
            log.warn("Token de reset no puede ser usado (expirado o ya usado)");
            throw new BusinessException("El token ha expirado o ya ha sido usado");
        }

        log.info("Token de reset verificado exitosamente");

        return resetToken;
    }

    /**
     * Marca un token como usado.
     *
     * @param resetToken token a marcar
     */
    @Transactional
    public void markTokenAsUsed(PasswordResetToken resetToken) {
        log.info("Marcando token de reset como usado");

        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);

        log.info("Token de reset marcado como usado");
    }

    /**
     * Limpia tokens expirados de la base de datos.
     * Este método debería ser llamado por un scheduler periódicamente.
     */
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Limpiando tokens de reset expirados");

        passwordResetTokenRepository.deleteExpiredTokens(Instant.now());

        log.info("Tokens de reset expirados eliminados");
    }
}
