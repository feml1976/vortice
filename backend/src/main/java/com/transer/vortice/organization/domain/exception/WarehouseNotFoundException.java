package com.transer.vortice.organization.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra un almacén.
 *
 * @author Vórtice Development Team
 */
public class WarehouseNotFoundException extends RuntimeException {

    public WarehouseNotFoundException(UUID warehouseId) {
        super(String.format("Almacén con ID %s no encontrado", warehouseId));
    }

    public WarehouseNotFoundException(String code, UUID officeId) {
        super(String.format("Almacén con código '%s' en oficina %s no encontrado", code, officeId));
    }

    public WarehouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
