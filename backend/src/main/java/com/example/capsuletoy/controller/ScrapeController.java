package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.ScrapeLog;
import com.example.capsuletoy.scraper.BandaiScraper;
import com.example.capsuletoy.scraper.TakaraTomyScraper;
import com.example.capsuletoy.service.ScrapeService;
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
 * スクレイピング管理用コントローラー
 * 管理者が手動でスクレイピングを実行するためのエンドポイント
 */
@RestController
@RequestMapping("/api/scrape")
@CrossOrigin(origins = "http://localhost:3000")
public class ScrapeController {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeController.class);

    @Autowired
    private ScrapeService scrapeService;

    @Autowired
    private BandaiScraper bandaiScraper;

    @Autowired
    private TakaraTomyScraper takaraTomyScraper;

    /**
     * バンダイサイトのスクレイピングを手動実行
     * POST /api/scrape/bandai
     */
    @PostMapping("/bandai")
    public ResponseEntity<?> scrapeBandai() {
        logger.info("Manual scraping requested for Bandai");

        try {
            ScrapeService.ScrapeResult result = scrapeService.executeScraping(bandaiScraper, "BANDAI_GASHAPON");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("site", "BANDAI_GASHAPON");
            response.put("totalProducts", result.totalProducts());
            response.put("newProducts", result.newProducts());
            response.put("message", "全取得: " + result.totalProducts() + "件、うち新着: " + result.newProducts() + "件");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Scraping failed for Bandai: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("site", "BANDAI_GASHAPON");
            errorResponse.put("message", "スクレイピングに失敗しました: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * タカラトミーアーツサイトのスクレイピングを手動実行
     * POST /api/scrape/takaratomy
     */
    @PostMapping("/takaratomy")
    public ResponseEntity<?> scrapeTakaraTomy() {
        logger.info("Manual scraping requested for Takara Tomy Arts");

        try {
            ScrapeService.ScrapeResult result = scrapeService.executeScraping(takaraTomyScraper, "TAKARA_TOMY_ARTS");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("site", "TAKARA_TOMY_ARTS");
            response.put("totalProducts", result.totalProducts());
            response.put("newProducts", result.newProducts());
            response.put("message", "全取得: " + result.totalProducts() + "件、うち新着: " + result.newProducts() + "件");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Scraping failed for Takara Tomy Arts: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("site", "TAKARA_TOMY_ARTS");
            errorResponse.put("message", "スクレイピングに失敗しました: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * スクレイピングログの取得
     * GET /api/scrape/logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<ScrapeLog>> getScrapeLogs(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching scrape logs, limit: {}", limit);

        try {
            List<ScrapeLog> logs = scrapeService.getRecentScrapeLogs(limit);
            return ResponseEntity.ok(logs);

        } catch (Exception e) {
            logger.error("Failed to fetch scrape logs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 特定サイトのスクレイピングログ取得
     * GET /api/scrape/logs/{site}
     */
    @GetMapping("/logs/{site}")
    public ResponseEntity<List<ScrapeLog>> getScrapeLogsBySite(@PathVariable String site) {
        logger.info("Fetching scrape logs for site: {}", site);

        try {
            List<ScrapeLog> logs = scrapeService.getScrapeLogsByTargetSite(site);
            return ResponseEntity.ok(logs);

        } catch (Exception e) {
            logger.error("Failed to fetch scrape logs for site {}: {}", site, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * スクレイピングステータス確認
     * GET /api/scrape/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getScrapeStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", true);
        status.put("supportedSites", List.of("BANDAI_GASHAPON", "TAKARA_TOMY_ARTS"));

        // 最新のログを取得
        List<ScrapeLog> recentLogs = scrapeService.getRecentScrapeLogs(1);
        if (!recentLogs.isEmpty()) {
            ScrapeLog latestLog = recentLogs.get(0);
            status.put("lastExecution", latestLog.getExecutedAt());
            status.put("lastStatus", latestLog.getStatus());
        }

        return ResponseEntity.ok(status);
    }
}
