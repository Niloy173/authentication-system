package com.project.authentication.service;

import com.project.authentication.constant.TokenType;
import com.project.authentication.entity.AuthToken;
import com.project.authentication.entity.User;

public interface TokenService {

    AuthToken createToken(User user, TokenType tokenType);

    AuthToken validateToken(String token, TokenType tokenType);
}
