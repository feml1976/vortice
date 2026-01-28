package com.transer.vortice.organization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para un almacén.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private UUID officeId;
    private Boolean isActive;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Long version;

    // Información de la oficina (opcional, para evitar queries adicionales)
    private String officeCode;
    private String officeName;

    // Información adicional (opcional, calculada)
    private Long totalLocations;
}
