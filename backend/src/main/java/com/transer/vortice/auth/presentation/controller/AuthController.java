package com.transer.vortice.auth.presentation.controller;

import com.transer.vortice.auth.application.service.AuthService;
import com.transer.vortice.auth.presentation.dto.request.LoginRequest;
import com.transer.vortice.auth.presentation.dto.request.RefreshTokenRequest;
import com.transer.vortice.auth.presentation.dto.request.RegisterRequest;
import com.transer.vortice.auth.presentation.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para endpoints de autenticación.
 * Maneja login, registro, renovación de tokens y logout.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de usuarios")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint de login.
     * Autentica un usuario y genera tokens de acceso.
     *
     * @param loginRequest datos de login (username/email y password)
     * @return respuesta con tokens y datos del usuario
     */
    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario con username/email y password, retorna tokens de acceso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "Usuario bloqueado o inactivo")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Request POST /api/auth/login - usuario: {}", loginRequest.getUsernameOrEmail());

        AuthResponse response = authService.login(loginRequest);

        log.info("Login exitoso para usuario: {}", loginRequest.getUsernameOrEmail());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de registro.
     * Crea un nuevo usuario en el sistema.
     *
     * @param registerRequest datos de registro
     * @return respuesta con tokens y datos del usuario creado
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema y retorna tokens de acceso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "Username o email ya registrado")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Request POST /api/auth/register - usuario: {}", registerRequest.getUsername());

        AuthResponse response = authService.register(registerRequest);

        log.info("Usuario registrado exitosamente: {}", registerRequest.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint de renovación de token.
     * Genera un nuevo access token usando un refresh token válido.
     *
     * @param refreshTokenRequest refresh token
     * @return respuesta con nuevos tokens
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar access token",
            description = "Genera un nuevo access token usando un refresh token válido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "Refresh token revocado")
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Request POST /api/auth/refresh");

        AuthResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());

        log.info("Token renovado exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de logout.
     * Revoca el refresh token del usuario.
     *
     * @param refreshTokenRequest refresh token a revocar
     * @return respuesta vacía con status 204
     */
    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Revoca el refresh token del usuario, invalidando futuras renovaciones"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout exitoso"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido")
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Request POST /api/auth/logout");

        authService.logout(refreshTokenRequest.getRefreshToken());

        log.info("Logout exitoso");

        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de verificación de salud del servicio de autenticación.
     *
     * @return mensaje de estado
     */
    @GetMapping("/health")
    @Operation(
            summary = "Verificar estado del servicio de autenticación",
            description = "Endpoint público para verificar que el servicio de autenticación está funcionando"
    )
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
