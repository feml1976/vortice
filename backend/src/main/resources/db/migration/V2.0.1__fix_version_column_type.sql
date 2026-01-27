-- =====================================================
-- CORRECCIÓN DEL TIPO DE DATO DE LA COLUMNA VERSION
-- =====================================================
-- Cambia el tipo de INTEGER a BIGINT para compatibilidad con JPA @Version
-- Solo afecta tablas del esquema tire_management que tienen esta columna
-- Fecha: 2026-01-26
-- =====================================================

ALTER TABLE tire_management.brands
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.types
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.tire_references
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.suppliers
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.tires
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.inventory
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.active_installations
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.intermediate
    ALTER COLUMN version TYPE BIGINT;

ALTER TABLE tire_management.retired
    ALTER COLUMN version TYPE BIGINT;
