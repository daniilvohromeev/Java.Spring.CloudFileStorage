package com.main.authserver.payload;

public record UserDTO(String username, String password, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled){}