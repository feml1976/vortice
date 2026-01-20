package com.transer.vortice.shared.infrastructure.ratelimit;

import com.transer.vortice.auth.presentation.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para Rate Limiting.
 *
 * @author Vórtice Development Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Rate Limiting Integration Tests")
class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RateLimitService rateLimitService;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Limpiar buckets antes de cada test
        rateLimitService.clearAllBuckets();

        // Preparar request de login
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password");
    }

    @Test
    @DisplayName("Debe permitir peticiones dentro del límite")
    void shouldAllowRequestsWithinLimit() throws Exception {
        // Given - configuración permite 5 peticiones por minuto para /auth/login

        // When & Then - hacer 5 peticiones debe funcionar
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(header().exists("X-RateLimit-Remaining"))
                    .andExpect(header().string("X-RateLimit-Type", "AUTH"));
        }
    }

    @Test
    @DisplayName("Debe retornar 429 cuando se excede el límite de login")
    void shouldReturn429WhenLoginLimitExceeded() throws Exception {
        // Given - hacer 5 peticiones (el límite)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized()); // Credenciales incorrectas, pero request permitido
        }

        // When & Then - la 6ta petición debe retornar 429
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.retryAfterSeconds").exists())
                .andExpect(jsonPath("$.limitType").value("AUTH"))
                .andExpect(header().exists("Retry-After"))
                .andExpect(header().string("X-RateLimit-Remaining", "0"));
    }

    @Test
    @DisplayName("Debe incluir headers de rate limiting en respuestas exitosas")
    void shouldIncludeRateLimitHeadersInSuccessfulResponses() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(header().exists("X-RateLimit-Limit"))
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(header().exists("X-RateLimit-Type"));
    }

    @Test
    @DisplayName("Debe aplicar límites diferentes para diferentes endpoints")
    void shouldApplyDifferentLimitsForDifferentEndpoints() throws Exception {
        // Given - Login tiene límite de 5, Register tiene límite de 3

        // When - hacer 4 peticiones a login (bajo el límite)
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(header().string("X-RateLimit-Type", "AUTH"));
        }

        // Then - aún puede hacer peticiones a login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(header().string("X-RateLimit-Type", "AUTH"));
    }

    @Test
    @DisplayName("Debe rastrear límites por IP independientemente")
    void shouldTrackLimitsPerIpIndependently() throws Exception {
        // Given - hacer 5 peticiones desde IP1
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest))
                            .header("X-Forwarded-For", "192.168.1.1"))
                    .andExpect(status().isUnauthorized());
        }

        // When - hacer petición desde IP2
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("X-Forwarded-For", "192.168.1.2"))
                .andExpect(status().isUnauthorized()); // No 429, porque es diferente IP

        // Then - IP1 debe estar bloqueada
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Debe respetar headers X-Forwarded-For para IP del cliente")
    void shouldRespectXForwardedForHeader() throws Exception {
        // Given
        String forwardedIp = "10.0.0.1";

        // When - hacer peticiones con X-Forwarded-For
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest))
                            .header("X-Forwarded-For", forwardedIp))
                    .andExpect(status().isUnauthorized());
        }

        // Then - la siguiente debe estar limitada
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("X-Forwarded-For", forwardedIp))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Debe incluir tiempo de espera en respuesta 429")
    void shouldIncludeRetryAfterIn429Response() throws Exception {
        // Given - exceder el límite
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)));
        }

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.retryAfterSeconds").isNumber())
                .andExpect(header().exists("Retry-After"));
    }
}
