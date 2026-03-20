package com.project.authentication.repository;

import com.project.authentication.entity.UserRole;
import com.project.authentication.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepostory extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByIdUserId(Long userId);

    List<UserRole> findByIdRoleId(Long roleId);
}
