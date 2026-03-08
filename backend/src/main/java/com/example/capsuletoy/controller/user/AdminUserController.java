package com.example.capsuletoy.controller.user;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.response.user.UserResponse;
import com.example.capsuletoy.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // ユーザー一覧取得
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Stream<User> stream = users.stream();
        Stream<Map<String, Object>> userListStream = stream.map(UserResponse::toUserMap);
        List<Map<String, Object>> userList = userListStream.collect(Collectors.toList());
        return ResponseEntity.ok(userList);
    }

    // ユーザー詳細取得
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(UserResponse.toUserMap(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
