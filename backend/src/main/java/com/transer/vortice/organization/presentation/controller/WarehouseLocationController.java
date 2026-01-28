package com.transer.vortice.organization.presentation.controller;

import com.transer.vortice.organization.application.dto.CreateWarehouseLocationRequest;
import com.transer.vortice.organization.application.dto.UpdateWarehouseLocationRequest;
import com.transer.vortice.organization.application.dto.WarehouseLocationResponse;
import com.transer.vortice.organization.application.service.WarehouseLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestión de ubicaciones de almacén.
 * Expone endpoints para operaciones CRUD de ubicaciones.
 *
 * SEGURIDAD: Los endpoints están protegidos por Row-Level Security a través de los almacenes.
 * Los usuarios solo acceden a ubicaciones de almacenes de su oficina.
 *
 * Permisos:
 * - Crear/Actualizar/Eliminar: ROLE_WAREHOUSE_MANAGER, ROLE_ADMIN_OFFICE o ROLE_ADMIN_NATIONAL
 * - Consultar: Todos los usuarios autenticados
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse-locations")
@RequiredArgsConstructor
public class WarehouseLocationController {

    private final WarehouseLocationService warehouseLocationService;

    /**
     * Crea una nueva ubicación de almacén.
     * Gerentes de almacén, administradores de oficina y nacionales pueden crear ubicaciones.
     *
     * POST /api/v1/warehouse-locations
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<WarehouseLocationResponse> createWarehouseLocation(
            @Valid @RequestBody CreateWarehouseLocationRequest request) {
        log.info("REST: Solicitud para crear ubicación con código: {}", request.getCode());
        WarehouseLocationResponse response = warehouseLocationService.createWarehouseLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza una ubicación existente.
     * Gerentes de almacén, administradores de oficina y nacionales pueden actualizar ubicaciones.
     *
     * PUT /api/v1/warehouse-locations/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<WarehouseLocationResponse> updateWarehouseLocation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWarehouseLocationRequest request) {
        log.info("REST: Solicitud para actualizar ubicación: {}", id);
        WarehouseLocationResponse response = warehouseLocationService.updateWarehouseLocation(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una ubicación (soft delete).
     * Gerentes de almacén, administradores de oficina y nacionales pueden eliminar ubicaciones.
     *
     * DELETE /api/v1/warehouse-locations/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<Void> deleteWarehouseLocation(@PathVariable UUID id) {
        log.info("REST: Solicitud para eliminar ubicación: {}", id);
        warehouseLocationService.deleteWarehouseLocation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene una ubicación por su ID.
     * Todos los usuarios autenticados pueden consultar ubicaciones (sujeto a RLS).
     *
     * GET /api/v1/warehouse-locations/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WarehouseLocationResponse> getLocationById(@PathVariable UUID id) {
        log.debug("REST: Solicitud para obtener ubicación: {}", id);
        WarehouseLocationResponse response = warehouseLocationService.getLocationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una ubicación por su código y almacén.
     * Todos los usuarios autenticados pueden consultar ubicaciones.
     *
     * GET /api/v1/warehouse-locations/by-code/{code}?warehouseId=uuid
     */
    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WarehouseLocationResponse> getLocationByCode(
            @PathVariable String code,
            @RequestParam UUID warehouseId) {
        log.debug("REST: Solicitud para obtener ubicación por código: {} en almacén: {}", code, warehouseId);
        WarehouseLocationResponse response = warehouseLocationService.getLocationByCode(code, warehouseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas las ubicaciones accesibles por el usuario actual.
     * El RLS filtra automáticamente por oficina del usuario.
     *
     * GET /api/v1/warehouse-locations
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseLocationResponse>> listAllLocations() {
        log.debug("REST: Solicitud para listar todas las ubicaciones");
        List<WarehouseLocationResponse> response = warehouseLocationService.listAllLocations();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas las ubicaciones de un almacén específico.
     * El usuario debe tener acceso al almacén.
     *
     * GET /api/v1/warehouse-locations/by-warehouse/{warehouseId}
     */
    @GetMapping("/by-warehouse/{warehouseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseLocationResponse>> listLocationsByWarehouse(
            @PathVariable UUID warehouseId) {
        log.debug("REST: Solicitud para listar ubicaciones de almacén: {}", warehouseId);
        List<WarehouseLocationResponse> response = warehouseLocationService.listLocationsByWarehouse(warehouseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca ubicaciones por nombre o código con paginación.
     * El RLS filtra automáticamente por oficina del usuario.
     *
     * GET /api/v1/warehouse-locations/search?q=termino&page=0&size=20
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<WarehouseLocationResponse>> searchLocations(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        log.debug("REST: Solicitud para buscar ubicaciones con término: {}", q);
        Page<WarehouseLocationResponse> response = warehouseLocationService.searchLocations(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Activa o desactiva una ubicación.
     * Gerentes de almacén, administradores de oficina y nacionales pueden cambiar el estado.
     *
     * PATCH /api/v1/warehouse-locations/{id}/active
     */
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<WarehouseLocationResponse> setLocationActive(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        log.info("REST: Solicitud para cambiar estado de ubicación {} a: {}", id, active);
        WarehouseLocationResponse response = warehouseLocationService.setLocationActive(id, active);
        return ResponseEntity.ok(response);
    }
}
