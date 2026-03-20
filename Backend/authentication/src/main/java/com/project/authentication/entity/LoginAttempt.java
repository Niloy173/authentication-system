package com.project.authentication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "LOGIN_ATTEMPT")
@Getter
@Setter
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "login_attempt_seq")
    @SequenceGenerator(name = "login_attempt_seq", sequenceName = "LOGIN_ATTEMPT_SEQ", allocationSize = 1)
    @Column(name = "ATTEMPT_ID")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "ATTEMPT_TIME", nullable = false)
    private LocalDateTime attemptTime;

    @Column(name = "SUCCESS", length = 1)
    private Character success;

    @Column(name = "IP_ADDRESS", length = 50)
    private String ipAddress;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.attemptTime = LocalDateTime.now();
    }
}
