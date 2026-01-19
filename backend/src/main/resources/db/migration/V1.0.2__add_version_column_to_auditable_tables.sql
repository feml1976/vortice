-- =====================================================
-- MIGRACIÓN V1.0.2: AGREGAR COLUMNA VERSION
-- Sistema Vórtice - Gestión de Taller
-- Fecha: 2026-01-19
-- =====================================================

-- Agregar columna version para optimistic locking
-- Esta columna es utilizada por JPA (@Version) para prevenir conflictos de concurrencia

-- Tabla users
ALTER TABLE users
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Tabla offices
ALTER TABLE offices
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Comentarios descriptivos
COMMENT ON COLUMN users.version IS 'Versión para optimistic locking (JPA)';
COMMENT ON COLUMN offices.version IS 'Versión para optimistic locking (JPA)';

-- =====================================================
-- FIN DE LA MIGRACIÓN V1.0.2
-- =====================================================
