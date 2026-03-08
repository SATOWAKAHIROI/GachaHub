package com.example.capsuletoy.controller.scrapeConfig;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.response.ErrorResponse;
import com.example.capsuletoy.service.scrapeConfig.ScrapeConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * スクレイピング設定管理用コントローラー
 */
@RestController
@RequestMapping("/api/scrape/configs")
@CrossOrigin(origins = "http://localhost:3000")
public class ScrapeConfigController {
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.errorResponse(e.getMessage()));
        }
    }
}
