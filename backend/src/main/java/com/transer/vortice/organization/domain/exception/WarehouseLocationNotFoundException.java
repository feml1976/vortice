package com.transer.vortice.organization.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra una ubicación de almacén.
 *
 * @author Vórtice Development Team
 */
public class WarehouseLocationNotFoundException extends RuntimeException {

    public WarehouseLocationNotFoundException(UUID locationId) {
        super(String.format("Ubicación con ID %s no encontrada", locationId));
    }

    public WarehouseLocationNotFoundException(String code, UUID warehouseId) {
        super(String.format("Ubicación con código '%s' en almacén %s no encontrada", code, warehouseId));
    }

    public WarehouseLocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
