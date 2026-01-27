-- =====================================================
-- AGREGAR COLUMNAS DE SOFT DELETE
-- =====================================================
-- Agrega las columnas deleted_at y deleted_by solo a tablas de catálogo
-- que NO las tienen en V2.0.0
-- Fecha: 2026-01-26
-- =====================================================

-- Tablas de catálogo básicas que NO tienen deleted_at en V2.0.0
ALTER TABLE tire_management.brands
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.types
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.tire_references
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.suppliers
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.warehouse_locations
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.removal_reasons
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.tread_compounds
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

ALTER TABLE tire_management.protectors
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN deleted_by BIGINT REFERENCES users(id);

-- Crear índices para mejorar rendimiento en queries con soft delete
CREATE INDEX IF NOT EXISTS idx_brands_deleted_at ON tire_management.brands(deleted_at);
CREATE INDEX IF NOT EXISTS idx_types_deleted_at ON tire_management.types(deleted_at);
CREATE INDEX IF NOT EXISTS idx_tire_references_deleted_at ON tire_management.tire_references(deleted_at);
CREATE INDEX IF NOT EXISTS idx_suppliers_deleted_at ON tire_management.suppliers(deleted_at);
