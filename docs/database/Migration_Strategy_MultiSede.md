# Estrategia de Migración Multi-Sede

**Versión:** 1.0
**Fecha:** 27 de Enero de 2026
**Autor:** Equipo de Migración TRANSER Vórtice

---

## 📋 TABLA DE CONTENIDO

1. [Introducción](#1-introducción)
2. [Análisis del Sistema Legacy](#2-análisis-del-sistema-legacy)
3. [Estrategia de Migración](#3-estrategia-de-migración)
4. [Fases de Implementación](#4-fases-de-implementación)
5. [Scripts de Migración](#5-scripts-de-migración)
6. [Validación y Testing](#6-validación-y-testing)
7. [Rollback Plan](#7-rollback-plan)
8. [Checklist de Ejecución](#8-checklist-de-ejecución)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento define la estrategia para migrar el sistema legacy (Oracle Forms 6i / Oracle 11g) a la nueva arquitectura multi-sede en PostgreSQL 18, incluyendo scripts, validaciones y plan de rollback.

### 1.2 Contexto

**Sistema Legacy:**
- Oracle Forms 6i (cliente/servidor)
- Oracle 11g Database
- **NO soporta multi-sede** (una sola ubicación)
- Tablas clave: `LOCALIZA`, `PROVEEDORES_LLANTAS`, `INVENTARIO`, `LLANTAS`, `HISTORIA`

**Sistema Modernizado:**
- Spring Boot 3.5 + React 18
- PostgreSQL 18
- **Arquitectura multi-sede** con Row-Level Security
- Tablas nuevas: `offices`, `warehouses`, `warehouse_locations`, `tire_suppliers`

### 1.3 Objetivos

- ✅ Migrar datos legacy sin pérdida de información
- ✅ Crear estructura organizacional por defecto (oficina principal)
- ✅ Mantener integridad referencial
- ✅ Permitir rollback seguro
- ✅ Minimizar tiempo de inactividad (downtime)
- ✅ Validar 100% de los datos migrados

---

## 2. ANÁLISIS DEL SISTEMA LEGACY

### 2.1 Tablas Legacy Relevantes

| Tabla Legacy | Descripción | Registros Estimados | Migra a |
|--------------|-------------|---------------------|---------|
| `PARAMETROS_OFICSISTEMA` | Parámetros de configuración por oficina (existe pero no se usa realmente) | ~10 | `offices` |
| `LOCALIZA` | Ubicaciones de inventario (sin concepto de almacén) | ~50 | `warehouse_locations` |
| `PROVEEDORES_LLANTAS` | Proveedores de llantas (globales) | ~20 | `tire_suppliers` |
| `INVENTARIO` | Llantas en inventario sin montar | ~500 | `tire_inventory` |
| `LLANTAS` | Llantas montadas en vehículos | ~1,200 | `tire_active` |
| `INTERMEDIO` | Llantas desmontadas en evaluación | ~80 | `tire_intermediate` |
| `RETIRADAS` | Llantas dadas de baja | ~300 | `tire_retired` |
| `HISTORIA` | Histórico de movimientos de llantas | ~5,000 | `tire_history` |
| `FICHATEC` | Fichas técnicas de llantas (global) | ~80 | `tire_specifications` |
| `VEHICULOS_LLANTAS` | Vehículos de la flota | ~200 | `vehicles` |

### 2.2 Desafíos de Migración

**1. No hay concepto de "oficina" o "almacén" en el legacy:**
- **Solución:** Crear oficina principal por defecto ("MAIN") y almacén principal por defecto ("PRIN")
- Todas las ubicaciones (`LOCALIZA`) se asignan a este almacén por defecto
- Todos los proveedores se asignan a la oficina principal por defecto
- Todos los usuarios se asignan a la oficina principal por defecto

**2. Tipos de datos diferentes:**
- Oracle: `VARCHAR2`, `NUMBER`, `DATE`
- PostgreSQL: `VARCHAR`, `BIGINT`/`UUID`, `TIMESTAMP WITH TIME ZONE`
- **Solución:** Mapeo explícito en scripts de migración

**3. Claves primarias:**
- Legacy: Secuencias de Oracle (`NUMBER`)
- Modernizado: `UUID` para entidades principales, `BIGSERIAL` para secundarias
- **Solución:** Generar UUIDs nuevos y mantener mapeo de IDs legacy → nuevos

**4. Soft deletes:**
- Legacy: Campo `ESTADO` ('A' = activo, 'I' = inactivo)
- Modernizado: Campo `deleted_at` (NULL = activo, timestamp = eliminado)
- **Solución:** Convertir `ESTADO='I'` a `deleted_at=CURRENT_TIMESTAMP`

**5. Auditoría:**
- Legacy: Sin campos de auditoría consistentes
- Modernizado: `created_at`, `created_by`, `updated_at`, `updated_by`
- **Solución:** Usar timestamps de migración y usuario de sistema (id=1)

---

## 3. ESTRATEGIA DE MIGRACIÓN

### 3.1 Enfoque: Big Bang con Ventana de Mantenimiento

**Razones:**
- Base de datos relativamente pequeña (~10K registros críticos)
- No hay integración en tiempo real con sistemas externos
- Downtime aceptable de 4-6 horas durante fin de semana

**Alternativa Considerada (rechazada):**
- Migración incremental dual-write: Demasiado complejo para el tamaño del sistema
- Migración en fases por módulo: Requiere mantener ambos sistemas activos simultáneamente

### 3.2 Ventana de Migración

**Fecha Propuesta:** Sábado, [FECHA], 10:00 PM - Domingo, [FECHA], 4:00 AM
**Duración:** 6 horas máximo
**Downtime:** Sistema completamente inactivo durante la ventana

### 3.3 Estrategia de Datos

**Orden de Migración (respetando dependencias):**

```
1. Estructura Organizacional
   ├── offices (oficina principal)
   ├── warehouses (almacén principal)
   └── warehouse_locations (desde LOCALIZA)

2. Catálogos Globales
   ├── tire_brands (desde MARCAS_LLANTAS)
   ├── tire_types (desde TIPOS)
   ├── tire_references (desde REFERENCIA)
   ├── tire_specifications (desde FICHATEC)
   └── observation_reasons (desde OBSERVA)

3. Proveedores
   └── tire_suppliers (desde PROVEEDORES_LLANTAS → oficina principal)

4. Usuarios
   └── users (agregar office_id → oficina principal)

5. Vehículos
   └── vehicles (desde VEHICULOS_LLANTAS → oficina principal)

6. Datos Transaccionales de Llantas
   ├── tire_inventory (desde INVENTARIO)
   ├── tire_active (desde LLANTAS)
   ├── tire_intermediate (desde INTERMEDIO)
   ├── tire_retired (desde RETIRADAS)
   └── tire_history (desde HISTORIA)

7. Datos Históricos Adicionales
   ├── tire_sampling (desde MUESTREO)
   └── tire_sampling_history (desde HISTOMUES)
```

---

## 4. FASES DE IMPLEMENTACIÓN

### Fase 0: Preparación (1 semana antes)

**Actividades:**
- [ ] Backup completo de Oracle
- [ ] Crear base de datos PostgreSQL en ambiente de staging
- [ ] Ejecutar scripts de creación de schema
- [ ] Ejecutar migración en staging (dry-run)
- [ ] Validar datos migrados en staging
- [ ] Preparar checklist de validación
- [ ] Notificar a usuarios del downtime

**Duración:** 1 semana previa

### Fase 1: Inicio de Ventana de Mantenimiento

**Actividades:**
- [ ] Deshabilitar acceso al sistema legacy (Oracle Forms)
- [ ] Anunciar inicio de mantenimiento
- [ ] Backup final de Oracle
- [ ] Exportar datos de Oracle a archivos CSV/JSON
- [ ] Validar integridad de exportación

**Duración:** 30 minutos

### Fase 2: Migración de Estructura Organizacional

**Actividades:**
- [ ] Crear oficina principal
- [ ] Crear almacén principal
- [ ] Migrar ubicaciones desde LOCALIZA
- [ ] Validar constraint de unicidad (warehouse + code)
- [ ] Verificar cantidad de registros migrados

**Duración:** 15 minutos

**Script:** `001_migrate_organizational_structure.sql`

### Fase 3: Migración de Catálogos Globales

**Actividades:**
- [ ] Migrar marcas, tipos, referencias de llantas
- [ ] Migrar fichas técnicas
- [ ] Migrar motivos de observación
- [ ] Verificar integridad referencial
- [ ] Validar que no hay duplicados

**Duración:** 30 minutos

**Script:** `002_migrate_global_catalogs.sql`

### Fase 4: Migración de Proveedores

**Actividades:**
- [ ] Migrar proveedores a tire_suppliers
- [ ] Asignar todos a oficina principal
- [ ] Validar códigos únicos por oficina
- [ ] Verificar cantidad de registros

**Duración:** 15 minutos

**Script:** `003_migrate_suppliers.sql`

### Fase 5: Migración de Usuarios

**Actividades:**
- [ ] Agregar campo office_id a users
- [ ] Asignar todos los usuarios a oficina principal
- [ ] Verificar que no hay usuarios sin oficina
- [ ] Hacer campo office_id obligatorio

**Duración:** 15 minutos

**Script:** `004_migrate_users.sql`

### Fase 6: Migración de Datos Transaccionales

**Actividades:**
- [ ] Migrar inventario de llantas
- [ ] Migrar llantas activas
- [ ] Migrar llantas intermedias
- [ ] Migrar llantas retiradas
- [ ] Migrar histórico completo
- [ ] Verificar integridad referencial
- [ ] Validar cantidades por tabla

**Duración:** 2 horas

**Script:** `005_migrate_tire_data.sql`

### Fase 7: Habilitación de Row-Level Security

**Actividades:**
- [ ] Crear funciones RLS (get_user_office_id, current_user_has_role)
- [ ] Habilitar RLS en todas las tablas necesarias
- [ ] Crear políticas de aislamiento por oficina
- [ ] Probar RLS con usuarios de prueba

**Duración:** 30 minutos

**Script:** `006_enable_rls.sql`

### Fase 8: Validación Completa

**Actividades:**
- [ ] Ejecutar suite de validación completa
- [ ] Comparar cantidades: Legacy vs Modernizado
- [ ] Verificar integridad referencial
- [ ] Probar casos de uso críticos
- [ ] Verificar que RLS funciona correctamente

**Duración:** 1 hora

**Script:** `007_validate_migration.sql`

### Fase 9: Activación del Sistema Modernizado

**Actividades:**
- [ ] Configurar conexión a PostgreSQL en backend
- [ ] Iniciar aplicación Spring Boot
- [ ] Verificar logs de arranque
- [ ] Ejecutar smoke tests
- [ ] Habilitar acceso para usuarios piloto
- [ ] Monitorear primeras operaciones

**Duración:** 30 minutos

### Fase 10: Go-Live y Monitoreo

**Actividades:**
- [ ] Anunciar fin de mantenimiento
- [ ] Habilitar acceso para todos los usuarios
- [ ] Monitorear performance y errores
- [ ] Soporte en línea para usuarios
- [ ] Documentar incidencias

**Duración:** Continuo (primeras 24 horas)

---

## 5. SCRIPTS DE MIGRACIÓN

### 5.1 Script 001: Estructura Organizacional

**Archivo:** `001_migrate_organizational_structure.sql`

```sql
-- ============================================================================
-- MIGRACIÓN: Estructura Organizacional Multi-Sede
-- Descripción: Crea oficina y almacén principal, migra ubicaciones
-- ============================================================================

BEGIN;

-- ============================================================================
-- PASO 1: Crear oficina principal
-- ============================================================================
DO $$
DECLARE
    v_office_id UUID;
BEGIN
    -- Insertar oficina principal
    INSERT INTO offices (id, code, name, city, address, phone, is_active, created_at, created_by)
    VALUES (
        'a0000000-0000-0000-0000-000000000001'::UUID,
        'MAIN',
        'Oficina Principal',
        'Bogotá',
        'Dirección desde sistema legacy',
        'Teléfono desde sistema legacy',
        true,
        CURRENT_TIMESTAMP,
        1  -- Usuario de sistema
    )
    ON CONFLICT (id) DO NOTHING;

    v_office_id := 'a0000000-0000-0000-0000-000000000001'::UUID;

    RAISE NOTICE 'Oficina principal creada: %', v_office_id;
END $$;

-- ============================================================================
-- PASO 2: Crear almacén principal
-- ============================================================================
DO $$
DECLARE
    v_warehouse_id UUID;
    v_office_id UUID := 'a0000000-0000-0000-0000-000000000001'::UUID;
BEGIN
    INSERT INTO warehouses (id, code, name, office_id, description, is_active, created_at, created_by)
    VALUES (
        'b0000000-0000-0000-0000-000000000001'::UUID,
        'PRIN',
        'Almacén Principal',
        v_office_id,
        'Almacén principal de la oficina (migrado desde sistema legacy)',
        true,
        CURRENT_TIMESTAMP,
        1
    )
    ON CONFLICT (id) DO NOTHING;

    v_warehouse_id := 'b0000000-0000-0000-0000-000000000001'::UUID;

    RAISE NOTICE 'Almacén principal creado: %', v_warehouse_id;
END $$;

-- ============================================================================
-- PASO 3: Migrar ubicaciones desde LOCALIZA legacy
-- ============================================================================
DO $$
DECLARE
    v_warehouse_id UUID := 'b0000000-0000-0000-0000-000000000001'::UUID;
    v_count INTEGER;
BEGIN
    -- Migrar ubicaciones
    INSERT INTO warehouse_locations (code, name, warehouse_id, is_active, created_at, created_by, deleted_at)
    SELECT
        l.cod_local AS code,
        COALESCE(NULLIF(TRIM(l.descri), ''), l.cod_local) AS name,  -- Si descri está vacío, usar código
        v_warehouse_id,
        CASE WHEN UPPER(l.estado) = 'A' THEN true ELSE false END AS is_active,
        CURRENT_TIMESTAMP AS created_at,
        1 AS created_by,
        CASE WHEN UPPER(l.estado) != 'A' THEN CURRENT_TIMESTAMP ELSE NULL END AS deleted_at
    FROM localiza@legacy_db_link l
    WHERE l.cod_local IS NOT NULL
      AND LENGTH(TRIM(l.cod_local)) > 0
    ON CONFLICT (warehouse_id, code) DO NOTHING;

    GET DIAGNOSTICS v_count = ROW_COUNT;
    RAISE NOTICE 'Ubicaciones migradas: %', v_count;
END $$;

-- ============================================================================
-- VALIDACIÓN
-- ============================================================================
DO $$
DECLARE
    v_legacy_count INTEGER;
    v_new_count INTEGER;
BEGIN
    -- Contar registros en legacy
    SELECT COUNT(*) INTO v_legacy_count
    FROM localiza@legacy_db_link
    WHERE cod_local IS NOT NULL;

    -- Contar registros migrados
    SELECT COUNT(*) INTO v_new_count
    FROM warehouse_locations;

    RAISE NOTICE '=== VALIDACIÓN ===';
    RAISE NOTICE 'Ubicaciones en legacy: %', v_legacy_count;
    RAISE NOTICE 'Ubicaciones migradas: %', v_new_count;

    IF v_new_count < v_legacy_count THEN
        RAISE WARNING 'Diferencia detectada: % ubicaciones no migradas', (v_legacy_count - v_new_count);
    END IF;
END $$;

COMMIT;

-- ============================================================================
-- FIN DEL SCRIPT 001
-- ============================================================================
```

### 5.2 Script 002: Catálogos Globales

**Archivo:** `002_migrate_global_catalogs.sql`

```sql
-- ============================================================================
-- MIGRACIÓN: Catálogos Globales
-- Descripción: Migra marcas, tipos, referencias y fichas técnicas de llantas
-- ============================================================================

BEGIN;

-- ============================================================================
-- PASO 1: Migrar marcas de llantas
-- ============================================================================
INSERT INTO tire_brands (code, name, is_active, created_at, created_by, deleted_at)
SELECT
    m.codigo AS code,
    m.descripcion AS name,
    CASE WHEN UPPER(m.estado) = 'A' THEN true ELSE false END AS is_active,
    CURRENT_TIMESTAMP AS created_at,
    1 AS created_by,
    CASE WHEN UPPER(m.estado) != 'A' THEN CURRENT_TIMESTAMP ELSE NULL END AS deleted_at
FROM marcas_llantas@legacy_db_link m
WHERE m.codigo IS NOT NULL
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- PASO 2: Migrar tipos de llantas
-- ============================================================================
INSERT INTO tire_types (code, name, description, is_active, created_at, created_by, deleted_at)
SELECT
    t.codigo AS code,
    t.descripcion AS name,
    t.observaciones AS description,
    CASE WHEN UPPER(t.estado) = 'A' THEN true ELSE false END AS is_active,
    CURRENT_TIMESTAMP AS created_at,
    1 AS created_by,
    CASE WHEN UPPER(t.estado) != 'A' THEN CURRENT_TIMESTAMP ELSE NULL END AS deleted_at
FROM tipos@legacy_db_link t
WHERE t.codigo IS NOT NULL
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- PASO 3: Migrar referencias de llantas
-- ============================================================================
INSERT INTO tire_references (code, name, specifications, is_active, created_at, created_by, deleted_at)
SELECT
    r.codigo AS code,
    r.descripcion AS name,
    r.especificaciones AS specifications,
    CASE WHEN UPPER(r.estado) = 'A' THEN true ELSE false END AS is_active,
    CURRENT_TIMESTAMP AS created_at,
    1 AS created_by,
    CASE WHEN UPPER(r.estado) != 'A' THEN CURRENT_TIMESTAMP ELSE NULL END AS deleted_at
FROM referencia@legacy_db_link r
WHERE r.codigo IS NOT NULL
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- PASO 4: Migrar fichas técnicas de llantas
-- ============================================================================
INSERT INTO tire_specifications (
    code, dimension,
    brand_id, type_id, reference_id,
    expected_mileage, mileage_range_min, mileage_range_avg, mileage_range_max,
    expected_retreads,
    initial_depth_internal_mm, initial_depth_central_mm, initial_depth_external_mm,
    average_initial_depth,
    main_provider_id, secondary_provider_id, last_used_provider_id,
    is_active, created_at, created_by, deleted_at
)
SELECT
    f.codigo::TEXT AS code,
    f.dimension,
    b.id AS brand_id,  -- FK a tire_brands
    t.id AS type_id,   -- FK a tire_types
    r.id AS reference_id,  -- FK a tire_references
    f.kms_esperados AS expected_mileage,
    f.kms_mayor AS mileage_range_min,
    f.kms_menor AS mileage_range_avg,
    f.kms_medio AS mileage_range_max,
    f.reencauches AS expected_retreads,
    f.pi AS initial_depth_internal_mm,
    f.pc AS initial_depth_central_mm,
    f.pd AS initial_depth_external_mm,
    ((f.pi + f.pc + f.pd) / 3.0) AS average_initial_depth,
    p1.id AS main_provider_id,
    p2.id AS secondary_provider_id,
    p3.id AS last_used_provider_id,
    CASE WHEN UPPER(f.estado) = 'A' THEN true ELSE false END AS is_active,
    CURRENT_TIMESTAMP AS created_at,
    1 AS created_by,
    CASE WHEN UPPER(f.estado) != 'A' THEN CURRENT_TIMESTAMP ELSE NULL END AS deleted_at
FROM fichatec@legacy_db_link f
LEFT JOIN tire_brands b ON b.code = f.marca
LEFT JOIN tire_types t ON t.code = f.tipo
LEFT JOIN tire_references r ON r.code = f.referencia
LEFT JOIN tire_suppliers p1 ON p1.code = f.proveedor_principal
LEFT JOIN tire_suppliers p2 ON p2.code = f.proveedor_secundario
LEFT JOIN tire_suppliers p3 ON p3.code = f.ultimo_proveedor
WHERE f.codigo IS NOT NULL
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- VALIDACIÓN
-- ============================================================================
DO $$
DECLARE
    v_brands_legacy INTEGER;
    v_brands_new INTEGER;
    v_specs_legacy INTEGER;
    v_specs_new INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_brands_legacy FROM marcas_llantas@legacy_db_link;
    SELECT COUNT(*) INTO v_brands_new FROM tire_brands;

    SELECT COUNT(*) INTO v_specs_legacy FROM fichatec@legacy_db_link;
    SELECT COUNT(*) INTO v_specs_new FROM tire_specifications;

    RAISE NOTICE '=== VALIDACIÓN ===';
    RAISE NOTICE 'Marcas: Legacy=%, Migrado=%', v_brands_legacy, v_brands_new;
    RAISE NOTICE 'Fichas técnicas: Legacy=%, Migrado=%', v_specs_legacy, v_specs_new;
END $$;

COMMIT;

-- ============================================================================
-- FIN DEL SCRIPT 002
-- ============================================================================
```

### 5.3 Script 003: Proveedores

**Archivo:** `003_migrate_suppliers.sql`

```sql
-- ============================================================================
-- MIGRACIÓN: Proveedores de Llantas
-- Descripción: Migra proveedores asignándolos a la oficina principal
-- ============================================================================

BEGIN;

DO $$
DECLARE
    v_office_id UUID := 'a0000000-0000-0000-0000-000000000001'::UUID;
    v_count INTEGER;
BEGIN
    -- Migrar proveedores a tire_suppliers
    INSERT INTO tire_suppliers (
        code, name, tax_id, office_id,
        contact_name, email, phone, address,
        is_active, created_at, created_by, deleted_at
    )
    SELECT
        p.codigopro AS code,
        p.nombre AS name,
        p.nit AS tax_id,
        v_office_id AS office_id,
        p.contacto AS contact_name,
        p.email,
        p.telefono AS phone,
        p.direccion AS address,
        CASE WHEN UPPER(p.estado) = 'A' THEN true ELSE false END AS is_active,
        CURRENT_TIMESTAMP AS created_at,
        1 AS created_by,
        CASE WHEN UPPER(p.estado) != 'A' THEN CURRENT_TIMESTAMP ELSE NULL END AS deleted_at
    FROM proveedores_llantas@legacy_db_link p
    WHERE p.codigopro IS NOT NULL
    ON CONFLICT (office_id, code) DO NOTHING;

    GET DIAGNOSTICS v_count = ROW_COUNT;
    RAISE NOTICE 'Proveedores migrados: %', v_count;
END $$;

-- ============================================================================
-- VALIDACIÓN
-- ============================================================================
DO $$
DECLARE
    v_legacy_count INTEGER;
    v_new_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_legacy_count FROM proveedores_llantas@legacy_db_link;
    SELECT COUNT(*) INTO v_new_count FROM tire_suppliers;

    RAISE NOTICE '=== VALIDACIÓN ===';
    RAISE NOTICE 'Proveedores en legacy: %', v_legacy_count;
    RAISE NOTICE 'Proveedores migrados: %', v_new_count;

    IF v_new_count < v_legacy_count THEN
        RAISE WARNING 'Diferencia: % proveedores no migrados', (v_legacy_count - v_new_count);
    END IF;
END $$;

COMMIT;

-- ============================================================================
-- FIN DEL SCRIPT 003
-- ============================================================================
```

### 5.4 Script 004: Usuarios

**Archivo:** `004_migrate_users.sql`

```sql
-- ============================================================================
-- MIGRACIÓN: Asignación de Oficina a Usuarios
-- Descripción: Agrega office_id a usuarios existentes
-- ============================================================================

BEGIN;

DO $$
DECLARE
    v_office_id UUID := 'a0000000-0000-0000-0000-000000000001'::UUID;
    v_count INTEGER;
BEGIN
    -- Agregar columna office_id si no existe
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'office_id'
    ) THEN
        ALTER TABLE users ADD COLUMN office_id UUID REFERENCES offices(id);
        RAISE NOTICE 'Columna office_id agregada a tabla users';
    END IF;

    -- Asignar todos los usuarios a oficina principal
    UPDATE users
    SET office_id = v_office_id
    WHERE office_id IS NULL;

    GET DIAGNOSTICS v_count = ROW_COUNT;
    RAISE NOTICE 'Usuarios actualizados con office_id: %', v_count;

    -- Hacer columna obligatoria
    ALTER TABLE users ALTER COLUMN office_id SET NOT NULL;
    RAISE NOTICE 'Columna office_id configurada como NOT NULL';
END $$;

-- ============================================================================
-- VALIDACIÓN
-- ============================================================================
DO $$
DECLARE
    v_users_without_office INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_users_without_office
    FROM users
    WHERE office_id IS NULL;

    RAISE NOTICE '=== VALIDACIÓN ===';
    RAISE NOTICE 'Usuarios sin oficina asignada: %', v_users_without_office;

    IF v_users_without_office > 0 THEN
        RAISE EXCEPTION 'ERROR: Hay usuarios sin oficina asignada';
    END IF;
END $$;

COMMIT;

-- ============================================================================
-- FIN DEL SCRIPT 004
-- ============================================================================
```

### 5.5 Script 005: Datos Transaccionales

**Archivo:** `005_migrate_tire_data.sql`

```sql
-- ============================================================================
-- MIGRACIÓN: Datos Transaccionales de Llantas
-- Descripción: Migra inventario, llantas activas, intermedias, retiradas e histórico
-- Advertencia: Este script puede tomar varias horas dependiendo del volumen
-- ============================================================================

BEGIN;

DECLARE
    v_warehouse_id UUID := 'b0000000-0000-0000-0000-000000000001'::UUID;
    v_count INTEGER;

-- ============================================================================
-- PASO 1: Migrar inventario de llantas
-- ============================================================================
INSERT INTO tire_inventory (
    tire_number, "group", value, entry_date, invoice_number, notes,
    specification_id, supplier_id, warehouse_id, location_id,
    tire_code, retread_value, protector_code, protector_value,
    created_at, created_by
)
SELECT
    i.llanta AS tire_number,
    i.grupo AS "group",
    i.valor AS value,
    i.fecha AS entry_date,
    i.factura AS invoice_number,
    i.obs AS notes,
    ts.id AS specification_id,
    sup.id AS supplier_id,
    v_warehouse_id AS warehouse_id,
    loc.id AS location_id,
    i.neuma AS tire_code,
    i.valorrn AS retread_value,
    i.protec AS protector_code,
    i.valorp AS protector_value,
    CURRENT_TIMESTAMP AS created_at,
    1 AS created_by
FROM inventario@legacy_db_link i
LEFT JOIN tire_specifications ts ON ts.code = i.ficha::TEXT
LEFT JOIN tire_suppliers sup ON sup.code = i.proveedor
LEFT JOIN warehouse_locations loc ON loc.code = i.local AND loc.warehouse_id = v_warehouse_id
WHERE i.llanta IS NOT NULL;

GET DIAGNOSTICS v_count = ROW_COUNT;
RAISE NOTICE 'Llantas en inventario migradas: %', v_count;

-- ============================================================================
-- Validación de integridad para inventario
-- ============================================================================
DO $$
DECLARE
    v_missing_specs INTEGER;
    v_missing_suppliers INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_missing_specs
    FROM tire_inventory
    WHERE specification_id IS NULL;

    SELECT COUNT(*) INTO v_missing_suppliers
    FROM tire_inventory
    WHERE supplier_id IS NULL;

    IF v_missing_specs > 0 THEN
        RAISE WARNING '% llantas sin ficha técnica', v_missing_specs;
    END IF;

    IF v_missing_suppliers > 0 THEN
        RAISE WARNING '% llantas sin proveedor', v_missing_suppliers;
    END IF;
END $$;

-- ============================================================================
-- PASO 2: Migrar llantas activas (montadas en vehículos)
-- ============================================================================
-- Similar pattern...

COMMIT;

-- ============================================================================
-- FIN DEL SCRIPT 005
-- ============================================================================
```

**Nota:** Este script es extenso. Ver archivo completo en repositorio.

### 5.6 Script 006: Habilitación de RLS

**Archivo:** `006_enable_rls.sql`

```sql
-- ============================================================================
-- HABILITACIÓN: Row-Level Security
-- Descripción: Crea funciones RLS y políticas de aislamiento por oficina
-- ============================================================================

BEGIN;

-- ============================================================================
-- PASO 1: Crear funciones de utilidad para RLS
-- ============================================================================

-- Función para obtener office_id del usuario actual
CREATE OR REPLACE FUNCTION get_user_office_id()
RETURNS UUID AS $$
DECLARE
    v_office_id UUID;
BEGIN
    SELECT office_id INTO v_office_id
    FROM users
    WHERE id = current_setting('app.current_user_id', true)::BIGINT;
    RETURN v_office_id;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

-- Función para verificar rol
CREATE OR REPLACE FUNCTION current_user_has_role(role_name TEXT)
RETURNS BOOLEAN AS $$
DECLARE
    has_role BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM users u
        JOIN user_roles ur ON u.id = ur.user_id
        JOIN roles r ON ur.role_id = r.id
        WHERE u.id = current_setting('app.current_user_id', true)::BIGINT
          AND r.name = role_name
          AND u.is_active = true
    ) INTO has_role;
    RETURN COALESCE(has_role, false);
EXCEPTION
    WHEN OTHERS THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

RAISE NOTICE 'Funciones RLS creadas';

-- ============================================================================
-- PASO 2: Habilitar RLS en tablas
-- ============================================================================

-- Warehouses
ALTER TABLE warehouses ENABLE ROW LEVEL SECURITY;

CREATE POLICY warehouses_office_isolation ON warehouses
    FOR ALL
    TO authenticated_user
    USING (
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        office_id = get_user_office_id()
    );

-- Warehouse Locations
ALTER TABLE warehouse_locations ENABLE ROW LEVEL SECURITY;

CREATE POLICY locations_office_isolation ON warehouse_locations
    FOR ALL
    TO authenticated_user
    USING (
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        warehouse_id IN (
            SELECT id FROM warehouses
            WHERE office_id = get_user_office_id()
              AND deleted_at IS NULL
        )
    );

-- Tire Suppliers
ALTER TABLE tire_suppliers ENABLE ROW LEVEL SECURITY;

CREATE POLICY suppliers_office_isolation ON tire_suppliers
    FOR ALL
    TO authenticated_user
    USING (
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        office_id = get_user_office_id()
    );

-- Tire Inventory
ALTER TABLE tire_inventory ENABLE ROW LEVEL SECURITY;

CREATE POLICY tire_inventory_office_isolation ON tire_inventory
    FOR ALL
    TO authenticated_user
    USING (
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        warehouse_id IN (
            SELECT id FROM warehouses
            WHERE office_id = get_user_office_id()
              AND deleted_at IS NULL
        )
    );

RAISE NOTICE 'Políticas RLS creadas y habilitadas';

COMMIT;

-- ============================================================================
-- FIN DEL SCRIPT 006
-- ============================================================================
```

### 5.7 Script 007: Validación

**Archivo:** `007_validate_migration.sql`

```sql
-- ============================================================================
-- VALIDACIÓN: Migración Completa
-- Descripción: Ejecuta validaciones de integridad y completitud
-- ============================================================================

-- ============================================================================
-- PASO 1: Validar cantidades
-- ============================================================================
DO $$
DECLARE
    v_legacy_offices INTEGER := 1;  -- Solo 1 oficina en legacy (implícita)
    v_new_offices INTEGER;
    v_legacy_locations INTEGER;
    v_new_locations INTEGER;
    v_legacy_suppliers INTEGER;
    v_new_suppliers INTEGER;
    v_legacy_inventory INTEGER;
    v_new_inventory INTEGER;
BEGIN
    -- Contar registros
    SELECT COUNT(*) INTO v_new_offices FROM offices WHERE deleted_at IS NULL;
    SELECT COUNT(*) INTO v_legacy_locations FROM localiza@legacy_db_link;
    SELECT COUNT(*) INTO v_new_locations FROM warehouse_locations WHERE deleted_at IS NULL;
    SELECT COUNT(*) INTO v_legacy_suppliers FROM proveedores_llantas@legacy_db_link;
    SELECT COUNT(*) INTO v_new_suppliers FROM tire_suppliers WHERE deleted_at IS NULL;
    SELECT COUNT(*) INTO v_legacy_inventory FROM inventario@legacy_db_link;
    SELECT COUNT(*) INTO v_new_inventory FROM tire_inventory WHERE deleted_at IS NULL;

    RAISE NOTICE '========================================';
    RAISE NOTICE 'VALIDACIÓN DE CANTIDADES';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Oficinas: Legacy=%, Migrado=%', v_legacy_offices, v_new_offices;
    RAISE NOTICE 'Ubicaciones: Legacy=%, Migrado=%', v_legacy_locations, v_new_locations;
    RAISE NOTICE 'Proveedores: Legacy=%, Migrado=%', v_legacy_suppliers, v_new_suppliers;
    RAISE NOTICE 'Inventario: Legacy=%, Migrado=%', v_legacy_inventory, v_new_inventory;

    -- Alertar si hay diferencias
    IF v_new_locations < v_legacy_locations THEN
        RAISE WARNING 'Diferencia en ubicaciones: % no migradas', (v_legacy_locations - v_new_locations);
    END IF;

    IF v_new_suppliers < v_legacy_suppliers THEN
        RAISE WARNING 'Diferencia en proveedores: % no migrados', (v_legacy_suppliers - v_new_suppliers);
    END IF;

    IF v_new_inventory < v_legacy_inventory THEN
        RAISE WARNING 'Diferencia en inventario: % llantas no migradas', (v_legacy_inventory - v_new_inventory);
    END IF;
END $$;

-- ============================================================================
-- PASO 2: Validar integridad referencial
-- ============================================================================
DO $$
DECLARE
    v_orphan_warehouses INTEGER;
    v_orphan_locations INTEGER;
    v_orphan_suppliers INTEGER;
    v_orphan_inventory INTEGER;
BEGIN
    -- Almacenes sin oficina
    SELECT COUNT(*) INTO v_orphan_warehouses
    FROM warehouses w
    WHERE w.office_id NOT IN (SELECT id FROM offices);

    -- Ubicaciones sin almacén
    SELECT COUNT(*) INTO v_orphan_locations
    FROM warehouse_locations wl
    WHERE wl.warehouse_id NOT IN (SELECT id FROM warehouses);

    -- Proveedores sin oficina
    SELECT COUNT(*) INTO v_orphan_suppliers
    FROM tire_suppliers ts
    WHERE ts.office_id NOT IN (SELECT id FROM offices);

    -- Inventario con referencias inválidas
    SELECT COUNT(*) INTO v_orphan_inventory
    FROM tire_inventory ti
    WHERE ti.warehouse_id NOT IN (SELECT id FROM warehouses)
       OR ti.supplier_id NOT IN (SELECT id FROM tire_suppliers)
       OR ti.specification_id NOT IN (SELECT id FROM tire_specifications);

    RAISE NOTICE '========================================';
    RAISE NOTICE 'VALIDACIÓN DE INTEGRIDAD REFERENCIAL';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Almacenes huérfanos: %', v_orphan_warehouses;
    RAISE NOTICE 'Ubicaciones huérfanas: %', v_orphan_locations;
    RAISE NOTICE 'Proveedores huérfanos: %', v_orphan_suppliers;
    RAISE NOTICE 'Inventario con refs inválidas: %', v_orphan_inventory;

    IF v_orphan_warehouses + v_orphan_locations + v_orphan_suppliers + v_orphan_inventory > 0 THEN
        RAISE EXCEPTION 'ERROR: Integridad referencial comprometida';
    END IF;
END $$;

-- ============================================================================
-- PASO 3: Validar usuarios sin oficina
-- ============================================================================
DO $$
DECLARE
    v_users_without_office INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_users_without_office
    FROM users
    WHERE office_id IS NULL;

    RAISE NOTICE '========================================';
    RAISE NOTICE 'VALIDACIÓN DE USUARIOS';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Usuarios sin oficina: %', v_users_without_office;

    IF v_users_without_office > 0 THEN
        RAISE EXCEPTION 'ERROR: Hay % usuarios sin oficina asignada', v_users_without_office;
    END IF;
END $$;

-- ============================================================================
-- PASO 4: Validar RLS habilitado
-- ============================================================================
DO $$
DECLARE
    v_tables_without_rls INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_tables_without_rls
    FROM pg_tables pt
    WHERE pt.schemaname = 'public'
      AND pt.tablename IN ('warehouses', 'warehouse_locations', 'tire_suppliers', 'tire_inventory')
      AND pt.rowsecurity = false;

    RAISE NOTICE '========================================';
    RAISE NOTICE 'VALIDACIÓN DE RLS';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Tablas sin RLS habilitado: %', v_tables_without_rls;

    IF v_tables_without_rls > 0 THEN
        RAISE WARNING 'Algunas tablas no tienen RLS habilitado';
    END IF;
END $$;

-- ============================================================================
-- RESUMEN FINAL
-- ============================================================================
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'MIGRACIÓN COMPLETADA';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Todas las validaciones han pasado.';
    RAISE NOTICE 'El sistema está listo para uso.';
END $$;

-- ============================================================================
-- FIN DEL SCRIPT 007
-- ============================================================================
```

---

## 6. VALIDACIÓN Y TESTING

### 6.1 Checklist de Validación

**Cantidades:**
- [ ] Oficinas: 1 (oficina principal)
- [ ] Almacenes: 1 (almacén principal)
- [ ] Ubicaciones: Igual a LOCALIZA legacy
- [ ] Proveedores: Igual a PROVEEDORES_LLANTAS legacy
- [ ] Usuarios con office_id: 100%
- [ ] Inventario: Igual a INVENTARIO legacy
- [ ] Llantas activas: Igual a LLANTAS legacy
- [ ] Histórico: Igual a HISTORIA legacy

**Integridad Referencial:**
- [ ] No hay almacenes sin oficina
- [ ] No hay ubicaciones sin almacén
- [ ] No hay proveedores sin oficina
- [ ] No hay llantas sin warehouse_id
- [ ] No hay llantas sin supplier_id
- [ ] No hay llantas sin specification_id
- [ ] No hay usuarios sin office_id

**Row-Level Security:**
- [ ] RLS habilitado en warehouses
- [ ] RLS habilitado en warehouse_locations
- [ ] RLS habilitado en tire_suppliers
- [ ] RLS habilitado en tire_inventory
- [ ] Funciones get_user_office_id() y current_user_has_role() creadas
- [ ] Políticas RLS creadas

**Funcionalidad:**
- [ ] Usuarios pueden iniciar sesión
- [ ] Usuarios pueden ver inventario de su oficina
- [ ] Usuarios NO pueden ver inventario de otras oficinas
- [ ] Admin nacional puede ver todas las oficinas
- [ ] Búsquedas funcionan correctamente
- [ ] Crear nueva llanta en inventario funciona
- [ ] Montar llanta en vehículo funciona

### 6.2 Tests de Humo (Smoke Tests)

```bash
# Test 1: Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Test 2: Listar inventario
curl -X GET http://localhost:8080/api/tire/inventory \
  -H "Authorization: Bearer {token}"

# Test 3: Crear llanta
curl -X POST http://localhost:8080/api/tire/inventory \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "tireNumber":"TEST001",
    "group":"000",
    "value":500000,
    "warehouseId":"b0000000-0000-0000-0000-000000000001",
    "supplierId":"...",
    "specificationId":"..."
  }'
```

---

## 7. ROLLBACK PLAN

### 7.1 Estrategia de Rollback

**Escenario:** Fallo crítico durante migración o validación.

**Opción 1: Rollback Completo (Recomendado para fallos en Fase 1-5)**

```sql
-- Ejecutar desde conexión PostgreSQL
ROLLBACK;  -- Si todavía estamos en transacción

-- Restaurar backup
psql -U postgres -d vortice_db < backup_pre_migration.sql
```

**Opción 2: Rollback Parcial (Fase 6-9)**

Si la migración de datos transaccionales falló pero la estructura organizacional está OK:

```sql
BEGIN;

-- Eliminar datos transaccionales migrados
DELETE FROM tire_history WHERE created_at >= '[TIMESTAMP_INICIO_MIGRACION]';
DELETE FROM tire_retired WHERE created_at >= '[TIMESTAMP_INICIO_MIGRACION]';
DELETE FROM tire_intermediate WHERE created_at >= '[TIMESTAMP_INICIO_MIGRACION]';
DELETE FROM tire_active WHERE created_at >= '[TIMESTAMP_INICIO_MIGRACION]';
DELETE FROM tire_inventory WHERE created_at >= '[TIMESTAMP_INICIO_MIGRACION]';

-- Deshabilitar RLS temporalmente
ALTER TABLE tire_inventory DISABLE ROW LEVEL SECURITY;
ALTER TABLE warehouses DISABLE ROW LEVEL SECURITY;
-- ... demás tablas

COMMIT;

-- Re-intentar migración desde Fase 6
```

**Opción 3: Volver a Sistema Legacy**

```bash
# 1. Detener aplicación Spring Boot
systemctl stop vortice-backend

# 2. Reconfigurar conexión a Oracle
# Editar application.properties

# 3. Reiniciar Oracle Forms
# (Procedimiento específico de Oracle Forms)

# 4. Habilitar acceso de usuarios a sistema legacy
```

### 7.2 Tiempo de Rollback

- **Rollback Completo:** 15-30 minutos
- **Rollback Parcial:** 30-60 minutos
- **Volver a Legacy:** 1-2 horas

### 7.3 Comunicación de Rollback

```
ASUNTO: [URGENTE] Rollback de Migración - Sistema Vórtice

Estimados usuarios:

Hemos detectado [DESCRIPCIÓN DEL PROBLEMA] durante el proceso de migración del sistema Vórtice.

Por seguridad, hemos decidido realizar un rollback al sistema anterior (Oracle Forms).

ESTADO ACTUAL:
- Sistema: Temporalmente inactivo
- Tiempo estimado de restauración: [X horas]
- Datos: Ningún dato se ha perdido (backup completo disponible)

PRÓXIMOS PASOS:
1. Completar rollback al sistema Oracle Forms
2. Analizar causa del fallo
3. Reprogramar migración para [NUEVA FECHA]

Lamentamos las molestias.

Equipo de TI - TRANSER
```

---

## 8. CHECKLIST DE EJECUCIÓN

### 8.1 Pre-Migración (1 semana antes)

- [ ] Backup completo de Oracle
- [ ] Crear snapshot de servidor PostgreSQL (staging)
- [ ] Ejecutar dry-run en staging
- [ ] Validar datos migrados en staging
- [ ] Revisar logs de dry-run
- [ ] Preparar scripts de rollback
- [ ] Notificar a usuarios del downtime programado
- [ ] Preparar equipo de soporte 24/7
- [ ] Verificar que todos los scripts están en repositorio Git

### 8.2 Durante Ventana de Migración

**Fase 0: Inicio (H+0:00)**
- [ ] Deshabilitar acceso a Oracle Forms
- [ ] Anunciar inicio de mantenimiento
- [ ] Backup final de Oracle
- [ ] Exportar datos a CSV
- [ ] Validar integridad de exportación

**Fase 1: Estructura (H+0:30)**
- [ ] Ejecutar script 001: Estructura organizacional
- [ ] Verificar cantidad de registros
- [ ] Validar constraints

**Fase 2: Catálogos (H+0:45)**
- [ ] Ejecutar script 002: Catálogos globales
- [ ] Verificar cantidad de registros
- [ ] Validar FKs

**Fase 3: Proveedores (H+1:15)**
- [ ] Ejecutar script 003: Proveedores
- [ ] Verificar cantidad de registros

**Fase 4: Usuarios (H+1:30)**
- [ ] Ejecutar script 004: Usuarios
- [ ] Verificar que todos tienen office_id

**Fase 5: Datos Transaccionales (H+1:45)**
- [ ] Ejecutar script 005: Datos transaccionales
- [ ] Verificar cantidades por tabla
- [ ] Validar integridad referencial

**Fase 6: RLS (H+3:45)**
- [ ] Ejecutar script 006: Habilitar RLS
- [ ] Verificar que políticas están activas
- [ ] Probar RLS con usuarios de prueba

**Fase 7: Validación (H+4:15)**
- [ ] Ejecutar script 007: Validación completa
- [ ] Revisar todos los checks
- [ ] Documentar warnings

**Fase 8: Activación (H+5:15)**
- [ ] Configurar backend con PostgreSQL
- [ ] Iniciar aplicación Spring Boot
- [ ] Ejecutar smoke tests
- [ ] Verificar logs de arranque

**Fase 9: Go-Live (H+5:45)**
- [ ] Anunciar fin de mantenimiento
- [ ] Habilitar acceso a usuarios
- [ ] Monitorear primeras operaciones
- [ ] Soporte en línea activo

### 8.3 Post-Migración (24 horas)

- [ ] Monitoreo continuo de logs
- [ ] Soporte activo para usuarios
- [ ] Documentar incidencias
- [ ] Realizar ajustes de performance si es necesario
- [ ] Backup de PostgreSQL post-migración
- [ ] Validar reportes críticos
- [ ] Reunión de retrospectiva del equipo

---

## 9. REFERENCIAS

- **Esquema de Base de Datos:** `/docs/database/Schema_Organization.md`
- **Guía de Seguridad:** `/docs/security/Multi_Tenant_Security.md`
- **Requerimientos:** `/docs/llantas/Requerimiento_Llantas.md`
- **Repositorio de Scripts:** `/database/migrations/multi-sede/`

---

**FIN DEL DOCUMENTO**
