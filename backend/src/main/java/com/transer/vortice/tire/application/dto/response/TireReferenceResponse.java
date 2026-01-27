package com.transer.vortice.tire.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de respuesta para Referencia de Llanta
 *
 * @author Vórtice Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TireReferenceResponse {
    private UUID id;
    private String code;
    private String name;
    private String specifications;
    private Boolean isActive;
}
