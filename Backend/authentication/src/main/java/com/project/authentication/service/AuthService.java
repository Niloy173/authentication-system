package com.project.authentication.service;

import com.project.authentication.dto.request.LoginRequest;
import com.project.authentication.dto.request.RegisterRequest;
import com.project.authentication.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request, String ipAddress);

    void verifyEmail(String token);
}
