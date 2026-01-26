# PRD - SISTEMA DE CONTROL Y GESTIÓN DE LLANTAS
## DOCUMENTO DE REQUISITOS DEL PRODUCTO

**Versión:** 1.0  
**Fecha de Análisis:** Enero 20, 2026  
**Analista:** Análisis basado en formularios Oracle Forms Legacy  
**Sistema Origen:** MILENIO - Módulo de Llantas

---

## 1. RESUMEN EJECUTIVO

### 1.1. Objetivo del Sistema
El Sistema de Control y Gestión de Llantas es una aplicación empresarial diseñada para gestionar integralmente el ciclo de vida de neumáticos en una flota vehicular. El sistema permite controlar inventarios, instalaciones, rotaciones, muestreos de desgaste, mantenimientos y el historial completo de cada llanta desde su adquisición hasta su baja definitiva.

### 1.2. Alcance Funcional
El sistema abarca:
- **Gestión de Inventario**: Control de llantas en bodega, instaladas y retiradas
- **Administración de Vehículos**: Registro y control de vehículos de la flota
- **Instalación y Desmontaje**: Gestión de movimientos de llantas entre vehículos y posiciones
- **Muestreos de Desgaste**: Registro periódico de profundidades y kilometraje
- **Gestión de Fichas Técnicas**: Catálogo de especificaciones de neumáticos
- **Reportería y Consultas**: Visualización de historiales y estadísticas
- **Control de Kilometraje**: Seguimiento acumulado de kilómetros recorridos por llanta
- **Gestión de Bajas**: Registro de llantas retiradas del servicio

### 1.3. Usuarios Objetivo
- **Jefe de Mantenimiento de Flota**: Supervisión general y toma de decisiones
- **Técnicos de Mantenimiento**: Registro de instalaciones, desmontajes y muestreos
- **Personal de Almacén**: Gestión de inventario de llantas
- **Administradores del Sistema**: Configuración de catálogos maestros
- **Personal de Contabilidad/Auditoría**: Consulta de movimientos y costos

---

## 2. REQUERIMIENTOS FUNCIONALES

### 2.1. ALFA.FMB - Módulo Principal de Gestión de Llantas

**Descripción Funcional:**  
Formulario principal y más complejo del sistema. Actúa como hub central para la gestión completa del ciclo de vida de las llantas. Integra múltiples sub-módulos para inventario, instalación, rotación, consultas y administración de catálogos maestros.

**Casos de Uso Principales:**

1. **UC-ALFA-01: Gestión de Inventario de Llantas**
   - Registrar nuevas llantas en inventario
   - Consultar llantas disponibles
   - Modificar información de llantas
   - Registrar cambios de estado (disponible, instalada, retirada)

2. **UC-ALFA-02: Instalación de Llantas en Vehículos (MONTAR)**
   - Seleccionar vehículo destino
   - Seleccionar llanta del inventario
   - Asignar posición en el vehículo
   - Registrar kilometraje de instalación
   - Generar registro en KMS_RECORRIDO_LLANTAS

3. **UC-ALFA-03: Localización y Consulta de Llantas**
   - Buscar llanta por código
   - Ver ubicación actual (vehículo y posición)
   - Consultar historial de movimientos
   - Ver estadísticas de kilometraje

4. **UC-ALFA-04: Gestión de Fichas Técnicas**
   - Crear/modificar fichas técnicas de neumáticos
   - Asociar fichas a marcas y referencias
   - Definir especificaciones técnicas
   - Gestionar observaciones

5. **UC-ALFA-05: Registro de Bajas de Llantas**
   - Seleccionar llanta a dar de baja
   - Registrar motivo de baja
   - Actualizar estado en inventario
   - Generar registro histórico

6. **UC-ALFA-06: Administración de Catálogos Maestros**
   - Gestionar proveedores de llantas
   - Administrar marcas
   - Gestionar tipos de llantas
   - Administrar referencias
   - Gestionar observaciones estándar
   - Administrar clases de vehículos

**Bloques Principales:**

| Bloque | Tabla Base | Propósito |
|--------|-----------|-----------|
| MAESTRO | LLANTAS | Bloque principal de control y navegación |
| INVENTARIO | LLANTAS | Gestión de llantas en bodega |
| MONTAR | LLANTAS | Instalación de llantas en vehículos |
| LOCALIZA | LLANTAS | Búsqueda y localización de llantas |
| ACTIVAS | LLANTAS | Consulta de llantas en uso |
| BAJAS | LLANTAS | Gestión de llantas dadas de baja |
| FICHATEC | FICHATEC | Catálogo de fichas técnicas |
| NEUMATICO | NEUMATICO | Datos complementarios de neumáticos |
| PROVEEDORES | PROVEEDORES_LLANTAS | Catálogo de proveedores |
| MARCAS | MARCAS_LLANTAS | Catálogo de marcas |
| TIPOS | TIPOS | Catálogo de tipos de llantas |
| REFERENCIA | REFERENCIA | Catálogo de referencias |
| OBSERVA | OBSERVA | Catálogo de observaciones |
| VEHICULOS | VEHICULOS_LLANTAS | Datos de vehículos de la flota |
| CIRCULAR | (Consulta) | Vista circular de llantas |
| CIRCULAR1 | (Consulta) | Detalle de vista circular |
| GRUPOS | (Control) | Agrupación de llantas |
| PONERNP | (Control) | Asignación de nueva posición |
| NUEVA | (Control) | Registro de llantas nuevas |

**Campos Principales - Bloque MAESTRO/INVENTARIO (Tabla LLANTAS):**
- LLANTA (PK): Código único de la llanta
- GRUPO: Código de grupo al que pertenece
- VEHICULO: Placa del vehículo donde está instalada
- POSICION: Posición en el vehículo
- MARCA: Marca del neumático
- TIPO: Tipo de llanta
- REFERENCIA: Referencia específica
- FICHA: Código de ficha técnica
- KINSTALA: Kilometraje de instalación
- FECHA_INST: Fecha de instalación
- VALOR: Valor de compra
- PROVEEDOR: Código del proveedor
- ESTADO: Estado actual (A=Activa, B=Baja, I=Inventario)
- OBSERVACIONES: Observaciones generales

**Validaciones Clave:**
- La llanta debe existir en inventario antes de instalarse
- No se puede instalar una llanta ya instalada en otro vehículo
- El vehículo debe existir en VEHICULOS_LLANTAS
- La posición debe ser válida para la clase de vehículo
- El kilometraje de instalación debe ser >= al kilometraje del vehículo

**Flujos de Trabajo:**

1. **Flujo: Instalación de Llanta Nueva**
   ```
   Inicio → Seleccionar Llanta del Inventario → 
   Validar Disponibilidad → Seleccionar Vehículo → 
   Asignar Posición → Ingresar Kilometraje → 
   Validar Datos → Grabar → 
   Actualizar KMS_RECORRIDO_LLANTAS → 
   Actualizar Estado LLANTAS → Fin
   ```

2. **Flujo: Consulta de Historial de Llanta**
   ```
   Inicio → Ingresar Código Llanta → 
   Buscar en LLANTAS → Consultar HISTORIA → 
   Consultar MUESTREO → Consultar KMS_RECORRIDO_LLANTAS → 
   Mostrar Información Consolidada → Fin
   ```

**Acciones Disponibles:**
- Insertar: Nueva llanta en inventario
- Modificar: Actualizar información de llanta
- Eliminar: Eliminar llanta (solo si no tiene historial)
- Consultar: Búsqueda y visualización
- Instalar: Montar llanta en vehículo
- Desmontar: Retirar llanta de vehículo
- Rotar: Cambiar posición de llanta
- Dar de Baja: Retirar llanta del servicio
- Imprimir: Generar reportes

---

### 2.2. MLFR008.FMB - Creación y Administración de Vehículos

**Descripción Funcional:**  
Formulario dedicado a la gestión del catálogo de vehículos de la flota. Permite crear, modificar y consultar información de vehículos que forman parte del sistema de control de llantas.

**Casos de Uso Principales:**

1. **UC-MLFR008-01: Registrar Nuevo Vehículo**
   - Ingresar placa única
   - Seleccionar clase de vehículo
   - Registrar marca y modelo
   - Definir kilometraje inicial y actual
   - Establecer estado operativo

2. **UC-MLFR008-02: Consultar Vehículos**
   - Buscar por placa
   - Filtrar por clase
   - Ver vehículos activos/inactivos
   - Ver vehículos operando/no operando

3. **UC-MLFR008-03: Actualizar Información de Vehículo**
   - Modificar datos generales
   - Actualizar kilometraje actual
   - Cambiar estado operativo
   - Registrar cambios de marca/modelo

**Bloque Principal:**

| Campo | Tipo | Descripción | Validación |
|-------|------|-------------|------------|
| PLACA | VARCHAR2(6) | Identificador único del vehículo (PK) | Alfanumérico, 6 posiciones, obligatorio |
| CLASE | VARCHAR2 | Clase de vehículo | LOV desde tabla CLASES |
| MARCA | VARCHAR2(15) | Marca del vehículo | Alfabético, 15 letras |
| MODELO | NUMBER | Año del modelo | Debe ser >= 1970 |
| KILOMINI | NUMBER(7) | Kilometraje inicial | Numérico, 7 dígitos |
| KILOMACT | NUMBER(7) | Kilometraje actual | >= KILOMINI |
| ESTADO | VARCHAR2 | Estado del vehículo | Activo/Inactivo |
| OPERANDO | VARCHAR2 | Si está operando | Sí/No |
| FRM_CLASE_V2 | VARCHAR2 | Descripción de la clase (calculado) | Solo lectura |

**Tabla Base:** VEHICULOS_LLANTAS

**Tablas Relacionadas:**
- CLASES: Catálogo de clases de vehículos

**Validaciones Específicas:**
- El modelo no puede ser inferior a 1970
- El kilometraje actual debe ser mayor o igual al inicial
- La placa debe ser única en el sistema
- La clase debe existir en el catálogo CLASES

**Procedimientos Embebidos:**
- `P_INICIARFORMA`: Inicialización del formulario
- `P_INICIARTOOLBAR`: Configuración de la barra de herramientas
- `VALIDAR`: Validación general de datos
- `SALVAR`: Grabación de datos
- `LLAMAR_REPORTE`: Generación de reportes
- Referencia a procedimientos de MILENIO.FMB: NOTAG, NOTAP, NOTAA, NOTA (para mensajes)

**Triggers Principales:**
- WHEN-NEW-FORM-INSTANCE: Inicialización y configuración
- POST-QUERY (VEHICULOS_LLANTAS): Obtiene descripción de la clase
- WHEN-VALIDATE-ITEM (MODELO): Valida que el modelo sea >= 1970
- WHEN-BUTTON-PRESSED: Múltiples botones de la toolbar

**List of Values (LOV):**
- LV_CLASES: 
  ```sql
  SELECT nombre, codigo 
  FROM clases 
  ORDER BY nombre
  ```

**Acciones Disponibles:**
- Adicionar: Crear nuevo vehículo
- Grabar: Guardar cambios
- Consultar: Búsqueda de vehículos
- Limpiar: Limpiar formulario
- Imprimir: Generar listado
- Navegación: Primero, Anterior, Siguiente, Último
- Salir: Cerrar formulario

---

### 2.3. MLFR009.FMB - Muestreos de Vehículos (Control de Desgaste)

**Descripción Funcional:**  
Formulario especializado para el registro periódico de muestreos de desgaste de llantas. Permite capturar mediciones de profundidad de banda de rodadura en múltiples puntos (izquierda, centro, derecha) y asociarlas con el kilometraje y fecha del vehículo. Este módulo es crítico para la planificación de mantenimientos preventivos y reemplazo de neumáticos.

**Casos de Uso Principales:**

1. **UC-MLFR009-01: Registrar Muestreo Completo de Vehículo**
   - Seleccionar vehículo (por placa)
   - Consultar llantas instaladas
   - Ingresar kilometraje actual del vehículo
   - Registrar fecha del muestreo
   - Para cada llanta: ingresar profundidades (izquierda, centro, derecha)
   - Sistema calcula profundidad promedio automáticamente
   - Validar datos vs. último muestreo
   - Grabar muestreo
   - Actualizar tabla KMS_RECORRIDO_LLANTAS

2. **UC-MLFR009-02: Consultar Muestreos por Placa**
   - Buscar vehículo por placa (LOV)
   - Sistema muestra todas las llantas del vehículo
   - Ver últimos muestreos registrados
   - Comparar tendencias de desgaste

3. **UC-MLFR009-03: Consultar Muestreos por Llanta**
   - Ordenar por número de llanta
   - Ver historial completo de muestreos
   - Analizar patrón de desgaste

4. **UC-MLFR009-04: Consultar por Grupo o Posición**
   - Ordenar por grupo de llantas
   - Ordenar por posición en vehículo
   - Análisis comparativo de desgaste

**Bloques Principales:**

| Bloque | Tabla Base | Propósito |
|--------|-----------|-----------|
| CONTROL | (Virtual) | Parámetros del muestreo (KMS, fecha, botones) |
| LLANTAS | LLANTAS | Bloque multi-registro con llantas del vehículo |

**Campos - Bloque CONTROL:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| FRM_KMSMUESTREO_NB | NUMBER | Kilometraje del vehículo al momento del muestreo |
| FRM_FECMUESTREO_DT | DATE | Fecha del muestreo |
| BT_PLACA | Button | Abre LOV de placas |
| BT_LLANTA | Button | Ordena por llanta |
| BT_GRUPO | Button | Ordena por grupo |
| BT_POSICION | Button | Ordena por posición |

**Campos - Bloque LLANTAS (Multi-Registro):**

| Campo | Tipo | Descripción | Validación |
|-------|------|-------------|------------|
| LLANTA | NUMBER | Código de la llanta | PK, solo lectura |
| GRUPO | VARCHAR2 | Grupo de la llanta | Solo lectura |
| POSICION | VARCHAR2 | Posición en el vehículo | Solo lectura |
| FRM_PROFIMUESTREO_NB | NUMBER | Profundidad izquierda (mm) | Obligatorio |
| FRM_PROFCMUESTREO_NB | NUMBER | Profundidad central (mm) | Obligatorio |
| FRM_PROFDMUESTREO_NB | NUMBER | Profundidad derecha (mm) | Obligatorio |
| FRM_PROFPMUESTREO_NB | NUMBER | Profundidad promedio (mm) | Calculado: (PI+PC+PD)/3 |
| FRM_KMSM_NB | NUMBER | KMS del último muestreo | Solo lectura, POST-QUERY |
| FRM_FECHAM_DT | DATE | Fecha del último muestreo | Solo lectura, POST-QUERY |
| FRM_PROFM_NB | NUMBER | Profundidad promedio última medición | Solo lectura, POST-QUERY |
| FRM_PRESION_NB | NUMBER | Presión del neumático (PSI) | Opcional |

**Tablas Involucradas:**
- **LLANTAS**: Tabla principal de llantas
- **MUESTREO**: Almacena cada registro de muestreo (INSERT)
- **KMS_RECORRIDO_LLANTAS**: Se actualiza con kilómetros recorridos desde último muestreo (UPDATE)
- **VEHICULOS_LLANTAS**: Consulta para validar vehículo
- **INVENTARIO**: Validación de existencia
- **RETIRADAS**: Exclusión de llantas retiradas

**Validaciones Críticas:**

1. **Validación de Kilometraje:**
   ```
   IF (control.frm_kmsmuestreo_nb < llantas.frm_kmsm_nb) THEN
     ERROR: "No se pueden ingresar los muestreos. Error en el kms para la llanta No. X"
   ```

2. **Validación de Profundidades:**
   ```
   IF (llantas.frm_profpmuestreo_nb > llantas.frm_profm_nb) THEN
     ERROR: "No se pueden ingresar los muestreos. Error en las profundidades para la llanta No. X"
     (Las profundidades no pueden aumentar vs. medición anterior)
   ```

3. **Validación de Fecha:**
   ```
   IF (control.frm_fecmuestreo_dt < llantas.frm_fecham_dt) THEN
     ERROR: "No se pueden ingresar los muestreos. Error en la fecha de muestreo para la llanta No. X"
   ```

**Trigger POST-QUERY (LLANTAS):**
Consulta automática del último muestreo registrado:
```sql
SELECT a.kilom, a.fecha, ((a.pi+a.pc+a.pd)/3)
INTO :llantas.frm_kmsm_nb, :llantas.frm_fecham_dt, :llantas.frm_profm_nb
FROM muestreo a
WHERE a.fecha = (SELECT MAX(b.fecha)
                 FROM muestreo b
                 WHERE a.llanta = b.llanta
                 AND SUBSTR(a.grupo,3,1) = SUBSTR(b.grupo,3,1))
AND a.llanta = :llantas.llanta
AND a.grupo = :llantas.grupo
AND rownum < 2;
```

**Operaciones DML al Grabar:**

1. **INSERT INTO MUESTREO:**
   ```sql
   INSERT INTO MUESTREO VALUES (
     :llanta, :grupo, :fecha_muestreo, :kilometraje,
     :prof_izq, :prof_centro, :prof_der, :presion
   )
   ```

2. **UPDATE KMS_RECORRIDO_LLANTAS:**
   ```sql
   UPDATE KMS_RECORRIDO_LLANTAS 
   SET KMRL_KMSRECORRIDO_NB = KMRL_KMSRECORRIDO_NB + (kms_actual - kms_anterior),
       KMRL_FECHA_DT = SYSDATE
   WHERE KMRL_LLANTA_NB = :llanta
   AND SUBSTR(KMRL_GRUPO_CH,3,1) = SUBSTR(:grupo,3,1)
   ```

3. **SELECT FROM LLANTAS:**
   Consulta KINSTALA para cálculo de kilometraje acumulado

**Fórmula Calculada:**
```
FRM_PROFPMUESTREO_NB = (FRM_PROFIMUESTREO_NB + FRM_PROFCMUESTREO_NB + FRM_PROFDMUESTREO_NB) / 3
```

**List of Values:**
- LV_PLACAS: Selección de placas de vehículos activos
  ```sql
  SELECT placa FROM vehiculos_llantas 
  WHERE operando = 'S' 
  ORDER BY placa
  ```

**Procedimientos Embebidos:**
- `P_INICIARFORMA`: Inicialización
- `P_INICIARTOOLBAR`: Configuración de toolbar
- `VALIDAR`: Validación de datos antes de grabar
- `SALVAR`: Proceso de grabación con transacciones
- Referencia a MILENIO.FMB: NOTAP, NOTA, NOTAG, NOTAA

**Triggers Principales:**
- WHEN-NEW-FORM-INSTANCE: Configuración inicial
- POST-QUERY (LLANTAS): Carga datos del último muestreo
- WHEN-VALIDATE-RECORD (LLANTAS): Valida datos de cada registro
- WHEN-BUTTON-PRESSED (CONTROL.BT_PLACA): Abre LOV de placas
- WHEN-BUTTON-PRESSED (CONTROL.BT_LLANTA): Reordena por llanta
- WHEN-BUTTON-PRESSED (CONTROL.BT_GRUPO): Reordena por grupo
- WHEN-BUTTON-PRESSED (CONTROL.BT_POSICION): Reordena por posición
- FORMULA-CALCULATION (FRM_PROFPMUESTREO_NB): Cálculo automático de promedio

**Flujo de Trabajo Principal:**
```
Inicio → 
  Usuario presiona BT_PLACA → 
  Sistema muestra LOV de placas → 
  Usuario selecciona placa → 
  Sistema ejecuta query de llantas del vehículo → 
  Sistema ejecuta POST-QUERY para cada llanta (carga último muestreo) → 
  Usuario ingresa KMS y fecha en CONTROL → 
  Usuario ingresa profundidades para cada llanta → 
  Sistema calcula promedio automáticamente → 
  Usuario presiona GRABAR → 
  Sistema ejecuta VALIDAR → 
    Valida KMS >= último KMS →
    Valida profundidades <= último muestreo →
    Valida fecha >= última fecha →
  Sistema ejecuta SALVAR → 
    Para cada llanta validada:
      INSERT INTO MUESTREO →
      UPDATE KMS_RECORRIDO_LLANTAS →
  Sistema muestra mensaje de éxito → 
Fin
```

**Acciones Disponibles:**
- Ordenar por Placa / Llanta / Grupo / Posición
- Adicionar: Nuevo registro de muestreo
- Grabar: Guardar todos los muestreos
- Limpiar: Limpiar formulario
- Consultar: Búsqueda de muestreos anteriores
- Imprimir: Generar reporte de muestreos
- Navegación: Entre registros del bloque LLANTAS
- Ayuda: Acceso al manual (C:\MANUALES\MANUSCAT.HTML)

---

### 2.4. MLFR010.FMB - Gestión de Historia de Llantas

**Descripción Funcional:**  
Formulario dedicado a la consulta y gestión del historial completo de movimientos y eventos de las llantas. Registra cada cambio significativo en la vida de un neumático, incluyendo instalaciones, desmontajes, rotaciones, mantenimientos y observaciones.

**Casos de Uso Principales:**

1. **UC-MLFR010-01: Consultar Historial Completo de Llanta**
   - Buscar llanta por código
   - Ver todos los eventos históricos
   - Ordenar por fecha
   - Filtrar por tipo de evento

2. **UC-MLFR010-02: Registrar Evento en Historia**
   - Seleccionar llanta
   - Seleccionar tipo de evento
   - Ingresar detalles del evento
   - Registrar fecha y usuario
   - Asociar observaciones del catálogo

3. **UC-MLFR010-03: Consultar Observaciones Estándar**
   - Acceder a catálogo de observaciones
   - Seleccionar observación predefinida
   - Ver detalle de observación

**Bloque Principal:**

| Bloque | Tabla Base | Propósito |
|--------|-----------|-----------|
| HISTORIA | HISTORIA | Registro de eventos históricos |
| (Referencia) | OBSERVA | Catálogo de observaciones |

**Campos Principales:**
- LLANTA: Código de la llanta
- GRUPO: Grupo al que pertenece
- FECHA: Fecha del evento
- EVENTO: Tipo de evento (Instalación, Desmontaje, Rotación, Mantenimiento, Baja)
- VEHICULO: Vehículo relacionado (si aplica)
- POSICION: Posición relacionada (si aplica)
- KILOMETRAJE: Kilometraje en el momento del evento
- OBSERVACIONES: Código de observación o texto libre
- USUARIO: Usuario que registra el evento
- DETALLE: Descripción detallada del evento

**Tablas Involucradas:**
- **HISTORIA**: Tabla principal de eventos (INSERT, SELECT)
- **OBSERVA**: Catálogo de observaciones predefinidas (SELECT)

**Validaciones:**
- La llanta debe existir en el sistema
- La fecha no puede ser futura
- El tipo de evento debe ser válido
- El kilometraje debe ser coherente con registros anteriores

**Procedimientos Embebidos:**
- Procedimientos estándar de MILENIO.FMB para mensajes y validaciones

**Triggers Principales:**
- WHEN-NEW-FORM-INSTANCE: Inicialización
- POST-QUERY: Carga detalles de observaciones
- KEY-EXIT: Salida del formulario

**Acciones Disponibles:**
- Consultar: Búsqueda de historiales
- Adicionar: Nuevo evento histórico
- Limpiar: Limpiar formulario
- Imprimir: Generar reporte de historial
- Navegación: Entre eventos históricos

---

### 2.5. MLFR011.FMB - Gestión de Llantas Retiradas

**Descripción Funcional:**  
Formulario especializado para el manejo de llantas que han sido dadas de baja o retiradas del servicio activo. Permite consultar el inventario de llantas retiradas, analizar motivos de baja y gestionar el proceso de disposición final.

**Casos de Uso Principales:**

1. **UC-MLFR011-01: Consultar Llantas Retiradas**
   - Ver listado de llantas en estado "retirada"
   - Filtrar por fecha de baja
   - Filtrar por motivo de retiro
   - Ver detalles de ficha técnica

2. **UC-MLFR011-02: Registrar Retiro de Llanta**
   - Seleccionar llanta activa
   - Ingresar motivo de retiro
   - Registrar fecha de baja
   - Transferir a tabla RETIRADAS
   - Actualizar estado en LLANTAS
   - Registrar en HISTORIA

3. **UC-MLFR011-03: Gestionar Disposición Final**
   - Ver llantas pendientes de disposición
   - Registrar método de disposición
   - Marcar como procesada

4. **UC-MLFR011-04: Análisis de Causas de Baja**
   - Reportes por motivo de retiro
   - Estadísticas de vida útil
   - Análisis por marca/modelo

**Bloque Principal:**

| Bloque | Tabla Base | Propósito |
|--------|-----------|-----------|
| CONTROL | (Virtual) | Parámetros de consulta |
| RETIRADAS | RETIRADAS | Llantas dadas de baja |

**Campos Principales - RETIRADAS:**
- LLANTA: Código de la llanta retirada (PK)
- GRUPO: Grupo al que pertenecía
- FECHA_RETIRO: Fecha en que se dio de baja
- MOTIVO: Motivo del retiro
- FICHA: Código de ficha técnica (para estadísticas)
- KILOMETRAJE_TOTAL: Kilómetros totales recorridos
- VIDA_UTIL: Días de servicio
- ESTADO_FINAL: Estado físico al retiro
- DESTINO: Disposición final (Reciclaje, Venta, Desecho)

**Tablas Involucradas:**
- **RETIRADAS**: Tabla principal de llantas retiradas (SELECT, INSERT, DELETE)
- **HISTORIA**: Consulta de eventos previos (SELECT)
- **LLANTAS**: Actualización de estado (UPDATE)
- **INTERMEDIO**: Tabla temporal para procesos (INSERT)

**Operaciones DML Identificadas:**

1. **Consulta de Ficha:**
   ```sql
   SELECT FICHA 
   FROM HISTORIA 
   WHERE LLANTA = :llanta 
   AND GRUPO = :grupo
   ```

2. **Verificar si Llanta está Retirada:**
   ```sql
   SELECT COUNT(*) 
   FROM RETIRADAS 
   WHERE LLANTA = :llanta 
   AND GRUPO = :grupo
   ```

3. **Eliminar de Retiradas (Reactivación):**
   ```sql
   DELETE FROM RETIRADAS 
   WHERE LLANTA = :llanta 
   AND GRUPO = :grupo
   ```

4. **Registro en Tabla Intermedia:**
   ```sql
   INSERT INTO INTERMEDIO 
   VALUES (:llanta, :grupo, 1, NULL, :fecha)
   ```

**Validaciones:**
- Solo se pueden retirar llantas que no estén instaladas
- Debe registrarse un motivo válido de retiro
- El kilometraje total debe ser >= al kilometraje de instalación
- La fecha de retiro no puede ser anterior a la fecha de instalación

**Procedimientos Embebidos:**
- Procedimientos estándar de MILENIO.FMB

**Triggers Principales:**
- WHEN-NEW-FORM-INSTANCE: Inicialización
- POST-QUERY: Carga información complementaria
- PRE-INSERT: Validaciones antes de retiro
- POST-INSERT: Actualización de estado en LLANTAS

**Flujo de Retiro:**
```
Inicio → 
  Seleccionar llanta activa → 
  Validar que no esté instalada → 
  Ingresar motivo de retiro → 
  Calcular kilometraje total → 
  Calcular vida útil → 
  INSERT INTO RETIRADAS → 
  UPDATE LLANTAS (estado='R') → 
  INSERT INTO HISTORIA → 
  Confirmar retiro → 
Fin
```

**Acciones Disponibles:**
- Consultar: Búsqueda de llantas retiradas
- Adicionar: Registrar nueva baja
- Eliminar: Reactivar llanta (DELETE FROM RETIRADAS)
- Limpiar: Limpiar formulario
- Imprimir: Generar reporte de bajas
- Navegación: Entre registros

---

## 3. ARQUITECTURA DE DATOS

### 3.1. Tablas Identificadas y Descripción

#### 3.1.1. Tablas Principales (Transaccionales)

**LLANTAS** (Tabla central del sistema)
- **Descripción**: Almacena el inventario maestro de todas las llantas del sistema
- **Campos Clave**: 
  - LLANTA (PK): Código único de la llanta
  - GRUPO: Agrupación lógica
  - VEHICULO (FK): Placa del vehículo donde está instalada
  - POSICION: Posición en el vehículo
  - MARCA (FK): Marca del neumático
  - TIPO (FK): Tipo de llanta
  - REFERENCIA (FK): Referencia específica
  - FICHA (FK): Código de ficha técnica
  - KINSTALA: Kilometraje de instalación
  - FECHA_INST: Fecha de instalación
  - VALOR: Valor monetario
  - PROVEEDOR (FK): Proveedor
  - ESTADO: Estado actual (A/B/I)
  - OBSERVACIONES: Notas
- **Relaciones**: 
  - → VEHICULOS_LLANTAS (VEHICULO)
  - → MARCAS_LLANTAS (MARCA)
  - → TIPOS (TIPO)
  - → REFERENCIA (REFERENCIA)
  - → FICHATEC (FICHA)
  - → PROVEEDORES_LLANTAS (PROVEEDOR)
- **Operaciones**: INSERT (32x), UPDATE (5x), SELECT (32x)

**KMS_RECORRIDO_LLANTAS** (Control de kilometraje acumulado)
- **Descripción**: Registra el kilometraje acumulado de cada llanta por grupo
- **Campos Clave**:
  - KMRL_LLANTA_NB (PK): Código de llanta
  - KMRL_GRUPO_CH (PK): Grupo
  - KMRL_KMSRECORRIDO_NB: Kilómetros acumulados
  - KMRL_FECHA_DT: Fecha de última actualización
- **Relaciones**: 
  - → LLANTAS (KMRL_LLANTA_NB)
- **Operaciones**: INSERT (6x), UPDATE (2x), SELECT (5x)
- **Notas**: Se actualiza en cada muestreo con la diferencia de kilometraje

**MUESTREO** (Registro de mediciones de desgaste)
- **Descripción**: Almacena las mediciones periódicas de profundidad de banda de rodadura
- **Campos Clave**:
  - LLANTA (PK): Código de llanta
  - GRUPO (PK): Grupo
  - FECHA (PK): Fecha del muestreo
  - KILOM: Kilometraje en el momento del muestreo
  - PI: Profundidad izquierda (mm)
  - PC: Profundidad central (mm)
  - PD: Profundidad derecha (mm)
  - PRESION: Presión del neumático (PSI)
- **Relaciones**: 
  - → LLANTAS (LLANTA, GRUPO)
- **Operaciones**: INSERT (3x en MLFR009), SELECT (consultas históricas)
- **Notas**: Tabla histórica crítica para análisis de desgaste

**HISTORIA** (Bitácora de eventos)
- **Descripción**: Registra todos los eventos significativos de cada llanta
- **Campos Clave**:
  - HISTORIA_ID (PK): Identificador único del evento
  - LLANTA (FK): Código de llanta
  - GRUPO: Grupo
  - FECHA: Fecha del evento
  - EVENTO: Tipo de evento
  - VEHICULO: Vehículo relacionado
  - POSICION: Posición relacionada
  - KILOMETRAJE: Kilometraje en el evento
  - FICHA: Ficha técnica relacionada
  - OBSERVACIONES: Detalles del evento
  - USUARIO: Usuario que registra
- **Relaciones**: 
  - → LLANTAS (LLANTA)
- **Operaciones**: INSERT (2x), SELECT (múltiples consultas)
- **Notas**: Auditoría completa del ciclo de vida

**VEHICULOS_LLANTAS** (Catálogo de vehículos)
- **Descripción**: Maestro de vehículos de la flota
- **Campos Clave**:
  - PLACA (PK): Identificador único del vehículo
  - CLASE (FK): Clase de vehículo
  - MARCA: Marca del vehículo
  - MODELO: Año del modelo
  - KILOMINI: Kilometraje inicial
  - KILOMACT: Kilometraje actual
  - ESTADO: Estado (Activo/Inactivo)
  - OPERANDO: Si está en operación (S/N)
- **Relaciones**: 
  - → CLASES (CLASE)
  - ← LLANTAS (VEHICULO)
- **Operaciones**: SELECT (6x), UPDATE (kilometraje actual)
- **Formulario Principal**: MLFR008

**RETIRADAS** (Llantas dadas de baja)
- **Descripción**: Almacena llantas retiradas del servicio
- **Campos Clave**:
  - LLANTA (PK): Código de llanta
  - GRUPO (PK): Grupo
  - FECHA_RETIRO: Fecha de baja
  - MOTIVO: Motivo del retiro
  - KILOMETRAJE_TOTAL: KMS totales recorridos
  - VIDA_UTIL: Días de servicio
  - ESTADO_FINAL: Estado físico
  - DESTINO: Disposición final
- **Relaciones**: 
  - → LLANTAS (LLANTA)
- **Operaciones**: INSERT, SELECT, DELETE (reactivación)
- **Formulario Principal**: MLFR011

#### 3.1.2. Tablas de Catálogo (Maestros)

**FICHATEC** (Fichas técnicas de neumáticos)
- **Descripción**: Especificaciones técnicas detalladas de tipos de llantas
- **Campos Clave**:
  - CODIGO (PK): Código de ficha
  - REF: Referencia del fabricante
  - DESCRIPCION: Descripción técnica
  - ESPECIFICACIONES: Detalles técnicos
  - DIMENSIONES: Medidas
  - CAPACIDAD_CARGA: Índice de carga
  - VELOCIDAD_MAX: Índice de velocidad
- **Relaciones**: 
  - ← LLANTAS (FICHA)
- **Operaciones**: SELECT (28x - alta frecuencia de consultas)

**PROVEEDORES_LLANTAS** (Proveedores)
- **Descripción**: Catálogo de proveedores de neumáticos
- **Campos Clave**:
  - CODIGO (PK): Código del proveedor
  - NOMBRE: Razón social
  - CONTACTO: Persona de contacto
  - TELEFONO: Teléfono
  - DIRECCION: Dirección
  - EMAIL: Correo electrónico
- **Relaciones**: 
  - ← LLANTAS (PROVEEDOR)
- **Operaciones**: SELECT (18x)

**MARCAS_LLANTAS** (Marcas de neumáticos)
- **Descripción**: Catálogo de marcas de llantas
- **Campos Clave**:
  - CODIGO (PK): Código de marca
  - NOMBRE: Nombre de la marca
  - FABRICANTE: Fabricante
  - PAIS_ORIGEN: País de origen
- **Relaciones**: 
  - ← LLANTAS (MARCA)
- **Operaciones**: SELECT

**TIPOS** (Tipos de llantas)
- **Descripción**: Clasificación por tipo de llanta
- **Campos Clave**:
  - CODIGO (PK): Código del tipo
  - NOMBRE: Nombre del tipo (Radial, Convencional, etc.)
  - DESCRIPCION: Descripción
- **Relaciones**: 
  - ← LLANTAS (TIPO)
- **Operaciones**: SELECT (2x)

**REFERENCIA** (Referencias de productos)
- **Descripción**: Catálogo de referencias específicas
- **Campos Clave**:
  - CODIGO (PK): Código de referencia
  - NOMBRE: Nombre de la referencia
  - DESCRIPCION: Descripción
- **Relaciones**: 
  - ← LLANTAS (REFERENCIA)
- **Operaciones**: SELECT (2x)

**OBSERVA** (Observaciones predefinidas)
- **Descripción**: Catálogo de observaciones estándar
- **Campos Clave**:
  - CODIGO (PK): Código de observación
  - DETALLE: Texto de la observación
- **Relaciones**: 
  - Referencias en HISTORIA, LLANTAS
- **Operaciones**: SELECT (3x)

**CLASES** (Clases de vehículos)
- **Descripción**: Clasificación de vehículos (Bus, Camión, Camioneta, etc.)
- **Campos Clave**:
  - CODIGO (PK): Código de clase
  - NOMBRE: Nombre de la clase
  - DESCRIPCION: Descripción
  - NUM_LLANTAS: Número típico de llantas
  - POSICIONES: Configuración de posiciones
- **Relaciones**: 
  - ← VEHICULOS_LLANTAS (CLASE)
- **Operaciones**: SELECT (LOV frecuente)

#### 3.1.3. Tablas de Control y Proceso

**INTERMEDIO** (Tabla temporal)
- **Descripción**: Tabla temporal para procesos batch y cálculos
- **Campos Clave**:
  - LLANTA
  - GRUPO
  - FLAG: Indicador de proceso
  - DATO1, DATO2: Datos temporales
- **Operaciones**: INSERT (2x), SELECT (4x)
- **Notas**: Se usa para procesos de cálculo y migración de datos

**VIDAK** (Vida de llantas - cálculos)
- **Descripción**: Tabla de cálculo de vida útil de llantas
- **Campos Clave**: (Por determinar - requiere análisis de procedimientos)
- **Operaciones**: INSERT (4x), SELECT (3x)
- **Notas**: Probablemente usada en reportes de análisis

**INVENTARIO** (Control de inventario)
- **Descripción**: Vista o tabla de control de inventario actual
- **Campos Clave**: (Similar a LLANTAS con estado filtrado)
- **Operaciones**: SELECT (6x)
- **Notas**: Posiblemente una vista de LLANTAS WHERE ESTADO='I'

**LOCALIZA** (Tabla de localización)
- **Descripción**: Tabla auxiliar para búsqueda rápida de ubicación
- **Campos Clave**: (Por determinar)
- **Operaciones**: SELECT (3x)
- **Notas**: Optimización para consultas de ubicación

**NEUMATICO** (Datos adicionales de neumáticos)
- **Descripción**: Información complementaria de neumáticos
- **Campos Clave**: (Por determinar)
- **Operaciones**: SELECT (3x)
- **Notas**: Posible extensión de LLANTAS o FICHATEC

#### 3.1.4. Tablas No Identificadas Completamente

- **HISTORIAN**: Referenciada pero sin detalles completos
- **ACTIVAS**: Posible vista de llantas activas
- **BAJAS**: Posible vista de llantas dadas de baja

### 3.2. Relaciones Inferidas (Diagrama Conceptual)

```
PROVEEDORES_LLANTAS ──┐
                      │
MARCAS_LLANTAS ───────┤
                      │
TIPOS ────────────────┤
                      │
REFERENCIA ───────────┤
                      │
FICHATEC ─────────────┤
                      │
                      ├──→ LLANTAS ←──┐
                      │      │        │
CLASES ───→ VEHICULOS_LLANTAS │      │
                      ↓        ↓      │
                   (instalada) │      │
                               │      │
                               ├──→ HISTORIA
                               │
                               ├──→ MUESTREO
                               │
                               ├──→ KMS_RECORRIDO_LLANTAS
                               │
                               └──→ RETIRADAS

OBSERVA ──────────────────→ HISTORIA
```

**Relaciones Maestro-Detalle:**
1. VEHICULOS_LLANTAS (1) → LLANTAS (N) - Un vehículo tiene múltiples llantas
2. LLANTAS (1) → MUESTREO (N) - Una llanta tiene múltiples muestreos
3. LLANTAS (1) → HISTORIA (N) - Una llanta tiene múltiples eventos
4. LLANTAS (1) → KMS_RECORRIDO_LLANTAS (N) - Una llanta tiene registro por grupo

**Relaciones de Catálogo:**
- Todas las tablas de catálogo (PROVEEDORES_LLANTAS, MARCAS_LLANTAS, TIPOS, REFERENCIA, FICHATEC, OBSERVA, CLASES) tienen relación 1:N con LLANTAS

---

## 4. COMPONENTES TÉCNICOS

### 4.1. Procedimientos Embebidos en Formularios

#### 4.1.1. Procedimientos del Formulario ALFA.FMB

**Procedimientos PL/SQL Locales:**

| Procedimiento | Propósito | Ubicación |
|---------------|-----------|-----------|
| (Múltiples triggers inline) | Lógica de negocio embebida | Triggers de Form, Block, Item |

**Referencias a Biblioteca MILENIO.FMB:**

| Procedimiento | Propósito Inferido | Tipo |
|---------------|-------------------|------|
| NOTAP | Mostrar notificación/alerta (popup) | Mensaje |
| NOTAG | Mostrar notificación (general) | Mensaje |
| NOTAA | Mostrar alerta de advertencia | Mensaje |
| NOTA | Mostrar nota informativa | Mensaje |

**Cursores Identificados:**

```sql
-- Cursor para vehículos
CURSOR vehi IS 
  SELECT * FROM llantas 
  WHERE vehiculo = objeto 
  ORDER BY llanta;

-- Cursor para todas las llantas
CURSOR todas IS 
  SELECT * FROM llantas 
  ORDER BY vehiculo, llanta;

-- Cursor para muestreos de una llanta
CURSOR muestras(dato VARCHAR2) IS 
  SELECT * FROM muestreo 
  WHERE llanta = dato 
  ORDER BY kilom;

-- Cursor para consulta de llantas
CURSOR llan1 IS 
  SELECT * FROM llantas;

-- Cursor para fichas técnicas
CURSOR fic IS 
  SELECT ref FROM fichatec;
```

**Validaciones Complejas Identificadas:**
- Validación de disponibilidad de llanta antes de instalar
- Validación de kilometraje coherente
- Validación de posiciones válidas por clase de vehículo
- Validación de integridad referencial manual

#### 4.1.2. Procedimientos del Formulario MLFR008.FMB

| Procedimiento | Tipo | Descripción |
|---------------|------|-------------|
| P_INICIARFORMA | Inicialización | Configura formulario al inicio |
| P_INICIARTOOLBAR | Inicialización | Configura barra de herramientas |
| VALIDAR | Validación | Valida datos antes de grabar |
| SALVAR | Transacción | Ejecuta grabación de datos |
| LLAMAR_REPORTE | Reporte | Invoca generador de reportes |
| P_AUMENTAS | Utilidad | (Función no clara, posible placeholder) |
| P_CONSECUTIVOS | Utilidad | (Función no clara, posible placeholder) |

**Procedimientos de Biblioteca MILENIO.FMB:**
- NOTAP, NOTAG, NOTAA, NOTA (Mismas funciones de mensajería)

#### 4.1.3. Procedimientos del Formulario MLFR009.FMB

| Procedimiento | Tipo | Descripción |
|---------------|------|-------------|
| P_INICIARFORMA | Inicialización | Configura formulario al inicio |
| P_INICIARTOOLBAR | Inicialización | Configura barra de herramientas |
| VALIDAR | Validación | Valida muestreos antes de grabar |
| SALVAR | Transacción | Graba muestreos y actualiza KMS |
| P_AUMENTAS | Utilidad | (Función no clara) |
| P_CONSECUTIVOS | Utilidad | (Función no clara) |

**Procedimientos de Biblioteca MILENIO.FMB:**
- NOTAP, NOTA, NOTAG, NOTAA

**Lógica Crítica en SALVAR (MLFR009):**
```pseudocode
PROCEDURE SALVAR IS
BEGIN
  FOR cada_llanta IN bloque_llantas LOOP
    -- Validar profundidades ingresadas
    IF profundidades_completas THEN
      -- Obtener KINSTALA
      SELECT KINSTALA INTO v_kinstala 
      FROM LLANTAS WHERE LLANTA = :llanta;
      
      -- Insertar en MUESTREO
      INSERT INTO MUESTREO VALUES (
        :llanta, :grupo, :fecha, :kilometraje,
        :prof_izq, :prof_centro, :prof_der, :presion
      );
      
      -- Actualizar kilómetros recorridos acumulados
      UPDATE KMS_RECORRIDO_LLANTAS 
      SET KMRL_KMSRECORRIDO_NB = KMRL_KMSRECORRIDO_NB + (kms_actual - kms_anterior),
          KMRL_FECHA_DT = SYSDATE
      WHERE KMRL_LLANTA_NB = :llanta
      AND SUBSTR(KMRL_GRUPO_CH,3,1) = SUBSTR(:grupo,3,1);
    END IF;
  END LOOP;
  
  COMMIT;
  NOTA('Muestreos grabados exitosamente');
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    NOTAP('Error al grabar muestreos: ' || SQLERRM);
END;
```

#### 4.1.4. Procedimientos del Formulario MLFR010.FMB

**Procedimientos de Biblioteca MILENIO.FMB:**
- Mismos procedimientos estándar de mensajería

**Lógica Principal:**
- Principalmente consultas (SELECT FROM HISTORIA, OBSERVA)
- Navegación por registros históricos

#### 4.1.5. Procedimientos del Formulario MLFR011.FMB

**Lógica de Retiro:**
```pseudocode
PROCEDURE RETIRAR_LLANTA IS
BEGIN
  -- Verificar que llanta existe y no está instalada
  SELECT COUNT(*) INTO v_count
  FROM HISTORIA WHERE LLANTA = :llanta AND GRUPO = :grupo;
  
  -- Verificar si ya está retirada
  SELECT COUNT(*) INTO v_retirada
  FROM RETIRADAS WHERE LLANTA = :llanta AND GRUPO = :grupo;
  
  IF v_retirada > 0 THEN
    NOTAP('La llanta ya está retirada');
    RAISE FORM_TRIGGER_FAILURE;
  END IF;
  
  -- Registrar en RETIRADAS
  INSERT INTO RETIRADAS (llanta, grupo, fecha_retiro, motivo, ...)
  VALUES (:llanta, :grupo, SYSDATE, :motivo, ...);
  
  -- Actualizar estado en LLANTAS
  UPDATE LLANTAS SET ESTADO = 'R' 
  WHERE LLANTA = :llanta AND GRUPO = :grupo;
  
  -- Registrar en HISTORIA
  INSERT INTO HISTORIA (llanta, grupo, fecha, evento, ...)
  VALUES (:llanta, :grupo, SYSDATE, 'BAJA', ...);
  
  -- Registro en tabla intermedia (para procesos)
  INSERT INTO INTERMEDIO VALUES (:llanta, :grupo, 1, NULL, :fecha);
  
  COMMIT;
  NOTA('Llanta retirada exitosamente');
END;
```

**Lógica de Reactivación:**
```pseudocode
PROCEDURE REACTIVAR_LLANTA IS
BEGIN
  -- Eliminar de RETIRADAS
  DELETE FROM RETIRADAS 
  WHERE LLANTA = :llanta AND GRUPO = :grupo;
  
  -- Actualizar estado en LLANTAS
  UPDATE LLANTAS SET ESTADO = 'I' -- Vuelve a inventario
  WHERE LLANTA = :llanta AND GRUPO = :grupo;
  
  COMMIT;
  NOTA('Llanta reactivada exitosamente');
END;
```

### 4.2. Dependencias de Base de Datos (Procedimientos Almacenados Externos)

**ASUNCIÓN CRÍTICA:** Los formularios analizados (.fmb) no muestran llamadas explícitas a procedimientos almacenados en paquetes PL/SQL externos (PACKAGE.PROCEDURE). La lógica de negocio está mayormente embebida en los triggers de los formularios.

**Posibles Procedimientos Almacenados No Evidentes:**

Basándome en las mejores prácticas de Oracle Forms y la complejidad del sistema, es probable que existan estos procedimientos almacenados que no fueron evidentes en el análisis:

| Procedimiento Probable | Propósito Inferido | Evidencia |
|------------------------|-------------------|-----------|
| GENERAR_CODIGO_LLANTA | Generar código único de llanta | Referencias a códigos secuenciales |
| VALIDAR_INSTALACION_LLANTA | Validación compleja de instalación | Lógica de validación compleja |
| CALCULAR_VIDA_UTIL | Calcular vida útil de llanta | Tabla VIDAK y cálculos |
| ACTUALIZAR_KILOMETRAJE_VEHICULO | Actualizar KMS de vehículo | Coherencia de kilometrajes |
| GENERAR_REPORTE_MUESTREOS | Generación de reportes | LLAMAR_REPORTE en formularios |
| GENERAR_REPORTE_INVENTARIO | Generación de reportes | LLAMAR_REPORTE en formularios |

**Procedimientos de Utilidad (Biblioteca MILENIO):**

Los formularios hacen referencia constante a procedimientos de la biblioteca MILENIO.FMB. Aunque estos NO son procedimientos almacenados de base de datos, sino procedimientos PL/SQL en una biblioteca de Forms, es importante documentarlos:

| Procedimiento | Tipo | Descripción | Usado en |
|---------------|------|-------------|----------|
| P_INICIARFORMA | Inicialización | Configura propiedades iniciales del formulario | Todos los formularios |
| P_INICIARTOOLBAR | Inicialización | Configura botones y estados de toolbar | Todos los formularios |
| VALIDAR | Validación | Ejecuta validaciones pre-grabación | ALFA, MLFR008, MLFR009 |
| SALVAR | Transacción | Ejecuta COMMIT con manejo de errores | ALFA, MLFR008, MLFR009 |
| LLAMAR_REPORTE | Reporte | Invoca Report Server con parámetros | MLFR008, otros |
| NOTAP | Mensaje | Muestra popup con mensaje (ALERT) | Todos |
| NOTAG | Mensaje | Muestra mensaje general | Todos |
| NOTAA | Mensaje | Muestra alerta de advertencia | Todos |
| NOTA | Mensaje | Muestra nota informativa | Todos |
| P_AUMENTAS | Utilidad | (Función no determinada) | Varios |
| P_CONSECUTIVOS | Utilidad | (Función no determinada) | Varios |

**Posibles Paquetes PL/SQL en Base de Datos:**

| Paquete Probable | Propósito Inferido | Justificación |
|------------------|-------------------|---------------|
| PKG_LLANTAS | Operaciones complejas sobre llantas | Centralización de lógica de negocio |
| PKG_MUESTREOS | Procesamiento de muestreos | Cálculos y validaciones complejas |
| PKG_REPORTES | Generación de reportes | LLAMAR_REPORTE debe invocar algo |
| PKG_UTILS | Utilidades generales | Funciones comunes |

**NOTA IMPORTANTE:** Sin acceso a la base de datos o a los archivos .pll (PL/SQL Library) del sistema, no es posible identificar con certeza qué procedimientos almacenados existen. Se recomienda realizar una consulta a:

```sql
-- Para identificar todos los paquetes del esquema
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type IN ('PACKAGE', 'PACKAGE BODY')
AND object_name LIKE '%LLANTA%' OR object_name LIKE '%MILENIO%'
ORDER BY object_name;

-- Para ver procedimientos y funciones standalone
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type IN ('PROCEDURE', 'FUNCTION')
ORDER BY object_name;
```

---

## 5. GAPS DE INFORMACIÓN

### 5.1. Procedimientos Requeridos para Contexto Completo

**CRÍTICO - PROCEDIMIENTOS FALTANTES:**

Los siguientes procedimientos son referenciados en los formularios pero NO fueron encontrados en los archivos .fmb analizados. Se requiere acceso a estos componentes para completar el análisis:

#### 5.1.1. Biblioteca MILENIO.FMB - Procedimientos PL/SQL

| Procedimiento | Formulario que lo Llama | Evento Trigger | Propósito Inferido | Prioridad |
|---------------|------------------------|----------------|-------------------|-----------|
| P_INICIARFORMA | ALFA, MLFR008, MLFR009 | WHEN-NEW-FORM-INSTANCE | Inicialización de formulario | ALTA |
| P_INICIARTOOLBAR | ALFA, MLFR008, MLFR009 | WHEN-NEW-FORM-INSTANCE | Configuración de toolbar | ALTA |
| VALIDAR | ALFA, MLFR008, MLFR009 | WHEN-BUTTON-PRESSED (BT_GRABAR) | Validación pre-grabación | CRÍTICA |
| SALVAR | ALFA, MLFR008, MLFR009 | WHEN-BUTTON-PRESSED (BT_GRABAR) | Transacción de grabación | CRÍTICA |
| LLAMAR_REPORTE | MLFR008 | WHEN-BUTTON-PRESSED (BT_IMPRIMIR) | Invocación de reportes | MEDIA |
| NOTAP | Todos | Múltiples | Mensaje de alerta popup | MEDIA |
| NOTAG | Todos | Múltiples | Mensaje general | MEDIA |
| NOTAA | Todos | Múltiples | Alerta de advertencia | MEDIA |
| NOTA | Todos | Múltiples | Nota informativa | MEDIA |
| P_AUMENTAS | Varios | Múltiples | (Función desconocida) | BAJA |
| P_CONSECUTIVOS | Varios | Múltiples | (Función desconocida) | BAJA |

**NECESIDAD:** Archivo MILENIO.FMB o MILENIO.PLL (biblioteca compilada)

#### 5.1.2. Posibles Paquetes PL/SQL de Base de Datos

| Paquete/Procedimiento Probable | Uso Inferido | Formulario | Evidencia | Prioridad |
|--------------------------------|--------------|------------|-----------|-----------|
| PKG_LLANTAS.GENERAR_CODIGO | Generación de código único | ALFA | Secuencias en inserciones | ALTA |
| PKG_LLANTAS.VALIDAR_INSTALACION | Validaciones complejas | ALFA (MONTAR) | Validaciones en triggers | ALTA |
| PKG_MUESTREOS.VALIDAR_DATOS | Validación de muestreos | MLFR009 | Lógica en VALIDAR | ALTA |
| PKG_MUESTREOS.CALCULAR_PROMEDIOS | Cálculos de promedios | MLFR009 | Fórmulas complejas | MEDIA |
| PKG_REPORTES.GENERAR_REPORTE_VEHICULOS | Reporte de vehículos | MLFR008 | LLAMAR_REPORTE | MEDIA |
| PKG_REPORTES.GENERAR_REPORTE_MUESTREOS | Reporte de muestreos | MLFR009 | LLAMAR_REPORTE | MEDIA |
| PKG_UTILS.VALIDAR_FECHA | Validación de fechas | Varios | Validaciones de fecha | BAJA |

**NECESIDAD:** Script SQL con definición de paquetes o acceso a base de datos

#### 5.1.3. Archivos de Biblioteca (.PLL) Adicionales

| Biblioteca Probable | Contenido Esperado | Evidencia | Prioridad |
|---------------------|-------------------|-----------|-----------|
| MILENIO.PLL | Procedimientos estándar del sistema | Referencias en todos los .fmb | CRÍTICA |
| LLANTAS.PLL | Procedimientos específicos del módulo | Complejidad del dominio | MEDIA |
| REPORTES.PLL | Generación de reportes | LLAMAR_REPORTE | MEDIA |

#### 5.1.4. Reportes Oracle Reports

| Reporte Probable | Formulario | Datos de Entrada | Prioridad |
|------------------|------------|------------------|-----------|
| REP_VEHICULOS.RDF | MLFR008 | PLACA, CLASE | MEDIA |
| REP_MUESTREOS.RDF | MLFR009 | PLACA, FECHA_DESDE, FECHA_HASTA | MEDIA |
| REP_INVENTARIO.RDF | ALFA | ESTADO, PROVEEDOR | MEDIA |
| REP_HISTORIAL_LLANTA.RDF | ALFA | LLANTA | MEDIA |
| REP_ANALISIS_DESGASTE.RDF | MLFR009 | VEHICULO, PERIODO | BAJA |

### 5.2. Información Técnica Adicional Requerida

#### 5.2.1. Esquema de Base de Datos Completo

**NECESIDAD:** Script DDL completo con:
- Definiciones de todas las tablas (CREATE TABLE)
- Constraints (PRIMARY KEY, FOREIGN KEY, CHECK, UNIQUE)
- Índices (CREATE INDEX)
- Secuencias (CREATE SEQUENCE)
- Vistas (CREATE VIEW)
- Sinónimos (CREATE SYNONYM)
- Grants y privilegios

**PRIORIDAD:** ALTA

**JUSTIFICACIÓN:** 
- Confirmar relaciones inferidas
- Identificar constraints no evidentes en formularios
- Entender índices para optimización
- Verificar tipos de datos exactos

#### 5.2.2. Paquetes PL/SQL

**NECESIDAD:** Código fuente de todos los paquetes PL/SQL del esquema LLANTAS

```sql
-- Comando para extraer:
SELECT dbms_metadata.get_ddl('PACKAGE', object_name) 
FROM user_objects 
WHERE object_type = 'PACKAGE';
```

**PRIORIDAD:** ALTA

#### 5.2.3. Triggers de Base de Datos

**NECESIDAD:** Código fuente de triggers a nivel de tabla

```sql
-- Comando para extraer:
SELECT trigger_name, table_name, trigger_type, triggering_event 
FROM user_triggers
WHERE table_name IN ('LLANTAS', 'MUESTREO', 'KMS_RECORRIDO_LLANTAS', 
                     'HISTORIA', 'VEHICULOS_LLANTAS', 'RETIRADAS');
```

**PRIORIDAD:** MEDIA

**JUSTIFICACIÓN:**
- Los triggers de BD pueden tener lógica de negocio crítica
- Pueden existir auditorías automáticas
- Pueden existir validaciones adicionales

#### 5.2.4. Configuración del Sistema

**NECESIDAD:** Archivos de configuración:
- Archivo de configuración de Oracle Forms (formsweb.cfg, default.env)
- Cadenas de conexión
- Configuración de reportes (rwbuilder.conf, rwserver.conf)
- Variables de entorno

**PRIORIDAD:** BAJA

### 5.3. Documentación Faltante

| Documento | Descripción | Impacto en Análisis |
|-----------|-------------|---------------------|
| Manual de Usuario | Guía de uso del sistema | Comprender flujos de trabajo reales |
| Diagramas de Flujo | Procesos de negocio | Validar casos de uso inferidos |
| Diccionario de Datos | Descripción de tablas y campos | Confirmar propósito de cada campo |
| Reglas de Negocio | Validaciones y cálculos | Entender lógica compleja |
| Catálogo de Reportes | Lista de reportes disponibles | Documentar salidas del sistema |
| Matriz de Roles | Permisos por perfil | Diseñar seguridad |

**PRIORIDAD GENERAL:** MEDIA

### 5.4. Consultas SQL Recomendadas para Completar el Análisis

Si se obtiene acceso a la base de datos, ejecutar:

```sql
-- 1. Ver todas las tablas del esquema
SELECT table_name, num_rows, last_analyzed 
FROM user_tables 
ORDER BY table_name;

-- 2. Ver todas las relaciones (Foreign Keys)
SELECT 
  a.constraint_name,
  a.table_name,
  a.column_name,
  c_pk.table_name r_table_name,
  c_pk.constraint_name r_pk
FROM user_cons_columns a
JOIN user_constraints c ON a.constraint_name = c.constraint_name
JOIN user_constraints c_pk ON c.r_constraint_name = c_pk.constraint_name
WHERE c.constraint_type = 'R'
ORDER BY a.table_name;

-- 3. Ver estructura completa de una tabla
SELECT 
  column_name,
  data_type,
  data_length,
  nullable,
  data_default
FROM user_tab_columns
WHERE table_name = 'LLANTAS'
ORDER BY column_id;

-- 4. Ver indices
SELECT 
  index_name,
  table_name,
  uniqueness,
  column_name,
  column_position
FROM user_ind_columns
WHERE table_name IN ('LLANTAS', 'MUESTREO', 'KMS_RECORRIDO_LLANTAS')
ORDER BY table_name, index_name, column_position;

-- 5. Ver secuencias
SELECT sequence_name, last_number 
FROM user_sequences;

-- 6. Ver triggers
SELECT trigger_name, table_name, trigger_type, triggering_event
FROM user_triggers
ORDER BY table_name, trigger_name;

-- 7. Ver paquetes
SELECT object_name, object_type, status, last_ddl_time
FROM user_objects
WHERE object_type IN ('PACKAGE', 'PACKAGE BODY')
ORDER BY object_name;

-- 8. Ver vistas
SELECT view_name, text_length
FROM user_views;
```

---

## 6. RECOMENDACIONES

### 6.1. Priorización de Información Faltante

**FASE 1 - CRÍTICO (Requerido para continuar análisis):**
1. Archivo MILENIO.FMB o MILENIO.PLL (biblioteca de procedimientos)
2. Script DDL completo del esquema de base de datos
3. Código fuente de paquetes PL/SQL (si existen)

**FASE 2 - ALTA (Requerido para diseño completo):**
4. Definiciones de triggers de base de datos
5. Documentación de reglas de negocio
6. Manual de usuario o guía de procesos

**FASE 3 - MEDIA (Requerido para implementación):**
7. Archivos de reportes Oracle Reports (.RDF)
8. Configuración del sistema (archivos .cfg, .env)
9. Diagramas de flujo de procesos

**FASE 4 - BAJA (Nice to have):**
10. Catálogo de códigos de observaciones
11. Matriz de permisos por rol
12. Diccionario de datos histórico

### 6.2. Áreas que Requieren Clarificación

#### 6.2.1. Proceso de Rotación de Llantas

**PREGUNTA:** ¿Cómo se maneja el cambio de posición de una llanta dentro del mismo vehículo (rotación)?

**CONTEXTO:** El formulario ALFA tiene bloques para instalación y desmontaje, pero no se identificó claramente el proceso de rotación.

**ASUNCIÓN:** Probablemente se realiza mediante:
1. Actualizar campo POSICION en tabla LLANTAS
2. Registrar evento en HISTORIA
3. Mantener mismos valores de VEHICULO y GRUPO

**REQUIERE VALIDACIÓN:** ¿Existe un formulario específico para rotaciones?

#### 6.2.2. Gestión de Grupos de Llantas

**PREGUNTA:** ¿Qué representa exactamente el campo GRUPO en la tabla LLANTAS?

**CONTEXTO:** El campo GRUPO aparece en múltiples tablas (LLANTAS, MUESTREO, KMS_RECORRIDO_LLANTAS) pero su significado no es completamente claro.

**ASUNCIONES POSIBLES:**
1. Grupo por eje del vehículo (eje delantero, eje trasero)
2. Grupo por vida de la llanta (nueva, primera reencauchada, segunda reencauchada)
3. Grupo por tipo de uso (estándar, repuesto)
4. Combinación de los anteriores (ej: "D1" = Delantero, Primera vida)

**REQUIERE VALIDACIÓN:** Definición exacta del concepto GRUPO y su estructura

#### 6.2.3. Cálculo de Kilometraje Acumulado

**PREGUNTA:** ¿Cómo se sincroniza el kilometraje de la llanta con el del vehículo?

**CONTEXTO:** Existen varias tablas relacionadas con kilometraje:
- VEHICULOS_LLANTAS (KILOMINI, KILOMACT)
- LLANTAS (KINSTALA)
- MUESTREO (KILOM)
- KMS_RECORRIDO_LLANTAS (KMRL_KMSRECORRIDO_NB)

**ASUNCIÓN:** 
- KINSTALA guarda el KMS del vehículo al momento de instalación
- KMS_RECORRIDO_LLANTAS acumula diferencias entre muestreos
- Fórmula: KMS_LLANTA = Σ(MUESTREO.KILOM[n] - MUESTREO.KILOM[n-1])

**REQUIERE VALIDACIÓN:** Lógica exacta de cálculo y sincronización

#### 6.2.4. Proceso de Reencauche

**PREGUNTA:** ¿El sistema maneja el proceso de reencauche de llantas?

**CONTEXTO:** Tablas VIDAK y referencias a "VIDA" sugieren control de vidas de llantas.

**ASUNCIÓN:** Posible flujo:
1. Llanta llega a profundidad mínima
2. Se desmonta y envía a reencauche
3. Se registra como nueva vida (GRUPO cambia?)
4. Se reintegra al inventario
5. Se mantiene historial completo

**REQUIERE VALIDACIÓN:** ¿Existe formulario de reencauche? ¿Cómo se identifica cada vida?

#### 6.2.5. Integración con Otros Módulos

**PREGUNTA:** ¿Cómo se integra el módulo de Llantas con otros módulos de MILENIO?

**CONTEXTO:** Los formularios hacen referencia a MILENIO como sistema base.

**ASUNCIONES:**
- Posible integración con Contabilidad (VALOR de llantas)
- Posible integración con Compras (PROVEEDOR, adquisiciones)
- Posible integración con Mantenimiento (alertas, programación)
- Posible integración con Costos (análisis de costo por kilómetro)

**REQUIERE VALIDACIÓN:** Identificar todos los puntos de integración

### 6.3. Supuestos Realizados Durante el Análisis

#### Supuestos de Datos

| Supuesto | Justificación | Riesgo |
|----------|---------------|--------|
| LLANTA es un código numérico secuencial único | Uso como PK en todas las tablas | BAJO |
| GRUPO tiene máximo 3 caracteres | Uso de SUBSTR(GRUPO,3,1) en queries | MEDIO |
| Estado de llanta es: A=Activa, B=Baja, I=Inventario | Nomenclatura común en sistemas legacy | BAJO |
| Profundidades se miden en milímetros | Estándar de la industria | BAJO |
| Presión se mide en PSI | Estándar en contexto latinoamericano | BAJO |
| Kilometraje en kilómetros (no millas) | Contexto del sistema | BAJO |

#### Supuestos Funcionales

| Supuesto | Justificación | Riesgo |
|----------|---------------|--------|
| Una llanta no puede estar instalada en dos vehículos simultáneamente | Lógica de negocio básica | BAJO |
| Los muestreos se registran para todas las llantas del vehículo simultáneamente | Proceso lógico de inspección | MEDIO |
| Las profundidades no pueden aumentar entre muestreos | Desgaste natural | BAJO |
| El proceso de retiro es reversible | Existe DELETE FROM RETIRADAS | MEDIO |
| Los reportes se generan vía Oracle Reports | LLAMAR_REPORTE y arquitectura Forms/Reports | BAJO |

#### Supuestos Técnicos

| Supuesto | Justificación | Riesgo |
|----------|---------------|--------|
| Oracle Forms versión 6 (FORMS4C en archivos) | Referencias en código | BAJO |
| Base de datos Oracle 8i o superior | Sintaxis SQL y características | BAJO |
| Sistema en ambiente Windows | Rutas tipo F:\MILENIO\LLANTAS\FORMAS\ | BAJO |
| Codificación de caracteres ISO-8859-1 | Acentos y ñ en español | MEDIO |
| No hay autenticación integrada (usuarios Forms) | No se evidencia uso de usuarios DB | ALTO |

### 6.4. Siguientes Pasos Recomendados

#### Para el Cliente

1. **Proporcionar archivos faltantes:**
   - MILENIO.FMB o MILENIO.PLL
   - Scripts DDL de base de datos
   - Archivos .RDF de reportes (si existen)

2. **Facilitar acceso temporal a base de datos de desarrollo/pruebas:**
   - Para ejecutar consultas de metadatos
   - Para extraer definiciones de paquetes y triggers
   - Para verificar datos de ejemplo

3. **Proporcionar documentación existente:**
   - Manuales de usuario
   - Guías de procesos
   - Especificaciones funcionales (si existen)
   - Diccionario de datos

4. **Asignar usuario experto del negocio:**
   - Para validar casos de uso inferidos
   - Para aclarar conceptos de dominio (GRUPO, tipos de evento, etc.)
   - Para revisar flujos de trabajo identificados

#### Para el Análisis Técnico

1. **Análisis de Procedimientos MILENIO.FMB:**
   - Reverse engineering de biblioteca
   - Documentación de procedimientos estándar
   - Identificación de dependencias

2. **Modelado Completo de Base de Datos:**
   - Generar diagrama ER completo
   - Documentar todas las relaciones
   - Identificar índices y optimizaciones

3. **Análisis de Integración:**
   - Identificar todos los puntos de integración con otros módulos
   - Documentar flujos de datos entre módulos
   - Mapear dependencias externas

4. **Documentación de Procesos de Negocio:**
   - Crear diagramas de flujo detallados
   - Documentar reglas de validación
   - Crear matriz de permisos necesaria

#### Para Migración/Modernización (si aplica)

1. **Evaluar Tecnologías Objetivo:**
   - .NET / .NET Core
   - PostgreSQL
   - Arquitectura N-Layer

2. **Diseñar Arquitectura Nueva:**
   - API REST para lógica de negocio
   - Frontend moderno (Web, Desktop)
   - Capa de datos con ORM

3. **Plan de Migración de Datos:**
   - Estrategia de migración de tablas
   - Transformaciones necesarias
   - Validación de integridad

4. **Plan de Capacitación:**
   - Identificar cambios en UI/UX
   - Preparar material de capacitación
   - Definir estrategia de rollout

---

## 7. ANEXOS

### 7.1. Glosario de Términos

| Término | Definición |
|---------|------------|
| **Llanta/Neumático** | Elemento de caucho que se instala en las ruedas de los vehículos |
| **Profundidad de Banda** | Medida en milímetros del grosor de la banda de rodadura del neumático |
| **Muestreo** | Medición periódica de profundidades de banda para evaluar desgaste |
| **Reencauche** | Proceso de renovación de la banda de rodadura de un neumático |
| **Vida** | Número de veces que un neumático ha sido reencauchado (vida 1, vida 2, etc.) |
| **Grupo** | Clasificación de llanta según posición y/o vida |
| **KINSTALA** | Kilometraje del vehículo al momento de instalar la llanta |
| **KMS Acumulado** | Kilómetros totales recorridos por la llanta desde su instalación |
| **Ficha Técnica** | Especificaciones técnicas del modelo de neumático |
| **Rotación** | Cambio de posición de llanta dentro del mismo vehículo |
| **Baja** | Retiro definitivo de llanta del servicio activo |
| **Estado** | Situación actual de la llanta (Activa, Inventario, Retirada) |

### 7.2. Acrónimos

| Acrónimo | Significado |
|----------|-------------|
| **PRD** | Product Requirements Document (Documento de Requisitos del Producto) |
| **PK** | Primary Key (Llave Primaria) |
| **FK** | Foreign Key (Llave Foránea) |
| **LOV** | List of Values (Lista de Valores) |
| **DDL** | Data Definition Language |
| **DML** | Data Manipulation Language |
| **UC** | Use Case (Caso de Uso) |
| **KMS** | Kilómetros |
| **NB** | Number (campo numérico en nomenclatura de Forms) |
| **CH** | Character (campo alfanumérico) |
| **DT** | Date (campo de fecha) |
| **V2** | Versión 2 o campo calculado/virtual |
| **FRM** | Forms - prefijo para campos del formulario |
| **PSI** | Pounds per Square Inch (libras por pulgada cuadrada - medida de presión) |

### 7.3. Convenciones de Nomenclatura Identificadas

**Tablas:**
- Nombres en plural (LLANTAS, MUESTREOS)
- Todo en mayúsculas
- Separación por guión bajo cuando necesario

**Campos:**
- Prefijo del módulo cuando es calculado (FRM_)
- Sufijo de tipo (_NB=Number, _CH=Character, _DT=Date)
- Todo en mayúsculas
- Separación por guión bajo

**Bloques en Formularios:**
- Nombres en mayúsculas
- Generalmente coinciden con tabla base o función
- Sin prefijos especiales

**Triggers:**
- Formato: WHEN-[EVENTO] ([NIVEL].[OBJETO])
- Ejemplo: WHEN-BUTTON-PRESSED (TOOLBAR.BT_GRABAR)
- Nivel puede ser: Form, Block, Item

---

## 8. SOLICITUD DE INFORMACIÓN ADICIONAL

### 8.1. Archivos Requeridos (Por Prioridad)

#### PRIORIDAD CRÍTICA

1. **MILENIO.FMB** o **MILENIO.PLL**
   - Contiene: Procedimientos estándar del sistema (VALIDAR, SALVAR, NOTAP, etc.)
   - Sin este archivo: No se puede entender lógica de validación y grabación
   - Ubicación probable: F:\MILENIO\LLANTAS\FORMAS\ o similar

2. **Script DDL del esquema LLANTAS**
   - Contiene: CREATE TABLE de todas las tablas
   - Alternativa: Ejecutar consultas de metadatos en base de datos
   - Formato: Archivo .SQL

3. **Paquetes PL/SQL del esquema LLANTAS** (si existen)
   - Contiene: Lógica de negocio en base de datos
   - Extracción: Via DBMS_METADATA.GET_DDL o export de Developer
   - Formato: Archivo .SQL

#### PRIORIDAD ALTA

4. **Triggers de Base de Datos**
   - Tablas de interés: LLANTAS, MUESTREO, KMS_RECORRIDO_LLANTAS, HISTORIA, VEHICULOS_LLANTAS, RETIRADAS
   - Contiene: Lógica automática de auditoría, validaciones, cálculos
   - Formato: Archivo .SQL

5. **Archivos de Reportes** (Si aplica):
   - REP_VEHICULOS.RDF
   - REP_MUESTREOS.RDF
   - REP_INVENTARIO.RDF
   - REP_HISTORIAL_LLANTA.RDF
   - Otros reportes del módulo de llantas

6. **Documentación de Procesos de Negocio**
   - Manual de usuario
   - Guías de procedimientos
   - Flujos de trabajo documentados

#### PRIORIDAD MEDIA

7. **Diccionario de Datos** (Si existe)
   - Descripción de cada tabla
   - Descripción de cada campo
   - Reglas de negocio documentadas

8. **Configuración del Sistema**
   - formsweb.cfg
   - default.env
   - Cadenas de conexión
   - Variables de entorno

9. **Otros Formularios del Módulo** (Si existen):
   - Formularios de administración de catálogos
   - Formularios de reportería
   - Formularios de configuración

#### PRIORIDAD BAJA

10. **Matriz de Roles y Permisos**
    - Definición de roles de usuario
    - Permisos por formulario
    - Restricciones de acceso

### 8.2. Acceso a Base de Datos (Temporal)

Para completar el análisis de manera óptima, se solicita:

**Acceso de Solo Lectura a Base de Datos de Desarrollo/Pruebas:**

```sql
-- Permisos mínimos requeridos:
GRANT SELECT ON USER_TABLES TO usuario_analisis;
GRANT SELECT ON USER_TAB_COLUMNS TO usuario_analisis;
GRANT SELECT ON USER_CONSTRAINTS TO usuario_analisis;
GRANT SELECT ON USER_CONS_COLUMNS TO usuario_analisis;
GRANT SELECT ON USER_INDEXES TO usuario_analisis;
GRANT SELECT ON USER_IND_COLUMNS TO usuario_analisis;
GRANT SELECT ON USER_TRIGGERS TO usuario_analisis;
GRANT SELECT ON USER_OBJECTS TO usuario_analisis;
GRANT SELECT ON USER_VIEWS TO usuario_analisis;
GRANT SELECT ON USER_SEQUENCES TO usuario_analisis;

-- Acceso a tablas de datos (para entender estructura y contenido):
GRANT SELECT ON LLANTAS TO usuario_analisis;
GRANT SELECT ON VEHICULOS_LLANTAS TO usuario_analisis;
GRANT SELECT ON MUESTREO TO usuario_analisis;
GRANT SELECT ON KMS_RECORRIDO_LLANTAS TO usuario_analisis;
GRANT SELECT ON HISTORIA TO usuario_analisis;
GRANT SELECT ON RETIRADAS TO usuario_analisis;
GRANT SELECT ON FICHATEC TO usuario_analisis;
GRANT SELECT ON PROVEEDORES_LLANTAS TO usuario_analisis;
GRANT SELECT ON MARCAS_LLANTAS TO usuario_analisis;
GRANT SELECT ON TIPOS TO usuario_analisis;
GRANT SELECT ON REFERENCIA TO usuario_analisis;
GRANT SELECT ON OBSERVA TO usuario_analisis;
GRANT SELECT ON CLASES TO usuario_analisis;
-- [Otras tablas identificadas]
```

**Duración del Acceso:** 1-2 semanas para análisis completo

**Propósito:**
1. Extraer metadatos de la base de datos
2. Verificar relaciones entre tablas
3. Entender estructura y tipos de datos
4. Consultar datos de ejemplo para validar suposiciones
5. Identificar paquetes, triggers y procedimientos almacenados

### 8.3. Sesión de Validación con Experto del Negocio

Se solicita agendar una o más sesiones con un usuario experto que pueda:

**Validar:**
- Casos de uso identificados
- Flujos de trabajo inferidos
- Reglas de negocio supuestas

**Aclarar:**
- Significado exacto del campo GRUPO
- Proceso de reencauche (si aplica)
- Proceso de rotación de llantas
- Criterios de decisión para dar de baja
- Frecuencia típica de muestreos
- Integración con otros módulos

**Demostrar:**
- Uso real del sistema en ambiente productivo
- Casos extremos o excepciones
- Reportes más utilizados

**Duración Estimada:** 4-8 horas (puede dividirse en sesiones de 2 horas)

---

## 9. CONCLUSIONES

### 9.1. Resumen del Sistema Identificado

El Sistema de Control y Gestión de Llantas analizado es una aplicación empresarial robusta construida sobre Oracle Forms 6.0 y Oracle Database. El sistema gestiona integralmente el ciclo de vida de neumáticos en una flota vehicular, desde la adquisición hasta la baja definitiva.

**Componentes Principales Identificados:**
- 5 formularios Oracle Forms (.fmb)
- 17+ tablas de base de datos
- 10+ procedimientos PL/SQL embebidos
- Múltiples catálogos maestros
- Biblioteca de procedimientos compartida (MILENIO.FMB)

**Funcionalidades Core:**
- Gestión de inventario de llantas
- Control de vehículos de la flota
- Instalación y desmontaje de neumáticos
- Registro de muestreos de desgaste
- Seguimiento de kilometraje acumulado
- Historial completo de eventos
- Gestión de bajas y retiradas
- Administración de catálogos maestros

**Volumen Operativo Estimado (basado en complejidad):**
- Tamaño de flota: 50-500 vehículos
- Inventario de llantas: 500-5000 unidades
- Muestreos: Mensuales o bimensuales
- Historiales: 3-5 años de registros

### 9.2. Nivel de Completitud del Análisis

**Información Completa (90-100%):**
- ✅ Estructura de formularios
- ✅ Bloques de datos y su propósito
- ✅ Campos principales de cada formulario
- ✅ Triggers identificados
- ✅ Flujos de trabajo básicos
- ✅ Tablas principales y su uso
- ✅ Relaciones entre tablas (inferidas)
- ✅ Validaciones básicas

**Información Parcial (50-80%):**
- ⚠️ Lógica de procedimientos embebidos (referencias sin código)
- ⚠️ Reglas de negocio complejas
- ⚠️ Cálculos específicos
- ⚠️ Integración con otros módulos
- ⚠️ Proceso de reencauche (si existe)
- ⚠️ Generación de reportes

**Información Faltante (0-30%):**
- ❌ Código fuente de MILENIO.FMB/PLL
- ❌ Procedimientos almacenados en BD (si existen)
- ❌ Triggers de base de datos
- ❌ DDL completo del esquema
- ❌ Archivos de reportes (.RDF)
- ❌ Documentación oficial

### 9.3. Viabilidad de Migración/Modernización

**Factores Favorables:**
- ✅ Arquitectura relativamente simple (Forms + BD)
- ✅ Lógica de negocio bien encapsulada en formularios
- ✅ Modelo de datos normalizado
- ✅ Dominio de negocio bien definido
- ✅ No se identificaron dependencias complejas externas

**Factores de Riesgo:**
- ⚠️ Lógica en biblioteca MILENIO puede ser compleja
- ⚠️ Posibles procedimientos almacenados no documentados
- ⚠️ Integración con otros módulos no completamente clara
- ⚠️ Usuarios acostumbrados a interfaz Oracle Forms

**Recomendación:**
- **Migración a .NET 9.0 + PostgreSQL 17: VIABLE**
- Complejidad Estimada: **MEDIA**
- Duración Estimada: **4-6 meses** (con equipo de 2-3 desarrolladores)
- Fase 1: Análisis detallado (1 mes)
- Fase 2: Diseño arquitectura nueva (0.5 meses)
- Fase 3: Desarrollo iterativo (2.5-3.5 meses)
- Fase 4: Testing y migración de datos (1 mes)

**Stack Tecnológico Recomendado:**
- **Backend:** .NET 9.0 Web API + Dapper
- **Base de Datos:** PostgreSQL 17
- **Frontend:** Blazor WebAssembly o Aplicación de Escritorio (.NET MAUI)
- **ORM:** Dapper (como está en las preferencias del usuario)
- **Validación:** FluentValidation
- **Logging:** Serilog
- **Arquitectura:** N-Layer según preferencias documentadas

### 9.4. Próximos Pasos Inmediatos

1. **Obtener MILENIO.FMB o MILENIO.PLL** → Desbloquea análisis de lógica de validación y grabación
2. **Obtener DDL del esquema** → Confirma modelo de datos y relaciones
3. **Acceso temporal a BD** → Valida suposiciones y completa metadatos
4. **Sesión con experto de negocio** → Valida casos de uso y aclara ambigüedades

### 9.5. Valor del Análisis Actual

Este PRD proporciona:
- ✅ **Visión 360° del sistema** a partir de archivos .fmb
- ✅ **Base sólida para planificación** de migración
- ✅ **Identificación clara de gaps** de información
- ✅ **Casos de uso documentados** para validación
- ✅ **Modelo de datos inferido** para diseño
- ✅ **Roadmap claro** de siguientes pasos

**Nivel de Confianza del Análisis: 75%**

Con la información adicional solicitada, el nivel de confianza puede aumentar a **95%+**, suficiente para proceder con diseño e implementación con seguridad.

---

**FIN DEL DOCUMENTO**

---

## CONTROL DE CAMBIOS

| Versión | Fecha | Autor | Cambios |
|---------|-------|-------|---------|
| 1.0 | 2026-01-20 | Análisis Técnico | Creación inicial del documento basado en análisis de archivos .fmb |

---

## APROBACIONES

| Rol | Nombre | Firma | Fecha |
|-----|--------|-------|-------|
| Cliente - Responsable del Sistema | | | |
| Analista de Negocio | | | |
| Arquitecto de Software | | | |
| Líder Técnico | | | |
