package com.microservice.archchatnotificationservice.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCodeEmail(String toEmail, String username, String code) {
        try {
            String htmlContent = loadHtmlTemplate("templates/verification-email.html")
                    .replace("{{username}}", username)
                    .replace("{{verification_code}}", code);

            sendHtmlEmail(toEmail, "ArchChat - Seu Código de Verificação", htmlContent);

            System.out.println("E-mail de verificação enviado com sucesso para: " + toEmail);

        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail para " + toEmail + ": " + e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail de verificação", e);
        }
    }

    private String loadHtmlTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("no-reply@archchat.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}
