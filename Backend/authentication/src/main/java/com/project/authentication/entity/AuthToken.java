package com.project.authentication.entity;

import com.project.authentication.constant.TokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUTH_TOKEN")
@Getter
@Setter
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_token_seq")
    @SequenceGenerator(name = "auth_token_seq", sequenceName = "AUTH_TOKEN_SEQ", allocationSize = 1)
    @Column(name = "TOKEN_ID")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "TOKEN", nullable = false, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false, length = 30)
    private TokenType tokenType;

    @Column(name = "EXPIRY_TIME", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "USED", length = 1)
    private Character used = 'N';

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
