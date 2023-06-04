package com.main.authserver.controller;

import com.main.authserver.config.RsaKeyProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwkSetRestController {

    private final RsaKeyProperties jwtConfigProperties;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        RSAKey key = new RSAKey.Builder(jwtConfigProperties.publicKey())
                .build();
        return new JWKSet(key).toJSONObject();
    }
}