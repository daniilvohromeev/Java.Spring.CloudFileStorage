package com.main.authserver.controller;

import com.main.authserver.mapper.UserMapper;
import com.main.authserver.payload.*;
import com.main.authserver.payload.request.LoginRequest;
import com.main.authserver.payload.request.RegisterRequest;
import com.main.authserver.payload.request.TokenRefreshRequest;
import com.main.authserver.payload.response.TokenRefreshResponse;
import com.main.authserver.payload.response.TokenResponse;
import com.main.authserver.service.TokenService;
import com.main.authserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final UserMapper mapper;
    @PostMapping("/token")
    public ResponseEntity<UserDTO> token(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        LOG.debug("Token requested for user: '{}'", authentication.getName());
        LOG.debug("Refresh token requested for user: '{}'", authentication.getName());
        TokenResponse token = tokenService.generateToken(authentication);
        UserDTO userDTO = mapper.convertToDto(userService.getUserByUsername(loginRequest.username()));
        userDTO.setAccessToken(token.token());
        userDTO.setRefreshToken(token.refresh_token());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequest request) {
        UserDTO response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = tokenService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
