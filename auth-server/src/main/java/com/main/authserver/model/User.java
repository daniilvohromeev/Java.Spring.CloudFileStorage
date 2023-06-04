package com.main.authserver.model;

import com.main.authserver.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User extends BaseEntity<Long> implements UserDetails {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean accountNonExpired = true;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean accountNonLocked = true;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean credentialsNonExpired = true;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "users_role",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Set<Role> authorities;
}