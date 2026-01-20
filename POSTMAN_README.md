# Colección de Postman - Vórtice Auth API

## 📋 Descripción

Esta colección de Postman contiene todos los endpoints de autenticación del sistema Vórtice, incluyendo:

- ✅ Registro de nuevos usuarios
- ✅ Login (por username o email)
- ✅ Renovación de tokens (Refresh Token Rotation)
- ✅ Logout
- ✅ Health Check
- ✅ Ejemplos de casos de error

## 🚀 Instalación

### Opción 1: Importar archivos

1. Abre Postman
2. Click en "Import" en la esquina superior izquierda
3. Arrastra y suelta los siguientes archivos:
   - `Vortice_Auth_API.postman_collection.json`
   - `Vortice_Auth_Environment.postman_environment.json`
4. Selecciona el entorno "Vórtice - Development" en el dropdown superior derecho

### Opción 2: Importar desde URL

1. Abre Postman
2. Click en "Import"
3. Pega la URL del archivo JSON (si está en un repositorio)

## ⚙️ Configuración

### Variables de Entorno

El archivo de entorno incluye las siguientes variables:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `base_url` | URL base de la API | `http://localhost:8080/api` |
| `access_token` | Token JWT de acceso | (auto-generado) |
| `refresh_token` | Token de refresco | (auto-generado) |
| `user_id` | ID del usuario autenticado | (auto-generado) |
| `username` | Nombre de usuario | (auto-generado) |

**Nota:** Los tokens se guardan automáticamente después de hacer login o register.

## 📖 Uso

### Flujo Básico

1. **Health Check** (opcional)
   - Verifica que el servidor está corriendo
   - No requiere autenticación

2. **Login con usuario por defecto**
   ```json
   {
     "usernameOrEmail": "admin",
     "password": "Admin123!"
   }
   ```
   - Los tokens se guardarán automáticamente en las variables de entorno
   - Verás mensajes de confirmación en la consola de Postman

3. **Usar endpoints protegidos**
   - Los tokens se incluyen automáticamente en las peticiones
   - Usa `{{access_token}}` y `{{refresh_token}}` en tus requests

4. **Refresh Token** (cuando el access token expire)
   - Usa el endpoint "Refresh Token"
   - Recibirás nuevos tokens (el anterior será revocado)
   - Los nuevos tokens se guardan automáticamente

5. **Logout** (cuando termines)
   - Los tokens serán revocados en el servidor
   - Las variables de entorno se limpiarán automáticamente

### Crear un Nuevo Usuario

1. Ejecuta el endpoint **Register** con estos datos de ejemplo:
   ```json
   {
     "username": "testuser",
     "email": "testuser@example.com",
     "password": "Test123!@#",
     "firstName": "Test",
     "lastName": "User"
   }
   ```

2. Los tokens se guardarán automáticamente después del registro exitoso

### Login Alternativo (por Email)

Puedes usar el endpoint "Login (by email)" para autenticarte usando tu correo electrónico en lugar del username:
```json
{
  "usernameOrEmail": "admin@vortice.com",
  "password": "Admin123!"
}
```

## 🧪 Tests Automatizados

Cada endpoint incluye tests automáticos que verifican:

- ✅ Código de estado HTTP correcto
- ✅ Estructura de la respuesta
- ✅ Presencia de tokens
- ✅ Validación de datos

Los resultados de los tests aparecen en la pestaña "Test Results" de Postman.

## 📊 Scripts Incluidos

### Pre-request Scripts

Los scripts de pre-request se ejecutan **antes** de enviar la petición:
- Configuración de headers
- Preparación de datos dinámicos

### Test Scripts

Los scripts de test se ejecutan **después** de recibir la respuesta:
- Guardar tokens en variables de entorno
- Validar respuestas
- Logging en consola
- Cleanup después de logout

## 🔐 Seguridad

### Refresh Token Rotation

El sistema implementa **refresh token rotation**, una práctica de seguridad que:

1. Cada vez que renuevas el access token, recibes un NUEVO refresh token
2. El refresh token anterior es revocado inmediatamente
3. Esto previene ataques de reuso de tokens

**Importante:** Guarda siempre el nuevo refresh token después de una renovación.

### Manejo de Intentos Fallidos

- El sistema bloquea la cuenta después de **5 intentos fallidos** de login
- Los intentos se resetean después de un login exitoso
- Una cuenta bloqueada requiere intervención manual para desbloquearse

## 🐛 Casos de Error

La carpeta "Examples - Error Cases" contiene ejemplos de peticiones que fallan:

1. **Login - Invalid Credentials**
   - Demuestra el manejo de credenciales incorrectas
   - Incrementa el contador de intentos fallidos

2. **Register - Duplicate Username**
   - Demuestra validación de username único
   - Retorna error 400/409

3. **Refresh - Invalid Token**
   - Demuestra manejo de tokens inválidos
   - Retorna error 400/404

## 📝 Logs

Para ver los logs detallados:

1. Abre la **Consola de Postman** (View > Show Postman Console)
2. Ejecuta cualquier endpoint
3. Verás mensajes como:
   ```
   ✅ Login exitoso
   Access Token guardado: eyJhbGciOiJIUzI1NiIs...
   Usuario: admin
   ```

## 🔄 Variables en Uso

Puedes usar las variables de entorno en cualquier parte de tus requests:

```
URL: {{base_url}}/auth/login
Header: Authorization: Bearer {{access_token}}
Body: {"refreshToken": "{{refresh_token}}"}
```

**Nota:** La variable `base_url` ya incluye el context path `/api`, por lo que no necesitas agregarlo manualmente.

## 🛠️ Troubleshooting

### Error: "Could not get any response"

- Verifica que el backend está corriendo en `http://localhost:8080`
- Verifica que no hay problemas de firewall
- Prueba el endpoint Health Check primero (`http://localhost:8080/api/auth/health`)

### Error: "Refresh token no encontrado"

- El token fue revocado o expiró
- Haz login nuevamente para obtener nuevos tokens

### Error: "Usuario ya existe"

- El username o email ya están registrados
- Usa credenciales diferentes

### Los tokens no se guardan automáticamente

- Verifica que el entorno "Vórtice - Development" está seleccionado
- Revisa la consola de Postman para ver si hay errores en los scripts

## 📚 Recursos Adicionales

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api/v3/api-docs
- **Health Check**: http://localhost:8080/api/auth/health
- **Actuator Health**: http://localhost:8080/api/actuator/health

## 📞 Soporte

Para reportar problemas o sugerencias:
- Crea un issue en el repositorio del proyecto
- Contacta al equipo de desarrollo

---

**Última actualización:** 2026-01-19
**Versión de la API:** 1.0.0
**Autor:** Vórtice Development Team
