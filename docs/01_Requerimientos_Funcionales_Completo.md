# REQUERIMIENTOS FUNCIONALES COMPLETOS
## Sistema TRANSER Modernizado - Módulo Operativo (Taller)

**Versión:** 1.0
**Fecha:** 31 de Diciembre de 2025
**Empresa:** TRANSER
**Proyecto:** Modernización Sistema Operativo/Taller

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Alcance del Proyecto](#2-alcance-del-proyecto)
3. [Usuarios y Roles](#3-usuarios-y-roles)
4. [Módulos Funcionales](#4-módulos-funcionales)
5. [Requerimientos Funcionales Detallados](#5-requerimientos-funcionales-detallados)
6. [Requerimientos No Funcionales](#6-requerimientos-no-funcionales)
7. [Integraciones](#7-integraciones)
8. [Glosario](#8-glosario)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Documento

Este documento describe los requerimientos funcionales completos para la modernización del sistema TRANSER, con énfasis en el módulo de Taller/Operativo que constituye el MVP (Minimum Viable Product) del proyecto.

### 1.2 Contexto del Proyecto

**Sistema Actual:**
- Oracle Developer 6i (Forms y Reports)
- Base de datos Oracle 11g
- Arquitectura cliente-servidor

**Sistema Objetivo:**
- PostgreSQL 16+ (Base de datos)
- Java con Spring Boot (Backend)
- React + TypeScript (Frontend)
- Arquitectura web moderna y responsiva

### 1.3 Objetivos del Proyecto

1. **Modernización Tecnológica:** Migrar de tecnologías obsoletas a stack moderno
2. **Mejora de UX/UI:** Interfaces intuitivas y responsivas
3. **Acceso Multiplataforma:** Web, móvil, tablets
4. **Automatización:** Reducir tareas manuales y errores humanos
5. **Trazabilidad:** Control total de operaciones y auditoría
6. **Escalabilidad:** Preparar el sistema para crecimiento futuro

---

## 2. ALCANCE DEL PROYECTO

### 2.1 Alcance General

El sistemaSistema de Informacion Milenioes un ERP integral para empresas de transporte de carga por carretera que gestiona:

- **Gestión de Flota y Transporte**
- **Taller y Mantenimiento** ⭐ (MVP - Alta Prioridad)
- **Inventarios y Almacenes** ⭐ (MVP - Alta Prioridad)
- **Compras y Proveedores** ⭐ (MVP - Alta Prioridad)
- **Facturación y Ventas**
- **Tesorería y Finanzas**
- **Recursos Humanos**
- **Administración y Seguridad**
- **Reportes y Análisis**

### 2.2 Alcance del MVP (Fase 1)

El MVP se enfoca en el **Módulo de Taller/Operativo** e incluye:

1. **Gestión de Órdenes de Trabajo (OT)**
2. **Mantenimiento Preventivo y Correctivo**
3. **Gestión de Inventarios** (Nuevos, Consignación, Reparados, Segundas)
4. **Proceso de Compras** (12 etapas completas)
5. **Portal de Proveedores**
6. **Gestión de Garantías**
7. **Control de Mano de Obra** (Mecánicos, Contratistas)
8. **Tablero de Control en Tiempo Real**
9. **Gestión de Llantas**
10. **Manejo de Chatarra y RESPEL**

### 2.3 Fuera de Alcance (MVP)

- Módulo de Nómina completo
- Contabilidad general (solo integración)
- Módulo de Viajes
- Módulo de Facturación a Clientes
- BI/Analytics avanzado

---

## 3. USUARIOS Y ROLES

### 3.1 Roles del Sistema

| Rol | Descripción | Acceso |
|-----|-------------|--------|
| **Administrador del Sistema** | Configuración general, usuarios, permisos | Total |
| **Coordinador de Taller** | Supervisión de operaciones, asignación de recursos | Módulo Taller |
| **Mecánico** | Ejecución de OTs, reportes de falla | OTs asignadas |
| **Almacenista** | Gestión de inventarios, entradas/salidas | Almacenes |
| **Funcionario de Compras** | Proceso de compras, proveedores, cotizaciones | Compras |
| **Patinador** | Transporte de repuestos taller-almacén | Logística interna |
| **Inspector de Calidad** | Validación de trabajos, auditorías | Inspecciones |
| **Proveedor (Externo)** | Portal de proveedores, cotizaciones, facturas | Portal limitado |
| **Gerente de Mantenimiento** | Reportes, indicadores, toma de decisiones | Reportes/Dashboard |
| **Contador** | Validación contable, facturas, causación | Contabilidad |

### 3.2 Usuarios Estimados

- **Concurrentes:** 100-500 usuarios
- **Total registrados:** 800-1000 usuarios
- **Geografía:** Colombia (múltiples sedes)

---

## 4. MÓDULOS FUNCIONALES

### 4.1 Módulo: Gestión de Vehículos y Flota

**Descripción:** Administración de la información técnica y operativa de vehículos, remolques y componentes.

**Funcionalidades principales:**
- Ficha técnica completa de vehículos
- Historial de mantenimientos
- Control de kilometraje
- Gestión de documentos (SOAT, Tecnomecánica, Tarjeta de Propiedad)
- Trazabilidad de componentes serializados
- Asociación de plantillas de inventario (equipos de carretera, herramientas)

---

### 4.2 Módulo: Taller y Mantenimiento ⭐ MVP

**Descripción:** Gestión integral del taller de mantenimiento, desde la creación de OTs hasta el cierre y facturación.

#### 4.2.1 Submódulo: Tablero de Control

**RF-001: Visualización en Tiempo Real**
- El sistema debe mostrar un tablero de control con vista general del taller
- Debe visualizar esquemas operativos: 1x1, 1x2, 2x4, 4x8 (turnos de 8 horas)
- Debe mostrar islas de trabajo con estado (Ocupada/Libre)
- Debe indicar vehículos en cada isla con placa, mecánico asignado y estado de OT
- Debe permitir filtros por sede, turno, estado

**RF-002: Indicadores en Tiempo Real**
- Vehículos en taller (total, por tipo, por estado)
- OTs abiertas/cerradas/pendientes
- Tiempo promedio de reparación
- Índice de ocupación del taller
- Alertas de vehículos con tiempos excedidos
- Vehículos parados por falta de repuestos

**RF-003: Gestión de Islas de Trabajo**
- Crear, editar, eliminar islas de trabajo
- Asignar tipo de isla (Mantenimiento General, Motor, Transmisión, Electricidad, Llantas, Latonería)
- Configurar capacidad por isla
- Asociar equipos/herramientas fijas a cada isla
- Definir restricciones de uso (solo cabezotes, solo remolques, mixto)

#### 4.2.2 Submódulo: Reportes de Falla

**RF-004: Creación de Reporte de Falla**
- Cualquier usuario autorizado puede crear un reporte de falla
- Fuentes: Conductor, Coordinador, Mecánico, Inspector, Sistema automático
- Captura: Placa, descripción de falla, sistema afectado, ubicación, fecha/hora
- Clasificación automática por sistema (Motor, Transmisión, Frenos, Suspensión, etc.)
- Adjuntar fotos/videos de la falla
- Asignación de prioridad (Crítica, Alta, Media, Baja)

**RF-005: Clasificación de Reportes**
- El sistema debe clasificar automáticamente por:
  - **Tipo de intervención:** Preventivo, Correctivo, Predictivo
  - **Origen:** Carretera, Taller, Inspección rutinaria
  - **Urgencia:** Inmediato (vehículo parado), 24 horas, Programable
- Sugerir sistema afectado basado en descripción (IA/ML opcional)

**RF-006: Flujo de Aprobación de Reportes**
- Reportes críticos requieren validación de Coordinador de Taller
- Sistema debe alertar vía notificación push/email
- Tiempo máximo de respuesta configurable (ej: 30 minutos para críticos)
- Si no hay respuesta, escalar al siguiente nivel (Gerente de Mantenimiento)

#### 4.2.3 Submódulo: Órdenes de Trabajo (OT)

**RF-007: Creación de OT**
- Generación automática desde reporte de falla aprobado
- Generación manual por Coordinador de Taller
- Generación automática desde rutinas de mantenimiento preventivo
- Numeración consecutiva única por sede con formato: [SEDE]-OT-[AÑO]-[CONSECUTIVO]
- Tipos de OT:
  - Vehículo (Cabezote/Remolque)
  - Componente reparable serializado (Motor, Transmisión, Diferencial)
  - Componente reparable no serializado (Radiador, Bomba de agua)
  - Máquina/Herramienta

**RF-008: Asignación de Recursos a OT**
- Asignar mecánico principal (responsable)
- Asignar mecánicos auxiliares (opcional)
- Asignar isla de trabajo
- Asignar prioridad de ejecución
- Estimar tiempo de reparación
- Registrar fecha/hora de inicio programada

**RF-009: Estados de OT**
El sistema debe manejar los siguientes estados:
1. **Creada:** OT generada, pendiente de asignación
2. **Asignada:** Mecánico e isla asignados
3. **En Progreso:** Mecánico inició trabajos
4. **Pausada por Repuestos:** Esperando llegada de repuestos
5. **Pausada por Diagnóstico:** Requiere análisis adicional
6. **Pausada por Autorización:** Excede presupuesto, requiere aprobación
7. **En Pruebas:** Trabajo terminado, en pruebas de funcionamiento
8. **Cerrada:** Trabajo finalizado satisfactoriamente
9. **Anulada:** OT cancelada (con justificación)

**RF-010: Gestión de Tareas en OT**
- Una OT puede contener múltiples tareas
- Cada tarea tiene:
  - Descripción
  - Sistema asociado
  - Estado (Pendiente, En Progreso, Completada)
  - Tiempo estimado vs. real
  - Mecánico ejecutor
  - Observaciones
- Poder agregar/eliminar tareas durante ejecución
- Requiere autorización de Coordinador para cambios mayores

**RF-011: Solicitud de Repuestos desde OT**
- Mecánico solicita repuestos desde aplicativo móvil/web
- Especifica: repuesto, cantidad, urgencia (Inmediata/No Urgente)
- Repuestos urgentes generan alerta a almacenista
- Repuestos no urgentes se acumulan para entrega programada
- Reserva automática de inventario disponible
- Si no hay stock, genera requisición de compra automática

**RF-012: Control de Tiempo en OT**
- Registro automático de fecha/hora de inicio
- Pausas automáticas cuando cambia a estado "Pausada"
- Cálculo de tiempo neto de trabajo
- Tiempo perdido por falta de repuestos (KPI crítico)
- Alertas cuando se excede tiempo estimado (ej: +20%)
- Comparación tiempo estimado vs. real para mejora continua

**RF-013: Validación y Cierre de OT**
- Mecánico marca tarea como completada
- Inspector de calidad valida trabajo (opcional, configurable)
- Pruebas de funcionamiento registradas
- Coordinador de Taller aprueba cierre
- Registro de kilometraje al cierre
- Generación automática de documento de entrega
- Cálculo automático de costos (mano de obra + repuestos + servicios externos)

**RF-014: Reapertura de OT**
- Si la falla se repite en X días (configurable, ej: 7 días), permitir reabrir OT
- Asociar nueva OT con la anterior (trazabilidad de reincidencias)
- No cobrar mano de obra si es mismo mecánico y misma falla (política de garantía interna)

#### 4.2.4 Submódulo: Mantenimiento Preventivo

**RF-015: Creación de Rutinas de Mantenimiento**
- Definir nombre, código, descripción de la rutina
- Tipo: Normal (se repite siempre igual) o Secuencial (varía en cada ciclo)
- Asociar tareas que componen la rutina
- Estado: Activa/Inactiva
- Asociar a grupos de vehículos (por marca, modelo, año, tipo)

**RF-016: Periodicidad de Rutinas**
- Definir frecuencia:
  - Por Kilometraje (ej: cada 20,000 km)
  - Por Tiempo (ej: cada 3 meses)
  - Por Horas de Operación (ej: cada 500 horas)
  - Combinado (lo que ocurra primero)
- Ventana de tolerancia (ej: ±500 km, ±5 días)

**RF-017: Rutinas Secuenciales**
- Definir secuencia de rutinas (Rutina A → Rutina B → Rutina C → Rutina A)
- Cada rutina puede tener periodicidad diferente
- Ejemplo:
  - Rutina 1 (Servicio menor): 7,500 km
  - Rutina 2 (Servicio mayor): 15,000 km
  - Rutina 3 (Overhaul): 50,000 km

**RF-018: Generación Automática de OT Preventivas**
- El sistema debe monitorear kilometraje/tiempo de cada vehículo
- Generar alerta 7 días antes de vencimiento de rutina
- Generar OT automáticamente al alcanzar el punto de mantenimiento
- Permitir adelantar mantenimiento (ej: vehículo en taller por otra razón)
- Registrar si se ejecutó antes/después de tiempo programado

**RF-019: Gestión de Tareas Estándar**
- Biblioteca de tareas reutilizables
- Cada tarea incluye:
  - Descripción detallada
  - Sistema asociado
  - Tiempo estándar de ejecución
  - Repuestos típicos necesarios
  - Herramientas especiales requeridas
  - Procedimiento (opcional: adjuntar PDF/video)
- Categorías: Verificación, Ajuste, Cambio, Lubricación, Limpieza, Inspección

**RF-020: Plantillas de Mantenimiento por Fabricante**
- Importar/cargar rutinas recomendadas por fabricante (Kenworth, International, Volvo, etc.)
- Adaptar rutinas a condiciones de operación local
- Versionamiento de rutinas (mantener histórico de cambios)

#### 4.2.5 Submódulo: Gestión de Llantas

**RF-021: Inventario de Llantas**
- Registro individual de cada llanta con serial único
- Información: Marca, referencia, medida, índice de carga, diseño (Dirección/Tracción/Libre)
- Estado: Nueva, Reencauchada 1ª vez, Reencauchada 2ª vez, Reencauchada 3ª vez, Dada de baja
- Costo de adquisición
- Proveedor
- Fecha de compra
- Profundidad de labrado inicial y actual

**RF-022: Montaje y Desmontaje de Llantas**
- Registrar montaje en vehículo: placa, posición, kilometraje, fecha
- Registrar presión de inflado recomendada por posición
- Registro fotográfico del estado al montar
- Desmontaje: registrar kilometraje, profundidad final, motivo (Desgaste normal, Pinchadura, Corte, Reventón)
- Cálculo automático de kilometraje recorrido

**RF-023: Control de Rotación de Llantas**
- Programar rotaciones según política (ej: cada 20,000 km)
- Generar alertas de rotación pendiente
- Registrar cambios de posición dentro del mismo vehículo
- Mantener trazabilidad completa de cada llanta

**RF-024: Control de Profundidad de Labrado**
- Registro periódico de profundidad (mínimo mensual)
- Alertas cuando profundidad < límite legal (ej: 1.6 mm)
- Cálculo de desgaste promedio por km
- Predicción de vida útil restante

**RF-025: Gestión de Reencauche**
- Registro de envío a proveedor de reencauche
- Seguimiento de estado (En planta, Aprobada, Rechazada, Reencauchada)
- Registro de recepción con nuevo serial interno
- Costo de reencauche
- Número de reencauches permitidos por llanta (configurable)

**RF-026: Reportes de Llantas**
- Costo por kilómetro por llanta
- Pareto de marcas por rendimiento
- Análisis de causas de baja
- Inventario de llantas disponibles por tipo
- Llantas montadas por vehículo (visualización gráfica)
- Pronóstico de compra de llantas

#### 4.2.6 Submódulo: Campañas de Mantenimiento

**RF-027: Creación de Campañas**
- Definir campaña con nombre, código, descripción, fecha inicio/fin
- Motivos: Recall de fabricante, mejora técnica, cambio de política, problema recurrente
- Asociar a grupo de vehículos afectados
- Definir tareas a ejecutar
- Asignar prioridad
- Registrar origen (Fabricante, Empresa, Autoridad)

**RF-028: Seguimiento de Campañas**
- Dashboard de campaña con % de avance
- Lista de vehículos pendientes
- Generación automática de OTs para vehículos en campaña
- Registro de fecha de ejecución por vehículo
- Costo total de la campaña
- Alertas de vehículos no atendidos

**RF-029: Cierre de Campañas**
- Verificación de cumplimiento 100%
- Generar reporte final con costos, tiempos, hallazgos
- Archivar documentación (facturas, certificados de fabricante)

#### 4.2.7 Submódulo: Mano de Obra

**RF-030: Registro de Mecánicos**
- Datos personales
- Especialidades (Motor, Transmisión, Frenos, Electricidad, etc.)
- Certificaciones (con fechas de vencimiento)
- Nivel de experiencia (Junior, Semi-senior, Senior, Experto)
- Tarifa por hora (puede variar según complejidad de tarea)
- Turno asignado
- Estado (Activo, Licencia, Incapacidad, Vacaciones)

**RF-031: Registro de Tiempo de Mecánicos**
- Check-in / Check-out diario
- Registro automático de tiempo en cada OT
- Pausas (Almuerzo, descanso, fin de turno)
- Tiempo productivo vs. tiempo disponible
- Cálculo de eficiencia (tiempo real vs. tiempo estándar)

**RF-032: Gestión de Contratistas In-House**
- Registro de empresa contratista
- Registro de mecánicos de contratista (temporal)
- Tarifas por hora o por tarea
- Control de acceso y permisos limitados
- Validación de trabajos por Coordinador antes de cierre de OT
- Generación de acta de trabajos para facturación

**RF-033: Gestión de Servicios Externos**
- Registro de proveedores de servicios (Rectificadoras, Tornos, Soldadura especializada, etc.)
- Orden de Servicio Externo
- Seguimiento de componentes enviados a proveedor
- Registro de recepción y validación de calidad
- Asociación de factura de proveedor a OT

**RF-034: Cálculo de Costos de Mano de Obra**
- Costo por OT = Σ (Tiempo de cada mecánico × Tarifa/hora)
- Diferenciación: Mecánico propio, Contratista, Servicio externo
- Recargos por horario nocturno, festivos (configurable)
- Comparación costo real vs. costo estándar

#### 4.2.8 Submódulo: Garantías

**RF-035: Solicitud de Garantía de Repuesto**
- Crear solicitud desde OT
- Motivos:
  - Defecto al momento de instalación
  - Falla antes de cumplir vida útil esperada
  - Equivalencia incorrecta sugerida por proveedor
  - Servicio no conforme (reparación externa)
- Capturar información:
  - Repuesto (marca, referencia, serial si aplica)
  - Proveedor
  - Fecha de compra
  - Fecha de instalación y kilometraje
  - Fecha de falla y kilometraje
  - Duración real vs. esperada
  - Descripción técnica de la falla
  - Registro fotográfico/video
  - OT donde se instaló
  - OT donde se detectó falla

**RF-036: Flujo de Garantía**
- Mecánico/Coordinador crea solicitud
- Almacén recibe componente fallado
- Compras envía solicitud a proveedor vía portal
- Proveedor responde: Acepta/Rechaza (con justificación)
- Si acepta: registrar reposición de repuesto o devolución de dinero
- Si rechaza: análisis interno (Comité Técnico)
  - Si rechazo es procedente: afectar cuenta de "Pérdidas por garantías no aprobadas"
  - Si rechazo no es procedente: escalar reclamo con proveedor
- Afectación a calificación de proveedor

**RF-037: Registro de Garantías de Servicio**
- Garantía de trabajos propios (política interna: ej. 7 días o 1000 km)
- Garantía de trabajos de contratistas
- Garantía de servicios externos
- Reapertura automática de OT si falla dentro de período de garantía
- No cobro de mano de obra en garantías internas

**RF-038: Reportes de Garantías**
- Pareto de repuestos con mayor reclamación
- % de aceptación de garantías por proveedor
- Costo de garantías no aprobadas
- Tiempo promedio de respuesta de proveedores
- Repuestos con mayor tasa de falla prematura

---

### 4.3 Módulo: Inventarios y Almacenes ⭐ MVP

**Descripción:** Gestión integral de inventarios de repuestos, herramientas, EPPs, insumos en múltiples almacenes y sedes.

#### 4.3.1 Submódulo: Estructura de Almacenes

**RF-039: Gestión de Sedes y Almacenes**
- Crear múltiples sedes (ej: Tocancipá, Duitama, Bogotá)
- Cada sede puede tener múltiples almacenes (Almacén Central, Almacén de Llantas, Almacén de EPPs, Almacén de Herramientas)
- Cada almacén tiene:
  - Código único
  - Descripción
  - Encargado (almacenista)
  - Ubicación física
  - Estado (Activo/Inactivo)
  - Tipo (General, Especializado, Consignación)
- Relación de abastecimiento (ej: Duitama se abastece desde Tocancipá)

**RF-040: Gestión de Turnos de Almacenistas**
- Asignar almacenistas a almacén con turnos
- Control de acceso según turno
- Traspaso de responsabilidad entre turnos (acta de entrega)

#### 4.3.2 Submódulo: Gestión de Ítems

**RF-041: Creación de Ítems**
- Información básica: Código interno, descripción, sistema asociado
- Clasificación:
  - Repuesto (Filtro, Correa, Bujía, etc.)
  - Componente reparable (Motor, Transmisión, Alternador)
  - Herramienta (Llave, Torquímetro, Gato hidráulico)
  - Insumo (Aceite, Grasa, Líquido de frenos)
  - EPP (Casco, Guantes, Botas)
  - Equipo (Computador, Tablet, Radio)
- Tipo: Serializado / No Serializado
- Unidad de medida (Unidad, Galón, Litro, Kilo, Metro)
- Marca
- Referencia del fabricante
- Código de barras / QR
- Proveedor preferido
- Proveedor alterno
- Vida útil esperada (en km, horas, meses según aplique)
- Peso, dimensiones (para control de bodega)
- Foto/imagen del ítem

**RF-042: Gestión de Equivalencias**
- Definir ítems equivalentes (diferentes marcas, misma función)
- Registrar equivalencias sugeridas por proveedores (requiere aprobación técnica)
- Aprobación de equivalencias por personal de mantenimiento
- Registro de pruebas de equivalencias
- Histórico de equivalencias aprobadas/rechazadas
- Orden de preferencia de equivalentes

**RF-043: Ítems Serializados**
- Asignar serial único a cada unidad
- Trazabilidad completa:
  - Fecha de compra
  - Proveedor
  - Costo
  - Fecha de instalación y kilometraje
  - Placa del vehículo donde se instaló
  - Fecha de remoción y kilometraje
  - Motivo de remoción
  - Duración (km, horas, meses)
  - Costo por km/hora
- Histórico de montajes/desmontajes (para reparables)

#### 4.3.3 Submódulo: Matriz de Control de Inventarios

**RF-044: Creación de Matriz de Control**
- Seleccionar ítems críticos para mantener en inventario
- Definir por cada ítem y por cada sede:
  - Inventario Mínimo (punto de reorden)
  - Inventario Máximo (stock óptimo)
  - Lead Time del proveedor (días)
  - Consumo promedio mensual
  - Costo promedio unitario
- Clasificación ABC (Pareto)
- Revisión y ajuste periódico (trimestral/semestral)

**RF-045: Alertas de Inventario**
- Alerta cuando inventario < Mínimo (generar requisición automática)
- Alerta cuando inventario > Máximo (sobre stock)
- Alerta de ítems sin movimiento en X meses (ej: 6 meses)
- Alerta de ítems con alta rotación no incluidos en matriz

**RF-046: Cálculo de Inventario Óptimo**
- Basado en consumo histórico (mínimo 6 meses de data)
- Considerar estacionalidad (si aplica)
- Considerar lead time de proveedor
- Considerar costo de mantener inventario vs. costo de parada por falta
- Sugerencia automática de ajuste de Min/Max

#### 4.3.4 Submódulo: Tipos de Inventario

**RF-047: Inventario de Nuevos**
- Repuestos comprados nuevos
- Entrada desde compras (orden de compra)
- Valorización al costo de adquisición
- Método de valorización: Promedio ponderado
- Rotación y antigüedad
- Obsolescencia (ítems sin movimiento > X meses)

**RF-048: Inventario en Consignación**
- Objetivo: Maximizar inventario en consignación (libera capital de trabajo)
- Registro de contrato de consignación con proveedor
- Definir ítems incluidos en consignación
- Niveles Min/Max por ítem
- Propiedad del inventario: Proveedor (hasta que se consuma)
- Cortes de inventario periódicos (semanal, quincenal, mensual)
- Generación de Pre-Factura automática
- Recepción de Factura del proveedor
- Relación Pre-Factura vs. Factura (una factura puede recoger varias pre-facturas)
- Diferenciación: Vehículos propios vs. Terceros (para IVA)
- Programación de rellenos de inventario
- Costo de inventario en consignación (control, no propiedad)

**RF-049: Manejo de Consignación en Moneda Extranjera**
- Soporte para consignaciones en USD u otra moneda
- Registro de tasa de cambio diaria
- Liquidación al momento de salida del repuesto (tasa del día)
- Reportes en moneda original y en pesos colombianos

**RF-050: Inventario de Reparados**
- Componentes reparados internamente o externamente
- Entrada desde OT de reparación
- Valorización: Costo de reparación + repuestos utilizados
- Control de número de reparaciones por componente
- Preferencia de uso (consumir reparados antes que nuevos)

**RF-051: Inventario de Segundas (Recuperación)**
- Repuestos recuperados de vehículos dados de baja
- Repuestos en buenas condiciones devueltos de OT
- Valorización a costo cero
- Uso preferencial (reducir costos)
- Control de estado (Bueno, Regular)

#### 4.3.5 Submódulo: Entradas de Almacén

**RF-052: Entrada por Compras**
- Recepción desde Orden de Compra
- Validación vs. OC: cantidad, marca, referencia, estado
- Registro de factura o remisión del proveedor
- Devoluciones parciales o totales al proveedor (si no conforme)
- Generación de Entrada de Almacén con consecutivo único
- Doble validación: Compras recibe, Almacén confirma recepción
- Estados: Recibida en Compras, Ingresada en Almacén
- Registro fotográfico (opcional)

**RF-053: Entrada por Reparación**
- Entrada de componente reparado desde OT
- Seriados: una unidad por OT
- No seriados: múltiples unidades en misma OT
- Registro de costo de reparación (mano de obra + repuestos)
- Entregas parciales permitidas
- Acta de baja para componentes no reparables

**RF-054: Entrada por Consignación**
- Cargue inicial de inventario en consignación
- Rellenos periódicos
- Validación de cantidades vs. niveles Min/Max
- Generación de documento de ingreso (no factura)
- Registro de remisión del proveedor

**RF-055: Entrada por Garantía**
- Reposición de repuesto por garantía aceptada
- Ingreso al mismo costo de compra original
- Asociación a solicitud de garantía
- Cierre de ciclo de garantía

**RF-056: Entrada por Devolución de OT**
- Repuestos solicitados pero no utilizados
- Validación de estado (si está deteriorado, dar de baja)
- Retorno a inventario disponible
- Registro de quién solicitó, quién devolvió, quién recibió (Almacenista)

**RF-057: Entrada por Pruebas de Proveedor**
- Repuestos entregados por proveedor para prueba
- Costo cero o con descuento especial
- Trazabilidad de prueba (OT donde se usó, resultados)
- Aprobación/Rechazo final
- Habilitación para compra futura si es aprobado

**RF-058: Entrada por Atención Comercial**
- Obsequios, bonos, incentivos de proveedores
- Costo cero
- Afecta positivamente calificación de proveedor
- Registro de motivo

**RF-059: Entrada por Traslado entre Almacenes**
- Solicitud de traslado desde almacén receptor
- Aprobación de almacén emisor
- Registro de transportador (interno o externo)
- Documento de traslado con consecutivo único
- Confirmación de recepción
- Ajuste de inventarios en ambos almacenes

**RF-060: Entrada por Recuperación (Segundas)**
- Recuperación de repuestos de vehículos dados de baja (siniestros, venta)
- Remoción de componente de ficha técnica de vehículo
- Traslado a otro vehículo (mismo grupo) o a almacén
- Ajuste de vida útil remanente
- Costo cero

#### 4.3.6 Submódulo: Salidas de Almacén

**RF-061: Salida a Orden de Trabajo**
- Solicitud de repuesto desde OT por mecánico vía app móvil
- Visualización de pedido en pantalla de almacenista
- Alistamiento de pedido
- Selección de tipo de inventario en orden:
  1. Reparados
  2. Segundas
  3. Nuevos
  4. Consignación
- Permitir cambio de orden con justificación (observación obligatoria)
- Entrega a Patinador (si aplica) o directamente a Mecánico
- Confirmación de recepción por Mecánico en app
- Reserva de inventario cuando solicitud es "No Urgente"
- Liberación de reserva si se asigna a otra OT más urgente (genera requisición automática)
- Repuestos para prueba: marcación especial
- Validación de inventario durante alistamiento (ajuste si hay diferencias con alerta)

**RF-062: Salida a Compras (Garantía)**
- Salida de repuesto fallado a Compras para reclamación
- Trazabilidad de compra original
- Control de inventario en poder de Compras
- Seguimiento en portal de proveedores

**RF-063: Salida a Funcionario**
- Dotación y EPPs (nueva dotación o reposición por deterioro)
- Herramientas asignadas a funcionario
- Requiere aprobación de superior jerárquico
- Registro de entrega con firma electrónica
- Inventario asignado al funcionario (responsabilidad)

**RF-064: Salida a Sección/Instalaciones**
- Elementos para áreas (oficinas, cafetería, etc.)
- Inventario auditable
- Responsable de sección
- Planes de mantenimiento de equipos asignados

**RF-065: Salida a Máquina/Herramienta**
- Repuestos para reparación de maquinaria de taller
- Apertura de OT para máquina/herramienta
- Proceso similar a vehículos

**RF-066: Salida por Traslado de Almacén**
- Proceso descrito en RF-059

**RF-067: Salida por Baja de Inventario**
- Motivos: Obsolescencia, Deterioro, Pérdida, Robo, Vencimiento
- Acta de baja con consecutivo único
- Requiere aprobación de Coordinador/Gerente según monto
- Ajuste de inventario contable
- Registro fotográfico

**RF-068: Salida por Préstamo de Herramientas**
- Préstamo temporal a mecánico
- Funcionario no puede cerrar tarea hasta devolver herramienta
- Registro de estado al devolver
- Diferenciación préstamo vs. asignación permanente

**RF-069: Salida por Devolución de Consignación**
- Devolución parcial o total a proveedor
- Motivos: Baja rotación, Cambio de política, Precio alto, Desabastecimiento
- Acta de devolución
- Registro en portal de proveedores
- Afecta calificación de proveedor

#### 4.3.7 Submódulo: Control de Chatarra y RESPEL

**RF-070: Gestión de Chatarra**
- Clasificación de material removido: Chatarra ferrosa, Colado, Aluminio, Cobre, etc.
- Asociación de peso estimado por tipo de material (una sola vez por ítem)
- Depósitos predefinidos (Depósito Chatarra 1, Depósito Aluminio, etc.)
- Acumulación de material con peso total por depósito
- Acta de baja de chatarra con consecutivo
- Registro de proveedor que recoge
- Precio unitario por tipo de material
- Totalización en dinero
- Certificado de disposición final (recibido de proveedor)
- Generación de factura de venta

**RF-071: Gestión de RESPEL**
- Identificación de residuos peligrosos (Aceite usado, Baterías, Filtros contaminados, Líquidos de frenos)
- Almacenamiento temporal en área segregada
- Registro de cantidades acumuladas
- Acta de entrega a gestor RESPEL
- Factura de pago por recolección (empresa paga al gestor)
- Certificado de disposición final
- Cumplimiento normativo ambiental

#### 4.3.8 Submódulo: Inventarios de Plantillas

**RF-072: Creación de Plantillas de Inventario**
- Definir plantillas para equipos estándar:
  - Equipo de carretera de vehículos (Tacos, Extintor, Botiquín, Conos, etc.)
  - Kit de manejo ambiental (Paños absorbentes, Arena, etc.)
  - Herramientas de mecánico (Juego de llaves, Destornilladores, etc.)
  - Equipos de funcionario (Laptop, Celular, Cargador, etc.)
- Asociar ítems a plantilla con cantidad estándar

**RF-073: Asignación de Plantillas**
- Asociar plantilla a: Vehículo, Funcionario, Instalación, etc.
- Entrega de inventario completo según plantilla
- Firma electrónica de recepción
- Responsabilidad sobre inventario asignado

**RF-074: Traspaso de Inventario**
- Traspaso entre conductores (cambio de turno)
- Traspaso entre mecánicos (cambio de herramientas)
- Traspaso de almacén a mecánico y viceversa
- Firma electrónica de quien entrega y quien recibe
- Validación de estado y cantidades
- Actualización en tiempo real de responsabilidad

**RF-075: Baja de Inventario Asignado**
- Reporte de pérdida/robo por responsable
- Evaluación de responsabilidad
- Descuento de nómina (si aplica)
- Reposición de elemento

#### 4.3.9 Submódulo: Reportes de Inventarios

**RF-076: Reporte de Duración de Componentes**
- Por cada componente serializado: duración en km, horas, meses
- Costo por km/hora (Costo de adquisición / Duración)
- Comparación entre marcas/proveedores
- Pareto de mejor rendimiento
- Identificación de componentes con baja duración (análisis de causa raíz)

**RF-077: Reporte de Rotación de Inventarios**
- Rotación por ítem (ventas/inventario promedio)
- Ítems de alta rotación no incluidos en matriz de control
- Ítems de baja rotación (candidatos a baja)
- Valor de inventario sin movimiento > X meses

**RF-078: Reporte de Valorización de Inventarios**
- Valor total de inventario por almacén, por sede
- Desglose por tipo: Nuevos, Consignación, Reparados, Segundas
- Desglose por categoría: Repuestos, Herramientas, EPPs, Insumos
- Evolución mensual del valor de inventario

**RF-079: Reporte de Diferencias de Inventario**
- Comparación físico vs. sistema
- Sobrantes y faltantes
- Valor de ajustes
- Responsables de almacén

**RF-080: Reporte de Consumo por Vehículo**
- Total de repuestos consumidos por vehículo en período
- Desglose por sistema (Motor, Transmisión, etc.)
- Comparación vs. promedio de la flota
- Identificación de vehículos con consumo anómalo

---

### 4.4 Módulo: Compras y Proveedores ⭐ MVP

**Descripción:** Gestión integral del proceso de compras desde la requisición hasta el pago, incluyendo portal de proveedores.

#### 4.4.1 Submódulo: Gestión de Proveedores

**RF-081: Registro de Proveedores**
- Información básica: NIT, Razón Social, Nombre Comercial
- Tipo: Persona Natural / Jurídica
- Clasificación contable: Régimen (Común/Simplificado), Tipo de operación, Responsabilidades tributarias
- Contacto: Dirección, Teléfono, Email, Sitio web
- Representante legal
- Certificaciones (Cámara de Comercio, RUT, Estados Financieros)
- Banco, tipo de cuenta, número de cuenta
- Productos/Servicios que ofrece
- Sistemas de especialidad (Motor, Transmisión, Llantas, etc.)
- Estado: Pre-registro, Activo, Suspendido, Bloqueado, Inactivo

**RF-082: Registro de Contactos de Proveedor (Agentes)**
- Múltiples contactos por proveedor
- Información: Nombre, Cargo, Email, Teléfono, Celular
- Rol: Comercial, Técnico, Facturación, Gerencia, Garantías
- Contacto principal

**RF-083: Portal de Proveedores - Acceso**
- Cada proveedor tiene usuario y contraseña
- Autenticación segura (2FA recomendado)
- Recuperación de contraseña
- Múltiples usuarios por proveedor (agentes)
- Roles: Administrador, Cotizador, Facturador

#### 4.4.2 Submódulo: Proceso de Compras (12 Etapas)

**Etapa 1: Identificación de la Necesidad**

**RF-084: Requisición Automática**
- Generada automáticamente cuando inventario < Mínimo
- Generada desde OT cuando repuesto no está en inventario
- Incluye: ítem, cantidad sugerida, urgencia, justificación
- Requisiciones se acumulan para procesamiento en lote

**RF-085: Requisición Manual**
- Funcionario de compras crea requisición manual
- Casos: Compras de equipos, servicios, ítems nuevos no catalogados
- Aprobación según monto (flujo de aprobaciones configurable)

**Etapa 2: Consolidación de Requisiciones**

**RF-086: Agrupación de Requisiciones**
- Agrupar requisiciones por sistema (Motor, Transmisión, etc.)
- Agrupar por proveedor preferido
- Agrupar por urgencia
- Generar Solicitud de Cotización agrupada

**Etapa 3: Invitación a Cotizar**

**RF-087: Selección de Proveedores a Invitar**
- Listar proveedores activos que suministran los ítems requeridos
- Sistema sugiere proveedores basado en:
  - Calificación del proveedor
  - Histórico de compras del ítem
  - Mejores precios históricos
- Funcionario de compras confirma/ajusta lista

**RF-088: Generación de Listado de Cotización**
- Crear documento con:
  - Consecutivo único de solicitud de cotización
  - Fecha de solicitud
  - Fecha límite para cotizar (configurable, ej: 3 días)
  - Listado de ítems con descripción, cantidad, urgencia
  - Especificaciones técnicas (marca preferida, referencia)
  - Condiciones de entrega
  - Términos de pago
- Publicar en portal de proveedores
- Notificar vía email a proveedores invitados

**Etapa 4: Recepción de Cotizaciones**

**RF-089: Portal de Proveedores - Cotizar**
- Proveedor accede a solicitudes de cotización pendientes
- Por cada ítem puede:
  - Cotizar con: Marca, Referencia, Precio unitario, Plazo de entrega, Vigencia de oferta
  - Indicar que no cotiza (motivo: No tiene, Precio no competitivo, Plazo muy corto)
  - Adjuntar ficha técnica (PDF)
- Proveedor confirma envío de cotización
- Sistema valida que todos los campos estén completos
- Registro de fecha/hora de recepción de cotización

**RF-090: Alerta de Proveedores que No Cotizan**
- Si proveedor entra al portal pero no cotiza antes del plazo, generar alerta
- Reporte de proveedores que no cotizan a tiempo
- Seguimiento para identificar problemas (capacitación, dificultades técnicas)

**Etapa 5: Análisis de Cotizaciones**

**RF-091: Comparación de Cotizaciones**
- Vista comparativa por ítem:
  - Proveedores que cotizaron
  - Precio, marca, referencia, plazo, vigencia
  - Indicador de mejor precio
  - Indicador de mejor plazo
  - Calificación del proveedor
  - Últimas 3 compras del ítem (precio histórico)
- Filtros: Por sistema, por proveedor, por urgencia

**RF-092: Análisis de Costo-Oportunidad**
- Si vehículo está parado por falta de repuesto:
  - Calcular Costo de Vehículo Parado por Hora (configurable)
  - Comparar diferencia de precio vs. diferencia de plazo × Costo/hora parado
  - Sugerir decisión óptima
- Ejemplo: Cotización A: $100K, 24 horas. Cotización B: $90K, 72 horas. Diferencia: $10K. Diferencia plazo: 48 horas. Costo parada: $50K/hora. Costo adicional por esperar: 48 × $50K = $2.4M. Decisión óptima: Cotización A.

**RF-093: Pre-selección Automática**
- Sistema pre-selecciona por cada ítem:
  - Si urgencia = Inmediata Y precio ≤ último precio comprado: Seleccionar mejor plazo
  - Si urgencia = Normal: Seleccionar mejor precio
  - Generar Orden de Compra automática para pre-seleccionados
- Funcionario de compras revisa y puede cambiar selección (con justificación)

**Etapa 6: Adjudicación**

**RF-094: Selección Manual de Adjudicación**
- Funcionario de compras puede cambiar pre-selección
- Debe registrar justificación (desplegable de motivos + observación)
- Motivos: Plazo más conveniente, Calidad superior, Proveedor más confiable, etc.

**RF-095: Generación de Orden de Compra**
- Agrupar ítems adjudicados por proveedor
- Generar una OC por proveedor con consecutivo único
- Contenido de OC:
  - Número de OC
  - Fecha
  - Proveedor
  - Condiciones de pago
  - Lugar de entrega
  - Listado de ítems con: Descripción, Marca, Referencia, Cantidad, Precio unitario, Total
  - Plazo de entrega acordado
  - Vigencia de la OC
  - Términos y condiciones
- Envío automático a proveedor vía portal y email

**Etapa 7: Aceptación de Orden de Compra**

**RF-096: Portal de Proveedores - Aceptar OC**
- Proveedor recibe notificación de OC adjudicada
- Revisa y confirma aceptación
- Si no acepta: registrar motivo
- Plazo para aceptar (configurable, ej: 24 horas)
- Alerta a compras cuando proveedor acepta OC

**RF-097: Gestión de OC No Aceptadas**
- Si proveedor no acepta o no responde a tiempo:
  - Anular OC
  - Re-adjudicar a segunda opción de proveedores que cotizaron
  - Si no hay segunda opción: repetir proceso desde invitación a cotizar, incluyendo nuevos proveedores

**Etapa 8: Programación de Entrega**

**RF-098: Portal de Proveedores - Programar Entrega**
- Proveedor programa fecha/hora de entrega
- Registra:
  - Fecha y hora estimada de arribo
  - Tipo de documento (Factura/Remisión)
  - Número de documento
  - Nombre e identificación de quien entrega
  - Placas del vehículo de transporte
- Información se comparte con módulo de Portería (control de acceso)
- Notificación a Compras de programación

**Etapa 9: Recepción en Portería**

**RF-099: Módulo de Portería**
- Llegada de proveedor: registrar hora de arribo
- Validar vs. programación
- Notificar a Compras
- Autorización de ingreso por Compras
- Registro de hora de autorización
- Tiempo de espera = Hora autorización - Hora arribo

**Etapa 10: Recepción de Mercancía en Compras**

**RF-100: Validación de Orden de Compra**
- Cargar OC correspondiente
- Registrar automáticamente fecha/hora de recepción
- Inspeccionar cada ítem vs. OC:
  - Marca
  - Referencia
  - Cantidad
  - Estado físico (empaque, producto)
- Posibles acciones:
  - Recibir conforme (total)
  - Recibir parcial (registrar cantidad recibida < cantidad pedida)
  - Rechazar total (devolución inmediata)
  - Rechazar parcial (recibir algunos ítems, devolver otros)
- Si se recibe con Factura y hay devolución parcial: registrar pendiente de Nota Crédito

**RF-101: Firma Electrónica de Recepción**
- Funcionario de compras firma electrónicamente como recibido
- Proveedor ingresa clave como entregado y conforme con resultado de inspección
- Si hay discrepancias: registro de no conformidad (requiere firma de proveedor)
- Generación de Entrada de Almacén en estado "Pendiente por Legalizar"
- Sello electrónico de Factura/Remisión

**Etapa 11: Entrega a Almacenista**

**RF-102: Traspaso Compras → Almacén**
- Funcionario de compras entrega mercancía a almacenista
- Almacenista consulta Entradas de Almacén pendientes por legalizar
- Valida inventario recibido vs. Entrada de Almacén
- Confirma recepción con firma electrónica
- Ubicación de mercancía en estantes/ubicaciones predefinidas
- Cambio de estado: "Ingresada en Almacén" (ahora disponible para OTs)

**Etapa 12: Registro Contable y Pago**

**RF-103: Asociación de Factura Electrónica**
- Portal de Proveedores: Proveedor carga Factura Electrónica (XML + PDF)
- Validación de Factura Electrónica (DIAN)
- Asociación de Factura a Entrada(s) de Almacén
- Validación de valores: Total factura = Total entradas asociadas
- Aprobación de Compras
- Envío a Contabilidad para causación
- Programación de pago según términos acordados

**RF-104: Seguimiento de Mora en Entregas**
- Sistema compara fecha de entrega comprometida vs. fecha real de entrega
- Si hay retraso:
  - Registrar mora (días de atraso)
  - Calcular valor en pesos de lo incumplido
  - Afectar calificación de proveedor (criterio "Cumplimiento")
  - Alerta a Compras de proveedores con entregas retrasadas

**RF-105: Alertas de Seguimiento de OC**
- Alerta si proveedor no programa entrega dentro de plazo
- Alerta si mercancía no llega en fecha programada
- Alerta si al recibir faltan ítems → liberar ítems faltantes para re-compra a otro proveedor
- Alerta si proveedor no despacha por mora en pago de cartera (afectar reporte de sobre-costos)

#### 4.4.3 Submódulo: Calificación de Proveedores

**RF-106: Calificación Automática Semestral**
- El sistema califica automáticamente cada semestre
- Proveedores calificados: Solo los que suministran productos (no servicios)
- Criterios de calificación:

**Criterio 1: Calidad (Peso 40%)**
- Puntos a restar = ($ Devuelto en semestre / $ Comprado en semestre) × 100
- Devoluciones consideradas:
  - Al momento de ingreso (no conforme)
  - Al momento de utilizar en OT (defectuoso)
  - En operación (falla prematura, garantía)
- Decisión de procedencia de rechazo de garantía (Mantenimiento + Compras)

**Criterio 2: Cumplimiento (Peso 20%)**
- Puntos a restar = ($ Incumplido en semestre / $ Comprado en semestre) × 100 × Factor
- Factor según días promedio de mora:
  - 1.0 si mora entre 0-3 días
  - 1.5 si mora entre 4-7 días
  - 2.0 si mora > 7 días
- Máximo a restar: 100 puntos

**Criterio 3: Reclamos y Garantías (Peso 10%)**
- Puntos a restar = (Cantidad reclamos no aceptados / Cantidad reclamos totales) × 100

**Criterio 4: Costo (Peso 30%)**
- Penalizar proveedores que coticen >15% del mejor precio
- Solo considerar cotizaciones con mínimo 3 proveedores y mismo plazo
- Puntos a restar = Cantidad de veces que cotizó >15% más caro

**Fórmula final:**
- CGS = 0.40 × Puntos Calidad + 0.20 × Puntos Cumplimiento + 0.10 × Puntos Garantías + 0.30 × Puntos Costo

**RF-107: Categorización de Proveedores**
- CGS ≥ 80: Proveedor Aceptable (sin acciones)
- 60 ≤ CGS < 80: Proveedor en Observación (notificar y dar 6 meses para mejorar)
- CGS < 60: Proveedor Restringido (bloquear en sistema, no invitar a cotizar)

**RF-108: Notificación y Plan de Mejora**
- Notificar automáticamente a proveedores con CGS < 80
- Indicar criterios que afectaron la calificación
- Solicitar plan de mejora (registrado en portal)
- Seguimiento de acciones de mejora
- Re-calificación extraordinaria si proveedor lo solicita

**RF-109: Reportes de Calificación**
- Ranking de proveedores por calificación
- Evolución de calificación por proveedor (histórico)
- Pareto de proveedores con mayor impacto en compras
- Proveedores bloqueados y motivo

#### 4.4.4 Submódulo: Consignación

**RF-110: Creación de Consignación**
- Seleccionar proveedor (previamente adjudicado)
- Seleccionar ítems a incluir (puede ser por sistema)
- Cada ítem tiene niveles Min/Max definidos
- Definir día y hora de corte de inventario (semanal, quincenal, mensual)
- Cargar contrato de consignación (PDF)
- Estado: Activa / Cerrada

**RF-111: Cargue Inicial de Inventario en Consignación**
- Recepción con remisión de proveedor (no factura)
- Registro de cantidades iniciales
- Generación de acta de inventario inicial (PDF)
- Incluye: cantidad, precio unitario, valor total por ítem, valor total de consignación

**RF-112: Cortes de Inventario Automáticos**
- Sistema ejecuta corte en fecha/hora programada
- Acumula todas las salidas desde último corte
- Genera Pre-Factura automáticamente
- Pre-Factura contiene: Ítems consumidos, cantidad, precio unitario, total
- Estado: Pendiente por Facturar

**RF-113: Diferenciación Vehículos Propios vs. Terceros**
- Sistema identifica si OT es de vehículo propio o tercero (administrado)
- Genera Pre-Facturas separadas:
  - Pre-Factura Propios
  - Pre-Factura Terceros (incluye IVA)
- Proveedor debe facturar cada tipo por separado

**RF-114: Relación Pre-Factura - Factura**
- Portal de Proveedores: Proveedor lista Pre-Facturas pendientes
- Puede seleccionar varias Pre-Facturas para recoger en una Factura
- Sube Factura Electrónica
- Sistema valida: Total Factura = Suma de Pre-Facturas seleccionadas
- Compras aprueba y asocia
- Envío a Contabilidad para pago

**RF-115: Rellenos de Inventario**
- Sistema calcula faltante vs. niveles Max
- Programación de cita de relleno en Portería (día asignado por proveedor)
- Proveedor ingresa con remisión
- Recepción de relleno (validación de cantidades)
- Actualización de inventario en consignación

**RF-116: Retiro de Inventario en Consignación**
- Motivos: Baja rotación, Alto precio, Incumplimiento de proveedor
- Acta de devolución
- Si ya hubo corte facturado: inventario restante pasa a inventario propio (como si fuera compra)
- Si no ha habido corte: devolución simple sin afectar contabilidad

**RF-117: Cierre de Consignación**
- Corte final de inventario
- Generación de Pre-Factura final
- Devolución de inventario restante o compra total
- Acta de cierre de consignación
- Archivo de contrato

#### 4.4.5 Submódulo: Reportes de Compras

**RF-118: Pareto de Compras por Proveedor**
- Para un período: Total comprado por proveedor (repuestos + servicios)
- Orden descendente
- Columnas: Repuestos, Servicios, Total, % Participación, % Acumulado
- Gráfico de Pareto

**RF-119: Pareto de Compras por Sistema**
- Similar a RF-118, agrupado por sistema (Motor, Transmisión, etc.)

**RF-120: Pareto de Compras por Ítem/Servicio**
- Similar a RF-118, agrupado por ítem individual
- Permite identificar ítems de mayor gasto para negociar mejores condiciones

**RF-121: Reporte de Tiempo Perdido por Falta de Repuestos**
- Acumulado de tiempo (horas) de OTs pausadas por falta de repuestos
- Desglose por sistema
- Desglose por ítems de matriz de control vs. fuera de matriz
- Orden descendente por tiempo perdido
- % del total
- Gráfico de Pareto

**RF-122: Reporte de Duración de Componentes**
- Duración promedio (km, horas, meses) por componente
- Marca, referencia
- Costo por km/hora
- Comparación entre marcas
- Mejor costo/beneficio

**RF-123: Reporte de Sobre-Costos por Mora en Pago**
- Cuando proveedor no despacha por mora en pago de cartera
- Sistema detecta que precio de compra a proveedor alterno es mayor
- Acumular sobre-costo mensual
- Identificar proveedores afectados
- Total de sobre-costo en período

**RF-124: Reporte de Proveedores que No Cotizan**
- Proveedores invitados vs. que cotizaron
- Proveedores que entraron al portal pero no enviaron cotización
- Análisis de causas
- Período de análisis

**RF-125: Reporte de Tiempo de Suministro de Repuestos**
- Tiempo promedio desde OC hasta entrega en almacén
- Por proveedor
- Comparación vs. plazo comprometido
- % de cumplimiento de plazos

**RF-126: Reporte de Tiempo de Atención a Proveedor**
- Tiempo desde arribo en portería hasta finalización de recepción
- Identificar cuellos de botella
- Mejorar eficiencia de recepción

**RF-127: Reporte de Variación de Costos (Pareto)**
- Impacto de variación de precios en costos totales
- Fórmula: (Precio final - Precio inicial) × Consumo total en período
- Para ítems con equivalentes: usar precio promedio
- Orden descendente por impacto
- Permite focalizar negociaciones

---

## 5. REQUERIMIENTOS FUNCIONALES DETALLADOS

### 5.1 Módulos Complementarios (Fuera del MVP pero Importantes)

#### 5.1.1 Módulo: Gestión de Empleados y Recursos Humanos

**RF-128: Gestión de Empleados**
- Registro completo de empleados: Datos personales, contacto, formación
- Vinculación: Tipo de contrato, cargo, dependencia, gerencia, sede, turno
- Documentos: Contrato, hoja de vida, certificados, exámenes médicos
- Estados: Activo, Licencia, Incapacidad, Vacaciones, Retirado

**RF-129: Control de Dotaciones y EPPs**
- Asignación según cargo (plantillas)
- Control de entregas (fecha, cantidad)
- Renovaciones periódicas (anual, semestral)
- Solicitudes de cambio por deterioro (con aprobación)
- Inventario de EPPs asignados por funcionario

**RF-130: Gestión de Aspectos Disciplinarios**
- Registro de llamados de atención, sanciones
- Tipos: Verbal, Escrito, Suspensión, Terminación
- Motivos codificados
- Documentos de respaldo
- Historial disciplinario
- Proceso de descargos

#### 5.1.2 Módulo: Siniestros y Autoseguro

**RF-131: Registro de Siniestros**
- Información del siniestro: Fecha, hora, lugar, descripción
- Vehículos involucrados (propios y terceros)
- Lesionados
- Testigos
- Registro fotográfico, croquis, video
- Autoridades que atendieron (Policía, Tránsito)
- Comparendos

**RF-132: Gestión de Autoseguro**
- Registro de placa en autoseguro
- Deducibles por tipo de siniestro
- Proceso de reclamación interna
- Auditoría de siniestros
- Indicadores: Frecuencia, Severidad, Costo promedio

#### 5.1.3 Módulo: Pruebas de Productos

**RF-133: Registro de Pruebas**
- Solicitud de prueba de proveedor
- Acuerdo de condiciones (gratuito, con descuento, plazo de prueba)
- Ingreso de producto en inventario (marcado como "Prueba")
- Asignación a OT específica
- Registro de resultados:
  - Rendimiento
  - Calidad
  - Duración
  - Costo/beneficio
- Decisión: Aprobado (habilitar para compra) / Rechazado (devolver a proveedor)
- Memoria técnica de pruebas

---

## 6. REQUERIMIENTOS NO FUNCIONALES

### 6.1 Rendimiento

**RNF-001: Tiempo de Respuesta**
- Consultas simples: < 2 segundos
- Consultas complejas (reportes): < 10 segundos
- Transacciones (guardar, actualizar): < 3 segundos
- Carga de dashboards: < 5 segundos

**RNF-002: Concurrencia**
- Soportar 100-500 usuarios concurrentes sin degradación
- Picos de hasta 800 usuarios concurrentes (casos excepcionales)

**RNF-003: Disponibilidad**
- 99.5% de disponibilidad (permitir máximo 43 horas de downtime al año)
- Ventanas de mantenimiento programadas fuera de horario laboral

**RNF-004: Escalabilidad**
- Arquitectura que permita escalar horizontalmente (más servidores)
- Preparado para crecimiento de 50% en usuarios sin cambios arquitectónicos

### 6.2 Seguridad

**RNF-005: Autenticación**
- Autenticación basada en usuario y contraseña
- Políticas de contraseña fuerte (min. 8 caracteres, mayúsculas, números, símbolos)
- Bloqueo de cuenta tras 5 intentos fallidos
- Sesiones con timeout de inactividad (30 minutos)
- Soporte para 2FA (Autenticación de Dos Factores) - Opcional

**RNF-006: Autorización**
- Control de acceso basado en roles (RBAC)
- Permisos granulares por módulo y funcionalidad
- Segregación de funciones (principio de menor privilegio)

**RNF-007: Auditoría**
- Log de todas las transacciones críticas (CRUD de datos sensibles)
- Registro de: Usuario, Fecha/Hora, Acción, Datos anteriores, Datos nuevos
- Logs inmutables
- Retención mínima: 5 años (cumplimiento normativo)

**RNF-008: Encriptación**
- Comunicación HTTPS (TLS 1.2+)
- Contraseñas hasheadas (bcrypt, scrypt)
- Datos sensibles encriptados en BD (ej: números de cuenta bancaria)

**RNF-009: Protección contra Amenazas**
- Protección contra SQL Injection
- Protección contra XSS (Cross-Site Scripting)
- Protección contra CSRF (Cross-Site Request Forgery)
- Rate limiting para prevenir ataques de fuerza bruta
- Validación de entrada en cliente y servidor

### 6.3 Usabilidad

**RNF-010: Diseño Responsivo**
- Interfaz adaptable a: Desktop (1920×1080), Laptop (1366×768), Tablet (768×1024), Móvil (375×667)
- Touch-friendly para tablets y móviles

**RNF-011: Experiencia de Usuario**
- Navegación intuitiva (máximo 3 clics para llegar a cualquier función)
- Mensajes de error claros y accionables
- Feedback visual inmediato (loaders, confirmaciones)
- Soporte para teclado (shortcuts, tab navigation)

**RNF-012: Accesibilidad**
- Cumplimiento WCAG 2.1 Nivel AA (mínimo)
- Contraste de colores adecuado
- Etiquetas descriptivas para lectores de pantalla
- Navegación por teclado completa

**RNF-013: Idioma**
- Soporte inicial: Español (Colombia)
- Preparado para internacionalización (i18n) futura

### 6.4 Compatibilidad

**RNF-014: Navegadores**
- Soporte para últimas 2 versiones de: Chrome, Firefox, Edge, Safari
- No requerido: Internet Explorer

**RNF-015: Dispositivos**
- Desktop: Windows 10+, macOS 10.14+, Linux (Ubuntu 20.04+)
- Móvil: Android 9+, iOS 13+

**RNF-016: Resoluciones**
- Mínimo soportado: 1024×768
- Óptimo: 1920×1080 o superior

### 6.5 Mantenibilidad

**RNF-017: Código Limpio**
- Código auto-documentado (nombres descriptivos)
- Comentarios solo cuando lógica no es evidente
- Seguir principios SOLID
- Arquitectura en capas bien definida

**RNF-018: Versionamiento**
- Código en Git con estrategia de branching (GitFlow o similar)
- Commits descriptivos
- Tags para releases

**RNF-019: Pruebas**
- Cobertura de pruebas unitarias > 70%
- Pruebas de integración para flujos críticos
- Pruebas de regresión automatizadas (E2E con Cypress, Playwright o Selenium)

**RNF-020: Documentación**
- Documentación de API (Swagger/OpenAPI)
- Manual de usuario
- Manual técnico (arquitectura, deployment, troubleshooting)
- Scripts de despliegue documentados

### 6.6 Portabilidad

**RNF-021: Base de Datos**
- PostgreSQL 16+ (preferido)
- Migraciones versionadas (Flyway o Liquibase)
- Evitar SQL específico de motor (mantener portabilidad ANSI SQL)

**RNF-022: Despliegue**
- Soporte para contenedores (Docker)
- Preparado para orquestación (Kubernetes) - Opcional
- Variables de entorno para configuración (12-factor app)

**RNF-023: Integración**
- APIs REST bien documentadas para integraciones futuras
- Webhooks para eventos críticos (opcional)
- Exportación de datos en formatos estándar (CSV, Excel, PDF)

### 6.7 Cumplimiento Normativo

**RNF-024: Facturación Electrónica**
- Integración con facturación electrónica DIAN (Colombia)
- Soporte para XML de factura, nota débito, nota crédito
- Validación de numeración autorizada
- Almacenamiento de representación gráfica (PDF)

**RNF-025: Protección de Datos Personales**
- Cumplimiento Ley 1581 de 2012 (Colombia)
- Política de privacidad
- Consentimiento informado para tratamiento de datos
- Derecho de acceso, rectificación, supresión

**RNF-026: RESPEL (Residuos Peligrosos)**
- Registro de manejo de RESPEL según normativa ambiental
- Trazabilidad de certificados de disposición final
- Reportes para autoridades ambientales

### 6.8 Respaldo y Recuperación

**RNF-027: Backups**
- Backup completo diario (automático)
- Backup incremental cada 6 horas
- Retención: 30 días online, 12 meses en almacenamiento frío
- Backups almacenados en ubicación geográfica diferente

**RNF-028: Recuperación ante Desastres**
- RPO (Recovery Point Objective): < 6 horas
- RTO (Recovery Time Objective): < 4 horas
- Plan de recuperación documentado y probado trimestralmente

---

## 7. INTEGRACIONES

### 7.1 Integraciones Requeridas

**INT-001: Contabilidad**
- Exportación de transacciones contables (compras, ventas, nómina)
- Formato: Archivo plano delimitado o integración API
- Frecuencia: Diaria o en tiempo real

**INT-002: Facturación Electrónica DIAN**
- Generación de XML según estándar DIAN
- Envío a proveedor tecnológico autorizado
- Recepción de respuesta (CAE, CUFE)
- Almacenamiento de representación gráfica

**INT-003: Bancos (Opcional)**
- Consulta de saldos
- Descarga de extractos
- Conciliación automática

**INT-004: Proveedores Tecnológicos**
- Sistema de rastreo GPS de vehículos (para kilometraje automático)
- Sistema de control de combustible
- Sistema de peajes

### 7.2 APIs a Exponer

**API-001: Datos Maestros**
- Consulta de vehículos, empleados, proveedores, ítems
- Operaciones: GET (solo lectura para sistemas externos)

**API-002: Telemetría de Vehículos**
- Recepción de datos de GPS: Ubicación, Kilometraje, Velocidad
- Método: POST
- Formato: JSON
- Autenticación: API Key

**API-003: Indicadores**
- Exposición de KPIs para BI externo
- Método: GET
- Formato: JSON
- Autenticación: OAuth 2.0

---

## 8. GLOSARIO

| Término | Definición |
|---------|------------|
| **OT** | Orden de Trabajo. Documento que autoriza la ejecución de mantenimiento o reparación. |
| **EPP** | Elementos de Protección Personal (casco, guantes, botas, gafas, etc.) |
| **RESPEL** | Residuos Peligrosos (aceites usados, baterías, filtros contaminados, etc.) |
| **Matriz de Control** | Lista de repuestos críticos que deben mantenerse en inventario con niveles Min/Max. |
| **Consignación** | Modalidad de inventario donde el proveedor mantiene el stock en instalaciones de la empresa, pero la propiedad solo se transfiere al momento de consumo. |
| **Componente Reparable** | Elemento que puede ser removido del vehículo, reparado y reinstalado (motor, transmisión, alternador, etc.) |
| **Serializado** | Ítem que tiene un serial único que permite trazabilidad individual. |
| **Lead Time** | Tiempo que tarda el proveedor en entregar un repuesto desde que se hace el pedido. |
| **Pareto** | Principio 80/20. En este contexto, análisis que identifica el 20% de elementos que generan el 80% del impacto (costos, consumo, fallas, etc.) |
| **Isla de Trabajo** | Espacio físico en el taller donde se ejecutan las OTs. Puede ser especializada (motor, llantas, etc.) |
| **Patinador** | Funcionario encargado de transportar repuestos entre almacén y taller. |
| **Rutina de Mantenimiento** | Conjunto de tareas de mantenimiento preventivo que se ejecutan periódicamente. |
| **Campaña** | Conjunto de intervenciones masivas en la flota por mejora técnica, recall o problema recurrente. |
| **Portal de Proveedores** | Interfaz web donde proveedores externos gestionan cotizaciones, órdenes de compra, facturas, etc. |
| **CGS** | Calificación General del Proveedor (0-100 puntos). |
| **Pre-Factura** | Documento interno generado automáticamente en consignación con el consumo acumulado. El proveedor luego emite factura oficial. |
| **Vehículo Propio** | Vehículo de propiedad de la empresa. |
| **Vehículo Tercero/Administrado** | Vehículo de terceros al que la empresa presta servicios de mantenimiento (se factura). |
| **In-House** | Contratista que trabaja dentro de las instalaciones de la empresa. |
| **Desvare** | Atención de vehículo varado en carretera. |
| **Reencauche** | Proceso de renovación de llantas usadas mediante aplicación de nueva banda de rodamiento. |
| **Profundidad de Labrado** | Medida de la profundidad de las ranuras de la llanta (indicador de desgaste). |
| **SOAT** | Seguro Obligatorio de Accidentes de Tránsito. |
| **Tecnomecánica** | Revisión técnico-mecánica obligatoria para vehículos. |
| **DIAN** | Dirección de Impuestos y Aduanas Nacionales (Colombia). |
| **CUFE** | Código Único de Factura Electrónica. |
| **CAE** | Código de Autorización Electrónica (facturación). |

---

## CONTROL DE VERSIONES

| Versión | Fecha | Autor | Cambios |
|---------|-------|-------|---------|
| 1.0 | 31-Dic-2025 | Equipo TRANSER | Versión inicial completa |

---

## APROBACIONES

| Rol | Nombre | Firma | Fecha |
|-----|--------|-------|-------|
| Gerente de Tecnología | | | |
| Gerente de Mantenimiento | | | |
| Gerente de Compras | | | |
| Gerente General | | | |

---

**FIN DEL DOCUMENTO**
