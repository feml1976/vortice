# 🛡️ Rate Limiting - Vórtice API

## 📋 Descripción

El sistema implementa **Rate Limiting** (limitación de velocidad de peticiones) para proteger la API contra:

- ✅ Ataques de fuerza bruta en login
- ✅ Abuso de endpoints de registro
- ✅ Sobrecarga del servidor
- ✅ Spam y peticiones maliciosas

## 🔧 Implementación

### Tecnología

- **Biblioteca:** Bucket4j 8.10.1
- **Algoritmo:** Token Bucket
- **Almacenamiento:** En memoria (ConcurrentHashMap)
- **Granularidad:** Por IP del cliente

### Arquitectura

```
Cliente → RateLimitFilter → JwtAuthenticationFilter → Controllers
           ↓
      RateLimitService
           ↓
      Bucket4j (Token Bucket)
```

## ⚙️ Configuración

### Límites Predeterminados

La configuración se encuentra en `application.yml`:

```yaml
vortice:
  rate-limit:
    enabled: true

    # Límite global: 100 requests/minuto por IP
    global:
      capacity: 100
      refill-tokens: 100
      refill-period-minutes: 1

    # Límite para /auth/login: 5 requests/minuto por IP
    auth:
      capacity: 5
      refill-tokens: 5
      refill-period-minutes: 1

    # Límite para /auth/register: 3 requests/hora por IP
    register:
      capacity: 3
      refill-tokens: 3
      refill-period-minutes: 60
```

### Tipos de Límites

| Endpoint | Límite | Periodo | Uso |
|----------|--------|---------|-----|
| **Global** | 100 requests | 1 minuto | Todos los endpoints no especificados |
| **Auth (Login)** | 5 requests | 1 minuto | `/auth/login` - Protección contra fuerza bruta |
| **Register** | 3 requests | 1 hora | `/auth/register` - Prevención de spam |

## 🚦 Funcionamiento

### Algoritmo Token Bucket

1. Cada IP tiene un "bucket" (cubo) con tokens
2. Cada request consume 1 token
3. Los tokens se rellenan automáticamente según el periodo configurado
4. Si no hay tokens disponibles, la petición es rechazada

### Ejemplo Práctico

**Escenario:** Usuario intenta hacer login

```
Límite: 5 tokens, relleno 5 tokens/minuto

Intento 1: ✅ Tokens: 5 → 4
Intento 2: ✅ Tokens: 4 → 3
Intento 3: ✅ Tokens: 3 → 2
Intento 4: ✅ Tokens: 2 → 1
Intento 5: ✅ Tokens: 1 → 0
Intento 6: ❌ Tokens: 0 → 429 Too Many Requests
```

Después de 1 minuto: Tokens se rellenan → 5 tokens disponibles

## 📡 Headers HTTP

### Headers en Respuestas Exitosas

```http
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 3
X-RateLimit-Type: AUTH
```

- **X-RateLimit-Limit:** Número total de requests permitidos
- **X-RateLimit-Remaining:** Requests restantes
- **X-RateLimit-Type:** Tipo de límite aplicado (GLOBAL, AUTH, REGISTER)

### Headers en Respuesta 429

```http
HTTP/1.1 429 Too Many Requests
X-RateLimit-Limit: 0
X-RateLimit-Remaining: 0
X-RateLimit-Type: AUTH
Retry-After: 45
Content-Type: application/json
```

### Cuerpo de Respuesta 429

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

## 🔍 Detección de IP

El sistema detecta la IP del cliente en este orden:

1. **X-Forwarded-For** - Para proxies/load balancers
2. **X-Real-IP** - Header alternativo de proxy
3. **RemoteAddr** - IP directa de la conexión

### Ejemplo con Proxy

```
Cliente (203.0.113.1) → Nginx → Spring Boot

X-Forwarded-For: 203.0.113.1
Rate Limit aplicado a: 203.0.113.1
```

## 🧪 Testing

### Probar Rate Limiting con curl

**1. Verificar límite de login:**

```bash
# Hacer 6 peticiones rápidas
for i in {1..6}; do
  echo "Intento $i:"
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"usernameOrEmail":"admin","password":"wrong"}' \
    -w "\nStatus: %{http_code}\n" \
    -s | jq .
  echo "---"
done
```

**Resultado esperado:**
- Intentos 1-5: Status 400/401 (credenciales incorrectas)
- Intento 6: Status 429 (rate limit excedido)

**2. Verificar headers de rate limiting:**

```bash
curl -v -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"test"}' \
  2>&1 | grep -i "x-ratelimit"
```

**Salida esperada:**
```
< X-RateLimit-Limit: 5
< X-RateLimit-Remaining: 4
< X-RateLimit-Type: AUTH
```

### Probar con diferentes IPs

```bash
# IP 1
curl -X POST http://localhost:8080/api/auth/login \
  -H "X-Forwarded-For: 192.168.1.1" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"test"}'

# IP 2 (no afectada por límite de IP 1)
curl -X POST http://localhost:8080/api/auth/login \
  -H "X-Forwarded-For: 192.168.1.2" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"test"}'
```

## ⚡ Personalización

### Cambiar Límites

Edita `application.yml` y reinicia la aplicación:

```yaml
vortice:
  rate-limit:
    auth:
      capacity: 10        # Aumentar a 10 requests
      refill-period-minutes: 5  # por 5 minutos
```

### Deshabilitar Rate Limiting

```yaml
vortice:
  rate-limit:
    enabled: false
```

### Variables de Entorno

```bash
# Deshabilitar en producción si usas WAF externo
VORTICE_RATE_LIMIT_ENABLED=false

# Ajustar límite de login
VORTICE_RATE_LIMIT_AUTH_CAPACITY=10
VORTICE_RATE_LIMIT_AUTH_REFILL_PERIOD_MINUTES=5
```

## 🔧 Mantenimiento

### Limpiar Buckets de una IP

```java
@Autowired
private RateLimitService rateLimitService;

// Limpiar buckets de una IP específica
rateLimitService.clearBucketsForIp("192.168.1.100");
```

### Obtener Estadísticas

```java
// Obtener stats de una IP
RateLimitService.BucketStats stats = rateLimitService.getStats(
    "192.168.1.100",
    RateLimitService.RateLimitType.AUTH
);

log.info("Tokens disponibles: {}", stats.remainingTokens());
log.info("Capacidad total: {}", stats.capacity());
```

## 🚨 Casos de Uso

### Caso 1: Usuario Olvidó su Contraseña

**Problema:** Intenta 10 veces con contraseñas diferentes

**Solución:**
1. Después de 5 intentos: Rate limit activado
2. Mensaje claro: "Intente en 60 segundos"
3. Usuario puede usar "Olvidé mi contraseña"

### Caso 2: Ataque de Fuerza Bruta

**Escenario:** Bot intenta 1000 combinaciones

**Protección:**
1. Solo se permiten 5 intentos/minuto
2. Después: 429 con Retry-After header
3. Bot bloqueado efectivamente
4. Servidor protegido

### Caso 3: Spike Legítimo de Tráfico

**Escenario:** 100 usuarios legítimos intentan login simultáneamente

**Comportamiento:**
- Límite GLOBAL: 100 requests/minuto
- La mayoría pasa sin problema
- Solo usuarios que excedan su límite individual son bloqueados temporalmente

## 📊 Monitoreo

### Logs

El sistema registra cuando se excede un límite:

```
WARN  RateLimitFilter - Rate limit excedido - IP: 203.0.113.1,
      URI: /api/auth/login, Type: AUTH, Wait: 45s
```

### Métricas Recomendadas

Para producción, considera monitorear:

- Número de 429 responses por endpoint
- IPs más bloqueadas
- Tiempo promedio de espera (Retry-After)
- Distribución de requests por tipo de límite

## 🔐 Seguridad

### Mejores Prácticas

✅ **Usar detrás de Proxy/Load Balancer:**
- Configurar correctamente X-Forwarded-For
- Validar IPs trusted en el proxy

✅ **Combinar con Account Locking:**
- Rate limiting + bloqueo de cuenta = doble protección
- 5 intentos rate limit + 5 intentos = cuenta bloqueada

✅ **Logs y Alertas:**
- Monitorear patrones de 429
- Alertar sobre IPs sospechosas

❌ **No confiar solo en Rate Limiting:**
- Usar también: CAPTCHA, MFA, validación fuerte de contraseñas
- Rate limiting es UNA capa de muchas

## 🆘 Troubleshooting

### Problema: Usuarios legítimos bloqueados

**Causa:** Límites muy restrictivos o IPs compartidas (NAT)

**Solución:**
```yaml
vortice:
  rate-limit:
    auth:
      capacity: 10  # Aumentar límite
      refill-period-minutes: 1
```

### Problema: Rate limiting no funciona

**Verificar:**
1. `vortice.rate-limit.enabled=true` en config
2. RateLimitFilter está registrado en SecurityConfig
3. Revisar logs para errores de inicialización

### Problema: IPs incorrectas en logs

**Causa:** Proxy no configura X-Forwarded-For

**Solución:** Configurar proxy:
```nginx
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Real-IP $remote_addr;
```

## 📚 Referencias

- [Bucket4j Documentation](https://bucket4j.com/)
- [Token Bucket Algorithm](https://en.wikipedia.org/wiki/Token_bucket)
- [RFC 6585 - 429 Too Many Requests](https://tools.ietf.org/html/rfc6585#section-4)

---

**Última actualización:** 2026-01-19
**Versión:** 1.0.0
**Autor:** Vórtice Development Team
