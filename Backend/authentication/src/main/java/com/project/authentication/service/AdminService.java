package com.project.authentication.service;

public interface AdminService {

    void assignRole(Long userId, String roleName);

    void removeRole(Long userId, String roleName);

    void disableUser(Long userId);
}
