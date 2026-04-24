package com.project.authentication.mail.impl;

import com.project.authentication.config.AppProperties;
import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;
import com.project.authentication.mail.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    @Override
    public void sendVerificationEmail(User user, AuthToken token) {
        String link = appProperties.getFrontend().getBaseUrl()
                + "/verify-email?token=" + token.getToken();

        String body = loadTemplate("templates/mail/verify-email.html")
                .replace("{{name}}", user.getUsername())
                .replace("{{verificationLink}}", link)
                .replace("{{expiryMinutes}}", String.valueOf(appProperties.getToken().getExpiryMinutes()))
                .replace("{{year}}", String.valueOf(Year.now().getValue()));

        send(user.getEmail(), "Verify Your Email Address", body);
    }

    @Override
    public void sendLoginOtpEmail(User user, AuthToken token) {
        String body = loadTemplate("templates/mail/otp-login.html")
                .replace("{{name}}", user.getUsername())
                .replace("{{otp}}", token.getToken())
                .replace("{{expiryMinutes}}", String.valueOf(appProperties.getToken().getExpiryMinutes()))
                .replace("{{maxAttempts}}", String.valueOf(appProperties.getToken().getMaxAttempts()))
                .replace("{{year}}", String.valueOf(Year.now().getValue()));

        send(user.getEmail(), "Your Login Verification Code", body);
    }

    @Override
    public void sendResetPasswordEmail(User user, AuthToken token) {
        String link = appProperties.getFrontend().getBaseUrl()
                + "/reset-password?token=" + token.getToken();

        String body = loadTemplate("templates/mail/reset-password.html")
                .replace("{{name}}", user.getUsername())
                .replace("{{resetLink}}", link)
                .replace("{{expiryMinutes}}", String.valueOf(appProperties.getToken().getExpiryMinutes()))
                .replace("{{year}}", String.valueOf(Year.now().getValue()));

        send(user.getEmail(), "Reset Your Password", body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load email template: {}", path);
            throw new RuntimeException("Email template not found: " + path);
        }
    }
}
