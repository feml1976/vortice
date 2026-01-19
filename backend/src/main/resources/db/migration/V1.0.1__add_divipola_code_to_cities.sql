-- =====================================================
-- MIGRACIÓN V1.0.1: AGREGAR CÓDIGO DIVIPOLA A CITIES
-- Sistema Vórtice - Gestión de Taller
-- Fecha: 2026-01-19
-- =====================================================

-- Agregar columna divipola_code a la tabla cities
-- DIVIPOLA: División Político-Administrativa de Colombia (DANE)
-- Formato: 8 dígitos
--   - 2 dígitos: Código del departamento
--   - 3 dígitos: Código del municipio
--   - 3 dígitos: Subdivisión/extensión (000 para cabecera municipal)
ALTER TABLE cities
ADD COLUMN divipola_code CHAR(8);

-- Crear restricción UNIQUE para el código DIVIPOLA
-- Cada código DIVIPOLA debe ser único en el sistema
ALTER TABLE cities
ADD CONSTRAINT uk_cities_divipola_code UNIQUE (divipola_code);

-- Crear índice para optimizar búsquedas por código DIVIPOLA
CREATE INDEX idx_cities_divipola_code ON cities(divipola_code);

-- Agregar restricción CHECK para validar formato
-- Debe contener exactamente 8 dígitos numéricos
ALTER TABLE cities
ADD CONSTRAINT chk_cities_divipola_format
CHECK (divipola_code ~ '^[0-9]{8}$');

-- Comentario descriptivo de la columna
COMMENT ON COLUMN cities.divipola_code IS
'Código DIVIPOLA extendido del DANE (8 dígitos: 2 dpto + 3 municipio + 3 subdivisión/extensión). Formato estándar para identificación de municipios en Colombia.';

-- =====================================================
-- FIN DE LA MIGRACIÓN V1.0.1
-- =====================================================
