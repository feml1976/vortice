-- Script para limpiar datos de test entre test classes
-- Mantiene el esquema de Flyway pero elimina todos los datos de las tablas

-- Deshabilitar triggers temporalmente para evitar problemas de foreign keys
SET session_replication_role = 'replica';

-- Truncar tablas en orden correcto (respetando foreign keys)
TRUNCATE TABLE refresh_tokens CASCADE;
TRUNCATE TABLE user_roles CASCADE;
TRUNCATE TABLE role_permissions CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE roles CASCADE;
TRUNCATE TABLE permissions CASCADE;

-- Rehabilitar triggers
SET session_replication_role = 'origin';

-- Reset sequences si es necesario
-- (No aplicable para tablas con UUIDs o BIGSERIALs que Flyway maneja)
