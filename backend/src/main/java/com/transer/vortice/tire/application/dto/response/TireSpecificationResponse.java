package com.transer.vortice.tire.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * DTO de respuesta completo para Especificación Técnica de Llanta
 *
 * Contiene toda la información de la ficha técnica incluyen do relaciones y auditoría.
 *
 * @author Vórtice Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TireSpecificationResponse {

    // Identificación
    private UUID id;
    private String code;

    // Relaciones (catálogos)
    private TireBrandResponse brand;
    private TireTypeResponse type;
    private TireReferenceResponse reference;
    private String dimension;

    // Especificaciones de rendimiento
    private Integer expectedMileage;
    private Integer mileageRangeMin;
    private Integer mileageRangeAvg;
    private Integer mileageRangeMax;
    private Short expectedRetreads;
    private BigDecimal expectedLossPercentage;
    private Integer totalExpected;
    private BigDecimal costPerHour;

    // Profundidades iniciales (en mm)
    private BigDecimal initialDepthInternalMm;
    private BigDecimal initialDepthCentralMm;
    private BigDecimal initialDepthExternalMm;
    private BigDecimal averageInitialDepth; // Campo calculado

    // Información comercial - Última compra
    private Integer lastPurchaseQuantity;
    private BigDecimal lastPurchaseUnitPrice;
    private LocalDate lastPurchaseDate;

    // Proveedores
    private TireSupplierResponse mainProvider;
    private TireSupplierResponse secondaryProvider;
    private TireSupplierResponse lastUsedProvider;

    // Características físicas
    private Integer weightKg;

    // Información adicional
    private Map<String, Object> expectedPerformance;

    // Estado
    private Boolean isActive;

    // Auditoría
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Instant deletedAt;
    private Long deletedBy;
}
