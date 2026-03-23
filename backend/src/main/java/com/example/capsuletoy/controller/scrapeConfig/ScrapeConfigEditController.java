package com.example.capsuletoy.controller.scrapeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.response.ErrorResponse;
import com.example.capsuletoy.service.scrapeConfig.ScrapeConfigUpdateService;

@RestController
@RequestMapping("/api/scrape/configs")
public class ScrapeConfigEditController {
    @Autowired
    private ScrapeConfigUpdateService scrapeConfigUpdateService;

    /**
     * 設定を更新
     * PUT /api/scrape/configs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody ScrapeConfig config) {
        try {
            ScrapeConfig updated = scrapeConfigUpdateService.updateConfig(id, config);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.errorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.errorResponse(e.getMessage()));
        }
    }

    /**
     * 有効/無効を切り替え
     * PATCH /api/scrape/configs/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleEnabled(@PathVariable Long id) {
        try {
            ScrapeConfig toggled = scrapeConfigUpdateService.toggleEnabled(id);
            return ResponseEntity.ok(toggled);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.errorResponse(e.getMessage()));
        }
    }
}
