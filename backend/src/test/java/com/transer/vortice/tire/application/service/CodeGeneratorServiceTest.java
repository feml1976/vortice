package com.transer.vortice.tire.application.service;

import com.transer.vortice.tire.domain.repository.TireSpecificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CodeGeneratorService.
 * Usa Mockito para simular dependencias.
 *
 * @author Vórtice Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CodeGeneratorService Tests")
class CodeGeneratorServiceTest {

    @Mock
    private TireSpecificationRepository tireSpecificationRepository;

    @InjectMocks
    private CodeGeneratorService codeGeneratorService;

    @BeforeEach
    void setUp() {
        // Setup inicial si es necesario
    }

    // =====================================================
    // TESTS: Generación de Código
    // =====================================================

    @Test
    @DisplayName("Debe generar código con formato correcto FT-000001")
    void shouldGenerateCodeWithCorrectFormat() {
        // Given
        when(tireSpecificationRepository.count()).thenReturn(0L);
        when(tireSpecificationRepository.existsByCode("FT-000001")).thenReturn(false);

        // When
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(generatedCode).isNotNull();
        assertThat(generatedCode).isEqualTo("FT-000001");
        assertThat(generatedCode).matches("FT-\\d{6}");

        verify(tireSpecificationRepository, times(1)).count();
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000001");
    }

    @Test
    @DisplayName("Debe generar código secuencial basado en el contador")
    void shouldGenerateSequentialCodeBasedOnCount() {
        // Given
        when(tireSpecificationRepository.count()).thenReturn(42L);
        when(tireSpecificationRepository.existsByCode("FT-000043")).thenReturn(false);

        // When
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(generatedCode).isNotNull();
        assertThat(generatedCode).isEqualTo("FT-000043");

        verify(tireSpecificationRepository, times(1)).count();
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000043");
    }

    @Test
    @DisplayName("Debe generar código con padding de 6 dígitos")
    void shouldGenerateCodeWithSixDigitPadding() {
        // Given
        when(tireSpecificationRepository.count()).thenReturn(99999L);
        when(tireSpecificationRepository.existsByCode("FT-100000")).thenReturn(false);

        // When
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(generatedCode).isNotNull();
        assertThat(generatedCode).isEqualTo("FT-100000");
        assertThat(generatedCode).hasSize(9); // FT- (3) + 6 dígitos

        verify(tireSpecificationRepository, times(1)).count();
    }

    @Test
    @DisplayName("Debe saltar código si ya existe (manejo de eliminaciones)")
    void shouldSkipCodeIfAlreadyExists() {
        // Given
        when(tireSpecificationRepository.count()).thenReturn(5L);
        when(tireSpecificationRepository.existsByCode("FT-000006")).thenReturn(true);
        when(tireSpecificationRepository.existsByCode("FT-000007")).thenReturn(false);

        // When
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(generatedCode).isNotNull();
        assertThat(generatedCode).isEqualTo("FT-000007");

        verify(tireSpecificationRepository, times(1)).count();
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000006");
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000007");
    }

    @Test
    @DisplayName("Debe intentar múltiples códigos hasta encontrar uno disponible")
    void shouldTryMultipleCodesUntilFindingAvailableOne() {
        // Given
        when(tireSpecificationRepository.count()).thenReturn(10L);
        when(tireSpecificationRepository.existsByCode("FT-000011")).thenReturn(true);
        when(tireSpecificationRepository.existsByCode("FT-000012")).thenReturn(true);
        when(tireSpecificationRepository.existsByCode("FT-000013")).thenReturn(false);

        // When
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(generatedCode).isNotNull();
        assertThat(generatedCode).isEqualTo("FT-000013");

        verify(tireSpecificationRepository, times(1)).count();
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000011");
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000012");
        verify(tireSpecificationRepository, times(1)).existsByCode("FT-000013");
    }

    @Test
    @DisplayName("Debe generar código con prefijo FT-")
    void shouldGenerateCodeWithPrefixFT() {
        // Given
        when(tireSpecificationRepository.count()).thenReturn(0L);
        when(tireSpecificationRepository.existsByCode(anyString())).thenReturn(false);

        // When
        String generatedCode = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(generatedCode).startsWith("FT-");
        assertThat(generatedCode).matches("^FT-\\d{6}$");
    }

    @Test
    @DisplayName("Debe generar diferentes códigos en múltiples llamadas")
    void shouldGenerateDifferentCodesOnMultipleCalls() {
        // Given
        when(tireSpecificationRepository.count())
                .thenReturn(0L)
                .thenReturn(1L)
                .thenReturn(2L);
        when(tireSpecificationRepository.existsByCode(anyString())).thenReturn(false);

        // When
        String code1 = codeGeneratorService.generateTireSpecificationCode();
        String code2 = codeGeneratorService.generateTireSpecificationCode();
        String code3 = codeGeneratorService.generateTireSpecificationCode();

        // Then
        assertThat(code1).isEqualTo("FT-000001");
        assertThat(code2).isEqualTo("FT-000002");
        assertThat(code3).isEqualTo("FT-000003");
        assertThat(code1).isNotEqualTo(code2);
        assertThat(code2).isNotEqualTo(code3);
    }
}
