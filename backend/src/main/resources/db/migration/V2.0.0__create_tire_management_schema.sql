-- =====================================================
-- MIGRACIÓN V2.0.0: MÓDULO DE GESTIÓN DE LLANTAS
-- Sistema Vórtice - Gestión de Taller
-- Arquitectura: Monolito Modular con DDD
-- Base de Datos: PostgreSQL 18
-- Fecha: 2026-01-21
-- =====================================================

-- =====================================================
-- ESTRATEGIA DE ESQUEMAS MÚLTIPLES (DDD)
-- =====================================================
-- Este script implementa la arquitectura de esquemas múltiples
-- donde cada bounded context tiene su propio esquema físico.
--
-- Esquemas:
-- - tire_management: Bounded Context de Gestión de Llantas
-- - shared: Catálogos compartidos (usuarios, vehículos, geografía)
--
-- Beneficios:
-- 1. Alineación con Domain-Driven Design
-- 2. Nombres de tablas concisos (sin prefijos redundantes)
-- 3. Permisos granulares por dominio
-- 4. Evolución a microservicios facilitada
-- 5. Backup/restore selectivo por bounded context
-- =====================================================

-- =====================================================
-- EXTENSIONES REQUERIDAS
-- =====================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";  -- Para búsquedas de texto con similitud

-- =====================================================
-- CREACIÓN DE ESQUEMAS
-- =====================================================

-- Esquema para el bounded context de llantas
CREATE SCHEMA IF NOT EXISTS tire_management;
COMMENT ON SCHEMA tire_management IS 'Bounded Context: Gestión y Control de Llantas';

-- Esquema para catálogos compartidos entre módulos
CREATE SCHEMA IF NOT EXISTS shared;
COMMENT ON SCHEMA shared IS 'Shared Kernel: Catálogos compartidos (usuarios, vehículos, geografía)';

-- =====================================================
-- TIPOS ENUM
-- =====================================================

CREATE TYPE tire_management.tire_state AS ENUM (
    'INVENTORY',
    'ACTIVE',
    'INTERMEDIATE',
    'RETIRED'
);

CREATE TYPE tire_management.evaluation_status AS ENUM (
    'PENDING',
    'APPROVED_FOR_USE',
    'REQUIRES_RETREADING',
    'MUST_BE_RETIRED'
);

CREATE TYPE tire_management.alert_type AS ENUM (
    'CRITICAL_DEPTH',
    'IRREGULAR_WEAR',
    'SAMPLING_REQUIRED',
    'INCORRECT_PRESSURE',
    'LOW_INVENTORY'
);

CREATE TYPE tire_management.alert_priority AS ENUM (
    'HIGH',    -- Acción < 24h
    'MEDIUM',  -- Acción < 72h
    'LOW'      -- Informacional
);

CREATE TYPE tire_management.position_type AS ENUM (
    'DIRECTIONAL',
    'TRACTION',
    'TRAILER'
);

-- =====================================================
-- FUNCIONES AUXILIARES PARA GENERATION
-- =====================================================
-- El campo generation es un código de 3 dígitos: [VV][R]
-- - VV (dígitos 1-2): Contador de vehículos (00-99)
-- - R (dígito 3): Contador de reencauches (0-9)
-- Ejemplo: "032" = 3 vehículos, 2 reencauches
-- =====================================================

-- Función: Extraer contador de vehículos
CREATE OR REPLACE FUNCTION tire_management.get_vehicle_count(generation_value CHAR(3))
RETURNS INTEGER AS $$
BEGIN
    IF generation_value IS NULL OR LENGTH(generation_value) != 3 THEN
        RAISE EXCEPTION 'Invalid generation format. Must be 3 digits (e.g., "032")';
    END IF;
    RETURN CAST(SUBSTRING(generation_value, 1, 2) AS INTEGER);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION tire_management.get_vehicle_count IS
'Extrae el contador de vehículos de generation. Ejemplo: "032" → 3';

-- Función: Extraer contador de reencauches
CREATE OR REPLACE FUNCTION tire_management.get_retread_count(generation_value CHAR(3))
RETURNS INTEGER AS $$
BEGIN
    IF generation_value IS NULL OR LENGTH(generation_value) != 3 THEN
        RAISE EXCEPTION 'Invalid generation format. Must be 3 digits (e.g., "032")';
    END IF;
    RETURN CAST(SUBSTRING(generation_value, 3, 1) AS INTEGER);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION tire_management.get_retread_count IS
'Extrae el contador de reencauches de generation. Ejemplo: "032" → 2';

-- Función: Incrementar contador de vehículos
CREATE OR REPLACE FUNCTION tire_management.increment_vehicle_count(current_generation CHAR(3))
RETURNS CHAR(3) AS $$
DECLARE
    vehicle_count INTEGER;
    retread_count INTEGER;
BEGIN
    vehicle_count := tire_management.get_vehicle_count(current_generation);
    retread_count := tire_management.get_retread_count(current_generation);

    IF vehicle_count >= 99 THEN
        RAISE EXCEPTION 'Maximum vehicle count (99) reached for generation %', current_generation;
    END IF;

    RETURN LPAD((vehicle_count + 1)::TEXT, 2, '0') || retread_count::TEXT;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION tire_management.increment_vehicle_count IS
'Incrementa el contador de vehículos manteniendo reencauches. Ejemplo: "020" → "030"';

-- Función: Incrementar contador de reencauches (reset vehículos a 00)
CREATE OR REPLACE FUNCTION tire_management.increment_retread_count(current_generation CHAR(3))
RETURNS CHAR(3) AS $$
DECLARE
    retread_count INTEGER;
BEGIN
    retread_count := tire_management.get_retread_count(current_generation);

    IF retread_count >= 9 THEN
        RAISE EXCEPTION 'Maximum retread count (9) reached for generation %', current_generation;
    END IF;

    RETURN '00' || (retread_count + 1)::TEXT;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION tire_management.increment_retread_count IS
'Incrementa reencauches y resetea vehículos a 00. Ejemplo: "042" → "003"';

-- Función: Construir generation desde contadores
CREATE OR REPLACE FUNCTION tire_management.build_generation(vehicle_count INTEGER, retread_count INTEGER)
RETURNS CHAR(3) AS $$
BEGIN
    IF vehicle_count < 0 OR vehicle_count > 99 THEN
        RAISE EXCEPTION 'Vehicle count must be between 0 and 99';
    END IF;
    IF retread_count < 0 OR retread_count > 9 THEN
        RAISE EXCEPTION 'Retread count must be between 0 and 9';
    END IF;

    RETURN LPAD(vehicle_count::TEXT, 2, '0') || retread_count::TEXT;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION tire_management.build_generation IS
'Construye generation desde contadores. Ejemplo: (3, 2) → "032"';

-- =====================================================
-- TABLAS SHARED: CATÁLOGOS COMPARTIDOS
-- =====================================================

-- Nota: Las tablas users, roles, permissions, countries, departments, cities, offices
-- se asumen ya creadas por migraciones anteriores (V1.0.0)

-- Tabla: vehicle_classes (Clases de vehículos)
CREATE TABLE shared.vehicle_classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    number_of_tires SMALLINT NOT NULL CHECK (number_of_tires > 0 AND number_of_tires <= 32),
    category CHAR(1) CHECK (category IN ('C', 'T')),
    tire_configuration JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE shared.vehicle_classes IS 'Catálogo de clases de vehículos (compartido entre módulos)';
COMMENT ON COLUMN shared.vehicle_classes.category IS 'C: Camión, T: Trailer';
COMMENT ON COLUMN shared.vehicle_classes.tire_configuration IS 'Configuración JSON: {positions: [{number: 1, type: "DIRECTIONAL", description: "Delantera izquierda"}]}';

-- Tabla: vehicles (Vehículos de la flota)
CREATE TABLE shared.vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    license_plate VARCHAR(10) UNIQUE NOT NULL,
    vehicle_class_id UUID NOT NULL REFERENCES shared.vehicle_classes(id),
    brand VARCHAR(50) NOT NULL,
    model_year INTEGER NOT NULL CHECK (model_year >= 1970 AND model_year <= EXTRACT(YEAR FROM CURRENT_DATE) + 1),
    initial_mileage INTEGER NOT NULL CHECK (initial_mileage >= 0),
    current_mileage INTEGER NOT NULL CHECK (current_mileage >= 0),
    current_mileage_updated_at TIMESTAMP WITH TIME ZONE,
    status CHAR(1) NOT NULL DEFAULT 'A' CHECK (status IN ('A', 'I')),
    is_operating BOOLEAN NOT NULL DEFAULT true,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    CONSTRAINT chk_vehicles_current_mileage_gte_initial CHECK (current_mileage >= initial_mileage)
);

COMMENT ON TABLE shared.vehicles IS 'Catálogo maestro de vehículos de la flota (compartido entre módulos)';
COMMENT ON COLUMN shared.vehicles.status IS 'A: Activo, I: Inactivo';
COMMENT ON COLUMN shared.vehicles.current_mileage_updated_at IS 'Timestamp de última actualización de kilometraje';

-- =====================================================
-- TABLAS TIRE_MANAGEMENT: CATÁLOGOS DEL DOMINIO
-- =====================================================

-- Tabla: brands (Marcas de llantas)
CREATE TABLE tire_management.brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.brands IS 'Catálogo de marcas de llantas';

-- Tabla: types (Tipos de llantas)
CREATE TABLE tire_management.types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.types IS 'Catálogo de tipos de llantas (radial, convencional, etc.)';

-- Tabla: references (Referencias de llantas)
CREATE TABLE tire_management.tire_references (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    specifications TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.tire_references IS 'Catálogo de referencias de llantas';

-- Tabla: providers (Proveedores)
CREATE TABLE tire_management.suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    tax_id VARCHAR(20) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    contact_name VARCHAR(200),
    contact_info JSONB,
    payment_terms_days INTEGER NOT NULL DEFAULT 30,
    legacy_milenio_code INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.suppliers IS 'Proveedores de llantas y servicios de reencauche';
COMMENT ON COLUMN tire_management.suppliers.contact_info IS 'Información de contacto JSON: {email, phone, position, notes}';

-- Tabla: warehouse_locations (Ubicaciones en bodega)
CREATE TABLE tire_management.warehouse_locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    capacity INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.warehouse_locations IS 'Ubicaciones físicas de almacenamiento en bodega';

-- Tabla: removal_reasons (Motivos de desmontaje/baja)
CREATE TABLE tire_management.removal_reasons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    allows_reentry BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.removal_reasons IS 'Motivos de desmontaje y baja de llantas';
COMMENT ON COLUMN tire_management.removal_reasons.allows_reentry IS 'Indica si la llanta puede volver a circular después de desmontaje';

-- Tabla: tread_compounds (Neumáticos para reencauche)
CREATE TABLE tire_management.tread_compounds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    brand_id UUID NOT NULL REFERENCES tire_management.brands(id),
    compound_type INTEGER NOT NULL CHECK (compound_type IN (0, 1)),
    quantity_purchased INTEGER NOT NULL DEFAULT 0,
    current_stock INTEGER NOT NULL DEFAULT 0,
    unit_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    supplier_id UUID NOT NULL REFERENCES tire_management.suppliers(id),
    invoice_number VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.tread_compounds IS 'Catálogo de neumáticos para reencauche';
COMMENT ON COLUMN tire_management.tread_compounds.compound_type IS '0: Neumático, 1: Protector';

-- Tabla: protectors (Protectores)
CREATE TABLE tire_management.protectors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    brand_id UUID NOT NULL REFERENCES tire_management.brands(id),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.protectors IS 'Catálogo de protectores para llantas';

-- =====================================================
-- TABLAS TIRE_MANAGEMENT: FICHAS TÉCNICAS
-- =====================================================

-- Tabla: technical_specifications (Fichas técnicas)
CREATE TABLE tire_management.technical_specifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    brand_id UUID NOT NULL REFERENCES tire_management.brands(id),
    type_id UUID NOT NULL REFERENCES tire_management.types(id),
    reference_id UUID NOT NULL REFERENCES tire_management.tire_references(id),
    dimension VARCHAR(20),

    expected_mileage INTEGER NOT NULL CHECK (expected_mileage > 0),
    mileage_range_min INTEGER CHECK (mileage_range_min >= 0),
    mileage_range_avg INTEGER CHECK (mileage_range_avg >= 0),
    mileage_range_max INTEGER CHECK (mileage_range_max >= 0),
    expected_retreads SMALLINT NOT NULL DEFAULT 0 CHECK (expected_retreads >= 0),
    expected_loss_percentage NUMERIC(5,2),
    total_expected INTEGER,
    cost_per_hour NUMERIC(10,2),

    initial_depth_internal_mm NUMERIC(4,1) NOT NULL CHECK (initial_depth_internal_mm >= 0 AND initial_depth_internal_mm <= 99.9),
    initial_depth_central_mm NUMERIC(4,1) NOT NULL CHECK (initial_depth_central_mm >= 0 AND initial_depth_central_mm <= 99.9),
    initial_depth_external_mm NUMERIC(4,1) NOT NULL CHECK (initial_depth_external_mm >= 0 AND initial_depth_external_mm <= 99.9),

    last_purchase_quantity INTEGER,
    last_purchase_unit_price NUMERIC(12,2),
    last_purchase_date DATE,

    main_supplier_id UUID REFERENCES tire_management.suppliers(id),
    secondary_supplier_id UUID REFERENCES tire_management.suppliers(id),
    last_used_supplier_id UUID REFERENCES tire_management.suppliers(id),

    weight_kg INTEGER,

    expected_performance JSONB,

    is_active BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id)
);

COMMENT ON TABLE tire_management.technical_specifications IS 'Especificaciones técnicas y expectativas de rendimiento por tipo de llanta';
COMMENT ON COLUMN tire_management.technical_specifications.expected_performance IS 'Datos de rendimiento JSON: {cost_per_km, pressure_range: {min, max}}';

-- =====================================================
-- TABLAS TIRE_MANAGEMENT: CORE DOMAIN
-- =====================================================

-- Tabla: tires (Aggregate Root)
CREATE TABLE tire_management.tires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_number VARCHAR(20) NOT NULL,
    generation CHAR(3) NOT NULL CHECK (generation ~ '^\d{3}$'),
    current_state tire_management.tire_state NOT NULL DEFAULT 'INVENTORY',
    technical_specification_id UUID NOT NULL REFERENCES tire_management.technical_specifications(id),

    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    CONSTRAINT uq_tires_number UNIQUE (tire_number)
);

COMMENT ON TABLE tire_management.tires IS 'Aggregate root: Llantas del sistema. UN SOLO registro por llanta física.';
COMMENT ON COLUMN tire_management.tires.tire_number IS 'Identificador único de la llanta física. INMUTABLE a lo largo de su vida.';
COMMENT ON COLUMN tire_management.tires.generation IS 'Código de 3 dígitos [VV][R]: VV=vehículos (00-99), R=reencauches (0-9). Ejemplo: "032" = 3 vehículos, 2 reencauches. Campo MUTABLE: se actualiza al montar (+1 veh) o reencauchar (reset a 00, +1 reenc). Reglas: (1) Incrementa al montar en nuevo vehículo: "020"→"030", (2) NO incrementa en rotación, (3) Reset al reencauchar: "030"→"001", (4) NO cambia en desmontaje.';
COMMENT ON COLUMN tire_management.tires.current_state IS 'Estado actual en el ciclo de vida: INVENTORY, ACTIVE, INTERMEDIATE, RETIRED';

-- Tabla: inventory (Estado: INVENTORY)
CREATE TABLE tire_management.inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    warehouse_location_id UUID NOT NULL REFERENCES tire_management.warehouse_locations(id),

    purchase_cost NUMERIC(12,2) NOT NULL CHECK (purchase_cost >= 0),
    purchase_date DATE NOT NULL,
    supplier_id UUID NOT NULL REFERENCES tire_management.suppliers(id),
    invoice_number VARCHAR(50) NOT NULL,

    is_retreaded BOOLEAN NOT NULL DEFAULT false,
    tread_compound_id UUID REFERENCES tire_management.tread_compounds(id),
    retreading_cost NUMERIC(12,2) CHECK (retreading_cost >= 0),
    protector_id UUID REFERENCES tire_management.protectors(id),
    protector_cost NUMERIC(12,2) CHECK (protector_cost >= 0),

    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    CONSTRAINT uq_inventory_tire UNIQUE (tire_id)
);

COMMENT ON TABLE tire_management.inventory IS 'Llantas en inventario (bodega) sin montar';

-- Tabla: active_installations (Estado: ACTIVE)
CREATE TABLE tire_management.active_installations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    vehicle_id UUID NOT NULL REFERENCES shared.vehicles(id) ON DELETE RESTRICT,
    position SMALLINT NOT NULL CHECK (position >= 1 AND position <= 32),

    purchase_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    supplier_id UUID NOT NULL REFERENCES tire_management.suppliers(id),
    invoice_number VARCHAR(50) NOT NULL,

    is_retreaded BOOLEAN NOT NULL DEFAULT false,
    tread_compound_id UUID REFERENCES tire_management.tread_compounds(id),
    retreading_cost NUMERIC(12,2),
    protector_id UUID REFERENCES tire_management.protectors(id),
    protector_cost NUMERIC(12,2),

    mileage_at_installation INTEGER NOT NULL CHECK (mileage_at_installation >= 0),
    installation_date DATE NOT NULL,

    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    CONSTRAINT uq_active_installations_tire UNIQUE (tire_id),
    CONSTRAINT uq_active_installations_vehicle_position UNIQUE (vehicle_id, position)
);

COMMENT ON TABLE tire_management.active_installations IS 'Llantas actualmente instaladas en vehículos';
COMMENT ON COLUMN tire_management.active_installations.vehicle_id IS 'FK cross-schema a shared.vehicles';
COMMENT ON COLUMN tire_management.active_installations.position IS 'Posición física en el vehículo (1-N según clase)';
COMMENT ON CONSTRAINT uq_active_installations_vehicle_position ON tire_management.active_installations IS 'Garantiza unicidad: una posición = una llanta';

-- Tabla: intermediate (Estado: INTERMEDIATE)
CREATE TABLE tire_management.intermediate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id) ON DELETE RESTRICT,

    purchase_cost NUMERIC(12,2) NOT NULL,
    technical_specification_id UUID NOT NULL REFERENCES tire_management.technical_specifications(id),

    evaluation_status tire_management.evaluation_status NOT NULL DEFAULT 'PENDING',
    evaluation_date DATE,
    evaluation_supplier_id UUID REFERENCES tire_management.suppliers(id),
    evaluation_notes TEXT,

    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    CONSTRAINT uq_intermediate_tire UNIQUE (tire_id)
);

COMMENT ON TABLE tire_management.intermediate IS 'Llantas desmontadas en evaluación para recirculación, reencauche o baja';

-- Tabla: retired (Estado: RETIRED)
CREATE TABLE tire_management.retired (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    technical_specification_id UUID NOT NULL REFERENCES tire_management.technical_specifications(id),

    residual_value NUMERIC(12,2) NOT NULL DEFAULT 0,
    retirement_certificate_number VARCHAR(50) NOT NULL,
    retirement_date DATE NOT NULL DEFAULT CURRENT_DATE,
    retired_by_user VARCHAR(200) NOT NULL,
    removal_reason_id UUID REFERENCES tire_management.removal_reasons(id),
    retirement_notes TEXT,

    life_metrics JSONB,

    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    CONSTRAINT uq_retired_tire UNIQUE (tire_id)
);

COMMENT ON TABLE tire_management.retired IS 'Llantas dadas de baja definitivamente';
COMMENT ON COLUMN tire_management.retired.life_metrics IS 'Métricas finales JSON: {total_mileage, total_retreads, total_cost, cost_per_km}';

-- =====================================================
-- TABLAS TIRE_MANAGEMENT: TRAZABILIDAD
-- =====================================================

-- Tabla: history_records (Event Sourcing) - PARTICIONADA
CREATE TABLE tire_management.history_records (
    id UUID DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    generation_at_event CHAR(3) NOT NULL,

    purchase_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    supplier_id UUID NOT NULL REFERENCES tire_management.suppliers(id),
    invoice_number VARCHAR(50) NOT NULL,
    technical_specification_id UUID NOT NULL REFERENCES tire_management.technical_specifications(id),

    is_retreaded BOOLEAN NOT NULL DEFAULT false,
    tread_compound_id UUID REFERENCES tire_management.tread_compounds(id),
    retreading_cost NUMERIC(12,2),
    protector_id UUID REFERENCES tire_management.protectors(id),
    protector_cost NUMERIC(12,2),

    vehicle_id UUID REFERENCES shared.vehicles(id),
    position SMALLINT CHECK (position >= 1 AND position <= 32),
    mileage_at_installation INTEGER CHECK (mileage_at_installation >= 0),
    installation_date DATE,

    mileage_at_removal INTEGER CHECK (mileage_at_removal >= 0),
    removal_date DATE,
    removal_reason_id UUID REFERENCES tire_management.removal_reasons(id),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),

    CONSTRAINT chk_history_removal_after_installation CHECK (
        removal_date IS NULL OR installation_date IS NULL OR removal_date >= installation_date
    ),
    CONSTRAINT chk_history_mileage_increase CHECK (
        mileage_at_removal IS NULL OR mileage_at_installation IS NULL OR mileage_at_removal >= mileage_at_installation
    )
) PARTITION BY RANGE (created_at);

COMMENT ON TABLE tire_management.history_records IS 'Registro histórico INMUTABLE (append-only): cada evento (montaje/desmontaje/reencauche) = 1 registro';
COMMENT ON COLUMN tire_management.history_records.generation_at_event IS 'Snapshot del valor de generation en el momento del evento. Ejemplo: Al montar post-reencauche captura "011". Permite reconstruir historial completo de la llanta.';
COMMENT ON COLUMN tire_management.history_records.vehicle_id IS 'FK cross-schema a shared.vehicles: NULLABLE en eventos de reencauche (sin vehículo), NOT NULL en eventos de montaje';
COMMENT ON COLUMN tire_management.history_records.mileage_at_removal IS 'NULLABLE hasta desmontaje: se completa al finalizar el montaje';

-- Crear particiones por año
CREATE TABLE tire_management.history_records_2024 PARTITION OF tire_management.history_records
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE tire_management.history_records_2025 PARTITION OF tire_management.history_records
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE tire_management.history_records_2026 PARTITION OF tire_management.history_records
FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

CREATE TABLE tire_management.history_records_default PARTITION OF tire_management.history_records
DEFAULT;

-- Tabla: accumulated_mileage (Kilometraje acumulado)
CREATE TABLE tire_management.accumulated_mileage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    generation CHAR(3) NOT NULL,
    total_accumulated_mileage INTEGER NOT NULL DEFAULT 0 CHECK (total_accumulated_mileage >= 0),
    last_updated_date DATE NOT NULL DEFAULT CURRENT_DATE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_accumulated_mileage_tire_gen UNIQUE (tire_id, generation)
);

COMMENT ON TABLE tire_management.accumulated_mileage IS 'Kilometraje acumulado total por llanta y generación';

-- =====================================================
-- TABLAS TIRE_MANAGEMENT: MUESTREOS
-- =====================================================

-- Tabla: samplings (Muestreos actuales)
CREATE TABLE tire_management.samplings (
    id BIGSERIAL PRIMARY KEY,
    tire_installation_id UUID NOT NULL REFERENCES tire_management.active_installations(id) ON DELETE CASCADE,

    sampling_date DATE NOT NULL,
    vehicle_mileage_at_sampling INTEGER NOT NULL CHECK (vehicle_mileage_at_sampling >= 0),

    depth_internal_mm NUMERIC(4,1) NOT NULL CHECK (depth_internal_mm >= 0 AND depth_internal_mm <= 99.9),
    depth_central_mm NUMERIC(4,1) NOT NULL CHECK (depth_central_mm >= 0 AND depth_central_mm <= 99.9),
    depth_external_mm NUMERIC(4,1) NOT NULL CHECK (depth_external_mm >= 0 AND depth_external_mm <= 99.9),

    pressure_psi INTEGER NOT NULL CHECK (pressure_psi >= 0 AND pressure_psi <= 200),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),

    CONSTRAINT uq_sampling_installation_mileage UNIQUE (tire_installation_id, vehicle_mileage_at_sampling)
);

COMMENT ON TABLE tire_management.samplings IS 'Muestreos actuales de llantas activas';

-- Tabla: sampling_history (Histórico inmutable)
CREATE TABLE tire_management.sampling_history (
    id BIGSERIAL PRIMARY KEY,
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id),
    generation CHAR(3) NOT NULL,

    sampling_date DATE NOT NULL,
    vehicle_mileage_at_sampling INTEGER NOT NULL,

    depth_internal_mm NUMERIC(4,1) NOT NULL,
    depth_central_mm NUMERIC(4,1) NOT NULL,
    depth_external_mm NUMERIC(4,1) NOT NULL,

    pressure_psi INTEGER NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tire_management.sampling_history IS 'Histórico inmutable (append-only) de todos los muestreos';

-- =====================================================
-- TABLAS TIRE_MANAGEMENT: ALERTAS
-- =====================================================

-- Tabla: alerts (Sistema de alertas)
CREATE TABLE tire_management.alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_type tire_management.alert_type NOT NULL,
    priority tire_management.alert_priority NOT NULL,

    tire_id UUID REFERENCES tire_management.tires(id),
    tire_installation_id UUID REFERENCES tire_management.active_installations(id),
    vehicle_id UUID REFERENCES shared.vehicles(id),

    message TEXT NOT NULL,
    alert_data JSONB,

    is_acknowledged BOOLEAN NOT NULL DEFAULT false,
    acknowledged_at TIMESTAMP WITH TIME ZONE,
    acknowledged_by BIGINT REFERENCES users(id),
    resolution_notes TEXT,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tire_management.alerts IS 'Sistema de alertas de negocio del módulo de llantas';

-- =====================================================
-- ÍNDICES: SHARED SCHEMA
-- =====================================================

-- Índices: vehicle_classes
CREATE INDEX idx_vehicle_classes_code ON shared.vehicle_classes(code);
CREATE INDEX idx_vehicle_classes_category ON shared.vehicle_classes(category);

-- Índices: vehicles
CREATE INDEX idx_vehicles_license_plate ON shared.vehicles(license_plate);
CREATE INDEX idx_vehicles_class ON shared.vehicles(vehicle_class_id);
CREATE INDEX idx_vehicles_status ON shared.vehicles(status);
CREATE INDEX idx_vehicles_active ON shared.vehicles(id) WHERE is_active = true AND deleted_at IS NULL;

-- =====================================================
-- ÍNDICES: TIRE_MANAGEMENT SCHEMA
-- =====================================================

-- Índices: tires
CREATE INDEX idx_tires_number_generation ON tire_management.tires(tire_number, generation);
CREATE INDEX idx_tires_state ON tire_management.tires(current_state) WHERE deleted_at IS NULL;
CREATE INDEX idx_tires_tech_spec ON tire_management.tires(technical_specification_id);
CREATE INDEX idx_tires_not_deleted ON tire_management.tires(id) WHERE deleted_at IS NULL;

-- Índices: inventory
CREATE INDEX idx_inventory_tire ON tire_management.inventory(tire_id);
CREATE INDEX idx_inventory_location ON tire_management.inventory(warehouse_location_id);
CREATE INDEX idx_inventory_purchase_date ON tire_management.inventory(purchase_date DESC);
CREATE INDEX idx_inventory_active ON tire_management.inventory(id) WHERE deleted_at IS NULL;

-- Índices: active_installations
CREATE INDEX idx_active_installations_tire ON tire_management.active_installations(tire_id);
CREATE INDEX idx_active_installations_vehicle ON tire_management.active_installations(vehicle_id);
CREATE INDEX idx_active_installations_vehicle_position ON tire_management.active_installations(vehicle_id, position);
CREATE INDEX idx_active_installations_install_date ON tire_management.active_installations(installation_date DESC);
CREATE INDEX idx_active_installations_not_deleted ON tire_management.active_installations(id) WHERE deleted_at IS NULL;

-- Índices: intermediate
CREATE INDEX idx_intermediate_tire ON tire_management.intermediate(tire_id);
CREATE INDEX idx_intermediate_status ON tire_management.intermediate(evaluation_status);
CREATE INDEX idx_intermediate_pending ON tire_management.intermediate(id) WHERE evaluation_status = 'PENDING' AND deleted_at IS NULL;

-- Índices: retired
CREATE INDEX idx_retired_tire ON tire_management.retired(tire_id);
CREATE INDEX idx_retired_date ON tire_management.retired(retirement_date DESC);
CREATE INDEX idx_retired_reason ON tire_management.retired(removal_reason_id);

-- Índices: history_records
CREATE INDEX idx_history_tire_gen ON tire_management.history_records(tire_id, generation_at_event);
CREATE INDEX idx_history_vehicle ON tire_management.history_records(vehicle_id) WHERE vehicle_id IS NOT NULL;
CREATE INDEX idx_history_install_date ON tire_management.history_records(installation_date DESC) WHERE installation_date IS NOT NULL;

-- Índices: accumulated_mileage
-- Nota: generation en esta tabla representa el dígito de reencauche (última generación)
CREATE INDEX idx_accumulated_tire_gen ON tire_management.accumulated_mileage(tire_id, generation);

-- Índices: samplings
CREATE INDEX idx_samplings_installation ON tire_management.samplings(tire_installation_id);
CREATE INDEX idx_samplings_date ON tire_management.samplings(sampling_date DESC);
CREATE INDEX idx_samplings_installation_date ON tire_management.samplings(tire_installation_id, sampling_date DESC);

-- Índices: sampling_history
CREATE INDEX idx_sampling_history_tire_gen ON tire_management.sampling_history(tire_id, generation);
CREATE INDEX idx_sampling_history_date ON tire_management.sampling_history(sampling_date DESC);
CREATE INDEX idx_sampling_history_tire_date ON tire_management.sampling_history(tire_id, sampling_date DESC);

-- Índices: alerts
CREATE INDEX idx_alerts_tire ON tire_management.alerts(tire_id);
CREATE INDEX idx_alerts_installation ON tire_management.alerts(tire_installation_id);
CREATE INDEX idx_alerts_vehicle ON tire_management.alerts(vehicle_id);
CREATE INDEX idx_alerts_priority ON tire_management.alerts(priority) WHERE is_acknowledged = false;
CREATE INDEX idx_alerts_created ON tire_management.alerts(created_at DESC);

-- Índices: technical_specifications
CREATE INDEX idx_tech_specs_brand ON tire_management.technical_specifications(brand_id);
CREATE INDEX idx_tech_specs_type ON tire_management.technical_specifications(type_id);
CREATE INDEX idx_tech_specs_reference ON tire_management.technical_specifications(reference_id);
CREATE INDEX idx_tech_specs_active ON tire_management.technical_specifications(id) WHERE is_active = true AND deleted_at IS NULL;

-- Índices GIN para JSONB
CREATE INDEX idx_tech_specs_performance_gin ON tire_management.technical_specifications USING GIN(expected_performance);
CREATE INDEX idx_retired_metrics_gin ON tire_management.retired USING GIN(life_metrics);

-- =====================================================
-- TRIGGERS: SHARED SCHEMA
-- =====================================================

CREATE TRIGGER set_updated_at_vehicle_classes
BEFORE UPDATE ON shared.vehicle_classes
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_vehicles
BEFORE UPDATE ON shared.vehicles
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- TRIGGERS: TIRE_MANAGEMENT SCHEMA
-- =====================================================

-- Triggers: updated_at
CREATE TRIGGER set_updated_at_brands
BEFORE UPDATE ON tire_management.brands
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_types
BEFORE UPDATE ON tire_management.types
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_tire_references
BEFORE UPDATE ON tire_management.tire_references
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_suppliers
BEFORE UPDATE ON tire_management.suppliers
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_warehouse_locations
BEFORE UPDATE ON tire_management.warehouse_locations
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_removal_reasons
BEFORE UPDATE ON tire_management.removal_reasons
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_tread_compounds
BEFORE UPDATE ON tire_management.tread_compounds
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_protectors
BEFORE UPDATE ON tire_management.protectors
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_technical_specifications
BEFORE UPDATE ON tire_management.technical_specifications
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_tires
BEFORE UPDATE ON tire_management.tires
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_inventory
BEFORE UPDATE ON tire_management.inventory
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_active_installations
BEFORE UPDATE ON tire_management.active_installations
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_intermediate
BEFORE UPDATE ON tire_management.intermediate
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_retired
BEFORE UPDATE ON tire_management.retired
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_updated_at_accumulated_mileage
BEFORE UPDATE ON tire_management.accumulated_mileage
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Trigger: Validar unicidad de posición
CREATE OR REPLACE FUNCTION tire_management.validate_position_uniqueness()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM tire_management.active_installations
        WHERE vehicle_id = NEW.vehicle_id
        AND position = NEW.position
        AND deleted_at IS NULL
        AND id != COALESCE(NEW.id, '00000000-0000-0000-0000-000000000000'::UUID)
    ) THEN
        RAISE EXCEPTION 'Position % in vehicle % is already occupied', NEW.position, NEW.vehicle_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_position_uniqueness
BEFORE INSERT OR UPDATE ON tire_management.active_installations
FOR EACH ROW
EXECUTE FUNCTION tire_management.validate_position_uniqueness();

-- Trigger: Validar kilometraje al instalar
CREATE OR REPLACE FUNCTION tire_management.validate_installation_mileage()
RETURNS TRIGGER AS $$
DECLARE
    v_current_mileage INTEGER;
BEGIN
    -- Query cross-schema a shared.vehicles
    SELECT current_mileage INTO v_current_mileage
    FROM shared.vehicles
    WHERE id = NEW.vehicle_id;

    IF NEW.mileage_at_installation < v_current_mileage THEN
        RAISE EXCEPTION 'Installation mileage (%) cannot be less than vehicle current mileage (%)',
            NEW.mileage_at_installation, v_current_mileage;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_installation_mileage_trigger
BEFORE INSERT ON tire_management.active_installations
FOR EACH ROW
EXECUTE FUNCTION tire_management.validate_installation_mileage();

-- Trigger: Duplicar muestreo a histórico
CREATE OR REPLACE FUNCTION tire_management.duplicate_sampling_to_history()
RETURNS TRIGGER AS $$
DECLARE
    v_tire_id UUID;
    v_generation CHAR(3);
BEGIN
    SELECT ai.tire_id, t.generation
    INTO v_tire_id, v_generation
    FROM tire_management.active_installations ai
    JOIN tire_management.tires t ON ai.tire_id = t.id
    WHERE ai.id = NEW.tire_installation_id;

    INSERT INTO tire_management.sampling_history (
        tire_id,
        generation,
        sampling_date,
        vehicle_mileage_at_sampling,
        depth_internal_mm,
        depth_central_mm,
        depth_external_mm,
        pressure_psi,
        created_at
    ) VALUES (
        v_tire_id,
        v_generation,
        NEW.sampling_date,
        NEW.vehicle_mileage_at_sampling,
        NEW.depth_internal_mm,
        NEW.depth_central_mm,
        NEW.depth_external_mm,
        NEW.pressure_psi,
        NEW.created_at
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER duplicate_sampling_trigger
AFTER INSERT ON tire_management.samplings
FOR EACH ROW
EXECUTE FUNCTION tire_management.duplicate_sampling_to_history();

-- =====================================================
-- VISTAS
-- =====================================================

-- Vista: Llantas con contadores descompuestos
-- NOTA: Comentada temporalmente - requiere revisión del esquema
-- Esta vista intentaba acceder a columnas que no existen en la tabla tires
-- (purchase_value, purchase_date, supplier_id, invoice_number)
/*
CREATE OR REPLACE VIEW tire_management.v_tires_with_counters AS
SELECT
    t.id,
    t.tire_number,
    t.generation,
    tire_management.get_vehicle_count(t.generation) AS vehicle_count,
    tire_management.get_retread_count(t.generation) AS retread_count,
    t.current_state,
    t.technical_specification_id,
    ts.dimension,
    ts.brand_name,
    ts.type_name,
    ts.reference_name,
    ts.expected_mileage,
    ts.expected_retreads,
    t.created_at,
    t.updated_at
FROM tire_management.tires t
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id
WHERE t.deleted_at IS NULL;

COMMENT ON VIEW tire_management.v_tires_with_counters
    IS 'Vista que expone todas las llantas con generation descompuesto en vehicle_count y retread_count. Útil para análisis de vida útil y filtrado por estado de la llanta.';
*/

-- Vista: Llantas activas con último muestreo
CREATE OR REPLACE VIEW tire_management.v_active_installations_with_sampling AS
SELECT
    ai.id AS installation_id,
    ai.tire_id,
    t.tire_number,
    t.generation,
    tire_management.get_vehicle_count(t.generation) AS vehicle_count,
    tire_management.get_retread_count(t.generation) AS retread_count,

    -- Datos del vehículo (cross-schema)
    v.id AS vehicle_id,
    v.license_plate,
    v.brand AS vehicle_brand,
    v.model_year AS vehicle_model,
    v.current_mileage AS vehicle_current_mileage,
    vc.name AS vehicle_class_name,

    ai.position,
    ai.mileage_at_installation,
    ai.installation_date,

    ts.dimension,
    ts.initial_depth_internal_mm,
    ts.initial_depth_central_mm,
    ts.initial_depth_external_mm,
    ROUND((ts.initial_depth_internal_mm + ts.initial_depth_central_mm + ts.initial_depth_external_mm) / 3.0, 2) AS initial_avg_depth,

    s.sampling_date AS last_sampling_date,
    s.vehicle_mileage_at_sampling AS last_sampling_mileage,
    s.depth_internal_mm AS current_depth_internal,
    s.depth_central_mm AS current_depth_central,
    s.depth_external_mm AS current_depth_external,
    ROUND((s.depth_internal_mm + s.depth_central_mm + s.depth_external_mm) / 3.0, 2) AS current_avg_depth,
    s.pressure_psi AS current_pressure,

    am.total_accumulated_mileage

FROM tire_management.active_installations ai
JOIN tire_management.tires t ON ai.tire_id = t.id
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id

-- Joins cross-schema
JOIN shared.vehicles v ON ai.vehicle_id = v.id
JOIN shared.vehicle_classes vc ON v.vehicle_class_id = vc.id

LEFT JOIN LATERAL (
    SELECT *
    FROM tire_management.samplings
    WHERE tire_installation_id = ai.id
    ORDER BY sampling_date DESC
    LIMIT 1
) s ON true

LEFT JOIN tire_management.accumulated_mileage am ON am.tire_id = t.id AND am.generation = t.generation

WHERE ai.deleted_at IS NULL
  AND v.deleted_at IS NULL;

COMMENT ON VIEW tire_management.v_active_installations_with_sampling
    IS 'Vista con llantas activas, último muestreo e información del vehículo (incluye datos cross-schema)';

-- Vista: Consumo mensual
CREATE OR REPLACE VIEW tire_management.v_monthly_consumption AS
WITH new_tires_inventory AS (
    SELECT
        EXTRACT(YEAR FROM purchase_date) AS year,
        EXTRACT(MONTH FROM purchase_date) AS month,
        COUNT(*) AS qty
    FROM tire_management.inventory
    WHERE tire_id IN (SELECT id FROM tire_management.tires WHERE generation = '000')
    AND deleted_at IS NULL
    GROUP BY EXTRACT(YEAR FROM purchase_date), EXTRACT(MONTH FROM purchase_date)
),
new_tires_active AS (
    SELECT
        EXTRACT(YEAR FROM purchase_date) AS year,
        EXTRACT(MONTH FROM purchase_date) AS month,
        COUNT(*) AS qty
    FROM tire_management.active_installations
    WHERE tire_id IN (SELECT id FROM tire_management.tires WHERE generation = '000')
    AND deleted_at IS NULL
    GROUP BY EXTRACT(YEAR FROM purchase_date), EXTRACT(MONTH FROM purchase_date)
),
new_tires_history AS (
    SELECT
        EXTRACT(YEAR FROM purchase_date) AS year,
        EXTRACT(MONTH FROM purchase_date) AS month,
        COUNT(*) AS qty
    FROM tire_management.history_records
    WHERE generation_at_event = '000'
    AND tire_id NOT IN (SELECT tire_id FROM tire_management.active_installations WHERE deleted_at IS NULL)
    GROUP BY EXTRACT(YEAR FROM purchase_date), EXTRACT(MONTH FROM purchase_date)
)
SELECT
    year,
    month,
    SUM(qty) AS total_consumption
FROM (
    SELECT * FROM new_tires_inventory
    UNION ALL
    SELECT * FROM new_tires_active
    UNION ALL
    SELECT * FROM new_tires_history
) combined
GROUP BY year, month
ORDER BY year DESC, month DESC;

-- =====================================================
-- VISTA MATERIALIZADA: Vida Útil
-- =====================================================

CREATE MATERIALIZED VIEW tire_management.mv_useful_life AS
SELECT
    ai.id AS installation_id,
    ai.tire_id,
    t.tire_number,
    t.generation,
    tire_management.get_vehicle_count(t.generation) AS vehicle_count,
    tire_management.get_retread_count(t.generation) AS retread_count,

    -- Datos del vehículo (cross-schema)
    v.license_plate AS vehicle_license_plate,
    v.id AS vehicle_id,
    v.current_mileage AS vehicle_current_mileage,

    ai.position,

    s.depth_internal_mm AS current_depth_internal,
    s.depth_central_mm AS current_depth_central,
    s.depth_external_mm AS current_depth_external,
    ROUND((COALESCE(s.depth_internal_mm, 0) + COALESCE(s.depth_central_mm, 0) + COALESCE(s.depth_external_mm, 0)) / 3.0, 2) AS current_avg_depth,
    s.sampling_date AS last_sampling_date,

    am.total_accumulated_mileage,
    ai.mileage_at_installation,
    v.current_mileage - ai.mileage_at_installation AS mileage_since_installation,

    ts.expected_mileage,
    ts.initial_depth_internal_mm AS expected_depth_internal,
    ts.initial_depth_central_mm AS expected_depth_central,
    ts.initial_depth_external_mm AS expected_depth_external,
    ROUND((ts.initial_depth_internal_mm + ts.initial_depth_central_mm + ts.initial_depth_external_mm) / 3.0, 2) AS expected_avg_depth,

    CASE
        WHEN am.total_accumulated_mileage IS NOT NULL AND am.total_accumulated_mileage > 0
        THEN ROUND((am.total_accumulated_mileage::NUMERIC / ts.expected_mileage::NUMERIC) * 100, 2)
        ELSE 0
    END AS mileage_efficiency_percentage,

    CURRENT_TIMESTAMP AS refreshed_at

FROM tire_management.active_installations ai
JOIN tire_management.tires t ON ai.tire_id = t.id
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id

-- Join cross-schema
JOIN shared.vehicles v ON ai.vehicle_id = v.id

LEFT JOIN LATERAL (
    SELECT *
    FROM tire_management.samplings
    WHERE tire_installation_id = ai.id
    ORDER BY sampling_date DESC
    LIMIT 1
) s ON true

LEFT JOIN tire_management.accumulated_mileage am ON am.tire_id = t.id AND am.generation = t.generation

WHERE ai.deleted_at IS NULL
  AND v.deleted_at IS NULL;

CREATE UNIQUE INDEX ON tire_management.mv_useful_life(installation_id);

COMMENT ON MATERIALIZED VIEW tire_management.mv_useful_life
    IS 'Vista materializada con proyecciones de vida útil (incluye datos cross-schema de vehículos)';

-- =====================================================
-- PERMISOS (Opcional - según estrategia de seguridad)
-- =====================================================

-- Ejemplo de permisos granulares por esquema
-- GRANT USAGE ON SCHEMA tire_management TO tire_management_role;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA tire_management TO tire_management_role;
-- GRANT USAGE ON SCHEMA shared TO tire_management_role;
-- GRANT SELECT ON ALL TABLES IN SCHEMA shared TO tire_management_role;

-- =====================================================
-- FIN DE LA MIGRACIÓN V2.0.0
-- =====================================================
