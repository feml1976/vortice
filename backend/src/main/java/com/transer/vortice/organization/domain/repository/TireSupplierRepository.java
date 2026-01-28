package com.transer.vortice.organization.domain.repository;

import com.transer.vortice.organization.domain.model.TireSupplier;
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
 * Repositorio para la entidad TireSupplier (Proveedor de Llantas).
 * <p>
 * IMPORTANTE: Los queries están sujetos a Row-Level Security (RLS) en PostgreSQL.
 * Los usuarios solo verán proveedores de su oficina automáticamente.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface TireSupplierRepository extends JpaRepository<TireSupplier, UUID> {

    /**
     * Busca un proveedor por su ID (solo si no está eliminado)
     * RLS filtrará automáticamente por oficina del usuario
     *
     * @param id ID del proveedor
     * @return TireSupplier si existe y no está eliminado
     */
    Optional<TireSupplier> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca un proveedor por su código y oficina
     *
     * @param code Código del proveedor
     * @param officeId ID de la oficina
     * @return TireSupplier si existe
     */
    Optional<TireSupplier> findByCodeAndOfficeIdAndDeletedAtIsNull(String code, UUID officeId);

    /**
     * Verifica si existe un proveedor con el código especificado en una oficina
     *
     * @param code Código a verificar
     * @param officeId ID de la oficina
     * @return true si existe un proveedor con ese código en la oficina
     */
    boolean existsByCodeAndOfficeIdAndDeletedAtIsNull(String code, UUID officeId);

    /**
     * Busca un proveedor por su NIT
     *
     * @param taxId NIT del proveedor
     * @return TireSupplier si existe
     */
    Optional<TireSupplier> findByTaxIdAndDeletedAtIsNull(String taxId);

    /**
     * Busca todos los proveedores con el mismo NIT (pueden estar en diferentes oficinas)
     *
     * @param taxId NIT del proveedor
     * @return Lista de proveedores con ese NIT
     */
    List<TireSupplier> findAllByTaxIdAndDeletedAtIsNull(String taxId);

    /**
     * Busca todos los proveedores activos de una oficina
     *
     * @param officeId ID de la oficina
     * @param pageable Paginación
     * @return Página de proveedores
     */
    @Query("SELECT ts FROM TireSupplier ts WHERE ts.officeId = :officeId AND ts.deletedAt IS NULL ORDER BY ts.name")
    Page<TireSupplier> findByOfficeId(@Param("officeId") UUID officeId, Pageable pageable);

    /**
     * Busca todos los proveedores activos de una oficina sin paginación
     *
     * @param officeId ID de la oficina
     * @return Lista de proveedores
     */
    @Query("SELECT ts FROM TireSupplier ts WHERE ts.officeId = :officeId AND ts.deletedAt IS NULL AND ts.isActive = true ORDER BY ts.name")
    List<TireSupplier> findActiveByOfficeId(@Param("officeId") UUID officeId);

    /**
     * Busca todos los proveedores activos sin paginación (filtrados por RLS)
     *
     * @return Lista de proveedores
     */
    @Query("SELECT ts FROM TireSupplier ts WHERE ts.deletedAt IS NULL AND ts.isActive = true ORDER BY ts.name")
    List<TireSupplier> findAllActive();

    /**
     * Busca todos los proveedores activos con paginación (filtrados por RLS)
     *
     * @param pageable Paginación
     * @return Página de proveedores
     */
    @Query("SELECT ts FROM TireSupplier ts WHERE ts.deletedAt IS NULL ORDER BY ts.name")
    Page<TireSupplier> findAllActive(Pageable pageable);

    /**
     * Busca proveedores por nombre o código (búsqueda parcial, insensible a mayúsculas)
     *
     * @param search Texto a buscar
     * @param pageable Paginación
     * @return Página de proveedores que coinciden
     */
    @Query("SELECT ts FROM TireSupplier ts WHERE ts.deletedAt IS NULL AND " +
           "(LOWER(ts.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(ts.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<TireSupplier> search(@Param("search") String search, Pageable pageable);

    /**
     * Verifica si existe un proveedor con el código especificado en una oficina (excluyendo un proveedor específico)
     *
     * @param code Código a verificar
     * @param officeId ID de la oficina
     * @param id ID del proveedor a excluir (para updates)
     * @return true si existe otro proveedor con ese código en la oficina
     */
    @Query("SELECT COUNT(ts) > 0 FROM TireSupplier ts WHERE ts.code = :code AND ts.officeId = :officeId AND ts.id != :id AND ts.deletedAt IS NULL")
    boolean existsByCodeAndOfficeIdAndIdNot(@Param("code") String code, @Param("officeId") UUID officeId, @Param("id") UUID id);

    /**
     * Cuenta proveedores activos de una oficina
     *
     * @param officeId ID de la oficina
     * @return Número de proveedores activos
     */
    @Query("SELECT COUNT(ts) FROM TireSupplier ts WHERE ts.officeId = :officeId AND ts.deletedAt IS NULL AND ts.isActive = true")
    long countActiveByOfficeId(@Param("officeId") UUID officeId);

    /**
     * Cuenta todos los proveedores de una oficina (incluyendo inactivos)
     *
     * @param officeId ID de la oficina
     * @return Número de proveedores
     */
    long countByOfficeIdAndDeletedAtIsNull(UUID officeId);
}
