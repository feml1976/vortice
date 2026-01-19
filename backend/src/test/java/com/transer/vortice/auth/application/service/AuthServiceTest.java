package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.RefreshToken;
import com.transer.vortice.auth.domain.model.Role;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.RoleRepository;
import com.transer.vortice.auth.domain.repository.UserRepository;
import com.transer.vortice.auth.presentation.dto.request.LoginRequest;
import com.transer.vortice.auth.presentation.dto.request.RegisterRequest;
import com.transer.vortice.auth.presentation.dto.response.AuthResponse;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.ValidationException;
import com.transer.vortice.shared.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService.
 * Usa Mockito para simular dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role userRole;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private RefreshToken refreshToken;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Crear rol de prueba
        userRole = new Role("USER", "Usuario estándar", false);
        userRole.setId(1L);

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
        testUser.setFailedLoginAttempts(0);
        testUser.addRole(userRole);

        // Crear request de login
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        // Crear request de registro
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        // Crear refresh token
        refreshToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        refreshToken.setId(1L);

        // Crear authentication
        authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password123",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("Debe realizar login exitosamente")
    void shouldLoginSuccessfully() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken.getToken());
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(refreshTokenService, times(1)).createRefreshToken(testUser);
    }

    @Test
    @DisplayName("Debe lanzar excepción con credenciales inválidas")
    void shouldThrowExceptionWithInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales inválidas");

        // Verificar que se incrementaron los intentos fallidos
        verify(userRepository, times(1)).save(testUser);
        assertThat(testUser.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe bloquear cuenta después de 5 intentos fallidos")
    void shouldLockAccountAfter5FailedAttempts() {
        // Given
        testUser.setFailedLoginAttempts(4); // Ya tiene 4 intentos fallidos
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class);

        // Verificar que la cuenta fue bloqueada
        verify(userRepository, times(1)).save(testUser);
        assertThat(testUser.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(testUser.getIsLocked()).isTrue();
    }

    @Test
    @DisplayName("Debe lanzar excepción con cuenta bloqueada")
    void shouldThrowExceptionWithLockedAccount() {
        // Given
        testUser.setIsLocked(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqueada");

        verify(jwtTokenProvider, never()).generateToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción con cuenta inactiva")
    void shouldThrowExceptionWithInactiveAccount() {
        // Given
        testUser.setIsActive(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactiva");

        verify(jwtTokenProvider, never()).generateToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    @Test
    @DisplayName("Debe registrar nuevo usuario exitosamente")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(refreshToken);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken.getToken());
        assertThat(response.getUser()).isNotNull();

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción con username duplicado")
    void shouldThrowExceptionWithDuplicateUsername() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("nombre de usuario ya está en uso");

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción con email duplicado")
    void shouldThrowExceptionWithDuplicateEmail() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("correo electrónico ya está registrado");

        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe renovar token exitosamente")
    void shouldRefreshTokenSuccessfully() {
        // Given
        String refreshTokenValue = refreshToken.getToken();
        RefreshToken newRefreshToken = new RefreshToken(UUID.randomUUID().toString(), testUser,
                Instant.now().plusSeconds(604800));
        newRefreshToken.setId(2L);

        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(refreshToken);
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(refreshTokenService.rotateRefreshToken(refreshToken, testUser)).thenReturn(newRefreshToken);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("new-access-token");

        // When
        AuthResponse response = authService.refreshToken(refreshTokenValue);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken.getToken());
        assertThat(response.getUser()).isNotNull();

        verify(refreshTokenService, times(1)).findByToken(refreshTokenValue);
        verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
        verify(refreshTokenService, times(1)).rotateRefreshToken(refreshToken, testUser);
    }

    @Test
    @DisplayName("Debe realizar logout exitosamente")
    void shouldLogoutSuccessfully() {
        // Given
        String refreshTokenValue = refreshToken.getToken();
        doNothing().when(refreshTokenService).revokeToken(refreshTokenValue);

        // When
        authService.logout(refreshTokenValue);

        // Then
        verify(refreshTokenService, times(1)).revokeToken(refreshTokenValue);
    }

    @Test
    @DisplayName("Debe resetear intentos fallidos después de login exitoso")
    void shouldResetFailedAttemptsAfterSuccessfulLogin() {
        // Given
        testUser.setFailedLoginAttempts(3);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        authService.login(loginRequest);

        // Then
        assertThat(testUser.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(testUser.getLastLoginAt()).isNotNull();
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Debe asignar rol USER por defecto al registrar")
    void shouldAssignUserRoleByDefaultWhenRegistering() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(refreshToken);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        verify(roleRepository, times(1)).findByName("USER");
        assertThat(response.getUser()).isNotNull();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando rol USER no existe")
    void shouldThrowExceptionWhenUserRoleNotExists() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Rol USER no encontrado");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe manejar login por email")
    void shouldHandleLoginByEmail() {
        // Given
        loginRequest.setUsernameOrEmail("test@example.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("test@example.com", "test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");

        verify(userRepository, times(1))
                .findByUsernameOrEmail("test@example.com", "test@example.com");
    }

    @Test
    @DisplayName("Debe incluir información del usuario en AuthResponse")
    void shouldIncludeUserInfoInAuthResponse() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getUser().getFirstName()).isEqualTo("Test");
        assertThat(response.getUser().getLastName()).isEqualTo("User");
        assertThat(response.getUser().getRoles()).isNotEmpty();
    }
}
