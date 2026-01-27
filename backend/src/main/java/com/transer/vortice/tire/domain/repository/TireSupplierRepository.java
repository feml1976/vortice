package com.transer.vortice.tire.domain.repository;

import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad TireSupplier (Proveedores de llantas)
 *
 * @author Vórtice Development Team
 */
@Repository
public interface TireSupplierRepository extends JpaRepository<TireSupplier, UUID> {

    /**
     * Busca un proveedor por su código
     *
     * @param code código del proveedor
     * @return Optional con el proveedor si existe
     */
    Optional<TireSupplier> findByCode(String code);

    /**
     * Busca un proveedor por su NIT/RUC
     *
     * @param taxId NIT/RUC del proveedor
     * @return Optional con el proveedor si existe
     */
    Optional<TireSupplier> findByTaxId(String taxId);

    /**
     * Verifica si existe un proveedor con el código dado
     *
     * @param code código del proveedor
     * @return true si existe, false en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Verifica si existe un proveedor con el NIT/RUC dado
     *
     * @param taxId NIT/RUC del proveedor
     * @return true si existe, false en caso contrario
     */
    boolean existsByTaxId(String taxId);

    /**
     * Busca todos los proveedores activos y no eliminados
     *
     * @return lista de proveedores activos
     */
    @Query("SELECT s FROM TireSupplier s WHERE s.isActive = true AND s.deletedAt IS NULL")
    List<TireSupplier> findAllActive();

    /**
     * Busca un proveedor activo por su código
     *
     * @param code código del proveedor
     * @return Optional con el proveedor si existe y está activo
     */
    @Query("SELECT s FROM TireSupplier s WHERE s.code = :code AND s.isActive = true AND s.deletedAt IS NULL")
    Optional<TireSupplier> findActiveByCode(String code);
}
