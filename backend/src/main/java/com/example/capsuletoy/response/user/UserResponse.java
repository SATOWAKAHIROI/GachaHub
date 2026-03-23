package com.example.capsuletoy.response.user;

import java.util.HashMap;
import java.util.Map;

import com.example.capsuletoy.model.User;

public final class UserResponse {
    public static Map<String, Object> toUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("role", user.getRole().name());
        map.put("notificationEnabled", user.getNotificationEnabled());
        map.put("createdAt", user.getCreatedAt().toString());
        return map;
    }

    // レスポンスビルダー
    public static Map<String, Object> buildUserResponse(String token, User user) {
        Map<String, Object> userInfo = UserResponse.toUserMap(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userInfo);

        return response;
    }
}
