package com.project.authentication.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
public class TokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateToken() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
