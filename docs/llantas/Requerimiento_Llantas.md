# REQUERIMIENTOS FUNCIONALES
## SISTEMA DE GESTIÓN Y CONTROL DE LLANTAS PARA FLOTAS DE VEHÍCULOS

---

**Versión:** 1.0  
**Fecha:** 20 de Enero de 2026  
**Elaborado por:** Análisis de documentación técnica existente

---

## 📋 TABLA DE CONTENIDO

1. [Introducción](#1-introducción)
2. [Alcance del Sistema](#2-alcance-del-sistema)
3. [Módulos Funcionales](#3-módulos-funcionales)
4. [Requerimientos Funcionales Detallados](#4-requerimientos-funcionales-detallados)
5. [Entidades Principales](#5-entidades-principales)
6. [Flujos de Proceso](#6-flujos-de-proceso)
7. [Reportes y Consultas](#7-reportes-y-consultas)

---

## ⚠️ PREREQUISITO CRÍTICO: Estructura Organizacional Multi-Sede

**IMPORTANTE:** Antes de implementar cualquier RF del módulo Tire, debe implementarse la estructura organizacional multi-sede.

El sistema debe soportar múltiples sedes/oficinas con la siguiente jerarquía:

```
EMPRESA (TRANSER)
    │
    ├── SEDE/OFICINA 1 (ej: Bogotá)
    │       │
    │       ├── ALMACÉN 1 (ej: Almacén Principal)
    │       │       ├── UBICACIÓN 1 (ej: Estante A1)
    │       │       └── UBICACIÓN 2 (ej: Estante A2)
    │       │
    │       ├── ALMACÉN 2 (ej: Almacén Taller)
    │       │       └── UBICACIÓN 1 (ej: Zona de Trabajo)
    │       │
    │       └── PROVEEDORES (específicos de la sede)
    │
    └── SEDE/OFICINA 2 (ej: Medellín)
            └── ...
```

### Características Clave:
- ✅ Cada **usuario** está asignado a UNA sede específica
- ✅ Los usuarios ven SOLO el inventario de su sede (aislamiento por Row-Level Security)
- ✅ Las **fichas técnicas** son GLOBALES (compartidas entre todas las sedes)
- ✅ Los **almacenes, ubicaciones y proveedores** son específicos por sede
- ✅ Una llanta física en Bogotá es diferente a otra en Medellín (inventarios separados)
- ✅ Los **traslados entre sedes** requieren proceso especial con aprobación
- ✅ Los **reportes** pueden ser por sede o consolidados (según rol)

### Roles de Seguridad:
- **ROLE_ADMIN_NATIONAL:** Acceso a todas las sedes (reportes consolidados)
- **ROLE_ADMIN_OFFICE:** Administrador de sede específica
- **ROLE_WAREHOUSE_MANAGER:** Gestión de almacén específico
- **ROLE_MECHANIC:** Operario de taller (acceso restringido)

**Referencia:** Ver RF-001-EXT para detalles completos de implementación.

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Documento
Este documento especifica los requerimientos funcionales del Sistema de Gestión y Control de Llantas, diseñado para administrar el ciclo de vida completo de neumáticos en una flota de vehículos de transporte.

### 1.2 Visión General del Sistema
El sistema permite:
- Controlar el inventario de llantas (nuevas, reencauchadas, en uso, retiradas)
- Gestionar la instalación y rotación de llantas en vehículos
- Realizar seguimiento del desgaste mediante muestreos periódicos
- Controlar costos y rendimiento por llanta
- Generar reportes de gestión y análisis de consumo

### 1.3 Usuarios del Sistema
- **Jefe de Taller/Mantenimiento:** Control general de llantas
- **Operarios de Taller:** Registro de montajes, desmontajes y muestreos
- **Administrador de Flota:** Análisis de costos y rendimiento
- **Almacenista:** Control de inventarios

---

## 2. ALCANCE DEL SISTEMA

### 2.1 Ciclo de Vida de las Llantas
El sistema gestiona las llantas en los siguientes estados:

```
┌─────────────┐
│ INVENTARIO  │ ← Llantas nuevas o reencauchadas sin montar
└──────┬──────┘
       │ Montaje
       ↓
┌─────────────┐
│   LLANTAS   │ ← Llantas instaladas actualmente en vehículos
│  (ACTIVAS)  │   (Incluye muestreos periódicos)
└──────┬──────┘
       │ Desmontaje
       ↓
┌─────────────┐
│ INTERMEDIO  │ ← Llantas desmontadas, aptas para recircular
└──────┬──────┘
       │ Evaluación
       ├────────→ Reencauche/Reparación → INVENTARIO
       │
       ↓
┌─────────────┐
│  RETIRADAS  │ ← Llantas dadas de baja
└─────────────┘
       │
       ↓
┌─────────────┐
│   HISTORIA  │ ← Registro histórico de toda la vida útil
└─────────────┘
```

### 2.2 Procesos Principales
1. Gestión de Maestros (Catálogos)
2. Control de Inventario
3. Montaje/Desmontaje de Llantas
4. Muestreo y Control de Desgaste
5. Gestión de Bajas
6. Reportes y Análisis

---

## 3. MÓDULOS FUNCIONALES

### 3.1 Módulo de Administración de Maestros (ALFA)
**Formulario:** ALFA.FMB

**Funcionalidades:**
- Gestión de Catálogos Base:
  - Marcas de llantas
  - Tipos de llantas
  - Referencias de llantas
  - Proveedores
  - Clases de vehículos
  - Localizaciones de inventario
  - Observaciones/Motivos de baja
  - Fichas técnicas de llantas
  - Protectores y neumáticos

**Tablas Involucradas:**
- `MARCAS_LLANTAS`
- `TIPOS`
- `REFERENCIA`
- `PROVEEDORES_LLANTAS`
- `CLASES`
- `LOCALIZA`
- `OBSERVA`
- `FICHATEC`
- `NEUMATICO`

---

### 3.2 Módulo de Gestión de Vehículos (MLFR008)
**Formulario:** MLFR008.FMB

**Funcionalidades:**
- Registro y mantenimiento de vehículos de la flota
- Campos principales:
  - Placa (identificador único)
  - Clase de vehículo
  - Marca y modelo
  - Kilometraje inicial y actual
  - Estado del vehículo
  - Estado operativo

**Validaciones:**
- Modelo debe ser >= 1970
- Clase debe existir en catálogo CLASES
- Placa única en el sistema

**Tablas Involucradas:**
- `VEHICULOS_LLANTAS`
- `CLASES` (FK)

---

### 3.3 Módulo de Muestreo de Llantas (MLFR009)
**Formulario:** MLFR009.FMB

**Funcionalidades:**
- Registro de muestreos periódicos de profundidad de banda
- Medición en tres puntos:
  - **PI:** Profundidad Interna
  - **PC:** Profundidad Central
  - **PD:** Profundidad Derecha
- Registro de presión de inflado
- Control de kilometraje en el momento del muestreo

**Proceso de Muestreo:**
1. Selección del vehículo
2. Visualización de llantas activas en el vehículo
3. Ingreso de datos para cada llanta:
   - Kilometraje actual del vehículo
   - Profundidades (PI, PC, PD)
   - Presión
   - Fecha del muestreo

**Validaciones:**
- Kilometraje de muestreo debe ser >= kilometraje de instalación
- Profundidades no pueden exceder profundidades iniciales
- Fecha de muestreo >= fecha de instalación
- Actualiza automáticamente los kilómetros recorridos por cada llanta

**Tablas Involucradas:**
- `MUESTREO` (registro del muestreo)
- `HISTOMUES` (histórico de muestreos)
- `LLANTAS` (llantas activas)
- `KMS_RECORRIDO_LLANTAS` (actualización automática de KMs)

**Cálculos Automáticos:**
```sql
-- Profundidad promedio
Profundidad_Promedio = (PI + PC + PD) / 3

-- Kilómetros recorridos
KMs_Totales = (KM_Actual - KM_Instalación) + KMs_Históricos
```

---

### 3.4 Módulo de Historia de Llantas (MLFR010)
**Formulario:** MLFR010.FMB

**Funcionalidades:**
- Consulta del histórico completo de cada llanta
- Visualización de todos los montajes y desmontajes
- Seguimiento del recorrido por diferentes vehículos

**Información Mostrada:**
- Vehículo donde se instaló
- Posición de la llanta
- Fecha de instalación
- Kilometraje de instalación
- Fecha de remoción
- Kilometraje de remoción
- Motivo de remoción
- Valor de la llanta en ese momento
- Información de reencauches

**Tablas Involucradas:**
- `HISTORIA`
- `FICHATEC` (FK)
- `OBSERVA` (FK - motivo)

---

### 3.5 Módulo de Gestión de Bajas (MLFR011)
**Formulario:** MLFR011.FMB

**Funcionalidades:**
- Dar de baja llantas por diferentes motivos
- Reversar bajas (reactivar llantas)
- Gestión de llantas en estado intermedio

**Proceso de Baja:**
1. Selección de llanta en estado INTERMEDIO
2. Evaluación del estado
3. Decisión:
   - **Dar de baja:** Pasa a RETIRADAS
   - **Reencauchar:** Vuelve a INVENTARIO
   - **Recircular:** Queda en INTERMEDIO

**Proceso de Reversión:**
- Elimina el registro de RETIRADAS
- Regresa la llanta a INTERMEDIO
- Permite nueva evaluación

**Tablas Involucradas:**
- `INTERMEDIO`
- `RETIRADAS`
- `INVENTARIO`

---

### 3.6 Módulo Principal / Menú (MILENIO)
**Formulario:** MILENIO.FMB

**Funcionalidades:**
- Menú principal de la aplicación
- Control de acceso por usuario
- Gestión de permisos por formulario
- Configuración de parámetros del sistema
- Auditoría de operaciones

**Características de Seguridad:**
- Validación de usuario contra tabla USUARIOS
- Control de permisos por formulario (USUARIOS_FORMAS)
- Registro de auditoría (AUDITA)
- Parámetros por oficina/sucursal

**Tablas de Sistema:**
- `USUARIOS`
- `USUARIOS_FORMAS`
- `USUARIOS_LISTADOS`
- `PARAMETROS_SISTEMA`
- `PARAMETROS_OFICSISTEMA`
- `DISCOS` (rutas de archivos)
- `AUDITA`

---

## 4. REQUERIMIENTOS FUNCIONALES DETALLADOS

### RF-001: Gestión de Fichas Técnicas
**Prioridad:** Alta

**Descripción:**  
El sistema debe permitir el registro y mantenimiento de fichas técnicas para cada tipo de llanta.

**Datos de la Ficha Técnica:**
- Código único
- Marca
- Tipo
- Referencia
- Dimensiones (ancho, alto, radio)
- Kilómetros esperados de vida útil
- Rangos de kilometraje (mayor, menor, medio)
- Número de reencauches esperados
- Pérdida esperada (%)
- Total esperado
- Costo por hora
- Profundidades iniciales (PI, PC, PD)
- Información de última compra (cantidad, precio, fecha)
- Proveedores (principal, secundario, último usado)
- Peso


**Validaciones:**
- Código único
- Marca, tipo y referencia deben existir en catálogos
- Kilómetros esperados > 0
- Profundidades iniciales entre 0 y 99.9 mm
- Proveedores deben existir en catálogo

---

### RF-001-EXT: Gestión de Estructura Organizacional Multi-Sede
**Prioridad:** Crítica (Prerequisito de TODOS los demás RFs)

**Descripción:**
El sistema debe gestionar la estructura organizacional multi-sede de la empresa, permitiendo que cada oficina opere de forma independiente con sus propios almacenes, ubicaciones y proveedores, mientras comparte catálogos técnicos globales.

**Jerarquía Organizacional:**

```
┌─────────────────────────────────────────────┐
│ EMPRESA (TRANSER)                           │
└──────────────────┬──────────────────────────┘
                   │
    ┌──────────────┴──────────────┐
    │                             │
┌───▼────────┐            ┌───────▼──────┐
│ OFICINA 1  │            │  OFICINA 2   │
│ (Bogotá)   │            │  (Medellín)  │
└───┬────────┘            └───────┬──────┘
    │                             │
    ├── ALMACÉN 1                 ├── ALMACÉN 1
    │   ├── Ubicación A1          │   └── Ubicación M1
    │   └── Ubicación A2          │
    ├── ALMACÉN 2                 └── PROVEEDORES
    │   └── Ubicación B1              └── Proveedor M-01
    │
    └── PROVEEDORES
        ├── Proveedor B-01
        └── Proveedor B-02
```

#### Entidad: Office (Sede/Oficina)
**Tabla:** `offices`

**Descripción:** Representa cada sede u oficina de la empresa.

**Campos:**
- `id` (UUID): Identificador único
- `code` (VARCHAR(10)): Código corto único (ej: "BOG", "MED", "CALI")
- `name` (VARCHAR(100)): Nombre de la oficina (ej: "Bogotá - Sede Principal")
- `city` (VARCHAR(50)): Ciudad donde se ubica
- `address` (TEXT): Dirección física
- `phone` (VARCHAR(20)): Teléfono de contacto
- `is_active` (BOOLEAN): Estado de la oficina
- `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`: Auditoría

**Validaciones:**
- Código único en el sistema
- Nombre obligatorio
- Ciudad obligatoria
- No se puede eliminar oficina con almacenes o usuarios asociados (soft delete)

**Operaciones CRUD:**
- Crear nueva oficina
- Listar oficinas (filtro por activas/inactivas)
- Editar información de oficina
- Desactivar oficina (soft delete)

---

#### Entidad: Warehouse (Almacén)
**Tabla:** `warehouses`

**Descripción:** Representa cada almacén dentro de una oficina. Una oficina puede tener múltiples almacenes (ej: Almacén Principal, Almacén de Taller, Almacén de Reencauche).

**Campos:**
- `id` (UUID): Identificador único
- `code` (VARCHAR(10)): Código del almacén (único por oficina)
- `name` (VARCHAR(100)): Nombre descriptivo
- `office_id` (UUID): FK a `offices` - oficina a la que pertenece
- `description` (TEXT): Descripción o propósito del almacén
- `is_active` (BOOLEAN): Estado del almacén
- `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`: Auditoría

**Validaciones:**
- Código único dentro de la oficina (constraint: `uk_warehouse_office_code`)
- Nombre obligatorio
- `office_id` debe existir y estar activo
- No se puede eliminar almacén con llantas asociadas (soft delete)

**Operaciones CRUD:**
- Crear nuevo almacén en una oficina
- Listar almacenes de una oficina
- Editar información de almacén
- Desactivar almacén (soft delete)

**Regla de Negocio:**
- Un almacén pertenece a UNA SOLA oficina
- Los códigos de almacén pueden repetirse entre oficinas (ej: "PRIN" en Bogotá y "PRIN" en Medellín son diferentes)

---

#### Entidad: WarehouseLocation (Ubicación dentro del Almacén)
**Tabla:** `warehouse_locations`

**Descripción:** Representa ubicaciones físicas específicas dentro de un almacén (ej: estantes, zonas, bahías). Equivalente a la tabla legacy `LOCALIZA` pero ahora asociada a un almacén específico.

**Campos:**
- `id` (UUID): Identificador único
- `code` (VARCHAR(10)): Código de la ubicación (único por almacén)
- `name` (VARCHAR(100)): Nombre descriptivo (ej: "Estante A1", "Zona de Trabajo")
- `warehouse_id` (UUID): FK a `warehouses` - almacén al que pertenece
- `description` (TEXT): Descripción adicional
- `is_active` (BOOLEAN): Estado de la ubicación
- `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`: Auditoría

**Validaciones:**
- Código único dentro del almacén (constraint: `uk_location_warehouse_code`)
- `warehouse_id` debe existir y estar activo
- No se puede eliminar ubicación con llantas asociadas (soft delete)

**Operaciones CRUD:**
- Crear nueva ubicación en un almacén
- Listar ubicaciones de un almacén
- Editar información de ubicación
- Desactivar ubicación (soft delete)

**Regla de Negocio:**
- Una ubicación pertenece a UN SOLO almacén
- Los códigos de ubicación pueden repetirse entre almacenes

---

#### Entidad: TireSupplier (Proveedor de Llantas)
**Tabla:** `tire_suppliers`

**Descripción:** Proveedores de llantas específicos por oficina. La tabla legacy `PROVEEDORES_LLANTAS` se transforma en una tabla multi-sede donde cada proveedor está asociado a una oficina.

**Campos:**
- `id` (UUID): Identificador único
- `code` (VARCHAR(10)): Código del proveedor (único por oficina)
- `name` (VARCHAR(100)): Nombre o razón social
- `tax_id` (VARCHAR(20)): NIT o identificación tributaria
- `office_id` (UUID): FK a `offices` - oficina a la que pertenece
- `contact_name` (VARCHAR(100)): Nombre del contacto
- `email` (VARCHAR(100)): Email del proveedor
- `phone` (VARCHAR(20)): Teléfono
- `address` (TEXT): Dirección física
- `is_active` (BOOLEAN): Estado del proveedor
- `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`: Auditoría

**Validaciones:**
- Código único dentro de la oficina (constraint: `uk_supplier_office_code`)
- Nombre y tax_id obligatorios
- `office_id` debe existir y estar activo
- Email con formato válido
- No se puede eliminar proveedor con compras asociadas (soft delete)

**Operaciones CRUD:**
- Crear nuevo proveedor en una oficina
- Listar proveedores de una oficina
- Editar información de proveedor
- Desactivar proveedor (soft delete)

**Regla de Negocio:**
- Un proveedor pertenece a UNA SOLA oficina
- Si un mismo proveedor real opera en múltiples oficinas, debe registrarse por separado en cada una
- Los códigos de proveedor pueden repetirse entre oficinas

---

#### Modificación a Users (Usuarios)
**Tabla:** `users` (ya existente, se agrega campo)

**Nuevos Campos:**
- `office_id` (UUID): FK a `offices` - oficina a la que está asignado el usuario

**Reglas:**
- Todo usuario DEBE estar asignado a una oficina (campo obligatorio)
- Los usuarios solo pueden ver datos de su oficina (implementado via Row-Level Security)
- Excepción: Usuarios con rol `ROLE_ADMIN_NATIONAL` pueden ver todas las oficinas

**Validación:**
- `office_id` debe existir y estar activo
- No se puede cambiar la oficina de un usuario si tiene operaciones pendientes

---

#### Seguridad: Row-Level Security (RLS)

**Política de Aislamiento por Oficina:**

Todas las tablas relacionadas con llantas deben implementar políticas RLS:

```sql
-- Función para obtener office_id del usuario actual
CREATE OR REPLACE FUNCTION get_user_office_id()
RETURNS UUID AS $$
DECLARE
    v_office_id UUID;
BEGIN
    SELECT office_id INTO v_office_id
    FROM users
    WHERE id = current_setting('app.current_user_id')::BIGINT;
    RETURN v_office_id;
END;
$$ LANGUAGE plpgsql STABLE;

-- Política ejemplo para tire_inventory
ALTER TABLE tire_inventory ENABLE ROW LEVEL SECURITY;

CREATE POLICY tire_inventory_office_isolation ON tire_inventory
    FOR ALL
    TO authenticated_user
    USING (
        -- Admin nacional: acceso a todas las oficinas
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        -- Usuarios normales: solo su oficina
        warehouse_id IN (
            SELECT w.id FROM warehouses w
            WHERE w.office_id = get_user_office_id()
              AND w.deleted_at IS NULL
        )
    );
```

**Tablas que requieren RLS:**
- `warehouses` (filtro por `office_id`)
- `warehouse_locations` (filtro via `warehouse_id`)
- `tire_suppliers` (filtro por `office_id`)
- `tire_inventory` (filtro via `warehouse_id → office_id`)
- `tire_specifications` (NO requiere RLS, son globales)

---

#### Catálogos Globales vs Específicos de Oficina

**Catálogos GLOBALES (compartidos entre todas las oficinas):**
- ✅ `tire_brands` (Marcas de llantas)
- ✅ `tire_types` (Tipos de llantas)
- ✅ `tire_references` (Referencias)
- ✅ `tire_specifications` (Fichas técnicas)
- ✅ `observation_reasons` (Motivos de baja/observaciones)

**Catálogos ESPECÍFICOS por oficina:**
- 🏢 `warehouses` (Almacenes)
- 🏢 `warehouse_locations` (Ubicaciones)
- 🏢 `tire_suppliers` (Proveedores)

**Datos Transaccionales (específicos por oficina):**
- 🏢 `tire_inventory` (Inventario de llantas)
- 🏢 `tire_active` (Llantas montadas en vehículos)
- 🏢 `tire_intermediate` (Llantas desmontadas)
- 🏢 `tire_retired` (Llantas dadas de baja)
- 🏢 `tire_history` (Histórico)

---

#### Flujos de Gestión

**1. Creación de Nueva Oficina:**
```
Admin Nacional → Crear Office → Crear Warehouse(s) → Crear Locations
                                ↓
                        Crear Tire Suppliers
                                ↓
                        Asignar Users a la oficina
```

**2. Consulta de Inventario (Usuario Normal):**
```
User autenticado → Sistema obtiene office_id del user
                → RLS filtra automáticamente warehouse_id de esa oficina
                → Usuario ve SOLO inventario de su oficina
```

**3. Reporte Consolidado (Admin Nacional):**
```
Admin Nacional → Sistema detecta rol ROLE_ADMIN_NATIONAL
              → RLS permite acceso a TODAS las oficinas
              → Reporte incluye datos de todas las sedes
```

---

#### Endpoints REST Necesarios

**Offices:**
- `POST /api/offices` - Crear oficina
- `GET /api/offices` - Listar oficinas
- `GET /api/offices/{id}` - Obtener oficina por ID
- `PUT /api/offices/{id}` - Actualizar oficina
- `DELETE /api/offices/{id}` - Desactivar oficina (soft delete)

**Warehouses:**
- `POST /api/offices/{officeId}/warehouses` - Crear almacén en oficina
- `GET /api/offices/{officeId}/warehouses` - Listar almacenes de oficina
- `GET /api/warehouses/{id}` - Obtener almacén por ID
- `PUT /api/warehouses/{id}` - Actualizar almacén
- `DELETE /api/warehouses/{id}` - Desactivar almacén

**WarehouseLocations:**
- `POST /api/warehouses/{warehouseId}/locations` - Crear ubicación
- `GET /api/warehouses/{warehouseId}/locations` - Listar ubicaciones
- `GET /api/warehouse-locations/{id}` - Obtener ubicación por ID
- `PUT /api/warehouse-locations/{id}` - Actualizar ubicación
- `DELETE /api/warehouse-locations/{id}` - Desactivar ubicación

**TireSuppliers:**
- `POST /api/offices/{officeId}/suppliers` - Crear proveedor
- `GET /api/offices/{officeId}/suppliers` - Listar proveedores de oficina
- `GET /api/tire-suppliers/{id}` - Obtener proveedor por ID
- `PUT /api/tire-suppliers/{id}` - Actualizar proveedor
- `DELETE /api/tire-suppliers/{id}` - Desactivar proveedor

---

#### Componentes Frontend Necesarios

**Selectores Jerárquicos:**
- `OfficeSelector` - Selector de oficina (solo para admins nacionales)
- `WarehouseSelector` - Selector de almacén (filtrado por oficina del usuario)
- `LocationSelector` - Selector de ubicación (filtrado por almacén seleccionado)
- `SupplierSelector` - Selector de proveedor (filtrado por oficina del usuario)

**Páginas de Gestión:**
- `/admin/offices` - Gestión de oficinas
- `/admin/offices/:id/warehouses` - Gestión de almacenes
- `/admin/warehouses/:id/locations` - Gestión de ubicaciones
- `/admin/suppliers` - Gestión de proveedores

**Indicador de Contexto:**
- Mostrar en navbar/header la oficina actual del usuario
- Para admins nacionales: mostrar filtro de oficina activo

---

#### Migración desde Sistema Legacy

**Estrategia de Migración:**

1. **Fase 1: Crear estructura organizacional**
   - Crear oficinas basadas en `PARAMETROS_OFICSISTEMA`
   - Migrar `LOCALIZA` a `warehouse_locations` asociándolas a un warehouse por defecto
   - Migrar `PROVEEDORES_LLANTAS` a `tire_suppliers` asociándolos a oficina

2. **Fase 2: Migrar datos transaccionales**
   - Determinar oficina de origen de cada registro (basado en parámetros legacy)
   - Migrar `INVENTARIO` asociando a warehouse correspondiente
   - Migrar `LLANTAS`, `INTERMEDIO`, `RETIRADAS`, `HISTORIA`

3. **Fase 3: Actualizar usuarios**
   - Asignar `office_id` a cada usuario basado en su configuración legacy
   - Implementar RLS policies

**Script de Migración:**
```sql
-- 1. Crear oficina por defecto
INSERT INTO offices (code, name, city, is_active)
VALUES ('MAIN', 'Oficina Principal', 'Bogotá', true);

-- 2. Crear almacén por defecto
INSERT INTO warehouses (code, name, office_id, is_active)
SELECT 'PRIN', 'Almacén Principal', o.id, true
FROM offices o WHERE o.code = 'MAIN';

-- 3. Migrar ubicaciones
INSERT INTO warehouse_locations (code, name, warehouse_id, is_active)
SELECT
    l.cod_local,
    l.descri,
    (SELECT id FROM warehouses WHERE code = 'PRIN'),
    CASE WHEN l.estado = 'A' THEN true ELSE false END
FROM localiza l;

-- 4. Migrar proveedores
INSERT INTO tire_suppliers (code, name, tax_id, office_id, is_active)
SELECT
    p.codigopro,
    p.nombre,
    p.nit,
    (SELECT id FROM offices WHERE code = 'MAIN'),
    CASE WHEN p.estado = 'A' THEN true ELSE false END
FROM proveedores_llantas p;
```

---

#### Testing

**Tests Unitarios:**
- CRUD de cada entidad (Office, Warehouse, WarehouseLocation, TireSupplier)
- Validaciones de constraints únicos por oficina
- Verificación de soft delete

**Tests de Integración:**
- Flujo completo: crear oficina → almacén → ubicación
- Verificar que usuarios no puedan ver datos de otras oficinas
- Verificar que admins nacionales vean todas las oficinas

**Tests de Seguridad:**
- Verificar políticas RLS
- Intentar acceder a datos de otra oficina (debe fallar)
- Verificar permisos por rol

---

### RF-002: Control de Inventario de Llantas (Multi-Sede)
**Prioridad:** Alta
**Prerequisito:** RF-001-EXT (Estructura Organizacional)

**Descripción:**
El sistema debe controlar las llantas que están en inventario (bodega) sin montar. Cada llanta física pertenece a un almacén específico de una oficina, y los usuarios solo pueden ver y gestionar las llantas de su oficina.

**Contexto Multi-Sede:**
- ✅ Una llanta física en Bogotá es **diferente** a una en Medellín (inventarios separados)
- ✅ Los usuarios ven SOLO las llantas del almacén de su oficina (filtrado automático por RLS)
- ✅ Las fichas técnicas son globales (compartidas entre oficinas)
- ✅ Los proveedores son específicos por oficina
- ✅ Las ubicaciones pertenecen a almacenes específicos

**Operaciones:**
1. **Ingreso de llantas nuevas:**
   - Número de llanta (identificador único **por oficina**)
   - Grupo (tipo: 000=nueva, 001-009=reencauche)
   - Valor
   - Fecha de ingreso
   - **Almacén (warehouse_id)** - selector filtrado por oficina del usuario
   - **Proveedor (supplier_id)** - selector filtrado por oficina del usuario
   - **Ubicación (location_id)** - selector filtrado por almacén seleccionado
   - Número de factura
   - Ficha técnica asociada (catálogo global)
   - Notas adicionales

2. **Consulta de inventario:**
   - **Filtrado automático por oficina del usuario** (via RLS)
   - Por ficha técnica
   - Por almacén
   - Por ubicación dentro del almacén
   - Por proveedor
   - Por rango de fechas
   - Por estado (nueva/reencauchada)
   - **Admin Nacional:** puede filtrar por oficina específica o ver todas

3. **Salida de inventario:**
   - Al montar en vehículo → pasa a TIRE_ACTIVE
   - Registro automático en TIRE_HISTORY
   - Validación: el vehículo debe pertenecer a la misma oficina

**Reglas de Negocio:**
- Cada llanta tiene identificador único (tire_number, group) **dentro de su oficina**
- El mismo tire_number puede existir en diferentes oficinas (inventarios separados)
- GRUPO = '000' para llantas nuevas
- GRUPO > '000' para reencauches (incrementa con cada reencauche)
- No se pueden eliminar llantas con movimientos históricos
- **RN-MULTISEDE-001:** Usuario no puede ingresar llantas en almacenes de otras oficinas
- **RN-MULTISEDE-002:** Usuario no puede seleccionar proveedores de otras oficinas
- **RN-MULTISEDE-003:** La ubicación seleccionada debe pertenecer al almacén seleccionado
- **RN-MULTISEDE-004:** Al montar llanta en vehículo, ambos deben ser de la misma oficina

**Tablas:**
- `tire_inventory` (con warehouse_id)
- `tire_specifications` (FK - catálogo global)
- `tire_suppliers` (FK - filtrado por office_id)
- `warehouses` (FK - filtrado por office_id)
- `warehouse_locations` (FK - filtrado por warehouse_id)

**Campos Adicionales en tire_inventory:**
```sql
CREATE TABLE tire_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_number VARCHAR(20) NOT NULL,
    "group" CHAR(3) NOT NULL CHECK ("group" ~ '^[0-9]{3}$'),
    value DECIMAL(12,2) NOT NULL CHECK (value > 0),
    entry_date DATE NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    notes TEXT,

    -- Referencias multi-sede
    specification_id UUID NOT NULL REFERENCES tire_specifications(id),
    supplier_id UUID NOT NULL REFERENCES tire_suppliers(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),  -- CLAVE: define la oficina
    location_id UUID NOT NULL REFERENCES warehouse_locations(id),

    -- Información de reencauche
    tire_code VARCHAR(20),
    retread_value DECIMAL(12,2),
    protector_code VARCHAR(20),
    protector_value DECIMAL(12,2),

    -- Auditoría
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id),

    -- Constraints
    CONSTRAINT uk_tire_inventory_number_group_office
        UNIQUE(tire_number, "group", warehouse_id),
    CONSTRAINT fk_location_belongs_to_warehouse
        FOREIGN KEY (location_id)
        REFERENCES warehouse_locations(id)
        CHECK (
            location_id IN (
                SELECT id FROM warehouse_locations
                WHERE warehouse_id = tire_inventory.warehouse_id
            )
        )
);

-- Índices para performance
CREATE INDEX idx_tire_inventory_warehouse ON tire_inventory(warehouse_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tire_inventory_supplier ON tire_inventory(supplier_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tire_inventory_specification ON tire_inventory(specification_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tire_inventory_entry_date ON tire_inventory(entry_date) WHERE deleted_at IS NULL;

-- Row-Level Security Policy
ALTER TABLE tire_inventory ENABLE ROW LEVEL SECURITY;

CREATE POLICY tire_inventory_office_isolation ON tire_inventory
    FOR ALL
    TO authenticated_user
    USING (
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        warehouse_id IN (
            SELECT w.id FROM warehouses w
            WHERE w.office_id = get_user_office_id()
              AND w.deleted_at IS NULL
        )
    );
```

**Validaciones de Negocio:**
1. **Validación de Almacén:**
   - El warehouse_id debe existir, estar activo y pertenecer a la oficina del usuario
   - El usuario debe tener permisos para operar en ese almacén

2. **Validación de Ubicación:**
   - La location_id debe pertenecer al warehouse_id seleccionado
   - La ubicación debe estar activa

3. **Validación de Proveedor:**
   - El supplier_id debe existir, estar activo y pertenecer a la misma oficina que el almacén

4. **Validación de Ficha Técnica:**
   - El specification_id debe existir y estar activo (catálogo global)

5. **Validación de Número de Llanta:**
   - Único dentro de la combinación (tire_number, group, warehouse_id)
   - Permite mismo número en diferentes oficinas

**Casos de Uso:**

**UC-002-01: Ingresar Llanta Nueva a Inventario**
1. Usuario selecciona almacén (filtrado por su oficina)
2. Sistema muestra selector de ubicaciones del almacén seleccionado
3. Usuario ingresa datos de la llanta
4. Usuario selecciona proveedor (filtrado por su oficina)
5. Usuario selecciona ficha técnica (catálogo global)
6. Sistema valida que warehouse, location y supplier pertenezcan a la misma oficina
7. Sistema guarda registro en tire_inventory con warehouse_id
8. RLS automáticamente asocia a la oficina del usuario

**UC-002-02: Consultar Inventario por Almacén**
1. Usuario accede a módulo de inventario
2. Sistema aplica RLS: muestra SOLO almacenes de la oficina del usuario
3. Usuario filtra por almacén específico
4. Sistema muestra llantas de ese almacén
5. Usuario puede filtrar además por ubicación, proveedor, ficha técnica

**UC-002-03: Reportar Inventario Consolidado (Admin Nacional)**
1. Admin Nacional accede a reporte consolidado
2. Sistema detecta rol ROLE_ADMIN_NATIONAL
3. Sistema muestra selector de oficina (opcional)
4. Admin puede ver:
   - Inventario de todas las oficinas
   - Inventario de oficina específica
   - Resúmenes por oficina
5. Reporte incluye columna "Oficina" para distinguir origen

**UC-002-04: Montar Llanta desde Inventario a Vehículo**
1. Usuario selecciona vehículo (de su oficina)
2. Sistema muestra llantas disponibles en inventario de la misma oficina
3. Usuario selecciona llanta y posición
4. Sistema valida:
   - Llanta y vehículo pertenecen a la misma oficina
   - Posición no está ocupada
5. Sistema mueve llanta: tire_inventory → tire_active
6. Sistema registra en tire_history

**Endpoints REST:**
```
POST   /api/tire/inventory                    - Crear llanta en inventario
GET    /api/tire/inventory                    - Listar inventario (filtrado por RLS)
GET    /api/tire/inventory/{id}               - Obtener llanta por ID
PUT    /api/tire/inventory/{id}               - Actualizar llanta
DELETE /api/tire/inventory/{id}               - Eliminar llanta (soft delete)
GET    /api/tire/inventory/by-warehouse/{warehouseId}  - Filtrar por almacén
GET    /api/tire/inventory/by-location/{locationId}    - Filtrar por ubicación
GET    /api/tire/inventory/available          - Llantas disponibles para montar
POST   /api/tire/inventory/{id}/mount         - Montar llanta en vehículo
GET    /api/tire/inventory/report/consolidated - Reporte consolidado (admin nacional)
```

**Componentes Frontend:**
- `TireInventoryList` - Lista de inventario con filtros por almacén/ubicación
- `TireInventoryForm` - Formulario de ingreso con selectores jerárquicos
- `WarehouseSelector` - Selector de almacén (filtrado por oficina)
- `LocationSelector` - Selector de ubicación (filtrado por almacén)
- `SupplierSelector` - Selector de proveedor (filtrado por oficina)
- `TireInventoryReport` - Reporte con agrupación por oficina (admin nacional)

**Migración de Datos Legacy:**
```sql
-- Asociar inventario legacy a almacén por defecto de oficina principal
INSERT INTO tire_inventory (
    tire_number, "group", value, entry_date, invoice_number, notes,
    specification_id, supplier_id, warehouse_id, location_id,
    tire_code, retread_value, protector_code, protector_value,
    created_at, created_by
)
SELECT
    i.llanta,
    i.grupo,
    i.valor,
    i.fecha,
    i.factura,
    i.obs,
    ts.id,  -- specification_id migrado
    sup.id, -- supplier_id migrado
    (SELECT id FROM warehouses WHERE code = 'PRIN' AND office_id = (SELECT id FROM offices WHERE code = 'MAIN')),
    loc.id, -- location_id migrado
    i.neuma,
    i.valorrn,
    i.protec,
    i.valorp,
    CURRENT_TIMESTAMP,
    1  -- usuario de migración
FROM inventario i
LEFT JOIN tire_specifications ts ON i.ficha = ts.legacy_code
LEFT JOIN tire_suppliers sup ON i.proveedor = sup.legacy_code
LEFT JOIN warehouse_locations loc ON i.local = loc.legacy_code
WHERE i.llanta IS NOT NULL;
```

---

### RF-003: Montaje de Llantas en Vehículos
**Prioridad:** Alta

**Descripción:**  
El sistema debe permitir registrar la instalación de llantas en vehículos.

**Proceso:**
1. Selección del vehículo (PLACA)
2. Selección de llanta desde:
   - INVENTARIO (llanta nueva o reencauchada)
   - INTERMEDIO (llanta lista para recircular)
3. Especificación de:
   - Posición en el vehículo (1-N según configuración)
   - Kilometraje del vehículo al instalar
   - Fecha de instalación

**Validaciones:**
- Vehículo debe existir y estar activo
- Posición no debe estar ocupada
- Llanta debe estar disponible (en INVENTARIO o INTERMEDIO)
- Kilometraje >= kilometraje actual del vehículo
- Fecha >= fecha de última operación del vehículo

**Efectos:**
- Elimina llanta de INVENTARIO o INTERMEDIO
- Crea registro en LLANTAS (activas)
- Crea registro en HISTORIA
- Actualiza kilometraje del vehículo
- Inicia contador de kilómetros para la llanta

**Constraint Importante:**
- Un vehículo no puede tener dos llantas en la misma posición
- Índice único: `UK_VEHI_POS (VEHICULO, POSICION)`

---

### RF-004: Desmontaje de Llantas
**Prioridad:** Alta

**Descripción:**  
El sistema debe permitir registrar el retiro de llantas de vehículos.

**Proceso:**
1. Selección del vehículo
2. Selección de la llanta a desmontar
3. Especificación de:
   - Kilometraje del vehículo al desmontar
   - Fecha de desmontaje
   - Motivo de desmontaje (OBSERVA)

**Validaciones:**
- Llanta debe estar actualmente montada
- Kilometraje >= kilometraje de instalación
- Fecha >= fecha de instalación
- Motivo debe existir en catálogo

**Efectos:**
- Elimina llanta de LLANTAS (activas)
- Actualiza registro en HISTORIA con:
  - Fecha de remoción
  - Kilometraje de remoción
  - Motivo
- Mueve llanta a INTERMEDIO
- Libera la posición en el vehículo

**Motivos Comunes (OBSERVA):**
- Desgaste normal
- Daño en lateral
- Daño en banda
- Rotación preventiva
- Fin de vida útil
- Otros

---

### RF-005: Muestreo y Control de Desgaste
**Prioridad:** Alta

**Descripción:**  
El sistema debe permitir el registro periódico de mediciones de profundidad de banda para controlar el desgaste.

**Frecuencia Sugerida:**
- Cada 10,000-15,000 km
- Mensualmente si el kilometraje es bajo

**Datos a Registrar:**
- Llanta (identificación)
- Grupo
- Kilometraje del vehículo
- Fecha del muestreo
- Profundidad Interna (PI)
- Profundidad Central (PC)
- Profundidad Derecha (PD)
- Presión de inflado (PSI)

**Cálculos Automáticos:**
```
Profundidad Promedio = (PI + PC + PD) / 3
Desgaste = Profundidad_Inicial - Profundidad_Actual
Porcentaje_Desgaste = (Desgaste / Profundidad_Inicial) * 100
KMs_por_mm = KMs_Recorridos / Desgaste
Vida_Útil_Estimada = (Profundidad_Inicial * KMs_por_mm) - KMs_Actuales
```

**Alertas:**
- Profundidad < límite legal (ej: 1.6mm)
- Desgaste irregular (diferencia entre PI, PC, PD > umbral)
- Presión fuera de rango recomendado

**Tablas:**
- `MUESTREO` (último muestreo)
- `HISTOMUES` (histórico de todos los muestreos)

---

### RF-006: Gestión de Bajas de Llantas
**Prioridad:** Media

**Descripción:**  
El sistema debe permitir dar de baja llantas que han cumplido su vida útil.

**Criterios de Baja:**
- Profundidad mínima alcanzada
- Daño irreparable
- No apto para reencauche
- Antigüedad excesiva

**Proceso:**
1. Llanta debe estar en estado INTERMEDIO
2. Evaluación técnica
3. Registro de baja con:
   - Valor residual
   - Número de acta
   - Fecha de baja
   - Autor de la baja
   - Observación (motivo detallado)
   - Motivo codificado

**Efectos:**
- Elimina llanta de INTERMEDIO
- Crea registro en RETIRADAS
- Cierra el ciclo de vida de la llanta
- Mantiene todo el histórico en HISTORIA

**Reversión de Baja:**
- Solo permitido si no hay conflictos
- Regresa llanta a INTERMEDIO
- Permite nueva evaluación

---

### RF-007: Gestión de Reencauches
**Prioridad:** Media

**Descripción:**  
El sistema debe permitir registrar el proceso de reencauche de llantas.

**Proceso:**
1. Llanta en INTERMEDIO evaluada como apta
2. Envío a proveedor de reencauche
3. Regreso del reencauche:
   - Nuevo grupo (incrementa contador)
   - Nueva ficha técnica (puede cambiar)
   - Nuevo valor
   - Nuevo proveedor
   - Nueva factura
   - Profundidades iniciales restauradas

**Campos de Control:**
- `NEUMA`: Código del neumático aplicado
- `VALORRN`: Valor del reencauche
- `PROTEC`: Código del protector (si aplica)
- `VALORP`: Valor del protector

**Efectos:**
- Elimina de INTERMEDIO
- Crea nuevo registro en INVENTARIO con grupo incrementado
- Mantiene vínculo histórico (mismo LLANTA, diferente GRUPO)

---

### RF-008: Rotación de Llantas
**Prioridad:** Media

**Descripción:**  
El sistema debe facilitar la rotación de llantas entre posiciones del mismo vehículo.

**Propósito:**
- Desgaste uniforme
- Maximizar vida útil
- Cumplir programas de mantenimiento preventivo

**Proceso:**
1. Selección de vehículo
2. Visualización de configuración actual
3. Definición de nuevo esquema de rotación
4. Ejecución:
   - Desmonta llantas de posiciones actuales
   - Remonta en nuevas posiciones
   - Registra movimiento en HISTORIA

---

### RF-009: Consulta de Vida Útil
**Prioridad:** Media

**Descripción:**  
El sistema debe proporcionar información detallada sobre el estado y proyección de vida útil de cada llanta.

**Información Mostrada:**
- **Datos Actuales:**
  - Vehículo actual
  - Posición actual
  - Profundidades actuales (PI, PC, PD)
  - Kilometraje actual
  - Fecha último muestreo

- **Datos Históricos:**
  - Total kilómetros recorridos (suma de todos los montajes)
  - Número de reencauches
  - Vehículos anteriores

- **Proyecciones:**
  - Vida útil estimada (basada en desgaste)
  - Kilómetros esperados restantes
  - Días estimados restantes (basado en uso promedio)
  - Fecha estimada de reemplazo

**Tabla/Vista:**
- `VIDAK` (vista calculada de vida útil)

---

### RF-010: Control de Costos
**Prioridad:** Media

**Descripción:**  
El sistema debe permitir analizar costos asociados a las llantas.

**Métricas de Costo:**
1. **Costo por Kilómetro:**
   ```
   Costo_KM = (Valor_Llanta + Suma_Reencauches) / KMs_Totales
   ```

2. **Costo por Hora:**
   - Basado en uso promedio del vehículo

3. **Retorno de Inversión:**
   - KMs logrados vs KMs esperados (ficha técnica)

4. **Análisis por:**
   - Marca
   - Tipo
   - Referencia
   - Proveedor
   - Clase de vehículo
   - Posición en el vehículo

---

## 5. ENTIDADES PRINCIPALES

### 5.1 Llanta (LLANTAS, INVENTARIO, INTERMEDIO, RETIRADAS, HISTORIA)
**Identificación:**
- `LLANTA` (VARCHAR2(20)): Número único de llanta
- `GRUPO` (CHAR(3)): Contador de reencauches
  - '000': Llanta nueva
  - '001'-'999': Número de reencauche

**Atributos Comunes:**
- Valor
- Fecha
- Proveedor
- Factura
- Ficha técnica
- Información de reencauche (si aplica)
- Información de protector (si aplica)

**Estados:**
1. **INVENTARIO**: Sin montar, en bodega
2. **LLANTAS**: Montada en vehículo
3. **INTERMEDIO**: Desmontada, lista para recircular
4. **RETIRADAS**: Dada de baja
5. **HISTORIA**: Registro histórico

---

### 5.2 Vehículo (VEHICULOS_LLANTAS)
**Identificación:**
- `PLACA` (CHAR(6)): Placa única del vehículo

**Atributos:**
- Clase (tractocamión, sencillo, trailer, etc.)
- Marca
- Modelo
- Kilometraje inicial
- Kilometraje actual
- Estado (activo/inactivo)
- Operando (sí/no)

**Configuración de Llantas:**
- Cada clase de vehículo tiene un esquema de posiciones
- Ejemplo:
  - Tractocamión: 10 llantas (2 direccionales, 8 de tracción)
  - Trailer: 8 llantas
  - Camión sencillo: 6 llantas

---

### 5.3 Ficha Técnica (FICHATEC)
**Identificación:**
- `CODIGO` (NUMBER(5)): Código único

**Especificaciones:**
- Marca, Tipo, Referencia
- Dimensión
- Kilómetros esperados
- Rangos de kilometraje
- Reencauches esperados
- Pérdida esperada
- Profundidades iniciales (PI, PC, PD)
- Información comercial (proveedores, precio, etc.)
- Peso

---

### 5.4 Muestreo (MUESTREO, HISTOMUES)
**Identificación:**
- `LLANTA` + `GRUPO` + `KILOM`: Clave compuesta

**Mediciones:**
- PI: Profundidad Interna (mm)
- PC: Profundidad Central (mm)
- PD: Profundidad Derecha (mm)
- PRESION: Presión de inflado (PSI)
- FECHA: Fecha del muestreo

---

## 6. FLUJOS DE PROCESO

### 6.1 Proceso de Adquisición e Instalación

```
┌─────────────────────────────────────────────────────────────┐
│ 1. COMPRA DE LLANTAS NUEVAS                                 │
│    - Registro en INVENTARIO                                 │
│    - GRUPO = '000'                                          │
│    - Asignación de ubicación en bodega                      │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. NECESIDAD DE MONTAJE                                     │
│    - Vehículo requiere llanta en posición X                 │
│    - Selección de llanta desde INVENTARIO                   │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. MONTAJE EN VEHÍCULO                                      │
│    - Registro de kilometraje de instalación                 │
│    - Asignación de posición                                 │
│    - Movimiento: INVENTARIO → LLANTAS                       │
│    - Creación de registro en HISTORIA                       │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. MUESTREOS PERIÓDICOS                                     │
│    - Cada 10,000-15,000 km o mensualmente                   │
│    - Registro de profundidades (PI, PC, PD)                 │
│    - Control de presión                                     │
│    - Guardado en MUESTREO y HISTOMUES                       │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. EVALUACIÓN DE DESGASTE                                   │
│    - Análisis de tendencia de desgaste                      │
│    - Proyección de vida útil restante                       │
│    - Decisión:                                              │
│      a) Continuar en uso                                    │
│      b) Rotación de posición                                │
│      c) Desmontaje para evaluación                          │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. DESMONTAJE                                               │
│    - Registro de motivo de desmontaje                       │
│    - Kilometraje de remoción                                │
│    - Movimiento: LLANTAS → INTERMEDIO                       │
│    - Actualización de HISTORIA                              │
└────────────┬────────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. EVALUACIÓN EN INTERMEDIO                                 │
│    - Inspección técnica                                     │
│    - Decisión:                                              │
│      a) Apta para recircular → Volver a paso 2              │
│      b) Enviar a reencauche → Paso 8                        │
│      c) Dar de baja → Paso 9                                │
└────────────┬────────────────────────────────────────────────┘
             │
     ┌───────┴────────┐
     │                │
     ↓                ↓
┌─────────┐    ┌──────────┐
│ 8. REEN │    │ 9. BAJA  │
│  CAUCHE │    │          │
└────┬────┘    └────┬─────┘
     │              │
     ↓              ↓
┌─────────┐    ┌──────────┐
│INVENTAR │    │RETIRADAS │
│(GRUPO+1)│    │          │
└─────────┘    └──────────┘
```

---

### 6.2 Proceso de Muestreo Detallado

```
Inicio
  │
  ↓
Seleccionar Vehículo
  │
  ↓
Mostrar Llantas Activas del Vehículo
  │
  ↓
Para cada Llanta:
  ├─→ Obtener última profundidad (PI, PC, PD)
  ├─→ Mostrar histórico de muestreos
  ├─→ Ingresar nuevo muestreo:
  │     ├─ Kilometraje actual del vehículo
  │     ├─ Profundidad Interna (PI)
  │     ├─ Profundidad Central (PC)
  │     ├─ Profundidad Derecha (PD)
  │     ├─ Presión de inflado
  │     └─ Fecha del muestreo
  │
  ↓
Validaciones:
  ├─ KM >= KM_Instalación ✓
  ├─ Profundidades <= Profundidades_Iniciales ✓
  ├─ Fecha >= Fecha_Instalación ✓
  └─ Presión en rango permitido ✓
  │
  ↓
Cálculos Automáticos:
  ├─ Profundidad_Promedio = (PI + PC + PD) / 3
  ├─ Desgaste = Prof_Inicial - Prof_Actual
  ├─ KMs_desde_Instalación = KM_Actual - KM_Instalación
  └─ Actualizar KMS_RECORRIDO_LLANTAS
  │
  ↓
Guardar en:
  ├─ MUESTREO (registro actual)
  └─ HISTOMUES (histórico)
  │
  ↓
Generar Alertas (si aplica):
  ├─ Profundidad < Límite Legal
  ├─ Desgaste Irregular
  └─ Presión Fuera de Rango
  │
  ↓
Fin
```

---

## 7. REPORTES Y CONSULTAS

### 7.1 Reportes Operativos

#### R-001: Reporte de Llantas Activas
**Descripción:** Lista de todas las llantas actualmente instaladas en vehículos.

**Información:**
- Vehículo (placa)
- Número de llanta
- Grupo
- Posición
- Ficha técnica
- Dimensión
- Fecha de instalación
- Kilometraje de instalación
- Profundidad inicial
- Profundidad actual (último muestreo)
- Kilometraje total recorrido
- Estado de desgaste

**Vista SQL:**
```sql
SELECT 
    c.vehiculo,
    c.llanta,
    c.grupo,
    c.posicion,
    c.ficha,
    a.dimension,
    c.fechai,
    c.kinstala,
    TRUNC((a.pi + a.pc + a.pd) / 3, 2) AS prof_inicial,
    TRUNC((m.pi + m.pc + m.pd) / 3, 2) AS prof_actual,
    (m.kilom - c.kinstala) + NVL(h.kms_historicos, 0) AS total_kms
FROM llantas c
JOIN fichatec a ON c.ficha = a.codigo
LEFT JOIN (
    SELECT llanta, grupo, kilom, pi, pc, pd, fecha
    FROM muestreo
    WHERE (llanta, grupo, fecha) IN (
        SELECT llanta, grupo, MAX(fecha)
        FROM muestreo
        GROUP BY llanta, grupo
    )
) m ON c.llanta = m.llanta AND c.grupo = m.grupo
LEFT JOIN (
    SELECT llanta, 
           SUBSTR(grupo, 3, 1) AS tipo_llanta,
           SUM(kremueve - kinstala) AS kms_historicos
    FROM historia
    GROUP BY llanta, SUBSTR(grupo, 3, 1)
) h ON c.llanta = h.llanta 
   AND SUBSTR(c.grupo, 3, 1) = h.tipo_llanta;
```

**Filtros:**
- Por vehículo
- Por clase de vehículo
- Por marca de llanta
- Por tipo de llanta
- Por rango de profundidad
- Por rango de kilometraje

---

#### R-002: Reporte de Inventario de Llantas
**Descripción:** Llantas disponibles en bodega sin montar.

**Información:**
- Número de llanta
- Grupo (nueva/reencauche)
- Ficha técnica
- Marca, tipo, referencia
- Dimensión
- Valor
- Localización en bodega
- Fecha de ingreso
- Proveedor
- Antigüedad (días en inventario)

**Agrupaciones:**
- Por ficha técnica
- Por localización
- Por proveedor
- Por antigüedad

---

#### R-003: Histórico de Llanta Individual
**Descripción:** Trayectoria completa de una llanta específica.

**Información:**
Para cada montaje/desmontaje:
- Vehículo
- Posición
- Fecha de instalación
- Kilometraje de instalación
- Fecha de remoción
- Kilometraje de remoción
- Kilómetros recorridos en ese montaje
- Motivo de desmontaje
- Reencauches realizados
- Valor en ese momento

**Total Acumulado:**
- Kilómetros totales
- Número de montajes
- Número de reencauches
- Costo total invertido
- Costo por kilómetro

---

#### R-004: Programación de Muestreos
**Descripción:** Llantas que requieren muestreo próximamente.

**Criterios:**
- Llantas sin muestreo en los últimos X kilómetros
- Llantas sin muestreo en los últimos X días
- Próximas a alcanzar límite de profundidad

**Información:**
- Vehículo
- Llanta
- Último muestreo (fecha y KM)
- Kilómetros desde último muestreo
- Días desde último muestreo
- Profundidad actual
- Prioridad (Alta/Media/Baja)

---

### 7.2 Reportes de Gestión

#### R-005: Análisis de Consumo de Llantas
**Descripción:** Consumo mensual/anual de llantas.

**Vista SQL:**
```sql
-- Consumo por mes
SELECT 
    EXTRACT(YEAR FROM fecha) AS ano,
    EXTRACT(MONTH FROM fecha) AS mes,
    COUNT(*) AS total_consumo
FROM (
    -- Llantas nuevas consumidas
    SELECT fecha FROM historia WHERE grupo = '000'
    UNION ALL
    -- Llantas en uso
    SELECT fecha FROM llantas WHERE grupo = '000'
    UNION ALL
    -- Llantas en inventario
    SELECT fecha FROM inventario WHERE grupo = '000'
)
GROUP BY EXTRACT(YEAR FROM fecha), EXTRACT(MONTH FROM fecha)
ORDER BY ano, mes;
```

**Análisis:**
- Por tipo de llanta
- Por marca
- Por clase de vehículo
- Tendencias históricas
- Proyecciones

---

#### R-006: Rendimiento por Marca/Tipo/Referencia
**Descripción:** Comparativo de rendimiento entre diferentes fichas técnicas.

**Métricas:**
- Kilómetros promedio logrados vs esperados
- Costo por kilómetro
- Número de reencauches logrados
- Tasa de fallas prematuras
- Vida útil promedio en días

**Agrupaciones:**
- Por marca
- Por tipo
- Por referencia
- Por clase de vehículo
- Por posición

---

#### R-007: Costos por Vehículo
**Descripción:** Análisis de costos de llantas por vehículo.

**Información:**
- Vehículo (placa)
- Clase
- Número de llantas actuales
- Valor total invertido en llantas actuales
- Costo de llantas en el último año
- Proyección de reemplazo en próximos 6 meses
- Costo estimado de próximos reemplazos

---

#### R-008: Llantas Críticas
**Descripción:** Llantas que requieren atención inmediata.

**Criterios de Criticidad:**
1. Profundidad < límite legal (1.6 mm)
2. Desgaste irregular severo
3. Edad > límite recomendado
4. Sin muestreo en tiempo excesivo

**Información:**
- Vehículo
- Llanta
- Posición
- Tipo de criticidad
- Profundidad actual
- Recomendación (desmontar, rotar, monitorear)

---

#### R-009: Eficiencia de Proveedores
**Descripción:** Análisis de rendimiento de llantas por proveedor.

**Métricas:**
- Kilómetros promedio logrados
- Costo promedio por kilómetro
- Tasa de fallas
- Cumplimiento de especificaciones
- Calidad de reencauches

---

#### R-010: Proyección de Compras
**Descripción:** Estimación de necesidades futuras de llantas.

**Basado en:**
- Consumo histórico
- Llantas próximas a reemplazo
- Crecimiento de flota
- Estacionalidad

**Salida:**
- Cantidad estimada por mes
- Por ficha técnica
- Por tipo
- Presupuesto estimado

---

## 8. REGLAS DE NEGOCIO CRÍTICAS

### RN-001: Unicidad de Posiciones
- Un vehículo no puede tener dos llantas en la misma posición simultáneamente
- Implementado mediante constraint: `UK_VEHI_POS (VEHICULO, POSICION)`

### RN-002: Secuencia de Grupos
- GRUPO = '000' para llantas nuevas
- GRUPO incrementa en uno con cada reencauche: '001', '002', ...
- Máximo 999 reencauches (limitación técnica, no realista)

### RN-003: Trazabilidad Completa
- Toda llanta debe tener registro en HISTORIA desde su compra
- No se permite eliminar histórico
- Auditoría completa del ciclo de vida

### RN-004: Validación de Kilometrajes
- KM_Instalación >= KM_Actual_Vehículo
- KM_Remoción >= KM_Instalación
- KM_Muestreo >= KM_Instalación

### RN-005: Validación de Fechas
- Fecha_Instalación >= Fecha_Compra
- Fecha_Remoción >= Fecha_Instalación
- Fecha_Muestreo >= Fecha_Instalación

### RN-006: Profundidades
- 0 <= Profundidad_Actual <= Profundidad_Inicial
- Profundidad < Límite_Legal → Alerta obligatoria
- Desgaste_Irregular > Umbral → Alerta de inspección

### RN-007: Estados Mutuamente Excluyentes
- Una llanta solo puede estar en uno de estos estados:
  - INVENTARIO
  - LLANTAS (activa)
  - INTERMEDIO
  - RETIRADAS
- Excepción: HISTORIA mantiene todo el registro histórico

### RN-008: Integridad Referencial
- Ficha técnica no puede eliminarse si tiene llantas asociadas
- Vehículo no puede eliminarse si tiene llantas instaladas
- Proveedor no puede eliminarse si tiene movimientos

---

## 9. CONSIDERACIONES TÉCNICAS

### 9.1 Índices Críticos
Todos implementados según `Llantas_INDICES.sql`:
- `PK_LLANTAS_LLANGRU`: Clave primaria (LLANTA, GRUPO)
- `UK_VEHI_POS`: Unicidad de posiciones (VEHICULO, POSICION)
- `PK_MUESTREO_LLANGRU`: Clave primaria muestreos
- Índices en FKs para joins eficientes

### 9.2 Triggers Recomendados
1. **Actualización automática de kilometraje de vehículos**
2. **Registro de auditoría en tabla AUDITA**
3. **Validación de reglas de negocio complejas**
4. **Generación de alertas automáticas**

### 9.3 Vistas Importantes
- `V_LLANTAS_ACTIVAS`: Llantas con último muestreo y KMs totales
- `V_CONSUMOS_LLANTAS`: Consumo mensual agregado
- `V_LLANTAS_TOTAL`: Consolidado de todas las llantas

### 9.4 Seguridad
- Control de acceso por usuario (USUARIOS)
- Permisos por formulario (USUARIOS_FORMAS)
- Permisos por reporte (USUARIOS_LISTADOS)
- Auditoría de operaciones (AUDITA)
- Parámetros por oficina/sucursal

---

## 10. GLOSARIO

| Término | Definición |
|---------|------------|
| **Llanta** | Neumático o cubierta de rueda |
| **Grupo** | Contador de ciclo de vida (000=nueva, 001+=reencauches) |
| **Reencauche** | Proceso de renovación de banda de rodadura |
| **Muestreo** | Medición periódica de profundidad de banda |
| **Profundidad** | Medida de banda de rodadura en mm |
| **PI** | Profundidad Interna |
| **PC** | Profundidad Central |
| **PD** | Profundidad Derecha |
| **Ficha Técnica** | Especificaciones técnicas de un tipo de llanta |
| **Dimensión** | Medidas de la llanta (ej: 295/80R22.5) |
| **Intermedio** | Estado de llanta desmontada en evaluación |
| **Baja** | Retiro definitivo de llanta por fin de vida útil |

---

## 11. ANEXOS

### Anexo A: Esquema de Posiciones de Llantas

#### Tractocamión (10 llantas):
```
     [1]  [2]      ← Direccionales
        │  │
   ┌────┴──┴────┐
   │   CABINA   │
   └────────────┘
     [3][4][5][6]  ← Tracción eje 1
     [7][8][9][10] ← Tracción eje 2
```

#### Trailer (8 llantas):
```
   ┌─────────────┐
   │   CARGA     │
   └─────────────┘
     [1][2][3][4]  ← Eje 1
     [5][6][7][8]  ← Eje 2
```

### Anexo B: Tabla de Límites Legales
| País | Profundidad Mínima Legal | Presión Recomendada |
|------|--------------------------|---------------------|
| Colombia | 1.6 mm | 100-120 PSI |
| México | 1.6 mm | 100-120 PSI |
| USA | 2/32 inch (1.6 mm) | 100-120 PSI |

---

## 12. CONTROL DE CAMBIOS

| Versión | Fecha | Autor | Descripción |
|---------|-------|-------|-------------|
| 1.0 | 2026-01-20 | Análisis Técnico | Versión inicial basada en documentación existente |

---

**FIN DEL DOCUMENTO**
