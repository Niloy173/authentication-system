package com.project.authentication.mail.impl;

import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;
import com.project.authentication.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "false")
public class NoOpMailServiceImpl implements MailService {

    @Override
    public void sendVerificationEmail(User user, AuthToken token) {
        log.info("[MAIL DISABLED] Verification email skipped for: {}", user.getEmail());
    }

    @Override
    public void sendLoginOtpEmail(User user, AuthToken token) {
        log.info("[MAIL DISABLED] OTP email skipped for: {}", user.getEmail());
    }

    @Override
    public void sendResetPasswordEmail(User user, AuthToken token) {
        log.info("[MAIL DISABLED] Reset password email skipped for: {}", user.getEmail());
    }
}
