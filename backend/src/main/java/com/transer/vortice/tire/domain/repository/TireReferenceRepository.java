package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.tire.domain.model.catalog.TireReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad TireReference (Referencias de llantas)
 *
 * @author Vórtice Development Team
 */
@Repository
public interface TireReferenceRepository extends JpaRepository<TireReference, UUID> {

    /**
     * Busca una referencia por su código
     *
     * @param code código de la referencia
     * @return Optional con la referencia si existe
     */
    Optional<TireReference> findByCode(String code);

    /**
     * Verifica si existe una referencia con el código dado
     *
     * @param code código de la referencia
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Busca todas las referencias activas y no eliminadas
     *
     * @return lista de referencias activas
     */
    @Query("SELECT r FROM TireReference r WHERE r.isActive = true AND r.deletedAt IS NULL")
    List<TireReference> findAllActive();

    /**
     * Busca una referencia activa por su código
     *
     * @param code código de la referencia
     * @return Optional con la referencia si existe y está activa
     */
    @Query("SELECT r FROM TireReference r WHERE r.code = :code AND r.isActive = true AND r.deletedAt IS NULL")
    Optional<TireReference> findActiveByCode(String code);
}
