package com.transer.vortice.tire.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de respuesta resumido para Especificación Técnica de Llanta
 *
 * Utilizado en listados y búsquedas para optimizar la transferencia de datos.
 * Contiene solo los campos más relevantes para mostrar en tablas.
 *
 * @author Vórtice Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TireSpecificationSummaryResponse {

    private UUID id;
    private String code;

    // Información básica de catálogos (solo ID y nombre)
    private UUID brandId;
    private String brandName;

    private UUID typeId;
    private String typeName;

    private UUID referenceId;
    private String referenceName;

    private String dimension;

    // Especificaciones clave
    private Integer expectedMileage;
    private Short expectedRetreads;

    // Profundidades (solo promedio)
    private BigDecimal averageInitialDepth;

    // Estado
    private Boolean isActive;
}
