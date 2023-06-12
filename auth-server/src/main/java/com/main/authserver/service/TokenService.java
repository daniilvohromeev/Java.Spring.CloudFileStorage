package com.main.authserver.service;

import com.main.authserver.model.User;
import com.main.authserver.payload.request.TokenRefreshRequest;
import com.main.authserver.payload.response.TokenRefreshResponse;
import com.main.authserver.payload.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final UserService userService;

    public TokenResponse generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return new TokenResponse(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(), claims.getExpiresAt().toString(), generateRefreshToken(authentication));
    }

    public String generateRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        try {
            Jwt jwt = this.decoder.decode(request.getRefreshToken());
            if (Objects.requireNonNull(jwt.getExpiresAt()).isBefore(Instant.now())) {
                throw new IllegalArgumentException("Refresh token is expired");
            }
            String subject = jwt.getSubject();
            if (subject == null) {
                throw new IllegalArgumentException("Refresh token does not have a subject claim");
            }
            User user =  userService.getUserByUsername(jwt.getSubject());
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());

            // Установка Authentication объекта в SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            TokenResponse tokenDTO = generateToken(authentication);
            return new TokenRefreshResponse(tokenDTO.token(), tokenDTO.refresh_token());
        } catch (JwtException e) {
            throw new IllegalArgumentException("Failed to decode refresh token", e);
        }
    }
}