package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Permission.
 * Proporciona métodos de acceso a datos para permisos.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Busca un permiso por su nombre.
     *
     * @param name nombre del permiso
     * @return Optional con el permiso si existe
     */
    Optional<Permission> findByName(String name);

    /**
     * Busca un permiso por recurso y acción.
     *
     * @param resource recurso del permiso
     * @param action acción del permiso
     * @return Optional con el permiso si existe
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Busca todos los permisos de un recurso específico.
     *
     * @param resource nombre del recurso
     * @return lista de permisos del recurso
     */
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource")
    List<Permission> findByResource(@Param("resource") String resource);

    /**
     * Busca todos los permisos con una acción específica.
     *
     * @param action acción del permiso
     * @return lista de permisos con la acción
     */
    @Query("SELECT p FROM Permission p WHERE p.action = :action")
    List<Permission> findByAction(@Param("action") String action);

    /**
     * Verifica si existe un permiso con el nombre dado.
     *
     * @param name nombre del permiso
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Verifica si existe un permiso con el recurso y acción dados.
     *
     * @param resource recurso del permiso
     * @param action acción del permiso
     * @return true si existe, false en caso contrario
     */
    boolean existsByResourceAndAction(String resource, String action);
}
