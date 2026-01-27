package com.transer.vortice.tire.presentation.controller;

import com.transer.vortice.tire.application.dto.response.TireBrandResponse;
import com.transer.vortice.tire.application.dto.response.TireReferenceResponse;
import com.transer.vortice.tire.application.dto.response.TireSupplierResponse;
import com.transer.vortice.tire.application.dto.response.TireTypeResponse;
import com.transer.vortice.tire.application.service.TireCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para Catálogos del Módulo de Llantas
 *
 * Proporciona endpoints para obtener catálogos base (marcas, tipos, referencias, proveedores)
 * utilizados en formularios y filtros.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/tire-catalogs")
@RequiredArgsConstructor
@Tag(name = "Catálogos de Llantas", description = "Endpoints para obtener catálogos base del módulo de llantas")
@SecurityRequirement(name = "bearerAuth")
public class TireCatalogController {

    private final TireCatalogService tireCatalogService;

    // =====================================================
    // MARCAS DE LLANTAS
    // =====================================================

    /**
     * Obtiene todas las marcas activas
     */
    @GetMapping("/brands")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar marcas activas",
            description = "Retorna la lista de todas las marcas de llantas activas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireBrandResponse>> getAllActiveBrands() {
        log.info("Request GET /api/v1/tire-catalogs/brands");

        List<TireBrandResponse> brands = tireCatalogService.getAllActiveBrands();

        log.info("Marcas activas obtenidas: {} elementos", brands.size());

        return ResponseEntity.ok(brands);
    }

    /**
     * Obtiene todas las marcas (activas e inactivas)
     */
    @GetMapping("/brands/all")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar todas las marcas",
            description = "Retorna la lista de todas las marcas de llantas (activas e inactivas)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireBrandResponse>> getAllBrands() {
        log.info("Request GET /api/v1/tire-catalogs/brands/all");

        List<TireBrandResponse> brands = tireCatalogService.getAllBrands();

        log.info("Todas las marcas obtenidas: {} elementos", brands.size());

        return ResponseEntity.ok(brands);
    }

    // =====================================================
    // TIPOS DE LLANTAS
    // =====================================================

    /**
     * Obtiene todos los tipos activos
     */
    @GetMapping("/types")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar tipos activos",
            description = "Retorna la lista de todos los tipos de llantas activos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireTypeResponse>> getAllActiveTypes() {
        log.info("Request GET /api/v1/tire-catalogs/types");

        List<TireTypeResponse> types = tireCatalogService.getAllActiveTypes();

        log.info("Tipos activos obtenidos: {} elementos", types.size());

        return ResponseEntity.ok(types);
    }

    /**
     * Obtiene todos los tipos (activos e inactivos)
     */
    @GetMapping("/types/all")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar todos los tipos",
            description = "Retorna la lista de todos los tipos de llantas (activos e inactivos)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireTypeResponse>> getAllTypes() {
        log.info("Request GET /api/v1/tire-catalogs/types/all");

        List<TireTypeResponse> types = tireCatalogService.getAllTypes();

        log.info("Todos los tipos obtenidos: {} elementos", types.size());

        return ResponseEntity.ok(types);
    }

    // =====================================================
    // REFERENCIAS DE LLANTAS
    // =====================================================

    /**
     * Obtiene todas las referencias activas
     */
    @GetMapping("/references")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar referencias activas",
            description = "Retorna la lista de todas las referencias de llantas activas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireReferenceResponse>> getAllActiveReferences() {
        log.info("Request GET /api/v1/tire-catalogs/references");

        List<TireReferenceResponse> references = tireCatalogService.getAllActiveReferences();

        log.info("Referencias activas obtenidas: {} elementos", references.size());

        return ResponseEntity.ok(references);
    }

    /**
     * Obtiene todas las referencias (activas e inactivas)
     */
    @GetMapping("/references/all")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar todas las referencias",
            description = "Retorna la lista de todas las referencias de llantas (activas e inactivas)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireReferenceResponse>> getAllReferences() {
        log.info("Request GET /api/v1/tire-catalogs/references/all");

        List<TireReferenceResponse> references = tireCatalogService.getAllReferences();

        log.info("Todas las referencias obtenidas: {} elementos", references.size());

        return ResponseEntity.ok(references);
    }

    // =====================================================
    // PROVEEDORES DE LLANTAS
    // =====================================================

    /**
     * Obtiene todos los proveedores activos
     */
    @GetMapping("/suppliers")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar proveedores activos",
            description = "Retorna la lista de todos los proveedores de llantas activos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireSupplierResponse>> getAllActiveSuppliers() {
        log.info("Request GET /api/v1/tire-catalogs/suppliers");

        List<TireSupplierResponse> suppliers = tireCatalogService.getAllActiveSuppliers();

        log.info("Proveedores activos obtenidos: {} elementos", suppliers.size());

        return ResponseEntity.ok(suppliers);
    }

    /**
     * Obtiene todos los proveedores (activos e inactivos)
     */
    @GetMapping("/suppliers/all")
    @PreAuthorize("hasAuthority('TIRE_SPECIFICATION_VIEW')")
    @Operation(
            summary = "Listar todos los proveedores",
            description = "Retorna la lista de todos los proveedores de llantas (activos e inactivos)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<List<TireSupplierResponse>> getAllSuppliers() {
        log.info("Request GET /api/v1/tire-catalogs/suppliers/all");

        List<TireSupplierResponse> suppliers = tireCatalogService.getAllSuppliers();

        log.info("Todos los proveedores obtenidos: {} elementos", suppliers.size());

        return ResponseEntity.ok(suppliers);
    }
}
