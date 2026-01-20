# Configuración de Envío de Emails

Este documento describe cómo configurar el sistema de envío de emails en Vórtice para recuperación de contraseñas, bienvenida y notificaciones.

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Configuración Básica](#configuración-básica)
- [Proveedores de Email](#proveedores-de-email)
- [Variables de Entorno](#variables-de-entorno)
- [Templates de Email](#templates-de-email)
- [Modo Desarrollo vs Producción](#modo-desarrollo-vs-producción)
- [Solución de Problemas](#solución-de-problemas)

## Descripción General

El sistema de emails de Vórtice utiliza:
- **Spring Boot Mail**: Para envío SMTP
- **Thymeleaf**: Para templates HTML profesionales
- **Async Processing**: Envío asíncrono para no bloquear operaciones

### Funcionalidades de Email

1. **Bienvenida**: Email enviado al registrar un nuevo usuario
2. **Recuperación de Contraseña**: Email con token de reset
3. **Confirmación de Cambio**: Email al cambiar contraseña exitosamente

## Configuración Básica

### application.yml

La configuración de email se encuentra en `backend/src/main/resources/application.yml`:

```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
        debug: false

vortice:
  app:
    name: ${APP_NAME:Vórtice}
    url: ${APP_URL:http://localhost:5173}
    support-email: ${SUPPORT_EMAIL:soporte@vortice.transer.com}
```

## Proveedores de Email

### Gmail

Para usar Gmail como proveedor SMTP:

1. **Habilitar verificación en dos pasos** en tu cuenta de Google
2. **Generar una contraseña de aplicación**:
   - Ve a [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
   - Genera una nueva contraseña de aplicación
   - Usa esta contraseña (no tu contraseña de Gmail)

**Variables de entorno:**

```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-contraseña-de-aplicacion
```

### Outlook/Office 365

Para usar Outlook como proveedor SMTP:

**Variables de entorno:**

```bash
MAIL_HOST=smtp-mail.outlook.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@outlook.com
MAIL_PASSWORD=tu-contraseña
```

### Amazon SES

Para usar Amazon SES:

**Variables de entorno:**

```bash
MAIL_HOST=email-smtp.us-east-1.amazonaws.com
MAIL_PORT=587
MAIL_USERNAME=tu-aws-smtp-username
MAIL_PASSWORD=tu-aws-smtp-password
```

### SendGrid

Para usar SendGrid:

**Variables de entorno:**

```bash
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=tu-sendgrid-api-key
```

### Servidor SMTP Personalizado

```bash
MAIL_HOST=smtp.tu-dominio.com
MAIL_PORT=587
MAIL_USERNAME=tu-usuario
MAIL_PASSWORD=tu-contraseña
```

## Variables de Entorno

### Variables Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `MAIL_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Usuario SMTP | `tu-email@gmail.com` |
| `MAIL_PASSWORD` | Contraseña SMTP | `tu-contraseña` |

### Variables Opcionales

| Variable | Descripción | Default |
|----------|-------------|---------|
| `APP_NAME` | Nombre de la aplicación | `Vórtice` |
| `APP_URL` | URL del frontend | `http://localhost:5173` |
| `SUPPORT_EMAIL` | Email de soporte | `soporte@vortice.transer.com` |

### Configuración en Desarrollo

Crear archivo `.env` en el directorio raíz del backend:

```bash
# SMTP Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-contraseña-de-aplicacion

# Application Configuration
APP_NAME=Vórtice
APP_URL=http://localhost:5173
SUPPORT_EMAIL=soporte@vortice.transer.com
```

### Configuración en Producción

Usar variables de entorno del sistema o del contenedor Docker:

**Docker Compose:**

```yaml
services:
  backend:
    environment:
      - MAIL_HOST=smtp.gmail.com
      - MAIL_PORT=587
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - APP_NAME=Vórtice
      - APP_URL=https://vortice.transer.com
      - SUPPORT_EMAIL=soporte@vortice.transer.com
```

**Kubernetes:**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: vortice-email-secret
type: Opaque
stringData:
  MAIL_HOST: smtp.gmail.com
  MAIL_PORT: "587"
  MAIL_USERNAME: tu-email@gmail.com
  MAIL_PASSWORD: tu-contraseña-de-aplicacion
```

## Templates de Email

Los templates HTML se encuentran en `backend/src/main/resources/templates/email/`:

### Estructura de Templates

```
templates/
  email/
    ├── welcome.html              # Email de bienvenida
    ├── password-reset.html       # Email de recuperación
    └── password-changed.html     # Email de confirmación
```

### Variables Disponibles en Templates

Todas las templates tienen acceso a estas variables:

| Variable | Descripción |
|----------|-------------|
| `appName` | Nombre de la aplicación |
| `appUrl` | URL del frontend |
| `supportEmail` | Email de soporte |
| `currentYear` | Año actual |

### Personalizar Templates

Para personalizar un template:

1. Editar el archivo HTML en `templates/email/`
2. Usar sintaxis Thymeleaf para variables: `th:text="${variableName}"`
3. Mantener estructura responsive del template
4. Probar cambios reiniciando la aplicación

**Ejemplo:**

```html
<h1 th:text="${appName}">Vórtice</h1>
<p>Hola <span th:text="${userName}">Usuario</span>,</p>
<a th:href="${resetUrl}">Resetear Contraseña</a>
```

## Modo Desarrollo vs Producción

### Modo Desarrollo

En modo desarrollo (`spring.profiles.active=dev`):

- El **token de reset** se retorna en la respuesta HTTP para facilitar testing
- Los emails se envían normalmente
- Los errores de email NO fallan las operaciones

**Respuesta en desarrollo:**

```json
{
  "message": "Email de recuperación enviado. Token (solo desarrollo): abc-123-def-456"
}
```

### Modo Producción

En modo producción (`spring.profiles.active=prod`):

- El **token de reset** NO se retorna en la respuesta (solo se envía por email)
- Respuesta genérica por seguridad
- Los errores de email fallan la operación de reset

**Respuesta en producción:**

```json
{
  "message": "Si el email está registrado, recibirás un enlace de recuperación."
}
```

## Solución de Problemas

### Error: "Authentication failed"

**Causa:** Credenciales SMTP incorrectas

**Solución:**
1. Verificar `MAIL_USERNAME` y `MAIL_PASSWORD`
2. Para Gmail, usar contraseña de aplicación, no contraseña normal
3. Verificar que la cuenta no tenga restricciones de seguridad

### Error: "Connection timed out"

**Causa:** Problemas de red o firewall

**Solución:**
1. Verificar que el puerto SMTP (587 o 465) no esté bloqueado
2. Probar con diferentes puertos (587 para STARTTLS, 465 para SSL)
3. Verificar configuración del firewall

### Error: "Could not send email"

**Causa:** Template Thymeleaf no encontrado o error en template

**Solución:**
1. Verificar que el archivo HTML existe en `templates/email/`
2. Revisar logs para detalles del error
3. Validar sintaxis Thymeleaf en el template

### Emails no se reciben

**Posibles causas:**

1. **Carpeta de spam**: Revisar carpeta de correo no deseado
2. **Email inválido**: Verificar que el email del usuario sea correcto
3. **Rate limiting**: El proveedor SMTP puede tener límites de envío

**Solución:**
1. Revisar logs del backend para confirmar que el email se envió
2. Verificar configuración SPF/DKIM en el dominio
3. Contactar al proveedor SMTP si el problema persiste

### Debugging

Habilitar debug de emails en `application.yml`:

```yaml
spring:
  mail:
    properties:
      mail:
        debug: true

logging:
  level:
    com.transer.vortice.shared.infrastructure.email: DEBUG
```

## Testing

### Probar Envío de Email

1. **Registrar un nuevo usuario**:
   ```bash
   POST http://localhost:8080/api/auth/register
   ```

2. **Solicitar recuperación de contraseña**:
   ```bash
   POST http://localhost:8080/api/auth/forgot-password
   ```

3. **Revisar logs del backend**:
   ```
   Enviando email a: usuario@example.com con template: welcome
   Email enviado exitosamente a: usuario@example.com
   ```

### Herramientas de Testing

- **Mailtrap**: [https://mailtrap.io/](https://mailtrap.io/) - SMTP de prueba
- **MailHog**: Servidor SMTP local para desarrollo
- **Postman**: Para probar endpoints de autenticación

### Configurar Mailtrap para Testing

```bash
MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=tu-mailtrap-username
MAIL_PASSWORD=tu-mailtrap-password
```

## Seguridad

### Buenas Prácticas

1. **NUNCA** commitear credenciales en el código
2. Usar **variables de entorno** para configuración sensible
3. Usar **contraseñas de aplicación** en lugar de contraseñas principales
4. Habilitar **STARTTLS** para encriptar comunicaciones SMTP
5. Implementar **rate limiting** en endpoints de email

### Rate Limiting

El sistema incluye rate limiting para prevenir abuso:

```yaml
vortice:
  rate-limit:
    # Límite para registro: 3 requests por hora por IP
    register:
      capacity: 3
      refill-tokens: 3
      refill-period-minutes: 60
```

## Monitoreo

### Métricas

El sistema expone métricas de email a través de Actuator:

```bash
GET http://localhost:8080/api/actuator/metrics
```

### Logs

Los logs de email incluyen:

- **INFO**: Confirmación de envío exitoso
- **ERROR**: Errores al enviar email (incluye stack trace)
- **WARN**: Intentos de recuperación para usuarios inactivos

**Ejemplo:**

```log
2026-01-20 10:30:15 - Enviando email a: usuario@example.com con template: password-reset
2026-01-20 10:30:17 - Email enviado exitosamente a: usuario@example.com
```

## Referencias

- [Spring Boot Mail Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.email)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
- [Amazon SES Documentation](https://docs.aws.amazon.com/ses/)

---

**Última actualización:** 2026-01-20
**Versión:** 1.0.0
