package com.transer.vortice.tire.presentation.controller;

import com.transer.vortice.tire.application.dto.request.CreateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.request.UpdateTireSpecificationRequest;
import com.transer.vortice.tire.application.dto.response.TireSpecificationResponse;
import com.transer.vortice.tire.application.dto.response.TireSpecificationSummaryResponse;
import com.transer.vortice.tire.application.service.TireSpecificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para Especificaciones Técnicas de Llantas
 *
 * Proporciona endpoints para gestionar las fichas técnicas (catálogo maestro de llantas).
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/tire-specifications")
@RequiredArgsConstructor
@Tag(name = "Especificaciones Técnicas", description = "Endpoints para gestión de fichas técnicas de llantas")
@SecurityRequirement(name = "bearerAuth")
public class TireSpecificationController {

    private final TireSpecificationService tireSpecificationService;

    // =====================================================
    // CREATE
    // =====================================================

    /**
     * Crea una nueva especificación técnica de llanta
     */
    @PostMapping
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_CREATE')")
    @Operation(
            summary = "Crear especificación técnica",
            description = "Crea una nueva ficha técnica de llanta con todas sus especificaciones. El código se genera automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Especificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para crear especificaciones"),
            @ApiResponse(responseCode = "404", description = "Marca, tipo, referencia o proveedor no encontrado"),
            @ApiResponse(responseCode = "422", description = "Error de validación de negocio")
    })
    public ResponseEntity<TireSpecificationResponse> createTireSpecification(
            @Valid @RequestBody CreateTireSpecificationRequest request) {
        log.info("Request POST /api/v1/tire-specifications");

        TireSpecificationResponse response = tireSpecificationService.createTireSpecification(request);

        log.info("Especificación técnica creada exitosamente con código: {}", response.getCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =====================================================
    // READ - LIST
    // =====================================================

    /**
     * Lista todas las especificaciones técnicas con paginación
     */
    @GetMapping
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar especificaciones técnicas",
            description = "Retorna una lista paginada de especificaciones técnicas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para ver especificaciones")
    })
    public ResponseEntity<Page<TireSpecificationSummaryResponse>> listTireSpecifications(
            @Parameter(description = "Número de página (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "code") String sort,

            @Parameter(description = "Dirección de ordenamiento (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Request GET /api/v1/tire-specifications?page={}&size={}&sort={}&direction={}",
                page, size, sort, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<TireSpecificationSummaryResponse> response = tireSpecificationService.listTireSpecifications(pageable);

        log.info("Lista de especificaciones obtenida: {} elementos en página {}", response.getNumberOfElements(), page);

        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas las especificaciones técnicas activas sin paginación
     */
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar especificaciones activas",
            description = "Retorna una lista completa (sin paginación) de especificaciones técnicas activas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para ver especificaciones")
    })
    public ResponseEntity<List<TireSpecificationSummaryResponse>> listActiveTireSpecifications() {
        log.info("Request GET /api/v1/tire-specifications/active");

        List<TireSpecificationSummaryResponse> response = tireSpecificationService.listActiveTireSpecifications();

        log.info("Lista de especificaciones activas obtenida: {} elementos", response.size());

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // READ - GET BY ID
    // =====================================================

    /**
     * Obtiene una especificación técnica por su ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Obtener especificación por ID",
            description = "Retorna los datos completos de una especificación técnica específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especificación encontrada"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para ver especificaciones"),
            @ApiResponse(responseCode = "404", description = "Especificación no encontrada")
    })
    public ResponseEntity<TireSpecificationResponse> getTireSpecificationById(
            @Parameter(description = "ID de la especificación técnica")
            @PathVariable UUID id) {
        log.info("Request GET /api/v1/tire-specifications/{}", id);

        TireSpecificationResponse response = tireSpecificationService.getTireSpecificationById(id);

        log.info("Especificación técnica obtenida: {}", response.getCode());

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    /**
     * Actualiza una especificación técnica existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_UPDATE')")
    @Operation(
            summary = "Actualizar especificación técnica",
            description = "Actualiza los datos de una especificación técnica existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especificación actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para actualizar especificaciones"),
            @ApiResponse(responseCode = "404", description = "Especificación, marca, tipo, referencia o proveedor no encontrado"),
            @ApiResponse(responseCode = "422", description = "Error de validación de negocio")
    })
    public ResponseEntity<TireSpecificationResponse> updateTireSpecification(
            @Parameter(description = "ID de la especificación técnica")
            @PathVariable UUID id,

            @Valid @RequestBody UpdateTireSpecificationRequest request) {
        log.info("Request PUT /api/v1/tire-specifications/{}", id);

        TireSpecificationResponse response = tireSpecificationService.updateTireSpecification(id, request);

        log.info("Especificación técnica actualizada exitosamente: {}", response.getCode());

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // DELETE
    // =====================================================

    /**
     * Elimina una especificación técnica (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_DELETE')")
    @Operation(
            summary = "Eliminar especificación técnica",
            description = "Elimina (soft delete) una especificación técnica. Solo se permite si no tiene llantas asociadas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Especificación eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para eliminar especificaciones"),
            @ApiResponse(responseCode = "404", description = "Especificación no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar: tiene llantas asociadas")
    })
    public ResponseEntity<Void> deleteTireSpecification(
            @Parameter(description = "ID de la especificación técnica")
            @PathVariable UUID id) {
        log.info("Request DELETE /api/v1/tire-specifications/{}", id);

        tireSpecificationService.deleteTireSpecification(id);

        log.info("Especificación técnica eliminada exitosamente: {}", id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // SEARCH
    // =====================================================

    /**
     * Busca especificaciones técnicas por texto libre
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Buscar especificaciones técnicas",
            description = "Busca especificaciones técnicas por texto libre en código, dimensión, marca, tipo o referencia"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para ver especificaciones")
    })
    public ResponseEntity<Page<TireSpecificationSummaryResponse>> searchTireSpecifications(
            @Parameter(description = "Texto a buscar")
            @RequestParam String query,

            @Parameter(description = "Número de página (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Request GET /api/v1/tire-specifications/search?query={}", query);

        Pageable pageable = PageRequest.of(page, size);
        Page<TireSpecificationSummaryResponse> response = tireSpecificationService.searchTireSpecifications(query, pageable);

        log.info("Búsqueda completada: {} resultados encontrados", response.getTotalElements());

        return ResponseEntity.ok(response);
    }

    /**
     * Busca especificaciones técnicas con filtros múltiples
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Filtrar especificaciones técnicas",
            description = "Busca especificaciones técnicas aplicando múltiples filtros opcionales"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtrado realizado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para ver especificaciones")
    })
    public ResponseEntity<Page<TireSpecificationSummaryResponse>> filterTireSpecifications(
            @Parameter(description = "ID de la marca (opcional)")
            @RequestParam(required = false) UUID brandId,

            @Parameter(description = "ID del tipo (opcional)")
            @RequestParam(required = false) UUID typeId,

            @Parameter(description = "ID de la referencia (opcional)")
            @RequestParam(required = false) UUID referenceId,

            @Parameter(description = "Estado activo (opcional)")
            @RequestParam(required = false) Boolean isActive,

            @Parameter(description = "Número de página (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "code") String sort,

            @Parameter(description = "Dirección de ordenamiento (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Request GET /api/v1/tire-specifications/filter?brandId={}&typeId={}&referenceId={}&isActive={}",
                brandId, typeId, referenceId, isActive);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<TireSpecificationSummaryResponse> response = tireSpecificationService.findByFilters(
                brandId, typeId, referenceId, isActive, pageable
        );

        log.info("Filtrado completado: {} resultados encontrados", response.getTotalElements());

        return ResponseEntity.ok(response);
    }
}
