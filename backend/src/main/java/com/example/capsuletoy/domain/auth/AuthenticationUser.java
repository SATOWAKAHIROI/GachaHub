package com.example.capsuletoy.domain.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.model.UserRole;

@Component
public class AuthenticationUser {
    @Autowired
    private AuthenticationManager authenticationManager;

    public void authenticate(String email, String password) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    public boolean checkAdmin(User user){
        // 管理者権限であるか確認
        boolean isAdmin = user.getRole().equals(UserRole.ADMIN);

        return isAdmin;
    }
}
