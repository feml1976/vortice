package com.transer.vortice.organization.application.mapper;

import com.transer.vortice.organization.application.dto.CreateWarehouseLocationRequest;
import com.transer.vortice.organization.application.dto.WarehouseLocationResponse;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.Warehouse;
import com.transer.vortice.organization.domain.model.WarehouseLocation;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad WarehouseLocation y DTOs.
 *
 * @author Vórtice Development Team
 */
@Component
public class WarehouseLocationMapper {

    /**
     * Convierte CreateWarehouseLocationRequest a WarehouseLocation entity
     *
     * @param request DTO de creación
     * @return Entidad WarehouseLocation
     */
    public WarehouseLocation toEntity(CreateWarehouseLocationRequest request) {
        if (request == null) {
            return null;
        }

        return new WarehouseLocation(
            request.getCode(),
            request.getName(),
            request.getWarehouseId(),
            request.getDescription()
        );
    }

    /**
     * Convierte WarehouseLocation entity a WarehouseLocationResponse DTO
     *
     * @param location Entidad WarehouseLocation
     * @return DTO de respuesta
     */
    public WarehouseLocationResponse toResponse(WarehouseLocation location) {
        if (location == null) {
            return null;
        }

        return WarehouseLocationResponse.builder()
            .id(location.getId())
            .code(location.getCode())
            .name(location.getName())
            .description(location.getDescription())
            .warehouseId(location.getWarehouseId())
            .isActive(location.getIsActive())
            .createdAt(location.getCreatedAt())
            .createdBy(location.getCreatedBy())
            .updatedAt(location.getUpdatedAt())
            .updatedBy(location.getUpdatedBy())
            .version(location.getVersion())
            .build();
    }

    /**
     * Convierte WarehouseLocation entity a WarehouseLocationResponse DTO con información del almacén
     *
     * @param location Entidad WarehouseLocation
     * @param warehouse Entidad Warehouse
     * @return DTO de respuesta con información del almacén
     */
    public WarehouseLocationResponse toResponseWithWarehouse(WarehouseLocation location, Warehouse warehouse) {
        WarehouseLocationResponse response = toResponse(location);
        if (response != null && warehouse != null) {
            response.setWarehouseCode(warehouse.getCode());
            response.setWarehouseName(warehouse.getName());
            response.setOfficeId(warehouse.getOfficeId());
        }
        return response;
    }

    /**
     * Convierte WarehouseLocation entity a WarehouseLocationResponse DTO con información completa
     *
     * @param location Entidad WarehouseLocation
     * @param warehouse Entidad Warehouse
     * @param office Entidad Office
     * @return DTO de respuesta con información completa de almacén y oficina
     */
    public WarehouseLocationResponse toResponseWithDetails(WarehouseLocation location, Warehouse warehouse, Office office) {
        WarehouseLocationResponse response = toResponseWithWarehouse(location, warehouse);
        if (response != null && office != null) {
            response.setOfficeCode(office.getCode());
            response.setOfficeName(office.getName());
        }
        return response;
    }
}
