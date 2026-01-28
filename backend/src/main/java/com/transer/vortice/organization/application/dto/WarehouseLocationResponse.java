package com.transer.vortice.organization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para una ubicación.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocationResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private UUID warehouseId;
    private Boolean isActive;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Long version;

    // Información del almacén (opcional, para evitar queries adicionales)
    private String warehouseCode;
    private String warehouseName;

    // Información de la oficina (opcional)
    private UUID officeId;
    private String officeCode;
    private String officeName;
}
