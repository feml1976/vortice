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
- Dimensión (ej: 295/80R22.5)
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
- ancho de banda

**Validaciones:**
- Código único
- Marca, tipo y referencia deben existir en catálogos
- Kilómetros esperados > 0
- Profundidades iniciales entre 0 y 99.9 mm
- Proveedores deben existir en catálogo

---

### RF-002: Control de Inventario de Llantas
**Prioridad:** Alta

**Descripción:**  
El sistema debe controlar las llantas que están en inventario (bodega) sin montar.

**Operaciones:**
1. **Ingreso de llantas nuevas:**
   - Número de llanta (identificador único)
   - Grupo (tipo: 000=nueva, 001-999=reencauche)
   - Valor
   - Fecha de ingreso
   - Proveedor
   - Número de factura
   - Ficha técnica asociada
   - Localización en bodega

2. **Consulta de inventario:**
   - Por ficha técnica
   - Por localización
   - Por proveedor
   - Por rango de fechas

3. **Salida de inventario:**
   - Al montar en vehículo → pasa a LLANTAS
   - Registro automático en HISTORIA

**Reglas de Negocio:**
- Cada llanta tiene identificador único (LLANTA, GRUPO)
- GRUPO = '000' para llantas nuevas
- GRUPO > '000' para reencauches (incrementa con cada reencauche)
- No se pueden eliminar llantas con movimientos históricos

**Tablas:**
- `INVENTARIO`
- `FICHATEC` (FK)
- `PROVEEDORES_LLANTAS` (FK)
- `LOCALIZA` (FK)

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