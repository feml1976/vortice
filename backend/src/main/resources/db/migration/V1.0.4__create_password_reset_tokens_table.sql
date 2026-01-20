-- =============================================================================
-- Descripción: Crea la tabla password_reset_tokens para gestión de tokens de
--              recuperación de contraseña
-- Versión: 1.0.4
-- Autor: Vórtice Development Team
-- Fecha: 2026-01-20
-- =============================================================================

-- Crear tabla password_reset_tokens
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    is_valid BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);

-- Comentarios de documentación
COMMENT ON TABLE password_reset_tokens IS 'Tokens de recuperación de contraseña';
COMMENT ON COLUMN password_reset_tokens.id IS 'Identificador único del token';
COMMENT ON COLUMN password_reset_tokens.token IS 'Token UUID para recuperación de contraseña';
COMMENT ON COLUMN password_reset_tokens.user_id IS 'ID del usuario asociado';
COMMENT ON COLUMN password_reset_tokens.expires_at IS 'Fecha y hora de expiración del token';
COMMENT ON COLUMN password_reset_tokens.used_at IS 'Fecha y hora en que se usó el token';
COMMENT ON COLUMN password_reset_tokens.is_valid IS 'Indica si el token es válido';
