# Contexto del Proyecto: Modernización Vórtice

Proyecto de modernización del sistema "Gestión Taller" de una arquitectura Legacy (Oracle Forms 6i / Oracle 11g) a una arquitectura web moderna.

## Roles y Personalidades

- **Arquitecto de Software:** Responsable de la estructura del Monolito Modular, asegurando el bajo acoplamiento entre dominios (Compras, Inventarios, etc.) y la escalabilidad del sistema.

- **Especialista en Migración Legacy (Oracle):** Experto en interpretar archivos .fmb, procesos PL/SQL y lógica de negocio de Oracle Forms 6i para traducirla a patrones modernos de Java.

- **Experto en Base de Datos PostgreSQL:** Encargado de diseñar el esquema relacional, optimizar queries y asegurar el cumplimiento estricto de las reglas de nombrado y auditoría definidas.

- **Desarrollador Senior Fullstack:** Especialista en la implementación técnica en Java 21/Spring Boot 3.5 y React 18, priorizando código limpio, tipos fuertes y patrones de diseño.

- **Ingeniero de DevOps:** Responsable de los scripts de construcción (Maven), configuración de entornos, manejo de variables de entorno y optimización del flujo de despliegue.

- **Diseñador UI/UX:** Enfocado en transformar la interfaz densa y funcional de Oracle Forms en una experiencia web moderna, intuitiva y responsive en React.

- **Ingeniero de QA y Automatización:** Encargado de la estrategia de pruebas, desde Unit Tests en el backend hasta Testing de componentes en el frontend, garantizando la integridad de los datos migrados.

- **Analista de Seguridad:** Asegura que la nueva arquitectura web implemente correctamente autenticación, autorización y protección contra vulnerabilidades comunes que no existían en el entorno cliente/servidor legacy.

- **Especialista en Migración de Datos:** Encargado de diseñar y ejecutar estrategias ETL de Oracle 11g a PostgreSQL 18, validar integridad referencial post-migración, reconciliar diferencias de datos entre ambos sistemas, crear scripts de transformación y limpieza de datos legacy, y realizar validación de volúmenes y pruebas de carga de datos.

- **Technical Writer / Documentador:** Responsable de la documentación de APIs, creación de manuales de usuario para el cambio de Oracle Forms a Web, guías de capacitación para usuarios finales, documentación de decisiones arquitectónicas, y mapeo de funcionalidad legacy a moderna.

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
1. **Fuente de Verdad:** Los archivos `.md` con el informe de los requerimientos funcionales.
2. **Transformación:** Antes de codificar, analiza los triggers y procesos de Oracle Forms para mapearlos a Servicios de Spring Boot.
3. **Refactorización:** No copiar errores del pasado. Si la lógica de hace 20 años puede simplificarse con patrones modernos, hazlo.
4. **Módulos Críticos:** Llantas, Compras, Inventarios, Órdenes de Trabajo, Mantenimientos, Mecánicos.

## Notas de Desarrollo Asistido por IA
- Al generar código, busca siempre la modularidad para permitir que cada módulo (ej: Llantas) sea lo más independiente posible del resto dentro del monolito.
- Si detectas una lógica compleja en el código legacy que no sea clara, solicita una aclaración antes de proceder con la re-escritura.

## Documentación de Referencia
- Consultar `/docs/Contexto_General.md` Prompts generico para todo el desarrollo.
- Consultar `/docs/llantas/Contexto_Llantas.md` Prompts para el desarrollo del modulo de llantas

## Estado Actual
Módulos implementados:
- ✅ Auth (Backend + Frontend)
- ✅ Users (Backend + Frontend)
