package com.example.capsuletoy.controller.user;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.request.user.UpdateUserRequest;
import com.example.capsuletoy.service.user.UserService;
import com.example.capsuletoy.service.user.UserUpdateService;

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

    @Autowired 
    private UserUpdateService userUpdateService;

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
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UpdateUserRequest request) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            User updatedUser = userUpdateService.updateUser(
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
}
