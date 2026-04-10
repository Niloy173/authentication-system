package com.project.authentication.service.impl;

import com.project.authentication.config.AppProperties;
import com.project.authentication.constant.TokenType;
import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;
import com.project.authentication.exception.AppException;
import com.project.authentication.repository.AuthTokenRepository;
import com.project.authentication.service.TokenService;
import com.project.authentication.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final AuthTokenRepository authTokenRepository;
    private final TokenUtil tokenUtil;
    private final AppProperties appProperties;



    @Override
    @Transactional
    public AuthToken createToken(User user, TokenType tokenType) {
        // Invalidate all previous tokens of same type for this user
        authTokenRepository.invalidatePreviousTokens(user.getUserId(), tokenType);

        AuthToken authToken = new AuthToken();
        authToken.setUser(user);
        authToken.setTokenType(tokenType);
        authToken.setUsed('N');
        authToken.setAttempts(0);
        authToken.setExpiryTime(LocalDateTime.now()
                .plusMinutes(appProperties.getToken().getExpiryMinutes()));

        // OTP → 6-digit numeric code, everything else → UUID link token
        if (tokenType == TokenType.OTP) {
            authToken.setToken(tokenUtil.generateOtp());
        } else {
            authToken.setToken(tokenUtil.generateToken());
        }

        return authTokenRepository.save(authToken);
    }

    @Override
    public AuthToken validateToken(String token, TokenType tokenType) {

        AuthToken authToken = authTokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new AppException("Invalid token", HttpStatus.BAD_REQUEST));

        // Already used
        if (authToken.getUsed() == 'Y') {
            throw new AppException("Token has already been used", HttpStatus.BAD_REQUEST);
        }

        // Expired
        if (authToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        // OTP-only: check attempt count before accepting
        if (tokenType == TokenType.OTP) {
            if (authToken.getAttempts() >= appProperties.getToken().getMaxAttempts()) {
                throw new AppException("Too many attempts. Please request a new OTP", HttpStatus.TOO_MANY_REQUESTS);
            }
        }

        return authToken;
    }

    @Override
    @Transactional
    public void burnToken(AuthToken authToken) {
        authToken.setUsed('Y');
        authTokenRepository.save(authToken);
    }

    @Override
    @Transactional
    public void incrementAttempts(AuthToken authToken) {
        int attempts = authToken.getAttempts() + 1;
        authToken.setAttempts(attempts);

        // If max attempts reached → burn the token, force user to resend
        if (attempts >= appProperties.getToken().getMaxAttempts()) {
            authToken.setUsed('Y');
            log.warn("OTP max attempts reached for user: {}", authToken.getUser().getEmail());
        }

        authTokenRepository.save(authToken);
    }
}
