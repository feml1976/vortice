# 📋 REGLAS DE CÓDIGO PARA CLAUDE CODE
## Proyecto: Migración Oracle Forms 6i → Stack Moderno

---

## 🎯 CONTEXTO DEL PROYECTO

### Sistema Legacy
- **Tecnología Original:** Oracle Forms 6i + Reports 6i
- **Base de Datos:** Oracle 11g
- **Antigüedad:** 20 años en producción
- **Complejidad:** Lógica de negocio extensa y validada en el tiempo

### Stack Objetivo
- **Backend:** Java 21 + Spring Boot 3.5
- **Frontend:** React 18 + TypeScript + Material-UI
- **Base de Datos:** PostgreSQL 18
- **Arquitectura:** Clean Architecture / Hexagonal con DDD
- **Build:** Maven (backend), Vite (frontend)

---

## 🏗️ ARQUITECTURA Y DISEÑO

### A1. Estructura de Capas (OBLIGATORIO)

**SIEMPRE respetar la siguiente jerarquía de dependencias:**

```
┌─────────────────────────────────────┐
│   Presentation Layer                │  ← Controllers, DTOs, REST APIs
├─────────────────────────────────────┤
│   Application Layer                 │  ← Use Cases, Application Services
├─────────────────────────────────────┤
│   Domain Layer                      │  ← Entities, Value Objects, Domain Services
├─────────────────────────────────────┤
│   Infrastructure Layer              │  ← JPA, External APIs, File System
└─────────────────────────────────────┘
```

**Reglas de Dependencia:**
- ✅ Domain NO debe depender de ninguna capa
- ✅ Application solo depende de Domain
- ✅ Infrastructure depende de Domain (implementa interfaces)
- ✅ Presentation depende de Application y Domain
- ❌ NUNCA inyectar repositorios directamente en controllers
- ❌ NUNCA exponer entidades de dominio en controllers (usar DTOs)

**Ejemplo de Estructura de Paquetes:**
```
com.empresa.tire
├── domain
│   ├── model (Entities, Value Objects)
│   ├── repository (Interfaces)
│   └── service (Domain Services)
├── application
│   ├── usecase (Use Cases)
│   ├── dto (DTOs de Application)
│   └── service (Application Services)
├── infrastructure
│   ├── persistence (JPA Entities, Repositories Impl)
│   ├── config (Configuración)
│   └── adapter (Adaptadores externos)
└── presentation
    ├── controller (REST Controllers)
    └── dto (DTOs de API)
```

### A2. Bounded Contexts (DDD)

**SIEMPRE definir límites claros entre módulos:**

**Módulos Identificados:**
- `auth` - Autenticación y autorización
- `user` - Gestión de usuarios
- `shared` - Catálogos y funcionalidad compartida
- `tire` - Gestión de llantas
- `purchasing` - Compras
- `inventory` - Inventarios
- `workshop` - Taller

**Reglas de Comunicación entre Contextos:**
- ✅ Usar eventos de dominio para comunicación asíncrona
- ✅ Usar servicios de aplicación para comunicación síncrona
- ✅ Definir contratos claros (interfaces) entre contextos
- ❌ NUNCA acceder directamente a repositorios de otro contexto
- ❌ NUNCA compartir entidades entre contextos (usar DTOs o Value Objects)

### A3. Nomenclatura y Convenciones

**Backend (Java):**
```java
// ✅ CORRECTO
public class Tire { }                          // Entity
public class TireId { }                        // Value Object
public interface TireRepository { }            // Repository Interface
public class JpaTireRepository { }             // Repository Implementation
public class RegisterTireUseCase { }           // Use Case
public class TireApplicationService { }        // Application Service
public class TireController { }                // Controller
public class CreateTireRequest { }             // DTO Request
public class TireResponse { }                  // DTO Response

// ❌ INCORRECTO
public class TireManager { }                   // Nombre genérico
public class TireHandler { }                   // Ambiguo
public class TireDTO { }                       // No específica propósito
```

**Frontend (TypeScript):**
```typescript
// ✅ CORRECTO
interface Tire { }                             // Domain Model
interface CreateTireDto { }                    // DTO
interface TireRepository { }                   // Repository Interface
class TireService { }                          // Service
const useTires = () => { }                     // Custom Hook
const TireList: React.FC = () => { }           // Component

// ❌ INCORRECTO
interface tire { }                             // Lowercase
interface TireData { }                         // Genérico
class TireManager { }                          // Ambiguo
```

---

## 💻 CALIDAD DE CÓDIGO

### C1. Principios SOLID (OBLIGATORIO)

**Single Responsibility Principle (SRP):**
```java
// ❌ INCORRECTO - Múltiples responsabilidades
public class TireService {
    public void createTire(Tire tire) {
        validateTire(tire);
        saveTire(tire);
        sendEmail(tire);
        logOperation(tire);
    }
}

// ✅ CORRECTO - Responsabilidades separadas
public class CreateTireUseCase {
    private final TireRepository tireRepository;
    private final TireValidator tireValidator;
    private final EventPublisher eventPublisher;
    
    public TireId execute(CreateTireCommand command) {
        Tire tire = command.toDomain();
        tireValidator.validate(tire);
        Tire savedTire = tireRepository.save(tire);
        eventPublisher.publish(new TireCreatedEvent(savedTire));
        return savedTire.getId();
    }
}
```

**Open/Closed Principle (OCP):**
```java
// ✅ Usar Strategy Pattern para lógica variable
public interface TirePriceCalculator {
    Money calculate(Tire tire);
}

public class NewTirePriceCalculator implements TirePriceCalculator { }
public class UsedTirePriceCalculator implements TirePriceCalculator { }
public class RecappedTirePriceCalculator implements TirePriceCalculator { }
```

**Dependency Inversion Principle (DIP):**
```java
// ✅ Depender de abstracciones
public class RegisterTireUseCase {
    private final TireRepository tireRepository;  // Interface, no implementación
    private final EmailService emailService;       // Interface, no implementación
}
```

### C2. Manejo de Errores

**SIEMPRE usar excepciones específicas del dominio:**

```java
// ✅ CORRECTO - Jerarquía de excepciones
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}

public class TireNotFoundException extends DomainException {
    public TireNotFoundException(TireId id) {
        super("Tire not found with id: " + id.getValue());
    }
}

public class InvalidTireStateException extends DomainException {
    public InvalidTireStateException(String message) {
        super(message);
    }
}

// ❌ INCORRECTO
throw new RuntimeException("Error");
throw new Exception("Tire not found");
```

**Handler Global de Excepciones:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

### C3. Validación

**SIEMPRE validar en la capa correcta:**

1. **Validación Básica (Presentation Layer):** Usar Bean Validation
```java
public record CreateTireRequest(
    @NotBlank(message = "Serial is required")
    @Size(min = 5, max = 20)
    String serial,
    
    @NotNull(message = "Brand is required")
    Long brandId,
    
    @Positive(message = "Price must be positive")
    BigDecimal price
) { }
```

2. **Validación de Negocio (Domain Layer):** Lógica en entidades/value objects
```java
public class Tire {
    private TireId id;
    private SerialNumber serialNumber;
    private TireCondition condition;
    
    public void markAsRetired() {
        if (!this.condition.canBeRetired()) {
            throw new InvalidTireStateException(
                "Tire cannot be retired in current condition: " + condition
            );
        }
        this.condition = TireCondition.RETIRED;
    }
}
```

3. **Validación de Aplicación (Application Layer):** Reglas complejas
```java
public class RegisterTireUseCase {
    
    public TireId execute(CreateTireCommand command) {
        // Validar reglas de negocio complejas
        if (tireRepository.existsBySerialNumber(command.serialNumber())) {
            throw new DuplicateTireException(command.serialNumber());
        }
        
        Brand brand = brandRepository.findById(command.brandId())
            .orElseThrow(() -> new BrandNotFoundException(command.brandId()));
            
        if (!brand.isActive()) {
            throw new InactiveBrandException(command.brandId());
        }
        
        // ... resto de lógica
    }
}
```

### C4. Logging (OBLIGATORIO)

**SIEMPRE usar SLF4J + Logback:**

```java
@Service
public class RegisterTireUseCase {
    private static final Logger log = LoggerFactory.getLogger(RegisterTireUseCase.class);
    
    public TireId execute(CreateTireCommand command) {
        log.info("Registering new tire with serial: {}", command.serialNumber());
        
        try {
            // lógica
            log.debug("Tire validated successfully: {}", tire);
            Tire savedTire = tireRepository.save(tire);
            log.info("Tire registered successfully with id: {}", savedTire.getId());
            return savedTire.getId();
            
        } catch (DomainException e) {
            log.warn("Business rule violation while registering tire: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering tire", e);
            throw new TireRegistrationException("Failed to register tire", e);
        }
    }
}
```

**Niveles de Log:**
- `ERROR`: Errores que requieren intervención inmediata
- `WARN`: Situaciones anómalas que no impiden operación
- `INFO`: Eventos importantes del negocio (auditoría)
- `DEBUG`: Información detallada para debugging
- `TRACE`: Información muy detallada (normalmente desactivado)

**❌ NUNCA hacer:**
- No usar `System.out.println()` o `System.err.println()`
- No loggear información sensible (passwords, tokens, datos personales)
- No loggear objetos completos en INFO (usar DEBUG)

---

## 🔄 MIGRACIÓN DESDE ORACLE FORMS

### M1. Análisis de Lógica Legacy

**ANTES de migrar cualquier formulario, SIEMPRE documentar:**

```markdown
## Form: F_TIRE_REGISTRATION

### Triggers Identificados
- **WHEN-NEW-FORM-INSTANCE**: Inicialización, carga catálogos
- **WHEN-VALIDATE-RECORD**: Validación serial único, marca activa
- **POST-INSERT**: Auditoría, generación código barras
- **KEY-COMMIT**: Validación final, transacción

### Lógica de Negocio Crítica
1. Serial debe ser único en el sistema
2. Solo marcas activas pueden ser asignadas
3. Precio no puede ser negativo
4. Al crear llanta nueva, generar código de barras automáticamente
5. Registrar usuario y fecha de creación

### Validaciones Forms vs Domain
| Forms Trigger | Equivalente Moderno |
|--------------|---------------------|
| WHEN-VALIDATE-ITEM | Bean Validation (@NotNull, etc.) |
| WHEN-VALIDATE-RECORD | Domain Entity validation |
| POST-QUERY | Read Model / Projection |
| PRE-INSERT | Use Case validation |
```

### M2. Migración de PL/SQL

**SIEMPRE migrar lógica PL/SQL a la capa correcta:**

```sql
-- ❌ LEGACY PL/SQL en Forms (Trigger POST-INSERT)
BEGIN
  IF :TIRE.CONDITION = 'NEW' THEN
    UPDATE INVENTORY 
    SET AVAILABLE_QTY = AVAILABLE_QTY + 1
    WHERE TIRE_TYPE_ID = :TIRE.TYPE_ID;
  END IF;
  
  INSERT INTO AUDIT_LOG (TABLE_NAME, OPERATION, USER_ID)
  VALUES ('TIRES', 'INSERT', :GLOBAL.USER_ID);
END;
```

**✅ MIGRACIÓN CORRECTA:**

```java
// Domain Event
public record TireRegisteredEvent(
    TireId tireId,
    TireTypeId typeId,
    TireCondition condition,
    LocalDateTime occurredOn,
    UserId registeredBy
) { }

// Use Case
public class RegisterTireUseCase {
    
    public TireId execute(CreateTireCommand command) {
        Tire tire = Tire.create(command.toAttributes());
        Tire savedTire = tireRepository.save(tire);
        
        // Publicar evento de dominio
        eventPublisher.publish(new TireRegisteredEvent(
            savedTire.getId(),
            savedTire.getTypeId(),
            savedTire.getCondition(),
            LocalDateTime.now(),
            command.userId()
        ));
        
        return savedTire.getId();
    }
}

// Event Handler (en Infrastructure)
@Component
public class TireInventoryUpdater {
    
    @EventListener
    @Transactional
    public void on(TireRegisteredEvent event) {
        if (event.condition() == TireCondition.NEW) {
            inventoryService.incrementAvailableQuantity(
                event.typeId()
            );
        }
    }
}

// Audit Handler (en Infrastructure)
@Component
public class AuditLogHandler {
    
    @EventListener
    @Async
    public void on(TireRegisteredEvent event) {
        auditRepository.save(new AuditLog(
            "TIRES",
            "INSERT",
            event.tireId().getValue(),
            event.registeredBy(),
            event.occurredOn()
        ));
    }
}
```

### M3. Migración de Reports

**SIEMPRE analizar estructura de report antes de migrar:**

```markdown
## Report: REP_TIRE_INVENTORY

### Consulta Original
- Query principal con 5 JOINs
- Filtros: fecha, marca, condición, ubicación
- Agrupación: por tipo de llanta
- Totales: cantidad, valor total

### Estrategia de Migración
1. Crear Read Model optimizado (tabla denormalizada o vista)
2. Implementar endpoint REST para filtros
3. Frontend: Exportar a PDF/Excel usando biblioteca
4. Considerar caché para reports frecuentes

### Decisión: Query Projection vs Read Model
- **Query Projection**: Para reports simples, bajo volumen
- **Read Model**: Para reports complejos, alto volumen, necesidad de performance
```

**Ejemplo de implementación:**

```java
// Query Projection (para reports simples)
public interface TireInventoryQueryRepository {
    
    @Query("""
        SELECT new com.empresa.tire.application.dto.TireInventoryDto(
            t.serialNumber, 
            b.name, 
            t.condition,
            t.location,
            t.purchasePrice
        )
        FROM Tire t
        JOIN t.brand b
        WHERE t.registeredDate BETWEEN :startDate AND :endDate
        AND (:brandId IS NULL OR t.brand.id = :brandId)
        ORDER BY t.registeredDate DESC
        """)
    List<TireInventoryDto> findInventoryReport(
        LocalDate startDate,
        LocalDate endDate,
        Long brandId
    );
}

// Controller
@GetMapping("/reports/inventory")
public ResponseEntity<List<TireInventoryDto>> getInventoryReport(
    @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
    @RequestParam(required = false) Long brandId
) {
    return ResponseEntity.ok(
        tireInventoryQueryRepository.findInventoryReport(startDate, endDate, brandId)
    );
}
```

### M4. Migración de Base de Datos Oracle → PostgreSQL

**Mapeo de Tipos de Datos:**

| Oracle | PostgreSQL | Notas |
|--------|-----------|-------|
| NUMBER(p,s) | NUMERIC(p,s) | Usar BigDecimal en Java |
| NUMBER | BIGINT | Para IDs |
| VARCHAR2(n) | VARCHAR(n) | |
| DATE | TIMESTAMP | Oracle DATE incluye tiempo |
| CLOB | TEXT | |
| BLOB | BYTEA | |
| RAW | BYTEA | |
| SYSDATE | CURRENT_TIMESTAMP | |

**Secuencias:**
```sql
-- ❌ Oracle
CREATE SEQUENCE tire_seq START WITH 1 INCREMENT BY 1;

-- ✅ PostgreSQL
CREATE SEQUENCE tire_seq START WITH 1 INCREMENT BY 1;
-- O usar IDENTITY (recomendado)
CREATE TABLE tires (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    -- ...
);
```

**Funciones Comunes:**
```sql
-- Oracle: NVL
-- PostgreSQL: COALESCE

-- Oracle: TO_CHAR(date, 'format')
-- PostgreSQL: TO_CHAR(date, 'format') -- mismo

-- Oracle: ROWNUM
-- PostgreSQL: LIMIT/OFFSET o ROW_NUMBER()
```

---

## 🔒 SEGURIDAD

### S1. Autenticación y Autorización

**SIEMPRE usar Spring Security + JWT:**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

**NUNCA exponer información sensible:**
```java
// ❌ INCORRECTO
public class UserResponse {
    private String password;  // NUNCA!
    private String salt;      // NUNCA!
}

// ✅ CORRECTO
public class UserResponse {
    private String username;
    private String email;
    private Set<String> roles;
    // Sin información sensible
}
```

### S2. Validación de Entrada (OBLIGATORIO)

**SIEMPRE sanitizar y validar entrada del usuario:**

```java
@PostMapping("/tires")
public ResponseEntity<TireResponse> create(
    @Valid @RequestBody CreateTireRequest request  // @Valid es OBLIGATORIO
) {
    // ...
}

// DTO con validaciones
public record CreateTireRequest(
    @NotBlank @Pattern(regexp = "^[A-Z0-9-]{5,20}$")
    String serialNumber,
    
    @NotNull @Positive
    Long brandId,
    
    @NotNull @DecimalMin("0.0")
    BigDecimal price
) {
    // Validación adicional de negocio
    public CreateTireRequest {
        if (serialNumber != null) {
            serialNumber = serialNumber.trim().toUpperCase();
        }
    }
}
```

### S3. SQL Injection Prevention

**SIEMPRE usar JPA/JPQL o Named Parameters:**

```java
// ✅ CORRECTO - JPA Query con parámetros
@Query("SELECT t FROM Tire t WHERE t.serialNumber = :serial")
Optional<Tire> findBySerialNumber(@Param("serial") String serial);

// ✅ CORRECTO - Criteria API
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Tire> query = cb.createQuery(Tire.class);
Root<Tire> tire = query.from(Tire.class);
query.where(cb.equal(tire.get("serialNumber"), serialNumber));

// ❌ INCORRECTO - Concatenación de strings
String sql = "SELECT * FROM tires WHERE serial = '" + serial + "'";  // NUNCA!
```

---

## 🧪 TESTING

### T1. Estrategia de Testing (OBLIGATORIO)

**Pirámide de Testing:**
```
           /\
          /  \
         / E2E\      ← Pocos (críticos del negocio)
        /______\
       /        \
      /  Integr. \   ← Algunos (por módulo)
     /____________\
    /              \
   /   Unit Tests   \  ← Muchos (toda lógica de negocio)
  /__________________\
```

**Cobertura Mínima Obligatoria:**
- Domain Layer: 90%+
- Application Layer: 85%+
- Infrastructure: 70%+
- Presentation: 60%+

### T2. Unit Tests

**SIEMPRE testear lógica de dominio:**

```java
class TireTest {
    
    @Test
    void shouldNotRetireTireInNewCondition() {
        // Given
        Tire tire = TireFixture.createNew();
        
        // When & Then
        assertThrows(InvalidTireStateException.class, () -> 
            tire.markAsRetired()
        );
    }
    
    @Test
    void shouldCalculateRemainingLifeCorrectly() {
        // Given
        Tire tire = TireFixture.createWithMileage(50000);
        int expectedLifeMileage = 80000;
        
        // When
        int remainingLife = tire.calculateRemainingLife();
        
        // Then
        assertEquals(30000, remainingLife);
    }
}
```

**Use Cases con Mocks:**
```java
@ExtendWith(MockitoExtension.class)
class RegisterTireUseCaseTest {
    
    @Mock
    private TireRepository tireRepository;
    
    @Mock
    private BrandRepository brandRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private RegisterTireUseCase useCase;
    
    @Test
    void shouldRegisterTireSuccessfully() {
        // Given
        CreateTireCommand command = new CreateTireCommand(
            "ABC123",
            1L,
            BigDecimal.valueOf(500)
        );
        
        Brand brand = BrandFixture.createActive();
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(tireRepository.existsBySerialNumber(any())).thenReturn(false);
        when(tireRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        // When
        TireId result = useCase.execute(command);
        
        // Then
        assertNotNull(result);
        verify(tireRepository).save(any(Tire.class));
        verify(eventPublisher).publish(any(TireRegisteredEvent.class));
    }
    
    @Test
    void shouldThrowExceptionWhenBrandNotFound() {
        // Given
        CreateTireCommand command = new CreateTireCommand("ABC123", 999L, BigDecimal.valueOf(500));
        when(brandRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(BrandNotFoundException.class, () -> 
            useCase.execute(command)
        );
        verify(tireRepository, never()).save(any());
    }
}
```

### T3. Integration Tests

**SIEMPRE usar Testcontainers para PostgreSQL:**

```java
@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TireRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private TireRepository tireRepository;
    
    @Test
    void shouldSaveAndRetrieveTire() {
        // Given
        Tire tire = TireFixture.create();
        
        // When
        Tire saved = tireRepository.save(tire);
        Optional<Tire> retrieved = tireRepository.findById(saved.getId());
        
        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(tire.getSerialNumber(), retrieved.get().getSerialNumber());
    }
}
```

### T4. Test Fixtures

**SIEMPRE usar Builder Pattern o Test Data Builders:**

```java
public class TireFixture {
    
    public static Tire create() {
        return Tire.builder()
            .id(new TireId(1L))
            .serialNumber(new SerialNumber("ABC123"))
            .brand(BrandFixture.create())
            .condition(TireCondition.NEW)
            .purchasePrice(Money.of(500))
            .build();
    }
    
    public static Tire createWithSerialNumber(String serial) {
        return create().toBuilder()
            .serialNumber(new SerialNumber(serial))
            .build();
    }
    
    public static Tire createWithMileage(int mileage) {
        return create().toBuilder()
            .currentMileage(mileage)
            .build();
    }
}
```

---

## 📊 BASE DE DATOS

### D1. JPA Entities vs Domain Entities

**SIEMPRE separar entidades JPA de entidades de dominio:**

```java
// ✅ CORRECTO - Domain Entity (en domain package)
public class Tire {
    private TireId id;
    private SerialNumber serialNumber;
    private Brand brand;
    private TireCondition condition;
    private Money purchasePrice;
    
    // Lógica de negocio
    public void markAsRetired() {
        if (!condition.canBeRetired()) {
            throw new InvalidTireStateException();
        }
        this.condition = TireCondition.RETIRED;
    }
}

// ✅ CORRECTO - JPA Entity (en infrastructure.persistence)
@Entity
@Table(name = "tires")
public class TireJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandJpaEntity brand;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "condition")
    private TireConditionEnum condition;
    
    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;
    
    // Métodos de mapeo
    public Tire toDomain() {
        return new Tire(
            new TireId(id),
            new SerialNumber(serialNumber),
            brand.toDomain(),
            TireCondition.valueOf(condition.name()),
            Money.of(purchasePrice)
        );
    }
    
    public static TireJpaEntity fromDomain(Tire tire) {
        TireJpaEntity entity = new TireJpaEntity();
        entity.id = tire.getId().getValue();
        entity.serialNumber = tire.getSerialNumber().getValue();
        // ... mapeo completo
        return entity;
    }
}
```

### D2. Repositorios

**SIEMPRE usar el patrón Repository correctamente:**

```java
// Domain Repository Interface (en domain.repository)
public interface TireRepository {
    Tire save(Tire tire);
    Optional<Tire> findById(TireId id);
    Optional<Tire> findBySerialNumber(SerialNumber serialNumber);
    List<Tire> findByBrand(BrandId brandId);
    boolean existsBySerialNumber(SerialNumber serialNumber);
    void delete(TireId id);
}

// JPA Repository (en infrastructure.persistence)
public interface TireJpaRepository extends JpaRepository<TireJpaEntity, Long> {
    Optional<TireJpaEntity> findBySerialNumber(String serialNumber);
    List<TireJpaEntity> findByBrandId(Long brandId);
    boolean existsBySerialNumber(String serialNumber);
}

// Adapter (en infrastructure.persistence)
@Repository
public class JpaTireRepositoryAdapter implements TireRepository {
    
    private final TireJpaRepository jpaRepository;
    
    @Override
    public Tire save(Tire tire) {
        TireJpaEntity entity = TireJpaEntity.fromDomain(tire);
        TireJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Tire> findById(TireId id) {
        return jpaRepository.findById(id.getValue())
            .map(TireJpaEntity::toDomain);
    }
    
    // ... resto de implementaciones
}
```

### D3. Transacciones

**SIEMPRE manejar transacciones en la capa de aplicación:**

```java
@Service
public class RegisterTireUseCase {
    
    @Transactional  // ← Aquí, no en el repositorio
    public TireId execute(CreateTireCommand command) {
        // Toda la operación en una transacción
        Brand brand = brandRepository.findById(command.brandId())
            .orElseThrow(() -> new BrandNotFoundException(command.brandId()));
            
        Tire tire = Tire.create(command.toAttributes());
        Tire savedTire = tireRepository.save(tire);
        
        eventPublisher.publish(new TireRegisteredEvent(savedTire));
        
        return savedTire.getId();
    }
}
```

**Propagación de transacciones:**
```java
// REQUIRES_NEW: Para operaciones que deben ejecutarse independientemente
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logAuditEvent(AuditEvent event) {
    auditRepository.save(event);
}

// MANDATORY: Para operaciones que deben ejecutarse dentro de una transacción existente
@Transactional(propagation = Propagation.MANDATORY)
public void updateInventory(TireId tireId) {
    // ...
}
```

### D4. Migrations con Flyway

**SIEMPRE usar Flyway para migrations:**

```sql
-- V1__create_tires_table.sql
CREATE TABLE tires (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    serial_number VARCHAR(20) NOT NULL UNIQUE,
    brand_id BIGINT NOT NULL REFERENCES brands(id),
    condition VARCHAR(20) NOT NULL,
    purchase_price NUMERIC(10,2) NOT NULL,
    purchase_date DATE NOT NULL,
    current_mileage INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL
);

CREATE INDEX idx_tires_serial_number ON tires(serial_number);
CREATE INDEX idx_tires_brand_id ON tires(brand_id);
CREATE INDEX idx_tires_condition ON tires(condition);
```

**Convenciones de nombrado:**
- `V{version}__{description}.sql` - Migraciones versionadas
- `R__{description}.sql` - Migraciones repetibles (vistas, funciones)
- Siempre incluir rollback manual en comentarios

---

## 🎨 FRONTEND (React + TypeScript)

### F1. Estructura de Componentes

**SIEMPRE seguir arquitectura de componentes clara:**

```
src/
├── features/
│   └── tire/
│       ├── components/
│       │   ├── TireList.tsx
│       │   ├── TireForm.tsx
│       │   └── TireDetail.tsx
│       ├── hooks/
│       │   └── useTires.ts
│       ├── services/
│       │   └── tireService.ts
│       ├── types/
│       │   └── tire.types.ts
│       └── api/
│           └── tireApi.ts
├── shared/
│   ├── components/
│   ├── hooks/
│   └── utils/
└── core/
    ├── api/
    ├── auth/
    └── config/
```

### F2. TypeScript Strict Mode

**SIEMPRE usar TypeScript en modo estricto:**

```typescript
// tsconfig.json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true
  }
}
```

**Definir tipos explícitos:**
```typescript
// ✅ CORRECTO
interface Tire {
  id: number;
  serialNumber: string;
  brand: Brand;
  condition: TireCondition;
  purchasePrice: number;
}

interface CreateTireDto {
  serialNumber: string;
  brandId: number;
  purchasePrice: number;
}

// ❌ INCORRECTO
const tire: any = { ... };  // NUNCA usar 'any'
```

### F3. Custom Hooks

**SIEMPRE encapsular lógica en custom hooks:**

```typescript
// useTires.ts
export const useTires = () => {
  const [tires, setTires] = useState<Tire[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchTires = async (filters?: TireFilters) => {
    setLoading(true);
    setError(null);
    try {
      const data = await tireService.getAll(filters);
      setTires(data);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  const createTire = async (dto: CreateTireDto) => {
    try {
      const newTire = await tireService.create(dto);
      setTires(prev => [...prev, newTire]);
      return newTire;
    } catch (err) {
      setError(err as Error);
      throw err;
    }
  };

  return {
    tires,
    loading,
    error,
    fetchTires,
    createTire
  };
};
```

### F4. Manejo de Errores en Frontend

**SIEMPRE usar Error Boundaries:**

```typescript
class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // Enviar a servicio de logging (ej: Sentry)
  }

  render() {
    if (this.state.hasError) {
      return (
        <Alert severity="error">
          <AlertTitle>Error</AlertTitle>
          {this.state.error?.message || 'Ocurrió un error inesperado'}
        </Alert>
      );
    }

    return this.props.children;
  }
}
```

---

## 🚀 PERFORMANCE

### P1. Optimizaciones de Backend

**SIEMPRE considerar estas optimizaciones:**

1. **Lazy Loading en JPA:**
```java
@Entity
public class TireJpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)  // ← Lazy por defecto
    private BrandJpaEntity brand;
}
```

2. **Paginación obligatoria:**
```java
@GetMapping("/tires")
public Page<TireResponse> findAll(
    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
    Pageable pageable
) {
    return tireService.findAll(pageable)
        .map(TireResponse::from);
}
```

3. **Caché para catálogos:**
```java
@Service
public class BrandService {
    
    @Cacheable(value = "brands", key = "#id")
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }
    
    @CacheEvict(value = "brands", key = "#brand.id")
    public Brand update(Brand brand) {
        return brandRepository.save(brand);
    }
}
```

4. **Connection Pooling (HikariCP):**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### P2. Optimizaciones de Frontend

**SIEMPRE implementar:**

1. **Code Splitting:**
```typescript
// Lazy loading de rutas
const TireModule = lazy(() => import('./features/tire/TireModule'));

<Suspense fallback={<Loading />}>
  <Routes>
    <Route path="/tires/*" element={<TireModule />} />
  </Routes>
</Suspense>
```

2. **Memoización:**
```typescript
const TireList: React.FC<Props> = ({ tires, onSelect }) => {
  const filteredTires = useMemo(() => {
    return tires.filter(tire => tire.condition === 'ACTIVE');
  }, [tires]);

  const handleSelect = useCallback((id: number) => {
    onSelect(id);
  }, [onSelect]);

  return (
    // ...
  );
};
```

3. **Virtualization para listas grandes:**
```typescript
import { FixedSizeList } from 'react-window';

const TireListVirtualized: React.FC<Props> = ({ tires }) => {
  return (
    <FixedSizeList
      height={600}
      itemCount={tires.length}
      itemSize={80}
      width="100%"
    >
      {({ index, style }) => (
        <TireItem tire={tires[index]} style={style} />
      )}
    </FixedSizeList>
  );
};
```

---

## 📖 DOCUMENTACIÓN

### DOC1. Documentación de Código

**SIEMPRE documentar:**

1. **Clases de Dominio:**
```java
/**
 * Representa una llanta en el sistema de gestión.
 * 
 * <p>Una llanta puede estar en diferentes condiciones (nueva, usada, reencauchada)
 * y tiene un ciclo de vida completo desde su registro hasta su retiro.</p>
 * 
 * <p>Reglas de negocio:</p>
 * <ul>
 *   <li>El número de serie debe ser único en el sistema</li>
 *   <li>Solo llantas en condición USADA pueden ser reencauchadas</li>
 *   <li>Una llanta retirada no puede volver a estado activo</li>
 * </ul>
 * 
 * @author Sistema Migración
 * @since 1.0.0
 */
public class Tire {
    // ...
}
```

2. **Métodos Complejos:**
```java
/**
 * Calcula la vida útil restante de la llanta basándose en el kilometraje actual
 * y el kilometraje esperado según el tipo y condición.
 * 
 * @return kilometraje restante estimado, 0 si ya excedió la vida útil
 * @throws IllegalStateException si la llanta está en estado RETIRED
 */
public int calculateRemainingLife() {
    // ...
}
```

3. **APIs REST (OpenAPI):**
```java
@Operation(
    summary = "Registrar nueva llanta",
    description = "Registra una nueva llanta en el sistema con su información básica"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Llanta creada exitosamente"),
    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
    @ApiResponse(responseCode = "409", description = "Número de serie duplicado")
})
@PostMapping("/tires")
public ResponseEntity<TireResponse> create(
    @Valid @RequestBody CreateTireRequest request
) {
    // ...
}
```

### DOC2. README por Módulo

**SIEMPRE incluir README.md en cada módulo:**

```markdown
# Módulo: Tire (Gestión de Llantas)

## Descripción
Módulo responsable de la gestión completa del ciclo de vida de las llantas.

## Bounded Context
Este módulo maneja todo lo relacionado con:
- Registro y baja de llantas
- Control de condiciones (nueva, usada, reencauchada)
- Seguimiento de kilometraje
- Mantenimiento preventivo

## Dependencias
- `shared`: Para catálogos (marcas, tipos)
- `inventory`: Eventos de movimientos

## Eventos de Dominio
- `TireRegisteredEvent`: Cuando se registra una llanta nueva
- `TireRetiredEvent`: Cuando se retira una llanta
- `TireConditionChangedEvent`: Cuando cambia la condición

## Endpoints Principales
- `POST /api/v1/tires` - Registrar llanta
- `GET /api/v1/tires/{id}` - Obtener llanta
- `PUT /api/v1/tires/{id}` - Actualizar llanta
- `DELETE /api/v1/tires/{id}` - Retirar llanta

## Casos de Uso
1. `RegisterTireUseCase`: Registrar nueva llanta
2. `UpdateTireConditionUseCase`: Cambiar condición
3. `RetireTireUseCase`: Retirar llanta del servicio
```

---

## 🔍 CODE REVIEW CHECKLIST

**ANTES de considerar completo cualquier feature, verificar:**

### Arquitectura
- [ ] Respeta las capas de Clean Architecture
- [ ] Dependencias apuntan hacia el dominio
- [ ] No hay lógica de negocio en controllers
- [ ] Entidades de dominio separadas de JPA entities

### Código
- [ ] Cumple principios SOLID
- [ ] Nombres descriptivos y consistentes
- [ ] Sin código duplicado (DRY)
- [ ] Métodos cortos y enfocados (SRP)
- [ ] Sin "magic numbers" (usar constantes)

### Seguridad
- [ ] Input validation implementada
- [ ] Sin hardcoded credentials
- [ ] Autorización verificada
- [ ] Sin SQL injection posible
- [ ] Información sensible no expuesta

### Testing
- [ ] Unit tests para lógica de dominio
- [ ] Integration tests para repositorios
- [ ] Cobertura mínima alcanzada
- [ ] Tests pasan en CI/CD

### Performance
- [ ] Paginación implementada
- [ ] Queries optimizadas (sin N+1)
- [ ] Lazy loading configurado correctamente
- [ ] Caché considerado para catálogos

### Documentación
- [ ] JavaDoc/TSDoc en clases y métodos públicos
- [ ] README actualizado
- [ ] OpenAPI/Swagger documentado
- [ ] Decisiones arquitectónicas registradas

---

## ⚠️ ANTI-PATRONES A EVITAR

### Backend

**❌ Anemic Domain Model:**
```java
// MAL: Entidades sin comportamiento
public class Tire {
    private Long id;
    private String serialNumber;
    // Solo getters/setters, sin lógica
}

public class TireService {
    public void retireTire(Tire tire) {
        tire.setCondition("RETIRED");  // Lógica fuera del dominio
        tireRepository.save(tire);
    }
}

// ✅ BIEN: Rich Domain Model
public class Tire {
    private TireId id;
    private SerialNumber serialNumber;
    private TireCondition condition;
    
    public void retire() {
        if (!condition.canBeRetired()) {
            throw new InvalidTireStateException();
        }
        this.condition = TireCondition.RETIRED;
        this.registerEvent(new TireRetiredEvent(this.id));
    }
}
```

**❌ Transaction Script:**
```java
// MAL: Toda la lógica en el servicio
public class TireService {
    public void process(TireRequest request) {
        // 200 líneas de lógica aquí
    }
}

// ✅ BIEN: Use Cases específicos
public class RegisterTireUseCase { }
public class UpdateTireConditionUseCase { }
public class RetireTireUseCase { }
```

**❌ God Objects:**
```java
// MAL
public class TireService {
    public void create() { }
    public void update() { }
    public void delete() { }
    public void search() { }
    public void export() { }
    public void import() { }
    public void validate() { }
    public void sendEmail() { }
    // ... 50 métodos más
}
```

### Frontend

**❌ Prop Drilling:**
```typescript
// MAL: Pasar props a través de múltiples niveles
<GrandParent data={data}>
  <Parent data={data}>
    <Child data={data} />
  </Parent>
</GrandParent>

// ✅ BIEN: Usar Context o State Management
const TireContext = createContext<TireContextType | null>(null);

export const useTireContext = () => {
  const context = useContext(TireContext);
  if (!context) throw new Error('useTireContext must be used within TireProvider');
  return context;
};
```

**❌ Inline Styles:**
```typescript
// MAL
<div style={{ color: 'red', fontSize: '16px' }}>

// ✅ BIEN: Usar Material-UI sx prop o styled components
<Box sx={{ color: 'error.main', typography: 'body1' }}>
```

---

## 📝 CONVENCIONES DE COMMIT

**SIEMPRE usar Conventional Commits:**

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Tipos:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `refactor`: Refactorización de código
- `docs`: Cambios en documentación
- `test`: Añadir o modificar tests
- `chore`: Cambios en build, deps, etc.
- `perf`: Mejoras de performance

**Ejemplos:**
```
feat(tire): add tire registration use case

Implement RegisterTireUseCase with validation and events.
- Serial number uniqueness validation
- Brand existence verification
- Event publishing on success

Closes #123

---

fix(tire): prevent retired tire condition change

Add validation to prevent changing condition of retired tires.

Fixes #456

---

refactor(tire): extract tire validation to domain service

Move validation logic from use case to TireValidationService
to improve reusability and testability.
```

---

## 🎓 RECURSOS Y REFERENCIAS

### Arquitectura
- Clean Architecture (Robert C. Martin)
- Domain-Driven Design (Eric Evans)
- Implementing Domain-Driven Design (Vaughn Vernon)

### Spring Boot
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)

### React + TypeScript
- [React Documentation](https://react.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Material-UI Documentation](https://mui.com/)

### Testing
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)

---

## 📞 SOPORTE

**Si encuentras dudas o necesitas clarificación:**
1. Consulta primero este documento
2. Revisa los ejemplos de código en el proyecto
3. Pregunta en el canal de desarrollo
4. Documenta decisiones arquitectónicas en ADR (Architecture Decision Records)

---

## ✅ RESUMEN DE REGLAS CRÍTICAS

**SIEMPRE:**
1. Respetar Clean Architecture y separación de capas
2. Validar input en todas las capas apropiadas
3. Usar excepciones específicas del dominio
4. Implementar logging apropiado
5. Escribir tests para lógica de negocio
6. Documentar decisiones arquitectónicas
7. Usar transacciones en la capa de aplicación
8. Separar entidades JPA de entidades de dominio
9. Implementar paginación para listas
10. Seguir convenciones de naming

**NUNCA:**
1. Hardcodear credenciales o configuración
2. Exponer entidades de dominio en APIs
3. Usar concatenación de strings para SQL
4. Ignorar errores silenciosamente
5. Dejar código comentado en commits
6. Usar `any` en TypeScript
7. Implementar lógica de negocio en controllers
8. Hacer queries sin paginación
9. Deployar sin tests pasando
10. Olvidar migrations de base de datos

---

**Versión:** 1.0.0  
**Última actualización:** 2026-01-22  
**Autor:** Arquitectura de Software
