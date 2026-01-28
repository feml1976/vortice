package com.transer.vortice.organization.domain.repository;

import com.transer.vortice.organization.domain.model.Warehouse;
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
 * Repositorio para la entidad Warehouse (Almacén).
 * <p>
 * IMPORTANTE: Los queries están sujetos a Row-Level Security (RLS) en PostgreSQL.
 * Los usuarios solo verán almacenes de su oficina automáticamente.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    /**
     * Busca un almacén por su ID (solo si no está eliminado)
     * RLS filtrará automáticamente por oficina del usuario
     *
     * @param id ID del almacén
     * @return Warehouse si existe y no está eliminado
     */
    Optional<Warehouse> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca un almacén por su código y oficina
     *
     * @param code Código del almacén
     * @param officeId ID de la oficina
     * @return Warehouse si existe
     */
    Optional<Warehouse> findByCodeAndOfficeIdAndDeletedAtIsNull(String code, UUID officeId);

    /**
     * Verifica si existe un almacén con el código especificado en una oficina
     *
     * @param code Código a verificar
     * @param officeId ID de la oficina
     * @return true si existe un almacén con ese código en la oficina
     */
    boolean existsByCodeAndOfficeIdAndDeletedAtIsNull(String code, UUID officeId);

    /**
     * Busca todos los almacenes activos de una oficina
     *
     * @param officeId ID de la oficina
     * @param pageable Paginación
     * @return Página de almacenes
     */
    @Query("SELECT w FROM Warehouse w WHERE w.officeId = :officeId AND w.deletedAt IS NULL ORDER BY w.name")
    Page<Warehouse> findByOfficeId(@Param("officeId") UUID officeId, Pageable pageable);

    /**
     * Busca todos los almacenes activos de una oficina sin paginación
     *
     * @param officeId ID de la oficina
     * @return Lista de almacenes
     */
    @Query("SELECT w FROM Warehouse w WHERE w.officeId = :officeId AND w.deletedAt IS NULL AND w.isActive = true ORDER BY w.name")
    List<Warehouse> findActiveByOfficeId(@Param("officeId") UUID officeId);

    /**
     * Busca todos los almacenes activos sin paginación (filtrados por RLS)
     *
     * @return Lista de almacenes
     */
    @Query("SELECT w FROM Warehouse w WHERE w.deletedAt IS NULL AND w.isActive = true ORDER BY w.name")
    List<Warehouse> findAllActive();

    /**
     * Busca todos los almacenes activos con paginación (filtrados por RLS)
     *
     * @param pageable Paginación
     * @return Página de almacenes
     */
    @Query("SELECT w FROM Warehouse w WHERE w.deletedAt IS NULL ORDER BY w.name")
    Page<Warehouse> findAllActive(Pageable pageable);

    /**
     * Busca almacenes por nombre (búsqueda parcial, insensible a mayúsculas)
     *
     * @param name Nombre a buscar
     * @param pageable Paginación
     * @return Página de almacenes que coinciden
     */
    @Query("SELECT w FROM Warehouse w WHERE w.deletedAt IS NULL AND LOWER(w.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Warehouse> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Verifica si existe un almacén con el código especificado en una oficina (excluyendo un almacén específico)
     *
     * @param code Código a verificar
     * @param officeId ID de la oficina
     * @param id ID del almacén a excluir (para updates)
     * @return true si existe otro almacén con ese código en la oficina
     */
    @Query("SELECT COUNT(w) > 0 FROM Warehouse w WHERE w.code = :code AND w.officeId = :officeId AND w.id != :id AND w.deletedAt IS NULL")
    boolean existsByCodeAndOfficeIdAndIdNot(@Param("code") String code, @Param("officeId") UUID officeId, @Param("id") UUID id);

    /**
     * Cuenta almacenes activos de una oficina
     *
     * @param officeId ID de la oficina
     * @return Número de almacenes activos
     */
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.officeId = :officeId AND w.deletedAt IS NULL AND w.isActive = true")
    long countActiveByOfficeId(@Param("officeId") UUID officeId);

    /**
     * Cuenta todos los almacenes de una oficina (incluyendo inactivos)
     *
     * @param officeId ID de la oficina
     * @return Número de almacenes
     */
    long countByOfficeIdAndDeletedAtIsNull(UUID officeId);

    /**
     * Busca almacenes por nombre o código con paginación
     * RLS filtrará automáticamente por oficina del usuario
     *
     * @param searchTerm Término de búsqueda
     * @param pageable Paginación
     * @return Página de almacenes que coinciden
     */
    @Query("SELECT w FROM Warehouse w WHERE w.deletedAt IS NULL AND " +
           "(LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Warehouse> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}
