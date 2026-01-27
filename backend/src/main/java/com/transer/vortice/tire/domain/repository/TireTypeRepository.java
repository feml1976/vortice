package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.tire.domain.model.catalog.TireType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad TireType (Tipos de llantas)
 *
 * @author Vórtice Development Team
 */
@Repository
public interface TireTypeRepository extends JpaRepository<TireType, UUID> {

    /**
     * Busca un tipo por su código
     *
     * @param code código del tipo
     * @return Optional con el tipo si existe
     */
    Optional<TireType> findByCode(String code);

    /**
     * Verifica si existe un tipo con el código dado
     *
     * @param code código del tipo
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Busca todos los tipos activos y no eliminados
     *
     * @return lista de tipos activos
     */
    @Query("SELECT t FROM TireType t WHERE t.isActive = true AND t.deletedAt IS NULL")
    List<TireType> findAllActive();

    /**
     * Busca un tipo activo por su código
     *
     * @param code código del tipo
     * @return Optional con el tipo si existe y está activo
     */
    @Query("SELECT t FROM TireType t WHERE t.code = :code AND t.isActive = true AND t.deletedAt IS NULL")
    Optional<TireType> findActiveByCode(String code);
}
