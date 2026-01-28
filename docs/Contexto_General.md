# PROMPT MASTER PARA DESARROLLO CON IA
## Sistema TRANSER Modernizado - Guía Completa para Desarrollo Asistido por IA

**Versión:** 1.2
**Fecha:** 22 de Enero de 2026
**Compatible con:** Claude (Anthropic)

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Context Setting Base](#2-context-setting-base)
---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento proporciona una guia para el  desarrollo del sistema TRANSER - Vortice modernizado con asistencia de IA (Claude). La guia esta diseñada para:

- ✅ Maximizar la calidad del código generado
- ✅ Mantener consistencia arquitectónica
- ✅ Acelerar el desarrollo sin comprometer calidad
- ✅ Facilitar aprendizaje del equipo

### 1.2 Cómo Usar Este Documento

1. **Antes de iniciar desarrollo:** Establecer contexto base (sección 2)
2. **Durante desarrollo:** Usar prompts específicos por fase (sección 3)
3. **Iteración:** Refinar outputs con prompts de seguimiento

---

## 2. CONTEXT SETTING BASE

### 2.1 CONTEXTO DEL PROYECTO: Sistema de Informacion Milenio Operativo TRANSER Vortice

## Sobre el Proyecto
Estoy desarrollando la modernización de nuestro Sistema de Informacion Milenio Operativo, un ERP para empresa de transporte de carga en Colombia.
El sistema actual está en desarrollado con Oracle Forms 6i (obsoleto) y lo estamos modernizando a un stack moderno.

## Stack Tecnológico
- **Backend:** Java 21 + Spring Boot 3.5
- **Frontend:** React 18 + TypeScript + Material-UI
- **Base de Datos:** PostgreSQL 18
- **Arquitectura:** Monolito modular con separación por bounded contexts (DDD)
- **Build:** Maven (backend), Vite (frontend)

## Arquitectura
- **Estilo:** Clean Architecture / Hexagonal
- **Capas:**
  1. Presentation (Controllers, DTOs)
  2. Application (Use Cases, Services)
  3. Domain (Entities, Value Objects, Business Rules)
  4. Infrastructure (JPA, External APIs, File System)

- **Módulos principales:**
  - Auth (Autenticacion)
  - User (Usuarios)
  - Share (Catalogos)
  - Tire (Llantas)
  - Purchasing (Compras)
  - Inventory (Inventarios)
  - Workshop (Taller)

## Convenciones de Código

### Backend (Java)
- Nomenclatura: camelCase para variables/métodos, PascalCase para clases
- Paquetes: com.transer.vortice.[module].[layer]
- No usar `@Autowired` en campos, usar constructor injection
- Preferir records para DTOs inmutables
- Usar Lombok solo para @Getter, @Setter, @Builder (evitar @Data)
- Validación con Jakarta Validation (@NotNull, @NotBlank, etc.)

### Frontend (TypeScript/React)
- Nomenclatura: camelCase para variables/funciones, PascalCase para componentes
- Hooks personalizados con prefijo `use`
- Props con interface, no type alias
- Preferir function components, NO class components
- Estado global con Zustand, estado del servidor con React Query

### Base de Datos (PostgreSQL)
- Nombres de tablas: plural, snake_case (ej: `work_orders`)
- Columnas: singular, snake_case (ej: `created_at`)
- Primary keys: `id` (UUID para entidades principales, BIGSERIAL para secundarias)
- Foreign keys: `[tabla_singular]_id` (ej: `vehicle_id`)
- Timestamps: `created_at`, `updated_at`, `deleted_at` (TIMESTAMP WITH TIME ZONE)
- Auditoría: `created_by`, `updated_by`, `deleted_by` (BIGINT referencias a users)
- Soft deletes con `deleted_at IS NULL`
- Boolean: `is_[adjetivo]` (ej: `is_active`)

## Principios de Desarrollo
1. **YAGNI:** No implementar funcionalidad que no se necesita ahora
2. **DRY:** No repetir código, pero no abstraer prematuramente
3. **SOLID:** Especialmente Single Responsibility y Dependency Inversion
4. **Testing:** Unit tests para lógica de negocio, integration tests para use cases
5. **Seguridad:** Validar inputs, no confiar en el cliente, usar prepared statements
6. **Multi-Tenancy:** Implementar aislamiento de datos por oficina usando Row-Level Security

## Arquitectura Multi-Sede (Multi-Tenant)

El sistema soporta múltiples sedes/oficinas operando de forma independiente. Este es un cambio arquitectónico clave respecto al sistema legacy Oracle Forms.

### Principios de Multi-Tenancy

**1. Aislamiento de Datos:**
- Cada usuario pertenece a UNA oficina (`users.office_id`)
- Los usuarios ven SOLO datos de su oficina (transparente via RLS en PostgreSQL)
- Excepción: `ROLE_ADMIN_NATIONAL` puede ver todas las oficinas

**2. Tipos de Datos:**
- **Globales:** Compartidos entre todas las oficinas (ej: fichas técnicas, marcas, tipos)
- **Específicos por Oficina:** Cada oficina tiene sus propios datos (ej: almacenes, proveedores, inventario)

**3. Row-Level Security (RLS):**
- Implementado en PostgreSQL para filtrado automático
- La aplicación NO debe filtrar manualmente por `office_id` en queries
- RLS se encarga de forma transparente

### Implementación de RLS

**Paso 1: Función para obtener oficina del usuario actual**
```sql
CREATE OR REPLACE FUNCTION get_user_office_id()
RETURNS UUID AS $$
DECLARE
    v_office_id UUID;
BEGIN
    SELECT office_id INTO v_office_id
    FROM users
    WHERE id = current_setting('app.current_user_id')::BIGINT;
    RETURN v_office_id;
END;
$$ LANGUAGE plpgsql STABLE;

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
        WHERE u.id = current_setting('app.current_user_id')::BIGINT
          AND r.name = role_name
    ) INTO has_role;
    RETURN has_role;
END;
$$ LANGUAGE plpgsql STABLE;
```

**Paso 2: Habilitar RLS en tabla**
```sql
ALTER TABLE tire_inventory ENABLE ROW LEVEL SECURITY;
```

**Paso 3: Crear política de aislamiento**
```sql
CREATE POLICY tire_inventory_office_isolation ON tire_inventory
    FOR ALL
    TO authenticated_user
    USING (
        -- Admin nacional: acceso total
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        -- Usuario normal: solo su oficina
        warehouse_id IN (
            SELECT w.id FROM warehouses w
            WHERE w.office_id = get_user_office_id()
              AND w.deleted_at IS NULL
        )
    );
```

**Paso 4: Setear contexto en cada request (Spring Boot)**
```java
@Component
public class RLSContextFilter extends OncePerRequestFilter {

    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Long userId = ((UserPrincipal) auth.getPrincipal()).getId();

            // Setear user_id en sesión de PostgreSQL
            jdbcTemplate.execute(
                "SET LOCAL app.current_user_id = " + userId
            );
        }

        filterChain.doFilter(request, response);
    }
}
```

### Tablas que Requieren RLS

**Con Filtrado por Oficina (via warehouse_id → office_id):**
- `tire_inventory`
- `tire_active`
- `tire_intermediate`
- `tire_retired`
- `tire_history`
- `vehicles`
- `work_orders`

**Con Filtrado Directo por office_id:**
- `warehouses`
- `tire_suppliers`
- `purchase_orders`

**Sin Filtrado (Globales):**
- `tire_specifications`
- `tire_brands`
- `tire_types`
- `tire_references`
- `observation_reasons`
- `users` (filtrado manual por seguridad)

### Reglas de Negocio Multi-Sede

**RN-MULTISEDE-001: Validación de Pertenencia**
- Antes de crear/modificar registro, validar que todas las FKs pertenezcan a la misma oficina
- Ejemplo: Al crear `tire_inventory`, validar que `supplier_id` y `warehouse_id` sean de la misma oficina

**RN-MULTISEDE-002: Traslados Entre Sedes**
- Los traslados de activos entre oficinas requieren proceso especial con aprobación
- No se permite mover directamente (requiere workflow)

**RN-MULTISEDE-003: Reportes Consolidados**
- Solo usuarios con `ROLE_ADMIN_NATIONAL` pueden generar reportes consolidados
- Los reportes deben incluir columna de oficina para distinguir origen

**RN-MULTISEDE-004: Auditoría Multi-Sede**
- Los campos de auditoría (`created_by`, `updated_by`) referencian tabla `users`
- No es necesario guardar `office_id` en cada tabla (se infiere via usuario)

### Testing Multi-Sede

**Tests Obligatorios:**
1. **Test de Aislamiento:**
   - Usuario de oficina A no puede ver datos de oficina B
   - Usuario de oficina A no puede modificar datos de oficina B

2. **Test de Admin Nacional:**
   - Admin puede ver todas las oficinas
   - Admin puede filtrar por oficina específica

3. **Test de Integridad:**
   - Intentar crear registro con FKs de diferentes oficinas (debe fallar)
   - Validar que RLS policies se aplican correctamente

4. **Test de Performance:**
   - RLS no debe degradar significativamente el performance
   - Índices apropiados en `office_id` y `warehouse_id`

### Ejemplo Completo: Crear Llanta en Inventario

**Backend Service:**
```java
@Service
@Transactional
public class TireInventoryService {

    private final TireInventoryRepository repository;
    private final WarehouseRepository warehouseRepository;
    private final TireSupplierRepository supplierRepository;
    private final SecurityUtils securityUtils;

    public TireInventoryDTO createTireInventory(CreateTireInventoryRequest request) {
        // 1. Obtener oficina del usuario actual
        UUID userOfficeId = securityUtils.getCurrentUserOfficeId();

        // 2. Validar que warehouse pertenece a la oficina del usuario
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        if (!warehouse.getOfficeId().equals(userOfficeId) &&
            !securityUtils.isNationalAdmin()) {
            throw new ForbiddenException("Warehouse does not belong to your office");
        }

        // 3. Validar que supplier pertenece a la misma oficina
        TireSupplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (!supplier.getOfficeId().equals(warehouse.getOfficeId())) {
            throw new BusinessException("Supplier and warehouse must belong to same office");
        }

        // 4. Crear entidad (RLS se encarga del filtrado en queries)
        TireInventory tire = TireInventory.builder()
            .tireNumber(request.getTireNumber())
            .group(request.getGroup())
            .value(request.getValue())
            .warehouseId(request.getWarehouseId())
            .supplierId(request.getSupplierId())
            // ... demás campos
            .build();

        TireInventory saved = repository.save(tire);

        return mapper.toDTO(saved);
    }

    // RLS automáticamente filtra por oficina en este método
    public Page<TireInventoryDTO> listInventory(Pageable pageable) {
        // No necesitamos filtrar por office_id manualmente
        // RLS lo hace de forma transparente
        return repository.findAllActive(pageable)
            .map(mapper::toDTO);
    }
}
```

**Frontend Component:**
```typescript
const TireInventoryForm: React.FC = () => {
  const { currentOfficeId } = useOfficeContext();
  const [selectedWarehouse, setSelectedWarehouse] = useState<string>('');

  // Warehouses filtrados por oficina del usuario (backend aplica RLS)
  const { data: warehouses } = useWarehouses();

  // Suppliers filtrados por oficina del usuario (backend aplica RLS)
  const { data: suppliers } = useSuppliers();

  // Locations filtrados por warehouse seleccionado
  const { data: locations } = useWarehouseLocations(selectedWarehouse);

  return (
    <Form>
      {/* Selector de almacén - solo muestra almacenes de la oficina del usuario */}
      <WarehouseSelect
        warehouses={warehouses}
        value={selectedWarehouse}
        onChange={setSelectedWarehouse}
      />

      {/* Selector de ubicación - solo muestra ubicaciones del almacén seleccionado */}
      <LocationSelect
        locations={locations}
        disabled={!selectedWarehouse}
      />

      {/* Selector de proveedor - solo muestra proveedores de la oficina del usuario */}
      <SupplierSelect
        suppliers={suppliers}
      />

      {/* Ficha técnica - catálogo global */}
      <SpecificationSelect
        specifications={allSpecifications}
      />
    </Form>
  );
};
```

### Migración de Datos Legacy

Al migrar datos desde Oracle Forms, asignar `office_id` basado en:
1. Parámetros de oficina en tabla `PARAMETROS_OFICSISTEMA`
2. Usuario que creó el registro
3. Si no hay información clara, asignar a oficina principal por defecto

```sql
-- Migración ejemplo
INSERT INTO tire_inventory (
    tire_number, "group", warehouse_id, ...
)
SELECT
    i.llanta,
    i.grupo,
    (SELECT id FROM warehouses
     WHERE code = 'PRIN'
       AND office_id = (SELECT id FROM offices WHERE code = 'MAIN')),
    ...
FROM inventario_legacy i;
```

## Patrones Preferidos
- Repository pattern para acceso a datos
- DTO pattern para exponer APIs (NO exponer entidades de dominio)
- Builder pattern para objetos complejos
- Strategy pattern para variaciones de comportamiento
- Observer pattern (Domain Events) para desacoplamiento

## Lo que NO hacer
- ❌ No usar `@Transactional` en capa de presentación
- ❌ No mezclar lógica de negocio en controllers
- ❌ No usar JPA entities en DTOs de respuesta
- ❌ No hacer consultas N+1 (usar JOIN FETCH)
- ❌ No hardcodear valores (usar constantes o configuración)
- ❌ No usar `Optional.get()` sin verificar `isPresent()`
- ❌ No crear Pull Requests sin pruebas

---

**Con este contexto, por favor asísteme en el desarrollo del proyecto.**

### 2.2 Prompt de Verificación de Contexto

Después de establecer el contexto base, verificar que la IA lo entendió:

```markdown
Para confirmar que entendiste el contexto, por favor:
1. Resume el stack tecnológico en una línea
2. Indica cuál es la arquitectura de software que estamos usando
3. Dame un ejemplo de cómo se vería el nombre de una tabla en PostgreSQL siguiendo nuestras convenciones
4. Dame un ejemplo de cómo se vería un package de Java para el módulo de Tire (Llantas)

Si todo es correcto, responde "Contexto confirmado ✓" y espera mi siguiente instrucción.
```
**FIN DEL DOCUMENTO**