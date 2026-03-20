package com.project.authentication.repository;

import com.project.authentication.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    List<LoginAttempt> findByUserUserIdOrderByAttemptTimeDesc(Long userId);
}
