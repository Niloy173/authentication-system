package com.project.authentication.service.impl;

import com.project.authentication.config.AppProperties;
import com.project.authentication.constant.RoleName;
import com.project.authentication.constant.TokenType;
import com.project.authentication.constant.UserStatus;
import com.project.authentication.dto.request.LoginRequest;
import com.project.authentication.dto.request.RegisterRequest;
import com.project.authentication.dto.response.AuthResponse;
import com.project.authentication.entity.*;
import com.project.authentication.exception.AppException;
import com.project.authentication.mapper.UserMapper;
import com.project.authentication.repository.LoginAttemptRepository;
import com.project.authentication.repository.RoleRepository;
import com.project.authentication.repository.UserRepository;
import com.project.authentication.repository.UserRoleRepostory;
import com.project.authentication.service.AuthService;
import com.project.authentication.service.TokenService;
import com.project.authentication.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepostory userRoleRepostory;
    private final LoginAttemptRepository loginAttemptRepository;
    private final TokenService tokenService;
    private final PasswordUtil passwordUtil;
    private final UserMapper userMapper;
    private final AppProperties appProperties;


    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException("Username already exists", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already exists", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordUtil.encodePassword(request.getPassword()));
        user.setStatus(UserStatus.DISABLED);

        userRepository.save(user);

        Role userRole = roleRepository.findByRoleName(RoleName.USER.name())
                .orElseThrow(() -> new AppException("Default Role not found", HttpStatus.NOT_FOUND));

        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setUserId(user.getUserId());
        userRoleId.setRoleId(userRole.getRoleId());

        UserRole mappedRole = new UserRole();
        mappedRole.setId(userRoleId);
        mappedRole.setRole(userRole);
        mappedRole.setUser(user);

        userRoleRepostory.save(mappedRole);

        var token = tokenService.createToken(user, TokenType.VERIFY_EMAIL);
        log.info("Email verification token for {}: {}", user.getEmail(), token.getToken());

        // mail will be triggered here later

    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {

        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AppException("User is disabled", HttpStatus.FORBIDDEN);
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new AppException("Account is locked. Try again later", HttpStatus.FORBIDDEN);
            }

            user.setStatus(UserStatus.ACTIVE);
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }

        if (!passwordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedAttempt(user,ipAddress);
            throw new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        recordAttempt(user, 'Y', ipAddress);

        // JWT will be generated here later
        return AuthResponse.builder()
                .accessToken("jwt-pending")
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();


    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        AuthToken authToken = tokenService.validateToken(token, TokenType.VERIFY_EMAIL);

        User user = authToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

    }


    private void handleFailedAttempt(User user, String ipAddress) {
        int attempt = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempt);

        if (attempt >= appProperties.getSecurity().getMaxFailedAttempts()) {
            user.setStatus(UserStatus.LOCKED);
            user.setLockedUntil(LocalDateTime.now()
                    .plusMinutes(appProperties.getSecurity().getLockDurationMinutes()));
            log.warn("Account locked for user: {}", user.getEmail());

        }

        userRepository.save(user);
        recordAttempt(user, 'N', ipAddress);
    }

    private void recordAttempt(User user, char success, String ipAddress) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUser(user);
        attempt.setSuccess(success);
        attempt.setIpAddress(ipAddress);
        loginAttemptRepository.save(attempt);
    }
}
