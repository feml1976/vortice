# INFORME DE MODERNIZACIÓN DE BASE DE DATOS
## MÓDULO DE GESTIÓN DE LLANTAS

---

**Proyecto:** Vórtice - Modernización Sistema Gestión Taller
**Módulo:** Sistema de Gestión y Control de Llantas
**Versión:** 1.0
**Fecha:** 21 de Enero de 2026
**Autor:** Equipo de Arquitectura Vórtice

---

## 📋 TABLA DE CONTENIDO

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Análisis del Modelo Legacy](#2-análisis-del-modelo-legacy)
3. [Problemas Identificados](#3-problemas-identificados)
4. [Modelo Propuesto Modernizado](#4-modelo-propuesto-modernizado)
5. [Mejoras Implementadas](#5-mejoras-implementadas)
6. [Estrategia de Migración](#6-estrategia-de-migración)
7. [Consideraciones de Rendimiento](#7-consideraciones-de-rendimiento)
8. [Scripts SQL Completos](#8-scripts-sql-completos)
9. [Recomendaciones Finales](#9-recomendaciones-finales)

---

## 1. RESUMEN EJECUTIVO

### 1.1 Objetivo del Informe

Este documento presenta el análisis exhaustivo del modelo de datos LEGACY de Oracle 11g del módulo de Llantas y propone un modelo modernizado optimizado para PostgreSQL 18, alineado con:

- **Domain-Driven Design (DDD)**
- **Arquitectura de Monolito Modular**
- **Mejores prácticas de PostgreSQL 18**
- **Stack tecnológico: Java 21 + Spring Boot 3.5**
- **Estándares de nombrado modernos**

### 1.2 Métricas del Análisis

| Aspecto | Legacy Oracle | Propuesto PostgreSQL | Mejora |
|---------|---------------|---------------------|--------|
| **Tablas principales** | 23 | 28 | +21% (separación de concerns) |
| **Constraints FK** | 11 | 45 | +309% (integridad reforzada) |
| **Índices optimizados** | ~15 | 68 | +353% (rendimiento) |
| **Tipos de datos modernos** | Oracle legacy | PostgreSQL native | 100% compatibilidad |
| **Auditoría completa** | Parcial | Total (columnas estándar) | 100% trazabilidad |
| **Soft deletes** | No implementado | Sí (deleted_at) | Mejora seguridad |
| **Optimistic locking** | No | Sí (version) | Concurrencia |
| **UUID para PKs principales** | No | Sí | Escalabilidad |

### 1.3 Beneficios Principales

✅ **Integridad de Datos:** Constraints robustos garantizan consistencia
✅ **Rendimiento:** Índices estratégicos optimizados para consultas comunes
✅ **Escalabilidad:** UUIDs permiten sharding futuro
✅ **Trazabilidad:** Auditoría completa (created_at, updated_at, created_by, etc.)
✅ **Modularidad:** Esquema organizado por bounded contexts
✅ **Mantenibilidad:** Nombrado consistente y autodocumentado
✅ **Seguridad:** Soft deletes evitan pérdida accidental de datos

---

## 2. ANÁLISIS DEL MODELO LEGACY

### 2.1 Arquitectura General del Modelo Legacy

El modelo de Oracle 11g consta de **23 tablas** organizadas en los siguientes grupos funcionales:

#### **2.1.1 Tablas de Estado de Llantas (4)**
```
INVENTARIO    → Llantas sin montar en bodega
LLANTAS       → Llantas activas montadas en vehículos
INTERMEDIO    → Llantas desmontadas en evaluación
RETIRADAS     → Llantas dadas de baja definitiva
```

#### **2.1.2 Tablas de Trazabilidad (2)**
```
HISTORIA      → Ciclo de vida completo por montaje
HISTOMUES     → Histórico de muestreos
```

#### **2.1.3 Tablas Maestras (8)**
```
FICHATEC            → Fichas técnicas de llantas
MARCAS_LLANTAS      → Catálogo de marcas
TIPOS               → Catálogo de tipos
REFERENCIA          → Catálogo de referencias
CLASES              → Clases de vehículos
LOCALIZA            → Ubicaciones en bodega
OBSERVA             → Motivos de desmontaje/baja
PROVEEDORES_LLANTAS → Proveedores
```

#### **2.1.4 Tablas de Control (5)**
```
VEHICULOS_LLANTAS        → Vehículos de la flota
MUESTREO                 → Muestreos actuales
KMS_RECORRIDO_LLANTAS    → Acumulado de kilómetros
NEUMATICO                → Catálogo de neumáticos para reencauche
BAJA                     → Tabla auxiliar de bajas (redundante)
```

#### **2.1.5 Tablas Auxiliares/Técnicas (4)**
```
AUDITA           → Auditoría básica
LOG_LLANTAS      → Logs de sistema
TMPLOGLLA        → Tabla temporal de logs
TMPLOGMOV        → Tabla temporal de movimientos
VIDAK            → Vista materializada de vida útil (tabla)
REPORTE_LLANTAS  → Tabla de reportes precalculados
HISTORIAN        → Histórico de neumáticos
TIPVEHRES        → Resumen por tipo de vehículo
```

### 2.2 Fortalezas del Modelo Legacy

| Fortaleza | Descripción |
|-----------|-------------|
| **Separación de estados** | Tablas distintas para INVENTARIO, LLANTAS, INTERMEDIO, RETIRADAS |
| **Trazabilidad histórica** | HISTORIA mantiene todo el ciclo de vida |
| **Identificador único** | LLANTA como identificador único de la llanta física (GRUPO es mutable) |
| **Constraint de unicidad** | `UK_VEHI_POS (VEHICULO, POSICION)` evita duplicados |
| **Foreign keys básicas** | Relaciones críticas están definidas |
| **Vistas para reportes** | V_CONSUMOS_LLANTAS, V_LLANTAS_ACTIVAS, V_LLANTAS_TOTAL |

### 2.3 Tecnologías Legacy Identificadas

```sql
-- Oracle-specific features en uso:
- VARCHAR2                → Tipo de dato Oracle
- CHAR(n)                → Longitud fija (desperdicio de espacio)
- NUMBER(p,s)            → Tipo numérico Oracle
- DATE                   → Sin zona horaria
- SYSDATE                → Función Oracle
- NVL()                  → Función Oracle para NULL handling
- SUBSTR()               → Función Oracle (PostgreSQL usa SUBSTRING)
- TO_CHAR(), TO_DATE()   → Conversiones Oracle
- DECODE()               → Lógica condicional Oracle
- CREATE FORCE VIEW      → Sintaxis Oracle
- (+) outer join syntax  → Sintaxis antigua Oracle
- Schema prefix LLANTAS. → Organización Oracle
```

---

## 3. PROBLEMAS IDENTIFICADOS

### 3.1 🔴 PROBLEMAS CRÍTICOS

#### **P-001: Falta de Auditoría Completa**
```sql
-- Legacy: Solo tabla AUDITA separada (inconsistente)
CREATE TABLE AUDITA (
    USUARIO VARCHAR2(20),  -- Sin FK a tabla de usuarios
    OPCION VARCHAR2(4),
    FECHA DATE,
    NUMERO VARCHAR2(20),
    TABLA VARCHAR2(12)
);

-- PROBLEMA:
-- ❌ No hay created_at, updated_at en tablas principales
-- ❌ No hay created_by, updated_by para trazabilidad
-- ❌ AUDITA no está ligada por FK, puede desincronizarse
-- ❌ No captura valores antiguos vs nuevos
```

**Impacto:** Imposible auditar cambios individuales, no cumple requisitos regulatorios.

---

#### **P-002: Ausencia de Soft Deletes**
```sql
-- Legacy: DELETE físico de datos
DELETE FROM INVENTARIO WHERE LLANTA = 'LL001';

-- PROBLEMA:
-- ❌ Pérdida permanente de datos
-- ❌ No hay forma de "deshacer" errores
-- ❌ Rompe trazabilidad histórica
-- ❌ Violaciones de integridad referencial
```

**Impacto:** Pérdida de información crítica, no recuperable.

---

#### **P-003: Tipos de Datos Obsoletos y No Portables**
```sql
-- Oracle legacy types
VARCHAR2(20)       → No estándar SQL
NUMBER(7,0)        → Ambiguo (¿entero?, ¿decimal?)
DATE               → Sin timezone (problemas multi-sede)
CHAR(6)            → Desperdicia espacio con padding

-- PostgreSQL equivalente moderno:
VARCHAR(20)        → Estándar SQL
INTEGER / BIGINT   → Tipos específicos
TIMESTAMP WITH TIME ZONE → Manejo correcto de zonas horarias
VARCHAR(6)         → Sin desperdicio de espacio
```

**Impacto:** Dificulta migración, bugs por timezone, desperdicio de espacio.

---

#### **P-004: Primary Keys No Escalables**
```sql
-- Legacy: Claves primarias compuestas sin surrogate key
CREATE TABLE HISTORIA (
    LLANTA VARCHAR2(20),
    GRUPO CHAR(3),
    CONSTRAINT PK_HISTORIA_LLANGRU PRIMARY KEY (LLANTA, GRUPO)
);

-- PROBLEMAS:
-- ❌ Claves compuestas dificultan JOINs
-- ❌ No soporta sharding horizontal futuro
-- ❌ ORMs (JPA/Hibernate) requieren clases embebidas complejas
-- ❌ Problemas con caching (keys complejas)
```

**Impacto:** Bajo rendimiento en ORMs, no escalable.

---

#### **P-005: Falta de Optimistic Locking**
```sql
-- Legacy: Sin control de concurrencia
UPDATE LLANTAS
SET POSICION = 5
WHERE LLANTA = 'LL001' AND GRUPO = '000';

-- PROBLEMA:
-- ❌ Lost updates en ambientes multi-usuario
-- ❌ Dos usuarios pueden modificar simultáneamente
-- ❌ No hay versionado de entidades
```

**Impacto:** Corrupción de datos en concurrencia, overwrite silencioso.

---

### 3.2 🟡 PROBLEMAS DE DISEÑO

#### **P-006: Redundancia de Datos**
```sql
-- HISTORIA duplica casi todos los campos de LLANTAS
HISTORIA: VALOR, FECHA, PROVEE, FACTURA, FICHA, NEUMA, VALORRN, PROTEC, VALORP
LLANTAS:  VALOR, FECHA, PROVEE, FACTURA, FICHA, NEUMA, VALORRN, PROTEC, VALORP

-- PROBLEMA: Cuando LLANTAS → INTERMEDIO, se pierde sincronización
```

**Impacto:** Inconsistencias, difícil mantener sincronizado.

---

#### **P-007: Tablas Temporales en Esquema de Producción**
```sql
CREATE TABLE TMPLOGLLA (...)
CREATE TABLE TMPLOGMOV (...)

-- PROBLEMA:
-- ❌ Tablas temporales mezcladas con datos de negocio
-- ❌ Sin estrategia de limpieza (crecimiento infinito)
-- ❌ Contaminan esquema principal
```

**Impacto:** Confusión, crecimiento descontrolado de datos.

---

#### **P-008: Tabla VIDAK: Vista Materializada Como Tabla**
```sql
CREATE TABLE VIDAK (
    LLANTA VARCHAR2(20),
    GRUPO CHAR(3),
    VEHICULO CHAR(6),
    -- ... 18 columnas calculadas
);

-- PROBLEMA:
-- ❌ Datos calculados almacenados como tabla
-- ❌ Sin mecanismo de refresh automático
-- ❌ Puede quedar desincronizada
-- ❌ Debería ser MATERIALIZED VIEW real
```

**Impacto:** Datos obsoletos, mantenimiento manual.

---

#### **P-009: Falta de Constraints CHECK**
```sql
-- Legacy: Sin validaciones de negocio en BD
GRUPO CHAR(3)  -- No valida formato '000'-'999'
ESTADO VARCHAR2(1)  -- No valida valores permitidos
CATEGORIA CHAR(1)  -- Sin constraint de dominio

-- PostgreSQL moderno:
GRUPO CHAR(3) CHECK (GRUPO ~ '^\d{3}$')
ESTADO CHAR(1) CHECK (ESTADO IN ('A', 'I'))
```

**Impacto:** Datos inválidos pueden entrar, validación solo en aplicación.

---

#### **P-010: Nomenclatura Inconsistente**
```sql
-- Abreviaciones sin patrón:
FICHATEC      → ¿ficha técnica?
PROVEE        → ¿proveedor?
PROVE         → ¿proveedor? (diferente de PROVEE)
KINSTALA      → ¿kilometros instalación?
KREMUEVE      → ¿kilometros remoción?
VALORRN       → ¿valor reencauche?
PORQUE        → ¿motivo?

-- Mezcla español/inglés
REF vs REFERENCIA
CANTI vs CANTIDAD
```

**Impacto:** Código difícil de leer, propenso a errores de typo.

---

#### **P-011: HISTORIA: Campos NOT NULL Incorrectos**
```sql
CREATE TABLE HISTORIA (
    VEHICULO CHAR(6) CONSTRAINT NN_HISTORIA_VEHICULO NOT NULL,
    KREMUEVE NUMBER(8,0) CONSTRAINT NN_HISTORIA_KREMUEVE NOT NULL,
    FECHAF DATE CONSTRAINT NN_HISTORIA_FECHAF NOT NULL,
    PORQUE NUMBER(3,0) CONSTRAINT NN_HISTORIA_PORQUE NOT NULL
);

-- PROBLEMA:
-- ❌ Al insertar registro inicial, no hay valores de remoción
-- ❌ Campos deben ser NULLABLE hasta que ocurra desmontaje
-- ❌ Fuerza INSERT con datos dummy ('      ', 0, sysdate, 0)
```

**Impacto:** Lógica de negocio compleja para mantener constraints.

---

### 3.3 🟢 PROBLEMAS MENORES (Mejoras Deseables)

#### **P-012: Falta de Índices Compuestos**
```sql
-- Legacy: Índices solo en FKs
-- No hay índices para queries comunes:
SELECT * FROM LLANTAS WHERE VEHICULO = ? AND POSICION = ?
SELECT * FROM HISTORIA WHERE LLANTA = ? ORDER BY GRUPO

-- PostgreSQL moderno:
CREATE INDEX idx_llantas_vehiculo_posicion ON llantas(vehiculo, posicion);
CREATE INDEX idx_historia_llanta_grupo ON historia(llanta, grupo);
```

---

#### **P-013: Sin Particionamiento para Tablas Grandes**
```sql
-- HISTORIA y HISTOMUES crecen indefinidamente
-- PostgreSQL 18: Particionamiento nativo por fecha/año
```

---

#### **P-014: Comentarios Insuficientes**
```sql
-- Comentarios genéricos, no documentan reglas de negocio
COMMENT ON TABLE HISTORIA IS 'Almacena la vida histórica...';
-- No explica: ¿cuándo se cierra un registro? ¿qué significa GRUPO?
-- GRUPO: Código de 3 dígitos [VV][R] donde VV=cantidad de vehículos (00-99)
--        y R=cantidad de reencauches (0-9). Campo MUTABLE que se actualiza
--        al montar en nuevo vehículo (+1 veh) o al reencauchar (reset a 00, +1 reenc)
```

---

## 4. MODELO PROPUESTO MODERNIZADO

### 4.1 Principios de Diseño

#### **4.1.1 Estándares de Nombrado PostgreSQL**

```sql
-- TABLAS: Plural, snake_case
tire_technical_specifications
tire_active_installations
tire_history_records

-- COLUMNAS: Singular, snake_case
tire_number
installation_date
mileage_at_installation

-- PRIMARY KEYS:
id (UUID para entidades principales, BIGSERIAL para secundarias)

-- FOREIGN KEYS:
[tabla_singular]_id
technical_specification_id
vehicle_id
provider_id

-- TIMESTAMPS:
created_at, updated_at, deleted_at (TIMESTAMP WITH TIME ZONE)

-- AUDIT COLUMNS:
created_by, updated_by, deleted_by (BIGINT REFERENCES users)

-- BOOLEANS:
is_[adjetivo]
is_active, is_retreaded, is_damaged

-- ÍNDICES:
idx_[tabla]_[columna(s)]
idx_tire_installations_vehicle_position
```

#### **4.1.2 Convenciones de Tipos de Datos**

| Tipo de Dato Legacy | Tipo PostgreSQL Moderno | Justificación |
|---------------------|------------------------|---------------|
| `VARCHAR2(n)` | `VARCHAR(n)` | Estándar SQL |
| `CHAR(n)` espacios fijos | `VARCHAR(n)` | Sin padding innecesario |
| `NUMBER(p,0)` entero | `INTEGER` o `BIGINT` | Tipo específico, mejor rendimiento |
| `NUMBER(p,s)` decimal | `NUMERIC(p,s)` o `DECIMAL(p,s)` | Estándar SQL |
| `DATE` sin hora | `DATE` (si solo fecha) o `TIMESTAMP WITH TIME ZONE` | Precisión y timezone |
| Claves numéricas pequeñas | `SMALLINT` (hasta 32,767) | Ahorro de espacio |
| Claves numéricas grandes | `BIGINT` | Escalabilidad |
| Identificadores únicos | `UUID` | Distribución, seguridad |

#### **4.1.3 Estrategia de Primary Keys**

```sql
-- ENTIDADES PRINCIPALES (Aggregates):
-- UUID para permitir generación distribuida y sharding futuro
CREATE TABLE tires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_number VARCHAR(20) NOT NULL UNIQUE,  -- Natural key: UNA llanta física = UN registro
    generation CHAR(3) NOT NULL,              -- Campo MUTABLE: [VV][R] vehículos+reencauches
    -- ...
);

-- ENTIDADES SECUNDARIAS (no aggregates):
-- BIGSERIAL para simplicidad y rendimiento
CREATE TABLE tire_samplings (
    id BIGSERIAL PRIMARY KEY,
    tire_installation_id UUID NOT NULL REFERENCES tire_active_installations(id),
    -- ...
);

-- TABLAS DE RELACIÓN (Many-to-Many):
-- Composite PK sin surrogate key
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);
```

---

### 4.2 Estructura del Esquema Modular

El modelo se organiza utilizando **OPCIÓN 3: ARQUITECTURA HÍBRIDA** con múltiples esquemas PostgreSQL para alinear con Domain-Driven Design y facilitar la evolución del Monolito Modular:

#### **4.2.1 Principio de Organización**

- **Esquema `tire_management`**: Contiene TODO el dominio de llantas (sin prefijos "tire_")
- **Esquema `shared`**: Contiene catálogos compartidos entre múltiples módulos (vehículos, usuarios, geografía)
- **Foreign Keys Cross-Schema**: Referencias explícitas entre esquemas (ej: `tire_management.active_installations` → `shared.vehicles`)

```
vortice_db
│
├── SCHEMA: tire_management (Bounded Context: Gestión de Llantas)
│   │
│   ├── Core Domain (Aggregate Roots y Estados)
│   │   ├── tires                         → Aggregate Root, tabla principal
│   │   ├── active_installations          → Estado ACTIVA (sin prefijo tire_)
│   │   ├── inventory                     → Estado INVENTARIO
│   │   ├── intermediate                  → Estado INTERMEDIO
│   │   ├── retired                       → Estado RETIRADA
│   │   └── history_records               → Event sourcing (particionada)
│   │
│   ├── Supporting Domain (Mediciones y Análisis)
│   │   ├── technical_specifications      → Fichas técnicas
│   │   ├── samplings                     → Muestreos actuales
│   │   ├── sampling_history              → Histórico de muestreos
│   │   ├── accumulated_mileage           → Kilómetros acumulados
│   │   └── alerts                        → Alertas de negocio
│   │
│   └── Catalogs (Maestros del Dominio de Llantas)
│       ├── brands                        → Marcas de llantas
│       ├── types                         → Tipos de llantas
│       ├── tire_references               → Referencias específicas
│       ├── providers                     → Proveedores
│       ├── warehouse_locations           → Ubicaciones en bodega
│       ├── removal_reasons               → Motivos de desmontaje/baja
│       ├── tread_compounds               → Compuestos de banda (NEUMATICO)
│       └── protectors                    → Protectores
│
├── SCHEMA: shared (Shared Kernel: Catálogos Compartidos)
│   │
│   ├── Core Shared Entities
│   │   ├── users                         → Usuarios del sistema
│   │   ├── roles                         → Roles y permisos
│   │   ├── permissions                   → Permisos específicos
│   │   └── audit_log                     → Log de auditoría central
│   │
│   ├── Fleet Catalog (Compartido: Llantas, Mantenimiento, Combustible, etc.)
│   │   ├── vehicles                      → Vehículos de la flota
│   │   ├── vehicle_classes               → Clases de vehículos
│   │   └── vehicle_tire_positions        → Configuración de posiciones
│   │
│   └── Geographic Catalog
│       ├── countries
│       ├── departments
│       ├── cities
│       └── offices
```

#### **4.2.2 Beneficios de esta Arquitectura**

| Aspecto | Beneficio |
|---------|-----------|
| **Separación de Concerns** | Cada esquema representa un bounded context claro |
| **Sin Prefijos Redundantes** | El esquema provee el namespace: `tire_management.brands` en lugar de `tire_brands` |
| **Escalabilidad** | Facilita migración futura a microservicios (un esquema → un servicio) |
| **Compartición Explícita** | `shared.vehicles` usado por múltiples módulos (llantas, mantenimiento, combustible) |
| **Seguridad Granular** | Permisos por esquema: `GRANT USAGE ON SCHEMA tire_management TO role_tire_manager` |
| **Backups Independientes** | Posibilidad de backup/restore por esquema |
| **Claridad en el Código** | Queries explícitas: `SELECT * FROM tire_management.tires JOIN shared.vehicles` |

#### **4.2.3 Ejemplo de Relación Cross-Schema**

```sql
-- Tabla en tire_management con FK a shared
CREATE TABLE tire_management.active_installations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id),
    vehicle_id UUID NOT NULL REFERENCES shared.vehicles(id),  -- Cross-schema FK
    position SMALLINT NOT NULL,
    mileage_at_installation INTEGER NOT NULL,
    -- ...
);

-- Query cross-schema
SELECT
    ai.id,
    t.tire_number,
    v.license_plate,
    vc.name AS vehicle_class
FROM tire_management.active_installations ai
JOIN tire_management.tires t ON ai.tire_id = t.id
JOIN shared.vehicles v ON ai.vehicle_id = v.id
JOIN shared.vehicle_classes vc ON v.vehicle_class_id = vc.id
WHERE ai.deleted_at IS NULL;
```

---

### 4.3 Mapeo de Tablas Legacy → Moderno

#### **Nota Importante sobre Nomenclatura**
- ✅ **SIN prefijos "tire_"**: El esquema `tire_management` provee el namespace
- 📦 **Esquema explícito**: Nombres completos como `tire_management.brands` o `shared.vehicles`
- 🎯 **Catálogos compartidos**: `vehicles` y `vehicle_classes` están en esquema `shared`

| Tabla Legacy Oracle | Tabla Moderna PostgreSQL | Esquema | Cambios Principales |
|---------------------|--------------------------|---------|---------------------|
| `LLANTAS` | `active_installations` | `tire_management` | + UUID id, + auditoría, + version, + deleted_at, + FK a shared.vehicles |
| `INVENTARIO` | `inventory` | `tire_management` | + UUID id, + auditoría, + version, + soft delete |
| `INTERMEDIO` | `intermediate` | `tire_management` | + UUID id, + evaluation_status ENUM, + auditoría |
| `RETIRADAS` | `retired` | `tire_management` | + UUID id, + metrics JSONB, + auditoría |
| `HISTORIA` | `history_records` | `tire_management` | + UUID id, campos NULLABLE correctos, + particionamiento por año |
| `MUESTREO` | `samplings` | `tire_management` | + BIGSERIAL id, + installation_id FK, + auditoría |
| `HISTOMUES` | `sampling_history` | `tire_management` | + BIGSERIAL id, + auditoría, append-only |
| `KMS_RECORRIDO_LLANTAS` | `accumulated_mileage` | `tire_management` | + UUID id, + auditoría, renombrado claramente |
| `FICHATEC` | `technical_specifications` | `tire_management` | + UUID id, + auditoría, + expected_performance JSONB |
| `VEHICULOS_LLANTAS` | `vehicles` | **`shared`** ⭐ | + UUID id, + auditoría, compartido entre módulos |
| `CLASES` | `vehicle_classes` | **`shared`** ⭐ | + UUID id, + tire_configuration JSONB, compartido |
| `MARCAS_LLANTAS` | `brands` | `tire_management` | + UUID id, + auditoría, + is_active, sin prefijo |
| `TIPOS` | `types` | `tire_management` | + UUID id, + auditoría, + is_active |
| `REFERENCIA` | `tire_references` | `tire_management` | + UUID id, + auditoría, + is_active (mantiene "tire_" por ambigüedad) |
| `PROVEEDORES_LLANTAS` | `providers` | `tire_management` | + UUID id, + auditoría, + is_active, + contact_info JSONB |
| `LOCALIZA` | `warehouse_locations` | `tire_management` | + UUID id, + auditoría, + capacity, + is_active |
| `OBSERVA` | `removal_reasons` | `tire_management` | + UUID id, + auditoría, + allows_reentry BOOLEAN |
| `NEUMATICO` | `tread_compounds` | `tire_management` | + UUID id, + auditoría, + current_stock |
| `PROTEC` | `protectors` | `tire_management` | + UUID id, + auditoría, nueva estructura |
| `AUDITA` | ❌ Eliminada | - | Reemplazada por columnas audit en cada tabla + shared.audit_log |
| `BAJA` | ❌ Eliminada | - | Redundante con `tire_management.retired` |
| `VIDAK` | ❌ Eliminada | - | Reemplazada por vista materializada `tire_management.mv_useful_life` |
| `TMPLOGLLA`, `TMPLOGMOV` | ❌ Eliminadas | - | Movidas a esquema `temp` separado (fuera de producción) |
| `LOG_LLANTAS` | ❌ Eliminada | - | Reemplazada por sistema de logging centralizado |
| `REPORTE_LLANTAS` | ❌ Eliminada | - | Reemplazada por vistas materializadas y queries on-demand |
| `HISTORIAN` | ❌ Eliminada | - | Consolidada en `tire_management.history_records` |
| `TIPVEHRES` | ❌ Eliminada | - | Reemplazada por queries agregadas con CTEs |

#### **Nuevas Tablas Añadidas (No existían en Legacy)**

| Tabla | Esquema | Propósito |
|-------|---------|-----------|
| `tires` | `tire_management` | **Aggregate Root**: Tabla maestra con tire_number + generation |
| `alerts` | `tire_management` | Sistema de alertas de negocio (desgaste, presión, inventario) |
| `vehicle_tire_positions` | `shared` | Configuración dinámica de posiciones por vehículo |

#### **Ejemplo de Nombres Completos en Queries**

```sql
-- ✅ CORRECTO: Esquema explícito
SELECT
    t.tire_number,
    ai.position,
    v.license_plate,
    vc.name AS vehicle_class
FROM tire_management.active_installations ai
JOIN tire_management.tires t ON ai.tire_id = t.id
JOIN shared.vehicles v ON ai.vehicle_id = v.id  -- Cross-schema
JOIN shared.vehicle_classes vc ON v.vehicle_class_id = vc.id
WHERE ai.deleted_at IS NULL;

-- ❌ INCORRECTO: Nombres con prefijo redundante
SELECT * FROM tire_management.tire_brands;  -- NO, debe ser tire_management.brands

-- ✅ CORRECTO: Sin prefijo
SELECT * FROM tire_management.brands;
```

---

### 4.4 Diagrama Entidad-Relación (Simplificado)

**NOTA:** Los nombres de tablas NO incluyen prefijos. El esquema proporciona el namespace.
- 📦 `tire_management.*` → Dominio de llantas
- 🌐 `shared.*` → Catálogos compartidos

```
┌─────────────────────────────────────────────────────────────────┐
│         SCHEMA: tire_management - CORE DOMAIN (Lifecycle)       │
└─────────────────────────────────────────────────────────────────┘

                    ┌──────────────────┐
                    │      tires       │ (Aggregate Root)
                    │   (UUID id)      │
                    │ • tire_number    │
                    │ • generation     │
                    └────────┬─────────┘
                             │
             ┌───────────────┼───────────────┐
             │               │               │
             ▼               ▼               ▼
    ┌──────────────┐  ┌─────────────┐  ┌─────────────┐
    │  inventory   │  │   active_   │  │   retired   │
    │              │  │installations│  │             │
    │  Estado:     │  │  Estado:    │  │  Estado:    │
    │  INVENTORY   │  │  ACTIVE     │  │  RETIRED    │
    └──────────────┘  └──────┬──────┘  └─────────────┘
                             │
                             │ 1:N
                             ▼
                    ┌─────────────────┐
                    │   samplings     │
                    │                 │
                    │ • Muestreos     │
                    │   actuales      │
                    │ • depth_*_mm    │
                    │ • pressure_psi  │
                    └─────────────────┘
                             │
                             │ auto duplicated
                             ▼
                    ┌─────────────────┐
                    │  sampling_      │
                    │   history       │
                    │ (append-only)   │
                    └─────────────────┘

    ┌──────────────┐
    │ intermediate │
    │              │
    │  Estado:     │
    │ INTERMEDIATE │
    └──────────────┘


           ┌────────────────────────────────────┐
           │  HISTORY (Event Sourcing)          │
           ├────────────────────────────────────┤
           │  history_records (PARTITIONED)     │
           │                                    │
           │  • Cada montaje = 1 registro       │
           │  • installation_date NOT NULL      │
           │  • removal_date NULLABLE           │
           │  • Particionado por año            │
           └────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│    SCHEMA: tire_management - SUPPORTING DOMAIN (Specifications) │
└─────────────────────────────────────────────────────────────────┘

    ┌──────────────────────────────────────┐
    │ technical_specifications             │
    │                                      │
    │ • dimension                          │
    │ • expected_mileage                   │
    │ • expected_retreads                  │
    │ • initial_depth_*_mm (PI, PC, PD)    │
    │ • expected_performance (JSONB)       │
    └──────────────────────────────────────┘
              │ FKs
              ├──→ brands
              ├──→ types
              ├──→ tire_references
              └──→ providers (main, secondary, last_used)


┌─────────────────────────────────────────────────────────────────┐
│         SCHEMA: shared - FLEET CATALOG (Shared Kernel)          │
└─────────────────────────────────────────────────────────────────┘

    ┌──────────────────┐           ┌──────────────────────┐
    │    vehicles      │──────────▶│  vehicle_classes     │
    │  (shared.*)      │  N:1      │    (shared.*)        │
    │                  │           │                      │
    │ • license_plate  │           │ • number_of_tires    │
    │ • current_mileage│           │ • tire_configuration │
    │ • is_active      │           │   (JSONB)            │
    └──────────────────┘           └──────────────────────┘
           │ 1:N
           │ (CROSS-SCHEMA FK)
           ▼
    ┌──────────────────┐
    │ tire_management. │
    │ active_          │
    │ installations    │
    │                  │
    │ • position       │
    │ • mileage_at_    │
    │   installation   │
    └──────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│    SCHEMA: tire_management - CATALOGS (Domain Specific)         │
└─────────────────────────────────────────────────────────────────┘

    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │   brands     │  │    types     │  │   tire_      │
    │              │  │              │  │ references   │
    └──────────────┘  └──────────────┘  └──────────────┘

    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │  providers   │  │  warehouse_  │  │   removal_   │
    │              │  │  locations   │  │   reasons    │
    └──────────────┘  └──────────────┘  └──────────────┘

    ┌──────────────┐  ┌──────────────┐
    │    tread_    │  │  protectors  │
    │  compounds   │  │              │
    └──────────────┘  └──────────────┘
```

**Leyenda:**
- Sin prefijo "tire_" en nombres de tabla (el esquema provee el namespace)
- `shared.*` → Tablas compartidas entre múltiples módulos
- `tire_management.*` → Tablas específicas del dominio de llantas
- **Cross-Schema FK**: `tire_management.active_installations.vehicle_id` → `shared.vehicles.id`

---

## 5. MEJORAS IMPLEMENTADAS

### 5.1 ✅ Auditoría Completa en Todas las Tablas

```sql
-- Columnas de auditoría estándar en TODAS las tablas de negocio
created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
created_by      BIGINT REFERENCES users(id),
updated_by      BIGINT REFERENCES users(id),

-- Para tablas con soft delete:
deleted_at      TIMESTAMP WITH TIME ZONE,
deleted_by      BIGINT REFERENCES users(id),

-- Trigger automático para updated_at
CREATE TRIGGER set_updated_at
BEFORE UPDATE ON [tabla]
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
```

**Beneficio:** Trazabilidad completa de quién, cuándo y qué cambió.

---

### 5.2 ✅ Soft Deletes Implementados

```sql
-- Columna deleted_at en lugar de DELETE físico
deleted_at TIMESTAMP WITH TIME ZONE

-- Queries automáticas excluyen registros eliminados:
CREATE VIEW tire_management.v_inventory_active AS
SELECT * FROM tire_management.inventory
WHERE deleted_at IS NULL;

-- Índice parcial para performance (solo registros NO eliminados)
CREATE INDEX idx_inventory_not_deleted
ON tire_management.inventory(id)
WHERE deleted_at IS NULL;

-- Spring Data JPA puede filtrar automáticamente con @Where
@Entity
@Table(name = "inventory", schema = "tire_management")
@Where(clause = "deleted_at IS NULL")
public class TireInventory {
    // ...
}
```

**Beneficio:** Recuperación de datos, cumplimiento regulatorio, trazabilidad completa.

---

### 5.3 ✅ Optimistic Locking con Versionado

```sql
-- Columna version en todas las entidades principales
version INTEGER NOT NULL DEFAULT 1,

-- JPA/Hibernate usa @Version para control de concurrencia
@Entity
public class Tire {
    @Version
    private Integer version;
    // ...
}

-- PostgreSQL actualiza automáticamente:
UPDATE tires
SET position = 5, version = version + 1
WHERE id = ? AND version = ?;  -- Falla si version cambió
```

**Beneficio:** Evita lost updates, detecta conflictos de concurrencia.

---

### 5.4 ✅ Primary Keys Escalables

```sql
-- Aggregate Roots: UUID (tire_management schema)
CREATE TABLE tire_management.tires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_number VARCHAR(20) NOT NULL UNIQUE,  -- Natural key: UNA llanta física = UN registro
    generation CHAR(3) NOT NULL,              -- Campo MUTABLE: se actualiza según eventos del ciclo de vida
    -- ...
);

-- Beneficios:
-- • Generación distribuida sin coordinación central
-- • Sharding horizontal futuro (por rangos de UUID)
-- • Seguridad (no expone secuencias predecibles)
-- • Merge de bases de datos sin colisiones

-- Entidades secundarias: BIGSERIAL (mejor performance para queries locales)
CREATE TABLE tire_management.samplings (
    id BIGSERIAL PRIMARY KEY,
    installation_id UUID NOT NULL REFERENCES tire_management.active_installations(id),
    -- ...
);

-- Tablas compartidas también usan UUID
CREATE TABLE shared.vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    license_plate VARCHAR(10) UNIQUE NOT NULL,
    -- ...
);
```

---

### 5.5 ✅ Constraints CHECK para Validación de Dominio

```sql
-- Validación de formato generation: [VV][R] donde VV=vehículos (00-99), R=reencauches (0-9)
-- Ejemplos válidos: '000' (nueva), '030' (3 vehículos, 0 reencauches), '011' (1 vehículo, 1 reencauche)
generation CHAR(3) NOT NULL CHECK (generation ~ '^\d{3}$'),

-- Validación de estados
is_active BOOLEAN NOT NULL DEFAULT true,
evaluation_status VARCHAR(20) CHECK (evaluation_status IN
    ('PENDING', 'APPROVED_FOR_USE', 'REQUIRES_RETREADING', 'MUST_BE_RETIRED')
),

-- Validación de rangos
depth_internal_mm NUMERIC(4,1) CHECK (depth_internal_mm >= 0 AND depth_internal_mm <= 99.9),
pressure_psi INTEGER CHECK (pressure_psi >= 0 AND pressure_psi <= 200),

-- Validación de fechas lógicas
CHECK (removal_date IS NULL OR removal_date >= installation_date),
CHECK (mileage_at_removal IS NULL OR mileage_at_removal >= mileage_at_installation),
```

**Beneficio:** La base de datos garantiza integridad, no solo la aplicación.

---

### 5.6 ✅ Foreign Keys Completas (Cross-Schema)

```sql
-- Legacy: Solo 11 FKs
-- Moderno: 45+ FKs cubriendo todas las relaciones, incluyendo cross-schema

CREATE TABLE tire_management.active_installations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- FKs dentro del mismo esquema (tire_management)
    tire_id UUID NOT NULL
        REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    technical_specification_id UUID NOT NULL
        REFERENCES tire_management.technical_specifications(id) ON DELETE RESTRICT,

    -- FK CROSS-SCHEMA a shared
    vehicle_id UUID NOT NULL
        REFERENCES shared.vehicles(id) ON DELETE RESTRICT,

    -- FKs a usuarios (shared)
    created_by BIGINT REFERENCES shared.users(id),
    updated_by BIGINT REFERENCES shared.users(id),
    deleted_by BIGINT REFERENCES shared.users(id),

    position SMALLINT NOT NULL,
    mileage_at_installation INTEGER NOT NULL,
    -- ...

    CONSTRAINT uq_active_vehicle_position UNIQUE (vehicle_id, position)
);

-- Políticas de DELETE:
-- • ON DELETE RESTRICT: No permite eliminar si hay dependencias (default para entidades de negocio)
-- • ON DELETE CASCADE: Elimina en cascada (solo para relaciones de ownership estricto)
-- • ON DELETE SET NULL: Pone NULL (para referencias opcionales/audit)
```

**Beneficio:** Integridad referencial garantizada por PostgreSQL, incluso entre esquemas.

---

### 5.7 ✅ Índices Estratégicos (68 índices totales)

```sql
-- Índices compuestos para queries comunes (tire_management schema)
CREATE INDEX idx_active_installations_vehicle_position
ON tire_management.active_installations(vehicle_id, position)
WHERE deleted_at IS NULL;

CREATE INDEX idx_history_records_tire_generation
ON tire_management.history_records(tire_id, generation_at_event);

CREATE INDEX idx_samplings_installation_date
ON tire_management.samplings(installation_id, sampling_date DESC);

-- Índices parciales para performance (solo registros activos)
CREATE INDEX idx_tires_active
ON tire_management.tires(id)
WHERE deleted_at IS NULL;

CREATE INDEX idx_vehicles_active
ON shared.vehicles(id)
WHERE is_active = true AND deleted_at IS NULL;

-- Índices GIN para JSONB (búsquedas en estructuras semi-estructuradas)
CREATE INDEX idx_technical_specs_performance
ON tire_management.technical_specifications
USING GIN(expected_performance);

CREATE INDEX idx_vehicles_tire_config
ON shared.vehicles
USING GIN(tire_configuration);

-- Índices BRIN para tablas append-only grandes (historia particionada)
CREATE INDEX idx_history_records_created_at
ON tire_management.history_records
USING BRIN(created_at);

-- Índices de texto completo (si se implementa búsqueda textual)
CREATE INDEX idx_tires_search
ON tire_management.tires
USING GIN(to_tsvector('spanish', tire_number || ' ' || COALESCE(notes, '')));

-- Índices para foreign keys (cross-schema)
CREATE INDEX idx_active_installations_vehicle
ON tire_management.active_installations(vehicle_id)
WHERE deleted_at IS NULL;

CREATE INDEX idx_history_records_vehicle
ON tire_management.history_records(vehicle_id);
```

**Beneficio:** Consultas 10-100x más rápidas en queries comunes. Optimización cross-schema.

---

### 5.8 ✅ Tipos ENUM y JSONB

```sql
-- ENUMs para estados fijos
CREATE TYPE tire_state AS ENUM ('INVENTORY', 'ACTIVE', 'INTERMEDIATE', 'RETIRED');

CREATE TYPE evaluation_status AS ENUM (
    'PENDING',
    'APPROVED_FOR_USE',
    'REQUIRES_RETREADING',
    'MUST_BE_RETIRED'
);

CREATE TYPE alert_priority AS ENUM ('HIGH', 'MEDIUM', 'LOW');

-- JSONB para datos semi-estructurados
CREATE TABLE tire_technical_specifications (
    id UUID PRIMARY KEY,
    expected_performance JSONB,  -- {mileage_range: {min, avg, max}, cost_per_hour, ...}
    -- ...
);

CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    tire_configuration JSONB,  -- {positions: [{number: 1, type: "DIRECTIONAL"}, ...]}
    -- ...
);
```

**Beneficio:** Type-safe, flexible, soporta queries avanzadas con JSONB operators.

---

### 5.9 ✅ Vistas Materializadas para Reportes

```sql
-- Reemplazo de tabla VIDAK
CREATE MATERIALIZED VIEW mv_tire_useful_life AS
SELECT
    ta.id AS installation_id,
    ta.tire_id,
    t.tire_number,
    t.generation,
    v.license_plate,
    ta.position,
    -- Datos actuales
    ts.depth_internal_mm,
    ts.depth_central_mm,
    ts.depth_external_mm,
    (ts.depth_internal_mm + ts.depth_central_mm + ts.depth_external_mm) / 3.0 AS avg_depth,
    -- Cálculos de vida útil
    tam.total_accumulated_mileage,
    tts.expected_mileage,
    -- Proyecciones
    calculate_estimated_remaining_mileage(tam.total_accumulated_mileage, avg_depth, tts.initial_depth_avg) AS estimated_remaining_km,
    calculate_estimated_retirement_date(v.id, estimated_remaining_km) AS estimated_retirement_date
FROM tire_active_installations ta
JOIN tires t ON ta.tire_id = t.id
JOIN vehicles v ON ta.vehicle_id = v.id
JOIN tire_technical_specifications tts ON t.technical_specification_id = tts.id
LEFT JOIN tire_samplings ts ON ts.tire_installation_id = ta.id
    AND ts.sampling_date = (SELECT MAX(sampling_date) FROM tire_samplings WHERE tire_installation_id = ta.id)
LEFT JOIN tire_accumulated_mileage tam ON tam.tire_id = t.id AND tam.generation = t.generation
WHERE ta.deleted_at IS NULL;

-- Refresh automático cada hora
CREATE UNIQUE INDEX ON mv_tire_useful_life(installation_id);
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_tire_useful_life;
```

**Beneficio:** Performance de tabla, actualización controlada, queries complejas precalculadas.

---

### 5.10 ✅ Particionamiento para Tablas Históricas

```sql
-- Particionamiento por rango de fecha en HISTORIA
CREATE TABLE tire_history_records (
    id UUID DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL,
    generation_at_event CHAR(3) NOT NULL,  -- Snapshot INMUTABLE del generation en momento del evento
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- ...
) PARTITION BY RANGE (created_at);

-- Particiones por año
CREATE TABLE tire_history_records_2024 PARTITION OF tire_history_records
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE tire_history_records_2025 PARTITION OF tire_history_records
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE tire_history_records_2026 PARTITION OF tire_history_records
FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

-- Beneficios:
-- • Queries por fecha solo escanean partición relevante
-- • Mantenimiento (VACUUM, REINDEX) por partición
-- • Archivado histórico: desacoplar particiones antiguas
```

---

### 5.11 ✅ Normalización de Nombres de Columnas

| Legacy (Oracle) | Moderno (PostgreSQL) | Mejora |
|-----------------|---------------------|--------|
| `LLANTA` | `tire_number` | Descriptivo |
| `GRUPO` | `generation` | **Clarifica concepto**: Código [VV][R] mutable que registra vida de la llanta (vehículos + reencauches) |
| `PROVEE` / `PROVE` | `provider_id` | Consistente |
| `KINSTALA` | `mileage_at_installation` | Autodocumentado |
| `KREMUEVE` | `mileage_at_removal` | Autodocumentado |
| `FECHAI` | `installation_date` | Estándar |
| `FECHAF` | `removal_date` | Estándar |
| `PORQUE` | `removal_reason_id` | Explícito |
| `FICHA` | `technical_specification_id` | Completo |
| `VALORRN` | `retreading_cost` | Claro |
| `NEUMA` | `tread_compound_id` | Específico |

---

### 5.11.1 📘 Explicación Detallada del Campo `generation` (anteriormente `GRUPO`)

El campo `generation` es un **código mutable de 3 dígitos** que codifica dos contadores independientes que rastrean el ciclo de vida completo de una llanta física:

#### **Formato: [VV][R]**

- **VV** (posiciones 1-2): Contador de vehículos en los que ha estado montada la llanta (00-99)
- **R** (posición 3): Contador de reencauches que ha recibido la llanta (0-9)

#### **Ejemplos:**

| generation | vehicle_count | retread_count | Significado |
|-----------|---------------|---------------|-------------|
| `000` | 0 | 0 | Llanta nueva en inventario, nunca montada |
| `010` | 1 | 0 | Llanta nueva montada en su primer vehículo |
| `030` | 3 | 0 | Llanta nueva que ha estado en 3 vehículos diferentes |
| `001` | 0 | 1 | Llanta con 1 reencauche en inventario, no montada aún |
| `011` | 1 | 1 | Llanta con 1 reencauche montada en su primer vehículo post-reencauche |
| `032` | 3 | 2 | Llanta con 2 reencauches que ha estado en 3 vehículos desde el último reencauche |

#### **Reglas de Negocio del Campo generation:**

1. **Llanta nueva (compra inicial)**: `generation = '000'`

2. **Montaje en vehículo NUEVO**:
   - Incrementa contador de vehículos: `'020'` → `'030'`
   - **NO incrementa en rotación** dentro del mismo vehículo

3. **Desmontaje**:
   - `generation` **NO cambia** al desmontar

4. **Reencauche**:
   - Resetea contador de vehículos a `00`
   - Incrementa contador de reencauches: `'030'` → `'001'`
   - Llanta queda en estado INVENTARIO con generation `'001'`

5. **Montaje post-reencauche**:
   - Incrementa contador de vehículos: `'001'` → `'011'`

#### **Secuencia completa de ejemplo:**

```
Compra                    → generation = '000' (INVENTARIO)
Monta en VH-100          → generation = '010' (ACTIVA)
Desmonta de VH-100       → generation = '010' (INVENTARIO)
Monta en VH-200          → generation = '020' (ACTIVA)
Desmonta de VH-200       → generation = '020' (INVENTARIO)
Monta en VH-300          → generation = '030' (ACTIVA)
Desmonta de VH-300       → generation = '030' (INTERMEDIO)
Envía a reencauche       → generation = '001' (INVENTARIO) ← RESET vehículos, +1 reencauche
Monta en VH-400          → generation = '011' (ACTIVA)
Rota a otra posición VH-400 → generation = '011' (ACTIVA) ← NO cambia
```

#### **Implicaciones en el Diseño:**

- **Tabla `tires`**: Contiene **UN SOLO REGISTRO** por llanta física. El campo `generation` es **MUTABLE** y se actualiza mediante `UPDATE` en cada evento del ciclo de vida.

- **Tabla `history_records`**: Contiene **MÚLTIPLES REGISTROS** por llanta física. El campo `generation_at_event` captura el valor **INMUTABLE** de `generation` en el momento del evento histórico.

- **Constraint UNIQUE**: Solo `tire_number` debe ser UNIQUE en la tabla `tires`, **NO** `(tire_number, generation)`.

- **Funciones Helper SQL**: Se proveen funciones para parsear y manipular el campo:
  - `tire_management.get_vehicle_count(generation)` → Extrae VV
  - `tire_management.get_retread_count(generation)` → Extrae R
  - `tire_management.increment_vehicle_count(generation)` → VV + 1
  - `tire_management.increment_retread_count(generation)` → Reset VV a 00, R + 1
  - `tire_management.build_generation(vehicle_count, retread_count)` → Construye código

---

### 5.12 ✅ Trigger para Validación de Reglas de Negocio Complejas

```sql
-- Trigger: Prevenir montaje si posición ya ocupada
CREATE OR REPLACE FUNCTION prevent_duplicate_position()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM tire_active_installations
        WHERE vehicle_id = NEW.vehicle_id
        AND position = NEW.position
        AND deleted_at IS NULL
        AND id != NEW.id
    ) THEN
        RAISE EXCEPTION 'Position % in vehicle % is already occupied',
            NEW.position, NEW.vehicle_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_position_uniqueness
BEFORE INSERT OR UPDATE ON tire_active_installations
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_position();

-- Trigger: Validar kilometrajes monotónicos
CREATE OR REPLACE FUNCTION validate_mileage_monotonicity()
RETURNS TRIGGER AS $$
DECLARE
    v_current_mileage INTEGER;
BEGIN
    SELECT current_mileage INTO v_current_mileage
    FROM vehicles
    WHERE id = NEW.vehicle_id;

    IF NEW.mileage_at_installation < v_current_mileage THEN
        RAISE EXCEPTION 'Installation mileage (%) cannot be less than vehicle current mileage (%)',
            NEW.mileage_at_installation, v_current_mileage;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_installation_mileage
BEFORE INSERT ON tire_active_installations
FOR EACH ROW
EXECUTE FUNCTION validate_mileage_monotonicity();
```

---

## 6. ESTRATEGIA DE MIGRACIÓN

### 6.1 Fases de Migración

```
┌──────────────────────────────────────────────────────────────────┐
│                   ESTRATEGIA DE MIGRACIÓN                         │
└──────────────────────────────────────────────────────────────────┘

FASE 1: PREPARACIÓN (Semana 1-2)
├─ 1.1 Backup completo de Oracle
├─ 1.2 Análisis de volúmenes de datos
├─ 1.3 Identificación de dependencias
├─ 1.4 Creación de esquema PostgreSQL en DEV
└─ 1.5 Configuración de herramientas de migración

FASE 2: MIGRACIÓN DE CATÁLOGOS (Semana 3)
├─ 2.1 Migrar tablas maestras (marcas, tipos, referencias, etc.)
├─ 2.2 Validar integridad de datos
├─ 2.3 Migrar proveedores
└─ 2.4 Migrar clases de vehículos

FASE 3: MIGRACIÓN DE VEHÍCULOS (Semana 4)
├─ 3.1 Migrar tabla VEHICULOS_LLANTAS → vehicles
├─ 3.2 Validar kilometrajes
└─ 3.3 Verificar integridad con CLASES

FASE 4: MIGRACIÓN DE FICHAS TÉCNICAS (Semana 5)
├─ 4.1 Migrar FICHATEC → tire_technical_specifications
├─ 4.2 Transformar campos a JSONB (expected_performance)
└─ 4.3 Validar referencias a marcas/tipos/referencias

FASE 5: MIGRACIÓN DE HISTÓRICO (Semana 6-7)
├─ 5.1 Migrar HISTORIA → tire_history_records (tabla particionada)
│   ├─ Convertir campos NOT NULL incorrectos a NULLABLE
│   ├─ Crear registros "abiertos" para montajes actuales
│   └─ Cerrar registros históricos completados
├─ 5.2 Migrar HISTOMUES → tire_sampling_history
└─ 5.3 Validar integridad histórica

FASE 6: MIGRACIÓN DE ESTADOS ACTUALES (Semana 8)
├─ 6.1 Migrar INVENTARIO → tire_inventory
├─ 6.2 Migrar LLANTAS → tire_active_installations
│   └─ Validar constraint UK_VEHI_POS
├─ 6.3 Migrar INTERMEDIO → tire_intermediate
├─ 6.4 Migrar RETIRADAS → tire_retired
├─ 6.5 Migrar MUESTREO → tire_samplings
└─ 6.6 Validar exclusividad de estados

FASE 7: MIGRACIÓN DE DATOS CALCULADOS (Semana 9)
├─ 7.1 Recalcular KMS_RECORRIDO_LLANTAS → tire_accumulated_mileage
├─ 7.2 Recrear vista materializada mv_tire_useful_life
└─ 7.3 Validar cálculos vs sistema legacy

FASE 8: VALIDACIÓN Y RECONCILIACIÓN (Semana 10-11)
├─ 8.1 Comparación de totales (conteos, sumas)
├─ 8.2 Validación de integridad referencial
├─ 8.3 Pruebas de queries de negocio críticas
├─ 8.4 Performance testing
└─ 8.5 Corrección de inconsistencias

FASE 9: CUTOVER (Semana 12)
├─ 9.1 Freeze de sistema legacy (modo read-only)
├─ 9.2 Migración incremental final (delta)
├─ 9.3 Despliegue de aplicación modernizada
├─ 9.4 Activación de PostgreSQL como BD primaria
└─ 9.5 Monitoreo intensivo

FASE 10: POST-MIGRACIÓN (Semana 13-14)
├─ 10.1 Validación en producción
├─ 10.2 Ajustes de performance
├─ 10.3 Capacitación a usuarios
└─ 10.4 Documentación de lecciones aprendidas
```

### 6.2 Herramientas de Migración

```bash
# Opción 1: ora2pg (Herramienta especializada Oracle → PostgreSQL)
ora2pg -c ora2pg.conf -t TABLE -o tables.sql
ora2pg -c ora2pg.conf -t INSERT -o data.sql

# Opción 2: AWS Database Migration Service (DMS)
# - Migración continua con replicación
# - Soporte para cutover con downtime mínimo

# Opción 3: Scripts Python personalizados
# - Control total sobre transformaciones
# - Validación en línea durante migración

# Opción 4: Flyway + Scripts SQL manuales
# - Control versionado de migraciones
# - Rollback controlado
```

### 6.3 Script de Validación Post-Migración

```sql
-- Script de validación: Comparar conteos entre Oracle y PostgreSQL

-- 1. Conteo de llantas por estado
-- Oracle:
SELECT 'INVENTARIO' AS estado, COUNT(*) FROM INVENTARIO
UNION ALL
SELECT 'ACTIVAS', COUNT(*) FROM LLANTAS
UNION ALL
SELECT 'INTERMEDIO', COUNT(*) FROM INTERMEDIO
UNION ALL
SELECT 'RETIRADAS', COUNT(*) FROM RETIRADAS;

-- PostgreSQL:
SELECT 'INVENTORY' AS state, COUNT(*) FROM tire_inventory WHERE deleted_at IS NULL
UNION ALL
SELECT 'ACTIVE', COUNT(*) FROM tire_active_installations WHERE deleted_at IS NULL
UNION ALL
SELECT 'INTERMEDIATE', COUNT(*) FROM tire_intermediate WHERE deleted_at IS NULL
UNION ALL
SELECT 'RETIRED', COUNT(*) FROM tire_retired WHERE deleted_at IS NULL;

-- 2. Validar integridad de HISTORIA
-- Oracle:
SELECT COUNT(*) AS total_registros,
       COUNT(DISTINCT LLANTA) AS llantas_unicas,
       SUM(CASE WHEN KREMUEVE IS NULL THEN 1 ELSE 0 END) AS registros_abiertos
FROM HISTORIA;

-- PostgreSQL:
SELECT COUNT(*) AS total_records,
       COUNT(DISTINCT tire_id) AS unique_tires,
       SUM(CASE WHEN removal_date IS NULL THEN 1 ELSE 0 END) AS open_records
FROM tire_history_records
WHERE deleted_at IS NULL;

-- 3. Validar suma de kilometrajes
-- Oracle:
SELECT SUM(KREMUEVE - KINSTALA) AS total_kms
FROM HISTORIA
WHERE KREMUEVE IS NOT NULL;

-- PostgreSQL:
SELECT SUM(mileage_at_removal - mileage_at_installation) AS total_kms
FROM tire_history_records
WHERE removal_date IS NOT NULL AND deleted_at IS NULL;
```

---

## 7. CONSIDERACIONES DE RENDIMIENTO

### 7.1 Estrategias de Indexación

```sql
-- 1. Índices para queries de listado (dashboards)
CREATE INDEX idx_tire_installations_active
ON tire_active_installations(vehicle_id, position)
WHERE deleted_at IS NULL;

-- 2. Índice para tire_number ya existe (UNIQUE constraint lo crea automáticamente)
-- generation no necesita índice separado (campo de uso interno)

-- 3. Índices para queries de histórico
CREATE INDEX idx_history_tire_created
ON tire_history_records(tire_id, created_at DESC);

-- 4. Índices para alertas (queries frecuentes)
CREATE INDEX idx_samplings_installation_date
ON tire_samplings(tire_installation_id, sampling_date DESC);

-- 5. Índices parciales para estados específicos
CREATE INDEX idx_tire_intermediate_pending
ON tire_intermediate(tire_id)
WHERE evaluation_status = 'PENDING' AND deleted_at IS NULL;
```

### 7.2 Particionamiento

```sql
-- Particionamiento de tire_history_records por año
-- Beneficio: Queries históricas solo escanean partición relevante

-- Ejemplo de query que se beneficia:
SELECT * FROM tire_history_records
WHERE created_at >= '2025-01-01' AND created_at < '2026-01-01';
-- Solo escanea partición 2025, ignora otras

-- Mantenimiento por partición:
VACUUM tire_history_records_2024;  -- Solo una partición
```

### 7.3 Caching con Redis

```java
// Caché de catálogos que rara vez cambian
@Cacheable(value = "tire-brands", key = "#id")
public TireBrand findBrandById(UUID id) {
    return tireBrandRepository.findById(id).orElseThrow();
}

// Caché de vista materializada
@Cacheable(value = "tire-useful-life", key = "#installationId")
public TireUsefulLife getUsefulLife(UUID installationId) {
    return usefulLifeRepository.findById(installationId).orElseThrow();
}

// Invalidación selectiva
@CacheEvict(value = "tire-useful-life", key = "#installationId")
public void refreshUsefulLife(UUID installationId) {
    // Recalcular
}
```

### 7.4 Connection Pooling

```yaml
# application.yml - HikariCP configuration
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: TireManagementPool
```

### 7.5 Query Optimization

```sql
-- EXPLAIN ANALYZE para identificar cuellos de botella
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM tire_active_installations ta
JOIN tires t ON ta.tire_id = t.id
WHERE ta.vehicle_id = 'uuid-here'
AND ta.deleted_at IS NULL;

-- Resultado muestra:
-- • Index scans vs Sequential scans
-- • Buffers leídos
-- • Tiempo de ejecución real
```

---

## 8. SCRIPTS SQL COMPLETOS

### 8.1 Script de Creación del Esquema

**📄 Archivo de Migración Flyway:** `backend/src/main/resources/db/migration/V2.0.0__create_tire_management_schema.sql`

El script SQL completo implementa la **Arquitectura Híbrida Multi-Esquema** descrita en la sección 4.2.

#### **8.1.1 Características Principales del Script**

| Característica | Implementación |
|----------------|----------------|
| **Esquemas PostgreSQL** | `tire_management` (dominio) + `shared` (catálogos compartidos) |
| **Total de Tablas** | 28 tablas (19 en tire_management, 9 en shared*) |
| **Sin Prefijos Redundantes** | Nombres limpios: `tire_management.brands` en lugar de `tire_brands` |
| **Foreign Keys Cross-Schema** | `tire_management.active_installations` → `shared.vehicles` |
| **ENUMs** | 5 tipos ENUM en esquema `tire_management` |
| **Índices** | 68 índices estratégicos (compuestos, parciales, GIN, BRIN) |
| **Particionamiento** | `history_records` particionada por año (RANGE) |
| **Triggers** | 6 triggers para validación y auditoría automática |
| **Vistas** | 3 vistas normales + 1 materializada (`mv_useful_life`) |
| **Auditoría Completa** | Todas las tablas con created_at, updated_at, created_by, updated_by |
| **Soft Deletes** | Todas las entidades principales con deleted_at |

*Nota: Solo `vehicles` y `vehicle_classes` del esquema `shared` están incluidas en este módulo. Otras tablas compartidas (users, roles, geographic_catalog) se definen en otros módulos.*

---

#### **8.1.2 Estructura de Esquemas**

```sql
-- =====================================================
-- CREACIÓN DE ESQUEMAS
-- =====================================================

CREATE SCHEMA IF NOT EXISTS tire_management;
COMMENT ON SCHEMA tire_management IS 'Bounded Context: Gestión y Control de Llantas';

CREATE SCHEMA IF NOT EXISTS shared;
COMMENT ON SCHEMA shared IS 'Shared Kernel: Catálogos compartidos entre múltiples módulos (vehículos, usuarios, geografía)';
```

---

#### **8.1.3 Tipos ENUM (tire_management schema)**

```sql
-- Todos los ENUMs pertenecen al esquema tire_management
CREATE TYPE tire_management.tire_state AS ENUM (
    'INVENTORY',    -- En bodega, sin montar
    'ACTIVE',       -- Montada en vehículo
    'INTERMEDIATE', -- Desmontada, en evaluación
    'RETIRED'       -- Dada de baja definitiva
);

CREATE TYPE tire_management.evaluation_status AS ENUM (
    'PENDING',              -- Esperando evaluación
    'APPROVED_FOR_USE',     -- Puede recircular
    'REQUIRES_RETREADING',  -- Requiere reencauche
    'MUST_BE_RETIRED'       -- Debe darse de baja
);

CREATE TYPE tire_management.alert_type AS ENUM (
    'CRITICAL_DEPTH',       -- Profundidad crítica
    'IRREGULAR_WEAR',       -- Desgaste irregular
    'SAMPLING_REQUIRED',    -- Requiere muestreo
    'INCORRECT_PRESSURE',   -- Presión incorrecta
    'LOW_INVENTORY'         -- Inventario bajo
);

CREATE TYPE tire_management.alert_priority AS ENUM (
    'HIGH',   -- < 24h
    'MEDIUM', -- < 72h
    'LOW'     -- Informational
);

CREATE TYPE tire_management.tire_position_type AS ENUM (
    'DIRECTIONAL', -- Direccional
    'TRACTION',    -- Tracción
    'TRAILER'      -- Arrastre
);
```

---

#### **8.1.4 Ejemplo de Tabla con Cross-Schema FK**

```sql
-- Tabla en tire_management con FK a shared
CREATE TABLE tire_management.active_installations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- FK dentro de tire_management
    tire_id UUID NOT NULL
        REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    technical_specification_id UUID NOT NULL
        REFERENCES tire_management.technical_specifications(id) ON DELETE RESTRICT,

    -- FK CROSS-SCHEMA a shared
    vehicle_id UUID NOT NULL
        REFERENCES shared.vehicles(id) ON DELETE RESTRICT,

    position SMALLINT NOT NULL CHECK (position >= 1 AND position <= 32),
    mileage_at_installation INTEGER NOT NULL,
    installation_date DATE NOT NULL,

    -- Información de compra (copiada desde inventory al montar)
    purchase_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    provider_id UUID NOT NULL
        REFERENCES tire_management.providers(id),
    invoice_number VARCHAR(50) NOT NULL,

    -- Metadata de auditoría
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES shared.users(id),
    updated_by BIGINT REFERENCES shared.users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES shared.users(id),

    -- Constraint de unicidad: una posición solo puede tener una llanta
    CONSTRAINT uq_active_installations_vehicle_position UNIQUE (vehicle_id, position)
);

-- Índices para optimizar queries cross-schema
CREATE INDEX idx_active_installations_vehicle
ON tire_management.active_installations(vehicle_id)
WHERE deleted_at IS NULL;

CREATE INDEX idx_active_installations_tire
ON tire_management.active_installations(tire_id)
WHERE deleted_at IS NULL;

CREATE INDEX idx_active_installations_vehicle_position
ON tire_management.active_installations(vehicle_id, position)
WHERE deleted_at IS NULL;
```

---

#### **8.1.5 Ejemplo de Vista Cross-Schema**

```sql
-- Vista que combina tablas de tire_management y shared
CREATE OR REPLACE VIEW tire_management.v_active_installations_summary AS
SELECT
    ai.id AS installation_id,
    ai.tire_id,
    t.tire_number,
    t.generation,
    ai.position,
    -- Columnas de shared.vehicles (CROSS-SCHEMA)
    v.license_plate,
    v.current_mileage AS vehicle_mileage,
    -- Columnas de shared.vehicle_classes (CROSS-SCHEMA)
    vc.name AS vehicle_class_name,
    vc.number_of_tires,
    -- Columnas de tire_management.technical_specifications
    ts.dimension,
    ts.expected_mileage,
    -- Cálculos
    v.current_mileage - ai.mileage_at_installation AS km_since_installation,
    -- Metadata
    ai.installation_date,
    ai.created_at
FROM tire_management.active_installations ai
JOIN tire_management.tires t ON ai.tire_id = t.id
JOIN shared.vehicles v ON ai.vehicle_id = v.id
JOIN shared.vehicle_classes vc ON v.vehicle_class_id = vc.id
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id
WHERE ai.deleted_at IS NULL
  AND v.deleted_at IS NULL
  AND v.is_active = true
ORDER BY v.license_plate, ai.position;
```

---

#### **8.1.6 Particionamiento de Tabla de Historia**

```sql
-- Tabla particionada por rango de fecha (año)
CREATE TABLE tire_management.history_records (
    id UUID DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL,
    generation_at_event CHAR(3) NOT NULL,  -- Snapshot INMUTABLE del generation en momento del evento
    vehicle_id UUID,  -- NULLABLE hasta que se monte
    position SMALLINT,
    mileage_at_installation INTEGER,
    installation_date DATE,
    mileage_at_removal INTEGER,  -- NULLABLE hasta que se desmonte
    removal_date DATE,  -- NULLABLE hasta que se desmonte
    removal_reason_id UUID,

    -- Información de compra
    purchase_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    provider_id UUID NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,

    -- Reencauche (si aplica)
    is_retreaded BOOLEAN NOT NULL DEFAULT false,
    retreading_number SMALLINT CHECK (retreading_number >= 0 AND retreading_number <= 9),
    tread_compound_id UUID,
    retreading_cost NUMERIC(12,2),

    -- Metadata
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,

    -- Constraints
    CHECK (removal_date IS NULL OR removal_date >= installation_date),
    CHECK (mileage_at_removal IS NULL OR mileage_at_removal >= mileage_at_installation)
) PARTITION BY RANGE (created_at);

-- Crear particiones por año
CREATE TABLE tire_management.history_records_2024
PARTITION OF tire_management.history_records
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE tire_management.history_records_2025
PARTITION OF tire_management.history_records
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE tire_management.history_records_2026
PARTITION OF tire_management.history_records
FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

-- Índice BRIN para fecha (eficiente en datos secuenciales)
CREATE INDEX idx_history_records_created_at_brin
ON tire_management.history_records USING BRIN(created_at);
```

---

#### **8.1.7 Configuración de Spring Boot / Hibernate**

Para trabajar con múltiples esquemas en Spring Boot:

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vortice_db
    username: vortice_app
    password: ${DB_PASSWORD}

  jpa:
    properties:
      hibernate:
        default_schema: tire_management  # Esquema por defecto
        jdbc:
          lob:
            non_contextual_creation: true
        # Configurar search_path para incluir ambos esquemas
    database-schema: tire_management,shared

  flyway:
    schemas:
      - tire_management
      - shared
    default-schema: tire_management
```

**Configuración de Entidades JPA:**

```java
// Entidad en tire_management (usa default_schema)
@Entity
@Table(name = "tires")  // No necesita schema si es el default
@Where(clause = "deleted_at IS NULL")
public class Tire {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "tire_number", nullable = false, length = 20)
    private String tireNumber;

    @Column(name = "generation", nullable = false, length = 3)
    private String generation;

    // FK a otra tabla del mismo esquema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technical_specification_id", nullable = false)
    private TechnicalSpecification technicalSpecification;

    // ... otros campos
}

// Entidad en shared (debe especificar schema explícitamente)
@Entity
@Table(name = "vehicles", schema = "shared")
@Where(clause = "deleted_at IS NULL")
public class Vehicle {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 10)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_class_id", nullable = false)
    private VehicleClass vehicleClass;

    // ... otros campos
}

// Entidad con FK cross-schema
@Entity
@Table(name = "active_installations")
@Where(clause = "deleted_at IS NULL")
public class ActiveInstallation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tire_id", nullable = false)
    private Tire tire;  // Mismo esquema (tire_management)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;  // Cross-schema: shared.vehicles

    @Column(name = "position", nullable = false)
    private Short position;

    // ... otros campos
}
```

---

#### **8.1.8 Script Completo**

📄 **Ubicación:** `backend/src/main/resources/db/migration/V2.0.0__create_tire_management_schema.sql`

**Contenido del script (resumen):**
- ✅ Creación de esquemas `tire_management` y `shared`
- ✅ 5 tipos ENUM en esquema `tire_management`
- ✅ 28 tablas (19 en tire_management, 2 en shared para este módulo)
- ✅ 68 índices estratégicos (compuestos, parciales, GIN, BRIN)
- ✅ 45+ foreign keys (incluyendo cross-schema)
- ✅ 6 triggers para validación y auditoría
- ✅ 3 vistas + 1 vista materializada
- ✅ Particionamiento de `history_records` por año
- ✅ Comentarios completos en todas las tablas y columnas clave

**El script es ejecutable directamente con Flyway o psql:**

```bash
# Con Flyway (recomendado)
./mvnw flyway:migrate

# Con psql (manual)
psql -U vortice_app -d vortice_db -f backend/src/main/resources/db/migration/V2.0.0__create_tire_management_schema.sql
```

---

### 8.2 Script de Migración de Datos (Ejemplo: FICHATEC → technical_specifications)

```sql
-- =====================================================
-- SCRIPT DE MIGRACIÓN: FICHATEC → tire_management.technical_specifications
-- =====================================================

-- Paso 1: Insertar UUID en tablas de catálogos primero (dependencias)
-- Ya deben estar migradas: tire_management.brands, types, tire_references, providers

-- Paso 2: Migrar FICHATEC a tire_management.technical_specifications
INSERT INTO tire_management.technical_specifications (
    id,
    code,
    brand_id,
    type_id,
    reference_id,
    dimension,
    expected_mileage,
    mileage_range_min,
    mileage_range_avg,
    mileage_range_max,
    expected_retreads,
    expected_loss_percentage,
    total_expected,
    cost_per_hour,
    initial_depth_internal_mm,
    initial_depth_central_mm,
    initial_depth_external_mm,
    last_purchase_quantity,
    last_purchase_unit_price,
    last_purchase_date,
    main_provider_id,
    secondary_provider_id,
    last_used_provider_id,
    weight_kg,
    is_active,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),  -- Nuevo UUID
    LPAD(ft.CODIGO::TEXT, 20, '0'),  -- Code con padding
    tb.id,  -- FK a tire_management.brands (ya migrada)
    tt.id,  -- FK a tire_management.types (ya migrada)
    tr.id,  -- FK a tire_management.tire_references (ya migrada)
    ft.DIMENSION,
    ft.KESPERA,  -- expected_mileage
    ft.KMENOR,   -- mileage_range_min
    ft.KMEDIO,   -- mileage_range_avg
    ft.KMAYOR,   -- mileage_range_max
    ft.RESPERA,  -- expected_retreads
    ft.PERDIDA,  -- expected_loss_percentage
    ft.TOTOAL,   -- total_expected
    ft.COSTOH,   -- cost_per_hour
    ft.PI,       -- initial_depth_internal_mm
    ft.PC,       -- initial_depth_central_mm
    ft.PD,       -- initial_depth_external_mm
    ft.UCOMPRA,  -- last_purchase_quantity
    ft.UPRECIO,  -- last_purchase_unit_price
    ft.UFECHA,   -- last_purchase_date
    tp1.id,      -- main_provider
    tp2.id,      -- secondary_provider
    tp3.id,      -- last_used_provider
    ft.PESO,     -- weight_kg
    true,        -- is_active
    COALESCE(ft.UFECHA, CURRENT_TIMESTAMP),  -- created_at
    CURRENT_TIMESTAMP  -- updated_at
FROM LLANTAS.FICHATEC ft
-- JOINs con tablas de catálogos ya migradas (tire_management schema)
LEFT JOIN tire_management.brands tb ON tb.code = LPAD(ft.MARCA::TEXT, 10, '0')
LEFT JOIN tire_management.types tt ON tt.code = LPAD(ft.TIPO::TEXT, 10, '0')
LEFT JOIN tire_management.tire_references tr ON tr.code = LPAD(ft.REF::TEXT, 20, '0')
LEFT JOIN tire_management.providers tp1 ON tp1.code = LPAD(ft.PROVEE1::TEXT, 20, '0')
LEFT JOIN tire_management.providers tp2 ON tp2.code = LPAD(ft.PROVEE2::TEXT, 20, '0')
LEFT JOIN tire_management.providers tp3 ON tp3.code = LPAD(ft.PROVEEU::TEXT, 20, '0');

-- Paso 3: Validar migración
SELECT
    (SELECT COUNT(*) FROM LLANTAS.FICHATEC) AS legacy_count,
    (SELECT COUNT(*) FROM tire_management.technical_specifications) AS modern_count,
    (SELECT COUNT(*) FROM tire_management.technical_specifications) -
        (SELECT COUNT(*) FROM LLANTAS.FICHATEC) AS difference;

-- Paso 4: Crear tabla de mapeo para referencias futuras (en esquema de migración temporal)
CREATE TABLE IF NOT EXISTS migration.mapping_fichatec (
    legacy_codigo INTEGER PRIMARY KEY,
    modern_uuid UUID NOT NULL REFERENCES tire_management.technical_specifications(id)
);

INSERT INTO migration.mapping_fichatec (legacy_codigo, modern_uuid)
SELECT
    ft.CODIGO,
    tts.id
FROM LLANTAS.FICHATEC ft
JOIN tire_management.technical_specifications tts
    ON tts.code = LPAD(ft.CODIGO::TEXT, 20, '0');

-- Paso 5: Verificar integridad referencial
-- Comprobar que todos los FKs apuntan a registros válidos
SELECT
    'brands' AS catalog,
    COUNT(*) FILTER (WHERE brand_id IS NULL) AS null_fks,
    COUNT(*) AS total
FROM tire_management.technical_specifications
UNION ALL
SELECT
    'types' AS catalog,
    COUNT(*) FILTER (WHERE type_id IS NULL) AS null_fks,
    COUNT(*) AS total
FROM tire_management.technical_specifications
UNION ALL
SELECT
    'tire_references' AS catalog,
    COUNT(*) FILTER (WHERE reference_id IS NULL) AS null_fks,
    COUNT(*) AS total
FROM tire_management.technical_specifications;

-- Resultado esperado: null_fks = 0 para todos los catálogos
```

---

## 9. RECOMENDACIONES FINALES

### 9.1 Estrategia de Implementación

```
┌──────────────────────────────────────────────────────┐
│        ROADMAP DE IMPLEMENTACIÓN (12 SEMANAS)       │
└──────────────────────────────────────────────────────┘

SPRINT 1-2: FUNDACIONES
├─ Crear esquema PostgreSQL en DEV
├─ Implementar tablas de catálogos
├─ Migrar datos maestros (marcas, tipos, referencias)
└─ Tests de integridad

SPRINT 3-4: CORE DOMAIN
├─ Implementar tablas de estados (inventory, active, intermediate, retired)
├─ Implementar tire_history_records (particionada)
├─ Migrar HISTORIA y validar integridad
└─ Tests de reglas de negocio

SPRINT 5-6: APLICACIÓN BACKEND
├─ Implementar Aggregates en Java (Tire, TireInstallation, Vehicle)
├─ Implementar Repositories con Spring Data JPA
├─ Implementar Domain Services
└─ Tests unitarios y de integración

SPRINT 7-8: CASOS DE USO PRINCIPALES
├─ Implementar montaje de llantas
├─ Implementar desmontaje de llantas
├─ Implementar registro de muestreos
├─ Implementar alertas automáticas
└─ Tests end-to-end

SPRINT 9-10: REPORTES Y ANÁLISIS
├─ Crear vistas materializadas
├─ Implementar queries de reportes
├─ Implementar dashboards
└─ Performance testing

SPRINT 11: MIGRACIÓN FINAL
├─ Migración completa de datos legacy
├─ Validación exhaustiva
├─ Performance tuning
└─ Documentación

SPRINT 12: DESPLIEGUE Y MONITOREO
├─ Despliegue en producción
├─ Cutover
├─ Monitoreo intensivo
└─ Ajustes post-deployment
```

### 9.2 Mejores Prácticas para el Equipo

#### **9.2.1 Desarrollo Backend (Java + Spring Boot)**

```java
// Ejemplo: Entity con auditoría y soft delete
@Entity
@Table(name = "tires")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE tires SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Tire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "tire_number", nullable = false, length = 20)
    private String tireNumber;

    @Column(name = "generation", nullable = false, length = 3)
    private String generation;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private TireState currentState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technical_specification_id", nullable = false)
    private TireTechnicalSpecification technicalSpecification;

    // Optimistic Locking
    @Version
    private Integer version;

    // Auditoría
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    // Soft delete
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    // Business methods
    public void markAsActive() {
        this.currentState = TireState.ACTIVE;
    }

    public void markAsIntermediate() {
        this.currentState = TireState.INTERMEDIATE;
    }

    public boolean canBeMounted() {
        return this.currentState == TireState.INVENTORY ||
               this.currentState == TireState.INTERMEDIATE;
    }
}
```

#### **9.2.2 Queries con Spring Data JPA**

```java
@Repository
public interface TireRepository extends JpaRepository<Tire, UUID> {

    // Query methods derivados
    Optional<Tire> findByTireNumber(String tireNumber);  // tire_number es UNIQUE

    List<Tire> findByCurrentState(TireState state);

    // @Query para queries complejas
    @Query("""
        SELECT t FROM Tire t
        WHERE t.currentState = :state
        AND t.deletedAt IS NULL
        ORDER BY t.createdAt DESC
        """)
    List<Tire> findActiveByState(@Param("state") TireState state);

    // Query nativa cuando sea necesario
    @Query(value = """
        SELECT t.* FROM tires t
        JOIN tire_active_installations tai ON t.id = tai.tire_id
        WHERE tai.vehicle_id = :vehicleId
        AND tai.deleted_at IS NULL
        """, nativeQuery = true)
    List<Tire> findByVehicleId(@Param("vehicleId") UUID vehicleId);

    // Projection para DTOs
    @Query("""
        SELECT new com.vortice.tires.dto.TireSummaryDTO(
            t.id,
            t.tireNumber,
            t.generation,
            t.currentState,
            ts.dimension
        )
        FROM Tire t
        JOIN t.technicalSpecification ts
        WHERE t.currentState = :state
        """)
    List<TireSummaryDTO> findSummariesByState(@Param("state") TireState state);
}
```

#### **9.2.3 Configuración de Flyway para Migraciones**

```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    validate-on-migrate: true
    out-of-order: false
    schemas: tire_management
```

```
db/migration/
├── V1.0.0__create_catalog_tables.sql
├── V1.0.1__create_vehicle_tables.sql
├── V1.0.2__create_tire_core_tables.sql
├── V1.0.3__create_tire_history_tables.sql
├── V1.0.4__create_sampling_tables.sql
├── V1.0.5__create_indexes.sql
├── V1.0.6__create_triggers.sql
├── V1.0.7__create_views.sql
└── V1.0.8__create_materialized_views.sql
```

### 9.3 Monitoreo y Observabilidad

```sql
-- Queries para monitoreo de salud del sistema

-- 1. Conteo de llantas por estado
SELECT
    current_state,
    COUNT(*) as qty
FROM tires
WHERE deleted_at IS NULL
GROUP BY current_state;

-- 2. Llantas activas próximas a límite crítico (< 1.6mm)
SELECT
    COUNT(*) as critical_tires
FROM v_active_tires_with_last_sampling
WHERE current_avg_depth < 1.6;

-- 3. Alertas pendientes por prioridad
SELECT
    priority,
    COUNT(*) as pending_alerts
FROM tire_alerts
WHERE is_acknowledged = false
GROUP BY priority
ORDER BY
    CASE priority
        WHEN 'HIGH' THEN 1
        WHEN 'MEDIUM' THEN 2
        WHEN 'LOW' THEN 3
    END;

-- 4. Performance de queries críticas
SELECT
    schemaname,
    tablename,
    indexrelname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC
LIMIT 20;

-- 5. Tamaño de tablas
SELECT
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 10;
```

### 9.4 Backup y Recuperación

```bash
# Backup completo diario
pg_dump -h localhost -U vortice_user -d vortice_db \
    --format=custom \
    --file=backup_vortice_$(date +%Y%m%d).dump

# Backup solo del esquema de llantas
pg_dump -h localhost -U vortice_user -d vortice_db \
    --schema=tire_management \
    --format=custom \
    --file=backup_tires_$(date +%Y%m%d).dump

# Restauración
pg_restore -h localhost -U vortice_user -d vortice_db_new \
    --verbose \
    backup_vortice_20260121.dump
```

### 9.5 Checklist de Calidad

```
☐ Todas las tablas tienen primary key
☐ Todas las foreign keys están definidas
☐ Todas las columnas tienen tipo de dato apropiado
☐ Todas las tablas de negocio tienen columnas de auditoría
☐ Todas las tablas principales tienen soft delete
☐ Todas las tablas con concurrencia tienen version
☐ Todos los índices críticos están creados
☐ Todos los constraints CHECK están definidos
☐ Todos los triggers de validación están implementados
☐ Todas las vistas necesarias están creadas
☐ Vista materializada tiene refresh programado
☐ Comentarios en tablas y columnas críticas
☐ Scripts de migración versionados con Flyway
☐ Tests de integridad ejecutados
☐ Performance testing completado
☐ Documentación actualizada
```

---

## 10. CONCLUSIÓN

El modelo de datos modernizado propuesto para PostgreSQL 18 representa una mejora sustancial sobre el modelo legacy de Oracle 11g, incorporando:

✅ **45+ Foreign Keys** garantizando integridad referencial completa
✅ **68 índices estratégicos** optimizando las consultas más comunes
✅ **Auditoría completa** con columnas estándar en todas las tablas
✅ **Soft deletes** protegiendo contra pérdida accidental de datos
✅ **Optimistic locking** previniendo conflictos de concurrencia
✅ **UUIDs para escalabilidad** facilitando sharding futuro
✅ **Particionamiento** para tablas históricas grandes
✅ **Vistas materializadas** para reportes de alto rendimiento
✅ **Triggers de validación** garantizando reglas de negocio
✅ **Nombrado consistente y autodocumentado** mejorando mantenibilidad

**Elaborado por:** Equipo de Arquitectura Vórtice
**Fecha:** 21 de Enero de 2026
**Versión:** 1.0
**Estado:** Listo para Revisión

---

**FIN DEL INFORME**### 8.1 Script de Creación del Esquema

**📄 Archivo de Migración Flyway:** `backend/src/main/resources/db/migration/V2.0.0__create_tire_management_schema.sql`

El script SQL completo implementa la **Arquitectura Híbrida Multi-Esquema** descrita en la sección 4.2.

#### **8.1.1 Características Principales del Script**

| Característica | Implementación |
|----------------|----------------|
| **Esquemas PostgreSQL** | `tire_management` (dominio) + `shared` (catálogos compartidos) |
| **Total de Tablas** | 28 tablas (19 en tire_management, 9 en shared*) |
| **Sin Prefijos Redundantes** | Nombres limpios: `tire_management.brands` en lugar de `tire_brands` |
| **Foreign Keys Cross-Schema** | `tire_management.active_installations` → `shared.vehicles` |
| **ENUMs** | 5 tipos ENUM en esquema `tire_management` |
| **Índices** | 68 índices estratégicos (compuestos, parciales, GIN, BRIN) |
| **Particionamiento** | `history_records` particionada por año (RANGE) |
| **Triggers** | 6 triggers para validación y auditoría automática |
| **Vistas** | 3 vistas normales + 1 materializada (`mv_useful_life`) |
| **Auditoría Completa** | Todas las tablas con created_at, updated_at, created_by, updated_by |
| **Soft Deletes** | Todas las entidades principales con deleted_at |

*Nota: Solo `vehicles` y `vehicle_classes` del esquema `shared` están incluidas en este módulo. Otras tablas compartidas (users, roles, geographic_catalog) se definen en otros módulos.*

---

#### **8.1.2 Estructura de Esquemas**

```sql
-- =====================================================
-- CREACIÓN DE ESQUEMAS
-- =====================================================

CREATE SCHEMA IF NOT EXISTS tire_management;
COMMENT ON SCHEMA tire_management IS 'Bounded Context: Gestión y Control de Llantas';

CREATE SCHEMA IF NOT EXISTS shared;
COMMENT ON SCHEMA shared IS 'Shared Kernel: Catálogos compartidos entre múltiples módulos (vehículos, usuarios, geografía)';
```

---

#### **8.1.3 Tipos ENUM (tire_management schema)**

```sql
-- Todos los ENUMs pertenecen al esquema tire_management
CREATE TYPE tire_management.tire_state AS ENUM (
    'INVENTORY',    -- En bodega, sin montar
    'ACTIVE',       -- Montada en vehículo
    'INTERMEDIATE', -- Desmontada, en evaluación
    'RETIRED'       -- Dada de baja definitiva
);

CREATE TYPE tire_management.evaluation_status AS ENUM (
    'PENDING',              -- Esperando evaluación
    'APPROVED_FOR_USE',     -- Puede recircular
    'REQUIRES_RETREADING',  -- Requiere reencauche
    'MUST_BE_RETIRED'       -- Debe darse de baja
);

CREATE TYPE tire_management.alert_type AS ENUM (
    'CRITICAL_DEPTH',       -- Profundidad crítica
    'IRREGULAR_WEAR',       -- Desgaste irregular
    'SAMPLING_REQUIRED',    -- Requiere muestreo
    'INCORRECT_PRESSURE',   -- Presión incorrecta
    'LOW_INVENTORY'         -- Inventario bajo
);

CREATE TYPE tire_management.alert_priority AS ENUM (
    'HIGH',   -- < 24h
    'MEDIUM', -- < 72h
    'LOW'     -- Informational
);

CREATE TYPE tire_management.tire_position_type AS ENUM (
    'DIRECTIONAL', -- Direccional
    'TRACTION',    -- Tracción
    'TRAILER'      -- Arrastre
);
```

---

#### **8.1.4 Ejemplo de Tabla con Cross-Schema FK**

```sql
-- Tabla en tire_management con FK a shared
CREATE TABLE tire_management.active_installations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- FK dentro de tire_management
    tire_id UUID NOT NULL
        REFERENCES tire_management.tires(id) ON DELETE RESTRICT,
    technical_specification_id UUID NOT NULL
        REFERENCES tire_management.technical_specifications(id) ON DELETE RESTRICT,

    -- FK CROSS-SCHEMA a shared
    vehicle_id UUID NOT NULL
        REFERENCES shared.vehicles(id) ON DELETE RESTRICT,

    position SMALLINT NOT NULL CHECK (position >= 1 AND position <= 32),
    mileage_at_installation INTEGER NOT NULL,
    installation_date DATE NOT NULL,

    -- Información de compra (copiada desde inventory al montar)
    purchase_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    provider_id UUID NOT NULL
        REFERENCES tire_management.providers(id),
    invoice_number VARCHAR(50) NOT NULL,

    -- Metadata de auditoría
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES shared.users(id),
    updated_by BIGINT REFERENCES shared.users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES shared.users(id),

    -- Constraint de unicidad: una posición solo puede tener una llanta
    CONSTRAINT uq_active_installations_vehicle_position UNIQUE (vehicle_id, position)
);

-- Índices para optimizar queries cross-schema
CREATE INDEX idx_active_installations_vehicle
ON tire_management.active_installations(vehicle_id)
WHERE deleted_at IS NULL;

CREATE INDEX idx_active_installations_tire
ON tire_management.active_installations(tire_id)
WHERE deleted_at IS NULL;

CREATE INDEX idx_active_installations_vehicle_position
ON tire_management.active_installations(vehicle_id, position)
WHERE deleted_at IS NULL;
```

---

#### **8.1.5 Ejemplo de Vista Cross-Schema**

```sql
-- Vista que combina tablas de tire_management y shared
CREATE OR REPLACE VIEW tire_management.v_active_installations_summary AS
SELECT
    ai.id AS installation_id,
    ai.tire_id,
    t.tire_number,
    t.generation,
    ai.position,
    -- Columnas de shared.vehicles (CROSS-SCHEMA)
    v.license_plate,
    v.current_mileage AS vehicle_mileage,
    -- Columnas de shared.vehicle_classes (CROSS-SCHEMA)
    vc.name AS vehicle_class_name,
    vc.number_of_tires,
    -- Columnas de tire_management.technical_specifications
    ts.dimension,
    ts.expected_mileage,
    -- Cálculos
    v.current_mileage - ai.mileage_at_installation AS km_since_installation,
    -- Metadata
    ai.installation_date,
    ai.created_at
FROM tire_management.active_installations ai
JOIN tire_management.tires t ON ai.tire_id = t.id
JOIN shared.vehicles v ON ai.vehicle_id = v.id
JOIN shared.vehicle_classes vc ON v.vehicle_class_id = vc.id
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id
WHERE ai.deleted_at IS NULL
  AND v.deleted_at IS NULL
  AND v.is_active = true
ORDER BY v.license_plate, ai.position;
```

---

#### **8.1.6 Particionamiento de Tabla de Historia**

```sql
-- Tabla particionada por rango de fecha (año)
CREATE TABLE tire_management.history_records (
    id UUID DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL,
    generation_at_event CHAR(3) NOT NULL,  -- Snapshot INMUTABLE del generation en momento del evento
    vehicle_id UUID,  -- NULLABLE hasta que se monte
    position SMALLINT,
    mileage_at_installation INTEGER,
    installation_date DATE,
    mileage_at_removal INTEGER,  -- NULLABLE hasta que se desmonte
    removal_date DATE,  -- NULLABLE hasta que se desmonte
    removal_reason_id UUID,

    -- Información de compra
    purchase_cost NUMERIC(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    provider_id UUID NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,

    -- Reencauche (si aplica)
    is_retreaded BOOLEAN NOT NULL DEFAULT false,
    retreading_number SMALLINT CHECK (retreading_number >= 0 AND retreading_number <= 9),
    tread_compound_id UUID,
    retreading_cost NUMERIC(12,2),

    -- Metadata
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,

    -- Constraints
    CHECK (removal_date IS NULL OR removal_date >= installation_date),
    CHECK (mileage_at_removal IS NULL OR mileage_at_removal >= mileage_at_installation)
) PARTITION BY RANGE (created_at);

-- Crear particiones por año
CREATE TABLE tire_management.history_records_2024
PARTITION OF tire_management.history_records
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE tire_management.history_records_2025
PARTITION OF tire_management.history_records
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE tire_management.history_records_2026
PARTITION OF tire_management.history_records
FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

-- Índice BRIN para fecha (eficiente en datos secuenciales)
CREATE INDEX idx_history_records_created_at_brin
ON tire_management.history_records USING BRIN(created_at);
```

---

#### **8.1.7 Configuración de Spring Boot / Hibernate**

Para trabajar con múltiples esquemas en Spring Boot:

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vortice_db
    username: vortice_app
    password: ${DB_PASSWORD}

  jpa:
    properties:
      hibernate:
        default_schema: tire_management  # Esquema por defecto
        jdbc:
          lob:
            non_contextual_creation: true
        # Configurar search_path para incluir ambos esquemas
    database-schema: tire_management,shared

  flyway:
    schemas:
      - tire_management
      - shared
    default-schema: tire_management
```

**Configuración de Entidades JPA:**

```java
// Entidad en tire_management (usa default_schema)
@Entity
@Table(name = "tires")  // No necesita schema si es el default
@Where(clause = "deleted_at IS NULL")
public class Tire {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "tire_number", nullable = false, length = 20)
    private String tireNumber;

    @Column(name = "generation", nullable = false, length = 3)
    private String generation;

    // FK a otra tabla del mismo esquema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technical_specification_id", nullable = false)
    private TechnicalSpecification technicalSpecification;

    // ... otros campos
}

// Entidad en shared (debe especificar schema explícitamente)
@Entity
@Table(name = "vehicles", schema = "shared")
@Where(clause = "deleted_at IS NULL")
public class Vehicle {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 10)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_class_id", nullable = false)
    private VehicleClass vehicleClass;

    // ... otros campos
}

// Entidad con FK cross-schema
@Entity
@Table(name = "active_installations")
@Where(clause = "deleted_at IS NULL")
public class ActiveInstallation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tire_id", nullable = false)
    private Tire tire;  // Mismo esquema (tire_management)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;  // Cross-schema: shared.vehicles

    @Column(name = "position", nullable = false)
    private Short position;

    // ... otros campos
}
```

---

#### **8.1.8 Script Completo**

📄 **Ubicación:** `backend/src/main/resources/db/migration/V2.0.0__create_tire_management_schema.sql`

**Contenido del script (resumen):**
- ✅ Creación de esquemas `tire_management` y `shared`
- ✅ 5 tipos ENUM en esquema `tire_management`
- ✅ 28 tablas (19 en tire_management, 2 en shared para este módulo)
- ✅ 68 índices estratégicos (compuestos, parciales, GIN, BRIN)
- ✅ 45+ foreign keys (incluyendo cross-schema)
- ✅ 6 triggers para validación y auditoría
- ✅ 3 vistas + 1 vista materializada
- ✅ Particionamiento de `history_records` por año
- ✅ Comentarios completos en todas las tablas y columnas clave

**El script es ejecutable directamente con Flyway o psql:**

```bash
# Con Flyway (recomendado)
./mvnw flyway:migrate

# Con psql (manual)
psql -U vortice_app -d vortice_db -f backend/src/main/resources/db/migration/V2.0.0__create_tire_management_schema.sql
```

