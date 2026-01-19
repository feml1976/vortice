# SUGERENCIAS Y RECOMENDACIONES TÉCNICAS
## Sistema TRANSER Modernizado - Arquitectura y Mejores Prácticas

**Versión:** 1.0
**Fecha:** 31 de Diciembre de 2025
**Empresa:** TRANSER
**Proyecto:** Modernización Sistema Operativo/Taller

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Análisis del Sistema Actual](#2-análisis-del-sistema-actual)
3. [Arquitectura Propuesta](#3-arquitectura-propuesta)
4. [Rediseño de Base de Datos](#4-rediseño-de-base-de-datos)
5. [Stack Tecnológico Detallado](#5-stack-tecnológico-detallado)
6. [Patrones de Diseño y Mejores Prácticas](#6-patrones-de-diseño-y-mejores-prácticas)
7. [Seguridad](#7-seguridad)
8. [DevOps y CI/CD](#8-devops-y-cicd)
9. [Estrategia de Migración](#9-estrategia-de-migración)
10. [Roadmap de Implementación](#10-roadmap-de-implementación)
11. [Estimaciones de Esfuerzo y Costos](#11-estimaciones-de-esfuerzo-y-costos)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Documento

Este documento proporciona recomendaciones técnicas para la modernización del sistema TRANSER, enfocándose en:

- **Arquitectura de software moderna y escalable**
- **Mejoras en el diseño de base de datos**
- **Adopción de tecnologías actuales y probadas**
- **Implementación de mejores prácticas de la industria**
- **Estrategia de migración segura y gradual**

### 1.2 Principios Rectores

La modernización se guiará por los siguientes principios:

1. **Pragmatismo sobre perfección:** Soluciones prácticas que aporten valor real
2. **Simplicidad sobre complejidad:** YAGNI (You Aren't Gonna Need It)
3. **Evolución sobre revolución:** Migración gradual, no big-bang
4. **Estándares sobre invención:** Usar tecnologías probadas y ampliamente adoptadas
5. **Automatización sobre procesos manuales:** Reducir errores humanos
6. **Observabilidad sobre debugging:** Logs, métricas, trazas
7. **Seguridad por diseño:** No como afterthought

---

## 2. ANÁLISIS DEL SISTEMA ACTUAL

### 2.1 Problemas Identificados

#### 2.1.1 Arquitectura y Tecnología

| Problema | Impacto | Severidad |
|----------|---------|-----------|
| **Oracle Forms 6i obsoleto** | No hay soporte, no hay talento en el mercado, sin actualizaciones de seguridad | 🔴 Crítico |
| **Arquitectura cliente-servidor** | Requiere instalación en cada PC, dificulta trabajo remoto | 🔴 Crítico |
| **Sin acceso móvil** | Mecánicos/almacenistas deben ir a PC para registrar información | 🟠 Alto |
| **Código embebido en Forms** | Lógica de negocio mezclada con UI, difícil de mantener | 🟠 Alto |
| **Sin APIs** | Imposible integrar con sistemas externos (GPS, combustible, etc.) | 🟡 Medio |
| **Reports en formato propietario** | Dificultad para compartir/exportar reportes | 🟡 Medio |

#### 2.1.2 Base de Datos

| Problema | Ejemplo | Recomendación |
|----------|---------|---------------|
| **Nomenclatura poco intuitiva** | `AGEN_CLIEPROV_NB`, `USUCREA_NB` | Usar nombres descriptivos en inglés |
| **Prefijos de tabla crípticos** | `AGEN_`, `ALMA_`, `CADE_` | Usar nombres completos: `agents`, `warehouses`, `positions` |
| **Sufijos técnicos** | `_NB` (number), `_V2` (varchar2), `_DT` (date) | El tipo ya lo define la columna, no es necesario en el nombre |
| **Abreviaturas inconsistentes** | `USUA` vs `USUARIO` vs `USU` | Estandarizar nomenclatura |
| **Mezcla de idiomas** | Algunos campos en inglés, mayoría en español | Todo en inglés (estándar de la industria) |
| **Claves compuestas innecesarias** | `(CODIGO_NB, OFICINA_NB)` cuando `CODIGO_NB` es único globalmente | Simplificar con UUIDs o seriales simples |
| **Nombres de tabla en singular** | Depende de tabla (inconsistente) | Estandarizar: **Plural** (convención mayoritaria) |
| **Falta de constraints** | Algunos checks y validaciones solo en Forms | Mover a BD (single source of truth) |

#### 2.1.3 Procesos de Negocio

| Problema | Impacto |
|----------|---------|
| **Pasos manuales en compras** | Posibilidad de errores, tiempos largos |
| **Sin trazabilidad completa** | Difícil auditar cambios en datos críticos |
| **Reportes estáticos** | No permiten drill-down ni exploración |
| **Falta de alertas proactivas** | Problemas se detectan tarde (inventario bajo, vencimientos, etc.) |
| **Sin workflows formales** | Aprobaciones por email/llamada, sin registro sistemático |

### 2.2 Fortalezas a Preservar

A pesar de los problemas, el sistema actual tiene fortalezas que deben preservarse:

1. **Conocimiento del dominio:** El sistema refleja años de experiencia del negocio
2. **Procesos probados:** Los flujos actuales funcionan operativamente
3. **Datos históricos valiosos:** 10+ años de información de mantenimiento, costos, proveedores
4. **Usuarios entrenados:** El personal conoce el sistema actual
5. **Estabilidad:** A pesar de obsoleto, el sistema es estable

**Recomendación:** No intentar "mejorar" procesos de negocio y tecnología simultáneamente. Primero migrar funcionalidad actual (lift and shift modernizado), luego optimizar procesos.

---

## 3. ARQUITECTURA PROPUESTA

### 3.1 Decisión Arquitectónica: Monolito Modular

**Recomendación:** Iniciar con **Monolito Modular**, NO Microservicios.

#### 3.1.1 Justificación

| Criterio | Microservicios | Monolito Modular | Decisión |
|----------|----------------|------------------|----------|
| **Complejidad inicial** | 🔴 Alta | 🟢 Baja | ✅ Monolito |
| **Velocidad de desarrollo** | 🔴 Lenta | 🟢 Rápida | ✅ Monolito |
| **Facilidad de deploy** | 🔴 Complejo | 🟢 Simple | ✅ Monolito |
| **Debugging** | 🔴 Difícil | 🟢 Fácil | ✅ Monolito |
| **Consistencia de datos** | 🔴 Compleja | 🟢 Simple (transacciones ACID) | ✅ Monolito |
| **Escalabilidad** | 🟢 Alta | 🟡 Media (suficiente para 500 usuarios) | ⚖️ Empate |
| **Team size requerido** | 🔴 Múltiples equipos | 🟢 Un equipo pequeño | ✅ Monolito |
| **Infraestructura** | 🔴 Kubernetes, service mesh, etc. | 🟢 Servidor web + BD | ✅ Monolito |

**Cita importante:**
> "Microservices are a solution to a scaling problem you probably don't have yet. Start with a well-structured monolith." — Martin Fowler

**Para TRANSER:**
- 500 usuarios concurrentes es FÁCIL para un monolito bien diseñado
- No hay necesidad de escalar partes del sistema independientemente (al menos inicialmente)
- Equipo de desarrollo probablemente pequeño (5-10 devs)
- Presupuesto limitado (microservicios = mayor costo de desarrollo y operación)

#### 3.1.2 Arquitectura de Monolito Modular

```
┌─────────────────────────────────────────────────────────────┐
│                      FRONTEND (React)                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │Dashboard │ │ Taller   │ │Inventario│ │ Compras  │ ...  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │
└────────────────────┬────────────────────────────────────────┘
                     │ HTTPS/REST
┌────────────────────┴────────────────────────────────────────┐
│                   API GATEWAY (Spring Boot)                 │
│  ┌────────────┐ ┌────────────┐ ┌─────────────┐              │
│  │   Auth     │ │  Logging   │ │Rate Limiting│              │
│  └────────────┘ └────────────┘ └─────────────┘              │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│              APPLICATION LAYER (Spring Boot)                │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              MÓDULOS (Bounded Contexts)               │  │
│  │                                                       │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐             │  │
│  │  │  Workshop│  │Inventory │  │Purchasing│             │  │
│  │  │  Module  │  │  Module  │  │  Module  │  ...        │  │
│  │  └──────────┘  └──────────┘  └──────────┘             │  │
│  │                                                       │  │
│  │  - Cada módulo es INDEPENDIENTE                       │  │
│  │  - Comunicación vía interfaces bien definidas         │  │
│  │  - Posibilidad de extraer a microservicio futuro      │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              DOMAIN LAYER (Business Logic)            │  │
│  │  - Entities                                            │ │
│  │  - Value Objects                                       │ │
│  │  - Domain Services                                     │ │
│  │  - Domain Events                                       │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │            INFRASTRUCTURE LAYER (Persistence)         │ │
│  │  - JPA/Hibernate Repositories                         │ │
│  │  - Database Access                                     │ │
│  │  - External Integrations                              │ │
│  └───────────────────────────────────────────────────────┘ │
└────────────────────┬────────────────────────────────────────┘
                     │ JDBC
┌────────────────────┴────────────────────────────────────────┐
│                   PostgreSQL Database                        │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Arquitectura por Capas

Aplicar **Arquitectura Hexagonal** (Ports & Adapters) / **Clean Architecture**

```
┌──────────────────────────────────────────────┐
│         PRESENTATION LAYER                    │
│  - REST Controllers                           │
│  - DTOs (Data Transfer Objects)              │
│  - Request/Response Mappers                   │
│  - Exception Handlers                         │
└──────────────┬───────────────────────────────┘
               │
┌──────────────┴───────────────────────────────┐
│         APPLICATION LAYER                     │
│  - Use Cases / Application Services           │
│  - Command/Query Handlers                     │
│  - Validation                                 │
│  - Transaction Management                     │
└──────────────┬───────────────────────────────┘
               │
┌──────────────┴───────────────────────────────┐
│            DOMAIN LAYER                       │
│  - Entities (Aggregates)                      │
│  - Value Objects                              │
│  - Domain Events                              │
│  - Business Rules                             │
│  - Domain Services                            │
│  - Repository Interfaces (Port)               │
└──────────────┬───────────────────────────────┘
               │
┌──────────────┴───────────────────────────────┐
│        INFRASTRUCTURE LAYER                   │
│  - JPA Entities (separate from Domain)        │
│  - Repository Implementations (Adapter)       │
│  - External API Clients                       │
│  - File System Access                         │
│  - Email/SMS Sending                          │
└───────────────────────────────────────────────┘
```

**Beneficios:**
- **Testabilidad:** Dominio independiente de infraestructura
- **Flexibilidad:** Cambiar BD o frameworks sin tocar dominio
- **Claridad:** Separación clara de responsabilidades
- **Mantenibilidad:** Módulos desacoplados

### 3.3 Estructura de Proyecto (Ejemplo)

```
transer-backend/
├── pom.xml (o build.gradle)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── transer/
│   │   │           ├── TranserApplication.java
│   │   │           │
│   │   │           ├── shared/                  # Compartido
│   │   │           │   ├── domain/
│   │   │           │   │   ├── Entity.java
│   │   │           │   │   ├── AggregateRoot.java
│   │   │           │   │   └── DomainEvent.java
│   │   │           │   ├── infrastructure/
│   │   │           │   │   ├── config/          # Configuración Spring
│   │   │           │   │   ├── security/
│   │   │           │   │   └── persistence/     # Configuración JPA
│   │   │           │   └── application/
│   │   │           │       └── dto/             # DTOs compartidos
│   │   │           │
│   │   │           ├── workshop/                # Módulo Taller
│   │   │           │   ├── domain/
│   │   │           │   │   ├── model/
│   │   │           │   │   │   ├── WorkOrder.java
│   │   │           │   │   │   ├── Task.java
│   │   │           │   │   │   └── Mechanic.java
│   │   │           │   │   ├── repository/      # Interfaces
│   │   │           │   │   │   └── WorkOrderRepository.java
│   │   │           │   │   └── service/         # Domain services
│   │   │           │   │       └── WorkOrderService.java
│   │   │           │   ├── application/
│   │   │           │   │   ├── dto/
│   │   │           │   │   ├── usecase/
│   │   │           │   │   │   ├── CreateWorkOrderUseCase.java
│   │   │           │   │   │   └── AssignMechanicUseCase.java
│   │   │           │   │   └── mapper/
│   │   │           │   ├── infrastructure/
│   │   │           │   │   ├── persistence/
│   │   │           │   │   │   ├── entity/      # JPA entities
│   │   │           │   │   │   │   └── WorkOrderJpaEntity.java
│   │   │           │   │   │   └── repository/  # JPA repos
│   │   │           │   │   │       └── WorkOrderJpaRepository.java
│   │   │           │   │   └── web/
│   │   │           │   │       └── WorkOrderController.java
│   │   │           │   └── WorkshopModule.java
│   │   │           │
│   │   │           ├── inventory/               # Módulo Inventario
│   │   │           │   ├── domain/
│   │   │           │   ├── application/
│   │   │           │   ├── infrastructure/
│   │   │           │   └── InventoryModule.java
│   │   │           │
│   │   │           ├── purchasing/              # Módulo Compras
│   │   │           │   ├── domain/
│   │   │           │   ├── application/
│   │   │           │   ├── infrastructure/
│   │   │           │   └── PurchasingModule.java
│   │   │           │
│   │   │           └── ... (otros módulos)
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/
│   │           └── migration/                    # Flyway migrations
│   │               ├── V1__create_initial_schema.sql
│   │               ├── V2__add_work_orders.sql
│   │               └── ...
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── transer/
│                   ├── workshop/
│                   │   ├── domain/               # Unit tests
│                   │   ├── application/          # Integration tests
│                   │   └── infrastructure/       # E2E tests
│                   └── ...
└── README.md
```

### 3.4 Comunicación entre Módulos

**Regla de Oro:** Módulos NO deben acceder directamente a la base de datos de otros módulos.

**Opciones de comunicación:**

1. **Interfaces de Aplicación (Recomendado para monolito):**
   ```java
   // En módulo Inventory
   public interface InventoryQueryService {
       ItemDTO findItemById(UUID itemId);
       boolean isAvailable(UUID itemId, int quantity);
   }

   // En módulo Workshop
   @Service
   public class WorkOrderService {
       private final InventoryQueryService inventoryService;
       // Usa la interfaz, no accede directamente a BD de inventario
   }
   ```

2. **Domain Events (Para operaciones asíncronas):**
   ```java
   // Workshop publica evento
   eventPublisher.publish(new WorkOrderClosedEvent(workOrderId));

   // Inventory escucha evento y actualiza stock de herramientas
   @EventListener
   public void onWorkOrderClosed(WorkOrderClosedEvent event) {
       // Retornar herramientas prestadas al inventario
   }
   ```

3. **API Interna REST (Preparación para microservicios):**
   - Cada módulo expone endpoints REST internos
   - Comunicación vía HTTP dentro del mismo proceso
   - Facilita futura extracción a microservicios

### 3.5 Path to Microservices (Futuro)

Si en 2-3 años se requiere escalar módulos independientemente:

1. **Identificar módulo a extraer** (ej: Purchasing crece mucho por expansión)
2. **Separar base de datos** (ya están separados lógicamente)
3. **Convertir interfaz de aplicación en API REST**
4. **Desplegar como servicio independiente**
5. **Actualizar llamadas de otros módulos para usar HTTP**

**Ventaja:** La arquitectura modular hace que esta transición sea relativamente sencilla.

---

## 4. REDISEÑO DE BASE DE DATOS

### 4.1 Principios de Nomenclatura

#### 4.1.1 Convenciones Generales

| Aspecto | Convención | Ejemplo Actual | Ejemplo Propuesto |
|---------|------------|----------------|-------------------|
| **Idioma** | Inglés | `ALMACENES` | `warehouses` |
| **Caso** | snake_case | `ALMA_CODIGO_NB` | `id` |
| **Tablas** | Plural | `VEHICULO` | `vehicles` |
| **Nombres de columnas** | Descriptivos, sin sufijos técnicos | `ALMA_DESCRIPCION_V2` | `description` |
| **Primary Keys** | `id` (simple) o `[tabla]_id` | `ALMA_CODIGO_NB` | `id` |
| **Foreign Keys** | `[tabla_referenciada]_id` | `ALMA_CIUDAD_NB` | `city_id` |
| **Timestamps** | `created_at`, `updated_at`, `deleted_at` | `FECCREA_DT`, `FECANULA_DT` | `created_at`, `deleted_at` |
| **Usuarios auditoría** | `created_by`, `updated_by`, `deleted_by` | `USUCREA_NB`, `USUANULA_NB` | `created_by`, `deleted_by` |
| **Boolean** | `is_[adjetivo]` o `has_[sustantivo]` | `ALMA_ESTADO_V2` ('A'/'I') | `is_active` (boolean) |
| **Enums** | Descripción en texto o tabla de referencia | `ESTADO_V2` ('A', 'I', 'P') | `status` (enum o tabla `statuses`) |

#### 4.1.2 Tipos de Datos

**Usar tipos nativos de PostgreSQL:**

| Tipo de Dato | Tipo PostgreSQL | Comentario |
|--------------|-----------------|------------|
| **ID (Primary Key)** | `BIGSERIAL` o `UUID` | `UUID` para sistemas distribuidos |
| **String corto** | `VARCHAR(n)` | Definir tamaño razonable |
| **Texto largo** | `TEXT` | Sin límite |
| **Número entero** | `INTEGER` o `BIGINT` | Según rango |
| **Decimal** | `NUMERIC(p,s)` | Ej: `NUMERIC(14,2)` para dinero |
| **Fecha** | `DATE` | Solo fecha |
| **Timestamp** | `TIMESTAMP WITH TIME ZONE` | Fecha + hora (¡siempre con zona!) |
| **Boolean** | `BOOLEAN` | NO usar `CHAR(1)` |
| **JSON** | `JSONB` | Para datos semi-estructurados |
| **Enum** | `ENUM` o tabla de referencia | Preferir tabla para flexibilidad |

### 4.2 Ejemplo de Transformación: Tabla ALMACENES

**Tabla Actual:**
```sql
CREATE TABLE TRANSER.ALMACENES (
    ALMA_CODIGO_NB        NUMBER(3,0) PRIMARY KEY,
    ALMA_DESCRIPCION_V2   VARCHAR2(50) NOT NULL,
    ALMA_CIUDAD_NB        NUMBER(5,0),
    ALMA_ENCARGADO_NB     NUMBER(13,0),
    ALMA_DIRECCION_V2     VARCHAR2(80),
    ALMA_ESTADO_V2        VARCHAR2(1) NOT NULL,
    ALMA_TIPOENCAR_V2     VARCHAR2(1) NOT NULL,
    ALMA_OFICINA_NB       NUMBER(5,0),
    CONSTRAINT ALMA_CIUDAD_CIUD_CODIGO_FK
        FOREIGN KEY (ALMA_CIUDAD_NB) REFERENCES CIUDADES(CIUD_CODIGO_NB)
);
```

**Problemas:**
- Prefijo `ALMA_` en todas las columnas (redundante)
- Sufijos `_NB`, `_V2` innecesarios
- `ESTADO_V2` como `VARCHAR2(1)` en lugar de `BOOLEAN`
- Sin timestamps de auditoría
- Sin índices definidos
- Clave primaria numérica secuencial limitada

**Tabla Propuesta:**
```sql
CREATE TABLE warehouses (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(20) UNIQUE NOT NULL,
    name                VARCHAR(100) NOT NULL,
    description         TEXT,
    address             VARCHAR(255),
    city_id             BIGINT NOT NULL REFERENCES cities(id),
    responsible_user_id BIGINT REFERENCES users(id),
    office_id           BIGINT REFERENCES offices(id),
    warehouse_type      VARCHAR(20) NOT NULL,  -- 'GENERAL', 'SPECIALIZED', 'CONSIGNMENT'
    is_active           BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by          BIGINT NOT NULL REFERENCES users(id),
    updated_at          TIMESTAMP WITH TIME ZONE,
    updated_by          BIGINT REFERENCES users(id),
    deleted_at          TIMESTAMP WITH TIME ZONE,  -- Soft delete
    deleted_by          BIGINT REFERENCES users(id),

    -- Indexes
    CONSTRAINT check_warehouse_type
        CHECK (warehouse_type IN ('GENERAL', 'SPECIALIZED', 'CONSIGNMENT'))
);

CREATE INDEX idx_warehouses_city ON warehouses(city_id);
CREATE INDEX idx_warehouses_active ON warehouses(is_active) WHERE is_active = true;
CREATE INDEX idx_warehouses_deleted ON warehouses(deleted_at) WHERE deleted_at IS NULL;
```

**Mejoras:**
✅ Nombres claros y descriptivos
✅ Tipos de datos apropiados
✅ Auditoría completa
✅ Soft delete (no se pierde información)
✅ Constraints para integridad
✅ Índices para performance

### 4.3 Ejemplo de Transformación: Tabla Órdenes de Trabajo

**Tabla Propuesta (Nueva):**
```sql
CREATE TABLE work_orders (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                  VARCHAR(50) UNIQUE NOT NULL,  -- ej: "BOG-OT-2025-00123"
    work_order_type       VARCHAR(20) NOT NULL,  -- 'VEHICLE', 'COMPONENT', 'TOOL'

    -- Vehículo / Componente
    vehicle_id            BIGINT REFERENCES vehicles(id),
    component_id          BIGINT REFERENCES components(id),
    tool_id               BIGINT REFERENCES tools(id),

    -- Origen
    origin                VARCHAR(20) NOT NULL,  -- 'FAILURE_REPORT', 'PREVENTIVE', 'MANUAL'
    failure_report_id     BIGINT REFERENCES failure_reports(id),
    maintenance_routine_id BIGINT REFERENCES maintenance_routines(id),

    -- Estado
    status                VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    -- 'CREATED', 'ASSIGNED', 'IN_PROGRESS', 'PAUSED_PARTS', 'PAUSED_DIAGNOSIS',
    -- 'PAUSED_AUTHORIZATION', 'TESTING', 'CLOSED', 'CANCELLED'

    -- Asignación
    island_id             BIGINT REFERENCES islands(id),
    lead_mechanic_id      BIGINT NOT NULL REFERENCES users(id),
    priority              VARCHAR(10) NOT NULL,  -- 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW'

    -- Fechas
    scheduled_start_at    TIMESTAMP WITH TIME ZONE,
    actual_start_at       TIMESTAMP WITH TIME ZONE,
    estimated_end_at      TIMESTAMP WITH TIME ZONE,
    actual_end_at         TIMESTAMP WITH TIME ZONE,

    -- Kilometraje
    odometer_at_start     INTEGER,
    odometer_at_end       INTEGER,

    -- Costos
    estimated_cost        NUMERIC(14,2),
    actual_labor_cost     NUMERIC(14,2),
    actual_parts_cost     NUMERIC(14,2),
    actual_external_cost  NUMERIC(14,2),
    total_cost            NUMERIC(14,2) GENERATED ALWAYS AS
        (COALESCE(actual_labor_cost, 0) + COALESCE(actual_parts_cost, 0) + COALESCE(actual_external_cost, 0)) STORED,

    -- Tiempos (en minutos)
    estimated_time_minutes   INTEGER,
    actual_work_time_minutes INTEGER,
    downtime_parts_minutes   INTEGER,  -- Tiempo perdido por falta de repuestos
    downtime_other_minutes   INTEGER,

    -- Observaciones
    description           TEXT NOT NULL,
    diagnosis             TEXT,
    work_performed        TEXT,
    observations          TEXT,

    -- Control de calidad
    quality_checked       BOOLEAN DEFAULT false,
    quality_checked_by    BIGINT REFERENCES users(id),
    quality_checked_at    TIMESTAMP WITH TIME ZONE,
    quality_notes         TEXT,

    -- Autorización (si requiere)
    requires_authorization BOOLEAN DEFAULT false,
    authorized_by         BIGINT REFERENCES users(id),
    authorized_at         TIMESTAMP WITH TIME ZONE,

    -- Auditoría
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by            BIGINT NOT NULL REFERENCES users(id),
    updated_at            TIMESTAMP WITH TIME ZONE,
    updated_by            BIGINT REFERENCES users(id),
    closed_at             TIMESTAMP WITH TIME ZONE,
    closed_by             BIGINT REFERENCES users(id),
    cancelled_at          TIMESTAMP WITH TIME ZONE,
    cancelled_by          BIGINT REFERENCES users(id),
    cancellation_reason   TEXT,

    -- Constraints
    CONSTRAINT check_work_order_type
        CHECK (work_order_type IN ('VEHICLE', 'COMPONENT', 'TOOL')),
    CONSTRAINT check_origin
        CHECK (origin IN ('FAILURE_REPORT', 'PREVENTIVE', 'MANUAL', 'CAMPAIGN')),
    CONSTRAINT check_status
        CHECK (status IN ('CREATED', 'ASSIGNED', 'IN_PROGRESS', 'PAUSED_PARTS',
                          'PAUSED_DIAGNOSIS', 'PAUSED_AUTHORIZATION', 'TESTING',
                          'CLOSED', 'CANCELLED')),
    CONSTRAINT check_priority
        CHECK (priority IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW')),
    CONSTRAINT check_entity
        CHECK (
            (work_order_type = 'VEHICLE' AND vehicle_id IS NOT NULL) OR
            (work_order_type = 'COMPONENT' AND component_id IS NOT NULL) OR
            (work_order_type = 'TOOL' AND tool_id IS NOT NULL)
        )
);

-- Índices
CREATE INDEX idx_work_orders_status ON work_orders(status);
CREATE INDEX idx_work_orders_vehicle ON work_orders(vehicle_id);
CREATE INDEX idx_work_orders_mechanic ON work_orders(lead_mechanic_id);
CREATE INDEX idx_work_orders_created ON work_orders(created_at DESC);
CREATE INDEX idx_work_orders_active ON work_orders(status)
    WHERE status NOT IN ('CLOSED', 'CANCELLED');

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_work_orders_updated_at BEFORE UPDATE ON work_orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

### 4.4 Estrategia de Migración de Nomenclatura

**Opción 1: Migración Directa (Recomendado)**
- Crear esquema nuevo con nomenclatura moderna
- Migrar datos de Oracle a PostgreSQL con transformación
- Mapear nombres viejos a nuevos en scripts de migración

**Opción 2: Migración Gradual (Si se requiere coexistencia)**
- Crear vistas en PostgreSQL con nombres antiguos apuntando a tablas nuevas
- Permite que aplicación legacy (si se mantiene temporalmente) siga funcionando
- Gradualmente migrar código a usar nombres nuevos

**Opción 3: Capa de Abstracción**
- JPA permite mapear nombres de tablas/columnas diferentes en Java
- Código usa nomenclatura moderna, BD puede mantener nombres legacy temporalmente
- No recomendado (genera confusión), pero es opción si hay restricciones

### 4.5 Estrategia de IDs

**Recomendación: UUID para tablas principales, BIGSERIAL para secundarias**

**UUID:**
- **Pros:** Globalmente únicos, no secuenciales (seguridad), fácil merge de datos
- **Contras:** Más espacio (16 bytes vs. 8 bytes), índices más grandes
- **Uso:** Tablas principales (vehicles, work_orders, purchase_orders, users)

**BIGSERIAL:**
- **Pros:** Más eficiente, índices más pequeños, legible
- **Contras:** Secuencial (puede exponer información de volumen)
- **Uso:** Tablas de referencia, relaciones muchos-a-muchos

**Ejemplo:**
```sql
-- UUID para entidades principales
CREATE TABLE vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ...
);

-- BIGSERIAL para tablas de soporte
CREATE TABLE vehicle_documents (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    ...
);
```

### 4.6 Soft Deletes vs. Hard Deletes

**Recomendación: Soft Delete para entidades de negocio, Hard Delete para datos temporales**

**Soft Delete:**
- Columna `deleted_at TIMESTAMP WITH TIME ZONE`
- No se elimina físicamente, se marca como eliminado
- Ventajas: Auditoría, recuperación, análisis histórico
- **Uso:** vehicles, work_orders, users, suppliers, items

**Hard Delete:**
- Eliminación física real
- **Uso:** Logs temporales, sesiones, tokens expirados

**Implementación:**
```sql
-- Soft delete
CREATE TABLE items (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    deleted_at TIMESTAMP WITH TIME ZONE,
    ...
);

-- Índice para consultas de registros activos
CREATE INDEX idx_items_active ON items(id) WHERE deleted_at IS NULL;

-- Consulta típica (solo activos)
SELECT * FROM items WHERE deleted_at IS NULL;

-- Trigger para prevenir actualización de registros eliminados (opcional)
CREATE OR REPLACE FUNCTION prevent_update_deleted()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.deleted_at IS NOT NULL THEN
        RAISE EXCEPTION 'Cannot update deleted record';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

### 4.7 Auditoría Completa

**Para todas las tablas importantes, incluir:**

```sql
created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
created_by   BIGINT NOT NULL REFERENCES users(id),
updated_at   TIMESTAMP WITH TIME ZONE,
updated_by   BIGINT REFERENCES users(id),
deleted_at   TIMESTAMP WITH TIME ZONE,
deleted_by   BIGINT REFERENCES users(id)
```

**Para auditoría detallada de cambios (opcional):**

Usar tabla de auditoría separada con triggers:

```sql
CREATE TABLE audit_log (
    id              BIGSERIAL PRIMARY KEY,
    table_name      VARCHAR(50) NOT NULL,
    record_id       VARCHAR(100) NOT NULL,  -- UUID o BIGINT como string
    operation       VARCHAR(10) NOT NULL,   -- 'INSERT', 'UPDATE', 'DELETE'
    old_values      JSONB,
    new_values      JSONB,
    changed_by      BIGINT NOT NULL REFERENCES users(id),
    changed_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ip_address      INET,
    user_agent      TEXT
);

CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at DESC);
```

**Ventaja de JSONB:** Flexible, permite almacenar cambios sin modificar esquema de auditoría.

### 4.8 Particionamiento (Para Tablas Grandes)

**Para tablas que crecerán mucho (work_orders, inventory_movements):**

```sql
-- Particionamiento por rango de fechas
CREATE TABLE work_orders (
    ...
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
) PARTITION BY RANGE (created_at);

-- Crear particiones por año
CREATE TABLE work_orders_2024 PARTITION OF work_orders
    FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE work_orders_2025 PARTITION OF work_orders
    FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

-- Automatizar creación de particiones futuras con pg_partman (extensión)
```

**Ventajas:**
- Consultas más rápidas (PostgreSQL solo busca en partición relevante)
- Archivado más fácil (mover particiones antiguas a almacenamiento frío)
- Mantenimiento más eficiente (VACUUM, ANALYZE por partición)

---

## 5. STACK TECNOLÓGICO DETALLADO

### 5.1 Backend

#### 5.1.1 Lenguaje y Framework

**Java 21 LTS + Spring Boot 3.2+**

**Justificación:**
- ✅ Java 21 es LTS (soporte hasta 2029+)
- ✅ Spring Boot es el framework más maduro y popular para Java
- ✅ Ecosistema enorme: librerías, herramientas, documentación
- ✅ Talento disponible en mercado colombiano
- ✅ Performance excelente (comparable a Go, Node.js para casos de uso típicos)
- ✅ Type-safety (menos bugs en producción)
- ✅ Tooling maduro (IntelliJ IDEA, Eclipse, VS Code)

**Alternativas consideradas:**
- **Kotlin + Spring Boot:** Excelente opción, pero menos talento en mercado
- **Node.js + NestJS:** Bueno, pero TypeScript aún no tan maduro para backend empresarial
- **Python + Django/FastAPI:** Excelente para prototipado, pero performance inferior para sistema transaccional
- **.NET Core:** Muy bueno, pero ecosistema más cerrado (Microsoft)

#### 5.1.2 Dependencias Core

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Flyway para migraciones -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Lombok (reduce boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- MapStruct (mapeo DTO <-> Entity) -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- JWT para autenticación -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Springdoc OpenAPI (Swagger) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- Monitoring -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

#### 5.1.3 Configuración de JPA/Hibernate

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/transer
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: validate  # ¡NUNCA 'update' o 'create-drop' en producción!
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 5.2 Frontend

#### 5.2.1 Framework y Librerías

**React 18+ con TypeScript**

**Stack Propuesto:**
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",

    "typescript": "^5.3.0",

    "axios": "^1.6.2",
    "react-query": "^5.12.0",

    "@mui/material": "^5.15.0",
    "@mui/x-data-grid": "^6.18.0",
    "@mui/x-date-pickers": "^6.18.0",
    "@mui/icons-material": "^5.15.0",
    "@emotion/react": "^11.11.0",
    "@emotion/styled": "^11.11.0",

    "formik": "^2.4.5",
    "yup": "^1.3.3",

    "zustand": "^4.4.7",

    "recharts": "^2.10.0",
    "date-fns": "^3.0.0",
    "react-hot-toast": "^2.4.1"
  },
  "devDependencies": {
    "vite": "^5.0.0",
    "@vitejs/plugin-react": "^4.2.0",
    "eslint": "^8.55.0",
    "prettier": "^3.1.1",
    "vitest": "^1.0.0",
    "@testing-library/react": "^14.1.0"
  }
}
```

**Justificación:**
- **React:** Framework más popular, gran comunidad, trabajo abundante
- **TypeScript:** Type-safety reduce bugs, mejor DX (developer experience)
- **Vite:** Build tool moderno, rápido (3-5x más rápido que Webpack)
- **Material-UI (MUI):** Biblioteca de componentes madura, bien documentada, accesible
- **React Query:** Manejo de estado del servidor, caching, revalidación automática
- **Zustand:** State management simple (alternativa liviana a Redux)
- **Formik + Yup:** Manejo de formularios y validaciones
- **Recharts:** Gráficos y visualizaciones

#### 5.2.2 Estructura de Proyecto Frontend

```
transer-frontend/
├── public/
│   ├── index.html
│   └── assets/
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   │
│   ├── modules/                      # Módulos por dominio
│   │   ├── workshop/
│   │   │   ├── components/
│   │   │   │   ├── WorkOrderList.tsx
│   │   │   │   ├── WorkOrderForm.tsx
│   │   │   │   └── WorkOrderDetails.tsx
│   │   │   ├── hooks/
│   │   │   │   └── useWorkOrders.ts
│   │   │   ├── services/
│   │   │   │   └── workOrderService.ts
│   │   │   ├── types/
│   │   │   │   └── workOrder.types.ts
│   │   │   └── pages/
│   │   │       ├── WorkOrderListPage.tsx
│   │   │       └── WorkOrderDetailPage.tsx
│   │   │
│   │   ├── inventory/
│   │   ├── purchasing/
│   │   └── ...
│   │
│   ├── shared/                        # Compartido
│   │   ├── components/
│   │   │   ├── Button.tsx
│   │   │   ├── DataTable.tsx
│   │   │   ├── Modal.tsx
│   │   │   └── ...
│   │   ├── hooks/
│   │   │   ├── useAuth.ts
│   │   │   ├── useDebounce.ts
│   │   │   └── ...
│   │   ├── services/
│   │   │   ├── api.ts               # Axios instance
│   │   │   └── authService.ts
│   │   ├── store/
│   │   │   ├── authStore.ts
│   │   │   └── ...
│   │   ├── types/
│   │   │   └── common.types.ts
│   │   ├── utils/
│   │   │   ├── formatters.ts
│   │   │   ├── validators.ts
│   │   │   └── constants.ts
│   │   └── layouts/
│   │       ├── MainLayout.tsx
│   │       ├── Sidebar.tsx
│   │       └── Navbar.tsx
│   │
│   ├── router/
│   │   └── index.tsx                 # React Router config
│   │
│   └── styles/
│       └── theme.ts                  # MUI theme customization
│
├── .env.development
├── .env.production
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

#### 5.2.3 Ejemplo de Componente con TypeScript

```typescript
// types/workOrder.types.ts
export interface WorkOrder {
  id: string;
  code: string;
  status: WorkOrderStatus;
  vehicleId: string;
  vehiclePlate: string;
  leadMechanicId: string;
  leadMechanicName: string;
  description: string;
  createdAt: string;
  estimatedCost: number;
}

export enum WorkOrderStatus {
  CREATED = 'CREATED',
  ASSIGNED = 'ASSIGNED',
  IN_PROGRESS = 'IN_PROGRESS',
  PAUSED_PARTS = 'PAUSED_PARTS',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED'
}

// hooks/useWorkOrders.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { workOrderService } from '../services/workOrderService';
import { WorkOrder } from '../types/workOrder.types';

export const useWorkOrders = (filters?: WorkOrderFilters) => {
  return useQuery({
    queryKey: ['workOrders', filters],
    queryFn: () => workOrderService.getAll(filters),
    staleTime: 30000, // 30 segundos
  });
};

export const useCreateWorkOrder = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateWorkOrderDTO) => workOrderService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workOrders'] });
      toast.success('Orden de trabajo creada exitosamente');
    },
    onError: (error) => {
      toast.error('Error al crear orden de trabajo');
      console.error(error);
    },
  });
};

// components/WorkOrderList.tsx
import React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Chip, Box, CircularProgress } from '@mui/material';
import { useWorkOrders } from '../hooks/useWorkOrders';
import { WorkOrderStatus } from '../types/workOrder.types';

const statusColors: Record<WorkOrderStatus, 'default' | 'primary' | 'success' | 'error'> = {
  [WorkOrderStatus.CREATED]: 'default',
  [WorkOrderStatus.ASSIGNED]: 'primary',
  [WorkOrderStatus.IN_PROGRESS]: 'primary',
  [WorkOrderStatus.PAUSED_PARTS]: 'error',
  [WorkOrderStatus.CLOSED]: 'success',
  [WorkOrderStatus.CANCELLED]: 'default',
};

export const WorkOrderList: React.FC = () => {
  const { data: workOrders, isLoading, error } = useWorkOrders();

  const columns: GridColDef[] = [
    { field: 'code', headerName: 'Código', width: 150 },
    { field: 'vehiclePlate', headerName: 'Placa', width: 100 },
    {
      field: 'status',
      headerName: 'Estado',
      width: 150,
      renderCell: (params) => (
        <Chip
          label={params.value}
          color={statusColors[params.value as WorkOrderStatus]}
          size="small"
        />
      ),
    },
    { field: 'leadMechanicName', headerName: 'Mecánico', width: 200 },
    { field: 'description', headerName: 'Descripción', width: 300 },
    {
      field: 'estimatedCost',
      headerName: 'Costo Estimado',
      width: 150,
      valueFormatter: (params) => `$${params.value.toLocaleString('es-CO')}`,
    },
  ];

  if (isLoading) return <Box display="flex" justifyContent="center"><CircularProgress /></Box>;
  if (error) return <div>Error al cargar órdenes de trabajo</div>;

  return (
    <Box sx={{ height: 600, width: '100%' }}>
      <DataGrid
        rows={workOrders || []}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10, 25, 50]}
        checkboxSelection
        disableSelectionOnClick
      />
    </Box>
  );
};
```

### 5.3 Base de Datos

**PostgreSQL 16+**

**Justificación:**
- ✅ Open source, sin costos de licenciamiento
- ✅ ACID completo, transacciones robustas
- ✅ Performance excelente (comparable a Oracle para cargas OLTP)
- ✅ Extensiones potentes: JSONB, Full-text search, PostGIS (si se requiere geo)
- ✅ Herramientas de administración maduras (pgAdmin, DBeaver, TablePlus)
- ✅ Soporte para particionamiento nativo
- ✅ Replicación y alta disponibilidad (Patroni, Stolon)
- ✅ Comunidad activa, excelente documentación

**Configuración Recomendada (postgresql.conf):**
```ini
# Conexiones
max_connections = 200

# Memoria
shared_buffers = 4GB              # 25% de RAM total (si servidor tiene 16GB)
effective_cache_size = 12GB       # 75% de RAM total
maintenance_work_mem = 1GB
work_mem = 20MB

# WAL (Write-Ahead Logging)
wal_buffers = 16MB
min_wal_size = 1GB
max_wal_size = 4GB
checkpoint_completion_target = 0.9

# Query Planning
random_page_cost = 1.1            # Para SSDs (default 4.0 es para HDDs)
effective_io_concurrency = 200    # Para SSDs

# Logging
log_min_duration_statement = 1000  # Loggear queries > 1 segundo
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
log_checkpoints = on
log_connections = on
log_disconnections = on
log_lock_waits = on

# Autovacuum (importante para performance)
autovacuum = on
autovacuum_max_workers = 4
autovacuum_naptime = 10s
```

### 5.4 Herramientas de Desarrollo

| Categoría | Herramienta | Propósito |
|-----------|-------------|-----------|
| **IDE Backend** | IntelliJ IDEA Ultimate | Desarrollo Java, refactoring, debugging |
| **IDE Frontend** | Visual Studio Code | Desarrollo React/TypeScript |
| **Control de Versiones** | Git + GitHub/GitLab | Código fuente |
| **API Testing** | Postman / Insomnia | Pruebas de API REST |
| **DB Management** | DBeaver / pgAdmin 4 | Administración PostgreSQL |
| **Containerización** | Docker Desktop | Desarrollo local |
| **CI/CD** | GitHub Actions / Jenkins | Integración y despliegue continuo |
| **Monitoring** | Prometheus + Grafana | Métricas y dashboards |
| **Logging** | ELK Stack (Elasticsearch, Logstash, Kibana) | Logs centralizados |
| **Error Tracking** | Sentry | Tracking de errores en producción |
| **Load Testing** | JMeter / K6 | Pruebas de carga |

---

## 6. PATRONES DE DISEÑO Y MEJORES PRÁCTICAS

### 6.1 Domain-Driven Design (DDD)

**Aplicar conceptos de DDD para módulos complejos:**

#### 6.1.1 Aggregates

Un **Aggregate** es un cluster de objetos de dominio que se tratan como una unidad.

**Ejemplo: WorkOrder Aggregate**

```java
@Entity
@Table(name = "work_orders")
public class WorkOrder extends AggregateRoot {  // AggregateRoot marca la raíz

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;
    private WorkOrderStatus status;

    // Tasks es parte del aggregate, se gestiona vía WorkOrder
    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    // Método de negocio, NO setter público
    public void assignMechanic(Mechanic mechanic) {
        if (this.status != WorkOrderStatus.CREATED) {
            throw new IllegalStateException("Solo se puede asignar mecánico a OT en estado CREATED");
        }
        this.leadMechanic = mechanic;
        this.status = WorkOrderStatus.ASSIGNED;

        // Publicar evento de dominio
        registerEvent(new WorkOrderAssignedEvent(this.id, mechanic.getId()));
    }

    public void addTask(String description, System system) {
        Task task = new Task(description, system);
        task.setWorkOrder(this);  // Bidireccional
        this.tasks.add(task);
    }

    // Regla de negocio: no se puede cerrar si hay tareas pendientes
    public void close() {
        if (tasks.stream().anyMatch(t -> t.getStatus() != TaskStatus.COMPLETED)) {
            throw new IllegalStateException("No se puede cerrar OT con tareas pendientes");
        }
        this.status = WorkOrderStatus.CLOSED;
        this.closedAt = Instant.now();

        registerEvent(new WorkOrderClosedEvent(this.id));
    }
}
```

**Reglas de Aggregates:**
- Solo la raíz (WorkOrder) se expone fuera del aggregate
- Objetos internos (Task) NO se acceden directamente desde fuera
- Modificaciones siempre a través de métodos de la raíz
- Garantiza invariantes de negocio

#### 6.1.2 Value Objects

**Value Objects** son objetos sin identidad, definidos solo por sus atributos.

**Ejemplo: Money**

```java
@Embeddable
public class Money {
    private BigDecimal amount;
    private String currency;  // "COP", "USD"

    // Constructor privado, usar factory method
    private Money(BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money cop(BigDecimal amount) {
        return new Money(amount, "COP");
    }

    // Inmutable: operaciones retornan nuevo objeto
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    // Equals y hashCode basados en valores
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}

// Uso en Entity
@Entity
public class WorkOrder {
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "estimated_cost")),
        @AttributeOverride(name = "currency", column = @Column(name = "estimated_cost_currency"))
    })
    private Money estimatedCost;
}
```

#### 6.1.3 Domain Events

**Domain Events** representan algo que ocurrió en el dominio.

```java
// Evento de dominio
public record WorkOrderAssignedEvent(
    UUID workOrderId,
    UUID mechanicId,
    Instant occurredAt
) {
    public WorkOrderAssignedEvent(UUID workOrderId, UUID mechanicId) {
        this(workOrderId, mechanicId, Instant.now());
    }
}

// Publicador de eventos (Spring)
@Component
public class DomainEventPublisher {
    private final ApplicationEventPublisher publisher;

    public DomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}

// Listener en otro módulo
@Component
public class NotificationEventListener {

    @EventListener
    @Async  // Procesamiento asíncrono
    public void onWorkOrderAssigned(WorkOrderAssignedEvent event) {
        // Enviar notificación push al mecánico
        notificationService.notify(event.mechanicId(), "Nueva OT asignada: " + event.workOrderId());
    }
}
```

### 6.2 CQRS (Command Query Responsibility Segregation)

**Separar lecturas de escrituras cuando tiene sentido.**

**No es necesario CQRS completo con bases de datos separadas**, pero sí separar:

```java
// COMMAND: Modifica estado
@Service
public class WorkOrderCommandService {

    public UUID createWorkOrder(CreateWorkOrderCommand command) {
        // Validaciones
        // Crear entidad
        // Guardar en BD
        // Publicar evento
        return workOrderId;
    }

    public void assignMechanic(AssignMechanicCommand command) {
        WorkOrder wo = repository.findById(command.workOrderId())
            .orElseThrow();
        wo.assignMechanic(mechanic);
        repository.save(wo);
    }
}

// QUERY: Solo lee, puede optimizar con DTOs y queries nativas
@Service
public class WorkOrderQueryService {

    public Page<WorkOrderSummaryDTO> findAll(WorkOrderFilters filters, Pageable pageable) {
        // Query optimizada, JOIN FETCH, DTOs proyectados
        return repository.findAllSummaries(filters, pageable);
    }

    public WorkOrderDetailDTO findById(UUID id) {
        // Query con todos los detalles necesarios
        return repository.findDetailById(id)
            .map(WorkOrderMapper::toDetailDTO)
            .orElseThrow();
    }
}

// Controlador usa ambos
@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderCommandService commandService;
    private final WorkOrderQueryService queryService;

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CreateWorkOrderRequest request) {
        CreateWorkOrderCommand command = mapper.toCommand(request);
        UUID id = commandService.createWorkOrder(command);
        return ResponseEntity.created(URI.create("/api/work-orders/" + id)).body(id);
    }

    @GetMapping
    public Page<WorkOrderSummaryDTO> list(@RequestParam Map<String, String> filters, Pageable pageable) {
        return queryService.findAll(WorkOrderFilters.from(filters), pageable);
    }
}
```

**Ventajas:**
- Queries optimizadas sin afectar lógica de negocio
- Escalabilidad futura (read replicas)
- Claridad de código (comandos vs. consultas)

### 6.3 Repository Pattern

**No exponer JPA directamente, usar capa de abstracción.**

```java
// Domain interface (no depende de JPA)
public interface WorkOrderRepository {
    WorkOrder findById(UUID id);
    List<WorkOrder> findByStatus(WorkOrderStatus status);
    void save(WorkOrder workOrder);
    void delete(WorkOrder workOrder);
}

// Implementación JPA (Infrastructure)
@Repository
class JpaWorkOrderRepository implements WorkOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public WorkOrder findById(UUID id) {
        return entityManager.find(WorkOrderJpaEntity.class, id)
            .map(JpaMapper::toDomain)
            .orElse(null);
    }

    @Override
    public void save(WorkOrder workOrder) {
        WorkOrderJpaEntity entity = JpaMapper.toJpaEntity(workOrder);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }
}
```

**Ventaja:** Podemos cambiar JPA por otro ORM o incluso por JDBC sin tocar dominio.

### 6.4 DTO Pattern

**NUNCA exponer entidades de dominio en API REST.**

```java
// Entity (interno)
@Entity
public class WorkOrder {
    private UUID id;
    private String code;
    // ... muchos campos internos
}

// DTO (expuesto en API)
public record WorkOrderSummaryDTO(
    UUID id,
    String code,
    String status,
    String vehiclePlate,
    String mechanicName,
    BigDecimal estimatedCost
) {}

public record CreateWorkOrderRequest(
    @NotNull UUID vehicleId,
    @NotBlank String description,
    @NotNull Priority priority
) {}

// Mapper (MapStruct)
@Mapper(componentModel = "spring")
public interface WorkOrderMapper {
    WorkOrderSummaryDTO toSummaryDTO(WorkOrder workOrder);
    List<WorkOrderSummaryDTO> toSummaryDTOs(List<WorkOrder> workOrders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    WorkOrder toEntity(CreateWorkOrderRequest request);
}
```

**Ventajas:**
- API estable (cambios internos no afectan contratos)
- Seguridad (no exponer datos sensibles)
- Performance (DTOs solo con datos necesarios)
- Versionamiento fácil (DTOv1, DTOv2)

---

## 7. SEGURIDAD

### 7.1 Autenticación

**JWT (JSON Web Tokens) + Refresh Tokens**

**Flujo:**

1. **Login:**
   ```
   POST /api/auth/login
   {
     "username": "juan.perez",
     "password": "********"
   }

   Response:
   {
     "accessToken": "eyJhbGciOiJIUzI1...",  // Válido 15 minutos
     "refreshToken": "dGhpcyBpcyBhIHJlZn...",  // Válido 7 días
     "expiresIn": 900,
     "tokenType": "Bearer"
   }
   ```

2. **Requests autenticados:**
   ```
   GET /api/work-orders
   Authorization: Bearer eyJhbGciOiJIUzI1...
   ```

3. **Refresh token (cuando access token expira):**
   ```
   POST /api/auth/refresh
   {
     "refreshToken": "dGhpcyBpcyBhIHJlZn..."
   }

   Response:
   {
     "accessToken": "nuevo_token...",
     "expiresIn": 900
   }
   ```

**Implementación (Spring Security):**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Usar JWT, no CSRF
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/work-orders/**").hasAnyRole("MECHANIC", "COORDINATOR")
                .requestMatchers(HttpMethod.POST, "/api/work-orders").hasRole("COORDINATOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // Strength 12
    }
}

// JWT Filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtService.validateToken(token);
                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);

                // Crear autenticación
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        roles.stream().map(SimpleGrantedAuthority::new).toList()
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException e) {
                // Token inválido
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### 7.2 Autorización (RBAC)

**Roles principales:**

```sql
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administrador del sistema'),
    ('WORKSHOP_COORDINATOR', 'Coordinador de taller'),
    ('MECHANIC', 'Mecánico'),
    ('WAREHOUSE_MANAGER', 'Almacenista'),
    ('PURCHASING_OFFICER', 'Funcionario de compras'),
    ('QUALITY_INSPECTOR', 'Inspector de calidad'),
    ('SUPPLIER', 'Proveedor externo (portal)'),
    ('MANAGER', 'Gerente (solo lectura y reportes)'),
    ('ACCOUNTANT', 'Contador');

-- Permisos granulares
INSERT INTO permissions (name, description) VALUES
    ('work_order:create', 'Crear órdenes de trabajo'),
    ('work_order:read', 'Ver órdenes de trabajo'),
    ('work_order:update', 'Actualizar órdenes de trabajo'),
    ('work_order:delete', 'Eliminar órdenes de trabajo'),
    ('work_order:assign', 'Asignar mecánico a OT'),
    ('work_order:close', 'Cerrar OT'),
    ('inventory:read', 'Consultar inventario'),
    ('inventory:request', 'Solicitar repuestos'),
    ('inventory:release', 'Entregar repuestos'),
    ('purchase_order:create', 'Crear orden de compra'),
    ('purchase_order:approve', 'Aprobar orden de compra');

-- Relación Roles - Permisos
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ((SELECT id FROM roles WHERE name = 'WORKSHOP_COORDINATOR'), (SELECT id FROM permissions WHERE name LIKE 'work_order:%')),
    ((SELECT id FROM roles WHERE name = 'MECHANIC'), (SELECT id FROM permissions WHERE name IN ('work_order:read', 'work_order:update', 'inventory:request')));
```

**Uso en código:**

```java
@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    @PreAuthorize("hasPermission(null, 'work_order:create')")
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CreateWorkOrderRequest request) {
        // ...
    }

    @PreAuthorize("hasPermission(#id, 'WorkOrder', 'work_order:close')")
    @PutMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable UUID id) {
        // ...
    }
}
```

### 7.3 Protección contra Amenazas

#### 7.3.1 SQL Injection

✅ **Usar JPA/Hibernate con Prepared Statements:**

```java
// SEGURO (JPA)
@Query("SELECT w FROM WorkOrder w WHERE w.status = :status")
List<WorkOrder> findByStatus(@Param("status") WorkOrderStatus status);

// SEGURO (Query nativa con parámetros)
@Query(value = "SELECT * FROM work_orders WHERE status = ?1", nativeQuery = true)
List<WorkOrder> findByStatusNative(String status);

// INSEGURO - ¡NUNCA HACER ESTO!
@Query(value = "SELECT * FROM work_orders WHERE status = '" + status + "'", nativeQuery = true)
List<WorkOrder> findByStatusUnsafe(String status);  // ❌ VULNERABLE
```

#### 7.3.2 XSS (Cross-Site Scripting)

✅ **Escapar salidas en frontend:**

```typescript
// React automáticamente escapa por defecto
<div>{workOrder.description}</div>  // ✅ Seguro

// Si necesitas HTML, sanitizar
import DOMPurify from 'dompurify';
<div dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(htmlContent) }} />
```

✅ **Content Security Policy (CSP):**

```java
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public FilterRegistrationBean<Filter> securityHeadersFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter((request, response, chain) -> {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            chain.doFilter(request, response);
        });

        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
```

#### 7.3.3 CSRF (Cross-Site Request Forgery)

Como usamos JWT (stateless), no es necesario protección CSRF tradicional.

**Pero sí implementar:**

1. **SameSite cookies para refresh token:**
   ```java
   Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
   refreshTokenCookie.setHttpOnly(true);  // No accesible desde JS
   refreshTokenCookie.setSecure(true);    // Solo HTTPS
   refreshTokenCookie.setPath("/api/auth");
   refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);  // 7 días
   refreshTokenCookie.setAttribute("SameSite", "Strict");  // Protección CSRF
   response.addCookie(refreshTokenCookie);
   ```

2. **Validar origen de requests:**
   ```java
   @Component
   public class CorsConfig implements WebMvcConfigurer {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/api/**")
               .allowedOrigins("https://transer.com", "https://app.transer.com")  // ¡NO usar "*"!
               .allowedMethods("GET", "POST", "PUT", "DELETE")
               .allowedHeaders("*")
               .allowCredentials(true)
               .maxAge(3600);
       }
   }
   ```

#### 7.3.4 Rate Limiting

**Prevenir ataques de fuerza bruta y DDoS:**

```java
// Usar biblioteca Bucket4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String key = getClientKey(request);  // IP o user ID

        Bucket bucket = cache.computeIfAbsent(key, k -> createBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);  // Too Many Requests
            response.getWriter().write("{ \"error\": \"Too many requests\" }");
        }
    }

    private Bucket createBucket() {
        // 100 requests por minuto
        Bandwidth limit = Bandwidth.builder()
            .capacity(100)
            .refillGreedy(100, Duration.ofMinutes(1))
            .build();

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    private String getClientKey(HttpServletRequest request) {
        // Preferir user ID autenticado, fallback a IP
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return request.getRemoteAddr();
    }
}
```

---

## 8. DEVOPS Y CI/CD

### 8.1 Containerización con Docker

**Dockerfile para Backend (Java + Spring Boot):**

```dockerfile
# Build stage
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/transer-backend-*.jar app.jar

# Crear usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Dockerfile para Frontend (React + Nginx):**

```dockerfile
# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

**docker-compose.yml (Desarrollo Local):**

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: transer-db
    environment:
      POSTGRES_DB: transer
      POSTGRES_USER: transer_user
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U transer_user"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./transer-backend
    container_name: transer-api
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/transer
      SPRING_DATASOURCE_USERNAME: transer_user
      SPRING_DATASOURCE_PASSWORD: dev_password
      SPRING_PROFILES_ACTIVE: dev
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build: ./transer-frontend
    container_name: transer-web
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

### 8.2 CI/CD con GitHub Actions

**.github/workflows/ci.yml:**

```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test-backend:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: transer_test
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_pass
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'corretto'
          cache: maven

      - name: Run tests
        run: mvn test
        working-directory: ./transer-backend
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/transer_test
          SPRING_DATASOURCE_USERNAME: test_user
          SPRING_DATASOURCE_PASSWORD: test_pass

      - name: Build
        run: mvn clean package -DskipTests
        working-directory: ./transer-backend

      - name: Upload JAR
        uses: actions/upload-artifact@v4
        with:
          name: backend-jar
          path: transer-backend/target/*.jar

  test-frontend:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: transer-frontend/package-lock.json

      - name: Install dependencies
        run: npm ci
        working-directory: ./transer-frontend

      - name: Lint
        run: npm run lint
        working-directory: ./transer-frontend

      - name: Test
        run: npm test
        working-directory: ./transer-frontend

      - name: Build
        run: npm run build
        working-directory: ./transer-frontend

      - name: Upload build
        uses: actions/upload-artifact@v4
        with:
          name: frontend-build
          path: transer-frontend/dist

  deploy:
    needs: [test-backend, test-frontend]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Download artifacts
        uses: actions/download-artifact@v4

      - name: Deploy to production
        run: |
          echo "Deploying to production..."
          # Aquí iría el script de deployment real
```

### 8.3 Monitoreo y Observabilidad

#### 8.3.1 Logs Estructurados

**Usar Logback con formato JSON:**

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>true</includeContext>
            <includeCallerData>false</includeCallerData>
            <customFields>{"application":"transer-backend"}</customFields>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/transer-backend.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/transer-backend.%d{yyyy-MM-dd}.gz</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

    <logger name="com.transer" level="DEBUG"/>
    <logger name="org.springframework.web" level="INFO"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
</configuration>
```

#### 8.3.2 Métricas con Prometheus

**Exponer métricas en Spring Boot:**

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${spring.profiles.active}
```

**Métricas custom:**

```java
@Component
public class WorkOrderMetrics {

    private final Counter workOrdersCreated;
    private final Gauge workOrdersActive;

    public WorkOrderMetrics(MeterRegistry registry) {
        this.workOrdersCreated = Counter.builder("work_orders_created_total")
            .description("Total de órdenes de trabajo creadas")
            .tag("type", "vehicle")
            .register(registry);

        this.workOrdersActive = Gauge.builder("work_orders_active")
            .description("Órdenes de trabajo activas")
            .register(registry, workOrderRepository, repo -> repo.countByStatusNot(WorkOrderStatus.CLOSED));
    }

    public void incrementCreated() {
        workOrdersCreated.increment();
    }
}
```

**Prometheus config (prometheus.yml):**

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'transer-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

#### 8.3.3 Dashboards con Grafana

**Dashboard recomendado: JVM Micrometer**

- Importar dashboard ID: 4701
- Muestra: Heap, CPU, GC, Threads, HTTP requests, latencias

**Custom dashboard para métricas de negocio:**

```json
{
  "dashboard": {
    "title": "TRANSER - Taller",
    "panels": [
      {
        "title": "Órdenes de Trabajo Activas",
        "targets": [
          {
            "expr": "work_orders_active"
          }
        ]
      },
      {
        "title": "OTs Creadas (por hora)",
        "targets": [
          {
            "expr": "rate(work_orders_created_total[1h])"
          }
        ]
      }
    ]
  }
}
```

---

## 9. ESTRATEGIA DE MIGRACIÓN

### 9.1 Enfoque Recomendado: Big Bang con Datos Paralelos

**NO recomendamos coexistencia de sistemas (muy complejo).**

**Estrategia:**

1. **Fase 1: Desarrollo y Testing (6 meses)**
   - Desarrollar MVP (Taller, Inventario, Compras)
   - Testing exhaustivo con datos de prueba
   - UAT con usuarios clave

2. **Fase 2: Migración de Datos (1 mes)**
   - Congelar cambios en sistema legacy (solo lectura)
   - Migrar datos históricos a PostgreSQL
   - Validación de integridad de datos
   - Testing con datos reales

3. **Fase 3: Capacitación (2 semanas)**
   - Capacitar usuarios finales
   - Documentación de usuario
   - Videos tutoriales

4. **Fase 4: Go-Live (1 semana)**
   - Fin de semana largo para cutover
   - Sistema legacy apagado
   - Sistema nuevo en vivo
   - Soporte 24/7 primera semana

5. **Fase 5: Estabilización (1 mes)**
   - Corrección de bugs urgentes
   - Ajustes finos
   - Retroalimentación de usuarios

### 9.2 Migración de Datos

**Herramienta: Talend Open Studio / Apache NiFi / Scripts personalizados**

**Proceso:**

```sql
-- Ejemplo de script de migración: ALMACENES → warehouses

-- 1. Crear staging table en PostgreSQL
CREATE TABLE staging_warehouses (
    old_id INTEGER,
    codigo VARCHAR(20),
    descripcion VARCHAR(50),
    ciudad_old_id INTEGER,
    encargado_old_id BIGINT,
    direccion VARCHAR(80),
    estado CHAR(1),
    tipo_encargado CHAR(1),
    oficina_old_id INTEGER
);

-- 2. Exportar desde Oracle a CSV
-- (Usar SQL Developer o script PL/SQL)

-- 3. Importar CSV a staging
\copy staging_warehouses FROM '/path/to/warehouses.csv' CSV HEADER;

-- 4. Transformar y cargar a tabla final
INSERT INTO warehouses (
    code,
    name,
    description,
    address,
    city_id,
    responsible_user_id,
    office_id,
    warehouse_type,
    is_active,
    created_at,
    created_by
)
SELECT
    s.codigo,
    s.descripcion,
    s.descripcion,  -- Description = name por ahora
    s.direccion,
    c.id,  -- Mapeo de ciudad_old_id a nuevo city_id
    u.id,  -- Mapeo de encargado_old_id a nuevo user_id
    o.id,  -- Mapeo de oficina_old_id a nuevo office_id
    CASE s.tipo_encargado
        WHEN 'G' THEN 'GENERAL'
        WHEN 'E' THEN 'SPECIALIZED'
        WHEN 'C' THEN 'CONSIGNMENT'
    END,
    s.estado = 'A',
    NOW(),
    (SELECT id FROM users WHERE username = 'migration_user')
FROM staging_warehouses s
LEFT JOIN cities c ON s.ciudad_old_id = c.legacy_id  -- Assuming cities migradas antes
LEFT JOIN users u ON s.encargado_old_id = u.legacy_id
LEFT JOIN offices o ON s.oficina_old_id = o.legacy_id;

-- 5. Validación
SELECT
    'Oracle' AS source,
    COUNT(*) AS count
FROM staging_warehouses
UNION ALL
SELECT
    'PostgreSQL' AS source,
    COUNT(*) AS count
FROM warehouses;

-- Deben ser iguales

-- 6. Cleanup
DROP TABLE staging_warehouses;
```

**Orden de migración (por dependencias):**

1. Datos maestros sin dependencias:
   - Países, Departamentos, Ciudades
   - Bancos, Monedas
   - Marcas, Sistemas
2. Datos maestros con pocas dependencias:
   - Empresas, Gerencias, Dependencias
   - Oficinas, Almacenes
   - Cargos
3. Personas:
   - Empleados
   - Usuarios
   - Clientes, Proveedores
4. Flota:
   - Vehículos, Remolques
   - Componentes
5. Transaccional:
   - Ítems de inventario
   - Órdenes de Trabajo
   - Órdenes de Compra
   - Movimientos de inventario
   - Facturas

### 9.3 Validación Post-Migración

**Checklist:**

✅ **Integridad referencial:** Todas las FKs válidas
✅ **Conteos:** Totales de registros coinciden
✅ **Sumas de control:** Totales de $ coinciden
✅ **Datos críticos:** Verificar 100 registros aleatorios manualmente
✅ **Pruebas funcionales:** Ejecutar casos de prueba end-to-end

**Query de validación:**

```sql
-- Comparar totales de $ de órdenes de trabajo
SELECT
    'Oracle' AS source,
    SUM(actual_labor_cost + actual_parts_cost + actual_external_cost) AS total
FROM legacy_work_orders
UNION ALL
SELECT
    'PostgreSQL' AS source,
    SUM(total_cost) AS total
FROM work_orders;

-- Diferencia debe ser < 0.01%
```

---

## 10. ROADMAP DE IMPLEMENTACIÓN

### 10.1 Fases del Proyecto

| Fase | Duración | Descripción | Entregables |
|------|----------|-------------|-------------|
| **Fase 0: Preparación** | 1 mes | Infraestructura, ambientes, herramientas | Servidores, repos Git, CI/CD básico |
| **Fase 1: MVP Backend** | 3 meses | Core de Taller, Inventario, Compras | API REST funcional |
| **Fase 2: MVP Frontend** | 2 meses | UI para módulos MVP | Aplicación web usable |
| **Fase 3: Integraciones** | 1 mes | Portal proveedores, facturación electrónica | Integraciones funcionando |
| **Fase 4: Testing y Ajustes** | 1 mes | QA, corrección de bugs | Sistema estable |
| **Fase 5: Migración de Datos** | 1 mes | Scripts, validación | Datos migrados |
| **Fase 6: Capacitación** | 2 semanas | Entrenamiento usuarios | Usuarios capacitados |
| **Fase 7: Go-Live** | 1 semana | Cutover, soporte intensivo | Sistema en producción |
| **Fase 8: Estabilización** | 1 mes | Ajustes post-go-live | Sistema estable en producción |
| **Fase 9: Módulos Adicionales** | 6 meses | Facturación, Viajes, RRHH completo | ERP completo |

**Total MVP:** ~9 meses
**Total ERP Completo:** ~15 meses

### 10.2 Equipo Recomendado

| Rol | Cantidad | Dedicación | Fase Principal |
|-----|----------|------------|----------------|
| **Arquitecto de Software** | 1 | 50% | Fases 0-3 |
| **Tech Lead Backend** | 1 | 100% | Fases 1-9 |
| **Desarrollador Backend Senior** | 2 | 100% | Fases 1-9 |
| **Desarrollador Frontend Senior** | 1 | 100% | Fases 2-9 |
| **Desarrollador Frontend** | 1 | 100% | Fases 2-9 |
| **QA Engineer** | 1 | 100% | Fases 3-8 |
| **DBA / DevOps** | 1 | 50% | Fases 0, 5, 7 |
| **UX/UI Designer** | 1 | 50% | Fases 0-2 |
| **Product Owner** | 1 | 50% | Fases 0-9 |
| **Scrum Master** | 1 | 50% | Fases 1-9 |

**Costo estimado equipo (Colombia):**
- Equipo senior: ~$80M-120M COP/mes
- 9 meses MVP: ~$720M-1,080M COP
- 15 meses completo: ~$1,200M-1,800M COP

---

## 11. ESTIMACIONES DE ESFUERZO Y COSTOS

### 11.1 Estimación por Módulo (MVP)

| Módulo | Backend (días) | Frontend (días) | Testing (días) | Total (días) |
|--------|----------------|-----------------|----------------|--------------|
| **Autenticación y Seguridad** | 10 | 8 | 5 | 23 |
| **Gestión de Usuarios y Roles** | 8 | 6 | 4 | 18 |
| **Vehículos (CRUD básico)** | 12 | 10 | 6 | 28 |
| **Órdenes de Trabajo** | 25 | 20 | 12 | 57 |
| **Mantenimiento Preventivo** | 15 | 12 | 8 | 35 |
| **Inventarios (4 tipos)** | 30 | 25 | 15 | 70 |
| **Compras (12 etapas)** | 35 | 30 | 18 | 83 |
| **Portal de Proveedores** | 20 | 25 | 12 | 57 |
| **Garantías** | 12 | 10 | 6 | 28 |
| **Llantas** | 18 | 15 | 9 | 42 |
| **Reportes Básicos** | 15 | 20 | 8 | 43 |
| **Dashboards** | 10 | 25 | 6 | 41 |
| **Integraciones (Factura electrónica)** | 15 | 5 | 8 | 28 |
| **TOTAL** | **225** | **211** | **117** | **553 días** |

**Nota:** Días de esfuerzo, no días calendario.

**Con equipo de 7 personas (3 backend, 2 frontend, 1 QA, 1 full-stack):**
- Esfuerzo total: 553 días
- Duración: ~4-5 meses (considerando paralelismo y overhead)

**Más contingencia 30%:** 6-7 meses

### 11.2 Costos de Infraestructura

**Opción Cloud (AWS/GCP/Azure):**

| Recurso | Especificación | Costo Mensual (USD) |
|---------|----------------|---------------------|
| **Compute (Backend)** | 2× t3.medium (2 vCPU, 4GB RAM) | $120 |
| **Database** | RDS PostgreSQL db.t3.medium (2 vCPU, 4GB) | $100 |
| **Frontend Hosting** | S3 + CloudFront CDN | $20 |
| **Load Balancer** | ALB | $30 |
| **Monitoring** | CloudWatch, Logs | $50 |
| **Backups** | S3 (500GB) | $15 |
| **TOTAL** | | **~$335/mes** |

**Opción On-Premise (Servidor Dedicado):**

| Recurso | Especificación | Costo Una Vez (USD) |
|---------|----------------|---------------------|
| **Servidor** | Dell PowerEdge (16 cores, 64GB RAM, 2TB SSD) | $4,000 |
| **Backup/NAS** | Synology 4-bay | $800 |
| **UPS** | APC 2200VA | $400 |
| **TOTAL** | | **~$5,200** |

**Mantenimiento anual:** ~$1,000

**Recomendación:** Cloud para flexibilidad y menor inversión inicial. On-premise si hay restricciones de seguridad/regulatorias.

---

## CONCLUSIONES

### Puntos Clave

1. ✅ **Arquitectura Monolítica Modular** es la mejor opción para iniciar (no microservicios)
2. ✅ **Nomenclatura de BD en inglés con snake_case** mejorará mantenibilidad
3. ✅ **Stack Java/Spring Boot + React + PostgreSQL** es sólido y tiene talento disponible
4. ✅ **Migración Big Bang con preparación exhaustiva** minimiza riesgos
5. ✅ **MVP de 6-9 meses** es realista para módulos críticos (Taller, Inventario, Compras)
6. ✅ **Seguridad, observabilidad y CI/CD desde día 1** evitará deuda técnica futura

### Próximos Pasos

1. Aprobar stack tecnológico y arquitectura
2. Conformar equipo de desarrollo
3. Configurar infraestructura y ambientes
4. Iniciar Fase 1: Desarrollo MVP
5. Iterar con usuarios durante desarrollo (validación temprana)

---

**FIN DEL DOCUMENTO**
