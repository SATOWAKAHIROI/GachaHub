package com.example.capsuletoy.controller.auth;

import com.example.capsuletoy.request.user.LoginRequest;
import com.example.capsuletoy.service.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 管理者専用ログイン
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        try {
            String requestEmail = request.getEmail();
            String requestPassword = request.getPassword();

            // レスポンス作成
            Map<String, Object> response = authService.adminLogin(requestEmail, requestPassword);

            // レスポンス送信
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            BodyBuilder responseStatus = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
            return responseStatus.body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            BodyBuilder responseStatus = ResponseEntity.status(HttpStatus.FORBIDDEN);
            return responseStatus.body(Map.of("error", e.getMessage()));
        }catch (Exception e) {
            BodyBuilder responseStatus = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            return responseStatus.body(Map.of("error", e.getMessage()));
        }
    }
}
