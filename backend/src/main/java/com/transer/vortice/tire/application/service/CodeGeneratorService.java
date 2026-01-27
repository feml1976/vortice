package com.transer.vortice.tire.application.service;

import com.transer.vortice.tire.domain.repository.TireSpecificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para generar códigos únicos autoincrementales
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGeneratorService {

    private final TireSpecificationRepository tireSpecificationRepository;

    /**
     * Genera un código único autoincremental para especificaciones técnicas
     *
     * Formato: FT-NNNNNN (ej: FT-000001, FT-000002, etc.)
     *
     * @return código único generado
     */
    @Transactional
    public String generateTireSpecificationCode() {
        // Obtener el último código usado
        long count = tireSpecificationRepository.count();
        long nextNumber = count + 1;

        // Generar código con formato FT-NNNNNN (6 dígitos)
        String code = String.format("FT-%06d", nextNumber);

        // Verificar que no exista (por si hay eliminaciones)
        while (tireSpecificationRepository.existsByCode(code)) {
            nextNumber++;
            code = String.format("FT-%06d", nextNumber);
        }

        log.info("Código generado para especificación técnica: {}", code);

        return code;
    }
}
