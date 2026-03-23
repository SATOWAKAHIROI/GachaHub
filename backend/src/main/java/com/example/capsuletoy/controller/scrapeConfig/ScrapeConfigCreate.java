package com.example.capsuletoy.controller.scrapeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.response.ErrorResponse;
import com.example.capsuletoy.service.scrapeConfig.ScrapeConfigCreateService;

@RestController
@RequestMapping("/api/scrape/configs")
public class ScrapeConfigCreate {
    @Autowired
    private ScrapeConfigCreateService scrapeConfigCreateService;

    /**
     * 新規設定を作成
     * POST /api/scrape/configs
     */
    @PostMapping
    public ResponseEntity<?> createConfig(@RequestBody ScrapeConfig config) {
        try {
            ScrapeConfig created = scrapeConfigCreateService.createConfig(config);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.errorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.errorResponse(e.getMessage()));
        }
    }

    
}
