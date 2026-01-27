package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.tire.domain.model.TireSpecification;
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
 * Repositorio para la entidad TireSpecification (Especificaciones Técnicas de Llantas)
 *
 * Proporciona métodos de acceso a datos para las fichas técnicas de llantas.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface TireSpecificationRepository extends JpaRepository<TireSpecification, UUID> {

    /**
     * Busca una especificación por su código
     *
     * @param code código de la especificación
     * @return Optional con la especificación si existe
     */
    Optional<TireSpecification> findByCode(String code);

    /**
     * Verifica si existe una especificación con el código dado
     *
     * @param code código de la especificación
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verifica si existe una especificación con el código dado, excluyendo un ID específico
     *
     * @param code código de la especificación
     * @param id ID a excluir de la búsqueda
     * @return true si existe, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM TireSpecification s " +
           "WHERE s.code = :code AND s.id != :id AND s.deletedAt IS NULL")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") UUID id);

    /**
     * Busca todas las especificaciones activas y no eliminadas
     *
     * @return lista de especificaciones activas
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.isActive = true AND s.deletedAt IS NULL")
    List<TireSpecification> findAllActive();

    /**
     * Busca una especificación activa por su código
     *
     * @param code código de la especificación
     * @return Optional con la especificación si existe y está activa
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.code = :code AND s.isActive = true AND s.deletedAt IS NULL")
    Optional<TireSpecification> findActiveByCode(@Param("code") String code);

    /**
     * Busca especificaciones por marca
     *
     * @param brandId ID de la marca
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.brand.id = :brandId AND s.deletedAt IS NULL")
    Page<TireSpecification> findByBrandId(@Param("brandId") UUID brandId, Pageable pageable);

    /**
     * Busca especificaciones por tipo
     *
     * @param typeId ID del tipo
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.type.id = :typeId AND s.deletedAt IS NULL")
    Page<TireSpecification> findByTypeId(@Param("typeId") UUID typeId, Pageable pageable);

    /**
     * Busca especificaciones por referencia
     *
     * @param referenceId ID de la referencia
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.reference.id = :referenceId AND s.deletedAt IS NULL")
    Page<TireSpecification> findByReferenceId(@Param("referenceId") UUID referenceId, Pageable pageable);

    /**
     * Busca especificaciones por dimensión (búsqueda exacta)
     *
     * @param dimension dimensión de la llanta
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.dimension = :dimension AND s.deletedAt IS NULL")
    Page<TireSpecification> findByDimension(@Param("dimension") String dimension, Pageable pageable);

    /**
     * Busca especificaciones por dimensión (búsqueda parcial)
     *
     * @param dimension dimensión de la llanta (puede ser parcial)
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s WHERE LOWER(s.dimension) LIKE LOWER(CONCAT('%', :dimension, '%')) AND s.deletedAt IS NULL")
    Page<TireSpecification> findByDimensionContaining(@Param("dimension") String dimension, Pageable pageable);

    /**
     * Busca especificaciones por estado activo
     *
     * @param isActive estado activo
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s WHERE s.isActive = :isActive AND s.deletedAt IS NULL")
    Page<TireSpecification> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Búsqueda global por texto (código, dimensión, marca, tipo, referencia)
     *
     * @param searchText texto a buscar
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s " +
           "LEFT JOIN s.brand b " +
           "LEFT JOIN s.type t " +
           "LEFT JOIN s.reference r " +
           "WHERE (LOWER(s.code) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "   OR LOWER(s.dimension) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "   OR LOWER(b.name) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "   OR LOWER(r.name) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "AND s.deletedAt IS NULL")
    Page<TireSpecification> searchByText(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Búsqueda avanzada con múltiples filtros opcionales
     *
     * @param brandId ID de la marca (opcional)
     * @param typeId ID del tipo (opcional)
     * @param referenceId ID de la referencia (opcional)
     * @param isActive estado activo (opcional)
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Query("SELECT s FROM TireSpecification s " +
           "WHERE (:brandId IS NULL OR s.brand.id = :brandId) " +
           "AND (:typeId IS NULL OR s.type.id = :typeId) " +
           "AND (:referenceId IS NULL OR s.reference.id = :referenceId) " +
           "AND (:isActive IS NULL OR s.isActive = :isActive) " +
           "AND s.deletedAt IS NULL")
    Page<TireSpecification> findByFilters(
            @Param("brandId") UUID brandId,
            @Param("typeId") UUID typeId,
            @Param("referenceId") UUID referenceId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    /**
     * Cuenta especificaciones por marca
     *
     * @param brandId ID de la marca
     * @return cantidad de especificaciones
     */
    @Query("SELECT COUNT(s) FROM TireSpecification s WHERE s.brand.id = :brandId AND s.deletedAt IS NULL")
    long countByBrandId(@Param("brandId") UUID brandId);

    /**
     * Cuenta especificaciones por tipo
     *
     * @param typeId ID del tipo
     * @return cantidad de especificaciones
     */
    @Query("SELECT COUNT(s) FROM TireSpecification s WHERE s.type.id = :typeId AND s.deletedAt IS NULL")
    long countByTypeId(@Param("typeId") UUID typeId);

    /**
     * Cuenta especificaciones por referencia
     *
     * @param referenceId ID de la referencia
     * @return cantidad de especificaciones
     */
    @Query("SELECT COUNT(s) FROM TireSpecification s WHERE s.reference.id = :referenceId AND s.deletedAt IS NULL")
    long countByReferenceId(@Param("referenceId") UUID referenceId);

    /**
     * Busca una especificación por ID incluyendo sus relaciones (con FETCH JOIN)
     *
     * @param id ID de la especificación
     * @return Optional con la especificación y sus relaciones cargadas
     */
    @Query("SELECT s FROM TireSpecification s " +
           "LEFT JOIN FETCH s.brand " +
           "LEFT JOIN FETCH s.type " +
           "LEFT JOIN FETCH s.reference " +
           "LEFT JOIN FETCH s.mainProvider " +
           "LEFT JOIN FETCH s.secondaryProvider " +
           "LEFT JOIN FETCH s.lastUsedProvider " +
           "WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<TireSpecification> findByIdWithRelations(@Param("id") UUID id);
}
