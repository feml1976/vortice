package com.transer.vortice.shared.infrastructure.exception;

import java.util.Map;

/**
 * Excepción para errores de validación
 */
public class ValidationException extends BusinessException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.errors = Map.of();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super("VALIDATION_ERROR", message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
