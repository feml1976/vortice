package com.transer.vortice.organization.domain.repository;

import com.transer.vortice.organization.domain.model.WarehouseLocation;
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
 * Repositorio para la entidad WarehouseLocation (Ubicación).
 * <p>
 * IMPORTANTE: Los queries están sujetos a Row-Level Security (RLS) en PostgreSQL.
 * Los usuarios solo verán ubicaciones de almacenes de su oficina automáticamente.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, UUID> {

    /**
     * Busca una ubicación por su ID (solo si no está eliminada)
     * RLS filtrará automáticamente por oficina del usuario
     *
     * @param id ID de la ubicación
     * @return WarehouseLocation si existe y no está eliminada
     */
    Optional<WarehouseLocation> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca una ubicación por su código y almacén
     *
     * @param code Código de la ubicación
     * @param warehouseId ID del almacén
     * @return WarehouseLocation si existe
     */
    Optional<WarehouseLocation> findByCodeAndWarehouseIdAndDeletedAtIsNull(String code, UUID warehouseId);

    /**
     * Verifica si existe una ubicación con el código especificado en un almacén
     *
     * @param code Código a verificar
     * @param warehouseId ID del almacén
     * @return true si existe una ubicación con ese código en el almacén
     */
    boolean existsByCodeAndWarehouseIdAndDeletedAtIsNull(String code, UUID warehouseId);

    /**
     * Busca todas las ubicaciones activas de un almacén
     *
     * @param warehouseId ID del almacén
     * @param pageable Paginación
     * @return Página de ubicaciones
     */
    @Query("SELECT wl FROM WarehouseLocation wl WHERE wl.warehouseId = :warehouseId AND wl.deletedAt IS NULL ORDER BY wl.code")
    Page<WarehouseLocation> findByWarehouseId(@Param("warehouseId") UUID warehouseId, Pageable pageable);

    /**
     * Busca todas las ubicaciones activas de un almacén sin paginación
     *
     * @param warehouseId ID del almacén
     * @return Lista de ubicaciones
     */
    @Query("SELECT wl FROM WarehouseLocation wl WHERE wl.warehouseId = :warehouseId AND wl.deletedAt IS NULL AND wl.isActive = true ORDER BY wl.code")
    List<WarehouseLocation> findActiveByWarehouseId(@Param("warehouseId") UUID warehouseId);

    /**
     * Busca todas las ubicaciones activas sin paginación (filtradas por RLS)
     *
     * @return Lista de ubicaciones
     */
    @Query("SELECT wl FROM WarehouseLocation wl WHERE wl.deletedAt IS NULL AND wl.isActive = true ORDER BY wl.code")
    List<WarehouseLocation> findAllActive();

    /**
     * Busca todas las ubicaciones activas con paginación (filtradas por RLS)
     *
     * @param pageable Paginación
     * @return Página de ubicaciones
     */
    @Query("SELECT wl FROM WarehouseLocation wl WHERE wl.deletedAt IS NULL ORDER BY wl.code")
    Page<WarehouseLocation> findAllActive(Pageable pageable);

    /**
     * Busca ubicaciones por código o nombre (búsqueda parcial, insensible a mayúsculas)
     *
     * @param search Texto a buscar
     * @param pageable Paginación
     * @return Página de ubicaciones que coinciden
     */
    @Query("SELECT wl FROM WarehouseLocation wl WHERE wl.deletedAt IS NULL AND " +
           "(LOWER(wl.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(wl.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<WarehouseLocation> search(@Param("search") String search, Pageable pageable);

    /**
     * Verifica si existe una ubicación con el código especificado en un almacén (excluyendo una ubicación específica)
     *
     * @param code Código a verificar
     * @param warehouseId ID del almacén
     * @param id ID de la ubicación a excluir (para updates)
     * @return true si existe otra ubicación con ese código en el almacén
     */
    @Query("SELECT COUNT(wl) > 0 FROM WarehouseLocation wl WHERE wl.code = :code AND wl.warehouseId = :warehouseId AND wl.id != :id AND wl.deletedAt IS NULL")
    boolean existsByCodeAndWarehouseIdAndIdNot(@Param("code") String code, @Param("warehouseId") UUID warehouseId, @Param("id") UUID id);

    /**
     * Cuenta ubicaciones activas de un almacén
     *
     * @param warehouseId ID del almacén
     * @return Número de ubicaciones activas
     */
    @Query("SELECT COUNT(wl) FROM WarehouseLocation wl WHERE wl.warehouseId = :warehouseId AND wl.deletedAt IS NULL AND wl.isActive = true")
    long countActiveByWarehouseId(@Param("warehouseId") UUID warehouseId);

    /**
     * Cuenta todas las ubicaciones de un almacén (incluyendo inactivas)
     *
     * @param warehouseId ID del almacén
     * @return Número de ubicaciones
     */
    long countByWarehouseIdAndDeletedAtIsNull(UUID warehouseId);
}
