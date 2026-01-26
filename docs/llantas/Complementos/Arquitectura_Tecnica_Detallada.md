# ARQUITECTURA TÉCNICA DETALLADA
## MIGRACIÓN SISTEMA DE GESTIÓN DE LLANTAS

---

**Versión:** 1.0  
**Fecha:** 20 de Enero de 2026  
**Stack:** Java 21 + Spring Boot 3.5 | React 18 + TypeScript | PostgreSQL 18

---

## 📋 ÍNDICE

1. [Visión General](#1-visión-general)
2. [Arquitectura de Capas](#2-arquitectura-de-capas)
3. [Modelo de Dominio (DDD)](#3-modelo-de-dominio-ddd)
4. [Diseño de Base de Datos](#4-diseño-de-base-de-datos)
5. [APIs y Contratos](#5-apis-y-contratos)
6. [Patrones de Diseño](#6-patrones-de-diseño)
7. [Seguridad](#7-seguridad)
8. [Performance y Escalabilidad](#8-performance-y-escalabilidad)
9. [Estrategia de Migración](#9-estrategia-de-migración)

---

## 1. VISIÓN GENERAL

### 1.1 Arquitectura Objetivo

```
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND LAYER                               │
│  React 18 + TypeScript + Material-UI + Redux Toolkit           │
└────────────────────┬────────────────────────────────────────────┘
                     │ HTTPS/REST
┌────────────────────▼────────────────────────────────────────────┐
│                   API GATEWAY                                    │
│  Spring Cloud Gateway + JWT Authentication + Rate Limiting      │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                  BACKEND SERVICES                                │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  PRESENTATION LAYER (Controllers + DTOs)                 │  │
│  └────────────────────┬─────────────────────────────────────┘  │
│                       │                                          │
│  ┌────────────────────▼─────────────────────────────────────┐  │
│  │  APPLICATION LAYER (Use Cases + Services)                │  │
│  └────────────────────┬─────────────────────────────────────┘  │
│                       │                                          │
│  ┌────────────────────▼─────────────────────────────────────┐  │
│  │  DOMAIN LAYER (Entities + Value Objects + Repositories) │  │
│  └────────────────────┬─────────────────────────────────────┘  │
│                       │                                          │
│  ┌────────────────────▼─────────────────────────────────────┐  │
│  │  INFRASTRUCTURE LAYER (JPA + External APIs + Cache)      │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│                 POSTGRESQL 18                                    │
└──────────────────────────────────────────────────────────────────┘
```

### 1.2 Principios Arquitectónicos

1. **Clean Architecture / Hexagonal**
   - Independencia de frameworks
   - Testabilidad
   - Independencia de UI
   - Independencia de Base de Datos

2. **Domain-Driven Design (DDD)**
   - Ubiquitous Language
   - Bounded Contexts
   - Aggregates y Entities
   - Value Objects
   - Domain Services
   - Domain Events

3. **SOLID Principles**
   - Single Responsibility
   - Open/Closed
   - Liskov Substitution
   - Interface Segregation
   - Dependency Inversion

---

## 2. ARQUITECTURA DE CAPAS

### 2.1 Presentation Layer

**Responsabilidad:** Manejar HTTP requests/responses, validación de entrada, transformación a DTOs

**Componentes:**
```
src/main/java/com/transer/llantas/presentation/
├── controller/
│   ├── VehiculoController.java
│   ├── LlantaController.java
│   ├── MuestreoController.java
│   └── ReporteController.java
├── dto/
│   ├── request/
│   │   ├── CrearVehiculoRequest.java
│   │   ├── MontarLlantaRequest.java
│   │   └── RegistrarMuestreoRequest.java
│   └── response/
│       ├── VehiculoResponse.java
│       ├── LlantaActivaResponse.java
│       └── MuestreoResultResponse.java
├── mapper/
│   ├── VehiculoMapper.java
│   └── LlantaMapper.java
└── validation/
    ├── PlacaValidator.java
    └── KilometrajeValidator.java
```

**Ejemplo de Controller:**
```java
@RestController
@RequestMapping("/api/llantas")
@RequiredArgsConstructor
@Validated
public class LlantaController {

    private final MontarLlantaUseCase montarLlantaUseCase;
    private final LlantaMapper mapper;

    @PostMapping("/montar")
    @PreAuthorize("hasRole('OPERARIO_TALLER')")
    public ResponseEntity<MontarLlantaResponse> montarLlanta(
            @Valid @RequestBody MontarLlantaRequest request
    ) {
        // 1. Mapear DTO a Comando
        var comando = mapper.toComando(request);
        
        // 2. Ejecutar caso de uso
        var resultado = montarLlantaUseCase.execute(comando);
        
        // 3. Mapear resultado a Response
        var response = mapper.toResponse(resultado);
        
        return ResponseEntity.ok(response);
    }
}
```

**DTOs:**
```java
@Data
@Builder
public class MontarLlantaRequest {
    
    @NotNull(message = "Llanta es requerida")
    private LlantaId llantaId;
    
    @NotBlank(message = "Placa del vehículo es requerida")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}$", message = "Formato de placa inválido")
    private String placa;
    
    @NotNull(message = "Posición es requerida")
    @Min(value = 1, message = "Posición debe ser >= 1")
    @Max(value = 20, message = "Posición debe ser <= 20")
    private Integer posicion;
    
    @NotNull(message = "Kilometraje de instalación es requerido")
    @Min(value = 0, message = "Kilometraje debe ser >= 0")
    private Integer kilometrajeInstalacion;
    
    @NotNull(message = "Fecha de instalación es requerida")
    @PastOrPresent(message = "Fecha no puede ser futura")
    private LocalDate fechaInstalacion;
}
```

---

### 2.2 Application Layer

**Responsabilidad:** Orquestación de casos de uso, transacciones, eventos de dominio

**Componentes:**
```
src/main/java/com/transer/llantas/application/
├── usecase/
│   ├── llanta/
│   │   ├── MontarLlantaUseCase.java
│   │   ├── DesmontarLlantaUseCase.java
│   │   └── RotarLlantasUseCase.java
│   ├── muestreo/
│   │   ├── RegistrarMuestreoUseCase.java
│   │   └── CalcularProyeccionVidaUtilUseCase.java
│   └── vehiculo/
│       ├── CrearVehiculoUseCase.java
│       └── ActualizarKilometrajeUseCase.java
├── service/
│   ├── GestionLlantaService.java
│   ├── MuestreoService.java
│   └── AnalisisRendimientoService.java
└── event/
    ├── LlantaMontadaEvent.java
    ├── AlertaGeneradaEvent.java
    └── MuestreoRegistradoEvent.java
```

**Ejemplo de Use Case:**
```java
@Service
@Transactional
@RequiredArgsConstructor
public class MontarLlantaUseCase {

    private final VehiculoRepository vehiculoRepository;
    private final LlantaRepository llantaRepository;
    private final InventarioRepository inventarioRepository;
    private final HistoriaRepository historiaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MontarLlantaResultado execute(MontarLlantaComando comando) {
        // 1. Validar vehículo existe y está activo
        var vehiculo = vehiculoRepository.findByPlaca(comando.getPlaca())
            .orElseThrow(() -> new VehiculoNoEncontradoException(comando.getPlaca()));
        
        if (!vehiculo.estaActivo()) {
            throw new VehiculoInactivoException(comando.getPlaca());
        }

        // 2. Validar posición disponible
        if (llantaRepository.existeLlantaEnPosicion(
                comando.getPlaca(), 
                comando.getPosicion()
        )) {
            throw new PosicionOcupadaException(
                comando.getPlaca(), 
                comando.getPosicion()
            );
        }

        // 3. Obtener llanta del inventario
        var llanta = inventarioRepository.findByLlantaId(comando.getLlantaId())
            .orElseThrow(() -> new LlantaNoDisponibleException(comando.getLlantaId()));

        // 4. Validar kilometraje
        if (comando.getKilometrajeInstalacion() < vehiculo.getKilometrajeActual()) {
            throw new KilometrajeInvalidoException(
                "Kilometraje de instalación no puede ser menor que kilometraje actual del vehículo"
            );
        }

        // 5. Crear llanta activa
        var llantaActiva = LlantaActiva.builder()
            .llantaId(comando.getLlantaId())
            .placa(comando.getPlaca())
            .posicion(comando.getPosicion())
            .kilometrajeInstalacion(comando.getKilometrajeInstalacion())
            .fechaInstalacion(comando.getFechaInstalacion())
            .valor(llanta.getValor())
            .fichaId(llanta.getFichaId())
            .build();

        // 6. Ejecutar transacción
        inventarioRepository.eliminar(comando.getLlantaId());
        llantaRepository.guardar(llantaActiva);
        
        // 7. Crear registro en historia
        var historia = Historia.nuevaMontaje(
            comando.getLlantaId(),
            comando.getPlaca(),
            comando.getPosicion(),
            comando.getKilometrajeInstalacion(),
            comando.getFechaInstalacion(),
            llanta
        );
        historiaRepository.guardar(historia);

        // 8. Actualizar kilometraje del vehículo si es mayor
        if (comando.getKilometrajeInstalacion() > vehiculo.getKilometrajeActual()) {
            vehiculo.actualizarKilometraje(comando.getKilometrajeInstalacion());
            vehiculoRepository.actualizar(vehiculo);
        }

        // 9. Publicar evento de dominio
        eventPublisher.publishEvent(new LlantaMontadaEvent(
            comando.getLlantaId(),
            comando.getPlaca(),
            comando.getPosicion(),
            LocalDateTime.now()
        ));

        // 10. Retornar resultado
        return MontarLlantaResultado.builder()
            .llantaId(comando.getLlantaId())
            .placa(comando.getPlaca())
            .posicion(comando.getPosicion())
            .exitoso(true)
            .mensaje("Llanta montada exitosamente")
            .build();
    }
}
```

---

### 2.3 Domain Layer

**Responsabilidad:** Lógica de negocio, entidades, value objects, reglas de dominio

**Estructura:**
```
src/main/java/com/transer/llantas/domain/
├── entity/
│   ├── Llanta.java
│   ├── LlantaActiva.java
│   ├── Vehiculo.java
│   ├── Muestreo.java
│   └── Historia.java
├── valueobject/
│   ├── LlantaId.java
│   ├── Placa.java
│   ├── Profundidad.java
│   └── EstadoLlanta.java
├── aggregate/
│   └── VehiculoAggregate.java
├── service/
│   ├── GestionLlantaDomainService.java
│   └── MuestreoDomainService.java
├── repository/
│   ├── VehiculoRepository.java
│   ├── LlantaRepository.java
│   └── MuestreoRepository.java
├── event/
│   ├── DomainEvent.java
│   ├── LlantaMontadaEvent.java
│   └── MuestreoRegistradoEvent.java
└── exception/
    ├── DomainException.java
    ├── VehiculoNoEncontradoException.java
    └── PosicionOcupadaException.java
```

**Ejemplo de Entity:**
```java
@Entity
@Table(name = "llantas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LlantaActiva {

    @EmbeddedId
    private LlantaId id;
    
    @Column(name = "vehiculo", nullable = false, length = 6)
    private String placa;
    
    @Column(name = "posicion", nullable = false)
    private Integer posicion;
    
    @Column(name = "kinstala", nullable = false)
    private Integer kilometrajeInstalacion;
    
    @Column(name = "fechai", nullable = false)
    private LocalDate fechaInstalacion;
    
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;
    
    @Column(name = "ficha", nullable = false)
    private Integer fichaId;
    
    // Domain logic
    public int calcularKilometrosRecorridos(int kilometrajeActual) {
        if (kilometrajeActual < kilometrajeInstalacion) {
            throw new IllegalArgumentException(
                "Kilometraje actual no puede ser menor que kilometraje de instalación"
            );
        }
        return kilometrajeActual - kilometrajeInstalacion;
    }
    
    public boolean requiereMuestreo(Muestreo ultimoMuestreo, int kilometrajeActual) {
        if (ultimoMuestreo == null) {
            return calcularKilometrosRecorridos(kilometrajeActual) >= 10000;
        }
        
        int kmsDesdeMuestreo = kilometrajeActual - ultimoMuestreo.getKilometraje();
        int diasDesdeMuestreo = (int) ChronoUnit.DAYS.between(
            ultimoMuestreo.getFecha(), 
            LocalDate.now()
        );
        
        return kmsDesdeMuestreo >= 15000 || diasDesdeMuestreo >= 30;
    }
    
    public boolean estaEnPosicionDireccional() {
        return posicion <= 2; // Asumiendo que posiciones 1 y 2 son direccionales
    }
}
```

**Value Object:**
```java
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class LlantaId implements Serializable {

    @Column(name = "llanta", nullable = false, length = 20)
    private String numeroLlanta;
    
    @Column(name = "grupo", nullable = false, length = 3)
    private String grupo;
    
    public static LlantaId of(String numeroLlanta, String grupo) {
        Objects.requireNonNull(numeroLlanta, "Número de llanta no puede ser null");
        Objects.requireNonNull(grupo, "Grupo no puede ser null");
        
        if (!grupo.matches("\\d{3}")) {
            throw new IllegalArgumentException("Grupo debe ser 3 dígitos");
        }
        
        return new LlantaId(numeroLlanta, grupo);
    }
    
    public boolean esLlantaNueva() {
        return "000".equals(grupo);
    }
    
    public boolean esReencauche() {
        return !esLlantaNueva();
    }
    
    public int getNumeroReencauche() {
        return Integer.parseInt(grupo);
    }
}
```

---

### 2.4 Infrastructure Layer

**Responsabilidad:** Implementaciones concretas, acceso a datos, servicios externos

**Estructura:**
```
src/main/java/com/transer/llantas/infrastructure/
├── persistence/
│   ├── jpa/
│   │   ├── entity/
│   │   │   ├── LlantaJpaEntity.java
│   │   │   └── VehiculoJpaEntity.java
│   │   ├── repository/
│   │   │   ├── LlantaJpaRepository.java
│   │   │   └── VehiculoJpaRepository.java
│   │   └── mapper/
│   │       └── LlantaEntityMapper.java
│   └── adapter/
│       ├── LlantaRepositoryAdapter.java
│       └── VehiculoRepositoryAdapter.java
├── cache/
│   └── RedisCacheConfig.java
├── external/
│   ├── SiesaApiClient.java
│   └── EmailService.java
└── config/
    ├── DatabaseConfig.java
    └── SecurityConfig.java
```

**Repository Implementation:**
```java
@Repository
@RequiredArgsConstructor
public class LlantaRepositoryAdapter implements LlantaRepository {

    private final LlantaJpaRepository jpaRepository;
    private final LlantaEntityMapper mapper;
    
    @Override
    public Optional<LlantaActiva> findById(LlantaId id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<LlantaActiva> findByVehiculo(String placa) {
        return jpaRepository.findByPlaca(placa)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = "llantasActivas", key = "#placa")
    public boolean existeLlantaEnPosicion(String placa, Integer posicion) {
        return jpaRepository.existsByPlacaAndPosicion(placa, posicion);
    }
    
    @Override
    public void guardar(LlantaActiva llanta) {
        var entity = mapper.toEntity(llanta);
        jpaRepository.save(entity);
    }
    
    @Override
    public void eliminar(LlantaId id) {
        jpaRepository.deleteById(id);
    }
}
```

---

## 3. MODELO DE DOMINIO (DDD)

### 3.1 Bounded Contexts

**Context Map:**
```
┌──────────────────────┐
│  Gestión de Flota    │  ← Upstream
│  (Vehículos)         │
└──────────┬───────────┘
           │ Conformist
           ↓
┌──────────────────────┐
│  Gestión de Llantas  │  ← Core Domain
│  (Agregado raíz)     │
└──────────┬───────────┘
           │ Customer/Supplier
           ↓
┌──────────────────────┐     ┌──────────────────────┐
│  Muestreo y Control  │     │  Inventario          │
│  de Desgaste         │     │                      │
└──────────────────────┘     └──────────────────────┘
```

### 3.2 Aggregates

**Aggregate: VehiculoAggregate**
```java
@Getter
public class VehiculoAggregate {
    // Raíz del agregado
    private final Vehiculo vehiculo;
    
    // Entidades del agregado
    private final List<LlantaActiva> llantas;
    private final ConfiguracionLlantas configuracion;
    
    // Invariantes del agregado
    public void montarLlanta(LlantaActiva llanta) {
        // Validar invariantes
        validarPosicionDisponible(llanta.getPosicion());
        validarKilometraje(llanta.getKilometrajeInstalacion());
        
        // Agregar llanta
        this.llantas.add(llanta);
        
        // Publicar evento
        registrarEvento(new LlantaMontadaEvent(...));
    }
    
    private void validarPosicionDisponible(Integer posicion) {
        boolean ocupada = llantas.stream()
            .anyMatch(l -> l.getPosicion().equals(posicion));
            
        if (ocupada) {
            throw new PosicionOcupadaException(
                vehiculo.getPlaca(), 
                posicion
            );
        }
    }
    
    public boolean todasLlantasTienenMuestreoReciente() {
        return llantas.stream()
            .allMatch(l -> l.tieneM uestreoReciente());
    }
}
```

---

## 4. DISEÑO DE BASE DE DATOS

### 4.1 Migración de Oracle a PostgreSQL

**Cambios Principales:**

1. **Tipos de Datos:**
```sql
-- Oracle → PostgreSQL
VARCHAR2(20) → VARCHAR(20)
NUMBER(5,0) → INTEGER
NUMBER(7,2) → DECIMAL(7,2)
DATE → DATE (compatible)
CHAR(3) → CHAR(3) (compatible)
```

2. **Secuencias:**
```sql
-- PostgreSQL usa SERIAL o IDENTITY
CREATE TABLE fichatec (
    codigo SERIAL PRIMARY KEY,
    -- otros campos...
);

-- O explícitamente
CREATE SEQUENCE fichatec_codigo_seq;
ALTER TABLE fichatec ALTER COLUMN codigo 
SET DEFAULT nextval('fichatec_codigo_seq');
```

3. **Constraints:**
```sql
-- Mantener constraints con nombres compatibles
ALTER TABLE llantas
ADD CONSTRAINT pk_llantas_llangru 
PRIMARY KEY (llanta, grupo);

ALTER TABLE llantas
ADD CONSTRAINT uk_vehi_pos 
UNIQUE (vehiculo, posicion);

ALTER TABLE llantas
ADD CONSTRAINT fk_llantas_ficha 
FOREIGN KEY (ficha) REFERENCES fichatec(codigo);
```

### 4.2 Índices Optimizados

```sql
-- Índices para queries frecuentes
CREATE INDEX idx_llantas_vehiculo ON llantas(vehiculo);
CREATE INDEX idx_llantas_ficha ON llantas(ficha);
CREATE INDEX idx_muestreo_fecha ON muestreo(fecha DESC);
CREATE INDEX idx_historia_llanta_grupo ON historia(llanta, grupo);

-- Índices compuestos para consultas complejas
CREATE INDEX idx_llantas_activas_lookup 
ON llantas(vehiculo, posicion, llanta, grupo);

-- Índice parcial para llantas críticas
CREATE INDEX idx_muestreo_critico 
ON muestreo ((pi + pc + pd) / 3) 
WHERE (pi + pc + pd) / 3 < 3.0;

-- Índice GIN para búsquedas full-text (si se necesita)
CREATE INDEX idx_observa_detalle_fulltext 
ON observa USING GIN(to_tsvector('spanish', detalle));
```

### 4.3 Particionamiento (Opcional para gran volumen)

```sql
-- Particionar historia por año
CREATE TABLE historia_partitioned (
    llanta VARCHAR(20),
    grupo CHAR(3),
    fechai DATE NOT NULL,
    -- otros campos...
) PARTITION BY RANGE (fechai);

CREATE TABLE historia_2024 PARTITION OF historia_partitioned
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE historia_2025 PARTITION OF historia_partitioned
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

-- Índices en particiones
CREATE INDEX ON historia_2024(llanta, grupo);
CREATE INDEX ON historia_2025(llanta, grupo);
```

---

## 5. APIS Y CONTRATOS

### 5.1 API REST Endpoints

**Base URL:** `https://api.sistema-llantas.com/api/v1`

#### Vehículos
```yaml
GET    /vehiculos
GET    /vehiculos/{placa}
POST   /vehiculos
PUT    /vehiculos/{placa}
DELETE /vehiculos/{placa}
GET    /vehiculos/{placa}/llantas
```

#### Llantas
```yaml
POST   /llantas/montar
POST   /llantas/desmontar
POST   /llantas/rotar
GET    /llantas/activas
GET    /llantas/inventario
GET    /llantas/{numero}/{grupo}/historia
```

#### Muestreos
```yaml
POST   /muestreos/batch
GET    /muestreos/{llanta}/{grupo}
GET    /muestreos/pendientes
GET    /muestreos/reporte/{vehiculo}/{fecha}
```

#### Reportes
```yaml
GET    /reportes/llantas-activas
GET    /reportes/consumo-mensual
GET    /reportes/rendimiento-marcas
GET    /reportes/dashboard-kpis
```

### 5.2 Ejemplo de Contrato OpenAPI

```yaml
openapi: 3.0.3
info:
  title: Sistema de Gestión de Llantas API
  version: 1.0.0
  description: API para gestión integral de llantas en flotas de vehículos

servers:
  - url: https://api.sistema-llantas.com/api/v1
    description: Producción
  - url: https://api-dev.sistema-llantas.com/api/v1
    description: Desarrollo

paths:
  /llantas/montar:
    post:
      summary: Montar llanta en vehículo
      operationId: montarLlanta
      tags:
        - Llantas
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/MontarLlantaRequest'
      responses:
        '200':
          description: Llanta montada exitosamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/MontarLlantaResponse'
        '400':
          description: Datos inválidos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: Vehículo o llanta no encontrada
        '409':
          description: Posición ya ocupada

components:
  schemas:
    MontarLlantaRequest:
      type: object
      required:
        - llantaId
        - placa
        - posicion
        - kilometrajeInstalacion
        - fechaInstalacion
      properties:
        llantaId:
          $ref: '#/components/schemas/LlantaId'
        placa:
          type: string
          pattern: '^[A-Z]{3}[0-9]{3}$'
          example: 'XYZ123'
        posicion:
          type: integer
          minimum: 1
          maximum: 20
          example: 1
        kilometrajeInstalacion:
          type: integer
          minimum: 0
          example: 100000
        fechaInstalacion:
          type: string
          format: date
          example: '2026-01-20'
    
    LlantaId:
      type: object
      required:
        - numeroLlanta
        - grupo
      properties:
        numeroLlanta:
          type: string
          maxLength: 20
          example: 'LL-00543'
        grupo:
          type: string
          pattern: '^\d{3}$'
          example: '000'
          description: '000 para nueva, 001-999 para reencauches'
  
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

---

## 6. PATRONES DE DISEÑO

### 6.1 Patrones Utilizados

#### Repository Pattern
```java
public interface LlantaRepository {
    Optional<LlantaActiva> findById(LlantaId id);
    List<LlantaActiva> findByVehiculo(String placa);
    void guardar(LlantaActiva llanta);
    void eliminar(LlantaId id);
}
```

#### Use Case Pattern (Command Pattern)
```java
@FunctionalInterface
public interface UseCase<C extends Comando, R> {
    R execute(C comando);
}

// Implementación
public class MontarLlantaUseCase implements UseCase<MontarLlantaComando, MontarLlantaResultado> {
    @Override
    public MontarLlantaResultado execute(MontarLlantaComando comando) {
        // Lógica del caso de uso
    }
}
```

#### Factory Pattern
```java
public class LlantaFactory {
    public static LlantaActiva crearNueva(LlantaId id, String placa, Integer posicion, ...) {
        // Crear llanta nueva
    }
    
    public static LlantaActiva crearDesdeInventario(Llanta inventario, String placa, Integer posicion) {
        // Crear llanta desde inventario
    }
}
```

#### Strategy Pattern
```java
public interface CalculadorDesgasteStrategy {
    BigDecimal calcular(Muestreo muestreo, FichaTecnica ficha);
}

public class DesgastePorcentualStrategy implements CalculadorDesgasteStrategy {
    @Override
    public BigDecimal calcular(Muestreo muestreo, FichaTecnica ficha) {
        // Cálculo porcentual
    }
}
```

#### Observer Pattern (Domain Events)
```java
@Component
@RequiredArgsConstructor
public class AlertaEventListener {
    
    private final NotificacionService notificacionService;
    
    @EventListener
    @Async
    public void onAlertaGenerada(AlertaGeneradaEvent event) {
        if (event.getPrioridad() == Prioridad.ALTA) {
            notificacionService.enviarSMS(event);
            notificacionService.enviarEmail(event);
        }
    }
}
```

---

## 7. SEGURIDAD

### 7.1 Autenticación JWT

**Configuración:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/reportes/**").hasAnyRole("GERENTE", "ADMIN")
                .requestMatchers("/api/llantas/montar").hasAnyRole("OPERARIO", "JEFE_TALLER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }
}
```

### 7.2 Roles y Permisos

```java
public enum Role {
    ADMIN,
    GERENTE,
    JEFE_TALLER,
    OPERARIO_TALLER,
    ALMACENISTA,
    VISUALIZADOR
}

@PreAuthorize("hasRole('JEFE_TALLER') or hasRole('ADMIN')")
public void darDeBajaLlanta(LlantaId id) {
    // ...
}
```

---

## 8. PERFORMANCE Y ESCALABILIDAD

### 8.1 Caché con Redis

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                )
            );
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}

// Uso
@Cacheable(value = "llantasActivas", key = "#placa")
public List<LlantaActiva> findByVehiculo(String placa) {
    // ...
}

@CacheEvict(value = "llantasActivas", key = "#placa")
public void montarLlanta(String placa, LlantaActiva llanta) {
    // ...
}
```

### 8.2 Connection Pooling

```yaml
# application.yml
spring:
  datasource:
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
```

### 8.3 Paginación

```java
@GetMapping("/llantas/activas")
public Page<LlantaActivaResponse> getLlantasActivas(
    @PageableDefault(size = 50, sort = "fechaInstalacion", direction = Sort.Direction.DESC)
    Pageable pageable
) {
    return llantaService.findAll(pageable)
        .map(mapper::toResponse);
}
```

---

## 9. ESTRATEGIA DE MIGRACIÓN

### 9.1 Fases de Migración

**Fase 1: Preparación (2 semanas)**
- Setup de infraestructura (servidores, BD PostgreSQL)
- Migración de esquema de BD
- Configuración de entornos (dev, staging, prod)

**Fase 2: Desarrollo Core (8 semanas)**
- Sprint 1-2: Módulos de maestros y vehículos
- Sprint 3-4: Módulo de montaje/desmontaje
- Sprint 5-6: Módulo de muestreo
- Sprint 7-8: Reportes y dashboard

**Fase 3: Migración de Datos (1 semana)**
- Extracción de datos de Oracle
- Transformación y limpieza
- Carga en PostgreSQL
- Validación de integridad

**Fase 4: Pruebas (2 semanas)**
- Pruebas unitarias y de integración
- Pruebas de carga y performance
- Pruebas de aceptación de usuario

**Fase 5: Despliegue (1 semana)**
- Despliegue en staging
- Validación final
- Despliegue en producción
- Monitoreo post-despliegue

### 9.2 Estrategia de Migración de Datos

**Script de Migración:**
```sql
-- 1. Exportar desde Oracle
SELECT * FROM llantas.vehiculos_llantas
INTO OUTFILE '/tmp/vehiculos.csv'
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- 2. Importar a PostgreSQL
COPY vehiculos_llantas
FROM '/tmp/vehiculos.csv'
DELIMITER ','
CSV HEADER QUOTE '"';

-- 3. Validar integridad
SELECT 'Vehículos' AS tabla, 
       (SELECT COUNT(*) FROM vehiculos_llantas) AS total_postgres,
       12 AS total_oracle_esperado,
       CASE WHEN (SELECT COUNT(*) FROM vehiculos_llantas) = 12 
            THEN 'OK' ELSE 'ERROR' END AS status;
```

### 9.3 Rollback Plan

**En caso de problemas:**
1. Mantener sistema Oracle activo durante 2 meses
2. Replicación bidireccional temporal (Oracle ↔ PostgreSQL)
3. Switch back a Oracle si es necesario
4. Logs detallados de todas las transacciones

---

**FIN DEL DOCUMENTO**
