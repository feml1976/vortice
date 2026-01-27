package com.transer.vortice.tire.application.service;

import com.transer.vortice.shared.infrastructure.exception.BusinessException;
import com.transer.vortice.shared.infrastructure.exception.NotFoundException;
import com.transer.vortice.shared.infrastructure.exception.ValidationException;
import com.transer.vortice.tire.application.dto.request.CreateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.request.UpdateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.response.TireSpecificationResponse;
import com.transer.vortice.tire.application.dto.response.TireSpecificationSummaryResponse;
import com.transer.vortice.tire.application.mapper.TireSpecificationMapper;
import com.transer.vortice.tire.domain.model.TireSpecification;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import com.transer.vortice.tire.domain.repository.*;
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
 * Servicio de aplicación para Especificaciones Técnicas de Llantas
 *
 * Implementa todos los casos de uso relacionados con la gestión de fichas técnicas.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TireSpecificationService {

    private final TireSpecificationRepository tireSpecificationRepository;
    private final TireBrandRepository tireBrandRepository;
    private final TireTypeRepository tireTypeRepository;
    private final TireReferenceRepository tireReferenceRepository;
    private final TireSupplierRepository tireSupplierRepository;
    private final TireSpecificationMapper mapper;
    private final CodeGeneratorService codeGeneratorService;

    // =====================================================
    // USE CASE: Crear Especificación Técnica
    // =====================================================

    /**
     * Crea una nueva especificación técnica de llanta
     *
     * @param request datos de la especificación a crear
     * @return especificación creada
     * @throws ValidationException si hay errores de validación
     */
    @Transactional
    public TireSpecificationResponse createTireSpecification(CreateTireSpecificationRequest request) {
        log.info("Creando nueva especificación técnica");

        // Validar que las relaciones existan
        TireBrand brand = validateAndGetBrand(request.getBrandId());
        TireType type = validateAndGetType(request.getTypeId());
        TireReference reference = validateAndGetReference(request.getReferenceId());

        // Validar proveedores (opcionales)
        TireSupplier mainProvider = validateAndGetSupplierIfPresent(request.getMainProviderId());
        TireSupplier secondaryProvider = validateAndGetSupplierIfPresent(request.getSecondaryProviderId());
        TireSupplier lastUsedProvider = validateAndGetSupplierIfPresent(request.getLastUsedProviderId());

        // Crear la entidad
        TireSpecification specification = mapper.toEntity(request);

        // Generar código único autoincremental
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();
        specification.setCode(generatedCode);

        // Inyectar las relaciones
        specification.setBrand(brand);
        specification.setType(type);
        specification.setReference(reference);
        specification.setMainProvider(mainProvider);
        specification.setSecondaryProvider(secondaryProvider);
        specification.setLastUsedProvider(lastUsedProvider);

        // Validar coherencia de rangos de kilometraje
        if (!specification.validateMileageRanges()) {
            throw new ValidationException("Los rangos de kilometraje no son coherentes (min <= avg <= max)");
        }

        // Guardar
        TireSpecification savedSpecification = tireSpecificationRepository.save(specification);

        log.info("Especificación técnica creada exitosamente con código: {}", savedSpecification.getCode());

        return mapper.toResponse(savedSpecification);
    }

    // =====================================================
    // USE CASE: Listar Especificaciones Técnicas
    // =====================================================

    /**
     * Lista todas las especificaciones técnicas con paginación
     *
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Transactional(readOnly = true)
    public Page<TireSpecificationSummaryResponse> listTireSpecifications(Pageable pageable) {
        log.info("Listando especificaciones técnicas - Página: {}, Tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<TireSpecification> specificationsPage = tireSpecificationRepository.findAll(pageable);

        return specificationsPage.map(mapper::toSummaryResponse);
    }

    /**
     * Lista todas las especificaciones técnicas activas sin paginación
     *
     * @return lista de especificaciones activas
     */
    @Transactional(readOnly = true)
    public List<TireSpecificationSummaryResponse> listActiveTireSpecifications() {
        log.info("Listando todas las especificaciones técnicas activas");

        List<TireSpecification> specifications = tireSpecificationRepository.findAllActive();

        return specifications.stream()
                .map(mapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // USE CASE: Obtener Especificación Técnica por ID
    // =====================================================

    /**
     * Obtiene una especificación técnica por su ID
     *
     * @param id ID de la especificación
     * @return especificación encontrada
     * @throws NotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public TireSpecificationResponse getTireSpecificationById(UUID id) {
        log.info("Obteniendo especificación técnica por ID: {}", id);

        TireSpecification specification = tireSpecificationRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Especificación técnica no encontrada con ID: " + id));

        return mapper.toResponse(specification);
    }

    /**
     * Obtiene una especificación técnica por su código
     *
     * @param code código de la especificación
     * @return especificación encontrada
     * @throws NotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public TireSpecificationResponse getTireSpecificationByCode(String code) {
        log.info("Obteniendo especificación técnica por código: {}", code);

        TireSpecification specification = tireSpecificationRepository.findActiveByCode(code)
                .orElseThrow(() -> new NotFoundException("Especificación técnica no encontrada con código: " + code));

        return mapper.toResponse(specification);
    }

    // =====================================================
    // USE CASE: Actualizar Especificación Técnica
    // =====================================================

    /**
     * Actualiza una especificación técnica existente
     *
     * @param id ID de la especificación a actualizar
     * @param request datos de actualización
     * @return especificación actualizada
     * @throws NotFoundException si no existe
     * @throws ValidationException si hay errores de validación
     */
    @Transactional
    public TireSpecificationResponse updateTireSpecification(UUID id, UpdateTireSpecificationRequest request) {
        log.info("Actualizando especificación técnica con ID: {}", id);

        // Buscar la especificación existente
        TireSpecification specification = tireSpecificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Especificación técnica no encontrada con ID: " + id));

        // Validar que no esté eliminada (soft delete)
        if (specification.isDeleted()) {
            throw new BusinessException("No se puede actualizar una especificación técnica eliminada");
        }

        // Validar que las relaciones existan
        TireBrand brand = validateAndGetBrand(request.getBrandId());
        TireType type = validateAndGetType(request.getTypeId());
        TireReference reference = validateAndGetReference(request.getReferenceId());

        // Validar proveedores (opcionales)
        TireSupplier mainProvider = validateAndGetSupplierIfPresent(request.getMainProviderId());
        TireSupplier secondaryProvider = validateAndGetSupplierIfPresent(request.getSecondaryProviderId());
        TireSupplier lastUsedProvider = validateAndGetSupplierIfPresent(request.getLastUsedProviderId());

        // Actualizar los campos
        mapper.updateEntity(specification, request);

        // Actualizar las relaciones
        specification.setBrand(brand);
        specification.setType(type);
        specification.setReference(reference);
        specification.setMainProvider(mainProvider);
        specification.setSecondaryProvider(secondaryProvider);
        specification.setLastUsedProvider(lastUsedProvider);

        // Validar coherencia de rangos de kilometraje
        if (!specification.validateMileageRanges()) {
            throw new ValidationException("Los rangos de kilometraje no son coherentes (min <= avg <= max)");
        }

        // Guardar cambios
        TireSpecification updatedSpecification = tireSpecificationRepository.save(specification);

        log.info("Especificación técnica actualizada exitosamente: {}", updatedSpecification.getCode());

        return mapper.toResponse(updatedSpecification);
    }

    // =====================================================
    // USE CASE: Eliminar Especificación Técnica (Soft Delete)
    // =====================================================

    /**
     * Elimina una especificación técnica (soft delete)
     *
     * @param id ID de la especificación a eliminar
     * @throws NotFoundException si no existe
     * @throws BusinessException si tiene llantas asociadas
     */
    @Transactional
    public void deleteTireSpecification(UUID id) {
        log.info("Eliminando especificación técnica con ID: {}", id);

        // Buscar la especificación
        TireSpecification specification = tireSpecificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Especificación técnica no encontrada con ID: " + id));

        // Validar que no esté ya eliminada
        if (specification.isDeleted()) {
            throw new BusinessException("La especificación técnica ya está eliminada");
        }

        // TODO: Validar que no existan llantas asociadas en las tablas:
        // - tire_management.inventory
        // - tire_management.active_installations
        // - tire_management.intermediate
        // - tire_management.retired
        // - tire_management.history_records
        // Esta validación se implementará cuando se creen esos repositorios

        // Por ahora, solo verificamos que se pueda eliminar según el método del dominio
        if (!specification.canBeDeleted()) {
            throw new BusinessException("La especificación técnica no puede ser eliminada en este momento");
        }

        // Realizar soft delete
        specification.markAsDeleted(getCurrentUserId());
        tireSpecificationRepository.save(specification);

        log.info("Especificación técnica eliminada exitosamente (soft delete): {}", specification.getCode());
    }

    // =====================================================
    // USE CASE: Buscar Especificaciones Técnicas
    // =====================================================

    /**
     * Busca especificaciones técnicas por texto libre
     *
     * @param searchText texto a buscar
     * @param pageable configuración de paginación
     * @return página de especificaciones encontradas
     */
    @Transactional(readOnly = true)
    public Page<TireSpecificationSummaryResponse> searchTireSpecifications(String searchText, Pageable pageable) {
        log.info("Buscando especificaciones técnicas con texto: {}", searchText);

        Page<TireSpecification> specificationsPage = tireSpecificationRepository.searchByText(searchText, pageable);

        return specificationsPage.map(mapper::toSummaryResponse);
    }

    /**
     * Busca especificaciones técnicas con filtros múltiples
     *
     * @param brandId ID de la marca (opcional)
     * @param typeId ID del tipo (opcional)
     * @param referenceId ID de la referencia (opcional)
     * @param isActive estado activo (opcional)
     * @param pageable configuración de paginación
     * @return página de especificaciones encontradas
     */
    @Transactional(readOnly = true)
    public Page<TireSpecificationSummaryResponse> findByFilters(
            UUID brandId,
            UUID typeId,
            UUID referenceId,
            Boolean isActive,
            Pageable pageable
    ) {
        log.info("Buscando especificaciones técnicas con filtros - Brand: {}, Type: {}, Reference: {}, Active: {}",
                brandId, typeId, referenceId, isActive);

        Page<TireSpecification> specificationsPage = tireSpecificationRepository.findByFilters(
                brandId, typeId, referenceId, isActive, pageable
        );

        return specificationsPage.map(mapper::toSummaryResponse);
    }

    /**
     * Busca especificaciones técnicas por marca
     *
     * @param brandId ID de la marca
     * @param pageable configuración de paginación
     * @return página de especificaciones
     */
    @Transactional(readOnly = true)
    public Page<TireSpecificationSummaryResponse> findByBrand(UUID brandId, Pageable pageable) {
        log.info("Buscando especificaciones técnicas por marca: {}", brandId);

        // Validar que la marca exista
        validateAndGetBrand(brandId);

        Page<TireSpecification> specificationsPage = tireSpecificationRepository.findByBrandId(brandId, pageable);

        return specificationsPage.map(mapper::toSummaryResponse);
    }

    // =====================================================
    // MÉTODOS AUXILIARES DE VALIDACIÓN
    // =====================================================

    /**
     * Valida y obtiene una marca por su ID
     *
     * @param brandId ID de la marca
     * @return marca encontrada
     * @throws NotFoundException si no existe
     */
    private TireBrand validateAndGetBrand(UUID brandId) {
        return tireBrandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundException("Marca de llanta no encontrada con ID: " + brandId));
    }

    /**
     * Valida y obtiene un tipo por su ID
     *
     * @param typeId ID del tipo
     * @return tipo encontrado
     * @throws NotFoundException si no existe
     */
    private TireType validateAndGetType(UUID typeId) {
        return tireTypeRepository.findById(typeId)
                .orElseThrow(() -> new NotFoundException("Tipo de llanta no encontrado con ID: " + typeId));
    }

    /**
     * Valida y obtiene una referencia por su ID
     *
     * @param referenceId ID de la referencia
     * @return referencia encontrada
     * @throws NotFoundException si no existe
     */
    private TireReference validateAndGetReference(UUID referenceId) {
        return tireReferenceRepository.findById(referenceId)
                .orElseThrow(() -> new NotFoundException("Referencia de llanta no encontrada con ID: " + referenceId));
    }

    /**
     * Valida y obtiene un proveedor por su ID (si está presente)
     *
     * @param supplierId ID del proveedor (puede ser null)
     * @return proveedor encontrado o null
     * @throws NotFoundException si el ID no es null y no existe
     */
    private TireSupplier validateAndGetSupplierIfPresent(UUID supplierId) {
        if (supplierId == null) {
            return null;
        }
        return tireSupplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Proveedor de llantas no encontrado con ID: " + supplierId));
    }

    /**
     * Obtiene el ID del usuario autenticado actual
     * TODO: Implementar cuando se integre con Spring Security
     *
     * @return ID del usuario actual
     */
    private Long getCurrentUserId() {
        // Temporalmente retorna null
        // Se implementará cuando se integre con Spring Security Context
        return null;
    }
}
