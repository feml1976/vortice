package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Role.
 * Proporciona métodos de acceso a datos para roles.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param name nombre del rol
     * @return Optional con el rol si existe
     */
    Optional<Role> findByName(String name);

    /**
     * Verifica si existe un rol con el nombre dado.
     *
     * @param name nombre del rol
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Busca todos los roles del sistema.
     *
     * @return lista de roles del sistema
     */
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = true")
    List<Role> findSystemRoles();

    /**
     * Busca todos los roles que no son del sistema.
     *
     * @return lista de roles personalizados
     */
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = false")
    List<Role> findCustomRoles();
}
