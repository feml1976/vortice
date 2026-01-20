package com.transer.vortice.shared.infrastructure.ratelimit;

import com.transer.vortice.shared.infrastructure.config.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para gestionar rate limiting basado en IP y endpoint.
 * Usa Bucket4j para implementar el algoritmo de token bucket.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitProperties rateLimitProperties;

    // Cache de buckets por IP
    private final Map<String, Bucket> globalBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    /**
     * Resuelve el bucket apropiado para la IP y tipo de límite.
     *
     * @param key IP del cliente
     * @param limitType tipo de límite (GLOBAL, AUTH, REGISTER)
     * @return bucket correspondiente
     */
    public Bucket resolveBucket(String key, RateLimitType limitType) {
        return switch (limitType) {
            case GLOBAL -> globalBuckets.computeIfAbsent(key, k -> createBucket(rateLimitProperties.getGlobal()));
            case AUTH -> authBuckets.computeIfAbsent(key, k -> createBucket(rateLimitProperties.getAuth()));
            case REGISTER -> registerBuckets.computeIfAbsent(key, k -> createBucket(rateLimitProperties.getRegister()));
        };
    }

    /**
     * Crea un nuevo bucket con la configuración especificada.
     *
     * @param config configuración del límite
     * @return bucket configurado
     */
    private Bucket createBucket(RateLimitProperties.LimitConfig config) {
        Bandwidth limit = Bandwidth.classic(
                config.getCapacity(),
                Refill.intervally(
                        config.getRefillTokens(),
                        Duration.ofMinutes(config.getRefillPeriodMinutes())
                )
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Verifica si rate limiting está habilitado.
     *
     * @return true si está habilitado
     */
    public boolean isEnabled() {
        return rateLimitProperties.isEnabled();
    }

    /**
     * Limpia los buckets de una IP específica (útil para testing o mantenimiento).
     *
     * @param ip dirección IP a limpiar
     */
    public void clearBucketsForIp(String ip) {
        globalBuckets.remove(ip);
        authBuckets.remove(ip);
        registerBuckets.remove(ip);
        log.info("Buckets limpiados para IP: {}", ip);
    }

    /**
     * Limpia todos los buckets (útil para testing).
     */
    public void clearAllBuckets() {
        globalBuckets.clear();
        authBuckets.clear();
        registerBuckets.clear();
        log.info("Todos los buckets han sido limpiados");
    }

    /**
     * Obtiene estadísticas del bucket para una IP y tipo de límite.
     *
     * @param ip dirección IP
     * @param limitType tipo de límite
     * @return objeto con estadísticas
     */
    public BucketStats getStats(String ip, RateLimitType limitType) {
        Bucket bucket = resolveBucket(ip, limitType);
        long availableTokens = bucket.getAvailableTokens();

        RateLimitProperties.LimitConfig config = switch (limitType) {
            case GLOBAL -> rateLimitProperties.getGlobal();
            case AUTH -> rateLimitProperties.getAuth();
            case REGISTER -> rateLimitProperties.getRegister();
        };

        return new BucketStats(
                availableTokens,
                config.getCapacity(),
                config.getRefillPeriodMinutes()
        );
    }

    /**
     * Tipos de límite de rate.
     */
    public enum RateLimitType {
        GLOBAL,
        AUTH,
        REGISTER
    }

    /**
     * Estadísticas de un bucket.
     */
    public record BucketStats(
            long availableTokens,
            long capacity,
            long refillPeriodMinutes
    ) {
        public long remainingTokens() {
            return availableTokens;
        }

        public boolean isEmpty() {
            return availableTokens == 0;
        }
    }
}
