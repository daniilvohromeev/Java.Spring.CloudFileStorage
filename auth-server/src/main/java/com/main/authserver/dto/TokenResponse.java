package com.main.authserver.dto;

import com.main.authserver.model.User;

public record TokenResponse(String token, String expires_in, String refresh_token, User user) {

}
