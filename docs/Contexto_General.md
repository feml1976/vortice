# PROMPT MASTER PARA DESARROLLO CON IA
## Sistema TRANSER Modernizado - Guía Completa para Desarrollo Asistido por IA

**Versión:** 1.2
**Fecha:** 22 de Enero de 2026
**Compatible con:** Claude (Anthropic)

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Context Setting Base](#2-context-setting-base)
---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento proporciona una guia para el  desarrollo del sistema TRANSER - Vortice modernizado con asistencia de IA (Claude). La guia esta diseñada para:

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

### 2.1 CONTEXTO DEL PROYECTO: Sistema de Informacion Milenio Operativo TRANSER Vortice

## Sobre el Proyecto
Estoy desarrollando la modernización de nuestro Sistema de Informacion Milenio Operativo, un ERP para empresa de transporte de carga en Colombia.
El sistema actual está en desarrollado con Oracle Forms 6i (obsoleto) y lo estamos modernizando a un stack moderno.

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
  - Auth (Autenticacion)
  - User (Usuarios)
  - Share (Catalogos)
  - Tire (Llantas)
  - Purchasing (Compras)
  - Inventory (Inventarios)
  - Workshop (Taller)

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

### 2.2 Prompt de Verificación de Contexto

Después de establecer el contexto base, verificar que la IA lo entendió:

```markdown
Para confirmar que entendiste el contexto, por favor:
1. Resume el stack tecnológico en una línea
2. Indica cuál es la arquitectura de software que estamos usando
3. Dame un ejemplo de cómo se vería el nombre de una tabla en PostgreSQL siguiendo nuestras convenciones
4. Dame un ejemplo de cómo se vería un package de Java para el módulo de Tire (Llantas)

Si todo es correcto, responde "Contexto confirmado ✓" y espera mi siguiente instrucción.
```
**FIN DEL DOCUMENTO**