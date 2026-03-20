package com.project.authentication.service;

import com.project.authentication.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse getMyProfile(Long userId);
}
