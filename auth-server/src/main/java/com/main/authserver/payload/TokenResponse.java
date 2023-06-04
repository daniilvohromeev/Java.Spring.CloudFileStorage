package com.main.authserver.payload;

public record TokenResponse(String token, String expires_in, String refresh_token) {

}
