-- ============================================================================
-- Script para actualizar passwords de usuarios de prueba
-- ============================================================================
--
-- NOTA: Este script actualiza los passwords de los usuarios de prueba creados
-- en la migración V3.0.2. El hash actual en la migración no corresponde a
-- "Password123!" como se indica en los comentarios.
--
-- Para usar este script:
-- 1. Generar un hash válido para "Password123!" desde la aplicación Spring Boot
-- 2. Reemplazar el hash PLACEHOLDER_HASH con el hash generado
-- 3. Ejecutar este script
--
-- Alternativamente, puede usar el hash del usuario admin (password: Admin123!)
-- ============================================================================

BEGIN;

-- Opción 1: Usar el mismo password que el usuario admin (Admin123!)
UPDATE users
SET password_hash = (SELECT password_hash FROM users WHERE id = 1)
WHERE email IN (
    'admin.nacional@vortice.com',
    'admin.medellin@vortice.com',
    'gerente.almacen@vortice.com',
    'admin.cali@vortice.com'
);

-- Verificar cambios
SELECT
    id,
    username,
    email,
    substring(password_hash, 1, 30) as hash_preview
FROM users
WHERE email IN (
    'admin.nacional@vortice.com',
    'admin.medellin@vortice.com',
    'gerente.almacen@vortice.com',
    'admin.cali@vortice.com'
)
ORDER BY id;

COMMIT;

-- ============================================================================
-- IMPORTANTE: Después de ejecutar este script, todos los usuarios de prueba
-- tendrán el mismo password que el usuario admin: Admin123!
-- ============================================================================
