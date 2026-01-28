package com.transer.vortice.organization.application.service;

import com.transer.vortice.organization.application.dto.CreateOfficeRequest;
import com.transer.vortice.organization.application.dto.OfficeResponse;
import com.transer.vortice.organization.application.dto.UpdateOfficeRequest;
import com.transer.vortice.organization.application.mapper.OfficeMapper;
import com.transer.vortice.organization.domain.exception.DuplicateCodeException;
import com.transer.vortice.organization.domain.exception.EntityInUseException;
import com.transer.vortice.organization.domain.exception.OfficeNotFoundException;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.repository.OfficeRepository;
import com.transer.vortice.organization.domain.repository.TireSupplierRepository;
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
 * Servicio de aplicación para gestión de oficinas.
 * Implementa los casos de uso relacionados con oficinas.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final WarehouseRepository warehouseRepository;
    private final TireSupplierRepository tireSupplierRepository;
    private final OfficeMapper officeMapper;
    private final SecurityUtils securityUtils;

    /**
     * Crea una nueva oficina.
     * Solo administradores nacionales pueden crear oficinas.
     *
     * @param request datos de la oficina a crear
     * @return DTO con los datos de la oficina creada
     * @throws DuplicateCodeException si ya existe una oficina con ese código
     */
    @Transactional
    public OfficeResponse createOffice(CreateOfficeRequest request) {
        log.info("Creando nueva oficina con código: {}", request.getCode());

        // Validar que no exista otra oficina con el mismo código
        if (officeRepository.existsByCodeAndDeletedAtIsNull(request.getCode())) {
            log.warn("Ya existe una oficina con el código: {}", request.getCode());
            throw new DuplicateCodeException("Oficina", request.getCode());
        }

        // Convertir DTO a entidad
        Office office = officeMapper.toEntity(request);

        // Guardar
        Office savedOffice = officeRepository.save(office);
        log.info("Oficina creada exitosamente con ID: {}", savedOffice.getId());

        return officeMapper.toResponse(savedOffice);
    }

    /**
     * Actualiza una oficina existente.
     * Solo administradores nacionales pueden actualizar oficinas.
     *
     * @param officeId ID de la oficina a actualizar
     * @param request datos actualizados
     * @return DTO con los datos de la oficina actualizada
     * @throws OfficeNotFoundException si la oficina no existe
     */
    @Transactional
    public OfficeResponse updateOffice(UUID officeId, UpdateOfficeRequest request) {
        log.info("Actualizando oficina con ID: {}", officeId);

        // Buscar oficina
        Office office = officeRepository.findByIdAndDeletedAtIsNull(officeId)
                .orElseThrow(() -> {
                    log.warn("Oficina no encontrada con ID: {}", officeId);
                    return new OfficeNotFoundException(officeId);
                });

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Actualizar información
        office.updateInfo(
            request.getName(),
            request.getCity(),
            request.getAddress(),
            request.getPhone(),
            currentUserId
        );

        // Guardar cambios
        Office updatedOffice = officeRepository.save(office);
        log.info("Oficina actualizada exitosamente: {}", officeId);

        return officeMapper.toResponse(updatedOffice);
    }

    /**
     * Elimina (soft delete) una oficina.
     * Solo se puede eliminar si no tiene almacenes, proveedores o usuarios activos.
     * Solo administradores nacionales pueden eliminar oficinas.
     *
     * @param officeId ID de la oficina a eliminar
     * @throws OfficeNotFoundException si la oficina no existe
     * @throws EntityInUseException si la oficina tiene dependencias activas
     */
    @Transactional
    public void deleteOffice(UUID officeId) {
        log.info("Eliminando oficina con ID: {}", officeId);

        // Buscar oficina
        Office office = officeRepository.findByIdAndDeletedAtIsNull(officeId)
                .orElseThrow(() -> {
                    log.warn("Oficina no encontrada con ID: {}", officeId);
                    return new OfficeNotFoundException(officeId);
                });

        // Validar que no tenga almacenes activos
        long activeWarehouses = warehouseRepository.countByOfficeIdAndDeletedAtIsNull(officeId);
        if (activeWarehouses > 0) {
            log.warn("No se puede eliminar la oficina {} porque tiene {} almacenes activos",
                    officeId, activeWarehouses);
            throw new EntityInUseException("Oficina", office.getCode(), "almacenes", activeWarehouses);
        }

        // Validar que no tenga proveedores activos
        long activeSuppliers = tireSupplierRepository.countByOfficeIdAndDeletedAtIsNull(officeId);
        if (activeSuppliers > 0) {
            log.warn("No se puede eliminar la oficina {} porque tiene {} proveedores activos",
                    officeId, activeSuppliers);
            throw new EntityInUseException("Oficina", office.getCode(), "proveedores", activeSuppliers);
        }

        // TODO: Validar que no tenga usuarios activos cuando se implemente el módulo de usuarios

        // Obtener usuario actual
        Long currentUserId = securityUtils.getCurrentUserId();

        // Realizar soft delete
        office.markAsDeleted(currentUserId);
        officeRepository.save(office);

        log.info("Oficina eliminada exitosamente: {}", officeId);
    }

    /**
     * Obtiene una oficina por su ID.
     *
     * @param officeId ID de la oficina
     * @return DTO con los datos de la oficina
     * @throws OfficeNotFoundException si la oficina no existe
     */
    @Transactional(readOnly = true)
    public OfficeResponse getOfficeById(UUID officeId) {
        log.debug("Obteniendo oficina con ID: {}", officeId);

        Office office = officeRepository.findByIdAndDeletedAtIsNull(officeId)
                .orElseThrow(() -> new OfficeNotFoundException(officeId));

        return officeMapper.toResponse(office);
    }

    /**
     * Obtiene una oficina por su código.
     *
     * @param code código de la oficina
     * @return DTO con los datos de la oficina
     * @throws OfficeNotFoundException si la oficina no existe
     */
    @Transactional(readOnly = true)
    public OfficeResponse getOfficeByCode(String code) {
        log.debug("Obteniendo oficina con código: {}", code);

        Office office = officeRepository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new OfficeNotFoundException(code));

        return officeMapper.toResponse(office);
    }

    /**
     * Lista todas las oficinas activas.
     * Los administradores nacionales ven todas las oficinas.
     * Otros usuarios solo ven su propia oficina.
     *
     * @return lista de DTOs con todas las oficinas
     */
    @Transactional(readOnly = true)
    public List<OfficeResponse> listAllOffices() {
        log.debug("Listando todas las oficinas");

        List<Office> offices;

        if (securityUtils.isNationalAdmin()) {
            // Admin nacional ve todas las oficinas
            offices = officeRepository.findAllActive();
        } else {
            // Otros usuarios solo ven su propia oficina
            UUID userOfficeId = securityUtils.getCurrentUserOfficeId();
            offices = officeRepository.findByIdAndDeletedAtIsNull(userOfficeId)
                    .map(List::of)
                    .orElse(List.of());
        }

        return offices.stream()
                .map(officeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca oficinas por nombre o ciudad con paginación.
     * Solo administradores nacionales pueden buscar en todas las oficinas.
     *
     * @param searchTerm término de búsqueda
     * @param pageable configuración de paginación
     * @return página de DTOs con las oficinas encontradas
     */
    @Transactional(readOnly = true)
    public Page<OfficeResponse> searchOffices(String searchTerm, Pageable pageable) {
        log.debug("Buscando oficinas con término: {}", searchTerm);

        Page<Office> offices;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            offices = officeRepository.findAllActive(pageable);
        } else {
            offices = officeRepository.searchByNameOrCity(searchTerm, pageable);
        }

        return offices.map(officeMapper::toResponse);
    }

    /**
     * Obtiene una oficina con información detallada (totales de almacenes, proveedores, usuarios).
     * Solo administradores nacionales pueden ver detalles de cualquier oficina.
     *
     * @param officeId ID de la oficina
     * @return DTO con información detallada
     * @throws OfficeNotFoundException si la oficina no existe
     */
    @Transactional(readOnly = true)
    public OfficeResponse getOfficeWithDetails(UUID officeId) {
        log.debug("Obteniendo oficina con detalles: {}", officeId);

        Office office = officeRepository.findByIdAndDeletedAtIsNull(officeId)
                .orElseThrow(() -> new OfficeNotFoundException(officeId));

        // Contar almacenes, proveedores y usuarios
        Long totalWarehouses = warehouseRepository.countByOfficeIdAndDeletedAtIsNull(officeId);
        Long totalSuppliers = tireSupplierRepository.countByOfficeIdAndDeletedAtIsNull(officeId);
        Long totalUsers = 0L; // TODO: Implementar cuando exista UserRepository con officeId

        return officeMapper.toResponseWithDetails(office, totalWarehouses, totalSuppliers, totalUsers);
    }

    /**
     * Activa o desactiva una oficina.
     * Solo administradores nacionales pueden cambiar el estado de una oficina.
     *
     * @param officeId ID de la oficina
     * @param active true para activar, false para desactivar
     * @return DTO con los datos de la oficina actualizada
     * @throws OfficeNotFoundException si la oficina no existe
     */
    @Transactional
    public OfficeResponse setOfficeActive(UUID officeId, boolean active) {
        log.info("Cambiando estado de oficina {} a: {}", officeId, active ? "activa" : "inactiva");

        Office office = officeRepository.findByIdAndDeletedAtIsNull(officeId)
                .orElseThrow(() -> new OfficeNotFoundException(officeId));

        office.setIsActive(active);
        Office updatedOffice = officeRepository.save(office);

        log.info("Estado de oficina actualizado exitosamente: {}", officeId);

        return officeMapper.toResponse(updatedOffice);
    }
}
