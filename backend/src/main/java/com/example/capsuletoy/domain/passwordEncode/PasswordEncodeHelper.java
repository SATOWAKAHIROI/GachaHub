package com.example.capsuletoy.domain.passwordEncode;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncodeHelper {
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncodeHelper(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }
}
