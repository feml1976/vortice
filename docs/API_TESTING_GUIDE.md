# 🧪 Guía de Testing de la API - Vórtice

**Fecha:** 2026-01-19
**Versión:** 1.0.0
**Base URL:** `http://localhost:8080/api`

---

## 📋 Tabla de Contenidos

1. [Configuración Inicial](#configuración-inicial)
2. [Autenticación](#autenticación)
   - [Health Check](#health-check)
   - [Login](#login)
   - [Registro](#registro)
   - [Refresh Token](#refresh-token)
   - [Logout](#logout)
3. [Ejemplos con cURL](#ejemplos-con-curl)
4. [Ejemplos con Postman](#ejemplos-con-postman)
5. [Rate Limiting](#rate-limiting)
6. [Códigos de Estado HTTP](#códigos-de-estado-http)
7. [Troubleshooting](#troubleshooting)

---

## 🚀 Configuración Inicial

### Requisitos Previos

1. **Backend corriendo:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Frontend corriendo (opcional):**
   ```bash
   cd frontend
   npm run dev
   ```

3. **Usuario administrador por defecto:**
   - **Username:** `admin`
   - **Email:** `admin@vortice.com`
   - **Password:** `Admin123!`

### Variables de Entorno

Exportar para facilitar las pruebas:

```bash
export BASE_URL="http://localhost:8080/api"
export ADMIN_USER="admin"
export ADMIN_PASS="Admin123!"
```

---

## 🔐 Autenticación

### Health Check

Verificar que el servicio esté activo.

**Endpoint:** `GET /auth/health`

**cURL:**
```bash
curl -X GET http://localhost:8080/api/auth/health
```

**Respuesta Esperada:**
```
Auth service is running
```

**Status Code:** `200 OK`

---

### Login

Iniciar sesión con credenciales.

**Endpoint:** `POST /auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "usernameOrEmail": "admin",
  "password": "Admin123!"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin123!"
  }'
```

**Respuesta Exitosa (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@vortice.com",
    "firstName": "Admin",
    "lastName": "Vortice",
    "isActive": true,
    "roles": ["ADMIN"]
  }
}
```

**Errores Comunes:**

- **401 Unauthorized** - Credenciales incorrectas
  ```json
  {
    "timestamp": 1705685947123,
    "status": 401,
    "error": "Unauthorized",
    "message": "Credenciales inválidas",
    "path": "/api/auth/login"
  }
  ```

- **423 Locked** - Usuario bloqueado por intentos fallidos
  ```json
  {
    "timestamp": 1705685947123,
    "status": 423,
    "error": "Locked",
    "message": "Usuario bloqueado por múltiples intentos fallidos",
    "path": "/api/auth/login"
  }
  ```

- **429 Too Many Requests** - Límite de peticiones excedido
  ```json
  {
    "timestamp": 1705685947123,
    "status": 429,
    "error": "Too Many Requests",
    "message": "Límite de peticiones excedido para intentos de login. Por favor, intente nuevamente en 45 segundos.",
    "path": "/api/auth/login",
    "retryAfterSeconds": 45,
    "limitType": "AUTH"
  }
  ```

**Headers de Rate Limiting:**
```
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 4
X-RateLimit-Type: AUTH
```

---

### Registro

Crear una nueva cuenta de usuario.

**Endpoint:** `POST /auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Respuesta Exitosa (201 Created):**
```json
{
  "id": 2,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "message": "Usuario registrado exitosamente"
}
```

**Validaciones:**

- **Username:** 3-50 caracteres, alfanumérico, guiones y guiones bajos permitidos
- **Email:** Formato válido de email
- **Password:** Mínimo 8 caracteres, debe incluir:
  - Al menos una mayúscula
  - Al menos una minúscula
  - Al menos un número
  - Al menos un carácter especial (@$!%*?&)
- **FirstName/LastName:** 1-100 caracteres

**Errores Comunes:**

- **400 Bad Request** - Validación fallida
  ```json
  {
    "timestamp": 1705685947123,
    "status": 400,
    "error": "Bad Request",
    "message": "El username debe tener entre 3 y 50 caracteres",
    "path": "/api/auth/register"
  }
  ```

- **409 Conflict** - Usuario o email ya existe
  ```json
  {
    "timestamp": 1705685947123,
    "status": 409,
    "error": "Conflict",
    "message": "El username ya está en uso",
    "path": "/api/auth/register"
  }
  ```

**Rate Limiting:** 3 registros por hora por IP

---

### Refresh Token

Renovar el access token usando el refresh token.

**Endpoint:** `POST /auth/refresh`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**cURL:**
```bash
# Guardar el refresh token en una variable
REFRESH_TOKEN="a1b2c3d4-e5f6-7890-abcd-ef1234567890"

curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

**Respuesta Exitosa (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "b2c3d4e5-f6g7-8901-bcde-fg2345678901",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

**Notas:**
- El refresh token se rota (se genera uno nuevo)
- El refresh token anterior queda revocado
- El nuevo access token tiene una nueva fecha de expiración

**Errores Comunes:**

- **401 Unauthorized** - Refresh token inválido o expirado
  ```json
  {
    "timestamp": 1705685947123,
    "status": 401,
    "error": "Unauthorized",
    "message": "Refresh token inválido o expirado",
    "path": "/api/auth/refresh"
  }
  ```

---

### Logout

Cerrar sesión y revocar el refresh token.

**Endpoint:** `POST /auth/logout`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {accessToken}
```

**Body:**
```json
{
  "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**cURL:**
```bash
ACCESS_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
REFRESH_TOKEN="a1b2c3d4-e5f6-7890-abcd-ef1234567890"

curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

**Respuesta Exitosa (200 OK):**
```json
{
  "message": "Sesión cerrada exitosamente"
}
```

**Notas:**
- El refresh token se revoca en la base de datos
- El access token sigue siendo válido hasta su expiración (limitación de JWT stateless)
- El frontend debe eliminar los tokens del localStorage

---

## 🔄 Ejemplos con cURL

### Flujo Completo de Autenticación

```bash
#!/bin/bash

# 1. Health Check
echo "=== Health Check ==="
curl -X GET http://localhost:8080/api/auth/health
echo -e "\n"

# 2. Login
echo "=== Login ==="
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin123!"
  }')

echo $LOGIN_RESPONSE | jq .
echo -e "\n"

# 3. Extraer tokens
ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken')
REFRESH_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.refreshToken')

echo "Access Token: ${ACCESS_TOKEN:0:50}..."
echo "Refresh Token: $REFRESH_TOKEN"
echo -e "\n"

# 4. Usar endpoint protegido (ejemplo)
echo "=== Accediendo a recurso protegido ==="
curl -X GET http://localhost:8080/api/some-protected-endpoint \
  -H "Authorization: Bearer $ACCESS_TOKEN"
echo -e "\n"

# 5. Refresh Token
echo "=== Refresh Token ==="
REFRESH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")

echo $REFRESH_RESPONSE | jq .
echo -e "\n"

# 6. Actualizar tokens
ACCESS_TOKEN=$(echo $REFRESH_RESPONSE | jq -r '.accessToken')
REFRESH_TOKEN=$(echo $REFRESH_RESPONSE | jq -r '.refreshToken')

# 7. Logout
echo "=== Logout ==="
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
echo -e "\n"
```

### Probar Rate Limiting

```bash
# Hacer 6 peticiones rápidas para probar el límite de login (5 por minuto)
for i in {1..6}; do
  echo "Intento $i:"
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "usernameOrEmail": "admin",
      "password": "WrongPassword123!"
    }' \
    -w "\nHTTP Status: %{http_code}\n" \
    -s | jq .
  echo "---"
  sleep 1
done
```

### Verificar Headers de Rate Limiting

```bash
curl -v -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin123!"
  }' \
  2>&1 | grep -i "x-ratelimit"
```

---

## 📮 Ejemplos con Postman

### Importar Colección

La colección de Postman ya está incluida en el repositorio:

```
Vortice_Auth_API.postman_collection.json
Vortice_Auth_Environment.postman_environment.json
```

### Configurar Entorno

1. Importar ambos archivos en Postman
2. Seleccionar el entorno "Vórtice - Development" en el dropdown
3. Los tokens se guardan automáticamente en variables de entorno

### Variables de Entorno

```json
{
  "base_url": "http://localhost:8080/api",
  "access_token": "{{accessToken}}",
  "refresh_token": "{{refreshToken}}"
}
```

### Scripts Pre-request y Tests

La colección incluye scripts automáticos para:

- Guardar tokens en variables de entorno
- Agregar Authorization header automáticamente
- Validar respuestas
- Extraer datos de respuestas

**Ejemplo de Test Script:**
```javascript
// Guardar tokens en variables de entorno
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.accessToken);
    pm.environment.set("refresh_token", jsonData.refreshToken);

    // Tests
    pm.test("Status code is 200", function () {
        pm.response.to.have.status(200);
    });

    pm.test("Access token is present", function () {
        pm.expect(jsonData.accessToken).to.not.be.undefined;
    });
}
```

---

## 🚦 Rate Limiting

### Límites Configurados

| Endpoint | Límite | Periodo | Tipo |
|----------|--------|---------|------|
| **Global** | 100 requests | 1 minuto | Por IP |
| `/auth/login` | 5 requests | 1 minuto | Por IP |
| `/auth/register` | 3 requests | 1 hora | Por IP |

### Headers de Rate Limiting

Todas las respuestas incluyen headers informativos:

```
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 4
X-RateLimit-Type: AUTH
```

En caso de exceder el límite (429):

```
X-RateLimit-Limit: 0
X-RateLimit-Remaining: 0
X-RateLimit-Type: AUTH
Retry-After: 45
```

### Testear Rate Limiting

```bash
# Script para probar límite de login
for i in {1..6}; do
  echo "Request $i"
  response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"usernameOrEmail":"admin","password":"test"}')

  status=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  echo "Status: $status"

  if [ "$status" == "429" ]; then
    retry_after=$(echo "$body" | jq -r '.retryAfterSeconds')
    echo "Rate limit exceeded! Retry after: ${retry_after}s"
  fi

  echo "---"
done
```

---

## 📊 Códigos de Estado HTTP

### Respuestas Exitosas

- **200 OK** - Solicitud exitosa
- **201 Created** - Recurso creado exitosamente (registro)

### Errores del Cliente (4xx)

- **400 Bad Request** - Validación fallida, datos incorrectos
- **401 Unauthorized** - No autenticado o credenciales inválidas
- **403 Forbidden** - No tiene permisos para el recurso
- **404 Not Found** - Recurso no encontrado
- **409 Conflict** - Conflicto (usuario/email ya existe)
- **423 Locked** - Usuario bloqueado por intentos fallidos
- **429 Too Many Requests** - Límite de rate limiting excedido

### Errores del Servidor (5xx)

- **500 Internal Server Error** - Error interno del servidor

### Formato de Error Estándar

```json
{
  "timestamp": 1705685947123,
  "status": 400,
  "error": "Bad Request",
  "message": "Mensaje descriptivo del error",
  "path": "/api/auth/login",
  "details": {
    "field": "username",
    "rejectedValue": "ab",
    "message": "El username debe tener al menos 3 caracteres"
  }
}
```

---

## 🔍 Troubleshooting

### Error 404 - Not Found

**Problema:** URL incorrecta, falta el context path `/api`

**Solución:**
```bash
# ❌ Incorrecto
curl http://localhost:8080/auth/login

# ✅ Correcto
curl http://localhost:8080/api/auth/login
```

### Error: Connection Refused

**Causas:**
1. Backend no está corriendo
2. Puerto 8080 ocupado

**Soluciones:**
```bash
# Verificar si el backend está corriendo
curl http://localhost:8080/api/auth/health

# Verificar procesos en puerto 8080 (Windows)
netstat -ano | findstr :8080

# Iniciar el backend
cd backend
mvn spring-boot:run
```

### Error 401 - Credenciales Inválidas

**Verificar:**
1. Username/Password correctos (case-sensitive)
2. Usuario no está bloqueado
3. Usuario está activo

**Resetear usuario bloqueado:**
```sql
UPDATE users
SET is_locked = false, failed_login_attempts = 0
WHERE username = 'admin';
```

### Error 429 - Rate Limit Exceeded

**Solución:** Esperar el tiempo indicado en `retryAfterSeconds`

```bash
# Ver tiempo de espera
curl -s http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"test"}' \
  | jq '.retryAfterSeconds'
```

### Token Expirado

**Síntomas:**
- Error 401 en endpoints protegidos
- Mensaje: "Token JWT expirado"

**Solución:**
```bash
# Usar el refresh token para obtener un nuevo access token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

### Verificar Base de Datos

```sql
-- Ver usuarios activos
SELECT id, username, email, is_active, is_locked, failed_login_attempts
FROM users;

-- Ver tokens de refresh activos
SELECT id, token, user_id, expires_at, revoked
FROM refresh_tokens
WHERE revoked = false AND expires_at > NOW();

-- Ver intentos fallidos por usuario
SELECT username, failed_login_attempts, is_locked, last_login_at
FROM users
WHERE failed_login_attempts > 0;
```

---

## 📚 Referencias

- [QUICK_START.md](../QUICK_START.md) - Guía de inicio rápido
- [RATE_LIMITING.md](../RATE_LIMITING.md) - Documentación de rate limiting
- [POSTMAN_README.md](../POSTMAN_README.md) - Guía de Postman
- Swagger UI: http://localhost:8080/api/swagger-ui.html

---

## 🆘 Soporte

Si tienes problemas:

1. Revisa los logs del backend
2. Verifica que PostgreSQL está corriendo
3. Consulta la sección de [Troubleshooting](#troubleshooting)
4. Revisa la documentación de Swagger UI

---

**Última actualización:** 2026-01-19
**Versión:** 1.0.0
**Autor:** Vórtice Development Team
