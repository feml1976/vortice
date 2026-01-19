-- =====================================================
-- MIGRACIÓN V1.0.3: CORREGIR PASSWORD DEL ADMIN
-- Sistema Vórtice - Gestión de Taller
-- Fecha: 2026-01-19
-- =====================================================

-- Actualizar el password del usuario admin con un hash BCrypt válido
-- Password: Admin123!
-- Hash generado con BCrypt (strength 10)
UPDATE users
SET password_hash = '$2a$10$rBV2xSjTmI4bCEqHd9VkI.4mVzHZFP/HkRuD0nY5F5H.g5zU5k.wC',
    updated_at = NOW()
WHERE username = 'admin';

-- Comentario: El password por defecto del admin es "Admin123!"
-- IMPORTANTE: Este password debe cambiarse en producción

-- =====================================================
-- FIN DE LA MIGRACIÓN V1.0.3
-- =====================================================
