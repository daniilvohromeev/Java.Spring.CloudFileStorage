package com.main.authserver.service;

import com.main.authserver.model.Role;
import com.main.authserver.model.User;
import com.main.authserver.repository.RoleRepository;
import com.main.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {
    private final RoleRepository roleService;
    private final UserRepository userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Role root = Role.builder().authority("ROLE_ADMIN").build();
        Role basic = Role.builder().authority("ROLE_USER").build();

        User user =
                User.builder()
                        .username("root")
                        .password(passwordEncoder.encode("root"))
                        .authorities(Set.of(root))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();

        if (roleService.findAll().isEmpty()) {
            roleService.save(root);
            roleService.save(basic);
        }
        if (userService.findAll().isEmpty()) {
            userService.save(user);
        }
    }
}