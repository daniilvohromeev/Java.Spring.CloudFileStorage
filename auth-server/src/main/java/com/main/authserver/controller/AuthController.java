package com.main.authserver.controller;

import com.main.authserver.model.User;
import com.main.authserver.payload.LoginRequest;
import com.main.authserver.payload.RegisterRequest;
import com.main.authserver.payload.TokenResponse;
import com.main.authserver.payload.UserDTO;
import com.main.authserver.service.TokenService;
import com.main.authserver.service.UserService;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        LOG.debug("Token requested for user: '{}'", authentication.getName());
        LOG.debug("Refresh token requested for user: '{}'", authentication.getName());
        return ResponseEntity.ok(tokenService.generateToken(authentication));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterRequest request) {
        try {
            User newUser = userService.createUser(User.builder()
                    .username(request.username())
                    .password(request.password())
                    .build());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }
}
