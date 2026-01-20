# 🚀 Quick Start - Vórtice Auth API

## ✅ Verificar que el Backend está Corriendo

### 1. Health Check
Abre tu navegador o usa curl para verificar que el servicio está activo:

```bash
curl http://localhost:8080/api/auth/health
```

Deberías ver:
```
Auth service is running
```

### 2. Verificar Swagger UI
Abre en tu navegador:
```
http://localhost:8080/api/swagger-ui.html
```

## 🔐 Probar Autenticación

### Usuario por Defecto

El sistema viene con un usuario administrador preconfigurado:

- **Username:** `admin`
- **Password:** `Admin123!`
- **Email:** `admin@vortice.com`

### Opción 1: Usar Postman (Recomendado)

1. **Importar la colección:**
   - Abre Postman
   - Click en "Import"
   - Arrastra `Vortice_Auth_API.postman_collection.json`
   - Arrastra `Vortice_Auth_Environment.postman_environment.json`

2. **Seleccionar el entorno:**
   - En el dropdown superior derecho, selecciona "Vórtice - Development"

3. **Hacer Login:**
   - Abre la carpeta "Auth"
   - Ejecuta el request "Login"
   - Los tokens se guardarán automáticamente

### Opción 2: Usar curl

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin123!"
  }'
```

### Opción 3: Usar Swagger UI

1. Ve a http://localhost:8080/api/swagger-ui.html
2. Busca el endpoint `POST /auth/login`
3. Click en "Try it out"
4. Ingresa:
   ```json
   {
     "usernameOrEmail": "admin",
     "password": "Admin123!"
   }
   ```
5. Click en "Execute"

## 📝 Respuesta Esperada

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

## ⚠️ Problemas Comunes

### Error 404 - Not Found

**Problema:** La URL no incluye el context path `/api`

❌ **Incorrecto:**
```
http://localhost:8080/auth/login
```

✅ **Correcto:**
```
http://localhost:8080/api/auth/login
```

### Error: Connection Refused

**Causas posibles:**
1. El backend no está corriendo
   - Verifica: `./mvnw spring-boot:run` (o `mvn spring-boot:run` en Windows)
2. El puerto 8080 está ocupado por otra aplicación
   - Verifica: `netstat -ano | findstr :8080` (Windows)
   - Solución: Mata el proceso o cambia el puerto en `application.yml`

### Error: Invalid Credentials

**Causas posibles:**
1. Password incorrecto
   - Asegúrate de usar: `Admin123!` (case-sensitive)
2. Usuario no existe
   - Verifica que Flyway ejecutó las migraciones correctamente
   - Revisa los logs: Debe aparecer "Flyway migration V1.0.0"

### Error: User Account is Locked

**Causa:** Demasiados intentos fallidos de login (5 intentos)

**Solución:** Resetear manualmente en la base de datos:
```sql
UPDATE users
SET is_locked = false, failed_login_attempts = 0
WHERE username = 'admin';
```

## 🔄 Flujo Completo de Autenticación

### 1. Login
```bash
# Guardar el access token en una variable
ACCESS_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin123!"}' \
  | jq -r '.accessToken')

# Guardar el refresh token
REFRESH_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin123!"}' \
  | jq -r '.refreshToken')
```

### 2. Usar el Access Token (Ejemplo con endpoint protegido)
```bash
curl -X GET http://localhost:8080/api/some-protected-endpoint \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 3. Renovar Token (cuando expire)
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

### 4. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

## 📊 Verificar Base de Datos

### Ver usuarios en la base de datos
```sql
SELECT id, username, email, is_active, is_locked, failed_login_attempts
FROM users;
```

### Ver tokens activos
```sql
SELECT id, token, expires_at, revoked
FROM refresh_tokens
WHERE revoked = false
  AND expires_at > NOW();
```

## 🎯 Próximos Pasos

1. ✅ Probar todos los endpoints con Postman
2. ✅ Crear nuevos usuarios con `/auth/register`
3. ✅ Verificar la rotación de refresh tokens
4. ✅ Probar el logout y verificar que el token se revoca
5. ✅ Intentar 5 logins fallidos para probar el bloqueo de cuenta

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs del backend
2. Verifica que PostgreSQL está corriendo
3. Consulta `POSTMAN_README.md` para más detalles
4. Revisa Swagger UI para documentación de la API

---

**Última actualización:** 2026-01-19
**Versión:** 1.0.0
