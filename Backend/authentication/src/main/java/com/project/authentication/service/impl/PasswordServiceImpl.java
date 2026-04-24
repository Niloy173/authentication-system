package com.project.authentication.service.impl;

import com.project.authentication.constant.TokenType;
import com.project.authentication.dto.request.ChangePasswordRequest;
import com.project.authentication.dto.request.ForgotPasswordRequest;
import com.project.authentication.dto.request.ResetPasswordRequest;
import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;
import com.project.authentication.exception.AppException;
import com.project.authentication.mail.MailService;
import com.project.authentication.repository.UserRepository;
import com.project.authentication.service.PasswordService;
import com.project.authentication.service.TokenService;
import com.project.authentication.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {


    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordUtil passwordUtil;
    private final MailService mailService;

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        // Always return without error — email enumeration protection
        // Caller (controller) will return 200 regardless
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            AuthToken token = tokenService.createToken(user, TokenType.RESET_PASSWORD);
            log.info("Password reset token for {}: {}", user.getEmail(), token.getToken());

            mailService.sendResetPasswordEmail(user, token);
        });


    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        AuthToken authToken = tokenService.validateToken(request.getToken(), TokenType.RESET_PASSWORD);

        User user = authToken.getUser();
        user.setPasswordHash(passwordUtil.encodePassword(request.getNewPassword()));
        userRepository.save(user);

        tokenService.burnToken(authToken);

    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, User currentUser) {

        if (!passwordUtil.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new AppException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        currentUser.setPasswordHash(passwordUtil.encodePassword(request.getNewPassword()));
        userRepository.save(currentUser);

    }
}
