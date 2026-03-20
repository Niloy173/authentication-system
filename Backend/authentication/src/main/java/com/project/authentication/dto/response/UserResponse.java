package com.project.authentication.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserResponse {


    private String userId;
    private String username;
    private String email;
    private String status;
    private List<String> roles;
    private LocalDateTime createdAt;
}
