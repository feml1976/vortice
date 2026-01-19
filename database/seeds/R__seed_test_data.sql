


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

-- Insertar ciudades con códigos DIVIPOLA
-- Formato DIVIPOLA: 8 dígitos (2 dpto + 3 municipio + 3 subdivisión)
-- Fuente: DANE - División Político-Administrativa de Colombia

INSERT INTO cities (department_id, code, name, divipola_code, is_active)
SELECT d.id, '001', 'Bogotá', '11001000', true FROM departments d WHERE d.code = '11'
ON CONFLICT (department_id, code) DO UPDATE SET divipola_code = EXCLUDED.divipola_code;

INSERT INTO cities (department_id, code, name, divipola_code, is_active)
SELECT d.id, '001', 'Medellín', '05001000', true FROM departments d WHERE d.code = '05'
ON CONFLICT (department_id, code) DO UPDATE SET divipola_code = EXCLUDED.divipola_code;

INSERT INTO cities (department_id, code, name, divipola_code, is_active)
SELECT d.id, '817', 'Tocancipá', '25817000', true FROM departments d WHERE d.code = '25'
ON CONFLICT (department_id, code) DO UPDATE SET divipola_code = EXCLUDED.divipola_code;

-- Insertar oficinas
INSERT INTO offices (code, name, city_id, address, phone, is_active)
SELECT 'BOG01', 'Sede Principal Bogotá', c.id, 'Calle 100 # 20-30', '601-5551234', true
FROM cities c WHERE c.name = 'Bogotá'
ON CONFLICT (code) DO NOTHING;

INSERT INTO offices (code, name, city_id, address, phone, is_active)
SELECT 'TOC01', 'Sede Tocancipá', c.id, 'Km 5 vía Tocancipá', '601-8881234', true
FROM cities c WHERE c.name = 'Tocancipá'
ON CONFLICT (code) DO NOTHING;
