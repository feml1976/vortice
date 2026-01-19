package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.Permission;
import com.transer.vortice.auth.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para RoleRepository.
 * Usa @DataJpaTest para levantar una BD H2 en memoria.
 *
 * @author Vórtice Development Team
 */
@DataJpaTest
@DisplayName("RoleRepository Tests")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Role systemRole;
    private Role customRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        // Crear permiso de prueba
        testPermission = new Permission("TEST:READ", "test", "read", "Permiso de lectura de prueba");
        testPermission = permissionRepository.save(testPermission);

        // Crear rol del sistema
        systemRole = new Role("ADMIN", "Administrador del sistema", true);
        systemRole = roleRepository.save(systemRole);

        // Crear rol personalizado
        customRole = new Role("CUSTOM_ROLE", "Rol personalizado", false);
        customRole = roleRepository.save(customRole);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Debe encontrar rol por nombre")
    void shouldFindRoleByName() {
        // When
        Optional<Role> found = roleRepository.findByName("ADMIN");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ADMIN");
        assertThat(found.get().getDescription()).isEqualTo("Administrador del sistema");
        assertThat(found.get().getIsSystemRole()).isTrue();
    }

    @Test
    @DisplayName("Debe retornar empty cuando rol no existe")
    void shouldReturnEmptyWhenRoleNotExists() {
        // When
        Optional<Role> found = roleRepository.findByName("NONEXISTENT_ROLE");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar si existe rol por nombre")
    void shouldCheckIfRoleExistsByName() {
        // When
        boolean existsAdmin = roleRepository.existsByName("ADMIN");
        boolean existsCustom = roleRepository.existsByName("CUSTOM_ROLE");
        boolean notExists = roleRepository.existsByName("NONEXISTENT_ROLE");

        // Then
        assertThat(existsAdmin).isTrue();
        assertThat(existsCustom).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debe encontrar todos los roles del sistema")
    void shouldFindSystemRoles() {
        // Given - crear otro rol del sistema
        Role anotherSystemRole = new Role("SUPER_ADMIN", "Super Administrador", true);
        roleRepository.save(anotherSystemRole);
        entityManager.flush();

        // When
        List<Role> systemRoles = roleRepository.findSystemRoles();

        // Then
        assertThat(systemRoles).hasSize(2);
        assertThat(systemRoles)
                .extracting(Role::getName)
                .containsExactlyInAnyOrder("ADMIN", "SUPER_ADMIN");
        assertThat(systemRoles)
                .allMatch(role -> role.getIsSystemRole().equals(true));
    }

    @Test
    @DisplayName("Debe encontrar todos los roles personalizados")
    void shouldFindCustomRoles() {
        // Given - crear otro rol personalizado
        Role anotherCustomRole = new Role("MANAGER", "Gerente", false);
        roleRepository.save(anotherCustomRole);
        entityManager.flush();

        // When
        List<Role> customRoles = roleRepository.findCustomRoles();

        // Then
        assertThat(customRoles).hasSize(2);
        assertThat(customRoles)
                .extracting(Role::getName)
                .containsExactlyInAnyOrder("CUSTOM_ROLE", "MANAGER");
        assertThat(customRoles)
                .allMatch(role -> role.getIsSystemRole().equals(false));
    }

    @Test
    @DisplayName("Debe distinguir entre roles del sistema y personalizados")
    void shouldDistinguishBetweenSystemAndCustomRoles() {
        // When
        List<Role> systemRoles = roleRepository.findSystemRoles();
        List<Role> customRoles = roleRepository.findCustomRoles();

        // Then
        assertThat(systemRoles).hasSize(1);
        assertThat(customRoles).hasSize(1);
        assertThat(systemRoles.get(0).getName()).isEqualTo("ADMIN");
        assertThat(customRoles.get(0).getName()).isEqualTo("CUSTOM_ROLE");

        // Verificar que no hay intersección
        assertThat(systemRoles).doesNotContainAnyElementsOf(customRoles);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay roles del sistema")
    void shouldReturnEmptyListWhenNoSystemRoles() {
        // Given - eliminar todos los roles del sistema
        roleRepository.delete(systemRole);
        entityManager.flush();

        // When
        List<Role> systemRoles = roleRepository.findSystemRoles();

        // Then
        assertThat(systemRoles).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay roles personalizados")
    void shouldReturnEmptyListWhenNoCustomRoles() {
        // Given - eliminar todos los roles personalizados
        roleRepository.delete(customRole);
        entityManager.flush();

        // When
        List<Role> customRoles = roleRepository.findCustomRoles();

        // Then
        assertThat(customRoles).isEmpty();
    }

    @Test
    @DisplayName("Debe agregar permiso a rol")
    void shouldAddPermissionToRole() {
        // Given
        Role role = roleRepository.findByName("ADMIN").orElseThrow();
        assertThat(role.getPermissions()).isEmpty();

        // When
        role.addPermission(testPermission);
        roleRepository.save(role);
        entityManager.flush();
        entityManager.clear();

        // Then
        Role updated = roleRepository.findById(role.getId()).orElseThrow();
        assertThat(updated.getPermissions()).hasSize(1);
        assertThat(updated.getPermissions()).contains(testPermission);
    }

    @Test
    @DisplayName("Debe remover permiso de rol")
    void shouldRemovePermissionFromRole() {
        // Given - agregar permiso primero
        Role role = roleRepository.findByName("ADMIN").orElseThrow();
        role.addPermission(testPermission);
        roleRepository.save(role);
        entityManager.flush();

        // When
        role.removePermission(testPermission);
        roleRepository.save(role);
        entityManager.flush();
        entityManager.clear();

        // Then
        Role updated = roleRepository.findById(role.getId()).orElseThrow();
        assertThat(updated.getPermissions()).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar si rol tiene un permiso específico")
    void shouldCheckIfRoleHasPermission() {
        // Given - agregar permiso
        Role role = roleRepository.findByName("ADMIN").orElseThrow();
        role.addPermission(testPermission);
        roleRepository.save(role);
        entityManager.flush();
        entityManager.clear();

        // When
        Role updated = roleRepository.findById(role.getId()).orElseThrow();

        // Then
        assertThat(updated.hasPermission("TEST_READ")).isTrue();
        assertThat(updated.hasPermission("TEST_WRITE")).isFalse();
    }

    @Test
    @DisplayName("Debe verificar si rol es del sistema")
    void shouldCheckIfRoleIsSystemRole() {
        // When
        Role admin = roleRepository.findByName("ADMIN").orElseThrow();
        Role custom = roleRepository.findByName("CUSTOM_ROLE").orElseThrow();

        // Then
        assertThat(admin.isSystemRole()).isTrue();
        assertThat(custom.isSystemRole()).isFalse();
    }

    @Test
    @DisplayName("Debe agregar múltiples permisos a un rol")
    void shouldAddMultiplePermissionsToRole() {
        // Given
        Permission permission1 = new Permission("TEST:PERM_1", "test", "perm_1", "Permiso 1");
        Permission permission2 = new Permission("TEST:PERM_2", "test", "perm_2", "Permiso 2");
        Permission permission3 = new Permission("TEST:PERM_3", "test", "perm_3", "Permiso 3");
        permissionRepository.save(permission1);
        permissionRepository.save(permission2);
        permissionRepository.save(permission3);

        Role role = roleRepository.findByName("CUSTOM_ROLE").orElseThrow();

        // When
        role.addPermission(permission1);
        role.addPermission(permission2);
        role.addPermission(permission3);
        roleRepository.save(role);
        entityManager.flush();
        entityManager.clear();

        // Then
        Role updated = roleRepository.findById(role.getId()).orElseThrow();
        assertThat(updated.getPermissions()).hasSize(3);
        assertThat(updated.hasPermission("PERM_1")).isTrue();
        assertThat(updated.hasPermission("PERM_2")).isTrue();
        assertThat(updated.hasPermission("PERM_3")).isTrue();
    }

    @Test
    @DisplayName("Debe guardar rol con descripción larga")
    void shouldSaveRoleWithLongDescription() {
        // Given
        String longDescription = "Esta es una descripción muy larga que tiene más de 100 caracteres " +
                "para verificar que el campo TEXT en la base de datos puede almacenar descripciones " +
                "extensas sin problemas. Este test asegura que no hay límites restrictivos en la " +
                "longitud de las descripciones de los roles.";

        Role roleWithLongDesc = new Role(
                "ROLE_WITH_LONG_DESC",
                longDescription,
                false
        );

        // When
        Role saved = roleRepository.save(roleWithLongDesc);
        entityManager.flush();
        entityManager.clear();

        // Then
        Role found = roleRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getDescription()).isEqualTo(longDescription);
        assertThat(found.getDescription().length()).isGreaterThan(100);
    }

    @Test
    @DisplayName("Debe mantener timestamps de auditoría")
    void shouldMaintainAuditTimestamps() {
        // When
        Role role = roleRepository.findByName("ADMIN").orElseThrow();

        // Then
        assertThat(role.getCreatedAt()).isNotNull();
        assertThat(role.getUpdatedAt()).isNotNull();
        assertThat(role.getCreatedAt()).isBeforeOrEqualTo(role.getUpdatedAt());
    }
}
