package com.transer.vortice.organization.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando un usuario intenta acceder a datos de una oficina
 * a la que no tiene acceso.
 *
 * @author Vórtice Development Team
 */
public class ForbiddenOfficeAccessException extends RuntimeException {

    public ForbiddenOfficeAccessException(UUID officeId) {
        super(String.format("No tiene acceso a los datos de la oficina %s", officeId));
    }

    public ForbiddenOfficeAccessException(String message) {
        super(message);
    }

    public ForbiddenOfficeAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
