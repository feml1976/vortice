# ✅ CHECKLIST DE VALIDACIÓN - COMPRENSIÓN DE REQUERIMIENTOS
## Sistema TRANSER Vórtice - Módulo Tire

**Uso:** Antes de iniciar desarrollo de cualquier RF (Requerimiento Funcional)

---

## 📋 CUESTIONARIO DE VALIDACIÓN (30 PREGUNTAS)

### A. COMPRENSIÓN DEL PROBLEMA DE NEGOCIO (3)
- [ ] 1. ¿Qué problema de negocio específico resuelve?
- [ ] 2. ¿Quiénes son los usuarios/actores involucrados?
- [ ] 3. ¿Cuál es el valor que aporta al negocio?

### B. ALCANCE FUNCIONAL (3)
- [ ] 4. ¿Cuáles son los casos de uso principales?
- [ ] 5. ¿Qué operaciones CRUD son necesarias?
- [ ] 6. ¿Hay operaciones especiales o procesos complejos?

### C. REGLAS DE NEGOCIO (4)
- [ ] 7. Enumera reglas de negocio explícitas
- [ ] 8. ¿Hay reglas de negocio implícitas?
- [ ] 9. ¿Qué validaciones son obligatorias?
- [ ] 10. ¿Qué validaciones opcionales recomiendas?

### D. MODELO DE DATOS (3)
- [ ] 11. ¿Qué tablas están involucradas?
- [ ] 12. ¿Qué relaciones entre entidades?
- [ ] 13. ¿Hay campos calculados o derivados?

### E. ESTADOS Y TRANSICIONES (3)
- [ ] 14. ¿El requerimiento involucra estados? Enuméralos
- [ ] 15. ¿Qué transiciones de estado son válidas?
- [ ] 16. ¿Qué transiciones están prohibidas?

### F. INVARIANTES DEL DOMINIO (3)
- [ ] 17. ¿Qué condiciones SIEMPRE deben ser verdaderas?
- [ ] 18. ¿Qué es irreversible una vez hecho?
- [ ] 19. ¿Qué restricciones temporales existen?

### G. EVENTOS Y EFECTOS SECUNDARIOS (3)
- [ ] 20. ¿Esta operación genera eventos de dominio?
- [ ] 21. ¿Hay efectos en cascada sobre otras entidades?
- [ ] 22. ¿Qué se debe registrar en auditoría?

### H. INTERFAZ DE USUARIO (3)
- [ ] 23. ¿Qué tipo de formulario se necesita?
- [ ] 24. ¿Qué campos son obligatorios vs opcionales?
- [ ] 25. ¿Hay dependencias entre campos?

### I. RIESGOS E INCERTIDUMBRES (3)
- [ ] 26. ¿Hay información ambigua o faltante?
- [ ] 27. ¿Qué supuestos debo hacer?
- [ ] 28. ¿Qué requiere confirmación del PO?

### J. INTEGRACIÓN CON MÓDULOS EXISTENTES (3)
- [ ] 29. ¿Depende de Auth o Users?
- [ ] 30. ¿Necesita permisos especiales?
- [ ] 31. ¿Consume catálogos compartidos?

---

## 📐 DISEÑO DE SOLUCIÓN (Entregables)

### 1. AGGREGATE ROOT Y ENTIDADES
```
- [ ] Aggregate Root identificado
- [ ] Entidades del agregado listadas
- [ ] Value Objects definidos
- [ ] Límites del agregado claros
```

### 2. CASOS DE USO
```
Para cada Use Case:
- [ ] Nombre descriptivo
- [ ] Flujo principal (pasos)
- [ ] Precondiciones
- [ ] Postcondiciones
- [ ] Invariantes validadas
```

### 3. API REST
```
Para cada endpoint:
- [ ] Método HTTP
- [ ] Ruta (/api/v1/tires/...)
- [ ] Request DTO
- [ ] Response DTO
- [ ] Códigos de error (400, 404, 422, 500)
- [ ] Permisos requeridos
```

### 4. DIAGRAMA DE DOMINIO
```
- [ ] Entidades y relaciones
- [ ] Estados y transiciones
- [ ] Invariantes principales
- [ ] Formato: Mermaid o texto estructurado
```

---

## 🛑 GATE DE CALIDAD

**Después de completar análisis y diseño:**

### Preguntar Explícitamente:
> "¿Confirmas que el análisis y diseño presentado es correcto y puedo proceder con la implementación?"

### Respuestas Aceptables para Continuar:
- ✅ "Sí, procede"
- ✅ "Confirmado, adelante"
- ✅ "Correcto, implementa"
- ✅ "Aprobado"

### Si la Respuesta NO es Clara:
- ⏸️ **NO CONTINUAR**
- ⏸️ Aclarar dudas
- ⏸️ Ajustar diseño
- ⏸️ Re-solicitar confirmación

---

## 📝 TEMPLATE DE RESPUESTA

### Estructura Recomendada:

```markdown
# ANÁLISIS: [RF-XXX] - [Nombre del Requerimiento]

## A. COMPRENSIÓN DEL PROBLEMA DE NEGOCIO
1. Problema: ...
2. Actores: ...
3. Valor: ...

## B. ALCANCE FUNCIONAL
4. Casos de uso: ...
5. Operaciones CRUD: ...
6. Procesos especiales: ...

[... continuar con todas las secciones ...]

---

## DISEÑO DE SOLUCIÓN

### 1. Aggregate Root y Entidades
...

### 2. Casos de Uso
...

### 3. API REST
...

### 4. Diagrama de Dominio
...

---

## RESUMEN Y CONFIRMACIÓN

**Puntos Clave:**
- ...
- ...

**Supuestos:**
- ...
- ...

**Áreas de Riesgo:**
- ...
- ...

**❓ CONFIRMACIÓN REQUERIDA:**
¿Confirmas que el análisis y diseño presentado es correcto y puedo proceder con la implementación?
```

---

## 🎯 FLUJO DE TRABAJO VISUAL

```
┌─────────────────────────────────────────┐
│ 1. Product Owner indica RF a desarrollar│
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 2. Claude Code lee RF completo          │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 3. Claude Code responde 30 preguntas    │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 4. Claude Code presenta diseño          │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 5. Claude Code solicita confirmación    │
└──────────────┬──────────────────────────┘
               │
               ▼
        ┌──────┴──────┐
        │             │
        ▼             ▼
┌──────────┐   ┌─────────────┐
│ NO/Dudas │   │ SÍ/Aprobado │
└────┬─────┘   └──────┬──────┘
     │                │
     │                ▼
     │      ┌──────────────────────┐
     │      │ 6. Iniciar desarrollo│
     │      │    Backend → Frontend│
     │      └──────────────────────┘
     │
     ▼
┌──────────────┐
│ Aclarar/     │
│ Ajustar      │
│ Re-confirmar │
└──────────────┘
```

---

## ⚠️ RECORDATORIOS CRÍTICOS

1. **NUNCA** saltar la fase de análisis
2. **SIEMPRE** responder las 30 preguntas
3. **OBLIGATORIO** solicitar confirmación explícita
4. **NO** asumir - preguntar cuando haya duda
5. **PRESERVAR** trazabilidad completa
6. **VALIDAR** invariantes en domain/application, no en presentation

---

## 📊 MÉTRICAS DE CALIDAD

Un buen análisis debe tener:

- ✅ **30/30 preguntas** respondidas con detalle
- ✅ **Diagrama visual** del dominio
- ✅ **Use Cases** con flujos claros
- ✅ **API** completamente especificada
- ✅ **Riesgos** identificados y documentados
- ✅ **Supuestos** explícitos
- ✅ **Confirmación** solicitada y obtenida

---

**Versión:** 1.0  
**Última Actualización:** 2026-01-26
