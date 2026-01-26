## 🎯 EJEMPLO DE USO

### Escenario: Implementar módulo Tire

```markdown
# CONTEXTO DE CONTINUACIÓN - Sistema TRANSER Vórtice

## Archivos de Contexto
He leído:
- ✅ C:\Dirsop\Proyectos\vortice\CLAUDE.md
- ✅ C:\Dirsop\Proyectos\vortice\docs\PROMPT_MASTER.md

## Estado Actual
Módulos implementados:
- ✅ Auth (Backend + Frontend)
- ✅ Users (Backend + Frontend)

Próximo módulo: Tire (Llantas)

## Solicitud
Necesito implementar el módulo Tire para gestión de Llantas.

### Paso 1: Análisis
Por favor, primero:
1. Explora la estructura actual del backend en src/main/java
2. Identifica cómo están organizados los módulos Auth y Users
3. Detecta patrones de DTOs, Services, Controllers, Repositories
4. Confirma el patrón antes de proceder

### Paso 2: Diseño
Una vez confirmado el patrón:
1. Diseña el modelo de dominio para Tire
2. Propón el esquema de base de datos (DDL)
3. Diseña los endpoints REST principales

### Paso 3: Implementación (Incremental)
No generes todo de una vez. Vamos paso a paso:
1. Primero: Entidades de dominio
2. Segundo: DTOs
3. Tercero: Repository
4. Cuarto: Service
5. Quinto: Controller
6. Sexto: Tests

Después de cada paso, espera mi confirmación antes de continuar.

¿Entendido? Comencemos con el Paso 1: Análisis.
```

---

## 📚 RECURSOS DE REFERENCIA

### Documentación del Proyecto
- `C:\Dirsop\Proyectos\vortice\CLAUDE.md` - Contexto general
- `C:\Dirsop\Proyectos\vortice\docs\PROMPT_MASTER.md` - Especificaciones técnicas
- `C:\Dirsop\Proyectos\vortice\docs\Informe_Modernizacion_BD_Llantas.md` - Modelo datos
- `C:\Dirsop\Proyectos\vortice\Requerimientos\*` - Requerimientos funcionales

### Referencias de Código Existente
- Auth module: `backend/src/main/java/com/transer/vortice/auth`
- Users module: `backend/src/main/java/com/transer/vortice/users`
- Frontend Auth: `frontend/src/features/auth`
- Frontend Users: `frontend/src/features/users`

---

## ✅ CHECKLIST DE CALIDAD

Antes de considerar una tarea como "completa":

**Backend:**
- [ ] Código sigue Clean Architecture (capas bien separadas)
- [ ] DTOs no exponen entidades de dominio
- [ ] Validaciones implementadas (Jakarta Validation)
- [ ] Exception handling implementado
- [ ] Unit tests con >70% cobertura
- [ ] Integration tests para endpoints críticos
- [ ] Documentación OpenAPI/Swagger
- [ ] Logs apropiados (no secretos, nivel correcto)

**Frontend:**
- [ ] TypeScript strict mode sin errores
- [ ] Componentes funcionales (no class components)
- [ ] Props tipadas con interfaces
- [ ] React Query para fetching
- [ ] Manejo de loading/error states
- [ ] Formularios con validación (Formik + Yup)
- [ ] Responsive design (funciona en mobile)
- [ ] Tests de componentes principales

**Base de Datos:**
- [ ] Nomenclatura snake_case consistente
- [ ] Columnas de auditoría (created_at, updated_by, etc.)
- [ ] Soft delete implementado (deleted_at)
- [ ] Índices en foreign keys y campos de búsqueda
- [ ] Constraints apropiados
- [ ] Scripts de migración versionados

---

## 🔄 VERSIONADO DE PROMPTS

**Versión:** 1.0  
**Fecha:** 22 de Enero de 2026  
**Autor:** femon76  
**Propósito:** Prompt master para continuación de desarrollo con Claude Code  
**Compatible con:** Claude Code, Claude Sonnet 4.5

---

**¡ÉXITO EN EL DESARROLLO! 🚀**
