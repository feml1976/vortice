package com.transer.vortice.organization.infrastructure.security;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para SecurityUtils.
 * Valida la obtención de información del usuario autenticado.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityUtils - Tests de Utilidades de Seguridad")
class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SecurityUtils securityUtils;

    private User user;
    private UUID officeId;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        officeId = UUID.randomUUID();

        // Configurar usuario mock
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setOfficeId(officeId);
        user.setIsActive(true);
        user.setIsLocked(false);

        // Configurar roles
        Role roleUser = new Role();
        roleUser.setName("USER");
        user.setRoles(Set.of(roleUser));

        // Configurar authentication mock
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password",
                authorities
        );

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Obtener username del usuario autenticado - exitoso")
    void getCurrentUsername_Authenticated_ReturnsUsername() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        String username = securityUtils.getCurrentUsername();

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Obtener username sin autenticación - retorna null")
    void getCurrentUsername_NotAuthenticated_ReturnsNull() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        String username = securityUtils.getCurrentUsername();

        // Then
        assertThat(username).isNull();
    }

    @Test
    @DisplayName("Obtener usuario actual - exitoso")
    void getCurrentUser_Authenticated_ReturnsUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        // When
        User currentUser = securityUtils.getCurrentUser();

        // Then
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getUsername()).isEqualTo("testuser");
        assertThat(currentUser.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener ID del usuario actual - exitoso")
    void getCurrentUserId_Authenticated_ReturnsUserId() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        // When
        Long userId = securityUtils.getCurrentUserId();

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener officeId del usuario actual - exitoso")
    void getCurrentUserOfficeId_Authenticated_ReturnsOfficeId() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        // When
        UUID userOfficeId = securityUtils.getCurrentUserOfficeId();

        // Then
        assertThat(userOfficeId).isEqualTo(officeId);
    }

    @Test
    @DisplayName("Verificar si tiene rol - retorna true si tiene el rol")
    void hasRole_WithRole_ReturnsTrue() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean hasRole = securityUtils.hasRole("USER");

        // Then
        assertThat(hasRole).isTrue();
    }

    @Test
    @DisplayName("Verificar si tiene rol - retorna false si no tiene el rol")
    void hasRole_WithoutRole_ReturnsFalse() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean hasRole = securityUtils.hasRole("ADMIN_NATIONAL");

        // Then
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("Verificar si es admin nacional - retorna false para usuario normal")
    void isNationalAdmin_RegularUser_ReturnsFalse() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean isNationalAdmin = securityUtils.isNationalAdmin();

        // Then
        assertThat(isNationalAdmin).isFalse();
    }

    @Test
    @DisplayName("Verificar si es admin nacional - retorna true para admin nacional")
    void isNationalAdmin_AdminUser_ReturnsTrue() {
        // Given
        List<SimpleGrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN_NATIONAL")
        );
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                adminAuthorities
        );
        when(securityContext.getAuthentication()).thenReturn(adminAuth);

        // When
        boolean isNationalAdmin = securityUtils.isNationalAdmin();

        // Then
        assertThat(isNationalAdmin).isTrue();
    }

    @Test
    @DisplayName("Verificar si es admin de oficina")
    void isOfficeAdmin_WithRole_ReturnsTrue() {
        // Given
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN_OFFICE")
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "officeadmin",
                "password",
                authorities
        );
        when(securityContext.getAuthentication()).thenReturn(auth);

        // When
        boolean isOfficeAdmin = securityUtils.isOfficeAdmin();

        // Then
        assertThat(isOfficeAdmin).isTrue();
    }

    @Test
    @DisplayName("Verificar si es gerente de almacén")
    void isWarehouseManager_WithRole_ReturnsTrue() {
        // Given
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_WAREHOUSE_MANAGER")
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "manager",
                "password",
                authorities
        );
        when(securityContext.getAuthentication()).thenReturn(auth);

        // When
        boolean isWarehouseManager = securityUtils.isWarehouseManager();

        // Then
        assertThat(isWarehouseManager).isTrue();
    }

    @Test
    @DisplayName("Tiene acceso a oficina - admin nacional tiene acceso a todas")
    void hasAccessToOffice_NationalAdmin_ReturnsTrue() {
        // Given
        UUID anyOfficeId = UUID.randomUUID();
        List<SimpleGrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN_NATIONAL")
        );
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                adminAuthorities
        );
        when(securityContext.getAuthentication()).thenReturn(adminAuth);

        // When
        boolean hasAccess = securityUtils.hasAccessToOffice(anyOfficeId);

        // Then
        assertThat(hasAccess).isTrue();
    }

    @Test
    @DisplayName("Tiene acceso a oficina - usuario normal solo a su oficina")
    void hasAccessToOffice_RegularUser_OnlyOwnOffice() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        // When - Misma oficina
        boolean hasAccessToOwnOffice = securityUtils.hasAccessToOffice(officeId);

        // Then
        assertThat(hasAccessToOwnOffice).isTrue();

        // When - Otra oficina
        UUID otherOfficeId = UUID.randomUUID();
        boolean hasAccessToOtherOffice = securityUtils.hasAccessToOffice(otherOfficeId);

        // Then
        assertThat(hasAccessToOtherOffice).isFalse();
    }

    @Test
    @DisplayName("Verificar si está autenticado - retorna true si hay autenticación")
    void isAuthenticated_WithAuthentication_ReturnsTrue() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean isAuthenticated = securityUtils.isAuthenticated();

        // Then
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    @DisplayName("Verificar si está autenticado - retorna false sin autenticación")
    void isAuthenticated_WithoutAuthentication_ReturnsFalse() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        boolean isAuthenticated = securityUtils.isAuthenticated();

        // Then
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    @DisplayName("Verificar si está autenticado - retorna false con usuario anónimo")
    void isAuthenticated_AnonymousUser_ReturnsFalse() {
        // Given
        Authentication anonymousAuth = new UsernamePasswordAuthenticationToken(
                "anonymousUser",
                null,
                List.of()
        );
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

        // When
        boolean isAuthenticated = securityUtils.isAuthenticated();

        // Then
        assertThat(isAuthenticated).isFalse();
    }
}
