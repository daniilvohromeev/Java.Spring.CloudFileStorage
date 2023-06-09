package com.main.authserver.payload.response;

public record TokenResponse(String token, String expires_in, String refresh_token) {

}
