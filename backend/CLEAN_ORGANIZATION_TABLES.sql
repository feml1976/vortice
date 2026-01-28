-- ============================================================================
-- Script para limpiar tablas de organización antes de ejecutar migración V3.0.0
-- ============================================================================

-- IMPORTANTE: Ejecutar este script SOLO si la migración V3.0.0 está fallando
-- debido a que las tablas ya existen con un esquema incompleto.

-- Este script elimina completamente las tablas de organización para permitir
-- que la migración V3.0.0 las cree desde cero con el esquema correcto.

BEGIN;

-- Eliminar vistas
DROP VIEW IF EXISTS v_organizational_hierarchy CASCADE;
DROP VIEW IF EXISTS v_office_summary CASCADE;

-- Eliminar tablas en orden inverso de dependencias
DROP TABLE IF EXISTS tire_suppliers CASCADE;
DROP TABLE IF EXISTS warehouse_locations CASCADE;
DROP TABLE IF EXISTS warehouses CASCADE;
DROP TABLE IF EXISTS offices CASCADE;

-- Eliminar columna office_id de users si existe
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'office_id'
    ) THEN
        ALTER TABLE users DROP COLUMN office_id;
    END IF;
END $$;

-- Eliminar el registro de la migración V3.0.0 de Flyway
DELETE FROM flyway_schema_history WHERE version = '3.0.0';

COMMIT;

-- ============================================================================
-- Instrucciones de uso:
-- ============================================================================
-- 1. Conectarse a la base de datos:
--    psql -U vortice_admin -d vortice_dev
--
-- 2. Ejecutar este script:
--    \i CLEAN_ORGANIZATION_TABLES.sql
--
-- 3. Reiniciar el backend para que Flyway ejecute la migración V3.0.0
-- ============================================================================
