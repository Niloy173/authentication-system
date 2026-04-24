package com.project.authentication.constant;

public enum TokenType {

    // Authentication tokens
    BEARER,

    // Action-oriented tokens (one-time use)
    OTP,
    RESET_PASSWORD,
    VERIFY_EMAIL,

    // State tokens
    PENDING,
    DISABLED,
    LOCKED
}
