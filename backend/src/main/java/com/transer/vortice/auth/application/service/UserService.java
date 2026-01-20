package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.UserRepository;
import com.transer.vortice.auth.presentation.dto.request.ChangePasswordRequest;
import com.transer.vortice.auth.presentation.dto.request.UpdateProfileRequest;
import com.transer.vortice.auth.presentation.dto.response.UserResponse;
import com.transer.vortice.shared.infrastructure.email.EmailService;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import com.transer.vortice.shared.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios.
 * Maneja operaciones de perfil, cambio de contraseña, etc.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Obtiene el usuario autenticado actualmente.
     *
     * @return usuario autenticado
     * @throws BusinessException si no hay usuario autenticado
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("No hay usuario autenticado");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @return datos del perfil del usuario
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        log.info("Obteniendo perfil del usuario autenticado");

        User user = getCurrentAuthenticatedUser();

        return mapToUserResponse(user);
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     *
     * @param updateRequest datos de actualización
     * @return datos actualizados del usuario
     * @throws ValidationException si el email ya está en uso
     */
    @Transactional
    public UserResponse updateCurrentUserProfile(UpdateProfileRequest updateRequest) {
        log.info("Actualizando perfil del usuario autenticado");

        User user = getCurrentAuthenticatedUser();

        // Verificar si el email está siendo cambiado
        if (!user.getEmail().equals(updateRequest.getEmail())) {
            // Verificar que el nuevo email no esté en uso
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new ValidationException("El email ya está en uso por otro usuario");
            }
            user.setEmail(updateRequest.getEmail());
            log.info("Email actualizado para usuario: {}", user.getUsername());
        }

        // Actualizar nombre y apellido
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());

        User updatedUser = userRepository.save(user);

        log.info("Perfil actualizado exitosamente para usuario: {}", user.getUsername());

        return mapToUserResponse(updatedUser);
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param changePasswordRequest datos del cambio de contraseña
     * @throws ValidationException si la contraseña actual es incorrecta o las contraseñas no coinciden
     */
    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        log.info("Solicitando cambio de contraseña para usuario autenticado");

        User user = getCurrentAuthenticatedUser();

        // Validar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Intento de cambio de contraseña con contraseña actual incorrecta: {}", user.getUsername());
            throw new ValidationException("La contraseña actual es incorrecta");
        }

        // Validar que las nuevas contraseñas coincidan
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new ValidationException("Las contraseñas no coinciden");
        }

        // Validar que la nueva contraseña sea diferente a la actual
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPasswordHash())) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Cambiar contraseña
        user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());

        userRepository.save(user);

        log.info("Contraseña cambiada exitosamente para usuario: {}", user.getUsername());

        // Enviar email de confirmación de cambio de contraseña
        try {
            emailService.sendPasswordChangedEmail(user.getEmail(), user.getFullName());
        } catch (Exception e) {
            // No fallar el cambio de contraseña si el email falla
            log.error("Error al enviar email de confirmación de cambio de contraseña a: {}", user.getEmail(), e);
        }
    }

    /**
     * Convierte una entidad User a UserResponse DTO.
     *
     * @param user entidad de usuario
     * @return DTO de respuesta
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .isLocked(user.getIsLocked())
                .lastLoginAt(user.getLastLoginAt())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
