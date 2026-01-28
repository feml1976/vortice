package com.transer.vortice.organization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para una oficina.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeResponse {

    private UUID id;
    private String code;
    private String name;
    private String city;
    private String address;
    private String phone;
    private Boolean isActive;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Long version;

    // Información adicional (opcional, calculada)
    private Long totalWarehouses;
    private Long totalSuppliers;
    private Long totalUsers;
}
