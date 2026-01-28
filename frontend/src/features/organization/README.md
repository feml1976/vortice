
# Módulo de Estructura Organizacional Multi-Sede - Frontend

Este módulo implementa la interfaz de usuario para el sistema de gestión multi-sede (multi-tenant) de Vórtice.

## 📁 Estructura del Módulo

```
organization/
├── types/
│   └── organization.types.ts          # Tipos TypeScript para todas las entidades
├── services/
│   ├── officeService.ts               # API client para oficinas
│   ├── warehouseService.ts            # API client para almacenes
│   ├── warehouseLocationService.ts    # API client para ubicaciones
│   └── tireSupplierService.ts         # API client para proveedores
├── hooks/
│   ├── useOffices.ts                  # React Query hooks para oficinas
│   ├── useWarehouses.ts               # React Query hooks para almacenes
│   ├── useWarehouseLocations.ts       # React Query hooks para ubicaciones
│   └── useTireSuppliers.ts            # React Query hooks para proveedores
├── context/
│   └── OfficeContext.tsx              # Context Provider para oficina actual
├── components/
│   ├── OfficeSelector.tsx             # Selector de oficinas
│   ├── WarehouseSelector.tsx          # Selector de almacenes
│   ├── OfficeList.tsx                 # DataGrid de oficinas
│   ├── WarehouseList.tsx              # DataGrid de almacenes
│   ├── WarehouseLocationList.tsx      # DataGrid de ubicaciones
│   ├── TireSupplierList.tsx           # DataGrid de proveedores
│   ├── OfficeForm.tsx                 # Formulario de oficinas
│   ├── WarehouseForm.tsx              # Formulario de almacenes
│   ├── WarehouseLocationForm.tsx      # Formulario de ubicaciones
│   ├── TireSupplierForm.tsx           # Formulario de proveedores
│   ├── OfficeDetailDialog.tsx         # Diálogo detalle de oficina
│   └── WarehouseDetailDialog.tsx      # Diálogo detalle de almacén
├── pages/
│   ├── OfficePage.tsx                 # Página de gestión de oficinas
│   ├── WarehousePage.tsx              # Página de gestión de almacenes
│   ├── WarehouseLocationPage.tsx      # Página de gestión de ubicaciones
│   └── TireSupplierPage.tsx           # Página de gestión de proveedores
└── index.ts                            # Barrel exports
```

## 🚀 Uso Rápido

### 1. Setup del Context Provider

Envuelve tu aplicación con el `OfficeProvider`:

```tsx
import { OfficeProvider } from '@/features/organization';

function App() {
  return (
    <OfficeProvider>
      {/* Tu aplicación */}
    </OfficeProvider>
  );
}
```

### 2. Usar el Context de Oficina

```tsx
import { useOfficeContext } from '@/features/organization';

function MyComponent() {
  const {
    currentOffice,
    isNationalAdmin,
    isOfficeAdmin,
    hasAccessToOffice,
    canManageWarehouses,
  } = useOfficeContext();

  // Verificar permisos
  if (!canManageWarehouses()) {
    return <div>No tienes permisos</div>;
  }

  // Usar oficina actual
  return (
    <div>
      <h1>Oficina: {currentOffice?.name}</h1>
      {isNationalAdmin && <button>Ver todas las oficinas</button>}
    </div>
  );
}
```

### 3. Listar Oficinas con React Query

```tsx
import { useOffices } from '@/features/organization';

function OfficeList() {
  const { data: offices, isLoading, error } = useOffices();

  if (isLoading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <ul>
      {offices?.map((office) => (
        <li key={office.id}>
          {office.code} - {office.name} ({office.city})
        </li>
      ))}
    </ul>
  );
}
```

### 4. Crear una Nueva Oficina

```tsx
import { useCreateOffice } from '@/features/organization';
import { CreateOfficeRequest } from '@/features/organization';

function CreateOfficeForm() {
  const createOffice = useCreateOffice();

  const handleSubmit = async (data: CreateOfficeRequest) => {
    await createOffice.mutateAsync(data);
    // Success toast se muestra automáticamente
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Campos del formulario */}
    </form>
  );
}
```

### 5. Selector de Oficinas

```tsx
import { OfficeSelector } from '@/features/organization';

function MyForm() {
  const [officeId, setOfficeId] = useState('');

  return (
    <OfficeSelector
      value={officeId}
      onChange={setOfficeId}
      label="Seleccionar Oficina"
      required
    />
  );
}
```

### 6. Selector de Almacenes (Filtrado por Oficina)

```tsx
import { WarehouseSelector } from '@/features/organization';

function MyForm() {
  const [officeId, setOfficeId] = useState('');
  const [warehouseId, setWarehouseId] = useState('');

  return (
    <>
      <OfficeSelector value={officeId} onChange={setOfficeId} />
      <WarehouseSelector
        value={warehouseId}
        onChange={setWarehouseId}
        officeId={officeId} // Filtra almacenes por esta oficina
      />
    </>
  );
}
```

### 7. Páginas CRUD Completas

Las páginas completas incluyen listado, creación, edición y visualización de detalles:

```tsx
// En tu router
import {
  OfficePage,
  WarehousePage,
  WarehouseLocationPage,
  TireSupplierPage,
} from '@/features/organization';

// Rutas
<Route path="/organization/offices" element={<OfficePage />} />
<Route path="/organization/warehouses" element={<WarehousePage />} />
<Route path="/organization/locations" element={<WarehouseLocationPage />} />
<Route path="/organization/suppliers" element={<TireSupplierPage />} />
```

Características de las páginas:
- **DataGrid con paginación**: Listado de registros con ordenamiento y búsqueda
- **Filtros inteligentes**: Por oficina (admin nacional) o almacén (ubicaciones)
- **Permisos integrados**: Solo muestra acciones según el rol del usuario
- **CRUD completo**: Crear, leer, actualizar, activar/desactivar y eliminar
- **Validación**: Formularios con Zod y React Hook Form
- **Feedback visual**: Toasts de éxito/error y estados de loading

## 🔐 Sistema de Permisos

El módulo respeta la jerarquía de permisos definida en el backend:

### Roles

- **ROLE_ADMIN_NATIONAL**: Acceso completo a todas las oficinas
- **ROLE_ADMIN_OFFICE**: Gestión de su oficina, almacenes y proveedores
- **ROLE_WAREHOUSE_MANAGER**: Gestión de ubicaciones de almacén

### Permisos por Entidad

| Entidad | ADMIN_NATIONAL | ADMIN_OFFICE | WAREHOUSE_MANAGER | Otros |
|---------|----------------|--------------|-------------------|-------|
| **Oficinas** | CRUD | Read | Read | Read |
| **Almacenes** | CRUD | CRUD | Read | Read |
| **Ubicaciones** | CRUD | CRUD | CRUD | Read |
| **Proveedores** | CRUD | CRUD | Read | Read |

### Verificación de Permisos en Componentes

```tsx
const { canManageOffices, canManageWarehouses } = useOfficeContext();

// Mostrar botón solo si tiene permisos
{canManageOffices() && (
  <Button onClick={createOffice}>Crear Oficina</Button>
)}

// Deshabilitar campo si no puede editar
<TextField
  disabled={!canManageWarehouses()}
  label="Almacén"
/>
```

## 📊 Hooks Disponibles

### Oficinas

```tsx
// Listar
const { data, isLoading } = useOffices(filters);

// Obtener por ID
const { data } = useOffice(id);

// Con detalles (incluye totales)
const { data } = useOfficeWithDetails(id);

// Buscar con paginación
const { data } = useOfficesSearch(filters, pageRequest);

// Crear
const createMutation = useCreateOffice();

// Actualizar
const updateMutation = useUpdateOffice();

// Eliminar
const deleteMutation = useDeleteOffice();

// Activar/Desactivar
const toggleMutation = useToggleOfficeActive();
```

### Almacenes

```tsx
// Listar (RLS automático)
const { data } = useWarehouses(filters);

// Por oficina
const { data } = useWarehousesByOffice(officeId);

// Obtener por ID
const { data } = useWarehouse(id);

// Con detalles
const { data } = useWarehouseWithDetails(id);

// Crear/Actualizar/Eliminar/Toggle
const create = useCreateWarehouse();
const update = useUpdateWarehouse();
const del = useDeleteWarehouse();
const toggle = useToggleWarehouseActive();
```

### Ubicaciones y Proveedores

Siguen el mismo patrón que Almacenes. Ver archivos en `/hooks`.

## 🔄 Row-Level Security (RLS)

El frontend trabaja en conjunto con el RLS del backend:

1. **Filtrado Automático**: Los usuarios ven solo datos de su oficina
2. **Admin Nacional**: Ve todos los datos sin restricción
3. **Validación**: El backend valida accesos, el frontend solo optimiza UX

### Ejemplo: Listar Almacenes

```tsx
// Usuario normal: ve solo almacenes de su oficina (RLS en backend)
// Admin nacional: ve almacenes de todas las oficinas
const { data: warehouses } = useWarehouses();

// Ambos casos usan el mismo hook, RLS se aplica en backend
```

## 🎨 Componentes UI

### OfficeSelector

Selector inteligente que:
- Muestra todas las oficinas si es admin nacional
- Muestra solo la oficina del usuario si no es admin
- Pre-selecciona automáticamente si hay una sola opción
- Deshabilita el campo si el usuario no puede cambiar

**Props:**
```tsx
interface OfficeSelectorProps {
  value: string;
  onChange: (officeId: string) => void;
  label?: string;
  required?: boolean;
  disabled?: boolean;
  error?: boolean;
  helperText?: string;
  fullWidth?: boolean;
  showInactive?: boolean;
}
```

### WarehouseSelector

Selector cascada que:
- Requiere una oficina seleccionada primero
- Filtra almacenes por la oficina
- Maneja estados de loading y error
- Muestra mensaje si no hay almacenes

**Props:** Similar a OfficeSelector + `officeId`

## 📝 Tipos TypeScript

Todos los tipos están fuertemente tipados:

```tsx
import type {
  Office,
  Warehouse,
  WarehouseLocation,
  TireSupplier,
  CreateOfficeRequest,
  UpdateWarehouseRequest,
  // ...etc
} from '@/features/organization';
```

### Tipos Principales

- `Office`: Oficina/Sede
- `Warehouse`: Almacén
- `WarehouseLocation`: Ubicación dentro de almacén
- `TireSupplier`: Proveedor de llantas
- `OfficeContext`: Interface del contexto

### Request/Response Types

- `Create*Request`: DTOs para crear entidades
- `Update*Request`: DTOs para actualizar entidades
- `*Filters`: Filtros de búsqueda
- `PageRequest`/`PageResponse`: Paginación

## 🧪 Testing

### Ejemplo de Test con React Testing Library

```tsx
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { OfficeProvider } from '@/features/organization';
import MyComponent from './MyComponent';

test('muestra oficina actual', async () => {
  const queryClient = new QueryClient();

  render(
    <QueryClientProvider client={queryClient}>
      <OfficeProvider>
        <MyComponent />
      </OfficeProvider>
    </QueryClientProvider>
  );

  // Assertions...
});
```

## 🚦 Manejo de Errores

Los hooks ya incluyen manejo de errores con toast notifications:

```tsx
const createOffice = useCreateOffice();

// onSuccess: toast.success automático
// onError: toast.error automático
await createOffice.mutateAsync(data);

// Manejo adicional si necesario
if (createOffice.isError) {
  console.error(createOffice.error);
}
```

## 📚 Recursos Adicionales

- **Backend API**: Ver documentación en `/backend/README.md`
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **React Query Docs**: https://tanstack.com/query/latest
- **Material-UI**: https://mui.com/

## ✅ Checklist de Integración

Al integrar este módulo:

**Setup Inicial:**
- [ ] Wrap app con `OfficeProvider`
- [ ] Importar tipos desde `@/features/organization`
- [ ] Configurar rutas para las páginas CRUD

**Desarrollo:**
- [ ] Usar hooks de React Query para data fetching
- [ ] Verificar permisos con `useOfficeContext()`
- [ ] Usar selectores para oficinas/almacenes en formularios
- [ ] Importar componentes de lista/formulario según necesidad

**Testing:**
- [ ] Probar con diferentes roles de usuario (ADMIN_NATIONAL, ADMIN_OFFICE, WAREHOUSE_MANAGER)
- [ ] Validar que RLS funcione correctamente (usuarios solo ven su oficina)
- [ ] Verificar que admin nacional ve todas las oficinas
- [ ] Probar filtros en cascada (Oficina → Almacén → Ubicación)
- [ ] Validar creación/edición de todas las entidades
- [ ] Verificar activar/desactivar y eliminación
- [ ] Manejar estados de loading y error
- [ ] Probar búsqueda y ordenamiento en DataGrids
