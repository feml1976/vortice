# 🚀 PROMPT MASTER PARA CLAUDE CODE
## Continuación Desarrollo Sistema TRANSER - Vórtice Modernizado

---

## 📖 INSTRUCCIONES DE CARGA DE CONTEXTO

**PASO 1: Cargar contexto del proyecto**
```
Lee y analiza los siguientes archivos que contienen el contexto completo del proyecto:

1. Archivo: C:\Dirsop\Proyectos\vortice\CLAUDE.md
   - Contiene: Roles, tecnologías, comandos, guías de estilo, flujo de modernización
   - Propósito: Entender el contexto general del proyecto de migración

2. Archivo: C:\Dirsop\Proyectos\vortice\docs\PROMPT_MASTER.md
   - Contiene: Requerimientos funcionales detallados, stack tecnológico, prompts por fase
   - Propósito: Conocer las especificaciones técnicas y arquitectura del sistema

Por favor confirma que has leído ambos archivos antes de continuar.
```

---

## 🎯 ESTADO ACTUAL DEL PROYECTO

### ✅ Componentes YA Implementados

**Backend (Java 21 + Spring Boot 3.5):**
- ✓ Módulo de Autenticación (Auth)
- ✓ Módulo de Usuarios (Users)
- ✓ Estructura base del proyecto siguiendo Clean Architecture
- ✓ Configuración de Spring Boot y dependencias Maven

**Frontend (React 18 + TypeScript):**
- ✓ Formularios para consumo de endpoints de Auth
- ✓ Formularios para consumo de endpoints de Usuarios
- ✓ Configuración base de Vite y dependencias

**Infraestructura:**
- ✓ Base de datos PostgreSQL 18 configurada
- ✓ Scripts de creación de tablas base (audit, users, roles)

### 📍 Ubicación de Archivos
- **Raíz del proyecto:** `C:\Dirsop\Proyectos\vortice`
- **Backend:** `C:\Dirsop\Proyectos\vortice\backend` (asumir si no se especifica)
- **Frontend:** `C:\Dirsop\Proyectos\vortice\frontend` (asumir si no se especifica)
- **Documentación:** `C:\Dirsop\Proyectos\vortice\docs`

---

## 🔄 OBJETIVO: CONTINUACIÓN DEL DESARROLLO

### ⚠️ RESTRICCIÓN CRÍTICA
**NO REINICIAR EL PROYECTO DESDE CERO**

Debes:
- ✅ Analizar el código existente en las carpetas backend/frontend
- ✅ Identificar patrones y convenciones ya establecidos
- ✅ Reutilizar componentes, servicios y estructuras existentes
- ✅ Mantener consistencia con lo implementado

### 🎯 Enfoque de Continuación

**ANTES de generar cualquier código nuevo:**

1. **Explorar estructura existente:**
   ```bash
   # Listar estructura del backend
   tree backend/src -L 4
   
   # Listar estructura del frontend
   tree frontend/src -L 3
   ```

2. **Identificar patrones establecidos:**
   - ¿Cómo están organizados los paquetes/carpetas?
   - ¿Qué naming conventions se están usando?
   - ¿Qué librerías/frameworks están configurados?
   - ¿Cómo se estructuran los DTOs, Services, Controllers?

3. **Confirmar entendimiento:**
   ```
   Antes de proceder, confirma:
   - Estructura de carpetas identificada
   - Patrones de código detectados
   - Próximo módulo a desarrollar
   ```

---

## 📋 PRÓXIMOS MÓDULO A DESARROLLAR

Según el contexto del proyecto (CLAUDE.md), los módulos principales son:

### Orden Sugerido de Implementación:
1. **Workshop (Taller)** - Módulo crítico
   - Work Orders (Órdenes de Trabajo)
   - Tareas de mantenimiento
   - Asignación de mecánicos
   - Control de tiempos

2. **Inventory (Inventarios)**
   - Gestión de repuestos
   - Control de stock
   - Movimientos de inventario
   - Alertas de stock mínimo

3. **Purchasing (Compras)**
   - Solicitudes de compra
   - Órdenes de compra
   - Recepción de mercancía
   - Integración con proveedores

4. **Fleet (Flota)**
   - Gestión de vehículos
   - Documentación de vehículos
   - Asignación de conductores
   - Historial de mantenimientos

5. **HR (Recursos Humanos)**
   - Gestión de empleados
   - Mecánicos y conductores
   - Asignaciones de turnos
   - Capacitaciones

### 🎯 Desarrollo Iterativo por Módulo

Para cada módulo, seguir este flujo:

**Fase 1: Análisis y Diseño**
```markdown
Para el módulo [NOMBRE_MÓDULO]:

1. Diseña el modelo de dominio:
   - Identifica Aggregates y Value Objects
   - Define relaciones entre entidades
   - Establece reglas de negocio
   - Propone Domain Events

2. Diseña el esquema de base de datos:
   - Scripts DDL siguiendo convenciones (snake_case, timestamps, audit)
   - Índices necesarios
   - Constraints y foreign keys
   - Trigger de auditoría si aplica

3. Diseña la API REST:
   - Endpoints siguiendo RESTful best practices
   - DTOs de request/response
   - Códigos de estado HTTP
   - Documentación OpenAPI/Swagger
```

**Fase 2: Implementación Backend**
```markdown
Genera el código Java siguiendo la estructura existente:

1. Domain Layer:
   - Entities (JPA con anotaciones Jakarta)
   - Value Objects (records inmutables)
   - Domain Events (si aplica)
   - Especificaciones/Validaciones

2. Application Layer:
   - DTOs (preferir records)
   - Services (lógica de negocio)
   - Mappers (MapStruct o manual)
   - Validators (Jakarta Validation)

3. Infrastructure Layer:
   - Repositories (Spring Data JPA)
   - Configuraciones
   - Adapters externos

4. Presentation Layer:
   - Controllers REST
   - Exception Handlers
   - API Documentation (Swagger annotations)

Reutilizar:
- Clases base de auditoría
- Exception handlers existentes
- Configuraciones de seguridad
- Patterns de DTOs y Services
```

**Fase 3: Implementación Frontend**
```markdown
Genera componentes React siguiendo la estructura existente:

1. Hooks personalizados:
   - `use[Entidad]` para fetching (React Query)
   - `use[Entidad]Form` para formularios
   - Reutilizar patterns de hooks de Auth/Users

2. Componentes:
   - Listados con DataGrid/Table (MUI)
   - Formularios (Formik + Yup validation)
   - Modales de confirmación
   - Componentes de detalle

3. Páginas:
   - Estructura de rutas
   - Layouts consistentes
   - Navegación

4. Servicios:
   - API clients (Axios)
   - Interceptors para auth
   - Error handling

Reutilizar:
- Componentes comunes (Layout, Navbar, etc.)
- Hooks de autenticación
- Servicios HTTP base
- Theme y estilos MUI
```

**Fase 4: Testing**
```markdown
Genera tests siguiendo patterns existentes:

Backend:
- Unit tests (JUnit 5 + Mockito)
- Integration tests (Spring Boot Test)
- Repositorio tests (DataJpaTest)

Frontend:
- Component tests (React Testing Library)
- Hook tests
- Integration tests (Cypress o Playwright)
```

---

## 🎨 CONVENCIONES OBLIGATORIAS

### Backend (Java)
```java
// ✅ CORRECTO: Constructor injection
@Service
@RequiredArgsConstructor
public class WorkOrderService {
    private final WorkOrderRepository repository;
    private final WorkOrderMapper mapper;
}

// ❌ INCORRECTO: Field injection
@Service
public class WorkOrderService {
    @Autowired
    private WorkOrderRepository repository;
}

// ✅ CORRECTO: DTO con record
public record CreateWorkOrderRequest(
    @NotNull UUID vehicleId,
    @NotBlank String description,
    Priority priority
) {}

// ❌ INCORRECTO: Exponer entidad en API
@GetMapping("/{id}")
public WorkOrder getWorkOrder(@PathVariable UUID id) { }
```

### Frontend (TypeScript/React)
```typescript
// ✅ CORRECTO: Function component con types
interface WorkOrderListProps {
  vehicleId?: string;
  onSelect?: (id: string) => void;
}

export const WorkOrderList: React.FC<WorkOrderListProps> = ({ 
  vehicleId, 
  onSelect 
}) => {
  const { data, isLoading } = useWorkOrders({ vehicleId });
  // ...
}

// ❌ INCORRECTO: Class component
class WorkOrderList extends React.Component { }

// ✅ CORRECTO: Custom hook con React Query
export const useWorkOrders = (filters?: WorkOrderFilters) => {
  return useQuery({
    queryKey: ['workOrders', filters],
    queryFn: () => workOrderApi.getAll(filters)
  });
}
```

### Base de Datos (PostgreSQL)
```sql
-- ✅ CORRECTO: Nomenclatura y estructura
CREATE TABLE work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by BIGINT REFERENCES users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by BIGINT REFERENCES users(id)
);

-- ❌ INCORRECTO: camelCase, sin auditoría
CREATE TABLE WorkOrders (
    Id SERIAL PRIMARY KEY,
    VehicleId INT NOT NULL
);
```

---

## 🔍 FLUJO DE TRABAJO RECOMENDADO

### Para Cada Nueva Tarea:

1. **Confirmar contexto:**
   ```
   Estoy por implementar [FUNCIONALIDAD].
   He revisado el código existente en [RUTA].
   Identifico que debo seguir el patrón [PATRÓN_DETECTADO].
   ¿Procedo?
   ```

2. **Generar código incremental:**
   - Un archivo/clase a la vez
   - Explicar decisiones de diseño
   - Mostrar cómo se integra con código existente

3. **Validar antes de continuar:**
   ```
   He generado [ARCHIVO].
   ¿Deseas revisar antes de continuar con el siguiente componente?
   ```

4. **Testing junto con implementación:**
   - No dejar testing para el final
   - Generar test unitario junto con cada servicio/componente

---

## 🚨 REGLAS DE ORO

### ❌ NUNCA:
1. Sobrescribir archivos existentes sin confirmar
2. Cambiar convenciones ya establecidas
3. Generar código completo de un módulo de una sola vez (incremental es mejor)
4. Ignorar el código existente y "empezar de nuevo"
5. Usar `@Autowired` en campos (usar constructor injection)
6. Exponer entidades JPA en DTOs de respuesta
7. Crear Pull Requests sin pruebas

### ✅ SIEMPRE:
1. Analizar código existente antes de generar nuevo código
2. Mantener consistencia con patrones establecidos
3. Preguntar si hay dudas sobre decisiones de diseño
4. Generar tests junto con código de producción
5. Documentar decisiones importantes con comentarios
6. Seguir principios SOLID y Clean Architecture
7. Validar inputs y manejar errores apropiadamente

---

## 📝 PLANTILLA DE INICIO DE SESIÓN

**Copia y pega esto al iniciar una nueva sesión con Claude Code:**

```markdown
# CONTEXTO DE CONTINUACIÓN - Sistema TRANSER Vórtice

## Archivos de Contexto
He leído los siguientes archivos:
- [ ] C:\Dirsop\Proyectos\vortice\CLAUDE.md
- [ ] C:\Dirsop\Proyectos\vortice\docs\PROMPT_MASTER.md

## Estado Actual
Módulos implementados:
- ✅ Auth (Backend + Frontend)
- ✅ Users (Backend + Frontend)

Próximo módulo a trabajar: [ESPECIFICAR]

## Solicitud
[DESCRIBE LO QUE NECESITAS IMPLEMENTAR]

Recuerda:
- NO reiniciar desde cero
- Analizar código existente primero
- Mantener patrones establecidos
- Desarrollo incremental (paso a paso)
```

---

## 🎯 EJEMPLO DE USO

### Escenario: Implementar módulo Workshop

```markdown
# CONTEXTO DE CONTINUACIÓN - Sistema TRANSER Vórtice

## Archivos de Contexto
He leído:
- ✅ C:\Dirsop\Proyectos\vortice\CLAUDE.md
- ✅ C:\Dirsop\Proyectos\vortice\docs\PROMPT_MASTER.md

## Estado Actual
Módulos implementados:
- ✅ Auth (Backend + Frontend)
- ✅ Users (Backend + Frontend)

Próximo módulo: Workshop (Taller)

## Solicitud
Necesito implementar el módulo Workshop para gestión de órdenes de trabajo.

### Paso 1: Análisis
Por favor, primero:
1. Explora la estructura actual del backend en src/main/java
2. Identifica cómo están organizados los módulos Auth y Users
3. Detecta patrones de DTOs, Services, Controllers, Repositories
4. Confirma el patrón antes de proceder

### Paso 2: Diseño
Una vez confirmado el patrón:
1. Diseña el modelo de dominio para Work Orders
2. Propón el esquema de base de datos (DDL)
3. Diseña los endpoints REST principales

### Paso 3: Implementación (Incremental)
No generes todo de una vez. Vamos paso a paso:
1. Primero: Entidades de dominio
2. Segundo: DTOs
3. Tercero: Repository
4. Cuarto: Service
5. Quinto: Controller
6. Sexto: Tests

Después de cada paso, espera mi confirmación antes de continuar.

¿Entendido? Comencemos con el Paso 1: Análisis.
```

---

## 📚 RECURSOS DE REFERENCIA

### Documentación del Proyecto
- `C:\Dirsop\Proyectos\vortice\CLAUDE.md` - Contexto general
- `C:\Dirsop\Proyectos\vortice\docs\PROMPT_MASTER.md` - Especificaciones técnicas
- `C:\Dirsop\Proyectos\vortice\docs\Informe_Modernizacion_BD_Llantas.md` - Modelo datos
- `C:\Dirsop\Proyectos\vortice\Requerimientos\*` - Requerimientos funcionales

### Referencias de Código Existente
- Auth module: `backend/src/main/java/com/transer/vortice/auth`
- Users module: `backend/src/main/java/com/transer/vortice/users`
- Frontend Auth: `frontend/src/features/auth`
- Frontend Users: `frontend/src/features/users`

---

## ✅ CHECKLIST DE CALIDAD

Antes de considerar una tarea como "completa":

**Backend:**
- [ ] Código sigue Clean Architecture (capas bien separadas)
- [ ] DTOs no exponen entidades de dominio
- [ ] Validaciones implementadas (Jakarta Validation)
- [ ] Exception handling implementado
- [ ] Unit tests con >70% cobertura
- [ ] Integration tests para endpoints críticos
- [ ] Documentación OpenAPI/Swagger
- [ ] Logs apropiados (no secretos, nivel correcto)

**Frontend:**
- [ ] TypeScript strict mode sin errores
- [ ] Componentes funcionales (no class components)
- [ ] Props tipadas con interfaces
- [ ] React Query para fetching
- [ ] Manejo de loading/error states
- [ ] Formularios con validación (Formik + Yup)
- [ ] Responsive design (funciona en mobile)
- [ ] Tests de componentes principales

**Base de Datos:**
- [ ] Nomenclatura snake_case consistente
- [ ] Columnas de auditoría (created_at, updated_by, etc.)
- [ ] Soft delete implementado (deleted_at)
- [ ] Índices en foreign keys y campos de búsqueda
- [ ] Constraints apropiados
- [ ] Scripts de migración versionados

---

## 🔄 VERSIONADO DE PROMPTS

**Versión:** 1.0  
**Fecha:** 22 de Enero de 2026  
**Autor:** femon76  
**Propósito:** Prompt master para continuación de desarrollo con Claude Code  
**Compatible con:** Claude Code, Claude Sonnet 4.5

---

**¡ÉXITO EN EL DESARROLLO! 🚀**
