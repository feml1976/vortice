package com.transer.vortice.auth.presentation.controller;

import com.transer.vortice.auth.application.service.UserService;
import com.transer.vortice.auth.presentation.dto.request.ChangePasswordRequest;
import com.transer.vortice.auth.presentation.dto.request.UpdateProfileRequest;
import com.transer.vortice.auth.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de usuarios.
 * Maneja operaciones de perfil, cambio de contraseña, etc.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para gestión de perfil y configuración de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @return datos del perfil del usuario
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Obtener perfil del usuario",
            description = "Retorna los datos del perfil del usuario autenticado actualmente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        log.info("Request GET /api/users/me");

        UserResponse userResponse = userService.getCurrentUserProfile();

        log.info("Perfil obtenido exitosamente para usuario: {}", userResponse.getUsername());

        return ResponseEntity.ok(userResponse);
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     *
     * @param updateRequest datos de actualización
     * @return datos actualizados del usuario
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Actualizar perfil del usuario",
            description = "Actualiza los datos del perfil del usuario autenticado (email, nombre, apellido)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "409", description = "Email ya está en uso")
    })
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest updateRequest) {
        log.info("Request PUT /api/users/me");

        UserResponse userResponse = userService.updateCurrentUserProfile(updateRequest);

        log.info("Perfil actualizado exitosamente para usuario: {}", userResponse.getUsername());

        return ResponseEntity.ok(userResponse);
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param changePasswordRequest datos del cambio de contraseña
     * @return respuesta vacía con status 204
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Cambiar contraseña",
            description = "Cambia la contraseña del usuario autenticado. Requiere la contraseña actual y la nueva contraseña."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contraseña cambiada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o contraseña actual incorrecta"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Request PUT /api/users/me/password");

        userService.changePassword(changePasswordRequest);

        log.info("Contraseña cambiada exitosamente");

        return ResponseEntity.noContent().build();
    }
}
