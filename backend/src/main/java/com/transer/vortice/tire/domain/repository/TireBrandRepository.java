package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad TireBrand (Marcas de llantas)
 *
 * @author Vórtice Development Team
 */
@Repository
public interface TireBrandRepository extends JpaRepository<TireBrand, UUID> {

    /**
     * Busca una marca por su código
     *
     * @param code código de la marca
     * @return Optional con la marca si existe
     */
    Optional<TireBrand> findByCode(String code);

    /**
     * Verifica si existe una marca con el código dado
     *
     * @param code código de la marca
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Busca todas las marcas activas y no eliminadas
     *
     * @return lista de marcas activas
     */
    @Query("SELECT b FROM TireBrand b WHERE b.isActive = true AND b.deletedAt IS NULL")
    List<TireBrand> findAllActive();

    /**
     * Busca una marca activa por su código
     *
     * @param code código de la marca
     * @return Optional con la marca si existe y está activa
     */
    @Query("SELECT b FROM TireBrand b WHERE b.code = :code AND b.isActive = true AND b.deletedAt IS NULL")
    Optional<TireBrand> findActiveByCode(String code);
}
