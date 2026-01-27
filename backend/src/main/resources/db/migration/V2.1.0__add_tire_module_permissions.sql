-- =====================================================
-- AGREGAR PERMISOS DEL MÓDULO DE LLANTAS
-- =====================================================
-- Fecha: 2026-01-26
-- Descripción: Agrega los permisos necesarios para el módulo de
--              Especificaciones Técnicas de Llantas y los asigna
--              al rol ADMIN
-- =====================================================

-- PASO 1: Insertar permisos para el módulo de llantas
INSERT INTO permissions (name, resource, action, description, created_at) VALUES
('TIRE_SPECIFICATION_VIEW', 'TIRE_SPECIFICATION', 'VIEW', 'Permite ver y listar especificaciones técnicas de llantas', CURRENT_TIMESTAMP),
('TIRE_SPECIFICATION_CREATE', 'TIRE_SPECIFICATION', 'CREATE', 'Permite crear nuevas especificaciones técnicas de llantas', CURRENT_TIMESTAMP),
('TIRE_SPECIFICATION_UPDATE', 'TIRE_SPECIFICATION', 'UPDATE', 'Permite actualizar especificaciones técnicas existentes', CURRENT_TIMESTAMP),
('TIRE_SPECIFICATION_DELETE', 'TIRE_SPECIFICATION', 'DELETE', 'Permite eliminar especificaciones técnicas de llantas', CURRENT_TIMESTAMP);

-- PASO 2: Asignar todos los permisos del módulo de llantas al rol ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name IN (
    'TIRE_SPECIFICATION_VIEW',
    'TIRE_SPECIFICATION_CREATE',
    'TIRE_SPECIFICATION_UPDATE',
    'TIRE_SPECIFICATION_DELETE'
  );

-- PASO 3: Verificación - Mostrar permisos asignados al rol ADMIN
SELECT
    r.name AS rol,
    p.name AS permiso,
    p.resource AS recurso,
    p.action AS accion,
    p.description AS descripcion
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ADMIN'
  AND p.resource = 'TIRE_SPECIFICATION'
ORDER BY p.action;

-- =====================================================
-- FIN DE LA MIGRACIÓN V2.1.0
-- =====================================================
