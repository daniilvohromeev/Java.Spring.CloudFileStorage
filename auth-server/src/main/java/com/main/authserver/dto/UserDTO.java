package com.main.authserver.dto;

public record UserDTO(String username, String password, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled){}