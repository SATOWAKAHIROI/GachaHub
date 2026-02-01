package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.service.ScrapeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * スクレイピング設定管理用コントローラー
 */
@RestController
@RequestMapping("/api/scrape/configs")
@CrossOrigin(origins = "http://localhost:3000")
public class ScrapeConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeConfigController.class);

    @Autowired
    private ScrapeConfigService scrapeConfigService;

    /**
     * 全設定一覧を取得
     * GET /api/scrape/configs
     */
    @GetMapping
    public ResponseEntity<List<ScrapeConfig>> getAllConfigs() {
        List<ScrapeConfig> configs = scrapeConfigService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    /**
     * 個別設定を取得
     * GET /api/scrape/configs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getConfigById(@PathVariable Long id) {
        try {
            ScrapeConfig config = scrapeConfigService.getConfigById(id);
            return ResponseEntity.ok(config);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 新規設定を作成
     * POST /api/scrape/configs
     */
    @PostMapping
    public ResponseEntity<?> createConfig(@RequestBody ScrapeConfig config) {
        try {
            ScrapeConfig created = scrapeConfigService.createConfig(config);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 設定を更新
     * PUT /api/scrape/configs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody ScrapeConfig config) {
        try {
            ScrapeConfig updated = scrapeConfigService.updateConfig(id, config);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 設定を削除
     * DELETE /api/scrape/configs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long id) {
        try {
            scrapeConfigService.deleteConfig(id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "設定を削除しました: ID=" + id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 有効/無効を切り替え
     * PATCH /api/scrape/configs/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleEnabled(@PathVariable Long id) {
        try {
            ScrapeConfig toggled = scrapeConfigService.toggleEnabled(id);
            return ResponseEntity.ok(toggled);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return error;
    }
}
