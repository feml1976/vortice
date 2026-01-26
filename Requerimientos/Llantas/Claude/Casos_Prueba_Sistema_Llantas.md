# CASOS DE PRUEBA DETALLADOS
## SISTEMA DE GESTIÓN DE LLANTAS

---

**Versión:** 1.0  
**Fecha:** 20 de Enero de 2026  
**Nivel de Pruebas:** Funcionales, Integración, Performance

---

## 📋 ÍNDICE

1. [Introducción](#1-introducción)
2. [Módulo: Gestión de Vehículos](#2-módulo-gestión-de-vehículos)
3. [Módulo: Montaje de Llantas](#3-módulo-montaje-de-llantas)
4. [Módulo: Registro de Muestreo](#4-módulo-registro-de-muestreo)
5. [Módulo: Desmontaje y Gestión de Bajas](#5-módulo-desmontaje-y-gestión-de-bajas)
6. [Módulo: Reportes y Consultas](#6-módulo-reportes-y-consultas)
7. [Pruebas de Integración](#7-pruebas-de-integración)
8. [Pruebas de Performance](#8-pruebas-de-performance)
9. [Pruebas de Seguridad](#9-pruebas-de-seguridad)

---

## 1. INTRODUCCIÓN

### 1.1 Convenciones
**Estado de Prueba:**
- ✅ Aprobado
- ❌ Fallado
- ⏸️ Pendiente
- 🔄 En ejecución

**Prioridad:**
- 🔴 Alta (Crítico para el negocio)
- 🟡 Media (Importante pero no bloqueante)
- 🟢 Baja (Mejora o edge case)

---

## 2. MÓDULO: GESTIÓN DE VEHÍCULOS

### TC-VEH-001: Crear Vehículo Exitosamente
**Prioridad:** 🔴 Alta  
**Tipo:** Funcional  
**Módulo:** Gestión de Vehículos

**Precondiciones:**
- Usuario autenticado con permisos de gestión de vehículos
- Catálogo de clases de vehículos configurado

**Datos de Entrada:**
```json
{
  "placa": "XYZ123",
  "claseId": 1,
  "marca": "Freightliner",
  "modelo": 2020,
  "kilometrajeInicial": 0,
  "kilometrajeActual": 0,
  "estado": "ACTIVO",
  "operando": true
}
```

**Pasos:**
1. Navegar a módulo "Gestión de Vehículos"
2. Click en botón "Nuevo Vehículo"
3. Completar formulario con datos de entrada
4. Click en "Guardar"

**Resultado Esperado:**
- ✅ Vehículo creado exitosamente
- ✅ Mensaje de confirmación: "Vehículo XYZ123 registrado correctamente"
- ✅ Vehículo aparece en lista de vehículos
- ✅ Registro insertado en tabla `vehiculos_llantas`

**Validaciones:**
```sql
-- Verificar que el vehículo fue creado
SELECT * FROM vehiculos_llantas WHERE placa = 'XYZ123';

-- Resultado esperado: 1 fila con los datos correctos
```

**Criterios de Aceptación:**
- Placa debe ser única en el sistema
- Modelo >= 1970
- Kilometraje actual >= kilometraje inicial
- Clase debe existir en catálogo

---

### TC-VEH-002: Validar Placa Duplicada
**Prioridad:** 🔴 Alta  
**Tipo:** Validación Negativa

**Precondiciones:**
- Vehículo con placa "XYZ123" ya existe en el sistema

**Datos de Entrada:**
```json
{
  "placa": "XYZ123",
  "claseId": 1,
  "marca": "Kenworth",
  "modelo": 2021
}
```

**Pasos:**
1. Intentar crear vehículo con placa duplicada

**Resultado Esperado:**
- ❌ Error de validación
- ❌ Mensaje: "La placa XYZ123 ya está registrada en el sistema"
- ❌ No se crea registro duplicado en BD

**Query de Validación:**
```sql
-- Debe retornar solo 1 registro (el original)
SELECT COUNT(*) FROM vehiculos_llantas WHERE placa = 'XYZ123';
```

---

### TC-VEH-003: Actualizar Kilometraje de Vehículo
**Prioridad:** 🔴 Alta  
**Tipo:** Funcional

**Precondiciones:**
- Vehículo XYZ123 existe con kilometraje actual = 100,000 km

**Datos de Entrada:**
```json
{
  "placa": "XYZ123",
  "nuevoKilometraje": 105000
}
```

**Pasos:**
1. Seleccionar vehículo XYZ123
2. Actualizar campo kilometraje actual
3. Guardar cambios

**Resultado Esperado:**
- ✅ Kilometraje actualizado correctamente
- ✅ Validación: Nuevo kilometraje >= kilometraje anterior

**Validación Negativa:**
- ❌ No permitir kilometraje < kilometraje actual
- ❌ No permitir kilometraje < kilometraje inicial

---

## 3. MÓDULO: MONTAJE DE LLANTAS

### TC-MON-001: Montar Llanta desde Inventario
**Prioridad:** 🔴 Alta  
**Tipo:** Funcional (Flujo Completo)

**Precondiciones:**
- Vehículo XYZ123 activo con posición 1 disponible
- Llanta LL-00543 en inventario
- Usuario con permisos de montaje

**Datos de Entrada:**
```json
{
  "llantaId": {
    "numeroLlanta": "LL-00543",
    "grupo": "000"
  },
  "vehiculo": "XYZ123",
  "posicion": 1,
  "kilometrajeInstalacion": 100000,
  "fechaInstalacion": "2026-01-20"
}
```

**Pasos de Ejecución:**
1. Navegar a módulo "Montaje de Llantas"
2. Seleccionar vehículo XYZ123
3. Visualizar esquema de posiciones
4. Click en posición 1 (vacía)
5. Seleccionar llanta LL-00543 del inventario disponible
6. Ingresar kilometraje de instalación: 100,000
7. Confirmar fecha de instalación
8. Click en "Montar Llanta"

**Resultado Esperado:**

**1. Transacción Completa:**
```sql
BEGIN TRANSACTION;

-- 1. Llanta eliminada de inventario
SELECT COUNT(*) FROM inventario 
WHERE llanta = 'LL-00543' AND grupo = '000';
-- Resultado esperado: 0

-- 2. Llanta creada como activa
SELECT * FROM llantas 
WHERE llanta = 'LL-00543' AND grupo = '000';
-- Resultado esperado: 1 fila con vehiculo='XYZ123', posicion=1

-- 3. Registro en historia creado
SELECT * FROM historia 
WHERE llanta = 'LL-00543' AND grupo = '000';
-- Resultado esperado: 1 fila con fechai='2026-01-20'

-- 4. Contador de kilómetros iniciado
SELECT * FROM kms_recorrido_llantas 
WHERE kmrl_llanta_nb = 'LL-00543';
-- Resultado esperado: 1 fila con kmrl_kmsrecorrido_nb = 0

COMMIT;
```

**2. UI:**
- ✅ Mensaje: "Llanta LL-00543 montada exitosamente en posición 1"
- ✅ Posición 1 ahora muestra la llanta con estado visual
- ✅ Llanta ya no aparece en lista de inventario disponible

**Validaciones de Negocio:**
- ✅ Posición 1 estaba vacía antes del montaje
- ✅ Kilometraje >= kilometraje actual del vehículo
- ✅ Fecha >= fecha actual
- ✅ Llanta existía en inventario

---

### TC-MON-002: Validar Posición Ocupada
**Prioridad:** 🔴 Alta  
**Tipo:** Validación Negativa

**Precondiciones:**
- Posición 1 del vehículo XYZ123 ya está ocupada por llanta LL-00892

**Datos de Entrada:**
```json
{
  "llantaId": {"numeroLlanta": "LL-00543", "grupo": "000"},
  "vehiculo": "XYZ123",
  "posicion": 1,
  "kilometrajeInstalacion": 100000
}
```

**Resultado Esperado:**
- ❌ Error de validación
- ❌ Mensaje: "La posición 1 ya está ocupada por la llanta LL-00892"
- ❌ No se ejecuta ninguna operación en BD

**Constraint de BD:**
```sql
-- Debe cumplirse: UK_VEHI_POS (VEHICULO, POSICION)
-- Restricción de unicidad impide dos llantas en misma posición
```

---

### TC-MON-003: Validar Kilometraje Inválido
**Prioridad:** 🔴 Alta  
**Tipo:** Validación de Negocio

**Precondiciones:**
- Vehículo XYZ123 con kilometraje actual = 100,000 km

**Datos de Entrada:**
```json
{
  "kilometrajeInstalacion": 95000  // < kilometraje actual
}
```

**Resultado Esperado:**
- ❌ Error: "Kilometraje de instalación (95,000) no puede ser menor que el kilometraje actual del vehículo (100,000)"
- ❌ Formulario no permite continuar

---

### TC-MON-004: Montar Llanta desde Intermedio
**Prioridad:** 🟡 Media  
**Tipo:** Funcional

**Precondiciones:**
- Llanta LL-01234 en estado INTERMEDIO (desmontada, apta para recircular)
- Posición 5 del vehículo ABC456 disponible

**Flujo:**
Igual que TC-MON-001 pero:
- Llanta viene de tabla `intermedio` en lugar de `inventario`
- Grupo puede ser > '000' (reencauche)

**Resultado Esperado:**
- ✅ Llanta eliminada de `intermedio`
- ✅ Llanta creada en `llantas` (activa)
- ✅ Historia actualizada

---

## 4. MÓDULO: REGISTRO DE MUESTREO

### TC-MUES-001: Registrar Muestreo Completo de Vehículo
**Prioridad:** 🔴 Alta  
**Tipo:** Funcional (Flujo Completo)

**Precondiciones:**
- Vehículo XYZ123 con 10 llantas activas
- Usuario con permisos de muestreo
- Kilometraje actual del vehículo: 105,000 km

**Datos de Entrada (Batch):**
```json
{
  "placa": "XYZ123",
  "kilometraje": 105000,
  "fechaMuestreo": "2026-01-20",
  "muestreos": [
    {
      "llantaId": {"numeroLlanta": "LL-00543", "grupo": "000"},
      "profundidadPI": 8.5,
      "profundidadPC": 8.3,
      "profundidadPD": 8.4,
      "presion": 110
    },
    {
      "llantaId": {"numeroLlanta": "LL-00892", "grupo": "000"},
      "profundidadPI": 2.1,
      "profundidadPC": 1.9,
      "profundidadPD": 2.0,
      "presion": 105
    },
    // ... 8 llantas más
  ]
}
```

**Pasos:**
1. Navegar a "Registro de Muestreo"
2. Seleccionar vehículo XYZ123
3. Sistema muestra grilla con 10 llantas activas
4. Ingresar kilometraje actual: 105,000
5. Para cada llanta, ingresar:
   - Profundidad Interna (PI)
   - Profundidad Central (PC)
   - Profundidad Derecha (PD)
   - Presión (PSI)
6. Sistema calcula automáticamente:
   - Profundidad promedio
   - % Desgaste
   - Color según criticidad
7. Click en "Registrar Muestreo"

**Resultado Esperado:**

**1. Registros en BD:**
```sql
-- Verificar que se crearon 10 registros en muestreo
SELECT COUNT(*) FROM muestreo 
WHERE kilom = 105000 
  AND fecha = '2026-01-20';
-- Resultado: 10

-- Verificar registro en histórico
SELECT COUNT(*) FROM histomues 
WHERE kilom = 105000 
  AND fecha = '2026-01-20';
-- Resultado: 10

-- Verificar actualización de kilómetros recorridos
SELECT kmrl_kmsrecorrido_nb 
FROM kms_recorrido_llantas 
WHERE kmrl_llanta_nb = 'LL-00543';
-- Debe reflejar suma de todos los kms recorridos
```

**2. Alertas Generadas:**
```sql
-- Verificar que se generó alerta para llanta LL-00892 (prof. < 2.1mm)
SELECT * FROM alertas 
WHERE llanta_id = 'LL-00892' 
  AND tipo_alerta = 'DESGASTE_CRITICO'
  AND fecha_generacion = '2026-01-20';
-- Resultado: 1 alerta con prioridad ALTA
```

**3. UI:**
- ✅ Mensaje: "Muestreo registrado para 10 llantas exitosamente"
- ⚠️ Modal con alertas críticas:
  - "ALERTA: Llanta LL-00892 en posición 2 tiene profundidad crítica (2.0mm)"
- ✅ Resumen del muestreo mostrado
- ✅ Opción para descargar reporte PDF

---

### TC-MUES-002: Validar Profundidades Excesivas
**Prioridad:** 🔴 Alta  
**Tipo:** Validación de Negocio

**Precondiciones:**
- Llanta LL-00543 con profundidad inicial PI=15.2mm

**Datos de Entrada:**
```json
{
  "profundidadPI": 16.5  // > profundidad inicial
}
```

**Resultado Esperado:**
- ❌ Error: "La profundidad ingresada (16.5mm) no puede ser mayor que la profundidad inicial (15.2mm)"
- ❌ Campo marcado en rojo
- ❌ No permite continuar

**Validación en Backend:**
```java
@Test
public void testValidarProfundidadExcesiva() {
    // Arrange
    FichaTecnica ficha = new FichaTecnica();
    ficha.setProfundidadInicialPI(15.2);
    
    Muestreo muestreo = new Muestreo();
    muestreo.setProfundidadPI(16.5);
    
    // Act & Assert
    assertThrows(
        BusinessValidationException.class,
        () -> muestreoService.validarProfundidades(muestreo, ficha),
        "Profundidad PI excede valor inicial"
    );
}
```

---

### TC-MUES-003: Detectar Desgaste Irregular
**Prioridad:** 🟡 Media  
**Tipo:** Regla de Negocio

**Precondiciones:**
- Llanta en vehículo con muestreo

**Datos de Entrada:**
```json
{
  "profundidadPI": 10.5,
  "profundidadPC": 8.2,
  "profundidadPD": 7.1  // Diferencia: 10.5 - 7.1 = 3.4mm
}
```

**Regla de Negocio:**
- Diferencia > 2.0mm entre máxima y mínima profundidad = Desgaste irregular

**Resultado Esperado:**
- ⚠️ Alerta generada: "DESGASTE_IRREGULAR"
- ⚠️ Prioridad: MEDIA
- ⚠️ Mensaje: "Diferencia de 3.4mm entre profundidades. Revisar alineación y presión"
- ⚠️ Badge amarillo en UI

---

### TC-MUES-004: Calcular Proyección de Vida Útil
**Prioridad:** 🟡 Media  
**Tipo:** Cálculo de Negocio

**Precondiciones:**
- Llanta LL-00543:
  - Profundidad inicial: 15.2mm
  - Profundidad actual: 8.5mm
  - KMs recorridos: 50,000
  - Ficha técnica: KMs esperados = 120,000

**Cálculo Esperado:**
```
Desgaste = 15.2 - 8.5 = 6.7mm
Tasa_Desgaste = 6.7mm / 50,000km = 0.000134 mm/km
Profundidad_Restante = 8.5 - 1.6 (límite legal) = 6.9mm
KMs_Estimados_Restantes = 6.9 / 0.000134 = 51,493 km
Fecha_Estimada_Reemplazo = Fecha_Actual + (51,493 / 300 km/día promedio) ≈ 172 días
```

**Resultado en UI:**
- ✅ "Vida útil estimada: 51,500 km restantes"
- ✅ "Fecha estimada de reemplazo: ~7 de Julio, 2026"
- ✅ Barra de progreso visual: 49% de vida útil consumida

---

## 5. MÓDULO: DESMONTAJE Y GESTIÓN DE BAJAS

### TC-DESM-001: Desmontar Llanta Exitosamente
**Prioridad:** 🔴 Alta  
**Tipo:** Funcional

**Precondiciones:**
- Llanta LL-00543 montada en posición 1 del vehículo XYZ123
- Kilometraje de instalación: 100,000
- Kilometraje actual del vehículo: 115,000

**Datos de Entrada:**
```json
{
  "llantaId": {"numeroLlanta": "LL-00543", "grupo": "000"},
  "vehiculo": "XYZ123",
  "posicion": 1,
  "kilometrajeRemocion": 115000,
  "fechaRemocion": "2026-01-20",
  "motivoId": 3  // Desgaste normal
}
```

**Pasos:**
1. Seleccionar vehículo XYZ123
2. Click en llanta en posición 1
3. Seleccionar "Desmontar"
4. Ingresar kilometraje de remoción: 115,000
5. Seleccionar motivo: "Desgaste normal"
6. Confirmar desmontaje

**Resultado Esperado:**

**1. Transacción en BD:**
```sql
BEGIN TRANSACTION;

-- 1. Llanta eliminada de activas
SELECT COUNT(*) FROM llantas 
WHERE llanta = 'LL-00543' AND grupo = '000';
-- Resultado: 0

-- 2. Historia actualizada con fecha y km de remoción
SELECT kremueve, fechaf, porque FROM historia 
WHERE llanta = 'LL-00543' AND grupo = '000';
-- Resultado: kremueve=115000, fechaf='2026-01-20', porque=3

-- 3. Llanta movida a intermedio
SELECT * FROM intermedio 
WHERE llanta = 'LL-00543' AND grupo = '000';
-- Resultado: 1 fila

-- 4. Posición liberada
SELECT COUNT(*) FROM llantas 
WHERE vehiculo = 'XYZ123' AND posicion = 1;
-- Resultado: 0

COMMIT;
```

**2. UI:**
- ✅ Mensaje: "Llanta LL-00543 desmontada exitosamente"
- ✅ Posición 1 ahora aparece vacía
- ✅ Llanta disponible en módulo "Intermedio" para evaluación

---

### TC-DESM-002: Validar Kilometraje de Remoción Inválido
**Prioridad:** 🔴 Alta  
**Tipo:** Validación Negativa

**Precondiciones:**
- Llanta instalada en km 100,000

**Datos de Entrada:**
```json
{
  "kilometrajeRemocion": 95000  // < kilometraje instalación
}
```

**Resultado Esperado:**
- ❌ Error: "Kilometraje de remoción (95,000) no puede ser menor que kilometraje de instalación (100,000)"

---

### TC-BAJA-001: Dar de Baja Llanta
**Prioridad:** 🟡 Media  
**Tipo:** Funcional

**Precondiciones:**
- Llanta LL-01234 en estado INTERMEDIO

**Datos de Entrada:**
```json
{
  "llantaId": {"numeroLlanta": "LL-01234", "grupo": "002"},
  "valorResidual": 5000,
  "numeroActa": 1025,
  "fechaBaja": "2026-01-20",
  "autor": "Juan Pérez",
  "motivoId": 15  // Daño irreparable
}
```

**Resultado Esperado:**
```sql
-- 1. Eliminada de intermedio
SELECT COUNT(*) FROM intermedio WHERE llanta = 'LL-01234';
-- Resultado: 0

-- 2. Creada en retiradas
SELECT * FROM retiradas WHERE llanta = 'LL-01234';
-- Resultado: 1 fila con todos los datos

-- 3. Historia mantiene registro completo
SELECT COUNT(*) FROM historia WHERE llanta = 'LL-01234';
-- Resultado: >= 1 (todas las veces que estuvo montada)
```

---

## 6. MÓDULO: REPORTES Y CONSULTAS

### TC-REP-001: Reporte de Llantas Activas
**Prioridad:** 🟡 Media  
**Tipo:** Funcional

**Filtros:**
```json
{
  "claseVehiculo": "Tractocamión",
  "estadoDesgaste": "CRITICO",
  "fechaDesde": "2026-01-01",
  "fechaHasta": "2026-01-31"
}
```

**Resultado Esperado:**
- ✅ Reporte generado en < 3 segundos
- ✅ Datos correctos según filtros
- ✅ Opciones de exportación: PDF, Excel, CSV
- ✅ Columnas incluidas:
  - Vehículo
  - Llanta
  - Posición
  - Profundidad actual
  - % Desgaste
  - KMs recorridos
  - Última fecha de muestreo

---

### TC-REP-002: Dashboard de KPIs
**Prioridad:** 🔴 Alta  
**Tipo:** Performance

**Precondiciones:**
- Base de datos con 10,000 registros históricos

**Requisitos de Performance:**
- ⏱️ Carga inicial del dashboard: < 2 segundos
- ⏱️ Actualización de gráficos: < 500ms
- ⏱️ Uso de caché Redis para KPIs calculados

**Validaciones:**
```javascript
// Test de performance
describe('Dashboard Performance', () => {
  it('debe cargar KPIs principales en menos de 2 segundos', async () => {
    const startTime = Date.now();
    
    const response = await api.get('/api/dashboard/kpis');
    
    const elapsedTime = Date.now() - startTime;
    
    expect(elapsedTime).toBeLessThan(2000);
    expect(response.status).toBe(200);
    expect(response.data).toHaveProperty('totalLlantas');
  });
});
```

---

## 7. PRUEBAS DE INTEGRACIÓN

### TI-001: Flujo Completo de Vida de Llanta
**Prioridad:** 🔴 Alta  
**Tipo:** End-to-End

**Flujo Completo:**
1. Compra de llanta nueva → Inventario
2. Montaje en vehículo → Activa
3. 5 muestreos periódicos
4. Desmontaje → Intermedio
5. Evaluación → Envío a reencauche
6. Retorno de reencauche → Inventario (grupo '001')
7. Segundo montaje → Activa
8. 3 muestreos más
9. Desmontaje final → Intermedio
10. Baja → Retiradas

**Validaciones en Cada Paso:**
- Cambios de estado correctos
- Historia completa registrada
- Kilómetros acumulados correctos
- Alertas generadas apropiadamente

**Tiempo de Ejecución:** ~5 minutos

---

### TI-002: Integridad Transaccional
**Prioridad:** 🔴 Alta  
**Tipo:** Integridad de Datos

**Escenario:** Montaje de llanta con fallo de red

**Pasos:**
1. Iniciar proceso de montaje
2. Simular fallo de red después de eliminar de inventario pero antes de crear en llantas activas

**Resultado Esperado:**
- ✅ ROLLBACK completo de la transacción
- ✅ Llanta permanece en inventario
- ✅ No se crea registro incompleto en llantas ni historia
- ✅ Error manejado gracefully en UI

**Test de Código:**
```java
@Test
@Transactional
public void testRollbackOnFailure() {
    // Arrange
    LlantaId llantaId = new LlantaId("LL-TEST", "000");
    // Mock repository to throw exception
    when(llantaRepository.save(any())).thenThrow(new DataAccessException("Network error"));
    
    // Act & Assert
    assertThrows(TransactionException.class, 
        () -> montarLlantaUseCase.execute(comando));
    
    // Verify llanta still in inventory
    assertTrue(inventarioRepository.existsById(llantaId));
}
```

---

## 8. PRUEBAS DE PERFORMANCE

### TP-001: Carga Concurrente de Muestreos
**Prioridad:** 🟡 Media  
**Tipo:** Carga

**Escenario:**
- 50 usuarios simultáneos registrando muestreos
- Cada usuario registra 10 llantas
- Total: 500 transacciones concurrentes

**Requisitos:**
- ⏱️ Tiempo de respuesta promedio: < 1 segundo
- ⏱️ Tiempo de respuesta percentil 95: < 2 segundos
- ⏱️ Tasa de error: < 0.1%
- 💾 Uso de CPU: < 70%
- 💾 Uso de memoria: < 80%

**Herramienta:** JMeter o Gatling

**Script de Prueba (Gatling):**
```scala
val scn = scenario("Registro de Muestreos")
  .exec(
    http("Registrar Muestreo")
      .post("/api/muestreos/batch")
      .body(StringBody(muestreoJson))
      .check(status.is(200))
      .check(jsonPath("$.success").is("true"))
  )

setUp(
  scn.inject(
    rampUsers(50) during (10 seconds)
  )
).protocols(http.baseUrl("https://api.sistema-llantas.com"))
 .assertions(
    global.responseTime.percentile(95).lt(2000),
    global.successfulRequests.percent.gt(99.9)
  )
```

---

### TP-002: Consulta de Reportes con Gran Volumen
**Prioridad:** 🟡 Media  
**Tipo:** Performance de Queries

**Escenario:**
- Base de datos con 1,000,000 de registros históricos
- Consulta de reporte de llantas activas sin filtros

**Requisitos:**
- ⏱️ Tiempo de respuesta: < 5 segundos
- 💾 Uso eficiente de índices
- 📊 Paginación obligatoria (máximo 100 registros por página)

**Query Optimizada:**
```sql
-- Con índices apropiados
EXPLAIN ANALYZE
SELECT 
    l.vehiculo,
    l.llanta,
    l.grupo,
    l.posicion,
    f.dimension,
    m.profundidad_promedio
FROM llantas l
JOIN fichatec f ON l.ficha = f.codigo
LEFT JOIN LATERAL (
    SELECT (pi + pc + pd) / 3 AS profundidad_promedio
    FROM muestreo
    WHERE llanta = l.llanta AND grupo = l.grupo
    ORDER BY fecha DESC
    LIMIT 1
) m ON true
LIMIT 100 OFFSET 0;

-- Debe usar índices y ejecutar en < 100ms
```

---

## 9. PRUEBAS DE SEGURIDAD

### TS-001: Control de Acceso Basado en Roles
**Prioridad:** 🔴 Alta  
**Tipo:** Seguridad

**Escenarios:**

**1. Usuario sin permisos intenta montar llanta:**
```http
POST /api/llantas/montar
Authorization: Bearer <token_operario_sin_permisos>
```

**Resultado Esperado:**
- ❌ Status: 403 Forbidden
- ❌ Mensaje: "No tiene permisos para realizar esta operación"

**2. Token expirado:**
```http
GET /api/vehiculos
Authorization: Bearer <token_expirado>
```

**Resultado Esperado:**
- ❌ Status: 401 Unauthorized
- ❌ Mensaje: "Token expirado. Por favor, inicie sesión nuevamente"

---

### TS-002: Inyección SQL
**Prioridad:** 🔴 Alta  
**Tipo:** Vulnerabilidad

**Intento de Ataque:**
```http
GET /api/vehiculos?placa=ABC123' OR '1'='1
```

**Resultado Esperado:**
- ✅ Uso de PreparedStatements previene inyección
- ✅ Entrada sanitizada
- ✅ No se ejecuta código malicioso
- ✅ Log de intento de ataque

---

### TS-003: Rate Limiting
**Prioridad:** 🟡 Media  
**Tipo:** Seguridad

**Escenario:**
- Cliente realiza 1000 requests en 1 minuto

**Límite Configurado:**
- 100 requests por minuto por IP

**Resultado Esperado:**
- ✅ Primeros 100 requests: 200 OK
- ❌ Requests 101-1000: 429 Too Many Requests
- ⏱️ Reinicio del límite después de 1 minuto

---

## 10. COBERTURA DE PRUEBAS

### Objetivos de Cobertura

**Unit Tests:**
- Cobertura de código: ≥ 80%
- Cobertura de ramas: ≥ 75%

**Integration Tests:**
- Flujos críticos: 100%
- Flujos secundarios: ≥ 80%

**E2E Tests:**
- Casos de uso principales: 100%

**Herramientas:**
- Java: JaCoCo
- React: Jest + React Testing Library
- E2E: Cypress

---

## 11. EJECUCIÓN Y REPORTE

### Pipeline CI/CD

```yaml
# .gitlab-ci.yml
stages:
  - test
  - integration
  - performance
  - security

unit-tests:
  stage: test
  script:
    - mvn clean test
    - npm test
  coverage: '/Total.*?([0-9]{1,3})%/'
  
integration-tests:
  stage: integration
  script:
    - docker-compose up -d postgres
    - mvn verify -P integration-tests
    
performance-tests:
  stage: performance
  only:
    - develop
    - main
  script:
    - gatling:test
    
security-scan:
  stage: security
  script:
    - sonar-scanner
    - dependency-check
```

### Reporte de Ejecución

**Template de Reporte:**
```markdown
# Reporte de Pruebas - Sprint X

## Resumen Ejecutivo
- Total de casos: 150
- Aprobados: 145
- Fallados: 3
- Pendientes: 2
- Cobertura: 82%

## Casos Fallados
1. TC-MUES-005: Error en cálculo de proyección
   - Severidad: Media
   - Ticket: JIRA-1234

## Métricas de Performance
- Tiempo promedio de respuesta: 450ms
- Percentil 95: 1.2s
- CPU máximo: 65%
```

---

**FIN DEL DOCUMENTO**
