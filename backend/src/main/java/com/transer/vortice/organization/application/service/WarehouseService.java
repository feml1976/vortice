package com.transer.vortice.organization.application.service;

import com.transer.vortice.organization.application.dto.CreateWarehouseRequest;
import com.transer.vortice.organization.application.dto.UpdateWarehouseRequest;
import com.transer.vortice.organization.application.dto.WarehouseResponse;
import com.transer.vortice.organization.application.mapper.WarehouseMapper;
import com.transer.vortice.organization.domain.exception.*;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.Warehouse;
import com.transer.vortice.organization.domain.repository.OfficeRepository;
import com.transer.vortice.organization.domain.repository.WarehouseLocationRepository;
import com.transer.vortice.organization.domain.repository.WarehouseRepository;
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
 * Servicio de aplicación para gestión de almacenes.
 * Implementa los casos de uso relacionados con almacenes.
 *
 * SEGURIDAD: Este servicio está sujeto a Row-Level Security (RLS).
 * Los usuarios solo pueden ver almacenes de su oficina, excepto administradores nacionales.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OfficeRepository officeRepository;
    private final WarehouseLocationRepository warehouseLocationRepository;
    private final WarehouseMapper warehouseMapper;
    private final SecurityUtils securityUtils;

    /**
     * Crea un nuevo almacén.
     * El almacén debe pertenecer a una oficina válida.
     * Los usuarios solo pueden crear almacenes en su propia oficina (excepto admin nacional).
     *
     * @param request datos del almacén a crear
     * @return DTO con los datos del almacén creado
     * @throws OfficeNotFoundException si la oficina no existe
     * @throws ForbiddenOfficeAccessException si el usuario no tiene acceso a la oficina
     * @throws DuplicateCodeException si ya existe un almacén con ese código en la oficina
     */
    @Transactional
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {
        log.info("Creando nuevo almacén con código: {} en oficina: {}",
                request.getCode(), request.getOfficeId());

        // Validar que el usuario tenga acceso a la oficina
        if (!securityUtils.hasAccessToOffice(request.getOfficeId())) {
            log.warn("Usuario sin acceso intenta crear almacén en oficina: {}", request.getOfficeId());
            throw new ForbiddenOfficeAccessException(request.getOfficeId());
        }

        // Validar que la oficina exista
        Office office = officeRepository.findByIdAndDeletedAtIsNull(request.getOfficeId())
                .orElseThrow(() -> {
                    log.warn("Oficina no encontrada: {}", request.getOfficeId());
                    return new OfficeNotFoundException(request.getOfficeId());
                });

        // Validar que no exista otro almacén con el mismo código en la misma oficina
        if (warehouseRepository.existsByCodeAndOfficeIdAndDeletedAtIsNull(
                request.getCode(), request.getOfficeId())) {
            log.warn("Ya existe almacén con código {} en oficina {}",
                    request.getCode(), request.getOfficeId());
            throw new DuplicateCodeException("Almacén", request.getCode(),
                    "la oficina " + office.getName());
        }

        // Convertir DTO a entidad
        Warehouse warehouse = warehouseMapper.toEntity(request);

        // Guardar
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        log.info("Almacén creado exitosamente con ID: {}", savedWarehouse.getId());

        return warehouseMapper.toResponseWithOffice(savedWarehouse, office);
    }

    /**
     * Actualiza un almacén existente.
     * No se puede cambiar el código ni la oficina del almacén.
     *
     * @param warehouseId ID del almacén a actualizar
     * @param request datos actualizados
     * @return DTO con los datos del almacén actualizado
     * @throws WarehouseNotFoundException si el almacén no existe o no es accesible
     */
    @Transactional
    public WarehouseResponse updateWarehouse(UUID warehouseId, UpdateWarehouseRequest request) {
        log.info("Actualizando almacén con ID: {}", warehouseId);

        // Buscar almacén (RLS filtrará automáticamente por oficina del usuario)
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> {
                    log.warn("Almacén no encontrado con ID: {}", warehouseId);
                    return new WarehouseNotFoundException(warehouseId);
                });

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Actualizar información
        warehouse.updateInfo(
            request.getName(),
            request.getDescription(),
            currentUserId
        );

        // Guardar cambios
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        log.info("Almacén actualizado exitosamente: {}", warehouseId);

        // Cargar oficina para respuesta completa
        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        return warehouseMapper.toResponseWithOffice(updatedWarehouse, office);
    }

    /**
     * Elimina (soft delete) un almacén.
     * Solo se puede eliminar si no tiene ubicaciones activas.
     *
     * @param warehouseId ID del almacén a eliminar
     * @throws WarehouseNotFoundException si el almacén no existe
     * @throws EntityInUseException si el almacén tiene ubicaciones activas
     */
    @Transactional
    public void deleteWarehouse(UUID warehouseId) {
        log.info("Eliminando almacén con ID: {}", warehouseId);

        // Buscar almacén (RLS filtrará automáticamente)
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> {
                    log.warn("Almacén no encontrado con ID: {}", warehouseId);
                    return new WarehouseNotFoundException(warehouseId);
                });

        // Validar que no tenga ubicaciones activas
        long activeLocations = warehouseLocationRepository.countByWarehouseIdAndDeletedAtIsNull(warehouseId);
        if (activeLocations > 0) {
            log.warn("No se puede eliminar el almacén {} porque tiene {} ubicaciones activas",
                    warehouseId, activeLocations);
            throw new EntityInUseException("Almacén", warehouse.getCode(),
                    "ubicaciones", activeLocations);
        }

        // TODO: Validar que no tenga inventario activo cuando se implemente el módulo de inventario

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Realizar soft delete
        warehouse.markAsDeleted(currentUserId);
        warehouseRepository.save(warehouse);

        log.info("Almacén eliminado exitosamente: {}", warehouseId);
    }

    /**
     * Obtiene un almacén por su ID.
     *
     * @param warehouseId ID del almacén
     * @return DTO con los datos del almacén
     * @throws WarehouseNotFoundException si el almacén no existe o no es accesible
     */
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(UUID warehouseId) {
        log.debug("Obteniendo almacén con ID: {}", warehouseId);

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        return warehouseMapper.toResponseWithOffice(warehouse, office);
    }

    /**
     * Obtiene un almacén por su código y oficina.
     *
     * @param code código del almacén
     * @param officeId ID de la oficina
     * @return DTO con los datos del almacén
     * @throws WarehouseNotFoundException si el almacén no existe
     * @throws ForbiddenOfficeAccessException si el usuario no tiene acceso a la oficina
     */
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseByCode(String code, UUID officeId) {
        log.debug("Obteniendo almacén con código: {} en oficina: {}", code, officeId);

        // Validar acceso a la oficina
        if (!securityUtils.hasAccessToOffice(officeId)) {
            throw new ForbiddenOfficeAccessException(officeId);
        }

        Warehouse warehouse = warehouseRepository.findByCodeAndOfficeIdAndDeletedAtIsNull(code, officeId)
                .orElseThrow(() -> new WarehouseNotFoundException(code, officeId));

        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        return warehouseMapper.toResponseWithOffice(warehouse, office);
    }

    /**
     * Lista todos los almacenes accesibles por el usuario actual.
     * Los administradores nacionales ven todos los almacenes.
     * Otros usuarios solo ven almacenes de su oficina (filtrado por RLS).
     *
     * @return lista de DTOs con todos los almacenes
     */
    @Transactional(readOnly = true)
    public List<WarehouseResponse> listAllWarehouses() {
        log.debug("Listando todos los almacenes");

        List<Warehouse> warehouses = warehouseRepository.findAllActive();

        // Cargar todas las oficinas necesarias
        // Nota: en producción se podría optimizar con fetch join
        return warehouses.stream()
                .map(warehouse -> {
                    Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);
                    return warehouseMapper.toResponseWithOffice(warehouse, office);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los almacenes de una oficina específica.
     *
     * @param officeId ID de la oficina
     * @return lista de DTOs con los almacenes de la oficina
     * @throws ForbiddenOfficeAccessException si el usuario no tiene acceso a la oficina
     */
    @Transactional(readOnly = true)
    public List<WarehouseResponse> listWarehousesByOffice(UUID officeId) {
        log.debug("Listando almacenes de oficina: {}", officeId);

        // Validar acceso a la oficina
        if (!securityUtils.hasAccessToOffice(officeId)) {
            throw new ForbiddenOfficeAccessException(officeId);
        }

        List<Warehouse> warehouses = warehouseRepository.findActiveByOfficeId(officeId);

        Office office = officeRepository.findById(officeId).orElse(null);

        return warehouses.stream()
                .map(warehouse -> warehouseMapper.toResponseWithOffice(warehouse, office))
                .collect(Collectors.toList());
    }

    /**
     * Busca almacenes por nombre o código con paginación.
     * El RLS filtrará automáticamente por oficina del usuario.
     *
     * @param searchTerm término de búsqueda
     * @param pageable configuración de paginación
     * @return página de DTOs con los almacenes encontrados
     */
    @Transactional(readOnly = true)
    public Page<WarehouseResponse> searchWarehouses(String searchTerm, Pageable pageable) {
        log.debug("Buscando almacenes con término: {}", searchTerm);

        Page<Warehouse> warehouses = warehouseRepository.search(searchTerm, pageable);

        return warehouses.map(warehouse -> {
            Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);
            return warehouseMapper.toResponseWithOffice(warehouse, office);
        });
    }

    /**
     * Obtiene un almacén con información detallada (totales de ubicaciones, inventario).
     *
     * @param warehouseId ID del almacén
     * @return DTO con información detallada
     * @throws WarehouseNotFoundException si el almacén no existe
     */
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseWithDetails(UUID warehouseId) {
        log.debug("Obteniendo almacén con detalles: {}", warehouseId);

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        // Contar ubicaciones
        Long totalLocations = warehouseLocationRepository.countByWarehouseIdAndDeletedAtIsNull(warehouseId);

        return warehouseMapper.toResponseWithDetails(warehouse, office, totalLocations);
    }

    /**
     * Activa o desactiva un almacén.
     *
     * @param warehouseId ID del almacén
     * @param active true para activar, false para desactivar
     * @return DTO con los datos del almacén actualizado
     * @throws WarehouseNotFoundException si el almacén no existe
     */
    @Transactional
    public WarehouseResponse setWarehouseActive(UUID warehouseId, boolean active) {
        log.info("Cambiando estado de almacén {} a: {}", warehouseId, active ? "activo" : "inactivo");

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

        warehouse.setIsActive(active);
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        log.info("Estado de almacén actualizado exitosamente: {}", warehouseId);

        return warehouseMapper.toResponseWithOffice(updatedWarehouse, office);
    }
}
