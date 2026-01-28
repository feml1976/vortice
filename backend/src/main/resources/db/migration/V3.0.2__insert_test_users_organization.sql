--
-- Inserta usuarios de prueba para el módulo de Estructura Organizacional
-- Contraseña para todos: Password123!
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
--

-- =====================================================
-- CREAR OFICINAS ADICIONALES
-- =====================================================

-- Oficina Medellín
INSERT INTO offices (code, name, city, is_active, created_by)
VALUES ('MED', 'Medellín', 'Medellín', true, 1)
ON CONFLICT (code) DO NOTHING;

-- Oficina Cali
INSERT INTO offices (code, name, city, is_active, created_by)
VALUES ('CALI', 'Cali', 'Cali', true, 1)
ON CONFLICT (code) DO NOTHING;

-- =====================================================
-- USUARIOS DE PRUEBA
-- =====================================================

-- 1. Admin Nacional (acceso a todas las oficinas)
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active,
  created_at, created_by
) VALUES (
  'admin.nacional',
  'admin.nacional@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Admin',
  'Nacional',
  (SELECT id FROM offices WHERE code = 'MAIN' LIMIT 1),
  true,
  CURRENT_TIMESTAMP,
  1
)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol ADMIN_NATIONAL
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'ADMIN_NATIONAL'
FROM users
WHERE email = 'admin.nacional@vortice.com'
ON CONFLICT DO NOTHING;

-- 2. Admin de Oficina Medellín
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active,
  created_at, created_by
) VALUES (
  'admin.medellin',
  'admin.medellin@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Admin',
  'Medellín',
  (SELECT id FROM offices WHERE code = 'MED' LIMIT 1),
  true,
  CURRENT_TIMESTAMP,
  1
)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol ADMIN_OFFICE
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'ADMIN_OFFICE'
FROM users
WHERE email = 'admin.medellin@vortice.com'
ON CONFLICT DO NOTHING;

-- 3. Gerente de Almacén Medellín
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active,
  created_at, created_by
) VALUES (
  'gerente.almacen',
  'gerente.almacen@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Gerente',
  'Almacén',
  (SELECT id FROM offices WHERE code = 'MED' LIMIT 1),
  true,
  CURRENT_TIMESTAMP,
  1
)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol WAREHOUSE_MANAGER
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'WAREHOUSE_MANAGER'
FROM users
WHERE email = 'gerente.almacen@vortice.com'
ON CONFLICT DO NOTHING;

-- 4. Admin de Oficina Cali
INSERT INTO users (
  username, email, password_hash,
  first_name, last_name,
  office_id,
  is_active,
  created_at, created_by
) VALUES (
  'admin.cali',
  'admin.cali@vortice.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Admin',
  'Cali',
  (SELECT id FROM offices WHERE code = 'CALI' LIMIT 1),
  true,
  CURRENT_TIMESTAMP,
  1
)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol ADMIN_OFFICE
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'ADMIN_OFFICE'
FROM users
WHERE email = 'admin.cali@vortice.com'
ON CONFLICT DO NOTHING;

-- =====================================================
-- DATOS DE EJEMPLO
-- =====================================================

-- Almacenes para Bogotá (MAIN)
INSERT INTO warehouses (code, name, office_id, is_active, created_at, created_by)
VALUES
  ('ALM01', 'Almacén Central Bogotá', (SELECT id FROM offices WHERE code = 'MAIN'), true, CURRENT_TIMESTAMP, 1),
  ('ALM02', 'Almacén Norte Bogotá', (SELECT id FROM offices WHERE code = 'MAIN'), true, CURRENT_TIMESTAMP, 1)
ON CONFLICT (code, office_id) DO NOTHING;

-- Almacenes para Medellín (MED)
INSERT INTO warehouses (code, name, office_id, is_active, created_at, created_by)
VALUES
  ('ALM01', 'Almacén Principal Medellín', (SELECT id FROM offices WHERE code = 'MED'), true, CURRENT_TIMESTAMP, 1),
  ('ALM02', 'Almacén Sur Medellín', (SELECT id FROM offices WHERE code = 'MED'), true, CURRENT_TIMESTAMP, 1)
ON CONFLICT (code, office_id) DO NOTHING;

-- Almacenes para Cali (CALI)
INSERT INTO warehouses (code, name, office_id, is_active, created_at, created_by)
VALUES
  ('ALM01', 'Almacén Principal Cali', (SELECT id FROM offices WHERE code = 'CALI'), true, CURRENT_TIMESTAMP, 1)
ON CONFLICT (code, office_id) DO NOTHING;

-- Ubicaciones para almacenes de Bogotá
INSERT INTO warehouse_locations (code, description, warehouse_id, is_active, created_at, created_by)
SELECT 'A1', 'Estantería A - Nivel 1', id, true, CURRENT_TIMESTAMP, 1
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MAIN')
ON CONFLICT (code, warehouse_id) DO NOTHING;

INSERT INTO warehouse_locations (code, description, warehouse_id, is_active, created_at, created_by)
SELECT 'B2', 'Estantería B - Nivel 2', id, true, CURRENT_TIMESTAMP, 1
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MAIN')
ON CONFLICT (code, warehouse_id) DO NOTHING;

INSERT INTO warehouse_locations (code, description, warehouse_id, is_active, created_at, created_by)
SELECT 'C3', 'Estantería C - Nivel 3', id, true, CURRENT_TIMESTAMP, 1
FROM warehouses WHERE code = 'ALM02' AND office_id = (SELECT id FROM offices WHERE code = 'MAIN')
ON CONFLICT (code, warehouse_id) DO NOTHING;

-- Ubicaciones para almacenes de Medellín
INSERT INTO warehouse_locations (code, description, warehouse_id, is_active, created_at, created_by)
SELECT 'A1', 'Pasillo A - Nivel 1', id, true, CURRENT_TIMESTAMP, 1
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MED')
ON CONFLICT (code, warehouse_id) DO NOTHING;

INSERT INTO warehouse_locations (code, description, warehouse_id, is_active, created_at, created_by)
SELECT 'B1', 'Pasillo B - Nivel 1', id, true, CURRENT_TIMESTAMP, 1
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'MED')
ON CONFLICT (code, warehouse_id) DO NOTHING;

-- Ubicaciones para almacenes de Cali
INSERT INTO warehouse_locations (code, description, warehouse_id, is_active, created_at, created_by)
SELECT 'A1', 'Zona A - Nivel 1', id, true, CURRENT_TIMESTAMP, 1
FROM warehouses WHERE code = 'ALM01' AND office_id = (SELECT id FROM offices WHERE code = 'CALI')
ON CONFLICT (code, warehouse_id) DO NOTHING;

-- Proveedores para Bogotá
INSERT INTO tire_suppliers (code, name, tax_id, contact_name, phone, email, address, office_id, is_active, created_at, created_by)
VALUES
  ('PROV01', 'Michelin Colombia', '900123456-1', 'Carlos Pérez', '3001234567', 'carlos@michelin.com', 'Bogotá',
   (SELECT id FROM offices WHERE code = 'MAIN'), true, CURRENT_TIMESTAMP, 1),
  ('PROV02', 'Goodyear Bogotá', '900234567-2', 'María González', '3002345678', 'maria@goodyear.com', 'Bogotá',
   (SELECT id FROM offices WHERE code = 'MAIN'), true, CURRENT_TIMESTAMP, 1),
  ('PROV03', 'Continental Colombia', '900345678-3', 'Luis Martínez', '3003456789', 'luis@continental.com', 'Bogotá',
   (SELECT id FROM offices WHERE code = 'MAIN'), true, CURRENT_TIMESTAMP, 1)
ON CONFLICT (code, office_id) DO NOTHING;

-- Proveedores para Medellín
INSERT INTO tire_suppliers (code, name, tax_id, contact_name, phone, email, address, office_id, is_active, created_at, created_by)
VALUES
  ('PROV01', 'Bridgestone Medellín', '800456789-4', 'Juan Ramírez', '3004567890', 'juan@bridgestone.com', 'Medellín',
   (SELECT id FROM offices WHERE code = 'MED'), true, CURRENT_TIMESTAMP, 1),
  ('PROV02', 'Pirelli Medellín', '800567890-5', 'Ana López', '3005678901', 'ana@pirelli.com', 'Medellín',
   (SELECT id FROM offices WHERE code = 'MED'), true, CURRENT_TIMESTAMP, 1)
ON CONFLICT (code, office_id) DO NOTHING;

-- Proveedores para Cali
INSERT INTO tire_suppliers (code, name, tax_id, contact_name, phone, email, address, office_id, is_active, created_at, created_by)
VALUES
  ('PROV01', 'Yokohama Cali', '700678901-6', 'Pedro Sánchez', '3006789012', 'pedro@yokohama.com', 'Cali',
   (SELECT id FROM offices WHERE code = 'CALI'), true, CURRENT_TIMESTAMP, 1)
ON CONFLICT (code, office_id) DO NOTHING;

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Mostrar usuarios creados con sus roles y oficinas
DO $$
BEGIN
  RAISE NOTICE '';
  RAISE NOTICE '==============================================';
  RAISE NOTICE 'USUARIOS DE PRUEBA CREADOS EXITOSAMENTE';
  RAISE NOTICE '==============================================';
  RAISE NOTICE '';
  RAISE NOTICE 'Credenciales: Email / Password123!';
  RAISE NOTICE '';
  RAISE NOTICE '1. admin.nacional@vortice.com - ADMIN_NATIONAL (Oficina: MAIN)';
  RAISE NOTICE '2. admin.medellin@vortice.com - ADMIN_OFFICE (Oficina: MED)';
  RAISE NOTICE '3. gerente.almacen@vortice.com - WAREHOUSE_MANAGER (Oficina: MED)';
  RAISE NOTICE '4. admin.cali@vortice.com - ADMIN_OFFICE (Oficina: CALI)';
  RAISE NOTICE '';
  RAISE NOTICE '==============================================';
  RAISE NOTICE '';
END $$;
