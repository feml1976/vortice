package com.transer.vortice.organization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para un proveedor de llantas.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TireSupplierResponse {

    private UUID id;
    private String code;
    private String name;
    private String taxId;
    private UUID officeId;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Long version;

    // Información de la oficina (opcional, para evitar queries adicionales)
    private String officeCode;
    private String officeName;
}
