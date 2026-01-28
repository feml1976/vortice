package com.transer.vortice.organization.presentation.controller;

import com.transer.vortice.organization.application.dto.CreateOfficeRequest;
import com.transer.vortice.organization.application.dto.OfficeResponse;
import com.transer.vortice.organization.application.dto.UpdateOfficeRequest;
import com.transer.vortice.organization.application.service.OfficeService;
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
 * Controlador REST para gestión de oficinas.
 * Expone endpoints para operaciones CRUD de oficinas.
 *
 * Permisos:
 * - Crear/Actualizar/Eliminar oficinas: Solo ROLE_ADMIN_NATIONAL
 * - Consultar oficinas: Todos los usuarios autenticados
 *
 * @author Vórtice Development Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/offices")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

    /**
     * Crea una nueva oficina.
     * Solo administradores nacionales pueden crear oficinas.
     *
     * POST /api/v1/offices
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<OfficeResponse> createOffice(@Valid @RequestBody CreateOfficeRequest request) {
        log.info("REST: Solicitud para crear oficina con código: {}", request.getCode());
        OfficeResponse response = officeService.createOffice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza una oficina existente.
     * Solo administradores nacionales pueden actualizar oficinas.
     *
     * PUT /api/v1/offices/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<OfficeResponse> updateOffice(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOfficeRequest request) {
        log.info("REST: Solicitud para actualizar oficina: {}", id);
        OfficeResponse response = officeService.updateOffice(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una oficina (soft delete).
     * Solo administradores nacionales pueden eliminar oficinas.
     *
     * DELETE /api/v1/offices/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<Void> deleteOffice(@PathVariable UUID id) {
        log.info("REST: Solicitud para eliminar oficina: {}", id);
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene una oficina por su ID.
     * Todos los usuarios autenticados pueden consultar oficinas.
     *
     * GET /api/v1/offices/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OfficeResponse> getOfficeById(@PathVariable UUID id) {
        log.debug("REST: Solicitud para obtener oficina: {}", id);
        OfficeResponse response = officeService.getOfficeById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una oficina por su código.
     * Todos los usuarios autenticados pueden consultar oficinas.
     *
     * GET /api/v1/offices/by-code/{code}
     */
    @GetMapping("/by-code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OfficeResponse> getOfficeByCode(@PathVariable String code) {
        log.debug("REST: Solicitud para obtener oficina por código: {}", code);
        OfficeResponse response = officeService.getOfficeByCode(code);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas las oficinas activas.
     * Los administradores nacionales ven todas las oficinas.
     * Otros usuarios solo ven su propia oficina.
     *
     * GET /api/v1/offices
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OfficeResponse>> listAllOffices() {
        log.debug("REST: Solicitud para listar todas las oficinas");
        List<OfficeResponse> response = officeService.listAllOffices();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca oficinas por nombre o ciudad con paginación.
     * Solo administradores nacionales pueden buscar en todas las oficinas.
     *
     * GET /api/v1/offices/search?q=termino&page=0&size=20
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<Page<OfficeResponse>> searchOffices(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        log.debug("REST: Solicitud para buscar oficinas con término: {}", q);
        Page<OfficeResponse> response = officeService.searchOffices(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una oficina con información detallada (totales).
     * Solo administradores nacionales pueden ver detalles de cualquier oficina.
     *
     * GET /api/v1/offices/{id}/details
     */
    @GetMapping("/{id}/details")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<OfficeResponse> getOfficeWithDetails(@PathVariable UUID id) {
        log.debug("REST: Solicitud para obtener detalles de oficina: {}", id);
        OfficeResponse response = officeService.getOfficeWithDetails(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Activa o desactiva una oficina.
     * Solo administradores nacionales pueden cambiar el estado.
     *
     * PATCH /api/v1/offices/{id}/active
     */
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<OfficeResponse> setOfficeActive(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        log.info("REST: Solicitud para cambiar estado de oficina {} a: {}", id, active);
        OfficeResponse response = officeService.setOfficeActive(id, active);
        return ResponseEntity.ok(response);
    }
}
