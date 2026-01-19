package com.transer.vortice.shared.infrastructure.exception;

/**
 * Excepción para recursos no encontrados
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }

    public NotFoundException(String entity, Object id) {
        super("NOT_FOUND", String.format("%s con ID %s no encontrado", entity, id));
    }
}
