## Petición
1. Explica con tus palabras qué problema de negocio resuelve el módulo.
2. Identifica actores, roles y responsabilidades.
3. Enumera los casos de uso principales.
4. Lista reglas de negocio explícitas e implícitas.
5. Indica supuestos o información faltante.

GATE: ❌ No continuar hasta que este análisis sea validado explícitamente.

#### 3.1.2 Diseño de Dominio (DDD)

# TAREA: Diseño del Modelo de Dominio – [MÓDULO]

## Petición
Antes de cualquier código:

1. Identifica Aggregates y Aggregate Roots.
2. Define entidades y Value Objects.
3. Enumera invariantes del dominio.
4. Define estados y transiciones válidas.
5. Identifica eventos de dominio.

Entrega:
- Explicación textual
- Diagrama (Mermaid o PlantUML)
- Tabla de invariantes

GATE: ❌ No generar código sin aprobación del modelo.

#### 3.1.3 Diseño de API REST

# TAREA: Diseño de API REST – [MÓDULO / AGGREGATE]

## Petición
1. Diseña endpoints RESTful.
2. Define validaciones de negocio (422).
3. Establece roles y permisos.
4. Define errores esperados.

GATE: ❌ No implementar endpoints todavía.

#### 3.1.4 Migración Oracle → PostgreSQL (Genérico)

# TAREA: Estrategia de Migración Oracle → PostgreSQL

## Petición
1. Mapping de tipos Oracle → PostgreSQL.
2. Estrategia de preservación histórica.
3. Identificación de riesgos.
4. Controles post-migración.

GATE: ❌ No escribir código Java aún.

#### 3.2.1 Comprensión Profunda del Dominio

# TAREA: Comprensión funcional del Sistema de Llantas

## Petición
Antes de diseñar o codificar:

1. Describe el ciclo de vida completo de una llanta.
2. Explica claramente los conceptos:
   - LLANTA
   - GRUPO
   - Generación
   - Vida / Reencauche
3. Explica cómo se preserva el histórico completo.
4. Enumera errores comunes al modelar este dominio.

GATE: ❌ No continuar sin validación funcional explícita.

#### 3.2.2 Traducción Legacy (Oracle Forms / PL-SQL → Use Cases)

# TAREA: Traducción de lógica legacy a casos de uso modernos

## Insumos
- Procedimientos PL/SQL (PDB_MONTARLLANTA, PDB_DESMONTARLLANTA, PDB_ROTARLLANTA, PDB_RECIRCULAR)
- Triggers Oracle Forms
- Validaciones implícitas

## Petición
Para cada proceso legacy:

1. Qué hace funcionalmente.
2. Reglas de negocio ocultas.
3. Estados involucrados.
4. Caso de uso moderno equivalente.
5. Entradas, salidas y validaciones.

Entrega tabla:
[Proceso Legacy] → [Use Case Moderno]

GATE: ❌ No diseñar dominio todavía.

#### 3.2.3 Modelado de Dominio Específico de Llantas

# TAREA: Modelo de Dominio – Sistema de Llantas

## Petición
Diseña el dominio considerando:

- Aggregate Root del ciclo de vida de la llanta
- Entidades: Inventario, Instalación, Muestreo, Historia, Baja
- Value Objects: Kilometraje, Profundidad, Presión, Fecha Operativa
- Estados válidos
- Transiciones permitidas y prohibidas

Entrega:
- Diagrama
- Narrativa detallada

GATE: ❌ No generar entidades ni tablas sin aprobación.

#### 3.2.4 Invariantes Críticas del Dominio

# TAREA: Identificación de Invariantes – Sistema de Llantas

## Petición
Enumera y explica:

1. Reglas temporales (fechas).
2. Reglas de kilometraje.
3. Reglas de estado.
4. Reglas de unicidad (LLANTA + GRUPO).
5. Reglas irreversibles.

Para cada invariante indica:
- Dónde se valida (Use Case / Dominio).
- Mensaje de error esperado.

#### 3.2.5 Migración de Datos con Preservación Histórica

# TAREA: Estrategia de Migración de Datos – Llantas

## Petición
1. Estrategia ETL.
2. Preservación total del histórico.
3. Reconstrucción del estado actual.
4. Validaciones y reconciliación post-migración.

GATE: ❌ No ejecutar migración sin revisión formal.

#### 3.2.6 Implementación Backend – Use Cases

# TAREA: Implementación de Use Case – [NOMBRE]

## Obligatorio antes de escribir código
1. Explica el flujo funcional.
2. Enumera invariantes validadas.
3. Identifica efectos secundarios y eventos.

Luego genera:
- Clase Use Case
- Servicios de dominio
- Validaciones explícitas
- Manejo transaccional

GATE: ❌ No avanzar sin pruebas funcionales.

#### 3.2.7 Pruebas Funcionales de Negocio

# TAREA: Pruebas de Negocio – Sistema de Llantas

## Petición
Genera escenarios Given / When / Then para:

- Casos felices
- Casos límite
- Violación de invariantes
- Escenarios históricos (reencauche, reversión, baja)

Estas pruebas deben representar el negocio real, no solo el código.

#### 3.3 CIERRE DE FASE

Antes de avanzar a la siguiente fase:

1. Resume lo construido.
2. Lista reglas de negocio cubiertas.
3. Identifica riesgos y supuestos.
4. Solicita validación explícita.

GATE: ❌ No continuar sin aprobación.
### Paso 1: Análisis
Por favor, primero:
1. Explora la estructura actual del backend en src/main/java
2. Identifica cómo están organizados los módulos
3. Detecta patrones de DTOs, Services, Controllers, Repositories
4. Confirma el patrón antes de proceder

### Paso 2: Diseño
Una vez confirmado el patrón:
1. Diseña el modelo de dominio para Tire
2. Con base en el documento 
3. Diseña los endpoints REST principales

### Paso 3: Implementación (Incremental)
No generes todo de una vez. Vamos paso a paso:
1. Primero: Entidades de dominio
2. Segundo: DTOs
3. Tercero: Repository
4. Cuarto: Service
5. Quinto: Controller
6. Sexto: Tests

# TAREA: Análisis funcional del módulo [MODULO]

## Petición
1. Explica con tus palabras qué problema de negocio resuelve el módulo.
2. Identifica actores, roles y responsabilidades.
3. Enumera los casos de uso principales.
4. Lista reglas de negocio explícitas e implícitas.
5. Indica supuestos o información faltante.

GATE: ❌ No continuar hasta que este análisis sea validado explícitamente.

## Petición
Antes de diseñar o codificar:

1. Describe el ciclo de vida completo de una llanta.
2. Explica claramente los conceptos:
   - LLANTA
   - GRUPO
   - Generación
   - Vida / Reencauche
3. Explica cómo se preserva el histórico completo.
4. Enumera errores comunes al modelar este dominio.

GATE: ❌ No continuar sin validación funcional explícita.

## Obligatorio antes de escribir código
1. Explica el flujo funcional.
2. Enumera invariantes validadas.
3. Identifica efectos secundarios y eventos.

Luego genera:
- Clase Use Case
- Servicios de dominio
- Validaciones explícitas
- Manejo transaccional

GATE: ❌ No avanzar sin pruebas funcionales.