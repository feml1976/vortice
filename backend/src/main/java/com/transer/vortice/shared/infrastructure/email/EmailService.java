package com.transer.vortice.shared.infrastructure.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Servicio para envío de emails.
 * Utiliza JavaMailSender y Thymeleaf para templates HTML.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${vortice.app.name:Vórtice}")
    private String appName;

    @Value("${vortice.app.url:http://localhost:5173}")
    private String appUrl;

    @Value("${vortice.app.support-email:soporte@vortice.transer.com}")
    private String supportEmail;

    /**
     * Envía un email de forma asíncrona.
     *
     * @param to destinatario
     * @param subject asunto
     * @param templateName nombre del template Thymeleaf
     * @param variables variables para el template
     */
    @Async
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            log.info("Enviando email a: {} con template: {}", to, templateName);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            // Configurar destinatario y asunto
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail, appName);

            // Agregar variables comunes a todos los templates
            variables.put("appName", appName);
            variables.put("appUrl", appUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("currentYear", java.time.Year.now().getValue());

            // Procesar template con Thymeleaf
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            // Establecer contenido HTML
            helper.setText(htmlContent, true);

            // Enviar email
            mailSender.send(mimeMessage);

            log.info("Email enviado exitosamente a: {}", to);

        } catch (MessagingException e) {
            log.error("Error al enviar email a: {}", to, e);
            throw new RuntimeException("Error al enviar email", e);
        } catch (Exception e) {
            log.error("Error inesperado al enviar email a: {}", to, e);
            throw new RuntimeException("Error inesperado al enviar email", e);
        }
    }

    /**
     * Envía un email de recuperación de contraseña.
     *
     * @param to email del destinatario
     * @param userName nombre del usuario
     * @param resetToken token de recuperación
     */
    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        log.info("Enviando email de recuperación de contraseña a: {}", to);

        String resetUrl = appUrl + "/reset-password?token=" + resetToken;

        Map<String, Object> variables = Map.of(
                "userName", userName,
                "resetUrl", resetUrl,
                "resetToken", resetToken,
                "expirationMinutes", 60
        );

        sendEmail(to, "Recuperación de Contraseña - " + appName, "password-reset", variables);
    }

    /**
     * Envía un email de bienvenida a un nuevo usuario.
     *
     * @param to email del destinatario
     * @param userName nombre del usuario
     */
    public void sendWelcomeEmail(String to, String userName) {
        log.info("Enviando email de bienvenida a: {}", to);

        Map<String, Object> variables = Map.of(
                "userName", userName,
                "loginUrl", appUrl + "/login"
        );

        sendEmail(to, "Bienvenido a " + appName, "welcome", variables);
    }

    /**
     * Envía un email de confirmación de cambio de contraseña.
     *
     * @param to email del destinatario
     * @param userName nombre del usuario
     */
    public void sendPasswordChangedEmail(String to, String userName) {
        log.info("Enviando email de confirmación de cambio de contraseña a: {}", to);

        Map<String, Object> variables = Map.of(
                "userName", userName,
                "loginUrl", appUrl + "/login"
        );

        sendEmail(to, "Contraseña Cambiada - " + appName, "password-changed", variables);
    }
}
