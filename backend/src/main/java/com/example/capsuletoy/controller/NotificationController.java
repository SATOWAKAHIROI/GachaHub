package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知管理用コントローラー
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * テストメール送信
     * POST /api/notifications/test
     */
    @PostMapping("/test")
    public ResponseEntity<?> sendTestMail(@RequestBody Map<String, String> request) {
        String toAddress = request.get("email");
        if (toAddress == null || toAddress.isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("メールアドレスが指定されていません"));
        }

        try {
            notificationService.sendTestMail(toAddress);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "テストメールを送信しました: " + toAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("テストメール送信失敗: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("メール送信に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * ユーザーの通知設定を切り替え
     * PATCH /api/notifications/users/{userId}/toggle
     */
    @PatchMapping("/users/{userId}/toggle")
    public ResponseEntity<?> toggleNotification(@PathVariable Long userId) {
        try {
            User user = notificationService.toggleNotification(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("userId", user.getId());
            response.put("notificationEnabled", user.getNotificationEnabled());
            response.put("message", user.getNotificationEnabled() ? "通知を有効にしました" : "通知を無効にしました");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return error;
    }
}
