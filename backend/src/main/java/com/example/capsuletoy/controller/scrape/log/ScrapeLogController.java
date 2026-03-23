package com.example.capsuletoy.controller.scrape.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.domain.log.ScrapeLogAdministrater;
import com.example.capsuletoy.model.ScrapeLog;

@RestController
@RequestMapping("/api/scrape")
public class ScrapeLogController {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeLogController.class);

    @Autowired
    private ScrapeLogAdministrater scrapeLogAdministrater;

     /**
     * スクレイピングログの取得
     * GET /api/scrape/logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<ScrapeLog>> getScrapeLogs(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching scrape logs, limit: {}", limit);

        try {
            List<ScrapeLog> logs = scrapeLogAdministrater.getRecentScrapeLogs(limit);
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
            List<ScrapeLog> logs = scrapeLogAdministrater.getScrapeLogsByTargetSite(site);
            return ResponseEntity.ok(logs);

        } catch (Exception e) {
            logger.error("Failed to fetch scrape logs for site {}: {}", site, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
