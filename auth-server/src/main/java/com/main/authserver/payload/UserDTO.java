package com.main.authserver.payload;

import com.main.authserver.model.Role;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    public long id;
    public String username;
    public String accessToken;
    public String refreshToken;
    public Set<Role> authorities;
}