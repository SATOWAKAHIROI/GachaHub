package com.example.capsuletoy.response;

import java.util.HashMap;
import java.util.Map;

public final class ErrorResponse {
    public static Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return error;
    }
}
