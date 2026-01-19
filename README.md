# 🔧 Vórtice - Sistema de Gestión de Taller

Sistema de Gestión de Taller Modernizado para TRANSER.
Modernización de Oracle Forms 6i / Oracle 11g a stack web moderno.

## 📋 Tabla de Contenidos

- [Stack Tecnológico](#-stack-tecnológico)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración Inicial](#-configuración-inicial)
- [Desarrollo Local](#-desarrollo-local)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Convenciones de Código](#-convenciones-de-código)
- [Scripts Disponibles](#-scripts-disponibles)
- [Documentación](#-documentación)

---

## 🛠 Stack Tecnológico

### Backend
- **Java:** 21 (LTS)
- **Framework:** Spring Boot 3.5.0
- **Base de Datos:** PostgreSQL 18
- **ORM:** JPA/Hibernate
- **Seguridad:** Spring Security + JWT
- **Migración BD:** Flyway
- **Build Tool:** Maven 3.9+
- **Documentación API:** SpringDoc OpenAPI 3 (Swagger)

### Frontend
- **Framework:** React 18.3
- **Lenguaje:** TypeScript 5.7
- **Build Tool:** Vite 6
- **UI Library:** Material-UI (MUI) 6
- **Estado Global:** Zustand 5
- **Estado del Servidor:** TanStack Query (React Query) 5
- **Formularios:** React Hook Form + Zod
- **HTTP Client:** Axios
- **Notificaciones:** React Hot Toast

### DevOps & Herramientas
- **Contenedores:** Docker + Docker Compose
- **Control de Versiones:** Git
- **CI/CD:** (Por configurar)

---

## 📦 Requisitos Previos

Asegúrate de tener instalado:

- **Java JDK 21:** [Descargar Aquí](https://www.oracle.com/java/technologies/downloads/#java21)
- **Node.js 20+:** [Descargar Aquí](https://nodejs.org/)
- **PostgreSQL 18+:** [Descargar Aquí](https://www.postgresql.org/download/) *(Opcional si usas Docker)*
- **Docker Desktop:** [Descargar Aquí](https://www.docker.com/products/docker-desktop) *(Recomendado)*
- **Git:** [Descargar Aquí](https://git-scm.com/downloads)
- **Maven 3.9+:** [Descargar Aquí](https://maven.apache.org/download.cgi) *(Opcional, el proyecto incluye Maven Wrapper)*

### Verificar Instalación

```bash
java -version      # Debe mostrar Java 21
node -version      # Debe mostrar v20+
npm -version       # Debe mostrar v10+
docker --version   # Docker version 20+
git --version      # Git version 2+
```

---

## ⚙️ Configuración Inicial

### 1. Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd vortice
```

### 2. Configurar Base de Datos

#### Opción A: Usar Docker (Recomendado)

```bash
# Levantar solo PostgreSQL
docker-compose up -d postgres

# Verificar que está corriendo
docker-compose ps
```

La base de datos estará disponible en:
- **Host:** localhost
- **Puerto:** 5432
- **Database:** vortice_dev
- **Usuario:** vortice_user
- **Password:** vortice_pass

#### Opción B: PostgreSQL Local

Si tienes PostgreSQL instalado localmente, crea la base de datos:

```sql
CREATE DATABASE vortice_dev;
CREATE USER vortice_user WITH ENCRYPTED PASSWORD 'vortice_pass';
GRANT ALL PRIVILEGES ON DATABASE vortice_dev TO vortice_user;
```

Luego actualiza `backend/src/main/resources/application-dev.yml` con tu configuración.

### 3. Configurar Backend

```bash
cd backend

# Instalar dependencias y compilar (Maven Wrapper incluido)
./mvnw clean install

# O si tienes Maven instalado globalmente
mvn clean install
```

### 4. Configurar Frontend

```bash
cd frontend

# Instalar dependencias
npm install
```

---

## 🚀 Desarrollo Local

### Opción 1: Ejecutar Backend y Frontend por Separado (Recomendado para desarrollo)

#### Terminal 1: Backend

```bash
cd backend
./mvnw spring-boot:run

# O en Windows
mvnw.cmd spring-boot:run
```

El backend estará disponible en: **http://localhost:8080/api**

- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **API Docs:** http://localhost:8080/api/v3/api-docs
- **Health Check:** http://localhost:8080/api/actuator/health

#### Terminal 2: Frontend

```bash
cd frontend
npm run dev
```

El frontend estará disponible en: **http://localhost:5173**

### Opción 2: Ejecutar Todo con Docker Compose

```bash
# Desde la raíz del proyecto
docker-compose --profile full up --build

# En modo detached (segundo plano)
docker-compose --profile full up -d --build
```

Esto levantará:
- PostgreSQL en puerto 5432
- Backend en puerto 8080
- Frontend en puerto 5173

### Opción 3: Con Herramientas Adicionales

```bash
# Levantar con PgAdmin y MailHog
docker-compose --profile full --profile tools up -d
```

Herramientas disponibles:
- **PgAdmin:** http://localhost:5050
  - Email: admin@vortice.local
  - Password: admin
- **MailHog:** http://localhost:8025 (captura emails de desarrollo)

---

## 📁 Estructura del Proyecto

```
vortice/
├── backend/                    # Aplicación Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/transer/vortice/
│   │   │   │   ├── shared/            # Código compartido
│   │   │   │   ├── workshop/          # Módulo Taller
│   │   │   │   ├── inventory/         # Módulo Inventarios
│   │   │   │   ├── purchasing/        # Módulo Compras
│   │   │   │   └── ...
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/      # Migraciones Flyway
│   │   └── test/
│   └── pom.xml
│
├── frontend/                   # Aplicación React
│   ├── src/
│   │   ├── features/          # Módulos por funcionalidad
│   │   │   ├── auth/
│   │   │   ├── workshop/
│   │   │   ├── inventory/
│   │   │   └── ...
│   │   ├── shared/            # Código compartido
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   ├── services/
│   │   │   └── types/
│   │   ├── App.tsx
│   │   └── main.tsx
│   └── package.json
│
├── database/                  # Scripts SQL
│   ├── migrations/
│   └── seeds/
│
├── docker/                    # Dockerfiles
├── docs/                      # Documentación
├── docker-compose.yml
└── README.md
```

---

## 📝 Convenciones de Código

### Backend (Java)

- **Nomenclatura:**
  - Clases: `PascalCase`
  - Métodos/Variables: `camelCase`
  - Constantes: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

- **Arquitectura:**
  - **Domain:** Entidades, Value Objects, Events, Repository interfaces
  - **Application:** Use Cases, Services (lógica de negocio)
  - **Infrastructure:** Implementaciones técnicas (JPA, APIs externas)
  - **Presentation:** Controllers, DTOs, Mappers

- **Principios:**
  - Clean Architecture / Hexagonal
  - SOLID
  - DRY (Don't Repeat Yourself)
  - YAGNI (You Aren't Gonna Need It)

### Frontend (TypeScript/React)

- **Nomenclatura:**
  - Componentes: `PascalCase.tsx`
  - Hooks: `useCamelCase.ts`
  - Archivos: `kebab-case.ts`

- **Estructura:**
  - Function components (NO class components)
  - Custom hooks para lógica reutilizable
  - React Query para estado del servidor
  - Zustand para estado global de UI

### Base de Datos (PostgreSQL)

- **Nomenclatura:**
  - Tablas: `plural`, `snake_case` (ej: `work_orders`)
  - Columnas: `singular`, `snake_case` (ej: `created_at`)
  - Primary keys: `id` (UUID o BIGSERIAL)
  - Foreign keys: `[tabla_singular]_id` (ej: `vehicle_id`)
  - Timestamps: `created_at`, `updated_at`, `deleted_at`
  - Booleans: `is_[adjetivo]` (ej: `is_active`)

---

## 📜 Scripts Disponibles

### Backend

```bash
# Compilar proyecto
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Compilar y empaquetar
./mvnw clean package

# Ejecutar aplicación
./mvnw spring-boot:run

# Ejecutar con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Flyway: Migrar BD
./mvnw flyway:migrate

# Flyway: Ver estado de migraciones
./mvnw flyway:info
```

### Frontend

```bash
# Desarrollo con hot-reload
npm run dev

# Build de producción
npm run build

# Preview del build
npm run preview

# Linting
npm run lint

# Linting con auto-fix
npm run lint:fix

# Format con Prettier
npm run format

# Type checking
npm run type-check
```

### Docker

```bash
# Solo PostgreSQL
docker-compose up -d postgres

# Stack completo
docker-compose --profile full up -d

# Con herramientas adicionales
docker-compose --profile full --profile tools up -d

# Ver logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Detener todo
docker-compose down

# Detener y eliminar volúmenes (¡CUIDADO! Elimina la BD)
docker-compose down -v

# Reconstruir imágenes
docker-compose --profile full up --build
```

---

## 📚 Documentación

### Documentación del Proyecto

- **Requerimientos Funcionales:** `docs/01_Requerimientos_Funcionales_Completo.md`
- **Requerimientos Sistema Modernizado:** `docs/02_Requerimientos_Funcionales_Sistema_Modernizado.md`
- **Sugerencias Técnicas:** `docs/02_Sugerencias_y_Recomendaciones_Tecnicas.md`
- **Prompt Master IA:** `docs/03_Prompt_Master_Desarrollo_IA.md`
- **Roadmap:** `docs/04_Plan_de_Etapas_y_Roadmap.md`
- **Contexto del Proyecto:** `CLAUDE.md`

### API Documentation

Una vez el backend esté corriendo, accede a:

- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api/v3/api-docs

---

## 🧪 Testing

### Backend

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests con cobertura
./mvnw test jacoco:report

# Ejecutar solo tests unitarios
./mvnw test -Dtest=*Test

# Ejecutar solo tests de integración
./mvnw test -Dtest=*IT
```

### Frontend

```bash
# Tests (Por configurar)
npm run test
```

---

## 🤝 Contribución

1. Crear una rama desde `main`:
   ```bash
   git checkout -b feature/nombre-feature
   ```

2. Hacer commits descriptivos:
   ```bash
   git commit -m "feat: agregar listado de órdenes de trabajo"
   ```

3. Push a la rama:
   ```bash
   git push origin feature/nombre-feature
   ```

4. Crear Pull Request en GitHub/GitLab

---

## 📧 Contacto

**Equipo de Desarrollo Vórtice**
Empresa: TRANSER
Proyecto: Modernización Sistema de Gestión de Taller

---

## 📄 Licencia

Copyright © 2026 TRANSER. Todos los derechos reservados.

---

**¡Feliz Desarrollo! 🚀**
