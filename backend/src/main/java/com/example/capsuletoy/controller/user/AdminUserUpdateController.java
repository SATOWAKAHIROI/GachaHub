package com.example.capsuletoy.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.request.user.UpdateUserRequest;
import com.example.capsuletoy.response.user.UserResponse;
import com.example.capsuletoy.service.user.UserUpdateService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserUpdateController {
    @Autowired
    private UserUpdateService userUpdateService;

    // ユーザー更新
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User user = userUpdateService.updateUser(
                    id,
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getNotificationEnabled()
            );
            return ResponseEntity.ok(UserResponse.toUserMap(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
