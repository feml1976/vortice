package com.transer.vortice.organization.application.service;

import com.transer.vortice.organization.application.dto.CreateWarehouseLocationRequest;
import com.transer.vortice.organization.application.dto.UpdateWarehouseLocationRequest;
import com.transer.vortice.organization.application.dto.WarehouseLocationResponse;
import com.transer.vortice.organization.application.mapper.WarehouseLocationMapper;
import com.transer.vortice.organization.domain.exception.DuplicateCodeException;
import com.transer.vortice.organization.domain.exception.EntityInUseException;
import com.transer.vortice.organization.domain.exception.ForbiddenOfficeAccessException;
import com.transer.vortice.organization.domain.exception.WarehouseLocationNotFoundException;
import com.transer.vortice.organization.domain.exception.WarehouseNotFoundException;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.Warehouse;
import com.transer.vortice.organization.domain.model.WarehouseLocation;
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
 * Servicio de aplicación para gestión de ubicaciones de almacén.
 * Implementa los casos de uso relacionados con ubicaciones de almacén.
 *
 * SEGURIDAD: Este servicio está sujeto a Row-Level Security (RLS) a través de los almacenes.
 * Los usuarios solo pueden ver ubicaciones de almacenes de su oficina.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseLocationService {

    private final WarehouseLocationRepository warehouseLocationRepository;
    private final WarehouseRepository warehouseRepository;
    private final OfficeRepository officeRepository;
    private final WarehouseLocationMapper warehouseLocationMapper;
    private final SecurityUtils securityUtils;

    /**
     * Crea una nueva ubicación de almacén.
     * La ubicación debe pertenecer a un almacén válido y accesible.
     *
     * @param request datos de la ubicación a crear
     * @return DTO con los datos de la ubicación creada
     * @throws WarehouseNotFoundException si el almacén no existe o no es accesible
     * @throws DuplicateCodeException si ya existe una ubicación con ese código en el almacén
     */
    @Transactional
    public WarehouseLocationResponse createWarehouseLocation(CreateWarehouseLocationRequest request) {
        log.info("Creando nueva ubicación con código: {} en almacén: {}",
                request.getCode(), request.getWarehouseId());

        // Validar que el almacén exista y sea accesible (RLS filtrará automáticamente)
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getWarehouseId())
                .orElseThrow(() -> {
                    log.warn("Almacén no encontrado o no accesible: {}", request.getWarehouseId());
                    return new WarehouseNotFoundException(request.getWarehouseId());
                });

        // Validar que no exista otra ubicación con el mismo código en el mismo almacén
        if (warehouseLocationRepository.existsByCodeAndWarehouseIdAndDeletedAtIsNull(
                request.getCode(), request.getWarehouseId())) {
            log.warn("Ya existe ubicación con código {} en almacén {}",
                    request.getCode(), request.getWarehouseId());
            throw new DuplicateCodeException("Ubicación", request.getCode(),
                    "el almacén " + warehouse.getName());
        }

        // Convertir DTO a entidad
        WarehouseLocation location = warehouseLocationMapper.toEntity(request);

        // Guardar
        WarehouseLocation savedLocation = warehouseLocationRepository.save(location);
        log.info("Ubicación creada exitosamente con ID: {}", savedLocation.getId());

        // Cargar oficina para respuesta completa
        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        return warehouseLocationMapper.toResponseWithDetails(savedLocation, warehouse, office);
    }

    /**
     * Actualiza una ubicación existente.
     * No se puede cambiar el código ni el almacén de la ubicación.
     *
     * @param locationId ID de la ubicación a actualizar
     * @param request datos actualizados
     * @return DTO con los datos de la ubicación actualizada
     * @throws WarehouseLocationNotFoundException si la ubicación no existe o no es accesible
     */
    @Transactional
    public WarehouseLocationResponse updateWarehouseLocation(UUID locationId,
                                                              UpdateWarehouseLocationRequest request) {
        log.info("Actualizando ubicación con ID: {}", locationId);

        // Buscar ubicación (RLS filtrará automáticamente a través del almacén)
        WarehouseLocation location = warehouseLocationRepository.findByIdAndDeletedAtIsNull(locationId)
                .orElseThrow(() -> {
                    log.warn("Ubicación no encontrada con ID: {}", locationId);
                    return new WarehouseLocationNotFoundException(locationId);
                });

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Actualizar información
        location.updateInfo(
            request.getName(),
            request.getDescription(),
            currentUserId
        );

        // Guardar cambios
        WarehouseLocation updatedLocation = warehouseLocationRepository.save(location);
        log.info("Ubicación actualizada exitosamente: {}", locationId);

        // Cargar almacén y oficina para respuesta completa
        Warehouse warehouse = warehouseRepository.findById(location.getWarehouseId()).orElse(null);
        Office office = warehouse != null ?
                officeRepository.findById(warehouse.getOfficeId()).orElse(null) : null;

        return warehouseLocationMapper.toResponseWithDetails(updatedLocation, warehouse, office);
    }

    /**
     * Elimina (soft delete) una ubicación de almacén.
     * Solo se puede eliminar si no tiene llantas asignadas.
     *
     * @param locationId ID de la ubicación a eliminar
     * @throws WarehouseLocationNotFoundException si la ubicación no existe
     * @throws EntityInUseException si la ubicación tiene llantas asignadas
     */
    @Transactional
    public void deleteWarehouseLocation(UUID locationId) {
        log.info("Eliminando ubicación con ID: {}", locationId);

        // Buscar ubicación (RLS filtrará automáticamente)
        WarehouseLocation location = warehouseLocationRepository.findByIdAndDeletedAtIsNull(locationId)
                .orElseThrow(() -> {
                    log.warn("Ubicación no encontrada con ID: {}", locationId);
                    return new WarehouseLocationNotFoundException(locationId);
                });

        // TODO: Validar que no tenga llantas asignadas cuando se implemente el módulo de llantas
        // long assignedTires = tireRepository.countByLocationIdAndDeletedAtIsNull(locationId);
        // if (assignedTires > 0) {
        //     throw new EntityInUseException("Ubicación", location.getCode(), "llantas", assignedTires);
        // }

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Realizar soft delete
        location.markAsDeleted(currentUserId);
        warehouseLocationRepository.save(location);

        log.info("Ubicación eliminada exitosamente: {}", locationId);
    }

    /**
     * Obtiene una ubicación por su ID.
     *
     * @param locationId ID de la ubicación
     * @return DTO con los datos de la ubicación
     * @throws WarehouseLocationNotFoundException si la ubicación no existe o no es accesible
     */
    @Transactional(readOnly = true)
    public WarehouseLocationResponse getLocationById(UUID locationId) {
        log.debug("Obteniendo ubicación con ID: {}", locationId);

        WarehouseLocation location = warehouseLocationRepository.findByIdAndDeletedAtIsNull(locationId)
                .orElseThrow(() -> new WarehouseLocationNotFoundException(locationId));

        Warehouse warehouse = warehouseRepository.findById(location.getWarehouseId()).orElse(null);
        Office office = warehouse != null ?
                officeRepository.findById(warehouse.getOfficeId()).orElse(null) : null;

        return warehouseLocationMapper.toResponseWithDetails(location, warehouse, office);
    }

    /**
     * Obtiene una ubicación por su código y almacén.
     *
     * @param code código de la ubicación
     * @param warehouseId ID del almacén
     * @return DTO con los datos de la ubicación
     * @throws WarehouseLocationNotFoundException si la ubicación no existe
     */
    @Transactional(readOnly = true)
    public WarehouseLocationResponse getLocationByCode(String code, UUID warehouseId) {
        log.debug("Obteniendo ubicación con código: {} en almacén: {}", code, warehouseId);

        // Validar que el almacén sea accesible
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

        WarehouseLocation location = warehouseLocationRepository
                .findByCodeAndWarehouseIdAndDeletedAtIsNull(code, warehouseId)
                .orElseThrow(() -> new WarehouseLocationNotFoundException(code, warehouseId));

        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        return warehouseLocationMapper.toResponseWithDetails(location, warehouse, office);
    }

    /**
     * Lista todas las ubicaciones accesibles por el usuario actual.
     * El RLS filtrará automáticamente por oficina del usuario.
     *
     * @return lista de DTOs con todas las ubicaciones
     */
    @Transactional(readOnly = true)
    public List<WarehouseLocationResponse> listAllLocations() {
        log.debug("Listando todas las ubicaciones");

        List<WarehouseLocation> locations = warehouseLocationRepository.findAllActive();

        return locations.stream()
                .map(location -> {
                    Warehouse warehouse = warehouseRepository.findById(location.getWarehouseId()).orElse(null);
                    Office office = warehouse != null ?
                            officeRepository.findById(warehouse.getOfficeId()).orElse(null) : null;
                    return warehouseLocationMapper.toResponseWithDetails(location, warehouse, office);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las ubicaciones de un almacén específico.
     *
     * @param warehouseId ID del almacén
     * @return lista de DTOs con las ubicaciones del almacén
     * @throws WarehouseNotFoundException si el almacén no existe o no es accesible
     */
    @Transactional(readOnly = true)
    public List<WarehouseLocationResponse> listLocationsByWarehouse(UUID warehouseId) {
        log.debug("Listando ubicaciones de almacén: {}", warehouseId);

        // Validar que el almacén sea accesible
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

        List<WarehouseLocation> locations =
                warehouseLocationRepository.findActiveByWarehouseId(warehouseId);

        Office office = officeRepository.findById(warehouse.getOfficeId()).orElse(null);

        return locations.stream()
                .map(location -> warehouseLocationMapper.toResponseWithDetails(location, warehouse, office))
                .collect(Collectors.toList());
    }

    /**
     * Busca ubicaciones por nombre o código con paginación.
     * El RLS filtrará automáticamente por oficina del usuario.
     *
     * @param searchTerm término de búsqueda
     * @param pageable configuración de paginación
     * @return página de DTOs con las ubicaciones encontradas
     */
    @Transactional(readOnly = true)
    public Page<WarehouseLocationResponse> searchLocations(String searchTerm, Pageable pageable) {
        log.debug("Buscando ubicaciones con término: {}", searchTerm);

        Page<WarehouseLocation> locations = warehouseLocationRepository.search(searchTerm, pageable);

        return locations.map(location -> {
            Warehouse warehouse = warehouseRepository.findById(location.getWarehouseId()).orElse(null);
            Office office = warehouse != null ?
                    officeRepository.findById(warehouse.getOfficeId()).orElse(null) : null;
            return warehouseLocationMapper.toResponseWithDetails(location, warehouse, office);
        });
    }

    /**
     * Activa o desactiva una ubicación.
     *
     * @param locationId ID de la ubicación
     * @param active true para activar, false para desactivar
     * @return DTO con los datos de la ubicación actualizada
     * @throws WarehouseLocationNotFoundException si la ubicación no existe
     */
    @Transactional
    public WarehouseLocationResponse setLocationActive(UUID locationId, boolean active) {
        log.info("Cambiando estado de ubicación {} a: {}", locationId, active ? "activa" : "inactiva");

        WarehouseLocation location = warehouseLocationRepository.findByIdAndDeletedAtIsNull(locationId)
                .orElseThrow(() -> new WarehouseLocationNotFoundException(locationId));

        location.setIsActive(active);
        WarehouseLocation updatedLocation = warehouseLocationRepository.save(location);

        Warehouse warehouse = warehouseRepository.findById(location.getWarehouseId()).orElse(null);
        Office office = warehouse != null ?
                officeRepository.findById(warehouse.getOfficeId()).orElse(null) : null;

        log.info("Estado de ubicación actualizado exitosamente: {}", locationId);

        return warehouseLocationMapper.toResponseWithDetails(updatedLocation, warehouse, office);
    }
}
