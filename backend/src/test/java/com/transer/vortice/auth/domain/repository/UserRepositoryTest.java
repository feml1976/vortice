package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.Role;
import com.transer.vortice.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para UserRepository.
 * Usa @DataJpaTest para levantar una BD H2 en memoria.
 *
 * @author Vórtice Development Team
 */
@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Crear rol de prueba
        testRole = new Role("USER", "Usuario estándar", false);
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
        testUser.setFailedLoginAttempts(0);
        testUser.addRole(testRole);
        testUser = userRepository.save(testUser);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Debe encontrar usuario por username")
    void shouldFindUserByUsername() {
        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Debe encontrar usuario por email")
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Debe encontrar usuario por username o email")
    void shouldFindUserByUsernameOrEmail() {
        // When - buscar por username
        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail("testuser", "testuser");

        // Then
        assertThat(foundByUsername).isPresent();
        assertThat(foundByUsername.get().getUsername()).isEqualTo("testuser");

        // When - buscar por email
        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail(
                "test@example.com", "test@example.com");

        // Then
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Debe retornar empty cuando usuario no existe")
    void shouldReturnEmptyWhenUserNotExists() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar si existe usuario por username")
    void shouldCheckIfUserExistsByUsername() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debe verificar si existe usuario por email")
    void shouldCheckIfUserExistsByEmail() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debe encontrar usuario activo por username")
    void shouldFindActiveUserByUsername() {
        // When
        Optional<User> found = userRepository.findActiveUserByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("No debe encontrar usuario inactivo")
    void shouldNotFindInactiveUser() {
        // Given - crear usuario inactivo
        User inactiveUser = new User();
        inactiveUser.setUsername("inactive");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPasswordHash("$2a$10$hash");
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User");
        inactiveUser.setIsActive(false);
        inactiveUser.setIsLocked(false);
        userRepository.save(inactiveUser);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findActiveUserByUsername("inactive");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe encontrar usuario activo y desbloqueado")
    void shouldFindActiveAndUnlockedUser() {
        // When
        Optional<User> found = userRepository.findActiveAndUnlockedUserByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getIsActive()).isTrue();
        assertThat(found.get().getIsLocked()).isFalse();
    }

    @Test
    @DisplayName("No debe encontrar usuario bloqueado")
    void shouldNotFindLockedUser() {
        // Given - bloquear usuario
        testUser.setIsLocked(true);
        userRepository.save(testUser);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findActiveAndUnlockedUserByUsername("testuser");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe incrementar intentos fallidos de login")
    void shouldIncrementFailedLoginAttempts() {
        // Given
        assertThat(testUser.getFailedLoginAttempts()).isEqualTo(0);

        // When
        testUser.incrementFailedLoginAttempts();
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe bloquear usuario después de 5 intentos fallidos")
    void shouldLockUserAfter5FailedAttempts() {
        // Given
        for (int i = 0; i < 5; i++) {
            testUser.incrementFailedLoginAttempts();
        }

        // When
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(updated.getIsLocked()).isTrue();
    }

    @Test
    @DisplayName("Debe resetear intentos fallidos de login")
    void shouldResetFailedLoginAttempts() {
        // Given
        testUser.incrementFailedLoginAttempts();
        testUser.incrementFailedLoginAttempts();
        userRepository.save(testUser);
        entityManager.flush();

        // When
        testUser.resetFailedLoginAttempts();
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getFailedLoginAttempts()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe registrar login exitoso")
    void shouldRegisterSuccessfulLogin() {
        // When
        testUser.registerSuccessfulLogin();
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getLastLoginAt()).isNotNull();
        assertThat(updated.getFailedLoginAttempts()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe verificar si usuario tiene un rol")
    void shouldCheckIfUserHasRole() {
        // When & Then
        assertThat(testUser.hasRole("USER")).isTrue();
        assertThat(testUser.hasRole("ADMIN")).isFalse();
    }

    @Test
    @DisplayName("Debe obtener nombre completo del usuario")
    void shouldGetFullName() {
        // When
        String fullName = testUser.getFullName();

        // Then
        assertThat(fullName).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Debe verificar si usuario puede hacer login")
    void shouldCheckIfUserCanLogin() {
        // When & Then - usuario activo y desbloqueado
        assertThat(testUser.canLogin()).isTrue();

        // Given - usuario bloqueado
        testUser.setIsLocked(true);
        assertThat(testUser.canLogin()).isFalse();

        // Given - usuario inactivo
        testUser.setIsLocked(false);
        testUser.setIsActive(false);
        assertThat(testUser.canLogin()).isFalse();
    }
}
