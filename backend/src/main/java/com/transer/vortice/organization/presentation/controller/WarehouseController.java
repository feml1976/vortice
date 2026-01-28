package com.transer.vortice.organization.presentation.controller;

import com.transer.vortice.organization.application.dto.CreateWarehouseRequest;
import com.transer.vortice.organization.application.dto.UpdateWarehouseRequest;
import com.transer.vortice.organization.application.dto.WarehouseResponse;
import com.transer.vortice.organization.application.service.WarehouseService;
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
 * Controlador REST para gestión de almacenes.
 * Expone endpoints para operaciones CRUD de almacenes.
 *
 * SEGURIDAD: Los endpoints están protegidos por Row-Level Security.
 * Los usuarios solo acceden a almacenes de su oficina (excepto admin nacional).
 *
 * Permisos:
 * - Crear/Actualizar/Eliminar: ROLE_ADMIN_OFFICE o ROLE_ADMIN_NATIONAL
 * - Consultar: Todos los usuarios autenticados
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * Crea un nuevo almacén.
     * Administradores de oficina y nacionales pueden crear almacenes.
     *
     * POST /api/v1/warehouses
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody CreateWarehouseRequest request) {
        log.info("REST: Solicitud para crear almacén con código: {}", request.getCode());
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un almacén existente.
     * Administradores de oficina y nacionales pueden actualizar almacenes.
     *
     * PUT /api/v1/warehouses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWarehouseRequest request) {
        log.info("REST: Solicitud para actualizar almacén: {}", id);
        WarehouseResponse response = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un almacén (soft delete).
     * Administradores de oficina y nacionales pueden eliminar almacenes.
     *
     * DELETE /api/v1/warehouses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID id) {
        log.info("REST: Solicitud para eliminar almacén: {}", id);
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene un almacén por su ID.
     * Todos los usuarios autenticados pueden consultar almacenes (sujeto a RLS).
     *
     * GET /api/v1/warehouses/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable UUID id) {
        log.debug("REST: Solicitud para obtener almacén: {}", id);
        WarehouseResponse response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un almacén por su código y oficina.
     * Todos los usuarios autenticados pueden consultar almacenes.
     *
     * GET /api/v1/warehouses/by-code/{code}?officeId=uuid
     */
    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WarehouseResponse> getWarehouseByCode(
            @PathVariable String code,
            @RequestParam UUID officeId) {
        log.debug("REST: Solicitud para obtener almacén por código: {} en oficina: {}", code, officeId);
        WarehouseResponse response = warehouseService.getWarehouseByCode(code, officeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los almacenes accesibles por el usuario actual.
     * El RLS filtra automáticamente por oficina del usuario.
     *
     * GET /api/v1/warehouses
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseResponse>> listAllWarehouses() {
        log.debug("REST: Solicitud para listar todos los almacenes");
        List<WarehouseResponse> response = warehouseService.listAllWarehouses();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los almacenes de una oficina específica.
     * El usuario debe tener acceso a la oficina.
     *
     * GET /api/v1/warehouses/by-office/{officeId}
     */
    @GetMapping("/by-office/{officeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseResponse>> listWarehousesByOffice(@PathVariable UUID officeId) {
        log.debug("REST: Solicitud para listar almacenes de oficina: {}", officeId);
        List<WarehouseResponse> response = warehouseService.listWarehousesByOffice(officeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca almacenes por nombre o código con paginación.
     * El RLS filtra automáticamente por oficina del usuario.
     *
     * GET /api/v1/warehouses/search?q=termino&page=0&size=20
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<WarehouseResponse>> searchWarehouses(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        log.debug("REST: Solicitud para buscar almacenes con término: {}", q);
        Page<WarehouseResponse> response = warehouseService.searchWarehouses(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un almacén con información detallada (totales de ubicaciones).
     * Todos los usuarios autenticados pueden consultar (sujeto a RLS).
     *
     * GET /api/v1/warehouses/{id}/details
     */
    @GetMapping("/{id}/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WarehouseResponse> getWarehouseWithDetails(@PathVariable UUID id) {
        log.debug("REST: Solicitud para obtener detalles de almacén: {}", id);
        WarehouseResponse response = warehouseService.getWarehouseWithDetails(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Activa o desactiva un almacén.
     * Administradores de oficina y nacionales pueden cambiar el estado.
     *
     * PATCH /api/v1/warehouses/{id}/active
     */
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<WarehouseResponse> setWarehouseActive(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        log.info("REST: Solicitud para cambiar estado de almacén {} a: {}", id, active);
        WarehouseResponse response = warehouseService.setWarehouseActive(id, active);
        return ResponseEntity.ok(response);
    }
}
