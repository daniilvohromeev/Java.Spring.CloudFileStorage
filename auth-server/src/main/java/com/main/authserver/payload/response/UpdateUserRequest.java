package com.main.authserver.payload.response;

public record UpdateUserRequest(long id, String username, String password) {
}
