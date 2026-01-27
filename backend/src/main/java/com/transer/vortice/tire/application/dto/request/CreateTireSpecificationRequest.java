package com.transer.vortice.tire.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * DTO de request para crear una nueva Especificación Técnica de Llanta
 *
 * @author Vórtice Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTireSpecificationRequest {

    // El código será generado automáticamente (autoincremental)
    // No se incluye en el request

    // Relaciones (IDs de catálogos)
    @NotNull(message = "El ID de la marca es obligatorio")
    private UUID brandId;

    @NotNull(message = "El ID del tipo es obligatorio")
    private UUID typeId;

    @NotNull(message = "El ID de la referencia es obligatorio")
    private UUID referenceId;

    @Size(max = 20, message = "La dimensión no puede superar los 20 caracteres")
    private String dimension;

    // Especificaciones de rendimiento
    @NotNull(message = "El kilometraje esperado es obligatorio")
    @Min(value = 1, message = "El kilometraje esperado debe ser mayor a 0")
    private Integer expectedMileage;

    @Min(value = 0, message = "El kilometraje mínimo debe ser mayor o igual a 0")
    private Integer mileageRangeMin;

    @Min(value = 0, message = "El kilometraje promedio debe ser mayor o igual a 0")
    private Integer mileageRangeAvg;

    @Min(value = 0, message = "El kilometraje máximo debe ser mayor o igual a 0")
    private Integer mileageRangeMax;

    @NotNull(message = "El número de reencauches esperados es obligatorio")
    @Min(value = 0, message = "El número de reencauches debe ser mayor o igual a 0")
    private Short expectedRetreads;

    @DecimalMin(value = "0.0", message = "El porcentaje de pérdida debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje de pérdida debe ser menor o igual a 100")
    private BigDecimal expectedLossPercentage;

    private Integer totalExpected;

    @DecimalMin(value = "0.0", message = "El costo por hora debe ser mayor o igual a 0")
    private BigDecimal costPerHour;

    // Profundidades iniciales (en mm)
    @NotNull(message = "La profundidad inicial interna es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad inicial interna debe estar entre 0 y 99.9 mm")
    @DecimalMax(value = "99.9", message = "La profundidad inicial interna debe estar entre 0 y 99.9 mm")
    private BigDecimal initialDepthInternalMm;

    @NotNull(message = "La profundidad inicial central es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad inicial central debe estar entre 0 y 99.9 mm")
    @DecimalMax(value = "99.9", message = "La profundidad inicial central debe estar entre 0 y 99.9 mm")
    private BigDecimal initialDepthCentralMm;

    @NotNull(message = "La profundidad inicial externa es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad inicial externa debe estar entre 0 y 99.9 mm")
    @DecimalMax(value = "99.9", message = "La profundidad inicial externa debe estar entre 0 y 99.9 mm")
    private BigDecimal initialDepthExternalMm;

    // Información comercial - Última compra
    @Min(value = 1, message = "La cantidad de última compra debe ser mayor a 0")
    private Integer lastPurchaseQuantity;

    @DecimalMin(value = "0.0", message = "El precio unitario debe ser mayor o igual a 0")
    private BigDecimal lastPurchaseUnitPrice;

    private LocalDate lastPurchaseDate;

    // Proveedores (IDs)
    private UUID mainProviderId;
    private UUID secondaryProviderId;
    private UUID lastUsedProviderId;

    // Características físicas
    @Min(value = 0, message = "El peso debe ser mayor o igual a 0")
    private Integer weightKg;

    // Información adicional (JSON)
    private Map<String, Object> expectedPerformance;

    // Validación personalizada: rangos de kilometraje coherentes
    @AssertTrue(message = "Los rangos de kilometraje deben ser coherentes (min <= avg <= max)")
    public boolean isValidMileageRanges() {
        if (mileageRangeMin != null && mileageRangeAvg != null && mileageRangeMin > mileageRangeAvg) {
            return false;
        }
        if (mileageRangeAvg != null && mileageRangeMax != null && mileageRangeAvg > mileageRangeMax) {
            return false;
        }
        if (mileageRangeMin != null && mileageRangeMax != null && mileageRangeMin > mileageRangeMax) {
            return false;
        }
        return true;
    }

    // Validación personalizada: información de última compra completa
    @AssertTrue(message = "Si se proporciona información de última compra, todos los campos (cantidad, precio, fecha) deben estar completos")
    public boolean isValidLastPurchaseInfo() {
        int nullCount = 0;
        int nonNullCount = 0;

        if (lastPurchaseQuantity == null) nullCount++; else nonNullCount++;
        if (lastPurchaseUnitPrice == null) nullCount++; else nonNullCount++;
        if (lastPurchaseDate == null) nullCount++; else nonNullCount++;

        // Todos nulos o todos no nulos está bien, mezcla no
        return nullCount == 0 || nullCount == 3;
    }
}
