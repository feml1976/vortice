package com.transer.vortice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Vórtice
 * Sistema de Gestión de Taller Modernizado - TRANSER
 *
 * @author Equipo de Desarrollo Vórtice
 * @version 1.0.0
 * @since 2026-01-19
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class VorticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VorticeApplication.class, args);
    }
}
