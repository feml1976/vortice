-- =============================================================================
-- Descripción: Agrega la columna version a la tabla password_reset_tokens
--              para optimistic locking
-- Versión: 1.0.5
-- Autor: Vórtice Development Team
-- Fecha: 2026-01-20
-- =============================================================================

-- Agregar columna version a password_reset_tokens
ALTER TABLE password_reset_tokens
ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- Comentario
COMMENT ON COLUMN password_reset_tokens.version IS 'Versión para optimistic locking';
