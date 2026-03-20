package com.project.authentication.mapper;

import com.project.authentication.dto.response.UserResponse;
import com.project.authentication.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }


    public UserResponse toResponse(User user) {
        List<String> roles = user.getUserRoles() == null
                ? Collections.emptyList()
                : user.getUserRoles()
                    .stream()
                    .map(userRole -> userRole.getRole().getRoleName())
                    .toList();

        return UserResponse.builder()
                .userId(String.valueOf(user.getUserId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
