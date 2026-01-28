
###Hola Claude Code,

  Voy a proporcionarte el contexto completo del proyecto TRANSER Vórtice.

  Por favor, lee los siguientes archivos en orden:

  1. /CLAUDE.md (contexto base del proyecto)
  2. /docs/Contexto_General.md (convenciones generales)
  3. /docs/llantas/Contexto_Llantas.md (contexto específico del módulo)
  4. /docs/PROMPT_MASTER_TIRE_MODULE.md (protocolo de trabajo)

  Después de leer estos archivos, confirma que entendiste:
  - El stack tecnológico
  - La arquitectura (Clean Architecture)
  - Los módulos existentes (Auth, Users)
  - El protocolo de validación antes de desarrollar

  Responde con un resumen de lo que entendiste.

###RF-001
  vamos a iniciar con el desarrollo del Requerimiento Funcional 001  RF-001 - Gestión de Fichas Técnicas. Antes de
  iniciar con el desarrollo realiza un breve resumen del requerimiento. No inicies el desarrollo hasta no tener la
  aprobacion

  Confirmamos que este es el requerimiento correcto y deseamos continuar con la FASE 1 según el protocolo
  establecido

   You've used 99% of your session limit · resets 2pm (America/Bogota)
   


   ###Hola Claude Code,

  Voy a proporcionarte el contexto completo del proyecto TRANSER Vórtice.

  Por favor, lee los siguientes archivos en orden:

  1. /CLAUDE.md (contexto base del proyecto)
  2. /docs/Contexto_General.md (convenciones generales)
  3. /docs/llantas/Contexto_Llantas.md (contexto específico del módulo)
  4. /docs/PROMPT_MASTER_TIRE_MODULE.md (protocolo de trabajo)

  Después de leer estos archivos, confirma que entendiste:
  - El stack tecnológico
  - La arquitectura (Clean Architecture)
  - Los módulos existentes (Auth, Users)
  - El protocolo de validación antes de desarrollar

  Responde con un resumen de lo que entendiste.

###RF-002
  vamos a iniciar con el desarrollo del Requerimiento Funcional RF-002: Control de Inventario de Llantas. 
  Antes de   iniciar con el desarrollo realiza un breve resumen del requerimiento. No inicies el desarrollo hasta no tener la
  aprobacion

❓ PREGUNTAS PARA ACLARAR ANTES DE IMPLEMENTAR

  1. ¿Los catálogos PROVEEDORES_LLANTAS y LOCALIZA están incluidos en este RF o son un RF separado?
    - Respuesta: Implementar como parte de RF-001 extendido (catálogos compartidos)
  2. ¿Permitimos editar el GRUPO de una llanta una vez creada?
    - Respuesta: Solo lectura, se modifica automáticamente al reencauchar
  3. ¿Permitimos eliminar llantas sin histórico?
    - Respuesta: Soft delete siempre para mantener trazabilidad
  4. ¿Qué información del proveedor necesitamos mostrar en el formulario?
    - Respuesta: como Mínimo: Código, Nombre, NIT, Contacto
  5. ¿Las localizaciones tienen jerarquía (bodega > pasillo > estante)?
    - Respuesta: Código simple (ej: "A-1", "B-5")

 El resumen del RF-002 es correcto y puedes proceder con el análisis detallado de 30 preguntas y diseño de arquitectura

Aclaraciones previas completadas. Procede con la FASE 1 según el protocolo establecido.
El contexto del proyecto al parecer no considera que puedan existir varias sedes y varios almacenes por sede.
  Es correcto este analisis?
  No inicies con el desarrollo hasta no aclarar este tema

● ¡Excelente observación! Tienes toda la razón. 🎯

  ⚠️ ANÁLISIS CRÍTICO: ESTRUCTURA MULTI-SEDE/MULTI-ALMACÉN

  🔍 Revisión del Contexto

  Revisando la documentación, encuentro evidencia de estructura organizacional:

  Indicadores de Multi-Sede en el Sistema Legacy

  Del archivo Requerimiento_Llantas.md:
  - PARAMETROS_OFICSISTEMA: "Parámetros por oficina/sucursal"
  - Tabla LOCALIZA: Catálogo de localizaciones de inventario
  - Sistema de transporte de carga → Múltiples operaciones geográficas

  Del contexto general:
  - Empresa de transporte en Colombia → Probablemente múltiples ciudades
  - Sistema de flota → Vehículos distribuidos geográficamente
  - Gestión de taller → Talleres en diferentes ubicaciones

  ❌ Errores en Mi Análisis

  Lo que asumí incorrectamente:
  1. Inventario centralizado (una sola bodega)
  2. TIRE_WAREHOUSE_LOCATIONS como simple código "A-1", "B-5"
  3. Sin filtrado por sede/oficina
  4. Catálogos globales sin contexto organizacional

  🏢 Estructura Organizacional Real (A Confirmar)

  Probablemente la estructura es:

  EMPRESA TRANSER
    │
    ├── SEDE BOGOTÁ
    │     ├── Almacén Principal
    │     │     ├── Ubicación A-1
    │     │     ├── Ubicación A-2
    │     │     └── Ubicación B-1
    │     ├── Almacén Temporal
    │     └── Taller Bogotá
    │
    ├── SEDE MEDELLÍN
    │     ├── Almacén Principal
    │     ├── Taller Medellín
    │     └── ...
    │
    └── SEDE CALI
          └── ...

  ❓ PREGUNTAS CRÍTICAS QUE DEBO ACLARAR

  1. Estructura Organizacional

  ¿El sistema maneja múltiples sedes/oficinas/sucursales?
  - Sí, múltiples sedes
  - No, es una sola sede centralizada

  Si es multi-sede, ¿cuál es la jerarquía?
  - ¿EMPRESA → SEDES → ALMACENES → UBICACIONES?
  - ¿EMPRESA → REGIONES → SEDES → ALMACENES?
  - ¿Otra estructura?

  2. Inventario por Sede

  ¿El inventario de llantas es por sede?
  - ¿Una llanta en Bogotá es diferente a una en Medellín?
  - ¿O hay inventario compartido/centralizado?

  ¿Los usuarios ven solo el inventario de su sede?
  - ¿Filtrado automático por sede del usuario logueado?
  - ¿O pueden ver todas las sedes?

  3. Catálogos Compartidos vs Por Sede

  Catálogos globales (todas las sedes comparten):
  - ¿Fichas técnicas (TIRE_SPECIFICATIONS)?
  - ¿Marcas, tipos, referencias?

  Catálogos por sede:
  - ¿Almacenes (WAREHOUSES)?
  - ¿Proveedores (TIRE_SUPPLIERS)? ¿O son globales?
  - ¿Localizaciones (WAREHOUSE_LOCATIONS)?

  4. Modelo de Datos Ajustado

  ¿La tabla INVENTARIO debe tener campo office_id o branch_id?

  ¿La jerarquía de localización es:
  -- Opción A: Simple (como lo tenía)
  tire_warehouse_locations (id, code, name)

  -- Opción B: Jerárquica
  offices (id, code, name, is_active)
  warehouses (id, code, name, office_id, is_active)
  warehouse_locations (id, code, name, warehouse_id, is_active)

  5. Seguridad Multi-Tenant

  ¿Los usuarios tienen asignada una sede/oficina?
  - ¿En la tabla USERS hay campo office_id?
  - ¿Los permisos son por sede?

  ¿Hay roles especiales?
  - Administrador Nacional (ve todas las sedes)
  - Administrador de Sede (solo su sede)
  - Almacenista de Sede (solo su almacén)

  6. Operaciones Entre Sedes

  ¿Se permite transferir llantas entre sedes?
  - Si una sede tiene exceso y otra necesita, ¿hay traslados?
  - ¿Es un proceso especial o simplemente no se permite?

  ¿Se permite transferir llantas entre almacenes de la misma sede?

  7. Reportes y Estadísticas

  ¿Los reportes son:
  - Por sede (cada sede ve solo sus números)
  - Consolidados (administración central ve todas las sedes)
  - Ambos

  8. Datos del Sistema Legacy

  En Oracle Forms (LOCALIZA):
  - ¿Cómo está estructurado el catálogo LOCALIZA?
  - ¿Tiene campo de oficina/sede?
  - ¿Cómo se relaciona con PARAMETROS_OFICSISTEMA?

  --- Conclusión




 ---
  📋 ANÁLISIS DETALLADO - RF-002: Control de Inventario de Llantas

  FASE 1: ANÁLISIS Y COMPRENSIÓN DEL REQUERIMIENTO

  A. COMPRENSIÓN DEL PROBLEMA DE NEGOCIO

  1. ¿Qué problema de negocio específico resuelve este requerimiento?

  Resuelve el control y trazabilidad de llantas en bodega antes de su uso operativo. Permite:
  - Conocer en tiempo real qué llantas están disponibles para instalar
  - Valorar el inventario de llantas (activo importante para la empresa)
  - Controlar la rotación de inventario (evitar llantas obsoletas por antigüedad)
  - Gestionar la entrada de llantas nuevas y reencauchadas al stock
  - Tener visibilidad de dónde está físicamente cada llanta en bodega
  - Rastrear qué proveedor suministró cada llanta

  2. ¿Quiénes son los usuarios/actores involucrados?

  - Almacenista: Usuario principal, registra ingresos y salidas de llantas
  - Jefe de Taller: Consulta disponibilidad para planificar montajes
  - Administrador de Flota: Consulta valor del inventario y costos
  - Operario de Taller: Consulta ubicación de llantas para montaje
  - Contador/Auditor: Verifica valores de inventario

  3. ¿Cuál es el valor que aporta al negocio?

  - Control financiero: Conocer el valor total invertido en llantas en bodega
  - Optimización de compras: Evitar compras innecesarias al ver stock disponible
  - Eficiencia operativa: Localizar rápidamente llantas en bodega
  - Trazabilidad: Saber el origen de cada llanta (proveedor, factura, fecha)
  - Gestión de obsolescencia: Identificar llantas con mucho tiempo en inventario
  - Planificación: Conocer disponibilidad para programar mantenimientos

  ---
  B. ALCANCE FUNCIONAL

  4. ¿Cuáles son los casos de uso principales que debo implementar?

  1. UC-001: Registrar ingreso de llanta nueva al inventario
  2. UC-002: Registrar ingreso de llanta reencauchada al inventario
  3. UC-003: Consultar inventario de llantas (lista paginada)
  4. UC-004: Buscar llanta específica por número
  5. UC-005: Filtrar inventario por criterios (ficha técnica, localización, proveedor, fecha)
  6. UC-006: Ver detalle completo de una llanta en inventario
  7. UC-007: Actualizar datos de llanta en inventario (localización, valor)
  8. UC-008: Dar de baja llanta de inventario (soft delete con justificación)
  9. UC-009: Obtener estadísticas de inventario (totales, valores, antigüedad)

  5. ¿Qué operaciones CRUD son necesarias?

  - Create: Registrar nueva llanta en inventario (nueva o reencauchada)
  - Read:
    - Obtener llanta por ID (LLANTA + GRUPO)
    - Listar inventario con paginación
    - Buscar por criterios
    - Obtener estadísticas
  - Update:
    - Modificar localización
    - Modificar valor (ajustes contables)
    - Modificar observaciones
    - NO modificar: LLANTA, GRUPO, fecha ingreso, proveedor, factura
  - Delete: Soft delete (mantener trazabilidad)

  6. ¿Hay operaciones especiales o procesos de negocio complejos?

  Sí:
  - Salida automática a montaje: Cuando se monta en vehículo (RF-003), debe eliminarse de inventario y registrarse en HISTORIA
  - Retorno desde reencauche: Llanta que vuelve de reencauchar debe ingresar con GRUPO incrementado y mantener mismo LLANTA
  - Validación de disponibilidad: Verificar que llanta esté realmente en inventario antes de permitir montaje
  - Cálculo de antigüedad: Días transcurridos desde fecha de ingreso (para alertas de obsolescencia)
  - Validación de unicidad: No permitir duplicados de (LLANTA, GRUPO) en inventario

  ---
  C. REGLAS DE NEGOCIO

  7. Enumera TODAS las reglas de negocio explícitas del requerimiento

  RN-001: Identificación única
  - Cada llanta se identifica por la combinación (LLANTA, GRUPO)
  - LLANTA es el número de serie físico de la llanta
  - GRUPO indica el ciclo de vida (000=nueva, 001+=reencauches)

  RN-002: Sistema de grupos
  - GRUPO = '000' para llantas nuevas (primera compra)
  - GRUPO incrementa en 1 con cada reencauche ('001', '002', etc.)
  - Formato: 3 caracteres numéricos ('000' a '999')

  RN-003: Restricción de eliminación
  - No se pueden eliminar llantas que tienen movimientos históricos
  - Si una llanta ya fue montada alguna vez, no puede eliminarse del sistema

  RN-004: Salida de inventario
  - Al montar en vehículo → se elimina de INVENTARIO
  - Pasa a tabla LLANTAS (activas)
  - Se registra automáticamente en HISTORIA

  RN-005: Integridad referencial
  - Ficha técnica debe existir antes de registrar llanta
  - Proveedor debe existir en catálogo PROVEEDORES_LLANTAS
  - Localización debe existir en catálogo LOCALIZA

  RN-006: Valor de llanta
  - Valor debe ser > 0
  - Para reencauches, incluye valor del proceso de reencauche

  8. ¿Hay reglas de negocio implícitas que identificas?

  RN-007: Preservación de trazabilidad
  - Toda llanta que ingresa debe tener: proveedor, factura, fecha
  - Estos datos son inmutables una vez registrados

  RN-008: Consistencia de ficha técnica
  - Una llanta debe mantener asociación con una ficha técnica válida
  - Si la ficha técnica se inactiva, llantas existentes siguen válidas

  RN-009: Control de fecha
  - Fecha de ingreso no puede ser futura
  - Fecha de ingreso debe ser >= fecha de última operación del sistema

  RN-010: Antigüedad de inventario
  - Llantas con > 365 días en inventario requieren revisión
  - Alerta de obsolescencia para llantas muy antiguas

  RN-011: Rotación FIFO implícita
  - Al consultar inventario para montaje, sugerir llantas más antiguas primero
  - Optimizar rotación del inventario

  9. ¿Qué validaciones son obligatorias?

  Backend (Java/Spring):
  - @NotNull en: llanta, grupo, valor, fecha, proveedor, factura, fichatec
  - @Positive en: valor
  - @Pattern(regexp="[0-9]{3}") en: grupo
  - @Size(max=20) en: llanta
  - @PastOrPresent en: fecha de ingreso
  - @Valid en objetos relacionados

  Frontend (React):
  - Número de llanta: requerido, alfanumérico, máx 20 caracteres
  - Grupo: requerido, 3 dígitos (000-999)
  - Valor: requerido, numérico positivo, formato moneda
  - Fecha: requerida, no futura
  - Ficha técnica: selección requerida (dropdown)
  - Proveedor: selección requerida (dropdown con búsqueda)
  - Localización: selección requerida (dropdown)

  10. ¿Qué validaciones podrían ser opcionales pero recomendables?

  - Alerta si valor de llanta es muy diferente al precio en ficha técnica (±30%)
  - Alerta si GRUPO > 005 (llanta con muchos reencauches)
  - Sugerencia de localización basada en ficha técnica (agrupar por tipo)
  - Validación de formato de factura según proveedor
  - Comparación con últimas compras del mismo tipo

  ---
  D. MODELO DE DATOS

  11. ¿Qué tablas de la base de datos están involucradas?

  Tabla Principal:
  - tire_inventory (inventario de llantas)

  Tablas Relacionadas (Foreign Keys):
  - tire_specifications (fichas técnicas - RF-001)
  - tire_suppliers (proveedores de llantas - nuevo catálogo)
  - tire_warehouse_locations (localizaciones en bodega - nuevo catálogo)

  Tablas de Destino (operaciones futuras):
  - active_tires (al montar en vehículo - RF-003)
  - tire_history (registro histórico completo)

  12. ¿Qué relaciones entre entidades debo considerar?

  tire_inventory (N) ──→ (1) tire_specifications
  tire_inventory (N) ──→ (1) tire_suppliers
  tire_inventory (N) ──→ (1) tire_warehouse_locations
  tire_inventory (1) ──→ (0..N) tire_history [futuro]

  Relaciones:
  - tire_inventory → tire_specifications: Muchas llantas de la misma ficha técnica
  - tire_inventory → tire_suppliers: Muchas llantas del mismo proveedor
  - tire_inventory → tire_warehouse_locations: Muchas llantas en la misma ubicación
  - Una llanta en inventario generará múltiples registros en HISTORIA cuando se monte/desmonte

  13. ¿Hay campos calculados o derivados?

  Sí:
  - Antigüedad (días): CURRENT_DATE - fecha_ingreso
  - Estado de antigüedad:
    - "Reciente" (< 30 días)
    - "Normal" (30-180 días)
    - "Antiguo" (180-365 días)
    - "Obsoleto" (> 365 días)
  - Valor total por ficha técnica: Suma de valores agrupado por ficha técnica
  - Cantidad por localización: COUNT agrupado por localización

  Estos no se almacenan, se calculan en tiempo real en consultas.

  ---
  E. ESTADOS Y TRANSICIONES

  14. ¿El requerimiento involucra estados? Si es sí, enuméralos

  Sí, aunque el RF-002 solo gestiona el estado INVENTARIO, la llanta como entidad tiene:

  Estados del ciclo de vida:
  1. INVENTARIO ← RF-002 gestiona este estado
  2. ACTIVA (montada en vehículo)
  3. INTERMEDIO (desmontada, evaluando)
  4. RETIRADA (baja definitiva)
  5. HISTORIA (registro histórico)

  Dentro de INVENTARIO:
  - Nueva: GRUPO = '000'
  - Reencauchada: GRUPO > '000'

  15. ¿Qué transiciones de estado son válidas?

  Desde INVENTARIO (RF-002):
  - INVENTARIO → ACTIVA (al montar en vehículo - RF-003)
  - INVENTARIO → RETIRADA (baja sin uso, caso excepcional)
  - INVENTARIO → INVENTARIO (actualización de datos)

  Hacia INVENTARIO:
  - COMPRA → INVENTARIO (llanta nueva)
  - INTERMEDIO → INVENTARIO (después de reencauche)

  16. ¿Qué transiciones están prohibidas?

  - ❌ INVENTARIO → INTERMEDIO directamente (debe pasar por ACTIVA)
  - ❌ INVENTARIO → HISTORIA sin pasar por ACTIVA
  - ❌ INVENTARIO → ACTIVA sin especificar vehículo y posición
  - ❌ Regreso de RETIRADA a INVENTARIO (baja es definitiva)

  ---
  F. INVARIANTES DEL DOMINIO

  17. ¿Qué condiciones SIEMPRE deben ser verdaderas?

  INV-001: Unicidad de identificación
  - La combinación (LLANTA, GRUPO) debe ser única en INVENTARIO
  - No pueden existir dos registros con misma llanta y mismo grupo

  INV-002: Valor positivo
  - valor > 0 siempre

  INV-003: Fecha coherente
  - fecha_ingreso <= CURRENT_DATE

  INV-004: Referencias válidas
  - fichatec_id debe existir en tire_specifications
  - supplier_id debe existir en tire_suppliers
  - location_id debe existir en tire_warehouse_locations

  INV-005: Formato de grupo
  - GRUPO debe ser string de 3 dígitos: '000' a '999'

  INV-006: Estado activo de relaciones
  - Si llanta está en inventario, sus referencias (ficha técnica, proveedor) pueden estar inactivas, pero deben existir

  18. ¿Qué es irreversible una vez hecho?

  - Número de llanta (LLANTA): Una vez asignado, no cambia nunca
  - Grupo (GRUPO): No se puede decrementar, solo incrementa con reencauches
  - Fecha de ingreso: Registro de cuándo ingresó al inventario (inmutable)
  - Proveedor original: Quién suministró la llanta (inmutable)
  - Número de factura: Documento de origen (inmutable)
  - Registro en HISTORIA: Cualquier movimiento de la llanta queda registrado permanentemente

  19. ¿Qué restricciones temporales existen?

  - Fecha de ingreso: No puede ser futura
  - Fecha de ingreso: Debe ser >= fecha de última operación documentada de esa llanta
  - Orden temporal: Si llanta tiene GRUPO > '000', debe haber existido antes con grupos anteriores
  - Secuencia: Una llanta con GRUPO '002' debió pasar por '000' y '001'

  ---
  G. EVENTOS Y EFECTOS SECUNDARIOS

  20. ¿Esta operación genera eventos de dominio?

  Sí:

  Eventos publicados:
  - TireAddedToInventoryEvent (al crear)
  - TireInventoryUpdatedEvent (al actualizar)
  - TireRemovedFromInventoryEvent (al dar de baja o montar)
  - TireRetreadReceivedEvent (cuando llanta reencauchada regresa)

  21. ¿Hay efectos en cascada sobre otras entidades?

  Sí:

  Al crear llanta en inventario:
  - Se crea registro en tabla de auditoría (audit_log)
  - Se actualiza contador de llantas por ficha técnica (estadística)
  - Se actualiza valor total de inventario (agregado)

  Al sacar llanta de inventario (montaje):
  - Se elimina de tire_inventory
  - Se crea registro en active_tires (RF-003)
  - Se crea primer registro en tire_history
  - Se actualiza estadísticas de consumo

  Al actualizar localización:
  - Se registra movimiento interno en log de auditoría

  22. ¿Qué se debe registrar en auditoría?

  Tabla audit_log:
  - Acción: CREATE, UPDATE, DELETE
  - Entidad: tire_inventory
  - ID de registro (llanta + grupo)
  - Usuario que realizó la acción (created_by / updated_by)
  - Timestamp (created_at / updated_at)
  - Valores anteriores y nuevos (JSON con campos modificados)
  - Razón del cambio (observaciones)

  Campos de auditoría en tire_inventory:
  - created_at: TIMESTAMP WITH TIME ZONE
  - created_by: BIGINT (FK a users)
  - updated_at: TIMESTAMP WITH TIME ZONE
  - updated_by: BIGINT (FK a users)
  - deleted_at: TIMESTAMP WITH TIME ZONE (soft delete)
  - deleted_by: BIGINT (FK a users)

  ---
  H. INTERFAZ DE USUARIO

  23. ¿Qué tipo de formulario se necesita?

  Tipo: Master-Detail con búsqueda avanzada

  Componentes principales:
  1. Grilla de inventario (Master)
    - DataGrid con paginación server-side
    - Filtros: ficha técnica, localización, proveedor, rango fechas
    - Búsqueda rápida por número de llanta
    - Acciones por fila: Ver, Editar, Eliminar
  2. Formulario de alta/edición (Dialog modal)
    - Campos agrupados lógicamente
    - Autocompletado en fichas técnicas y proveedores
    - Validación en tiempo real
  3. Panel de detalle (Dialog de solo lectura)
    - Toda la información de la llanta
    - Información de la ficha técnica asociada
    - Datos del proveedor
    - Indicadores visuales (antigüedad, valor)

  24. ¿Qué campos son obligatorios vs opcionales?

  Obligatorios:
  - Número de llanta (LLANTA) ✓
  - Grupo (GRUPO) ✓
  - Valor ✓
  - Fecha de ingreso ✓
  - Proveedor ✓
  - Número de factura ✓
  - Ficha técnica ✓
  - Localización ✓

  Opcionales:
  - Observaciones (notas adicionales)
  - Campos de reencauche (solo si GRUPO > '000'):
    - Código de neumático aplicado
    - Valor del reencauche (separado del valor base)
    - Código de protector
    - Valor del protector

  25. ¿Hay dependencias entre campos?

  Sí:

  Dependencia 1: Ficha técnica → Marca/Tipo/Dimensión
  - Al seleccionar ficha técnica, se muestra automáticamente marca, tipo y dimensión (solo lectura)

  Dependencia 2: GRUPO → Campos de reencauche
  - Si GRUPO = '000': ocultar sección de reencauche
  - Si GRUPO > '000': mostrar y requerir campos de reencauche

  Dependencia 3: Proveedor → Datos del proveedor
  - Al seleccionar proveedor, mostrar nombre completo, NIT, contacto (solo lectura)

  Dependencia 4: Valor → Alerta de precio
  - Al ingresar valor, comparar con precio en ficha técnica
  - Si diferencia > 30%, mostrar advertencia (no bloquear)

  ---
  I. RIESGOS E INCERTIDUMBRES

  26. ¿Hay información ambigua o faltante en el requerimiento?

  Sí:

  1. Catálogos PROVEEDORES y LOCALIZA:
    - Ya aclarado: implementar como RF-001 extendido
    - Necesito definir estructura completa de estos catálogos
  2. Campos de reencauche:
    - ¿Son obligatorios si GRUPO > '000'?
    - ¿Cómo se relacionan con la ficha técnica? ¿Es diferente ficha?
  3. Proceso de reencauche:
    - ¿Quién incrementa el GRUPO? ¿Manual o automático?
    - ¿RF-002 registra el ingreso post-reencauche o hay otro RF?
  4. Eliminación de inventario:
    - Ya aclarado: soft delete siempre
    - ¿Qué permisos se requieren para soft delete?

  27. ¿Qué supuestos debo hacer?

  Supuesto 1: Los catálogos TIRE_SUPPLIERS y TIRE_WAREHOUSE_LOCATIONS se implementan antes de RF-002 o en paralelo

  Supuesto 2: El incremento de GRUPO al reencauchar es manual en el formulario de ingreso (operario conoce el historial)

  Supuesto 3: Una llanta puede cambiar de localización mientras está en inventario (movilidad interna en bodega)

  Supuesto 4: El valor registrado es el valor contable/financiero, no necesariamente el precio de compra

  Supuesto 5: El soft delete requiere ROLE_ADMIN (no cualquier almacenista)

  28. ¿Qué requiere confirmación explícita del Product Owner?

  1. Estructura de catálogo TIRE_SUPPLIERS:
    - ¿Qué campos adicionales a código, nombre, NIT, contacto?
    - ¿Email? ¿Teléfono? ¿Dirección? ¿Cuenta bancaria?
  2. Estructura de catálogo TIRE_WAREHOUSE_LOCATIONS:
    - ¿Solo código/nombre simple?
    - ¿O estructura completa (bodega, zona, pasillo, nivel, posición)?
  3. Lógica de reencauche:
    - ¿Dónde se registra el envío a reencauche?
    - ¿Cómo se registra el regreso? ¿Es un nuevo ingreso a inventario con GRUPO+1?
  4. Alertas de obsolescencia:
    - ¿Cuál es el umbral de días para considerar una llanta obsoleta?
    - ¿120, 180, 365 días?

  ---
  J. INTEGRACIÓN CON MÓDULOS EXISTENTES

  29. ¿Este requerimiento depende de Auth o Users?

  Sí:

  Dependencia de Auth:
  - Control de acceso a endpoints de inventario
  - Validación de token JWT en cada operación

  Dependencia de Users:
  - Registrar created_by / updated_by (FK a tabla users)
  - Permisos por rol:
    - ROLE_ALMACENISTA: Crear, editar, consultar
    - ROLE_JEFE_TALLER: Solo consultar
    - ROLE_ADMIN: Todas las operaciones + soft delete

  30. ¿Necesita permisos especiales?

  Sí, nuevos permisos a crear:

  Permisos requeridos:
  - tire:inventory:read - Consultar inventario
  - tire:inventory:create - Crear registro en inventario
  - tire:inventory:update - Actualizar datos
  - tire:inventory:delete - Soft delete (solo admins)
  - tire:inventory:stats - Ver estadísticas y reportes

  Asignación por rol:
  ROLE_ALMACENISTA: read, create, update
  ROLE_JEFE_TALLER: read, stats
  ROLE_ADMIN: read, create, update, delete, stats

  31. ¿Consume catálogos compartidos (shared schema)?

  Sí:

  Catálogos de RF-001 (ya implementados):
  - tire_specifications (fichas técnicas)
  - tire_brands (marcas)
  - tire_types (tipos)
  - tire_references (referencias)

  Catálogos nuevos a implementar (RF-001 extendido):
  - tire_suppliers (proveedores)
  - tire_warehouse_locations (localizaciones)

  Integración:
  - Consultar fichas técnicas para selector (GET /api/v1/tires/specifications)
  - Validar que ficha técnica exista y esté activa
  - Usar DTOs de RF-001 para mostrar información de ficha técnica

  ---
  FASE 2: DISEÑO DE SOLUCIÓN

  📐 1. AGGREGATE ROOT Y ENTIDADES

  Aggregate Root:
  - TireInventory (Inventario de Llanta)

  Entidades del Agregado:
  - TireInventory (entidad principal)

  Value Objects:
  - TireIdentifier (LLANTA + GRUPO)
  - TireGroup (GRUPO con validaciones)
  - TireValue (valor monetario)
  - InvoiceNumber (número de factura)

  Entidades Relacionadas (fuera del agregado):
  - TireSpecification (RF-001 - ya implementado)
  - TireSupplier (nuevo catálogo)
  - WarehouseLocation (nuevo catálogo)

  Límites del agregado:
  - TireInventory es el Aggregate Root
  - Mantiene referencias (IDs) a TireSpecification, TireSupplier, WarehouseLocation
  - No incluye estas entidades dentro del agregado (bounded context separation)

  ---
  2. CASOS DE USO (Application Layer)

  UC-001: CreateTireInventoryUseCase
  - Nombre: Registrar nueva llanta en inventario
  - Flujo principal:
    a. Validar datos de entrada (DTOs con Bean Validation)
    b. Verificar que ficha técnica existe y está activa
    c. Verificar que proveedor existe
    d. Verificar que localización existe
    e. Verificar unicidad de (LLANTA, GRUPO)
    f. Crear entidad TireInventory
    g. Persistir en base de datos
    h. Registrar auditoría
    i. Publicar evento TireAddedToInventoryEvent
    j. Retornar DTO de respuesta
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:create
    - Ficha técnica, proveedor y localización existen
  - Postcondiciones:
    - Llanta registrada en inventario
    - Auditoría registrada
    - Evento publicado
  - Invariantes validadas:
    - INV-001 (unicidad), INV-002 (valor > 0), INV-003 (fecha coherente), INV-004 (referencias válidas), INV-005 (formato grupo)

  UC-002: GetTireInventoryByIdUseCase
  - Nombre: Obtener llanta de inventario por ID
  - Flujo principal:
    a. Recibir LLANTA + GRUPO
    b. Buscar en repositorio
    c. Si no existe, lanzar NotFoundException
    d. Mapear a DTO de respuesta con datos completos (incluir ficha técnica, proveedor, localización)
    e. Retornar DTO
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:read
  - Postcondiciones:
    - DTO con información completa retornado

  UC-003: ListTireInventoryUseCase
  - Nombre: Listar inventario con paginación y filtros
  - Flujo principal:
    a. Recibir parámetros de paginación (page, size, sort)
    b. Recibir filtros opcionales (fichatec_id, supplier_id, location_id, fecha_desde, fecha_hasta)
    c. Construir query con filtros
    d. Ejecutar consulta paginada
    e. Mapear resultados a DTOs
    f. Retornar PageResponse con metadata
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:read
  - Postcondiciones:
    - Lista paginada retornada

  UC-004: SearchTireInventoryUseCase
  - Nombre: Buscar llanta por número o texto libre
  - Flujo principal:
    a. Recibir texto de búsqueda
    b. Buscar en: LLANTA (exacto o like), número de factura, observaciones
    c. Aplicar paginación
    d. Retornar resultados
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:read
  - Postcondiciones:
    - Resultados de búsqueda retornados

  UC-005: UpdateTireInventoryUseCase
  - Nombre: Actualizar datos de llanta en inventario
  - Flujo principal:
    a. Validar datos de entrada
    b. Buscar llanta existente
    c. Verificar que no tiene movimientos históricos (si se intenta cambiar campos críticos)
    d. Actualizar campos permitidos: localización, valor, observaciones
    e. Persistir cambios
    f. Registrar auditoría
    g. Publicar evento TireInventoryUpdatedEvent
    h. Retornar DTO actualizado
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:update
    - Llanta existe en inventario
  - Postcondiciones:
    - Llanta actualizada
    - Auditoría registrada
  - Invariantes validadas:
    - No se permite cambiar: LLANTA, GRUPO, fecha_ingreso, proveedor, factura

  UC-006: DeleteTireInventoryUseCase (Soft Delete)
  - Nombre: Dar de baja llanta de inventario
  - Flujo principal:
    a. Verificar permisos de admin
    b. Buscar llanta existente
    c. Verificar que NO tiene movimientos históricos
    d. Marcar como eliminada (deleted_at, deleted_by)
    e. Registrar auditoría con razón de baja
    f. Publicar evento TireRemovedFromInventoryEvent
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:delete (ROLE_ADMIN)
    - Llanta existe y no tiene histórico
  - Postcondiciones:
    - Llanta marcada como eliminada (soft delete)
    - Auditoría registrada
  - Invariantes validadas:
    - RN-003 (no eliminar con histórico)

  UC-007: GetInventoryStatisticsUseCase
  - Nombre: Obtener estadísticas del inventario
  - Flujo principal:
    a. Calcular totales: cantidad de llantas, valor total
    b. Agrupar por ficha técnica
    c. Agrupar por localización
    d. Calcular antigüedad promedio
    e. Identificar llantas obsoletas (> 365 días)
    f. Retornar DTO con estadísticas
  - Precondiciones:
    - Usuario autenticado con permiso tire:inventory:stats
  - Postcondiciones:
    - DTO con métricas retornado

  ---
  3. API REST

  Endpoints RESTful:

  # Crear llanta en inventario
  POST /api/v1/tires/inventory
  Request:
    {
      "tireNumber": "LT-2024-001",
      "group": "000",
      "value": 1500000.00,
      "entryDate": "2026-01-27",
      "supplierId": "uuid",
      "invoiceNumber": "FAC-001",
      "specificationId": "uuid",
      "locationId": "uuid",
      "notes": "Llanta nueva Michelin 295/80R22.5"
    }
  Response: 201 Created
    {
      "id": "uuid",
      "tireNumber": "LT-2024-001",
      "group": "000",
      "value": 1500000.00,
      "entryDate": "2026-01-27",
      "specification": { ... },
      "supplier": { ... },
      "location": { ... },
      "ageInDays": 0,
      "createdAt": "2026-01-27T10:00:00Z"
    }
  Errores:
    - 400: Datos inválidos
    - 409: Llanta duplicada (LLANTA + GRUPO ya existe)
    - 404: Ficha técnica/proveedor/localización no encontrada

  # Obtener llanta por ID
  GET /api/v1/tires/inventory/{tireNumber}/{group}
  Response: 200 OK
    {
      "id": "uuid",
      "tireNumber": "LT-2024-001",
      "group": "000",
      // ... datos completos con relaciones
    }
  Errores:
    - 404: Llanta no encontrada

  # Listar inventario (paginado)
  GET /api/v1/tires/inventory?page=0&size=10&sort=entryDate,desc&specificationId=uuid&locationId=uuid
  Response: 200 OK
    {
      "content": [ ... ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "totalElements": 45,
        "totalPages": 5
      }
    }

  # Buscar por texto
  GET /api/v1/tires/inventory/search?query=LT-2024&page=0&size=10
  Response: 200 OK
    {
      "content": [ ... ],
      "pageable": { ... }
    }

  # Actualizar llanta
  PUT /api/v1/tires/inventory/{tireNumber}/{group}
  Request:
    {
      "locationId": "new-location-uuid",
      "value": 1450000.00,
      "notes": "Ajuste de precio"
    }
  Response: 200 OK
    { ... llanta actualizada ... }
  Errores:
    - 400: Datos inválidos
    - 404: Llanta no encontrada
    - 422: No se permite modificar campos inmutables

  # Soft delete
  DELETE /api/v1/tires/inventory/{tireNumber}/{group}?reason=obsoleta
  Response: 204 No Content
  Errores:
    - 403: Sin permisos (requiere ADMIN)
    - 404: Llanta no encontrada
    - 422: Llanta tiene movimientos históricos (no se puede eliminar)

  # Estadísticas
  GET /api/v1/tires/inventory/statistics
  Response: 200 OK
    {
      "totalQuantity": 45,
      "totalValue": 67500000.00,
      "averageAge": 45.3,
      "bySpecification": [ ... ],
      "byLocation": [ ... ],
      "obsoleteTires": 3
    }

  ---
  4. MODELO DE DOMINIO (Diagrama Mermaid)

  erDiagram
      TIRE_INVENTORY ||--|| TIRE_SPECIFICATIONS : "references"
      TIRE_INVENTORY ||--|| TIRE_SUPPLIERS : "references"
      TIRE_INVENTORY ||--|| TIRE_WAREHOUSE_LOCATIONS : "references"
      TIRE_INVENTORY ||--o{ TIRE_HISTORY : "generates"

      TIRE_INVENTORY {
          uuid id PK
          string tire_number "Número único de llanta"
          char(3) group "000=nueva, 001+=reencauche"
          decimal value "Valor de la llanta"
          date entry_date "Fecha de ingreso"
          string invoice_number "Número de factura"
          text notes "Observaciones"
          uuid specification_id FK
          uuid supplier_id FK
          uuid location_id FK
          timestamp created_at
          bigint created_by FK
          timestamp updated_at
          bigint updated_by FK
          timestamp deleted_at
          bigint deleted_by FK
      }

      TIRE_SPECIFICATIONS {
          uuid id PK
          string code "Código FT-XXXXXX"
          string brand
          string type
          string dimension
          int expected_mileage
          decimal initial_depth
          boolean is_active
      }

      TIRE_SUPPLIERS {
          uuid id PK
          string code "Código proveedor"
          string name "Nombre del proveedor"
          string tax_id "NIT"
          string contact_info "Email/teléfono"
          boolean is_active
      }

      TIRE_WAREHOUSE_LOCATIONS {
          uuid id PK
          string code "Código localización"
          string name "Nombre/descripción"
          boolean is_active
      }

      TIRE_HISTORY {
          uuid id PK
          string tire_number
          char(3) group
          uuid vehicle_id
          int position
          date install_date
          int install_km
          date removal_date
          int removal_km
      }

  ---
  FASE 3: CONFIRMACIÓN EXPLÍCITA

  📝 Resumen de Puntos Clave del Diseño

  1. Aggregate Root: TireInventory como entidad principal
  2. 7 Use Cases implementados con validaciones completas
  3. API RESTful con 6 endpoints principales
  4. Integración con RF-001 (tire_specifications) y catálogos nuevos (suppliers, locations)
  5. Auditoría completa con campos created_by, updated_by, deleted_at
  6. Soft delete para mantener trazabilidad
  7. Eventos de dominio para desacoplamiento
  8. Value Objects para encapsular lógica de validación
  9. Paginación server-side en listados
  10. Permisos granulares por operación

  ⚠️ Supuestos y Áreas de Riesgo Identificados

  Supuestos Críticos:
  1. Los catálogos TIRE_SUPPLIERS y TIRE_WAREHOUSE_LOCATIONS se implementarán antes o en paralelo como parte de RF-001 extendido
  2. El incremento de GRUPO al reencauchar es manual (operario conoce historial)
  3. Una llanta puede cambiar de localización mientras está en inventario
  4. El soft delete requiere ROLE_ADMIN

  Áreas de Riesgo:
  1. Concurrencia: Dos usuarios intentando crear la misma llanta simultáneamente
    - Mitigación: Unique constraint en BD + manejo de excepciones
  2. Integridad referencial: Dependencia de catálogos externos
    - Mitigación: Validaciones en Use Cases + constraints FK en BD
  3. Migración de datos: Si hay inventario existente en Oracle legacy
    - Mitigación: Scripts de migración con validaciones
  4. Performance: Consultas con múltiples JOINs para mostrar datos completos
    - Mitigación: Índices adecuados + DTOs proyectados

  ---
  ✅ SOLICITUD DE CONFIRMACIÓN EXPLÍCITA

  He completado el análisis detallado del RF-002 con:
  - ✅ 31 preguntas respondidas (A-J)
  - ✅ Diseño de arquitectura (Aggregates, Use Cases, API REST)
  - ✅ Modelo de dominio con diagrama
  - ✅ Identificación de riesgos y supuestos

  ¿Confirmas que el análisis y diseño presentado es correcto y puedo proceder con la implementación?

  Opciones de respuesta:
  - ✅ "Sí, procede con la implementación"
  - ✅ "Confirmado, adelante"
  - 🔄 "Correcto, pero ajusta [especificar qué]"
  - ❌ "Espera, necesito aclarar [especificar qué]"

  Aguardo tu confirmación explícita para continuar con la codificación.


 ---
  🏗️ REDISEÑO COMPLETO - RF-002 CON ARQUITECTURA MULTI-SEDE

  📊 NUEVA ESTRUCTURA ORGANIZACIONAL

  Jerarquía Confirmada

  EMPRESA TRANSER
    │
    ├── SEDE BOGOTÁ (Office)
    │     ├── Almacén Principal (Warehouse)
    │     │     ├── A-1 (Location)
    │     │     ├── A-2 (Location)
    │     │     └── B-1 (Location)
    │     └── Almacén Temporal (Warehouse)
    │           └── T-1 (Location)
    │
    ├── SEDE MEDELLÍN (Office)
    │     ├── Almacén Central (Warehouse)
    │     │     └── C-1, C-2, C-3 (Locations)
    │     └── Almacén Norte (Warehouse)
    │
    └── SEDE CALI (Office)
          └── ...

  ---
  🎯 ALCANCE REDEFINIDO

  RF-002: Control de Inventario de Llantas (Ajustado)

  Incluye:
  - ✅ Gestión de inventario por almacén/sede
  - ✅ CRUD de llantas en inventario
  - ✅ Filtrado automático por sede del usuario
  - ✅ Consultas y reportes por sede

  RF-001 EXTENDIDO: Gestión de Estructura Organizacional (NUEVO)

  Debe implementarse ANTES de RF-002:
  - ✅ CRUD de Oficinas/Sedes (Offices)
  - ✅ CRUD de Almacenes por Sede (Warehouses)
  - ✅ CRUD de Ubicaciones por Almacén (Warehouse Locations)
  - ✅ CRUD de Proveedores por Sede (Tire Suppliers)

  RF-007: Traslados de Llantas (NUEVO - Implementar DESPUÉS)

  Flujo especial:
  - ✅ Traslado entre almacenes de la misma sede
  - ✅ Traslado entre sedes (requiere aprobación)
  - ✅ Registro de movimientos en auditoría

  ---
  🗄️ MODELO DE DATOS COMPLETO

  1. Estructura Organizacional (RF-001 Extendido)

  -- =====================================================
  -- TABLA: offices (Sedes/Oficinas)
  -- =====================================================
  CREATE TABLE offices (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      code VARCHAR(10) NOT NULL UNIQUE,
      name VARCHAR(100) NOT NULL,
      city VARCHAR(50),
      address TEXT,
      phone VARCHAR(20),
      is_active BOOLEAN NOT NULL DEFAULT true,

      -- Auditoría
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      created_by BIGINT REFERENCES users(id),
      updated_at TIMESTAMP WITH TIME ZONE,
      updated_by BIGINT REFERENCES users(id),
      deleted_at TIMESTAMP WITH TIME ZONE,
      deleted_by BIGINT REFERENCES users(id)
  );

  CREATE INDEX idx_offices_code ON offices(code);
  CREATE INDEX idx_offices_is_active ON offices(is_active) WHERE deleted_at IS NULL;

  COMMENT ON TABLE offices IS 'Sedes u oficinas de la empresa';
  COMMENT ON COLUMN offices.code IS 'Código único de la sede (ej: BOG, MED, CAL)';

  -- =====================================================
  -- TABLA: warehouses (Almacenes por Sede)
  -- =====================================================
  CREATE TABLE warehouses (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      code VARCHAR(10) NOT NULL,
      name VARCHAR(100) NOT NULL,
      office_id UUID NOT NULL REFERENCES offices(id),
      description TEXT,
      is_active BOOLEAN NOT NULL DEFAULT true,

      -- Auditoría
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      created_by BIGINT REFERENCES users(id),
      updated_at TIMESTAMP WITH TIME ZONE,
      updated_by BIGINT REFERENCES users(id),
      deleted_at TIMESTAMP WITH TIME ZONE,
      deleted_by BIGINT REFERENCES users(id),

      -- Constraint: código único por sede
      CONSTRAINT uk_warehouse_office_code UNIQUE(office_id, code)
  );

  CREATE INDEX idx_warehouses_office ON warehouses(office_id);
  CREATE INDEX idx_warehouses_is_active ON warehouses(is_active) WHERE deleted_at IS NULL;

  COMMENT ON TABLE warehouses IS 'Almacenes de cada sede';
  COMMENT ON COLUMN warehouses.code IS 'Código del almacén dentro de la sede (ej: PRINCIPAL, TEMPORAL)';

  -- =====================================================
  -- TABLA: warehouse_locations (Ubicaciones dentro de Almacén)
  -- =====================================================
  CREATE TABLE warehouse_locations (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      code VARCHAR(10) NOT NULL,
      name VARCHAR(100),
      warehouse_id UUID NOT NULL REFERENCES warehouses(id),
      description TEXT,
      is_active BOOLEAN NOT NULL DEFAULT true,

      -- Auditoría
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      created_by BIGINT REFERENCES users(id),
      updated_at TIMESTAMP WITH TIME ZONE,
      updated_by BIGINT REFERENCES users(id),
      deleted_at TIMESTAMP WITH TIME ZONE,
      deleted_by BIGINT REFERENCES users(id),

      -- Constraint: código único por almacén
      CONSTRAINT uk_location_warehouse_code UNIQUE(warehouse_id, code)
  );

  CREATE INDEX idx_warehouse_locations_warehouse ON warehouse_locations(warehouse_id);
  CREATE INDEX idx_warehouse_locations_is_active ON warehouse_locations(is_active) WHERE deleted_at IS NULL;

  COMMENT ON TABLE warehouse_locations IS 'Ubicaciones físicas dentro de cada almacén';
  COMMENT ON COLUMN warehouse_locations.code IS 'Código de ubicación (ej: A-1, A-2, B-1)';

  -- =====================================================
  -- TABLA: tire_suppliers (Proveedores por Sede)
  -- =====================================================
  CREATE TABLE tire_suppliers (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      code VARCHAR(10) NOT NULL,
      name VARCHAR(100) NOT NULL,
      tax_id VARCHAR(20) NOT NULL, -- NIT
      office_id UUID NOT NULL REFERENCES offices(id),

      -- Información de contacto
      contact_name VARCHAR(100),
      email VARCHAR(100),
      phone VARCHAR(20),
      address TEXT,

      is_active BOOLEAN NOT NULL DEFAULT true,

      -- Auditoría
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      created_by BIGINT REFERENCES users(id),
      updated_at TIMESTAMP WITH TIME ZONE,
      updated_by BIGINT REFERENCES users(id),
      deleted_at TIMESTAMP WITH TIME ZONE,
      deleted_by BIGINT REFERENCES users(id),

      -- Constraint: código único por sede
      CONSTRAINT uk_supplier_office_code UNIQUE(office_id, code)
  );

  CREATE INDEX idx_tire_suppliers_office ON tire_suppliers(office_id);
  CREATE INDEX idx_tire_suppliers_tax_id ON tire_suppliers(tax_id);
  CREATE INDEX idx_tire_suppliers_is_active ON tire_suppliers(is_active) WHERE deleted_at IS NULL;

  COMMENT ON TABLE tire_suppliers IS 'Proveedores de llantas por sede';

  2. Inventario de Llantas (RF-002 Ajustado)

  -- =====================================================
  -- TABLA: tire_inventory (Inventario con Warehouse)
  -- =====================================================
  CREATE TABLE tire_inventory (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

      -- Identificación de la llanta
      tire_number VARCHAR(20) NOT NULL,
      "group" CHAR(3) NOT NULL, -- '000' = nueva, '001'+= reencauche

      -- Valor y documentación
      value DECIMAL(12,2) NOT NULL CHECK (value > 0),
      entry_date DATE NOT NULL,
      invoice_number VARCHAR(50) NOT NULL,
      notes TEXT,

      -- Relaciones
      specification_id UUID NOT NULL REFERENCES tire_specifications(id),
      supplier_id UUID NOT NULL REFERENCES tire_suppliers(id),
      warehouse_id UUID NOT NULL REFERENCES warehouses(id), -- CLAVE: almacén específico
      location_id UUID NOT NULL REFERENCES warehouse_locations(id),

      -- Campos de reencauche (solo si group > '000')
      retread_tire_code VARCHAR(20),
      retread_value DECIMAL(12,2),
      protector_code VARCHAR(20),
      protector_value DECIMAL(12,2),

      -- Auditoría
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      created_by BIGINT REFERENCES users(id),
      updated_at TIMESTAMP WITH TIME ZONE,
      updated_by BIGINT REFERENCES users(id),
      deleted_at TIMESTAMP WITH TIME ZONE,
      deleted_by BIGINT REFERENCES users(id),

      -- Constraints
      CONSTRAINT uk_tire_inventory_number_group UNIQUE(tire_number, "group"),
      CONSTRAINT chk_tire_inventory_group_format CHECK ("group" ~ '^[0-9]{3}$'),

      -- Constraint: location debe pertenecer al warehouse
      CONSTRAINT fk_tire_inventory_location_warehouse
          FOREIGN KEY (location_id)
          REFERENCES warehouse_locations(id)
  );

  -- Índices para performance
  CREATE INDEX idx_tire_inventory_warehouse ON tire_inventory(warehouse_id) WHERE deleted_at IS NULL;
  CREATE INDEX idx_tire_inventory_specification ON tire_inventory(specification_id);
  CREATE INDEX idx_tire_inventory_supplier ON tire_inventory(supplier_id);
  CREATE INDEX idx_tire_inventory_location ON tire_inventory(location_id);
  CREATE INDEX idx_tire_inventory_tire_number ON tire_inventory(tire_number);
  CREATE INDEX idx_tire_inventory_entry_date ON tire_inventory(entry_date);
  CREATE INDEX idx_tire_inventory_deleted ON tire_inventory(deleted_at);

  COMMENT ON TABLE tire_inventory IS 'Inventario de llantas por almacén';
  COMMENT ON COLUMN tire_inventory.warehouse_id IS 'Almacén donde está físicamente la llanta';
  COMMENT ON COLUMN tire_inventory.location_id IS 'Ubicación específica dentro del almacén';

  3. Vista para Consultas con Jerarquía Completa

  -- =====================================================
  -- VISTA: v_tire_inventory_full (con toda la jerarquía)
  -- =====================================================
  CREATE OR REPLACE VIEW v_tire_inventory_full AS
  SELECT
      ti.id,
      ti.tire_number,
      ti.group,
      ti.value,
      ti.entry_date,
      CURRENT_DATE - ti.entry_date AS age_in_days,
      ti.invoice_number,
      ti.notes,

      -- Warehouse y jerarquía
      w.id AS warehouse_id,
      w.code AS warehouse_code,
      w.name AS warehouse_name,
      o.id AS office_id,
      o.code AS office_code,
      o.name AS office_name,
      o.city AS office_city,

      -- Location
      wl.id AS location_id,
      wl.code AS location_code,
      wl.name AS location_name,

      -- Specification
      ts.id AS specification_id,
      ts.code AS specification_code,
      ts.dimension,
      tb.name AS brand_name,
      tt.name AS type_name,

      -- Supplier
      s.id AS supplier_id,
      s.code AS supplier_code,
      s.name AS supplier_name,
      s.tax_id AS supplier_tax_id,

      -- Auditoría
      ti.created_at,
      ti.created_by,
      ti.updated_at,
      ti.updated_by

  FROM tire_inventory ti
  JOIN warehouses w ON ti.warehouse_id = w.id
  JOIN offices o ON w.office_id = o.id
  JOIN warehouse_locations wl ON ti.location_id = wl.id
  JOIN tire_specifications ts ON ti.specification_id = ts.id
  JOIN tire_brands tb ON ts.brand_id = tb.id
  JOIN tire_types tt ON ts.type_id = tt.id
  JOIN tire_suppliers s ON ti.supplier_id = s.id
  WHERE ti.deleted_at IS NULL
    AND w.deleted_at IS NULL
    AND o.is_active = true;

  COMMENT ON VIEW v_tire_inventory_full IS 'Vista completa del inventario con toda la jerarquía organizacional';

  ---
  🔒 SEGURIDAD MULTI-TENANT

  Actualización en Tabla Users

  -- Agregar campo office_id a users (si no existe)
  ALTER TABLE users
  ADD COLUMN office_id UUID REFERENCES offices(id);

  CREATE INDEX idx_users_office ON users(office_id);

  COMMENT ON COLUMN users.office_id IS 'Sede asignada al usuario';

  Roles Actualizados

  -- Roles con alcance organizacional
  INSERT INTO roles (name, description) VALUES
  ('ROLE_ADMIN_NATIONAL', 'Administrador Nacional - Ve todas las sedes'),
  ('ROLE_ADMIN_OFFICE', 'Administrador de Sede - Ve solo su sede'),
  ('ROLE_WAREHOUSE_MANAGER', 'Almacenista - Ve solo su(s) almacén(es)'),
  ('ROLE_OFFICE_VIEWER', 'Consultor de Sede - Solo lectura de su sede');

  -- Permisos granulares
  INSERT INTO permissions (name, description) VALUES
  -- Inventario
  ('tire:inventory:read:own', 'Consultar inventario de su sede'),
  ('tire:inventory:read:all', 'Consultar inventario de todas las sedes'),
  ('tire:inventory:create', 'Crear registro en inventario'),
  ('tire:inventory:update', 'Actualizar inventario'),
  ('tire:inventory:delete', 'Eliminar inventario (soft delete)'),
  ('tire:inventory:transfer', 'Transferir llantas entre almacenes/sedes'),

  -- Estructura organizacional
  ('organization:offices:manage', 'Gestionar sedes'),
  ('organization:warehouses:manage', 'Gestionar almacenes'),
  ('organization:locations:manage', 'Gestionar ubicaciones'),
  ('organization:suppliers:manage', 'Gestionar proveedores');

  Filtrado Automático por Sede (Row-Level Security)

  -- Función para obtener office_id del usuario actual
  CREATE OR REPLACE FUNCTION get_user_office_id()
  RETURNS UUID AS $$
  DECLARE
      v_office_id UUID;
  BEGIN
      SELECT office_id INTO v_office_id
      FROM users
      WHERE id = current_setting('app.current_user_id')::BIGINT;

      RETURN v_office_id;
  END;
  $$ LANGUAGE plpgsql STABLE;

  -- Policy para tire_inventory (Row-Level Security)
  ALTER TABLE tire_inventory ENABLE ROW LEVEL SECURITY;

  -- Policy: Usuarios ven solo inventario de su sede
  CREATE POLICY tire_inventory_office_isolation ON tire_inventory
      FOR ALL
      TO authenticated_user
      USING (
          -- Admin nacional ve todo
          current_user_has_role('ROLE_ADMIN_NATIONAL')
          OR
          -- Otros usuarios solo su sede
          warehouse_id IN (
              SELECT w.id
              FROM warehouses w
              WHERE w.office_id = get_user_office_id()
                AND w.deleted_at IS NULL
          )
      );

  ---
  🏗️ ARQUITECTURA BACKEND (Clean Architecture)

  Domain Layer

  // Value Object para Office
  package com.transer.vortice.organization.domain.model;

  @Embeddable
  public class OfficeId {
      private UUID value;

      // Constructor, equals, hashCode
  }

  // Entity: Office
  @Entity
  @Table(name = "offices")
  public class Office extends AuditableUUIDEntity {
      @Column(nullable = false, unique = true, length = 10)
      private String code;

      @Column(nullable = false, length = 100)
      private String name;

      @Column(length = 50)
      private String city;

      @Column(columnDefinition = "TEXT")
      private String address;

      @Column(length = 20)
      private String phone;

      @Column(nullable = false)
      private Boolean isActive = true;

      // Getters, setters, business methods
  }

  // Entity: Warehouse
  @Entity
  @Table(name = "warehouses")
  public class Warehouse extends AuditableUUIDEntity {
      @Column(nullable = false, length = 10)
      private String code;

      @Column(nullable = false, length = 100)
      private String name;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "office_id", nullable = false)
      private Office office;

      @Column(columnDefinition = "TEXT")
      private String description;

      @Column(nullable = false)
      private Boolean isActive = true;

      // Business method
      public boolean belongsToOffice(UUID officeId) {
          return this.office.getId().equals(officeId);
      }
  }

  // Entity: WarehouseLocation
  @Entity
  @Table(name = "warehouse_locations")
  public class WarehouseLocation extends AuditableUUIDEntity {
      @Column(nullable = false, length = 10)
      private String code;

      @Column(length = 100)
      private String name;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "warehouse_id", nullable = false)
      private Warehouse warehouse;

      @Column(columnDefinition = "TEXT")
      private String description;

      @Column(nullable = false)
      private Boolean isActive = true;
  }

  // Entity: TireSupplier
  @Entity
  @Table(name = "tire_suppliers")
  public class TireSupplier extends AuditableUUIDEntity {
      @Column(nullable = false, length = 10)
      private String code;

      @Column(nullable = false, length = 100)
      private String name;

      @Column(name = "tax_id", nullable = false, length = 20)
      private String taxId; // NIT

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "office_id", nullable = false)
      private Office office;

      @Column(name = "contact_name", length = 100)
      private String contactName;

      @Column(length = 100)
      private String email;

      @Column(length = 20)
      private String phone;

      @Column(columnDefinition = "TEXT")
      private String address;

      @Column(nullable = false)
      private Boolean isActive = true;
  }

  Entity: TireInventory (Ajustado)

  package com.transer.vortice.tire.domain.model;

  @Entity
  @Table(name = "tire_inventory")
  public class TireInventory extends AuditableUUIDEntity {

      @Column(name = "tire_number", nullable = false, length = 20)
      private String tireNumber;

      @Column(name = "group", nullable = false, length = 3)
      private String group; // '000' = nueva, '001'+= reencauche

      @Column(nullable = false, precision = 12, scale = 2)
      private BigDecimal value;

      @Column(name = "entry_date", nullable = false)
      private LocalDate entryDate;

      @Column(name = "invoice_number", nullable = false, length = 50)
      private String invoiceNumber;

      @Column(columnDefinition = "TEXT")
      private String notes;

      // Relaciones
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "specification_id", nullable = false)
      private TireSpecification specification;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "supplier_id", nullable = false)
      private TireSupplier supplier;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "warehouse_id", nullable = false)
      private Warehouse warehouse; // CLAVE: almacén específico

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "location_id", nullable = false)
      private WarehouseLocation location;

      // Campos de reencauche (opcionales)
      @Column(name = "retread_tire_code", length = 20)
      private String retreadTireCode;

      @Column(name = "retread_value", precision = 12, scale = 2)
      private BigDecimal retreadValue;

      @Column(name = "protector_code", length = 20)
      private String protectorCode;

      @Column(name = "protector_value", precision = 12, scale = 2)
      private BigDecimal protectorValue;

      // Business methods
      public boolean isNew() {
          return "000".equals(this.group);
      }

      public boolean isRetreaded() {
          return !isNew();
      }

      public int getAgeInDays() {
          return (int) ChronoUnit.DAYS.between(this.entryDate, LocalDate.now());
      }

      public boolean isObsolete(int thresholdDays) {
          return getAgeInDays() > thresholdDays;
      }

      public UUID getOfficeId() {
          return this.warehouse.getOffice().getId();
      }

      public boolean belongsToOffice(UUID officeId) {
          return this.warehouse.belongsToOffice(officeId);
      }

      // Validación de negocio
      public void validateLocationBelongsToWarehouse() {
          if (!this.location.getWarehouse().getId().equals(this.warehouse.getId())) {
              throw new BusinessException("La ubicación no pertenece al almacén especificado");
          }
      }
  }

  ---
  🎯 USE CASES AJUSTADOS CON MULTI-TENANT

  CreateTireInventoryUseCase (Ajustado)

  @Service
  @RequiredArgsConstructor
  @Transactional
  public class CreateTireInventoryUseCase {

      private final TireInventoryRepository tireInventoryRepository;
      private final TireSpecificationRepository specificationRepository;
      private final TireSupplierRepository supplierRepository;
      private final WarehouseRepository warehouseRepository;
      private final WarehouseLocationRepository locationRepository;
      private final SecurityContext securityContext;
      private final TireInventoryMapper mapper;

      public TireInventoryResponse execute(CreateTireInventoryRequest request) {
          // 1. Obtener office_id del usuario autenticado
          UUID userOfficeId = securityContext.getCurrentUserOfficeId();

          // 2. Validar que specification existe (global, no por sede)
          TireSpecification specification = specificationRepository
              .findById(request.getSpecificationId())
              .orElseThrow(() -> new NotFoundException("Ficha técnica no encontrada"));

          // 3. Validar que warehouse existe Y pertenece a la sede del usuario
          Warehouse warehouse = warehouseRepository
              .findById(request.getWarehouseId())
              .orElseThrow(() -> new NotFoundException("Almacén no encontrado"));

          if (!warehouse.belongsToOffice(userOfficeId) && !securityContext.isNationalAdmin()) {
              throw new ForbiddenException("No tiene permiso para agregar inventario a este almacén");
          }

          // 4. Validar que location pertenece al warehouse
          WarehouseLocation location = locationRepository
              .findById(request.getLocationId())
              .orElseThrow(() -> new NotFoundException("Ubicación no encontrada"));

          if (!location.getWarehouse().getId().equals(warehouse.getId())) {
              throw new BusinessException("La ubicación no pertenece al almacén especificado");
          }

          // 5. Validar que supplier existe Y pertenece a la sede del usuario
          TireSupplier supplier = supplierRepository
              .findById(request.getSupplierId())
              .orElseThrow(() -> new NotFoundException("Proveedor no encontrado"));

          if (!supplier.getOffice().getId().equals(userOfficeId) && !securityContext.isNationalAdmin()) {
              throw new BusinessException("El proveedor no pertenece a su sede");
          }

          // 6. Validar unicidad de (tireNumber, group)
          if (tireInventoryRepository.existsByTireNumberAndGroup(
                  request.getTireNumber(), request.getGroup())) {
              throw new ConflictException("Ya existe una llanta con ese número y grupo");
          }

          // 7. Crear entidad
          TireInventory inventory = mapper.toEntity(request);
          inventory.setSpecification(specification);
          inventory.setSupplier(supplier);
          inventory.setWarehouse(warehouse);
          inventory.setLocation(location);

          // 8. Validaciones de negocio
          inventory.validateLocationBelongsToWarehouse();

          // 9. Persistir
          TireInventory saved = tireInventoryRepository.save(inventory);

          // 10. Publicar evento
          // eventPublisher.publish(new TireAddedToInventoryEvent(saved));

          // 11. Retornar DTO
          return mapper.toResponse(saved);
      }
  }

  ListTireInventoryUseCase (Ajustado con Filtrado por Sede)

  @Service
  @RequiredArgsConstructor
  @Transactional(readOnly = true)
  public class ListTireInventoryUseCase {

      private final TireInventoryRepository repository;
      private final SecurityContext securityContext;
      private final TireInventoryMapper mapper;

      public PageResponse<TireInventorySummaryResponse> execute(
              TireInventoryFilters filters,
              PageRequest pageRequest) {

          // 1. Obtener office_id del usuario
          UUID userOfficeId = securityContext.getCurrentUserOfficeId();
          boolean isNationalAdmin = securityContext.isNationalAdmin();

          // 2. Si NO es admin nacional, forzar filtro por su sede
          if (!isNationalAdmin) {
              filters.setOfficeId(userOfficeId);
          }
          // Si ES admin nacional y no especificó officeId, puede ver todas

          // 3. Ejecutar consulta con filtros
          Page<TireInventory> page = repository.findByFilters(filters, pageRequest);

          // 4. Mapear a DTOs
          List<TireInventorySummaryResponse> content = page.getContent()
              .stream()
              .map(mapper::toSummaryResponse)
              .toList();

          // 5. Retornar página
          return new PageResponse<>(
              content,
              page.getNumber(),
              page.getSize(),
              page.getTotalElements(),
              page.getTotalPages()
          );
      }
  }

  ---
  🌐 API REST AJUSTADA

  # =====================================================
  # ESTRUCTURA ORGANIZACIONAL (RF-001 EXTENDIDO)
  # =====================================================

  # Listar sedes (solo admin nacional)
  GET /api/v1/organization/offices
  Authorization: Bearer {token}
  Permissions: organization:offices:read
  Response: 200 OK
    {
      "content": [
        {
          "id": "uuid",
          "code": "BOG",
          "name": "Sede Bogotá",
          "city": "Bogotá",
          "isActive": true
        }
      ]
    }

  # Listar almacenes (filtrado por sede del usuario)
  GET /api/v1/organization/warehouses?officeId=uuid
  Authorization: Bearer {token}
  Response: 200 OK
    {
      "content": [
        {
          "id": "uuid",
          "code": "PRINCIPAL",
          "name": "Almacén Principal",
          "officeId": "uuid",
          "officeName": "Sede Bogotá",
          "isActive": true
        }
      ]
    }

  # Listar ubicaciones de un almacén
  GET /api/v1/organization/warehouses/{warehouseId}/locations
  Authorization: Bearer {token}
  Response: 200 OK
    {
      "content": [
        {
          "id": "uuid",
          "code": "A-1",
          "name": "Estante A-1",
          "warehouseId": "uuid",
          "isActive": true
        }
      ]
    }

  # Listar proveedores (filtrado por sede del usuario)
  GET /api/v1/tires/suppliers?officeId=uuid
  Authorization: Bearer {token}
  Response: 200 OK
    {
      "content": [
        {
          "id": "uuid",
          "code": "PROV01",
          "name": "Proveedor Principal",
          "taxId": "900123456-7",
          "contactName": "Juan Pérez",
          "email": "contacto@proveedor.com",
          "phone": "3001234567",
          "officeId": "uuid",
          "isActive": true
        }
      ]
    }

  # =====================================================
  # INVENTARIO DE LLANTAS (RF-002 AJUSTADO)
  # =====================================================

  # Crear llanta en inventario
  POST /api/v1/tires/inventory
  Authorization: Bearer {token}
  Permissions: tire:inventory:create
  Request:
    {
      "tireNumber": "LT-BOG-2024-001",
      "group": "000",
      "value": 1500000.00,
      "entryDate": "2026-01-27",
      "invoiceNumber": "FAC-001",
      "notes": "Llanta nueva Michelin",
      "specificationId": "uuid",
      "supplierId": "uuid",
      "warehouseId": "uuid",  # Debe pertenecer a la sede del usuario
      "locationId": "uuid"    # Debe pertenecer al warehouse
    }
  Response: 201 Created
    {
      "id": "uuid",
      "tireNumber": "LT-BOG-2024-001",
      "group": "000",
      "value": 1500000.00,
      "entryDate": "2026-01-27",
      "ageInDays": 0,
      "specification": { "code": "FT-000001", ... },
      "supplier": { "code": "PROV01", "name": "Proveedor Principal", ... },
      "warehouse": { "code": "PRINCIPAL", "name": "Almacén Principal", ... },
      "location": { "code": "A-1", ... },
      "office": { "code": "BOG", "name": "Sede Bogotá" },
      "createdAt": "2026-01-27T10:00:00Z"
    }
  Errores:
    - 400: Datos inválidos
    - 403: Almacén no pertenece a su sede
    - 404: Warehouse/Location/Specification no encontrados
    - 409: Llanta duplicada

  # Listar inventario (filtrado automático por sede)
  GET /api/v1/tires/inventory?page=0&size=10&warehouseId=uuid&specificationId=uuid
  Authorization: Bearer {token}
  Permissions: tire:inventory:read:own (o read:all para admin nacional)
  Response: 200 OK
    {
      "content": [
        {
          "id": "uuid",
          "tireNumber": "LT-BOG-2024-001",
          "group": "000",
          "value": 1500000.00,
          "entryDate": "2026-01-27",
          "ageInDays": 5,
          "specification": { ... },
          "warehouse": { ... },
          "location": { ... },
          "office": { "code": "BOG", "name": "Sede Bogotá" }
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "totalElements": 45,
        "totalPages": 5
      }
    }

  # Nota: Si usuario NO es admin nacional, solo ve inventario de su sede
  # Si ES admin nacional, puede especificar ?officeId=uuid para ver otra sede

  # Estadísticas por sede
  GET /api/v1/tires/inventory/statistics?officeId=uuid
  Authorization: Bearer {token}
  Permissions: tire:inventory:stats
  Response: 200 OK
    {
      "officeId": "uuid",
      "officeName": "Sede Bogotá",
      "totalQuantity": 120,
      "totalValue": 180000000.00,
      "averageAge": 45.3,
      "obsoleteTires": 5,
      "byWarehouse": [
        {
          "warehouseId": "uuid",
          "warehouseName": "Almacén Principal",
          "quantity": 80,
          "value": 120000000.00
        }
      ],
      "bySpecification": [ ... ]
    }

  # Estadísticas consolidadas (solo admin nacional)
  GET /api/v1/tires/inventory/statistics/consolidated
  Authorization: Bearer {token}
  Permissions: tire:inventory:stats, ROLE_ADMIN_NATIONAL
  Response: 200 OK
    {
      "totalQuantity": 450,
      "totalValue": 675000000.00,
      "byOffice": [
        {
          "officeId": "uuid",
          "officeName": "Sede Bogotá",
          "quantity": 120,
          "value": 180000000.00
        },
        {
          "officeId": "uuid",
          "officeName": "Sede Medellín",
          "quantity": 200,
          "value": 300000000.00
        }
      ]
    }

  ---
  📱 FRONTEND AJUSTADO

  Componentes Nuevos

  // WarehouseSelector.tsx - Selector de almacén filtrado por sede del usuario
  interface WarehouseSelectorProps {
    value: string | null;
    onChange: (warehouseId: string) => void;
    required?: boolean;
  }

  export function WarehouseSelector({ value, onChange, required }: WarehouseSelectorProps) {
    const { data: user } = useAuth();
    const { data: warehouses, isLoading } = useWarehouses(user.officeId);

    return (
      <FormControl fullWidth required={required}>
        <InputLabel>Almacén</InputLabel>
        <Select value={value || ''} onChange={(e) => onChange(e.target.value)}>
          {warehouses?.map(w => (
            <MenuItem key={w.id} value={w.id}>
              {w.name} ({w.code})
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    );
  }

  // LocationSelector.tsx - Selector de ubicaciones de un almacén específico
  interface LocationSelectorProps {
    warehouseId: string | null;
    value: string | null;
    onChange: (locationId: string) => void;
    required?: boolean;
  }

  export function LocationSelector({ warehouseId, value, onChange, required }: LocationSelectorProps) {
    const { data: locations, isLoading } = useWarehouseLocations(warehouseId);

    return (
      <FormControl fullWidth required={required} disabled={!warehouseId}>
        <InputLabel>Ubicación</InputLabel>
        <Select value={value || ''} onChange={(e) => onChange(e.target.value)}>
          {locations?.map(l => (
            <MenuItem key={l.id} value={l.id}>
              {l.name || l.code}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    );
  }

  // SupplierSelector.tsx - Selector de proveedores filtrado por sede
  interface SupplierSelectorProps {
    value: string | null;
    onChange: (supplierId: string) => void;
    required?: boolean;
  }

  export function SupplierSelector({ value, onChange, required }: SupplierSelectorProps) {
    const { data: user } = useAuth();
    const { data: suppliers, isLoading } = useSuppliers(user.officeId);

    return (
      <Autocomplete
        options={suppliers || []}
        getOptionLabel={(option) => `${option.name} (${option.code}) - ${option.taxId}`}
        value={suppliers?.find(s => s.id === value) || null}
        onChange={(_, newValue) => onChange(newValue?.id || '')}
        renderInput={(params) => (
          <TextField
            {...params}
            label="Proveedor"
            required={required}
          />
        )}
      />
    );
  }

  TireInventoryForm Ajustado

  export function TireInventoryForm({ onSuccess, onCancel }: Props) {
    const { data: user } = useAuth();
    const [selectedWarehouse, setSelectedWarehouse] = useState<string | null>(null);

    const form = useForm<TireInventoryFormData>({
      resolver: zodResolver(tireInventorySchema),
      defaultValues: {
        tireNumber: '',
        group: '000',
        value: 0,
        entryDate: new Date(),
        invoiceNumber: '',
        notes: '',
        specificationId: '',
        supplierId: '',
        warehouseId: '',
        locationId: '',
      }
    });

    return (
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <Grid container spacing={3}>
          {/* Sección: Identificación */}
          <Grid item xs={12} md={6}>
            <TextField
              label="Número de Llanta"
              {...form.register('tireNumber')}
              required
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              label="Grupo"
              {...form.register('group')}
              inputProps={{ maxLength: 3, pattern: '[0-9]{3}' }}
              required
            />
          </Grid>

          {/* Sección: Ubicación (Jerárquica) */}
          <Grid item xs={12}>
            <Typography variant="h6">Ubicación</Typography>
            <Chip
              label={`Sede: ${user.officeName}`}
              color="primary"
              size="small"
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <WarehouseSelector
              value={form.watch('warehouseId')}
              onChange={(warehouseId) => {
                form.setValue('warehouseId', warehouseId);
                setSelectedWarehouse(warehouseId);
                form.setValue('locationId', ''); // Reset location
              }}
              required
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <LocationSelector
              warehouseId={selectedWarehouse}
              value={form.watch('locationId')}
              onChange={(locationId) => form.setValue('locationId', locationId)}
              required
            />
          </Grid>

          {/* Sección: Ficha Técnica */}
          <Grid item xs={12}>
            <TireSpecificationSelector
              value={form.watch('specificationId')}
              onChange={(specId) => form.setValue('specificationId', specId)}
              required
            />
          </Grid>

          {/* Sección: Proveedor */}
          <Grid item xs={12}>
            <SupplierSelector
              value={form.watch('supplierId')}
              onChange={(suppId) => form.setValue('supplierId', suppId)}
              required
            />
          </Grid>

          {/* Resto de campos... */}
        </Grid>
      </form>
    );
  }

  ---
  ✅ PLAN DE IMPLEMENTACIÓN SECUENCIAL

  Dado que es un cambio arquitectónico significativo, propongo:

  FASE 1: Estructura Organizacional (RF-001 EXTENDIDO)

  Objetivo: Implementar la jerarquía Offices → Warehouses → Locations → Suppliers

  Duración estimada: Sin estimaciones de tiempo

  Tareas:
  1. Crear migraciones de BD (offices, warehouses, warehouse_locations, tire_suppliers)
  2. Crear entidades JPA
  3. Crear repositories
  4. Crear DTOs y mappers
  5. Crear Use Cases (CRUD completo para cada entidad)
  6. Crear Controllers REST
  7. Actualizar tabla users con office_id
  8. Crear permisos y roles multi-tenant
  9. Implementar SecurityContext con office_id
  10. Frontend: Componentes selectores jerárquicos
  11. Tests unitarios e integración

  FASE 2: Inventario Multi-Sede (RF-002 AJUSTADO)

  Objetivo: Implementar inventario con warehouse_id y filtrado por sede

  Tareas:
  1. Crear migración de tire_inventory con warehouse_id
  2. Ajustar entidad TireInventory
  3. Implementar Row-Level Security (policies)
  4. Ajustar Use Cases con filtrado por sede
  5. Ajustar Controllers con validaciones multi-tenant
  6. Frontend ajustado con selectores jerárquicos
  7. Tests multi-tenant

  FASE 3: Traslados Entre Almacenes/Sedes (RF-007 NUEVO)

  Objetivo: Flujo de traslados con aprobaciones

  Tareas:
  1. Crear tabla tire_transfers
  2. Implementar estado de traslado (PENDING, APPROVED, IN_TRANSIT, COMPLETED)
  3. Use Cases de solicitud, aprobación, recepción
  4. Workflow de aprobaciones
  5. Frontend para gestión de traslados

  ---
  🎯 DECISIÓN REQUERIDA

  ¿Confirmas que proceda con este diseño multi-sede y el plan de implementación secuencial (Fase 1 → Fase 2 → Fase 3)?

  Opciones:
  - ✅ "Sí, procede con FASE 1: Estructura Organizacional"
  - ✅ "Sí, pero ajusta [especificar qué]"
  - ❌ "Espera, necesito aclarar [especificar qué]"

  Aguardo confirmación para iniciar la implementación. 🚀

    ---
  🎯 ANÁLISIS DEL REQUERIMIENTO: RF-001-EXT - Estructura Organizacional Multi-Sede

  FASE 1: ANÁLISIS Y COMPRENSIÓN DEL REQUERIMIENTO

  A. COMPRENSIÓN DEL PROBLEMA DE NEGOCIO

  1. ¿Qué problema de negocio específico resuelve este requerimiento?

  El sistema legacy (Oracle Forms 6i) NO tiene concepto de múltiples oficinas/sedes. Toda la operación se asumía en una sola ubicación. Sin embargo,
  TRANSER opera en múltiples ciudades de Colombia (Bogotá, Medellín, Cali, etc.), y necesita:
  - Gestionar inventarios de llantas independientes por oficina
  - Cada oficina tiene sus propios almacenes y proveedores
  - Los usuarios solo deben ver datos de su oficina
  - Reportes consolidados para dirección nacional
  - Trazabilidad de qué oficina generó cada operación

  2. ¿Quiénes son los usuarios/actores involucrados?

  - Administrador Nacional: Configura oficinas, ve datos de todas las sedes, genera reportes consolidados
  - Administrador de Oficina: Gestiona almacenes y ubicaciones de su oficina específica
  - Gerente de Almacén: Gestiona inventario dentro de su almacén
  - Usuarios operativos: Ven y operan solo sobre datos de su oficina asignada

  3. ¿Cuál es el valor que aporta al negocio?

  - Aislamiento de datos: Cada oficina opera independientemente sin ver datos de otras
  - Seguridad: Previene accesos no autorizados entre oficinas
  - Escalabilidad: Permite agregar nuevas oficinas sin reestructurar el sistema
  - Reportes consolidados: Dirección puede ver performance de todas las oficinas
  - Auditoría: Rastreabilidad de operaciones por oficina
  - Gestión descentralizada: Cada oficina gestiona sus propios proveedores y almacenes

  ---
  B. ALCANCE FUNCIONAL

  4. ¿Cuáles son los casos de uso principales que debo implementar?

  Entidad: Office (Oficina)
  - UC-001-01: Crear nueva oficina
  - UC-001-02: Listar oficinas activas
  - UC-001-03: Editar información de oficina
  - UC-001-04: Desactivar oficina (soft delete)
  - UC-001-05: Consultar oficina por ID

  Entidad: Warehouse (Almacén)
  - UC-001-06: Crear almacén en una oficina
  - UC-001-07: Listar almacenes de una oficina
  - UC-001-08: Editar información de almacén
  - UC-001-09: Desactivar almacén
  - UC-001-10: Consultar almacén por ID

  Entidad: WarehouseLocation (Ubicación)
  - UC-001-11: Crear ubicación en un almacén
  - UC-001-12: Listar ubicaciones de un almacén
  - UC-001-13: Editar información de ubicación
  - UC-001-14: Desactivar ubicación
  - UC-001-15: Consultar ubicación por ID

  Entidad: TireSupplier (Proveedor)
  - UC-001-16: Crear proveedor en una oficina
  - UC-001-17: Listar proveedores de una oficina
  - UC-001-18: Editar información de proveedor
  - UC-001-19: Desactivar proveedor
  - UC-001-20: Consultar proveedor por ID

  Casos de Uso Especiales:
  - UC-001-21: Consultar jerarquía completa (Office → Warehouse → Location)
  - UC-001-22: Validar pertenencia a oficina

  5. ¿Qué operaciones CRUD son necesarias?

  Todas las entidades requieren CRUD completo:
  - Create: Con validaciones de pertenencia a oficina
  - Read: Filtrado automático por oficina del usuario (RLS)
  - Update: Solo campos editables, NO se puede cambiar office_id/warehouse_id
  - Delete: Soft delete únicamente (campo deleted_at)

  6. ¿Hay operaciones especiales o procesos de negocio complejos?

  - Validación jerárquica: Al crear ubicación, validar que warehouse pertenece a la oficina del usuario
  - Validación de códigos únicos contextuales: Código de almacén debe ser único dentro de la oficina (no globalmente)
  - Filtrado automático por RLS: Usuarios ven solo entidades de su oficina (excepto admins nacionales)
  - Cascada de desactivación: Al desactivar almacén, alertar si tiene ubicaciones activas

  ---
  C. REGLAS DE NEGOCIO

  7. Enumera TODAS las reglas de negocio explícitas del requerimiento:

  RN-001: Un usuario pertenece a UNA oficina (users.office_id obligatorio)

  RN-002: Los usuarios ven SOLO datos de su oficina (implementado via Row-Level Security)

  RN-003: Excepción: Usuarios con rol ROLE_ADMIN_NATIONAL pueden ver todas las oficinas

  RN-004: Código de oficina es único globalmente

  RN-005: Código de almacén es único dentro de la oficina (no globalmente)

  RN-006: Código de ubicación es único dentro del almacén

  RN-007: Código de proveedor es único dentro de la oficina

  RN-008: Un almacén pertenece a UNA SOLA oficina

  RN-009: Una ubicación pertenece a UN SOLO almacén

  RN-010: Un proveedor puede pertenece a UNA o muchas oficinas

  RN-011: No se permite eliminar físicamente registros (solo soft delete)

  RN-012: No se puede desactivar oficina si tiene almacenes activos con inventario

  RN-013: No se puede desactivar almacén si tiene llantas en inventario

  RN-014: No se puede desactivar ubicación si tiene llantas asignadas

  RN-015: No se puede desactivar proveedor si tiene compras asociadas

  8. ¿Hay reglas de negocio implícitas que identificas?

  RN-016: Al crear almacén, el usuario debe tener acceso a la oficina donde lo crea

  RN-017: Al crear ubicación, el almacén debe estar activo

  RN-018: Los campos de auditoría (created_by, updated_by) deben registrar el usuario autenticado

  RN-019: Los códigos deben ser alfanuméricos en mayúsculas (ej: "BOG", "PRIN", "A1")

  RN-020: Al listar entidades, por defecto mostrar solo las activas (deleted_at IS NULL)

  RN-021: Un proveedor que opera en múltiples oficinas debe registrarse por separado en cada una

  9. ¿Qué validaciones son obligatorias?

  Office:
  - code: Obligatorio, único, formato alfanumérico 2-10 caracteres mayúsculas
  - name: Obligatorio, no vacío
  - city: Obligatorio, no vacío

  Warehouse:
  - code: Obligatorio, único por oficina, formato alfanumérico 2-10 caracteres
  - name: Obligatorio, no vacío
  - office_id: Obligatorio, debe existir y estar activa

  WarehouseLocation:
  - code: Obligatorio, único por almacén, formato alfanumérico 1-10 caracteres
  - warehouse_id: Obligatorio, debe existir y estar activo

  TireSupplier:
  - code: Obligatorio, único por oficina, formato alfanumérico 2-10 caracteres
  - name: Obligatorio, no vacío
  - tax_id: Obligatorio, no vacío (NIT)
  - office_id: Obligatorio, debe existir y estar activa
  - email: Formato válido si se proporciona

  10. ¿Qué validaciones podrían ser opcionales pero recomendables?

  - Validar que el teléfono tenga formato válido (si se proporciona)
  - Validar que la ciudad exista en un catálogo de ciudades de Colombia
  - Validar longitud máxima de direcciones y notas
  - Validar que el nombre de oficina no esté duplicado (aunque el código es diferente)
  - Alertar si se intenta crear código similar a uno existente (ej: "PRIN" vs "PRINCIPAL")

  ---
  D. MODELO DE DATOS

  11. ¿Qué tablas de la base de datos están involucradas?

  Tablas Nuevas (a crear):
  - offices
  - warehouses
  - warehouse_locations
  - tire_suppliers

  Tablas Existentes (a modificar):
  - users → agregar columna office_id

  Tablas Relacionadas (en futuro):
  - tire_inventory → tendrá FK a warehouses y tire_suppliers
  - tire_active → FK a warehouses
  - vehicles → FK a offices

  12. ¿Qué relaciones entre entidades debo considerar?

  offices (1) ──< warehouses (N)
             └──< tire_suppliers (N)
             └──< users (N)

  warehouses (1) ──< warehouse_locations (N)
                └──< tire_inventory (N) [futuro]

  tire_suppliers (1) ──< tire_inventory (N) [futuro]

  Relaciones:
  - Office → Warehouses: One-to-Many
  - Office → TireSuppliers: One-to-Many
  - Office → Users: One-to-Many
  - Warehouse → WarehouseLocations: One-to-Many

  13. ¿Hay campos calculados o derivados?

  No hay campos calculados en las entidades organizacionales. Todos los campos son almacenados directamente.

  Posibles vistas calculadas (no en este RF):
  - Total de almacenes por oficina
  - Total de ubicaciones por almacén
  - Total de proveedores por oficina

  ---
  E. ESTADOS Y TRANSICIONES

  14. ¿El requerimiento involucra estados? Si es sí, enuméralos:

  Sí, mediante el campo is_active y soft delete:

  Estados de Office:
  - ACTIVE: is_active=true AND deleted_at IS NULL
  - DELETED: deleted_at IS NOT NULL

  Estados de Warehouse:
  - ACTIVE: is_active=true AND deleted_at IS NULL
  - INACTIVE: is_active=false AND deleted_at IS NULL (desactivado temporalmente)
  - DELETED: deleted_at IS NOT NULL

  Estados de WarehouseLocation:
  - ACTIVE: is_active=true AND deleted_at IS NULL
  - INACTIVE: is_active=false AND deleted_at IS NULL
  - DELETED: deleted_at IS NOT NULL

  Estados de TireSupplier:
  - ACTIVE: is_active=true AND deleted_at IS NULL
  - INACTIVE: is_active=false AND deleted_at IS NULL
  - DELETED: deleted_at IS NOT NULL

  15. ¿Qué transiciones de estado son válidas?

  ACTIVE → INACTIVE: Desactivar temporalmente (is_active = false)
  INACTIVE → ACTIVE: Reactivar (is_active = true)
  ACTIVE → DELETED: Eliminación definitiva (soft delete, deleted_at = timestamp)
  INACTIVE → DELETED: Eliminación definitiva

  16. ¿Qué transiciones están prohibidas?

  - DELETED → ACTIVE: No se puede reactivar algo eliminado (deleted_at NO se puede poner en NULL)
  - DELETED → INACTIVE: No tiene sentido
  - No se puede cambiar el office_id de un warehouse una vez creado
  - No se puede cambiar el warehouse_id de una location una vez creada

  ---
  F. INVARIANTES DEL DOMINIO

  17. ¿Qué condiciones SIEMPRE deben ser verdaderas?

  INV-001: users.office_id IS NOT NULL (todo usuario tiene oficina)

  INV-002: warehouses.office_id debe existir en offices.id

  INV-003: warehouse_locations.warehouse_id debe existir en warehouses.id

  INV-004: tire_suppliers.office_id debe existir en offices.id

  INV-005: Un warehouse.code es único dentro de su office_id

  INV-006: Un warehouse_location.code es único dentro de su warehouse_id

  INV-007: Un tire_supplier.code es único dentro de su office_id

  INV-008: offices.code es único globalmente

  INV-009: Si deleted_at IS NOT NULL, entonces is_active = false

  INV-010: Los campos de auditoría created_by, updated_by deben referenciar usuarios válidos

  18. ¿Qué es irreversible una vez hecho?

  - Soft delete: Una vez eliminado (deleted_at seteado), no se puede revertir (RN de negocio)
  - Creación de ID (UUID): Una vez asignado, no cambia
  - Asignación de office_id: No se puede cambiar la oficina de un warehouse después de creado
  - Asignación de warehouse_id: No se puede cambiar el almacén de una location después de creada

  19. ¿Qué restricciones temporales existen (fechas, orden de eventos)?

  - created_at siempre es anterior a updated_at (si existe)
  - deleted_at siempre es posterior a created_at
  - No se puede crear warehouse en una oficina que no existe aún
  - No se puede crear location en un almacén que no existe aún
  - Al migrar datos legacy, todas las entidades organizacionales deben crearse ANTES de migrar inventario

  ---
  G. EVENTOS Y EFECTOS SECUNDARIOS

  20. ¿Esta operación genera eventos de dominio?

  Sí, eventos a considerar (para auditoría futura):

  - OfficeCreated
  - OfficeDeactivated
  - WarehouseCreated
  - WarehouseDeactivated
  - WarehouseLocationCreated
  - WarehouseLocationDeactivated
  - TireSupplierCreated
  - TireSupplierDeactivated

  21. ¿Hay efectos en cascada sobre otras entidades?

  - Desactivar Office: Debería desactivar (o alertar sobre) warehouses, suppliers y usuarios asociados
  - Desactivar Warehouse: Debería alertar si tiene locations activas o inventario
  - Desactivar Location: Debería alertar si tiene llantas asignadas
  - Desactivar Supplier: Debería alertar si tiene compras pendientes o inventario asociado

  22. ¿Qué se debe registrar en auditoría?

  - Todas las operaciones CUD (Create, Update, Delete) con:
    - created_at, created_by
    - updated_at, updated_by
    - deleted_at, deleted_by
  - No se requiere tabla de auditoría separada en esta fase (los campos en cada tabla son suficientes)

  ---
  H. INTERFAZ DE USUARIO

  23. ¿Qué tipo de formulario se necesita?

  Office: Formulario simple CRUD
  - Campos: code, name, city, address, phone
  - Validación en tiempo real de código único

  Warehouse: Formulario simple CRUD
  - Selector de oficina (solo para admin nacional, auto-detectado para usuarios normales)
  - Campos: code, name, description

  WarehouseLocation: Formulario simple CRUD
  - Selector de almacén (filtrado por oficina del usuario)
  - Campos: code, name, description

  TireSupplier: Formulario completo
  - Campos: code, name, tax_id, contact_name, email, phone, address
  - Validación de email y formato de tax_id

  24. ¿Qué campos son obligatorios vs opcionales?

  Office:
  - Obligatorios: code, name, city
  - Opcionales: address, phone

  Warehouse:
  - Obligatorios: code, name, office_id
  - Opcionales: description

  WarehouseLocation:
  - Obligatorios: code, warehouse_id
  - Opcionales: name, description

  TireSupplier:
  - Obligatorios: code, name, tax_id, office_id
  - Opcionales: contact_name, email, phone, address

  25. ¿Hay dependencias entre campos?

  - Warehouse → Office: Al seleccionar office, se habilita la creación de warehouse
  - Location → Warehouse: Al seleccionar warehouse, se habilita la creación de location
  - Supplier → Office: El office se auto-detecta del usuario (excepto admin nacional)
  - Email: Si se proporciona email, debe validarse formato

  ---
  I. RIESGOS E INCERTIDUMBRES

  26. ¿Hay información ambigua o faltante en el requerimiento?

  - ✅ Aclarado: ¿Puede un proveedor operar en múltiples oficinas? → SÍ, pero debe registrarse por separado en cada una
  - ✅ Aclarado: ¿Los códigos pueden repetirse entre oficinas? → SÍ (excepto office.code que es global)
  - ⚠️ Pendiente: ¿Qué hacer con usuarios existentes al agregar office_id? → Asignar a oficina principal por defecto
  - ⚠️ Pendiente: ¿Se permite cambiar el office_id de un usuario? → Sí, pero con restricciones (no tener operaciones pendientes)

  27. ¿Qué supuestos debo hacer?

  - Supuesto 1: La oficina principal se crea con ID fijo: a0000000-0000-0000-0000-000000000001
  - Supuesto 2: El almacén principal se crea con ID fijo: b0000000-0000-0000-0000-000000000001
  - Supuesto 3: Todos los usuarios legacy se asignan a oficina principal inicialmente
  - Supuesto 4: Los códigos en mayúsculas son obligatorios (normalización automática)
  - Supuesto 5: El soft delete es permanente (no hay "restore")

  28. ¿Qué requiere confirmación explícita del Product Owner?

  - ✅ Confirmar que el soft delete NO permite restauración
  - ✅ Confirmar formato de códigos (alfanumérico, mayúsculas, longitud)
  - ⚠️ Confirmar si se permite cambiar office_id de usuarios existentes
  - ⚠️ Confirmar validaciones de desactivación (cascada vs alerta)

  ---
  J. INTEGRACIÓN CON MÓDULOS EXISTENTES

  29. ¿Este requerimiento depende de Auth o Users?

  SÍ, dependencias críticas:

  - Auth: Requiere que el JWT incluya office_id del usuario
  - Users: Requiere agregar columna office_id a tabla users
  - SecurityContext: Requiere implementar SecurityUtils.getCurrentUserOfficeId()
  - Roles: Requiere verificar rol ROLE_ADMIN_NATIONAL para permisos especiales

  30. ¿Necesita permisos especiales?

  SÍ, matriz de permisos:
  ┌────────────────────────┬────────────────┬───────────────┬───────────────────┐
  │       Operación        │ Admin Nacional │ Admin Oficina │  Usuario Normal   │
  ├────────────────────────┼────────────────┼───────────────┼───────────────────┤
  │ Crear oficina          │ ✅             │ ❌            │ ❌                │
  ├────────────────────────┼────────────────┼───────────────┼───────────────────┤
  │ Ver todas las oficinas │ ✅             │ ❌            │ ❌                │
  ├────────────────────────┼────────────────┼───────────────┼───────────────────┤
  │ Ver su oficina         │ ✅             │ ✅            │ ✅                │
  ├────────────────────────┼────────────────┼───────────────┼───────────────────┤
  │ Crear almacén          │ ✅             │ ✅            │ ❌                │
  ├────────────────────────┼────────────────┼───────────────┼───────────────────┤
  │ Crear ubicación        │ ✅             │ ✅            │ ✅ (con permisos) │
  ├────────────────────────┼────────────────┼───────────────┼───────────────────┤
  │ Crear proveedor        │ ✅             │ ✅            │ ❌                │
  └────────────────────────┴────────────────┴───────────────┴───────────────────┘
  ---
  K. MULTI-SEDE (ARQUITECTURA MULTI-OFICINA)

  31. ¿Los datos de este requerimiento son globales o específicos por oficina?

  AMBOS:
  - Globales: offices (todos los admins nacionales los ven)
  - Específicos por oficina:
    - warehouses (cada oficina tiene los suyos)
    - warehouse_locations (pertenecen a almacenes de una oficina)
    - tire_suppliers (cada oficina tiene sus proveedores)

  32. Si son específicos por oficina, ¿cómo se determina a qué oficina pertenece un registro?

  - Warehouses: Columna office_id directa
  - WarehouseLocations: Via warehouse_id → warehouses.office_id
  - TireSuppliers: Columna office_id directa

  33. ¿Se necesita filtrado automático por oficina del usuario (Row-Level Security)?

  SÍ, RLS obligatorio en:
  - warehouses (filtrado por office_id)
  - warehouse_locations (filtrado via warehouse_id)
  - tire_suppliers (filtrado por office_id)

  NO requiere RLS:
  - offices (se filtra manualmente según rol)

  34. ¿Hay traslados o movimientos entre oficinas? Si es sí, ¿qué restricciones existen?

  NO en este RF.

  Los traslados entre oficinas se manejarán en RF-007 (futuro) con:
  - Proceso de aprobación
  - Workflow específico
  - No se permite mover directamente entidades entre oficinas

  35. ¿Los usuarios con ROLE_ADMIN_NATIONAL pueden ver datos de todas las oficinas?

  SÍ, específicamente:
  - Pueden ver todas las oficinas en listados
  - Pueden crear oficinas
  - Pueden asignar usuarios a cualquier oficina
  - Pueden generar reportes consolidados
  - En RLS, la política retorna TRUE si tiene rol ROLE_ADMIN_NATIONAL

  ---
  ✅ FIN DEL ANÁLISIS DE 35 PREGUNTAS

  ---
  Ahora procederé con la FASE 2: DISEÑO DE SOLUCIÓN...

  FASE 2: DISEÑO DE SOLUCIÓN

  📐 1. AGGREGATE ROOT Y ENTIDADES

  Agregados identificados:

  Agregado 1: Office (Aggregate Root)

  - Entidades: Office
  - Value Objects: OfficeCode, CityName
  - Límite del agregado: La oficina por sí sola (no incluye warehouses como parte del agregado)

  Agregado 2: Warehouse (Aggregate Root)

  - Entidades: Warehouse
  - Value Objects: WarehouseCode, WarehouseName
  - Límite del agregado: El almacén por sí solo (las locations son un agregado separado)

  Agregado 3: WarehouseLocation (Aggregate Root)

  - Entidades: WarehouseLocation
  - Value Objects: LocationCode
  - Límite del agregado: La ubicación individual

  Agregado 4: TireSupplier (Aggregate Root)

  - Entidades: TireSupplier
  - Value Objects: SupplierCode, TaxId, Email
  - Límite del agregado: El proveedor individual

  Justificación: Cada entidad es un Aggregate Root separado porque:
  - Tienen ciclo de vida independiente
  - Se pueden modificar sin afectar a las demás
  - Las relaciones son por referencia (FK), no por composición

  ---
  📋 2. CASOS DE USO (Application Layer)

  UC-001-01: Crear Nueva Oficina

  Flujo Principal:
  1. Validar que el usuario tiene rol ROLE_ADMIN_NATIONAL
  2. Validar formato del código (alfanumérico, 2-10 caracteres, mayúsculas)
  3. Validar que el código no existe (unicidad global)
  4. Validar que name y city no estén vacíos
  5. Crear entidad Office con auditoría (created_by, created_at)
  6. Persistir en base de datos
  7. Retornar OfficeDTO

  Precondiciones:
  - Usuario autenticado con rol ROLE_ADMIN_NATIONAL

  Postcondiciones:
  - Office creada con is_active=true, deleted_at=NULL
  - Registro de auditoría completo

  Invariantes Validadas:
  - INV-008: Código único globalmente

  ---
  UC-001-06: Crear Almacén en Oficina

  Flujo Principal:
  1. Validar que el usuario tiene rol ROLE_ADMIN_NATIONAL o ROLE_ADMIN_OFFICE
  2. Obtener office_id del usuario autenticado
  3. Si es admin nacional, permitir seleccionar cualquier office_id; si no, usar su office_id
  4. Validar que la oficina existe y está activa
  5. Validar formato del código (alfanumérico, 2-10 caracteres)
  6. Validar que el código no existe en esa oficina (unicidad contextual)
  7. Validar que name no esté vacío
  8. Crear entidad Warehouse con auditoría
  9. Persistir en base de datos
  10. Retornar WarehouseDTO

  Precondiciones:
  - Usuario autenticado con rol ROLE_ADMIN_NATIONAL o ROLE_ADMIN_OFFICE
  - La oficina debe existir y estar activa

  Postcondiciones:
  - Warehouse creada con is_active=true, deleted_at=NULL
  - Warehouse asociada a la oficina especificada

  Invariantes Validadas:
  - INV-002: warehouses.office_id existe en offices.id
  - INV-005: Código único dentro de la oficina

  ---
  UC-001-11: Crear Ubicación en Almacén

  Flujo Principal:
  1. Validar que el usuario tiene permisos (cualquier rol autenticado puede crear)
  2. Obtener office_id del usuario
  3. Validar que el warehouse existe
  4. Validar que el warehouse pertenece a la oficina del usuario (o es admin nacional)
  5. Validar formato del código
  6. Validar que el código no existe en ese almacén (unicidad contextual)
  7. Crear entidad WarehouseLocation con auditoría
  8. Persistir en base de datos
  9. Retornar WarehouseLocationDTO

  Precondiciones:
  - Usuario autenticado
  - El warehouse debe existir y estar activo
  - El warehouse debe pertenecer a la oficina del usuario

  Postcondiciones:
  - WarehouseLocation creada con is_active=true
  - Location asociada al warehouse especificado

  Invariantes Validadas:
  - INV-003: warehouse_locations.warehouse_id existe en warehouses.id
  - INV-006: Código único dentro del almacén

  ---
  UC-001-16: Crear Proveedor en Oficina

  Flujo Principal:
  1. Validar que el usuario tiene rol ROLE_ADMIN_NATIONAL o ROLE_ADMIN_OFFICE
  2. Obtener office_id del usuario
  3. Si es admin nacional, permitir seleccionar cualquier office_id; si no, usar su office_id
  4. Validar que la oficina existe y está activa
  5. Validar formato del código
  6. Validar que el código no existe en esa oficina (unicidad contextual)
  7. Validar que name y tax_id no estén vacíos
  8. Si email proporcionado, validar formato
  9. Crear entidad TireSupplier con auditoría
  10. Persistir en base de datos
  11. Retornar TireSupplierDTO

  Precondiciones:
  - Usuario autenticado con rol ROLE_ADMIN_NATIONAL o ROLE_ADMIN_OFFICE
  - La oficina debe existir y estar activa

  Postcondiciones:
  - TireSupplier creado con is_active=true
  - Supplier asociado a la oficina especificada

  Invariantes Validadas:
  - INV-004: tire_suppliers.office_id existe en offices.id
  - INV-007: Código único dentro de la oficina

  ---
  🌐 3. API REST

  BASE URL: /api/v1/organization

  === OFFICES ===
  POST   /api/v1/organization/offices
  GET    /api/v1/organization/offices
  GET    /api/v1/organization/offices/{id}
  PUT    /api/v1/organization/offices/{id}
  DELETE /api/v1/organization/offices/{id}  (soft delete)

  === WAREHOUSES ===
  POST   /api/v1/organization/offices/{officeId}/warehouses
  GET    /api/v1/organization/offices/{officeId}/warehouses
  GET    /api/v1/organization/warehouses/{id}
  PUT    /api/v1/organization/warehouses/{id}
  DELETE /api/v1/organization/warehouses/{id}

  === WAREHOUSE LOCATIONS ===
  POST   /api/v1/organization/warehouses/{warehouseId}/locations
  GET    /api/v1/organization/warehouses/{warehouseId}/locations
  GET    /api/v1/organization/warehouse-locations/{id}
  PUT    /api/v1/organization/warehouse-locations/{id}
  DELETE /api/v1/organization/warehouse-locations/{id}

  === TIRE SUPPLIERS ===
  POST   /api/v1/organization/offices/{officeId}/suppliers
  GET    /api/v1/organization/offices/{officeId}/suppliers
  GET    /api/v1/organization/tire-suppliers/{id}
  PUT    /api/v1/organization/tire-suppliers/{id}
  DELETE /api/v1/organization/tire-suppliers/{id}

  === HIERARCHY ===
  GET    /api/v1/organization/hierarchy  (jerarquía completa filtrada por RLS)

  Ejemplo Detallado: POST /api/v1/organization/offices

  Request:
  {
    "code": "BOG",
    "name": "Bogotá - Sede Principal",
    "city": "Bogotá",
    "address": "Calle 123 #45-67",
    "phone": "+57 1 234 5678"
  }

  Response 201 Created:
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "code": "BOG",
    "name": "Bogotá - Sede Principal",
    "city": "Bogotá",
    "address": "Calle 123 #45-67",
    "phone": "+57 1 234 5678",
    "isActive": true,
    "createdAt": "2026-01-27T15:30:00Z",
    "createdBy": {
      "id": 1,
      "username": "admin"
    }
  }

  Errores:
  - 400 Bad Request: Validación fallida (formato incorrecto, campos vacíos)
  - 401 Unauthorized: Usuario no autenticado
  - 403 Forbidden: Usuario no tiene rol ROLE_ADMIN_NATIONAL
  - 409 Conflict: Código ya existe

  ---
  🗺️ 4. MODELO DE DOMINIO (Diagrama)

  classDiagram
      class Office {
          +UUID id
          +String code
          +String name
          +String city
          +String address
          +String phone
          +Boolean isActive
          +LocalDateTime createdAt
          +Long createdBy
          +LocalDateTime updatedAt
          +Long updatedBy
          +LocalDateTime deletedAt
          +Long deletedBy
          +markAsDeleted(userId)
          +update(name, city, address, phone, userId)
      }

      class Warehouse {
          +UUID id
          +String code
          +String name
          +UUID officeId
          +String description
          +Boolean isActive
          +LocalDateTime createdAt
          +Long createdBy
          +markAsDeleted(userId)
          +belongsToOffice(officeId) boolean
      }

      class WarehouseLocation {
          +UUID id
          +String code
          +String name
          +UUID warehouseId
          +String description
          +Boolean isActive
          +LocalDateTime createdAt
          +Long createdBy
          +markAsDeleted(userId)
      }

      class TireSupplier {
          +UUID id
          +String code
          +String name
          +String taxId
          +UUID officeId
          +String contactName
          +String email
          +String phone
          +String address
          +Boolean isActive
          +LocalDateTime createdAt
          +Long createdBy
          +markAsDeleted(userId)
          +belongsToOffice(officeId) boolean
      }

      class User {
          +Long id
          +String username
          +UUID officeId
      }

      Office "1" --> "N" Warehouse : contains
      Office "1" --> "N" TireSupplier : has
      Office "1" --> "N" User : assignedTo
      Warehouse "1" --> "N" WarehouseLocation : contains

  Estados y Transiciones:

  stateDiagram-v2
      [*] --> Active : create()
      Active --> Inactive : deactivate()
      Inactive --> Active : activate()
      Active --> Deleted : markAsDeleted()
      Inactive --> Deleted : markAsDeleted()
      Deleted --> [*]

  Invariantes Principales:
  - Un Warehouse siempre tiene un Office válido
  - Un WarehouseLocation siempre tiene un Warehouse válido
  - Un TireSupplier siempre tiene un Office válido
  - Códigos únicos dentro de su contexto
  - Soft delete es irreversible

  ---
  🛑 FASE 3: CONFIRMACIÓN EXPLÍCITA

  📊 Resumen del Diseño

  Entidades a implementar:
  - 4 Aggregate Roots: Office, Warehouse, WarehouseLocation, TireSupplier
  - Modificación a entidad existente: User (agregar office_id)

  Casos de Uso:
  - 20 Use Cases CRUD completos (5 por entidad x 4 entidades)
  - 2 Use Cases especiales (jerarquía, validación)

  API REST:
  - 20 endpoints REST (CRUD completo para cada entidad)
  - Respuesta consistente con DTOs
  - Manejo de errores estándar (400, 401, 403, 404, 409, 500)

  Row-Level Security:
  - RLS en warehouses, warehouse_locations, tire_suppliers
  - Funciones RLS: get_user_office_id(), current_user_has_role()
  - Políticas de aislamiento por oficina

  Testing:
  - Tests unitarios de validaciones
  - Tests de integración con RLS
  - Tests de seguridad (aislamiento entre oficinas)

  ⚠️ Supuestos y Áreas de Riesgo

  Supuestos:
  1. Todos los usuarios legacy se migran a oficina principal
  2. El soft delete NO permite restauración
  3. Los códigos se normalizan a mayúsculas automáticamente
  4. No se permite cambiar office_id/warehouse_id después de creado

  Áreas de Riesgo:
  1. Performance de RLS en tablas grandes (mitigar con índices)
  2. Migración de usuarios existentes (requiere downtime)
  3. Validación de cascada al desactivar (puede ser complejo)

  ---
  ❓ PREGUNTA OBLIGATORIA

  ¿Confirmas que el análisis y diseño presentado es correcto y puedo proceder con la implementación?

  Espero tu confirmación explícita para continuar con la implementación del código (migraciones, entidades, repositorios, servicios, controllers,
  frontend).

Confirmamos que el analisis y el diseño presentado es correcto. puedes proceder con la implementacion.