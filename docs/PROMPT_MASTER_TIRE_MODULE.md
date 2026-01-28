# 🎯 PROMPT MASTER - DESARROLLO MÓDULO TIRE (LLANTAS)
## Sistema TRANSER Vórtice - Desarrollo con Claude Code

**Versión:** 2.0  
**Fecha:** 26 de Enero de 2026  
**Módulo:** Tire Management (Gestión de Llantas)  
**Estado del Proyecto:** Continuación - Auth y Users ya implementados

---

## 📚 CONTEXTO DEL PROYECTO

Estoy desarrollando la modernización del Sistema TRANSER Vórtice, un ERP para empresa de transporte de carga en Colombia. El sistema actual está en Oracle Forms 6i (obsoleto) y lo estamos modernizando a un stack moderno.

### Stack Tecnológico
- **Backend:** Java 21 + Spring Boot 3.5
- **Frontend:** React 18 + TypeScript + Material-UI
- **Base de Datos:** PostgreSQL 18
- **Arquitectura:** Monolito modular con separación por bounded contexts (DDD)
- **Build:** Maven (backend), Vite (frontend)

### Arquitectura
- **Estilo:** Clean Architecture / Hexagonal
- **Capas:**
  1. Presentation (Controllers, DTOs)
  2. Application (Use Cases, Services)
  3. Domain (Entities, Value Objects, Business Rules)
  4. Infrastructure (JPA, External APIs, File System)

### Módulos del Sistema
- ✅ **Auth** (Autenticación) - IMPLEMENTADO
- ✅ **Users** (Usuarios) - IMPLEMENTADO
- 🔄 **Tire** (Llantas) - EN DESARROLLO
- ⏳ Share (Catálogos compartidos)
- ⏳ Purchasing (Compras)
- ⏳ Inventory (Inventarios)
- ⏳ Workshop (Taller)

---

## 🎯 OBJETIVO DE ESTE PROMPT

Necesito implementar el **Módulo Tire (Gestión de Llantas)** siguiendo los requerimientos funcionales documentados. Este módulo gestiona el ciclo de vida completo de neumáticos en una flota de vehículos de transporte.

### Referencias Documentales
- 📄 **Requerimientos Funcionales:** `Requerimiento_Llantas.md`
- 🗄️ **Modelo de Datos:** `Modelo_ER_Llantas.sql`
- 📋 **Contexto General:** `Contexto_General.md`
- 📋 **Contexto Específico:** `Contexto_Llantas.md`

---

## ⚠️ PROTOCOLO OBLIGATORIO: VALIDACIÓN DE COMPRENSIÓN

**🚫 NO INICIAR DESARROLLO SIN COMPLETAR ESTA FASE**

Antes de escribir **CUALQUIER** línea de código, debes:

### FASE 1: ANÁLISIS Y COMPRENSIÓN DEL REQUERIMIENTO

Cuando te indique un **Requerimiento Funcional específico** (por ejemplo: RF-001, RF-002, etc.), debes:

1. **LEER** el requerimiento completo desde `Requerimiento_Llantas.md`
2. **ANALIZAR** el requerimiento y responder las siguientes preguntas:

#### ❓ CUESTIONARIO DE VALIDACIÓN OBLIGATORIO

Para el requerimiento indicado, responde:

**A. COMPRENSIÓN DEL PROBLEMA DE NEGOCIO**
1. ¿Qué problema de negocio específico resuelve este requerimiento?
2. ¿Quiénes son los usuarios/actores involucrados?
3. ¿Cuál es el valor que aporta al negocio?

**B. ALCANCE FUNCIONAL**
4. ¿Cuáles son los casos de uso principales que debo implementar?
5. ¿Qué operaciones CRUD son necesarias? (Crear, Leer, Actualizar, Eliminar)
6. ¿Hay operaciones especiales o procesos de negocio complejos?

**C. REGLAS DE NEGOCIO**
7. Enumera TODAS las reglas de negocio explícitas del requerimiento
8. ¿Hay reglas de negocio implícitas que identificas?
9. ¿Qué validaciones son obligatorias?
10. ¿Qué validaciones podrían ser opcionales pero recomendables?

**D. MODELO DE DATOS**
11. ¿Qué tablas de la base de datos están involucradas?
12. ¿Qué relaciones entre entidades debo considerar?
13. ¿Hay campos calculados o derivados?

**E. ESTADOS Y TRANSICIONES**
14. ¿El requerimiento involucra estados? Si es sí, enuméralos
15. ¿Qué transiciones de estado son válidas?
16. ¿Qué transiciones están prohibidas?

**F. INVARIANTES DEL DOMINIO**
17. ¿Qué condiciones SIEMPRE deben ser verdaderas?
18. ¿Qué es irreversible una vez hecho?
19. ¿Qué restricciones temporales existen (fechas, orden de eventos)?

**G. EVENTOS Y EFECTOS SECUNDARIOS**
20. ¿Esta operación genera eventos de dominio?
21. ¿Hay efectos en cascada sobre otras entidades?
22. ¿Qué se debe registrar en auditoría?

**H. INTERFAZ DE USUARIO**
23. ¿Qué tipo de formulario se necesita? (Simple CRUD, Wizard, Master-Detail, etc.)
24. ¿Qué campos son obligatorios vs opcionales?
25. ¿Hay dependencias entre campos (ej: al seleccionar A, se habilita B)?

**I. RIESGOS E INCERTIDUMBRES**
26. ¿Hay información ambigua o faltante en el requerimiento?
27. ¿Qué supuestos debo hacer?
28. ¿Qué requiere confirmación explícita del Product Owner?

**J. INTEGRACIÓN CON MÓDULOS EXISTENTES**
29. ¿Este requerimiento depende de Auth o Users?
30. ¿Necesita permisos especiales?

**K. MULTI-SEDE (ARQUITECTURA MULTI-OFICINA)**
31. ¿Los datos de este requerimiento son globales (compartidos entre oficinas) o específicos por oficina?
32. Si son específicos por oficina, ¿cómo se determina a qué oficina pertenece un registro?
33. ¿Se necesita filtrado automático por oficina del usuario (Row-Level Security)?
34. ¿Hay traslados o movimientos entre oficinas? Si es sí, ¿qué restricciones existen?
35. ¿Los usuarios con ROLE_ADMIN_NATIONAL pueden ver datos de todas las oficinas?

---

### FASE 2: DISEÑO DE SOLUCIÓN

Una vez respondidas las preguntas anteriores, proporciona:

#### 📐 DISEÑO DE ARQUITECTURA

**1. Aggregate Root y Entidades**
- Identifica el Aggregate Root del dominio
- Lista entidades y Value Objects necesarios
- Define los límites del agregado

**2. Casos de Uso (Application Layer)**
- Lista los Use Cases que implementarás
- Para cada Use Case:
  - Nombre descriptivo
  - Flujo principal (pasos)
  - Precondiciones
  - Postcondiciones
  - Invariantes validadas

**3. API REST**
- Diseña los endpoints necesarios:
  ```
  [MÉTODO] /api/v1/tires/[recurso]
  Request: { ... }
  Response: { ... }
  Errores: 400, 404, 422, 500
  ```

**4. Modelo de Dominio (Diagrama)**
Genera un diagrama en formato Mermaid o texto que muestre:
- Entidades y sus relaciones
- Estados y transiciones
- Invariantes principales

---

### FASE 3: CONFIRMACIÓN EXPLÍCITA

**🛑 GATE DE CALIDAD - NO CONTINUAR SIN ESTE PASO**

Después de presentar tu análisis y diseño, debes:

1. Resumir los puntos clave del diseño
2. Identificar supuestos o áreas de riesgo
3. Preguntar explícitamente:

> **"¿Confirmas que el análisis y diseño presentado es correcto y puedo proceder con la implementación?"**

**SOLO** después de recibir confirmación explícita como:
- ✅ "Sí, procede"
- ✅ "Confirmado, adelante"
- ✅ "Correcto, implementa"

Puedes avanzar a la implementación.

---

## 🏗️ CONVENCIONES DE CÓDIGO

### Backend (Java)
- **Nomenclatura:** camelCase para variables/métodos, PascalCase para clases
- **Paquetes:** `com.transer.vortice.[module].[layer]`
- **No usar** `@Autowired` en campos, usar constructor injection
- **Preferir** records para DTOs inmutables
- **Lombok:** Solo @Getter, @Setter, @Builder (evitar @Data)
- **Validación:** Jakarta Validation (@NotNull, @NotBlank, @Valid)

### Frontend (TypeScript/React)
- **Nomenclatura:** camelCase para variables/funciones, PascalCase para componentes
- **Hooks personalizados:** prefijo `use`
- **Props:** interface, no type alias
- **Componentes:** function components (NO class components)
- **Estado global:** Zustand
- **Estado servidor:** React Query

### Base de Datos (PostgreSQL)
- **Esquemas:** `tire_management` (bounded context), `shared` (catálogos)
- **Tablas:** plural, snake_case (ej: `active_tires`)
- **Columnas:** singular, snake_case (ej: `created_at`)
- **Primary keys:** `id` (UUID para principales, BIGSERIAL para secundarias)
- **Foreign keys:** `[tabla_singular]_id` (ej: `tire_id`)
- **Timestamps:** `created_at`, `updated_at`, `deleted_at` (TIMESTAMP WITH TIME ZONE)
- **Auditoría:** `created_by`, `updated_by`, `deleted_by` (referencias a users)
- **Soft deletes:** `deleted_at IS NULL`
- **Boolean:** `is_[adjetivo]` (ej: `is_active`)

---

## 📋 ESTRUCTURA DEL PROYECTO

### Backend (Java)
```
src/main/java/com/transer/vortice/
├── auth/                    # ✅ IMPLEMENTADO
│   ├── application/
│   ├── domain/
│   ├── infrastructure/
│   └── presentation/
├── users/                   # ✅ IMPLEMENTADO
│   ├── application/
│   ├── domain/
│   ├── infrastructure/
│   └── presentation/
└── tire/                    # 🔄 EN DESARROLLO
    ├── application/
    │   ├── dto/
    │   ├── mapper/
    │   ├── service/
    │   └── usecase/
    ├── domain/
    │   ├── entity/
    │   ├── valueobject/
    │   ├── repository/
    │   └── service/
    ├── infrastructure/
    │   ├── persistence/
    │   └── external/
    └── presentation/
        ├── controller/
        └── request/
```

### Frontend (React)
```
src/
├── features/
│   ├── auth/               # ✅ IMPLEMENTADO
│   ├── users/              # ✅ IMPLEMENTADO
│   └── tire/               # 🔄 EN DESARROLLO
│       ├── components/
│       ├── hooks/
│       ├── pages/
│       ├── services/
│       └── types/
├── shared/
│   ├── components/
│   ├── hooks/
│   ├── utils/
│   └── types/
└── store/
```

---

## ✅ CHECKLIST DE CALIDAD (Post-Implementación)

### Backend
- [ ] Código sigue Clean Architecture (capas bien separadas)
- [ ] DTOs no exponen entidades de dominio
- [ ] Validaciones implementadas (Jakarta Validation)
- [ ] Exception handling implementado
- [ ] Unit tests con >70% cobertura
- [ ] Integration tests para endpoints críticos
- [ ] Documentación OpenAPI/Swagger
- [ ] Logs apropiados (sin secretos, nivel correcto)

### Frontend
- [ ] TypeScript strict mode sin errores
- [ ] Componentes funcionales (no class components)
- [ ] Props tipadas con interfaces
- [ ] React Query para fetching
- [ ] Manejo de loading/error states
- [ ] Formularios con validación
- [ ] Responsive design
- [ ] Tests de componentes principales

### Base de Datos
- [ ] Nomenclatura snake_case consistente
- [ ] Columnas de auditoría completas
- [ ] Soft delete implementado
- [ ] Índices en FKs y campos de búsqueda
- [ ] Constraints apropiados

---

## 🎓 PRINCIPIOS DE DESARROLLO

1. **YAGNI:** No implementar funcionalidad que no se necesita ahora
2. **DRY:** No repetir código, pero no abstraer prematuramente
3. **SOLID:** Especialmente Single Responsibility y Dependency Inversion
4. **Testing:** Unit tests para lógica de negocio, integration tests para use cases
5. **Seguridad:** Validar inputs, no confiar en el cliente

---

## 🚀 FLUJO DE TRABAJO

### Cuando te indique un Requerimiento Funcional:

1. **📖 FASE DE ANÁLISIS** (Obligatorio)
   - Responde el cuestionario de 30 preguntas
   - Presenta diseño de arquitectura
   - Solicita confirmación explícita

2. **⏸️ GATE DE CALIDAD**
   - Espera mi confirmación: "✅ Procede con la implementación"

3. **💻 FASE DE IMPLEMENTACIÓN**
   - Backend primero (domain → application → infrastructure → presentation)
   - Frontend después (types → services → hooks → components → pages)
   - Tests unitarios e integración

4. **✅ FASE DE VERIFICACIÓN**
   - Ejecutar checklist de calidad
   - Documentar decisiones técnicas
   - Preparar para revisión

---

## 📌 RECORDATORIOS IMPORTANTES

- ⚠️ **NUNCA** inicies código sin completar el análisis y obtener confirmación
- ⚠️ **SIEMPRE** considera el ciclo de vida completo de la llanta (estados y transiciones)
- ⚠️ **RECUERDA** que cada requerimiento funcional puede ser un formulario diferente
- ⚠️ **VALIDA** invariantes del dominio en el Use Case, no en el controlador
- ⚠️ **PRESERVA** el histórico completo (auditoría total)
- ⚠️ **NO** hagas supuestos sin confirmar - pregunta cuando haya ambigüedad

---

## 🎯 EJEMPLO DE INICIO

Cuando estés listo para comenzar, yo te indicaré:

```
🎯 INICIAR: RF-002 - Control de Inventario de Llantas
```

Y tú deberás responder con el análisis completo (30 preguntas) antes de cualquier código.

---

## ✨ CONFIRMACIÓN DE COMPRENSIÓN

Antes de comenzar, responde estas preguntas de verificación:

1. ¿Cuál es el stack tecnológico del proyecto?
2. ¿Qué arquitectura de software seguimos?
3. ¿Cuáles módulos ya están implementados?
4. ¿Cuál es el próximo módulo a desarrollar?
5. ¿Qué debo hacer ANTES de escribir cualquier código?
6. ¿Cómo se nombra una tabla en PostgreSQL siguiendo nuestras convenciones?
7. ¿Cuál es el package base para el módulo Tire en Java?

**Si respondiste correctamente las 7 preguntas, responde:**
> **"✅ Contexto confirmado. Listo para recibir el primer requerimiento funcional."**

**Y espera mi instrucción:**
> **"🎯 INICIAR: [RF-XXX] - [Nombre del Requerimiento]"**

---

**🎓 ¡EXCELENCIA EN EL DESARROLLO CON CLAUDE CODE! 🚀**
