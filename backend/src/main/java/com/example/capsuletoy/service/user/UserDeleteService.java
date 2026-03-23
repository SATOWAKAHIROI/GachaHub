package com.example.capsuletoy.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.capsuletoy.repository.UserRepository;

@Service
public class UserDeleteService {
    @Autowired
    private UserRepository userRepository;

    // ユーザー削除
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
