package com.project.authentication.service.impl;

import com.project.authentication.dto.response.UserResponse;
import com.project.authentication.exception.AppException;
import com.project.authentication.mapper.UserMapper;
import com.project.authentication.repository.UserRepository;
import com.project.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiuceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getMyProfile(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }
}
