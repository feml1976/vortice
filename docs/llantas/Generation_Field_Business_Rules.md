# Reglas de Negocio del Campo `generation`
## Sistema de Gestión de Llantas - Proyecto Vórtice

**Versión:** 1.0
**Fecha:** 22 de Enero de 2026
**Autor:** Equipo de Modernización Vórtice
**Estado:** APROBADO

---

## TABLA DE CONTENIDOS

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Concepto y Propósito](#2-concepto-y-propósito)
3. [Formato y Estructura](#3-formato-y-estructura)
4. [Reglas de Negocio Completas](#4-reglas-de-negocio-completas)
5. [Ciclo de Vida Completo](#5-ciclo-de-vida-completo)
6. [Casos de Uso y Ejemplos](#6-casos-de-uso-y-ejemplos)
7. [Implementación Técnica](#7-implementación-técnica)
8. [Validaciones y Constraints](#8-validaciones-y-constraints)
9. [Preguntas Frecuentes (FAQ)](#9-preguntas-frecuentes-faq)
10. [Glosario](#10-glosario)

---

## 1. RESUMEN EJECUTIVO

El campo `generation` es un **código mutable de 3 dígitos** que rastrea el ciclo de vida completo de una llanta física a través de dos contadores independientes:

- **VV** (posiciones 1-2): Cantidad de vehículos en los que ha estado montada (00-99)
- **R** (posición 3): Cantidad de reencauches recibidos (0-9)

### Características Clave:
- ✅ **Formato:** `[VV][R]` - Ejemplo: `"032"` = 3 vehículos, 2 reencauches
- ✅ **Mutabilidad:** Se actualiza mediante `UPDATE` en la tabla `tires`
- ✅ **Trazabilidad:** Se captura como snapshot inmutable en `history_records.generation_at_event`
- ✅ **Unicidad:** Una llanta física = UN registro en tabla `tires`

---

## 2. CONCEPTO Y PROPÓSITO

### 2.1 ¿Qué es `generation`?

El campo `generation` es un **indicador compuesto** que codifica dos métricas fundamentales del ciclo de vida de una llanta:

1. **Movilidad de la llanta:** Cuántos vehículos diferentes ha servido
2. **Vida útil extendida:** Cuántas veces ha sido reencauchada

### 2.2 ¿Por qué existe este campo?

#### **Problema de Negocio:**
La empresa de transporte necesita:
- Rastrear el rendimiento de llantas a través de múltiples vehículos
- Conocer cuántas vidas útiles ha tenido una llanta (original + reencauches)
- Optimizar costos identificando llantas con mejor ROI
- Proyectar necesidades de compra basándose en patrones históricos

#### **Solución Técnica:**
Un código de 3 dígitos que:
- Es compacto y fácil de interpretar visualmente
- Se puede descomponer en sus partes mediante funciones SQL
- Permite consultas analíticas eficientes
- Mantiene compatibilidad con el sistema legacy (Oracle Forms 6i - campo `GRUPO`)

### 2.3 Diferencia con Sistema Legacy

| Aspecto | Sistema Legacy (Oracle) | Sistema Moderno (PostgreSQL) |
|---------|------------------------|------------------------------|
| Nombre de campo | `GRUPO` | `generation` |
| Formato | CHAR(3) | CHAR(3) |
| Mutabilidad | ✅ Mutable | ✅ Mutable |
| Tabla principal | `LLANTAS` (múltiples registros) | `tires` (UN solo registro) |
| Histórico | `HISTORIA.GRUPO` (snapshot) | `history_records.generation_at_event` (snapshot) |
| Documentación | ❌ No documentado | ✅ Completamente documentado |

---

## 3. FORMATO Y ESTRUCTURA

### 3.1 Anatomía del Código

```
┌─────────────────────────────────────────┐
│          generation = "032"             │
│                                         │
│   ┌──┬──┬──┐                           │
│   │0 │3 │2 │                           │
│   └┬─┴┬─┴┬─┘                           │
│    │  │  └─── R: Retread Count (0-9)   │
│    └──┴────── VV: Vehicle Count (00-99)│
└─────────────────────────────────────────┘
```

### 3.2 Componentes

#### **Contador de Vehículos (VV) - Posiciones 1 y 2:**
- Rango: `00` a `99`
- Representa la cantidad de vehículos diferentes en los que ha estado montada la llanta
- Ejemplo: `03` significa que ha estado en 3 vehículos

#### **Contador de Reencauches (R) - Posición 3:**
- Rango: `0` a `9`
- Representa la cantidad de reencauches que ha recibido la llanta
- Ejemplo: `2` significa que ha sido reencauchada 2 veces (vida útil 3: original + 2 reencauches)

### 3.3 Tabla de Ejemplos

| generation | Vehicle Count | Retread Count | Significado en Español |
|-----------|---------------|---------------|------------------------|
| `000` | 0 | 0 | Llanta nueva en inventario, nunca montada |
| `010` | 1 | 0 | Llanta nueva montada en su primer vehículo |
| `020` | 2 | 0 | Llanta nueva que ha estado en 2 vehículos |
| `030` | 3 | 0 | Llanta nueva que ha estado en 3 vehículos |
| `001` | 0 | 1 | Llanta con 1 reencauche en inventario (post-reencauche) |
| `011` | 1 | 1 | Llanta con 1 reencauche montada en su primer vehículo post-reencauche |
| `021` | 2 | 1 | Llanta con 1 reencauche que ha estado en 2 vehículos desde el reencauche |
| `032` | 3 | 2 | Llanta con 2 reencauches que ha estado en 3 vehículos desde el último reencauche |
| `992` | 99 | 2 | Llanta con 2 reencauches en su vehículo número 99 (límite máximo) |
| `009` | 0 | 9 | Llanta con 9 reencauches en inventario (límite máximo de reencauches) |

---

## 4. REGLAS DE NEGOCIO COMPLETAS

### 4.1 Regla RN-001: Compra Inicial
**Evento:** Registro de compra de llanta nueva

```
ANTES:  (llanta no existe)
EVENTO: Compra de llanta nueva LL-001
DESPUÉS: generation = '000'
ESTADO: INVENTARIO
```

**Invariante:** Toda llanta nueva comienza con `generation = '000'`

---

### 4.2 Regla RN-002: Montaje en Vehículo Nuevo
**Evento:** Montaje de llanta en un vehículo diferente al anterior

```
PRECONDICIÓN: Llanta en estado INVENTARIO o INTERMEDIO
OPERACIÓN:    Incrementar contador de vehículos (VV + 1)
              Mantener contador de reencauches (R sin cambio)

ANTES:  generation = '020', estado = INVENTARIO
EVENTO: Montar en VH-300 (nuevo vehículo)
DESPUÉS: generation = '030', estado = ACTIVA
```

**Fórmula:** `new_generation = LPAD((VV + 1)::TEXT, 2, '0') || R::TEXT`

**Ejemplo SQL:**
```sql
-- Antes: '020'
UPDATE tires
SET generation = tire_management.increment_vehicle_count(generation),
    tire_state = 'ACTIVE'
WHERE tire_number = 'LL-001';
-- Después: '030'
```

---

### 4.3 Regla RN-003: Rotación Dentro del Mismo Vehículo
**Evento:** Cambio de posición de la llanta dentro del mismo vehículo

```
PRECONDICIÓN: Llanta montada en vehículo VH-XXX
OPERACIÓN:    Cambiar posición (ej: posición 1 → posición 3)
              NO modificar generation

ANTES:  generation = '030', vehículo = VH-300, posición = 1
EVENTO: Rotar a posición 3 en VH-300
DESPUÉS: generation = '030', vehículo = VH-300, posición = 3
```

**Invariante:** `generation` **NO cambia** durante rotaciones en el mismo vehículo

---

### 4.4 Regla RN-004: Desmontaje
**Evento:** Desmontaje de llanta del vehículo

```
PRECONDICIÓN: Llanta en estado ACTIVA
OPERACIÓN:    Cambiar estado (ACTIVA → INVENTARIO o INTERMEDIO)
              NO modificar generation

ANTES:  generation = '030', estado = ACTIVA, vehículo = VH-300
EVENTO: Desmontar de VH-300
DESPUÉS: generation = '030', estado = INTERMEDIO, vehículo = NULL
```

**Invariante:** `generation` **NO cambia** al desmontar

---

### 4.5 Regla RN-005: Reencauche
**Evento:** Llanta enviada a reencauche y devuelta

```
PRECONDICIÓN: Llanta en estado INTERMEDIO con evaluación "REQUIERE REENCAUCHE"
OPERACIÓN:    Resetear contador de vehículos a 00
              Incrementar contador de reencauches (R + 1)
              Cambiar estado a INVENTARIO

ANTES:  generation = '030', estado = INTERMEDIO
EVENTO: Proceso de reencauche completado
DESPUÉS: generation = '001', estado = INVENTARIO
```

**Fórmula:** `new_generation = '00' || (R + 1)::TEXT`

**Ejemplo SQL:**
```sql
-- Antes: '030'
UPDATE tires
SET generation = tire_management.increment_retread_count(generation),
    tire_state = 'INVENTORY'
WHERE tire_number = 'LL-001';
-- Después: '001'
```

**Lógica de Negocio:**
> Cuando una llanta es reencauchada, técnicamente "renace" con una nueva banda de rodadura. Por eso se resetea el contador de vehículos, pero se registra que es un reencauche. Es como decir: "Esta llanta está en su segunda vida útil, pero aún no ha sido montada en ningún vehículo en esta nueva vida."

---

### 4.6 Regla RN-006: Montaje Post-Reencauche
**Evento:** Montaje de llanta reencauchada en un vehículo

```
PRECONDICIÓN: Llanta reencauchada en estado INVENTARIO (generation = '00R')
OPERACIÓN:    Incrementar contador de vehículos (VV + 1)
              Mantener contador de reencauches (R sin cambio)

ANTES:  generation = '001', estado = INVENTARIO
EVENTO: Montar en VH-400 (primer vehículo post-reencauche)
DESPUÉS: generation = '011', estado = ACTIVA
```

**Nota:** Esta es exactamente la misma regla que RN-002 (montaje normal)

---

### 4.7 Regla RN-007: Baja de Llanta
**Evento:** Llanta dada de baja definitivamente

```
PRECONDICIÓN: Llanta en cualquier estado excepto RETIRADA
OPERACIÓN:    Cambiar estado a RETIRADA
              NO modificar generation (se preserva el valor final)

ANTES:  generation = '032', estado = INTERMEDIO
EVENTO: Dar de baja con motivo "DESGASTE IRREPARABLE"
DESPUÉS: generation = '032', estado = RETIRADA
```

**Invariante:** `generation` **NO cambia** al dar de baja (se preserva historial completo)

---

### 4.8 Regla RN-008: Reversión de Baja
**Evento:** Reversión de baja (caso excepcional)

```
PRECONDICIÓN: Llanta en estado RETIRADA
              Baja creada hace menos de 30 días (política de negocio)
OPERACIÓN:    Restaurar estado anterior (RETIRADA → INVENTARIO)
              NO modificar generation

ANTES:  generation = '032', estado = RETIRADA
EVENTO: Reversión de baja por error administrativo
DESPUÉS: generation = '032', estado = INVENTARIO
```

**Invariante:** `generation` **NO cambia** durante reversión

---

## 5. CICLO DE VIDA COMPLETO

### 5.1 Ejemplo Completo: Llanta LL-001

```
┌──────┬─────────────────────────────┬────────────┬──────────┬──────────┬─────────────┐
│ Paso │ Evento                      │ generation │ Estado   │ Vehículo │ Posición    │
├──────┼─────────────────────────────┼────────────┼──────────┼──────────┼─────────────┤
│  1   │ Compra inicial              │    000     │ INVENT.  │   NULL   │    NULL     │
│  2   │ Montar en VH-100            │    010     │ ACTIVA   │ VH-100   │      1      │
│  3   │ Rotar a posición 3 (VH-100) │    010     │ ACTIVA   │ VH-100   │      3      │
│  4   │ Desmontar de VH-100         │    010     │ INVENT.  │   NULL   │    NULL     │
│  5   │ Montar en VH-200            │    020     │ ACTIVA   │ VH-200   │      2      │
│  6   │ Desmontar de VH-200         │    020     │ INTERM.  │   NULL   │    NULL     │
│  7   │ Montar en VH-300            │    030     │ ACTIVA   │ VH-300   │      1      │
│  8   │ Desmontar de VH-300         │    030     │ INTERM.  │   NULL   │    NULL     │
│  9   │ Evaluar → Reencauchar       │    030     │ INTERM.  │   NULL   │    NULL     │
│ 10   │ Reencauche completado       │    001     │ INVENT.  │   NULL   │    NULL     │ ← RESET VV
│ 11   │ Montar en VH-400            │    011     │ ACTIVA   │ VH-400   │      4      │
│ 12   │ Desmontar de VH-400         │    011     │ INTERM.  │   NULL   │    NULL     │
│ 13   │ Segundo reencauche          │    002     │ INVENT.  │   NULL   │    NULL     │ ← RESET VV
│ 14   │ Montar en VH-500            │    012     │ ACTIVA   │ VH-500   │      1      │
│ 15   │ Montar en VH-600            │    022     │ ACTIVA   │ VH-600   │      2      │
│ 16   │ Montar en VH-700            │    032     │ ACTIVA   │ VH-700   │      3      │
│ 17   │ Desmontar de VH-700         │    032     │ INTERM.  │   NULL   │    NULL     │
│ 18   │ Evaluar → Dar de baja       │    032     │ RETIRADA │   NULL   │    NULL     │
└──────┴─────────────────────────────┴────────────┴──────────┴──────────┴─────────────┘
```

### 5.2 Análisis del Ciclo de Vida

**Resultado Final:** `generation = '032'`

**Lectura del código:**
- **03** vehículos: VH-500, VH-600, VH-700 (desde el último reencauche)
- **2** reencauches: Vida útil total = 3 vidas (original + 2 reencauches)

**Vehículos totales en toda su vida:**
- Vida original (000-030): VH-100, VH-200, VH-300 = 3 vehículos
- Primera vida reencauchada (001-011): VH-400 = 1 vehículo
- Segunda vida reencauchada (002-032): VH-500, VH-600, VH-700 = 3 vehículos
- **Total histórico:** 7 vehículos diferentes

---

## 6. CASOS DE USO Y EJEMPLOS

### 6.1 Caso de Uso 1: Detección de Llantas de Alto Rendimiento

**Objetivo:** Identificar llantas que han servido en muchos vehículos con pocos reencauches

**Query:**
```sql
SELECT
    tire_number,
    generation,
    tire_management.get_vehicle_count(generation) AS vehicle_count,
    tire_management.get_retread_count(generation) AS retread_count,
    tire_state
FROM tire_management.tires
WHERE tire_management.get_vehicle_count(generation) >= 5
  AND tire_management.get_retread_count(generation) <= 1
  AND deleted_at IS NULL
ORDER BY tire_management.get_vehicle_count(generation) DESC;
```

**Resultado esperado:**
```
tire_number │ generation │ vehicle_count │ retread_count │ tire_state
────────────┼────────────┼───────────────┼───────────────┼───────────
LL-042      │ 070        │      7        │      0        │ ACTIVE
LL-089      │ 051        │      5        │      1        │ INVENTORY
```

**Interpretación:** Llantas con alto ROI (muchos vehículos servidos con mínimos reencauches)

---

### 6.2 Caso de Uso 2: Auditoría de Reencauches

**Objetivo:** Verificar que ninguna llanta supere el límite de 3 reencauches permitidos

**Query:**
```sql
SELECT
    tire_number,
    generation,
    tire_management.get_retread_count(generation) AS retread_count,
    tire_state
FROM tire_management.tires
WHERE tire_management.get_retread_count(generation) > 3
  AND deleted_at IS NULL;
```

**Resultado esperado:**
```
tire_number │ generation │ retread_count │ tire_state
────────────┼────────────┼───────────────┼────────────
LL-123      │ 004        │      4        │ INVENTORY  ← ALERTA: Excede política
```

**Acción:** Marcar para baja inmediata

---

### 6.3 Caso de Uso 3: Proyección de Reencauches Necesarios

**Objetivo:** Estimar cuántas llantas necesitarán reencauche en los próximos 3 meses

**Query:**
```sql
SELECT
    COUNT(*) AS tires_to_retread,
    ts.dimension,
    ts.brand_name
FROM tire_management.tires t
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id
WHERE tire_state = 'INTERMEDIATE'
  AND tire_management.get_retread_count(generation) < 3  -- Aún puede reencaucharse
  AND deleted_at IS NULL
GROUP BY ts.dimension, ts.brand_name
ORDER BY tires_to_retread DESC;
```

---

### 6.4 Caso de Uso 4: Reconstrucción del Historial Completo

**Objetivo:** Ver todos los eventos de una llanta específica con snapshots de generation

**Query:**
```sql
SELECT
    created_at,
    generation_at_event,
    tire_management.get_vehicle_count(generation_at_event) AS veh_count,
    tire_management.get_retread_count(generation_at_event) AS ret_count,
    vehicle_id,
    position,
    installation_date,
    removal_date,
    removal_reason_id
FROM tire_management.history_records
WHERE tire_id = (SELECT id FROM tire_management.tires WHERE tire_number = 'LL-001')
ORDER BY created_at ASC;
```

**Resultado:**
```
created_at          │ generation_at_event │ veh_count │ ret_count │ vehicle_id │ position
────────────────────┼─────────────────────┼───────────┼───────────┼────────────┼──────────
2024-01-10 08:00:00 │ 000                 │     0     │     0     │    NULL    │   NULL
2024-01-15 10:30:00 │ 010                 │     1     │     0     │  VH-100-id │     1
2024-03-20 14:00:00 │ 020                 │     2     │     0     │  VH-200-id │     2
2024-06-01 09:15:00 │ 030                 │     3     │     0     │  VH-300-id │     1
2024-08-10 11:00:00 │ 001                 │     0     │     1     │    NULL    │   NULL  ← REENCAUCHE
2024-08-20 13:45:00 │ 011                 │     1     │     1     │  VH-400-id │     4
```

---

## 7. IMPLEMENTACIÓN TÉCNICA

### 7.1 Esquema de Base de Datos

#### Tabla `tires` (Aggregate Root)
```sql
CREATE TABLE tire_management.tires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tire_number VARCHAR(20) NOT NULL UNIQUE,  -- Natural key
    generation CHAR(3) NOT NULL,              -- MUTABLE
    tire_state tire_state_enum NOT NULL,
    -- ... otros campos

    CONSTRAINT ck_generation_format CHECK (generation ~ '^\d{3}$')
);

COMMENT ON COLUMN tire_management.tires.generation IS
'Código de 3 dígitos [VV][R]: VV=vehículos (00-99), R=reencauches (0-9).
Ejemplo: "032" = 3 vehículos, 2 reencauches.
Campo MUTABLE: se actualiza al montar (+1 veh) o reencauchar (reset a 00, +1 reenc).';
```

#### Tabla `history_records` (Event Sourcing)
```sql
CREATE TABLE tire_management.history_records (
    id UUID DEFAULT gen_random_uuid(),
    tire_id UUID NOT NULL REFERENCES tire_management.tires(id),
    generation_at_event CHAR(3) NOT NULL,  -- IMMUTABLE snapshot
    vehicle_id UUID,
    position SMALLINT,
    installation_date DATE,
    removal_date DATE,
    removal_reason_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- ... otros campos
) PARTITION BY RANGE (created_at);

COMMENT ON COLUMN tire_management.history_records.generation_at_event IS
'Snapshot del valor de generation en el momento del evento.
Ejemplo: Al montar post-reencauche captura "011".
Permite reconstruir historial completo de la llanta.';
```

### 7.2 Funciones Helper SQL

#### 7.2.1 Extraer Contador de Vehículos
```sql
CREATE OR REPLACE FUNCTION tire_management.get_vehicle_count(generation_value CHAR(3))
RETURNS INTEGER AS $$
BEGIN
    IF generation_value IS NULL OR LENGTH(generation_value) != 3 THEN
        RAISE EXCEPTION 'Invalid generation format. Must be 3 digits (e.g., "032")';
    END IF;
    RETURN CAST(SUBSTRING(generation_value, 1, 2) AS INTEGER);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Ejemplo de uso:
-- SELECT tire_management.get_vehicle_count('032');  -- Retorna: 3
```

#### 7.2.2 Extraer Contador de Reencauches
```sql
CREATE OR REPLACE FUNCTION tire_management.get_retread_count(generation_value CHAR(3))
RETURNS INTEGER AS $$
BEGIN
    IF generation_value IS NULL OR LENGTH(generation_value) != 3 THEN
        RAISE EXCEPTION 'Invalid generation format. Must be 3 digits (e.g., "032")';
    END IF;
    RETURN CAST(SUBSTRING(generation_value, 3, 1) AS INTEGER);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Ejemplo de uso:
-- SELECT tire_management.get_retread_count('032');  -- Retorna: 2
```

#### 7.2.3 Incrementar Contador de Vehículos
```sql
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

-- Ejemplo de uso:
-- SELECT tire_management.increment_vehicle_count('020');  -- Retorna: '030'
```

#### 7.2.4 Incrementar Contador de Reencauches
```sql
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

-- Ejemplo de uso:
-- SELECT tire_management.increment_retread_count('030');  -- Retorna: '001'
```

#### 7.2.5 Construir Generation desde Contadores
```sql
CREATE OR REPLACE FUNCTION tire_management.build_generation(
    vehicle_count INTEGER,
    retread_count INTEGER
)
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

-- Ejemplo de uso:
-- SELECT tire_management.build_generation(3, 2);  -- Retorna: '032'
```

### 7.3 Vista con Contadores Descompuestos

```sql
CREATE OR REPLACE VIEW tire_management.v_tires_with_counters AS
SELECT
    t.id,
    t.tire_number,
    t.generation,
    tire_management.get_vehicle_count(t.generation) AS vehicle_count,
    tire_management.get_retread_count(t.generation) AS retread_count,
    t.tire_state,
    t.purchase_value,
    t.purchase_date,
    ts.dimension,
    ts.brand_name,
    ts.expected_mileage,
    ts.expected_retreads
FROM tire_management.tires t
JOIN tire_management.technical_specifications ts ON t.technical_specification_id = ts.id
WHERE t.deleted_at IS NULL;

-- Ejemplo de uso:
-- SELECT * FROM tire_management.v_tires_with_counters WHERE retread_count > 2;
```

### 7.4 Implementación en Java (Spring Boot)

#### 7.4.1 Entity JPA
```java
@Entity
@Table(name = "tires", schema = "tire_management")
public class Tire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "tire_number", nullable = false, unique = true, length = 20)
    private String tireNumber;

    @Column(name = "generation", nullable = false, length = 3)
    @Pattern(regexp = "^\\d{3}$", message = "Generation must be 3 digits")
    private String generation;

    @Enumerated(EnumType.STRING)
    @Column(name = "tire_state", nullable = false)
    private TireState tireState;

    // Business methods

    public void mountOnNewVehicle() {
        // Incrementar VV
        int vehicleCount = Integer.parseInt(generation.substring(0, 2));
        int retreadCount = Integer.parseInt(generation.substring(2, 3));

        if (vehicleCount >= 99) {
            throw new BusinessException("Maximum vehicle count reached");
        }

        this.generation = String.format("%02d%d", vehicleCount + 1, retreadCount);
        this.tireState = TireState.ACTIVE;
    }

    public void retread() {
        // Reset VV a 00, incrementar R
        int retreadCount = Integer.parseInt(generation.substring(2, 3));

        if (retreadCount >= 9) {
            throw new BusinessException("Maximum retread count reached");
        }

        this.generation = String.format("00%d", retreadCount + 1);
        this.tireState = TireState.INVENTORY;
    }

    public int getVehicleCount() {
        return Integer.parseInt(generation.substring(0, 2));
    }

    public int getRetreadCount() {
        return Integer.parseInt(generation.substring(2, 3));
    }

    public boolean canBeRetreaded() {
        return getRetreadCount() < 9 && tireState == TireState.INTERMEDIATE;
    }
}
```

#### 7.4.2 Repository
```java
@Repository
public interface TireRepository extends JpaRepository<Tire, UUID> {

    Optional<Tire> findByTireNumber(String tireNumber);

    @Query("""
        SELECT t FROM Tire t
        WHERE CAST(SUBSTRING(t.generation, 1, 2) AS INTEGER) >= :minVehicles
        AND CAST(SUBSTRING(t.generation, 3, 1) AS INTEGER) <= :maxRetreads
        AND t.deletedAt IS NULL
        """)
    List<Tire> findHighPerformanceTires(
        @Param("minVehicles") int minVehicles,
        @Param("maxRetreads") int maxRetreads
    );
}
```

#### 7.4.3 Service Layer
```java
@Service
@Transactional
public class TireLifecycleService {

    private final TireRepository tireRepository;
    private final TireHistoryRepository historyRepository;

    public void mountTireOnVehicle(String tireNumber, UUID vehicleId, int position) {
        Tire tire = tireRepository.findByTireNumber(tireNumber)
            .orElseThrow(() -> new TireNotFoundException(tireNumber));

        // Validaciones
        if (tire.getTireState() != TireState.INVENTORY &&
            tire.getTireState() != TireState.INTERMEDIATE) {
            throw new BusinessException("Tire must be in INVENTORY or INTERMEDIATE state");
        }

        // Actualizar generation (incrementar VV)
        tire.mountOnNewVehicle();

        // Crear registro histórico con snapshot
        TireHistory history = new TireHistory();
        history.setTireId(tire.getId());
        history.setGenerationAtEvent(tire.getGeneration());  // Snapshot inmutable
        history.setVehicleId(vehicleId);
        history.setPosition(position);
        history.setInstallationDate(LocalDate.now());

        historyRepository.save(history);
        tireRepository.save(tire);
    }

    public void retreadTire(String tireNumber) {
        Tire tire = tireRepository.findByTireNumber(tireNumber)
            .orElseThrow(() -> new TireNotFoundException(tireNumber));

        if (!tire.canBeRetreaded()) {
            throw new BusinessException("Tire cannot be retreaded");
        }

        // Actualizar generation (reset VV, incrementar R)
        String oldGeneration = tire.getGeneration();
        tire.retread();

        // Registro histórico
        TireHistory history = new TireHistory();
        history.setTireId(tire.getId());
        history.setGenerationAtEvent(tire.getGeneration());  // '001', '002', etc.

        historyRepository.save(history);
        tireRepository.save(tire);
    }
}
```

---

## 8. VALIDACIONES Y CONSTRAINTS

### 8.1 Constraints de Base de Datos

```sql
-- Validación de formato (3 dígitos numéricos)
ALTER TABLE tire_management.tires
ADD CONSTRAINT ck_generation_format
CHECK (generation ~ '^\d{3}$');

-- Validación de rango (VV entre 00-99, R entre 0-9)
ALTER TABLE tire_management.tires
ADD CONSTRAINT ck_generation_range
CHECK (
    CAST(SUBSTRING(generation, 1, 2) AS INTEGER) BETWEEN 0 AND 99
    AND CAST(SUBSTRING(generation, 3, 1) AS INTEGER) BETWEEN 0 AND 9
);

-- Constraint específico: Llanta nueva debe comenzar en '000'
CREATE OR REPLACE FUNCTION tire_management.validate_new_tire_generation()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.purchase_date = CURRENT_DATE
       AND NEW.generation != '000'
       AND OLD.id IS NULL THEN  -- INSERT
        RAISE EXCEPTION 'New tire must have generation = 000';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_new_tire_generation
BEFORE INSERT ON tire_management.tires
FOR EACH ROW
EXECUTE FUNCTION tire_management.validate_new_tire_generation();
```

### 8.2 Validaciones de Negocio (Application Layer)

```java
public class GenerationValidator {

    private static final Pattern GENERATION_PATTERN = Pattern.compile("^\\d{3}$");

    public static void validateFormat(String generation) {
        if (generation == null || !GENERATION_PATTERN.matcher(generation).matches()) {
            throw new ValidationException("Generation must be 3 digits");
        }
    }

    public static void validateRange(String generation) {
        int vehicleCount = Integer.parseInt(generation.substring(0, 2));
        int retreadCount = Integer.parseInt(generation.substring(2, 3));

        if (vehicleCount < 0 || vehicleCount > 99) {
            throw new ValidationException("Vehicle count must be between 0 and 99");
        }

        if (retreadCount < 0 || retreadCount > 9) {
            throw new ValidationException("Retread count must be between 0 and 9");
        }
    }

    public static void validateBusinessRules(Tire tire, String newGeneration) {
        String currentGen = tire.getGeneration();
        int currentVV = Integer.parseInt(currentGen.substring(0, 2));
        int currentR = Integer.parseInt(currentGen.substring(2, 3));
        int newVV = Integer.parseInt(newGeneration.substring(0, 2));
        int newR = Integer.parseInt(newGeneration.substring(2, 3));

        // Regla: VV solo puede incrementar en 1 o resetear a 00
        if (newR == currentR && newVV != currentVV + 1) {
            throw new BusinessException("Vehicle count can only increment by 1");
        }

        // Regla: Al reencauchar, VV debe ser 00 y R debe incrementar en 1
        if (newR == currentR + 1 && newVV != 0) {
            throw new BusinessException("Vehicle count must reset to 00 on retread");
        }
    }
}
```

---

## 9. PREGUNTAS FRECUENTES (FAQ)

### Q1: ¿Por qué `generation` es mutable si necesitamos historial?

**R:** El campo `generation` en la tabla `tires` representa el **estado actual** de la llanta. El historial completo se preserva en `history_records.generation_at_event`, que captura snapshots inmutables en cada evento.

**Analogía:** Es como el kilometraje de un auto. El odómetro (tires.generation) siempre muestra el valor actual, pero puedes tener un cuaderno (history_records) donde anotaste los valores en cada revisión.

---

### Q2: ¿Por qué resetear el contador de vehículos al reencauchar?

**R:** Cuando una llanta es reencauchada, técnicamente es un "reinicio" de su vida útil. La banda de rodadura es nueva, por lo que es útil rastrear cuántos vehículos ha servido **desde ese reencauche específico**.

**Caso de uso:** Un gerente de flota puede preguntar: "¿Cuántos vehículos ha servido esta llanta desde su último reencauche?" La respuesta está directamente en VV.

---

### Q3: ¿Qué pasa si una llanta llega al límite (99 vehículos o 9 reencauches)?

**R:**
- **99 vehículos:** Extremadamente raro. La función `increment_vehicle_count()` lanzará una excepción. La llanta debe ser evaluada para baja.
- **9 reencauches:** Política de negocio establece máximo 3 reencauches. Llegar a 9 es teóricamente posible pero no permitido en la práctica. Sistema debe alertar al llegar a 3.

---

### Q4: ¿Cómo puedo saber el número total de vehículos en toda la vida de la llanta?

**R:** Debes consultar `history_records` y contar eventos únicos de montaje:

```sql
SELECT COUNT(DISTINCT vehicle_id) AS total_unique_vehicles
FROM tire_management.history_records
WHERE tire_id = (SELECT id FROM tire_management.tires WHERE tire_number = 'LL-001')
  AND vehicle_id IS NOT NULL;
```

El campo `generation` en `tires` solo muestra vehículos desde el último reencauche.

---

### Q5: ¿Qué diferencia hay entre `generation` y `generation_at_event`?

| Aspecto | `generation` (tires) | `generation_at_event` (history_records) |
|---------|----------------------|-----------------------------------------|
| Tabla | tires | history_records |
| Mutabilidad | ✅ MUTABLE (se actualiza) | ❌ IMMUTABLE (snapshot) |
| Cardinalidad | 1 valor por llanta | N valores por llanta (histórico) |
| Propósito | Estado ACTUAL | Auditoría y trazabilidad |
| Ejemplo | '032' (última actualización) | ['000', '010', '020', '030', '001', ...] |

---

### Q6: ¿Puede una llanta tener `generation = '000'` con estado ACTIVE?

**R:** **NO**. Esto violaría las reglas de negocio. Una llanta con `generation = '000'` nunca ha sido montada, por lo tanto debe estar en estado `INVENTORY`.

**Validación recomendada:**
```sql
ALTER TABLE tire_management.tires
ADD CONSTRAINT ck_generation_state_consistency
CHECK (
    (generation = '000' AND tire_state = 'INVENTORY')
    OR generation != '000'
);
```

---

### Q7: ¿Cómo afecta `generation` a los reportes de consumo?

**R:** Los reportes de consumo mensual filtran llantas nuevas usando `generation = '000'`:

```sql
-- Llantas nuevas compradas en enero 2025
SELECT COUNT(*)
FROM tire_management.tires
WHERE generation = '000'
  AND purchase_date BETWEEN '2025-01-01' AND '2025-01-31';
```

Esto asegura que solo contemos compras de llantas nuevas, no llantas reencauchadas.

---

### Q8: ¿Puedo modificar `generation` manualmente con un UPDATE directo?

**R:** **Técnicamente sí, pero NUNCA debe hacerse.**

- ✅ **CORRECTO:** Usar funciones de negocio (mountTireOnVehicle, retreadTire)
- ❌ **INCORRECTO:** `UPDATE tires SET generation = '050' WHERE ...`

**Razón:** Las funciones de negocio aseguran:
- Validación de reglas de negocio
- Creación de registros históricos sincronizados
- Cambios de estado consistentes
- Auditoría completa

---

### Q9: ¿Cómo busco todas las llantas que han sido reencauchadas al menos una vez?

**R:**
```sql
SELECT *
FROM tire_management.v_tires_with_counters
WHERE retread_count >= 1;

-- O usando función directamente:
SELECT *
FROM tire_management.tires
WHERE tire_management.get_retread_count(generation) >= 1;
```

---

### Q10: ¿Qué pasa si intento montar una llanta que ya está ACTIVE?

**R:** El sistema debe rechazar la operación. Las validaciones deben estar en el service layer:

```java
if (tire.getTireState() == TireState.ACTIVE) {
    throw new BusinessException(
        "Cannot mount tire " + tireNumber + ": already mounted on another vehicle"
    );
}
```

---

## 10. GLOSARIO

| Término | Definición |
|---------|-----------|
| **generation** | Código de 3 dígitos que codifica el contador de vehículos y reencauches de una llanta |
| **VV** | Vehicle count: Primeros 2 dígitos de generation (00-99) |
| **R** | Retread count: Último dígito de generation (0-9) |
| **generation_at_event** | Snapshot inmutable del valor de generation en el momento de un evento histórico |
| **MUTABLE** | Campo que puede cambiar mediante UPDATE (ej: tires.generation) |
| **IMMUTABLE** | Campo que no cambia después de INSERT (ej: history_records.generation_at_event) |
| **Aggregate Root** | En DDD, entidad principal que controla acceso a su grafo de entidades (Tire) |
| **Event Sourcing** | Patrón donde cambios se capturan como secuencia de eventos (history_records) |
| **Snapshot** | Captura del estado de un campo en un momento específico del tiempo |
| **Natural Key** | Identificador de negocio que tiene significado (tire_number) |
| **Surrogate Key** | Identificador técnico sin significado de negocio (UUID id) |
| **Soft Delete** | Borrado lógico mediante campo deleted_at en lugar de DELETE físico |
| **ROI** | Return on Investment: Retorno de inversión de una llanta |
| **Reencauche** | Proceso de renovar la banda de rodadura de una llanta usada |
| **Rotación** | Cambio de posición de una llanta dentro del mismo vehículo |
| **Montaje** | Instalación de una llanta en un vehículo |
| **Desmontaje** | Remoción de una llanta de un vehículo |

---

## REGISTRO DE CAMBIOS

| Versión | Fecha | Autor | Cambios |
|---------|-------|-------|---------|
| 1.0 | 2026-01-22 | Equipo Vórtice | Documento inicial creado |

---

## APROBACIONES

| Rol | Nombre | Fecha | Firma |
|-----|--------|-------|-------|
| Arquitecto de Software | [Pendiente] | 2026-01-22 | _________ |
| Especialista en Migración Legacy | [Pendiente] | 2026-01-22 | _________ |
| Jefe de Taller | [Pendiente] | 2026-01-22 | _________ |
| Administrador de Flota | [Pendiente] | 2026-01-22 | _________ |

---

**FIN DEL DOCUMENTO**
