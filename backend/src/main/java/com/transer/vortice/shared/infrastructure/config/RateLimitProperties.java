package com.transer.vortice.shared.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuración para Rate Limiting.
 * Lee la configuración desde application.yml bajo el prefijo vortice.rate-limit.
 *
 * @author Vórtice Development Team
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "vortice.rate-limit")
public class RateLimitProperties {

    /**
     * Habilitar/deshabilitar rate limiting globalmente.
     */
    private boolean enabled = true;

    /**
     * Configuración de límite global para todos los endpoints.
     */
    private LimitConfig global = new LimitConfig();

    /**
     * Configuración de límite para endpoints de autenticación (/auth/login).
     */
    private LimitConfig auth = new LimitConfig();

    /**
     * Configuración de límite para endpoint de registro (/auth/register).
     */
    private LimitConfig register = new LimitConfig();

    /**
     * Configuración individual de límite.
     */
    @Data
    public static class LimitConfig {
        /**
         * Capacidad máxima del bucket (número de tokens).
         */
        private long capacity = 100;

        /**
         * Número de tokens a rellenar por periodo.
         */
        private long refillTokens = 100;

        /**
         * Periodo de relleno en minutos.
         */
        private long refillPeriodMinutes = 1;
    }
}
