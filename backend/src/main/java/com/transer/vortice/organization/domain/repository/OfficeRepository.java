package com.transer.vortice.organization.domain.repository;

import com.transer.vortice.organization.domain.model.Office;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad Office (Oficina/Sede).
 * Proporciona métodos para consultar oficinas activas, buscar por código, etc.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    /**
     * Busca una oficina por su ID (solo si no está eliminada)
     *
     * @param id ID de la oficina
     * @return Office si existe y no está eliminada
     */
    Optional<Office> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca una oficina por su código (único globalmente)
     *
     * @param code Código de la oficina
     * @return Office si existe
     */
    Optional<Office> findByCodeAndDeletedAtIsNull(String code);

    /**
     * Verifica si existe una oficina con el código especificado
     *
     * @param code Código a verificar
     * @return true si existe una oficina con ese código
     */
    boolean existsByCodeAndDeletedAtIsNull(String code);

    /**
     * Busca todas las oficinas activas (no eliminadas)
     *
     * @param pageable Paginación
     * @return Página de oficinas activas
     */
    @Query("SELECT o FROM Office o WHERE o.deletedAt IS NULL ORDER BY o.name")
    Page<Office> findAllActive(Pageable pageable);

    /**
     * Busca todas las oficinas activas sin paginación
     *
     * @return Lista de oficinas activas
     */
    @Query("SELECT o FROM Office o WHERE o.deletedAt IS NULL AND o.isActive = true ORDER BY o.name")
    List<Office> findAllActive();

    /**
     * Busca oficinas por nombre o ciudad con paginación
     *
     * @param searchTerm Término de búsqueda
     * @param pageable Paginación
     * @return Página de oficinas que coinciden
     */
    @Query("SELECT o FROM Office o WHERE o.deletedAt IS NULL AND " +
           "(LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.city) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Office> searchByNameOrCity(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Busca oficinas por ciudad
     *
     * @param city Ciudad
     * @param pageable Paginación
     * @return Página de oficinas en la ciudad
     */
    Page<Office> findByCityAndDeletedAtIsNullOrderByName(String city, Pageable pageable);

    /**
     * Busca oficinas activas por nombre (búsqueda parcial, insensible a mayúsculas)
     *
     * @param name Nombre a buscar
     * @param pageable Paginación
     * @return Página de oficinas que coinciden
     */
    @Query("SELECT o FROM Office o WHERE o.deletedAt IS NULL AND LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Office> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Verifica si existe una oficina con el código especificado (excluyendo una oficina específica)
     *
     * @param code Código a verificar
     * @param id ID de la oficina a excluir (para updates)
     * @return true si existe otra oficina con ese código
     */
    @Query("SELECT COUNT(o) > 0 FROM Office o WHERE o.code = :code AND o.id != :id AND o.deletedAt IS NULL")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") UUID id);

    /**
     * Cuenta oficinas activas
     *
     * @return Número de oficinas activas
     */
    @Query("SELECT COUNT(o) FROM Office o WHERE o.deletedAt IS NULL AND o.isActive = true")
    long countActive();
}
