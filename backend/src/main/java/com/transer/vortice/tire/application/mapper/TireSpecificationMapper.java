package com.transer.vortice.tire.application.mapper;

import com.transer.vortice.tire.application.dto.request.CreateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.request.UpdateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.response.*;
import com.transer.vortice.tire.domain.model.TireSpecification;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio y DTOs
 * del módulo de Especificaciones Técnicas de Llantas
 *
 * @author Vórtice Development Team
 */
@Component
public class TireSpecificationMapper {

    // =====================================================
    // MAPEO DE CATÁLOGOS
    // =====================================================

    public TireBrandResponse toBrandResponse(TireBrand brand) {
        if (brand == null) return null;
        return new TireBrandResponse(
                brand.getId(),
                brand.getCode(),
                brand.getName(),
                brand.getIsActive()
        );
    }

    public TireTypeResponse toTypeResponse(TireType type) {
        if (type == null) return null;
        return new TireTypeResponse(
                type.getId(),
                type.getCode(),
                type.getName(),
                type.getDescription(),
                type.getIsActive()
        );
    }

    public TireReferenceResponse toReferenceResponse(TireReference reference) {
        if (reference == null) return null;
        return new TireReferenceResponse(
                reference.getId(),
                reference.getCode(),
                reference.getName(),
                reference.getSpecifications(),
                reference.getIsActive()
        );
    }

    public TireSupplierResponse toSupplierResponse(TireSupplier supplier) {
        if (supplier == null) return null;
        return new TireSupplierResponse(
                supplier.getId(),
                supplier.getCode(),
                supplier.getName(),
                supplier.getTaxId(),
                supplier.getPhone(),
                supplier.getContactName(),
                supplier.getIsActive()
        );
    }

    // =====================================================
    // MAPEO DE ESPECIFICACIONES TÉCNICAS
    // =====================================================

    /**
     * Convierte una entidad TireSpecification a un DTO de respuesta completo
     */
    public TireSpecificationResponse toResponse(TireSpecification specification) {
        if (specification == null) return null;

        TireSpecificationResponse response = new TireSpecificationResponse();

        // Identificación
        response.setId(specification.getId());
        response.setCode(specification.getCode());

        // Relaciones (catálogos)
        response.setBrand(toBrandResponse(specification.getBrand()));
        response.setType(toTypeResponse(specification.getType()));
        response.setReference(toReferenceResponse(specification.getReference()));
        response.setDimension(specification.getDimension());

        // Especificaciones de rendimiento
        response.setExpectedMileage(specification.getExpectedMileage());
        response.setMileageRangeMin(specification.getMileageRangeMin());
        response.setMileageRangeAvg(specification.getMileageRangeAvg());
        response.setMileageRangeMax(specification.getMileageRangeMax());
        response.setExpectedRetreads(specification.getExpectedRetreads());
        response.setExpectedLossPercentage(specification.getExpectedLossPercentage());
        response.setTotalExpected(specification.getTotalExpected());
        response.setCostPerHour(specification.getCostPerHour());

        // Profundidades iniciales
        response.setInitialDepthInternalMm(specification.getInitialDepthInternalMm());
        response.setInitialDepthCentralMm(specification.getInitialDepthCentralMm());
        response.setInitialDepthExternalMm(specification.getInitialDepthExternalMm());
        response.setAverageInitialDepth(specification.getAverageInitialDepth());

        // Información comercial
        response.setLastPurchaseQuantity(specification.getLastPurchaseQuantity());
        response.setLastPurchaseUnitPrice(specification.getLastPurchaseUnitPrice());
        response.setLastPurchaseDate(specification.getLastPurchaseDate());

        // Proveedores
        response.setMainProvider(toSupplierResponse(specification.getMainProvider()));
        response.setSecondaryProvider(toSupplierResponse(specification.getSecondaryProvider()));
        response.setLastUsedProvider(toSupplierResponse(specification.getLastUsedProvider()));

        // Características físicas
        response.setWeightKg(specification.getWeightKg());

        // Información adicional
        response.setExpectedPerformance(specification.getExpectedPerformance());

        // Estado
        response.setIsActive(specification.getIsActive());

        // Auditoría
        response.setCreatedAt(specification.getCreatedAt());
        response.setCreatedBy(specification.getCreatedBy());
        response.setUpdatedAt(specification.getUpdatedAt());
        response.setUpdatedBy(specification.getUpdatedBy());
        response.setDeletedAt(specification.getDeletedAt());
        response.setDeletedBy(specification.getDeletedBy());

        return response;
    }

    /**
     * Convierte una entidad TireSpecification a un DTO de respuesta resumido
     */
    public TireSpecificationSummaryResponse toSummaryResponse(TireSpecification specification) {
        if (specification == null) return null;

        TireSpecificationSummaryResponse response = new TireSpecificationSummaryResponse();

        response.setId(specification.getId());
        response.setCode(specification.getCode());

        // Información básica de catálogos
        if (specification.getBrand() != null) {
            response.setBrandId(specification.getBrand().getId());
            response.setBrandName(specification.getBrand().getName());
        }

        if (specification.getType() != null) {
            response.setTypeId(specification.getType().getId());
            response.setTypeName(specification.getType().getName());
        }

        if (specification.getReference() != null) {
            response.setReferenceId(specification.getReference().getId());
            response.setReferenceName(specification.getReference().getName());
        }

        response.setDimension(specification.getDimension());

        // Especificaciones clave
        response.setExpectedMileage(specification.getExpectedMileage());
        response.setExpectedRetreads(specification.getExpectedRetreads());
        response.setAverageInitialDepth(specification.getAverageInitialDepth());

        // Estado
        response.setIsActive(specification.getIsActive());

        return response;
    }

    /**
     * Crea una nueva entidad TireSpecification a partir de un DTO de creación
     * (Sin las relaciones, deben ser inyectadas por el servicio)
     */
    public TireSpecification toEntity(CreateTireSpecificationRequest request) {
        if (request == null) return null;

        TireSpecification specification = new TireSpecification();

        // El código será generado automáticamente por el servicio

        // Las relaciones (brand, type, reference, suppliers) deben ser inyectadas
        // por el servicio después de validar que existen

        specification.setDimension(request.getDimension());

        // Especificaciones de rendimiento
        specification.setExpectedMileage(request.getExpectedMileage());
        specification.setMileageRangeMin(request.getMileageRangeMin());
        specification.setMileageRangeAvg(request.getMileageRangeAvg());
        specification.setMileageRangeMax(request.getMileageRangeMax());
        specification.setExpectedRetreads(request.getExpectedRetreads());
        specification.setExpectedLossPercentage(request.getExpectedLossPercentage());
        specification.setTotalExpected(request.getTotalExpected());
        specification.setCostPerHour(request.getCostPerHour());

        // Profundidades iniciales
        specification.setInitialDepthInternalMm(request.getInitialDepthInternalMm());
        specification.setInitialDepthCentralMm(request.getInitialDepthCentralMm());
        specification.setInitialDepthExternalMm(request.getInitialDepthExternalMm());

        // Información comercial
        specification.setLastPurchaseQuantity(request.getLastPurchaseQuantity());
        specification.setLastPurchaseUnitPrice(request.getLastPurchaseUnitPrice());
        specification.setLastPurchaseDate(request.getLastPurchaseDate());

        // Características físicas
        specification.setWeightKg(request.getWeightKg());

        // Información adicional
        specification.setExpectedPerformance(request.getExpectedPerformance());

        // Estado por defecto: activo
        specification.setIsActive(true);

        return specification;
    }

    /**
     * Actualiza una entidad TireSpecification existente con datos del DTO de actualización
     * (Sin las relaciones, deben ser inyectadas por el servicio)
     */
    public void updateEntity(TireSpecification specification, UpdateTireSpecificationRequest request) {
        if (specification == null || request == null) return;

        // Las relaciones (brand, type, reference, suppliers) deben ser inyectadas
        // por el servicio después de validar que existen

        specification.setDimension(request.getDimension());

        // Especificaciones de rendimiento
        specification.setExpectedMileage(request.getExpectedMileage());
        specification.setMileageRangeMin(request.getMileageRangeMin());
        specification.setMileageRangeAvg(request.getMileageRangeAvg());
        specification.setMileageRangeMax(request.getMileageRangeMax());
        specification.setExpectedRetreads(request.getExpectedRetreads());
        specification.setExpectedLossPercentage(request.getExpectedLossPercentage());
        specification.setTotalExpected(request.getTotalExpected());
        specification.setCostPerHour(request.getCostPerHour());

        // Profundidades iniciales
        specification.setInitialDepthInternalMm(request.getInitialDepthInternalMm());
        specification.setInitialDepthCentralMm(request.getInitialDepthCentralMm());
        specification.setInitialDepthExternalMm(request.getInitialDepthExternalMm());

        // Información comercial
        specification.setLastPurchaseQuantity(request.getLastPurchaseQuantity());
        specification.setLastPurchaseUnitPrice(request.getLastPurchaseUnitPrice());
        specification.setLastPurchaseDate(request.getLastPurchaseDate());

        // Características físicas
        specification.setWeightKg(request.getWeightKg());

        // Información adicional
        specification.setExpectedPerformance(request.getExpectedPerformance());
    }
}
