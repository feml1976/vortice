package com.transer.vortice.organization.application.mapper;

import com.transer.vortice.organization.application.dto.CreateOfficeRequest;
import com.transer.vortice.organization.application.dto.OfficeResponse;
import com.transer.vortice.organization.domain.model.Office;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad Office y DTOs.
 *
 * @author Vórtice Development Team
 */
@Component
public class OfficeMapper {

    /**
     * Convierte CreateOfficeRequest a Office entity
     *
     * @param request DTO de creación
     * @return Entidad Office
     */
    public Office toEntity(CreateOfficeRequest request) {
        if (request == null) {
            return null;
        }

        return new Office(
            request.getCode(),
            request.getName(),
            request.getCity(),
            request.getAddress(),
            request.getPhone()
        );
    }

    /**
     * Convierte Office entity a OfficeResponse DTO
     *
     * @param office Entidad Office
     * @return DTO de respuesta
     */
    public OfficeResponse toResponse(Office office) {
        if (office == null) {
            return null;
        }

        return OfficeResponse.builder()
            .id(office.getId())
            .code(office.getCode())
            .name(office.getName())
            .city(office.getCity())
            .address(office.getAddress())
            .phone(office.getPhone())
            .isActive(office.getIsActive())
            .createdAt(office.getCreatedAt())
            .createdBy(office.getCreatedBy())
            .updatedAt(office.getUpdatedAt())
            .updatedBy(office.getUpdatedBy())
            .version(office.getVersion())
            .build();
    }

    /**
     * Convierte Office entity a OfficeResponse DTO con información adicional
     *
     * @param office Entidad Office
     * @param totalWarehouses Total de almacenes
     * @param totalSuppliers Total de proveedores
     * @param totalUsers Total de usuarios
     * @return DTO de respuesta con información adicional
     */
    public OfficeResponse toResponseWithDetails(Office office, Long totalWarehouses, Long totalSuppliers, Long totalUsers) {
        OfficeResponse response = toResponse(office);
        if (response != null) {
            response.setTotalWarehouses(totalWarehouses);
            response.setTotalSuppliers(totalSuppliers);
            response.setTotalUsers(totalUsers);
        }
        return response;
    }
}
