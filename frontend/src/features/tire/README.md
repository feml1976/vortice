# Módulo de Gestión de Llantas (Tire)

## Descripción General

Este módulo implementa la gestión completa de **Especificaciones Técnicas de Llantas**, cumpliendo con el requerimiento funcional **RF-001** del proyecto de modernización TRANSER Vórtice.

## Estructura del Módulo

```
frontend/src/features/tire/
├── components/              # Componentes React reutilizables
│   ├── TireSpecificationList.tsx           # DataGrid con búsqueda y paginación
│   ├── TireSpecificationDetailDialog.tsx   # Modal de detalle (solo lectura)
│   ├── TireSpecificationForm.tsx           # Formulario de creación/edición
│   └── index.ts                            # Exportaciones
├── hooks/                   # React Query hooks personalizados
│   ├── useTireCatalogs.ts                  # Hooks para catálogos
│   └── useTireSpecifications.ts            # Hooks para especificaciones
├── pages/                   # Páginas del módulo
│   ├── TireSpecificationPage.tsx           # Página principal
│   └── index.ts                            # Exportaciones
├── services/                # Servicios de API (Axios)
│   ├── tireCatalogService.ts               # Servicio de catálogos
│   └── tireSpecificationService.ts         # Servicio de especificaciones
├── types/                   # Definiciones de TypeScript
│   └── tire.types.ts                       # Interfaces y tipos
├── index.ts                 # Punto de entrada del módulo
└── README.md                # Esta documentación
```

## Características Implementadas

### 1. Gestión de Especificaciones Técnicas
- ✅ **Listado paginado** con DataGrid de Material-UI
- ✅ **Búsqueda** por texto libre (código, marca, tipo, referencia)
- ✅ **Filtros** por marca, tipo, referencia y estado
- ✅ **Ordenamiento** por cualquier columna
- ✅ **Vista de detalle** en modal (solo lectura)
- ✅ **Formulario de creación** con validaciones completas
- ✅ **Formulario de edición** con carga de datos existentes
- ✅ **Eliminación** con confirmación (soft delete)

### 2. Catálogos Relacionados
- ✅ **Marcas de Llantas** (Brands)
- ✅ **Tipos de Llantas** (Types)
- ✅ **Referencias** (References)
- ✅ **Proveedores** (Suppliers)

### 3. Validaciones del Formulario
- Campos requeridos: Marca, Tipo, Referencia, Kilometraje Esperado, Reencauches, Profundidades
- Validación de rangos numéricos (kilometraje, profundidades 0-99.9mm, porcentajes 0-100%)
- Validación de coherencia de rangos de kilometraje (min ≤ avg ≤ max)
- Cálculo automático de profundidad promedio
- Validaciones con Zod para máxima seguridad de tipos

### 4. Estado del Servidor con React Query
- Cache inteligente con tiempos de stale configurados:
  - Catálogos: 5 minutos (cambian poco)
  - Especificaciones: 30 segundos
- Invalidación automática de cache en mutaciones
- Optimistic updates
- Retry automático en errores
- DevTools para debugging en modo desarrollo

### 5. Interfaz de Usuario
- Diseño responsivo con Material-UI v6
- Notificaciones toast para feedback del usuario
- Loading states y spinners
- Manejo de errores con mensajes claros
- Breadcrumbs para navegación
- Dialogs modales para detalle y formularios

## Uso del Módulo

### Navegación

La página principal del módulo está en la ruta:
```
/tire/specifications
```

### Componentes Exportados

```typescript
import {
  TireSpecificationList,
  TireSpecificationDetailDialog,
  TireSpecificationForm,
  TireSpecificationPage,
} from '@/features/tire';
```

### Hooks Personalizados

```typescript
import {
  // Especificaciones
  useTireSpecifications,
  useActiveTireSpecifications,
  useTireSpecification,
  useSearchTireSpecifications,
  useFilterTireSpecifications,
  useCreateTireSpecification,
  useUpdateTireSpecification,
  useDeleteTireSpecification,

  // Catálogos
  useActiveTireBrands,
  useAllTireBrands,
  useActiveTireTypes,
  useAllTireTypes,
  useActiveTireReferences,
  useAllTireReferences,
  useActiveTireSuppliers,
  useAllTireSuppliers,
  useTireFormCatalogs,
} from '@/features/tire';
```

### Tipos TypeScript

```typescript
import type {
  TireBrand,
  TireType,
  TireReference,
  TireSupplier,
  TireSpecification,
  TireSpecificationSummary,
  CreateTireSpecificationRequest,
  UpdateTireSpecificationRequest,
  TireSpecificationFilters,
  PageRequest,
  PageResponse,
} from '@/features/tire';
```

## Endpoints de API Utilizados

### Catálogos
- `GET /api/v1/tire-catalogs/brands` - Marcas activas
- `GET /api/v1/tire-catalogs/brands/all` - Todas las marcas
- `GET /api/v1/tire-catalogs/types` - Tipos activos
- `GET /api/v1/tire-catalogs/types/all` - Todos los tipos
- `GET /api/v1/tire-catalogs/references` - Referencias activas
- `GET /api/v1/tire-catalogs/references/all` - Todas las referencias
- `GET /api/v1/tire-catalogs/suppliers` - Proveedores activos
- `GET /api/v1/tire-catalogs/suppliers/all` - Todos los proveedores

### Especificaciones Técnicas
- `POST /api/v1/tire-specifications` - Crear especificación
- `GET /api/v1/tire-specifications` - Listar con paginación
- `GET /api/v1/tire-specifications/active` - Listar activas
- `GET /api/v1/tire-specifications/{id}` - Obtener por ID
- `PUT /api/v1/tire-specifications/{id}` - Actualizar
- `DELETE /api/v1/tire-specifications/{id}` - Eliminar (soft delete)
- `GET /api/v1/tire-specifications/search` - Buscar por texto
- `GET /api/v1/tire-specifications/filter` - Filtrar con múltiples criterios

## Permisos Requeridos

El módulo utiliza autorización basada en permisos. Los permisos necesarios son:
- `TIRE_SPECIFICATION_READ` - Ver especificaciones
- `TIRE_SPECIFICATION_CREATE` - Crear especificaciones
- `TIRE_SPECIFICATION_UPDATE` - Editar especificaciones
- `TIRE_SPECIFICATION_DELETE` - Eliminar especificaciones

## Campos de Especificación Técnica

### Información General
- **Código**: Auto-generado (formato: FT-NNNNNN)
- **Marca**: Catálogo de marcas
- **Tipo**: Catálogo de tipos
- **Referencia**: Catálogo de referencias
- **Dimensión**: Texto libre (ej: 295/80R22.5)
- **Peso**: En kilogramos

### Especificaciones de Rendimiento
- **Kilometraje Esperado**: Kilometros de vida útil
- **Reencauches Esperados**: Número de reencauches
- **% de Pérdida Esperado**: Porcentaje de pérdida
- **Total Esperado**: Vida útil total en km
- **Costo por Hora**: Costo operativo
- **Rangos de Kilometraje**: Mínimo, Promedio, Máximo

### Profundidades Iniciales (mm)
- **Profundidad Interna**: 0-99.9mm
- **Profundidad Central**: 0-99.9mm
- **Profundidad Externa**: 0-99.9mm
- **Profundidad Promedio**: Calculado automáticamente

### Información Comercial
- **Última Compra - Cantidad**: Unidades
- **Última Compra - Precio Unitario**: Precio
- **Última Compra - Fecha**: Fecha de compra

### Proveedores
- **Proveedor Principal**: Catálogo de proveedores
- **Proveedor Secundario**: Catálogo de proveedores
- **Último Proveedor Usado**: Catálogo de proveedores

## Flujo de Trabajo

### Crear Nueva Especificación
1. Click en botón "Nueva Especificación"
2. Llenar formulario con campos requeridos
3. Sistema valida campos en tiempo real
4. Click en "Crear"
5. Toast de confirmación
6. Lista se actualiza automáticamente

### Editar Especificación
1. Click en icono de edición (✏️) en la lista
2. Formulario se carga con datos existentes
3. Modificar campos necesarios
4. Click en "Actualizar"
5. Toast de confirmación
6. Detalle y lista se actualizan

### Ver Detalle
1. Click en icono de ver (👁️) en la lista
2. Modal muestra toda la información
3. Opción de editar desde el detalle

### Eliminar Especificación
1. Click en icono de eliminar (🗑️) en la lista
2. Diálogo de confirmación
3. Click en "Eliminar"
4. Toast de confirmación
5. Lista se actualiza

### Buscar
1. Escribir texto en barra de búsqueda
2. Presionar Enter o click en buscar
3. Lista filtra resultados
4. Badge muestra query actual
5. Opción "Limpiar búsqueda"

## Consideraciones Técnicas

### Gestión de Estado
- **React Query**: Estado del servidor (datos de API)
- **React Hook Form**: Estado de formularios
- **useState**: Estado local de componentes (dialogs, UI)

### Optimización
- Lazy loading de datos con paginación server-side
- Cache de catálogos con staleTime largo
- Debouncing en búsqueda (pendiente)
- Code splitting a nivel de página (React.lazy - pendiente)

### Accesibilidad
- Labels en todos los inputs
- ARIA attributes en dialogs
- Navegación por teclado
- Focus management

### Responsive Design
- Breakpoints de Material-UI
- Grid system flexible
- Dialogs fullscreen en móvil (pendiente mejora)

## Próximas Mejoras

### Funcionalidades
- [ ] Exportación a Excel/PDF
- [ ] Importación masiva desde CSV
- [ ] Historial de cambios
- [ ] Versionado de especificaciones
- [ ] Dashboard con estadísticas
- [ ] Filtros avanzados con chips
- [ ] Comparación entre especificaciones

### Técnicas
- [ ] Tests unitarios de componentes
- [ ] Tests de integración
- [ ] Storybook para documentación de componentes
- [ ] Optimización de bundle size
- [ ] PWA para uso offline

## Dependencias

### Producción
- `@mui/material` v6.3.1 - Componentes UI
- `@mui/icons-material` v6.3.1 - Iconos
- `@mui/x-data-grid` v7+ - Tabla avanzada
- `@tanstack/react-query` v5.62.7 - Estado del servidor
- `react-hook-form` v7.54.2 - Gestión de formularios
- `zod` v3.24.1 - Validación de esquemas
- `@hookform/resolvers` v3.9.1 - Integración RHF + Zod
- `date-fns` v4.1.0 - Formateo de fechas
- `react-hot-toast` v2.4.1 - Notificaciones
- `axios` v1.7.9 - Cliente HTTP

## Autor

Implementado como parte del proyecto de modernización TRANSER Vórtice.

**Fecha de Implementación**: Enero 2026
**Requerimiento Funcional**: RF-001 - Gestión de Fichas Técnicas
**Versión**: 1.0.0
