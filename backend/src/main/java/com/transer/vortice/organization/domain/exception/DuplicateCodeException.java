package com.transer.vortice.organization.domain.exception;

/**
 * Excepción lanzada cuando se intenta crear o actualizar una entidad con un código
 * que ya existe en el contexto apropiado (global para oficinas, por oficina para almacenes, etc.).
 *
 * @author Vórtice Development Team
 */
public class DuplicateCodeException extends RuntimeException {

    public DuplicateCodeException(String entityType, String code) {
        super(String.format("Ya existe %s con el código '%s'", entityType, code));
    }

    public DuplicateCodeException(String entityType, String code, String context) {
        super(String.format("Ya existe %s con el código '%s' en %s", entityType, code, context));
    }

    public DuplicateCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
