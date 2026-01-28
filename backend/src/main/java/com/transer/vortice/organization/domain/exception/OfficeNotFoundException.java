package com.transer.vortice.organization.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra una oficina.
 *
 * @author Vórtice Development Team
 */
public class OfficeNotFoundException extends RuntimeException {

    public OfficeNotFoundException(UUID officeId) {
        super(String.format("Oficina con ID %s no encontrada", officeId));
    }

    public OfficeNotFoundException(String code) {
        super(String.format("Oficina con código '%s' no encontrada", code));
    }

    public OfficeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
