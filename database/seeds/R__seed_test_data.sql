-- =====================================================
-- DATOS DE PRUEBA PARA DESARROLLO
-- Este script es REPEATABLE (R__) y se ejecuta cada vez que cambia
-- SOLO para ambiente de desarrollo
-- =====================================================

-- Insertar departamentos de Colombia (principales)
INSERT INTO departments (country_id, code, name, is_active)
SELECT c.id, '05', 'Antioquia', true FROM countries c WHERE c.code = 'COL'
ON CONFLICT DO NOTHING;

INSERT INTO departments (country_id, code, name, is_active)
SELECT c.id, '11', 'Bogotá D.C.', true FROM countries c WHERE c.code = 'COL'
ON CONFLICT DO NOTHING;

INSERT INTO departments (country_id, code, name, is_active)
SELECT c.id, '25', 'Cundinamarca', true FROM countries c WHERE c.code = 'COL'
ON CONFLICT DO NOTHING;

-- Insertar ciudades
INSERT INTO cities (department_id, code, name, is_active)
SELECT d.id, '001', 'Bogotá', true FROM departments d WHERE d.code = '11'
ON CONFLICT DO NOTHING;

INSERT INTO cities (department_id, code, name, is_active)
SELECT d.id, '001', 'Medellín', true FROM departments d WHERE d.code = '05'
ON CONFLICT DO NOTHING;

INSERT INTO cities (department_id, code, name, is_active)
SELECT d.id, '899', 'Tocancipá', true FROM departments d WHERE d.code = '25'
ON CONFLICT DO NOTHING;

-- Insertar oficinas
INSERT INTO offices (code, name, city_id, address, phone, is_active)
SELECT 'BOG01', 'Sede Principal Bogotá', c.id, 'Calle 100 # 20-30', '601-5551234', true
FROM cities c WHERE c.name = 'Bogotá'
ON CONFLICT (code) DO NOTHING;

INSERT INTO offices (code, name, city_id, address, phone, is_active)
SELECT 'TOC01', 'Sede Tocancipá', c.id, 'Km 5 vía Tocancipá', '601-8881234', true
FROM cities c WHERE c.name = 'Tocancipá'
ON CONFLICT (code) DO NOTHING;
