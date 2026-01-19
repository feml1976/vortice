# PROMPT MASTER PARA DESARROLLO CON IA
## Sistema TRANSER Modernizado - Guía Completa para Desarrollo Asistido por IA

**Versión:** 1.0
**Fecha:** 31 de Diciembre de 2025
**Compatible con:** Claude (Anthropic), ChatGPT (OpenAI), GitHub Copilot

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Context Setting Base](#2-context-setting-base)
3. [Prompts por Fase de Desarrollo](#3-prompts-por-fase-de-desarrollo)
4. [Prompts por Módulo](#4-prompts-por-módulo)
5. [Mejores Prácticas](#5-mejores-prácticas)
6. [Troubleshooting](#6-troubleshooting)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento proporciona **prompts optimizados** para desarrollar el sistema TRANSER modernizado con asistencia de IA (Claude, ChatGPT, Copilot). Los prompts están diseñados para:

- ✅ Maximizar la calidad del código generado
- ✅ Mantener consistencia arquitectónica
- ✅ Acelerar el desarrollo sin comprometer calidad
- ✅ Facilitar aprendizaje del equipo

### 1.2 Cómo Usar Este Documento

1. **Antes de iniciar desarrollo:** Establecer contexto base (sección 2)
2. **Durante desarrollo:** Usar prompts específicos por fase (sección 3)
3. **Por módulo:** Usar prompts especializados (sección 4)
4. **Iteración:** Refinar outputs con prompts de seguimiento

---

## 2. CONTEXT SETTING BASE

### 2.1 Prompt Inicial (Copiar al inicio de cada sesión)

```markdown
# CONTEXTO DEL PROYECTO: Sistema de Informacion Milenio Operativo TRANSER MODERNIZADO

## Sobre el Proyecto
Estoy desarrollando la modernización de nuestro Sistema de Informacion Milenio Operativo, un ERP para empresa de transporte de carga en Colombia.
El sistema actual está en Oracle Forms 6i (obsoleto) y lo estamos migrando a stack moderno.

## Stack Tecnológico
- **Backend:** Java 21 + Spring Boot 3.2
- **Frontend:** React 18 + TypeScript + Material-UI
- **Base de Datos:** PostgreSQL 16
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
  - Workshop (Taller)
  - Inventory (Inventarios)
  - Purchasing (Compras)
  - Fleet (Flota)
  - HR (Recursos Humanos)

## Convenciones de Código

### Backend (Java)
- Nomenclatura: camelCase para variables/métodos, PascalCase para clases
- Paquetes: com.transer.[module].[layer]
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
# TAREA: Diseñar modelo de dominio para [MÓDULO]

## Contexto
[Descripción del módulo y sus responsabilidades]

## Requerimientos Funcionales
[Listar RFs relevantes del documento de requerimientos]

## Petición
Diseña el modelo de dominio siguiendo estos pasos:

1. **Identificar Aggregates:**
   - ¿Cuáles son las entidades raíz (Aggregate Roots)?
   - ¿Qué entidades son parte de cada aggregate?
   - Define los límites de cada aggregate (qué modificaciones deben hacerse juntas)

2. **Identificar Value Objects:**
   - ¿Qué conceptos son Value Objects (sin identidad, inmutables)?
   - Ejemplos: Money, Address, DateRange, etc.

3. **Relaciones:**
   - ¿Cómo se relacionan los aggregates entre sí?
   - ¿Qué relaciones son por ID y cuáles permiten navegación directa?

4. **Reglas de Negocio:**
   - ¿Qué invariantes debe mantener cada aggregate?
   - ¿Qué validaciones son necesarias?

5. **Domain Events:**
   - ¿Qué eventos de dominio se deben publicar?
   - ¿Qué otros módulos podrían estar interesados en estos eventos?

Por favor, entrega:
- Diagrama de clases (en formato textual Mermaid o PlantUML)
- Listado de Aggregates con sus responsabilidades
- Listado de Value Objects
- Listado de Domain Events
```

**Ejemplo de uso:**

```markdown
# TAREA: Diseñar modelo de dominio para Workshop (Taller)

## Contexto
El módulo Workshop gestiona las órdenes de trabajo (Work Orders) del taller de mantenimiento.
Incluye creación de OTs desde reportes de falla, asignación de mecánicos, gestión de tareas,
solicitud de repuestos, y cierre de trabajos.

## Requerimientos Funcionales
- RF-007: Creación de OT desde reporte de falla, manualmente, o desde rutina preventiva
- RF-008: Asignación de recursos (mecánico, isla de trabajo)
- RF-009: Estados de OT (Creada, Asignada, En Progreso, Pausada, Cerrada, Anulada)
- RF-010: Gestión de tareas dentro de OT
- RF-011: Solicitud de repuestos desde OT
- RF-012: Control de tiempo en OT
- RF-013: Validación y cierre de OT

## Petición
[Seguir el prompt de arriba]
```

#### 3.1.2 Diseño de API REST

```markdown
# TAREA: Diseñar API REST para [ENTIDAD/MÓDULO]

## Contexto
[Descripción de la entidad y operaciones requeridas]

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

**Ejemplo de uso:**

```markdown
# TAREA: Diseñar API REST para Work Orders

## Contexto
Las Work Orders (Órdenes de Trabajo) son el core del módulo Workshop.
Necesitamos operaciones CRUD completas, más operaciones específicas como asignación de mecánico,
cambio de estado, agregación de tareas, etc.

## Actores y Permisos
- **Coordinador de Taller:** Puede crear, asignar, cerrar OTs
- **Mecánico:** Puede ver OTs asignadas a él, actualizar progreso, solicitar repuestos
- **Inspector:** Puede validar trabajos antes de cierre
- **Gerente:** Solo lectura de todas las OTs

## Petición
[Seguir el prompt de arriba]
```

### 3.2 FASE: Implementación Backend

#### 3.2.1 Generar Entity (JPA)

```markdown
# TAREA: Generar JPA Entity para [ENTIDAD]

## Contexto del Dominio
[Descripción de la entidad y su propósito]

## Estructura de Tabla (PostgreSQL)
```sql
[Pegar el DDL de la tabla]
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
```

**Ejemplo de uso:**

```markdown
# TAREA: Generar unit tests para CreateWorkOrderUseCase

## Código de la Clase
```java
@Service
@RequiredArgsConstructor
public class CreateWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final CodeGeneratorService codeGenerator;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public UUID execute(CreateWorkOrderCommand command) {
        // Validar vehículo existe
        Vehicle vehicle = vehicleRepository.findById(command.vehicleId())
            .orElseThrow(() -> new VehicleNotFoundException(command.vehicleId()));

        // Validar mecánico existe
        User mechanic = userRepository.findById(command.mechanicId())
            .orElseThrow(() -> new UserNotFoundException(command.mechanicId()));

        // Generar código único
        String code = codeGenerator.generateWorkOrderCode();

        // Crear work order
        WorkOrder workOrder = WorkOrder.builder()
            .code(code)
            .vehicle(vehicle)
            .leadMechanic(mechanic)
            .description(command.description())
            .priority(command.priority())
            .status(WorkOrderStatus.CREATED)
            .build();

        workOrder = workOrderRepository.save(workOrder);

        // Publicar evento
        eventPublisher.publish(new WorkOrderCreatedEvent(workOrder.getId()));

        return workOrder.getId();
    }
}
```

## Petición
[Seguir el prompt de arriba]

Específicamente, genera tests para:
1. Caso exitoso: vehículo y mecánico existen, se crea OT correctamente
2. Error: vehículo no existe
3. Error: mecánico no existe
4. Verificar que se publica evento WorkOrderCreatedEvent
```

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

### 3.5 FASE: Refactoring y Optimización

#### 3.5.1 Revisar y Optimizar Código

```markdown
# TAREA: Revisar y optimizar código

## Código Actual
```java
[Pegar código a revisar]
```

## Petición
Analiza este código y sugiere mejoras en:

### Áreas de revisión:
1. **SOLID Principles:** ¿Se violan principios? ¿Cómo refactorizar?
2. **Performance:**
   - ¿Hay queries N+1?
   - ¿Se pueden optimizar loops?
   - ¿Hay cálculos que se pueden cachear?
3. **Seguridad:** ¿Hay vulnerabilidades (SQL injection, XSS, etc.)?
4. **Legibilidad:** ¿El código es fácil de entender? ¿Nombres descriptivos?
5. **Testabilidad:** ¿Es fácil de testear? ¿Demasiadas dependencias?
6. **Error Handling:** ¿Manejo de errores apropiado?
7. **Best Practices:** ¿Sigue convenciones de Java/Spring?

### Formato de entrega:
- Listado de problemas encontrados (priorizados por severidad)
- Código refactorizado con comentarios explicando los cambios
- Explicación del impacto de cada cambio
```

---

## 4. PROMPTS POR MÓDULO

### 4.1 Módulo: Workshop (Taller)

#### Prompt para iniciar desarrollo del módulo completo:

```markdown
# PROYECTO: Módulo Workshop (Taller) - Sistema TRANSER

## Contexto
Voy a desarrollar el módulo de Workshop para el sistema TRANSER. Este módulo es el core del MVP
y gestiona todo el ciclo de vida de las órdenes de trabajo en el taller de mantenimiento.

## Requerimientos Funcionales (Resumen)
1. **Work Orders (Órdenes de Trabajo):**
   - Crear desde reporte de falla, rutina preventiva, o manual
   - Asignar mecánico, isla de trabajo, prioridad
   - Gestionar estados (Creada, Asignada, En Progreso, Pausada, Cerrada, Anulada)
   - Agregar tareas a la OT
   - Solicitar repuestos
   - Control de tiempos (estimado vs real, downtime por repuestos)
   - Cierre con validación
2. **Failure Reports (Reportes de Falla):**
   - Crear reporte por conductor, mecánico, inspector, sistema
   - Clasificar por sistema afectado, urgencia
   - Flujo de aprobación
3. **Maintenance Routines (Rutinas de Mantenimiento Preventivo):**
   - Crear rutinas normales y secuenciales
   - Definir periodicidad (km, tiempo, horas)
   - Generar OT automáticamente
4. **Mechanics & Labor (Mecánicos y Mano de Obra):**
   - Registro de mecánicos con especialidades
   - Registro de tiempo en OT
   - Cálculo de costos de mano de obra

## Entidades Principales
- WorkOrder
- FailureReport
- MaintenanceRoutine
- Task
- Mechanic (o User con rol)
- Island (Isla de trabajo)

## Petición
Quiero que me asistas en el desarrollo de este módulo paso a paso.
Empecemos por:

1. **Validar el modelo de dominio:** ¿El listado de entidades es completo? ¿Sugiere agregar/quitar alguna?
2. **Definir el orden de implementación:** ¿En qué orden deberíamos implementar las entidades y casos de uso?
3. **Identificar dependencias:** ¿Qué otros módulos necesitamos (aunque sea stubs) para desarrollar Workshop?

Una vez acordemos esto, procederemos con la implementación entidad por entidad.
```

### 4.2 Módulo: Inventory (Inventarios)

```markdown
# PROYECTO: Módulo Inventory (Inventarios) - Sistema TRANSER

## Contexto
El módulo de Inventory gestiona 4 tipos de inventarios:
1. **Nuevos:** Repuestos comprados nuevos
2. **Consignación:** Repuestos de proveedor en nuestras instalaciones (no son propiedad hasta consumo)
3. **Reparados:** Componentes reparados (internamente o externamente)
4. **Segundas:** Repuestos recuperados (de vehículos dados de baja, devoluciones de OT)

## Requerimientos Funcionales (Resumen)
1. **Gestión de Ítems:**
   - CRUD de ítems (repuestos, herramientas, EPPs, insumos)
   - Ítems serializados vs no serializados
   - Equivalencias entre ítems
   - Matriz de control (Min/Max por almacén)
2. **Entradas de Almacén:**
   - Por compras
   - Por reparación
   - Por consignación
   - Por garantía
   - Por devolución de OT
   - Por traslado entre almacenes
3. **Salidas de Almacén:**
   - A Orden de Trabajo
   - A funcionario (dotación, herramientas)
   - A compras (para garantía)
   - Por baja
   - Por traslado
4. **Movimientos:**
   - Trazabilidad completa de cada movimiento
   - Cálculo de duración de componentes serializados
   - Rotación de inventarios

## Entidades Principales
- Item
- Warehouse
- InventoryMovement
- InventoryLevel (stock por almacén y tipo)
- Consignment

## Petición
Asísteme en el desarrollo de este módulo. Empecemos por definir el modelo de dominio completo.
```

### 4.3 Módulo: Purchasing (Compras)

```markdown
# PROYECTO: Módulo Purchasing (Compras) - Sistema TRANSER

## Contexto
El módulo de Purchasing gestiona el proceso completo de compras en 12 etapas:
1. Identificación de necesidad (requisición)
2. Consolidación de requisiciones
3. Invitación a cotizar
4. Recepción de cotizaciones (portal proveedores)
5. Análisis de cotizaciones
6. Adjudicación
7. Aceptación de OC por proveedor
8. Programación de entrega
9. Recepción en portería
10. Validación en compras
11. Entrega a almacén
12. Registro contable y pago

## Requerimientos Funcionales (Resumen)
1. **Proveedores:**
   - Registro y gestión de proveedores
   - Portal de proveedores (cotizar, ver OCs, subir facturas)
   - Calificación automática de proveedores (4 criterios)
2. **Proceso de Compras:**
   - Requisiciones automáticas (inventario < Min) y manuales
   - Consolidación y generación de solicitudes de cotización
   - Recepción y análisis de cotizaciones
   - Pre-selección automática por mejor precio/plazo
   - Generación de Órdenes de Compra
   - Seguimiento de entregas
3. **Consignación:**
   - Creación de acuerdos de consignación
   - Cortes automáticos de inventario
   - Generación de Pre-Facturas
   - Relación Pre-Factura → Factura
4. **Garantías:**
   - Solicitud de garantía a proveedor
   - Seguimiento y aprobación/rechazo
   - Impacto en calificación de proveedor

## Entidades Principales
- Supplier
- PurchaseRequisition
- QuotationRequest
- Quotation
- PurchaseOrder
- Consignment
- WarrantyRequest

## Petición
Asísteme en el desarrollo de este módulo. Es complejo por las múltiples etapas.
Empecemos por definir cómo modelar el flujo de estados de una Purchase Order.
```

---

## 5. MEJORES PRÁCTICAS

### 5.1 Cómo Interactuar con la IA

✅ **DO:**
- **Sé específico:** Cuanto más detalle, mejor output
- **Proporciona contexto:** Siempre incluye el contexto base al inicio de la sesión
- **Itera:** Si el primer output no es perfecto, refínalo con prompts de seguimiento
- **Pide explicaciones:** Pregunta "¿Por qué elegiste este enfoque?" para aprender
- **Valida:** No confíes ciegamente, revisa el código generado

❌ **DON'T:**
- **No pidas "código completo de todo el módulo":** La IA no puede generar 5000 líneas de código coherente de una vez
- **No omitas contexto:** Sin contexto, la IA asume defaults que pueden no ser apropiados
- **No copies/pegues sin entender:** Debes entender el código generado
- **No esperes perfección:** El código generado es un 80%, tú completas el 20%

### 5.2 Prompts de Seguimiento Útiles

**Para refinar código:**
```markdown
El código generado está bien, pero:
1. [Problema/Mejora específica]
2. Por favor, refactoriza considerando [principio/patrón]
```

**Para agregar funcionalidad:**
```markdown
Al código anterior, agrega:
- [Nueva funcionalidad]
Manteniendo la estructura y estilo existente.
```

**Para optimizar:**
```markdown
Analiza este código en términos de performance:
- ¿Hay queries N+1?
- ¿Se pueden optimizar loops?
- ¿Qué cambios sugieres?
```

**Para testing:**
```markdown
Genera tests para este código cubriendo:
- Happy path
- Casos límite: [especificar]
- Errores esperados: [especificar]
```

### 5.3 Gestión de Sesiones Largas

**Problema:** Las IAs tienen límite de contexto. En sesiones largas, "olvidan" el inicio.

**Solución:**
1. **Resúmenes periódicos:** Cada 10-15 prompts, pide a la IA:
   ```markdown
   Resume lo que hemos hecho hasta ahora en esta sesión.
   ```
2. **Nueva sesión con resumen:** Si necesitas continuar en nueva sesión:
   ```markdown
   Retomando sesión anterior. Contexto:
   [Pegar resumen de sesión anterior]
   [Pegar context setting base]

   Continúo con: [siguiente tarea]
   ```
3. **Guardar prompts y outputs:** Mantén un log de prompts importantes y sus outputs en documentos locales.

### 5.4 Trabajo en Equipo con IA

**Para mantener consistencia entre developers:**

1. **Shared Prompt Library:** Guardar prompts exitosos en repositorio del proyecto (ej: `/docs/prompts/`)
2. **Code Reviews de Código Generado por IA:** Tratar código de IA igual que código humano (peer review)
3. **Estandarizar Context Setting:** Todo el equipo usa el mismo context setting base
4. **Compartir Learnings:** Si alguien encuentra un prompt que genera código excelente, compartirlo con el equipo

---

## 6. TROUBLESHOOTING

### 6.1 La IA No Genera Código que Compila

**Causas comunes:**
- Contexto insuficiente
- Sintaxis de lenguaje incorrecta en el prompt
- IA asume imports/dependencias que no están disponibles

**Solución:**
```markdown
El código generado tiene error de compilación:
[Pegar error]

Por favor, corrige el código asegurando:
1. Imports correctos
2. Sintaxis válida de [Java/TypeScript]
3. Dependencias disponibles en nuestro proyecto: [listar dependencias]
```

### 6.2 La IA Genera Código que No Sigue Convenciones

**Solución:**
```markdown
El código generado no sigue nuestras convenciones. Por favor, refactoriza siguiendo:
- [Convención violada 1]
- [Convención violada 2]

Referencia nuestras convenciones:
[Pegar sección de convenciones del context setting]
```

### 6.3 La IA Sugiere Arquitectura Diferente

**Ejemplo:** La IA sugiere usar Controllers directamente accediendo a Repositories (sin Services).

**Solución:**
```markdown
Aprecio la sugerencia, pero en nuestro proyecto usamos Clean Architecture con capas bien definidas:
Presentation → Application → Domain → Infrastructure

Por favor, regenera el código siguiendo esta arquitectura.
Controllers NO deben acceder a Repositories directamente, deben usar Services/UseCases.
```

### 6.4 Código Demasiado Genérico o Demasiado Específico

**Problema:** IA genera código muy abstracto (difícil de entender) o muy específico (no reutilizable).

**Solución:**
```markdown
El código está demasiado [genérico/específico].
Por favor, ajusta el nivel de abstracción:
- Para genérico: Dame ejemplos concretos de uso
- Para específico: Generaliza para que sea reutilizable en [contexto más amplio]
```

---

## 7. ANEXO: EJEMPLO COMPLETO END-TO-END

### Escenario: Implementar CRUD de Work Orders

#### Paso 1: Establecer Contexto

```markdown
[Pegar Context Setting Base de sección 2.1]
```

#### Paso 2: Diseñar Modelo de Dominio

```markdown
# TAREA: Diseñar modelo de dominio para Work Orders

## Requerimientos Funcionales
- RF-007: Creación de OT
- RF-008: Asignación de mecánico e isla
- RF-009: Gestión de estados
- RF-010: Gestión de tareas
- RF-011: Solicitud de repuestos
- RF-012: Control de tiempos
- RF-013: Cierre de OT

[Continuar con prompt completo de sección 3.1.1]
```

**Output esperado:** Diagrama de clases, aggregates, value objects, domain events.

#### Paso 3: Diseñar API

```markdown
# TAREA: Diseñar API REST para Work Orders

[Continuar con prompt de sección 3.1.2]
```

**Output esperado:** Tabla de endpoints con request/response.

#### Paso 4: Implementar Entity

```markdown
# TAREA: Generar JPA Entity para Work Order

[Continuar con prompt de sección 3.2.1, incluir DDL]
```

**Output esperado:** Clase `WorkOrder.java`.

#### Paso 5: Implementar Repository

```markdown
# TAREA: Generar Repository para Work Order

## Consultas Requeridas
- Buscar por estado
- Buscar por vehículo
- Buscar por mecánico
- Buscar activas (no cerradas ni canceladas)
- Contar por estado

[Continuar con prompt de sección 3.2.2]
```

**Output esperado:** Interface `WorkOrderRepository.java`.

#### Paso 6: Implementar Service

```markdown
# TAREA: Generar Application Service para Crear Work Order

[Continuar con prompt de sección 3.2.3]
```

**Output esperado:** Clase `CreateWorkOrderUseCase.java` + DTOs.

#### Paso 7: Implementar Controller

```markdown
# TAREA: Generar REST Controller para Work Orders

[Continuar con prompt de sección 3.2.4]
```

**Output esperado:** Clase `WorkOrderController.java`.

#### Paso 8: Implementar Tests

```markdown
# TAREA: Generar unit tests para CreateWorkOrderUseCase

[Continuar con prompt de sección 3.4.1]
```

**Output esperado:** Clase `CreateWorkOrderUseCaseTest.java`.

#### Paso 9: Implementar Frontend

**a) Types:**
```markdown
# TAREA: Generar TypeScript types para Work Order

[Continuar con prompt de sección 3.3.1]
```

**b) Custom Hook:**
```markdown
# TAREA: Generar custom hook para listar Work Orders

[Continuar con prompt de sección 3.3.3]
```

**c) Component:**
```markdown
# TAREA: Generar componente React para Listado de Work Orders

[Continuar con prompt de sección 3.3.2]
```

#### Paso 10: Code Review

```markdown
# TAREA: Revisar código completo de Work Orders

Por favor, revisa todo el código generado (backend + frontend) y sugiere mejoras en:
- Arquitectura
- Performance
- Seguridad
- Legibilidad
- Testabilidad

[Pegar código relevante si es necesario]
```

---

## CONCLUSIÓN

Este prompt master es una guía viva. A medida que el equipo gana experiencia usando IA, se deben:

1. **Refinar prompts:** Mejorar los que no dan buenos resultados
2. **Agregar nuevos prompts:** Para casos de uso no cubiertos
3. **Compartir aprendizajes:** Documentar qué funciona y qué no
4. **Iterar continuamente:** La IA mejora constantemente, los prompts deben evolucionar

**Recuerda:** La IA es una herramienta poderosa, pero TÚ eres el desarrollador. La IA acelera, no reemplaza tu criterio y experiencia.

---

**FIN DEL DOCUMENTO**
