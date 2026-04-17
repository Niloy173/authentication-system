package com.project.authentication.service.impl;

import com.project.authentication.constant.UserStatus;
import com.project.authentication.entity.Role;
import com.project.authentication.entity.User;
import com.project.authentication.entity.UserRole;
import com.project.authentication.entity.UserRoleId;
import com.project.authentication.exception.AppException;
import com.project.authentication.repository.RoleRepository;
import com.project.authentication.repository.UserRepository;
import com.project.authentication.repository.UserRoleRepostory;
import com.project.authentication.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepostory userRoleRepository;

    @Override
    @Transactional
    public void assignRole(Long userId, String roleName) {
        User user = findUserById(userId);
        Role role = findRoleByName(roleName);

        UserRoleId id = new UserRoleId();
        id.setUserId(user.getUserId());
        id.setRoleId(role.getRoleId());

        // Don't assign if already has this role
        if (userRoleRepository.existsById(id)) {
            throw new AppException("User already has role: " + roleName, HttpStatus.CONFLICT);
        }

        UserRole userRole = new UserRole();
        userRole.setId(id);
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, String roleName) {
        User user = findUserById(userId);
        Role role = findRoleByName(roleName);

        UserRoleId id = new UserRoleId();
        id.setUserId(user.getUserId());
        id.setRoleId(role.getRoleId());

        if (!userRoleRepository.existsById(id)) {
            throw new AppException("User does not have role: " + roleName, HttpStatus.NOT_FOUND);
        }

        userRoleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void disableUser(Long userId) {
        User user = findUserById(userId);

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AppException("User is already disabled", HttpStatus.CONFLICT);
        }

        user.setStatus(UserStatus.DISABLED);
        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    private Role findRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException("Role not found: " + roleName, HttpStatus.NOT_FOUND));
    }
}
