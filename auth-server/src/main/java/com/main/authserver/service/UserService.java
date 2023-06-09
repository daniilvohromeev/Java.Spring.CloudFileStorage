package com.main.authserver.service;

import com.main.authserver.mapper.UserMapper;
import com.main.authserver.model.Role;
import com.main.authserver.model.User;
import com.main.authserver.payload.request.RegisterRequest;
import com.main.authserver.payload.UserDTO;
import com.main.authserver.repository.RoleRepository;
import com.main.authserver.repository.UserRepository;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final MinioClient minioClient;

    public UserDTO createUser(RegisterRequest registerRequest) {
        Role role = roleRepository.findByAuthority("ROLE_USER").orElseThrow(
                () -> new RuntimeException("Такой роли не найдено")
        );
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        User user =
                User.builder()
                        .username(registerRequest.username())
                        .password(passwordEncoder.encode(registerRequest.password()))
                        .authorities(Set.of(role))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
        userRepository.save(user);
        String bucketName = "bucket-"+registerRequest.username();
        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException("Упсс, MinIO упал :-)");
        }
        return userMapper.convertToDto(user);
    }

    @Transactional
    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.deleteById(id);
    }

    public void addRoles(Long id, Set<Role> roles) {
        User user = getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.getAuthorities().addAll(roles);
        userRepository.save(user);
    }
}