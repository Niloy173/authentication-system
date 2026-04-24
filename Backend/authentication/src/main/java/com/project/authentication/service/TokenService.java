package com.project.authentication.service;

import com.project.authentication.constant.TokenType;
import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;

public interface TokenService {

    AuthToken createToken(User user, TokenType tokenType);

    /**
     * Validates link-based tokens (VERIFY_EMAIL, RESET_PASSWORD).
     * Checks: exists, not used, not expired.
     * Not suitable for OTP validation — use validateOtp() instead.
     */
    AuthToken validateToken(String token, TokenType tokenType);

    /**
     * Validates OTP tokens submitted by the user.
     * Checks: exists by value, not used, not expired, attempts not maxed.
     * Increments attempts on failure, burns token when max attempts reached.
     *
     * TODO (Security Phase): When pending token is available in security context,
     * refactor to find AuthToken by user + TokenType first, then compare OTP value —
     * so attempts can be incremented even when OTP value is completely wrong.
     * Requires new repo query: findByUserAndTokenTypeAndUsed(user, tokenType, 'N')
     */
    AuthToken validateOtp(String otp);


    void burnToken(AuthToken authToken);

    void incrementAttempts(AuthToken authToken);
}
