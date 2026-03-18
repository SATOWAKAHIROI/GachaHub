package com.example.capsuletoy.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.capsuletoy.domain.passwordEncode.PasswordEncodeHelper;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.repository.UserRepository;

@Service
public class UserUpdateService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncodeHelper passwordEncoder;

    // ユーザー更新
    public User updateUser(Long id, String username, String email, String password, Boolean notificationEnabled) {
        User user = userService.findById(id);

        // ユーザー名の重複チェック（自分以外）
        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(username);
        }

        // メールアドレスの重複チェック（自分以外）
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email);
        }

        // パスワード更新（空でなければ）
        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encodePassword(password));
        }

        // 通知設定更新
        if (notificationEnabled != null) {
            user.setNotificationEnabled(notificationEnabled);
        }

        return userRepository.save(user);
    }
}
