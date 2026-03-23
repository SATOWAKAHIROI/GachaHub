package com.example.capsuletoy.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.model.UserRole;
import com.example.capsuletoy.request.user.CreateUserRequest;
import com.example.capsuletoy.response.user.UserResponse;
import com.example.capsuletoy.service.user.UserCreateService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserCreateController {
    @Autowired
    private UserCreateService userCreateService;

    // ユーザー作成
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            String requestRole = request.getRole();
            String requestRoleUpperCase = requestRole.toUpperCase();
            UserRole role = UserRole.valueOf(requestRoleUpperCase);
            User user = userCreateService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    role
            );
            BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.CREATED);
            return bodyBuilder.body(UserResponse.toUserMap(user));
        } catch (IllegalArgumentException e) {
            BodyBuilder bodyBuilder = ResponseEntity.badRequest();
            return bodyBuilder.body(Map.of("error", "無効なロールです: " + request.getRole()));
        } catch (RuntimeException e) {
            BodyBuilder bodyBuilder = ResponseEntity.badRequest();
            return bodyBuilder.body(Map.of("error", e.getMessage()));
        }
    }
}
