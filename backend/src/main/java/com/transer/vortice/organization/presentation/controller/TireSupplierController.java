package com.transer.vortice.organization.presentation.controller;

import com.transer.vortice.organization.application.dto.CreateTireSupplierRequest;
import com.transer.vortice.organization.application.dto.TireSupplierResponse;
import com.transer.vortice.organization.application.dto.UpdateTireSupplierRequest;
import com.transer.vortice.organization.application.service.TireSupplierService;
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
 * Controlador REST para gestión de proveedores de llantas.
 * Expone endpoints para operaciones CRUD de proveedores.
 *
 * SEGURIDAD: Los endpoints están protegidos por Row-Level Security.
 * Los usuarios solo acceden a proveedores de su oficina (excepto admin nacional).
 *
 * Permisos:
 * - Crear/Actualizar/Eliminar: ROLE_ADMIN_OFFICE o ROLE_ADMIN_NATIONAL
 * - Consultar: Todos los usuarios autenticados
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/tire-suppliers")
@RequiredArgsConstructor
public class TireSupplierController {

    private final TireSupplierService tireSupplierService;

    /**
     * Crea un nuevo proveedor de llantas.
     * Administradores de oficina y nacionales pueden crear proveedores.
     *
     * POST /api/v1/tire-suppliers
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<TireSupplierResponse> createTireSupplier(
            @Valid @RequestBody CreateTireSupplierRequest request) {
        log.info("REST: Solicitud para crear proveedor con código: {}", request.getCode());
        TireSupplierResponse response = tireSupplierService.createTireSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un proveedor existente.
     * Administradores de oficina y nacionales pueden actualizar proveedores.
     *
     * PUT /api/v1/tire-suppliers/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<TireSupplierResponse> updateTireSupplier(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTireSupplierRequest request) {
        log.info("REST: Solicitud para actualizar proveedor: {}", id);
        TireSupplierResponse response = tireSupplierService.updateTireSupplier(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un proveedor (soft delete).
     * Administradores de oficina y nacionales pueden eliminar proveedores.
     *
     * DELETE /api/v1/tire-suppliers/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<Void> deleteTireSupplier(@PathVariable UUID id) {
        log.info("REST: Solicitud para eliminar proveedor: {}", id);
        tireSupplierService.deleteTireSupplier(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene un proveedor por su ID.
     * Todos los usuarios autenticados pueden consultar proveedores (sujeto a RLS).
     *
     * GET /api/v1/tire-suppliers/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TireSupplierResponse> getSupplierById(@PathVariable UUID id) {
        log.debug("REST: Solicitud para obtener proveedor: {}", id);
        TireSupplierResponse response = tireSupplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un proveedor por su código y oficina.
     * Todos los usuarios autenticados pueden consultar proveedores.
     *
     * GET /api/v1/tire-suppliers/by-code/{code}?officeId=uuid
     */
    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TireSupplierResponse> getSupplierByCode(
            @PathVariable String code,
            @RequestParam UUID officeId) {
        log.debug("REST: Solicitud para obtener proveedor por código: {} en oficina: {}", code, officeId);
        TireSupplierResponse response = tireSupplierService.getSupplierByCode(code, officeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene proveedores por su NIT.
     * Puede retornar múltiples proveedores si el mismo NIT está registrado en diferentes oficinas.
     *
     * GET /api/v1/tire-suppliers/by-tax-id/{taxId}
     */
    @GetMapping("/by-tax-id/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TireSupplierResponse>> getSuppliersByTaxId(@PathVariable String taxId) {
        log.debug("REST: Solicitud para obtener proveedores por NIT: {}", taxId);
        List<TireSupplierResponse> response = tireSupplierService.getSuppliersByTaxId(taxId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los proveedores accesibles por el usuario actual.
     * El RLS filtra automáticamente por oficina del usuario.
     *
     * GET /api/v1/tire-suppliers
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TireSupplierResponse>> listAllSuppliers() {
        log.debug("REST: Solicitud para listar todos los proveedores");
        List<TireSupplierResponse> response = tireSupplierService.listAllSuppliers();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los proveedores de una oficina específica.
     * El usuario debe tener acceso a la oficina.
     *
     * GET /api/v1/tire-suppliers/by-office/{officeId}
     */
    @GetMapping("/by-office/{officeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TireSupplierResponse>> listSuppliersByOffice(@PathVariable UUID officeId) {
        log.debug("REST: Solicitud para listar proveedores de oficina: {}", officeId);
        List<TireSupplierResponse> response = tireSupplierService.listSuppliersByOffice(officeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca proveedores por nombre, código o NIT con paginación.
     * El RLS filtra automáticamente por oficina del usuario.
     *
     * GET /api/v1/tire-suppliers/search?q=termino&page=0&size=20
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TireSupplierResponse>> searchSuppliers(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        log.debug("REST: Solicitud para buscar proveedores con término: {}", q);
        Page<TireSupplierResponse> response = tireSupplierService.searchSuppliers(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Activa o desactiva un proveedor.
     * Administradores de oficina y nacionales pueden cambiar el estado.
     *
     * PATCH /api/v1/tire-suppliers/{id}/active
     */
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN_OFFICE', 'ADMIN_NATIONAL')")
    public ResponseEntity<TireSupplierResponse> setSupplierActive(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        log.info("REST: Solicitud para cambiar estado de proveedor {} a: {}", id, active);
        TireSupplierResponse response = tireSupplierService.setSupplierActive(id, active);
        return ResponseEntity.ok(response);
    }
}
