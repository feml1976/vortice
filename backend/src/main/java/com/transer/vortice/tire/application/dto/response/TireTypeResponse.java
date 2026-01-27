package com.transer.vortice.tire.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de respuesta para Tipo de Llanta
 *
 * @author Vórtice Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TireTypeResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Boolean isActive;
}
