package com.transer.vortice.organization.application.service;

import com.transer.vortice.organization.application.dto.CreateTireSupplierRequest;
import com.transer.vortice.organization.application.dto.TireSupplierResponse;
import com.transer.vortice.organization.application.dto.UpdateTireSupplierRequest;
import com.transer.vortice.organization.application.mapper.TireSupplierMapper;
import com.transer.vortice.organization.domain.exception.DuplicateCodeException;
import com.transer.vortice.organization.domain.exception.EntityInUseException;
import com.transer.vortice.organization.domain.exception.ForbiddenOfficeAccessException;
import com.transer.vortice.organization.domain.exception.OfficeNotFoundException;
import com.transer.vortice.organization.domain.exception.TireSupplierNotFoundException;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.TireSupplier;
import com.transer.vortice.organization.domain.repository.OfficeRepository;
import com.transer.vortice.organization.domain.repository.TireSupplierRepository;
import com.transer.vortice.organization.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestión de proveedores de llantas.
 * Implementa los casos de uso relacionados con proveedores de llantas.
 *
 * SEGURIDAD: Este servicio está sujeto a Row-Level Security (RLS).
 * Los usuarios solo pueden ver proveedores de su oficina, excepto administradores nacionales.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TireSupplierService {

    private final TireSupplierRepository tireSupplierRepository;
    private final OfficeRepository officeRepository;
    private final TireSupplierMapper tireSupplierMapper;
    private final SecurityUtils securityUtils;

    /**
     * Crea un nuevo proveedor de llantas.
     * El proveedor debe pertenecer a una oficina válida.
     * Los usuarios solo pueden crear proveedores en su propia oficina (excepto admin nacional).
     *
     * @param request datos del proveedor a crear
     * @return DTO con los datos del proveedor creado
     * @throws OfficeNotFoundException si la oficina no existe
     * @throws ForbiddenOfficeAccessException si el usuario no tiene acceso a la oficina
     * @throws DuplicateCodeException si ya existe un proveedor con ese código en la oficina
     */
    @Transactional
    public TireSupplierResponse createTireSupplier(CreateTireSupplierRequest request) {
        log.info("Creando nuevo proveedor con código: {} en oficina: {}",
                request.getCode(), request.getOfficeId());

        // Validar que el usuario tenga acceso a la oficina
        if (!securityUtils.hasAccessToOffice(request.getOfficeId())) {
            log.warn("Usuario sin acceso intenta crear proveedor en oficina: {}", request.getOfficeId());
            throw new ForbiddenOfficeAccessException(request.getOfficeId());
        }

        // Validar que la oficina exista
        Office office = officeRepository.findByIdAndDeletedAtIsNull(request.getOfficeId())
                .orElseThrow(() -> {
                    log.warn("Oficina no encontrada: {}", request.getOfficeId());
                    return new OfficeNotFoundException(request.getOfficeId());
                });

        // Validar que no exista otro proveedor con el mismo código en la misma oficina
        if (tireSupplierRepository.existsByCodeAndOfficeIdAndDeletedAtIsNull(
                request.getCode(), request.getOfficeId())) {
            log.warn("Ya existe proveedor con código {} en oficina {}",
                    request.getCode(), request.getOfficeId());
            throw new DuplicateCodeException("Proveedor", request.getCode(),
                    "la oficina " + office.getName());
        }

        // Validar que no exista otro proveedor con el mismo NIT (global)
        tireSupplierRepository.findByTaxIdAndDeletedAtIsNull(request.getTaxId())
                .ifPresent(existingSupplier -> {
                    // Si existe el NIT pero en otra oficina, es válido (mismo proveedor, diferentes oficinas)
                    if (existingSupplier.getOfficeId().equals(request.getOfficeId())) {
                        log.warn("Ya existe proveedor con NIT {} en la misma oficina", request.getTaxId());
                        throw new DuplicateCodeException("Proveedor", request.getTaxId());
                    }
                    log.info("Proveedor con NIT {} ya existe en otra oficina, se permite crear en oficina {}",
                            request.getTaxId(), request.getOfficeId());
                });

        // Convertir DTO a entidad
        TireSupplier supplier = tireSupplierMapper.toEntity(request);

        // Guardar
        TireSupplier savedSupplier = tireSupplierRepository.save(supplier);
        log.info("Proveedor creado exitosamente con ID: {}", savedSupplier.getId());

        return tireSupplierMapper.toResponseWithOffice(savedSupplier, office);
    }

    /**
     * Actualiza un proveedor existente.
     * No se puede cambiar el código ni la oficina del proveedor.
     *
     * @param supplierId ID del proveedor a actualizar
     * @param request datos actualizados
     * @return DTO con los datos del proveedor actualizado
     * @throws TireSupplierNotFoundException si el proveedor no existe o no es accesible
     */
    @Transactional
    public TireSupplierResponse updateTireSupplier(UUID supplierId, UpdateTireSupplierRequest request) {
        log.info("Actualizando proveedor con ID: {}", supplierId);

        // Buscar proveedor (RLS filtrará automáticamente por oficina del usuario)
        TireSupplier supplier = tireSupplierRepository.findByIdAndDeletedAtIsNull(supplierId)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado con ID: {}", supplierId);
                    return new TireSupplierNotFoundException(supplierId);
                });

        // Validar que no exista otro proveedor con el mismo NIT (excepto el actual)
        tireSupplierRepository.findByTaxIdAndDeletedAtIsNull(request.getTaxId())
                .ifPresent(existingSupplier -> {
                    if (!existingSupplier.getId().equals(supplierId) &&
                        existingSupplier.getOfficeId().equals(supplier.getOfficeId())) {
                        log.warn("Ya existe otro proveedor con NIT {} en la misma oficina", request.getTaxId());
                        throw new DuplicateCodeException("Proveedor", request.getTaxId());
                    }
                });

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Actualizar información
        supplier.updateInfo(
            request.getName(),
            request.getTaxId(),
            request.getContactName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            currentUserId
        );

        // Guardar cambios
        TireSupplier updatedSupplier = tireSupplierRepository.save(supplier);
        log.info("Proveedor actualizado exitosamente: {}", supplierId);

        // Cargar oficina para respuesta completa
        Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);

        return tireSupplierMapper.toResponseWithOffice(updatedSupplier, office);
    }

    /**
     * Elimina (soft delete) un proveedor.
     * Solo se puede eliminar si no tiene compras o llantas asociadas.
     *
     * @param supplierId ID del proveedor a eliminar
     * @throws TireSupplierNotFoundException si el proveedor no existe
     * @throws EntityInUseException si el proveedor tiene compras o llantas activas
     */
    @Transactional
    public void deleteTireSupplier(UUID supplierId) {
        log.info("Eliminando proveedor con ID: {}", supplierId);

        // Buscar proveedor (RLS filtrará automáticamente)
        TireSupplier supplier = tireSupplierRepository.findByIdAndDeletedAtIsNull(supplierId)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado con ID: {}", supplierId);
                    return new TireSupplierNotFoundException(supplierId);
                });

        // TODO: Validar que no tenga compras activas cuando se implemente el módulo de compras
        // long activePurchases = purchaseRepository.countBySupplierIdAndDeletedAtIsNull(supplierId);
        // if (activePurchases > 0) {
        //     throw new EntityInUseException("Proveedor", supplier.getCode(), "compras", activePurchases);
        // }

        // TODO: Validar que no tenga llantas activas cuando se implemente el módulo de llantas
        // long activeTires = tireRepository.countBySupplierIdAndDeletedAtIsNull(supplierId);
        // if (activeTires > 0) {
        //     throw new EntityInUseException("Proveedor", supplier.getCode(), "llantas", activeTires);
        // }

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Realizar soft delete
        supplier.markAsDeleted(currentUserId);
        tireSupplierRepository.save(supplier);

        log.info("Proveedor eliminado exitosamente: {}", supplierId);
    }

    /**
     * Obtiene un proveedor por su ID.
     *
     * @param supplierId ID del proveedor
     * @return DTO con los datos del proveedor
     * @throws TireSupplierNotFoundException si el proveedor no existe o no es accesible
     */
    @Transactional(readOnly = true)
    public TireSupplierResponse getSupplierById(UUID supplierId) {
        log.debug("Obteniendo proveedor con ID: {}", supplierId);

        TireSupplier supplier = tireSupplierRepository.findByIdAndDeletedAtIsNull(supplierId)
                .orElseThrow(() -> new TireSupplierNotFoundException(supplierId));

        Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);

        return tireSupplierMapper.toResponseWithOffice(supplier, office);
    }

    /**
     * Obtiene un proveedor por su código y oficina.
     *
     * @param code código del proveedor
     * @param officeId ID de la oficina
     * @return DTO con los datos del proveedor
     * @throws TireSupplierNotFoundException si el proveedor no existe
     * @throws ForbiddenOfficeAccessException si el usuario no tiene acceso a la oficina
     */
    @Transactional(readOnly = true)
    public TireSupplierResponse getSupplierByCode(String code, UUID officeId) {
        log.debug("Obteniendo proveedor con código: {} en oficina: {}", code, officeId);

        // Validar acceso a la oficina
        if (!securityUtils.hasAccessToOffice(officeId)) {
            throw new ForbiddenOfficeAccessException(officeId);
        }

        TireSupplier supplier = tireSupplierRepository.findByCodeAndOfficeIdAndDeletedAtIsNull(code, officeId)
                .orElseThrow(() -> new TireSupplierNotFoundException(code, officeId));

        Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);

        return tireSupplierMapper.toResponseWithOffice(supplier, office);
    }

    /**
     * Obtiene un proveedor por su NIT.
     * Puede haber múltiples proveedores con el mismo NIT en diferentes oficinas.
     *
     * @param taxId NIT del proveedor
     * @return lista de DTOs con los proveedores encontrados
     */
    @Transactional(readOnly = true)
    public List<TireSupplierResponse> getSuppliersByTaxId(String taxId) {
        log.debug("Obteniendo proveedores con NIT: {}", taxId);

        List<TireSupplier> suppliers = tireSupplierRepository.findAllByTaxIdAndDeletedAtIsNull(taxId);

        return suppliers.stream()
                .map(supplier -> {
                    Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);
                    return tireSupplierMapper.toResponseWithOffice(supplier, office);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los proveedores accesibles por el usuario actual.
     * Los administradores nacionales ven todos los proveedores.
     * Otros usuarios solo ven proveedores de su oficina (filtrado por RLS).
     *
     * @return lista de DTOs con todos los proveedores
     */
    @Transactional(readOnly = true)
    public List<TireSupplierResponse> listAllSuppliers() {
        log.debug("Listando todos los proveedores");

        List<TireSupplier> suppliers = tireSupplierRepository.findAllActive();

        return suppliers.stream()
                .map(supplier -> {
                    Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);
                    return tireSupplierMapper.toResponseWithOffice(supplier, office);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los proveedores de una oficina específica.
     *
     * @param officeId ID de la oficina
     * @return lista de DTOs con los proveedores de la oficina
     * @throws ForbiddenOfficeAccessException si el usuario no tiene acceso a la oficina
     */
    @Transactional(readOnly = true)
    public List<TireSupplierResponse> listSuppliersByOffice(UUID officeId) {
        log.debug("Listando proveedores de oficina: {}", officeId);

        // Validar acceso a la oficina
        if (!securityUtils.hasAccessToOffice(officeId)) {
            throw new ForbiddenOfficeAccessException(officeId);
        }

        List<TireSupplier> suppliers = tireSupplierRepository.findActiveByOfficeId(officeId);

        Office office = officeRepository.findById(officeId).orElse(null);

        return suppliers.stream()
                .map(supplier -> tireSupplierMapper.toResponseWithOffice(supplier, office))
                .collect(Collectors.toList());
    }

    /**
     * Busca proveedores por nombre, código o NIT con paginación.
     * El RLS filtrará automáticamente por oficina del usuario.
     *
     * @param searchTerm término de búsqueda
     * @param pageable configuración de paginación
     * @return página de DTOs con los proveedores encontrados
     */
    @Transactional(readOnly = true)
    public Page<TireSupplierResponse> searchSuppliers(String searchTerm, Pageable pageable) {
        log.debug("Buscando proveedores con término: {}", searchTerm);

        Page<TireSupplier> suppliers = tireSupplierRepository.search(searchTerm, pageable);

        return suppliers.map(supplier -> {
            Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);
            return tireSupplierMapper.toResponseWithOffice(supplier, office);
        });
    }

    /**
     * Activa o desactiva un proveedor.
     *
     * @param supplierId ID del proveedor
     * @param active true para activar, false para desactivar
     * @return DTO con los datos del proveedor actualizado
     * @throws TireSupplierNotFoundException si el proveedor no existe
     */
    @Transactional
    public TireSupplierResponse setSupplierActive(UUID supplierId, boolean active) {
        log.info("Cambiando estado de proveedor {} a: {}", supplierId, active ? "activo" : "inactivo");

        TireSupplier supplier = tireSupplierRepository.findByIdAndDeletedAtIsNull(supplierId)
                .orElseThrow(() -> new TireSupplierNotFoundException(supplierId));

        supplier.setIsActive(active);
        TireSupplier updatedSupplier = tireSupplierRepository.save(supplier);

        Office office = officeRepository.findById(supplier.getOfficeId()).orElse(null);

        log.info("Estado de proveedor actualizado exitosamente: {}", supplierId);

        return tireSupplierMapper.toResponseWithOffice(updatedSupplier, office);
    }
}
