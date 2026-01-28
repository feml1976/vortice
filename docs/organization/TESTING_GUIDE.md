# Guía de Pruebas - Módulo de Estructura Organizacional

Este documento proporciona instrucciones para probar el módulo de Estructura Organizacional Multi-Sede con diferentes roles de usuario.

## 🚀 Configuración Inicial

### 1. Usuarios de Prueba

Para probar completamente el módulo, necesitas usuarios con los siguientes roles:

#### Admin Nacional (ADMIN_NATIONAL)
- **Email:** admin.nacional@vortice.com
- **Nombre:** Admin Nacional
- **Oficina:** MAIN (Bogotá)
- **Permisos:**
  - ✅ Gestionar Oficinas (CRUD completo)
  - ✅ Gestionar Almacenes (CRUD completo en todas las oficinas)
  - ✅ Gestionar Ubicaciones (CRUD completo)
  - ✅ Gestionar Proveedores (CRUD completo en todas las oficinas)
  - ✅ Ver todas las oficinas y datos

#### Admin de Oficina (ADMIN_OFFICE)
- **Email:** admin.medellin@vortice.com
- **Nombre:** Admin Medellín
- **Oficina:** MED (Medellín)
- **Permisos:**
  - ❌ NO puede gestionar Oficinas
  - ✅ Gestionar Almacenes (solo de su oficina)
  - ✅ Gestionar Ubicaciones (solo de su oficina)
  - ✅ Gestionar Proveedores (solo de su oficina)
  - ⚠️ Solo ve datos de su oficina (RLS activo)

#### Gerente de Almacén (WAREHOUSE_MANAGER)
- **Email:** gerente.almacen@vortice.com
- **Nombre:** Gerente Almacén
- **Oficina:** MED (Medellín)
- **Permisos:**
  - ❌ NO puede gestionar Oficinas
  - ❌ NO puede gestionar Almacenes
  - ✅ Gestionar Ubicaciones (solo de su oficina)
  - ❌ NO puede gestionar Proveedores
  - ⚠️ Solo ve datos de su oficina (RLS activo)

### 2. SQL para Crear Usuarios de Prueba

Ejecuta este script en PostgreSQL para crear los usuarios de prueba:

```sql
-- Nota: Las contraseñas están hasheadas con BCrypt (Password123!)
-- Hash BCrypt para "Password123!": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- 1. Admin Nacional
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active, is_email_verified
) VALUES (
  'admin.nacional',
  'admin.nacional@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Admin',
  'Nacional',
  (SELECT id FROM offices WHERE code = 'MAIN' LIMIT 1),
  true,
  true
);

-- Asignar rol ADMIN_NATIONAL
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'ADMIN_NATIONAL'
FROM users
WHERE email = 'admin.nacional@vortice.com';

-- 2. Admin de Oficina Medellín
-- Primero, crear la oficina de Medellín si no existe
INSERT INTO offices (code, name, city, is_active)
VALUES ('MED', 'Medellín', 'Medellín', true)
ON CONFLICT (code) DO NOTHING;

-- Crear usuario Admin Medellín
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active, is_email_verified
) VALUES (
  'admin.medellin',
  'admin.medellin@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Admin',
  'Medellín',
  (SELECT id FROM offices WHERE code = 'MED' LIMIT 1),
  true,
  true
);

-- Asignar rol ADMIN_OFFICE
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'ADMIN_OFFICE'
FROM users
WHERE email = 'admin.medellin@vortice.com';

-- 3. Gerente de Almacén
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active, is_email_verified
) VALUES (
  'gerente.almacen',
  'gerente.almacen@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Gerente',
  'Almacén',
  (SELECT id FROM offices WHERE code = 'MED' LIMIT 1),
  true,
  true
);

-- Asignar rol WAREHOUSE_MANAGER
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'WAREHOUSE_MANAGER'
FROM users
WHERE email = 'gerente.almacen@vortice.com';

-- Verificar usuarios creados
SELECT
  u.username,
  u.email,
  u.first_name,
  u.last_name,
  o.code as office_code,
  o.name as office_name,
  STRING_AGG(ur.role_name, ', ') as roles
FROM users u
LEFT JOIN offices o ON u.office_id = o.id
LEFT JOIN user_roles ur ON u.id = ur.user_id
WHERE u.email IN (
  'admin.nacional@vortice.com',
  'admin.medellin@vortice.com',
  'gerente.almacen@vortice.com'
)
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, o.code, o.name;
```

### 3. Datos de Prueba Adicionales

Para facilitar las pruebas, puedes crear almacenes y ubicaciones de ejemplo:

```sql
-- Almacenes para Bogotá (MAIN)
INSERT INTO warehouses (code, name, office_id, is_active)
VALUES
  ('ALM01', 'Almacén Central Bogotá', (SELECT id FROM offices WHERE code = 'MAIN'), true),
  ('ALM02', 'Almacén Norte Bogotá', (SELECT id FROM offices WHERE code = 'MAIN'), true);

-- Almacenes para Medellín (MED)
INSERT INTO warehouses (code, name, office_id, is_active)
VALUES
  ('ALM01', 'Almacén Principal Medellín', (SELECT id FROM offices WHERE code = 'MED'), true),
  ('ALM02', 'Almacén Sur Medellín', (SELECT id FROM offices WHERE code = 'MED'), true);

-- Ubicaciones para almacenes de Bogotá
INSERT INTO warehouse_locations (code, description, warehouse_id, is_active)
SELECT 'A1', 'Estantería A - Nivel 1', id, true
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MAIN');

INSERT INTO warehouse_locations (code, description, warehouse_id, is_active)
SELECT 'B2', 'Estantería B - Nivel 2', id, true
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MAIN');

-- Ubicaciones para almacenes de Medellín
INSERT INTO warehouse_locations (code, description, warehouse_id, is_active)
SELECT 'A1', 'Pasillo A - Nivel 1', id, true
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MED');

-- Proveedores para Bogotá
INSERT INTO tire_suppliers (code, name, nit, contact_name, phone, city, office_id, is_active)
VALUES
  ('PROV01', 'Michelin Colombia', '900123456-1', 'Carlos Pérez', '3001234567', 'Bogotá',
   (SELECT id FROM offices WHERE code = 'MAIN'), true),
  ('PROV02', 'Goodyear Bogotá', '900234567-2', 'María González', '3002345678', 'Bogotá',
   (SELECT id FROM offices WHERE code = 'MAIN'), true);

-- Proveedores para Medellín
INSERT INTO tire_suppliers (code, name, nit, contact_name, phone, city, office_id, is_active)
VALUES
  ('PROV01', 'Bridgestone Medellín', '900345678-3', 'Juan Ramírez', '3003456789', 'Medellín',
   (SELECT id FROM offices WHERE code = 'MED'), true);
```

## 🧪 Plan de Pruebas

### Prueba 1: Admin Nacional

**Login:** admin.nacional@vortice.com / Password123!

**Verificar:**
1. ✅ Menú "Organización" visible en el sidebar
2. ✅ Acceso a todas las páginas:
   - `/organization/offices`
   - `/organization/warehouses`
   - `/organization/locations`
   - `/organization/suppliers`

**En Página de Oficinas:**
1. ✅ Ve TODAS las oficinas (MAIN, MED, etc.)
2. ✅ Puede crear nueva oficina
3. ✅ Puede editar cualquier oficina
4. ✅ Puede activar/desactivar oficinas
5. ✅ Puede eliminar oficinas (si no tienen dependencias)
6. ✅ Ve detalles completos (total almacenes, ubicaciones, proveedores)

**En Página de Almacenes:**
1. ✅ Filtro de oficina visible
2. ✅ Ve almacenes de todas las oficinas
3. ✅ Puede crear almacén en cualquier oficina
4. ✅ Puede editar/activar/desactivar/eliminar almacenes

**En Página de Ubicaciones:**
1. ✅ Filtros de oficina y almacén visibles
2. ✅ Ve todas las ubicaciones
3. ✅ Puede crear/editar/eliminar ubicaciones

**En Página de Proveedores:**
1. ✅ Filtro de oficina visible
2. ✅ Ve proveedores de todas las oficinas
3. ✅ Puede crear/editar/eliminar proveedores

### Prueba 2: Admin de Oficina

**Login:** admin.medellin@vortice.com / Password123!

**Verificar:**
1. ✅ Menú "Organización" visible

**En Página de Oficinas:**
1. ✅ Solo ve su oficina (MED - Medellín)
2. ❌ Botón "Nueva Oficina" NO visible
3. ❌ Botones de editar/eliminar NO visibles
4. ✅ Puede ver detalles de su oficina

**En Página de Almacenes:**
1. ❌ Filtro de oficina NO visible (o deshabilitado con solo su oficina)
2. ✅ Solo ve almacenes de Medellín (RLS activo)
3. ✅ Puede crear almacén (solo en su oficina)
4. ✅ Puede editar/activar/desactivar/eliminar almacenes de su oficina

**En Página de Ubicaciones:**
1. ❌ Filtro de oficina NO visible o solo muestra Medellín
2. ✅ Solo ve ubicaciones de almacenes de Medellín
3. ✅ Puede crear/editar/eliminar ubicaciones

**En Página de Proveedores:**
1. ❌ Filtro de oficina NO visible o solo muestra Medellín
2. ✅ Solo ve proveedores de Medellín (RLS activo)
3. ✅ Puede crear/editar/eliminar proveedores

### Prueba 3: Gerente de Almacén

**Login:** gerente.almacen@vortice.com / Password123!

**Verificar:**
1. ✅ Menú "Organización" visible

**En Página de Oficinas:**
1. ✅ Solo ve su oficina (MED)
2. ❌ Botón "Nueva Oficina" NO visible
3. ❌ Botones de editar/eliminar NO visibles
4. ✅ Puede ver detalles de su oficina

**En Página de Almacenes:**
1. ✅ Solo ve almacenes de Medellín
2. ❌ Botón "Nuevo Almacén" NO visible
3. ❌ Botones de editar/activar/desactivar/eliminar NO visibles
4. ✅ Puede ver detalles de almacenes

**En Página de Ubicaciones:**
1. ✅ Solo ve ubicaciones de almacenes de Medellín
2. ✅ Puede crear ubicación
3. ✅ Puede editar/activar/desactivar/eliminar ubicaciones
4. ✅ ÚNICO permiso de escritura

**En Página de Proveedores:**
1. ✅ Solo ve proveedores de Medellín
2. ❌ Botón "Nuevo Proveedor" NO visible
3. ❌ Botones de editar/activar/desactivar/eliminar NO visibles
4. ⚠️ Solo lectura

## 🔍 Pruebas de Seguridad (RLS)

### Verificar Row-Level Security

Intenta estas acciones para verificar que RLS funciona:

1. **Como Admin de Oficina (Medellín):**
   - Intenta acceder directamente a un almacén de Bogotá via URL
   - Debería recibir error 403 o no ver datos

2. **Prueba con API directa:**
```bash
# Login como Admin Medellín
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin.medellin@vortice.com","password":"Password123!"}'

# Guardar el token

# Intentar obtener almacenes (debería ver solo de Medellín)
curl -X GET http://localhost:8080/api/v1/warehouses \
  -H "Authorization: Bearer {token}"
```

## 📊 Casos de Prueba Adicionales

### Validaciones de Formularios

1. **Código único por contexto:**
   - Crear dos almacenes con código "ALM01" en diferentes oficinas → ✅ Permitido
   - Crear dos almacenes con código "ALM01" en la MISMA oficina → ❌ Error

2. **Campos inmutables:**
   - Editar oficina: campo "código" deshabilitado → ✅
   - Editar almacén: campo "código" y "oficina" deshabilitados → ✅

3. **Soft Delete:**
   - Eliminar oficina con almacenes activos → ❌ Error
   - Desactivar oficina → ✅ Permitido

### Filtros y Búsqueda

1. Buscar por código, nombre, ciudad
2. Filtrar por estado activo/inactivo
3. Ordenar por diferentes columnas
4. Paginación con diferentes tamaños

## ✅ Checklist de Pruebas Completo

**Setup:**
- [ ] Usuarios de prueba creados correctamente
- [ ] Datos de ejemplo insertados
- [ ] Backend ejecutándose en http://localhost:8080
- [ ] Frontend ejecutándose en http://localhost:5173

**Funcionalidad:**
- [ ] Admin Nacional: CRUD completo de oficinas
- [ ] Admin Nacional: Ve todas las oficinas y datos
- [ ] Admin de Oficina: No puede gestionar oficinas
- [ ] Admin de Oficina: Solo ve datos de su oficina (RLS)
- [ ] Gerente de Almacén: Solo puede gestionar ubicaciones
- [ ] Gerente de Almacén: Solo ve datos de su oficina (RLS)

**UI/UX:**
- [ ] Menú de navegación muestra "Organización"
- [ ] Breadcrumbs correctos en todas las páginas
- [ ] DataGrid carga y muestra datos
- [ ] Formularios validan correctamente
- [ ] Toasts de éxito/error se muestran
- [ ] Estados de loading visibles
- [ ] Responsive en móvil

**Seguridad:**
- [ ] RLS funciona correctamente
- [ ] No se puede acceder a datos de otras oficinas
- [ ] Permisos respetados en todos los componentes
- [ ] Tokens JWT requeridos para todas las operaciones

## 🐛 Reporte de Bugs

Si encuentras bugs durante las pruebas, documenta:

1. **Usuario:** ¿Con qué rol?
2. **Página:** ¿Dónde ocurrió?
3. **Acción:** ¿Qué estabas haciendo?
4. **Esperado:** ¿Qué debería pasar?
5. **Actual:** ¿Qué pasó realmente?
6. **Logs:** Consola del navegador y logs del backend
