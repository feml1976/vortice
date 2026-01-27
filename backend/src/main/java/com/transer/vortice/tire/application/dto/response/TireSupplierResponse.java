package com.transer.vortice.tire.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de respuesta para Proveedor de Llantas
 *
 * @author Vórtice Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TireSupplierResponse {
    private UUID id;
    private String code;
    private String name;
    private String taxId;
    private String phone;
    private String contactName;
    private Boolean isActive;
}
