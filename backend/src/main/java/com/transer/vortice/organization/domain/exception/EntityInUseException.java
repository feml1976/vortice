package com.transer.vortice.organization.domain.exception;

/**
 * Excepción lanzada cuando se intenta eliminar una entidad que tiene dependencias activas.
 * Por ejemplo: eliminar una oficina que tiene almacenes activos, o un almacén que tiene ubicaciones.
 *
 * @author Vórtice Development Team
 */
public class EntityInUseException extends RuntimeException {

    public EntityInUseException(String entityType, String entityId, String dependencyType, long count) {
        super(String.format(
            "No se puede eliminar %s '%s' porque tiene %d %s activo(s)",
            entityType, entityId, count, dependencyType
        ));
    }

    public EntityInUseException(String message) {
        super(message);
    }

    public EntityInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
