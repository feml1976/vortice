package com.transer.vortice.organization.application.mapper;

import com.transer.vortice.organization.application.dto.CreateWarehouseRequest;
import com.transer.vortice.organization.application.dto.WarehouseResponse;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.Warehouse;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad Warehouse y DTOs.
 *
 * @author Vórtice Development Team
 */
@Component
public class WarehouseMapper {

    /**
     * Convierte CreateWarehouseRequest a Warehouse entity
     *
     * @param request DTO de creación
     * @return Entidad Warehouse
     */
    public Warehouse toEntity(CreateWarehouseRequest request) {
        if (request == null) {
            return null;
        }

        return new Warehouse(
            request.getCode(),
            request.getName(),
            request.getOfficeId(),
            request.getDescription()
        );
    }

    /**
     * Convierte Warehouse entity a WarehouseResponse DTO
     *
     * @param warehouse Entidad Warehouse
     * @return DTO de respuesta
     */
    public WarehouseResponse toResponse(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }

        return WarehouseResponse.builder()
            .id(warehouse.getId())
            .code(warehouse.getCode())
            .name(warehouse.getName())
            .description(warehouse.getDescription())
            .officeId(warehouse.getOfficeId())
            .isActive(warehouse.getIsActive())
            .createdAt(warehouse.getCreatedAt())
            .createdBy(warehouse.getCreatedBy())
            .updatedAt(warehouse.getUpdatedAt())
            .updatedBy(warehouse.getUpdatedBy())
            .version(warehouse.getVersion())
            .build();
    }

    /**
     * Convierte Warehouse entity a WarehouseResponse DTO con información de la oficina
     *
     * @param warehouse Entidad Warehouse
     * @param office Entidad Office
     * @return DTO de respuesta con información de oficina
     */
    public WarehouseResponse toResponseWithOffice(Warehouse warehouse, Office office) {
        WarehouseResponse response = toResponse(warehouse);
        if (response != null && office != null) {
            response.setOfficeCode(office.getCode());
            response.setOfficeName(office.getName());
        }
        return response;
    }

    /**
     * Convierte Warehouse entity a WarehouseResponse DTO con información adicional
     *
     * @param warehouse Entidad Warehouse
     * @param office Entidad Office
     * @param totalLocations Total de ubicaciones
     * @return DTO de respuesta con información adicional
     */
    public WarehouseResponse toResponseWithDetails(Warehouse warehouse, Office office, Long totalLocations) {
        WarehouseResponse response = toResponseWithOffice(warehouse, office);
        if (response != null) {
            response.setTotalLocations(totalLocations);
        }
        return response;
    }
}
