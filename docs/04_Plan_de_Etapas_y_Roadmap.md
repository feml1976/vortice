# PLAN DE ETAPAS Y ROADMAP
## Sistema TRANSER Modernizado - Planificación Ejecutiva del Proyecto

**Versión:** 1.0
**Fecha:** 31 de Diciembre de 2025
**Empresa:** TRANSER
**Duración Estimada MVP:** 9 meses
**Duración Estimada Completa:** 15-18 meses

---

## TABLA DE CONTENIDOS

1. [Visión General](#1-visión-general)
2. [Definición del MVP](#2-definición-del-mvp)
3. [Fases del Proyecto](#3-fases-del-proyecto)
4. [Cronograma Detallado](#4-cronograma-detallado)
5. [Recursos y Equipo](#5-recursos-y-equipo)
6. [Riesgos y Mitigaciones](#6-riesgos-y-mitigaciones)
7. [Criterios de Éxito](#7-criterios-de-éxito)
8. [Plan de Comunicación](#8-plan-de-comunicación)

---

## 1. VISIÓN GENERAL

### 1.1 Objetivos del Proyecto

| Objetivo | Descripción | Métrica de Éxito |
|----------|-------------|-------------------|
| **Modernización Tecnológica** | Migrar de Oracle Forms 6i a stack moderno (Java/React/PostgreSQL) | Sistema nuevo en producción |
| **Mejora de UX/UI** | Interfaces intuitivas, responsivas, accesibles | Net Promoter Score > 70 |
| **Acceso Multiplataforma** | Web, móvil, tablets | 80% usuarios acceden desde móvil |
| **Reducción de Tiempos** | Automatizar procesos manuales | -30% tiempo en compras, -20% en OTs |
| **Trazabilidad Completa** | Auditoría de todas las operaciones | 100% transacciones auditadas |

### 1.2 Alcance del Proyecto

**Dentro del Alcance:**
- ✅ Módulo de Taller/Operativo (MVP - Prioridad Alta)
- ✅ Módulo de Inventarios (MVP - Prioridad Alta)
- ✅ Módulo de Compras y Proveedores (MVP - Prioridad Alta)
- ✅ Módulo de Gestión de Flota (Básico en MVP, Completo en Fase 2)
- ✅ Portal de Proveedores (MVP)
- ✅ Integración con Facturación Electrónica DIAN (Fase 2)
- ✅ Módulos Complementarios: RRHH, Facturación a Clientes, Viajes (Fase 3)

**Fuera del Alcance (al menos en primera versión):**
- ❌ Módulo de Nómina completo (usar sistema externo y solo integrar)
- ❌ BI/Analytics avanzado (usar herramientas externas: Power BI, Tableau)
- ❌ App móvil nativa (usar PWA en su lugar)
- ❌ Integración con sistemas de terceros fuera de los especificados

### 1.3 Estrategia de Implementación

**Enfoque:** Big Bang con Preparación Exhaustiva

**Justificación:**
- Coexistencia de sistemas legacy y nuevo es compleja y costosa
- Sincronización bidireccional de datos es propensa a errores
- Mejor invertir en preparación exhaustiva y migración de una sola vez

**Fases:**
1. **Desarrollo MVP** (6 meses)
2. **Testing y Ajustes** (1 mes)
3. **Migración de Datos** (1 mes)
4. **Capacitación** (2 semanas)
5. **Go-Live** (1 semana)
6. **Estabilización** (1 mes)
7. **Módulos Adicionales** (6-9 meses)

---

## 2. DEFINICIÓN DEL MVP

### 2.1 Alcance del MVP

El **Minimum Viable Product (MVP)** incluye funcionalidad crítica para que el taller opere sin el sistema legacy:

| Módulo | Funcionalidad Incluida | Funcionalidad Excluida (Fase 2) |
|--------|------------------------|----------------------------------|
| **Workshop (Taller)** | • Órdenes de Trabajo (CRUD completo)<br>• Reportes de Falla<br>• Rutinas de Mantenimiento Preventivo<br>• Control de Mano de Obra<br>• Tablero de Control<br>• Gestión de Llantas<br>• Garantías | • Campañas de mantenimiento<br>• Pruebas de producto<br>• Siniestros y autoseguro |
| **Inventory (Inventarios)** | • CRUD de Ítems<br>• 4 tipos de inventario (Nuevos, Consignación, Reparados, Segundas)<br>• Entradas de Almacén (todas)<br>• Salidas de Almacén (todas)<br>• Matriz de Control<br>• Alertas de inventario<br>• Manejo de Chatarra y RESPEL | • Inventarios de plantillas (equipos de carretera)<br>• Transferencias entre sedes complejas |
| **Purchasing (Compras)** | • Proceso completo de compras (12 etapas)<br>• Portal de Proveedores<br>• Gestión de Consignación<br>• Calificación de Proveedores<br>• Garantías de repuestos | • Compras de activos fijos<br>• Cotizaciones internacionales |
| **Fleet (Flota)** | • CRUD de Vehículos (ficha técnica básica)<br>• Historial de mantenimientos<br>• Control de kilometraje<br>• Documentos (SOAT, Tecnomecánica) | • Gestión de remolques completa<br>• Componentes reparables complejos<br>• Gestión de documentos avanzada |
| **Users & Auth** | • Autenticación (JWT)<br>• Gestión de Usuarios<br>• Roles y Permisos (RBAC)<br>• Auditoría de cambios | • 2FA (Autenticación de 2 factores)<br>• SSO (Single Sign-On) |
| **Reports** | • Reportes básicos (PDF/Excel):<br>  - Listado de OTs<br>  - Inventario valorizado<br>  - Pareto de compras<br>  - Duración de componentes | • Dashboards interactivos avanzados<br>• Reportes personalizables por usuario<br>• Suscripciones a reportes |

### 2.2 Criterios de Aceptación del MVP

El MVP se considera completo y listo para Go-Live cuando:

1. ✅ **Funcionalidad:**
   - Todos los RF del MVP (RF-001 a RF-127) implementados
   - Casos de uso críticos funcionando end-to-end
   - Integración con facturación electrónica DIAN funcionando

2. ✅ **Calidad:**
   - Cobertura de tests unitarios > 70%
   - Cobertura de tests de integración > 50%
   - Cero bugs críticos pendientes
   - < 5 bugs de severidad alta pendientes

3. ✅ **Performance:**
   - Tiempo de respuesta de consultas < 2 segundos (p95)
   - Tiempo de carga de dashboards < 5 segundos
   - Soporta 100 usuarios concurrentes sin degradación

4. ✅ **Datos:**
   - Migración de datos completa y validada
   - Integridad referencial 100%
   - Discrepancias en valores monetarios < 0.01%

5. ✅ **Usuarios:**
   - Usuarios clave capacitados (1 por área)
   - Documentación de usuario completa
   - UAT (User Acceptance Testing) superado

6. ✅ **Infraestructura:**
   - Ambientes de DEV, QA, PROD configurados
   - CI/CD funcionando
   - Backups automáticos configurados
   - Monitoring y alertas activos

---

## 3. FASES DEL PROYECTO

### FASE 0: PREPARACIÓN (Mes 1)

**Objetivo:** Configurar infraestructura, herramientas, y equipo para iniciar desarrollo.

**Actividades:**

| # | Actividad | Responsable | Duración | Entregable |
|---|-----------|-------------|----------|------------|
| 0.1 | Conformar equipo de desarrollo | Gerente de Proyecto | 2 semanas | Equipo completo contratado |
| 0.2 | Configurar repositorios Git (GitHub/GitLab) | DevOps | 2 días | Repos creados, accesos configurados |
| 0.3 | Configurar ambientes de desarrollo local (Docker) | DevOps | 3 días | docker-compose.yml funcionando |
| 0.4 | Configurar ambientes cloud (DEV, QA) | DevOps | 1 semana | Servidores en cloud activos |
| 0.5 | Configurar CI/CD básico (GitHub Actions) | DevOps | 3 días | Pipeline de build y test |
| 0.6 | Configurar herramientas de proyecto management (Jira, Trello) | Scrum Master | 2 días | Tablero Kanban activo |
| 0.7 | Kickoff con stakeholders | Gerente de Proyecto | 1 día | Acta de kickoff |
| 0.8 | Diseño de UI/UX (mockups de pantallas clave) | UX Designer | 2 semanas | Mockups en Figma |
| 0.9 | Diseño de arquitectura detallada | Arquitecto de Software | 1 semana | Documento de arquitectura |
| 0.10 | Diseño de base de datos (modelo ER completo) | Arquitecto + Tech Lead | 1 semana | Diagrama ER, scripts DDL iniciales |

**Hitos:**
- ✅ **H0.1:** Equipo conformado (Semana 2)
- ✅ **H0.2:** Infraestructura lista (Semana 3)
- ✅ **H0.3:** Diseños aprobados (Semana 4)

---

### FASE 1: DESARROLLO MVP - BACKEND (Meses 2-4)

**Objetivo:** Implementar backend del MVP (API REST completa para módulos core).

#### Sprint 1 (Semanas 5-6): Fundamentos

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-001 | Como desarrollador, necesito autenticación JWT para proteger APIs | 8 | Backend Dev 1 |
| US-002 | Como admin, necesito gestionar usuarios (CRUD) | 5 | Backend Dev 2 |
| US-003 | Como admin, necesito gestionar roles y permisos | 5 | Backend Dev 2 |
| US-004 | Como sistema, necesito auditar todos los cambios en tablas críticas | 8 | Backend Dev 1 |
| US-005 | Como desarrollador, necesito configuración de base de datos PostgreSQL con Flyway | 5 | Tech Lead |

**Total Sprint 1:** 31 puntos

#### Sprint 2 (Semanas 7-8): Flota (Básico)

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-006 | Como coordinador, necesito CRUD de vehículos | 8 | Backend Dev 1 |
| US-007 | Como coordinador, necesito ver historial de mantenimientos de un vehículo | 5 | Backend Dev 2 |
| US-008 | Como sistema, necesito actualizar kilometraje de vehículos | 3 | Backend Dev 2 |
| US-009 | Como coordinador, necesito gestionar documentos de vehículos (SOAT, Tecnomecánica) | 5 | Backend Dev 1 |

**Total Sprint 2:** 21 puntos

#### Sprint 3 (Semanas 9-10): Workshop - Work Orders (Parte 1)

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-010 | Como coordinador, necesito crear reportes de falla | 8 | Backend Dev 1 |
| US-011 | Como coordinador, necesito aprobar/rechazar reportes de falla | 5 | Backend Dev 1 |
| US-012 | Como coordinador, necesito crear OT desde reporte de falla | 13 | Backend Dev 2 |
| US-013 | Como coordinador, necesito asignar mecánico e isla a OT | 5 | Backend Dev 2 |
| US-014 | Como mecánico, necesito ver mis OTs asignadas | 3 | Backend Dev 1 |

**Total Sprint 3:** 34 puntos

#### Sprint 4 (Semanas 11-12): Workshop - Work Orders (Parte 2)

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-015 | Como mecánico, necesito agregar tareas a una OT | 5 | Backend Dev 1 |
| US-016 | Como mecánico, necesito solicitar repuestos desde OT | 8 | Backend Dev 2 |
| US-017 | Como mecánico, necesito actualizar progreso de tareas | 5 | Backend Dev 1 |
| US-018 | Como coordinador, necesito cerrar una OT con validación | 8 | Backend Dev 2 |
| US-019 | Como sistema, necesito calcular costos de OT (mano de obra + repuestos) | 8 | Backend Dev 1 |

**Total Sprint 4:** 34 puntos

#### Sprint 5 (Semanas 13-14): Workshop - Rutinas de Mantenimiento

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-020 | Como coordinador, necesito CRUD de rutinas de mantenimiento | 8 | Backend Dev 1 |
| US-021 | Como coordinador, necesito definir periodicidad de rutinas (km, tiempo) | 5 | Backend Dev 1 |
| US-022 | Como sistema, necesito generar OT automáticamente cuando vehículo alcanza km de rutina | 13 | Backend Dev 2 |
| US-023 | Como coordinador, necesito gestionar tareas estándar | 5 | Backend Dev 1 |

**Total Sprint 5:** 31 puntos

#### Sprint 6 (Semanas 15-16): Inventory - Ítems y Almacenes

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-024 | Como almacenista, necesito CRUD de ítems | 8 | Backend Dev 1 |
| US-025 | Como almacenista, necesito gestionar equivalencias de ítems | 5 | Backend Dev 2 |
| US-026 | Como almacenista, necesito CRUD de almacenes | 5 | Backend Dev 1 |
| US-027 | Como sistema, necesito gestionar matriz de control (Min/Max por almacén) | 8 | Backend Dev 2 |
| US-028 | Como sistema, necesito alertar cuando inventario < Mínimo | 5 | Backend Dev 2 |

**Total Sprint 6:** 31 puntos

#### Sprint 7 (Semanas 17-18): Inventory - Movimientos

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-029 | Como almacenista, necesito registrar entrada de almacén por compra | 8 | Backend Dev 1 |
| US-030 | Como almacenista, necesito registrar salida de almacén a OT | 13 | Backend Dev 2 |
| US-031 | Como almacenista, necesito manejar 4 tipos de inventario (Nuevos, Consignación, Reparados, Segundas) | 8 | Backend Dev 1 |
| US-032 | Como sistema, necesito calcular rotación de inventarios | 5 | Backend Dev 2 |

**Total Sprint 7:** 34 puntos

#### Sprint 8 (Semanas 19-20): Purchasing - Proveedores y Requisiciones

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-033 | Como comprador, necesito CRUD de proveedores | 8 | Backend Dev 1 |
| US-034 | Como sistema, necesito generar requisiciones automáticas cuando inventario < Mínimo | 8 | Backend Dev 2 |
| US-035 | Como comprador, necesito consolidar requisiciones | 5 | Backend Dev 1 |
| US-036 | Como comprador, necesito generar solicitud de cotización | 8 | Backend Dev 2 |

**Total Sprint 8:** 29 puntos

#### Sprint 9 (Semanas 21-22): Purchasing - Cotizaciones y Órdenes de Compra

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-037 | Como proveedor, necesito acceder a portal y ver solicitudes de cotización | 13 | Backend Dev 1 |
| US-038 | Como proveedor, necesito cotizar ítems | 8 | Backend Dev 1 |
| US-039 | Como comprador, necesito comparar cotizaciones y seleccionar la mejor | 13 | Backend Dev 2 |
| US-040 | Como sistema, necesito generar órdenes de compra | 8 | Backend Dev 2 |

**Total Sprint 9:** 42 puntos

#### Sprint 10 (Semanas 23-24): Purchasing - Consignación y Garantías

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-041 | Como comprador, necesito crear acuerdos de consignación | 8 | Backend Dev 1 |
| US-042 | Como sistema, necesito hacer cortes automáticos de inventario en consignación | 13 | Backend Dev 2 |
| US-043 | Como comprador, necesito gestionar garantías de repuestos | 8 | Backend Dev 1 |
| US-044 | Como sistema, necesito calificar proveedores automáticamente cada semestre | 13 | Backend Dev 2 |

**Total Sprint 10:** 42 puntos

**Resumen Fase 1:**
- **Duración:** 3 meses (10 sprints de 2 semanas)
- **Puntos totales:** ~330 puntos
- **Velocity promedio esperado:** 33 puntos/sprint (equipo de 2 devs backend)

**Hitos:**
- ✅ **H1.1:** Autenticación y usuarios funcionales (Semana 6)
- ✅ **H1.2:** Work Orders CRUD completo (Semana 12)
- ✅ **H1.3:** Inventarios funcionales (Semana 18)
- ✅ **H1.4:** Compras end-to-end funcionando (Semana 24)

---

### FASE 2: DESARROLLO MVP - FRONTEND (Meses 5-6)

**Objetivo:** Implementar interfaz de usuario para módulos del MVP.

**Nota:** Frontend se inicia después de tener APIs estables, pero puede haber overlap de 2 semanas con final del backend.

#### Sprint 11 (Semanas 23-24): Setup y Autenticación (Overlap con Sprint 10 backend)

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-045 | Como desarrollador frontend, necesito configuración inicial de proyecto React + TypeScript | 5 | Frontend Dev 1 |
| US-046 | Como usuario, necesito login/logout funcional | 8 | Frontend Dev 1 |
| US-047 | Como desarrollador, necesito configuración de rutas protegidas | 5 | Frontend Dev 2 |
| US-048 | Como usuario, necesito layout principal con navbar y sidebar | 8 | Frontend Dev 2 |
| US-049 | Como desarrollador, necesito configuración de React Query y Axios | 3 | Frontend Dev 1 |

**Total Sprint 11:** 29 puntos

#### Sprint 12 (Semanas 25-26): Flota

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-050 | Como coordinador, necesito ver listado de vehículos | 5 | Frontend Dev 1 |
| US-051 | Como coordinador, necesito crear/editar vehículo | 8 | Frontend Dev 1 |
| US-052 | Como coordinador, necesito ver detalle de vehículo con historial | 8 | Frontend Dev 2 |

**Total Sprint 12:** 21 puntos

#### Sprint 13 (Semanas 27-28): Workshop - Work Orders

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-053 | Como coordinador, necesito tablero de control del taller (dashboard) | 13 | Frontend Dev 2 |
| US-054 | Como coordinador, necesito ver listado de OTs con filtros | 8 | Frontend Dev 1 |
| US-055 | Como coordinador, necesito crear OT desde reporte de falla | 13 | Frontend Dev 1 |
| US-056 | Como mecánico, necesito ver detalle de OT con tareas | 8 | Frontend Dev 2 |
| US-057 | Como mecánico, necesito solicitar repuestos desde OT | 8 | Frontend Dev 2 |

**Total Sprint 13:** 50 puntos

#### Sprint 14 (Semanas 29-30): Workshop - Rutinas y Llantas

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-058 | Como coordinador, necesito CRUD de rutinas de mantenimiento | 8 | Frontend Dev 1 |
| US-059 | Como coordinador, necesito gestionar llantas (montaje/desmontaje) | 13 | Frontend Dev 2 |
| US-060 | Como coordinador, necesito ver reportes de falla pendientes de aprobación | 5 | Frontend Dev 1 |

**Total Sprint 14:** 26 puntos

#### Sprint 15 (Semanas 31-32): Inventory

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-061 | Como almacenista, necesito CRUD de ítems | 8 | Frontend Dev 1 |
| US-062 | Como almacenista, necesito ver inventario valorizado por almacén | 8 | Frontend Dev 2 |
| US-063 | Como almacenista, necesito registrar entrada de almacén | 13 | Frontend Dev 1 |
| US-064 | Como almacenista, necesito procesar solicitudes de repuestos de mecánicos | 13 | Frontend Dev 2 |

**Total Sprint 15:** 42 puntos

#### Sprint 16 (Semanas 33-34): Purchasing

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-065 | Como comprador, necesito ver y consolidar requisiciones | 8 | Frontend Dev 1 |
| US-066 | Como comprador, necesito generar solicitud de cotización | 8 | Frontend Dev 1 |
| US-067 | Como comprador, necesito comparar cotizaciones y adjudicar | 13 | Frontend Dev 2 |
| US-068 | Como comprador, necesito ver estado de órdenes de compra | 8 | Frontend Dev 2 |

**Total Sprint 16:** 37 puntos

#### Sprint 17 (Semanas 35-36): Portal de Proveedores

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-069 | Como proveedor, necesito login a portal | 5 | Frontend Dev 1 |
| US-070 | Como proveedor, necesito ver solicitudes de cotización pendientes | 8 | Frontend Dev 1 |
| US-071 | Como proveedor, necesito cotizar ítems | 13 | Frontend Dev 2 |
| US-072 | Como proveedor, necesito ver mis órdenes de compra | 8 | Frontend Dev 2 |
| US-073 | Como proveedor, necesito programar entrega | 8 | Frontend Dev 1 |

**Total Sprint 17:** 42 puntos

#### Sprint 18 (Semanas 37-38): Reportes y Ajustes

| User Story | Descripción | Puntos | Asignado a |
|------------|-------------|--------|------------|
| US-074 | Como gerente, necesito reportes básicos (Pareto compras, duración componentes) | 13 | Frontend Dev 1 |
| US-075 | Como usuario, necesito exportar reportes a PDF/Excel | 8 | Frontend Dev 2 |
| US-076 | Ajustes de UI/UX basados en feedback | 13 | Ambos |
| US-077 | Optimización de performance (lazy loading, memoization) | 8 | Ambos |

**Total Sprint 18:** 42 puntos

**Resumen Fase 2:**
- **Duración:** 2 meses (8 sprints de 2 semanas, con overlap de 2 semanas)
- **Puntos totales:** ~290 puntos
- **Velocity promedio esperado:** 36 puntos/sprint (equipo de 2 devs frontend)

**Hitos:**
- ✅ **H2.1:** Login y layout funcionando (Semana 24)
- ✅ **H2.2:** Pantallas de Workshop completas (Semana 30)
- ✅ **H2.3:** Pantallas de Inventory y Purchasing completas (Semana 36)
- ✅ **H2.4:** MVP frontend completo (Semana 38)

---

### FASE 3: INTEGRACIONES (Mes 7 - Semanas 39-42)

**Objetivo:** Integrar con sistemas externos y completar funcionalidades transversales.

| # | Integración | Descripción | Duración | Responsable |
|---|-------------|-------------|----------|-------------|
| I-001 | Facturación Electrónica DIAN | Integración con proveedor tecnológico para facturación electrónica | 3 semanas | Backend Dev 1 + Externo |
| I-002 | Notificaciones Email | Configuración de SMTP y templates de email | 1 semana | Backend Dev 2 |
| I-003 | Notificaciones Push (PWA) | Implementación de push notifications en frontend | 1 semana | Frontend Dev 1 |
| I-004 | Generación de PDFs | Reportes en PDF (OTs, Facturas, Inventarios) | 2 semanas | Backend Dev 2 |
| I-005 | Exportación a Excel | Exportación de listados y reportes a Excel | 1 semana | Backend Dev 1 |
| I-006 | Carga masiva de datos | Importación de datos desde CSV/Excel (catálogos, ítems) | 1 semana | Backend Dev 2 |

**Actividades Paralelas:**
- Optimización de performance (índices de BD, queries lentas)
- Hardening de seguridad (penetration testing básico)
- Configuración de monitoring (Prometheus + Grafana)
- Configuración de logging centralizado (opcional: ELK stack)

**Hitos:**
- ✅ **H3.1:** Facturación electrónica funcionando (Semana 41)
- ✅ **H3.2:** Todas las integraciones completas (Semana 42)

---

### FASE 4: TESTING Y AJUSTES (Mes 8 - Semanas 43-46)

**Objetivo:** Testing exhaustivo, corrección de bugs, y preparación para migración.

| Semana | Actividades | Responsable | Entregable |
|--------|-------------|-------------|------------|
| 43 | **Testing Funcional**<br>- Ejecutar casos de prueba end-to-end<br>- Validar todos los RF del MVP | QA Engineer | Reporte de bugs |
| 43 | **Corrección de Bugs Críticos** | Dev Team | Bugs críticos resueltos |
| 44 | **Testing de Performance**<br>- Load testing con JMeter/K6<br>- Identificar y resolver cuellos de botella | QA + DevOps | Reporte de performance |
| 44 | **Corrección de Performance Issues** | Dev Team | Performance optimizada |
| 45 | **User Acceptance Testing (UAT)**<br>- Usuarios clave prueban el sistema<br>- Feedback y ajustes | Usuarios + Product Owner | Aceptación de usuarios |
| 45 | **Ajustes de UI/UX basados en UAT** | Frontend Team | UI pulida |
| 46 | **Testing de Seguridad**<br>- Penetration testing básico<br>- Revisión de vulnerabilidades OWASP Top 10 | QA + Security Specialist (Externo) | Reporte de seguridad |
| 46 | **Corrección de vulnerabilidades** | Dev Team | Vulnerabilidades resueltas |
| 46 | **Preparación de ambiente de producción** | DevOps | Ambiente PROD listo |

**Hitos:**
- ✅ **H4.1:** Testing funcional completo, bugs críticos resueltos (Semana 43)
- ✅ **H4.2:** UAT aprobado (Semana 45)
- ✅ **H4.3:** Sistema aprobado para Go-Live (Semana 46)

---

### FASE 5: MIGRACIÓN DE DATOS (Mes 9 - Semanas 47-50)

**Objetivo:** Migrar datos de Oracle a PostgreSQL con validación completa.

#### Semana 47: Preparación de Scripts de Migración

| Actividad | Descripción | Responsable |
|-----------|-------------|-------------|
| Análisis de datos Oracle | Analizar estructura, volúmenes, calidad de datos en Oracle | DBA + Tech Lead |
| Mapeo de datos | Crear matriz de mapeo Oracle → PostgreSQL | DBA + Backend Dev |
| Desarrollo de scripts de extracción | Scripts para exportar datos de Oracle a CSV/SQL | DBA |
| Desarrollo de scripts de transformación | Scripts para limpiar, transformar, y validar datos | Backend Dev |
| Desarrollo de scripts de carga | Scripts para cargar datos en PostgreSQL | Backend Dev |

#### Semana 48: Migración de Prueba (Ambiente QA)

| Actividad | Descripción | Responsable |
|-----------|-------------|-------------|
| Ejecutar migración en QA | Migrar todos los datos a ambiente QA | DBA |
| Validación de integridad referencial | Verificar que todas las FKs sean válidas | DBA |
| Validación de conteos | Comparar totales de registros Oracle vs PostgreSQL | QA |
| Validación de valores monetarios | Comparar sumas de $ (OTs, facturas, inventarios) | Contador + QA |
| Pruebas funcionales con datos reales | Probar sistema con datos migrados | QA + Usuarios |
| Identificación de problemas de migración | Documentar discrepancias y datos faltantes | DBA |

#### Semana 49: Corrección de Scripts y Re-migración

| Actividad | Descripción | Responsable |
|-----------|-------------|-------------|
| Corregir scripts de migración | Resolver problemas identificados en semana 48 | DBA + Dev Team |
| Re-ejecutar migración en QA | Migrar nuevamente con scripts corregidos | DBA |
| Re-validar datos | Repetir validaciones de semana 48 | QA + Contador |
| Aprobación de migración | Sign-off de stakeholders | Gerente de Proyecto |

#### Semana 50: Congelamiento y Migración Final

| Actividad | Descripción | Responsable |
|-----------|-------------|-------------|
| **Lunes:** Congelar sistema legacy | Sistema Oracle pasa a modo solo-lectura | Administrador Oracle |
| **Martes-Miércoles:** Migración final a PROD | Ejecutar scripts de migración en ambiente PROD | DBA |
| **Jueves:** Validación en PROD | Validar datos en PROD | QA + Contador |
| **Viernes:** Preparación para Go-Live | Últimas verificaciones, backups, plan de rollback listo | DevOps |

**Hitos:**
- ✅ **H5.1:** Migración de prueba exitosa (Semana 48)
- ✅ **H5.2:** Migración final aprobada (Semana 50)

---

### FASE 6: CAPACITACIÓN (Semanas 49-50, paralelo a migración)

**Objetivo:** Capacitar usuarios finales en el uso del sistema nuevo.

| Grupo de Usuarios | Módulos | Duración | Formato | Semana |
|-------------------|---------|----------|---------|--------|
| **Coordinadores de Taller** | Workshop, Fleet | 2 días | Presencial | 49 |
| **Mecánicos** | Workshop (OTs, solicitar repuestos) | 1 día | Presencial | 49 |
| **Almacenistas** | Inventory | 2 días | Presencial | 50 |
| **Funcionarios de Compras** | Purchasing | 2 días | Presencial | 50 |
| **Proveedores** | Portal de Proveedores | 1 día | Virtual | 50 |
| **Gerentes** | Dashboards, Reportes | 1 día | Presencial | 50 |

**Materiales de Capacitación:**
- Manuales de usuario (PDF)
- Videos tutoriales (5-10 min por tema)
- FAQs
- Sandbox de pruebas (datos ficticios)

**Hitos:**
- ✅ **H6.1:** Usuarios clave capacitados (Semana 50)

---

### FASE 7: GO-LIVE (Semana 51)

**Objetivo:** Poner el sistema nuevo en producción y apagar el legacy.

#### Lunes de Semana 51: Cutover

| Hora | Actividad | Responsable |
|------|-----------|-------------|
| 00:00 | **Sistema legacy apagado** | Administrador Oracle |
| 00:30 | Verificación final de datos en PROD | DBA |
| 01:00 | Smoke tests en PROD | QA Lead |
| 02:00 | Activación de sistema nuevo | DevOps |
| 03:00 | Verificación de integraciones (facturación, etc.) | Backend Lead |
| 06:00 | **Sistema nuevo en vivo** | Gerente de Proyecto |

#### Lunes-Viernes: Soporte Intensivo

- **War room:** Equipo completo disponible 24/7 (turnos)
- **Resolución de bugs urgentes:** Tiempo de respuesta < 2 horas
- **Comunicación constante:** Updates cada 4 horas a stakeholders

**Criterios de Éxito Go-Live:**
- ✅ Sistema accesible y estable
- ✅ Usuarios pueden crear OTs, solicitar repuestos, hacer compras
- ✅ Sin bugs críticos (sistema bloqueado)
- ✅ < 5 bugs de severidad alta

**Plan de Rollback:**
Si se detectan problemas críticos que impiden operación:
1. **Decisión de rollback:** Gerente de Proyecto + Stakeholders (< 4 horas de detectar problema)
2. **Restaurar sistema legacy:** Administrador Oracle (< 2 horas)
3. **Comunicar a usuarios:** Inmediatamente
4. **Análisis post-mortem:** Identificar causa raíz
5. **Re-planificar Go-Live:** Nueva fecha (mínimo 2 semanas después)

**Hitos:**
- ✅ **H7.1:** Go-Live exitoso (Semana 51 Lunes)

---

### FASE 8: ESTABILIZACIÓN (Mes 11 - Semanas 52-55)

**Objetivo:** Resolver bugs post-go-live, optimizar, y estabilizar el sistema.

| Semana | Actividades | Responsable | Entregable |
|--------|-------------|-------------|------------|
| 52 | **Soporte 24/7** continúa<br>**Corrección de bugs urgentes**<br>**Recopilación de feedback de usuarios** | Dev Team completo | Bugs urgentes resueltos |
| 53 | **Reducción de soporte a horario laboral**<br>**Corrección de bugs de severidad media**<br>**Optimizaciones de performance basadas en uso real** | Dev Team | Bugs medios resueltos |
| 54 | **Ajustes de UX basados en feedback**<br>**Capacitación adicional si es necesaria**<br>**Documentación de workarounds y FAQs** | Frontend + Soporte | UX mejorada |
| 55 | **Cierre de fase de estabilización**<br>**Retrospectiva del proyecto**<br>**Planificación de Fase 2 (módulos adicionales)** | Todo el equipo | Retrospectiva |

**Hitos:**
- ✅ **H8.1:** Sistema estable, sin bugs críticos (Semana 54)
- ✅ **H8.2:** Proyecto MVP cerrado (Semana 55)

---

### FASE 9: MÓDULOS ADICIONALES (Meses 12-18, opcional)

**Objetivo:** Implementar módulos no incluidos en MVP.

**Módulos Propuestos (Priorizar según necesidad del negocio):**

1. **Facturación a Clientes (3 meses)**
   - Facturación de servicios de taller a terceros
   - Cotizaciones
   - Cuentas por cobrar

2. **Módulo de Viajes (3 meses)**
   - Gestión de viajes
   - Planificación de rutas
   - Control de combustible
   - Liquidación de viajes

3. **RRHH Completo (2 meses)**
   - Gestión de nómina (o integración con sistema externo)
   - Gestión de vacaciones, incapacidades, permisos
   - Evaluación de desempeño
   - Aspectos disciplinarios

4. **Módulo de Siniestros y Autoseguro (1 mes)**
   - Registro de siniestros
   - Gestión de reclamaciones
   - Indicadores de siniestralidad

5. **Módulo de Campañas de Mantenimiento (1 mes)**
   - Creación de campañas
   - Seguimiento de ejecución
   - Cierre de campañas

6. **BI y Analytics Avanzado (2 meses)**
   - Dashboards interactivos con drill-down
   - Reportes personalizables
   - Integración con Power BI o Tableau

**Cronograma Tentativo:**
- **Meses 12-14:** Facturación a Clientes
- **Meses 15-17:** Viajes
- **Mes 18:** RRHH Completo
- **Mes 19:** Siniestros y Campañas

---

## 4. CRONOGRAMA DETALLADO

### 4.1 Diagrama de Gantt (Resumen)

```
Mes 1  [FASE 0: PREPARACIÓN]
       ████████████████

Mes 2  [FASE 1: BACKEND MVP - PARTE 1]
       ████████████████ Autenticación, Flota, Work Orders

Mes 3  [FASE 1: BACKEND MVP - PARTE 2]
       ████████████████ Rutinas, Inventarios

Mes 4  [FASE 1: BACKEND MVP - PARTE 3]
       ████████████████ Compras, Consignación

Mes 5  [FASE 2: FRONTEND MVP - PARTE 1]
       ████████████████ Auth, Flota, Work Orders

Mes 6  [FASE 2: FRONTEND MVP - PARTE 2]
       ████████████████ Inventory, Purchasing, Portal Proveedores

Mes 7  [FASE 3: INTEGRACIONES]
       ████████████████ Facturación electrónica, Notificaciones, PDFs

Mes 8  [FASE 4: TESTING Y AJUSTES]
       ████████████████ QA, UAT, Performance, Security

Mes 9  [FASE 5: MIGRACIÓN DE DATOS]
       ████████████████ Scripts, Migración prueba, Migración final

Mes 9  [FASE 6: CAPACITACIÓN] (Paralelo a migración)
       ████████████

Mes 10 [FASE 7: GO-LIVE]
       ████ Go-Live en Semana 51

Mes 11 [FASE 8: ESTABILIZACIÓN]
       ████████████████ Soporte, Bugs, Optimizaciones

Total MVP: ~11 meses (con estabilización)
```

### 4.2 Cronograma por Hitos

| Hito | Fecha | Estado |
|------|-------|--------|
| H0.1: Equipo conformado | Mes 1, Semana 2 | 🔵 Futuro |
| H0.2: Infraestructura lista | Mes 1, Semana 3 | 🔵 Futuro |
| H0.3: Diseños aprobados | Mes 1, Semana 4 | 🔵 Futuro |
| H1.1: Autenticación y usuarios | Mes 2, Semana 6 | 🔵 Futuro |
| H1.2: Work Orders CRUD | Mes 3, Semana 12 | 🔵 Futuro |
| H1.3: Inventarios funcionales | Mes 4, Semana 18 | 🔵 Futuro |
| H1.4: Compras end-to-end | Mes 4, Semana 24 | 🔵 Futuro |
| H2.1: Login y layout | Mes 5, Semana 24 | 🔵 Futuro |
| H2.2: Pantallas Workshop | Mes 6, Semana 30 | 🔵 Futuro |
| H2.3: Pantallas Inventory/Purchasing | Mes 6, Semana 36 | 🔵 Futuro |
| H2.4: MVP frontend completo | Mes 6, Semana 38 | 🔵 Futuro |
| H3.1: Facturación electrónica | Mes 7, Semana 41 | 🔵 Futuro |
| H3.2: Integraciones completas | Mes 7, Semana 42 | 🔵 Futuro |
| H4.1: Testing funcional OK | Mes 8, Semana 43 | 🔵 Futuro |
| H4.2: UAT aprobado | Mes 8, Semana 45 | 🔵 Futuro |
| H4.3: Aprobado para Go-Live | Mes 8, Semana 46 | 🔵 Futuro |
| H5.1: Migración de prueba exitosa | Mes 9, Semana 48 | 🔵 Futuro |
| H5.2: Migración final aprobada | Mes 9, Semana 50 | 🔵 Futuro |
| H6.1: Usuarios capacitados | Mes 9, Semana 50 | 🔵 Futuro |
| H7.1: Go-Live exitoso | Mes 10, Semana 51 | 🔵 Futuro |
| H8.1: Sistema estable | Mes 11, Semana 54 | 🔵 Futuro |
| H8.2: Proyecto MVP cerrado | Mes 11, Semana 55 | 🔵 Futuro |

---

## 5. RECURSOS Y EQUIPO

### 5.1 Equipo de Desarrollo

| Rol | Cantidad | Fase | Costo Mensual (COP)* | Costo Total MVP |
|-----|----------|------|----------------------|-----------------|
| **Arquitecto de Software** | 1 (50%) | 0-3 | $6M | $24M |
| **Tech Lead Backend** | 1 | 0-11 | $12M | $132M |
| **Desarrollador Backend Senior** | 2 | 1-11 | $10M c/u | $220M |
| **Tech Lead Frontend** | 1 | 0-11 | $11M | $121M |
| **Desarrollador Frontend** | 1 | 2-11 | $8M | $80M |
| **QA Engineer** | 1 | 3-11 | $7M | $63M |
| **DevOps / DBA** | 1 (50%) | 0, 5, 7, 9 | $8M | $32M |
| **UX/UI Designer** | 1 (50%) | 0-2 | $6M | $18M |
| **Product Owner** | 1 (50%) | 0-11 | $7M | $77M |
| **Scrum Master / PM** | 1 (50%) | 0-11 | $6M | $66M |
| **TOTAL MENSUAL** | | | **~$90M** | |
| **TOTAL 11 MESES (MVP)** | | | | **~$990M** |

*\* Salarios estimados para Colombia (2025), pueden variar según seniority y ubicación.*

### 5.2 Infraestructura

| Recurso | Proveedor | Costo Mensual (USD) | Costo 11 Meses (USD) |
|---------|-----------|---------------------|----------------------|
| **Servidor Backend (DEV)** | AWS EC2 t3.medium | $60 | $660 |
| **Servidor Backend (QA)** | AWS EC2 t3.medium | $60 | $660 |
| **Servidor Backend (PROD)** | AWS EC2 t3.large | $120 | $1,320 |
| **Base de Datos (DEV)** | RDS PostgreSQL db.t3.medium | $100 | $1,100 |
| **Base de Datos (PROD)** | RDS PostgreSQL db.m5.large | $200 | $2,200 |
| **Frontend Hosting** | S3 + CloudFront | $30 | $330 |
| **Load Balancer** | AWS ALB | $30 | $330 |
| **Monitoring** | CloudWatch | $50 | $550 |
| **Backups** | S3 | $20 | $220 |
| **Otros** | Contingencia | $50 | $550 |
| **TOTAL MENSUAL** | | **~$720** | |
| **TOTAL 11 MESES** | | | **~$8,000** |

**En COP (TRM $4,000):** ~$32M

**Alternativa On-Premise:**
- Inversión inicial: ~$20M COP (servidores)
- Mantenimiento anual: ~$4M COP

### 5.3 Herramientas y Licencias

| Herramienta | Costo Mensual (USD) | Costo 11 Meses (USD) | Comentario |
|-------------|---------------------|----------------------|------------|
| **GitHub Team** | $40 (10 usuarios) | $440 | Repos privados, CI/CD |
| **IntelliJ IDEA Ultimate** | $150 (3 licencias) | $1,650 | Dev Backend |
| **Figma Professional** | $15 (1 usuario) | $165 | UX Designer |
| **Jira Software** | $70 (10 usuarios) | $770 | Project Management |
| **Postman Team** | $0 (Free tier) | $0 | API Testing |
| **Sentry** | $0 (Free tier) | $0 | Error Tracking |
| **TOTAL MENSUAL** | **~$275** | |
| **TOTAL 11 MESES** | | **~$3,000** |

**En COP (TRM $4,000):** ~$12M

### 5.4 Resumen de Costos Totales MVP

| Categoría | Costo (COP) |
|-----------|-------------|
| **Equipo de Desarrollo** | $990M |
| **Infraestructura Cloud** | $32M |
| **Herramientas y Licencias** | $12M |
| **Contingencia (10%)** | $103M |
| **TOTAL** | **~$1,137M COP** |

**En USD (TRM $4,000):** ~$284,000 USD

---

## 6. RIESGOS Y MITIGACIONES

### 6.1 Matriz de Riesgos

| # | Riesgo | Probabilidad | Impacto | Exposición | Mitigación |
|---|--------|--------------|---------|------------|------------|
| R1 | **Retrasos en desarrollo por subestimación** | 🟡 Media | 🔴 Alto | 🔴 Alta | • Estimaciones con contingencia 30%<br>• Sprints cortos (2 semanas) para detectar retrasos temprano<br>• Buffer de 1 mes antes de Go-Live |
| R2 | **Rotación de personal clave** | 🟢 Baja | 🔴 Alto | 🟡 Media | • Contratos con cláusula de permanencia<br>• Bonos por cumplimiento de hitos<br>• Documentación exhaustiva<br>• Pair programming |
| R3 | **Problemas de migración de datos** | 🟡 Media | 🔴 Alto | 🔴 Alta | • Migración de prueba 1 mes antes<br>• Scripts validados en QA<br>• Validaciones automáticas<br>• Plan de rollback |
| R4 | **Resistencia al cambio de usuarios** | 🟡 Media | 🟡 Medio | 🟡 Media | • Involucrar usuarios desde diseño<br>• UAT con usuarios reales<br>• Capacitación completa<br>• Soporte intensivo post-go-live |
| R5 | **Bugs críticos en producción** | 🟢 Baja | 🔴 Alto | 🟡 Media | • Testing exhaustivo (QA + UAT)<br>• Soporte 24/7 primera semana<br>• Plan de rollback preparado |
| R6 | **Cambios de alcance (scope creep)** | 🔴 Alta | 🟡 Medio | 🔴 Alta | • Congelar alcance del MVP<br>• Change control board<br>• Nuevos features a Fase 2 |
| R7 | **Falta de disponibilidad de stakeholders** | 🟡 Media | 🟡 Medio | 🟡 Media | • Definir horas de disponibilidad desde el inicio<br>• Comunicación asíncrona (Slack, Jira)<br>• Product Owner como proxy |
| R8 | **Problemas de performance en producción** | 🟡 Media | 🟡 Medio | 🟡 Media | • Load testing antes de go-live<br>• Monitoring desde día 1<br>• Optimización proactiva |
| R9 | **Dependencia de proveedor externo (facturación DIAN)** | 🟢 Baja | 🟡 Medio | 🟢 Baja | • Seleccionar proveedor confiable<br>• SLA claramente definido<br>• Contingencia manual si falla |
| R10 | **Falta de budget o cambios presupuestales** | 🟢 Baja | 🔴 Alto | 🟡 Media | • Aprobar presupuesto completo antes de iniciar<br>• Transparencia en costos reales<br>• Priorizar MVP sobre nice-to-haves |

**Leyenda:**
- 🟢 Baja
- 🟡 Media
- 🔴 Alta

### 6.2 Plan de Contingencia

**Si se detecta retraso > 2 semanas en Fase 1-2:**
1. Evaluar causas (subestimación, problemas técnicos, ausencias)
2. Opciones:
   - Reducir alcance del MVP (mover funcionalidades no críticas a Fase 2)
   - Incrementar equipo temporalmente (contratar freelancers)
   - Extender timeline (comunicar nueva fecha a stakeholders)

**Si migración de datos falla:**
1. Activar plan de rollback (restaurar sistema legacy)
2. Análisis de causa raíz
3. Corregir scripts de migración
4. Re-planificar go-live (mínimo 2 semanas después)

**Si bugs críticos en producción hacen el sistema inusable:**
1. Evaluar severidad en war room (< 2 horas)
2. Si es bloqueante total: rollback a sistema legacy
3. Si es bloqueante parcial: workaround manual + fix urgente
4. Post-mortem y prevención de recurrencia

---

## 7. CRITERIOS DE ÉXITO

### 7.1 Criterios de Éxito del Proyecto

El proyecto se considera exitoso si:

1. ✅ **Funcionalidad:**
   - Todos los RF del MVP implementados y funcionando
   - Usuarios pueden ejecutar flujos end-to-end sin bloqueadores

2. ✅ **Calidad:**
   - Cero bugs críticos en producción
   - < 10 bugs de severidad media en primer mes
   - Disponibilidad > 99% (medida en primer mes)

3. ✅ **Performance:**
   - Tiempo de respuesta p95 < 2 segundos
   - Soporta 100 usuarios concurrentes sin degradación
   - Tiempo de carga de dashboards < 5 segundos

4. ✅ **Datos:**
   - Migración 100% completa
   - Integridad referencial 100%
   - Discrepancias en valores monetarios < 0.01%

5. ✅ **Adopción:**
   - > 80% de usuarios usan el sistema diariamente en primer mes
   - Net Promoter Score (NPS) > 50 en primer mes

6. ✅ **Timeline:**
   - Go-Live dentro de 11 meses (con margen de ±1 mes aceptable)

7. ✅ **Budget:**
   - Costo real dentro de ±10% del presupuesto aprobado

### 7.2 KPIs Post-Go-Live (Primer Mes)

| KPI | Meta | Medición |
|-----|------|----------|
| **Disponibilidad del sistema** | > 99% | Uptime monitoring |
| **Tiempo promedio de respuesta (p95)** | < 2 seg | Application Performance Monitoring |
| **Bugs críticos** | 0 | Jira |
| **Bugs alta severidad** | < 5 | Jira |
| **Tickets de soporte por día** | < 10 | Sistema de ticketing |
| **Usuarios activos diarios** | > 80% del total | Analytics |
| **Net Promoter Score (NPS)** | > 50 | Encuesta |
| **Tiempo promedio para crear OT** | < 3 min | Análisis de logs |
| **Tiempo promedio para procesar compra** | < tiempo legacy | Comparación con histórico |

---

## 8. PLAN DE COMUNICACIÓN

### 8.1 Stakeholders

| Stakeholder | Rol | Interés | Comunicación |
|-------------|-----|---------|--------------|
| **Gerente General** | Sponsor ejecutivo | Éxito del proyecto, ROI | Mensual: reporte ejecutivo |
| **Gerente de Tecnología** | Sponsor técnico | Arquitectura, equipo, presupuesto | Semanal: reunión de seguimiento |
| **Gerente de Mantenimiento** | Usuario clave | Funcionalidad Workshop | Quincenal: demo de avances |
| **Gerente de Compras** | Usuario clave | Funcionalidad Purchasing | Quincenal: demo de avances |
| **Coordinadores de Taller** | Usuarios finales | Facilidad de uso, training | Mensual: demo, pre-go-live: capacitación |
| **Mecánicos** | Usuarios finales | Facilidad de uso | Pre-go-live: capacitación |
| **Almacenistas** | Usuarios finales | Facilidad de uso | Pre-go-live: capacitación |
| **Proveedores** | Usuarios externos | Portal de proveedores | Pre-go-live: capacitación |
| **Contador** | Usuario validador | Migración de datos, facturación | Fase 5: validación de migración |

### 8.2 Canales de Comunicación

| Canal | Propósito | Frecuencia |
|-------|-----------|------------|
| **Reunión de Sprint Planning** | Planificar trabajo del sprint | Cada 2 semanas (inicio de sprint) |
| **Daily Standup** | Sincronización del equipo de desarrollo | Diaria |
| **Sprint Review (Demo)** | Mostrar avances a stakeholders | Cada 2 semanas (fin de sprint) |
| **Sprint Retrospective** | Mejora continua del equipo | Cada 2 semanas (fin de sprint) |
| **Reporte Ejecutivo** | Actualización de status, riesgos, presupuesto | Mensual |
| **Slack/Teams** | Comunicación asíncrona, dudas rápidas | Continuo |
| **Jira/Trello** | Tracking de tareas, bugs | Continuo |
| **Email** | Comunicaciones formales (cambios de alcance, aprobaciones) | Según necesidad |

### 8.3 Reportes

**Reporte Semanal (Interno - Equipo de Dev):**
- Avances de la semana
- Blockers
- Próximas actividades

**Reporte Quincenal (Sprint Review - Stakeholders):**
- Demo de funcionalidad completada
- Burndown chart del sprint
- Ajustes de prioridades si es necesario

**Reporte Mensual (Ejecutivo - Gerencia):**
- Status general del proyecto (verde/amarillo/rojo)
- Hitos cumplidos vs. plan
- Riesgos y problemas
- Presupuesto ejecutado vs. planificado
- Próximos hitos

---

## 9. CONCLUSIONES Y PRÓXIMOS PASOS

### 9.1 Resumen Ejecutivo

| Aspecto | Detalle |
|---------|---------|
| **Duración Estimada MVP** | 11 meses (9 meses desarrollo + 2 meses migración/go-live/estabilización) |
| **Inversión Estimada** | ~$1,137M COP (~$284K USD) |
| **Equipo** | 8-10 personas (dev, QA, UX, PM) |
| **Enfoque** | Big Bang con preparación exhaustiva |
| **MVP** | Workshop, Inventory, Purchasing, Fleet (básico), Portal Proveedores |
| **Riesgo Principal** | Migración de datos (mitigado con migración de prueba) |

### 9.2 Próximos Pasos Inmediatos

1. **✅ Aprobar plan de proyecto** (Gerencia) - Esta semana
2. **✅ Aprobar presupuesto** (Gerencia) - Esta semana
3. **✅ Iniciar reclutamiento de equipo** (RRHH) - Próxima semana
4. **✅ Contratar infraestructura cloud** (TI) - Próxima semana
5. **✅ Configurar repos y herramientas** (Tech Lead) - Semana 2
6. **✅ Kickoff oficial** (Todo el equipo + Stakeholders) - Semana 2

### 9.3 Factores Críticos de Éxito

Para que este proyecto sea exitoso, es crítico:

1. ⭐ **Compromiso de la gerencia:** Apoyo ejecutivo visible y constante
2. ⭐ **Equipo estable:** Evitar rotación durante el proyecto
3. ⭐ **Alcance controlado:** Resistir el scope creep, MVP primero
4. ⭐ **Calidad sobre velocidad:** No sacrificar calidad por cumplir fechas
5. ⭐ **Involucrar usuarios:** Feedback temprano y frecuente
6. ⭐ **Testing exhaustivo:** Invertir en QA antes de go-live
7. ⭐ **Comunicación transparente:** Reportar problemas a tiempo

---

**¡El éxito del proyecto está en nuestras manos! 🚀**

---

**FIN DEL DOCUMENTO**
