package com.project.authentication.mail;

import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;

public interface MailService {

    /**
     * Sends email verification link to newly registered user.
     * Triggered from: AuthServiceImpl.register()
     */
    void sendVerificationEmail(User user, AuthToken token);

    /**
     * Sends 6-digit OTP for login verification.
     * Triggered from: AuthServiceImpl.login() when 7-day rule triggers.
     */
    void sendLoginOtpEmail(User user, AuthToken token);

    /**
     * Sends password reset link to user.
     * Triggered from: PasswordServiceImpl.forgotPassword()
     */
    void sendResetPasswordEmail(User user, AuthToken token);
}
