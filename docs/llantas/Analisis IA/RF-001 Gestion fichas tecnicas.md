
● 📋 FASE 1: ANÁLISIS DEL RF-001 - GESTIÓN DE FICHAS TÉCNICAS

  ---
  A. COMPRENSIÓN DEL PROBLEMA DE NEGOCIO

  1. ¿Qué problema de negocio específico resuelve este requerimiento?

  Respuesta:
  El RF-001 resuelve la necesidad de estandarizar y centralizar las especificaciones técnicas y comerciales de los
  diferentes tipos de llantas que utiliza la flota de vehículos. Sin un catálogo maestro de fichas técnicas, sería
  imposible:
  - Conocer el rendimiento esperado de cada tipo de llanta
  - Comparar diferentes marcas/modelos para toma de decisiones de compra
  - Proyectar vida útil y costos operativos
  - Establecer parámetros de control (profundidades iniciales, kilómetros esperados)
  - Rastrear información comercial (proveedores, precios, última compra)

  El problema que resuelve es la falta de normalización en la información de las llantas, lo cual dificulta la gestión,
  análisis y control del activo más costoso después de los vehículos.

  ---
  2. ¿Quiénes son los usuarios/actores involucrados?

  Respuesta:
  - Jefe de Taller/Mantenimiento: Usuario principal que configura y mantiene las fichas técnicas
  - Administrador de Flota: Consulta fichas para análisis de costos y decisiones de compra
  - Almacenista: Consulta fichas al recibir llantas nuevas en inventario
  - Analista de Compras: Utiliza información de proveedores y precios para negociaciones
  - Administrador del Sistema: Gestión inicial de catálogos maestros

  ---
  3. ¿Cuál es el valor que aporta al negocio?

  Respuesta:
  - Estandarización: Base de datos única de especificaciones técnicas
  - Trazabilidad: Toda llanta en el sistema está vinculada a una ficha técnica
  - Análisis: Permite comparar rendimiento real vs esperado por marca/tipo
  - Optimización de Compras: Información consolidada de proveedores y precios
  - Proyección Financiera: Cálculos de costos esperados y presupuestos
  - Toma de Decisiones: Datos objetivos para seleccionar mejores llantas según rendimiento histórico

  ---
  B. ALCANCE FUNCIONAL

  4. ¿Cuáles son los casos de uso principales que debo implementar?

  Respuesta:
  1. UC-001: Crear Ficha Técnica - Registrar nueva ficha con todas sus especificaciones
  2. UC-002: Listar Fichas Técnicas - Visualizar catálogo completo con filtros y búsqueda
  3. UC-003: Consultar Ficha Técnica - Ver detalle completo de una ficha específica
  4. UC-004: Actualizar Ficha Técnica - Modificar datos de ficha existente
  5. UC-005: Eliminar Ficha Técnica - Dar de baja ficha (con validación de uso)
  6. UC-006: Buscar Fichas Técnicas - Búsqueda por marca, tipo, referencia, dimensión

  ---
  5. ¿Qué operaciones CRUD son necesarias? (Crear, Leer, Actualizar, Eliminar)

  Respuesta:
  - ✅ CREATE: Crear nueva ficha técnica
  - ✅ READ: Listar todas las fichas, consultar una ficha específica, buscar/filtrar
  - ✅ UPDATE: Actualizar cualquier campo de la ficha (excepto código si tiene llantas asociadas)
  - ✅ DELETE: Eliminación lógica (soft delete) con validación de integridad referencial

  Nota: La eliminación física solo se permite si NO existen llantas asociadas a la ficha (verificar en INVENTARIO,
  LLANTAS, INTERMEDIO, RETIRADAS, HISTORIA).

  ---
  6. ¿Hay operaciones especiales o procesos de negocio complejos?

  Respuesta:
  NO hay procesos de negocio complejos en este requerimiento. Es un CRUD maestro simple con las siguientes
  particularidades:

  1. Validación de Integridad Referencial al Eliminar: Verificar que no existan llantas asociadas
  2. Cálculo de Campos Derivados (si aplica):
    - Total esperado podría calcularse automáticamente
    - Costo por hora podría derivarse de otros campos
  3. Auditoría Completa: Registrar quién crea/modifica cada ficha y cuándo
  4. Catálogos Dependientes: Validar que Marca, Tipo, Referencia, Proveedores existan antes de guardar

  No requiere: Máquinas de estado, transacciones complejas, eventos de dominio, procesamiento asíncrono.

  ---
  C. REGLAS DE NEGOCIO

  7. Enumera TODAS las reglas de negocio explícitas del requerimiento

  Respuesta:

  RN-001: Código único - No pueden existir dos fichas técnicas con el mismo código
  RN-002: Marca debe existir en catálogo MARCAS_LLANTAS
  RN-003: Tipo debe existir en catálogo TIPOS
  RN-004: Referencia debe existir en catálogo REFERENCIA
  RN-005: Kilómetros esperados > 0
  RN-006: Profundidades iniciales deben estar entre 0 y 99.9 mm (PI, PC, PD)
  RN-007: Proveedores (principal, secundario, último) deben existir en PROVEEDORES_LLANTAS
  RN-008: No se puede eliminar una ficha técnica si tiene llantas asociadas

  ---
  8. ¿Hay reglas de negocio implícitas que identificas?

  Respuesta:

  RN-009 (Implícita): Los campos de profundidad inicial (PI, PC, PD) deben tener valores lógicos coherentes (ninguno
  puede ser negativo)
  RN-010 (Implícita): El peso debe ser positivo (si se registra)
  RN-011 (Implícita): La fecha de última compra no puede ser futura
  RN-012 (Implícita): El precio de última compra debe ser >= 0
  RN-013 (Implícita): La cantidad de última compra debe ser > 0
  RN-014 (Implícita): El número de reencauches esperados debe ser >= 0
  RN-015 (Implícita): La pérdida esperada (%) debe estar entre 0 y 100
  RN-016 (Implícita): Los rangos de kilometraje (mayor, menor, medio) deben cumplir: menor <= medio <= mayor
  RN-017 (Implícita): Al actualizar una ficha en uso, no se debe afectar el histórico de llantas ya asociadas

  ---
  9. ¿Qué validaciones son obligatorias?

  Respuesta:

  Validaciones de Negocio (Obligatorias - 422 Unprocessable Entity):
  1. Código único en el sistema
  2. Marca existe en catálogo
  3. Tipo existe en catálogo
  4. Referencia existe en catálogo
  5. Kilómetros esperados > 0
  6. Profundidades iniciales: 0 <= valor <= 99.9
  7. Proveedores existen en catálogo (si se especifican)

  Validaciones de Entrada (Obligatorias - 400 Bad Request):
  1. Código no puede ser nulo ni vacío
  2. Marca no puede ser nula
  3. Tipo no puede ser nulo
  4. Formato de números válido
  5. Longitud de campos según esquema DB

  ---
  10. ¿Qué validaciones podrían ser opcionales pero recomendables?

  Respuesta:

  Validaciones Recomendables:
  1. Alerta de duplicados similares: Si ya existe una ficha con misma marca+tipo+dimensión, advertir al usuario
  2. Validación de coherencia de rangos: Verificar que menor <= medio <= mayor en kilometraje
  3. Validación de profundidades coherentes: Advertir si PI, PC, PD tienen valores muy dispares
  4. Validación de precio: Si el precio de última compra difiere mucho del histórico, solicitar confirmación
  5. Campos obligatorios contextuales: Si se ingresa "última compra", todos sus campos (cantidad, precio, fecha)
  deberían ser obligatorios
  6. Validación de dimensión: Formato estándar de dimensión de llantas (ej: 295/80R22.5)

  ---
  D. MODELO DE DATOS

  11. ¿Qué tablas de la base de datos están involucradas?

  Respuesta:

  Tabla Principal:
  - tire_specifications (equivalente moderno de FICHATEC)

  Tablas Relacionadas (Catálogos - esquema shared):
  - tire_brands (MARCAS_LLANTAS)
  - tire_types (TIPOS)
  - tire_references (REFERENCIA)
  - tire_suppliers (PROVEEDORES_LLANTAS)

  Tablas que Referencian a tire_specifications:
  - tire_inventory (INVENTARIO)
  - active_tires (LLANTAS)
  - intermediate_tires (INTERMEDIO)
  - retired_tires (RETIRADAS)
  - tire_history (HISTORIA)

  ---
  12. ¿Qué relaciones entre entidades debo considerar?

  Respuesta:

  tire_specifications (1) ----< (*) tire_inventory
  tire_specifications (1) ----< (*) active_tires
  tire_specifications (1) ----< (*) intermediate_tires
  tire_specifications (1) ----< (*) retired_tires
  tire_specifications (1) ----< (*) tire_history

  tire_brands (1) ----< (*) tire_specifications
  tire_types (1) ----< (*) tire_specifications
  tire_references (1) ----< (*) tire_specifications
  tire_suppliers (1) ----< (*) tire_specifications (main_supplier_id)
  tire_suppliers (1) ----< (*) tire_specifications (secondary_supplier_id)
  tire_suppliers (1) ----< (*) tire_specifications (last_supplier_id)

  users (1) ----< (*) tire_specifications (created_by)
  users (1) ----< (*) tire_specifications (updated_by)
  users (1) ----< (*) tire_specifications (deleted_by)

  ---
  13. ¿Hay campos calculados o derivados?

  Respuesta:

  Campos Potencialmente Calculados:

  1. average_initial_depth (Profundidad promedio inicial):
  (initial_depth_inner + initial_depth_center + initial_depth_outer) / 3
  2. total_expected (Total esperado):
    - Podría derivarse de otros campos, pero en el legacy parece ser un campo ingresado manualmente
  3. cost_per_hour (Costo por hora):
    - Podría calcularse, pero en legacy es campo manual

  Decisión: En la implementación moderna, recomiendo:
  - Almacenar initial_depth_inner, initial_depth_center, initial_depth_outer como campos
  - Calcular average_initial_depth como campo virtual/computed en el DTO
  - Mantener total_expected y cost_per_hour como campos editables (no calculados automáticamente)

  ---
  E. ESTADOS Y TRANSICIONES

  14. ¿El requerimiento involucra estados? Si es sí, enuméralos

  Respuesta:

  SÍ, pero de forma simple. La ficha técnica tiene estados básicos de registro:

  1. ACTIVE (Activa) - Ficha técnica disponible para uso normal
  2. INACTIVE (Inactiva) - Ficha descontinuada, no se debe usar para nuevas llantas
  3. DELETED (Eliminada) - Soft delete (deleted_at IS NOT NULL)

  Nota: Este NO es un flujo de estados complejo. Es simplemente un flag de activación/desactivación para gestión de
  catálogos.

  ---
  15. ¿Qué transiciones de estado son válidas?

  Respuesta:

  ACTIVE → INACTIVE (Descontinuar ficha)
  INACTIVE → ACTIVE (Reactivar ficha)
  ACTIVE → DELETED (Eliminar con validación)
  INACTIVE → DELETED (Eliminar con validación)

  NO permitido:
  - DELETED → (cualquier estado) - La eliminación lógica es irreversible
  - Transiciones directas sin validar integridad referencial

  ---
  16. ¿Qué transiciones están prohibidas?

  Respuesta:

  1. ACTIVE → DELETED si existen llantas asociadas (en cualquier tabla)
  2. INACTIVE → DELETED si existen llantas asociadas (en cualquier tabla)
  3. DELETED → ACTIVE - No se puede reactivar una ficha eliminada
  4. DELETED → INACTIVE - No se puede modificar una ficha eliminada

  Validación crítica: Antes de cambiar a DELETED, verificar que NO existan registros en:
  - tire_inventory
  - active_tires
  - intermediate_tires
  - retired_tires
  - tire_history

  ---
  F. INVARIANTES DEL DOMINIO

  17. ¿Qué condiciones SIEMPRE deben ser verdaderas?

  Respuesta:

  INV-001: Un código de ficha técnica es único en el sistema (solo una ficha activa con ese código)
  INV-002: Toda ficha debe tener marca, tipo y referencia válidos
  INV-003: Los kilómetros esperados siempre son mayores a cero
  INV-004: Las profundidades iniciales están en el rango [0, 99.9] mm
  INV-005: Si una ficha tiene estado DELETED, deleted_at debe ser NOT NULL
  INV-006: Si una ficha tiene estado ACTIVE o INACTIVE, deleted_at debe ser NULL
  INV-007: created_at <= updated_at (si fue actualizada)
  INV-008: Si un proveedor está referenciado, debe existir en tire_suppliers
  INV-009: Una ficha eliminada (soft delete) NO puede tener nuevas llantas asociadas

  ---
  18. ¿Qué es irreversible una vez hecho?

  Respuesta:

  1. Eliminación lógica (soft delete): Una vez marcada como deleted_at NOT NULL, no se puede revertir
  2. Auditoría de creación: created_at y created_by nunca cambian
  3. Asociación histórica: Si ya hay llantas en HISTORIA con esta ficha, el vínculo es permanente
  4. Cambio de código: Si la ficha ya tiene llantas asociadas, el código NO debe cambiar (sería perder trazabilidad)

  ---
  19. ¿Qué restricciones temporales existen (fechas, orden de eventos)?

  Respuesta:

  1. created_at siempre debe ser <= updated_at
  2. last_purchase_date (fecha última compra) no puede ser futura (> HOY)
  3. deleted_at (si existe) debe ser >= created_at
  4. deleted_at (si existe) debe ser >= updated_at

  ---
  G. EVENTOS Y EFECTOS SECUNDARIOS

  20. ¿Esta operación genera eventos de dominio?

  Respuesta:

  NO es necesario generar eventos de dominio complejos para este CRUD maestro. Sin embargo, para auditoría y
  trazabilidad:

  Eventos de Auditoría (Tabla AUDITA / audit_log):
  - TireSpecificationCreated - Al crear ficha
  - TireSpecificationUpdated - Al modificar ficha
  - TireSpecificationDeleted - Al eliminar (soft delete)
  - TireSpecificationReactivated - Al cambiar de INACTIVE a ACTIVE

  NO requiere: Domain Events para notificar a otros agregados (esto es un catálogo maestro simple).

  ---
  21. ¿Hay efectos en cascada sobre otras entidades?

  Respuesta:

  NO hay efectos en cascada automáticos al crear/modificar una ficha técnica.

  IMPORTANTE: Al eliminar (soft delete) una ficha, el sistema debe:
  1. Verificar que NO existan llantas asociadas (bloquea la eliminación si hay asociaciones)
  2. NO eliminar en cascada las llantas asociadas (esto sería destruir datos históricos)

  Efecto de lectura: Al actualizar especificaciones de una ficha en uso, los reportes de análisis podrían cambiar (pero
  esto no es un efecto de escritura).

  ---
  22. ¿Qué se debe registrar en auditoría?

  Respuesta:

  Auditoría Completa (Campos estándar):
  - created_by: Usuario que creó la ficha
  - created_at: Fecha/hora de creación
  - updated_by: Usuario que modificó la ficha (última modificación)
  - updated_at: Fecha/hora de última modificación
  - deleted_by: Usuario que eliminó la ficha (soft delete)
  - deleted_at: Fecha/hora de eliminación lógica

  Auditoría Extendida (Tabla audit_log):
  - Tipo de operación (CREATE, UPDATE, DELETE)
  - Usuario que realizó la operación
  - Timestamp de la operación
  - IP del usuario
  - Valores anteriores (para UPDATE)
  - Valores nuevos (para UPDATE)

  ---
  H. INTERFAZ DE USUARIO

  23. ¿Qué tipo de formulario se necesita? (Simple CRUD, Wizard, Master-Detail, etc.)

  Respuesta:

  Tipo: CRUD Simple con formulario modal o página dedicada

  Componentes UI necesarios:

  1. Listado (DataGrid/Table):
    - Tabla con paginación, filtros y búsqueda
    - Columnas: Código, Marca, Tipo, Referencia, Dimensión, Kilómetros esperados, Estado
    - Acciones: Ver, Editar, Eliminar (con confirmación)
  2. Formulario de Creación/Edición:
    - Formulario con múltiples secciones agrupadas:
        - Identificación: Código, Marca, Tipo, Referencia
      - Especificaciones: Dimensiones, Peso, Profundidades iniciales
      - Rendimiento: Kilómetros esperados, Rangos, Reencauches esperados
      - Comercial: Proveedores, Última compra (cantidad, precio, fecha)
  3. Modal de Detalle (Solo lectura):
    - Vista completa de la ficha técnica
    - Información de auditoría (creado por, modificado por, etc.)

  ---
  24. ¿Qué campos son obligatorios vs opcionales?

  Respuesta:

  OBLIGATORIOS:
  - Código (unique identifier)
  - Marca
  - Tipo
  - Referencia
  - Kilómetros esperados
  - Profundidad inicial interna (PI)
  - Profundidad inicial central (PC)
  - Profundidad inicial derecha (PD)

  OPCIONALES:
  - Dimensión (recomendado, pero puede ser opcional)
  - Peso
  - Rangos de kilometraje (mayor, menor, medio)
  - Número de reencauches esperados
  - Pérdida esperada (%)
  - Total esperado
  - Costo por hora
  - Proveedor principal
  - Proveedor secundario
  - Último proveedor usado
  - Información de última compra (cantidad, precio, fecha)

  ---
  25. ¿Hay dependencias entre campos (ej: al seleccionar A, se habilita B)?

  Respuesta:

  SÍ, hay dependencias contextuales:

  1. Última Compra (Grupo de campos):
    - Si se llena last_purchase_date, entonces last_purchase_quantity y last_purchase_price deberían ser obligatorios
    - Si se llena last_purchase_quantity o last_purchase_price, entonces last_purchase_date debería ser obligatorio
  2. Proveedores:
    - Si se selecciona last_supplier_id, debería validarse que exista en el catálogo
  3. Profundidades iniciales:
    - Los tres campos (PI, PC, PD) deben llenarse juntos (si se llena uno, los otros también)
  4. Rangos de kilometraje:
    - Si se llena expected_km_range_min, validar que sea menor que expected_km_range_avg y expected_km_range_max

  Interacción con Catálogos:
  - Al seleccionar Marca, el combo de Tipo podría filtrarse
  - Al seleccionar Tipo, el combo de Referencia podría filtrarse

  ---
  I. RIESGOS E INCERTIDUMBRES

  26. ¿Hay información ambigua o faltante en el requerimiento?

  Respuesta:

  SÍ, hay algunas ambigüedades:

  1. Campo "Dimensiones": No está claro si es un campo de texto libre o estructurado (ancho/alto/radio por separado)
  2. Campo "Total esperado": No está definido qué representa (¿total de kilómetros? ¿total de costo?)
  3. Campo "Costo por hora": No está claro cómo se calcula o si es manual
  4. Rango de kilometraje (mayor, menor, medio): No está explicado para qué se usan estos tres valores
  5. Pérdida esperada (%): No está claro qué tipo de pérdida (¿desgaste? ¿económica?)
  6. Peso: No especifica unidad (¿kg? ¿lbs?)
  7. Profundidades iniciales (PI, PC, PD): Unidad implícita es mm, pero no se especifica explícitamente

  ---
  27. ¿Qué supuestos debo hacer?

  Respuesta:

  SUPUESTOS que haré:

  1. Dimensión: Campo de texto libre con formato estándar (ej: "295/80R22.5")
  2. Profundidades: Unidad en milímetros (mm)
  3. Peso: Unidad en kilogramos (kg)
  4. Código: Numérico autoincremental o alfanumérico definido por usuario
  5. Total esperado y Costo por hora: Campos editables manualmente (no calculados)
  6. Pérdida esperada: Porcentaje de desgaste/depreciación esperada
  7. Rangos de kilometraje: Representan variación esperada de vida útil (mínimo, promedio, máximo)
  8. Estado por defecto: Nueva ficha se crea en estado ACTIVE
  9. Soft delete: Las fichas eliminadas mantienen deleted_at NOT NULL

  ---
  28. ¿Qué requiere confirmación explícita del Product Owner?

  Respuesta:

  REQUIERE CONFIRMACIÓN:

  1. Estructura del campo "Dimensiones": ¿Texto libre o campos separados (ancho, alto, radio)?
  2. Significado de "Total esperado": ¿Qué representa exactamente?
  3. Cálculo de "Costo por hora": ¿Se calcula automáticamente o es manual?
  4. Uso de los rangos de kilometraje: ¿Para qué se utilizan estos tres valores (menor, medio, mayor)?
  5. Definición de "Pérdida esperada (%)": ¿Qué tipo de pérdida mide?
  6. Estrategia de eliminación: ¿Soft delete siempre o permitir eliminación física si NO hay llantas asociadas?
  7. Código de ficha: ¿Autoincremental o definido por usuario?
  8. Catálogos previos: ¿Ya existen las tablas de Marcas, Tipos, Referencias, Proveedores o debo crearlas también?

  ---
  J. INTEGRACIÓN CON MÓDULOS EXISTENTES

  29. ¿Este requerimiento depende de Auth o Users?

  Respuesta:

  SÍ, depende de ambos:

  Dependencia de Auth:
  - Todos los endpoints deben estar protegidos con autenticación JWT
  - Solo usuarios autenticados pueden acceder al módulo

  Dependencia de Users:
  - Los campos de auditoría (created_by, updated_by, deleted_by) referencian a la tabla users
  - El sistema debe registrar qué usuario realizó cada operación

  ---
  30. ¿Necesita permisos especiales?

  Respuesta:

  SÍ, debe implementar control de acceso basado en roles:

  Permisos sugeridos:

  - TIRE_SPECIFICATION_VIEW - Ver listado y detalles de fichas técnicas
  - TIRE_SPECIFICATION_CREATE - Crear nueva ficha técnica
  - TIRE_SPECIFICATION_UPDATE - Modificar ficha existente
  - TIRE_SPECIFICATION_DELETE - Eliminar (soft delete) ficha técnica
  - TIRE_SPECIFICATION_ADMIN - Acceso completo (incluye reactivar fichas inactivas)

  Roles típicos:
  - ADMIN: Todos los permisos
  - JEFE_TALLER: VIEW, CREATE, UPDATE
  - ALMACENISTA: VIEW
  - ANALISTA_COMPRAS: VIEW

  ---
  31. ¿Consume catálogos compartidos (shared schema)?

  Respuesta:

  SÍ, consume CUATRO catálogos del esquema shared:

  1. tire_brands (Marcas de llantas) - FK: brand_id
  2. tire_types (Tipos de llantas) - FK: type_id
  3. tire_references (Referencias de llantas) - FK: reference_id
  4. tire_suppliers (Proveedores de llantas) - FKs: main_supplier_id, secondary_supplier_id, last_supplier_id

  Nota: Estos catálogos deben existir ANTES de implementar este RF-001, o deben crearse como parte de este mismo sprint.

  ---
  📐 FASE 2: DISEÑO DE SOLUCIÓN

  ---
  1. AGGREGATE ROOT Y ENTIDADES

  Aggregate Root

  TireSpecification (Ficha Técnica)

  Entidades del Dominio

  - TireSpecification (Entidad raíz del agregado)

  Value Objects

  - SpecificationCode (Código único de la ficha)
  - TireDimension (Dimensiones de la llanta)
  - TreadDepth (Profundidad de banda - PI, PC, PD)
  - ExpectedLifespan (Vida útil esperada en km)
  - PurchaseInfo (Información de última compra)

  Límites del Agregado

  Este es un agregado simple y autocontenido:
  - TireSpecification es el único elemento del agregado
  - NO tiene entidades hijas
  - Tiene Value Objects que encapsulan lógica de validación
  - Las relaciones con Brands, Types, References, Suppliers son referencias externas (no parte del agregado)

  ---
  2. CASOS DE USO (APPLICATION LAYER)

  UC-001: Crear Ficha Técnica

  Nombre: CreateTireSpecificationUseCase

  Flujo Principal:
  1. Recibir DTO con datos de la ficha técnica
  2. Validar que el código sea único
  3. Validar que marca, tipo, referencia existan en catálogos
  4. Validar que proveedores (si se especifican) existan
  5. Validar reglas de negocio (kilómetros > 0, profundidades en rango)
  6. Crear entidad TireSpecification
  7. Persistir en base de datos
  8. Registrar auditoría (created_by, created_at)
  9. Retornar DTO de respuesta con la ficha creada

  Precondiciones:
  - Usuario autenticado
  - Usuario con permiso TIRE_SPECIFICATION_CREATE
  - Catálogos de marca, tipo, referencia disponibles

  Postcondiciones:
  - Nueva ficha técnica creada en estado ACTIVE
  - Registro de auditoría creado

  Invariantes Validadas:
  - INV-001: Código único
  - INV-002: Marca, tipo, referencia válidos
  - INV-003: Kilómetros esperados > 0
  - INV-004: Profundidades en rango [0, 99.9]

  ---
  UC-002: Listar Fichas Técnicas

  Nombre: ListTireSpecificationsUseCase

  Flujo Principal:
  1. Recibir parámetros de paginación, filtros y ordenamiento
  2. Aplicar filtros (marca, tipo, estado, etc.)
  3. Consultar base de datos con paginación
  4. Mapear entidades a DTOs
  5. Retornar página de resultados

  Precondiciones:
  - Usuario autenticado
  - Usuario con permiso TIRE_SPECIFICATION_VIEW

  Postcondiciones:
  - Lista de fichas técnicas retornada
  - Sin modificación de datos

  ---
  UC-003: Consultar Ficha Técnica por ID

  Nombre: GetTireSpecificationByIdUseCase

  Flujo Principal:
  1. Recibir ID de la ficha técnica
  2. Buscar en base de datos
  3. Validar que existe (si no, lanzar NotFoundException)
  4. Mapear entidad a DTO completo
  5. Retornar DTO con todos los detalles

  Precondiciones:
  - Usuario autenticado
  - Usuario con permiso TIRE_SPECIFICATION_VIEW

  Postcondiciones:
  - Ficha técnica retornada
  - Sin modificación de datos

  ---
  UC-004: Actualizar Ficha Técnica

  Nombre: UpdateTireSpecificationUseCase

  Flujo Principal:
  1. Recibir ID y DTO con datos actualizados
  2. Buscar ficha existente (si no existe, lanzar NotFoundException)
  3. Validar que NO esté eliminada (deleted_at IS NULL)
  4. Validar cambios según reglas de negocio
  5. Si cambia marca/tipo/referencia/proveedores, validar que existan
  6. Actualizar campos modificados
  7. Registrar auditoría (updated_by, updated_at)
  8. Persistir cambios
  9. Retornar DTO actualizado

  Precondiciones:
  - Usuario autenticado
  - Usuario con permiso TIRE_SPECIFICATION_UPDATE
  - Ficha técnica existe y NO está eliminada

  Postcondiciones:
  - Ficha técnica actualizada
  - Registro de auditoría actualizado

  Invariantes Validadas:
  - Todas las invariantes del dominio

  ---
  UC-005: Eliminar Ficha Técnica (Soft Delete)

  Nombre: DeleteTireSpecificationUseCase

  Flujo Principal:
  1. Recibir ID de la ficha técnica
  2. Buscar ficha existente
  3. Validar integridad referencial: Verificar que NO existan llantas asociadas en:
    - tire_inventory
    - active_tires
    - intermediate_tires
    - retired_tires
    - tire_history
  4. Si hay llantas asociadas, lanzar BusinessRuleException
  5. Marcar como eliminada (deleted_at = NOW, deleted_by = current_user)
  6. Persistir cambios
  7. Retornar confirmación

  Precondiciones:
  - Usuario autenticado
  - Usuario con permiso TIRE_SPECIFICATION_DELETE
  - Ficha NO tiene llantas asociadas

  Postcondiciones:
  - Ficha marcada como eliminada (soft delete)
  - deleted_at IS NOT NULL
  - deleted_by apunta al usuario que eliminó

  Invariantes Validadas:
  - INV-005: deleted_at NOT NULL para estado DELETED

  ---
  UC-006: Buscar Fichas Técnicas

  Nombre: SearchTireSpecificationsUseCase

  Flujo Principal:
  1. Recibir criterios de búsqueda (marca, tipo, dimensión, texto libre)
  2. Construir query dinámica con filtros
  3. Ejecutar búsqueda con paginación
  4. Mapear resultados a DTOs
  5. Retornar resultados

  Precondiciones:
  - Usuario autenticado
  - Usuario con permiso TIRE_SPECIFICATION_VIEW

  Postcondiciones:
  - Resultados de búsqueda retornados

  ---
  3. API REST

  Endpoints del Módulo Tire Specifications

  # Crear nueva ficha técnica
  POST /api/v1/tire-specifications
  Authorization: Bearer {token}
  Content-Type: application/json

  Request:
  {
    "code": "FT-001",
    "brandId": 1,
    "typeId": 2,
    "referenceId": 5,
    "dimension": "295/80R22.5",
    "weight": 75.5,
    "expectedKm": 120000,
    "expectedKmMin": 100000,
    "expectedKmAvg": 120000,
    "expectedKmMax": 140000,
    "expectedRetreads": 3,
    "expectedLoss": 15.5,
    "totalExpected": 480000,
    "costPerHour": 25.0,
    "initialDepthInner": 18.5,
    "initialDepthCenter": 19.0,
    "initialDepthOuter": 18.8,
    "mainSupplierId": 10,
    "secondarySupplierId": 12,
    "lastSupplierId": 10,
    "lastPurchaseQuantity": 50,
    "lastPurchasePrice": 1500000.0,
    "lastPurchaseDate": "2026-01-15"
  }

  Response: 201 Created
  {
    "id": 123,
    "code": "FT-001",
    "brand": {
      "id": 1,
      "name": "Michelin"
    },
    "type": {
      "id": 2,
      "name": "Tracción"
    },
    "reference": {
      "id": 5,
      "name": "XZA2"
    },
    "dimension": "295/80R22.5",
    "expectedKm": 120000,
    "averageInitialDepth": 18.77,
    "isActive": true,
    "createdAt": "2026-01-26T10:30:00Z",
    "createdBy": {
      "id": 5,
      "fullName": "Juan Pérez"
    }
  }

  Errores:
  400 Bad Request - Datos de entrada inválidos
  409 Conflict - Código ya existe
  422 Unprocessable Entity - Violación de reglas de negocio
  401 Unauthorized - No autenticado
  403 Forbidden - Sin permisos

  # Listar fichas técnicas con paginación
  GET /api/v1/tire-specifications?page=0&size=20&sort=code,asc&brandId=1&isActive=true
  Authorization: Bearer {token}

  Response: 200 OK
  {
    "content": [
      {
        "id": 123,
        "code": "FT-001",
        "brand": { "id": 1, "name": "Michelin" },
        "type": { "id": 2, "name": "Tracción" },
        "dimension": "295/80R22.5",
        "expectedKm": 120000,
        "isActive": true
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 150,
      "totalPages": 8
    }
  }

  Errores:
  400 Bad Request - Parámetros inválidos
  401 Unauthorized - No autenticado
  403 Forbidden - Sin permisos

  # Obtener ficha técnica por ID
  GET /api/v1/tire-specifications/{id}
  Authorization: Bearer {token}

  Response: 200 OK
  {
    "id": 123,
    "code": "FT-001",
    "brand": { "id": 1, "name": "Michelin" },
    "type": { "id": 2, "name": "Tracción" },
    "reference": { "id": 5, "name": "XZA2" },
    "dimension": "295/80R22.5",
    "weight": 75.5,
    "expectedKm": 120000,
    "expectedKmMin": 100000,
    "expectedKmAvg": 120000,
    "expectedKmMax": 140000,
    "expectedRetreads": 3,
    "expectedLoss": 15.5,
    "totalExpected": 480000,
    "costPerHour": 25.0,
    "initialDepthInner": 18.5,
    "initialDepthCenter": 19.0,
    "initialDepthOuter": 18.8,
    "averageInitialDepth": 18.77,
    "mainSupplier": { "id": 10, "name": "Proveedor A" },
    "secondarySupplier": { "id": 12, "name": "Proveedor B" },
    "lastSupplier": { "id": 10, "name": "Proveedor A" },
    "lastPurchaseQuantity": 50,
    "lastPurchasePrice": 1500000.0,
    "lastPurchaseDate": "2026-01-15",
    "isActive": true,
    "createdAt": "2026-01-26T10:30:00Z",
    "createdBy": { "id": 5, "fullName": "Juan Pérez" },
    "updatedAt": "2026-01-26T11:00:00Z",
    "updatedBy": { "id": 5, "fullName": "Juan Pérez" }
  }

  Errores:
  404 Not Found - Ficha no existe
  401 Unauthorized - No autenticado
  403 Forbidden - Sin permisos

  # Actualizar ficha técnica
  PUT /api/v1/tire-specifications/{id}
  Authorization: Bearer {token}
  Content-Type: application/json

  Request:
  {
    "code": "FT-001",
    "brandId": 1,
    "typeId": 2,
    "referenceId": 5,
    "expectedKm": 125000,
    "initialDepthInner": 18.5,
    "initialDepthCenter": 19.0,
    "initialDepthOuter": 18.8
    // ... otros campos
  }

  Response: 200 OK
  {
    "id": 123,
    "code": "FT-001",
    // ... datos actualizados
    "updatedAt": "2026-01-26T12:00:00Z",
    "updatedBy": { "id": 5, "fullName": "Juan Pérez" }
  }

  Errores:
  400 Bad Request - Datos inválidos
  404 Not Found - Ficha no existe
  409 Conflict - Código duplicado (si cambió)
  422 Unprocessable Entity - Violación de reglas de negocio
  401 Unauthorized - No autenticado
  403 Forbidden - Sin permisos

  # Eliminar ficha técnica (soft delete)
  DELETE /api/v1/tire-specifications/{id}
  Authorization: Bearer {token}

  Response: 204 No Content

  Errores:
  404 Not Found - Ficha no existe
  409 Conflict - Tiene llantas asociadas (no se puede eliminar)
  401 Unauthorized - No autenticado
  403 Forbidden - Sin permisos

  # Buscar fichas técnicas
  GET /api/v1/tire-specifications/search?query=Michelin&brandId=1&dimension=295/80R22.5
  Authorization: Bearer {token}

  Response: 200 OK
  {
    "content": [ /* resultados */ ],
    "pageable": { /* paginación */ }
  }

  Errores:
  400 Bad Request - Parámetros inválidos
  401 Unauthorized - No autenticado
  403 Forbidden - Sin permisos

  ---
  4. MODELO DE DOMINIO (DIAGRAMA)

  erDiagram
      TIRE_SPECIFICATIONS {
          bigserial id PK
          varchar(50) code UK "NOT NULL"
          bigint brand_id FK "NOT NULL"
          bigint type_id FK "NOT NULL"
          bigint reference_id FK "NOT NULL"
          varchar(50) dimension
          decimal weight
          int expected_km "NOT NULL, > 0"
          int expected_km_min
          int expected_km_avg
          int expected_km_max
          int expected_retreads
          decimal expected_loss
          decimal total_expected
          decimal cost_per_hour
          decimal initial_depth_inner "0-99.9"
          decimal initial_depth_center "0-99.9"
          decimal initial_depth_outer "0-99.9"
          bigint main_supplier_id FK
          bigint secondary_supplier_id FK
          bigint last_supplier_id FK
          int last_purchase_quantity
          decimal last_purchase_price
          date last_purchase_date
          boolean is_active "DEFAULT true"
          timestamp created_at "NOT NULL"
          bigint created_by FK "NOT NULL"
          timestamp updated_at
          bigint updated_by FK
          timestamp deleted_at
          bigint deleted_by FK
      }

      TIRE_BRANDS {
          bigserial id PK
          varchar(100) name UK
          boolean is_active
      }

      TIRE_TYPES {
          bigserial id PK
          varchar(100) name UK
          boolean is_active
      }

      TIRE_REFERENCES {
          bigserial id PK
          varchar(100) name UK
          boolean is_active
      }

      TIRE_SUPPLIERS {
          bigserial id PK
          varchar(200) name UK
          boolean is_active
      }

      USERS {
          bigint id PK
          varchar(100) email UK
          varchar(200) full_name
      }

      TIRE_SPECIFICATIONS ||--o{ TIRE_BRANDS : "brand_id"
      TIRE_SPECIFICATIONS ||--o{ TIRE_TYPES : "type_id"
      TIRE_SPECIFICATIONS ||--o{ TIRE_REFERENCES : "reference_id"
      TIRE_SPECIFICATIONS ||--o{ TIRE_SUPPLIERS : "main_supplier_id"
      TIRE_SPECIFICATIONS ||--o{ TIRE_SUPPLIERS : "secondary_supplier_id"
      TIRE_SPECIFICATIONS ||--o{ TIRE_SUPPLIERS : "last_supplier_id"
      TIRE_SPECIFICATIONS ||--o{ USERS : "created_by"
      TIRE_SPECIFICATIONS ||--o{ USERS : "updated_by"
      TIRE_SPECIFICATIONS ||--o{ USERS : "deleted_by"

  Estados y Transiciones

  stateDiagram-v2
      [*] --> ACTIVE: Crear ficha
      ACTIVE --> INACTIVE: Descontinuar
      INACTIVE --> ACTIVE: Reactivar
      ACTIVE --> DELETED: Eliminar (sin llantas)
      INACTIVE --> DELETED: Eliminar (sin llantas)
      DELETED --> [*]

      note right of ACTIVE
          is_active = true
          deleted_at IS NULL
      end note

      note right of INACTIVE
          is_active = false
          deleted_at IS NULL
      end note

      note right of DELETED
          deleted_at IS NOT NULL
          (Soft Delete - Irreversible)
      end note

  Invariantes Principales

  INV-001: code UNIQUE WHERE deleted_at IS NULL
  INV-002: expected_km > 0
  INV-003: 0 <= initial_depth_* <= 99.9
  INV-004: brand_id, type_id, reference_id EXIST
  INV-005: deleted_at NOT NULL ⟺ Estado DELETED
  INV-006: created_at <= updated_at

  ---
  ⏸️ FASE 3: CONFIRMACIÓN EXPLÍCITA

  📊 RESUMEN DEL DISEÑO

  Tipo de Implementación: CRUD Simple (Catálogo Maestro)

  Complejidad: Baja-Media
  - Sin flujo de estados complejo
  - Sin eventos de dominio
  - Validaciones de integridad referencial estándar
  - Auditoría completa

  Componentes a Implementar:

  Backend:
  - Entidad: TireSpecification
  - Value Objects: SpecificationCode, TreadDepth
  - Repository: TireSpecificationRepository
  - 6 Use Cases (Create, List, GetById, Update, Delete, Search)
  - Controller REST con 6 endpoints
  - DTOs: Request, Response, Summary
  - Validators: Jakarta Validation + Custom

  Frontend:
  - Página de listado con DataGrid
  - Formulario modal de creación/edición
  - Modal de detalle (solo lectura)
  - Servicios de API (React Query)
  - Validación de formularios

  Base de Datos:
  - Tabla: tire_specifications (esquema tire_management)
  - 4 tablas de catálogos (esquema shared): tire_brands, tire_types, tire_references, tire_suppliers

  ---
  🚨 SUPUESTOS Y ÁREAS DE RIESGO

  Supuestos:
  1. Los catálogos de Brands, Types, References, Suppliers ya existen o se crearán en este sprint
  2. Campo "dimensiones" será texto libre con validación de formato opcional
  3. Soft delete será la estrategia por defecto
  4. Código de ficha será definido por el usuario (no autoincremental)

  Áreas de Riesgo:
  1. Dependencia de catálogos compartidos (deben existir primero)
  2. Definición ambigua de algunos campos ("Total esperado", "Costo por hora")
  3. Validación de integridad referencial puede ser costosa si hay muchas llantas

  ---
  ❓ PREGUNTAS PENDIENTES PARA EL PRODUCT OWNER

  1. ¿El código de ficha técnica es autoincremental o lo define el usuario?
  2. ¿Los catálogos de Brands, Types, References, Suppliers ya existen o los creamos ahora?
  3. ¿Qué representa exactamente el campo "Total esperado"?
  4. ¿Cómo se calcula el "Costo por hora" o es manual?
  5. ¿El campo "Dimensiones" debe validarse con un formato específico?
  6. ¿La unidad de peso es kilogramos?

  ---
  ✅ SOLICITUD DE APROBACIÓN

  ¿Confirmas que el análisis y diseño presentado es correcto y puedo proceder con la implementación del RF-001 - Gestión
   de Fichas Técnicas?

  Esperando tu confirmación para avanzar a la FASE DE IMPLEMENTACIÓN.