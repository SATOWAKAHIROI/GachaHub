package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // 自分のプロフィール取得
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            return ResponseEntity.ok(buildUserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "プロフィールの取得に失敗しました"));
        }
    }

    // 自分のプロフィール更新
    @PutMapping
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            User updatedUser = userService.updateUser(
                    user.getId(),
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getNotificationEnabled()
            );

            return ResponseEntity.ok(buildUserResponse(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("notificationEnabled", user.getNotificationEnabled());
        response.put("createdAt", user.getCreatedAt().toString());
        return response;
    }

    static class UpdateProfileRequest {
        private String username;
        private String email;
        private String password;
        private Boolean notificationEnabled;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Boolean getNotificationEnabled() { return notificationEnabled; }
        public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    }
}
