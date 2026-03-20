package com.project.authentication.repository;

import com.project.authentication.constant.TokenType;
import com.project.authentication.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenAndTokenType(String token, TokenType tokenType);

    @Modifying
    @Query("UPDATE AuthToken t SET t.used = 'Y' WHERE t.user.userId = :userId AND t.tokenType = :tokenType AND t.used = 'N'")
    void invalidatePreviousTokens(Long userId, TokenType tokenType);

}
