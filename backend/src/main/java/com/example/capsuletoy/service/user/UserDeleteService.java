package com.example.capsuletoy.service.user;

import org.springframework.stereotype.Service;

import com.example.capsuletoy.repository.UserRepository;

@Service
public class UserDeleteService {
    private final UserRepository userRepository;

    public UserDeleteService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ユーザー削除
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
