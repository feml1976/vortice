# Tests - Módulo Organizacional

Este documento describe la estrategia de testing implementada para el módulo de Estructura Organizacional Multi-Sede.

## 📋 Cobertura de Tests

### Tests Unitarios
Tests rápidos que validan la lógica de negocio de forma aislada usando mocks.

#### Servicios (Application Layer)
- ✅ **OfficeServiceTest**: 15 tests
  - CRUD completo de oficinas
  - Validación de códigos duplicados
  - Eliminación con dependencias activas
  - Control de acceso (admin nacional vs usuarios)

- ✅ **WarehouseServiceTest**: 15 tests
  - CRUD con validación de RLS
  - Validación de acceso a oficinas
  - Códigos únicos por contexto (oficina)
  - Eliminación con ubicaciones activas

#### Entidades (Domain Layer)
- ✅ **OfficeTest**: 12 tests
  - Reglas de negocio
  - Normalización de códigos
  - Soft delete
  - Validaciones de estado

- ✅ **WarehouseTest**: 14 tests
  - Pertenencia a oficina
  - Validación de ownership
  - Multi-tenancy
  - Inmutabilidad de officeId

#### Mappers
- ✅ **WarehouseMapperTest**: 11 tests
  - Conversión Request → Entity
  - Conversión Entity → Response
  - Conversiones con datos relacionados (Office)
  - Manejo de nulls

#### Infraestructura
- ✅ **SecurityUtilsTest**: 14 tests
  - Obtención de usuario actual
  - Obtención de officeId
  - Validación de roles
  - Control de acceso a oficinas

### Tests de Integración
Tests que validan el comportamiento completo con base de datos real (PostgreSQL via Testcontainers).

#### Repositorios
- ✅ **WarehouseRepositoryIntegrationTest**: 9 tests
  - Validación de Row-Level Security (RLS)
  - Queries con filtro de oficina
  - Operaciones CRUD con RLS activo
  - Aislamiento de datos por oficina

### Tests de API (Controllers)
Tests que validan los endpoints REST usando MockMvc.

- ✅ **OfficeControllerTest**: 14 tests
  - Autenticación y autorización
  - Validación de roles (@PreAuthorize)
  - Códigos de respuesta HTTP
  - Serialización JSON
  - Manejo de errores

## 🚀 Ejecutar Tests

### Todos los tests
```bash
./mvnw test
```

### Tests de un módulo específico
```bash
# Tests del módulo organizacional
./mvnw test -Dtest="com.transer.vortice.organization.**"

# Solo tests unitarios
./mvnw test -Dtest="**/*Test.java"

# Solo tests de integración
./mvnw test -Dtest="**/*IntegrationTest.java"
```

### Test específico
```bash
./mvnw test -Dtest=OfficeServiceTest
./mvnw test -Dtest=WarehouseRepositoryIntegrationTest
```

### Con cobertura (JaCoCo)
```bash
./mvnw test jacoco:report
# Ver reporte en: target/site/jacoco/index.html
```

## 🏗️ Estructura de Tests

```
src/test/java/
└── com/transer/vortice/organization/
    ├── application/
    │   ├── mapper/
    │   │   └── WarehouseMapperTest.java
    │   └── service/
    │       ├── OfficeServiceTest.java
    │       └── WarehouseServiceTest.java
    ├── domain/
    │   └── model/
    │       ├── OfficeTest.java
    │       └── WarehouseTest.java
    ├── infrastructure/
    │   ├── repository/
    │   │   └── WarehouseRepositoryIntegrationTest.java
    │   └── security/
    │       └── SecurityUtilsTest.java
    └── presentation/
        └── controller/
            └── OfficeControllerTest.java
```

## 🔧 Tecnologías de Testing

### Frameworks y Librerías
- **JUnit 5**: Framework de testing principal
- **Mockito**: Mocking de dependencias
- **AssertJ**: Assertions fluidas y expresivas
- **Testcontainers**: PostgreSQL real para tests de integración
- **Spring Boot Test**: Testing de contexto Spring
- **Spring Security Test**: Testing de seguridad con `@WithMockUser`
- **MockMvc**: Testing de endpoints REST

### Anotaciones Principales
- `@ExtendWith(MockitoExtension.class)`: Para tests unitarios con Mockito
- `@DataJpaTest`: Para tests de repositorios con base de datos en memoria
- `@WebMvcTest`: Para tests de controladores con MockMvc
- `@Testcontainers`: Para tests con Testcontainers
- `@WithMockUser`: Para simular usuarios autenticados

## 🎯 Estrategia de Testing

### 1. Tests Unitarios (Fast)
- **Objetivo**: Validar lógica de negocio aislada
- **Velocidad**: < 100ms por test
- **Dependencias**: Todas mockeadas con Mockito
- **Casos**: Happy path + casos de error + edge cases

### 2. Tests de Integración (Medium)
- **Objetivo**: Validar integración con PostgreSQL y RLS
- **Velocidad**: 1-5 segundos por test
- **Base de datos**: PostgreSQL 18 en Testcontainers
- **Casos**: RLS filtering, CRUD completo, constraints

### 3. Tests de API (Fast-Medium)
- **Objetivo**: Validar endpoints REST y seguridad
- **Velocidad**: 100-500ms por test
- **Mock**: Services mockeados, solo valida capa web
- **Casos**: Autenticación, autorización, serialización, errores

## 📊 Cobertura Actual

| Componente | Tests | Cobertura Estimada |
|------------|-------|-------------------|
| Services | 30+ | ~90% |
| Entities | 26+ | ~95% |
| Mappers | 11+ | ~100% |
| Controllers | 14+ | ~85% |
| Repositories | 9+ | ~80% |
| Security Utils | 14+ | ~90% |
| **TOTAL** | **104+** | **~90%** |

## 🐛 Testing de Row-Level Security (RLS)

Los tests de RLS son críticos para validar el aislamiento multi-sede:

### Setup de RLS en Tests
```java
// Establecer contexto RLS simulando usuario autenticado
private void setRLSContext(Long userId) {
    jdbcTemplate.execute(String.format("SET LOCAL app.current_user_id = %d", userId));
}

// Limpiar contexto
private void clearRLSContext() {
    jdbcTemplate.execute("RESET app.current_user_id");
}
```

### Casos de Test RLS
1. **Sin contexto RLS**: Encuentra todos los registros
2. **Con contexto oficina 1**: Solo registros de oficina 1
3. **Con contexto oficina 2**: Solo registros de oficina 2
4. **Buscar por ID**: RLS bloquea acceso a otras oficinas
5. **Actualizar**: Solo permite si pertenece a la oficina
6. **Count**: Cuenta solo registros de la oficina

## 📝 Convenciones de Naming

### Métodos de Test
```java
// Patrón: methodName_StateUnderTest_ExpectedBehavior
void createOffice_WithValidData_Success()
void createOffice_DuplicateCode_ThrowsException()
void deleteOffice_WithActiveWarehouses_ThrowsException()
```

### Display Names
```java
@DisplayName("Crear oficina - exitoso")
@DisplayName("Actualizar oficina - no encontrada lanza excepción")
@DisplayName("Con RLS context oficina 1 - encuentra solo almacenes de oficina 1")
```

## 🔍 Debugging Tests

### Ver SQL generado
```yaml
# application-test.yml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Ver logs de Testcontainers
```yaml
logging:
  level:
    org.testcontainers: DEBUG
```

### Ejecutar test en modo debug (IntelliJ)
1. Click derecho en el test
2. "Debug 'TestName'"
3. Breakpoints en código de producción o test

## ✅ Checklist para Nuevos Tests

Al agregar nuevos tests, asegurar:

- [ ] Test tiene `@DisplayName` descriptivo
- [ ] Test sigue patrón Given-When-Then
- [ ] Test valida un solo comportamiento
- [ ] Happy path está cubierto
- [ ] Casos de error están cubiertos
- [ ] Edge cases están cubiertos
- [ ] Mocks están correctamente configurados
- [ ] Assertions son claras y específicas
- [ ] Test es rápido (< 5 segundos)
- [ ] Test es independiente (no depende de orden)
- [ ] Clean up se realiza correctamente

## 🚦 CI/CD

Los tests se ejecutan automáticamente en:
- Cada commit (pre-commit hook)
- Cada push a feature branch
- Cada Pull Request
- Antes de merge a main

### Pipeline
```
1. Checkout código
2. Setup Java 21
3. Cache Maven dependencies
4. Run tests (./mvnw test)
5. Generate coverage report
6. Upload coverage to SonarQube
7. Fail build si cobertura < 80%
```

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
