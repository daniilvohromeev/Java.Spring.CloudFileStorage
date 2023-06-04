package com.main.authserver.service;

import com.main.authserver.model.Role;
import com.main.authserver.model.User;
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
    private final MinioClient minioClient;
    @Transactional
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        user.getAuthorities().add(roleRepository.findByAuthority("ROLE_USER").get());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String bucketName = "bucket-for-user-" + user.getId();
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
        return userRepository.save(user);
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