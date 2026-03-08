package com.example.capsuletoy.service.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.example.capsuletoy.domain.auth.AuthenticationUser;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.model.UserRole;
import com.example.capsuletoy.response.user.UserResponse;
import com.example.capsuletoy.security.JwtUtil;
import com.example.capsuletoy.service.user.UserService;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationUser authenticationUser;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> adminLogin(String email, String password){
        try{
            // メールアドレス、パスワード認証
            authenticationUser.authenticate(email, password);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("メールアドレスまたはパスワードが正しくありません");
        }

        User user = userService.findByEmail(email);

        if(user.getRole() != UserRole.ADMIN){
            throw new AccessDeniedException("管理者権限がありません");
        }

        // トークン生成
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // レスポンス作成
        Map<String, Object> response = UserResponse.buildUserResponse(token, user);

        return response;
    }
}
