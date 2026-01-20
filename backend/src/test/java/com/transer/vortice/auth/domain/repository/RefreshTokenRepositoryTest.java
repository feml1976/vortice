package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.RefreshToken;
import com.transer.vortice.auth.domain.model.Role;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.shared.infrastructure.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para RefreshTokenRepository.
 * Usa Testcontainers con PostgreSQL para ejecutar tests contra una BD real.
 *
 * @author Vórtice Development Team
 */
@DisplayName("RefreshTokenRepository Tests")
class RefreshTokenRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private RefreshToken testToken;
    private String tokenValue;

    @BeforeEach
    void setUp() {
        // Crear rol de prueba
        Role testRole = new Role("USER", "Usuario estándar", false);
        testRole = roleRepository.save(testRole);

        // Crear usuario de prueba
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setIsActive(true);
        testUser.setIsLocked(false);
        testUser.addRole(testRole);
        testUser = userRepository.save(testUser);

        // Crear refresh token de prueba
        tokenValue = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(604800); // 7 días
        testToken = new RefreshToken(tokenValue, testUser, expiresAt);
        testToken = refreshTokenRepository.save(testToken);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Debe encontrar token por su valor")
    void shouldFindTokenByValue() {
        // When
        Optional<RefreshToken> found = refreshTokenRepository.findByToken(tokenValue);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo(tokenValue);
        assertThat(found.get().getUser().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getRevoked()).isFalse();
    }

    @Test
    @DisplayName("Debe retornar empty cuando token no existe")
    void shouldReturnEmptyWhenTokenNotExists() {
        // When
        Optional<RefreshToken> found = refreshTokenRepository.findByToken("nonexistent-token");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar si existe token por su valor")
    void shouldCheckIfTokenExists() {
        // When
        boolean exists = refreshTokenRepository.existsByToken(tokenValue);
        boolean notExists = refreshTokenRepository.existsByToken("nonexistent-token");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debe encontrar token válido (no revocado y no expirado)")
    void shouldFindValidToken() {
        // When
        Optional<RefreshToken> found = refreshTokenRepository.findValidToken(tokenValue, Instant.now());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo(tokenValue);
        assertThat(found.get().getRevoked()).isFalse();
        assertThat(found.get().getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("No debe encontrar token revocado")
    void shouldNotFindRevokedToken() {
        // Given - revocar token
        testToken.revoke();
        refreshTokenRepository.save(testToken);
        entityManager.flush();

        // When
        Optional<RefreshToken> found = refreshTokenRepository.findValidToken(tokenValue, Instant.now());

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("No debe encontrar token expirado")
    void shouldNotFindExpiredToken() {
        // Given - crear token expirado
        String expiredTokenValue = UUID.randomUUID().toString();
        Instant expiredAt = Instant.now().minusSeconds(3600); // Expirado hace 1 hora
        RefreshToken expiredToken = new RefreshToken(expiredTokenValue, testUser, expiredAt);
        refreshTokenRepository.save(expiredToken);
        entityManager.flush();

        // When
        Optional<RefreshToken> found = refreshTokenRepository.findValidToken(expiredTokenValue, Instant.now());

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe encontrar todos los tokens de un usuario")
    void shouldFindAllTokensByUser() {
        // Given - crear tokens adicionales
        RefreshToken token2 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        RefreshToken token3 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        refreshTokenRepository.save(token2);
        refreshTokenRepository.save(token3);
        entityManager.flush();

        // When
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(testUser);

        // Then
        assertThat(tokens).hasSize(3);
        assertThat(tokens)
                .extracting(RefreshToken::getUser)
                .allMatch(user -> user.getId().equals(testUser.getId()));
    }

    @Test
    @DisplayName("Debe encontrar solo tokens válidos de un usuario")
    void shouldFindValidTokensByUser() {
        // Given - crear token revocado y token expirado
        RefreshToken revokedToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        revokedToken.revoke();
        refreshTokenRepository.save(revokedToken);

        RefreshToken expiredToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().minusSeconds(3600));
        refreshTokenRepository.save(expiredToken);

        entityManager.flush();

        // When
        List<RefreshToken> validTokens = refreshTokenRepository.findValidTokensByUser(testUser, Instant.now());

        // Then
        assertThat(validTokens).hasSize(1);
        assertThat(validTokens.get(0).getToken()).isEqualTo(tokenValue);
        assertThat(validTokens)
                .allMatch(token -> !token.getRevoked() && token.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Debe revocar todos los tokens de un usuario")
    void shouldRevokeAllUserTokens() {
        // Given - crear tokens adicionales
        RefreshToken token2 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        RefreshToken token3 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        refreshTokenRepository.save(token2);
        refreshTokenRepository.save(token3);
        entityManager.flush();

        // When
        int revokedCount = refreshTokenRepository.revokeAllUserTokens(testUser, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(revokedCount).isEqualTo(3);
        List<RefreshToken> allTokens = refreshTokenRepository.findByUser(testUser);
        assertThat(allTokens).allMatch(RefreshToken::getRevoked);
    }

    @Test
    @DisplayName("Debe eliminar tokens expirados")
    void shouldDeleteExpiredTokens() {
        // Given - crear tokens expirados
        RefreshToken expiredToken1 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().minusSeconds(7200));
        RefreshToken expiredToken2 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().minusSeconds(3600));
        refreshTokenRepository.save(expiredToken1);
        refreshTokenRepository.save(expiredToken2);
        entityManager.flush();

        // Total de tokens antes: 3 (1 válido + 2 expirados)
        assertThat(refreshTokenRepository.findAll()).hasSize(3);

        // When
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(deletedCount).isEqualTo(2);
        assertThat(refreshTokenRepository.findAll()).hasSize(1);
        assertThat(refreshTokenRepository.findByToken(tokenValue)).isPresent();
    }

    @Test
    @DisplayName("Debe eliminar todos los tokens de un usuario")
    void shouldDeleteAllUserTokens() {
        // Given - crear tokens adicionales
        RefreshToken token2 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        RefreshToken token3 = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        refreshTokenRepository.save(token2);
        refreshTokenRepository.save(token3);
        entityManager.flush();

        assertThat(refreshTokenRepository.findByUser(testUser)).hasSize(3);

        // When
        int deletedCount = refreshTokenRepository.deleteByUser(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(deletedCount).isEqualTo(3);
        assertThat(refreshTokenRepository.findByUser(testUser)).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar si token está expirado")
    void shouldCheckIfTokenIsExpired() {
        // Given - crear token expirado
        String expiredTokenValue = UUID.randomUUID().toString();
        Instant expiredAt = Instant.now().minusSeconds(3600);
        RefreshToken expiredToken = new RefreshToken(expiredTokenValue, testUser, expiredAt);
        refreshTokenRepository.save(expiredToken);
        entityManager.flush();

        // When
        RefreshToken validToken = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        RefreshToken expired = refreshTokenRepository.findByToken(expiredTokenValue).orElseThrow();

        // Then
        assertThat(validToken.isExpired()).isFalse();
        assertThat(expired.isExpired()).isTrue();
    }

    @Test
    @DisplayName("Debe verificar si token es válido")
    void shouldCheckIfTokenIsValid() {
        // Given - crear token expirado y token revocado
        RefreshToken expiredToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().minusSeconds(3600));
        refreshTokenRepository.save(expiredToken);

        RefreshToken revokedToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        revokedToken.revoke();
        refreshTokenRepository.save(revokedToken);

        entityManager.flush();

        // When
        RefreshToken validToken = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        RefreshToken expired = refreshTokenRepository.findById(expiredToken.getId()).orElseThrow();
        RefreshToken revoked = refreshTokenRepository.findById(revokedToken.getId()).orElseThrow();

        // Then
        assertThat(validToken.isValid()).isTrue();
        assertThat(expired.isValid()).isFalse();
        assertThat(revoked.isValid()).isFalse();
    }

    @Test
    @DisplayName("Debe revocar token")
    void shouldRevokeToken() {
        // Given
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        assertThat(token.getRevoked()).isFalse();
        assertThat(token.getRevokedAt()).isNull();

        // When
        token.revoke();
        refreshTokenRepository.save(token);
        entityManager.flush();
        entityManager.clear();

        // Then
        RefreshToken updated = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        assertThat(updated.getRevoked()).isTrue();
        assertThat(updated.getRevokedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe revocar y reemplazar token")
    void shouldRevokeAndReplaceToken() {
        // Given
        RefreshToken oldToken = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        String newTokenValue = UUID.randomUUID().toString();

        // When
        oldToken.revokeAndReplace(newTokenValue);
        refreshTokenRepository.save(oldToken);
        entityManager.flush();
        entityManager.clear();

        // Then
        RefreshToken updated = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        assertThat(updated.getRevoked()).isTrue();
        assertThat(updated.getRevokedAt()).isNotNull();
        assertThat(updated.getReplacedByToken()).isEqualTo(newTokenValue);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando usuario no tiene tokens")
    void shouldReturnEmptyListWhenUserHasNoTokens() {
        // Given - crear usuario sin tokens
        User userWithoutTokens = new User();
        userWithoutTokens.setUsername("notokens");
        userWithoutTokens.setEmail("notokens@example.com");
        userWithoutTokens.setPasswordHash("$2a$10$hash");
        userWithoutTokens.setFirstName("No");
        userWithoutTokens.setLastName("Tokens");
        userWithoutTokens.setIsActive(true);
        userWithoutTokens = userRepository.save(userWithoutTokens);
        entityManager.flush();

        // When
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(userWithoutTokens);
        List<RefreshToken> validTokens = refreshTokenRepository.findValidTokensByUser(userWithoutTokens, Instant.now());

        // Then
        assertThat(tokens).isEmpty();
        assertThat(validTokens).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar 0 cuando no hay tokens para revocar")
    void shouldReturn0WhenNoTokensToRevoke() {
        // Given - crear usuario sin tokens
        User userWithoutTokens = new User();
        userWithoutTokens.setUsername("notokens");
        userWithoutTokens.setEmail("notokens@example.com");
        userWithoutTokens.setPasswordHash("$2a$10$hash");
        userWithoutTokens.setFirstName("No");
        userWithoutTokens.setLastName("Tokens");
        userWithoutTokens.setIsActive(true);
        userWithoutTokens = userRepository.save(userWithoutTokens);
        entityManager.flush();

        // When
        int revokedCount = refreshTokenRepository.revokeAllUserTokens(userWithoutTokens, Instant.now());

        // Then
        assertThat(revokedCount).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe manejar múltiples usuarios con sus tokens")
    void shouldHandleMultipleUsersWithTokens() {
        // Given - crear otro usuario con tokens
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordHash("$2a$10$hash");
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser.setIsActive(true);
        anotherUser = userRepository.save(anotherUser);

        RefreshToken anotherToken = new RefreshToken(UUID.randomUUID().toString(), anotherUser,
                Instant.now().plusSeconds(604800));
        refreshTokenRepository.save(anotherToken);
        entityManager.flush();

        // When
        List<RefreshToken> testUserTokens = refreshTokenRepository.findByUser(testUser);
        List<RefreshToken> anotherUserTokens = refreshTokenRepository.findByUser(anotherUser);

        // Then
        assertThat(testUserTokens).hasSize(1);
        assertThat(anotherUserTokens).hasSize(1);
        assertThat(testUserTokens.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(anotherUserTokens.get(0).getUser().getId()).isEqualTo(anotherUser.getId());
    }
}
