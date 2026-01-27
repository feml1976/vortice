-- =====================================================
-- CORRECCIÓN DEL TIPO DE DATO DE VERSION EN TECHNICAL_SPECIFICATIONS
-- =====================================================
-- La tabla technical_specifications tiene version INTEGER pero deberia ser BIGINT
-- Fecha: 2026-01-26
-- =====================================================

ALTER TABLE tire_management.technical_specifications
    ALTER COLUMN version TYPE BIGINT;
