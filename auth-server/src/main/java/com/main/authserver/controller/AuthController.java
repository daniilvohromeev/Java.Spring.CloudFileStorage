package com.main.authserver.controller;

import com.main.authserver.dto.TokenResponse;
import com.main.authserver.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(Authentication authentication) {
        LOG.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.debug("Token granted: {}", token);
        LOG.debug("Refresh token requested for user: '{}'", authentication.getName());
        String refreshToken = tokenService.generateRefreshToken(authentication);
        LOG.debug("Refresh token granted: {}", refreshToken);
        return ;
    }

}
