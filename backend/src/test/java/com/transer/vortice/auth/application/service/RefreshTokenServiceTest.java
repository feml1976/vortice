package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.RefreshToken;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.RefreshTokenRepository;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para RefreshTokenService.
 * Usa Mockito para simular dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Tests")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken testToken;
    private String tokenValue;
    private final Long REFRESH_TOKEN_EXPIRATION_MS = 604800000L; // 7 días

    @BeforeEach
    void setUp() {
        // Configurar valor de expiración usando ReflectionTestUtils
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", REFRESH_TOKEN_EXPIRATION_MS);

        // Crear usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setIsActive(true);
        testUser.setIsLocked(false);

        // Crear refresh token de prueba
        tokenValue = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS);
        testToken = new RefreshToken(tokenValue, testUser, expiresAt);
        testToken.setId(1L);
    }

    @Test
    @DisplayName("Debe crear refresh token exitosamente")
    void shouldCreateRefreshToken() {
        // Given
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });

        // When
        RefreshToken createdToken = refreshTokenService.createRefreshToken(testUser);

        // Then
        assertThat(createdToken).isNotNull();
        assertThat(createdToken.getUser()).isEqualTo(testUser);
        assertThat(createdToken.getToken()).isNotNull();
        assertThat(createdToken.getExpiresAt()).isAfter(Instant.now());
        assertThat(createdToken.getRevoked()).isFalse();

        // Verificar que el token expira aproximadamente en 7 días
        long expirationDiff = createdToken.getExpiresAt().toEpochMilli() - Instant.now().toEpochMilli();
        assertThat(expirationDiff).isBetween(REFRESH_TOKEN_EXPIRATION_MS - 1000, REFRESH_TOKEN_EXPIRATION_MS + 1000);

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe encontrar token por su valor")
    void shouldFindTokenByValue() {
        // Given
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(testToken));

        // When
        RefreshToken found = refreshTokenService.findByToken(tokenValue);

        // Then
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(testToken);
        verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando token no existe")
    void shouldThrowExceptionWhenTokenNotFound() {
        // Given
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.findByToken("nonexistent-token"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Refresh token no encontrado");

        verify(refreshTokenRepository, times(1)).findByToken("nonexistent-token");
    }

    @Test
    @DisplayName("Debe verificar token válido sin errores")
    void shouldVerifyValidToken() {
        // When
        RefreshToken verified = refreshTokenService.verifyExpiration(testToken);

        // Then
        assertThat(verified).isNotNull();
        assertThat(verified).isEqualTo(testToken);
        verifyNoInteractions(refreshTokenRepository); // No se debe llamar al repositorio para token válido
    }

    @Test
    @DisplayName("Debe lanzar excepción para token revocado")
    void shouldThrowExceptionForRevokedToken() {
        // Given
        testToken.revoke();

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(testToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("revocado");

        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    @DisplayName("Debe lanzar excepción y eliminar token expirado")
    void shouldThrowExceptionAndDeleteExpiredToken() {
        // Given - token expirado
        RefreshToken expiredToken = new RefreshToken(tokenValue, testUser, Instant.now().minusSeconds(3600));
        expiredToken.setId(2L);

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(expiredToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("expirado");

        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    @DisplayName("Debe revocar token exitosamente")
    void shouldRevokeToken() {
        // Given
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(testToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // When
        refreshTokenService.revokeToken(tokenValue);

        // Then
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(1)).save(tokenCaptor.capture());

        RefreshToken revokedToken = tokenCaptor.getValue();
        assertThat(revokedToken.getRevoked()).isTrue();
        assertThat(revokedToken.getRevokedAt()).isNotNull();
    }

    @Test
    @DisplayName("No debe fallar al revocar token inexistente")
    void shouldNotFailWhenRevokingNonexistentToken() {
        // Given
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // When - no debe lanzar excepción
        refreshTokenService.revokeToken("nonexistent-token");

        // Then
        verify(refreshTokenRepository, times(1)).findByToken("nonexistent-token");
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe revocar todos los tokens de un usuario")
    void shouldRevokeAllUserTokens() {
        // Given
        when(refreshTokenRepository.revokeAllUserTokens(eq(testUser), any(Instant.class))).thenReturn(3);

        // When
        refreshTokenService.revokeAllUserTokens(testUser);

        // Then
        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(eq(testUser), any(Instant.class));
    }

    @Test
    @DisplayName("Debe rotar refresh token exitosamente")
    void shouldRotateRefreshToken() {
        // Given
        RefreshToken newToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS));
        newToken.setId(2L);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> {
                    RefreshToken token = invocation.getArgument(0);
                    if (token.getId() == null) {
                        token.setId(2L);
                    }
                    return token;
                });

        // When
        RefreshToken rotatedToken = refreshTokenService.rotateRefreshToken(testToken, testUser);

        // Then
        assertThat(rotatedToken).isNotNull();
        assertThat(rotatedToken.getUser()).isEqualTo(testUser);
        assertThat(rotatedToken.getToken()).isNotEqualTo(tokenValue);
        assertThat(rotatedToken.getRevoked()).isFalse();

        // Verificar que el token viejo fue revocado
        assertThat(testToken.getRevoked()).isTrue();
        assertThat(testToken.getRevokedAt()).isNotNull();
        assertThat(testToken.getReplacedByToken()).isEqualTo(rotatedToken.getToken());

        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe limpiar tokens expirados")
    void shouldCleanupExpiredTokens() {
        // Given
        when(refreshTokenRepository.deleteExpiredTokens(any(Instant.class))).thenReturn(5);

        // When
        refreshTokenService.cleanupExpiredTokens();

        // Then
        verify(refreshTokenRepository, times(1)).deleteExpiredTokens(any(Instant.class));
    }

    @Test
    @DisplayName("Debe generar UUID único para cada token")
    void shouldGenerateUniqueUUIDForEachToken() {
        // Given
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(token.getId() != null ? token.getId() : 1L);
            return token;
        });

        // When
        RefreshToken token1 = refreshTokenService.createRefreshToken(testUser);
        RefreshToken token2 = refreshTokenService.createRefreshToken(testUser);
        RefreshToken token3 = refreshTokenService.createRefreshToken(testUser);

        // Then
        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
        assertThat(token1.getToken()).isNotEqualTo(token3.getToken());
        assertThat(token2.getToken()).isNotEqualTo(token3.getToken());

        verify(refreshTokenRepository, times(3)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe crear token con fecha de expiración correcta")
    void shouldCreateTokenWithCorrectExpirationDate() {
        // Given
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instant beforeCreation = Instant.now();

        // When
        RefreshToken token = refreshTokenService.createRefreshToken(testUser);

        // Then
        Instant expectedExpiration = beforeCreation.plusMillis(REFRESH_TOKEN_EXPIRATION_MS);
        long timeDiff = Math.abs(token.getExpiresAt().toEpochMilli() - expectedExpiration.toEpochMilli());

        assertThat(timeDiff).isLessThan(1000); // Diferencia menor a 1 segundo
    }

    @Test
    @DisplayName("Debe manejar múltiples usuarios independientemente")
    void shouldHandleMultipleUsersIndependently() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(token.getUser().getId());
            return token;
        });

        // When
        RefreshToken token1 = refreshTokenService.createRefreshToken(testUser);
        RefreshToken token2 = refreshTokenService.createRefreshToken(anotherUser);

        // Then
        assertThat(token1.getUser()).isEqualTo(testUser);
        assertThat(token2.getUser()).isEqualTo(anotherUser);
        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());

        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe verificar que token no expirado pasa validación")
    void shouldVerifyNonExpiredTokenPassesValidation() {
        // Given - token con 7 días de validez
        RefreshToken validToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));

        // When
        RefreshToken verified = refreshTokenService.verifyExpiration(validToken);

        // Then
        assertThat(verified).isNotNull();
        assertThat(verified.isExpired()).isFalse();
        assertThat(verified.getRevoked()).isFalse();
    }

    @Test
    @DisplayName("Debe verificar que cleanup no elimina tokens válidos")
    void shouldVerifyCleanupDoesNotDeleteValidTokens() {
        // Given - simular que no se eliminó ningún token válido
        when(refreshTokenRepository.deleteExpiredTokens(any(Instant.class))).thenReturn(0);

        // When
        refreshTokenService.cleanupExpiredTokens();

        // Then
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(refreshTokenRepository, times(1)).deleteExpiredTokens(instantCaptor.capture());

        Instant deletionTime = instantCaptor.getValue();
        assertThat(deletionTime).isBeforeOrEqualTo(Instant.now());
    }
}
