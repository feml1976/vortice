package com.transer.vortice.organization.application.mapper;

import com.transer.vortice.organization.application.dto.CreateTireSupplierRequest;
import com.transer.vortice.organization.application.dto.TireSupplierResponse;
import com.transer.vortice.organization.domain.model.Office;
import com.transer.vortice.organization.domain.model.TireSupplier;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad TireSupplier y DTOs.
 *
 * @author Vórtice Development Team
 */
@Component
public class TireSupplierMapper {

    /**
     * Convierte CreateTireSupplierRequest a TireSupplier entity
     *
     * @param request DTO de creación
     * @return Entidad TireSupplier
     */
    public TireSupplier toEntity(CreateTireSupplierRequest request) {
        if (request == null) {
            return null;
        }

        return new TireSupplier(
            request.getCode(),
            request.getName(),
            request.getTaxId(),
            request.getOfficeId(),
            request.getContactName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress()
        );
    }

    /**
     * Convierte TireSupplier entity a TireSupplierResponse DTO
     *
     * @param supplier Entidad TireSupplier
     * @return DTO de respuesta
     */
    public TireSupplierResponse toResponse(TireSupplier supplier) {
        if (supplier == null) {
            return null;
        }

        return TireSupplierResponse.builder()
            .id(supplier.getId())
            .code(supplier.getCode())
            .name(supplier.getName())
            .taxId(supplier.getTaxId())
            .officeId(supplier.getOfficeId())
            .contactName(supplier.getContactName())
            .email(supplier.getEmail())
            .phone(supplier.getPhone())
            .address(supplier.getAddress())
            .isActive(supplier.getIsActive())
            .createdAt(supplier.getCreatedAt())
            .createdBy(supplier.getCreatedBy())
            .updatedAt(supplier.getUpdatedAt())
            .updatedBy(supplier.getUpdatedBy())
            .version(supplier.getVersion())
            .build();
    }

    /**
     * Convierte TireSupplier entity a TireSupplierResponse DTO con información de la oficina
     *
     * @param supplier Entidad TireSupplier
     * @param office Entidad Office
     * @return DTO de respuesta con información de oficina
     */
    public TireSupplierResponse toResponseWithOffice(TireSupplier supplier, Office office) {
        TireSupplierResponse response = toResponse(supplier);
        if (response != null && office != null) {
            response.setOfficeCode(office.getCode());
            response.setOfficeName(office.getName());
        }
        return response;
    }
}
