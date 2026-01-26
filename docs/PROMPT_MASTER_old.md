# PROMPT MASTER PARA DESARROLLO CON IA
## Sistema TRANSER Modernizado - Guía Completa para Desarrollo Asistido por IA

**Versión:** 1.1
**Fecha:** 19 de Enero de 2026
**Compatible con:** Claude (Anthropic), ChatGPT (OpenAI), GitHub Copilot

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Context Setting Base](#2-context-setting-base)
3. [Prompts por Fase de Desarrollo](#3-prompts-por-fase-de-desarrollo)
---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento proporciona **prompts optimizados** para desarrollar el sistema TRANSER - Vortice modernizado con asistencia de IA (Claude, ChatGPT, Copilot). Los prompts están diseñados para:

- ✅ Maximizar la calidad del código generado
- ✅ Mantener consistencia arquitectónica
- ✅ Acelerar el desarrollo sin comprometer calidad
- ✅ Facilitar aprendizaje del equipo

### 1.2 Cómo Usar Este Documento

1. **Antes de iniciar desarrollo:** Establecer contexto base (sección 2)
2. **Durante desarrollo:** Usar prompts específicos por fase (sección 3)
3. **Iteración:** Refinar outputs con prompts de seguimiento

---

## 2. CONTEXT SETTING BASE

### 2.1 Prompt Inicial (Copiar al inicio de cada sesión)

```markdown
# CONTEXTO DEL PROYECTO: Sistema de Informacion Milenio Operativo TRANSER Vortice

## Sobre el Proyecto
Estoy desarrollando la modernización de nuestro Sistema de Informacion Milenio Operativo, un ERP para empresa de transporte de carga en Colombia.
El sistema actual está en Oracle Forms 6i (obsoleto) y lo estamos migrando a stack moderno.

## Stack Tecnológico
- **Backend:** Java 21 + Spring Boot 3.5
- **Frontend:** React 18 + TypeScript + Material-UI
- **Base de Datos:** PostgreSQL 18
- **Arquitectura:** Monolito modular con separación por bounded contexts (DDD)
- **Build:** Maven (backend), Vite (frontend)

## Arquitectura
- **Estilo:** Clean Architecture / Hexagonal
- **Capas:**
  1. Presentation (Controllers, DTOs)
  2. Application (Use Cases, Services)
  3. Domain (Entities, Value Objects, Business Rules)
  4. Infrastructure (JPA, External APIs, File System)

- **Módulos principales:**
  - Tires (Llantas)
  - Managment (Administracion)
  - Dashboard ()
  - Catalog (Catalogos)
  - Inventory (Inventarios)
  - Purchasing (Compras)
  - Fleet (Flota)
  - HR (Recursos Humanos)

## Convenciones de Código

### Backend (Java)
- Nomenclatura: camelCase para variables/métodos, PascalCase para clases
- Paquetes: com.transer.vortice.[module].[layer]
- No usar `@Autowired` en campos, usar constructor injection
- Preferir records para DTOs inmutables
- Usar Lombok solo para @Getter, @Setter, @Builder (evitar @Data)
- Validación con Jakarta Validation (@NotNull, @NotBlank, etc.)

### Frontend (TypeScript/React)
- Nomenclatura: camelCase para variables/funciones, PascalCase para componentes
- Hooks personalizados con prefijo `use`
- Props con interface, no type alias
- Preferir function components, NO class components
- Estado global con Zustand, estado del servidor con React Query

### Base de Datos (PostgreSQL)
- Nombres de tablas: plural, snake_case (ej: `work_orders`)
- Columnas: singular, snake_case (ej: `created_at`)
- Primary keys: `id` (UUID para entidades principales, BIGSERIAL para secundarias)
- Foreign keys: `[tabla_singular]_id` (ej: `vehicle_id`)
- Timestamps: `created_at`, `updated_at`, `deleted_at` (TIMESTAMP WITH TIME ZONE)
- Auditoría: `created_by`, `updated_by`, `deleted_by` (BIGINT referencias a users)
- Soft deletes con `deleted_at IS NULL`
- Boolean: `is_[adjetivo]` (ej: `is_active`)

## Principios de Desarrollo
1. **YAGNI:** No implementar funcionalidad que no se necesita ahora
2. **DRY:** No repetir código, pero no abstraer prematuramente
3. **SOLID:** Especialmente Single Responsibility y Dependency Inversion
4. **Testing:** Unit tests para lógica de negocio, integration tests para use cases
5. **Seguridad:** Validar inputs, no confiar en el cliente, usar prepared statements

## Patrones Preferidos
- Repository pattern para acceso a datos
- DTO pattern para exponer APIs (NO exponer entidades de dominio)
- Builder pattern para objetos complejos
- Strategy pattern para variaciones de comportamiento
- Observer pattern (Domain Events) para desacoplamiento

## Lo que NO hacer
- ❌ No usar `@Transactional` en capa de presentación
- ❌ No mezclar lógica de negocio en controllers
- ❌ No usar JPA entities en DTOs de respuesta
- ❌ No hacer consultas N+1 (usar JOIN FETCH)
- ❌ No hardcodear valores (usar constantes o configuración)
- ❌ No usar `Optional.get()` sin verificar `isPresent()`
- ❌ No crear Pull Requests sin pruebas

---

**Con este contexto, por favor asísteme en el desarrollo del proyecto.**
```

### 2.2 Prompt de Verificación de Contexto

Después de establecer el contexto base, verificar que la IA lo entendió:

```markdown
Para confirmar que entendiste el contexto, por favor:
1. Resume el stack tecnológico en una línea
2. Indica cuál es la arquitectura de software que estamos usando
3. Dame un ejemplo de cómo se vería el nombre de una tabla en PostgreSQL siguiendo nuestras convenciones
4. Dame un ejemplo de cómo se vería un package de Java para el módulo de Taller (Workshop)

Si todo es correcto, responde "Contexto confirmado ✓" y espera mi siguiente instrucción.
```

---

## 3. PROMPTS POR FASE DE DESARROLLO

### 3.1 FASE: Análisis y Diseño

#### 3.1.1 Diseño de Modelo de Dominio

```markdown
# TAREA: Diseñar modelo de dominio para [Llantas]

## Contexto
[Descripción del módulo y sus responsabilidades]

## Requerimientos Funcionales
# PRD - SISTEMA DE CONTROL Y GESTIÓN DE LLANTAS
## DOCUMENTO DE REQUISITOS DEL PRODUCTO - VERSIÓN 2.0 ACTUALIZADA

**Versión:** 2.0  
**Fecha de Análisis:** Enero 20, 2026  
**Analista:** Análisis basado en formularios Oracle Forms Legacy + DDL + Procedimientos Almacenados  
**Sistema Origen:** MILENIO - Módulo de Llantas  
**Estado:** **ANÁLISIS COMPLETO - 95% de información disponible**

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

### 3.1 Módulo de Administración de Maestros 

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

### 3.4 Módulo de Historia de Llantas 

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

### 3.5 Módulo de Gestión de Bajas

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

### 9.1 Seguridad
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

#### 3.1.2 Diseño de API REST

```markdown
# TAREA: Diseñar API REST para [LLANTAS]

## Contexto
- Modulo de llantas

## Petición
Diseña los endpoints REST siguiendo RESTful best practices:

### Criterios:
1. **Recursos:** Usa sustantivos en plural (ej: `/work-orders`, no `/getWorkOrders`)
2. **Métodos HTTP:** GET (lectura), POST (creación), PUT (actualización completa), PATCH (actualización parcial), DELETE
3. **Códigos de respuesta:** 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 422 Unprocessable Entity, 500 Internal Server Error
4. **Paginación:** Query params `page`, `size`, `sort`
5. **Filtrado:** Query params descriptivos
6. **HATEOAS:** Incluir links a recursos relacionados (opcional para MVP)

Por favor, entrega:
- Tabla con endpoints (Método, Path, Descripción, Request Body, Response, Códigos de estado)
- Ejemplos de request/response en JSON
- Notas sobre autenticación/autorización (qué roles pueden acceder)
```


# TAREA: Generar DDL Optimizado y Mapeo de Migración (Oracle to PostgreSQL)

## Contexto de Origen
- **Base de Datos Origen:** Oracle 11g
- **Esquema/Tabla Origen:** [Nombre de la tabla en Oracle]
- **DDL Original:**
```sql
## 3. ARQUITECTURA DE DATOS

### 3.1. Tablas Identificadas - ACTUALIZACIÓN V2

#### 3.1.1. Tablas Principales (Transaccionales)

**Tabla: LLANTAS**
```sql
CREATE TABLE LLANTAS.LLANTAS (
  LLANTA VARCHAR2(20),
  GRUPO CHAR(3),  -- ESTRUCTURA: GG + V (Generación + Vida)
  VALOR NUMBER(7,0) NOT NULL,
  FECHA DATE NOT NULL,
  PROVEE NUMBER(5,0) NOT NULL,
  FACTURA NUMBER(7,0) NOT NULL,
  FICHA NUMBER(5,0) NOT NULL,
  NEUMA NUMBER(2,0),
  VALORRN NUMBER(7,0),
  PROTEC NUMBER(2,0),
  VALORP NUMBER(7,0),
  VEHICULO CHAR(6) NOT NULL,
  POSICION NUMBER(2,0) NOT NULL,
  KINSTALA NUMBER(8,0) NOT NULL,
  FECHAI DATE NOT NULL,
  CONSTRAINT PK_LLANTAS_LLANGRU PRIMARY KEY (LLANTA, GRUPO),
  CONSTRAINT UK_VEHI_POS UNIQUE (VEHICULO, POSICION),
  CONSTRAINT FK_LLANTAS_FICHA FOREIGN KEY (FICHA) REFERENCES FICHATEC (CODIGO),
  CONSTRAINT FK_VEHICULO_VEHILLANTAS FOREIGN KEY (VEHICULO) REFERENCES VEHICULOS_LLANTAS (PLACA)
);
```

**Relaciones Documentadas:**
- → VEHICULOS_LLANTAS (VEHICULO → PLACA)
- → FICHATEC (FICHA → CODIGO)
- ← MUESTREO (LLANTA, GRUPO)
- ← HISTORIA (LLANTA, GRUPO)
- ← KMS_RECORRIDO_LLANTAS (LLANTA, GRUPO)

**Tabla: VEHICULOS_LLANTAS (Simplificada para módulo de llantas)**
```sql
CREATE TABLE LLANTAS.VEHICULOS_LLANTAS (
  PLACA CHAR(6),
  CLASE NUMBER(2,0) NOT NULL,
  MARCA VARCHAR2(15) NOT NULL,
  MODELO NUMBER(4,0) NOT NULL,
  KILOMINI NUMBER(7,0) NOT NULL,
  KILOMACT NUMBER(7,0) NOT NULL,
  ESTADO VARCHAR2(1) NOT NULL,  -- A=Activo, I=Inactivo
  OPERANDO VARCHAR2(1) NOT NULL,  -- S/N
  CONSTRAINT PK_VEHICULOS_PLACA PRIMARY KEY (PLACA),
  CONSTRAINT FK_VEHICULOS_CLASE FOREIGN KEY (CLASE) REFERENCES CLASES (CODIGO)
);
```

**Tabla: MUESTREO**
```sql
CREATE TABLE LLANTAS.MUESTREO (
  LLANTA VARCHAR2(20),
  GRUPO CHAR(3),
  KILOM NUMBER(8,0),
  PI NUMBER(3,1) NOT NULL,  -- Profundidad Izquierda
  PC NUMBER(3,1) NOT NULL,  -- Profundidad Central
  PD NUMBER(3,1) NOT NULL,  -- Profundidad Derecha
  PRESION NUMBER(3,0) NOT NULL,
  FECHA DATE NOT NULL,
  CONSTRAINT PK_MUESTRE_LLANGRU PRIMARY KEY (LLANTA, GRUPO, KILOM)
);
```

**Tabla: KMS_RECORRIDO_LLANTAS**
```sql
CREATE TABLE LLANTAS.KMS_RECORRIDO_LLANTAS (
  KMRL_SECUENCIA_NB NUMBER(10,0) NOT NULL,  -- Secuencia única
  KMRL_LLANTA_NB VARCHAR2(20),
  KMRL_GRUPO_CH CHAR(3) NOT NULL,
  KMRL_KMSRECORRIDO_NB NUMBER(14,4) NOT NULL,  -- KMS acumulados
  KMRL_FECHA_DT DATE NOT NULL,
  CONSTRAINT KMRL_LLAN_GRU_FEC_PK PRIMARY KEY (KMRL_SECUENCIA_NB, KMRL_LLANTA_NB, KMRL_GRUPO_CH)
);
```

**NOTA IMPORTANTE:** Esta tabla acumula kilómetros por cada GRUPO (vida) de la llanta.

**Tabla: HISTORIA**
```sql
CREATE TABLE LLANTAS.HISTORIA (
  LLANTA VARCHAR2(20),
  GRUPO CHAR(3),
  VALOR NUMBER(7,0) NOT NULL,
  FECHA DATE NOT NULL,
  PROVEE NUMBER(5,0) NOT NULL,
  FACTURA NUMBER(7,0) NOT NULL,
  FICHA NUMBER(5,0) NOT NULL,
  NEUMA NUMBER(2,0),
  VALORRN NUMBER(7,0),
  PROTEC NUMBER(2,0),
  VALORP NUMBER(7,0),
  VEHICULO CHAR(6) NOT NULL,
  POSICION NUMBER(2,0) NOT NULL,
  KINSTALA NUMBER(8,0) NOT NULL,
  FECHAI DATE NOT NULL,
  KREMUEVE NUMBER(8,0) NOT NULL,  -- Kilometraje al desmontar
  FECHAF DATE NOT NULL,  -- Fecha de desmontaje
  PORQUE NUMBER(3,0) NOT NULL,  -- Motivo de desmontaje (FK a OBSERVA)
  CONSTRAINT PK_HISTORIA_LLANGRU PRIMARY KEY (LLANTA, GRUPO),
  CONSTRAINT FK_HISTORIA_FICHA FOREIGN KEY (FICHA) REFERENCES FICHATEC (CODIGO),
  CONSTRAINT FK_HIST_POR_OBS_COD FOREIGN KEY (PORQUE) REFERENCES OBSERVA (CODIGO)
);
```

**NOTA:** HISTORIA almacena el registro cuando una llanta es desmontada de un vehículo.

**Tabla: INVENTARIO**
```sql
CREATE TABLE LLANTAS.INVENTARIO (
  LLANTA VARCHAR2(20),
  GRUPO CHAR(3),
  INVENT NUMBER(2,0) NOT NULL,  -- Ubicación en inventario (FK a LOCALIZA)
  VALOR NUMBER(7,0) NOT NULL,
  FECHA DATE NOT NULL,
  PROVE NUMBER(5,0) NOT NULL,
  FACTURA NUMBER(7,0) NOT NULL,
  FICHA NUMBER(5,0) NOT NULL,
  CONSTRAINT PK_INVENTARIO_LLANGRU PRIMARY KEY (LLANTA, GRUPO),
  CONSTRAINT FK_INVENTARIO_FICHA FOREIGN KEY (FICHA) REFERENCES FICHATEC (CODIGO),
  CONSTRAINT FK_INVENTARIO_INVENT FOREIGN KEY (INVENT) REFERENCES LOCALIZA (CODIGO)
);
```

**Tabla: RETIRADAS**
```sql
CREATE TABLE LLANTAS.RETIRADAS (
  LLANTA VARCHAR2(20),
  GRUPO CHAR(3) NOT NULL,
  VALOR NUMBER(6,0) NOT NULL,
  ACTA NUMBER(5,0) NOT NULL,
  FECHA DATE DEFAULT SYSDATE,
  AUTOR VARCHAR2(25) NOT NULL,
  OBSER NUMBER(3,0),  -- Observación (FK a OBSERVA)
  FICHA NUMBER(5,0),
  PORQUE NUMBER(3,0),  -- Motivo de retiro (FK a OBSERVA)
  CONSTRAINT PK_RETIRADAS_LLANGRU PRIMARY KEY (LLANTA, GRUPO),
  CONSTRAINT FK_RETIRADAS_FICHA FOREIGN KEY (FICHA) REFERENCES FICHATEC (CODIGO),
  CONSTRAINT FK_RETIRADAS_OBSER FOREIGN KEY (OBSER) REFERENCES OBSERVA (CODIGO),
  CONSTRAINT RETI_PORQ_OBSE_COD_FK FOREIGN KEY (PORQUE) REFERENCES OBSERVA (CODIGO)
);
```

**Tabla: INTERMEDIO (Temporal para recirculación)**
```sql
CREATE TABLE LLANTAS.INTERMEDIO (
  LLANTA VARCHAR2(20),
  GRUPO CHAR(3),
  ESTADO NUMBER(2,0),
  PROVE NUMBER(5,0),
  FICHA NUMBER(5,0),
  CONSTRAINT PK_INTERMEDIO_LLANGRU PRIMARY KEY (LLANTA, GRUPO),
  CONSTRAINT FK_INTERMEDIO_FICHA FOREIGN KEY (FICHA) REFERENCES FICHATEC (CODIGO)
);
```

**PROPÓSITO:** Tabla temporal para almacenar llantas que están en proceso de reencauche o cambio de gallo antes de ser reincorporadas al inventario.

#### 3.1.2. Tablas de Catálogo (Maestros)

**Tabla: FICHATEC (Núcleo del Sistema)**
```sql
CREATE TABLE LLANTAS.FICHATEC (
  CODIGO NUMBER(5,0),
  MARCA NUMBER(2,0) NOT NULL,  -- FK a MARCAS_LLANTAS
  TIPO NUMBER(2,0) NOT NULL,  -- FK a TIPOS
  REF NUMBER(5,0) NOT NULL,  -- FK a REFERENCIA
  DIMENSION VARCHAR2(8),
  KESPERA NUMBER(6,0) NOT NULL,  -- Kilómetros esperados
  KMAYOR NUMBER(5,0),
  KMENOR NUMBER(5,0),
  KMEDIO NUMBER(5,0),
  RESPERA NUMBER(1,0),  -- Reencauches esperados
  PERDIDA NUMBER(4,0),
  TOTOAL NUMBER(6,0),
  COSTOH NUMBER(7,2),  -- Costo por hora
  PI NUMBER(3,1) NOT NULL,  -- Profundidad inicial izquierda
  PC NUMBER(3,1) NOT NULL,  -- Profundidad inicial central
  PD NUMBER(3,1) NOT NULL,  -- Profundidad inicial derecha
  UCOMPRA NUMBER(4,0),  -- Última compra (cantidad)
  UPRECIO NUMBER(14,4),  -- Último precio
  UFECHA DATE,  -- Última fecha de compra
  PROVEE1 NUMBER(5,0) NOT NULL,  -- Proveedor principal
  PROVEE2 NUMBER(5,0) NOT NULL,  -- Proveedor secundario
  PROVEEU NUMBER(5,0) NOT NULL,  -- Proveedor de última compra
  PESO NUMBER(4,0),
  CONSTRAINT PK_FICHATEC_CODIGO PRIMARY KEY (CODIGO)
);
```

**Relaciones:**
- ← LLANTAS, INVENTARIO, HISTORIA, RETIRADAS, INTERMEDIO (FICHA)

*Resto de tablas de catálogo documentadas en V1.0*

#### 3.1.3. Tablas de Log y Auditoría - **NUEVO EN V2**

**Tabla: LOG_LLANTAS**
```sql
CREATE TABLE LLANTAS.LOG_LLANTAS (
  LOGL_SECUENCIA_NB NUMBER(10,0) NOT NULL,
  LOGL_MENSAJE_V2 VARCHAR2(2000) NOT NULL,
  LOGL_FECHA_DT DATE NOT NULL,
  CONSTRAINT PK_SECLOGLLANTAS PRIMARY KEY (LOGL_SECUENCIA_NB)
);
```

**Tabla: TMPLOGMOV (Logs Temporales de Movimientos Web/Móvil)**
```sql
CREATE TABLE LLANTAS.TMPLOGMOV (
  SECUENCIA NUMBER(7,0),
  TICKET NUMBER(7,0),  -- Número de ticket/transacción
  LOGMOV VARCHAR2(4000),  -- String codificado de movimientos
  PLACA VARCHAR2(6),
  KLMS NUMBER(7,0),
  ESTADO VARCHAR2(1),  -- A=Activo, P=Procesado, R=Rechazado
  USUARIO NUMBER(13,0),
  OFICINA NUMBER(3,0),
  FECHA DATE DEFAULT SYSDATE,
  CONSTRAINT PK_TMPLOGMOV PRIMARY KEY (SECUENCIA)
);
```

**FORMATO DE LOGMOV:**
```
@P[Origen]@P[Destino]@[LLANTA]@[GRUPO]@[PI]@[PC]@[PD]@[PRESION]@[OBS]@[TIPO];
```

Donde TIPO:
- `M` = Montar llanta
- `R` = Retirar/Desmontar llanta
- `T` = Rotar llanta (cambiar posición)

**Tabla: TMPLOGLLA (Logs Persistentes)**
```sql
CREATE TABLE LLANTAS.TMPLOGLLA (
  SECUENCIA NUMBER(7,0) NOT NULL,
  MOVIMIENTOS VARCHAR2(4000),
  FECHA DATE DEFAULT SYSDATE,
  CONSTRAINT PK_TMPLOGLLA PRIMARY KEY (SECUENCIA)
);
```

#### 3.1.4. Tablas Externas (Integración) - **NUEVO EN V2**

**Tabla: VEHICULOS (Maestro Completo - Módulo Externo)**

Tabla externa al módulo de llantas que contiene información completa de vehículos.

**Campos Identificados (26 campos):**
- VEHI_PLACA_CH (PK)
- VEHI_CLASE_NB, VEHI_MARCA_V2, VEHI_MODELO_V2
- VEHI_CAPACIDAD_NB, VEHI_NOMOTOR_NB, VEHI_NOEJES_NB
- VEHI_COLOR_V2, VEHI_CHASIS_NB, VEHI_CONSUMO_NB
- VEHI_PROPIETARIO_NB, VEHI_NACION_NB, VEHI_EMPAFIL_NB
- VEHI_ESTADO_NB, VEHI_AFILIADO_NB, VEHI_VINCULA_NB
- VEHI_ESTADO_V2, VEHI_MODELOREPO_NB, VEHI_LINEA_NB
- VEHI_TIPOCARRO_NB, VEHI_NOSERIE_V2, VEHI_CONFIGURACION_V2
- VEHI_PESOVACIO_NB
- VEHI_FECCREA_DT, VEHI_USUCREA_NB, VEHI_FECANULA_DT
- VEHI_USUANULA_NB, VEHI_OFICREA_NB, VEHI_OFIACTUALIZA_NB

**Tabla: TRAILERS (Maestro de Remolques - Módulo Externo)**

Tabla externa que contiene información de trailers/remolques.

**Campos Identificados (20 campos):**
- TRAI_PLACA_CH (PK)
- TRAI_TIPO_NB, TRAI_MARCA_V2, TRAI_MODELO_NB
- TRAI_SERIE_NB, TRAI_PROPIET_NB, TRAI_CATEGORIA_NB
- TRAI_NACION_NB, TRAI_NOEJES_NB, TRAI_CAPACIDAD_NB
- TRAI_TIPOPROPIETA_NB, TRAI_ESTADO_V2, TRAI_PESO_NB
- TRAI_CAMPO1_NB
- TRAI_FECCREA_DT, TRAI_USUCREA_NB, TRAI_FECANULA_DT
- TRAI_USUANULA_NB, TRAI_OFICREA_NB, TRAI_OFIACTUALIZA_NB

**CONCLUSIÓN DE INTEGRACIÓN:**

El sistema de llantas se integra con un módulo más amplio de gestión de vehículos. VEHICULOS_LLANTAS es una vista simplificada o tabla derivada para uso específico del módulo de llantas.

### 3.2. Diagrama de Relaciones Actualizado

```
MÓDULO EXTERNO: VEHÍCULOS
┌─────────────────────────────────────────┐
│ VEHICULOS (26 campos)                   │
│ - VEHI_PLACA_CH (PK)                    │
│ - Información completa del vehículo     │
└───────────────┬─────────────────────────┘
                │ (Relación no directa)
                ↓
┌───────────────────────────────────────────┐
│ VEHICULOS_LLANTAS (8 campos)              │
│ - PLACA (PK)                              │
│ - Vista/Tabla simplificada para llantas  │
└───────────────┬───────────────────────────┘
                │
                │ FK_VEHICULO_VEHILLANTAS
                ↓
┌───────────────────────────────────────────┐
│ LLANTAS (Llantasinstaladas)          │
│ - LLANTA + GRUPO (PK)                     │
│ - VEHICULO (FK)                           │
│ - POSICION (UK con VEHICULO)              │
│ - FICHA (FK)                              │
└─────┬─────────────┬───────────────────────┘
      │             │
      │             └─────────┐
      ↓                       ↓
┌─────────────┐       ┌──────────────────┐
│ MUESTREO    │       │ KMS_RECORRIDO_   │
│             │       │ LLANTAS          │
│ - Registros │       │ - Acumuladores   │
│   múltiples │       │ - Un registro    │
│   por llanta│       │   por vida       │
└─────────────┘       └──────────────────┘
      │
      ↓ (Al desmontar)
┌──────────────────────────────────────────┐
│ HISTORIA                                  │
│ - LLANTA + GRUPO (PK)                     │
│ - Registro de instalación anterior        │
│ - KINSTALA, KREMUEVE, PORQUE              │
└───────────────────────────────────────────┘
                      │
                      ↓ (Al retirar)
            ┌─────────────────────┐
            │ RETIRADAS           │
            │ - Baja definitiva   │
            │ - LLANTA + GRUPO    │
            └─────────────────────┘
      
CATÁLOGOS MAESTROS:
┌──────────────┐
│ FICHATEC     │←─────────┐
│ - CODIGO(PK) │          │
│ - MARCA (FK)─┼──→ MARCAS_LLANTAS
│ - TIPO (FK)──┼──→ TIPOS
│ - REF (FK)───┼──→ REFERENCIA
│ - PROVEE1-3──┼──→ PROVEEDORES_LLANTAS
└──────────────┘

PROCESO REENCAUCHE:
┌──────────────┐
│ LLANTAS      │
│ GRUPO: 000   │ (Nueva)
└──────┬───────┘
       │ (Desmontar)
       ↓
┌──────────────┐
│ INTERMEDIO   │
│ GRUPO: 000   │ (Temporal)
└──────┬───────┘
       │ PDB_RECIRCULAR(tipo='R')
       ↓
┌──────────────┐
│ INVENTARIO   │
│ GRUPO: 001   │ (Primera reencauchada)
└──────────────┘
```
## Petición
Genera la clase JPA Entity siguiendo estas especificaciones:

### Requisitos:
1. **Nomenclatura:** Nombre de clase en PascalCase, nombre de tabla en snake_case
2. **Annotations:**
   - `@Entity` y `@Table(name = "...")`
   - `@Id` con generación apropiada (`@GeneratedValue` para UUID o SERIAL)
   - `@Column` solo cuando nombre difiere de convención
   - Relaciones con `@ManyToOne`, `@OneToMany`, etc. (especificar `fetch`, `cascade`)
3. **Auditoría:** Incluir campos de auditoría si aplica (created_at, created_by, etc.)
4. **Getters/Setters:** Usar Lombok `@Getter`, `@Setter`
5. **Equals/HashCode:** Basado en `id` si es generado, basado en business key si no
6. **ToString:** Usar Lombok `@ToString`, excluir colecciones lazy

### Formato de entrega:
- Código Java completo
- Imports necesarios
- Comentarios JavaDoc en la clase (breve descripción)
```

**Ejemplo de uso:**

```markdown
# TAREA: Generar JPA Entity para Work Order

## Contexto del Dominio
Work Order representa una orden de trabajo en el taller. Puede ser para un vehículo,
un componente reparable, o una herramienta. Tiene estados, tareas asociadas, mecánicos asignados,
y control de costos y tiempos.

## Estructura de Tabla (PostgreSQL)
```sql
CREATE TABLE work_orders (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                  VARCHAR(50) UNIQUE NOT NULL,
    work_order_type       VARCHAR(20) NOT NULL,
    vehicle_id            BIGINT REFERENCES vehicles(id),
    component_id          BIGINT REFERENCES components(id),
    status                VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    lead_mechanic_id      BIGINT NOT NULL REFERENCES users(id),
    description           TEXT NOT NULL,
    estimated_cost        NUMERIC(14,2),
    total_cost            NUMERIC(14,2),
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by            BIGINT NOT NULL REFERENCES users(id)
);
```

## Petición
[Seguir el prompt de arriba]
```

#### 3.2.2 Generar Repository

```markdown
# TAREA: Generar Repository para [ENTIDAD]

## Contexto
[Descripción de la entidad y operaciones de consulta necesarias]

## Consultas Requeridas
[Listar consultas necesarias más allá de las básicas CRUD]

Ejemplos:
- Buscar por estado
- Buscar por rango de fechas
- Buscar con filtros múltiples
- Consultas con agregaciones

## Petición
Genera la interface Repository extendiendo `JpaRepository` o `CrudRepository`:

### Requisitos:
1. **Queries derivadas:** Usa métodos con nombres convencionales cuando sea posible
   - Ejemplo: `findByStatus(Status status)`
2. **@Query:** Para consultas complejas que no se pueden expresar con nombre de método
   - Preferir JPQL sobre SQL nativo cuando sea posible
   - Usar `JOIN FETCH` para evitar N+1 queries
3. **Proyecciones:** Usar interfaces de proyección o DTOs para consultas de solo lectura
4. **Paginación:** Incluir variantes con `Pageable` cuando sea necesario
5. **Specifications:** Si hay filtros dinámicos, sugerir uso de Criteria API

### Formato de entrega:
- Interface de Repository con métodos
- Queries `@Query` con comentarios explicativos
- Ejemplo de uso desde un Service
```

#### 3.2.3 Generar Service / Use Case

```markdown
# TAREA: Generar Application Service (Use Case) para [OPERACIÓN]

## Contexto
[Descripción del caso de uso y flujo de negocio]

## Reglas de Negocio
[Listar reglas que se deben aplicar]

## Dependencias
[Qué repositories/services se necesitan]

## Petición
Genera la clase de Application Service siguiendo Clean Architecture:

### Estructura:
1. **Clase:** `[Operación]UseCase` o `[Entidad]Service`
2. **Constructor injection:** Todas las dependencias por constructor (Spring las inyecta)
3. **Método principal:** Descriptivo del caso de uso (ej: `createWorkOrder(...)`)
4. **Transacciones:** `@Transactional` si modifica datos
5. **Validaciones:** Validar inputs, lanzar excepciones de dominio si no cumple reglas
6. **Events:** Publicar domain events después de operación exitosa
7. **Logging:** Log de operaciones importantes (nivel INFO o DEBUG)
8. **Manejo de errores:** No capturar excepciones genéricas, dejar que suban al controller

### Formato de entrega:
- Código completo de la clase Service/UseCase
- DTOs de entrada y salida (records si son inmutables)
- Excepciones custom si es necesario
- Unit test básico (mocking dependencies)
```

**Ejemplo de uso:**

```markdown
# TAREA: Generar Application Service para Crear Work Order

## Contexto
El caso de uso "Crear Work Order" permite al Coordinador de Taller crear una nueva OT
a partir de un reporte de falla aprobado, una rutina de mantenimiento, o manualmente.

## Reglas de Negocio
1. El vehículo debe existir y estar activo
2. Si es desde reporte de falla, el reporte debe estar aprobado y no tener OT ya asociada
3. El mecánico líder debe existir y estar activo
4. Se debe generar un código único con formato: [SEDE]-OT-[AÑO]-[CONSECUTIVO]
5. Estado inicial siempre es CREATED
6. Se debe publicar evento `WorkOrderCreatedEvent` después de guardar

## Dependencias
- WorkOrderRepository
- VehicleRepository (para validar vehículo)
- FailureReportRepository (si es desde reporte)
- UserRepository (para validar mecánico)
- EventPublisher (para publicar eventos)
- CodeGeneratorService (para generar código único)

## Petición
[Seguir el prompt de arriba]
```

#### 3.2.4 Generar Controller

```markdown
# TAREA: Generar REST Controller para [ENTIDAD/MÓDULO]

## Contexto
[Descripción del módulo y operaciones a exponer]

## Endpoints Requeridos
[Listar endpoints diseñados en fase de diseño]

## Petición
Genera el Controller siguiendo estas especificaciones:

### Estructura:
1. **Annotations:**
   - `@RestController`
   - `@RequestMapping("/api/[recurso-plural]")`
   - `@RequiredArgsConstructor` (Lombok para constructor injection)
   - `@Validated` (para habilitar validación de método)
   - `@Tag` (OpenAPI/Swagger para documentación)

2. **Métodos:**
   - Un método por endpoint
   - Annotations: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
   - `@PreAuthorize` para control de acceso basado en roles/permisos
   - Parámetros anotados: `@RequestBody`, `@PathVariable`, `@RequestParam`
   - Validación con `@Valid` en request bodies
   - Retornar `ResponseEntity<T>` con código de estado apropiado

3. **Documentación OpenAPI:**
   - `@Operation(summary = "...", description = "...")`
   - `@ApiResponse` para códigos de respuesta
   - `@Parameter` para parámetros no obvios

4. **Manejo de Errores:**
   - No manejar excepciones en controller (usar `@ControllerAdvice` global)

### Formato de entrega:
- Código completo del Controller
- DTOs de request/response (si aún no existen)
- Mapper entre DTO y Domain (usar MapStruct o manual)
```

### 3.3 FASE: Implementación Frontend

#### 3.3.1 Generar TypeScript Interfaces/Types

```markdown
# TAREA: Generar TypeScript types para [ENTIDAD]

## Contexto
[Descripción de la entidad]

## DTOs del Backend
[Pegar los DTOs de Java o descripción de estructura de JSON]

## Petición
Genera los types/interfaces de TypeScript para trabajar con esta entidad:

### Requisitos:
1. **Interface** para la entidad completa (para GET responses)
2. **Type** o Interface para crear (request de POST) - puede ser `Omit<Entity, 'id' | 'createdAt'>`
3. **Type** o Interface para actualizar (request de PUT/PATCH) - puede ser `Partial<Entity>`
4. **Enums** para valores constantes (estados, tipos, etc.)
5. **Comentarios JSDoc** para campos no obvios

### Convenciones:
- PascalCase para interfaces/types/enums
- camelCase para propiedades
- Tipos primitivos: `string`, `number`, `boolean`, `Date` (NO Date para JSON, usar `string` y convertir)
- Arrays: `Array<T>` o `T[]`
- Opcionales: `field?: type`
- Null-safe: Evitar `| null` si no es necesario, usar `?` en su lugar

### Formato de entrega:
- Archivo `[entidad].types.ts`
- Exports de todas las interfaces/types/enums
```

**Ejemplo de uso:**

```markdown
# TAREA: Generar TypeScript types para Work Order

## DTOs del Backend

```java
// WorkOrderSummaryDTO
public record WorkOrderSummaryDTO(
    UUID id,
    String code,
    String status,
    String vehiclePlate,
    String mechanicName,
    BigDecimal estimatedCost,
    Instant createdAt
) {}

// CreateWorkOrderRequest
public record CreateWorkOrderRequest(
    UUID vehicleId,
    String description,
    Priority priority,
    UUID leadMechanicId
) {}
```

## Petición
[Seguir el prompt de arriba]
```

#### 3.3.2 Generar React Component

```markdown
# TAREA: Generar componente React para [PROPÓSITO]

## Contexto
[Descripción del componente y su rol en la UI]

## Funcionalidad Requerida
[Qué debe hacer el componente]

## Petición
Genera un componente React con TypeScript siguiendo estas especificaciones:

### Estructura:
1. **Function component** (NO class component)
2. **TypeScript:** Props con interface, estado tipado
3. **Hooks:**
   - `useState` para estado local
   - `useEffect` para efectos secundarios
   - Custom hooks para lógica reutilizable (ej: `useWorkOrders`)
   - React Query (`useQuery`, `useMutation`) para peticiones al backend

4. **Props:**
   - Interface `[Componente]Props` definida fuera del componente
   - Desestructuración de props en parámetro de función
   - Props opcionales con `?`

5. **Estilo:**
   - Material-UI (MUI) components
   - `sx` prop para estilos inline puntuales
   - Evitar CSS-in-JS complejo, usar theme del proyecto

6. **Accesibilidad:**
   - Labels descriptivos
   - ARIA attributes cuando sea necesario
   - Keyboard navigation

7. **Performance:**
   - `React.memo` si el componente re-renderiza frecuentemente sin cambios
   - `useCallback` y `useMemo` solo si hay problemas de performance medidos

### Formato de entrega:
- Archivo `[Componente].tsx`
- Imports ordenados (React, bibliotecas externas, imports locales)
- Interface de Props
- Código del componente
- Export default
```

**Ejemplo de uso:**

```markdown
# TAREA: Generar componente React para Listado de Work Orders

## Contexto
Necesito un componente que muestre una tabla con todas las órdenes de trabajo.
Debe permitir filtrado por estado, búsqueda por placa/código, y paginación.
Al hacer clic en una fila, debe navegar al detalle de la OT.

## Funcionalidad Requerida
1. Tabla con columnas: Código, Placa, Estado, Mecánico, Descripción, Costo Estimado
2. Barra de filtros encima de la tabla:
   - Dropdown para filtrar por estado
   - Input de búsqueda (placa o código)
   - Botón "Limpiar filtros"
3. Botón "Nueva OT" que abre modal/navega a formulario
4. Paginación en la parte inferior
5. Loading state mientras carga datos
6. Empty state si no hay resultados

## Estilo
- Usar Material-UI DataGrid o Table
- Chips para mostrar estado con colores (Verde=Cerrada, Azul=En Progreso, Rojo=Pausada)
- Responsive (debe verse bien en tablet)

## Petición
[Seguir el prompt de arriba]
```

#### 3.3.3 Generar Custom Hook

```markdown
# TAREA: Generar custom hook para [PROPÓSITO]

## Contexto
[Qué lógica debe encapsular el hook]

## Petición
Genera un custom hook de React siguiendo estas especificaciones:

### Estructura:
1. **Nombre:** `use[Nombre]` (prefijo `use` obligatorio)
2. **Parámetros:** Tipados con TypeScript
3. **Retorno:** Objeto con propiedades/métodos, o tupla `[state, actions]`
4. **React Query:** Si el hook hace fetching de datos, usar `useQuery` o `useMutation`
5. **Error handling:** Incluir manejo de errores, exponer `isError`, `error`
6. **Loading states:** Exponer `isLoading`, `isFetching`

### Casos de uso típicos:
- Fetching de datos (usar `useQuery`)
- Mutaciones (usar `useMutation`)
- Lógica de formulario (combinar con Formik)
- Estado compartido (combinar con Zustand)

### Formato de entrega:
- Archivo `use[Nombre].ts` o `.tsx`
- Interface de parámetros (si aplica)
- Type del retorno
- Código del hook
- Comentario JSDoc con ejemplo de uso
```

**Ejemplo de uso:**

```markdown
# TAREA: Generar custom hook para listar Work Orders

## Contexto
Necesito un hook que:
1. Haga fetch de work orders desde `/api/work-orders`
2. Soporte filtros (status, searchTerm) y paginación (page, size)
3. Use React Query para caching y revalidación
4. Exponga estados de loading, error, data

## API Endpoint
```typescript
GET /api/work-orders?status=IN_PROGRESS&search=SWO521&page=0&size=10

Response:
{
  "content": [...],
  "totalElements": 50,
  "totalPages": 5,
  "number": 0,
  "size": 10
}
```

## Petición
[Seguir el prompt de arriba]
```

### 3.4 FASE: Testing

#### 3.4.1 Generar Unit Tests (Backend)

```markdown
# TAREA: Generar unit tests para [CLASE]

## Contexto
[Descripción de la clase a testear]

## Código de la Clase
```java
[Pegar código de la clase]
```

## Petición
Genera unit tests usando JUnit 5 y Mockito:

### Estructura:
1. **Clase de test:** `[ClaseBajoTest]Test`
2. **Annotations:**
   - `@ExtendWith(MockitoExtension.class)` (si usas Mockito)
   - `@Mock` para dependencias
   - `@InjectMocks` para la clase bajo test

3. **Métodos de test:**
   - Nombre descriptivo: `metodo_escenario_resultadoEsperado()`
   - Estructura AAA (Arrange, Act, Assert)
   - Un assert por test (idealmente)

4. **Cobertura:**
   - Happy path (caso exitoso)
   - Edge cases (casos límite)
   - Error cases (qué pasa cuando falla)

5. **Assertions:**
   - AssertJ para assertions fluidas (recomendado)
   - O JUnit assertions básicas

### Formato de entrega:
- Clase de test completa
- Imports necesarios
- Comentarios en tests complejos

#### 3.4.2 Generar Integration Tests (Backend)

```markdown
# TAREA: Generar integration tests para [ENDPOINT/SERVICIO]

## Contexto
[Qué se está testeando end-to-end]

## Petición
Genera integration tests usando Spring Boot Test:

### Estructura:
1. **Annotations:**
   - `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`
   - `@AutoConfigureTestDatabase` (si usas H2 o Testcontainers)
   - `@Transactional` y `@Rollback` para limpiar después de cada test

2. **Setup:**
   - `@BeforeEach` para preparar datos de test
   - `@AfterEach` para cleanup (si es necesario)

3. **Testing de API:**
   - Usar `TestRestTemplate` o `MockMvc`
   - Verificar status codes
   - Verificar response bodies
   - Verificar side effects (datos guardados en BD)

4. **Cobertura:**
   - Happy path
   - Validaciones (bad request)
   - Autenticación/Autorización (si aplica)

### Formato de entrega:
- Clase de integration test
- Setup de datos de test
- Métodos de test con assertions completas
```

**FIN DEL DOCUMENTO**