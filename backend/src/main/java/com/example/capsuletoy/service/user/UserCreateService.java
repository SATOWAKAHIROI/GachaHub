package com.example.capsuletoy.service.user;

import org.springframework.stereotype.Service;

import com.example.capsuletoy.domain.passwordEncode.PasswordEncodeHelper;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.model.UserRole;
import com.example.capsuletoy.repository.UserRepository;

@Service
public class UserCreateService {
    private final UserRepository userRepository;

    private final PasswordEncodeHelper passwordEncoder;

    public UserCreateService(UserRepository userRepository, PasswordEncodeHelper passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ユーザー作成（管理者用）
    public User createUser(String username, String email, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encodePassword(password));
        user.setRole(role);
        user.setNotificationEnabled(false);

        return userRepository.save(user);
    }
}
