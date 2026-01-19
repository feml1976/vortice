package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.Role;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CustomUserDetailsService.
 * Usa Mockito para simular dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Crear roles de prueba
        adminRole = new Role("ADMIN", "Administrador", true);
        userRole = new Role("USER", "Usuario", false);

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
        testUser.addRole(userRole);
    }

    @Test
    @DisplayName("Debe cargar usuario por username")
    void shouldLoadUserByUsername() {
        // Given
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$hashedpassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();

        verify(userRepository, times(1)).findByUsernameOrEmail("testuser", "testuser");
    }

    @Test
    @DisplayName("Debe cargar usuario por email")
    void shouldLoadUserByEmail() {
        // Given
        when(userRepository.findByUsernameOrEmail("test@example.com", "test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");

        verify(userRepository, times(1))
                .findByUsernameOrEmail("test@example.com", "test@example.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(userRepository, times(1)).findByUsernameOrEmail("nonexistent", "nonexistent");
    }

    @Test
    @DisplayName("Debe construir UserDetails con authorities correctas")
    void shouldBuildUserDetailsWithCorrectAuthorities() {
        // Given
        testUser.addRole(adminRole);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(2);
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Debe manejar cuenta bloqueada correctamente")
    void shouldHandleLockedAccountCorrectly() {
        // Given
        testUser.setIsLocked(true);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.isAccountNonLocked()).isFalse();
        assertThat(userDetails.isEnabled()).isTrue(); // Sigue activo pero bloqueado
    }

    @Test
    @DisplayName("Debe manejar cuenta inactiva correctamente")
    void shouldHandleInactiveAccountCorrectly() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.isAccountNonLocked()).isTrue(); // No bloqueado pero inactivo
    }

    @Test
    @DisplayName("Debe construir UserDetails con múltiples roles")
    void shouldBuildUserDetailsWithMultipleRoles() {
        // Given
        testUser.addRole(adminRole);
        Role managerRole = new Role("MANAGER", "Gerente", false);
        testUser.addRole(managerRole);

        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(3);
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");
    }

    @Test
    @DisplayName("Debe construir UserDetails sin roles")
    void shouldBuildUserDetailsWithoutRoles() {
        // Given - usuario sin roles
        User userWithoutRoles = new User();
        userWithoutRoles.setId(2L);
        userWithoutRoles.setUsername("noroles");
        userWithoutRoles.setEmail("noroles@example.com");
        userWithoutRoles.setPasswordHash("$2a$10$hash");
        userWithoutRoles.setFirstName("No");
        userWithoutRoles.setLastName("Roles");
        userWithoutRoles.setIsActive(true);
        userWithoutRoles.setIsLocked(false);

        when(userRepository.findByUsernameOrEmail("noroles", "noroles"))
                .thenReturn(Optional.of(userWithoutRoles));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("noroles");

        // Then
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Debe manejar cuenta bloqueada e inactiva")
    void shouldHandleLockedAndInactiveAccount() {
        // Given
        testUser.setIsLocked(true);
        testUser.setIsActive(false);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.isAccountNonLocked()).isFalse();
    }

    @Test
    @DisplayName("Debe usar username en lugar de email en UserDetails")
    void shouldUseUsernameInsteadOfEmailInUserDetails() {
        // Given - buscar por email pero UserDetails debe usar username
        when(userRepository.findByUsernameOrEmail("test@example.com", "test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertThat(userDetails.getUsername()).isEqualTo("testuser"); // No el email
    }

    @Test
    @DisplayName("Debe agregar prefijo ROLE_ a los nombres de roles")
    void shouldAddRolePrefixToRoleNames() {
        // Given
        testUser.addRole(adminRole);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .allMatch(authority -> authority.startsWith("ROLE_"));
    }

    @Test
    @DisplayName("Debe manejar nombres de usuario con diferentes formatos")
    void shouldHandleUserNamesWithDifferentFormats() {
        // Given - usuario con username especial
        User specialUser = new User();
        specialUser.setId(3L);
        specialUser.setUsername("user.name-123");
        specialUser.setEmail("special@example.com");
        specialUser.setPasswordHash("$2a$10$hash");
        specialUser.setFirstName("Special");
        specialUser.setLastName("User");
        specialUser.setIsActive(true);
        specialUser.setIsLocked(false);
        specialUser.addRole(userRole);

        when(userRepository.findByUsernameOrEmail("user.name-123", "user.name-123"))
                .thenReturn(Optional.of(specialUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("user.name-123");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("user.name-123");
    }

    @Test
    @DisplayName("Debe llamar al repositorio solo una vez por carga")
    void shouldCallRepositoryOncePerLoad() {
        // Given
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        userDetailsService.loadUserByUsername("testuser");

        // Then
        verify(userRepository, times(1)).findByUsernameOrEmail("testuser", "testuser");
        verifyNoMoreInteractions(userRepository);
    }
}
