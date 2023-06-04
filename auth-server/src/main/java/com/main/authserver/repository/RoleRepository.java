package com.main.authserver.repository;

import com.main.authserver.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByAuthority(String authorityName);
}
