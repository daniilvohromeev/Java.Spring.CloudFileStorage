package com.main.authserver.model;

import com.main.authserver.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "roles")
public class Role extends BaseEntity<Long> implements GrantedAuthority {
    @Column(nullable = false, unique = true)
    private String authority;

    @ManyToMany(mappedBy = "authorities")
    private List<User> users = new ArrayList<>();
}