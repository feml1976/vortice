-- ============================================================================
-- MIGRACIÓN V3.0.0: Estructura Organizacional Multi-Sede
-- Descripción: Crea tablas para gestionar oficinas, almacenes, ubicaciones
--              y proveedores con soporte multi-sede
-- Fecha: 2026-01-27
-- Autor: Sistema de Migración
-- ============================================================================

-- ============================================================================
-- TABLA: offices (Oficinas/Sedes)
-- Descripción: Representa cada sede u oficina de la empresa
-- ============================================================================
CREATE TABLE IF NOT EXISTS offices (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Campos de negocio
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    address TEXT,
    phone VARCHAR(20),

    -- Estado
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_office_code_format CHECK (code ~ '^[A-Z0-9]{2,10}$'),
    CONSTRAINT chk_office_name_not_empty CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_office_city_not_empty CHECK (TRIM(city) <> ''),
    CONSTRAINT chk_office_deleted_inactive CHECK (deleted_at IS NULL OR is_active = false)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_offices_code ON offices(code) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_offices_city ON offices(city) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_offices_active ON offices(is_active) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_offices_created_at ON offices(created_at);

-- Agregar columnas faltantes si no existen
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'offices' AND column_name = 'deleted_at') THEN
        ALTER TABLE offices ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE;
        ALTER TABLE offices ADD COLUMN deleted_by BIGINT REFERENCES users(id);
    END IF;
END $$;

-- Comentarios
COMMENT ON TABLE offices IS 'Sedes u oficinas de la empresa';
COMMENT ON COLUMN offices.code IS 'Código único de la oficina (ej: BOG, MED, CALI)';
COMMENT ON COLUMN offices.name IS 'Nombre de la oficina (ej: Bogotá - Sede Principal)';
COMMENT ON COLUMN offices.city IS 'Ciudad donde se ubica la oficina';
COMMENT ON COLUMN offices.is_active IS 'Indica si la oficina está activa';
COMMENT ON COLUMN offices.version IS 'Versión para optimistic locking';

-- ============================================================================
-- TABLA: warehouses (Almacenes)
-- Descripción: Almacenes dentro de una oficina
-- ============================================================================
CREATE TABLE IF NOT EXISTS warehouses (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Campos de negocio
    code VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,

    -- Relación con oficina
    office_id UUID NOT NULL REFERENCES offices(id),

    -- Estado
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT uk_warehouse_office_code UNIQUE(office_id, code),
    CONSTRAINT chk_warehouse_code_format CHECK (code ~ '^[A-Z0-9]{2,10}$'),
    CONSTRAINT chk_warehouse_name_not_empty CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_warehouse_deleted_inactive CHECK (deleted_at IS NULL OR is_active = false)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_warehouses_office ON warehouses(office_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_warehouses_code ON warehouses(office_id, code) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_warehouses_active ON warehouses(is_active) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_warehouses_created_at ON warehouses(created_at);

-- Agregar columnas faltantes si no existen
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'warehouses' AND column_name = 'deleted_at') THEN
        ALTER TABLE warehouses ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE;
        ALTER TABLE warehouses ADD COLUMN deleted_by BIGINT REFERENCES users(id);
    END IF;
END $$;

-- Comentarios
COMMENT ON TABLE warehouses IS 'Almacenes de cada oficina';
COMMENT ON COLUMN warehouses.code IS 'Código del almacén (único por oficina)';
COMMENT ON COLUMN warehouses.office_id IS 'Oficina a la que pertenece el almacén';
COMMENT ON COLUMN warehouses.version IS 'Versión para optimistic locking';
COMMENT ON CONSTRAINT uk_warehouse_office_code ON warehouses IS 'El código de almacén debe ser único dentro de la oficina';

-- ============================================================================
-- TABLA: warehouse_locations (Ubicaciones dentro del Almacén)
-- Descripción: Ubicaciones físicas específicas dentro de un almacén
-- ============================================================================
CREATE TABLE IF NOT EXISTS warehouse_locations (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Campos de negocio
    code VARCHAR(10) NOT NULL,
    name VARCHAR(100),
    description TEXT,

    -- Relación con almacén
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),

    -- Estado
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT uk_location_warehouse_code UNIQUE(warehouse_id, code),
    CONSTRAINT chk_location_code_format CHECK (code ~ '^[A-Z0-9\-]{1,10}$'),
    CONSTRAINT chk_location_deleted_inactive CHECK (deleted_at IS NULL OR is_active = false)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_warehouse_locations_warehouse ON warehouse_locations(warehouse_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_warehouse_locations_code ON warehouse_locations(warehouse_id, code) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_warehouse_locations_active ON warehouse_locations(is_active) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_warehouse_locations_created_at ON warehouse_locations(created_at);

-- Agregar columnas faltantes si no existen
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'warehouse_locations' AND column_name = 'deleted_at') THEN
        ALTER TABLE warehouse_locations ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE;
        ALTER TABLE warehouse_locations ADD COLUMN deleted_by BIGINT REFERENCES users(id);
    END IF;
END $$;

-- Comentarios
COMMENT ON TABLE warehouse_locations IS 'Ubicaciones físicas dentro de cada almacén';
COMMENT ON COLUMN warehouse_locations.code IS 'Código de la ubicación (único por almacén)';
COMMENT ON COLUMN warehouse_locations.warehouse_id IS 'Almacén al que pertenece la ubicación';
COMMENT ON COLUMN warehouse_locations.version IS 'Versión para optimistic locking';
COMMENT ON CONSTRAINT uk_location_warehouse_code ON warehouse_locations IS 'El código de ubicación debe ser único dentro del almacén';

-- ============================================================================
-- TABLA: tire_suppliers (Proveedores de Llantas)
-- Descripción: Proveedores de llantas específicos por oficina
-- ============================================================================
CREATE TABLE IF NOT EXISTS tire_suppliers (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Campos de negocio
    code VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(20) NOT NULL,

    -- Relación con oficina
    office_id UUID NOT NULL REFERENCES offices(id),

    -- Información de contacto
    contact_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,

    -- Estado
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT uk_supplier_office_code UNIQUE(office_id, code),
    CONSTRAINT chk_supplier_code_format CHECK (code ~ '^[A-Z0-9\-]{2,10}$'),
    CONSTRAINT chk_supplier_name_not_empty CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_supplier_tax_id_not_empty CHECK (TRIM(tax_id) <> ''),
    CONSTRAINT chk_supplier_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_supplier_deleted_inactive CHECK (deleted_at IS NULL OR is_active = false)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_tire_suppliers_office ON tire_suppliers(office_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tire_suppliers_code ON tire_suppliers(office_id, code) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tire_suppliers_active ON tire_suppliers(is_active) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tire_suppliers_tax_id ON tire_suppliers(tax_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tire_suppliers_created_at ON tire_suppliers(created_at);

-- Agregar columnas faltantes si no existen
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'tire_suppliers' AND column_name = 'deleted_at') THEN
        ALTER TABLE tire_suppliers ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE;
        ALTER TABLE tire_suppliers ADD COLUMN deleted_by BIGINT REFERENCES users(id);
    END IF;
END $$;

-- Comentarios
COMMENT ON TABLE tire_suppliers IS 'Proveedores de llantas por oficina';
COMMENT ON COLUMN tire_suppliers.code IS 'Código del proveedor (único por oficina)';
COMMENT ON COLUMN tire_suppliers.tax_id IS 'NIT o identificación tributaria';
COMMENT ON COLUMN tire_suppliers.office_id IS 'Oficina a la que pertenece el proveedor';
COMMENT ON COLUMN tire_suppliers.version IS 'Versión para optimistic locking';
COMMENT ON CONSTRAINT uk_supplier_office_code ON tire_suppliers IS 'El código de proveedor debe ser único dentro de la oficina';

-- ============================================================================
-- MODIFICACIÓN: Agregar office_id a users
-- Descripción: Todos los usuarios deben estar asignados a una oficina
-- ============================================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'office_id'
    ) THEN
        ALTER TABLE users ADD COLUMN office_id UUID REFERENCES offices(id);
    END IF;
END $$;

-- Índice para performance
CREATE INDEX IF NOT EXISTS idx_users_office ON users(office_id) WHERE is_active = true;

-- Comentario
COMMENT ON COLUMN users.office_id IS 'Oficina a la que está asignado el usuario';

-- ============================================================================
-- DATOS INICIALES: Crear oficina y almacén principal
-- Descripción: Estructura organizacional por defecto para migración
-- ============================================================================

-- Insertar oficina principal
INSERT INTO offices (id, code, name, city, address, phone, is_active, created_by)
VALUES (
    'a0000000-0000-0000-0000-000000000001'::UUID,
    'MAIN',
    'Oficina Principal',
    'Bogotá',
    'Dirección por definir',
    'Teléfono por definir',
    true,
    1  -- Usuario admin
)
ON CONFLICT (id) DO NOTHING;

-- Insertar almacén principal
INSERT INTO warehouses (id, code, name, office_id, description, is_active, created_by)
VALUES (
    'b0000000-0000-0000-0000-000000000001'::UUID,
    'PRIN',
    'Almacén Principal',
    'a0000000-0000-0000-0000-000000000001'::UUID,
    'Almacén principal de la oficina',
    true,
    1  -- Usuario admin
)
ON CONFLICT (id) DO NOTHING;

-- Asignar todos los usuarios existentes a la oficina principal
UPDATE users
SET office_id = 'a0000000-0000-0000-0000-000000000001'::UUID
WHERE office_id IS NULL;

-- Hacer obligatorio el campo office_id después de asignar valores
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'office_id' AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE users ALTER COLUMN office_id SET NOT NULL;
    END IF;
END $$;

-- ============================================================================
-- VISTAS ÚTILES
-- ============================================================================

-- Vista: Jerarquía organizacional completa
CREATE OR REPLACE VIEW v_organizational_hierarchy AS
SELECT
    o.id AS office_id,
    o.code AS office_code,
    o.name AS office_name,
    o.city AS office_city,
    w.id AS warehouse_id,
    w.code AS warehouse_code,
    w.name AS warehouse_name,
    wl.id AS location_id,
    wl.code AS location_code,
    wl.name AS location_name,
    CONCAT(o.code, ' > ', w.code, COALESCE(' > ' || wl.code, '')) AS full_path,
    o.is_active AS office_active,
    w.is_active AS warehouse_active,
    wl.is_active AS location_active
FROM offices o
LEFT JOIN warehouses w ON o.id = w.office_id AND w.deleted_at IS NULL
LEFT JOIN warehouse_locations wl ON w.id = wl.warehouse_id AND wl.deleted_at IS NULL
WHERE o.deleted_at IS NULL;

COMMENT ON VIEW v_organizational_hierarchy IS 'Jerarquía completa de oficinas → almacenes → ubicaciones';

-- Vista: Resumen por oficina
CREATE OR REPLACE VIEW v_office_summary AS
SELECT
    o.id,
    o.code,
    o.name,
    o.city,
    o.is_active,
    COUNT(DISTINCT w.id) AS total_warehouses,
    COUNT(DISTINCT wl.id) AS total_locations,
    COUNT(DISTINCT ts.id) AS total_suppliers,
    COUNT(DISTINCT u.id) AS total_users
FROM offices o
LEFT JOIN warehouses w ON o.id = w.office_id AND w.deleted_at IS NULL AND w.is_active = true
LEFT JOIN warehouse_locations wl ON w.id = wl.warehouse_id AND wl.deleted_at IS NULL AND wl.is_active = true
LEFT JOIN tire_suppliers ts ON o.id = ts.office_id AND ts.deleted_at IS NULL AND ts.is_active = true
LEFT JOIN users u ON o.id = u.office_id AND u.is_active = true
WHERE o.deleted_at IS NULL
GROUP BY o.id, o.code, o.name, o.city, o.is_active;

COMMENT ON VIEW v_office_summary IS 'Resumen de recursos por oficina';

-- ============================================================================
-- FIN DE LA MIGRACIÓN V3.0.0
-- ============================================================================
