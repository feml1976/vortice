package com.transer.vortice.tire.domain.model;

import com.transer.vortice.shared.domain.entity.AuditableUUIDEntity;
import com.transer.vortice.tire.domain.model.catalog.TireBrand;
import com.transer.vortice.tire.domain.model.catalog.TireReference;
import com.transer.vortice.tire.domain.model.catalog.TireSupplier;
import com.transer.vortice.tire.domain.model.catalog.TireType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Entidad de dominio: Especificación Técnica de Llantas (Ficha Técnica)
 *
 * Representa las especificaciones técnicas, características de rendimiento
 * e información comercial de un tipo específico de llanta.
 *
 * Esta es una entidad maestra (catálogo) que sirve como base para todas
 * las llantas individuales del inventario.
 *
 * @author Vórtice Development Team
 */
@Entity
@Table(name = "technical_specifications", schema = "tire_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TireSpecification extends AuditableUUIDEntity {

    // =====================================================
    // IDENTIFICACIÓN Y CLASIFICACIÓN
    // =====================================================

    @NotBlank(message = "El código de la especificación es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @NotNull(message = "La marca es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private TireBrand brand;

    @NotNull(message = "El tipo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private TireType type;

    @NotNull(message = "La referencia es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reference_id", nullable = false)
    private TireReference reference;

    @Size(max = 20, message = "La dimensión no puede superar los 20 caracteres")
    @Column(name = "dimension", length = 20)
    private String dimension;

    // =====================================================
    // ESPECIFICACIONES DE RENDIMIENTO
    // =====================================================

    @NotNull(message = "El kilometraje esperado es obligatorio")
    @Min(value = 1, message = "El kilometraje esperado debe ser mayor a 0")
    @Column(name = "expected_mileage", nullable = false)
    private Integer expectedMileage;

    @Min(value = 0, message = "El kilometraje mínimo debe ser mayor o igual a 0")
    @Column(name = "mileage_range_min")
    private Integer mileageRangeMin;

    @Min(value = 0, message = "El kilometraje promedio debe ser mayor o igual a 0")
    @Column(name = "mileage_range_avg")
    private Integer mileageRangeAvg;

    @Min(value = 0, message = "El kilometraje máximo debe ser mayor o igual a 0")
    @Column(name = "mileage_range_max")
    private Integer mileageRangeMax;

    @NotNull(message = "El número de reencauches esperados es obligatorio")
    @Min(value = 0, message = "El número de reencauches debe ser mayor o igual a 0")
    @Column(name = "expected_retreads", nullable = false)
    private Short expectedRetreads = 0;

    @DecimalMin(value = "0.0", message = "El porcentaje de pérdida debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje de pérdida debe ser menor o igual a 100")
    @Digits(integer = 3, fraction = 2, message = "El porcentaje de pérdida debe tener máximo 3 enteros y 2 decimales")
    @Column(name = "expected_loss_percentage", precision = 5, scale = 2)
    private BigDecimal expectedLossPercentage;

    @Column(name = "total_expected")
    private Integer totalExpected;

    @DecimalMin(value = "0.0", message = "El costo por hora debe ser mayor o igual a 0")
    @Digits(integer = 8, fraction = 2, message = "El costo por hora debe tener máximo 8 enteros y 2 decimales")
    @Column(name = "cost_per_hour", precision = 10, scale = 2)
    private BigDecimal costPerHour;

    // =====================================================
    // PROFUNDIDADES INICIALES (en milímetros)
    // =====================================================

    @NotNull(message = "La profundidad inicial interna es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad inicial interna debe estar entre 0 y 99.9 mm")
    @DecimalMax(value = "99.9", message = "La profundidad inicial interna debe estar entre 0 y 99.9 mm")
    @Digits(integer = 2, fraction = 1, message = "La profundidad debe tener máximo 2 enteros y 1 decimal")
    @Column(name = "initial_depth_internal_mm", nullable = false, precision = 4, scale = 1)
    private BigDecimal initialDepthInternalMm;

    @NotNull(message = "La profundidad inicial central es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad inicial central debe estar entre 0 y 99.9 mm")
    @DecimalMax(value = "99.9", message = "La profundidad inicial central debe estar entre 0 y 99.9 mm")
    @Digits(integer = 2, fraction = 1, message = "La profundidad debe tener máximo 2 enteros y 1 decimal")
    @Column(name = "initial_depth_central_mm", nullable = false, precision = 4, scale = 1)
    private BigDecimal initialDepthCentralMm;

    @NotNull(message = "La profundidad inicial externa es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad inicial externa debe estar entre 0 y 99.9 mm")
    @DecimalMax(value = "99.9", message = "La profundidad inicial externa debe estar entre 0 y 99.9 mm")
    @Digits(integer = 2, fraction = 1, message = "La profundidad debe tener máximo 2 enteros y 1 decimal")
    @Column(name = "initial_depth_external_mm", nullable = false, precision = 4, scale = 1)
    private BigDecimal initialDepthExternalMm;

    // =====================================================
    // INFORMACIÓN COMERCIAL - ÚLTIMA COMPRA
    // =====================================================

    @Min(value = 1, message = "La cantidad de última compra debe ser mayor a 0")
    @Column(name = "last_purchase_quantity")
    private Integer lastPurchaseQuantity;

    @DecimalMin(value = "0.0", message = "El precio unitario debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 enteros y 2 decimales")
    @Column(name = "last_purchase_unit_price", precision = 12, scale = 2)
    private BigDecimal lastPurchaseUnitPrice;

    @Column(name = "last_purchase_date")
    private LocalDate lastPurchaseDate;

    // =====================================================
    // PROVEEDORES
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_supplier_id")
    private TireSupplier mainProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_supplier_id")
    private TireSupplier secondaryProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_used_supplier_id")
    private TireSupplier lastUsedProvider;

    // =====================================================
    // CARACTERÍSTICAS FÍSICAS
    // =====================================================

    @Min(value = 0, message = "El peso debe ser mayor o igual a 0")
    @Column(name = "weight_kg")
    private Integer weightKg;

    // =====================================================
    // INFORMACIÓN ADICIONAL (JSON)
    // =====================================================

    /**
     * Datos adicionales de rendimiento en formato JSON
     * Ejemplo: {"cost_per_km": 45.50, "pressure_range": {"min": 100, "max": 120}}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "expected_performance", columnDefinition = "jsonb")
    private Map<String, Object> expectedPerformance;

    // =====================================================
    // ESTADO
    // =====================================================

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // =====================================================
    // MÉTODOS DE NEGOCIO
    // =====================================================

    /**
     * Calcula la profundidad inicial promedio
     */
    public BigDecimal getAverageInitialDepth() {
        if (initialDepthInternalMm == null || initialDepthCentralMm == null || initialDepthExternalMm == null) {
            return BigDecimal.ZERO;
        }
        return initialDepthInternalMm
                .add(initialDepthCentralMm)
                .add(initialDepthExternalMm)
                .divide(BigDecimal.valueOf(3), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Activa la especificación técnica
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Desactiva la especificación técnica
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Verifica si la especificación puede ser eliminada
     * (No debe tener llantas asociadas en el sistema)
     */
    public boolean canBeDeleted() {
        // Esta validación se debe hacer en el servicio consultando las tablas relacionadas
        return !isDeleted();
    }

    /**
     * Actualiza la información de última compra
     */
    public void updateLastPurchaseInfo(Integer quantity, BigDecimal unitPrice, LocalDate date, TireSupplier supplier) {
        this.lastPurchaseQuantity = quantity;
        this.lastPurchaseUnitPrice = unitPrice;
        this.lastPurchaseDate = date;
        this.lastUsedProvider = supplier;
    }

    /**
     * Obtiene el código completo de la especificación (para display)
     */
    public String getFullCode() {
        return code + " - " + (dimension != null ? dimension : "N/A");
    }

    /**
     * Valida que los rangos de kilometraje sean coherentes
     */
    public boolean validateMileageRanges() {
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
}
