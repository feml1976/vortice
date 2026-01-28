-- ============================================================================
-- MIGRACIÓN V3.0.1: Habilitar Row-Level Security (RLS)
-- Descripción: Implementa políticas RLS para aislamiento de datos por oficina
-- Fecha: 2026-01-27
-- Autor: Sistema de Migración
-- ============================================================================

-- ============================================================================
-- FUNCIONES DE UTILIDAD PARA RLS
-- ============================================================================

-- Función: Obtener office_id del usuario actual
CREATE OR REPLACE FUNCTION get_user_office_id()
RETURNS UUID AS $$
DECLARE
    v_office_id UUID;
BEGIN
    -- Obtener office_id del usuario desde configuración de sesión
    SELECT office_id INTO v_office_id
    FROM users
    WHERE id = current_setting('app.current_user_id', true)::BIGINT;

    RETURN v_office_id;
EXCEPTION
    WHEN OTHERS THEN
        -- Si falla, retornar NULL para que RLS deniegue acceso
        RETURN NULL;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

COMMENT ON FUNCTION get_user_office_id() IS 'Obtiene el office_id del usuario actual desde la sesión de PostgreSQL';

-- Función: Verificar si el usuario actual tiene un rol específico
CREATE OR REPLACE FUNCTION current_user_has_role(role_name TEXT)
RETURNS BOOLEAN AS $$
DECLARE
    has_role BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM users u
        JOIN user_roles ur ON u.id = ur.user_id
        JOIN roles r ON ur.role_id = r.id
        WHERE u.id = current_setting('app.current_user_id', true)::BIGINT
          AND r.name = role_name
          AND u.is_active = true
    ) INTO has_role;

    RETURN COALESCE(has_role, false);
EXCEPTION
    WHEN OTHERS THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

COMMENT ON FUNCTION current_user_has_role(TEXT) IS 'Verifica si el usuario actual tiene un rol específico';

-- ============================================================================
-- HABILITAR RLS EN TABLAS
-- ============================================================================

-- RLS para warehouses
ALTER TABLE warehouses ENABLE ROW LEVEL SECURITY;

CREATE POLICY warehouses_office_isolation ON warehouses
    FOR ALL
    TO PUBLIC
    USING (
        -- Admin nacional: acceso total
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        -- Usuario normal: solo su oficina
        office_id = get_user_office_id()
    );

COMMENT ON POLICY warehouses_office_isolation ON warehouses IS 'Filtrado automático por oficina del usuario';

-- RLS para warehouse_locations
ALTER TABLE warehouse_locations ENABLE ROW LEVEL SECURITY;

CREATE POLICY locations_office_isolation ON warehouse_locations
    FOR ALL
    TO PUBLIC
    USING (
        -- Admin nacional: acceso total
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        -- Usuario normal: solo ubicaciones de almacenes de su oficina
        warehouse_id IN (
            SELECT id FROM warehouses
            WHERE office_id = get_user_office_id()
              AND deleted_at IS NULL
        )
    );

COMMENT ON POLICY locations_office_isolation ON warehouse_locations IS 'Filtrado automático por oficina del usuario via warehouse';

-- RLS para tire_suppliers
ALTER TABLE tire_suppliers ENABLE ROW LEVEL SECURITY;

CREATE POLICY suppliers_office_isolation ON tire_suppliers
    FOR ALL
    TO PUBLIC
    USING (
        -- Admin nacional: acceso total
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        -- Usuario normal: solo su oficina
        office_id = get_user_office_id()
    );

COMMENT ON POLICY suppliers_office_isolation ON tire_suppliers IS 'Filtrado automático por oficina del usuario';

-- ============================================================================
-- NOTA: offices NO tiene RLS
-- Razón: Los usuarios operan dentro de su oficina, no la consultan como dato.
--        Los admins nacionales la consultan manualmente con filtrado en la aplicación.
-- ============================================================================

-- ============================================================================
-- VALIDACIÓN DE RLS
-- ============================================================================

-- Verificar que RLS está habilitado en las tablas correctas
DO $$
DECLARE
    v_tables_with_rls INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_tables_with_rls
    FROM pg_tables pt
    WHERE pt.schemaname = 'public'
      AND pt.tablename IN ('warehouses', 'warehouse_locations', 'tire_suppliers')
      AND pt.rowsecurity = true;

    IF v_tables_with_rls = 3 THEN
        RAISE NOTICE 'RLS habilitado correctamente en 3 tablas: warehouses, warehouse_locations, tire_suppliers';
    ELSE
        RAISE WARNING 'RLS no está habilitado en todas las tablas esperadas. Tablas con RLS: %', v_tables_with_rls;
    END IF;
END $$;

-- Verificar que las políticas fueron creadas
DO $$
DECLARE
    v_policies_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_policies_count
    FROM pg_policies
    WHERE schemaname = 'public'
      AND tablename IN ('warehouses', 'warehouse_locations', 'tire_suppliers');

    IF v_policies_count = 3 THEN
        RAISE NOTICE 'Políticas RLS creadas correctamente: %', v_policies_count;
    ELSE
        RAISE WARNING 'Número de políticas creadas: % (esperado: 3)', v_policies_count;
    END IF;
END $$;

-- ============================================================================
-- FIN DE LA MIGRACIÓN V3.0.1
-- ============================================================================
