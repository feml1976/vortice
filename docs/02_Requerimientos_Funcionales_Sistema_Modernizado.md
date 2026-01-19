# REQUERIMIENTOS FUNCIONALES
## Sistema TRANSER Modernizado

**Fecha:** 2025-12-30
**Proyecto:** Modernización Sistema TRANSER
**Origen:** Oracle Forms 6i + Oracle 11g
**Destino:** Aplicación Web Moderna + PostgreSQL

---

## 1. INTRODUCCION

Este documento establece los requerimientos funcionales para la modernización del sistema TRANSER, un ERP integral para empresas de transporte de carga por carretera. El sistema actual está desarrollado en Oracle Forms 6i sobre Oracle 11g y requiere actualización tecnológica manteniendo y mejorando su funcionalidad actual.

### 1.1. Objetivos del Proyecto

1. **Modernizar la tecnología** sin perder funcionalidad existente
2. **Mejorar la experiencia de usuario** con interfaces web modernas
3. **Optimizar el rendimiento** aprovechando las capacidades de PostgreSQL
4. **Facilitar el acceso** mediante aplicaciones web responsivas
5. **Integrar nuevas tecnologías** (API REST, facturación electrónica, BI)
6. **Garantizar la escalabilidad** para crecimiento futuro

### 1.2. Alcance

**Módulos a Desarrollar:** 15 módulos funcionales
**Usuarios Estimados:** 100-500 usuarios concurrentes
**Dispositivos:** Web Desktop, Tablets, Móviles
**Geografía:** Multi-país (Colombia, Venezuela, potencial expansión)

---

## 2. REQUERIMIENTOS GENERALES DEL SISTEMA

### 2.1. Arquitectura General

**RF-001: Arquitectura Web Moderna**
- El sistema DEBE implementarse como aplicación web progresiva (PWA)
- DEBE funcionar en navegadores modernos (Chrome, Firefox, Edge, Safari)
- DEBE ser responsiva adaptándose a desktop, tablet y móvil
- DEBE permitir trabajo offline para módulos críticos (lectura de datos)

**RF-002: Arquitectura de Microservicios**
- El sistema PUEDE implementarse con arquitectura de microservicios
- DEBE exponer API REST para integración con sistemas externos
- DEBE implementar API Gateway para gestión centralizada

**RF-003: Multi-tenancy**
- El sistema DEBE soportar múltiples empresas (multi-tenant)
- DEBE aislar datos por empresa garantizando seguridad
- DEBE permitir configuración independiente por empresa

### 2.2. Seguridad y Acceso

**RF-004: Autenticación y Autorización**
- DEBE implementar autenticación multifactor (MFA) opcional
- DEBE soportar Single Sign-On (SSO) con proveedores SAML/OAuth2
- DEBE mantener sesiones con timeout configurable
- DEBE registrar todos los intentos de acceso

**RF-005: Control de Acceso Basado en Roles (RBAC)**
- DEBE mantener sistema de perfiles y permisos granulares
- DEBE permitir asignación de múltiples perfiles a un usuario
- DEBE validar permisos a nivel de: módulo, formulario, campo, acción
- DEBE heredar estructura actual de PERFILES, PERMISOS, PERFILES_USUARIOS

**RF-006: Auditoría Completa**
- DEBE registrar TODAS las operaciones (INSERT, UPDATE, DELETE)
- DEBE almacenar: usuario, fecha/hora, IP, antes/después
- DEBE mantener auditoría por al menos 7 años (requisito legal Colombia)
- DEBE permitir consulta y reporte de auditoría por usuarios autorizados

### 2.3. Experiencia de Usuario

**RF-007: Interfaz de Usuario Moderna**
- DEBE implementar diseño Material Design o similar
- DEBE ser intuitiva requiriendo mínima capacitación
- DEBE incluir tooltips y ayudas contextuales
- DEBE soportar temas claro/oscuro

**RF-008: Usabilidad y Accesibilidad**
- DEBE cumplir WCAG 2.1 nivel AA mínimo
- DEBE soportar navegación por teclado
- DEBE incluir búsquedas predictivas y autocompletado
- DEBE mostrar indicadores visuales de campos requeridos/opcionales

**RF-009: Notificaciones y Alertas**
- DEBE enviar notificaciones en tiempo real (websockets)
- DEBE soportar notificaciones por: email, SMS, push web
- DEBE alertar vencimientos (licencias, SOAT, documentos, mantenimientos)
- DEBE permitir configuración de alertas personalizadas

---

## 3. MODULOS FUNCIONALES

### MODULO 1: GESTION DE FLOTA Y TRANSPORTE

#### 3.1.1. Gestión de Vehículos

**RF-010: Catálogo de Vehículos**
- DEBE registrar vehículos con: placa, marca, modelo, año, color, clase
- DEBE almacenar documentación: SOAT, revisión técnico-mecánica, tarjeta propiedad
- DEBE alertar vencimientos con 30, 15 y 7 días de anticipación
- DEBE mantener histórico completo de cambios
- DEBE vincular vehículo a propietario (propio/afiliado)
- DEBE registrar características técnicas (cilindraje, capacidad, ejes, etc.)

**RF-011: Catálogo de Trailers**
- DEBE registrar trailers similar a vehículos
- DEBE controlar peso bruto vehicular y capacidad de carga
- DEBE permitir múltiples tipos (plataforma, furgón, tolva, etc.)
- DEBE validar compatibilidad vehículo-trailer

**RF-012: Gestión de Conductores**
- DEBE registrar conductores con: identificación, nombres, dirección, ciudad, teléfonos
- DEBE validar licencia de conducción (número, categoría, vencimiento, lugar expedición)
- DEBE registrar tipo afiliación (propio/afiliado)
- DEBE mantener calificación de desempeño
- DEBE registrar estudios de seguridad
- DEBE validar documentos: licencia, examen médico, curso seguridad vial
- DEBE permitir restricciones por conductor

**RF-013: Gestión de Propietarios**
- DEBE registrar propietarios de vehículos afiliados
- DEBE vincular múltiples vehículos a un propietario
- DEBE gestionar datos bancarios para pagos
- DEBE mantener histórico de relación

**RF-014: Enlaces Operativos (Conductor-Vehículo-Trailer)**
- DEBE crear enlace asignando conductor + vehículo + trailer (opcional)
- DEBE validar que conductor tenga licencia vigente
- DEBE validar que vehículo tenga documentación vigente
- DEBE prevenir enlaces duplicados (mismo recurso en dos viajes)
- DEBE registrar fecha inicio y fin de enlace
- DEBE mantener histórico completo

#### 3.1.2. Viajes y Operaciones

**RF-015: Gestión de Viajes (TABLA FALTANTE - CRITICA)**
- DEBE crear viaje con: planilla, cliente, origen, destino, ruta, carga
- DEBE asignar enlace (conductor-vehículo-trailer)
- DEBE calcular fletes según tarifa y ruta
- DEBE registrar estados: planeado, en tránsito, finalizado, legalizado
- DEBE controlar fechas: programada, salida real, llegada real
- DEBE vincular documentos de viaje (remesa, manifiesto, etc.)
- DEBE generar número de planilla automático

**RF-016: Rutas (TABLA FALTANTE - CRITICA)**
- DEBE crear catálogo de rutas predefinidas
- DEBE registrar origen, destino, puntos intermedios
- DEBE almacenar distancia en kilómetros
- DEBE definir tarifa base por ruta
- DEBE considerar peajes y gastos de ruta
- DEBE permitir rutas alternativas

**RF-017: Control de Kilometraje**
- DEBE registrar kilometraje al iniciar y finalizar viaje
- DEBE calcular kilómetros recorridos automáticamente
- DEBE mantener historial de kilometraje por vehículo
- DEBE alertar discrepancias vs. ruta estimada (variación > 10%)
- DEBE calcular costo por kilómetro

**RF-018: Legalización de Viajes**
- DEBE legalizar viaje registrando gastos reales
- DEBE adjuntar soportes digitales (fotos de facturas, tickets)
- DEBE comparar presupuestado vs. ejecutado
- DEBE calcular utilidad/pérdida del viaje
- DEBE aprobar/rechazar legalización con flujo de autorización

#### 3.1.3. Combustible

**RF-019: Control de Combustible**
- DEBE registrar diligenciamientos (carga de combustible)
- DEBE validar contra cupo asignado
- DEBE registrar: estación, galones, precio, odómetro
- DEBE calcular rendimiento (km/galón)
- DEBE alertar rendimientos anormales
- DEBE integrar con tarjetas de combustible

**RF-020: Legalización de Combustible**
- DEBE legalizar consumo vs. planilla/volante
- DEBE validar facturas de estación
- DEBE detectar inconsistencias y fraudes
- DEBE generar reportes de consumo por vehículo/conductor

#### 3.1.4. Escoltas

**RF-021: Gestión de Escoltas**
- DEBE asignar escoltas a viajes que lo requieran
- DEBE registrar empresa de escolta y vehículo
- DEBE controlar tarifa según tipo de carga
- DEBE validar certificaciones de la empresa

---

### MODULO 2: TALLER Y MANTENIMIENTO

#### 3.2.1. Ordenes de Trabajo

**RF-022: Creación de Ordenes de Trabajo (OT)**
- DEBE crear OT con: vehículo/trailer, tipo (preventivo/correctivo), prioridad
- DEBE asignar mecánico responsable
- DEBE generar número consecutivo automático
- DEBE registrar kilometraje al ingreso
- DEBE estimar tiempo y costo
- DEBE soportar OT para terceros (facturación externa)

**RF-023: Detalle de Ordenes de Trabajo**
- DEBE agregar múltiples tareas a una OT
- DEBE registrar por tarea: descripción, mecánico, tiempo estimado/real, estado
- DEBE vincular elementos (repuestos/servicios) por tarea
- DEBE calcular costo total (mano obra + repuestos + externos)
- DEBE permitir devolución de elementos no usados

**RF-024: Ejecución de Ordenes de Trabajo**
- DEBE cambiar estados: creada, en proceso, pausada, finalizada, cerrada
- DEBE registrar tiempo real de ejecución por tarea
- DEBE permitir agregar observaciones y hallazgos
- DEBE solicitar aprobación para trabajos adicionales no planeados
- DEBE tomar fotos del antes/después

**RF-025: Cierre de Ordenes de Trabajo**
- DEBE validar que todas las tareas estén completadas
- DEBE generar informe de trabajos realizados
- DEBE actualizar inventarios consumidos
- DEBE calcular costo total final
- DEBE requerir visto bueno de supervisor
- DEBE actualizar hoja de vida del vehículo

#### 3.2.2. Mantenimientos Preventivos

**RF-026: Plan de Mantenimiento**
- DEBE definir plan de mantenimiento por tipo de vehículo
- DEBE programar tareas por: tiempo (meses) o kilometraje
- DEBE incluir: cambio aceite, filtros, revisión frenos, alineación, etc.
- DEBE replicar planes a múltiples vehículos
- DEBE permitir mantenimientos personalizados

**RF-027: Programación de Mantenimientos**
- DEBE calcular próximo mantenimiento basado en km o fecha
- DEBE generar alertas preventivas (80% del intervalo)
- DEBE crear OT automática al alcanzar umbral
- DEBE considerar disponibilidad de taller y repuestos
- DEBE permitir adelantar/postergar con justificación

**RF-028: Histórico de Mantenimientos**
- DEBE mantener registro completo de todos los mantenimientos
- DEBE mostrar cumplimiento del plan (% ejecutado)
- DEBE generar hoja de vida del vehículo
- DEBE identificar problemas recurrentes

#### 3.2.3. Fallas y Diagnósticos

**RF-029: Reporte de Fallas**
- DEBE permitir reporte de falla por conductor o mecánico
- DEBE categorizar falla por sistema (motor, frenos, eléctrico, etc.)
- DEBE registrar síntomas y severidad
- DEBE adjuntar fotos/videos
- DEBE permitir reporte desde móvil

**RF-030: Diagnóstico Técnico**
- DEBE registrar diagnóstico detallado por mecánico
- DEBE identificar causa raíz
- DEBE recomendar acciones correctivas
- DEBE estimar costo y tiempo de reparación
- DEBE vincular a OT de reparación

**RF-031: Análisis de Fallas**
- DEBE generar estadísticas de fallas por vehículo/sistema
- DEBE identificar vehículos problemáticos
- DEBE sugerir reemplazo vs. reparación
- DEBE calcular MTBF (tiempo medio entre fallas)

#### 3.2.4. Llantas

**RF-032: Gestión de Llantas**
- DEBE registrar llantas con: serial, referencia, marca, medida
- DEBE controlar ubicación: posición en vehículo, almacén, reencauche, baja
- DEBE registrar presión de aire en inspecciones
- DEBE controlar profundidad de labrado
- DEBE programar rotación de llantas

**RF-033: Montaje y Desmontaje**
- DEBE registrar montaje indicando vehículo y posición
- DEBE registrar desmontaje con motivo (desgaste, daño, rotación)
- DEBE mantener histórico de montajes por llanta
- DEBE calcular kilometraje recorrido por llanta
- DEBE alertar llantas próximas a cambio

**RF-034: Reencauche**
- DEBE enviar llantas a reencauche externo
- DEBE controlar estado y proveedor
- DEBE registrar número de reencauches (máximo configurable)
- DEBE validar viabilidad técnica de reencauche

#### 3.2.5. Reparaciones Externas

**RF-035: Orden de Reparación Externa**
- DEBE crear orden para taller externo
- DEBE registrar proveedor, vehículo, trabajos solicitados
- DEBE enviar elementos (repuestos) con el vehículo
- DEBE controlar fecha entrega estimada
- DEBE validar garantías aplicables

**RF-036: Recepción de Reparación Externa**
- DEBE recibir vehículo validando trabajos realizados
- DEBE recibir elementos devueltos
- DEBE registrar factura del proveedor
- DEBE validar garantía de los trabajos

**RF-037: Gestión de Garantías**
- DEBE registrar garantías de trabajos externos
- DEBE alertar si falla recurrente está en garantía
- DEBE facilitar reclamo a proveedor
- DEBE dar seguimiento a reex (reparaciones por garantía)

---

### MODULO 3: INVENTARIOS Y ALMACENES

#### 3.3.1. Maestro de Items

**RF-038: Catálogo de Items**
- DEBE registrar items con: código, descripción, unidad medida, tipo
- DEBE soportar tipos: repuesto, servicio, herramienta, consumible
- DEBE mantener código alterno (referencia fabricante)
- DEBE incluir ficha técnica y especificaciones
- DEBE adjuntar imágenes y documentos
- DEBE definir si es serializado

**RF-039: Equivalencias de Items**
- DEBE definir items equivalentes/sustitutos
- DEBE sugerir equivalencias en requisiciones
- DEBE alertar cambio de item equivalente
- DEBE mantener histórico de equivalencias

**RF-040: Items por Proveedor**
- DEBE vincular item con proveedores habituales
- DEBE registrar precio por proveedor
- DEBE indicar proveedor preferido
- DEBE registrar tiempo entrega por proveedor
- DEBE considerar en sugerencias de compra

#### 3.3.2. Control de Almacenes

**RF-041: Gestión de Almacenes**
- DEBE crear almacenes con: código, nombre, ciudad, encargado
- DEBE soportar almacenes centrales y satélites
- DEBE definir tipo de almacén (repuestos, herramientas, llantas)
- DEBE controlar acceso por usuario

**RF-042: Inventario por Almacén**
- DEBE controlar existencias en tiempo real
- DEBE manejar múltiples líneas de inventario:
  - Disponible
  - En consignación
  - Reparados (re-manufacturados)
  - Segunda (usados en buen estado)
  - Desechos
  - En tránsito
  - En vehículo
- DEBE alertar stock mínimo y máximo
- DEBE calcular punto de reorden

**RF-043: Trazabilidad de Items Serializados**
- DEBE registrar serial único por item
- DEBE rastrear ubicación actual (almacén, vehículo, taller externo)
- DEBE mantener histórico completo de movimientos
- DEBE vincular a OT donde fue instalado
- DEBE controlar garantía individual

#### 3.3.3. Movimientos de Inventario

**RF-044: Entradas de Almacén**
- DEBE registrar entrada por: compra, devolución, traslado, ajuste
- DEBE vincular a orden de compra o remisión proveedor
- DEBE validar cantidades vs. orden
- DEBE registrar ubicación física (estante/anaquel)
- DEBE actualizar inventario en tiempo real

**RF-045: Salidas de Almacén**
- DEBE registrar salida por: requisición, venta, traslado, ajuste, baja
- DEBE validar existencias antes de aprobar
- DEBE vincular a OT o cliente
- DEBE requerir autorización según monto
- DEBE actualizar inventario en tiempo real

**RF-046: Requisiciones**
- DEBE crear requisición desde taller, oficina, vehículo
- DEBE validar disponibilidad
- DEBE reservar items al aprobar requisición
- DEBE generar picking list para almacenista
- DEBE permitir entrega parcial
- DEBE devolver sobrantes

**RF-047: Traslados entre Almacenes**
- DEBE crear orden de traslado
- DEBE generar remisión de salida y entrada
- DEBE controlar estado: creado, en tránsito, recibido
- DEBE validar cantidades recibidas vs. enviadas
- DEBE actualizar ambos inventarios

**RF-048: Ajustes de Inventario**
- DEBE permitir ajuste con justificación
- DEBE requerir autorización especial
- DEBE registrar responsable
- DEBE afectar contabilidad
- DEBE quedar en auditoría

#### 3.3.4. Inventario Físico

**RF-049: Toma Física de Inventario**
- DEBE programar inventarios cíclicos o totales
- DEBE generar listados de conteo
- DEBE permitir captura desde dispositivo móvil
- DEBE comparar físico vs. sistema
- DEBE generar diferencias para ajuste
- DEBE requerir reconteo si diferencia > umbral

**RF-050: Arqueo de Tarjetas (Control de Llantas)**
- DEBE realizar conteo físico de llantas
- DEBE validar ubicación vs. sistema
- DEBE detectar llantas no registradas
- DEBE generar informe de inconsistencias

#### 3.3.5. Inventario en Consignación

**RF-051: Gestión de Consignación**
- DEBE controlar inventario en consignación de proveedores
- DEBE registrar entrada sin afectar compras
- DEBE facturar al consumir (no al recibir)
- DEBE generar relación de consumos periódica
- DEBE controlar vencimientos de consignación

---

### MODULO 4: COMPRAS Y PROVEEDORES

#### 3.4.1. Gestión de Proveedores

**RF-052: Maestro de Proveedores**
- DEBE registrar proveedores con: NIT, nombre, dirección, contactos
- DEBE clasificar por tipo: repuestos, servicios, combustible, llantas
- DEBE registrar datos bancarios
- DEBE validar información tributaria (RUT)
- DEBE registrar actividad económica
- DEBE indicar si es gran contribuyente

**RF-053: Calificación de Proveedores**
- DEBE evaluar proveedores por: calidad, tiempo entrega, precio, servicio
- DEBE calcular calificación ponderada
- DEBE mantener histórico de calificaciones
- DEBE generar ranking de proveedores
- DEBE alertar proveedores con baja calificación

**RF-054: Documentos de Proveedores**
- DEBE almacenar RUT, cámara de comercio, certificación bancaria
- DEBE alertar vencimiento de documentos
- DEBE bloquear compras si documentos vencidos

#### 3.4.2. Ciclo de Compras

**RF-055: Cotizaciones de Compra**
- DEBE solicitar cotización a múltiples proveedores
- DEBE registrar items, cantidades, especificaciones
- DEBE recibir respuestas de proveedores
- DEBE comparar cotizaciones en cuadro comparativo
- DEBE seleccionar ganador con justificación

**RF-056: Orden de Compra**
- DEBE generar orden de compra desde cotización o directa
- DEBE incluir: items, cantidades, precios, condiciones, entrega
- DEBE calcular impuestos automáticamente
- DEBE requerir aprobación según monto (flujo multinivel)
- DEBE enviar OC a proveedor por email
- DEBE controlar estado: creada, aprobada, parcialmente recibida, cerrada

**RF-057: Recepción de Mercancía**
- DEBE registrar entrada vinculada a OC
- DEBE validar calidad y cantidad
- DEBE permitir recepción parcial
- DEBE generar diferencias si no coincide
- DEBE actualizar estado de la OC

**RF-058: Facturación de Proveedores**
- DEBE registrar factura del proveedor
- DEBE vincular a OC y/o remisión
- DEBE validar valores vs. OC
- DEBE distribuir costos a centros de costo
- DEBE calcular retenciones automáticamente
- DEBE generar cuenta por pagar

**RF-059: Notas Débito y Crédito de Proveedores**
- DEBE crear nota débito (reclamaciones)
- DEBE crear nota crédito (devoluciones, descuentos)
- DEBE afectar saldo del proveedor
- DEBE requerir soporte

---

### MODULO 5: FACTURACION Y VENTAS

#### 3.5.1. Gestión de Clientes

**RF-060: Maestro de Clientes**
- DEBE registrar clientes con: NIT, nombre, dirección, contactos
- DEBE soportar personas naturales y jurídicas
- DEBE clasificar por tipo: corporativo, PYME, particular
- DEBE asignar vendedor
- DEBE asignar encargado de cobro
- DEBE registrar actividad económica
- DEBE indicar responsabilidades tributarias (IVA, renta, etc.)

**RF-061: Agentes de Cliente**
- DEBE registrar múltiples contactos por cliente
- DEBE indicar rol (compras, operaciones, facturación, gerencia)
- DEBE almacenar email, teléfonos, cumpleaños
- DEBE permitir comunicación directa

**RF-062: Cupo de Crédito**
- DEBE asignar cupo de crédito por cliente
- DEBE controlar cupo disponible en tiempo real
- DEBE bloquear ventas si excede cupo
- DEBE permitir cupo transitorio con aprobación
- DEBE evaluar cupo periódicamente

**RF-063: Documentos de Clientes**
- DEBE almacenar RUT, cámara de comercio, referencias
- DEBE alertar vencimiento
- DEBE bloquear facturación si documentos vencidos

#### 3.5.2. Cotizaciones y Ventas

**RF-064: Cotizaciones a Clientes**
- DEBE crear cotización de servicios de transporte
- DEBE crear cotización de servicios de taller
- DEBE calcular precio según tarifa, distancia, peso
- DEBE incluir validez de la cotización
- DEBE permitir descuentos con autorización
- DEBE convertir cotización en venta

**RF-065: Facturación de Servicios**
- DEBE facturar servicios de transporte
- DEBE facturar servicios de taller a terceros
- DEBE generar factura electrónica (DIAN Colombia)
- DEBE calcular impuestos (IVA, retenciones)
- DEBE aplicar descuentos y conceptos adicionales
- DEBE generar consecutivo DIAN
- DEBE enviar factura por email automáticamente

**RF-066: Resoluciones DIAN (Colombia)**
- DEBE gestionar resoluciones de facturación
- DEBE controlar rangos autorizados
- DEBE alertar cuando se agote numeración
- DEBE prevenir facturar fuera de rango
- DEBE validar vigencia de resolución

**RF-067: Notas Débito y Crédito de Clientes**
- DEBE crear nota débito (intereses, ajustes)
- DEBE crear nota crédito (devoluciones, anulaciones)
- DEBE afectar saldo del cliente
- DEBE ser factura electrónica
- DEBE requerir justificación

---

### MODULO 6: TESORERIA Y FINANZAS

#### 3.6.1. Gestión de Caja

**RF-068: Cajas**
- DEBE crear múltiples cajas por oficina
- DEBE asignar cajero responsable
- DEBE vincular a cuenta contable
- DEBE controlar estado (activa/inactiva)

**RF-069: Recibos de Caja**
- DEBE emitir recibo de caja por pagos recibidos
- DEBE registrar: cliente/tercero, valor, concepto, forma pago
- DEBE soportar múltiples formas: efectivo, cheque, transferencia, tarjeta
- DEBE generar consecutivo automático
- DEBE imprimir/enviar recibo
- DEBE distribuir pago a múltiples facturas

**RF-070: Movimientos de Caja**
- DEBE registrar ingresos y egresos
- DEBE clasificar por concepto
- DEBE requerir autorización para egresos > umbral
- DEBE mantener saldo en tiempo real
- DEBE generar comprobante de egreso

**RF-071: Cierre de Caja**
- DEBE realizar cierre diario de caja
- DEBE calcular saldo teórico (inicial + ingresos - egresos)
- DEBE registrar arqueo físico
- DEBE calcular diferencia (faltante/sobrante)
- DEBE requerir justificación si diferencia > tolerancia
- DEBE generar relación de recibos/egresos
- DEBE transferir saldo a caja siguiente o banco

**RF-072: Consignaciones Bancarias**
- DEBE crear consignación agrupando recibos de caja
- DEBE generar planilla de consignación
- DEBE registrar banco, cuenta, número consignación
- DEBE confirmar consignación vs. extracto
- DEBE cerrar ciclo de caja

#### 3.6.2. Gestión Bancaria

**RF-073: Maestro de Bancos y Cuentas**
- DEBE registrar bancos
- DEBE registrar cuentas bancarias por oficina
- DEBE indicar tipo: ahorros, corriente, dólares
- DEBE vincular a cuenta contable
- DEBE registrar número de cuenta y datos

**RF-074: Movimientos Bancarios**
- DEBE registrar movimientos: depósitos, retiros, notas, intereses
- DEBE clasificar por concepto
- DEBE conciliar con extracto bancario
- DEBE mantener saldo en tiempo real

**RF-075: Conciliación Bancaria**
- DEBE importar extractos bancarios
- DEBE cruzar automáticamente movimientos
- DEBE identificar partidas no conciliadas
- DEBE permitir conciliación manual
- DEBE generar informe de conciliación

**RF-076: Giros y Cheques**
- DEBE gestionar chequeras
- DEBE emitir cheques con consecutivo
- DEBE registrar beneficiario, valor, concepto
- DEBE requerir firma autorizada (digital o física)
- DEBE controlar cheques emitidos/cobrados/anulados
- DEBE alertar cheques pendientes > 30 días

**RF-077: Traslados entre Bancos**
- DEBE crear traslado entre cuentas
- DEBE generar egreso en origen e ingreso en destino
- DEBE controlar comisiones
- DEBE validar confirmación del traslado

#### 3.6.3. Cuentas por Cobrar y Pagar

**RF-078: Cuentas por Cobrar**
- DEBE generar saldo por cliente desde facturas
- DEBE restar pagos aplicados
- DEBE calcular antigüedad de saldos (30, 60, 90, >90 días)
- DEBE generar extracto de cuenta por cliente
- DEBE alertar cartera vencida
- DEBE bloquear clientes morosos

**RF-079: Gestión de Cobro**
- DEBE generar reportes de cartera para cobradores
- DEBE registrar gestiones de cobro (llamadas, visitas, acuerdos)
- DEBE programar recordatorios
- DEBE enviar estados de cuenta automáticos
- DEBE escalar clientes críticos

**RF-080: Cuentas por Pagar**
- DEBE generar saldo por proveedor desde facturas
- DEBE restar pagos aplicados
- DEBE programar fechas de pago
- DEBE generar calendario de pagos
- DEBE alertar pagos próximos

**RF-081: Programación de Pagos**
- DEBE seleccionar facturas a pagar
- DEBE agrupar por proveedor
- DEBE generar orden de pago
- DEBE requerir aprobación
- DEBE generar cheque/transferencia
- DEBE marcar como pagado

#### 3.6.4. Conceptos Contables

**RF-082: Maestro de Conceptos**
- DEBE mantener catálogo de conceptos de ingreso/egreso
- DEBE vincular a cuenta contable
- DEBE indicar efecto (débito/crédito)
- DEBE clasificar en flujo de caja
- DEBE permitir conceptos calculados (IVA, retenciones)

**RF-083: Conceptos Calculados**
- DEBE calcular automáticamente IVA, retención en fuente, reteICA, reteIVA
- DEBE aplicar porcentajes según tipo de persona y concepto
- DEBE generar movimientos contables automáticamente

---

### MODULO 7: RECURSOS HUMANOS

#### 3.7.1. Gestión de Empleados

**RF-084: Maestro de Empleados**
- DEBE registrar empleados con: cédula, nombres, apellidos, dirección
- DEBE registrar teléfonos, email, fecha nacimiento
- DEBE registrar ciudad, estado civil, género
- DEBE almacenar foto
- DEBE registrar contacto de emergencia

**RF-085: Estructura Organizacional**
- DEBE definir empresas del grupo
- DEBE definir gerencias por empresa
- DEBE definir dependencias por gerencia
- DEBE definir cargos por dependencia
- DEBE asignar empleado a cargo-dependencia
- DEBE permitir múltiples asignaciones (cambios de cargo)
- DEBE mantener histórico

**RF-086: Datos Familiares**
- DEBE registrar cónyuge, hijos y otros familiares
- DEBE almacenar datos de contacto
- DEBE indicar beneficiarios

**RF-087: Horarios y Turnos**
- DEBE definir horarios de trabajo
- DEBE asignar horario por cargo
- DEBE controlar turnos especiales
- DEBE registrar restricciones de horario

**RF-088: Préstamos de Herramientas**
- DEBE prestar herramientas a empleados
- DEBE controlar devolución
- DEBE alertar herramientas no devueltas
- DEBE registrar estado al devolver

#### 3.7.2. Evaluación de Desempeño

**RF-089: Evaluaciones**
- DEBE crear evaluaciones de desempeño
- DEBE definir criterios y pesos
- DEBE asignar evaluador
- DEBE calcular calificación
- DEBE generar plan de mejora
- DEBE mantener histórico

**RF-090: Estudios de Seguridad**
- DEBE realizar estudios de seguridad a conductores
- DEBE registrar visitas domiciliarias
- DEBE almacenar referencias
- DEBE aprobar/rechazar
- DEBE renovar periódicamente

---

### MODULO 8: ADMINISTRACION Y SEGURIDAD

#### 3.8.1. Gestión de Usuarios

**RF-091: Usuarios**
- DEBE crear usuarios vinculados a empleados o externos
- DEBE asignar credenciales (usuario/contraseña)
- DEBE forzar cambio de contraseña inicial
- DEBE exigir contraseñas seguras (min 8 caracteres, mayúsculas, números, símbolos)
- DEBE bloquear cuenta tras N intentos fallidos
- DEBE inactivar usuarios automáticamente si no usan en X días

**RF-092: Perfiles y Permisos**
- DEBE definir perfiles con permisos granulares
- DEBE asignar permisos por: módulo, formulario, acción (crear, leer, actualizar, eliminar)
- DEBE permitir permisos a nivel de campo (visible, editable, requerido)
- DEBE asignar múltiples perfiles a un usuario (permisos acumulativos)
- DEBE heredar permisos de perfil padre

**RF-093: Multi-Oficina**
- DEBE registrar oficinas con: código, nombre, ciudad, dirección
- DEBE asignar usuarios a oficinas
- DEBE filtrar información por oficina del usuario
- DEBE permitar consulta inter-oficinas con permiso
- DEBE centralizar configuración

#### 3.8.2. Parámetros y Configuración

**RF-094: Parámetros del Sistema**
- DEBE centralizar parámetros globales: empresa, moneda base, idioma, zona horaria
- DEBE permitir parámetros por oficina
- DEBE incluir: umbrales, porcentajes, límites, rutas
- DEBE validar tipos de dato
- DEBE requerir autorización para cambios críticos

**RF-095: Consecutivos**
- DEBE generar consecutivos automáticos por tipo de documento
- DEBE controlar rangos por oficina
- DEBE prevenir saltos o duplicados
- DEBE alertar cuando se acerque al límite
- DEBE permitir reinicio con autorización

#### 3.8.3. Agentes del Sistema

**RF-096: Agentes y Tareas Programadas**
- DEBE ejecutar agentes en background: alertas, cálculos, integraciones
- DEBE programar ejecución (cron)
- DEBE registrar log de ejecución
- DEBE alertar fallos
- DEBE permitir ejecución manual

---

### MODULO 9: DOCUMENTOS Y LICENCIAS

#### 3.9.1. Control de Documentos

**RF-097: Licencias de Conducción**
- DEBE registrar licencia por conductor
- DEBE alertar vencimiento con 60, 30, 15 días
- DEBE bloquear asignación si vencida
- DEBE validar categoría vs. tipo de vehículo

**RF-098: Documentos de Vehículos**
- DEBE registrar por vehículo: SOAT, revisión técnica, tarjeta propiedad, pólizas
- DEBE alertar vencimientos
- DEBE bloquear viajes si documentos vencidos
- DEBE almacenar copias digitales

**RF-099: Documentos de Empresa**
- DEBE registrar: RUT, cámara comercio, certificaciones, pólizas
- DEBE alertar vencimientos
- DEBE asignar responsable de renovación

#### 3.9.2. Siniestros

**RF-100: Reporte de Siniestros**
- DEBE registrar siniestro: fecha, lugar, vehículo, conductor, descripción
- DEBE clasificar por tipo y gravedad
- DEBE adjuntar fotos, croquis, documentos
- DEBE notificar aseguradora y autoridades

**RF-101: Seguimiento de Siniestros**
- DEBE registrar acciones y avances
- DEBE controlar indemnización
- DEBE vincular gastos asociados
- DEBE cerrar siniestro formalmente

---

### MODULO 10: GEOGRAFIA Y DATOS MAESTROS

#### 3.10.1. Geografía

**RF-102: Países, Departamentos, Ciudades**
- DEBE mantener catálogo geográfico multi-país
- DEBE incluir código DIVIPOLA (Colombia)
- DEBE permitir agregar nuevas ubicaciones
- DEBE usar en validaciones de direcciones

#### 3.10.2. Catálogos Maestros

**RF-103: Marcas, Colores, Unidades de Medida**
- DEBE mantener catálogos de referencia
- DEBE permitir activar/inactivar sin eliminar
- DEBE validar uso antes de inactivar

**RF-104: Clasificaciones Tributarias**
- DEBE mantener: tipos de persona, regímenes, responsabilidades
- DEBE usar en cálculo automático de impuestos
- DEBE actualizar según normativa vigente

---

### MODULO 11: INTEGRACION Y CONTABILIDAD

#### 3.11.1. Integración Contable

**RF-105: Plan de Cuentas**
- DEBE importar plan de cuentas contable
- DEBE vincular conceptos a cuentas
- DEBE validar movimientos balanceados (débito=crédito)

**RF-106: Comprobantes Contables**
- DEBE generar comprobantes desde: facturas, pagos, nómina, ajustes
- DEBE numerar consecutivamente
- DEBE validar balance antes de contabilizar
- DEBE permitir reversar con autorización

**RF-107: Integración SIESA / Sistema Contable**
- DEBE exportar movimientos a sistema contable externo
- DEBE generar archivos en formato requerido
- DEBE controlar interface de exportación
- DEBE prevenir duplicados

**RF-108: Reportes NIIF**
- DEBE generar reportes bajo estándares NIIF
- DEBE mantener doble contabilidad (local + NIIF) si requiere
- DEBE reclasificar automáticamente según NIIF

#### 3.11.2. APIs e Integraciones

**RF-109: API REST**
- DEBE exponer API REST para integraciones
- DEBE implementar autenticación con tokens (JWT)
- DEBE documentar endpoints (Swagger/OpenAPI)
- DEBE controlar rate limiting
- DEBE versionar API

**RF-110: Webhooks**
- DEBE enviar notificaciones a sistemas externos vía webhooks
- DEBE reintentar envíos fallidos
- DEBE registrar log de notificaciones

---

### MODULO 12: INDICADORES Y CONTROL

#### 3.12.1. Indicadores de Gestión

**RF-111: KPIs del Negocio**
- DEBE calcular indicadores clave:
  - Utilización de flota (% tiempo en ruta)
  - Rendimiento de combustible promedio
  - Costo por kilómetro
  - Cumplimiento de mantenimientos preventivos
  - Rotación de inventarios
  - Cartera promedio (DSO)
  - Rentabilidad por cliente/ruta
- DEBE mostrar en dashboard en tiempo real
- DEBE permitir drill-down a detalle

**RF-112: Metas y Presupuestos**
- DEBE definir metas por oficina/período
- DEBE asignar presupuesto por centro de costo
- DEBE comparar real vs. presupuestado
- DEBE alertar desviaciones > umbral
- DEBE generar proyecciones

**RF-113: Alertas Inteligentes**
- DEBE generar alertas automáticas ante:
  - Vencimientos de documentos
  - Exceso de límites (cupos, presupuestos)
  - KPIs fuera de rango
  - Anomalías detectadas (fraudes, errores)
- DEBE enviar por canal configurado
- DEBE escalar si no se atiende

---

### MODULO 13: AUDITORIA Y TRAZABILIDAD

#### 3.13.1. Auditoría de Operaciones

**RF-114: Registro Automático de Auditoría**
- DEBE registrar automáticamente TODAS las operaciones
- DEBE capturar: tabla, llave, usuario, IP, fecha/hora, acción (I/U/D)
- DEBE almacenar estado antes y después (campo por campo)
- DEBE ser inmutable (no se puede editar/borrar auditoría)

**RF-115: Consulta de Auditoría**
- DEBE permitir consultar auditoría por: tabla, registro, usuario, fecha
- DEBE mostrar línea de tiempo de cambios
- DEBE comparar versiones
- DEBE exportar trazabilidad completa

#### 3.13.2. Históricos

**RF-116: Tablas Históricas**
- DEBE mantener histórico de cambios en maestros críticos:
  - Vehículos, conductores, trailers, propietarios
  - Clientes, proveedores
  - Enlaces
- DEBE permitir consultar estado en cualquier momento del pasado

---

### MODULO 14: COMUNICACIONES Y SERVICIOS

#### 3.14.1. Mensajería Interna

**RF-117: Mensajes entre Usuarios**
- DEBE permitir envío de mensajes internos
- DEBE soportar adjuntos
- DEBE notificar en tiempo real
- DEBE organizar en bandeja de entrada/enviados

**RF-118: Notificaciones**
- DEBE enviar notificaciones del sistema por:
  - Email
  - SMS
  - Notificaciones push web
  - Mensajería interna
- DEBE permitir configurar preferencias por usuario

#### 3.14.2. Correo Electrónico

**RF-119: Integración Email**
- DEBE enviar emails automáticos:
  - Facturas, cotizaciones
  - Estados de cuenta
  - Alertas y recordatorios
  - Reportes programados
- DEBE usar templates configurables
- DEBE registrar envíos

**RF-120: Recepción de Facturas por Email**
- DEBE recibir facturas de proveedores por email
- DEBE extraer adjuntos PDF/XML
- DEBE pre-procesar datos (OCR, lectura XML)
- DEBE facilitar radicación

---

### MODULO 15: REPORTES Y ANALISIS

#### 3.15.1. Reportes Operativos

**RF-121: Generador de Reportes**
- DEBE incluir reportes predefinidos por módulo:
  - Flota: vehículos disponibles, en ruta, en taller
  - Taller: OTs abiertas, pendientes, cerradas
  - Inventarios: existencias, movimientos, valorizado
  - Compras: órdenes, pendientes de recepción, facturas
  - Ventas: facturación, cartera
  - Financieros: flujo de caja, movimientos bancarios
- DEBE permitir filtros y parámetros
- DEBE exportar a: PDF, Excel, CSV
- DEBE programar envío automático

**RF-122: Reportes Personalizados**
- DEBE permitir crear reportes personalizados (query builder)
- DEBE guardar y compartir reportes
- DEBE controlar acceso por perfil

#### 3.15.2. Business Intelligence

**RF-123: Dashboards Ejecutivos**
- DEBE mostrar dashboards por rol:
  - Gerencia: KPIs, rentabilidad, proyecciones
  - Operaciones: utilización flota, pendientes
  - Taller: OTs, repuestos críticos
  - Finanzas: flujo de caja, cartera
- DEBE actualizar en tiempo real
- DEBE permitir personalización

**RF-124: Análisis de Datos**
- DEBE incluir herramientas de análisis:
  - Gráficos interactivos (barras, líneas, torta, mapas)
  - Tablas dinámicas
  - Análisis de tendencias
  - Comparativos período vs. período
- DEBE permitir drill-down y drill-up

---

## 4. REQUERIMIENTOS NO FUNCIONALES

### 4.1. Rendimiento

**RNF-001: Tiempo de Respuesta**
- Las pantallas DEBEN cargar en < 3 segundos
- Las consultas DEBEN responder en < 5 segundos
- Los reportes DEBEN generarse en < 30 segundos (o modo asíncrono)
- Las transacciones DEBEN confirmarse en < 2 segundos

**RNF-002: Concurrencia**
- El sistema DEBE soportar 100+ usuarios concurrentes sin degradación
- DEBE escalar horizontalmente agregando servidores
- DEBE usar caché para datos de consulta frecuente

**RNF-003: Volúmenes de Datos**
- DEBE manejar:
  - 10,000+ vehículos
  - 100,000+ items de inventario
  - 10M+ registros de auditoría
  - 1M+ transacciones anuales
- DEBE mantener rendimiento con crecimiento de datos

### 4.2. Disponibilidad y Confiabilidad

**RNF-004: Disponibilidad**
- El sistema DEBE estar disponible 99.5% del tiempo (máximo 3.65 horas downtime/mes)
- DEBE funcionar 24/7/365
- DEBE programar mantenimientos en horarios de baja demanda

**RNF-005: Respaldo y Recuperación**
- DEBE realizar backups diarios automáticos
- DEBE mantener backups por 90 días mínimo
- DEBE permitir recuperación ante desastres (RPO < 24 horas, RTO < 4 horas)
- DEBE probar restauraciones periódicamente

**RNF-006: Tolerancia a Fallos**
- DEBE continuar operando con fallas parciales (degraded mode)
- DEBE reintentar operaciones fallidas automáticamente
- DEBE registrar y alertar errores

### 4.3. Seguridad

**RNF-007: Seguridad de Datos**
- DEBE cifrar datos sensibles en base de datos (AES-256)
- DEBE cifrar transmisiones (HTTPS/TLS 1.3)
- DEBE aplicar principio de mínimo privilegio
- DEBE enmascarar datos sensibles en logs

**RNF-008: Protección contra Amenazas**
- DEBE proteger contra: SQL Injection, XSS, CSRF, clickjacking
- DEBE implementar rate limiting anti-DoS
- DEBE validar entradas en backend y frontend
- DEBE sanitizar salidas

**RNF-009: Cumplimiento Normativo**
- DEBE cumplir Ley de Protección de Datos (Colombia Ley 1581/2012, GDPR si aplica)
- DEBE permitir derecho al olvido (anonimización)
- DEBE registrar consentimientos

### 4.4. Usabilidad

**RNF-010: Experiencia de Usuario**
- DEBE ser intuitivo requiriendo < 8 horas capacitación por módulo
- DEBE incluir ayudas contextuales y tooltips
- DEBE mostrar mensajes claros de error con acciones sugeridas
- DEBE confirmar acciones destructivas

**RNF-011: Accesibilidad**
- DEBE cumplir WCAG 2.1 nivel AA
- DEBE soportar lectores de pantalla
- DEBE permitir navegación completa por teclado
- DEBE soportar zoom hasta 200% sin pérdida de funcionalidad

**RNF-012: Internacionalización**
- DEBE soportar español como idioma base
- DEBE permitir agregar idiomas (inglés, portugués)
- DEBE manejar múltiples zonas horarias
- DEBE formatear fechas, números y monedas según locale

### 4.5. Mantenibilidad

**RNF-013: Código Limpio**
- DEBE seguir estándares de código (linting)
- DEBE documentar código crítico
- DEBE mantener cobertura de pruebas > 70%
- DEBE usar versionamiento semántico

**RNF-014: Monitoreo**
- DEBE registrar logs estructurados
- DEBE monitorear: CPU, memoria, disco, red, tiempos de respuesta
- DEBE alertar anomalías
- DEBE proveer dashboards de monitoreo

**RNF-015: Actualización**
- DEBE permitir actualizaciones sin downtime (blue-green deployment)
- DEBE mantener compatibilidad hacia atrás en APIs
- DEBE migrar datos automáticamente

### 4.6. Escalabilidad

**RNF-016: Crecimiento**
- DEBE escalar a 10X usuarios sin re-arquitectura
- DEBE permitir agregar nuevas empresas sin límite (multi-tenant)
- DEBE soportar operación en múltiples países

### 4.7. Compatibilidad

**RNF-017: Navegadores**
- DEBE funcionar en:
  - Chrome/Edge (últimas 2 versiones)
  - Firefox (últimas 2 versiones)
  - Safari (últimas 2 versiones)
- DEBE degradar gracefully en navegadores antiguos

**RNF-018: Dispositivos**
- DEBE ser responsivo adaptándose a:
  - Desktop (1920x1080 y mayores)
  - Laptop (1366x768)
  - Tablet (768x1024)
  - Móvil (375x667 mínimo)

**RNF-019: Sistemas Operativos**
- DEBE funcionar en Windows, macOS, Linux, iOS, Android

---

## 5. TECNOLOGIAS RECOMENDADAS

### 5.1. Frontend

**Opción 1 (Recomendada): React + TypeScript**
- Framework: React 18+ con TypeScript
- UI Library: Material-UI (MUI) o Ant Design
- Estado: Redux Toolkit o Zustand
- Routing: React Router v6
- Forms: React Hook Form + Zod
- Gráficos: Recharts o Chart.js
- Tablas: TanStack Table (React Table v8)

**Opción 2: Angular + TypeScript**
- Framework: Angular 17+
- UI Library: Angular Material o PrimeNG
- Estado: NgRx
- Forms: Reactive Forms

**Opción 3: Vue + TypeScript**
- Framework: Vue 3 con Composition API
- UI Library: Vuetify o Quasar
- Estado: Pinia

### 5.2. Backend

**Opción 1 (Recomendada): Node.js + TypeScript**
- Runtime: Node.js 20 LTS
- Framework: NestJS (arquitectura modular, DI, decorators)
- ORM: Prisma o TypeORM
- Validación: class-validator
- Documentación: Swagger

**Opción 2: Java + Spring Boot**
- JDK: Java 17 LTS
- Framework: Spring Boot 3.x
- ORM: JPA/Hibernate
- Seguridad: Spring Security

**Opción 3: Python + FastAPI**
- Python: 3.11+
- Framework: FastAPI
- ORM: SQLAlchemy 2.0

### 5.3. Base de Datos

**Principal: PostgreSQL 16+**
- Versión: PostgreSQL 16 (última estable)
- Extensiones: PostGIS (si requiere geolocalización)
- Particionamiento: Para tablas de alto volumen (auditoría)
- Full-text search: Para búsquedas de texto

**Caché: Redis**
- Propósito: Caché de sesiones, consultas frecuentes, colas

### 5.4. Infraestructura

**Contenedores:**
- Docker para empaquetado
- Docker Compose para desarrollo local
- Kubernetes para producción (opcional, si escala)

**CI/CD:**
- GitLab CI/CD o GitHub Actions
- SonarQube para calidad de código
- Jest/Vitest para pruebas unitarias
- Playwright o Cypress para pruebas E2E

**Monitoreo:**
- Logs: ELK Stack (Elasticsearch, Logstash, Kibana) o Loki
- Métricas: Prometheus + Grafana
- APM: New Relic o Datadog (opcional)

**Cloud (si aplica):**
- AWS: EC2, RDS PostgreSQL, S3, CloudFront, SES
- Azure: App Service, Azure Database for PostgreSQL, Blob Storage
- GCP: Compute Engine, Cloud SQL, Cloud Storage

### 5.5. Integraciones

**Facturación Electrónica (Colombia):**
- Integración con proveedor tecnológico DIAN (ej: Facturama, Alegra, Siigo)
- Generación XML según estructura UBL 2.1
- Firma digital de facturas
- Envío a DIAN

**Pagos:**
- Pasarelas: PSE, tarjetas (Wompi, PayU, Stripe)
- Transferencias: Integración bancaria ACH

**Notificaciones:**
- Email: SendGrid, Amazon SES, Mailgun
- SMS: Twilio, Vonage
- WhatsApp: Twilio WhatsApp API

---

## 6. FASES DEL PROYECTO

### Fase 1: Fundación (3-4 meses)
- Configuración de infraestructura y DevOps
- Módulo 8: Administración y Seguridad (usuarios, perfiles, oficinas)
- Módulo 10: Geografía y Datos Maestros
- Módulo 13: Auditoría

### Fase 2: Operaciones Core (4-5 meses)
- Módulo 1: Gestión de Flota (sin viajes aún)
- Módulo 3: Inventarios y Almacenes
- Módulo 7: Recursos Humanos (básico)

### Fase 3: Taller (3-4 meses)
- Módulo 2: Taller y Mantenimiento completo

### Fase 4: Comercial (3-4 meses)
- Módulo 4: Compras y Proveedores
- Módulo 5: Facturación y Ventas

### Fase 5: Financiero (3-4 meses)
- Módulo 6: Tesorería y Finanzas

### Fase 6: Operaciones Avanzadas (2-3 meses)
- Módulo 1: Viajes, rutas, legalizaciones (completar)
- Módulo 14: Comunicaciones

### Fase 7: Inteligencia (2-3 meses)
- Módulo 12: Indicadores y Control
- Módulo 15: Reportes y BI
- Módulo 11: Integración Contable

**TOTAL ESTIMADO: 20-27 meses**

---

## 7. CRITERIOS DE ACEPTACION

Cada requerimiento funcional DEBE cumplir:

1. **Funciona según especificado** - Cumple el comportamiento descrito
2. **Validado por usuario clave** - Aprobado por experto del negocio
3. **Pruebas pasadas** - Unitarias, integración, E2E
4. **Documentado** - Manual de usuario y técnico
5. **Rendimiento aceptable** - Cumple RNF de tiempos
6. **Seguro** - Cumple estándares de seguridad
7. **Auditable** - Registra en logs y auditoría

---

## 8. EXCLUSIONES

**Fuera del Alcance Inicial:**

1. Aplicación móvil nativa (iOS/Android) - Se desarrollará PWA responsiva
2. Gestión de nómina completa - Solo datos básicos de RRHH
3. Contabilidad completa - Solo integración con sistema externo
4. Portal de clientes - Fase posterior
5. Portal de proveedores - Fase posterior
6. App móvil para conductores - Fase posterior
7. IoT / Telemetría vehicular - Fase posterior
8. Inteligencia artificial predictiva - Fase posterior

---

## 9. RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Tablas faltantes impidan migración | Alta | Alto | Reconstruir tablas antes de iniciar |
| Usuarios rechazen nuevo sistema | Media | Alto | Capacitación continua, involucramiento desde fase 1 |
| Datos de producción inconsistentes | Media | Alto | Limpieza de datos pre-migración |
| Problemas de rendimiento PostgreSQL | Baja | Medio | Pruebas de carga desde fase 1, índices optimizados |
| Integraciones fallen | Media | Medio | Desarrollo de conectores robustos con reintentos |
| Sobrecostos por complejidad | Media | Alto | Desarrollo iterativo, re-priorizar si necesario |

---

## APROBACIONES

Este documento debe ser revisado y aprobado por:

- [ ] Gerencia General
- [ ] Gerencia de Operaciones
- [ ] Gerencia Financiera
- [ ] Gerencia de Sistemas
- [ ] Líder de Proyecto
- [ ] Arquitecto de Software

---

**Documento:** Requerimientos Funcionales Sistema TRANSER Modernizado
**Versión:** 1.0
**Fecha:** 2025-12-30
**Preparado por:** Equipo de Análisis y Desarrollo
