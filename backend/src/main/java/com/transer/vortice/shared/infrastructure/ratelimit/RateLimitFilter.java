package com.transer.vortice.shared.infrastructure.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtro para aplicar rate limiting a las peticiones HTTP.
 * Intercepta todas las peticiones y verifica si la IP ha excedido el límite.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Si el rate limiting está deshabilitado, continuar sin verificar
        if (!rateLimitService.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        // Determinar el tipo de límite según el endpoint
        RateLimitService.RateLimitType limitType = determineLimitType(requestUri);

        // Resolver el bucket para la IP
        Bucket bucket = rateLimitService.resolveBucket(clientIp, limitType);

        // Intentar consumir 1 token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Token consumido exitosamente - agregar headers informativos
            addRateLimitHeaders(response, probe, limitType);
            filterChain.doFilter(request, response);
        } else {
            // Límite excedido - retornar 429 Too Many Requests
            handleRateLimitExceeded(response, probe, clientIp, requestUri, limitType);
        }
    }

    /**
     * Determina el tipo de límite según el URI de la petición.
     *
     * @param requestUri URI de la petición
     * @return tipo de límite a aplicar
     */
    private RateLimitService.RateLimitType determineLimitType(String requestUri) {
        if (requestUri.contains("/auth/login")) {
            return RateLimitService.RateLimitType.AUTH;
        } else if (requestUri.contains("/auth/register")) {
            return RateLimitService.RateLimitType.REGISTER;
        } else {
            return RateLimitService.RateLimitType.GLOBAL;
        }
    }

    /**
     * Obtiene la IP del cliente desde la petición.
     * Considera headers de proxy (X-Forwarded-For, X-Real-IP).
     *
     * @param request petición HTTP
     * @return dirección IP del cliente
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Agrega headers informativos sobre el rate limiting a la respuesta.
     *
     * @param response respuesta HTTP
     * @param probe resultado de la verificación del bucket
     * @param limitType tipo de límite aplicado
     */
    private void addRateLimitHeaders(
            HttpServletResponse response,
            ConsumptionProbe probe,
            RateLimitService.RateLimitType limitType) {

        // X-RateLimit-Limit: límite total de requests
        response.setHeader("X-RateLimit-Limit", String.valueOf(probe.getRemainingTokens() + 1));

        // X-RateLimit-Remaining: requests restantes
        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));

        // X-RateLimit-Type: tipo de límite aplicado
        response.setHeader("X-RateLimit-Type", limitType.name());
    }

    /**
     * Maneja el caso cuando se excede el límite de rate.
     * Retorna un 429 Too Many Requests con información del error.
     *
     * @param response respuesta HTTP
     * @param probe resultado de la verificación del bucket
     * @param clientIp IP del cliente
     * @param requestUri URI solicitado
     * @param limitType tipo de límite excedido
     * @throws IOException si hay error al escribir la respuesta
     */
    private void handleRateLimitExceeded(
            HttpServletResponse response,
            ConsumptionProbe probe,
            String clientIp,
            String requestUri,
            RateLimitService.RateLimitType limitType) throws IOException {

        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000; // convertir a segundos

        log.warn("Rate limit excedido - IP: {}, URI: {}, Type: {}, Wait: {}s",
                clientIp, requestUri, limitType, waitForRefill);

        // Configurar respuesta HTTP 429
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Headers estándar de rate limiting
        response.setHeader("X-RateLimit-Limit", "0");
        response.setHeader("X-RateLimit-Remaining", "0");
        response.setHeader("X-RateLimit-Type", limitType.name());
        response.setHeader("Retry-After", String.valueOf(waitForRefill));

        // Cuerpo de la respuesta con detalles del error
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        errorResponse.put("error", "Too Many Requests");
        errorResponse.put("message", String.format(
                "Límite de peticiones excedido para %s. Por favor, intente nuevamente en %d segundos.",
                getLimitTypeDescription(limitType),
                waitForRefill
        ));
        errorResponse.put("path", requestUri);
        errorResponse.put("retryAfterSeconds", waitForRefill);
        errorResponse.put("limitType", limitType.name());

        // Escribir respuesta JSON
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

    /**
     * Obtiene una descripción legible del tipo de límite.
     *
     * @param limitType tipo de límite
     * @return descripción en español
     */
    private String getLimitTypeDescription(RateLimitService.RateLimitType limitType) {
        return switch (limitType) {
            case GLOBAL -> "peticiones generales";
            case AUTH -> "intentos de login";
            case REGISTER -> "intentos de registro";
        };
    }
}
