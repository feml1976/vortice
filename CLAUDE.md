# Contexto del Proyecto: Modernización Vórtice

Proyecto de modernización del sistema "Gestión Taller" de una arquitectura Legacy (Oracle Forms 6i / Oracle 11g) a una arquitectura web moderna.

## Tecnologías y Stack
- **Backend:** Java 21+ con Spring Boot 3.5
- **Frontend:** React (Vite/TypeScript) version 18
- **Base de Datos:** PostgreSQL version 18
- **Arquitectura:** Monolito Modular (Separación clara por dominios de negocio)

## Comandos de Construcción y Test
- **Backend (Maven):** `./mvnw clean install`, `./mvnw spring-boot:run`
- **Frontend (NPM):** `npm install`, `npm run dev`, `npm run build`
- **Tests:** `./mvnw test` (Backend), `npm test` (Frontend)

## Guías de Estilo y Patrones
- **Nombramiento DB:** Usar siempre `snake_case` para tablas y columnas en PostgreSQL. 
    - Los nombres de las tablas y las columnas deben ser en ingles
    - Nombres de tablas: plural, snake_case (ej: `work_orders`)
    - Columnas: singular, snake_case (ej: `created_at`)
    - Primary keys: `id` (UUID para entidades, principales, BIGSERIAL para secundarias)
    - Foreign keys: `[tabla_singular]_id` (ej: `vehicle_id`)
    - Timestamps: `created_at`, `updated_at`, `deleted_at` (TIMESTAMP WITH TIME ZONE)
    - Auditoría: `created_by`, `updated_by`, `deleted_by` (BIGINT referencias a users)
    - Soft deletes con `deleted_at IS NULL`
    - Boolean: `is_[adjetivo]` (ej: `is_active`)

- **Backend:** - Seguir arquitectura de capas: Controller -> Service -> Repository -> Entity.
  - Priorizar el uso de DTOs para la comunicación con el frontend.
  - Implementar validaciones con Bean Validation (Hibernate Validator).
- **Frontend:**
  - Componentes funcionales con Hooks.
  - Estructura de carpetas por características (features).
- **General:** Los comentarios en el código y la documentación interna deben ser en español.

## Flujo de Modernización (Legacy a Modern)
1. **Fuente de Verdad:** Los archivos `.fmb` (o sus extractos de texto/documentación) dictan la lógica de negocio actual.
2. **Transformación:** Antes de codificar, analiza los triggers y procesos de Oracle Forms para mapearlos a Servicios de Spring Boot.
3. **Refactorización:** No copiar errores del pasado. Si la lógica de hace 20 años puede simplificarse con patrones modernos, hazlo.
4. **Módulos Críticos:** Compras, Inventarios, Órdenes de Trabajo, Llantas, Mantenimientos, Mecánicos.

## Notas de Desarrollo Asistido por IA
- Al generar código, busca siempre la modularidad para permitir que cada módulo (ej: Inventarios) sea lo más independiente posible del resto dentro del monolito.
- Si detectas una lógica compleja en el código legacy que no sea clara, solicita una aclaración antes de proceder con la re-escritura.

## Documentación de Referencia
- Consultar `/docs/01_Requerimientos_Funcionales_Completo.md` Requerimientos Funcionales Completo.
- Consultar `/docs/02_Requerimientos_Funcionales_Sistema_Modernizado.md` para Requerimientos funcionales.
- Consultar `/docs/02_Sugerencias_y_Recomendaciones_Tecnicas.md` sugerencias y recomendaciones.
- Consultar `/docs/03_Prompt_Master_Desarrollo_IA.md` Prompt Master.
- Consultar `/docs/04_Plan_de_Etapas_y_Roadmap.md` Roadmap.md.

