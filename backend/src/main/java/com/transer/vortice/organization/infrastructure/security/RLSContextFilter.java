package com.transer.vortice.organization.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que establece el contexto de Row-Level Security (RLS) en PostgreSQL.
 * Se ejecuta después de la autenticación para configurar la variable de sesión
 * app.current_user_id que utilizan las políticas RLS.
 *
 * IMPORTANTE: Este filtro debe ejecutarse DESPUÉS de JwtAuthenticationFilter
 * para que el usuario ya esté autenticado.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Component
@Order(2) // Ejecutar después del filtro de autenticación (que tiene Order 1 o default)
@RequiredArgsConstructor
public class RLSContextFilter extends OncePerRequestFilter {

    private final SecurityUtils securityUtils;
    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Solo establecer contexto RLS si hay usuario autenticado
            if (securityUtils.isAuthenticated()) {
                Long userId = securityUtils.getCurrentUserId();

                if (userId != null) {
                    // Establecer variable de sesión para RLS
                    // Esta variable es utilizada por las funciones get_user_office_id() y current_user_has_role()
                    jdbcTemplate.execute(String.format(
                        "SET LOCAL app.current_user_id = %d",
                        userId
                    ));

                    log.debug("RLS context establecido para usuario ID: {} (URI: {})",
                             userId, request.getRequestURI());
                } else {
                    log.warn("Usuario autenticado pero sin ID disponible");
                }
            } else {
                log.debug("Request sin autenticación, RLS context no establecido (URI: {})",
                         request.getRequestURI());
            }

        } catch (Exception e) {
            log.error("Error al establecer contexto RLS", e);
            // No lanzar excepción, dejar que el request continúe
            // Si RLS falla, las políticas de la base de datos bloquearán accesos no autorizados
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * No aplicar filtro a endpoints públicos que no requieren autenticación.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // No aplicar RLS en endpoints públicos
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/error");
    }
}
