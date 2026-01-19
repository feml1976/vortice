package com.transer.vortice.auth.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transer.vortice.auth.application.service.AuthService;
import com.transer.vortice.auth.presentation.dto.request.LoginRequest;
import com.transer.vortice.auth.presentation.dto.request.RefreshTokenRequest;
import com.transer.vortice.auth.presentation.dto.request.RegisterRequest;
import com.transer.vortice.auth.presentation.dto.response.AuthResponse;
import com.transer.vortice.auth.presentation.dto.response.UserResponse;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import com.transer.vortice.shared.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 * Usa @WebMvcTest para probar la capa de presentación con MockMvc.
 *
 * @author Vórtice Development Team
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitar filtros de seguridad para tests
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Crear LoginRequest de prueba
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        // Crear RegisterRequest de prueba
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        // Crear RefreshTokenRequest de prueba
        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid-refresh-token");

        // Crear AuthResponse de prueba
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .roles(Collections.singleton("USER"))
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("access-token-12345")
                .refreshToken("refresh-token-67890")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userResponse)
                .build();
    }

    @Test
    @DisplayName("POST /auth/login - Debe retornar 200 con login exitoso")
    void shouldReturn200WithSuccessfulLogin() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is("access-token-12345")))
                .andExpect(jsonPath("$.refreshToken", is("refresh-token-67890")))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.user.username", is("testuser")))
                .andExpect(jsonPath("$.user.email", is("test@example.com")));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - Debe retornar 400 con credenciales inválidas")
    void shouldReturn400WithInvalidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException("Credenciales inválidas"));

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Credenciales inválidas")));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - Debe retornar 400 con request body inválido")
    void shouldReturn400WithInvalidRequestBody() throws Exception {
        // Given - request sin password
        loginRequest.setPassword(null);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register - Debe retornar 201 con registro exitoso")
    void shouldReturn201WithSuccessfulRegistration() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is("access-token-12345")))
                .andExpect(jsonPath("$.refreshToken", is("refresh-token-67890")))
                .andExpect(jsonPath("$.user.username", is("testuser")));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register - Debe retornar 409 con username duplicado")
    void shouldReturn409WithDuplicateUsername() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("El nombre de usuario ya está en uso"));

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("El nombre de usuario ya está en uso")));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register - Debe retornar 400 con datos incompletos")
    void shouldReturn400WithIncompleteRegistrationData() throws Exception {
        // Given - request sin email
        registerRequest.setEmail(null);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/refresh - Debe retornar 200 con token renovado")
    void shouldReturn200WithRefreshedToken() throws Exception {
        // Given
        AuthResponse refreshedResponse = AuthResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(authResponse.getUser())
                .build();

        when(authService.refreshToken(anyString())).thenReturn(refreshedResponse);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is("new-access-token")))
                .andExpect(jsonPath("$.refreshToken", is("new-refresh-token")));

        verify(authService, times(1)).refreshToken("valid-refresh-token");
    }

    @Test
    @DisplayName("POST /auth/refresh - Debe retornar 400 con token inválido")
    void shouldReturn400WithInvalidRefreshToken() throws Exception {
        // Given
        when(authService.refreshToken(anyString()))
                .thenThrow(new NotFoundException("Refresh token no encontrado"));

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Refresh token no encontrado")));

        verify(authService, times(1)).refreshToken(anyString());
    }

    @Test
    @DisplayName("POST /auth/refresh - Debe retornar 400 con token expirado")
    void shouldReturn400WithExpiredRefreshToken() throws Exception {
        // Given
        when(authService.refreshToken(anyString()))
                .thenThrow(new BusinessException("El refresh token ha expirado"));

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("El refresh token ha expirado")));

        verify(authService, times(1)).refreshToken(anyString());
    }

    @Test
    @DisplayName("POST /auth/logout - Debe retornar 204 con logout exitoso")
    void shouldReturn204WithSuccessfulLogout() throws Exception {
        // Given
        doNothing().when(authService).logout(anyString());

        // When & Then
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isNoContent())
                .andExpect(content().string("")); // No content

        verify(authService, times(1)).logout("valid-refresh-token");
    }

    @Test
    @DisplayName("POST /auth/logout - Debe retornar 400 con token inválido")
    void shouldReturn400WithInvalidTokenOnLogout() throws Exception {
        // Given
        doThrow(new NotFoundException("Refresh token no encontrado"))
                .when(authService).logout(anyString());

        // When & Then
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Refresh token no encontrado")));

        verify(authService, times(1)).logout(anyString());
    }

    @Test
    @DisplayName("GET /auth/health - Debe retornar 200 con servicio funcionando")
    void shouldReturn200WithHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth service is running"));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /auth/login - Debe aceptar login por email")
    void shouldAcceptLoginByEmail() throws Exception {
        // Given
        loginRequest.setUsernameOrEmail("test@example.com");
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username", is("testuser")));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - Debe rechazar request sin username o password")
    void shouldRejectRequestWithoutUsernameOrPassword() throws Exception {
        // Given - request vacío
        LoginRequest emptyRequest = new LoginRequest();

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register - Debe validar formato de email")
    void shouldValidateEmailFormat() throws Exception {
        // Given - email inválido
        registerRequest.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register - Debe validar longitud mínima de password")
    void shouldValidatePasswordMinLength() throws Exception {
        // Given - password muy corto
        registerRequest.setPassword("123");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/refresh - Debe rechazar request sin token")
    void shouldRejectRefreshRequestWithoutToken() throws Exception {
        // Given - request sin token
        RefreshTokenRequest emptyRequest = new RefreshTokenRequest();

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).refreshToken(anyString());
    }

    @Test
    @DisplayName("Debe retornar Content-Type application/json para todos los endpoints")
    void shouldReturnApplicationJsonContentType() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);
        when(authService.refreshToken(anyString())).thenReturn(authResponse);

        // When & Then - Login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // When & Then - Register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // When & Then - Refresh
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
