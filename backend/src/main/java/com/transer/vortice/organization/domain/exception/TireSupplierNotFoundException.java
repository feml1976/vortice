package com.transer.vortice.organization.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra un proveedor de llantas.
 *
 * @author Vórtice Development Team
 */
public class TireSupplierNotFoundException extends RuntimeException {

    public TireSupplierNotFoundException(UUID supplierId) {
        super(String.format("Proveedor con ID %s no encontrado", supplierId));
    }

    public TireSupplierNotFoundException(String code, UUID officeId) {
        super(String.format("Proveedor con código '%s' en oficina %s no encontrado", code, officeId));
    }

    public TireSupplierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
