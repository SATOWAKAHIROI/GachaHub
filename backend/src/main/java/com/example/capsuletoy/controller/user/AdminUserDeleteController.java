package com.example.capsuletoy.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.service.user.UserDeleteService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserDeleteController {
    @Autowired
    private UserDeleteService userDeleteService;

    // ユーザー削除
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userDeleteService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "ユーザーを削除しました"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
