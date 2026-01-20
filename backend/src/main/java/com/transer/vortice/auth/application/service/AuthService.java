package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.PasswordResetToken;
import com.transer.vortice.auth.domain.model.RefreshToken;
import com.transer.vortice.auth.domain.model.Role;
import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.RoleRepository;
import com.transer.vortice.auth.domain.repository.UserRepository;
import com.transer.vortice.auth.presentation.dto.request.ForgotPasswordRequest;
import com.transer.vortice.auth.presentation.dto.request.LoginRequest;
import com.transer.vortice.auth.presentation.dto.request.RegisterRequest;
import com.transer.vortice.auth.presentation.dto.request.ResetPasswordRequest;
import com.transer.vortice.auth.presentation.dto.response.AuthResponse;
import com.transer.vortice.auth.presentation.dto.response.UserResponse;
import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import com.transer.vortice.shared.infrastructure.exception.ValidationException;
import com.transer.vortice.shared.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Servicio principal de autenticación.
 * Maneja login, registro, renovación de tokens y logout.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${vortice.jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Autentica un usuario y genera tokens de acceso.
     *
     * @param loginRequest datos de login
     * @return respuesta con tokens y datos del usuario
     * @throws BusinessException si las credenciales son inválidas
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getUsernameOrEmail());

        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Buscar usuario en BD
            User user = userRepository.findByUsernameOrEmail(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getUsernameOrEmail())
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            // Verificar que el usuario puede hacer login
            if (!user.canLogin()) {
                if (!user.getIsActive()) {
                    log.warn("Intento de login con usuario inactivo: {}", user.getUsername());
                    throw new DisabledException("La cuenta de usuario está desactivada");
                }
                if (user.getIsLocked()) {
                    log.warn("Intento de login con usuario bloqueado: {}", user.getUsername());
                    throw new LockedException("La cuenta de usuario está bloqueada debido a múltiples intentos fallidos");
                }
            }

            // Registrar login exitoso
            user.registerSuccessfulLogin();
            userRepository.save(user);

            // Generar tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            log.info("Login exitoso para usuario: {} (ID: {})", user.getUsername(), user.getId());

            return buildAuthResponse(accessToken, refreshToken.getToken(), user);

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para usuario: {}", loginRequest.getUsernameOrEmail());

            // Incrementar contador de intentos fallidos
            userRepository.findByUsernameOrEmail(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getUsernameOrEmail())
                    .ifPresent(user -> {
                        user.incrementFailedLoginAttempts();
                        userRepository.save(user);

                        if (user.getIsLocked()) {
                            log.warn("Usuario bloqueado por múltiples intentos fallidos: {}",
                                    user.getUsername());
                        }
                    });

            throw new BusinessException("Credenciales inválidas");

        } catch (DisabledException e) {
            log.warn("Intento de login con usuario desactivado: {}", loginRequest.getUsernameOrEmail());
            throw new BusinessException("La cuenta de usuario está desactivada");

        } catch (LockedException e) {
            log.warn("Intento de login con usuario bloqueado: {}", loginRequest.getUsernameOrEmail());
            throw new BusinessException("La cuenta de usuario está bloqueada. Contacte al administrador");

        } catch (AuthenticationException e) {
            log.error("Error de autenticación para usuario: {}", loginRequest.getUsernameOrEmail(), e);
            throw new BusinessException("Error al autenticar usuario: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest datos de registro
     * @return respuesta con tokens y datos del usuario creado
     * @throws ValidationException si ya existe un usuario con ese username/email
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Intento de registro para usuario: {}", registerRequest.getUsername());

        // Validar que no exista el usuario
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Intento de registro con username ya existente: {}", registerRequest.getUsername());
            throw new ValidationException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Intento de registro con email ya existente: {}", registerRequest.getEmail());
            throw new ValidationException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setPasswordChangedAt(Instant.now());

        // Asignar rol por defecto (puedes personalizar esto según tus necesidades)
        Role defaultRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    // Si no existe el rol USER, crear uno temporal
                    Role newRole = new Role("USER", "Usuario estándar", false);
                    return roleRepository.save(newRole);
                });
        user.addRole(defaultRole);

        // Guardar usuario
        user = userRepository.save(user);

        log.info("Usuario registrado exitosamente: {} (ID: {})", user.getUsername(), user.getId());

        // Generar tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                registerRequest.getPassword()
        );
        String accessToken = jwtTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken.getToken(), user);
    }

    /**
     * Renueva el access token usando un refresh token válido.
     *
     * @param refreshTokenValue valor del refresh token
     * @return respuesta con nuevos tokens
     * @throws BusinessException si el refresh token no es válido
     */
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        log.debug("Intentando renovar access token");

        // Buscar y verificar el refresh token
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenValue);
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        // Verificar que el usuario siga activo
        if (!user.canLogin()) {
            log.warn("Intento de refresh token con usuario inactivo/bloqueado: {}", user.getUsername());
            throw new BusinessException("El usuario no puede acceder al sistema");
        }

        // Rotar el refresh token (security best practice)
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, user);

        // Generar nuevo access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                user.getRoles().stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + role.getName()))
                        .collect(Collectors.toSet())
        );
        String newAccessToken = jwtTokenProvider.generateToken(authentication);

        log.info("Access token renovado exitosamente para usuario: {} (ID: {})",
                user.getUsername(), user.getId());

        return buildAuthResponse(newAccessToken, newRefreshToken.getToken(), user);
    }

    /**
     * Cierra la sesión de un usuario revocando su refresh token.
     *
     * @param refreshTokenValue valor del refresh token a revocar
     */
    @Transactional
    public void logout(String refreshTokenValue) {
        log.info("Procesando logout");

        refreshTokenService.revokeToken(refreshTokenValue);

        log.info("Logout exitoso");
    }

    /**
     * Solicita recuperación de contraseña para un usuario.
     * Genera un token de reset que será enviado por email.
     *
     * @param forgotPasswordRequest email del usuario
     * @return token de reset generado (en producción, esto se enviaría por email)
     */
    @Transactional
    public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Solicitud de recuperación de contraseña para email: {}", forgotPasswordRequest.getEmail());

        // Buscar usuario por email
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Solicitud de recuperación de contraseña para email no registrado: {}",
                            forgotPasswordRequest.getEmail());
                    // Por seguridad, no revelar si el email existe o no
                    return new NotFoundException("Si el email está registrado, recibirás un enlace de recuperación");
                });

        // Verificar que el usuario esté activo
        if (!user.getIsActive()) {
            log.warn("Solicitud de recuperación de contraseña para usuario inactivo: {}", user.getUsername());
            throw new BusinessException("La cuenta de usuario está desactivada");
        }

        // Generar token de reset
        PasswordResetToken resetToken = passwordResetTokenService.createPasswordResetToken(user);

        log.info("Token de recuperación generado para usuario: {}", user.getUsername());

        // TODO: En producción, enviar email con el token
        // emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());

        // Por ahora, retornar el token (solo para desarrollo/testing)
        return resetToken.getToken();
    }

    /**
     * Resetea la contraseña de un usuario usando un token de reset válido.
     *
     * @param resetPasswordRequest datos de reset (token y nueva contraseña)
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("Solicitando reset de contraseña con token");

        // Validar que las contraseñas coincidan
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new ValidationException("Las contraseñas no coinciden");
        }

        // Verificar token de reset
        PasswordResetToken resetToken = passwordResetTokenService.verifyToken(
                resetPasswordRequest.getResetToken());

        User user = resetToken.getUser();

        // Cambiar contraseña
        user.setPasswordHash(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());

        // Desbloquear usuario si estaba bloqueado por intentos fallidos
        if (user.getIsLocked()) {
            user.setIsLocked(false);
            user.resetFailedLoginAttempts();
            log.info("Usuario desbloqueado tras reset de contraseña: {}", user.getUsername());
        }

        userRepository.save(user);

        // Marcar token como usado
        passwordResetTokenService.markTokenAsUsed(resetToken);

        // Invalidar todos los refresh tokens del usuario (por seguridad)
        refreshTokenService.revokeAllUserTokens(user);

        log.info("Contraseña reseteada exitosamente para usuario: {}", user.getUsername());
    }

    /**
     * Construye la respuesta de autenticación con tokens y datos del usuario.
     */
    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        UserResponse userResponse = UserResponse.builder()
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
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000) // Convertir de ms a segundos
                .user(userResponse)
                .build();
    }
}
