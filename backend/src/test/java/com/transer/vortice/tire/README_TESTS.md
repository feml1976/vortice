# Tests del Módulo de Llantas

## Tests Implementados

### Tests Unitarios (✅ Funcionando)
Los tests unitarios usan Mockito y NO requieren Docker ni base de datos.

**Ejecutar tests unitarios:**
```bash
mvn test -Dtest=TireSpecificationServiceTest,CodeGeneratorServiceTest,TireCatalogServiceTest
```

**Cobertura:**
- `TireSpecificationServiceTest`: 21 tests - Servicio principal de especificaciones técnicas
- `CodeGeneratorServiceTest`: 7 tests - Servicio de generación de códigos autoincrementales
- `TireCatalogServiceTest`: 13 tests - Servicio de catálogos

**Total:** 41 tests unitarios ✅

### Tests de Integración (⚠️ Requieren Docker)
Los tests de integración usan Testcontainers con PostgreSQL y requieren Docker.

**Pre-requisitos:**
1. Docker Desktop instalado y ejecutándose
2. Conexión a internet para descargar la imagen de PostgreSQL

**Ejecutar tests de integración:**
```bash
mvn test -Dtest=TireSpecificationRepositoryTest,TireCatalogRepositoriesTest
```

**Cobertura:**
- `TireSpecificationRepositoryTest`: 20 tests - Tests de integración del repositorio principal
  - Consultas básicas (código, activos, soft delete)
  - Consultas con relaciones (JOIN FETCH)
  - Búsquedas por texto
  - Filtros múltiples
  - Validaciones de negocio

- `TireCatalogRepositoriesTest`: 16 tests - Tests de integración de catálogos
  - TireBrand: guardar, buscar, activar/desactivar
  - TireType: guardar, buscar, listar activos
  - TireReference: guardar, buscar, listar activos
  - TireSupplier: guardar, buscar, listar activos
  - Integridad referencial
  - Auditoría (created_at, updated_at)

**Total:** 36 tests de integración (requieren Docker)

## Estructura de Tests

```
src/test/java/com/transer/vortice/tire/
├── application/service/
│   ├── TireSpecificationServiceTest.java (21 tests unitarios)
│   ├── CodeGeneratorServiceTest.java (7 tests unitarios)
│   └── TireCatalogServiceTest.java (13 tests unitarios)
└── domain/repository/
    ├── TireSpecificationRepositoryTest.java (20 tests de integración)
    └── TireCatalogRepositoriesTest.java (16 tests de integración)
```

## Patrón de Tests

### Tests Unitarios
- **Framework:** JUnit 5 + Mockito
- **Estilo:** Given-When-Then
- **Assertions:** AssertJ
- **Mocks:** @Mock para dependencias, @InjectMocks para servicio bajo prueba

### Tests de Integración
- **Framework:** JUnit 5 + Spring Boot Test + Testcontainers
- **Base de datos:** PostgreSQL 16 (via Testcontainers)
- **Estilo:** Given-When-Then
- **Assertions:** AssertJ
- **Base class:** BaseRepositoryTest (configura Testcontainers)

## Ejemplo de Ejecución

### Solo tests unitarios (sin Docker):
```bash
cd backend
mvn clean test -Dtest=**/tire/application/**/*Test.java
```

### Todos los tests (requiere Docker):
```bash
cd backend
mvn clean test -Dtest=**/tire/**/*Test.java
```

### Tests específicos:
```bash
# Test unitario de un servicio
mvn test -Dtest=TireSpecificationServiceTest#shouldCreateTireSpecificationSuccessfully

# Test de integración de un repositorio
mvn test -Dtest=TireSpecificationRepositoryTest#shouldFindSpecificationByCode
```

## Troubleshooting

### Error: "Could not find a valid Docker environment"
**Causa:** Docker no está instalado o no está ejecutándose.

**Solución:**
1. Instalar Docker Desktop: https://www.docker.com/products/docker-desktop
2. Iniciar Docker Desktop
3. Verificar que Docker está corriendo: `docker ps`
4. Ejecutar los tests de integración

### Error: "Failed to pull image postgres:16-alpine"
**Causa:** Sin conexión a internet o problemas de red.

**Solución:**
1. Verificar conexión a internet
2. Descargar la imagen manualmente: `docker pull postgres:16-alpine`
3. Reintentar los tests

## Notas

- Los tests unitarios NO necesitan Docker y pueden ejecutarse en cualquier ambiente
- Los tests de integración SÍ necesitan Docker pero proveen mayor confianza al probar contra PostgreSQL real
- BaseRepositoryTest usa un contenedor singleton compartido para mejorar el rendimiento
- Los tests de integración ejecutan las migraciones de Flyway automáticamente
- TestEntityManager se usa para forzar flush/clear y garantizar que los datos se persistan y recarguen desde la BD
