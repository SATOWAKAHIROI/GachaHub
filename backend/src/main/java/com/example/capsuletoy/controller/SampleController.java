package com.example.capsuletoy.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SampleController {

    // ここにエンドポイントを実装してください

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
}
