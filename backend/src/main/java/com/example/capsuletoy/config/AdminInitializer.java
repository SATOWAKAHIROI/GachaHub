package com.example.capsuletoy.config;

import com.example.capsuletoy.model.UserRole;
import com.example.capsuletoy.repository.UserRepository;
import com.example.capsuletoy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.email:admin@gachahub.com}")
    private String adminEmail;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByUsername(adminUsername)) {
            userService.createUser(adminUsername, adminEmail, adminPassword, UserRole.ADMIN);
            logger.info("初期管理者ユーザーを作成しました: {}", adminUsername);
        } else {
            logger.info("管理者ユーザーは既に存在します: {}", adminUsername);
        }
    }
}
