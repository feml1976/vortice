# MÉTRICAS Y KPIS DEL NEGOCIO
## SISTEMA DE GESTIÓN DE LLANTAS

---

**Versión:** 1.0  
**Fecha:** 20 de Enero de 2026

---

## 📊 TABLA DE CONTENIDO

1. [Introducción](#1-introducción)
2. [KPIs Operacionales](#2-kpis-operacionales)
3. [KPIs Financieros](#3-kpis-financieros)
4. [KPIs de Calidad y Rendimiento](#4-kpis-de-calidad-y-rendimiento)
5. [KPIs de Mantenimiento Preventivo](#5-kpis-de-mantenimiento-preventivo)
6. [Dashboards y Visualizaciones](#6-dashboards-y-visualizaciones)
7. [Alertas y Umbrales](#7-alertas-y-umbrales)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito
Este documento define las métricas clave de desempeño (KPIs) que el sistema debe calcular, rastrear y visualizar para apoyar la toma de decisiones en la gestión de llantas.

### 1.2 Objetivos de las Métricas
- Optimizar costos de operación de la flota
- Maximizar la vida útil de las llantas
- Reducir tiempo de inactividad por fallas
- Mejorar la seguridad operacional
- Facilitar la planificación de compras

---

## 2. KPIS OPERACIONALES

### KPI-OP-001: Total de Llantas en el Sistema
**Descripción:** Cantidad total de llantas registradas en todos los estados

**Fórmula:**
```sql
SELECT COUNT(*) 
FROM (
    SELECT llanta, grupo FROM llantas
    UNION
    SELECT llanta, grupo FROM inventario
    UNION
    SELECT llanta, grupo FROM intermedio
    UNION
    SELECT llanta, grupo FROM retiradas
) AS total_llantas;
```

**Desagregación:**
- Por estado (Activas, Inventario, Intermedio, Retiradas)
- Por marca
- Por tipo
- Por clase de vehículo

**Frecuencia de Cálculo:** Tiempo real

**Objetivo:** Mantener visibilidad completa del inventario de llantas

---

### KPI-OP-002: Tasa de Utilización de Llantas
**Descripción:** Porcentaje de llantas activamente montadas vs disponibles

**Fórmula:**
```
Tasa_Utilización = (Llantas_Activas / (Llantas_Activas + Llantas_Inventario + Llantas_Intermedio)) × 100
```

**Cálculo SQL:**
```sql
WITH estados AS (
    SELECT 
        (SELECT COUNT(*) FROM llantas) AS activas,
        (SELECT COUNT(*) FROM inventario) AS inventario,
        (SELECT COUNT(*) FROM intermedio) AS intermedio
)
SELECT 
    activas,
    inventario,
    intermedio,
    ROUND((activas::DECIMAL / (activas + inventario + intermedio)) * 100, 2) AS tasa_utilizacion
FROM estados;
```

**Meta:** ≥ 85%

**Frecuencia de Cálculo:** Diario

**Alertas:**
- < 75%: Revisar exceso de inventario
- > 95%: Riesgo de stock insuficiente

---

### KPI-OP-003: Tiempo Promedio de Rotación de Inventario
**Descripción:** Días promedio que una llanta permanece en inventario antes de ser montada

**Fórmula:**
```
Días_Promedio = AVG(Fecha_Montaje - Fecha_Ingreso_Inventario)
```

**Cálculo SQL:**
```sql
SELECT 
    AVG(EXTRACT(DAY FROM (h.fechai - i.fecha))) AS dias_promedio_inventario
FROM historia h
JOIN inventario i ON h.llanta = i.llanta AND h.grupo = i.grupo
WHERE h.fechai >= CURRENT_DATE - INTERVAL '6 months';
```

**Meta:** < 30 días

**Frecuencia de Cálculo:** Semanal

---

### KPI-OP-004: Cobertura de Inventario
**Descripción:** Días de cobertura del inventario actual basado en consumo histórico

**Fórmula:**
```
Días_Cobertura = (Llantas_Inventario / Consumo_Promedio_Diario)
```

**Cálculo SQL:**
```sql
WITH consumo_mensual AS (
    SELECT 
        DATE_TRUNC('month', fecha) AS mes,
        COUNT(*) AS consumo
    FROM historia
    WHERE grupo = '000' -- Solo llantas nuevas
      AND fecha >= CURRENT_DATE - INTERVAL '6 months'
    GROUP BY DATE_TRUNC('month', fecha)
),
consumo_promedio AS (
    SELECT AVG(consumo) / 30 AS consumo_diario
    FROM consumo_mensual
),
inventario_actual AS (
    SELECT COUNT(*) AS total
    FROM inventario
    WHERE grupo = '000'
)
SELECT 
    inventario_actual.total AS inventario,
    consumo_promedio.consumo_diario,
    ROUND(inventario_actual.total / consumo_promedio.consumo_diario, 1) AS dias_cobertura
FROM inventario_actual, consumo_promedio;
```

**Meta:** 30-60 días

**Frecuencia de Cálculo:** Semanal

**Alertas:**
- < 20 días: Stock crítico, ordenar compra urgente
- > 90 días: Sobreinventario, revisar proyecciones

---

### KPI-OP-005: Tasa de Devoluciones/Reencauches
**Descripción:** Porcentaje de llantas que logran ser reencauchadas al menos una vez

**Fórmula:**
```
Tasa_Reencauche = (Llantas_con_Grupo_>_000 / Total_Llantas_Nuevas) × 100
```

**Cálculo SQL:**
```sql
WITH llantas_nuevas AS (
    SELECT DISTINCT llanta
    FROM historia
    WHERE grupo = '000'
),
llantas_reencauchadas AS (
    SELECT DISTINCT llanta
    FROM historia
    WHERE grupo > '000'
)
SELECT 
    COUNT(*) FILTER (WHERE ln.llanta IS NOT NULL) AS total_nuevas,
    COUNT(*) FILTER (WHERE lr.llanta IS NOT NULL) AS reencauchadas,
    ROUND((COUNT(*) FILTER (WHERE lr.llanta IS NOT NULL)::DECIMAL / 
           COUNT(*) FILTER (WHERE ln.llanta IS NOT NULL)) * 100, 2) AS tasa_reencauche
FROM llantas_nuevas ln
LEFT JOIN llantas_reencauchadas lr ON ln.llanta = lr.llanta;
```

**Meta:** ≥ 60%

**Frecuencia de Cálculo:** Mensual

---

## 3. KPIS FINANCIEROS

### KPI-FIN-001: Costo por Kilómetro Promedio
**Descripción:** Costo promedio por kilómetro recorrido de todas las llantas

**Fórmula:**
```
Costo_KM = (Valor_Inicial + SUM(Valores_Reencauches)) / Kilómetros_Totales
```

**Cálculo SQL:**
```sql
WITH vida_completa AS (
    SELECT 
        h.llanta,
        SUBSTR(h.grupo, 3, 1) AS tipo_llanta,
        SUM(h.kremueve - h.kinstala) AS kms_totales,
        SUM(h.valor + COALESCE(h.valorrn, 0) + COALESCE(h.valorp, 0)) AS costo_total
    FROM historia h
    GROUP BY h.llanta, SUBSTR(h.grupo, 3, 1)
)
SELECT 
    AVG(costo_total / NULLIF(kms_totales, 0)) AS costo_por_km_promedio
FROM vida_completa
WHERE kms_totales > 0;
```

**Meta:** < $0.025 USD/km

**Frecuencia de Cálculo:** Mensual

**Desagregación:**
- Por marca
- Por tipo de llanta
- Por clase de vehículo
- Por posición (direccional vs tracción)

---

### KPI-FIN-002: Inversión Total en Llantas
**Descripción:** Valor total invertido en llantas (activas + inventario + intermedio)

**Cálculo SQL:**
```sql
SELECT 
    SUM(valor) AS inversion_total
FROM (
    SELECT valor FROM llantas
    UNION ALL
    SELECT valor FROM inventario
    UNION ALL
    SELECT valor FROM intermedio
) AS todas_llantas;
```

**Meta:** Mantener dentro del presupuesto anual

**Frecuencia de Cálculo:** Diario

---

### KPI-FIN-003: Gasto Mensual en Llantas
**Descripción:** Inversión mensual en compra de llantas nuevas y reencauches

**Cálculo SQL:**
```sql
SELECT 
    DATE_TRUNC('month', fecha) AS mes,
    COUNT(*) AS cantidad_comprada,
    SUM(valor) AS gasto_total,
    AVG(valor) AS costo_promedio_unitario
FROM inventario
WHERE fecha >= CURRENT_DATE - INTERVAL '12 months'
GROUP BY DATE_TRUNC('month', fecha)
ORDER BY mes DESC;
```

**Meta:** ≤ Presupuesto mensual definido

**Frecuencia de Cálculo:** Mensual

---

### KPI-FIN-004: Retorno de Inversión (ROI) por Marca
**Descripción:** Análisis comparativo de ROI por marca de llanta

**Fórmula:**
```
ROI = ((Kms_Logrados × Ingreso_Por_Km) - Costo_Total) / Costo_Total × 100
```

**Nota:** Requiere definir ingreso por kilómetro del negocio

**Meta:** Identificar marcas con mejor ROI

**Frecuencia de Cálculo:** Trimestral

---

### KPI-FIN-005: Ahorro por Reencauches
**Descripción:** Ahorro generado por reencauchar vs comprar nueva

**Fórmula:**
```
Ahorro = (Precio_Llanta_Nueva - Precio_Reencauche) × Cantidad_Reencauches
```

**Cálculo SQL:**
```sql
WITH costos_promedio AS (
    SELECT 
        AVG(CASE WHEN grupo = '000' THEN valor END) AS precio_nueva,
        AVG(CASE WHEN grupo > '000' THEN valorrn END) AS precio_reencauche
    FROM historia
)
SELECT 
    COUNT(*) AS total_reencauches,
    (precio_nueva - precio_reencauche) AS ahorro_unitario,
    COUNT(*) * (precio_nueva - precio_reencauche) AS ahorro_total
FROM historia h, costos_promedio cp
WHERE h.grupo > '000'
  AND h.fecha >= CURRENT_DATE - INTERVAL '12 months';
```

**Meta:** Maximizar ahorro manteniendo calidad

**Frecuencia de Cálculo:** Trimestral

---

## 4. KPIS DE CALIDAD Y RENDIMIENTO

### KPI-CAL-001: Kilómetros Promedio por Llanta
**Descripción:** Kilómetros promedio logrados por las llantas antes de ser retiradas

**Cálculo SQL:**
```sql
WITH vida_util AS (
    SELECT 
        h.llanta,
        SUBSTR(h.grupo, 3, 1) AS tipo_llanta,
        SUM(h.kremueve - h.kinstala) AS kms_totales
    FROM historia h
    WHERE (h.llanta, h.grupo) IN (
        SELECT llanta, grupo FROM retiradas
    )
    GROUP BY h.llanta, SUBSTR(h.grupo, 3, 1)
)
SELECT 
    AVG(kms_totales) AS kms_promedio,
    MIN(kms_totales) AS kms_minimo,
    MAX(kms_totales) AS kms_maximo,
    STDDEV(kms_totales) AS desviacion_estandar
FROM vida_util;
```

**Meta:** ≥ 120,000 km (para llantas nuevas)

**Frecuencia de Cálculo:** Mensual

---

### KPI-CAL-002: Eficiencia vs Especificación
**Descripción:** Porcentaje de kilómetros logrados vs kilómetros esperados (ficha técnica)

**Fórmula:**
```
Eficiencia = (Kms_Reales / Kms_Esperados_FichaTecnica) × 100
```

**Cálculo SQL:**
```sql
WITH rendimiento_real AS (
    SELECT 
        h.ficha,
        AVG(h.kremueve - h.kinstala) AS kms_promedio_real
    FROM historia h
    WHERE (h.llanta, h.grupo) IN (SELECT llanta, grupo FROM retiradas)
    GROUP BY h.ficha
)
SELECT 
    f.codigo AS ficha,
    f.marca,
    f.dimension,
    f.kespera AS kms_esperados,
    rr.kms_promedio_real,
    ROUND((rr.kms_promedio_real / f.kespera) * 100, 2) AS eficiencia_porcentaje
FROM fichatec f
JOIN rendimiento_real rr ON f.codigo = rr.ficha
ORDER BY eficiencia_porcentaje DESC;
```

**Meta:** ≥ 90%

**Frecuencia de Cálculo:** Trimestral

---

### KPI-CAL-003: Tasa de Fallas Prematuras
**Descripción:** Porcentaje de llantas retiradas antes de alcanzar el 50% de vida útil esperada

**Fórmula:**
```
Tasa_Fallas_Prematuras = (Llantas_Retiradas_Antes_50% / Total_Llantas_Retiradas) × 100
```

**Cálculo SQL:**
```sql
WITH vida_util AS (
    SELECT 
        h.llanta,
        h.grupo,
        h.ficha,
        SUM(h.kremueve - h.kinstala) AS kms_totales,
        f.kespera AS kms_esperados
    FROM historia h
    JOIN fichatec f ON h.ficha = f.codigo
    WHERE (h.llanta, h.grupo) IN (SELECT llanta, grupo FROM retiradas)
    GROUP BY h.llanta, h.grupo, h.ficha, f.kespera
)
SELECT 
    COUNT(*) AS total_retiradas,
    COUNT(*) FILTER (WHERE kms_totales < (kms_esperados * 0.5)) AS fallas_prematuras,
    ROUND((COUNT(*) FILTER (WHERE kms_totales < (kms_esperados * 0.5))::DECIMAL / COUNT(*)) * 100, 2) AS tasa_fallas
FROM vida_util;
```

**Meta:** < 5%

**Frecuencia de Cálculo:** Mensual

---

### KPI-CAL-004: Desgaste Irregular
**Descripción:** Porcentaje de llantas con desgaste irregular detectado

**Fórmula:**
```
Desgaste_Irregular = ABS(MAX(PI, PC, PD) - MIN(PI, PC, PD)) > Umbral
```

**Cálculo SQL:**
```sql
WITH ultimo_muestreo AS (
    SELECT 
        m.llanta,
        m.grupo,
        m.pi,
        m.pc,
        m.pd,
        GREATEST(m.pi, m.pc, m.pd) - LEAST(m.pi, m.pc, m.pd) AS diferencia
    FROM muestreo m
    JOIN (
        SELECT llanta, grupo, MAX(fecha) AS ultima_fecha
        FROM muestreo
        GROUP BY llanta, grupo
    ) ult ON m.llanta = ult.llanta AND m.grupo = ult.grupo AND m.fecha = ult.ultima_fecha
)
SELECT 
    COUNT(*) AS total_llantas,
    COUNT(*) FILTER (WHERE diferencia > 2.0) AS con_desgaste_irregular,
    ROUND((COUNT(*) FILTER (WHERE diferencia > 2.0)::DECIMAL / COUNT(*)) * 100, 2) AS porcentaje_irregular
FROM ultimo_muestreo;
```

**Meta:** < 10%

**Frecuencia de Cálculo:** Semanal

**Umbral de Alerta:** Diferencia > 2.0 mm entre profundidades

---

### KPI-CAL-005: Índice de Satisfacción de Proveedores
**Descripción:** Evaluación del rendimiento de llantas por proveedor

**Criterios de Evaluación:**
1. Kilómetros promedio logrados (40%)
2. Tasa de fallas prematuras (30%)
3. Costo por kilómetro (20%)
4. Tasa de reencauches exitosos (10%)

**Fórmula:**
```
Índice = (Score_KMs × 0.4) + (Score_Fallas × 0.3) + (Score_Costo × 0.2) + (Score_Reencauches × 0.1)
```

**Meta:** ≥ 80/100

**Frecuencia de Cálculo:** Trimestral

---

## 5. KPIS DE MANTENIMIENTO PREVENTIVO

### KPI-MAN-001: Cumplimiento de Programación de Muestreos
**Descripción:** Porcentaje de muestreos realizados a tiempo según programación

**Fórmula:**
```
Cumplimiento = (Muestreos_A_Tiempo / Total_Muestreos_Programados) × 100
```

**Criterio:** Muestreo debe realizarse cada 10,000-15,000 km o 30 días

**Cálculo SQL:**
```sql
WITH llantas_activas AS (
    SELECT 
        l.llanta,
        l.grupo,
        l.kinstala,
        v.kilomact AS km_actual,
        v.kilomact - l.kinstala AS kms_desde_instalacion
    FROM llantas l
    JOIN vehiculos_llantas v ON l.vehiculo = v.placa
),
ultimo_muestreo AS (
    SELECT 
        m.llanta,
        m.grupo,
        MAX(m.kilom) AS ultimo_km_muestreo,
        MAX(m.fecha) AS ultima_fecha_muestreo
    FROM muestreo m
    GROUP BY m.llanta, m.grupo
)
SELECT 
    COUNT(*) AS total_llantas_activas,
    COUNT(*) FILTER (WHERE 
        la.km_actual - COALESCE(um.ultimo_km_muestreo, la.kinstala) < 15000
        AND (CURRENT_DATE - COALESCE(um.ultima_fecha_muestreo, l.fechai)) < 30
    ) AS muestreos_al_dia,
    ROUND((COUNT(*) FILTER (WHERE 
        la.km_actual - COALESCE(um.ultimo_km_muestreo, la.kinstala) < 15000
        AND (CURRENT_DATE - COALESCE(um.ultima_fecha_muestreo, l.fechai)) < 30
    )::DECIMAL / COUNT(*)) * 100, 2) AS porcentaje_cumplimiento
FROM llantas_activas la
LEFT JOIN ultimo_muestreo um ON la.llanta = um.llanta AND la.grupo = um.grupo
JOIN llantas l ON la.llanta = l.llanta AND la.grupo = l.grupo;
```

**Meta:** ≥ 95%

**Frecuencia de Cálculo:** Semanal

---

### KPI-MAN-002: Llantas en Estado Crítico
**Descripción:** Cantidad y porcentaje de llantas activas con profundidad < límite legal

**Límite Legal:** 1.6 mm

**Cálculo SQL:**
```sql
WITH ultimo_muestreo AS (
    SELECT 
        m.llanta,
        m.grupo,
        (m.pi + m.pc + m.pd) / 3 AS profundidad_promedio
    FROM muestreo m
    JOIN (
        SELECT llanta, grupo, MAX(fecha) AS ultima_fecha
        FROM muestreo
        GROUP BY llanta, grupo
    ) ult ON m.llanta = ult.llanta AND m.grupo = ult.grupo AND m.fecha = ult.ultima_fecha
)
SELECT 
    COUNT(*) AS total_llantas_activas,
    COUNT(*) FILTER (WHERE um.profundidad_promedio < 1.6) AS llantas_criticas,
    ROUND((COUNT(*) FILTER (WHERE um.profundidad_promedio < 1.6)::DECIMAL / COUNT(*)) * 100, 2) AS porcentaje_critico
FROM llantas l
JOIN ultimo_muestreo um ON l.llanta = um.llanta AND l.grupo = um.grupo;
```

**Meta:** 0%

**Frecuencia de Cálculo:** Diario

**Alerta:** Cualquier llanta en estado crítico genera alerta inmediata

---

### KPI-MAN-003: Tiempo Medio de Respuesta a Alertas
**Descripción:** Tiempo promedio desde que se genera una alerta hasta que se toma acción

**Meta:** < 24 horas para alertas críticas, < 72 horas para alertas medias

**Frecuencia de Cálculo:** Semanal

---

### KPI-MAN-004: Tasa de Rotación Preventiva
**Descripción:** Porcentaje de llantas que fueron rotadas preventivamente vs retiradas por desgaste

**Meta:** ≥ 40%

**Frecuencia de Cálculo:** Mensual

---

## 6. DASHBOARDS Y VISUALIZACIONES

### Dashboard 1: Ejecutivo (CEO/Gerencia General)
**Frecuencia de Actualización:** Diario

**KPIs Incluidos:**
- Total de llantas en sistema (KPI-OP-001)
- Inversión total en llantas (KPI-FIN-002)
- Costo por kilómetro promedio (KPI-FIN-001)
- Gasto mensual vs presupuesto (KPI-FIN-003)
- Ahorro por reencauches (KPI-FIN-005)

**Visualizaciones:**
- Gráfico de tendencia: Costo/km últimos 12 meses
- Gráfico de barras: Consumo mensual vs presupuesto
- Pie chart: Distribución de inversión por estado de llantas

---

### Dashboard 2: Operacional (Jefe de Taller)
**Frecuencia de Actualización:** Tiempo real

**KPIs Incluidos:**
- Llantas activas por estado (KPI-CAL-004)
- Muestreos pendientes (KPI-MAN-001)
- Alertas críticas (KPI-MAN-002)
- Inventario disponible (KPI-OP-002)

**Visualizaciones:**
- Mapa de calor: Estado de llantas por vehículo
- Lista priorizada: Alertas que requieren acción
- Calendario: Muestreos programados próximos 7 días
- Gauge: Nivel de inventario y días de cobertura

---

### Dashboard 3: Análisis de Rendimiento (Administrador de Flota)
**Frecuencia de Actualización:** Semanal

**KPIs Incluidos:**
- Kilómetros promedio por marca (KPI-CAL-001)
- Eficiencia vs especificación (KPI-CAL-002)
- Tasa de fallas prematuras (KPI-CAL-003)
- ROI por marca (KPI-FIN-004)
- Desgaste irregular (KPI-CAL-004)

**Visualizaciones:**
- Gráfico de barras comparativo: Rendimiento por marca
- Scatter plot: Costo vs Kilómetros logrados
- Tabla ranking: Mejores y peores proveedores
- Gráfico de líneas: Tendencia de eficiencia trimestral

---

## 7. ALERTAS Y UMBRALES

### Sistema de Alertas por Prioridad

#### 🔴 PRIORIDAD ALTA (Acción inmediata - < 24h)
1. **Profundidad Crítica**
   - Umbral: < 1.6 mm
   - Acción: Retirar llanta inmediatamente

2. **Desgaste Irregular Severo**
   - Umbral: Diferencia > 3.0 mm entre PI, PC, PD
   - Acción: Inspección mecánica del vehículo

3. **Inventario Crítico**
   - Umbral: < 10 días de cobertura
   - Acción: Orden de compra urgente

4. **Llanta sin muestreo excesivo**
   - Umbral: > 20,000 km o > 60 días sin muestreo
   - Acción: Programar muestreo inmediato

#### 🟡 PRIORIDAD MEDIA (Acción requerida - < 72h)
1. **Profundidad Baja**
   - Umbral: 1.6 mm - 3.0 mm
   - Acción: Programar reemplazo próximo

2. **Desgaste Irregular Moderado**
   - Umbral: Diferencia 2.0 - 3.0 mm
   - Acción: Revisar alineación y presión

3. **Presión Incorrecta**
   - Umbral: < 80 PSI o > 130 PSI
   - Acción: Ajustar presión en próximo mantenimiento

4. **Muestreo retrasado**
   - Umbral: 15,000 - 20,000 km o 30-60 días
   - Acción: Programar muestreo esta semana

#### 🟢 PRIORIDAD BAJA (Informativa - revisar semanalmente)
1. **Inventario alto**
   - Umbral: > 90 días de cobertura
   - Acción: Revisar proyecciones de compra

2. **Llanta próxima a rotación**
   - Umbral: Desgaste > 50% en direccionales
   - Acción: Programar rotación preventiva

---

## 8. IMPLEMENTACIÓN TÉCNICA

### 8.1 Almacenamiento de Métricas
```sql
-- Tabla para almacenar métricas históricas
CREATE TABLE metricas_historicas (
    id SERIAL PRIMARY KEY,
    codigo_metrica VARCHAR(20) NOT NULL,
    fecha_calculo TIMESTAMP NOT NULL,
    valor DECIMAL(15, 4),
    desagregacion JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_metricas_codigo_fecha ON metricas_historicas(codigo_metrica, fecha_calculo DESC);
```

### 8.2 Job Scheduler
- **Cron Jobs** para cálculo de métricas periódicas
- **Real-time calculations** para KPIs operacionales críticos
- **Cache Redis** para dashboards de alta frecuencia

### 8.3 Notificaciones
- **Email:** Reportes semanales/mensuales a gerencia
- **SMS:** Alertas críticas a jefe de taller
- **Push Notifications:** Alertas en aplicación móvil
- **Webhooks:** Integración con sistemas de terceros

---

## 9. REPORTES EJECUTIVOS

### Reporte Mensual de Gestión
**Destinatarios:** Gerencia General, Gerencia de Operaciones

**Contenido:**
1. Resumen Ejecutivo
   - KPIs financieros principales
   - Comparativo vs mes anterior
   - Varianza vs presupuesto

2. Análisis Operacional
   - Consumo mensual de llantas
   - Rendimiento por marca
   - Alertas y acciones tomadas

3. Proyecciones
   - Necesidades de compra próximos 3 meses
   - Estimación de costos
   - Recomendaciones de optimización

### Reporte Semanal de Operaciones
**Destinatarios:** Jefe de Taller, Coordinadores de Flota

**Contenido:**
1. Estado de la flota
   - Llantas en estado crítico
   - Muestreos realizados vs programados
   - Alertas pendientes

2. Actividades de la semana
   - Montajes y desmontajes
   - Reencauches enviados/recibidos
   - Rotaciones realizadas

---

**FIN DEL DOCUMENTO**
