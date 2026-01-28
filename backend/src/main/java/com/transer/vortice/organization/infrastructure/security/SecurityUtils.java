package com.transer.vortice.organization.infrastructure.security;

import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utilidad para acceder a información del usuario autenticado actual.
 * Proporciona métodos para obtener el ID del usuario, su oficina y verificar roles.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * Obtiene el nombre de usuario del usuario autenticado actualmente.
     *
     * @return username o null si no hay usuario autenticado
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * Obtiene el usuario autenticado actualmente desde la base de datos.
     *
     * @return User entity o null si no hay usuario autenticado
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }

        return userRepository.findByUsernameOrEmail(username, username)
                .orElse(null);
    }

    /**
     * Obtiene el ID del usuario autenticado actualmente.
     *
     * @return ID del usuario o null si no hay usuario autenticado
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Obtiene el ID de la oficina del usuario autenticado actualmente.
     * Este valor se utiliza para Row-Level Security en PostgreSQL.
     *
     * @return UUID de la oficina o null si no hay usuario autenticado
     */
    public UUID getCurrentUserOfficeId() {
        User user = getCurrentUser();
        return user != null ? user.getOfficeId() : null;
    }

    /**
     * Verifica si el usuario actual tiene el rol especificado.
     *
     * @param roleName nombre del rol (sin prefijo ROLE_)
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String roleWithPrefix = "ROLE_" + roleName;
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(roleWithPrefix));
    }

    /**
     * Verifica si el usuario actual es administrador nacional.
     * Los administradores nacionales tienen acceso a todas las oficinas.
     *
     * @return true si el usuario es administrador nacional
     */
    public boolean isNationalAdmin() {
        return hasRole("ADMIN_NATIONAL");
    }

    /**
     * Verifica si el usuario actual es administrador de oficina.
     *
     * @return true si el usuario es administrador de oficina
     */
    public boolean isOfficeAdmin() {
        return hasRole("ADMIN_OFFICE");
    }

    /**
     * Verifica si el usuario actual es gerente de almacén.
     *
     * @return true si el usuario es gerente de almacén
     */
    public boolean isWarehouseManager() {
        return hasRole("WAREHOUSE_MANAGER");
    }

    /**
     * Verifica si el usuario actual tiene acceso a una oficina específica.
     * Los administradores nacionales tienen acceso a todas las oficinas.
     *
     * @param officeId ID de la oficina a verificar
     * @return true si el usuario tiene acceso a la oficina
     */
    public boolean hasAccessToOffice(UUID officeId) {
        if (officeId == null) {
            return false;
        }

        // Admin nacional tiene acceso a todas las oficinas
        if (isNationalAdmin()) {
            return true;
        }

        // Otros usuarios solo tienen acceso a su propia oficina
        UUID userOfficeId = getCurrentUserOfficeId();
        return officeId.equals(userOfficeId);
    }

    /**
     * Verifica si hay un usuario autenticado.
     *
     * @return true si hay un usuario autenticado
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               !authentication.getPrincipal().equals("anonymousUser");
    }
}
