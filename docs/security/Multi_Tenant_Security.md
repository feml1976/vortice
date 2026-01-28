# Guía de Seguridad Multi-Tenant (Multi-Sede)

**Versión:** 1.0
**Fecha:** 27 de Enero de 2026
**Autor:** Equipo de Arquitectura TRANSER Vórtice

---

## 📋 TABLA DE CONTENIDO

1. [Introducción](#1-introducción)
2. [Principios de Seguridad Multi-Tenant](#2-principios-de-seguridad-multi-tenant)
3. [Row-Level Security (RLS)](#3-row-level-security-rls)
4. [Implementación en Backend](#4-implementación-en-backend)
5. [Implementación en Frontend](#5-implementación-en-frontend)
6. [Roles y Permisos](#6-roles-y-permisos)
7. [Testing de Seguridad](#7-testing-de-seguridad)
8. [Vulnerabilidades Comunes](#8-vulnerabilidades-comunes)
9. [Checklist de Implementación](#9-checklist-de-implementación)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento define las directrices de seguridad para implementar aislamiento de datos entre oficinas (multi-tenancy) en el sistema TRANSER Vórtice.

### 1.2 Modelo de Multi-Tenancy

El sistema utiliza un modelo de **multi-tenancy por oficina**:
- Cada usuario pertenece a UNA oficina
- Los usuarios ven SOLO los datos de su oficina
- El aislamiento se implementa mediante Row-Level Security en PostgreSQL
- Los administradores nacionales pueden acceder a todas las oficinas

### 1.3 Alcance

Esta guía cubre:
- ✅ Implementación de Row-Level Security (RLS) en PostgreSQL
- ✅ Configuración de contexto de usuario en Spring Boot
- ✅ Validaciones de pertenencia a oficina en la capa de aplicación
- ✅ Manejo de roles y permisos
- ✅ Testing de aislamiento de datos
- ✅ Prevención de vulnerabilidades comunes

---

## 2. PRINCIPIOS DE SEGURIDAD MULTI-TENANT

### 2.1 Principio de Menor Privilegio

**Definición:** Los usuarios solo deben tener acceso a los datos mínimos necesarios para realizar su trabajo.

**Aplicación:**
- ✅ Usuario asignado a oficina A NO puede ver datos de oficina B
- ✅ Usuario con rol `ROLE_MECHANIC` NO puede ver datos financieros
- ✅ Usuario con rol `ROLE_ADMIN_OFFICE` NO puede ver datos de otras oficinas
- ✅ Solo `ROLE_ADMIN_NATIONAL` tiene acceso multi-oficina

### 2.2 Defensa en Profundidad

**Implementación en múltiples capas:**

```
┌─────────────────────────────────────────┐
│ CAPA 1: Frontend                         │
│ - Ocultar opciones según oficina         │
│ - Filtrar selectores por oficina         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│ CAPA 2: Backend (Spring Boot)            │
│ - Validar pertenencia a oficina          │
│ - Verificar permisos                     │
│ - Auditar operaciones                    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│ CAPA 3: Base de Datos (PostgreSQL)       │
│ - Row-Level Security (RLS)                │
│ - Constraints de integridad              │
│ - Triggers de auditoría                  │
└─────────────────────────────────────────┘
```

**⚠️ IMPORTANTE:** No confiar solo en el frontend. La seguridad se implementa en TODAS las capas.

### 2.3 Fail-Safe Defaults

**Regla:** En caso de duda, denegar el acceso.

```java
// ❌ MAL: Permitir si no se puede determinar
if (userOfficeId == null) {
    return true; // PELIGROSO
}

// ✅ BIEN: Denegar si no se puede determinar
if (userOfficeId == null) {
    throw new ForbiddenException("Cannot determine user office");
}
```

### 2.4 Auditoría Completa

**Registrar:**
- Quién realizó la operación (`created_by`, `updated_by`)
- Cuándo se realizó (`created_at`, `updated_at`)
- Qué cambió (via triggers de auditoría)
- Desde qué oficina se realizó (implícito via `user.office_id`)

---

## 3. ROW-LEVEL SECURITY (RLS)

### 3.1 ¿Qué es RLS?

Row-Level Security es una característica de PostgreSQL que filtra automáticamente las filas de una tabla según políticas definidas.

**Ventajas:**
- ✅ Filtrado automático y transparente
- ✅ No se puede omitir desde la aplicación
- ✅ Protección a nivel de base de datos
- ✅ Mismo código funciona para todos los roles

**Desventajas:**
- ⚠️ Puede afectar el performance si no se indexa correctamente
- ⚠️ Debugging más complejo
- ⚠️ Requiere setear contexto en cada request

### 3.2 Implementación de RLS

#### 3.2.1 Funciones de Utilidad

```sql
-- Función para obtener office_id del usuario actual
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

-- Función para verificar rol
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
```

**⚠️ SEGURIDAD:** Usar `SECURITY DEFINER` con cuidado. Estas funciones se ejecutan con privilegios del creador, no del invocador.

#### 3.2.2 Políticas RLS Estándar

**Patrón 1: Filtrado directo por office_id**

Usar para tablas que tienen columna `office_id` directa:
- `warehouses`
- `tire_suppliers`
- `vehicles`

```sql
ALTER TABLE warehouses ENABLE ROW LEVEL SECURITY;

CREATE POLICY warehouses_office_isolation ON warehouses
    FOR ALL
    TO authenticated_user
    USING (
        -- Admin nacional: acceso total
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        -- Usuario normal: solo su oficina
        office_id = get_user_office_id()
    );
```

**Patrón 2: Filtrado indirecto via FK**

Usar para tablas que NO tienen `office_id` pero tienen FK a tabla que sí lo tiene:
- `tire_inventory` (via `warehouse_id` → `warehouses.office_id`)
- `warehouse_locations` (via `warehouse_id` → `warehouses.office_id`)

```sql
ALTER TABLE tire_inventory ENABLE ROW LEVEL SECURITY;

CREATE POLICY tire_inventory_office_isolation ON tire_inventory
    FOR ALL
    TO authenticated_user
    USING (
        current_user_has_role('ROLE_ADMIN_NATIONAL')
        OR
        warehouse_id IN (
            SELECT w.id
            FROM warehouses w
            WHERE w.office_id = get_user_office_id()
              AND w.deleted_at IS NULL
        )
    );
```

**⚠️ PERFORMANCE:** Crear índice en `warehouses(office_id)` para optimizar esta query.

#### 3.2.3 Tablas que NO requieren RLS

**Catálogos Globales (compartidos entre oficinas):**
- `tire_specifications`
- `tire_brands`
- `tire_types`
- `tire_references`
- `observation_reasons`
- `roles`

**Tablas de Sistema:**
- `users` (se filtra manualmente por seguridad)
- `audit_log`

### 3.3 Testing de RLS

```sql
-- Test 1: Verificar que RLS está habilitado
SELECT tablename, rowsecurity
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('warehouses', 'tire_inventory', 'tire_suppliers');
-- Todas deben tener rowsecurity = true

-- Test 2: Verificar políticas creadas
SELECT schemaname, tablename, policyname, permissive, roles, cmd
FROM pg_policies
WHERE schemaname = 'public'
  AND tablename IN ('warehouses', 'tire_inventory', 'tire_suppliers');

-- Test 3: Simular acceso de usuario
-- (Debe ejecutarse desde aplicación con contexto de usuario)
SET app.current_user_id = 1;  -- Usuario de oficina A
SELECT COUNT(*) FROM tire_inventory;  -- Solo ve llantas de oficina A

SET app.current_user_id = 2;  -- Usuario de oficina B
SELECT COUNT(*) FROM tire_inventory;  -- Solo ve llantas de oficina B
```

---

## 4. IMPLEMENTACIÓN EN BACKEND

### 4.1 Configuración de Contexto de Usuario

#### 4.1.1 SecurityUtils

```java
package com.transer.vortice.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * Obtiene el ID del usuario autenticado actual
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found");
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getId();
    }

    /**
     * Obtiene el office_id del usuario autenticado actual
     */
    public static UUID getCurrentUserOfficeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found");
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        UUID officeId = principal.getOfficeId();

        if (officeId == null) {
            throw new IllegalStateException("User does not have office assigned");
        }

        return officeId;
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public static boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        return auth.getAuthorities().stream()
            .anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals(roleName)
            );
    }

    /**
     * Verifica si el usuario actual es administrador nacional
     */
    public static boolean isNationalAdmin() {
        return hasRole("ROLE_ADMIN_NATIONAL");
    }

    /**
     * Verifica si el usuario puede acceder a una oficina específica
     */
    public static boolean canAccessOffice(UUID officeId) {
        if (officeId == null) {
            return false;
        }

        // Admin nacional puede acceder a cualquier oficina
        if (isNationalAdmin()) {
            return true;
        }

        // Usuario normal solo puede acceder a su oficina
        return officeId.equals(getCurrentUserOfficeId());
    }
}
```

#### 4.1.2 RLS Context Filter

```java
package com.transer.vortice.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que configura el contexto de RLS en PostgreSQL para cada request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RLSContextFilter extends OncePerRequestFilter {

    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Long userId = SecurityUtils.getCurrentUserId();

                // Setear app.current_user_id en la sesión de PostgreSQL
                // Esto será usado por las funciones RLS
                jdbcTemplate.execute(
                    "SET LOCAL app.current_user_id = " + userId
                );

                log.debug("RLS context set for user: {}", userId);
            } catch (Exception e) {
                log.error("Error setting RLS context", e);
                // No lanzar excepción aquí, dejar que el request continúe
                // RLS denegará el acceso si no está configurado
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

**⚠️ IMPORTANTE:** Este filtro debe ejecutarse DESPUÉS del filtro de autenticación JWT.

#### 4.1.3 Configuración del Filtro

```java
package com.transer.vortice.config;

import com.transer.vortice.shared.security.JwtAuthenticationFilter;
import com.transer.vortice.shared.security.RLSContextFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final RLSContextFilter rlsContextFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ... otras configuraciones
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(rlsContextFilter, JwtAuthenticationFilter.class);  // ← Después de JWT

        return http.build();
    }
}
```

### 4.2 Validaciones en la Capa de Aplicación

**⚠️ PRINCIPIO:** No confiar solo en RLS. Validar en la aplicación también.

#### 4.2.1 Validación de Pertenencia a Oficina

```java
package com.transer.vortice.tire.application;

import com.transer.vortice.shared.security.SecurityUtils;
import com.transer.vortice.tire.domain.TireInventory;
import com.transer.vortice.tire.infrastructure.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TireInventoryService {

    private final TireInventoryRepository tireInventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final TireSupplierRepository supplierRepository;

    public TireInventoryDTO createTireInventory(CreateTireInventoryRequest request) {
        UUID userOfficeId = SecurityUtils.getCurrentUserOfficeId();

        // 1. Validar que warehouse pertenece a la oficina del usuario
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        if (!warehouse.getOfficeId().equals(userOfficeId) &&
            !SecurityUtils.isNationalAdmin()) {
            throw new ForbiddenException(
                "Warehouse does not belong to your office. " +
                "User office: " + userOfficeId +
                ", Warehouse office: " + warehouse.getOfficeId()
            );
        }

        // 2. Validar que supplier pertenece a la misma oficina que el warehouse
        TireSupplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (!supplier.getOfficeId().equals(warehouse.getOfficeId())) {
            throw new BusinessException(
                "Supplier and warehouse must belong to the same office. " +
                "Supplier office: " + supplier.getOfficeId() +
                ", Warehouse office: " + warehouse.getOfficeId()
            );
        }

        // 3. Crear entidad (RLS ya filtra, pero validamos explícitamente)
        TireInventory tire = TireInventory.builder()
            .tireNumber(request.getTireNumber())
            .group(request.getGroup())
            .warehouseId(request.getWarehouseId())
            .supplierId(request.getSupplierId())
            .value(request.getValue())
            .build();

        TireInventory saved = tireInventoryRepository.save(tire);

        return mapper.toDTO(saved);
    }
}
```

#### 4.2.2 Método de Utilidad para Validación

```java
package com.transer.vortice.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfficeValidator {

    /**
     * Valida que el usuario puede acceder a una oficina específica
     */
    public void validateOfficeAccess(UUID officeId, String context) {
        if (officeId == null) {
            throw new IllegalArgumentException("Office ID cannot be null");
        }

        if (!SecurityUtils.canAccessOffice(officeId)) {
            throw new ForbiddenException(
                String.format(
                    "Access denied to office %s in context: %s. " +
                    "User office: %s",
                    officeId,
                    context,
                    SecurityUtils.getCurrentUserOfficeId()
                )
            );
        }
    }

    /**
     * Valida que dos entidades pertenecen a la misma oficina
     */
    public void validateSameOffice(UUID officeId1, UUID officeId2, String context) {
        if (officeId1 == null || officeId2 == null) {
            throw new IllegalArgumentException("Office IDs cannot be null");
        }

        if (!officeId1.equals(officeId2)) {
            throw new BusinessException(
                String.format(
                    "Entities must belong to the same office in context: %s. " +
                    "Office 1: %s, Office 2: %s",
                    context,
                    officeId1,
                    officeId2
                )
            );
        }
    }
}
```

---

## 5. IMPLEMENTACIÓN EN FRONTEND

### 5.1 Context de Oficina

```typescript
// src/shared/contexts/OfficeContext.tsx

import React, { createContext, useContext, ReactNode } from 'react';
import { useAuth } from './AuthContext';

interface OfficeContextValue {
  currentOfficeId: string;
  currentOfficeName: string;
  currentOfficeCode: string;
  isNationalAdmin: boolean;
}

const OfficeContext = createContext<OfficeContextValue | null>(null);

export const OfficeProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { user } = useAuth();

  if (!user) {
    return null;
  }

  const value: OfficeContextValue = {
    currentOfficeId: user.officeId,
    currentOfficeName: user.officeName,
    currentOfficeCode: user.officeCode,
    isNationalAdmin: user.roles.includes('ROLE_ADMIN_NATIONAL'),
  };

  return (
    <OfficeContext.Provider value={value}>
      {children}
    </OfficeContext.Provider>
  );
};

export const useOffice = (): OfficeContextValue => {
  const context = useContext(OfficeContext);
  if (!context) {
    throw new Error('useOffice must be used within OfficeProvider');
  }
  return context;
};
```

### 5.2 Indicador de Oficina en UI

```typescript
// src/shared/components/OfficeIndicator.tsx

import React from 'react';
import { Chip, Box } from '@mui/material';
import { Business as BusinessIcon } from '@mui/icons-material';
import { useOffice } from '../contexts/OfficeContext';

export const OfficeIndicator: React.FC = () => {
  const { currentOfficeCode, currentOfficeName } = useOffice();

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
      <Chip
        icon={<BusinessIcon />}
        label={`${currentOfficeCode} - ${currentOfficeName}`}
        color="primary"
        variant="outlined"
        size="small"
      />
    </Box>
  );
};
```

### 5.3 Filtrado de Selectores por Oficina

```typescript
// src/features/tire/components/WarehouseSelect.tsx

import React from 'react';
import { FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useOffice } from '@/shared/contexts/OfficeContext';
import { warehouseApi } from '../api/warehouseApi';

interface WarehouseSelectProps {
  value: string;
  onChange: (value: string) => void;
  disabled?: boolean;
}

export const WarehouseSelect: React.FC<WarehouseSelectProps> = ({
  value,
  onChange,
  disabled = false,
}) => {
  const { currentOfficeId } = useOffice();

  // El backend ya filtra por oficina via RLS,
  // pero enviamos el officeId para claridad
  const { data: warehouses, isLoading } = useQuery({
    queryKey: ['warehouses', currentOfficeId],
    queryFn: () => warehouseApi.listByOffice(currentOfficeId),
  });

  return (
    <FormControl fullWidth disabled={disabled || isLoading}>
      <InputLabel>Almacén</InputLabel>
      <Select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        label="Almacén"
      >
        {warehouses?.map((warehouse) => (
          <MenuItem key={warehouse.id} value={warehouse.id}>
            {warehouse.code} - {warehouse.name}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};
```

---

## 6. ROLES Y PERMISOS

### 6.1 Roles del Sistema

| Rol | Código | Alcance | Permisos |
|-----|--------|---------|----------|
| **Admin Nacional** | `ROLE_ADMIN_NATIONAL` | Todas las oficinas | Ver y gestionar datos de todas las oficinas, reportes consolidados |
| **Admin de Oficina** | `ROLE_ADMIN_OFFICE` | Oficina específica | Gestión completa dentro de su oficina |
| **Gerente de Almacén** | `ROLE_WAREHOUSE_MANAGER` | Almacén específico | Gestión de inventario de su almacén |
| **Mecánico** | `ROLE_MECHANIC` | Oficina específica | Solo operaciones de taller (montaje, desmontaje, muestreos) |

### 6.2 Matriz de Permisos

| Operación | Admin Nacional | Admin Oficina | Gerente Almacén | Mecánico |
|-----------|----------------|---------------|-----------------|----------|
| Ver todas las oficinas | ✅ | ❌ | ❌ | ❌ |
| Ver su oficina | ✅ | ✅ | ✅ | ✅ |
| Crear oficina | ✅ | ❌ | ❌ | ❌ |
| Crear almacén | ✅ | ✅ | ❌ | ❌ |
| Ingresar llanta a inventario | ✅ | ✅ | ✅ | ❌ |
| Montar llanta en vehículo | ✅ | ✅ | ✅ | ✅ |
| Dar de baja llanta | ✅ | ✅ | ✅ | ❌ |
| Registrar muestreo | ✅ | ✅ | ✅ | ✅ |
| Ver reportes consolidados | ✅ | ❌ | ❌ | ❌ |
| Ver reportes de su oficina | ✅ | ✅ | ✅ | ✅ |

### 6.3 Verificación de Permisos en Backend

```java
@PreAuthorize("hasAnyRole('ROLE_ADMIN_NATIONAL', 'ROLE_ADMIN_OFFICE')")
public OfficeDTO createOffice(CreateOfficeRequest request) {
    // Solo admins nacionales y de oficina pueden crear oficinas
    // ...
}

@PreAuthorize("hasAnyRole('ROLE_ADMIN_NATIONAL', 'ROLE_ADMIN_OFFICE', 'ROLE_WAREHOUSE_MANAGER')")
public TireInventoryDTO createTireInventory(CreateTireInventoryRequest request) {
    // Validación adicional de oficina
    officeValidator.validateOfficeAccess(
        request.getWarehouseOfficeId(),
        "create tire inventory"
    );
    // ...
}
```

---

## 7. TESTING DE SEGURIDAD

### 7.1 Tests Unitarios de Aislamiento

```java
@Test
@WithMockUser(username = "user_office_A", roles = "ADMIN_OFFICE")
void testUserFromOfficeA_CannotAccessOfficeB_Data() {
    // Arrange
    UUID officeAId = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    UUID officeBId = UUID.fromString("b0000000-0000-0000-0000-000000000001");

    when(securityUtils.getCurrentUserOfficeId()).thenReturn(officeAId);

    Warehouse warehouseB = Warehouse.builder()
        .id(UUID.randomUUID())
        .code("PRIN")
        .name("Almacén Principal")
        .officeId(officeBId)
        .build();

    // Act & Assert
    assertThrows(ForbiddenException.class, () -> {
        // Intentar crear llanta en almacén de oficina B
        tireInventoryService.createTireInventory(
            CreateTireInventoryRequest.builder()
                .warehouseId(warehouseB.getId())
                .build()
        );
    });
}

@Test
@WithMockUser(username = "national_admin", roles = "ADMIN_NATIONAL")
void testNationalAdmin_CanAccessAllOffices() {
    // Arrange
    UUID officeBId = UUID.fromString("b0000000-0000-0000-0000-000000000001");

    when(securityUtils.isNationalAdmin()).thenReturn(true);

    Warehouse warehouseB = Warehouse.builder()
        .id(UUID.randomUUID())
        .officeId(officeBId)
        .build();

    // Act - No debe lanzar excepción
    TireInventoryDTO result = tireInventoryService.createTireInventory(
        CreateTireInventoryRequest.builder()
            .warehouseId(warehouseB.getId())
            .build()
    );

    // Assert
    assertNotNull(result);
}
```

### 7.2 Tests de Integración con RLS

```java
@SpringBootTest
@Transactional
@Sql(scripts = "/sql/test-multi-tenant-setup.sql")
class RLSIntegrationTest {

    @Autowired
    private TireInventoryRepository tireInventoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testRLS_FiltersDataByOffice() {
        // Arrange: Setear contexto de usuario de oficina A
        Long userIdOfficeA = 1L;  // Usuario de oficina A
        jdbcTemplate.execute("SET LOCAL app.current_user_id = " + userIdOfficeA);

        // Act: Consultar inventario
        List<TireInventory> inventoryOfficeA = tireInventoryRepository.findAll();

        // Assert: Solo debe ver llantas de oficina A
        assertThat(inventoryOfficeA).allSatisfy(tire -> {
            Warehouse warehouse = tire.getWarehouse();
            assertThat(warehouse.getOfficeId()).isEqualTo(officeAId);
        });

        // Arrange: Cambiar a usuario de oficina B
        Long userIdOfficeB = 2L;
        jdbcTemplate.execute("SET LOCAL app.current_user_id = " + userIdOfficeB);

        // Act
        List<TireInventory> inventoryOfficeB = tireInventoryRepository.findAll();

        // Assert: Solo debe ver llantas de oficina B
        assertThat(inventoryOfficeB).allSatisfy(tire -> {
            Warehouse warehouse = tire.getWarehouse();
            assertThat(warehouse.getOfficeId()).isEqualTo(officeBId);
        });

        // Verificar que no se cruzan datos
        assertThat(inventoryOfficeA).doesNotContainAnyElementsOf(inventoryOfficeB);
    }
}
```

### 7.3 Tests de Seguridad End-to-End

```typescript
// tests/e2e/security/office-isolation.spec.ts

describe('Office Isolation - Security Tests', () => {
  test('User from Office A cannot see data from Office B', async () => {
    // Login como usuario de oficina A
    await loginAsUser('user_office_a@transer.com', 'password');

    // Ir a inventario
    await page.goto('/tire/inventory');

    // Verificar que solo ve llantas de oficina A
    const tires = await page.locator('[data-testid="tire-row"]').all();
    for (const tire of tires) {
      const officeCode = await tire.locator('[data-testid="office-code"]').textContent();
      expect(officeCode).toBe('BOG');  // Oficina A
    }

    // Intentar acceder directamente a llanta de oficina B via URL
    const tireBId = 'uuid-of-tire-from-office-b';
    const response = await page.goto(`/tire/inventory/${tireBId}`);

    // Debe retornar 403 Forbidden
    expect(response?.status()).toBe(403);
  });

  test('National admin can see all offices', async () => {
    // Login como admin nacional
    await loginAsUser('admin_national@transer.com', 'password');

    // Ir a inventario
    await page.goto('/tire/inventory');

    // Debe ver selector de oficina
    const officeSelector = page.locator('[data-testid="office-selector"]');
    await expect(officeSelector).toBeVisible();

    // Seleccionar oficina A
    await officeSelector.selectOption('BOG');
    await page.waitForLoadState('networkidle');

    const tiresOfficeA = await page.locator('[data-testid="tire-row"]').count();
    expect(tiresOfficeA).toBeGreaterThan(0);

    // Seleccionar oficina B
    await officeSelector.selectOption('MED');
    await page.waitForLoadState('networkidle');

    const tiresOfficeB = await page.locator('[data-testid="tire-row"]').count();
    expect(tiresOfficeB).toBeGreaterThan(0);
  });
});
```

---

## 8. VULNERABILIDADES COMUNES

### 8.1 Insecure Direct Object Reference (IDOR)

**Vulnerabilidad:**
```typescript
// ❌ VULNERABLE: No valida que el warehouse pertenezca a la oficina del usuario
async function getTireInventory(tireId: string) {
  const tire = await tireInventoryRepository.findById(tireId);
  return tire;  // Puede retornar llanta de otra oficina
}
```

**Mitigación:**
```typescript
// ✅ SEGURO: RLS filtra automáticamente
async function getTireInventory(tireId: string) {
  // RLS solo retornará la llanta si pertenece a la oficina del usuario
  const tire = await tireInventoryRepository.findById(tireId);

  if (!tire) {
    throw new NotFoundException("Tire not found or access denied");
  }

  return tire;
}
```

### 8.2 Mass Assignment

**Vulnerabilidad:**
```java
// ❌ VULNERABLE: Permite setear cualquier campo, incluyendo office_id
@PutMapping("/{id}")
public TireInventoryDTO update(@PathVariable UUID id, @RequestBody TireInventory tire) {
    tire.setId(id);
    return tireInventoryRepository.save(tire);  // Usuario puede cambiar office_id
}
```

**Mitigación:**
```java
// ✅ SEGURO: Usar DTO con solo campos permitidos
@PutMapping("/{id}")
public TireInventoryDTO update(
    @PathVariable UUID id,
    @Valid @RequestBody UpdateTireInventoryRequest request
) {
    TireInventory existing = tireInventoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tire not found"));

    // Validar que el usuario puede modificar esta llanta (RLS ya filtró, pero doble check)
    officeValidator.validateOfficeAccess(
        existing.getWarehouse().getOfficeId(),
        "update tire inventory"
    );

    // Actualizar solo campos permitidos (NO office_id)
    existing.setValue(request.getValue());
    existing.setNotes(request.getNotes());
    // ... demás campos seguros

    return mapper.toDTO(tireInventoryRepository.save(existing));
}
```

### 8.3 SQL Injection en Contexto RLS

**Vulnerabilidad:**
```java
// ❌ VULNERABLE: Concatenación directa de user input
jdbcTemplate.execute("SET LOCAL app.current_user_id = " + userId);  // SQL Injection
```

**Mitigación:**
```java
// ✅ SEGURO: Validar que userId es numérico
if (userId == null || userId <= 0) {
    throw new IllegalArgumentException("Invalid user ID");
}
jdbcTemplate.execute("SET LOCAL app.current_user_id = " + userId);

// O usar PreparedStatement (aunque SET LOCAL no lo soporta)
```

### 8.4 Authorization Bypass via Role Escalation

**Vulnerabilidad:**
```java
// ❌ VULNERABLE: No valida rol antes de operación crítica
public void deleteOffice(UUID officeId) {
    officeRepository.deleteById(officeId);  // Cualquier usuario puede eliminar
}
```

**Mitigación:**
```java
// ✅ SEGURO: Verificar rol y pertenencia
@PreAuthorize("hasRole('ROLE_ADMIN_NATIONAL')")
public void deleteOffice(UUID officeId) {
    Office office = officeRepository.findById(officeId)
        .orElseThrow(() -> new NotFoundException("Office not found"));

    // Solo admin nacional puede eliminar oficinas
    if (!SecurityUtils.isNationalAdmin()) {
        throw new ForbiddenException("Only national administrators can delete offices");
    }

    // Soft delete
    office.markAsDeleted(SecurityUtils.getCurrentUserId());
    officeRepository.save(office);
}
```

---

## 9. CHECKLIST DE IMPLEMENTACIÓN

### 9.1 Checklist de Base de Datos

- [ ] Tabla `offices` creada con constraints
- [ ] Tabla `warehouses` creada con FK a `offices`
- [ ] Tabla `warehouse_locations` creada con FK a `warehouses`
- [ ] Tabla `tire_suppliers` creada con FK a `offices`
- [ ] Campo `office_id` agregado a tabla `users`
- [ ] Índices creados en todas las FK
- [ ] Funciones `get_user_office_id()` y `current_user_has_role()` creadas
- [ ] Políticas RLS habilitadas en todas las tablas necesarias
- [ ] Políticas RLS testeadas con múltiples usuarios
- [ ] Migración de datos legacy ejecutada y validada

### 9.2 Checklist de Backend

- [ ] `SecurityUtils` implementado
- [ ] `RLSContextFilter` implementado y registrado
- [ ] `OfficeValidator` implementado
- [ ] Validaciones de pertenencia a oficina en todos los servicios
- [ ] `@PreAuthorize` en métodos críticos
- [ ] DTOs no exponen `office_id` (solo para admins nacionales)
- [ ] Tests unitarios de aislamiento
- [ ] Tests de integración con RLS
- [ ] Manejo de excepciones personalizado (ForbiddenException, etc.)

### 9.3 Checklist de Frontend

- [ ] `OfficeContext` implementado
- [ ] `OfficeProvider` envuelve la aplicación
- [ ] Indicador de oficina visible en navbar
- [ ] Selectores filtrados por oficina del usuario
- [ ] Selector de oficina para admins nacionales
- [ ] Tests E2E de aislamiento de oficinas
- [ ] Manejo de errores 403 Forbidden

### 9.4 Checklist de Testing

- [ ] Tests unitarios de `SecurityUtils`
- [ ] Tests unitarios de validaciones de oficina
- [ ] Tests de integración con RLS
- [ ] Tests E2E de aislamiento entre oficinas
- [ ] Tests E2E de acceso de admin nacional
- [ ] Penetration testing manual (intentar acceder a datos de otra oficina)
- [ ] Code review de seguridad

---

## 10. REFERENCIAS

- **PostgreSQL Row-Level Security:** https://www.postgresql.org/docs/current/ddl-rowsecurity.html
- **Spring Security:** https://docs.spring.io/spring-security/reference/
- **OWASP Top 10:** https://owasp.org/www-project-top-ten/
- **Esquema de Base de Datos:** `/docs/database/Schema_Organization.md`
- **Requerimientos:** `/docs/llantas/Requerimiento_Llantas.md` (RF-001-EXT)

---

**FIN DEL DOCUMENTO**
